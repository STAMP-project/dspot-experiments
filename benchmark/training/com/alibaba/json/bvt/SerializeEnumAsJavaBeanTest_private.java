package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;


/**
 * Created by wenshao on 08/01/2017.
 */
public class SerializeEnumAsJavaBeanTest_private extends TestCase {
    public void test_serializeEnumAsJavaBean() throws Exception {
        String text = JSON.toJSONString(SerializeEnumAsJavaBeanTest_private.OrderType.PayOrder);
        TestCase.assertEquals("{\"remark\":\"\u652f\u4ed8\u8ba2\u5355\",\"value\":1}", text);
    }

    public void test_field() throws Exception {
        SerializeEnumAsJavaBeanTest_private.Model model = new SerializeEnumAsJavaBeanTest_private.Model();
        model.orderType = SerializeEnumAsJavaBeanTest_private.OrderType.SettleBill;
        String text = JSON.toJSONString(model);
        TestCase.assertEquals("{\"orderType\":{\"remark\":\"\u7ed3\u7b97\u5355\",\"value\":2}}", text);
    }

    public void test_field_2() throws Exception {
        SerializeEnumAsJavaBeanTest_private.Model model = new SerializeEnumAsJavaBeanTest_private.Model();
        model.orderType = SerializeEnumAsJavaBeanTest_private.OrderType.SettleBill;
        model.orderType1 = SerializeEnumAsJavaBeanTest_private.OrderType.SettleBill;
        String text = JSON.toJSONString(model);
        TestCase.assertEquals("{\"orderType\":{\"remark\":\"\u7ed3\u7b97\u5355\",\"value\":2},\"orderType1\":{\"remark\":\"\u7ed3\u7b97\u5355\",\"value\":2}}", text);
    }

    @JSONType(serializeEnumAsJavaBean = true)
    private static enum OrderType {

        PayOrder(1, "????"),
        // 
        SettleBill(2, "???");
        public final int value;

        public final String remark;

        private OrderType(int value, String remark) {
            this.value = value;
            this.remark = remark;
        }
    }

    private static class Model {
        public SerializeEnumAsJavaBeanTest_private.OrderType orderType;

        public SerializeEnumAsJavaBeanTest_private.OrderType orderType1;
    }
}

