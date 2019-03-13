package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/02/2017.
 */
public class Issue_for_huangfeng extends TestCase {
    public void test_for_huangfeng() throws Exception {
        String json = "{\"success\":\"Y\"}";
        Issue_for_huangfeng.Model model = JSON.parseObject(json, Issue_for_huangfeng.Model.class);
        TestCase.assertTrue(model.isSuccess());
    }

    public void test_for_huangfeng_t() throws Exception {
        String json = "{\"success\":\"T\"}";
        Issue_for_huangfeng.Model model = JSON.parseObject(json, Issue_for_huangfeng.Model.class);
        TestCase.assertTrue(model.isSuccess());
    }

    public void test_for_huangfeng_is_t() throws Exception {
        String json = "{\"isSuccess\":\"T\"}";
        Issue_for_huangfeng.Model model = JSON.parseObject(json, Issue_for_huangfeng.Model.class);
        TestCase.assertTrue(model.isSuccess());
    }

    public void test_for_huangfeng_false() throws Exception {
        String json = "{\"success\":\"N\"}";
        Issue_for_huangfeng.Model model = JSON.parseObject(json, Issue_for_huangfeng.Model.class);
        TestCase.assertFalse(model.isSuccess());
    }

    public void test_for_huangfeng_false_f() throws Exception {
        String json = "{\"success\":\"F\"}";
        Issue_for_huangfeng.Model model = JSON.parseObject(json, Issue_for_huangfeng.Model.class);
        TestCase.assertFalse(model.isSuccess());
    }

    public static class Model {
        private boolean success;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}

