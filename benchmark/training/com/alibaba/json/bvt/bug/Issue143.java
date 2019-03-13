package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue143 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "{\"rec\":[{},{}]}";
        Issue143.JsonStroe store = new Issue143.JsonStroe();
        JSONReader reader = new JSONReader(new StringReader(text));
        reader.startObject();
        String key = reader.readString();
        Assert.assertEquals("rec", key);
        reader.startArray();
        List<Issue143.KeyValue> list = new ArrayList<Issue143.KeyValue>();
        while (reader.hasNext()) {
            Issue143.KeyValue keyValue = reader.readObject(Issue143.KeyValue.class);
            list.add(keyValue);
        } 
        store.setRec(list);
        reader.endArray();
        reader.endObject();
        reader.close();
    }

    public static class JsonStroe {
        private List rec = new ArrayList();

        public void setRec(List items) {
            this.rec = items;
        }

        public List getRec() {
            return rec;
        }
    }

    public static class KeyValue {
        private String key;

        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

