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
package org.apache.activemq.transport.amqp.message;


import java.util.HashMap;
import java.util.Map;
import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.amqp.Symbol;
import org.apache.qpid.proton.message.Message;
import org.junit.Assert;
import org.junit.Test;


public class AmqpMessageSupportTest {
    // ---------- getSymbol ---------------------------------------------------//
    @Test
    public void testGetSymbol() {
        Assert.assertNotNull(AmqpMessageSupport.getSymbol("x-opt-something-or-other"));
    }

    // ---------- getMessageAnnotation ----------------------------------------//
    @Test
    public void testGetMessageAnnotationWhenMessageHasAnnotationsMap() {
        Map<Symbol, Object> messageAnnotationsMap = new HashMap<Symbol, Object>();
        messageAnnotationsMap.put(Symbol.valueOf("x-opt-test"), Boolean.TRUE);
        Message message = Proton.message();
        message.setMessageAnnotations(new org.apache.qpid.proton.amqp.messaging.MessageAnnotations(messageAnnotationsMap));
        Assert.assertNotNull(AmqpMessageSupport.getMessageAnnotation("x-opt-test", message));
    }

    @Test
    public void testGetMessageAnnotationWhenMessageHasEmptyAnnotationsMap() {
        Map<Symbol, Object> messageAnnotationsMap = new HashMap<Symbol, Object>();
        Message message = Proton.message();
        message.setMessageAnnotations(new org.apache.qpid.proton.amqp.messaging.MessageAnnotations(messageAnnotationsMap));
        Assert.assertNull(AmqpMessageSupport.getMessageAnnotation("x-opt-test", message));
    }

    @Test
    public void testGetMessageAnnotationWhenMessageHasNoAnnotationsMap() {
        Message message = Proton.message();
        Assert.assertNull(AmqpMessageSupport.getMessageAnnotation("x-opt-test", message));
    }

    @Test
    public void testGetMessageAnnotationWhenMessageIsNull() {
        Assert.assertNull(AmqpMessageSupport.getMessageAnnotation("x-opt-test", null));
    }

    // ---------- isContentType -----------------------------------------------//
    @Test
    public void testIsContentTypeWithNullStringValueAndNullMessageContentType() {
        Message message = Proton.message();
        Assert.assertTrue(AmqpMessageSupport.isContentType(null, message));
    }

    @Test
    public void testIsContentTypeWithNonNullStringValueAndNullMessageContentType() {
        Message message = Proton.message();
        Assert.assertFalse(AmqpMessageSupport.isContentType("test", message));
    }

    @Test
    public void testIsContentTypeWithNonNullStringValueAndNonNullMessageContentTypeNotEqual() {
        Message message = Proton.message();
        message.setContentType("fails");
        Assert.assertFalse(AmqpMessageSupport.isContentType("test", message));
    }

    @Test
    public void testIsContentTypeWithNonNullStringValueAndNonNullMessageContentTypeEqual() {
        Message message = Proton.message();
        message.setContentType("test");
        Assert.assertTrue(AmqpMessageSupport.isContentType("test", message));
    }

    @Test
    public void testIsContentTypeWithNullStringValueAndNonNullMessageContentType() {
        Message message = Proton.message();
        message.setContentType("test");
        Assert.assertFalse(AmqpMessageSupport.isContentType(null, message));
    }
}

