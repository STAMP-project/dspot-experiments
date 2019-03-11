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


import CommandTypes.ACTIVEMQ_STREAM_MESSAGE;
import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the ActiveMQ StreamMessage implementation
 */
public class ActiveMQStreamMessageTest {
    @Test
    public void testGetDataStructureType() {
        ActiveMQStreamMessage msg = new ActiveMQStreamMessage();
        Assert.assertEquals(msg.getDataStructureType(), ACTIVEMQ_STREAM_MESSAGE);
    }

    @Test
    public void testReadBoolean() {
        ActiveMQStreamMessage msg = new ActiveMQStreamMessage();
        try {
            msg.writeBoolean(true);
            msg.reset();
            Assert.assertTrue(msg.readBoolean());
            msg.reset();
            Assert.assertTrue(msg.readString().equals("true"));
            msg.reset();
            try {
                msg.readByte();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readShort();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readInt();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readLong();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readFloat();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readDouble();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readChar();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readBytes(new byte[1]);
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testreadByte() {
        ActiveMQStreamMessage msg = new ActiveMQStreamMessage();
        try {
            byte test = ((byte) (4));
            msg.writeByte(test);
            msg.reset();
            Assert.assertTrue(((msg.readByte()) == test));
            msg.reset();
            Assert.assertTrue(((msg.readShort()) == test));
            msg.reset();
            Assert.assertTrue(((msg.readInt()) == test));
            msg.reset();
            Assert.assertTrue(((msg.readLong()) == test));
            msg.reset();
            Assert.assertTrue(msg.readString().equals(new Byte(test).toString()));
            msg.reset();
            try {
                msg.readBoolean();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readFloat();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readDouble();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readChar();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readBytes(new byte[1]);
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testReadShort() {
        ActiveMQStreamMessage msg = new ActiveMQStreamMessage();
        try {
            short test = ((short) (4));
            msg.writeShort(test);
            msg.reset();
            Assert.assertTrue(((msg.readShort()) == test));
            msg.reset();
            Assert.assertTrue(((msg.readInt()) == test));
            msg.reset();
            Assert.assertTrue(((msg.readLong()) == test));
            msg.reset();
            Assert.assertTrue(msg.readString().equals(new Short(test).toString()));
            msg.reset();
            try {
                msg.readBoolean();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readByte();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readFloat();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readDouble();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readChar();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readBytes(new byte[1]);
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testReadChar() {
        ActiveMQStreamMessage msg = new ActiveMQStreamMessage();
        try {
            char test = 'z';
            msg.writeChar(test);
            msg.reset();
            Assert.assertTrue(((msg.readChar()) == test));
            msg.reset();
            Assert.assertTrue(msg.readString().equals(new Character(test).toString()));
            msg.reset();
            try {
                msg.readBoolean();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readByte();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readShort();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readInt();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readLong();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readFloat();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readDouble();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readBytes(new byte[1]);
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testReadInt() {
        ActiveMQStreamMessage msg = new ActiveMQStreamMessage();
        try {
            int test = 4;
            msg.writeInt(test);
            msg.reset();
            Assert.assertTrue(((msg.readInt()) == test));
            msg.reset();
            Assert.assertTrue(((msg.readLong()) == test));
            msg.reset();
            Assert.assertTrue(msg.readString().equals(new Integer(test).toString()));
            msg.reset();
            try {
                msg.readBoolean();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readByte();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readShort();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readFloat();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readDouble();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readChar();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readBytes(new byte[1]);
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testReadLong() {
        ActiveMQStreamMessage msg = new ActiveMQStreamMessage();
        try {
            long test = 4L;
            msg.writeLong(test);
            msg.reset();
            Assert.assertTrue(((msg.readLong()) == test));
            msg.reset();
            Assert.assertTrue(msg.readString().equals(Long.valueOf(test).toString()));
            msg.reset();
            try {
                msg.readBoolean();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readByte();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readShort();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readInt();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readFloat();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readDouble();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readChar();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readBytes(new byte[1]);
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg = new ActiveMQStreamMessage();
            msg.writeObject(new Long("1"));
            // reset so it's readable now
            msg.reset();
            Assert.assertEquals(new Long("1"), msg.readObject());
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testReadFloat() {
        ActiveMQStreamMessage msg = new ActiveMQStreamMessage();
        try {
            float test = 4.4F;
            msg.writeFloat(test);
            msg.reset();
            Assert.assertTrue(((msg.readFloat()) == test));
            msg.reset();
            Assert.assertTrue(((msg.readDouble()) == test));
            msg.reset();
            Assert.assertTrue(msg.readString().equals(new Float(test).toString()));
            msg.reset();
            try {
                msg.readBoolean();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readByte();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readShort();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readInt();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readLong();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readChar();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readBytes(new byte[1]);
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testReadDouble() {
        ActiveMQStreamMessage msg = new ActiveMQStreamMessage();
        try {
            double test = 4.4;
            msg.writeDouble(test);
            msg.reset();
            Assert.assertTrue(((msg.readDouble()) == test));
            msg.reset();
            Assert.assertTrue(msg.readString().equals(new Double(test).toString()));
            msg.reset();
            try {
                msg.readBoolean();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readByte();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readShort();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readInt();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readLong();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readFloat();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readChar();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readBytes(new byte[1]);
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testReadString() {
        ActiveMQStreamMessage msg = new ActiveMQStreamMessage();
        try {
            byte testByte = ((byte) (2));
            msg.writeString(new Byte(testByte).toString());
            msg.reset();
            Assert.assertTrue(((msg.readByte()) == testByte));
            msg.clearBody();
            short testShort = 3;
            msg.writeString(new Short(testShort).toString());
            msg.reset();
            Assert.assertTrue(((msg.readShort()) == testShort));
            msg.clearBody();
            int testInt = 4;
            msg.writeString(new Integer(testInt).toString());
            msg.reset();
            Assert.assertTrue(((msg.readInt()) == testInt));
            msg.clearBody();
            long testLong = 6L;
            msg.writeString(new Long(testLong).toString());
            msg.reset();
            Assert.assertTrue(((msg.readLong()) == testLong));
            msg.clearBody();
            float testFloat = 6.6F;
            msg.writeString(new Float(testFloat).toString());
            msg.reset();
            Assert.assertTrue(((msg.readFloat()) == testFloat));
            msg.clearBody();
            double testDouble = 7.7;
            msg.writeString(new Double(testDouble).toString());
            msg.reset();
            Assert.assertTrue(((msg.readDouble()) == testDouble));
            msg.clearBody();
            msg.writeString("true");
            msg.reset();
            Assert.assertTrue(msg.readBoolean());
            msg.clearBody();
            msg.writeString("a");
            msg.reset();
            try {
                msg.readChar();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException e) {
            }
            msg.clearBody();
            msg.writeString("777");
            msg.reset();
            try {
                msg.readBytes(new byte[3]);
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException e) {
            }
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testReadBigString() {
        ActiveMQStreamMessage msg = new ActiveMQStreamMessage();
        try {
            // Test with a 1Meg String
            StringBuffer bigSB = new StringBuffer((1024 * 1024));
            for (int i = 0; i < (1024 * 1024); i++) {
                bigSB.append(('a' + (i % 26)));
            }
            String bigString = bigSB.toString();
            msg.writeString(bigString);
            msg.reset();
            Assert.assertEquals(bigString, msg.readString());
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testReadBytes() {
        ActiveMQStreamMessage msg = new ActiveMQStreamMessage();
        try {
            byte[] test = new byte[50];
            for (int i = 0; i < (test.length); i++) {
                test[i] = ((byte) (i));
            }
            msg.writeBytes(test);
            msg.reset();
            byte[] valid = new byte[test.length];
            msg.readBytes(valid);
            for (int i = 0; i < (valid.length); i++) {
                Assert.assertTrue(((valid[i]) == (test[i])));
            }
            msg.reset();
            try {
                msg.readByte();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readShort();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readInt();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readLong();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readFloat();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readChar();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readString();
                Assert.fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testReadObject() {
        ActiveMQStreamMessage msg = new ActiveMQStreamMessage();
        try {
            byte testByte = ((byte) (2));
            msg.writeByte(testByte);
            msg.reset();
            Assert.assertTrue(((((Byte) (msg.readObject())).byteValue()) == testByte));
            msg.clearBody();
            short testShort = 3;
            msg.writeShort(testShort);
            msg.reset();
            Assert.assertTrue(((((Short) (msg.readObject())).shortValue()) == testShort));
            msg.clearBody();
            int testInt = 4;
            msg.writeInt(testInt);
            msg.reset();
            Assert.assertTrue(((((Integer) (msg.readObject())).intValue()) == testInt));
            msg.clearBody();
            long testLong = 6L;
            msg.writeLong(testLong);
            msg.reset();
            Assert.assertTrue(((((Long) (msg.readObject())).longValue()) == testLong));
            msg.clearBody();
            float testFloat = 6.6F;
            msg.writeFloat(testFloat);
            msg.reset();
            Assert.assertTrue(((((Float) (msg.readObject())).floatValue()) == testFloat));
            msg.clearBody();
            double testDouble = 7.7;
            msg.writeDouble(testDouble);
            msg.reset();
            Assert.assertTrue(((((Double) (msg.readObject())).doubleValue()) == testDouble));
            msg.clearBody();
            char testChar = 'z';
            msg.writeChar(testChar);
            msg.reset();
            Assert.assertTrue(((((Character) (msg.readObject())).charValue()) == testChar));
            msg.clearBody();
            byte[] data = new byte[50];
            for (int i = 0; i < (data.length); i++) {
                data[i] = ((byte) (i));
            }
            msg.writeBytes(data);
            msg.reset();
            byte[] valid = ((byte[]) (msg.readObject()));
            Assert.assertTrue(((valid.length) == (data.length)));
            for (int i = 0; i < (valid.length); i++) {
                Assert.assertTrue(((valid[i]) == (data[i])));
            }
            msg.clearBody();
            msg.writeBoolean(true);
            msg.reset();
            Assert.assertTrue(((Boolean) (msg.readObject())).booleanValue());
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testClearBody() throws JMSException {
        ActiveMQStreamMessage streamMessage = new ActiveMQStreamMessage();
        try {
            streamMessage.writeObject(new Long(2));
            streamMessage.clearBody();
            Assert.assertFalse(streamMessage.isReadOnlyBody());
            streamMessage.writeObject(new Long(2));
            streamMessage.readObject();
            Assert.fail("should throw exception");
        } catch (MessageNotReadableException mnwe) {
        } catch (MessageNotWriteableException mnwe) {
            Assert.fail("should be writeable");
        }
    }

    @Test
    public void testReset() throws JMSException {
        ActiveMQStreamMessage streamMessage = new ActiveMQStreamMessage();
        try {
            streamMessage.writeDouble(24.5);
            streamMessage.writeLong(311);
        } catch (MessageNotWriteableException mnwe) {
            Assert.fail("should be writeable");
        }
        streamMessage.reset();
        try {
            Assert.assertTrue(streamMessage.isReadOnlyBody());
            Assert.assertEquals(streamMessage.readDouble(), 24.5, 0);
            Assert.assertEquals(streamMessage.readLong(), 311);
        } catch (MessageNotReadableException mnre) {
            Assert.fail("should be readable");
        }
        try {
            streamMessage.writeInt(33);
            Assert.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
    }

    @Test
    public void testReadOnlyBody() throws JMSException {
        ActiveMQStreamMessage message = new ActiveMQStreamMessage();
        try {
            message.writeBoolean(true);
            message.writeByte(((byte) (1)));
            message.writeBytes(new byte[1]);
            message.writeBytes(new byte[3], 0, 2);
            message.writeChar('a');
            message.writeDouble(1.5);
            message.writeFloat(((float) (1.5)));
            message.writeInt(1);
            message.writeLong(1);
            message.writeObject("stringobj");
            message.writeShort(((short) (1)));
            message.writeString("string");
        } catch (MessageNotWriteableException mnwe) {
            Assert.fail("Should be writeable");
        }
        message.reset();
        try {
            message.readBoolean();
            message.readByte();
            Assert.assertEquals(1, message.readBytes(new byte[10]));
            Assert.assertEquals((-1), message.readBytes(new byte[10]));
            Assert.assertEquals(2, message.readBytes(new byte[10]));
            Assert.assertEquals((-1), message.readBytes(new byte[10]));
            message.readChar();
            message.readDouble();
            message.readFloat();
            message.readInt();
            message.readLong();
            message.readString();
            message.readShort();
            message.readString();
        } catch (MessageNotReadableException mnwe) {
            Assert.fail("Should be readable");
        }
        try {
            message.writeBoolean(true);
            Assert.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeByte(((byte) (1)));
            Assert.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeBytes(new byte[1]);
            Assert.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeBytes(new byte[3], 0, 2);
            Assert.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeChar('a');
            Assert.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeDouble(1.5);
            Assert.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeFloat(((float) (1.5)));
            Assert.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeInt(1);
            Assert.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeLong(1);
            Assert.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeObject("stringobj");
            Assert.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeShort(((short) (1)));
            Assert.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeString("string");
            Assert.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
    }

    @Test
    public void testWriteOnlyBody() throws JMSException {
        ActiveMQStreamMessage message = new ActiveMQStreamMessage();
        message.clearBody();
        try {
            message.writeBoolean(true);
            message.writeByte(((byte) (1)));
            message.writeBytes(new byte[1]);
            message.writeBytes(new byte[3], 0, 2);
            message.writeChar('a');
            message.writeDouble(1.5);
            message.writeFloat(((float) (1.5)));
            message.writeInt(1);
            message.writeLong(1);
            message.writeObject("stringobj");
            message.writeShort(((short) (1)));
            message.writeString("string");
        } catch (MessageNotWriteableException mnwe) {
            Assert.fail("Should be writeable");
        }
        try {
            message.readBoolean();
            Assert.fail("Should have thrown exception");
        } catch (MessageNotReadableException mnwe) {
        }
        try {
            message.readByte();
            Assert.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readBytes(new byte[1]);
            Assert.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readBytes(new byte[2]);
            Assert.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readChar();
            Assert.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readDouble();
            Assert.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readFloat();
            Assert.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readInt();
            Assert.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readLong();
            Assert.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readString();
            Assert.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readShort();
            Assert.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readString();
            Assert.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
    }

    @Test
    public void testWriteObject() {
        try {
            ActiveMQStreamMessage message = new ActiveMQStreamMessage();
            message.clearBody();
            message.writeObject("test");
            message.writeObject(new Character('a'));
            message.writeObject(new Boolean(false));
            message.writeObject(new Byte(((byte) (2))));
            message.writeObject(new Short(((short) (2))));
            message.writeObject(new Integer(2));
            message.writeObject(new Long(2L));
            message.writeObject(new Float(2.0F));
            message.writeObject(new Double(2.0));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        try {
            ActiveMQStreamMessage message = new ActiveMQStreamMessage();
            message.clearBody();
            message.writeObject(new Object());
            Assert.fail("should throw an exception");
        } catch (MessageFormatException e) {
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testReadEmptyBufferFromStream() throws JMSException {
        ActiveMQStreamMessage message = new ActiveMQStreamMessage();
        message.clearBody();
        final byte[] BYTE_LIST = new byte[]{ 1, 2, 4 };
        byte[] readList = new byte[(BYTE_LIST.length) - 1];
        byte[] emptyList = new byte[]{  };
        message.writeBytes(emptyList);
        message.reset();
        // First call should return zero as the array written was zero sized.
        Assert.assertEquals(0, message.readBytes(readList));
        // Second call should return -1 as we've reached the end of element.
        Assert.assertEquals((-1), message.readBytes(readList));
    }

    @Test
    public void testReadMixBufferValuesFromStream() throws JMSException {
        ActiveMQStreamMessage message = new ActiveMQStreamMessage();
        message.clearBody();
        final int size = 3;
        final byte[] BYTE_LIST_1 = new byte[]{ 1, 2, 3 };
        final byte[] BYTE_LIST_2 = new byte[]{ 4, 5, 6 };
        final byte[] EMPTY_LIST = new byte[]{  };
        byte[] bigBuffer = new byte[size + size];
        byte[] smallBuffer = new byte[size - 1];
        message.writeBytes(BYTE_LIST_1);
        message.writeBytes(EMPTY_LIST);
        message.writeBytes(BYTE_LIST_2);
        message.writeBytes(EMPTY_LIST);
        message.reset();
        // Read first with big buffer
        Assert.assertEquals(size, message.readBytes(bigBuffer));
        Assert.assertEquals(1, bigBuffer[0]);
        Assert.assertEquals(2, bigBuffer[1]);
        Assert.assertEquals(3, bigBuffer[2]);
        Assert.assertEquals((-1), message.readBytes(bigBuffer));
        // Read the empty buffer, should not be able to read anything else until
        // the bytes read is completed.
        Assert.assertEquals(0, message.readBytes(bigBuffer));
        try {
            message.readBoolean();
        } catch (JMSException ex) {
        }
        Assert.assertEquals((-1), message.readBytes(bigBuffer));
        // Read the third buffer with small buffer, anything that is attempted
        // to be read in between reads or before read completion should throw.
        Assert.assertEquals(smallBuffer.length, message.readBytes(smallBuffer));
        Assert.assertEquals(4, smallBuffer[0]);
        Assert.assertEquals(5, smallBuffer[1]);
        try {
            message.readByte();
        } catch (JMSException ex) {
        }
        Assert.assertEquals(1, message.readBytes(smallBuffer));
        Assert.assertEquals(6, smallBuffer[0]);
        try {
            message.readBoolean();
        } catch (JMSException ex) {
        }
        Assert.assertEquals((-1), message.readBytes(bigBuffer));
        // Read the empty buffer, should not be able to read anything else until
        // the bytes read is completed.
        Assert.assertEquals(0, message.readBytes(bigBuffer));
        try {
            message.readBoolean();
        } catch (JMSException ex) {
        }
        Assert.assertEquals((-1), message.readBytes(bigBuffer));
        // Message should be empty now
        try {
            message.readBoolean();
        } catch (MessageEOFException ex) {
        }
    }
}

