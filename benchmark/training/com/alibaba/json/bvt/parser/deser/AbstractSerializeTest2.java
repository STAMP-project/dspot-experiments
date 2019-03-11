package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;
import org.junit.Assert;


public class AbstractSerializeTest2 extends TestCase {
    public void test_mapping_0() throws Exception {
        String text = "{\"@type\":\"com.alibaba.json.bvt.parser.deser.AbstractSerializeTest2$A\"}";
        AbstractSerializeTest2.B b = ((AbstractSerializeTest2.B) (JSON.parse(text)));
        Assert.assertNotNull(b);
    }

    public void test_mapping_1() throws Exception {
        String text = "{\"@type\":\"com.alibaba.json.bvt.parser.deser.AbstractSerializeTest2$A\",\"id\":123}";
        AbstractSerializeTest2.B b = ((AbstractSerializeTest2.B) (JSON.parse(text)));
        Assert.assertNotNull(b);
        Assert.assertEquals(123, b.getId());
    }

    public void test_mapping_2() throws Exception {
        String text = "{\"@type\":\"com.alibaba.json.bvt.parser.deser.AbstractSerializeTest2$A\",\"id\":234,\"name\":\"abc\"}";
        AbstractSerializeTest2.B b = ((AbstractSerializeTest2.B) (JSON.parse(text)));
        Assert.assertNotNull(b);
        Assert.assertEquals(234, b.getId());
        Assert.assertEquals("abc", b.getName());
    }

    public void test_mapping_group() throws Exception {
        String text = "{\"a\":{\"id\":234,\"name\":\"abc\"}}";
        AbstractSerializeTest2.G g = JSON.parseObject(text, AbstractSerializeTest2.G.class);
        Assert.assertTrue(((g.getA()) instanceof AbstractSerializeTest2.B));
    }

    public static class G {
        private AbstractSerializeTest2.A a;

        public AbstractSerializeTest2.A getA() {
            return a;
        }

        public void setA(AbstractSerializeTest2.A a) {
            this.a = a;
        }
    }

    @JSONType(mappingTo = AbstractSerializeTest2.B.class)
    public abstract static class A {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class B extends AbstractSerializeTest2.A {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

