package com.taobao.metamorphosis.client.extension.spring;


import com.taobao.metamorphosis.client.MessageSessionFactory;
import com.taobao.metamorphosis.client.consumer.ConsumerConfig;
import com.taobao.metamorphosis.client.consumer.MessageConsumer;
import java.util.HashMap;
import java.util.Map;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;


public class MessageListenerContainerUnitTest {
    private MessageListenerContainer container;

    private MessageSessionFactory sessionFactory;

    private MessageConsumer consumer;

    private IMocksControl control;

    private JavaSerializationMessageBodyConverter messageBodyConverter;

    private static class MyListener extends DefaultMessageListener<String> {
        @Override
        public void onReceiveMessages(MetaqMessage<String> msg) {
        }
    }

    @Test
    public void testSubscribeOneTopic() throws Exception {
        Map<MetaqTopic, MessageListenerContainerUnitTest.MyListener> subscribers = new HashMap<MetaqTopic, MessageListenerContainerUnitTest.MyListener>();
        ConsumerConfig consumerConfig = new ConsumerConfig("group1");
        MessageListenerContainerUnitTest.MyListener listener = new MessageListenerContainerUnitTest.MyListener();
        subscribers.put(new MetaqTopic("topic1", 1024, consumerConfig), listener);
        this.container.setSubscribers(subscribers);
        EasyMock.expect(this.sessionFactory.createConsumer(consumerConfig)).andReturn(this.consumer);
        EasyMock.expect(this.consumer.subscribe("topic1", 1024, listener)).andReturn(this.consumer);
        this.consumer.completeSubscribe();
        EasyMock.expectLastCall();
        this.control.replay();
        this.container.afterPropertiesSet();
        this.control.verify();
        Assert.assertTrue(this.container.consumers.contains(this.consumer));
    }

    @Test
    public void testSubscribeTwoTopics() throws Exception {
        Map<MetaqTopic, MessageListenerContainerUnitTest.MyListener> subscribers = new HashMap<MetaqTopic, MessageListenerContainerUnitTest.MyListener>();
        ConsumerConfig consumerConfig1 = new ConsumerConfig("group1");
        MessageListenerContainerUnitTest.MyListener listener1 = new MessageListenerContainerUnitTest.MyListener();
        subscribers.put(new MetaqTopic("topic1", 1024, consumerConfig1), listener1);
        ConsumerConfig consumerConfig2 = new ConsumerConfig("group1");
        MessageListenerContainerUnitTest.MyListener listener2 = new MessageListenerContainerUnitTest.MyListener();
        subscribers.put(new MetaqTopic("topic2", (1024 * 1024), consumerConfig2), listener2);
        this.container.setSubscribers(subscribers);
        EasyMock.expect(this.sessionFactory.createConsumer(consumerConfig1)).andReturn(this.consumer);
        EasyMock.expect(this.sessionFactory.createConsumer(consumerConfig2)).andReturn(this.consumer);
        EasyMock.expect(this.consumer.subscribe("topic1", 1024, listener1)).andReturn(this.consumer);
        EasyMock.expect(this.consumer.subscribe("topic2", (1024 * 1024), listener2)).andReturn(this.consumer);
        this.consumer.completeSubscribe();
        EasyMock.expectLastCall();
        this.control.replay();
        this.container.afterPropertiesSet();
        this.control.verify();
        Assert.assertTrue(this.container.consumers.contains(this.consumer));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubscribeTwoTopicsShareConsumerWithoutDefaultTopic() throws Exception {
        Map<MetaqTopic, MessageListenerContainerUnitTest.MyListener> subscribers = new HashMap<MetaqTopic, MessageListenerContainerUnitTest.MyListener>();
        ConsumerConfig consumerConfig1 = new ConsumerConfig("group1");
        MessageListenerContainerUnitTest.MyListener listener1 = new MessageListenerContainerUnitTest.MyListener();
        subscribers.put(new MetaqTopic("topic1", 1024, consumerConfig1), listener1);
        ConsumerConfig consumerConfig2 = new ConsumerConfig("group1");
        MessageListenerContainerUnitTest.MyListener listener2 = new MessageListenerContainerUnitTest.MyListener();
        subscribers.put(new MetaqTopic("topic2", (1024 * 1024), consumerConfig2), listener2);
        this.container.setSubscribers(subscribers);
        this.container.setShareConsumer(true);
        this.control.replay();
        this.container.afterPropertiesSet();
        this.control.verify();
    }

    @Test
    public void testSubscribeTwoTopicsShareTopic() throws Exception {
        Map<MetaqTopic, MessageListenerContainerUnitTest.MyListener> subscribers = new HashMap<MetaqTopic, MessageListenerContainerUnitTest.MyListener>();
        ConsumerConfig consumerConfig1 = new ConsumerConfig("group1");
        MessageListenerContainerUnitTest.MyListener listener1 = new MessageListenerContainerUnitTest.MyListener();
        subscribers.put(new MetaqTopic("topic1", 1024, consumerConfig1), listener1);
        ConsumerConfig consumerConfig2 = new ConsumerConfig("group1");
        MessageListenerContainerUnitTest.MyListener listener2 = new MessageListenerContainerUnitTest.MyListener();
        this.container.setSubscribers(subscribers);
        this.container.setShareConsumer(true);
        this.container.setDefaultTopic(new MetaqTopic("topic2", (1024 * 1024), consumerConfig2));
        this.container.setDefaultMessageListener(listener2);
        EasyMock.expect(this.sessionFactory.createConsumer(consumerConfig2)).andReturn(this.consumer);
        EasyMock.expect(this.consumer.subscribe("topic2", (1024 * 1024), listener2)).andReturn(this.consumer);
        EasyMock.expect(this.consumer.subscribe("topic1", 1024, listener1)).andReturn(this.consumer);
        this.consumer.completeSubscribe();
        EasyMock.expectLastCall();
        this.control.replay();
        this.container.afterPropertiesSet();
        this.control.verify();
    }

    @Test
    public void testDestroy() throws Exception {
        this.testSubscribeOneTopic();
        this.control.reset();
        this.consumer.shutdown();
        EasyMock.expectLastCall();
        this.control.replay();
        this.container.destroy();
        this.control.verify();
        Assert.assertTrue(this.container.consumers.isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void testShareConsumerAndProvideDefaultTopic() throws Exception {
        Map<MetaqTopic, MessageListenerContainerUnitTest.MyListener> subscribers = new HashMap<MetaqTopic, MessageListenerContainerUnitTest.MyListener>();
        ConsumerConfig consumerConfig = new ConsumerConfig("group1");
        MessageListenerContainerUnitTest.MyListener listener = new MessageListenerContainerUnitTest.MyListener();
        subscribers.put(new MetaqTopic("topic1", 1024, consumerConfig), listener);
        this.container.setSubscribers(subscribers);
        this.container.setDefaultMessageListener(listener);
        this.control.replay();
        this.container.afterPropertiesSet();
        this.control.verify();
    }
}

