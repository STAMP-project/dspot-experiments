package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class SmartMatchTest extends TestCase {
    public void test_vo2() throws Exception {
        String text = "{\"message_id\":1001}";
        SmartMatchTest.VO2 vo = JSON.parseObject(text, SmartMatchTest.VO2.class);
        Assert.assertEquals(1001, vo.getMessageId());
    }

    private static class VO {
        private int messageId;

        public int getMessageId() {
            return messageId;
        }

        public void setMessageId(int messageId) {
            this.messageId = messageId;
        }
    }

    public static class VO2 {
        private int messageId;

        public int getMessageId() {
            return messageId;
        }

        public void setMessageId(int messageId) {
            this.messageId = messageId;
        }
    }
}

