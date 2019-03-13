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


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test to make sure message.isMarshalled() returns the correct value
 */
@RunWith(Parameterized.class)
public class ActiveMQMessageIsMarshalledTest {
    protected enum MessageType {

        BYTES,
        MAP,
        TEXT,
        OBJECT,
        STREAM,
        MESSAGE;}

    private final ActiveMQMessageIsMarshalledTest.MessageType messageType;

    public ActiveMQMessageIsMarshalledTest(final ActiveMQMessageIsMarshalledTest.MessageType messageType) {
        super();
        this.messageType = messageType;
    }

    @Test
    public void testIsMarshalledWithBodyAndProperties() throws Exception {
        ActiveMQMessage message = getMessage(true, true);
        assertIsMarshalled(message, true, true);
    }

    @Test
    public void testIsMarshalledWithPropertyEmptyBody() throws Exception {
        ActiveMQMessage message = getMessage(false, true);
        assertIsMarshalled(message, false, true);
    }

    @Test
    public void testIsMarshalledWithBodyEmptyProperties() throws Exception {
        ActiveMQMessage message = getMessage(true, false);
        assertIsMarshalled(message, true, false);
    }

    @Test
    public void testIsMarshalledWithEmptyBodyEmptyProperties() throws Exception {
        ActiveMQMessage message = getMessage(false, false);
        // No body or properties so the message should be considered marshalled already
        Assert.assertTrue(message.isMarshalled());
    }
}

