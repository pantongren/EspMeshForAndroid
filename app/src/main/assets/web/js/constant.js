var TIMER_ID = "",
    WIFI_TIMER = "",
    SCAN_DEVICE = "",
    INSTANCE_TOAST = "",
    TIMER = 0,
    TIMER_DROP = "",
    ECHARTS = "echarts",
    STATISTICS = []
    STATISTICS_TIME = [],
    PARENT_MACS = [],
    ISCROLL_BAR = "",
    CURRSCALE = 1,
    ISCROLL_CHART = "",
    ISCROLL_DEVICE = "",
    ISCROLL_TABLE = "",
    BODY_WIDTH = 0,
    DEVICE_EVENT_FILE = "DEVICEEVENT",
    DEVICE_LIST_FILE = "DEVICEFILE",
    APP_USER_INFO = "APPUSERINFO",
    DEVICE_GROUP_LIST = "DEVICEGROUPLIST",
    COMMAND_LIST_FILE = "commandList";
    INDEX_URL= "file:///android_asset/web/index.html",
    TABLE_URL = "file:///android_asset/web/table.html",
    JSPLUMB_INSTANCE = "",
    JSPLUMB_INITNODE = "",
    LINE_COLOR = "#FEA463",
    TOUCH_SCALE = 1,
    TOUCH_TIME = 0,
    WIFI_SSID = "wifi_ssid",
    DEVICE_LIST = "device_list",
    DEVICE_BOOLEAN = "device_boolean",
    DEVICE_CHILD_LIST = "",
    MIN_LIGHT = 1,
    MAX_LIGHT = 10,
    MIN_SWITCH = 11,
    MAX_SWITCH = 20,
    MIN_SENSOR = 21,
    MAX_SENSOR = 30,
    MIN_OTHER = 31,
    MAX_OTHER = 999999999,
    TOUCH_PAD_SWITCH = 12,
    BUTTON_SWITCH = 13,
    SENSOR_24 = 24,
    STATUS_CID = 0,
    HUE_CID = 1,
    SATURATION_CID = 2,
    VALUE_CID = 3,
    TEMPERATURE_CID = 4,
    BRIGHTNESS_CID = 5,
    SENSOR_TEMPERATURE_CID = 2,
    SENSOR_HUMIDITY_CID = 1,
    SENSOR_LUMINANCE_CID = 0,
    SENSOR_TEMPERATURE_NAME = "temperature",
    SENSOR_HUMIDITY_NAME = "humidity",
    SENSOR_LUMINANCE_NAME = "luminance",
    MODE_CID = 6,
    MODE_HSV = 2,
    MODE_CTB = 3,
    DEFAULT_ON = 255,
    DEFAULT_OFF = 0,
    TOUC_PAD_BTN_0 = 0,
    TOUC_PAD_BTN_1 = 1,
    TOUC_PAD_BTN_2 = 2,
    TOUC_PAD_BTN_3 = 3,
    SYSC_RED_HUE = 0,
    SYSC_RED_SATURATION = 100,
    SYSC_GREEN_HUE = 120,
    SYSC_GREEN_SATURATION = 100,
    SYSC_BLUE_HUE = 240,
    SYSC_BLUE_SATURATION = 100,
    STATUS_ON = 1,
    STATUS_OFF = 0,
    SWITCH_CID = 0,
    SENSOR_CID = 0,
    SENSOR24_CID_3 = 3,
    SENSOR24_CID_2 = 2,
    BUTTON_EVENT_1 = 1,
    BUTTON_EVENT_2 = 2,
    BUTTON_EVENT_3 = 3,
    BUTTON_EVENT_4 = 4,
    BUTTON_EVENT_5 = 5,
    BUTTON_EVENT_6 = 6,
    BUTTON_EVENT_7 = 7,
    BUTTON_EVENT_8 = 8,
    BUTTON_EVENT_9 = 9,
    ON_EN = "ON",
    OFF_EN = "OFF",
    WHITE_EN = "WHITE",
    RED_EN = "RED",
    GREEN_EN = "GREEN",
    BLUE_EN = "BLUE",
    YELLOW_EN = "YELLOW",
    PURPLE_EN = "PURPLE",
    TURN_OFF ="TURN_OFF",
    TURN_ON ="TURN_ON",
    OPERATOR_EQUAL = "==",
    OPERATOR_MIN = ">",
    OPERATOR_MAX = "<",
    OPERATOR_VARIETY = "~",
    OPERATOR_COMPARE = "/",
    SLOW_VALUE = 50,
    MESH_LIGHT_SYSC = {'~': 1},
    MESH_LIGHT_SYSC_COLOR = {'==': 1},
    MESH_LIGHT_SYSC_COLOR_0 = {'==': 0}
    MESH_LIGHT_SYSC_COLOR_2 = {'==': 2},
    MESH_LIGHT_SYSC_COLOR_3 = {'==': 3},
    MESH_LIGHT_SYSC_COLOR_4 = {'==': 4},
    MESH_LIGHT_SYSC_COLOR_5 = {'==': 5},
    MESH_LIGHT_ON_COMPARE = {'==': 1, '~': 1},
    MESH_LIGHT_OFF_COMPARE = {'==': 0, '~': 1},
    MESH_SENSOR_OFF_COMPARE = {'>': 600},
    MESH_SENSOR_ON_COMPARE = {'<': 200},
    MESH_SENSOR_MAX = 1000,
    EVENT_LIST = [],
    MESH_LIST= [],
    MESH_IP = "",
    SINGLE_GROUP = 1,
    MULTIPLE_GROUP = 2,
    DEVICE_IP = "ip",
    MESH_INFO = "mesh_info",
    DEVICE_REQUEST = "device_request",
    DEVICE_INFO = "get_device_info",
    SET_EVENT = "set_event",
    GET_EVENT = "get_event",
    REMOVE_EVENT = "remove_event",
    SET_STATUS = "set_status",
    RESET_DEVICE = "reset",
    RENAME_DEVICE = "rename_device",
    SET_POSITION = "set_position",
    ADD_DEVICE = "add_device",
    GET_IBEACON = "get_ibeacon_config",
    SET_IBEACON = "set_ibeacon_config",
    GET_SNIFFER = "get_sniffer_config",
    SET_SNIFFER = "set_sniffer_config",
    SET_CALENDAR ="set_calendar",
    GTE_CALENDAR ="get_calendar",
    REMOVE_CALENDAR ="remove_calendar",
    GET_MESH = "get_mesh_config",
    SET_REGULAR_CONTROL = "set_regular_control",
    SET_SLOW_SWITCH = "set_slow_switch",
    SYSC = "sync",
    CONTROL = "linkage",
    MESH_HEADER_NUM = 1,
    MESH_REQUEST = "request",
    MESH_CALENDAR = "calendar",
    MESH_MAC = "mac",
    DEVICE_DELAY = "delay",
    NO_RESPONSE = "root_response",
    DELAY_TIME = 3000,
    DEL_WIDTH = 120,
    DEL_HEIGHT = 120,
    DEVICE_WIDTH = 80,
    DEVICE_HEIGHT = 30,
    DEVICE_TOP = 0,
    DEVICE_LEFT = 0,
    INIT_SIZE = 260;
    DELAY_TIME = 30,
    EVENT_TYPE = ["sync", "linkage"],
    LIGHT_CID_LIST = [0, 1, 2, 3, 4 , 5],
    SWITCH_CID_LIST = [0],
    SWITCH_TOUCH_CID_LIST = [0, 1, 2, 3],
    SENSOR_CID_LIST = [0],
    COL_NUM = 5,
    ROW_NUM = 5,
    POSITION_TOP = 0,
    POSITION_LEFT = 0,
    EXEC_FREQ = 1,
    RECENT_TYPE_DEVICE = "device",
    BIN_URL = "https://raw.githubusercontent.com/XuXiangJun/test/master/light.bin",
    SNIFFER_CLASS = [{"value":"0", "label": "Disable"}, {"value":"1", "label": "Enable"}],
    SNIFFER_TYPE = [{"value":"2", "label": "WiFi"}, {"value":"4", "label": "Ble"}, {"value":"6", "label": "All"}],
    SNIFFER_FILTER = [{"value":"0", "label": "No"}, {"value":"1", "label": "Yes"}],
    RECENT_TYPE_GROUP = "group",
    HOLIDAY = ["2018-1-1", "2018-1-15", "2018-1-16", "2018-1-17", "2018-1-18", "2018-1-19", "2018-1-20", "2018-1-21",
            "2018-4-5", "2018-4-6", "2018-4-7", "2018-4-29", "2018-4-30", "2018-5-1", "2018-6-16", "2018-6-17", "2018-6-18",
            "2018-9-22","2018-9-23", "2018-9-24", "2018-10-1", "2018-10-2", "2018-10-3", "2018-10-4", "2018-10-5", "2018-10-6",
            "2018-10-7"],
    OVERTIME = ["2018-1-24", "2018-4-8", "2018-4-28", "2018-9-29", "2018-9-30"],
    BUTTON_DEVICES = {"upleft": 0, "upright": 1, "downleft": 2, "downright": 3},
    SENSOR24_COLOR = {"white": {"h": 0, "s": 0, "v": 100}, "red": {"h": 0, "s": 100, "v": 100},
        "green": {"h": 120, "s": 100, "v": 100}, "blue": {"h": 240, "s": 100, "v": 100},
        "yellow": {"h": 60, "s": 100, "v": 100}, "purple": {"h": 300, "s": 100, "v": 50}};
