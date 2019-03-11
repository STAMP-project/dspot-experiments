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
package io.undertow.servlet.test.multipart;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.ProxyIgnore;
import io.undertow.testutils.TestHttpClient;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
@ProxyIgnore
public class MultiPartTestCase {
    @Test
    public void testMultiPartRequestWithNoMultipartConfig() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            String uri = (DefaultServer.getDefaultServerURL()) + "/servletContext/0";
            HttpPost post = new HttpPost(uri);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("formValue", new StringBody("myValue", "text/plain", StandardCharsets.UTF_8));
            entity.addPart("file", new FileBody(new File(MultiPartTestCase.class.getResource("uploadfile.txt").getFile())));
            post.setEntity(entity);
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("EXCEPTION: class java.lang.IllegalStateException", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testMultiPartRequest() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            String uri = (DefaultServer.getDefaultServerURL()) + "/servletContext/1";
            HttpPost post = new HttpPost(uri);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, StandardCharsets.UTF_8);
            entity.addPart("formValue", new StringBody("myValue", "text/plain", StandardCharsets.UTF_8));
            entity.addPart("file", new FileBody(new File(MultiPartTestCase.class.getResource("uploadfile.txt").getFile())));
            post.setEntity(entity);
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(("PARAMS:\n" + (((((((((((("name: formValue\n" + "filename: null\n") + "content-type: null\n") + "Content-Disposition: form-data; name=\"formValue\"\n") + "size: 7\n") + "content: myValue\n") + "name: file\n") + "filename: uploadfile.txt\n") + "content-type: application/octet-stream\n") + "Content-Disposition: form-data; name=\"file\"; filename=\"uploadfile.txt\"\n") + "Content-Type: application/octet-stream\n") + "size: 13\n") + "content: file contents\n")), response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testMultiPartRequestWithAddedServlet() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            String uri = (DefaultServer.getDefaultServerURL()) + "/servletContext/added";
            HttpPost post = new HttpPost(uri);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("formValue", new StringBody("myValue", "text/plain", StandardCharsets.UTF_8));
            entity.addPart("file", new FileBody(new File(MultiPartTestCase.class.getResource("uploadfile.txt").getFile())));
            post.setEntity(entity);
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(("PARAMS:\n" + (((((((((((("name: formValue\n" + "filename: null\n") + "content-type: null\n") + "Content-Disposition: form-data; name=\"formValue\"\n") + "size: 7\n") + "content: myValue\n") + "name: file\n") + "filename: uploadfile.txt\n") + "content-type: application/octet-stream\n") + "Content-Disposition: form-data; name=\"file\"; filename=\"uploadfile.txt\"\n") + "Content-Type: application/octet-stream\n") + "size: 13\n") + "content: file contents\n")), response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testMultiPartRequestToLarge() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            String uri = (DefaultServer.getDefaultServerURL()) + "/servletContext/2";
            HttpPost post = new HttpPost(uri);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("formValue", new StringBody("myValue", "text/plain", StandardCharsets.UTF_8));
            entity.addPart("file", new FileBody(new File(MultiPartTestCase.class.getResource("uploadfile.txt").getFile())));
            post.setEntity(entity);
            HttpResponse result = client.execute(post);
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("EXCEPTION: class java.lang.IllegalStateException", response);
        } catch (IOException expected) {
            // in some environments the forced close of the read side will cause a connection reset
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testMultiPartIndividualFileToLarge() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            String uri = (DefaultServer.getDefaultServerURL()) + "/servletContext/3";
            HttpPost post = new HttpPost(uri);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("formValue", new StringBody("myValue", "text/plain", StandardCharsets.UTF_8));
            entity.addPart("file", new FileBody(new File(MultiPartTestCase.class.getResource("uploadfile.txt").getFile())));
            post.setEntity(entity);
            HttpResponse result = client.execute(post);
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("EXCEPTION: class java.lang.IllegalStateException", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

