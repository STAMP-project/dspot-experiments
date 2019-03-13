package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_enum extends TestCase {
    public void test_name() throws Exception {
        JSONPath_enum.Model model = new JSONPath_enum.Model();
        model.size = JSONPath_enum.Size.Small;
        Assert.assertEquals(JSONPath_enum.Size.Small.name(), JSONPath.eval(model, "$.size.name"));
    }

    public void test_orginal() throws Exception {
        JSONPath_enum.Model model = new JSONPath_enum.Model();
        model.size = JSONPath_enum.Size.Small;
        Assert.assertEquals(JSONPath_enum.Size.Small.ordinal(), JSONPath.eval(model, "$.size.ordinal"));
    }

    public static class Model {
        public JSONPath_enum.Size size;
    }

    public static enum Size {

        Big,
        Median,
        Small;}
}

