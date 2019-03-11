/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.command;


import CommandTypes.ACTIVEMQ_MESSAGE;
import DeliveryMode.NON_PERSISTENT;
import DeliveryMode.PERSISTENT;
import java.io.IOException;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotWriteableException;
import javax.jms.org.apache.activemq.command.Message;
import junit.framework.TestCase;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.state.CommandVisitor;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.wireformat.WireFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ActiveMQMessageTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(ActiveMQMessageTest.class);

    protected boolean readOnlyMessage;

    private String jmsMessageID;

    private String jmsCorrelationID;

    private ActiveMQDestination jmsDestination;

    private ActiveMQDestination jmsReplyTo;

    private int jmsDeliveryMode;

    private boolean jmsRedelivered;

    private String jmsType;

    private long jmsExpiration;

    private int jmsPriority;

    private long jmsTimestamp;

    private long[] consumerIDs;

    /**
     * Constructor for ActiveMQMessageTest.
     *
     * @param name
     * 		
     */
    public ActiveMQMessageTest(String name) {
        super(name);
    }

    public void testGetDataStructureType() {
        ActiveMQMessage msg = new ActiveMQMessage();
        TestCase.assertEquals(msg.getDataStructureType(), ACTIVEMQ_MESSAGE);
    }

    public void testHashCode() throws Exception {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSMessageID(this.jmsMessageID);
        TestCase.assertTrue(((msg.getJMSMessageID().hashCode()) == (jmsMessageID.hashCode())));
    }

    public void testSetReadOnly() {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setReadOnlyProperties(true);
        boolean test = false;
        try {
            msg.setIntProperty("test", 1);
        } catch (MessageNotWriteableException me) {
            test = true;
        } catch (JMSException e) {
            e.printStackTrace(System.err);
            test = false;
        }
        TestCase.assertTrue(test);
    }

    public void testSetToForeignJMSID() throws Exception {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSMessageID("ID:EMS-SERVER.8B443C380083:429");
    }

    /* Class to test for boolean equals(Object) */
    public void testEqualsObject() throws Exception {
        ActiveMQMessage msg1 = new ActiveMQMessage();
        ActiveMQMessage msg2 = new ActiveMQMessage();
        msg1.setJMSMessageID(this.jmsMessageID);
        TestCase.assertTrue((!(msg1.equals(msg2))));
        msg2.setJMSMessageID(this.jmsMessageID);
        TestCase.assertTrue(msg1.equals(msg2));
    }

    public void testShallowCopy() throws Exception {
        ActiveMQMessage msg1 = new ActiveMQMessage();
        msg1.setJMSMessageID(jmsMessageID);
        ActiveMQMessage msg2 = ((ActiveMQMessage) (msg1.copy()));
        TestCase.assertTrue(((msg1 != msg2) && (msg1.equals(msg2))));
    }

    public void testCopy() throws Exception {
        this.jmsMessageID = "testid";
        this.jmsCorrelationID = "testcorrelationid";
        this.jmsDestination = new ActiveMQTopic("test.topic");
        this.jmsReplyTo = new ActiveMQTempTopic("test.replyto.topic:001");
        this.jmsDeliveryMode = Message.DEFAULT_DELIVERY_MODE;
        this.jmsRedelivered = true;
        this.jmsType = "test type";
        this.jmsExpiration = 100000;
        this.jmsPriority = 5;
        this.jmsTimestamp = System.currentTimeMillis();
        this.readOnlyMessage = false;
        ActiveMQMessage msg1 = new ActiveMQMessage();
        msg1.setJMSMessageID(this.jmsMessageID);
        msg1.setJMSCorrelationID(this.jmsCorrelationID);
        msg1.setJMSDestination(this.jmsDestination);
        msg1.setJMSReplyTo(this.jmsReplyTo);
        msg1.setJMSDeliveryMode(this.jmsDeliveryMode);
        msg1.setJMSRedelivered(this.jmsRedelivered);
        msg1.setJMSType(this.jmsType);
        msg1.setJMSExpiration(this.jmsExpiration);
        msg1.setJMSPriority(this.jmsPriority);
        msg1.setJMSTimestamp(this.jmsTimestamp);
        msg1.setReadOnlyProperties(true);
        ActiveMQMessage msg2 = new ActiveMQMessage();
        msg1.copy(msg2);
        TestCase.assertEquals(msg1.getJMSMessageID(), msg2.getJMSMessageID());
        TestCase.assertTrue(msg1.getJMSCorrelationID().equals(msg2.getJMSCorrelationID()));
        TestCase.assertTrue(msg1.getJMSDestination().equals(msg2.getJMSDestination()));
        TestCase.assertTrue(msg1.getJMSReplyTo().equals(msg2.getJMSReplyTo()));
        TestCase.assertTrue(((msg1.getJMSDeliveryMode()) == (msg2.getJMSDeliveryMode())));
        TestCase.assertTrue(((msg1.getJMSRedelivered()) == (msg2.getJMSRedelivered())));
        TestCase.assertTrue(msg1.getJMSType().equals(msg2.getJMSType()));
        TestCase.assertTrue(((msg1.getJMSExpiration()) == (msg2.getJMSExpiration())));
        TestCase.assertTrue(((msg1.getJMSPriority()) == (msg2.getJMSPriority())));
        TestCase.assertTrue(((msg1.getJMSTimestamp()) == (msg2.getJMSTimestamp())));
        ActiveMQMessageTest.LOG.info(("Message is:  " + msg1));
    }

    public void testGetAndSetJMSMessageID() throws Exception {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSMessageID(this.jmsMessageID);
        TestCase.assertEquals(msg.getJMSMessageID(), this.jmsMessageID);
    }

    public void testGetAndSetJMSTimestamp() {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSTimestamp(this.jmsTimestamp);
        TestCase.assertTrue(((msg.getJMSTimestamp()) == (this.jmsTimestamp)));
    }

    public void testGetJMSCorrelationIDAsBytes() throws Exception {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSCorrelationID(this.jmsCorrelationID);
        byte[] testbytes = msg.getJMSCorrelationIDAsBytes();
        String str2 = new String(testbytes);
        TestCase.assertTrue(this.jmsCorrelationID.equals(str2));
    }

    public void testSetJMSCorrelationIDAsBytes() throws Exception {
        ActiveMQMessage msg = new ActiveMQMessage();
        byte[] testbytes = this.jmsCorrelationID.getBytes();
        msg.setJMSCorrelationIDAsBytes(testbytes);
        testbytes = msg.getJMSCorrelationIDAsBytes();
        String str2 = new String(testbytes);
        TestCase.assertTrue(this.jmsCorrelationID.equals(str2));
    }

    public void testGetAndSetJMSCorrelationID() {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSCorrelationID(this.jmsCorrelationID);
        TestCase.assertTrue(msg.getJMSCorrelationID().equals(this.jmsCorrelationID));
    }

    public void testGetAndSetJMSReplyTo() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSReplyTo(this.jmsReplyTo);
        TestCase.assertTrue(msg.getJMSReplyTo().equals(this.jmsReplyTo));
    }

    public void testGetAndSetJMSDestination() throws Exception {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSDestination(this.jmsDestination);
        TestCase.assertTrue(msg.getJMSDestination().equals(this.jmsDestination));
    }

    public void testGetAndSetJMSDeliveryMode() {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSDeliveryMode(this.jmsDeliveryMode);
        TestCase.assertTrue(((msg.getJMSDeliveryMode()) == (this.jmsDeliveryMode)));
    }

    public void testGetAndSetMSRedelivered() {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSRedelivered(this.jmsRedelivered);
        TestCase.assertTrue(((msg.getJMSRedelivered()) == (this.jmsRedelivered)));
    }

    public void testGetAndSetJMSType() {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSType(this.jmsType);
        TestCase.assertTrue(msg.getJMSType().equals(this.jmsType));
    }

    public void testGetAndSetJMSExpiration() {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSExpiration(this.jmsExpiration);
        TestCase.assertTrue(((msg.getJMSExpiration()) == (this.jmsExpiration)));
    }

    public void testGetAndSetJMSPriority() {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSPriority(this.jmsPriority);
        TestCase.assertTrue(((msg.getJMSPriority()) == (this.jmsPriority)));
        msg.setJMSPriority((-90));
        TestCase.assertEquals(0, msg.getJMSPriority());
        msg.setJMSPriority(90);
        TestCase.assertEquals(9, msg.getJMSPriority());
    }

    public void testClearProperties() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setStringProperty("test", "test");
        msg.setContent(new ByteSequence(new byte[1], 0, 0));
        msg.setJMSMessageID(this.jmsMessageID);
        msg.clearProperties();
        TestCase.assertNull(msg.getStringProperty("test"));
        TestCase.assertNotNull(msg.getJMSMessageID());
        TestCase.assertNotNull(msg.getContent());
    }

    public void testPropertyExists() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setStringProperty("test", "test");
        TestCase.assertTrue(msg.propertyExists("test"));
        msg.setIntProperty("JMSXDeliveryCount", 1);
        TestCase.assertTrue(msg.propertyExists("JMSXDeliveryCount"));
    }

    public void testGetBooleanProperty() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String name = "booleanProperty";
        msg.setBooleanProperty(name, true);
        TestCase.assertTrue(msg.getBooleanProperty(name));
    }

    public void testGetByteProperty() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String name = "byteProperty";
        msg.setByteProperty(name, ((byte) (1)));
        TestCase.assertTrue(((msg.getByteProperty(name)) == 1));
    }

    public void testGetShortProperty() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String name = "shortProperty";
        msg.setShortProperty(name, ((short) (1)));
        TestCase.assertTrue(((msg.getShortProperty(name)) == 1));
    }

    public void testGetIntProperty() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String name = "intProperty";
        msg.setIntProperty(name, 1);
        TestCase.assertTrue(((msg.getIntProperty(name)) == 1));
    }

    public void testGetLongProperty() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String name = "longProperty";
        msg.setLongProperty(name, 1);
        TestCase.assertTrue(((msg.getLongProperty(name)) == 1));
    }

    public void testGetFloatProperty() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String name = "floatProperty";
        msg.setFloatProperty(name, 1.3F);
        TestCase.assertTrue(((msg.getFloatProperty(name)) == 1.3F));
    }

    public void testGetDoubleProperty() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String name = "doubleProperty";
        msg.setDoubleProperty(name, 1.3);
        TestCase.assertTrue(((msg.getDoubleProperty(name)) == 1.3));
    }

    public void testGetStringProperty() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String name = "stringProperty";
        msg.setStringProperty(name, name);
        TestCase.assertTrue(msg.getStringProperty(name).equals(name));
    }

    public void testGetObjectProperty() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String name = "floatProperty";
        msg.setFloatProperty(name, 1.3F);
        TestCase.assertTrue(((msg.getObjectProperty(name)) instanceof Float));
        TestCase.assertTrue(((((Float) (msg.getObjectProperty(name))).floatValue()) == 1.3F));
    }

    public void testSetJMSDeliveryModeProperty() throws JMSException {
        ActiveMQMessage message = new ActiveMQMessage();
        String propertyName = "JMSDeliveryMode";
        // Set as Boolean
        message.setObjectProperty(propertyName, Boolean.TRUE);
        TestCase.assertTrue(message.isPersistent());
        message.setObjectProperty(propertyName, Boolean.FALSE);
        TestCase.assertFalse(message.isPersistent());
        message.setBooleanProperty(propertyName, true);
        TestCase.assertTrue(message.isPersistent());
        message.setBooleanProperty(propertyName, false);
        TestCase.assertFalse(message.isPersistent());
        // Set as Integer
        message.setObjectProperty(propertyName, PERSISTENT);
        TestCase.assertTrue(message.isPersistent());
        message.setObjectProperty(propertyName, NON_PERSISTENT);
        TestCase.assertFalse(message.isPersistent());
        message.setIntProperty(propertyName, PERSISTENT);
        TestCase.assertTrue(message.isPersistent());
        message.setIntProperty(propertyName, NON_PERSISTENT);
        TestCase.assertFalse(message.isPersistent());
        // Set as String
        message.setObjectProperty(propertyName, "PERSISTENT");
        TestCase.assertTrue(message.isPersistent());
        message.setObjectProperty(propertyName, "NON_PERSISTENT");
        TestCase.assertFalse(message.isPersistent());
        message.setStringProperty(propertyName, "PERSISTENT");
        TestCase.assertTrue(message.isPersistent());
        message.setStringProperty(propertyName, "NON_PERSISTENT");
        TestCase.assertFalse(message.isPersistent());
    }

    public void testSetObjectProperty() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String name = "property";
        try {
            msg.setObjectProperty(name, "string");
            msg.setObjectProperty(name, Byte.valueOf("1"));
            msg.setObjectProperty(name, Short.valueOf("1"));
            msg.setObjectProperty(name, Integer.valueOf("1"));
            msg.setObjectProperty(name, Long.valueOf("1"));
            msg.setObjectProperty(name, Float.valueOf("1.1f"));
            msg.setObjectProperty(name, Double.valueOf("1.1"));
            msg.setObjectProperty(name, Boolean.TRUE);
            msg.setObjectProperty(name, null);
        } catch (MessageFormatException e) {
            TestCase.fail("should accept object primitives and String");
        }
        try {
            msg.setObjectProperty(name, new byte[5]);
            TestCase.fail("should accept only object primitives and String");
        } catch (MessageFormatException e) {
        }
        try {
            msg.setObjectProperty(name, new Object());
            TestCase.fail("should accept only object primitives and String");
        } catch (MessageFormatException e) {
        }
    }

    public void testConvertProperties() throws Exception {
        org.apache.activemq.command.Message msg = new org.apache.activemq.command.Message() {
            @Override
            public Message copy() {
                return null;
            }

            @Override
            public void beforeMarshall(WireFormat wireFormat) throws IOException {
                super.beforeMarshall(wireFormat);
            }

            @Override
            public byte getDataStructureType() {
                return 0;
            }

            @Override
            public Response visit(CommandVisitor visitor) throws Exception {
                return null;
            }

            @Override
            public void clearBody() throws JMSException {
            }

            @Override
            public void storeContent() {
            }

            @Override
            public void storeContentAndClear() {
            }
        };
        msg.setProperty("stringProperty", "string");
        msg.setProperty("byteProperty", Byte.valueOf("1"));
        msg.setProperty("shortProperty", Short.valueOf("1"));
        msg.setProperty("intProperty", Integer.valueOf("1"));
        msg.setProperty("longProperty", Long.valueOf("1"));
        msg.setProperty("floatProperty", Float.valueOf("1.1f"));
        msg.setProperty("doubleProperty", Double.valueOf("1.1"));
        msg.setProperty("booleanProperty", Boolean.TRUE);
        msg.setProperty("nullProperty", null);
        msg.beforeMarshall(new OpenWireFormat());
        Map<String, Object> properties = msg.getProperties();
        TestCase.assertEquals(properties.get("stringProperty"), "string");
        TestCase.assertEquals(((Byte) (properties.get("byteProperty"))).byteValue(), 1);
        TestCase.assertEquals(((Short) (properties.get("shortProperty"))).shortValue(), 1);
        TestCase.assertEquals(((Integer) (properties.get("intProperty"))).intValue(), 1);
        TestCase.assertEquals(((Long) (properties.get("longProperty"))).longValue(), 1);
        TestCase.assertEquals(((Float) (properties.get("floatProperty"))).floatValue(), 1.1F, 0);
        TestCase.assertEquals(((Double) (properties.get("doubleProperty"))).doubleValue(), 1.1, 0);
        TestCase.assertEquals(((Boolean) (properties.get("booleanProperty"))).booleanValue(), true);
        TestCase.assertNull(properties.get("nullProperty"));
    }

    public void testSetNullProperty() throws JMSException {
        Message msg = new ActiveMQMessage();
        String name = "cheese";
        msg.setStringProperty(name, "Cheddar");
        TestCase.assertEquals("Cheddar", msg.getStringProperty(name));
        msg.setStringProperty(name, null);
        TestCase.assertEquals(null, msg.getStringProperty(name));
    }

    public void testSetNullPropertyName() throws JMSException {
        Message msg = new ActiveMQMessage();
        try {
            msg.setStringProperty(null, "Cheese");
            TestCase.fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            ActiveMQMessageTest.LOG.info(("Worked, caught: " + e));
        }
    }

    public void testSetEmptyPropertyName() throws JMSException {
        Message msg = new ActiveMQMessage();
        try {
            msg.setStringProperty("", "Cheese");
            TestCase.fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            ActiveMQMessageTest.LOG.info(("Worked, caught: " + e));
        }
    }

    public void testGetAndSetJMSXDeliveryCount() throws JMSException {
        Message msg = new ActiveMQMessage();
        msg.setIntProperty("JMSXDeliveryCount", 1);
        int count = msg.getIntProperty("JMSXDeliveryCount");
        TestCase.assertTrue(("expected delivery count = 1 - got: " + count), (count == 1));
    }

    public void testClearBody() throws JMSException {
        ActiveMQBytesMessage message = new ActiveMQBytesMessage();
        message.clearBody();
        TestCase.assertFalse(message.isReadOnlyBody());
        TestCase.assertNull(message.getContent());
    }

    public void testBooleanPropertyConversion() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String propertyName = "property";
        msg.setBooleanProperty(propertyName, true);
        TestCase.assertEquals(((Boolean) (msg.getObjectProperty(propertyName))).booleanValue(), true);
        TestCase.assertTrue(msg.getBooleanProperty(propertyName));
        TestCase.assertEquals(msg.getStringProperty(propertyName), "true");
        try {
            msg.getByteProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getIntProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getLongProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    public void testBytePropertyConversion() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String propertyName = "property";
        msg.setByteProperty(propertyName, ((byte) (1)));
        TestCase.assertEquals(((Byte) (msg.getObjectProperty(propertyName))).byteValue(), 1);
        TestCase.assertEquals(msg.getByteProperty(propertyName), 1);
        TestCase.assertEquals(msg.getShortProperty(propertyName), 1);
        TestCase.assertEquals(msg.getIntProperty(propertyName), 1);
        TestCase.assertEquals(msg.getLongProperty(propertyName), 1);
        TestCase.assertEquals(msg.getStringProperty(propertyName), "1");
        try {
            msg.getBooleanProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    public void testShortPropertyConversion() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String propertyName = "property";
        msg.setShortProperty(propertyName, ((short) (1)));
        TestCase.assertEquals(((Short) (msg.getObjectProperty(propertyName))).shortValue(), 1);
        TestCase.assertEquals(msg.getShortProperty(propertyName), 1);
        TestCase.assertEquals(msg.getIntProperty(propertyName), 1);
        TestCase.assertEquals(msg.getLongProperty(propertyName), 1);
        TestCase.assertEquals(msg.getStringProperty(propertyName), "1");
        try {
            msg.getBooleanProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getByteProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    public void testIntPropertyConversion() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String propertyName = "property";
        msg.setIntProperty(propertyName, 1);
        TestCase.assertEquals(((Integer) (msg.getObjectProperty(propertyName))).intValue(), 1);
        TestCase.assertEquals(msg.getIntProperty(propertyName), 1);
        TestCase.assertEquals(msg.getLongProperty(propertyName), 1);
        TestCase.assertEquals(msg.getStringProperty(propertyName), "1");
        try {
            msg.getBooleanProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getByteProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    public void testLongPropertyConversion() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String propertyName = "property";
        msg.setLongProperty(propertyName, 1);
        TestCase.assertEquals(((Long) (msg.getObjectProperty(propertyName))).longValue(), 1);
        TestCase.assertEquals(msg.getLongProperty(propertyName), 1);
        TestCase.assertEquals(msg.getStringProperty(propertyName), "1");
        try {
            msg.getBooleanProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getByteProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getIntProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    public void testFloatPropertyConversion() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String propertyName = "property";
        msg.setFloatProperty(propertyName, ((float) (1.5)));
        TestCase.assertEquals(((Float) (msg.getObjectProperty(propertyName))).floatValue(), 1.5, 0);
        TestCase.assertEquals(msg.getFloatProperty(propertyName), 1.5, 0);
        TestCase.assertEquals(msg.getDoubleProperty(propertyName), 1.5, 0);
        TestCase.assertEquals(msg.getStringProperty(propertyName), "1.5");
        try {
            msg.getBooleanProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getByteProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getIntProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getLongProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    public void testDoublePropertyConversion() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String propertyName = "property";
        msg.setDoubleProperty(propertyName, 1.5);
        TestCase.assertEquals(((Double) (msg.getObjectProperty(propertyName))).doubleValue(), 1.5, 0);
        TestCase.assertEquals(msg.getDoubleProperty(propertyName), 1.5, 0);
        TestCase.assertEquals(msg.getStringProperty(propertyName), "1.5");
        try {
            msg.getBooleanProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getByteProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getIntProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getLongProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    public void testStringPropertyConversion() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String propertyName = "property";
        String stringValue = "true";
        msg.setStringProperty(propertyName, stringValue);
        TestCase.assertEquals(msg.getStringProperty(propertyName), stringValue);
        TestCase.assertEquals(((String) (msg.getObjectProperty(propertyName))), stringValue);
        TestCase.assertEquals(msg.getBooleanProperty(propertyName), true);
        stringValue = "1";
        msg.setStringProperty(propertyName, stringValue);
        TestCase.assertEquals(msg.getByteProperty(propertyName), 1);
        TestCase.assertEquals(msg.getShortProperty(propertyName), 1);
        TestCase.assertEquals(msg.getIntProperty(propertyName), 1);
        TestCase.assertEquals(msg.getLongProperty(propertyName), 1);
        stringValue = "1.5";
        msg.setStringProperty(propertyName, stringValue);
        TestCase.assertEquals(msg.getFloatProperty(propertyName), 1.5, 0);
        TestCase.assertEquals(msg.getDoubleProperty(propertyName), 1.5, 0);
        stringValue = "bad";
        msg.setStringProperty(propertyName, stringValue);
        try {
            msg.getByteProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (NumberFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (NumberFormatException e) {
        }
        try {
            msg.getIntProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (NumberFormatException e) {
        }
        try {
            msg.getLongProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (NumberFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (NumberFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (NumberFormatException e) {
        }
        TestCase.assertFalse(msg.getBooleanProperty(propertyName));
    }

    public void testObjectPropertyConversion() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String propertyName = "property";
        Object obj = new Object();
        try {
            setProperty(propertyName, obj);// bypass

            // object
            // check
        } catch (IOException e) {
        }
        try {
            msg.getStringProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getBooleanProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getByteProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getIntProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getLongProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            TestCase.fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    public void testReadOnlyProperties() throws JMSException {
        ActiveMQMessage msg = new ActiveMQMessage();
        String propertyName = "property";
        msg.setReadOnlyProperties(true);
        try {
            msg.setObjectProperty(propertyName, new Object());
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setStringProperty(propertyName, "test");
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setBooleanProperty(propertyName, true);
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setByteProperty(propertyName, ((byte) (1)));
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setShortProperty(propertyName, ((short) (1)));
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setIntProperty(propertyName, 1);
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setLongProperty(propertyName, 1);
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setFloatProperty(propertyName, ((float) (1.5)));
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setDoubleProperty(propertyName, 1.5);
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
    }

    public void testIsExpired() {
        ActiveMQMessage msg = new ActiveMQMessage();
        msg.setJMSExpiration(((System.currentTimeMillis()) - 1));
        TestCase.assertTrue(msg.isExpired());
        msg.setJMSExpiration(((System.currentTimeMillis()) + 10000));
        TestCase.assertFalse(msg.isExpired());
    }
}

