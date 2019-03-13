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


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Unit test for request-reply with jms where processing the input could cause: OK, FAULT or Exception
 */
public class BruceHandlingBeanExceptionTest extends CamelTestSupport {
    @Test
    public void testSendOK() throws Exception {
        Object out = template.requestBody("activemq:queue:ok", "Hello World");
        assertEquals("Bye World", out);
    }

    @Test
    public void testSendFailure() throws Exception {
        Object out = template.requestBody("activemq:queue:fault", "Hello World");
        assertEquals("This is a fault message", out);
    }

    @Test
    public void testSendError() throws Exception {
        Object out = template.requestBody("activemq:queue:error", "Hello World");
        IllegalArgumentException e = assertIsInstanceOf(IllegalArgumentException.class, out);
        assertEquals("Forced exception by unit test", e.getMessage());
    }

    public static class MyExceptionBean {
        public String doSomething(String input) throws Exception {
            throw new IllegalArgumentException("Forced exception by unit test");
        }
    }
}

