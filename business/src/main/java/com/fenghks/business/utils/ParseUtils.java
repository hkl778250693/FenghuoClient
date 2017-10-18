package com.fenghks.business.utils;

import android.content.Context;

import com.fenghks.business.AppConstants;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.bean.Business;
import com.fenghks.business.bean.Order;
import com.fenghks.business.bean.OrderItem;
import com.fenghks.business.bean.Printer;
import com.fenghks.business.bean.Sender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fei on 2016/10/23.
 */

public class ParseUtils {
    public static JSONObject parseDataString(Context context,String json) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
            if (0 == obj.optInt("status")) {
                return obj.optJSONObject("data");
            }else if (10002 == obj.optInt("status")) {
                CommonUtil.autoLogin(context,1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseMsg(String json) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
            return obj.optString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray parseDataArray(Context context,String json) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
            if (0 == obj.optInt("status")) {
                return obj.optJSONArray("data");
            } else if (10002 == obj.optInt("status")) {
                CommonUtil.autoLogin(context,1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Business parseBusiness(JSONObject obj) {
        Business item = new Business(obj.optInt("id"), obj.optInt("areaid"), obj.optInt("groupid"),
                obj.optInt("parentid"), obj.optString("code"), obj.optString("name"),
                obj.optString("phone"), obj.optString("address"), obj.optString("headimg"),
                obj.optDouble("lon"), obj.optDouble("lat"), obj.optDouble("surplus"), obj.optInt("readytime"),
                obj.optString("startworktime"), obj.optString("endworktime"));
        return item;
    }

    /**
     * 解析打印机实体类
     *
     * @param obj
     * @return
     */
    public static Printer parsePrinter(JSONObject obj) {
        Printer item = new Printer(FenghuoApplication.currentShopID, obj.optString("ip"), obj.optInt("port"), obj.optString("name"),
                obj.optInt("businessNum"), obj.optInt("kitchenNum"), obj.optInt("customerNum"),
                obj.optInt("senderNum"), obj.optInt("qrcode"), obj.optInt("ispause"), obj.optInt("printertype"), AppConstants.STATUS_NORMAL);
        return item;
    }

    public static Sender parseSender(JSONObject obj) {
        Sender item = new Sender(obj.optInt("id"), obj.optInt("areaid"), obj.optInt("groupid"),
                obj.optString("name"), obj.optString("phone"), obj.optString("headimg"), obj.optDouble("lat"),
                obj.optDouble("lon"), obj.optString("token"), obj.optString("registrationid"));
        return item;
    }

    public static Order parseOrder(JSONObject obj) {
        Order order = new Order(obj.optInt("orderid"), obj.optInt("areaid"), obj.optInt("businessid"),
                obj.optInt("senderid", 0), obj.optString("firecode", ""), obj.optString("ordercode", ""),
                obj.optString("clientfrom"), obj.optString("customername"), obj.optString("customerphone"),
                obj.optString("sendaddress"), obj.optDouble("totalprice", 0.0), obj.optDouble("originalfee", 0.0),
                obj.optDouble("sendfee", 0.0), obj.optDouble("lon", 0.0), obj.optDouble("lat", 0.0),
                obj.optInt("state"), obj.optInt("distance", 0), obj.optInt("timeconsuming", 0),
                obj.optInt("urgenum", 0), obj.optInt("ismerge", 1), obj.optString("createtime"),
                obj.optString("sendtime"), obj.optString("starttime"), obj.optString("gettime"),
                obj.optString("arrivedtime"), obj.optString("validatecode"), obj.optString("getcode"),
                obj.optInt("timelimit", 0), obj.optString("qrcode"), obj.optInt("isdelete", 0),
                obj.optString("note", ""), obj.optString("exceptionresult", ""), obj.optString("tag", ""),
                obj.optInt("mileage", 0), obj.optInt("createtype", 2), obj.optInt("isdoexception", 0),
                obj.optInt("issenderover", 0), obj.optInt("orderType", 0));
        JSONArray details = obj.optJSONArray("itemDetails");
        if (details != null) {
            Map<Integer, List<OrderItem>> map = new HashMap<>();
            OrderItem item = null;
            for (int i = 0; i < details.length(); i++) {
                item = parseOrderItem(details.optJSONObject(i));
                if (map.size() < item.getCartid() + 1) {
                    List<OrderItem> items = new ArrayList<>();
                    map.put(item.getCartid(), items);
                }
                map.get(item.getCartid()).add(item);
                //items.add(parseOrderItem(details.optJSONObject(i)));
            }
            order.setItems(map);
        }
        JSONArray extras = obj.optJSONArray("extras");
        if (extras != null) {
            List<OrderItem> items = new ArrayList<>();
            for (int i = 0; i < extras.length(); i++) {
                items.add(parseOrderItem(extras.optJSONObject(i)));
            }
            order.setExtras(items);
        }
        return order;
    }

    //解析菜单详情和配送费用等信息
    public static Order parseOrderDetails(JSONObject obj) {
        Order order = new Order();

        JSONArray details = obj.optJSONArray("itemDetails");
        if (details != null) {
            Map<Integer, List<OrderItem>> map = new HashMap<>();
            OrderItem item = null;
            for (int i = 0; i < details.length(); i++) {
                item = parseOrderItem(details.optJSONObject(i));
                if (map.size() < item.getCartid() + 1) {
                    List<OrderItem> items = new ArrayList<>();
                    map.put(item.getCartid(), items);
                }
                map.get(item.getCartid()).add(item);
                //items.add(parseOrderItem(details.optJSONObject(i)));
            }
            order.setItems(map);
        }
        JSONArray extras = obj.optJSONArray("extras");
        if (extras != null) {
            List<OrderItem> items = new ArrayList<>();
            for (int i = 0; i < extras.length(); i++) {
                items.add(parseOrderItem(extras.optJSONObject(i)));
            }
            order.setExtras(items);
        }
        return order;
    }

    //type:0正常菜品，1其它费用
    public static OrderItem parseOrderItem(JSONObject obj) {
        OrderItem item = new OrderItem(obj.optInt("id"), obj.optInt("orderid"), obj.optString("name"),
                obj.optInt("amount"), obj.optDouble("price"), obj.optDouble("totalprice", 0.0),
                obj.optString("note", ""), obj.optString("rawdata", ""), obj.optInt("cartid", 0),
                obj.optInt("itemtype", 0));
        return item;
    }

    /*public static ArticleItem parseRemind(String index_json){
        ArticleItem item = null;
        try {
            JSONObject indexObj = new JSONObject(index_json);
            JSONObject obj  = indexObj.optJSONArray("zygzlist").getJSONObject(0);
            item = parseArticle(obj);
        } catch (Exception e){
            e.printStackTrace();
        }
        return item;
    }

    public static ArticleItem parseRemind(JSONObject indexObj){
        ArticleItem item = null;
        try {
            JSONObject obj  = indexObj.optJSONArray("zygzlist").getJSONObject(0);
            item = parseArticle(obj);
        } catch (Exception e){
            e.printStackTrace();
        }
        return item;
    }

    public static ArticleItem parseArticle(String json){
        ArticleItem item = null;
        try {
            JSONObject obj = new JSONObject(json);
            item = new ArticleItem(obj.optInt("articleid"),obj.optString("mainpicture"),
                    obj.optString("title"),null,obj.optString("metaDes"),
                    obj.optString("createtime"),obj.optString("contentvalue"),
                    obj.optString("readcount"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return item;
    }

    public static ArticleItem parseArticle(JSONObject obj){
        ArticleItem item = null;
        item = new ArticleItem(obj.optInt("articleid"),obj.optString("mainpicture"),
                obj.optString("title"),null,obj.optString("metaDes"),
                obj.optString("createtime"),obj.optString("contentvalue"),
                obj.optString("readcount"));
        return item;
    }

    public static ContactItem parseContact(String json){
        ContactItem item = null;
        try {
            JSONObject obj = new JSONObject(json);
            item = parseContact(obj);
            *//*
            {
            "id": "1",
            "name": "张三",
            "mobile": "15236523652",
            "postname": "管理员",
            "img": "",
            "districtid": "1",
            "streetid": "1",
            "gridid": "1",
            "address": ""
            }
             *//*
        } catch (Exception e){
            e.printStackTrace();
        }
        return item;
    }

    public static ContactItem parseContact(JSONObject obj){
        ContactItem item = null;
        item = new ContactItem(obj.optInt("id"),obj.optString("name"),
                obj.optString("mobile"),obj.optString("postname"),
                obj.optString("img"),obj.optInt("districtid"),
                obj.optInt("streetid"),obj.optInt("gridid"),obj.optString("address"));
        return item;
    }

    public static List<ContactItem> parseContactsList(String json){
        *//*
        "data":[{"id":"2","name":"张老师","mobile":"15236523652","postname":"消防安全岗",
        "img":"/uploads/20161012/ed8fad87_7db9_4c9f_bf42_3d105f728026_00000000.png","districtid":"7",
        "streetid":"1","gridid":"1","address":"重庆市九龙坡区渝州路派出所"},{"id":"3","name":"李老师",
        "mobile":"1523658456","postname":"消防管理员",
        "img":"/uploads/20161012/ed8fad87_7db9_4c9f_bf42_3d105f728026_00000001.jpg","districtid":"7",
        "streetid":"1","gridid":"1","address":"重庆市九龙坡区消防指挥中心"}]
        *//*
        List<ContactItem> list = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray contactsListArray = obj.optJSONArray("data");
            for(int i = 0;i<contactsListArray.length();i++){
                list.add(parseContact(contactsListArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<ContactItem> parseContactsList(JSONArray jsonArray){
        *//*
        [{"id":"1","title":"消防安全每月常规检查","totalcount":"122",
        "finishcount":"9","description":"消防安全每月常规检查","img":""},
        {"id":"2","title":"餐饮行业煤气使用专项检查","totalcount":"122",
        "finishcount":"0","description":"餐饮行业煤气使用专项检查","img":""}]
        *//*
        List<ContactItem> list = new ArrayList<>();
        try {
            for(int i = 0;i<jsonArray.length();i++){
                list.add(parseContact(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static SecurityCheckItem parseCheckItem(String json){
        SecurityCheckItem item = null;
        try {
            JSONObject obj = new JSONObject(json);
            *//*
            {
            "id": "1",
            "title": "消防安全每月常规检查",
            "totalcount": "110",
            "finishcount": "10",
            "description": "消防安全每月常规检查",
            "img": ""
            }
             *//*
            item = new SecurityCheckItem(obj.optInt("id"),obj.optString("title"),
                    obj.optInt("totalcount"),obj.optInt("finishcount"),
                    obj.optString("description"),obj.optString("img"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return item;
    }

    public static SecurityCheckItem parseCheckItem(JSONObject obj){
        SecurityCheckItem item = null;
        item = new SecurityCheckItem(obj.optInt("id"),obj.optString("title"),
                obj.optInt("totalcount"),obj.optInt("finishcount"),
                obj.optString("description"),obj.optString("img"));
        return item;
    }

    public static UserItem parseUser(String json){
        *//*
    {"postid":"1","gridid":"1","phone":"15236548545","roleid":"4",
    "gridName":"渝州路渝州二社区","img":"","userid":"17","streetName":"石桥铺",
    "provinceid":"s-cq","userType":"1","cityid":"1","streetid":"1","districtName":"九龙坡",
    "postName":"管理员","partid":"1","userName":"网格员","districtid":"7"}
    {"status":"0","data":{"postid":5,"gridid":"","phone":"13222222222","roleid":6,"gridName":"",
    "img":"/uploads/20161116/51412e1d_e2d9_4dc7_9462_bbf6b45725b8_00000012.jpg","userid":72,
    "streetName":"杨家坪街道","provinceid":"","villagename":"Android社区1","userType":1,"cityid":"",
    "streetid":"1","districtName":"九龙坡","postName":"街道管理员","partid":2,"villageid":"4",
    "userName":"安卓社区人员1","districtid":"7"},"msg":"操作成功"}
     *//*
        UserItem item = null;
        try {
            JSONObject obj = new JSONObject(json);
            item = new UserItem(obj.optInt("postid"),obj.optInt("userid"),obj.optInt("roleid"),
                    obj.optInt("userType"),obj.optString("provinceid"),obj.optInt("cityid"),
                    obj.optInt("districtid"),obj.optInt("streetid"),obj.optInt("villageid"),obj.optInt("gridid"),
                    obj.optInt("partid"),obj.optString("userName"),obj.optString("phone"),
                    obj.optString("postName"),obj.optString("districtName"),obj.optString("streetName")
                    ,obj.optString("villagename"),obj.optString("gridName"),obj.optString("img"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return item;
    }

    public static UserItem parseUser(JSONObject obj){
        *//*
    {"postid":"1","gridid":"1","phone":"15236548545","roleid":"4",
    "gridName":"渝州路渝州二社区","img":"","userid":"17","streetName":"石桥铺",
    "provinceid":"s-cq","userType":"1","cityid":"1","streetid":"1","districtName":"九龙坡",
    "postName":"管理员","partid":"1","userName":"网格员","districtid":"7"}
     *//*
        UserItem item = null;
        try {
            item = new UserItem(obj.optInt("postid"),obj.optInt("userid"),obj.optInt("roleid"),
                    obj.optInt("userType"),obj.optString("provinceid"),obj.optInt("cityid"),
                    obj.optInt("districtid"),obj.optInt("streetid"),obj.optInt("villageid"),obj.optInt("gridid"),
                    obj.optInt("partid"),obj.optString("userName"),obj.optString("phone"),
                    obj.optString("postName"),obj.optString("districtName"),obj.optString("streetName")
                    ,obj.optString("villagename"),obj.optString("gridName"),obj.optString("img"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return item;
    }

    public static List<SecurityCheckItem> parseCheckList(String json){
        *//*
        "data":[{"id":"1","title":"消防安全每月常规检查","totalcount":"122",
        "finishcount":"9","description":"消防安全每月常规检查","img":""},
        {"id":"2","title":"餐饮行业煤气使用专项检查","totalcount":"122",
        "finishcount":"0","description":"餐饮行业煤气使用专项检查","img":""}]
        *//*
        List<SecurityCheckItem> list = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray checkListArray = obj.optJSONArray("data");
            for(int i = 0;i<checkListArray.length();i++){
                list.add(parseCheckItem(checkListArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<SecurityCheckItem> parseCheckList(JSONArray jsonArray){
        *//*
        [{"id":"1","title":"消防安全每月常规检查","totalcount":"122",
        "finishcount":"9","description":"消防安全每月常规检查","img":""},
        {"id":"2","title":"餐饮行业煤气使用专项检查","totalcount":"122",
        "finishcount":"0","description":"餐饮行业煤气使用专项检查","img":""}]
        *//*
        List<SecurityCheckItem> list = new ArrayList<>();
        try {
            for(int i = 0;i<jsonArray.length();i++){
                list.add(parseCheckItem(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<ArticleItem> parseArticleList(JSONArray jsonArray){
        *//*

        *//*
        List<ArticleItem> list = new ArrayList<>();
        try {
            for(int i = 0;i<jsonArray.length();i++){
                list.add(parseArticle(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static SelectItem parseSelectItem(JSONObject obj){
        SelectItem item = null;
        item = new SelectItem(obj.optString("id"),obj.optString("name"),
                obj.optString("seltype"),obj.optString("findtype"));
        return item;
    }

    public static SelectItem parseSelectItem(String json){
        SelectItem item = null;
        try {
            JSONObject obj = new JSONObject(json);
            item = parseSelectItem(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static CheckRecordItem parseCheckRecordItem(JSONObject obj){
        *//*  {"id":"19","resultStatus":"1","title":"发轩造型",
        "phone":"","address":"华轩支路35号","createtime":"2016-10-31 17:06:59",
        "punishCounts":[{"punish_status":0,"punish_counts":3}]}
     *//*
        int numPunish0 = 0;
        int numPunish1 = 0;
        int numPunish2 = 0;
        JSONArray punishCounts = obj.optJSONArray("punishCounts");
        boolean hasPunish = false;
        if(null != punishCounts){
            JSONObject punishObj = null;
            for (int i = 0;i<punishCounts.length(); i++){
                punishObj = punishCounts.optJSONObject(i);
                int punish_counts = punishObj.optInt("punish_counts");
                switch (punishObj.optInt("punish_status")) {
                    case 0:
                        numPunish0 = punish_counts;
                        break;
                    case 1:
                        numPunish1 = punish_counts;
                        break;
                    case 2:
                        numPunish2 = punish_counts;
                        break;
                }
            }
            if(punishCounts.length()>0){
                hasPunish = true;
            }
        }
        CheckRecordItem item = new CheckRecordItem(obj.optInt("id"),obj.optString("title"),
                obj.optString("address"),obj.optString("createtime"),obj.optString("phone"),
                numPunish0,numPunish1,numPunish2,hasPunish);
        return item;
    }

    public static CheckRecordItem parseCheckRecordItem(String json){
        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(null != obj){
            return parseCheckRecordItem(obj);
        }else {
            return null;
        }
    }

    public static List<CheckRecordItem> parseCheckRecordList(JSONArray jsonArray){
        List<CheckRecordItem> list = new ArrayList<>();
        try {
            for(int i = 0;i<jsonArray.length();i++) {
                list.add(parseCheckRecordItem(jsonArray.getJSONObject(i)));
            }
            Collections.sort(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Map<String,String> parseFilterItem(JSONObject obj){
        Map<String,String> map = new HashMap<>();
        map.put("id",obj.optString("id"));
        map.put("name",obj.optString("name"));
        return map;
    }

    public static List<Map<String,String>> parseFilterList(JSONArray jsonArray){
        List<Map<String,String>> list = new ArrayList<>();
        try {
            for(int i = 0;i<jsonArray.length();i++) {
                list.add(parseFilterItem(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static BusinessPlaceItem parseBusinessPlaceItem(JSONObject obj){
        *//*
        {"id":"1","name":"华西食堂","linkman":"温江","linkphone":"68165451",
        "address":"华轩支路47号","type":"餐饮","managerType":"0"}
         *//*
        BusinessPlaceItem item = null;
        try {
            item = new BusinessPlaceItem(obj.optInt("id"),obj.optString("name"),
                    obj.optString("address"),obj.optString("linkman"),
                    obj.optString("linkphone"),obj.optString("type"),obj.optString("managerType"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return item;
    }
    public static List<BusinessPlaceItem> parseBusinessPlaceList(JSONArray jsonArray){
        List<BusinessPlaceItem> list = new ArrayList<>();
        try {
            for(int i = 0;i<jsonArray.length();i++) {
                list.add(parseBusinessPlaceItem(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static BusinessPlaceDetailItem parseBusinessPlaceDetailItem(JSONObject obj){
        *//*
    {"address":"华轩支路45号","buildingId":1,"createtime":"2016-09-30 09:51:19","createuserid":null,
    "descInfo":"","dutyman":"","dutyphone":"","finishtime":"","floors":1,"gridId":0,"id":2,
    "lastchecktime":"2016-10-24 23:24:20","lastcheckuserid":17,"lasttopicid":1,"latitude":-122.40001,
    "linkman":"谭伟","linkphone":"15723021523","longitude":37.791485,"managerType":1,"name":"兴泰超市",
    "placeArea":40.0,"saletype":1,"shopname":null,"starttime":"","status":0,
    "updatetime":"2016-09-30 09:51:19","updateuserid":null,"usetypeId":1}
     *//*
        BusinessPlaceDetailItem item = null;
        try {
            item = new BusinessPlaceDetailItem(obj.optInt("id"),obj.optString("name"),
                    obj.optString("address"),obj.optInt("status"),obj.optString("dutyman"),
                    obj.optString("dutyphone"),obj.optString("linkman"),
                    obj.optString("linkphone"),obj.optString("descInfo"),
                    obj.optInt("saletype"),obj.optInt("managerType"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return item;
    }*/
}
