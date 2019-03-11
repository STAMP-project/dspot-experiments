package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue220 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue220.Attr attr = new Issue220.Attr();
        attr.jTType = 123;
        attr.value = "xxxx";
        attr.symbol = "yyyy";
        String text = JSON.toJSONString(attr);
        Assert.assertEquals("{\"jTType\":123,\"symbol\":\"yyyy\",\"value\":\"xxxx\"}", text);
    }

    public static class Attr {
        private int jTType;

        private String value;

        private String symbol;

        public int getjTType() {
            return jTType;
        }

        public void setjTType(int jTType) {
            this.jTType = jTType;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
    }
}

