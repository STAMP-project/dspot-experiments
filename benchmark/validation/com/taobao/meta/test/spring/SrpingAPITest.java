package com.taobao.meta.test.spring;


import com.taobao.meta.test.BaseMetaTest;
import com.taobao.metamorphosis.client.extension.spring.MessageBuilder;
import com.taobao.metamorphosis.client.extension.spring.MetaqTemplate;
import com.taobao.metamorphosis.client.producer.SendResult;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SrpingAPITest extends BaseMetaTest {
    @Test(timeout = 60000)
    public void sendConsume() throws Exception {
        this.createProducer();
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        // use template to send messages.
        final String topic = "meta-test";
        MetaqTemplate template = ((MetaqTemplate) (context.getBean("metaqTemplate")));
        int count = 100;
        for (int i = 0; i < count; i++) {
            SendResult result = template.send(MessageBuilder.withTopic(topic).withBody(new Trade(i, "test", i, "test")));
            Assert.assertTrue(result.isSuccess());
        }
        TradeMessageListener listener = ((TradeMessageListener) (context.getBean("messageListener")));
        while ((listener.counter.get()) != count) {
            Thread.sleep(100);
        } 
        Assert.assertEquals(listener.counter.get(), count);
    }
}

