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
package org.apache.activemq.bugs;


import org.apache.activemq.command.ActiveMQObjectMessage;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ4893Test {
    private static final transient Logger LOG = LoggerFactory.getLogger(AMQ4893Test.class);

    @Test
    public void testPropertiesInt() throws Exception {
        ActiveMQObjectMessage message = new ActiveMQObjectMessage();
        message.setIntProperty("TestProp", 333);
        fakeUnmarshal(message);
        roundTripProperties(message);
    }

    @Test
    public void testPropertiesString() throws Exception {
        ActiveMQObjectMessage message = new ActiveMQObjectMessage();
        message.setStringProperty("TestProp", "Value");
        fakeUnmarshal(message);
        roundTripProperties(message);
    }

    @Test
    public void testPropertiesObject() throws Exception {
        ActiveMQObjectMessage message = new ActiveMQObjectMessage();
        message.setObjectProperty("TestProp", "Value");
        fakeUnmarshal(message);
        roundTripProperties(message);
    }

    @Test
    public void testPropertiesObjectNoMarshalling() throws Exception {
        ActiveMQObjectMessage message = new ActiveMQObjectMessage();
        message.setObjectProperty("TestProp", "Value");
        roundTripProperties(message);
    }
}

