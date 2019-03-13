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


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the MapMessage implementation for spec compliance.
 */
public class ActiveMQMapMessageTest {
    private static final Logger LOG = LoggerFactory.getLogger(ActiveMQMapMessageTest.class);

    private final String name = "testName";

    @Test(timeout = 10000)
    public void testBytesConversion() throws IOException, JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setBoolean("boolean", true);
        msg.setByte("byte", ((byte) (1)));
        msg.setBytes("bytes", new byte[1]);
        msg.setChar("char", 'a');
        msg.setDouble("double", 1.5);
        msg.setFloat("float", 1.5F);
        msg.setInt("int", 1);
        msg.setLong("long", 1);
        msg.setObject("object", "stringObj");
        msg.setShort("short", ((short) (1)));
        msg.setString("string", "string");
        // Test with a 1Meg String
        StringBuffer bigSB = new StringBuffer((1024 * 1024));
        for (int i = 0; i < (1024 * 1024); i++) {
            bigSB.append(('a' + (i % 26)));
        }
        String bigString = bigSB.toString();
        msg.setString("bigString", bigString);
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertEquals(msg.getBoolean("boolean"), true);
        Assert.assertEquals(msg.getByte("byte"), ((byte) (1)));
        Assert.assertEquals(msg.getBytes("bytes").length, 1);
        Assert.assertEquals(msg.getChar("char"), 'a');
        Assert.assertEquals(msg.getDouble("double"), 1.5, 0);
        Assert.assertEquals(msg.getFloat("float"), 1.5F, 0);
        Assert.assertEquals(msg.getInt("int"), 1);
        Assert.assertEquals(msg.getLong("long"), 1);
        Assert.assertEquals(msg.getObject("object"), "stringObj");
        Assert.assertEquals(msg.getShort("short"), ((short) (1)));
        Assert.assertEquals(msg.getString("string"), "string");
        Assert.assertEquals(msg.getString("bigString"), bigString);
    }

    @Test(timeout = 10000)
    public void testGetBoolean() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setBoolean(name, true);
        msg.setReadOnlyBody(true);
        Assert.assertTrue(msg.getBoolean(name));
        msg.clearBody();
        msg.setString(name, "true");
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertTrue(msg.getBoolean(name));
    }

    @Test(timeout = 10000)
    public void testGetByte() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setByte(this.name, ((byte) (1)));
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertTrue(((msg.getByte(this.name)) == ((byte) (1))));
    }

    @Test(timeout = 10000)
    public void testGetShort() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setShort(this.name, ((short) (1)));
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertTrue(((msg.getShort(this.name)) == ((short) (1))));
    }

    @Test(timeout = 10000)
    public void testGetChar() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setChar(this.name, 'a');
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertTrue(((msg.getChar(this.name)) == 'a'));
    }

    @Test(timeout = 10000)
    public void testGetInt() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setInt(this.name, 1);
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertTrue(((msg.getInt(this.name)) == 1));
    }

    @Test(timeout = 10000)
    public void testGetLong() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setLong(this.name, 1);
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertTrue(((msg.getLong(this.name)) == 1));
    }

    @Test(timeout = 10000)
    public void testGetFloat() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setFloat(this.name, 1.5F);
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertTrue(((msg.getFloat(this.name)) == 1.5F));
    }

    @Test(timeout = 10000)
    public void testGetDouble() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setDouble(this.name, 1.5);
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertTrue(((msg.getDouble(this.name)) == 1.5));
    }

    @Test(timeout = 10000)
    public void testGetDoubleWithMaxValue() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setDouble(this.name, Double.MAX_VALUE);
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertEquals(Double.MAX_VALUE, msg.getDouble(this.name), 1.0);
    }

    @Test(timeout = 10000)
    public void testGetDoubleWithMaxValueAsString() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setString(this.name, String.valueOf(Double.MAX_VALUE));
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertEquals(Double.MAX_VALUE, msg.getDouble(this.name), 1.0);
    }

    @Test(timeout = 10000)
    public void testGetString() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        String str = "test";
        msg.setString(this.name, str);
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertEquals(msg.getString(this.name), str);
    }

    @Test(timeout = 10000)
    public void testGetBytes() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        byte[] bytes1 = new byte[3];
        byte[] bytes2 = new byte[2];
        System.arraycopy(bytes1, 0, bytes2, 0, 2);
        msg.setBytes(this.name, bytes1);
        msg.setBytes(((this.name) + "2"), bytes1, 0, 2);
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertTrue(Arrays.equals(msg.getBytes(this.name), bytes1));
        Assert.assertEquals(msg.getBytes(((this.name) + "2")).length, bytes2.length);
    }

    @Test(timeout = 10000)
    public void testGetBytesWithNullValue() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        Assert.assertNull(msg.getBytes(this.name));
    }

    @Test(timeout = 10000)
    public void testGetObject() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        Boolean booleanValue = Boolean.TRUE;
        Byte byteValue = Byte.valueOf("1");
        byte[] bytesValue = new byte[3];
        Character charValue = new Character('a');
        Double doubleValue = Double.valueOf("1.5");
        Float floatValue = Float.valueOf("1.5");
        Integer intValue = Integer.valueOf("1");
        Long longValue = Long.valueOf("1");
        Short shortValue = Short.valueOf("1");
        String stringValue = "string";
        try {
            msg.setObject("boolean", booleanValue);
            msg.setObject("byte", byteValue);
            msg.setObject("bytes", bytesValue);
            msg.setObject("char", charValue);
            msg.setObject("double", doubleValue);
            msg.setObject("float", floatValue);
            msg.setObject("int", intValue);
            msg.setObject("long", longValue);
            msg.setObject("short", shortValue);
            msg.setObject("string", stringValue);
        } catch (MessageFormatException mfe) {
            ActiveMQMapMessageTest.LOG.warn(("Caught: " + mfe));
            mfe.printStackTrace();
            Assert.fail("object formats should be correct");
        }
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Assert.assertTrue(((msg.getObject("boolean")) instanceof Boolean));
        Assert.assertEquals(msg.getObject("boolean"), booleanValue);
        Assert.assertEquals(msg.getBoolean("boolean"), booleanValue.booleanValue());
        Assert.assertTrue(((msg.getObject("byte")) instanceof Byte));
        Assert.assertEquals(msg.getObject("byte"), byteValue);
        Assert.assertEquals(msg.getByte("byte"), byteValue.byteValue());
        Assert.assertTrue(((msg.getObject("bytes")) instanceof byte[]));
        Assert.assertEquals(((byte[]) (msg.getObject("bytes"))).length, bytesValue.length);
        Assert.assertEquals(msg.getBytes("bytes").length, bytesValue.length);
        Assert.assertTrue(((msg.getObject("char")) instanceof Character));
        Assert.assertEquals(msg.getObject("char"), charValue);
        Assert.assertEquals(msg.getChar("char"), charValue.charValue());
        Assert.assertTrue(((msg.getObject("double")) instanceof Double));
        Assert.assertEquals(msg.getObject("double"), doubleValue);
        Assert.assertEquals(msg.getDouble("double"), doubleValue.doubleValue(), 0);
        Assert.assertTrue(((msg.getObject("float")) instanceof Float));
        Assert.assertEquals(msg.getObject("float"), floatValue);
        Assert.assertEquals(msg.getFloat("float"), floatValue.floatValue(), 0);
        Assert.assertTrue(((msg.getObject("int")) instanceof Integer));
        Assert.assertEquals(msg.getObject("int"), intValue);
        Assert.assertEquals(msg.getInt("int"), intValue.intValue());
        Assert.assertTrue(((msg.getObject("long")) instanceof Long));
        Assert.assertEquals(msg.getObject("long"), longValue);
        Assert.assertEquals(msg.getLong("long"), longValue.longValue());
        Assert.assertTrue(((msg.getObject("short")) instanceof Short));
        Assert.assertEquals(msg.getObject("short"), shortValue);
        Assert.assertEquals(msg.getShort("short"), shortValue.shortValue());
        Assert.assertTrue(((msg.getObject("string")) instanceof String));
        Assert.assertEquals(msg.getObject("string"), stringValue);
        Assert.assertEquals(msg.getString("string"), stringValue);
        msg.clearBody();
        try {
            msg.setObject("object", new Object());
            Assert.fail("should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    @Test(timeout = 10000)
    public void testGetMapNames() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setBoolean("boolean", true);
        msg.setByte("byte", ((byte) (1)));
        msg.setBytes("bytes1", new byte[1]);
        msg.setBytes("bytes2", new byte[3], 0, 2);
        msg.setChar("char", 'a');
        msg.setDouble("double", 1.5);
        msg.setFloat("float", 1.5F);
        msg.setInt("int", 1);
        msg.setLong("long", 1);
        msg.setObject("object", "stringObj");
        msg.setShort("short", ((short) (1)));
        msg.setString("string", "string");
        msg = ((ActiveMQMapMessage) (msg.copy()));
        Enumeration<String> mapNamesEnum = msg.getMapNames();
        List<String> mapNamesList = Collections.list(mapNamesEnum);
        Assert.assertEquals(mapNamesList.size(), 12);
        Assert.assertTrue(mapNamesList.contains("boolean"));
        Assert.assertTrue(mapNamesList.contains("byte"));
        Assert.assertTrue(mapNamesList.contains("bytes1"));
        Assert.assertTrue(mapNamesList.contains("bytes2"));
        Assert.assertTrue(mapNamesList.contains("char"));
        Assert.assertTrue(mapNamesList.contains("double"));
        Assert.assertTrue(mapNamesList.contains("float"));
        Assert.assertTrue(mapNamesList.contains("int"));
        Assert.assertTrue(mapNamesList.contains("long"));
        Assert.assertTrue(mapNamesList.contains("object"));
        Assert.assertTrue(mapNamesList.contains("short"));
        Assert.assertTrue(mapNamesList.contains("string"));
    }

    @Test(timeout = 10000)
    public void testItemExists() throws JMSException {
        ActiveMQMapMessage mapMessage = new ActiveMQMapMessage();
        mapMessage.setString("exists", "test");
        mapMessage = ((ActiveMQMapMessage) (mapMessage.copy()));
        Assert.assertTrue(mapMessage.itemExists("exists"));
        Assert.assertFalse(mapMessage.itemExists("doesntExist"));
    }

    @Test(timeout = 10000)
    public void testClearBody() throws JMSException {
        ActiveMQMapMessage mapMessage = new ActiveMQMapMessage();
        mapMessage.setString("String", "String");
        mapMessage.clearBody();
        Assert.assertFalse(mapMessage.isReadOnlyBody());
        mapMessage.onSend();
        mapMessage.setContent(mapMessage.getContent());
        Assert.assertNull(mapMessage.getString("String"));
        mapMessage.clearBody();
        mapMessage.setString("String", "String");
        mapMessage = ((ActiveMQMapMessage) (mapMessage.copy()));
        mapMessage.getString("String");
    }

    @Test(timeout = 10000)
    public void testReadOnlyBody() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setBoolean("boolean", true);
        msg.setByte("byte", ((byte) (1)));
        msg.setBytes("bytes", new byte[1]);
        msg.setBytes("bytes2", new byte[3], 0, 2);
        msg.setChar("char", 'a');
        msg.setDouble("double", 1.5);
        msg.setFloat("float", 1.5F);
        msg.setInt("int", 1);
        msg.setLong("long", 1);
        msg.setObject("object", "stringObj");
        msg.setShort("short", ((short) (1)));
        msg.setString("string", "string");
        msg.setReadOnlyBody(true);
        try {
            msg.getBoolean("boolean");
            msg.getByte("byte");
            msg.getBytes("bytes");
            msg.getChar("char");
            msg.getDouble("double");
            msg.getFloat("float");
            msg.getInt("int");
            msg.getLong("long");
            msg.getObject("object");
            msg.getShort("short");
            msg.getString("string");
        } catch (MessageNotReadableException mnre) {
            Assert.fail("should be readable");
        }
        try {
            msg.setBoolean("boolean", true);
            Assert.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            msg.setByte("byte", ((byte) (1)));
            Assert.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            msg.setBytes("bytes", new byte[1]);
            Assert.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            msg.setBytes("bytes2", new byte[3], 0, 2);
            Assert.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            msg.setChar("char", 'a');
            Assert.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            msg.setDouble("double", 1.5);
            Assert.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            msg.setFloat("float", 1.5F);
            Assert.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            msg.setInt("int", 1);
            Assert.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            msg.setLong("long", 1);
            Assert.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            msg.setObject("object", "stringObj");
            Assert.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            msg.setShort("short", ((short) (1)));
            Assert.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            msg.setString("string", "string");
            Assert.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
    }

    @Test(timeout = 10000)
    public void testWriteOnlyBody() throws JMSException {
        ActiveMQMapMessage msg = new ActiveMQMapMessage();
        msg.setReadOnlyBody(false);
        msg.setBoolean("boolean", true);
        msg.setByte("byte", ((byte) (1)));
        msg.setBytes("bytes", new byte[1]);
        msg.setBytes("bytes2", new byte[3], 0, 2);
        msg.setChar("char", 'a');
        msg.setDouble("double", 1.5);
        msg.setFloat("float", 1.5F);
        msg.setInt("int", 1);
        msg.setLong("long", 1);
        msg.setObject("object", "stringObj");
        msg.setShort("short", ((short) (1)));
        msg.setString("string", "string");
        msg.setReadOnlyBody(true);
        msg.getBoolean("boolean");
        msg.getByte("byte");
        msg.getBytes("bytes");
        msg.getChar("char");
        msg.getDouble("double");
        msg.getFloat("float");
        msg.getInt("int");
        msg.getLong("long");
        msg.getObject("object");
        msg.getShort("short");
        msg.getString("string");
    }
}

