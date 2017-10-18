package com.fenghks.business.print.params;

import java.util.List;
import java.util.Map;

/**
 * 需要打印的参数
 *
 * @author SubLuLu
 */
public class PosParam {

    // 替换模板中占位符的参数
    private Map<String, Object> keys;
    // 商品信息参数集合
    private Map<String,List<Map<String, Object>>> goods;
    //其它详情
    private List<Map<String, Object>> extras;

    public Map<String, Object> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, Object> keys) {
        this.keys = keys;
    }

    public Map<String,List<Map<String, Object>>> getGoods() {
        return goods;
    }

    public void setGoods(Map<String,List<Map<String, Object>>> goods) {
        this.goods = goods;
    }

    public List<Map<String, Object>> getExtras() {
        return extras;
    }

    public void setExtras(List<Map<String, Object>> extras) {
        this.extras = extras;
    }
}
