package com.alibaba.json.bvt.annotation;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import junit.framework.TestCase;


public class CustomSerializerTest_enum extends TestCase {
    public void test_0() throws Exception {
        CustomSerializerTest_enum.Model model = new CustomSerializerTest_enum.Model();
        model.id = 1001;
        model.orderType = CustomSerializerTest_enum.OrderType.PayOrder;
        String text = JSON.toJSONString(model);
        // Assert.assertEquals("{\"id\":1001,\"orderType\":{\"remark\":\"????\",\"value\":1}}", text);
    }

    public static class Model {
        public int id;

        public CustomSerializerTest_enum.OrderType orderType;
    }

    public static enum OrderType implements JSONSerializable {

        PayOrder(1, "????"),
        // 
        SettleBill(2, "???");
        public final int value;

        public final String remark;

        private OrderType(int value, String remark) {
            this.value = value;
            this.remark = remark;
        }

        @Override
        public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) throws IOException {
            JSONObject json = new JSONObject();
            json.put("value", value);
            json.put("remark", remark);
            serializer.write(json);
        }
    }
}

