package com.alibaba.json.bvt.parser.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_kongmu extends TestCase {
    public void test_for_bug() throws Exception {
        String JSON_STRING = "{\n" + (((((((("\t\"body\":\"parentBody\",\n" + "\t\"name\":\"child-1\",\n") + "\t\"result\":{\n") + "\t\t\"code\":11\n") + "\t},\n") + "\t\"toy\":{\n") + "\t\t\"type\":\"toytype\"\n") + "\t}\n") + "}");
        JSON.parseObject(JSON_STRING, Bug_for_kongmu.Child.class);
    }

    public static class BaseDO {
        private String body;

        private Bug_for_kongmu.BaseDO.Result result;

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public Bug_for_kongmu.BaseDO.Result getResult() {
            return result;
        }

        public void setResult(Bug_for_kongmu.BaseDO.Result result) {
            this.result = result;
        }

        // ?1.2.27?????1.2.48???
        public class Result {
            // ?1.2.48?????static
            // public static class Result {
            public Result() {
                this.code = 11;
            }

            private int code;

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }
        }
    }

    public static class Child extends Bug_for_kongmu.BaseDO {
        public String name;

        public Bug_for_kongmu.Child.Toy toy;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Bug_for_kongmu.Child.Toy getToy() {
            return toy;
        }

        public void setToy(Bug_for_kongmu.Child.Toy toy) {
            this.toy = toy;
        }

        // ????static??1.2.27?1.2.48???
        public class Toy {
            private String type;

            public Toy() {
                this.type = "toyType";
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}

