require.config({
    paths : {
        jQuery : 'jquery/jquery.min',
        IScroll: 'jquery/iscroll',
        "vue":"vue/vue.min",
        "vueRouter":"vue/vue-router.min",
        'jquery.ui' : 'jquery/jquery-ui.min',
        'jquery.ui.touch-punch' : 'jquery/jquery.ui.touch-punch.min',
        //"ELEMENT":"vue/ELEMENT",
        "MINT":"vue/mint-ui",
        "Vuex":"vue/vuex.min",
        "touch":"vue/vue-touch",
        "txt":"vue/text",
        'layer':'vue/layer',
        "routers":"../app/js/router",
        "i18n":"vue/vue-i18n.min",
        "Util":"utils",
         "zh":"../lang/zh",
         "en":"../lang/en"
    },
    shim:{
        'layer':['jQuery'],
        'base':['jQuery','layer'],
        "jquery.ui" : ["jQuery"],
        "jquery.ui.touch-punch" : ["jQuery", "jquery.ui"],
    }
});
require(["IScroll", "jQuery", "vue", "vueRouter", "MINT", "Util", "routers", "touch", "Vuex", "i18n", "zh", "en", "jquery.ui", "jquery.ui.touch-punch"],
    function(IScroll, $, Vue, VueRouter, MINT, Util, routers, touch, Vuex, VueI18n, zh, en) {
    Vue.use(VueRouter);
    //Vue.use(ELEMENT);
    Vue.use(MINT);
    Vue.use(Vuex);
    Vue.use(VueI18n);
    document.oncontextmenu=new Function("event.returnValue=false");
    document.onselectstart=new Function("event.returnValue=false");
    var router = new VueRouter({
        routes: routers
    });

    router.beforeEach(function(to, from, next) {
        var userInfo = espmesh.userLoadLastLogged();
        userInfo = JSON.parse(userInfo);
        if(userInfo == null || userInfo == "" || userInfo.status != 0){//如果有就直接到首页咯
            espmesh.userGuestLogin();
        }
        next();
    });
    var store = new Vuex.Store({
        state: {
            deviceList: [],
            groupList: [],
            mixList: [],
            deviceInfo: "",
            userName: "",
            searchName:"",
            scanDeviceList: [],
            conScanDeviceList: [],
            siteList: [],
            wifiInfo: "",
            rssiInfo: -80,
            showScanBle: true,
            deviceIp: "",
        },
        mutations: {
            setList: function(state, list){
                state.deviceList = list;
            },
            setGroupList: function(state, list){
                state.groupList = list;
            },
            setRecentList: function(state, list){
                state.mixList = list;
            },
            setUserName: function(state, name){
                state.userName = name;
            },
            setDeviceInfo: function(state, info){
                state.deviceInfo = info;
            },
            setWifiInfo: function(state, info){
                state.wifiInfo = info;
            },
            setScanDeviceList: function(state, info){
                state.scanDeviceList = info;
            },
            setConScanDeviceList: function(state, info){
                state.conScanDeviceList = info;
            },
            setSiteList: function(state, info){
                state.siteList = info;
            },
            setRssi: function(state, info){
                state.rssiInfo = info;
            },
            setShowScanBle: function(state, info){
                state.showScanBle = info;
            },
            setDeviceIp: function(state, info) {
                state.deviceIp = info;
            }
        }
    });
    var i18n = new VueI18n({
        locale: "zh",
        messages: {
            'zh': zh.m,   // 中文语言包
            'en': en.m    // 英文语言包
        }
    })
    var app = new Vue({
        el: "#app",
        i18n: i18n,
        store: store,
        router: router,
        mounted: function() {
            window.onLocaleGot = this.onLocaleGot;
            espmesh.getLocale();
        },
        methods: {
            onLocaleGot: function(res) {
                res = JSON.parse(res);
                if (res.language == "zh") {
                    this.$i18n.locale = "zh";
                } else {
                    this.$i18n.locale = "en";
                }
            }
        }
    });
    touch.VueTouch.setVue(Vue);
});
