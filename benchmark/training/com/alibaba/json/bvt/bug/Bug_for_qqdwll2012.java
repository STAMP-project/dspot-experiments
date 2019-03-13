package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteSlashAsSpecial;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_qqdwll2012 extends TestCase {
    public void test_for_x() throws Exception {
        Bug_for_qqdwll2012.VO vo = new Bug_for_qqdwll2012.VO();
        vo.setValue("<a href=\"http://www.baidu.com\"> \u95ee\u9898\u94fe\u63a5 </a> ");
        String text = JSON.toJSONString(vo, WriteSlashAsSpecial);
        System.out.println(text);
    }

    public static class VO {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

