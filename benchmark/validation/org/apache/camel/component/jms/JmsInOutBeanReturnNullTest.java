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
package org.apache.camel.component.jms;


import java.io.Serializable;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 *
 */
public class JmsInOutBeanReturnNullTest extends CamelTestSupport {
    @Test
    public void testReturnBean() throws Exception {
        JmsInOutBeanReturnNullTest.MyBean out = template.requestBody("activemq:queue:foo", "Camel", JmsInOutBeanReturnNullTest.MyBean.class);
        assertNotNull(out);
        assertEquals("Camel", out.getName());
    }

    @Test
    public void testReturnNull() throws Exception {
        Object out = template.requestBody("activemq:queue:foo", "foo");
        assertNull(out);
    }

    @Test
    public void testReturnNullMyBean() throws Exception {
        JmsInOutBeanReturnNullTest.MyBean out = template.requestBody("activemq:queue:foo", "foo", JmsInOutBeanReturnNullTest.MyBean.class);
        assertNull(out);
    }

    @Test
    public void testReturnNullExchange() throws Exception {
        Exchange reply = template.request("activemq:queue:foo", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("foo");
            }
        });
        assertNotNull(reply);
        assertTrue(reply.hasOut());
        Message out = reply.getOut();
        assertNotNull(out);
        Object body = out.getBody();
        assertNull("Should be a null body", body);
    }

    public static final class MyBean implements Serializable {
        private static final long serialVersionUID = 1L;

        public String name;

        public MyBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

