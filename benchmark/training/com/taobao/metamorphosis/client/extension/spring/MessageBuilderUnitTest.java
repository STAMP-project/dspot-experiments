package com.taobao.metamorphosis.client.extension.spring;


import com.taobao.metamorphosis.Message;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;


public class MessageBuilderUnitTest {
    public static class MyTest implements Serializable {
        private long value = 1000L;

        public long getValue() {
            return this.value;
        }

        public void setValue(long value) {
            this.value = value;
        }
    }

    @Test
    public void testBuildMessageWithBodyObject() throws Exception {
        MessageBuilder mb = MessageBuilder.withTopic("test");
        mb.withAttribute("a attribute").withBody(new MessageBuilderUnitTest.MyTest());
        JavaSerializationMessageBodyConverter converter = new JavaSerializationMessageBodyConverter();
        Message msg = mb.build(converter);
        Assert.assertNotNull(msg);
        Assert.assertEquals("test", msg.getTopic());
        Assert.assertEquals("a attribute", msg.getAttribute());
        Assert.assertTrue(msg.hasAttribute());
        byte[] data = msg.getData();
        Object obj = converter.fromByteArray(data);
        Assert.assertTrue((obj instanceof MessageBuilderUnitTest.MyTest));
        Assert.assertEquals(1000L, ((MessageBuilderUnitTest.MyTest) (obj)).getValue());
    }

    @Test
    public void testBuildMessageWithPayload() throws Exception {
        MessageBuilder mb = MessageBuilder.withTopic("test");
        mb.withAttribute("a attribute").withPayload(new byte[128]);
        Message msg = mb.build();
        Assert.assertNotNull(msg);
        Assert.assertEquals("test", msg.getTopic());
        Assert.assertEquals("a attribute", msg.getAttribute());
        Assert.assertTrue(msg.hasAttribute());
        byte[] data = msg.getData();
        Assert.assertEquals(128, data.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildMessageWithNothing() {
        MessageBuilder.withTopic("test").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithPayloadHasBody() {
        MessageBuilder.withTopic("test").withBody(new MessageBuilderUnitTest.MyTest()).withPayload(new byte[128]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithBodyHasPayload() {
        MessageBuilder.withTopic("test").withPayload(new byte[128]).withBody(new MessageBuilderUnitTest.MyTest());
    }
}

