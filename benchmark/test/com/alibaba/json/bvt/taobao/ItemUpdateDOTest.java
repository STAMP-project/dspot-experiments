package com.alibaba.json.bvt.taobao;


import SerializerFeature.IgnoreErrorGetter;
import SerializerFeature.IgnoreNonFieldGetter;
import SerializerFeature.WriteClassName;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;


public class ItemUpdateDOTest extends TestCase {
    public void test_1() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.setAsmEnable(false);
        ItemUpdateDOTest.Model item = new ItemUpdateDOTest.Model();
        JSON.toJSONString(item, config, IgnoreErrorGetter, IgnoreNonFieldGetter, WriteClassName, WriteMapNullValue);
        System.out.println(JSON.toJSONString("\u000b"));
    }

    public static class Model {
        private long f0 = 1;

        private long f1;

        public long getF0() {
            return f0;
        }

        public void setF0(long f0) {
            this.f0 = f0;
        }

        public long getF1() {
            return f1;
        }

        public void setF1(long f1) {
            this.f1 = f1;
        }

        /**
         *
         *
         * @deprecated 
         */
        @Deprecated
        public long getUpdateFeatureCc() {
            throw new IllegalArgumentException("updateFeatureCc????");
        }
    }
}

