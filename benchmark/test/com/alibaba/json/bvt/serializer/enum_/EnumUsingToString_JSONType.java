package com.alibaba.json.bvt.serializer.enum_;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;


/**
 * Created by wenshao on 17/03/2017.
 */
public class EnumUsingToString_JSONType extends TestCase {
    public void test_toString() {
        EnumUsingToString_JSONType.Model model = new EnumUsingToString_JSONType.Model();
        model.gender = EnumUsingToString_JSONType.Gender.M;
        String text = JSON.toJSONString(model);
        TestCase.assertEquals("{\"gender\":\"\u7537\"}", text);
    }

    @JSONType(serialzeFeatures = SerializerFeature.WriteEnumUsingToString)
    public static class Model {
        public EnumUsingToString_JSONType.Gender gender;
    }

    public static enum Gender {

        M("?"),
        W("?");
        private String msg;

        Gender(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return msg;
        }
    }
}

