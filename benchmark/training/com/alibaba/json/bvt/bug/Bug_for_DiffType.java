package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_DiffType extends TestCase {
    public void test_for_diff_type() throws Exception {
        Bug_for_DiffType.Model model = new Bug_for_DiffType.Model();
        model.setValue(1001);
        String text = JSON.toJSONString(model);
        Bug_for_DiffType.Model model2 = JSON.parseObject(text, Bug_for_DiffType.Model.class);
        Assert.assertEquals(model.value, model2.value);
    }

    public static class Model {
        public String value;

        public long getValue() {
            return Long.parseLong(value);
        }

        public void setValue(long value) {
            this.value = Long.toString(value);
        }
    }
}

