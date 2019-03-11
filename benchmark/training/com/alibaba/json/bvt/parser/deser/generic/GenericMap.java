package com.alibaba.json.bvt.parser.deser.generic;


import com.alibaba.fastjson.JSON;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 20/01/2017.
 */
public class GenericMap extends TestCase {
    public void test_generic() throws Exception {
        GenericMap.Model<GenericMap.User> model = JSON.parseObject("{\"values\":{\"1001\":{\"id\":1001}}}", new com.alibaba.fastjson.TypeReference<GenericMap.Model<GenericMap.User>>() {});
        GenericMap.User user = model.values.get("1001");
        TestCase.assertNotNull(user);
        TestCase.assertEquals(1001, user.id);
    }

    public static class Model<T> {
        public Map<String, T> values;
    }

    public static class User {
        public int id;
    }
}

