package com.alibaba.json.bvt.bug;


import MapSerializer.instance;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import junit.framework.TestCase;


/**
 * Created by wenshao on 10/01/2017.
 */
public class Issue989 extends TestCase {
    public void test_for_issue() throws Exception {
        TestCase.assertEquals(JSON.toJSONString(Issue989.getMyObject(new HashMap<String, Issue989.Name>())), JSON.toJSONString(Issue989.getMyObject(new TreeMap<String, Issue989.Name>())));
    }

    public static class NameMapCodec implements ObjectSerializer {
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            JSONObject names = new JSONObject();
            for (Map.Entry<String, Issue989.Name> entry : ((Map<String, Issue989.Name>) (object)).entrySet()) {
                Issue989.Name name = entry.getValue();
                names.put(entry.getKey(), (((name.getFirst()) + ":") + (name.getSecond())));
            }
            instance.write(serializer, names, fieldName, JSONObject.class, features);
        }
    }

    public static class MyObject {
        @JSONField(serializeUsing = Issue989.NameMapCodec.class)
        private Map<String, Issue989.Name> names;

        public Map<String, Issue989.Name> getNames() {
            return names;
        }

        public void setNames(Map<String, Issue989.Name> names) {
            this.names = names;
        }
    }

    private static class Name {
        private String first;

        private String second;

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }
    }
}

