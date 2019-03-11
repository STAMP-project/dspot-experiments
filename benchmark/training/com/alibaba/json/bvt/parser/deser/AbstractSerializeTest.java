package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class AbstractSerializeTest extends TestCase {
    public void test_mapping_0() throws Exception {
        String text = "{\"@type\":\"com.alibaba.json.bvt.parser.deser.AbstractSerializeTest$A\"}";
        AbstractSerializeTest.B b = ((AbstractSerializeTest.B) (JSON.parse(text)));
        Assert.assertNotNull(b);
    }

    public void test_mapping_1() throws Exception {
        String text = "{\"@type\":\"com.alibaba.json.bvt.parser.deser.AbstractSerializeTest$A\",\"id\":123}";
        AbstractSerializeTest.B b = ((AbstractSerializeTest.B) (JSON.parse(text)));
        Assert.assertNotNull(b);
        Assert.assertEquals(123, b.getId());
    }

    public void test_mapping_2() throws Exception {
        String text = "{\"@type\":\"com.alibaba.json.bvt.parser.deser.AbstractSerializeTest$A\",\"id\":234,\"name\":\"abc\"}";
        AbstractSerializeTest.B b = ((AbstractSerializeTest.B) (JSON.parse(text)));
        Assert.assertNotNull(b);
        Assert.assertEquals(234, b.getId());
        Assert.assertEquals("abc", b.getName());
    }

    public void test_mapping_group() throws Exception {
        String text = "{\"a\":{\"id\":234,\"name\":\"abc\"}}";
        AbstractSerializeTest.G g = JSON.parseObject(text, AbstractSerializeTest.G.class);
        Assert.assertTrue(((g.getA()) instanceof AbstractSerializeTest.B));
    }

    public static class G {
        private AbstractSerializeTest.A a;

        public AbstractSerializeTest.A getA() {
            return a;
        }

        public void setA(AbstractSerializeTest.A a) {
            this.a = a;
        }
    }

    public abstract static class A {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class B extends AbstractSerializeTest.A {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

