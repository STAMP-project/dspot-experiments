/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.jms.processors;


import JmsHeaders.REPLY_TO;
import PublishJMS.CF_SERVICE;
import PublishJMS.DESTINATION;
import PublishJMS.MESSAGE_BODY;
import PublishJMS.REL_FAILURE;
import PublishJMS.REL_SUCCESS;
import java.util.HashMap;
import java.util.Map;
import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.nifi.jms.cf.JMSConnectionFactoryProviderDefinition;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsTemplate;


public class PublishJMSIT {
    @Test(timeout = 10000)
    public void validateSuccessfulPublishAndTransferToSuccess() throws Exception {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        final String destinationName = "validateSuccessfulPublishAndTransferToSuccess";
        PublishJMS pubProc = new PublishJMS();
        TestRunner runner = TestRunners.newTestRunner(pubProc);
        JMSConnectionFactoryProviderDefinition cs = Mockito.mock(JMSConnectionFactoryProviderDefinition.class);
        Mockito.when(cs.getIdentifier()).thenReturn("cfProvider");
        Mockito.when(cs.getConnectionFactory()).thenReturn(cf);
        runner.addControllerService("cfProvider", cs);
        runner.enableControllerService(cs);
        runner.setProperty(CF_SERVICE, "cfProvider");
        runner.setProperty(DESTINATION, destinationName);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("foo", "foo");
        attributes.put(REPLY_TO, "cooQueue");
        runner.enqueue("Hey dude!".getBytes(), attributes);
        runner.run(1, false);// Run once but don't shut down because we want the Connection Factory left in tact so that we can use it.

        final MockFlowFile successFF = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(successFF);
        JmsTemplate jmst = new JmsTemplate(cf);
        BytesMessage message = ((BytesMessage) (jmst.receive(destinationName)));
        byte[] messageBytes = MessageBodyToBytesConverter.toBytes(message);
        Assert.assertEquals("Hey dude!", new String(messageBytes));
        Assert.assertEquals("cooQueue", getQueueName());
        Assert.assertEquals("foo", message.getStringProperty("foo"));
        runner.run(1, true, false);// Run once just so that we can trigger the shutdown of the Connection Factory

    }

    @Test(timeout = 10000)
    public void validateSuccessfulPublishAndTransferToSuccessWithEL() throws Exception {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        final String destinationNameExpression = "${foo}Queue";
        final String destinationName = "fooQueue";
        PublishJMS pubProc = new PublishJMS();
        TestRunner runner = TestRunners.newTestRunner(pubProc);
        JMSConnectionFactoryProviderDefinition cs = Mockito.mock(JMSConnectionFactoryProviderDefinition.class);
        Mockito.when(cs.getIdentifier()).thenReturn("cfProvider");
        Mockito.when(cs.getConnectionFactory()).thenReturn(cf);
        runner.addControllerService("cfProvider", cs);
        runner.enableControllerService(cs);
        runner.setProperty(CF_SERVICE, "cfProvider");
        runner.setProperty(DESTINATION, destinationNameExpression);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("foo", "foo");
        attributes.put(REPLY_TO, "cooQueue");
        runner.enqueue("Hey dude!".getBytes(), attributes);
        runner.run(1, false);// Run once but don't shut down because we want the Connection Factory left in tact so that we can use it.

        final MockFlowFile successFF = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(successFF);
        JmsTemplate jmst = new JmsTemplate(cf);
        BytesMessage message = ((BytesMessage) (jmst.receive(destinationName)));
        byte[] messageBytes = MessageBodyToBytesConverter.toBytes(message);
        Assert.assertEquals("Hey dude!", new String(messageBytes));
        Assert.assertEquals("cooQueue", getQueueName());
        Assert.assertEquals("foo", message.getStringProperty("foo"));
        runner.run(1, true, false);// Run once just so that we can trigger the shutdown of the Connection Factory

    }

    @Test
    public void validateFailedPublishAndTransferToFailure() throws Exception {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        PublishJMS pubProc = new PublishJMS();
        TestRunner runner = TestRunners.newTestRunner(pubProc);
        JMSConnectionFactoryProviderDefinition cs = Mockito.mock(JMSConnectionFactoryProviderDefinition.class);
        Mockito.when(cs.getIdentifier()).thenReturn("cfProvider");
        Mockito.when(cs.getConnectionFactory()).thenReturn(cf);
        runner.addControllerService("cfProvider", cs);
        runner.enableControllerService(cs);
        runner.setProperty(CF_SERVICE, "cfProvider");
        runner.setProperty(DESTINATION, "validateFailedPublishAndTransferToFailure");
        runner.enqueue("Hello Joe".getBytes());
        runner.run();
        Thread.sleep(200);
        Assert.assertTrue(runner.getFlowFilesForRelationship(REL_SUCCESS).isEmpty());
        Assert.assertNotNull(runner.getFlowFilesForRelationship(REL_FAILURE).get(0));
    }

    @Test(timeout = 10000)
    public void validatePublishTextMessage() throws Exception {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        final String destinationName = "validatePublishTextMessage";
        PublishJMS pubProc = new PublishJMS();
        TestRunner runner = TestRunners.newTestRunner(pubProc);
        JMSConnectionFactoryProviderDefinition cs = Mockito.mock(JMSConnectionFactoryProviderDefinition.class);
        Mockito.when(cs.getIdentifier()).thenReturn("cfProvider");
        Mockito.when(cs.getConnectionFactory()).thenReturn(cf);
        runner.addControllerService("cfProvider", cs);
        runner.enableControllerService(cs);
        runner.setProperty(CF_SERVICE, "cfProvider");
        runner.setProperty(DESTINATION, destinationName);
        runner.setProperty(MESSAGE_BODY, "text");
        Map<String, String> attributes = new HashMap<>();
        attributes.put("foo", "foo");
        attributes.put(REPLY_TO, "cooQueue");
        runner.enqueue("Hey dude!".getBytes(), attributes);
        runner.run(1, false);
        final MockFlowFile successFF = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(successFF);
        JmsTemplate jmst = new JmsTemplate(cf);
        Message message = jmst.receive(destinationName);
        Assert.assertTrue((message instanceof TextMessage));
        TextMessage textMessage = ((TextMessage) (message));
        byte[] messageBytes = MessageBodyToBytesConverter.toBytes(textMessage);
        Assert.assertEquals("Hey dude!", new String(messageBytes));
        Assert.assertEquals("cooQueue", getQueueName());
        Assert.assertEquals("foo", message.getStringProperty("foo"));
        runner.run(1, true, false);// Run once just so that we can trigger the shutdown of the Connection Factory

    }

    @Test(timeout = 10000)
    public void validatePublishPropertyTypes() throws Exception {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        final String destinationName = "validatePublishPropertyTypes";
        PublishJMS pubProc = new PublishJMS();
        TestRunner runner = TestRunners.newTestRunner(pubProc);
        JMSConnectionFactoryProviderDefinition cs = Mockito.mock(JMSConnectionFactoryProviderDefinition.class);
        Mockito.when(cs.getIdentifier()).thenReturn("cfProvider");
        Mockito.when(cs.getConnectionFactory()).thenReturn(cf);
        runner.addControllerService("cfProvider", cs);
        runner.enableControllerService(cs);
        runner.setProperty(CF_SERVICE, "cfProvider");
        runner.setProperty(DESTINATION, destinationName);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("foo", "foo");
        attributes.put("myboolean", "true");
        attributes.put("myboolean.type", "boolean");
        attributes.put("mybyte", "127");
        attributes.put("mybyte.type", "byte");
        attributes.put("myshort", "16384");
        attributes.put("myshort.type", "short");
        attributes.put("myinteger", "1544000");
        attributes.put("myinteger.type", "INTEGER");// test upper case

        attributes.put("mylong", "9876543210");
        attributes.put("mylong.type", "long");
        attributes.put("myfloat", "3.14");
        attributes.put("myfloat.type", "float");
        attributes.put("mydouble", "3.14159265359");
        attributes.put("mydouble.type", "double");
        attributes.put("badtype", "3.14");
        attributes.put("badtype.type", "pi");// pi not recognized as a type, so send as String

        attributes.put("badint", "3.14");// value is not an integer

        attributes.put("badint.type", "integer");
        runner.enqueue("Hey dude!".getBytes(), attributes);
        runner.run(1, false);// Run once but don't shut down because we want the Connection Factory left intact so that we can use it.

        final MockFlowFile successFF = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(successFF);
        JmsTemplate jmst = new JmsTemplate(cf);
        BytesMessage message = ((BytesMessage) (jmst.receive(destinationName)));
        byte[] messageBytes = MessageBodyToBytesConverter.toBytes(message);
        Assert.assertEquals("Hey dude!", new String(messageBytes));
        Assert.assertEquals(true, ((message.getObjectProperty("foo")) instanceof String));
        Assert.assertEquals("foo", message.getStringProperty("foo"));
        Assert.assertEquals(true, ((message.getObjectProperty("myboolean")) instanceof Boolean));
        Assert.assertEquals(true, message.getBooleanProperty("myboolean"));
        Assert.assertEquals(true, ((message.getObjectProperty("mybyte")) instanceof Byte));
        Assert.assertEquals(127, message.getByteProperty("mybyte"));
        Assert.assertEquals(true, ((message.getObjectProperty("myshort")) instanceof Short));
        Assert.assertEquals(16384, message.getShortProperty("myshort"));
        Assert.assertEquals(true, ((message.getObjectProperty("myinteger")) instanceof Integer));
        Assert.assertEquals(1544000, message.getIntProperty("myinteger"));
        Assert.assertEquals(true, ((message.getObjectProperty("mylong")) instanceof Long));
        Assert.assertEquals(9876543210L, message.getLongProperty("mylong"));
        Assert.assertEquals(true, ((message.getObjectProperty("myfloat")) instanceof Float));
        Assert.assertEquals(3.14F, message.getFloatProperty("myfloat"), 0.001F);
        Assert.assertEquals(true, ((message.getObjectProperty("mydouble")) instanceof Double));
        Assert.assertEquals(3.14159265359, message.getDoubleProperty("mydouble"), 1.0E-11);
        Assert.assertEquals(true, ((message.getObjectProperty("badtype")) instanceof String));
        Assert.assertEquals("3.14", message.getStringProperty("badtype"));
        Assert.assertFalse(message.propertyExists("badint"));
        runner.run(1, true, false);// Run once just so that we can trigger the shutdown of the Connection Factory

    }
}

