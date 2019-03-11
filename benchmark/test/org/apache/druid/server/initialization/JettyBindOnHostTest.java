/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server.initialization;


import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;


public class JettyBindOnHostTest extends BaseJettyTest {
    @Test
    public void testBindOnHost() throws Exception {
        Assert.assertEquals("localhost", server.getURI().getHost());
        final URL url = new URL((("http://localhost:" + (port)) + "/default"));
        final HttpURLConnection get = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(BaseJettyTest.DEFAULT_RESPONSE_CONTENT, IOUtils.toString(get.getInputStream(), StandardCharsets.UTF_8));
        final HttpURLConnection post = ((HttpURLConnection) (url.openConnection()));
        post.setRequestMethod("POST");
        Assert.assertEquals(BaseJettyTest.DEFAULT_RESPONSE_CONTENT, IOUtils.toString(post.getInputStream(), StandardCharsets.UTF_8));
    }
}

