package com.alibaba.json.bvt.issue_2000;


import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


public class Issue2065 extends TestCase {
    public void test_for_issue() throws Exception {
        // JSON.parseObject("{\"code\":1}", Model.class);
    }

    public static class Model {
        @JSONField(name = "code")
        private Issue2065.EnumClass code;

        public Model() {
        }

        public Issue2065.EnumClass getCode() {
            return code;
        }

        public void setCode(Issue2065.EnumClass code) {
            this.code = code;
        }
    }

    public static enum EnumClass {

        A(1);
        @JSONField
        private int code;

        EnumClass(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}

