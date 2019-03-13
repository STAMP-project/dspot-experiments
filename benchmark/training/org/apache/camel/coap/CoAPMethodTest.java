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
package org.apache.camel.coap;


import CoAPConstants.COAP_METHOD;
import org.junit.Test;


public class CoAPMethodTest extends CoAPTestSupport {
    @Test
    public void testCoAPMethodDefaultGet() {
        // No body means GET
        String result = template.requestBody((("coap://localhost:" + (CoAPTestSupport.PORT)) + "/test/a"), null, String.class);
        assertEquals("GET: /test/a", result);
    }

    @Test
    public void testCoAPMethodDefaultPost() {
        // Providing a body means POST
        String result = template.requestBody((("coap://localhost:" + (CoAPTestSupport.PORT)) + "/test/b"), "Camel", String.class);
        assertEquals("Hello Camel", result);
    }

    @Test
    public void testCoAPMethodHeader() {
        String result = template.requestBodyAndHeader((("coap://localhost:" + (CoAPTestSupport.PORT)) + "/test/c"), null, COAP_METHOD, "DELETE", String.class);
        assertEquals("DELETE: /test/c", result);
    }
}

