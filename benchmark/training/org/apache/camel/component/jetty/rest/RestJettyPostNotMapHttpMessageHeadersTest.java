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
package org.apache.camel.component.jetty.rest;


import Exchange.CONTENT_TYPE;
import Exchange.HTTP_METHOD;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.jetty.BaseJettyTest;
import org.junit.Test;


public class RestJettyPostNotMapHttpMessageHeadersTest extends BaseJettyTest {
    @Test
    public void testPostNotMapHttpMessageHeadersTest() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        headers.put(HTTP_METHOD, "POST");
        headers.put(CONTENT_TYPE, "application/x-www-form-urlencoded");
        String out = template.requestBodyAndHeaders((("http://localhost:" + (BaseJettyTest.getPort())) + "/rest/test"), "{\"msg\": \"TEST\"}", headers, String.class);
        assertEquals("\"OK\"", out);
    }
}

