package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.Currency;
import junit.framework.TestCase;


public class CurrencyTest5 extends TestCase {
    public void test_0() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.put(Currency.class, config.createJavaBeanSerializer(Currency.class));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", Currency.getInstance("CNY"));
        String text = JSON.toJSONString(jsonObject, config);
        System.out.println(text);
        String str1 = "{\"value\":{\"currencyCode\":\"CNY\",\"displayName\":\"Chinese Yuan\",\"symbol\":\"CNY\"}}";
        String str2 = "{\"value\":{\"currencyCode\":\"CNY\",\"displayName\":\"\u4eba\u6c11\u5e01\",\"symbol\":\"\uffe5\"}}";
        String str3 = "{\"value\":{\"currencyCode\":\"CNY\",\"displayName\":\"Chinese Yuan\",\"numericCodeAsString\":\"156\",\"symbol\":\"CN\u00a5\"}}";
        TestCase.assertTrue((((text.equals(str1)) || (text.equals(str2))) || (text.equals(str3))));
        Currency currency = JSON.parseObject(text, CurrencyTest5.VO.class).value;
        TestCase.assertSame(Currency.getInstance("CNY"), currency);
    }

    public static class VO {
        public Currency value;
    }
}

