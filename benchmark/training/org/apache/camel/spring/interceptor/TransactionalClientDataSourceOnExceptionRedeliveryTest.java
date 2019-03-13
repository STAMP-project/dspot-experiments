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
package org.apache.camel.spring.interceptor;


import Exchange.ERRORHANDLER_HANDLED;
import Exchange.FAILURE_HANDLED;
import Exchange.REDELIVERED;
import Exchange.REDELIVERY_COUNTER;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RuntimeCamelException;
import org.junit.Test;


public class TransactionalClientDataSourceOnExceptionRedeliveryTest extends TransactionalClientDataSourceTest {
    @Test
    public void testTransactionRollbackWithExchange() throws Exception {
        Exchange out = template.send("direct:fail", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Hello World");
            }
        });
        int count = jdbc.queryForObject("select count(*) from books", Integer.class);
        assertEquals("Number of books", 1, count);
        assertNotNull(out);
        Exception e = out.getException();
        assertIsInstanceOf(RuntimeCamelException.class, e);
        assertTrue(((e.getCause()) instanceof IllegalArgumentException));
        assertEquals("We don't have Donkeys, only Camels", e.getCause().getMessage());
        assertEquals(true, out.getIn().getHeader(REDELIVERED));
        assertEquals(3, out.getIn().getHeader(REDELIVERY_COUNTER));
        assertEquals(true, out.getProperty(FAILURE_HANDLED));
        assertEquals(false, out.getProperty(ERRORHANDLER_HANDLED));
    }
}

