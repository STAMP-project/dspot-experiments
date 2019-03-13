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
package org.apache.camel.component.jms.issues;


import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Unit test from an user request on the forum.
 */
public class JmsInOutPipelineWithBeanTest extends CamelTestSupport {
    @Test
    public void testA() throws Exception {
        Object response = template.requestBody("activemq:testA", "Hello World");
        assertEquals("Reply", "Hello World,From Bean,From A,From B", response);
    }

    @Test
    public void testB() throws Exception {
        Object response = template.requestBody("activemq:testB", "Hello World");
        assertEquals("Reply", "Hello World,From A,From Bean,From B", response);
    }

    @Test
    public void testC() throws Exception {
        Object response = template.requestBody("activemq:testC", "Hello World");
        assertEquals("Reply", "Hello World,From A,From B,From Bean", response);
    }

    public static class MyDummyBean {
        public void doSomething(Exchange exchange) {
            String body = exchange.getIn().getBody(String.class);
            exchange.getOut().setBody((body + ",From Bean"));
        }
    }
}

