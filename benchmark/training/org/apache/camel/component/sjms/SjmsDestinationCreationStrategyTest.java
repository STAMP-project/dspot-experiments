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
package org.apache.camel.component.sjms;


import ExchangePattern.InOut;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.camel.component.sjms.jms.DefaultDestinationCreationStrategy;
import org.apache.camel.component.sjms.support.JmsTestSupport;
import org.junit.Test;


public class SjmsDestinationCreationStrategyTest extends JmsTestSupport {
    private boolean createDestinationCalled;

    private boolean createTemporaryDestination;

    @Test
    public void testSjmsComponentUsesCustomDestinationCreationStrategy() throws Exception {
        assertFalse(createDestinationCalled);
        template.sendBody("sjms:queue:inonly?prefillPool=false", "hello world");
        assertTrue(createDestinationCalled);
        assertFalse(createTemporaryDestination);
        String response = ((String) (template.sendBody("sjms:queue:inout?prefillPool=false&exchangePattern=InOut", InOut, "hello world 2")));
        assertTrue(createTemporaryDestination);
        assertEquals("response", response);
    }

    class TestDestinationCreationStrategyTest extends DefaultDestinationCreationStrategy {
        @Override
        public Destination createDestination(Session session, String name, boolean topic) throws JMSException {
            if (name.equals("inonly")) {
                createDestinationCalled = true;
            }
            return super.createDestination(session, name, topic);
        }

        @Override
        public Destination createTemporaryDestination(Session session, boolean topic) throws JMSException {
            createTemporaryDestination = true;
            return super.createTemporaryDestination(session, topic);
        }
    }
}

