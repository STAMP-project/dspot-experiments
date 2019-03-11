package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Issue146 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue146.VO vo = new Issue146.VO();
        JSON json = JSON.parseObject("{}");
        vo.setName(json);
        String s = JSON.toJSONString(vo, WriteClassName);
        System.out.println(s);
        JSON.parseObject(s);
    }

    public static class VO {
        private int id;

        private Object name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Object getName() {
            return name;
        }

        public void setName(Object name) {
            this.name = name;
        }
    }
}

