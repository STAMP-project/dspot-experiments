package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;


public class Issue69 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue69.VO vo = new Issue69.VO();
        vo.a = new Issue69.Entry();
        vo.b = vo.a;
        String text = JSON.toJSONString(vo);
        System.out.println(text);
    }

    @JSONType(serialzeFeatures = { SerializerFeature.DisableCircularReferenceDetect })
    public static class VO {
        private Issue69.Entry a;

        private Issue69.Entry b;

        public Issue69.Entry getA() {
            return a;
        }

        public void setA(Issue69.Entry a) {
            this.a = a;
        }

        public Issue69.Entry getB() {
            return b;
        }

        public void setB(Issue69.Entry b) {
            this.b = b;
        }
    }

    public static class Entry {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

