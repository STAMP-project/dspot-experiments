package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class InterfaceParseTest extends TestCase {
    public void test_interface() throws Exception {
        InterfaceParseTest.VO vo = JSON.parseObject("{\"text\":\"abc\",\"b\":true}", InterfaceParseTest.VO.class);
        Assert.assertEquals("abc", vo.getText());
        Assert.assertEquals(Boolean.TRUE, vo.getB());
    }

    public static interface VO {
        void setText(String val);

        String getText();

        void setB(Boolean val);

        Boolean getB();

        void setI(int value);

        void setC(char value);

        void setS(short value);

        void setL(long value);
    }
}

