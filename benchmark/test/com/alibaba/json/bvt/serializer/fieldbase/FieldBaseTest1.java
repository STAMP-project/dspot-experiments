package com.alibaba.json.bvt.serializer.fieldbase;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;


/**
 * Created by wenshao on 01/04/2017.
 */
public class FieldBaseTest1 extends TestCase {
    private static SerializeConfig config = new SerializeConfig(true);

    private static ParserConfig parserConfig = new ParserConfig(true);

    public void test_0() throws Exception {
        FieldBaseTest1.Model model = new FieldBaseTest1.Model();
        ((FieldBaseTest1.AbstractModel) (model)).parentId = 234;
        model.id = 123;
        TestCase.assertEquals("{\"id\":123,\"parentId\":234}", JSON.toJSONString(model, FieldBaseTest1.config));
        FieldBaseTest1.Model model2 = JSON.parseObject("{\"id\":123,\"parentId\":234}", FieldBaseTest1.Model.class, FieldBaseTest1.parserConfig);
        TestCase.assertEquals(((FieldBaseTest1.AbstractModel) (model)).parentId, ((FieldBaseTest1.AbstractModel) (model)).parentId);
        TestCase.assertEquals(model.id, model2.id);
    }

    public static class AbstractModel {
        private int parentId;
    }

    public static class Model extends FieldBaseTest1.AbstractModel {
        private int id;
    }
}

