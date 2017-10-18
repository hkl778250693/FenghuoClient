package com.fenghks.business;

/**
 * Created by Fei on 2017/1/19.
 */

public class AppConstants {
    /**
     * 正式接口
     */
    /*public static final String BASE_URL = "http://api.fenghks.com:3001"; //正式链接
    public static final String MEITUAN_BASE_URL = "http://api.fenghks.com:8080"; //基本链接
    public static final String ELEME_BASE_URL = "https://api.fenghks.com:8080"; //基本链接*/

    /**
     * 测试接口
     */
    public static final String BASE_URL = "http://service.fenghks.com:3001"; //基本链接测试链接
    public static final String MEITUAN_BASE_URL = "http://service.fenghks.com:8080"; //基本链接
    public static final String ELEME_BASE_URL = "https://service.fenghks.com:8080"; //基本链接

    /*public static final String BASE_URL = "http://192.168.1.108:3001"; //基本链接
    public static final String MEITUAN_BASE_URL = "http://192.168.1.108:8080"; //基本链接
    public static final String ELEME_BASE_URL = "https://192.168.1.108:8080"; //基本链接*/

    /*public static final String MEITUAN_BASE_URL = "http://192.168.66.52:8080"; //基本链接
    public static final String BASE_URL = "http://192.168.66.52:3001"; //基本链接
    public static final String ELEME_BASE_URL = "https://192.168.66.52:8080"; //基本链接*/

    /**
     * 关于云设备打印机的接口地址
     */
    //获取云打印设备所管理的打印机
    public static final String OBTAIN_CLOUD_PRINTER_LIST = "/v1/clouddevice/getPrinters";
    //为云打印设备添加网络打印机
    public static final String ADD_NETWORK_PRINTER = "/v1/clouddevice/addPrinter";
    //修改云打印设备的某个打印机的设置
    public static final String SET_CLOUD_PRINTER = "/v1/clouddevice/setPrinter";
    //删除云打印设备的某个网络打印机
    public static final String DELETE_CLOUD_PRINTER = "/v1/clouddevice/delPrinter";
    //激活/反激活设备
    public static final String ACTIVATE_DEVICE = "/v1/clouddevice/activateCloudDevice";
    //解绑云接单设备
    public static final String UNREGISTER_DEVICE = "/v1/clouddevice/unregisterCloudDevice";
    //获取设备状态
    public static final String OBTAIN_DEVICE_STATE = "/v1/clouddevice/getCloudDeviceStatus";

    /**
     * 关于美团、饿了么的接口地址
     */
    public static final String MEITUAN_AUTH_URL = "https://open-erp.meituan.com/storemap";
    public static final String MEITUAN_UNAUTH_URL = "https://open-erp.meituan.com/releasebinding";
    public static final String MEITUAN_ZIRUZHU_URL = "https://open-erp.meituan.com/ziruzhu";
    //public static final String ELEME_AUTH_URL = "https://open-api-sandbox.shop.ele.me/authorize";
    //public static final String ELEME_AUTH_URL = "https://open-api.shop.ele.me/authorize";

    /**
     * 可作为参数标志（key）
     */
    //是否初始化
    public static final String IS_INITIALIZE = "is_initialize";
    //是否激活
    public static final String IS_ACTIVATED = "is_activated";
    //是否获取打印机列表成功
    public static final String IS_GET_PEINTERS = "is_get_printers";
    //是否设置成功
    public static final String IS_SETTING_PRINTER = "is_setting_printer";
    //是否添加打印机成功
    public static final String IS_ADD_PEINTER = "is_add_printer";
    //是否删除打印机成功
    public static final String IS_DELETE_PRINTER = "is_delete_printer";
    //失败提示
    public static final String NOTE = "note";

    /**
     * 打印机的操作状态常量 （0：正常使用  1：修改中..  2：停用中...  3：删除中...   4：修改失败    5：停用成功    6：停用失败    7：删除失败）
     */
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_SETTING = 1;
    public static final int STATUS_PAUSEING = 2;
    public static final int STATUS_DELETING = 3;
    public static final int STATUS_SET_FAILURE = 4;
    public static final int STATUS_PAUSE_SUCCEED = 5;
    public static final int STATUS_PAUSE_FAILURE = 6;
    public static final int STATUS_DELETE_FAILURE = 7;

    /**
     *  删除或者停用 flag
     */
    public static final int FLAG_DELETE = 0;
    public static final int FLAG_PAUSE = 1;

    /**
     * 网络请求类型 （0：获取打印机订单  1：设置打印机  2：添加打印机  3：删除打印机  ）
     */
    public static final int TYPE_OBTAIN_PRINTERS = 0;
    public static final int TYPE_SET_PRINTER = 1;
    public static final int TYPE_ADD_PRINTER = 2;
    public static final int TYPE_DELETE_PRINTER = 3;


    public static final String SP_APP = "app";
    public static final String SP_ACCOUNTS = "account";
    public static final String SP_SETTINGS = "com.fenghks.business_preferences";

    public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    public static final int FROM_SPLASH = 0;
    public static final int FROM_ACTIVITY = 1;

    /**
     * 订单类别
     */
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
    public static final int ERR_PARAM_MISSING = 10000;
    public static final int ERR_INTERNAL_SERVER = 10001;
    public static final int ERR_INVALID_TOKEN = 10002;
    public static final int ERR_NAME_OR_PWD = 10003;
    public static final int ERR_PARAM = 10004;
    public static final int ERR_ORDER_ALREADY_EXIST = 20001;
    public static final int ERR_ACCOUNT_STOP = 20002;
    public static final int ERR_OUT_OF_WORKTIME = 20003;
    public static final int ERR_ACCOUNT_TYPE = 20011;

    /**
     * Bluetooth 相关
     */
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

    /**
     * 广播action
     */
    //初始化
    public static final String ACTION_INITIALIZE_RESULT = "initialize_result";
    //激活
    public static final String ACTION_ACTIVATE_RESULT = "activate_result";
    //获取打印机列表
    public static final String ACTION_OBTAIN_PRINTERS_RESULT = "obtain_printers_result";
    //设置打印机
    public static final String ACTION_SETTING_PRINTER_RESULT = "setting_printer_result";
    //添加打印机
    public static final String ACTION_ADD_PRINTER_RESULT = "add_printer_result";
    //删除打印机
    public static final String ACTION_DELETE_PRINTER_RESULT = "delete_printer_result";

    /**
     * SPUtil相关key
     */
    public static final String KEY_PREVIOUS_OPERATION_STATE = "previous_operation_state";
    public static final String KEY_CURRENT_UUID = "current_uuid";
    public static final String KEY_PREVIOUS_OPERATION_PARAMS = "previous_operation_params";  //上次网络请求的参数（map集合）
    public static final String KEY_PREVIOUS_OPERATION_URL = "previous_operation_url";  //上次网络请求的地址
    public static final String KEY_PREVIOUS_OPERATION_URL_TYPE = "previous_operation_url_type";  //上次网络请求的类型
}