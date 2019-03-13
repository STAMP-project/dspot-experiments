package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class KeyBug_for_zhongl extends TestCase {
    public void testCustomedKey() throws Exception {
        Assert.assertEquals("{\"uid\":1}", JSON.toJSONString(new KeyBug_for_zhongl.V2(1)));
    }

    public void testDeserialize() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"uid\":123}", KeyBug_for_zhongl.V2.class).id);
    }

    public void testCustomedKey_static() throws Exception {
        Assert.assertEquals("{\"uid\":1}", JSON.toJSONString(new KeyBug_for_zhongl.VO(1)));
    }

    public void testDeserialize_static() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"uid\":123}", KeyBug_for_zhongl.VO.class).id);
    }

    public static class VO {
        @JSONField(name = "uid")
        public int id;

        @JSONField(serialize = false)
        public String name = "defaultName";

        public VO() {
        }

        VO(int id) {
            this.id = id;
        }
    }

    private static class V2 {
        @JSONField(name = "uid")
        public int id;

        @JSONField(serialize = false)
        public String name = "defaultName";

        private V2() {
        }

        private V2(int id) {
            this.id = id;
        }
    }
}

