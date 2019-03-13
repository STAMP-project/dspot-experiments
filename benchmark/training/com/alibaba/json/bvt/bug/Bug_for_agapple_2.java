package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_agapple_2 extends TestCase {
    public void test_bug() throws Exception {
        Bug_for_agapple_2.DbMediaSource obj = new Bug_for_agapple_2.DbMediaSource();
        obj.setType(Bug_for_agapple_2.DataMediaType.ORACLE);
        JSONObject json = ((JSONObject) (JSON.toJSON(obj)));
        Assert.assertEquals("ORACLE", json.get("type"));
    }

    public static class DbMediaSource {
        private Bug_for_agapple_2.DataMediaType type;

        public Bug_for_agapple_2.DataMediaType getType() {
            return type;
        }

        public void setType(Bug_for_agapple_2.DataMediaType type) {
            this.type = type;
        }
    }

    public static enum DataMediaType {

        ORACLE,
        MYSQL;}
}

