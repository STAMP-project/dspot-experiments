package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class TypeUtilsTest_interface extends TestCase {
    public void test_castToJavaBean() throws Exception {
        TypeUtilsTest_interface.VO vo = new TypeUtilsTest_interface.VO();
        vo.setId(123);
        vo.setName("abc");
        Assert.assertEquals("{\"ID\":123,\"name\":\"abc\"}", JSON.toJSONString(vo));
    }

    public void test_parse() throws Exception {
        TypeUtilsTest_interface.VO vo = JSON.parseObject("{\"xid\":123,\"name\":\"abc\"}", TypeUtilsTest_interface.VO.class);
        Assert.assertEquals(123, vo.getId());
        Assert.assertEquals("abc", vo.getName());
    }

    public void test_parse_var() throws Exception {
        List<?> list = JSON.parseObject("[]", new com.alibaba.fastjson.TypeReference<List<?>>() {});
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    public void test_deser() throws Exception {
        JSON.parseObject("{\"id\":123}", new com.alibaba.fastjson.TypeReference<TypeUtilsTest_interface.X_I>() {});
    }

    public void test_deser2() throws Exception {
        JSON.parseObject("{\"id\":123}", new com.alibaba.fastjson.TypeReference<TypeUtilsTest_interface.X_X<Integer>>() {});
    }

    public void test_deser2_x() throws Exception {
        JSON.parseObject("{\"id\":123}", new com.alibaba.fastjson.TypeReference<TypeUtilsTest_interface.X_X<?>>() {});
    }

    public static class X_I extends TypeUtilsTest_interface.X<Integer> {}

    public static class X_X<T> extends TypeUtilsTest_interface.X<T> {}

    public static class X<T> {
        private T id;

        public X() {
        }

        public T getId() {
            return id;
        }

        public void setId(T id) {
            this.id = id;
        }
    }

    public static class VO implements TypeUtilsTest_interface.IV {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName(String xx) {
            return null;
        }

        public String getName(String xx, int v) {
            // TODO Auto-generated method stub
            return null;
        }

        @JSONField(deserialize = false)
        public void setName(int value) {
            // TODO Auto-generated method stub
        }

        public void setName(int value, int x) {
            // TODO Auto-generated method stub
        }
    }

    public static interface IV {
        @JSONField(name = "ID")
        int getId();

        @JSONField(name = "xid")
        void setId(int value);

        @JSONField(name = "NAME")
        String getName(String xx);

        @JSONField(name = "NAME")
        String getName(String xx, int v);

        @JSONField(name = "xid_1")
        void setName(int value);
    }
}

