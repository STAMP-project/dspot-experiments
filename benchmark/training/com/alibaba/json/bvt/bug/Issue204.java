package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import junit.framework.TestCase;


public class Issue204 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue204.VO vo = new Issue204.VO();
        SerializeFilter filter = null;
        JSON.toJSONString(vo, SerializeConfig.getGlobalInstance(), filter);
        JSON.toJSONString(vo, SerializeConfig.getGlobalInstance(), new SerializeFilter[0]);
    }

    public static class VO {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

