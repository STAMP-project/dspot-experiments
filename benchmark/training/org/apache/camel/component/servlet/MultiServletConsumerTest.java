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
package org.apache.camel.component.servlet;


import com.meterware.httpunit.HttpNotFoundException;
import org.junit.Test;


public class MultiServletConsumerTest extends ServletCamelRouterTestSupport {
    @Test
    public void testMultiServletsConsumers() throws Exception {
        String result = getService("/services1/hello?name=Camel");
        assertEquals("Hello Camel", result);
        result = getService("/services2/echo?name=Camel");
        assertEquals("Camel Camel", result);
    }

    @Test
    public void testMultiServletsConsumersCannotAccessEachOther() throws Exception {
        try {
            getService("/services2/hello?name=Camel");
            fail("Should have thrown an exception");
        } catch (HttpNotFoundException e) {
            assertEquals(404, e.getResponseCode());
        }
        try {
            getService("/services1/echo?name=Camel");
            fail("Should have thrown an exception");
        } catch (HttpNotFoundException e) {
            assertEquals(404, e.getResponseCode());
        }
    }
}

