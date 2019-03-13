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


import CommandTypes.ACTIVEMQ_BYTES_MESSAGE;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;
import junit.framework.TestCase;


/**
 *
 */
public class ActiveMQBytesMessageTest extends TestCase {
    public ActiveMQBytesMessageTest(String name) {
        super(name);
    }

    public void testGetDataStructureType() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        TestCase.assertEquals(msg.getDataStructureType(), ACTIVEMQ_BYTES_MESSAGE);
    }

    public void testGetBodyLength() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        int len = 10;
        try {
            for (int i = 0; i < len; i++) {
                msg.writeLong(5L);
            }
        } catch (JMSException ex) {
            ex.printStackTrace();
        }
        try {
            msg.reset();
            TestCase.assertTrue(((msg.getBodyLength()) == (len * 8)));
        } catch (Throwable e) {
            e.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    public void testReadBoolean() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        try {
            msg.writeBoolean(true);
            msg.reset();
            TestCase.assertTrue(msg.readBoolean());
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    public void testReadByte() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        try {
            msg.writeByte(((byte) (2)));
            msg.reset();
            TestCase.assertTrue(((msg.readByte()) == 2));
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    public void testReadUnsignedByte() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        try {
            msg.writeByte(((byte) (2)));
            msg.reset();
            TestCase.assertTrue(((msg.readUnsignedByte()) == 2));
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    public void testReadShort() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        try {
            msg.writeShort(((short) (3000)));
            msg.reset();
            TestCase.assertTrue(((msg.readShort()) == 3000));
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    public void testReadUnsignedShort() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        try {
            msg.writeShort(((short) (3000)));
            msg.reset();
            TestCase.assertTrue(((msg.readUnsignedShort()) == 3000));
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    public void testReadChar() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        try {
            msg.writeChar('a');
            msg.reset();
            TestCase.assertTrue(((msg.readChar()) == 'a'));
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    public void testReadInt() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        try {
            msg.writeInt(3000);
            msg.reset();
            TestCase.assertTrue(((msg.readInt()) == 3000));
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    public void testReadLong() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        try {
            msg.writeLong(3000);
            msg.reset();
            TestCase.assertTrue(((msg.readLong()) == 3000));
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    public void testReadFloat() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        try {
            msg.writeFloat(3.3F);
            msg.reset();
            TestCase.assertTrue(((msg.readFloat()) == 3.3F));
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    public void testReadDouble() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        try {
            msg.writeDouble(3.3);
            msg.reset();
            TestCase.assertTrue(((msg.readDouble()) == 3.3));
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    public void testReadUTF() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        try {
            String str = "this is a test";
            msg.writeUTF(str);
            msg.reset();
            TestCase.assertTrue(msg.readUTF().equals(str));
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    /* Class to test for int readBytes(byte[]) */
    public void testReadBytesbyteArray() {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        try {
            byte[] data = new byte[50];
            for (int i = 0; i < (data.length); i++) {
                data[i] = ((byte) (i));
            }
            msg.writeBytes(data);
            msg.reset();
            byte[] test = new byte[data.length];
            msg.readBytes(test);
            for (int i = 0; i < (test.length); i++) {
                TestCase.assertTrue(((test[i]) == i));
            }
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    public void testWriteObject() throws JMSException {
        ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        try {
            msg.writeObject("fred");
            msg.writeObject(Boolean.TRUE);
            msg.writeObject(Character.valueOf('q'));
            msg.writeObject(Byte.valueOf(((byte) (1))));
            msg.writeObject(Short.valueOf(((short) (3))));
            msg.writeObject(Integer.valueOf(3));
            msg.writeObject(Long.valueOf(300L));
            msg.writeObject(new Float(3.3F));
            msg.writeObject(new Double(3.3));
            msg.writeObject(new byte[3]);
        } catch (MessageFormatException mfe) {
            TestCase.fail("objectified primitives should be allowed");
        }
        try {
            msg.writeObject(new Object());
            TestCase.fail("only objectified primitives are allowed");
        } catch (MessageFormatException mfe) {
        }
    }

    /* new */
    public void testClearBody() throws JMSException {
        ActiveMQBytesMessage bytesMessage = new ActiveMQBytesMessage();
        try {
            bytesMessage.writeInt(1);
            bytesMessage.clearBody();
            TestCase.assertFalse(bytesMessage.isReadOnlyBody());
            bytesMessage.writeInt(1);
            bytesMessage.readInt();
        } catch (MessageNotReadableException mnwe) {
        } catch (MessageNotWriteableException mnwe) {
            TestCase.assertTrue(false);
        }
    }

    public void testReset() throws JMSException {
        ActiveMQBytesMessage message = new ActiveMQBytesMessage();
        try {
            message.writeDouble(24.5);
            message.writeLong(311);
        } catch (MessageNotWriteableException mnwe) {
            TestCase.fail("should be writeable");
        }
        message.reset();
        try {
            TestCase.assertTrue(message.isReadOnlyBody());
            TestCase.assertEquals(message.readDouble(), 24.5, 0);
            TestCase.assertEquals(message.readLong(), 311);
        } catch (MessageNotReadableException mnre) {
            TestCase.fail("should be readable");
        }
        try {
            message.writeInt(33);
            TestCase.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
    }

    public void testReadOnlyBody() throws JMSException {
        ActiveMQBytesMessage message = new ActiveMQBytesMessage();
        try {
            message.writeBoolean(true);
            message.writeByte(((byte) (1)));
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
            message.writeShort(((short) (1)));
            message.writeUTF("utfstring");
        } catch (MessageNotWriteableException mnwe) {
            TestCase.fail("Should be writeable");
        }
        message.reset();
        try {
            message.readBoolean();
            message.readByte();
            message.readUnsignedByte();
            message.readBytes(new byte[1]);
            message.readBytes(new byte[2], 2);
            message.readChar();
            message.readDouble();
            message.readFloat();
            message.readInt();
            message.readLong();
            message.readUTF();
            message.readShort();
            message.readUnsignedShort();
            message.readUTF();
        } catch (MessageNotReadableException mnwe) {
            TestCase.fail("Should be readable");
        }
        try {
            message.writeBoolean(true);
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeByte(((byte) (1)));
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeBytes(new byte[1]);
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeBytes(new byte[3], 0, 2);
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeChar('a');
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeDouble(1.5);
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeFloat(((float) (1.5)));
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeInt(1);
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeLong(1);
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeObject("stringobj");
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeShort(((short) (1)));
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeUTF("utfstring");
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
    }

    public void testWriteOnlyBody() throws JMSException {
        ActiveMQBytesMessage message = new ActiveMQBytesMessage();
        message.clearBody();
        try {
            message.writeBoolean(true);
            message.writeByte(((byte) (1)));
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
            message.writeShort(((short) (1)));
            message.writeUTF("utfstring");
        } catch (MessageNotWriteableException mnwe) {
            TestCase.fail("Should be writeable");
        }
        try {
            message.readBoolean();
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException mnwe) {
        }
        try {
            message.readByte();
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readUnsignedByte();
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readBytes(new byte[1]);
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readBytes(new byte[2], 2);
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readChar();
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readDouble();
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readFloat();
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readInt();
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readLong();
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readUTF();
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readShort();
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readUnsignedShort();
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readUTF();
            TestCase.fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
    }
}

