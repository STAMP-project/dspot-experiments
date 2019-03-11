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
package io.undertow.server.handlers.file;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class ContentEncodedResourceTestCase {
    public static final String DIR_NAME = "contentEncodingTestCase";

    static Path tmpDir;

    @Test
    public void testFileIsCompressed() throws IOException, InterruptedException {
        String fileName = "hello.html";
        Path f = ContentEncodedResourceTestCase.tmpDir.resolve(fileName);
        Files.write(f, "hello world".getBytes());
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            for (int i = 0; i < 3; ++i) {
                HttpGet get = new HttpGet((((DefaultServer.getDefaultServerURL()) + "/") + fileName));
                CloseableHttpResponse result = client.execute(get);
                Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
                String response = HttpClientUtils.readResponse(result);
                Assert.assertEquals("hello world", response);
                assert (result.getEntity()) instanceof org.apache.http.client.entity.DecompressingEntity;// no other nice way to be sure we get back gzipped content

                result.close();
            }
            Files.write(f, "modified file".getBytes());
            // if it is serving a cached compressed version what is being served will not change
            HttpGet get = new HttpGet((((DefaultServer.getDefaultServerURL()) + "/") + fileName));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("hello world", response);
            assert (result.getEntity()) instanceof org.apache.http.client.entity.DecompressingEntity;// no other nice way to be sure we get back gzipped content

        }
    }
}

