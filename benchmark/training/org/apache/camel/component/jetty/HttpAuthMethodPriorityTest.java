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


import org.apache.camel.FailedToCreateProducerException;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class HttpAuthMethodPriorityTest extends BaseJettyTest {
    @Test
    public void testAuthMethodPriorityBasicDigest() throws Exception {
        String out = template.requestBody("http://localhost:{{port}}/test?authMethod=Basic&authMethodPriority=Basic,Digest&authUsername=donald&authPassword=duck", "Hello World", String.class);
        assertEquals("Bye World", out);
    }

    @Test
    public void testAuthMethodPriorityNTLMBasic() throws Exception {
        String out = template.requestBody("http://localhost:{{port}}/test?authMethod=Basic&authMethodPriority=NTLM,Basic&authUsername=donald&authPassword=duck", "Hello World", String.class);
        assertEquals("Bye World", out);
    }

    @Test
    public void testAuthMethodPriorityInvalid() throws Exception {
        try {
            template.requestBody("http://localhost:{{port}}/test?authMethod=Basic&authMethodPriority=Basic,foo&authUsername=donald&authPassword=duck", "Hello World", String.class);
            fail("Should have thrown an exception");
        } catch (FailedToCreateProducerException e) {
            IllegalArgumentException cause = assertIsInstanceOf(IllegalArgumentException.class, e.getCause().getCause().getCause());
            // JAXB 2.2 uses a slightly different message
            boolean b = (cause.getMessage().contains("No enum const")) && (cause.getMessage().contains("org.apache.camel.component.http4.AuthMethod.foo"));
            assertTrue(("Bad fault message: " + (cause.getMessage())), b);
        }
    }

    @Test
    public void testAuthMethodPriorityNTLM() throws Exception {
        try {
            template.requestBody("http://localhost:{{port}}/test?authMethod=Basic&authMethodPriority=NTLM&authUsername=donald&authPassword=duck", "Hello World", String.class);
            fail("Should have thrown exception");
        } catch (RuntimeCamelException e) {
            HttpOperationFailedException cause = assertIsInstanceOf(HttpOperationFailedException.class, e.getCause());
            assertEquals(401, cause.getStatusCode());
        }
    }
}

