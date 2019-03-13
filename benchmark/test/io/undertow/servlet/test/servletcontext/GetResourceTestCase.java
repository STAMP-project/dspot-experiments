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
package io.undertow.servlet.test.servletcontext;


import StatusCodes.OK;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.FileUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class GetResourceTestCase {
    @Test
    public void testGetResource() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/file?file=/file.txt"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("File Contents", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testGetResourceSpecialCharacterInFileName() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet((((DefaultServer.getDefaultServerURL()) + "/servletContext/file?file=/") + (URLEncoder.encode("1#2.txt", "UTF-8"))));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("Hello!", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testSpecialCharacterInFileURL() throws IOException {
        String tmp = System.getProperty("java.io.tmpdir");
        PathResourceManager pathResourceManager = new PathResourceManager(Paths.get(tmp), 1);
        Path file = Paths.get(tmp, "1#2.txt");
        Files.write(file, "Hi".getBytes());
        Resource res = pathResourceManager.getResource("1#2.txt");
        try (InputStream in = res.getUrl().openStream()) {
            Assert.assertEquals("Hi", FileUtils.readFile(in));
        }
    }
}

