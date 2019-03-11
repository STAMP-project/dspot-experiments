package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import java.util.Currency;
import java.util.Locale;
import junit.framework.TestCase;


public class CurrencyTest extends TestCase {
    public void test_0() throws Exception {
        CurrencyTest.VO vo = new CurrencyTest.VO();
        vo.setValue(Currency.getInstance(Locale.CHINA));
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        JSON.parseObject(text, CurrencyTest.VO.class);
    }

    public static class VO {
        private Currency value;

        public Currency getValue() {
            return value;
        }

        public void setValue(Currency value) {
            this.value = value;
        }
    }
}

