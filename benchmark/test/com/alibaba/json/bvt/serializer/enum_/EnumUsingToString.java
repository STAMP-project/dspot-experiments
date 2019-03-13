package com.alibaba.json.bvt.serializer.enum_;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;


/**
 * Created by wenshao on 17/03/2017.
 */
public class EnumUsingToString extends TestCase {
    public void test_toString() {
        EnumUsingToString.Model model = new EnumUsingToString.Model();
        model.gender = EnumUsingToString.Gender.M;
        String text = JSON.toJSONString(model);
        TestCase.assertEquals("{\"gender\":\"\u7537\"}", text);
    }

    public static class Model {
        @JSONField(serialzeFeatures = SerializerFeature.WriteEnumUsingToString)
        public EnumUsingToString.Gender gender;
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

