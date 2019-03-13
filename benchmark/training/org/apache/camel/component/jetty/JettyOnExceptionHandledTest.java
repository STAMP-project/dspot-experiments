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


import Exchange.HTTP_RESPONSE_CODE;
import org.apache.camel.Exchange;
import org.junit.Test;


public class JettyOnExceptionHandledTest extends BaseJettyTest {
    @Test
    public void testJettyOnException() throws Exception {
        Exchange reply = template.request("http://localhost:{{port}}/myserver?throwExceptionOnFailure=false", null);
        assertNotNull(reply);
        assertEquals("Dude something went wrong", reply.getOut().getBody(String.class));
        assertEquals(500, reply.getOut().getHeader(HTTP_RESPONSE_CODE));
    }
}

