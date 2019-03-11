package com.alibaba.json.test.a;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParseContext;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import junit.framework.TestCase;


/**
 * Created by wenshao on 27/03/2017.
 */
public class A20170327_0 extends TestCase {
    public void test_0() throws Exception {
        String s = "{\"itemCurrentAmount\":{\"amount\":12.50,\"cent\":1250,\"centFactor\":100,\"currency\":\"CNY\",\"currencyCode\":\"CNY\"},\"itemDiscountAmount\":{\"$ref\":\"$.itemCurrentAmount\"}}";
        // String s = "{\"itemDiscountAmount\":{\"$ref\":\"$.itemCurrentAmount\"},\"itemCurrentAmount\":{\"amount\":12.50,\"cent\":1250,\"centFactor\":100,\"currency\":\"CNY\",\"currencyCode\":\"CNY\"}}";
        ParserConfig config = new ParserConfig();
        config.putDeserializer(A20170327_0.Money.class, new A20170327_0.MoneyDeserialize());
        A20170327_0.Model model = JSON.parseObject(s, A20170327_0.Model.class, config);
        TestCase.assertSame(model.itemCurrentAmount, model.itemDiscountAmount);
        // JSONObject jsonObject = (JSONObject) JSON.parse(s);
        // assertSame(jsonObject.get("itemCurrentAmount"), jsonObject.get("itemDiscountAmount"));
    }

    public static class Model {
        public A20170327_0.Money itemCurrentAmount;

        public A20170327_0.Money itemDiscountAmount;
    }

    public static class Money {
        public BigDecimal amount;

        public long cent;

        public int centFactor;

        public String currency;

        public String currencyCode;
    }

    public static class MoneyDeserialize implements ObjectDeserializer {
        @SuppressWarnings("unchecked")
        public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            ParseContext cxt = parser.getContext();
            Object object = parser.parse(fieldName);
            if (object == null) {
                return null;
            }
            String moneyCentStr = null;
            if (object instanceof JSONObject) {
                // ??????
                JSONObject jsonObject = ((JSONObject) (object));
                moneyCentStr = jsonObject.getString("cent");
            } else
                if (object instanceof String) {
                    moneyCentStr = ((String) (object));
                } else {
                    throw new RuntimeException(("money????????????????" + (object.getClass().getName())));
                }

            if ((moneyCentStr.length()) != 0) {
                A20170327_0.Money m = new A20170327_0.Money();
                m.cent = Long.valueOf(moneyCentStr);
                return ((T) (m));
            }
            return null;
        }

        public int getFastMatchToken() {
            return 0;
        }
    }
}

