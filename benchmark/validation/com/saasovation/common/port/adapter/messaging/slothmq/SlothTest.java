/**
 * Copyright 2012,2013 Vaughn Vernon
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.saasovation.common.port.adapter.messaging.slothmq;


import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;


public class SlothTest extends TestCase {
    private ExchangePublisher publisher;

    private SlothTest.TestExchangeListener testExchangeListener;

    public SlothTest() {
        super();
    }

    public void testPublishSubscribe() throws Exception {
        this.publisher.publish("my.test.type", "A tiny little message.");
        this.publisher.publish("my.test.type1", "A slightly bigger message.");
        this.publisher.publish("my.test.type2", "An even bigger message, still.");
        this.publisher.publish("my.test.type3", "An even bigger (bigger!) message, still.");
        Thread.sleep(1000L);
        TestCase.assertEquals("my.test.type", testExchangeListener.receivedType());
        TestCase.assertEquals("A tiny little message.", testExchangeListener.receivedMessage());
        TestCase.assertEquals(4, SlothTest.TestExchangeListenerAgain.uniqueMessages().size());
    }

    private static class TestExchangeListener extends ExchangeListener {
        private String receivedMessage;

        private String receivedType;

        TestExchangeListener() {
            super();
        }

        public String receivedMessage() {
            return this.receivedMessage;
        }

        public String receivedType() {
            return this.receivedType;
        }

        @Override
        protected String exchangeName() {
            return "TestExchange";
        }

        @Override
        protected void filteredDispatch(String aType, String aTextMessage) {
            this.receivedType = aType;
            this.receivedMessage = aTextMessage;
        }

        @Override
        protected String[] listensTo() {
            return new String[]{ "my.test.type" };
        }

        @Override
        protected String name() {
            return this.getClass().getName();
        }
    }

    private static class TestExchangeListenerAgain extends ExchangeListener {
        private static int idCount = 0;

        private static Set<String> uniqueMessages = new HashSet<String>();

        private int id;

        public static Set<String> uniqueMessages() {
            return SlothTest.TestExchangeListenerAgain.uniqueMessages;
        }

        TestExchangeListenerAgain() {
            super();
            this.id = ++(SlothTest.TestExchangeListenerAgain.idCount);
        }

        @Override
        protected String exchangeName() {
            return "TestExchange";
        }

        @Override
        protected void filteredDispatch(String aType, String aTextMessage) {
            synchronized(SlothTest.TestExchangeListenerAgain.uniqueMessages) {
                SlothTest.TestExchangeListenerAgain.uniqueMessages.add(((aType + ":") + aTextMessage));
            }
        }

        @Override
        protected String[] listensTo() {
            return null;// all

        }

        @Override
        protected String name() {
            return ((this.getClass().getName()) + "#") + (this.id);
        }
    }
}

