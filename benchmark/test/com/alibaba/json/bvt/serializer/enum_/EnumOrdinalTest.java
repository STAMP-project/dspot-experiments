package com.alibaba.json.bvt.serializer.enum_;


import SerializerFeature.WriteEnumUsingName;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 17/03/2017.
 */
public class EnumOrdinalTest extends TestCase {
    public void test_enum_ordinal() throws Exception {
        EnumOrdinalTest.Model model = new EnumOrdinalTest.Model();
        model.type = EnumOrdinalTest.Type.Big;
        int serializerFeatures = (JSON.DEFAULT_GENERATE_FEATURE) & (~(WriteEnumUsingName.mask));
        String text = JSON.toJSONString(model, serializerFeatures);
        System.out.println(text);
    }

    public static class Model {
        public EnumOrdinalTest.Type type;
    }

    public static enum Type {

        Big,
        Medium,
        Small;}
}

