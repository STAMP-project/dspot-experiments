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


import org.apache.camel.RuntimeCamelException;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class JmsTransferExceptionTest extends CamelTestSupport {
    private static int counter;

    @Test
    public void testOk() throws Exception {
        Object out = template.requestBody(getUri(), "Hello World");
        assertEquals("Bye World", out);
        assertEquals(1, JmsTransferExceptionTest.counter);
    }

    @Test
    public void testTransferExeption() throws Exception {
        // we send something that causes a remote exception
        // then we expect our producer template to thrown
        // an exception with the remote exception as cause
        try {
            template.requestBody(getUri(), "Kabom");
            fail("Should have thrown an exception");
        } catch (RuntimeCamelException e) {
            assertEquals("Boom", e.getCause().getMessage());
            assertNotNull("Should contain a remote stacktrace", e.getCause().getStackTrace());
        }
        // we still try redeliver
        assertEquals(3, JmsTransferExceptionTest.counter);
    }
}

