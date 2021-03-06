package h5.espressif.esp32.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import h5.espressif.esp32.R;
import h5.espressif.esp32.model.web.AppApiForJS;
import h5.espressif.esp32.model.web.JSApi;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import iot.espressif.esp32.model.user.EspUser;
import libs.espressif.app.AppUtil;
import libs.espressif.app.PermissionHelper;
import libs.espressif.ble.BleAdvData;
import libs.espressif.ble.EspBleUtils;
import libs.espressif.ble.ScanListener;
import libs.espressif.log.EspLog;
import libs.espressif.net.NetUtil;
import libs.espressif.utils.DataUtil;
import libs.espressif.utils.TextUtils;

@SuppressLint("SetJavaScriptEnabled")
public class EspWebActivity extends AppCompatActivity {
    private static final String URL_PHONE = "file:///android_asset/web/app.html";
    private static final String URL_PAD = "file:///android_asset/web/ipad.html";

    private static final int REQUEST_PERMISSION = 0x01;
    private static final int REQUEST_QRCODE = 0x02;

    private static final int BLE_NOTIFY_INTERVAL = 1500;

    private static final int BLE_MANUFACTURER_ADV_TYPE = 0xff;
    private static final int ESP_MANUFACTURER_ID = 0x02E5;

    private final EspLog mLog = new EspLog(getClass());

    private ViewGroup mWebForm;
    private WebView mWebView;

    private ImageView mCoverIV;
    private volatile Bitmap mCoverBmp;

    private AppApiForJS mApiForJS;

    private EspUser mUser;

    private MainDeviceNotifyHelper mDeviceNotifyHelper;

    public boolean otaing = false;

    private volatile boolean mRegisteredCast = false;
    private BroadcastReceiver mReceiver = new MainReceiver();

    private BleCallback mBleCallback = new BleCallback();
    private final Set<BluetoothDevice> mBleSet = new HashSet<>();
    private final Map<BluetoothDevice, BleInfo> mBleInfoMap = new HashMap<>();
    private Thread mBleNotifyThread = new BleNotifyThread();
    private volatile boolean mBleScanning;
    private volatile long mBleLastClearTime;

    private PermissionHelper mPermissionHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.esp_web_activity);

        mUser = EspUser.INSTANCE;

        mCoverIV = findViewById(R.id.web_cover);

        mWebForm = findViewById(R.id.web_form);
        mWebView = new WebView(getApplicationContext());
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        ViewGroup.MarginLayoutParams mlp = new ViewGroup.MarginLayoutParams(width, height);
        mWebForm.addView(mWebView, mlp);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (Objects.equals(uri.getHost(), "www.espressif.com")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setTextZoom(100);

        setCoverAndUrl();

        mPermissionHelper = new PermissionHelper(this, REQUEST_PERMISSION);
        mPermissionHelper.setOnPermissionsListener(new PermissionListener());
        mPermissionHelper.requestAuthorities(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        mDeviceNotifyHelper = new MainDeviceNotifyHelper(this);

        mBleNotifyThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mWebView.removeJavascriptInterface(AppApiForJS.NAME);
        mWebView.destroy();
        mWebForm.removeAllViews();

        mBleNotifyThread.interrupt();
        if (mRegisteredCast) {
            unregisterReceiver(mReceiver);
        }
        release();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_QRCODE) {
            if (resultCode == RESULT_OK) {
                String message = data.getStringExtra(Intents.Scan.RESULT);
                mLog.d("QR code = " + message);
                evaluateJavascript(JSApi.onQRCodeScanned(message));
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        evaluateJavascript(JSApi.onBackPressed());
    }

    private void setCoverAndUrl() {
        Observable.just("image/welcome.png")
                .subscribeOn(Schedulers.io())
                .doOnNext(path -> {
                    InputStream is = getAssets().open(path);
                    mCoverBmp = BitmapFactory.decodeStream(is);
                    is.close();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(path -> {
                    mCoverIV.setImageBitmap(mCoverBmp);

                    if (AppUtil.isPad(this)) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        mWebView.loadUrl(URL_PAD);
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        mWebView.loadUrl(URL_PHONE);
                    }
                    mApiForJS = new AppApiForJS(this);
                    mWebView.addJavascriptInterface(mApiForJS, AppApiForJS.NAME);
                })
                .subscribe();
    }

    private void release() {
        mDeviceNotifyHelper.close();
        mApiForJS.release();
        mUser.clear();
        if (mCoverBmp != null) {
            mCoverBmp.recycle();
        }
    }

    public MainDeviceNotifyHelper getDeviceNotifyHelper() {
        return mDeviceNotifyHelper;
    }

    public void registerPhoneStateChange() {
        if (!mRegisteredCast) {
            mRegisteredCast = true;
            IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter);

            notifyBluetoothChanged(BluetoothAdapter.getDefaultAdapter().getState());
        }
    }

    public void hideCoverImage() {
        runOnUiThread(() -> {
            mCoverIV.setVisibility(View.GONE);
            mCoverIV.setImageBitmap(null);
            if (mCoverBmp != null) {
                mCoverBmp.recycle();
                mCoverBmp = null;
            }
        });
    }

    public boolean isLocationEnable() {
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (manager == null) {
            return false;
        }
        boolean gpsEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnable = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gpsEnable || networkEnable;
    }

    public void evaluateJavascript(String script) {
        runOnUiThread(() -> mWebView.evaluateJavascript(script, null));
    }

    private void notifyWifiChanged() {
        JSONObject json = new JSONObject();
        final String keyConnected = "connected";
        final String keySSID = "ssid";
        final String keyBSSID = "bssid";
        final String keyFreq = "frequency";
        try {
            String[] infos = NetUtil.getCurrentConnectionInfo(this);
            if (infos == null) {
                json.put(keyConnected, false);
            } else {
                String ssid = encodeSSID(infos[0]);
                json.put(keyConnected, true)
                        .put(keySSID, ssid)
                        .put(keyBSSID, infos[1])
                        .put(keyFreq, infos[3]);
            }
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        evaluateJavascript(JSApi.onWifiStateChanged(json.toString()));
    }

    private String encodeSSID(String ssid) throws UnsupportedEncodingException {
        List<String> list = new ArrayList<>();
        byte[] ssidBytes = ssid.getBytes();
        byte[] temp = new byte[ssidBytes.length];
        int index = 0;
        int length = 0;
        int offset = 0;
        for (byte b : ssidBytes) {
            if (b != ' ') {
                temp[index++] = b;
                length++;

                if (offset == ssidBytes.length - 1) {
                    String splitSSID = new String(temp, 0, length);
                    list.add(URLEncoder.encode(splitSSID, "UTF-8"));
                }
            } else {
                if (length > 0) {
                    String splitSSID = new String(temp, 0, length);
                    list.add(URLEncoder.encode(splitSSID, "UTF-8"));
                }
                index = 0;
                length = 0;

                list.add(" ");
            }

            offset++;
        }

        StringBuilder sb = new StringBuilder();
        for (String string : list) {
            sb.append(string);
        }

        return sb.toString();
    }

    private void notifyBluetoothChanged(int state) {
        boolean enable = state == BluetoothAdapter.STATE_ON;
        try {
            JSONObject json = new JSONObject()
                    .put("enable", enable);
            evaluateJavascript(JSApi.onBluetoothChanged(json.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startBleScan() {
        if (mBleScanning) {
            EspBleUtils.stopScanBle(mBleCallback);
        }
        mBleLastClearTime = SystemClock.elapsedRealtime();
        EspBleUtils.startScanBle(mBleCallback);
        mBleScanning = true;
    }

    public void stopBleScan() {
        mBleScanning = false;
        EspBleUtils.stopScanBle(mBleCallback);
        clearBle();
    }

    public void clearBle() {
        synchronized (mBleSet) {
            mBleInfoMap.clear();
            mBleSet.clear();
        }
    }

    public void requestCameraPermission() {
        mPermissionHelper.requestAuthorities(new String[]{
                Manifest.permission.CAMERA,
        });
    }

    private void scanQRCode() {
        Intent intent = new Intent(EspWebActivity.this, CaptureActivity.class);
        intent.setAction(Intents.Scan.ACTION);
        intent.putExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, 0L);
        intent.putExtra(Intents.Scan.RESULT_SHOW_BITMAP, false);
        int framesize = Math.min(mWebForm.getWidth(), mWebForm.getHeight()) * 2 / 3;
        intent.putExtra(Intents.Scan.WIDTH, framesize);
        intent.putExtra(Intents.Scan.HEIGHT, framesize);
        intent.putExtra(Intents.Scan.PROMPT_MESSAGE, getString(R.string.qr_code_message));

        startActivityForResult(intent, REQUEST_QRCODE);
    }

    private class PermissionListener implements PermissionHelper.OnPermissionsListener {

        @Override
        public void onPermissonsChange(String permission, boolean permited) {
            switch (permission) {
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    break;
                case Manifest.permission.ACCESS_COARSE_LOCATION:
                    break;
                case Manifest.permission.CAMERA:
                    scanQRCode();
                    break;
            }
        }
    }

    private class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }

            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    notifyWifiChanged();
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    notifyBluetoothChanged(state);
                    break;
            }
        }
    }

    private class BleCallback implements ScanListener {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String name = device.getName();
            if (TextUtils.isEmpty(name)) {
                return;
            }
//            boolean nameMatch = name != null && name.startsWith(IEspActionDeviceConfigure.PREFIX_BLUFI);
//            if (!nameMatch) {
//                return;
//            }

            synchronized (mBleSet) {
                BleInfo info = new BleInfo();
                info.manufacturerId = ESP_MANUFACTURER_ID;
                info.rssi = rssi;
                info.scanRecord = scanRecord;
                mBleInfoMap.put(device, info);
                mBleSet.add(device);
            }
        }
    }

    private class BleNotifyThread extends Thread {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(BLE_NOTIFY_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }

                if (!mBleScanning) {
                    continue;
                }

                synchronized (mBleSet) {
                    if (!mBleSet.isEmpty()) {
                        JSONArray array = new JSONArray();
                        for (BluetoothDevice ble : mBleSet) {
                            try {
                                String[] addrs = ble.getAddress().split(":");
                                StringBuilder address = new StringBuilder();
                                for (String str : addrs) {
                                    address.append(str.toLowerCase());
                                }
                                BleInfo info = mBleInfoMap.get(ble);
                                info.parseMesh();
                                String mac = address.toString();
                                JSONObject bleJSON = new JSONObject()
                                        .put("mac", mac)
                                        .put("name", ble.getName() == null ? JSONObject.NULL : ble.getName())
                                        .put("rssi", info.rssi)
                                        .put("version", info.version)
                                        .put("bssid", info.staBssid == null ? mac : info.staBssid)
                                        .put("tid", info.tid)
                                        .put("only_beacon", info.onlyBeacon);
                                array.put(bleJSON);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        evaluateJavascript(JSApi.onScanBLE(array.toString()));
                    }

                    // Keep cache ble 3 minutes
                    if (SystemClock.elapsedRealtime() - mBleLastClearTime > 180000L) {
                        clearBle();
                        mBleLastClearTime = SystemClock.elapsedRealtime();
                    }
                }
            }
        }
    }

    private class BleInfo {
        int manufacturerId;

        int rssi;
        byte[] scanRecord;
        String staBssid;
        int version;
        boolean onlyBeacon;
        int tid;

        void initVars() {
            version = -1;
            onlyBeacon = false;
            staBssid = null;
            tid = -1;
        }

        void parseMesh() {
            try {
                _parseMeshVersion();
            } catch (Exception e) {
                e.printStackTrace();
                initVars();
            }
        }

        void _parseMeshVersion() {
            List<BleAdvData> dataList = EspBleUtils.resolveScanRecord(scanRecord);
            for (BleAdvData advData : dataList) {
                // Check manufacturer adv type(0xff)
                if (advData.getType() != BLE_MANUFACTURER_ADV_TYPE) {
                    continue;
                }

                byte[] manuData = advData.getData();
                // Check data length
                if (manuData.length < 14) {
                    continue;
                }
                // Check manufacturer id
                int advManuId = (manuData[0] & 0xff) | ((manuData[1] & 0xff) << 8);
                if (advManuId != manufacturerId) {
                    mLog.d(String.format("BLE ADV ManufacturerID = %04x", advManuId));
//                    continue;
                }
                // Check (MDF)
                if ((manuData[2] & 0xff) != 0x4d
                        || (manuData[3] & 0xff) != 0x44
                        || (manuData[4] & 0xff) != 0x46) {
                    continue;
                }

                version = manuData[5] & 3;
                onlyBeacon = ((manuData[5] >> 4) & 1) == 1;
                staBssid = String.format("%02x%02x%02x%02x%02x%02x",
                        manuData[6], manuData[7], manuData[8], manuData[9], manuData[10], manuData[11]);
                tid = (manuData[12] & 0xff) | ((manuData[13] & 0xff) << 8);
                return;
            }

            initVars();
        }
    }
}
