package com.alibaba.json.bvt.bug;


import Feature.IgnoreNotMatch;
import SerializerFeature.QuoteFieldNames;
import SerializerFeature.SkipTransientField;
import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue_748 extends TestCase {
    public void testJsonObjectWithClassName() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", "value1");
        jsonObject.put("key2", "value2");
        Issue_748.DataObject dataObject = new Issue_748.DataObject();
        dataObject.setValue(jsonObject);
        String jsonStr = JSON.toJSONString(dataObject, QuoteFieldNames, SkipTransientField, WriteClassName);
        // System.out.println("parse??:" + jsonStr);
        Issue_748.DataObject obj = ((Issue_748.DataObject) (JSON.parse(jsonStr, IgnoreNotMatch)));
        Assert.assertNotNull(obj.value);
        Assert.assertNotNull(obj.value.get("key1"));
        Assert.assertNotNull(obj.value.get("key2"));
    }

    public static class DataObject {
        private JSONObject value;

        public DataObject() {
        }

        public JSONObject getValue() {
            return value;
        }

        public void setValue(JSONObject value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return (("DataObject{" + "value=") + (value)) + '}';
        }
    }
}

