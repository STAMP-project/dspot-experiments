package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class BooleanFieldDeserializerTest extends TestCase {
    public void test_0() throws Exception {
        BooleanFieldDeserializerTest.Entity a = JSON.parseObject("{f1:null, f2:null}", BooleanFieldDeserializerTest.Entity.class);
        Assert.assertEquals(true, a.isF1());
        Assert.assertEquals(null, a.getF2());
    }

    public static class Entity {
        private boolean f1 = true;

        private Boolean f2 = Boolean.TRUE;

        public boolean isF1() {
            return f1;
        }

        public void setF1(boolean f1) {
            this.f1 = f1;
        }

        public Boolean getF2() {
            return f2;
        }

        public void setF2(Boolean f2) {
            this.f2 = f2;
        }
    }
}

