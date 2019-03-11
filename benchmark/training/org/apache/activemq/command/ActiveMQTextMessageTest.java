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


import CommandTypes.ACTIVEMQ_TEXT_MESSAGE;
import java.beans.Transient;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.jms.JMSException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;
import junit.framework.TestCase;
import org.apache.activemq.util.ByteSequence;


/**
 *
 */
public class ActiveMQTextMessageTest extends TestCase {
    public void testGetDataStructureType() {
        ActiveMQTextMessage msg = new ActiveMQTextMessage();
        TestCase.assertEquals(msg.getDataStructureType(), ACTIVEMQ_TEXT_MESSAGE);
    }

    public void testShallowCopy() throws JMSException {
        ActiveMQTextMessage msg = new ActiveMQTextMessage();
        String string = "str";
        msg.setText(string);
        Message copy = msg.copy();
        TestCase.assertTrue(((msg.getText()) == (getText())));
    }

    public void testSetText() {
        ActiveMQTextMessage msg = new ActiveMQTextMessage();
        String str = "testText";
        try {
            msg.setText(str);
            TestCase.assertEquals(msg.getText(), str);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void testGetBytes() throws IOException, JMSException {
        ActiveMQTextMessage msg = new ActiveMQTextMessage();
        String str = "testText";
        msg.setText(str);
        msg.beforeMarshall(null);
        ByteSequence bytes = msg.getContent();
        msg = new ActiveMQTextMessage();
        msg.setContent(bytes);
        TestCase.assertEquals(msg.getText(), str);
    }

    public void testClearBody() throws IOException, JMSException {
        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText("string");
        textMessage.clearBody();
        TestCase.assertFalse(textMessage.isReadOnlyBody());
        TestCase.assertNull(textMessage.getText());
        try {
            textMessage.setText("String");
            textMessage.getText();
        } catch (MessageNotWriteableException mnwe) {
            TestCase.fail("should be writeable");
        } catch (MessageNotReadableException mnre) {
            TestCase.fail("should be readable");
        }
    }

    public void testReadOnlyBody() throws JMSException {
        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText("test");
        textMessage.setReadOnlyBody(true);
        try {
            textMessage.getText();
        } catch (MessageNotReadableException e) {
            TestCase.fail("should be readable");
        }
        try {
            textMessage.setText("test");
            TestCase.fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
    }

    public void testWriteOnlyBody() throws JMSException {
        // should always be readable
        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setReadOnlyBody(false);
        try {
            textMessage.setText("test");
            textMessage.getText();
        } catch (MessageNotReadableException e) {
            TestCase.fail("should be readable");
        }
        textMessage.setReadOnlyBody(true);
        try {
            textMessage.getText();
            textMessage.setText("test");
            TestCase.fail("should throw exception");
        } catch (MessageNotReadableException e) {
            TestCase.fail("should be readable");
        } catch (MessageNotWriteableException mnwe) {
        }
    }

    public void testShortText() throws Exception {
        String shortText = "Content";
        ActiveMQTextMessage shortMessage = new ActiveMQTextMessage();
        setContent(shortMessage, shortText);
        TestCase.assertTrue(shortMessage.toString().contains(("text = " + shortText)));
        TestCase.assertTrue(shortMessage.getText().equals(shortText));
        String longText = "Very very very very veeeeeeery loooooooooooooooooooooooooooooooooong text";
        String longExpectedText = "Very very very very veeeeeeery looooooooooooo...ooooong text";
        ActiveMQTextMessage longMessage = new ActiveMQTextMessage();
        setContent(longMessage, longText);
        TestCase.assertTrue(longMessage.toString().contains(("text = " + longExpectedText)));
        TestCase.assertTrue(longMessage.getText().equals(longText));
    }

    public void testNullText() throws Exception {
        ActiveMQTextMessage nullMessage = new ActiveMQTextMessage();
        setContent(nullMessage, null);
        TestCase.assertTrue(nullMessage.toString().contains("text = null"));
    }

    public void testTransient() throws Exception {
        Method method = ActiveMQTextMessage.class.getMethod("getRegionDestination", null);
        TestCase.assertTrue(method.isAnnotationPresent(Transient.class));
    }
}

