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


import org.apache.camel.util.IOHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;


public class MultiPartFormTest extends BaseJettyTest {
    @Test
    public void testSendMultiPartForm() throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost((("http://localhost:" + (BaseJettyTest.getPort())) + "/test"));
        post.setEntity(createMultipartRequestEntity());
        HttpResponse response = client.execute(post);
        int status = response.getStatusLine().getStatusCode();
        assertEquals("Get a wrong response status", 200, status);
        String result = IOHelper.loadText(response.getEntity().getContent()).trim();
        assertEquals("Get a wrong result", "A binary file of some kind", result);
    }

    @Test
    public void testSendMultiPartFormFromCamelHttpComponnent() throws Exception {
        String result = template.requestBody((("http://localhost:" + (BaseJettyTest.getPort())) + "/test"), createMultipartRequestEntity(), String.class);
        assertEquals("Get a wrong result", "A binary file of some kind", result);
    }
}

