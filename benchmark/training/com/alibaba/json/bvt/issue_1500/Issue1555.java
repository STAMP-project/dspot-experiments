package com.alibaba.json.bvt.issue_1500;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;


public class Issue1555 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1555.Model model = new Issue1555.Model();
        model.userId = 1001;
        model.userName = "test";
        String text = JSON.toJSONString(model);
        TestCase.assertEquals("{\"userName\":\"test\",\"user_id\":1001}", text);
        Issue1555.Model model2 = JSON.parseObject(text, Issue1555.Model.class);
        TestCase.assertEquals(1001, model2.userId);
        TestCase.assertEquals("test", model2.userName);
    }

    /**
     * ??????JSONField???JSONField?name??????json????????????????
     *
     * @throws Exception
     * 		
     */
    public void test_when_JSONField_have_not_name_attr() throws Exception {
        Issue1555.ModelTwo modelTwo = new Issue1555.ModelTwo();
        modelTwo.userId = 1001;
        modelTwo.userName = "test";
        String text = JSON.toJSONString(modelTwo);
        TestCase.assertEquals("{\"userName\":\"test\",\"user_id\":\"1001\"}", text);
        Issue1555.Model model2 = JSON.parseObject(text, Issue1555.Model.class);
        TestCase.assertEquals(1001, model2.userId);
        TestCase.assertEquals("test", model2.userName);
    }

    @JSONType(naming = PropertyNamingStrategy.SnakeCase)
    public static class Model {
        private int userId;

        @JSONField(name = "userName")
        private String userName;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }

    @JSONType(naming = PropertyNamingStrategy.SnakeCase)
    public static class ModelTwo {
        /**
         * ??????????????
         */
        @JSONField(serializeUsing = StringSerializer.class)
        private int userId;

        @JSONField(name = "userName")
        private String userName;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}

