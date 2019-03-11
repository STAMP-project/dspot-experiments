package com.taobao.metamorphosis.client.extension.spring;


import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.MessageAccessor;
import com.taobao.metamorphosis.exception.MetaClientException;
import org.junit.Assert;
import org.junit.Test;


public class DefaultMessageListenerUnitTest {
    private static class MyMessageListener extends DefaultMessageListener<String> {
        MetaqMessage<String> recvMsg;

        @Override
        public void onReceiveMessages(MetaqMessage<String> msg) {
            this.recvMsg = msg;
        }
    }

    @Test
    public void testOnReceiveMessagesWithConverter() throws Exception {
        DefaultMessageListenerUnitTest.MyMessageListener listener = new DefaultMessageListenerUnitTest.MyMessageListener();
        JavaSerializationMessageBodyConverter messageBodyConverter = new JavaSerializationMessageBodyConverter();
        listener.setMessageBodyConverter(messageBodyConverter);
        afterPropertiesSet();
        Message message = new Message("test", messageBodyConverter.toByteArray("hello world"));
        listener.recieveMessages(message);
        Assert.assertNotNull(listener.recvMsg);
        Assert.assertEquals("hello world", listener.recvMsg.getBody());
        Assert.assertSame(message, listener.recvMsg.getRawMessage());
    }

    @Test
    public void testConvertMessageBodyFailure() throws Exception {
        DefaultMessageListenerUnitTest.MyMessageListener listener = new DefaultMessageListenerUnitTest.MyMessageListener();
        JavaSerializationMessageBodyConverter messageBodyConverter = new JavaSerializationMessageBodyConverter();
        setMessageBodyConverter(new MessageBodyConverter<String>() {
            @Override
            public byte[] toByteArray(String body) throws MetaClientException {
                throw new RuntimeException();
            }

            @Override
            public String fromByteArray(byte[] bs) throws MetaClientException {
                throw new RuntimeException();
            }
        });
        afterPropertiesSet();
        Message message = new Message("test", messageBodyConverter.toByteArray("hello world"));
        listener.recieveMessages(message);
        Assert.assertNull(listener.recvMsg);
        Assert.assertTrue(MessageAccessor.isRollbackOnly(message));
    }

    @Test
    public void testInitDestroy() throws Exception {
        DefaultMessageListenerUnitTest.MyMessageListener listener = new DefaultMessageListenerUnitTest.MyMessageListener();
        setProcessThreads(10);
        Assert.assertNull(getExecutor());
        afterPropertiesSet();
        Assert.assertNotNull(getExecutor());
        destroy();
        Assert.assertNull(getExecutor());
    }

    @Test
    public void testOnReceiveMessagesWithoutConverter() throws Exception {
        DefaultMessageListenerUnitTest.MyMessageListener listener = new DefaultMessageListenerUnitTest.MyMessageListener();
        JavaSerializationMessageBodyConverter messageBodyConverter = new JavaSerializationMessageBodyConverter();
        afterPropertiesSet();
        Message message = new Message("test", messageBodyConverter.toByteArray("hello world"));
        listener.recieveMessages(message);
        Assert.assertNotNull(listener.recvMsg);
        Assert.assertNull(listener.recvMsg.getBody());
        Assert.assertSame(message, listener.recvMsg.getRawMessage());
    }
}

