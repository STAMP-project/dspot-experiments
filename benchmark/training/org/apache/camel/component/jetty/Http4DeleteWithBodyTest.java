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
package org.apache.camel.component.jetty;


import Exchange.HTTP_METHOD;
import org.junit.Test;


public class Http4DeleteWithBodyTest extends BaseJettyTest {
    @Test
    public void testHttp4DeleteWithBodyFalseTest() throws Exception {
        byte[] data = "World".getBytes();
        String out = template.requestBodyAndHeader("http4://localhost:{{port}}/test", data, HTTP_METHOD, "DELETE", String.class);
        assertEquals("Bye ", out);
    }

    @Test
    public void testHttp4DeleteWithBodyTrueTest() throws Exception {
        byte[] data = "World".getBytes();
        String out = template.requestBodyAndHeader("http4://localhost:{{port}}/test?deleteWithBody=true", data, HTTP_METHOD, "DELETE", String.class);
        assertEquals("Bye World", out);
    }
}

