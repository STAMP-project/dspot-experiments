package com.alibaba.json.bvt.parser.deser.asm;


import SerializerFeature.UseSingleQuotes;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM2 extends TestCase {
    public void test_0() throws Exception {
        String text = JSON.toJSONString(new TestASM2.V0());
        Assert.assertEquals("{}", text);
    }

    public void test_1() throws Exception {
        String text = JSON.toJSONString(new TestASM2.V1());
        Assert.assertEquals("{\"list\":[]}", text);
    }

    public void test_2() throws Exception {
        TestASM2.V1 v = new TestASM2.V1();
        v.getList().add(3);
        v.getList().add(4);
        String text = JSON.toJSONString(v);
        Assert.assertEquals("{\"list\":[3,4]}", text);
    }

    public void test_3() throws Exception {
        TestASM2.V2 v = new TestASM2.V2();
        v.setId(123);
        v.setName("???");
        String text = JSON.toJSONString(v);
        Assert.assertEquals("{\"id\":123,\"name\":\"\u5218\u52a0\u5927\"}", text);
    }

    public void test_4() throws Exception {
        TestASM2.V2 v = new TestASM2.V2();
        v.setId(123);
        String text = JSON.toJSONString(v);
        Assert.assertEquals("{\"id\":123}", text);
    }

    public void test_7() throws Exception {
        TestASM2.V2 v = new TestASM2.V2();
        v.setId(123);
        String text = JSON.toJSONString(v, WriteMapNullValue);
        Assert.assertEquals("{\"id\":123,\"name\":null}", text);
    }

    public void test_8() throws Exception {
        TestASM2.V3 v = new TestASM2.V3();
        v.setText("xxx");
        String text = JSON.toJSONString(v, UseSingleQuotes);
        Assert.assertEquals("{'text':'xxx'}", text);
    }

    public void test_9() throws Exception {
        TestASM2.V3 v = new TestASM2.V3();
        v.setText("xxx");
        String text = JSON.toJSONString(v, UseSingleQuotes, WriteMapNullValue);
        System.out.println(text);
        Assert.assertEquals(true, (("{'list':null,'text':'xxx'}".equals(text)) || ("{'text':'xxx','list':null}".equals(text))));
    }

    public static class V0 {}

    public static class V1 {
        private List<Integer> list = new ArrayList<Integer>();

        public List<Integer> getList() {
            return list;
        }

        public void setList(List<Integer> list) {
            this.list = list;
        }
    }

    public static class V2 {
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
    }

    public static class V3 {
        private List<Integer> list;

        private String text;

        public List<Integer> getList() {
            return list;
        }

        public void setList(List<Integer> list) {
            this.list = list;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}

