/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.servlet.test.security.basic;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.FlexBase64;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Tomas Hofman
 */
@RunWith(DefaultServer.class)
public class ServletCertAndDigestAuthTestCase {
    private static final String REALM_NAME = "Servlet_Realm";

    private static final String BASE_PATH = "/servletContext/secured/";

    private static SSLContext clientSSLContext;

    @Test
    public void testMultipartRequest() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            sb.append("0123456789");
        }
        try (TestHttpClient client = new TestHttpClient()) {
            // create POST request
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("part1", new ByteArrayBody(sb.toString().getBytes(), "file.txt"));
            builder.addPart("part2", new org.apache.http.entity.mime.content.StringBody("0123456789", ContentType.TEXT_HTML));
            HttpEntity entity = builder.build();
            client.setSSLContext(ServletCertAndDigestAuthTestCase.clientSSLContext);
            String url = ((DefaultServer.getDefaultServerSSLAddress()) + (ServletCertAndDigestAuthTestCase.BASE_PATH)) + "multipart";
            HttpPost post = new HttpPost(url);
            post.setEntity(entity);
            post.addHeader(AUTHORIZATION.toString(), (((BASIC) + " ") + (FlexBase64.encodeString(("user1" + (":" + "password1")).getBytes(StandardCharsets.UTF_8), false))));
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        }
    }
}

