package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_426 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "{value:\"264,010,000.00\"}";
        Bug_for_issue_426.Model model = JSON.parseObject(text, Bug_for_issue_426.Model.class);
        Assert.assertTrue((2.6401E8 == (model.value)));
    }

    public void test_for_issue_float() throws Exception {
        String text = "{value:\"264,010,000\"}";
        Bug_for_issue_426.ModelFloat model = JSON.parseObject(text, Bug_for_issue_426.ModelFloat.class);
        Assert.assertTrue((2.6401E8F == (model.value)));
    }

    public void test_for_issue_int() throws Exception {
        String text = "{value:\"264,010,000\"}";
        Bug_for_issue_426.ModelInt model = JSON.parseObject(text, Bug_for_issue_426.ModelInt.class);
        Assert.assertTrue((2.6401E8 == (model.value)));
    }

    public void test_for_issue_long() throws Exception {
        String text = "{value:\"264,010,000\"}";
        Bug_for_issue_426.ModelLong model = JSON.parseObject(text, Bug_for_issue_426.ModelLong.class);
        Assert.assertTrue((2.6401E8 == (model.value)));
    }

    public static class Model {
        public double value;
    }

    public static class ModelFloat {
        public float value;
    }

    public static class ModelInt {
        public int value;
    }

    public static class ModelLong {
        public long value;
    }
}

