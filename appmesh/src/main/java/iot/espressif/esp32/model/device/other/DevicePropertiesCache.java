package iot.espressif.esp32.model.device.other;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import iot.espressif.esp32.action.device.IEspActionDeviceInfo;
import iot.espressif.esp32.db.model.DeviceDB;
import iot.espressif.esp32.db.manager.EspDBManager;
import iot.espressif.esp32.model.device.IEspDevice;
import iot.espressif.esp32.model.device.properties.EspDeviceCharacteristic;

public class DevicePropertiesCache {

    private static final String KEY_TID = IEspActionDeviceInfo.KEY_TID;
    private static final String KEY_NAME = IEspActionDeviceInfo.KEY_NAME;
    private static final String KEY_CHARACTERISTICS = IEspActionDeviceInfo.KEY_CHARACTERISTICS;
    private static final String KEY_CID = IEspActionDeviceInfo.KEY_CID;
    private static final String KEY_FORMAT = IEspActionDeviceInfo.KEY_FORMAT;
    private static final String KEY_PERMS = IEspActionDeviceInfo.KEY_PERMS;
    private static final String KEY_MIN = IEspActionDeviceInfo.KEY_MIN;
    private static final String KEY_MAX = IEspActionDeviceInfo.KEY_MAX;
    private static final String KEY_STEP = IEspActionDeviceInfo.KEY_STEP;

    private static final String FORMAT_INT = EspDeviceCharacteristic.FORMAT_INT;
    private static final String FORMAT_DOUBLE = EspDeviceCharacteristic.FORMAT_DOUBLE;
    private static final String FORMAT_STRING = EspDeviceCharacteristic.FORMAT_STRING;
    private static final String FORMAT_JSON = EspDeviceCharacteristic.FORMAT_JSON;

    // Key=tid, Value=EspDeviceCharacteristic
    private Map<Integer, List<EspDeviceCharacteristic>> mAssetMap;
    // Key=DeviceMac, Value=DeviceDB
    Map<String, DeviceDB> mMacDeviceDBMap;

    public DevicePropertiesCache() {
        mAssetMap = getAssetCharacteristicMap();
        mMacDeviceDBMap = getMacDeviceDBMap();
    }

    public void setPropertiesIfCache(IEspDevice device) {
        DeviceDB db = mMacDeviceDBMap.get(device.getMac());
        if (db != null) {
            device.setName(db.getName());
            device.setDeviceTypeId(db.getTid());
            device.setRomVersion(db.getVersion());
            device.setProtocol(db.getProtocol());
            device.setProtocolPort(db.getProtocol_port());

            List<EspDeviceCharacteristic> cs = mAssetMap.get(db.getTid());
            if (cs != null) {
                for (EspDeviceCharacteristic c : cs) {
                    device.addOrReplaceCharacteristic(c.cloneInstance());
                }
            }
        }
    }

    private Map<Integer, List<EspDeviceCharacteristic>> getAssetCharacteristicMap() {
        Map<Integer, List<EspDeviceCharacteristic>> result = new HashMap<>();

        PropertiesModel propertiesModel = getTid1();
        result.put(propertiesModel.tid, propertiesModel.characteristics);

        propertiesModel = getTid22();
        result.put(propertiesModel.tid, propertiesModel.characteristics);

        propertiesModel = getTid23();
        result.put(propertiesModel.tid, propertiesModel.characteristics);
        return result;
    }

    private Map<String, DeviceDB> getMacDeviceDBMap() {
        Map<String, DeviceDB> result = new HashMap<>();

        List<DeviceDB> deviceDBList = EspDBManager.getInstance().device().loadDeviceList();
        for (DeviceDB db : deviceDBList) {
            result.put(db.getMac(), db);
        }
        return result;
    }

    private EspDeviceCharacteristic getCharcteristic(int cid, String name, String format, int perms, Number min, Number max, Number step) {
        EspDeviceCharacteristic characteristic = EspDeviceCharacteristic.newInstance(format);
        assert characteristic != null;
        characteristic.setCid(cid);
        characteristic.setName(name);
        characteristic.setPerms(perms);
        characteristic.setMin(min);
        characteristic.setMax(max);
        characteristic.setStep(step);

        return characteristic;
    }

    private PropertiesModel getTid1() {
        List<EspDeviceCharacteristic> characteristics = new LinkedList<>();

        EspDeviceCharacteristic characteristic = getCharcteristic(0, "on", "int", 7, 0, 1, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(1, "hue", "int", 7, 0, 360, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(2, "saturation", "int", 7, 0, 100, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(3, "value", "int", 7, 0, 100, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(4, "color_temperature", "int", 7, 0, 100, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(5, "brightness", "int", 0, 100, 1, 1);
        characteristics.add(characteristic);

        PropertiesModel result = new PropertiesModel();
        result.tid = 1;
        result.characteristics = characteristics;
        return result;
    }

    private PropertiesModel getTid22() {
        List<EspDeviceCharacteristic> characteristics = new LinkedList<>();

        EspDeviceCharacteristic characteristic = getCharcteristic(1, "lux", "int", 1, 0, 99999, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(2, "temp", "int", 1, -10000, 10000, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(3, "humidity", "int", 1, -10000, 10000, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(4, "pir", "int", 1, 0, 1, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(5, "lightState", "int", 7, 0, 1, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(9, "pirThre", "int", 7, 0, 200, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(10, "pirAlarmWidth", "int", 7, 0, 30, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(11, "pirRealBit", "int", 7, 0, 30, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(12, "lightDelay", "int", 7, 1, 999, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(13, "luxThre", "int", 7, 1, 999, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(14, "tempThre", "int", 7, 1, 999, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(15, "humThre", "int", 7, 1, 999, 1);
        characteristics.add(characteristic);

        PropertiesModel result = new PropertiesModel();
        result.tid = 22;
        result.characteristics = characteristics;
        return result;
    }

    private PropertiesModel getTid23() {
        List<EspDeviceCharacteristic> characteristics = new LinkedList<>();
        EspDeviceCharacteristic characteristic = getCharcteristic(0, "mean_max", "double", 3, 0, 40, 0.01);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(1, "mean_min", "double", 3, 0, 40, 0.01);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(2, "std_max", "double", 3, 0, 100, 0.01);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(3, "std_min", "double", 3, 0, 100, 0.01);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(4, "mode_w0", "double", 3, 0, 100, 0.01);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(5, "mode_w1", "double", 3, 0, 100, 0.01);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(6, "mode_intercept", "double", 3, -100, 100, 0.01);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(7, "num_count", "int", 3, 0, 600, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(8, "pir_data", "int", 3, 0, 1, 1);
        characteristics.add(characteristic);

        characteristic = getCharcteristic(9, "pudate_time", "int", 3, 0, 600, 1);
        characteristics.add(characteristic);

        PropertiesModel result = new PropertiesModel();
        result.tid = 23;
        result.characteristics = characteristics;
        return result;
    }

    private class PropertiesModel {
        int tid;
        List<EspDeviceCharacteristic> characteristics;
    }
}
