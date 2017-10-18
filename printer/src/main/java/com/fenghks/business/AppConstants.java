package com.fenghks.business;

/**
 * Created by Fei on 2017/1/19.
 */

public class AppConstants {

    /*public static final String BASE_URL = "http://api.fenghks.com:3001"; //正式链接
    public static final String MEITUAN_BASE_URL = "http://api.fenghks.com:8080"; //基本链接
    public static final String ELEME_BASE_URL = "https://api.fenghks.com:9090"; //基本链接*/

    public static final String BASE_URL = "http://service.fenghks.com:3001"; //基本链接测试链接
    public static final String MEITUAN_BASE_URL = "http://service.fenghks.com:8080"; //基本链接
    public static final String ELEME_BASE_URL = "https://service.fenghks.com:9090"; //基本链接

    /*public static final String BASE_URL = "http://192.168.1.104:3001"; //基本链接
    public static final String MEITUAN_BASE_URL = "http://192.168.1.104:8080"; //基本链接
    public static final String ELEME_BASE_URL = "https://192.168.1.104:9090"; //基本链接*/

    /*public static final String MEITUAN_BASE_URL = "http://192.168.66.52:8080"; //基本链接
    public static final String BASE_URL = "http://192.168.66.52:3001"; //基本链接
    public static final String ELEME_BASE_URL = "https://192.168.66.52:9090"; //基本链接*/

    //注册盒子(云接单设备)
    public static final String REGISTER_BOX = "/v1/clouddevice/cloudDeviceRegister";
    //解绑云接单设备
    public static final String UNREGISTER_BOX = "/v1/clouddevice/unregisterCloudDevice";
    //获取订单列表接口地址
    public static final String ORDER_LIST_URL = "/v1/business/listOrders";
    //消息上报接口
    public static final String MSG_REPORT = "/v1/clouddevice/cloudDeviceReport";
    //获取店铺今日订单ID列表
    public static final String OBTAIN_ORDER_IDS = "/v1/business/listOrderIds";
    //根据订单号获取店铺订单列表
    public static final String ORDER_LIST_BY_IDS = "/v1/business/listOrderByIds";
    //获取设备状态
    public static final String OBTAIN_DEVICE_STATE = "/v1/clouddevice/getCloudDeviceStatus";



    public static final String MEITUAN_AUTH_URL = "https://open-erp.meituan.com/storemap";
    public static final String MEITUAN_UNAUTH_URL = "https://open-erp.meituan.com/releasebinding";
    public static final String MEITUAN_ZIRUZHU_URL = "https://open-erp.meituan.com/ziruzhu";
    //public static final String ELEME_AUTH_URL = "https://open-api-sandbox.shop.ele.me/authorize";
    //public static final String ELEME_AUTH_URL = "https://open-api.shop.ele.me/authorize";

    //是否初始化
    public static final String IS_INITIALIZE = "is_initialize";
    public static final String IS_ACTIVATED = "is_activated";

    public static final String SP_APP = "app";
    public static final String SP_ACCOUNTS = "account";
    public static final String SP_SETTINGS = "com.fenghks.printer_preferences";

    public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    public static final int FROM_SPLASH = 0;
    public static final int FROM_ACTIVITY = 1;

    /**
     *  前台服务的ID
     */
    public static final int SERVICE_REGISTER_ID = 1;
    public static final int SERVICE_ORDER_ID = 2;

    /**
     * 打印机类型
     */
    public static final int PRINTER_TYPE_NETWORK = 1;
    public static final int PRINTER_TYPE_USB = 2;

    /**
     *  全局变量的key
     */
    public static final String APP_XG_TOKEN = "app_xg_token";
    public static final String APP_SHOP_ID = "app_shop_id";
    public static final String DEVICE_XG_TOKEN = "device_xg_token";
    public static final String DEVICE_TOKEN = "device_token";
    public static final String DEVICE_MAC_ADDRESS = "device_mac_address";
    static final String ASK_FOR_AUTO_RUN = "ask_for_auto_run";
    public static final String PRINTER_LIST = "printer_list";   //打印机列表
    public static final String PRINT_ORDER_ID_LIST = "print_order_id_list";    //打印过的订单id列表
    public static final String LAST_ORDER_ID = "last_order_id";  //最新保存的订单ID

    /**
     * 打印机的操作状态常量 （0：正常使用  1：已掉线 ）
     */
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_OFF_LINE = 1;


    public static final int ON_TIME_ORDER = 0;//即时单
    public static final int PREORDER_ORDER = 1;//预订单
    public static final int ALL_ORDER = -1;//全部订单
    public static final int WAITING_ORDER = -2;//待取餐

    public static final int WAITING_PLACE_ORDER = 0;//等待下单
    public static final int WAITING_ASSIGN_ORDER = 1;//等待分配
    public static final int WAITING_TAKE_ORDER = 2;//等待取餐
    public static final int TRANSPORTING_ORDER = 3;//派送中
    public static final int COMPLETE_ORDER = 4;//已完成
    public static final int ABNORMAL_ORDER = 5;//异常
    public static final int CHARGEBACK_ORDER = 6;//退单
    /*
        public static final String PREORDER_ORDER = "preorder_order";//预订单
        public static final String WAITING_ORDER = "waiting_order";//待取餐
        public static final String WAITING_PLACE_ORDER = "waiting_place_order";//等待下单
        public static final String WAITING_ASSIGN_ORDER = "waiting_assign_order";//等待分配
        public static final String WAITING_TAKE_ORDER = "waiting_take_order";//等待取餐
        public static final String TRANSPORTING_ORDER = "transporting_order";//派送中
        public static final String COMPLETE_ORDER = "complete_order";//已完成
        public static final String ABNORMAL_ORDER = "abnormal_order";//异常
        public static final String CHARGEBACK_ORDER = "chargeback_order";//退单
    */
    public static final int ERR_PARAM_MISSING = 10000;     //缺少必须的参数
    public static final int ERR_INTERNAL_SERVER = 10001;   //服务器内部错误
    public static final int ERR_INVALID_TOKEN = 10002;     //token过期或失效
    public static final int ERR_NAME_OR_PWD = 10003;       //用户名或密码错误
    public static final int ERR_PARAM = 10004;             //当天没有排班
    public static final int ERR_ORDER_ALREADY_EXIST = 20001;  //订单信息已存在
    public static final int ERR_ACCOUNT_STOP = 20002;         //客户帐号被停用
    public static final int ERR_OUT_OF_WORKTIME = 20003;      //店铺处于设定的工作时间之外
    public static final int ERR_ACCOUNT_TYPE = 20011;         //登陆的账号类型错误

    /** Bluetooth */
    // Message types sent from the PrintService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String EXTRA_ORDER = "bt_extra_order";
    public static final String EXTRA_QR = "bt_extra_qr";
    public static final String EXTRA_QR_STR = "bt_extra_qr_str";
    public static final String EXTRA_PRINT_SETTINGS = "bt_extra_print_settings";
    public static final String EXTRA_PRINTER_ADDRESS = "bt_extra_printer_address";

    public static final String ACTION_ORDER_CREATED = "new_order_created";
    public static final String ACTION_ORDER_STATE_CHANGED = "order_state_changed";
    public static final String ACTION_RECEIVER_UPDATE = "new_version_available";
    public static final String ACTION_RECEIVER_MSG = "new_msg_received";

}