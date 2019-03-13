package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.io.IOException;
import java.lang.reflect.Type;
import junit.framework.TestCase;
import org.junit.Assert;


public class ObjectSerializerTest extends TestCase {
    public void test_serialize() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.put(ObjectSerializerTest.ResultCode.class, new ObjectSerializerTest.ResultCodeSerilaizer());
        ObjectSerializerTest.Result result = new ObjectSerializerTest.Result();
        result.code = ObjectSerializerTest.ResultCode.SIGN_ERROR;
        String json = JSON.toJSONString(result, config);
        Assert.assertEquals("{\"code\":17}", json);
    }

    public static class Result {
        public ObjectSerializerTest.ResultCode code;
    }

    public static enum ResultCode {

        SUCCESS(1),
        ERROR((-1)),
        UNKOWN_ERROR(999),
        LOGIN_FAILURE(8),
        INVALID_ARGUMENT(0),
        SIGN_ERROR(17);
        public final int value;

        ResultCode(int value) {
            this.value = value;
        }
    }

    public static class ResultCodeSerilaizer implements ObjectSerializer {
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            serializer.write(((ObjectSerializerTest.ResultCode) (object)).value);
        }
    }
}

