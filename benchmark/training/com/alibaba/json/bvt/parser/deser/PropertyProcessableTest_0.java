package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.deserializer.PropertyProcessable;
import java.lang.reflect.Type;
import junit.framework.TestCase;


/**
 * Created by wenshao on 15/07/2017.
 */
public class PropertyProcessableTest_0 extends TestCase {
    public void test_processable() throws Exception {
        PropertyProcessableTest_0.VO vo = JSON.parseObject("{\"vo_id\":123,\"vo_name\":\"abc\",\"value\":{}}", PropertyProcessableTest_0.VO.class);
        TestCase.assertEquals(123, vo.id);
        TestCase.assertEquals("abc", vo.name);
        TestCase.assertNotNull(vo.value);
    }

    public static class VO implements PropertyProcessable {
        public int id;

        public String name;

        public PropertyProcessableTest_0.Value value;

        public Type getType(String name) {
            if ("value".equals(name)) {
                return PropertyProcessableTest_0.Value.class;
            }
            return null;
        }

        public void apply(String name, Object value) {
            if ("vo_id".equals(name)) {
                this.id = ((Integer) (value)).intValue();
            } else
                if ("vo_name".equals(name)) {
                    this.name = ((String) (value));
                } else
                    if ("value".equals(name)) {
                        this.value = ((PropertyProcessableTest_0.Value) (value));
                    }


        }
    }

    public static class Value {}
}

