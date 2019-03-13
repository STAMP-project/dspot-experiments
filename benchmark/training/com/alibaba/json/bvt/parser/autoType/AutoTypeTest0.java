package com.alibaba.json.bvt.parser.autoType;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 10/02/2017.
 */
public class AutoTypeTest0 extends TestCase {
    public void test_0() throws Exception {
        String text = "{\"@type\":\"com.alibaba.json.bvt.parser.autoType.AutoTypeTest0$Model\",\"id\":123}";
        AutoTypeTest0.Model model = JSON.parseObject(text, AutoTypeTest0.Model.class);
        TestCase.assertEquals(123, model.id);
        AutoTypeTest0.Model model2 = ((AutoTypeTest0.Model) (JSON.parse(text)));
        TestCase.assertEquals(123, model2.id);
    }

    public static class Model {
        public int id;
    }
}

