package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 02/07/2017.
 */
public class Issue569 extends TestCase {
    public void test_for_issue() throws Exception {
        String jsonString = "{\"backingMap\":{\"a\":{\"b\":{}}}}";
        Type type = getType();
        Issue569.MyTable<String, String, Issue569.MyValue> table = JSON.parseObject(jsonString, type);
        Map<String, Issue569.MyValue> valueMap = table.backingMap.get("a");
        TestCase.assertNotNull(valueMap);
        Issue569.MyValue value = valueMap.get("b");
        TestCase.assertNotNull(value);
    }

    public static class MyTable<R, C, V> implements Serializable {
        private Map<R, Map<C, V>> backingMap;

        public Map<R, Map<C, V>> getBackingMap() {
            return backingMap;
        }

        public void setBackingMap(Map<R, Map<C, V>> backingMap) {
            this.backingMap = backingMap;
        }
    }

    public static class MyValue {}
}

