package org.apache.rocketmq.jms.domain.message;


import JmsBaseConstant.JMS_DESTINATION;
import JmsBaseConstant.JMS_MESSAGE_ID;
import JmsBaseConstant.JMS_REDELIVERED;
import MessageConst.PROPERTY_KEYS;
import MessageConst.PROPERTY_TAGS;
import MsgConvertUtil.JMS_MSGMODEL;
import MsgConvertUtil.MSGMODEL_TEXT;
import MsgConvertUtil.MSG_TOPIC;
import MsgConvertUtil.MSG_TYPE;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.jms.domain.JmsBaseTopic;
import org.apache.rocketmq.jms.util.MessageConverter;
import org.junit.Assert;
import org.junit.Test;


public class JmsMessageConvertTest {
    @Test
    public void testCovert2RMQ() throws Exception {
        // init jmsBaseMessage
        String topic = "TestTopic";
        String messageType = "TagA";
        JmsBaseMessage jmsBaseMessage = new JmsTextMessage("testText");
        jmsBaseMessage.setHeader(JMS_DESTINATION, new JmsBaseTopic(topic, messageType));
        jmsBaseMessage.setHeader(JMS_MESSAGE_ID, "ID:null");
        jmsBaseMessage.setHeader(JMS_REDELIVERED, Boolean.FALSE);
        jmsBaseMessage.setObjectProperty(JMS_MSGMODEL, MSGMODEL_TEXT);
        jmsBaseMessage.setObjectProperty(MSG_TOPIC, topic);
        jmsBaseMessage.setObjectProperty(MSG_TYPE, messageType);
        jmsBaseMessage.setObjectProperty(PROPERTY_TAGS, messageType);
        jmsBaseMessage.setObjectProperty(PROPERTY_KEYS, messageType);
        // convert to RMQMessage
        MessageExt message = ((MessageExt) (MessageConverter.convert2RMQMessage(jmsBaseMessage)));
        System.out.println(message);
        // then convert back to jmsBaseMessage
        JmsBaseMessage jmsBaseMessageBack = MessageConverter.convert2JMSMessage(message);
        JmsTextMessage jmsTextMessage = ((JmsTextMessage) (jmsBaseMessage));
        JmsTextMessage jmsTextMessageBack = ((JmsTextMessage) (jmsBaseMessageBack));
        Assert.assertEquals(jmsTextMessage.getText(), jmsTextMessageBack.getText());
        Assert.assertEquals(jmsTextMessage.getJMSDestination().toString(), jmsTextMessageBack.getJMSDestination().toString());
        Assert.assertEquals(jmsTextMessage.getJMSMessageID(), jmsTextMessageBack.getJMSMessageID());
        Assert.assertEquals(jmsTextMessage.getJMSRedelivered(), jmsTextMessageBack.getJMSRedelivered());
        Assert.assertEquals(jmsTextMessage.getHeaders().get(JMS_MSGMODEL), jmsTextMessageBack.getHeaders().get(JMS_MSGMODEL));
        Assert.assertEquals(jmsTextMessage.getHeaders().get(MSG_TOPIC), jmsTextMessageBack.getHeaders().get(MSG_TOPIC));
        Assert.assertEquals(jmsTextMessage.getHeaders().get(MSG_TYPE), jmsTextMessageBack.getHeaders().get(MSG_TYPE));
        Assert.assertEquals(jmsTextMessage.getHeaders().get(PROPERTY_TAGS), jmsTextMessageBack.getHeaders().get(PROPERTY_TAGS));
        Assert.assertEquals(jmsTextMessage.getHeaders().get(PROPERTY_KEYS), jmsTextMessageBack.getHeaders().get(PROPERTY_KEYS));
    }
}

