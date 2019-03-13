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
package io.undertow.server.ssl;


import StatusCodes.OK;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.NameVirtualHostHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.file.FileHandlerTestCase;
import io.undertow.testutils.AjpIgnore;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@AjpIgnore
@RunWith(DefaultServer.class)
public class ComplexSSLTestCase {
    private static final String MESSAGE = "My HTTP Request!";

    private static volatile String message;

    @Test
    public void complexSSLTestCase() throws IOException, InterruptedException, URISyntaxException, GeneralSecurityException {
        final PathHandler pathHandler = new PathHandler();
        Path rootPath = Paths.get(FileHandlerTestCase.class.getResource("page.html").toURI()).getParent();
        final NameVirtualHostHandler virtualHostHandler = new NameVirtualHostHandler();
        HttpHandler root = virtualHostHandler;
        root = new io.undertow.server.handlers.error.SimpleErrorPageHandler(root);
        root = new io.undertow.server.handlers.CanonicalPathHandler(root);
        virtualHostHandler.addHost("default-host", pathHandler);
        virtualHostHandler.setDefaultHandler(pathHandler);
        pathHandler.addPrefixPath("/", setDirectoryListingEnabled(true));
        DefaultServer.setRootHandler(root);
        DefaultServer.startSSLServer();
        TestHttpClient client = new TestHttpClient();
        client.setSSLContext(DefaultServer.getClientSSLContext());
        try {
            // get file list, this works
            HttpGet getFileList = new HttpGet(DefaultServer.getDefaultServerSSLAddress());
            HttpResponse resultList = client.execute(getFileList);
            Assert.assertEquals(OK, resultList.getStatusLine().getStatusCode());
            String responseList = HttpClientUtils.readResponse(resultList);
            Assert.assertTrue(responseList, responseList.contains("page.html"));
            Header[] headersList = resultList.getHeaders("Content-Type");
            Assert.assertEquals("text/html; charset=UTF-8", headersList[0].getValue());
            // get file itself, breaks
            HttpGet getFile = new HttpGet(((DefaultServer.getDefaultServerSSLAddress()) + "/page.html"));
            HttpResponse result = client.execute(getFile);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Header[] headers = result.getHeaders("Content-Type");
            Assert.assertEquals("text/html", headers[0].getValue());
            Assert.assertTrue(response, response.contains("A web page"));
        } finally {
            client.getConnectionManager().shutdown();
            DefaultServer.stopSSLServer();
        }
    }

    @Test
    public void testSslLotsOfData() throws IOException, URISyntaxException, GeneralSecurityException {
        DefaultServer.setRootHandler(new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                if (exchange.isInIoThread()) {
                    exchange.dispatch(this);
                    return;
                }
                exchange.startBlocking();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[100];
                int res = 0;
                while ((res = exchange.getInputStream().read(buf)) > 0) {
                    out.write(buf, 0, res);
                } 
                System.out.println(("WRITE " + (out.size())));
                exchange.getOutputStream().write(out.toByteArray());
                System.out.println(("DONE " + (out.size())));
            }
        });
        DefaultServer.startSSLServer();
        TestHttpClient client = new TestHttpClient();
        client.setSSLContext(DefaultServer.getClientSSLContext());
        try {
            ComplexSSLTestCase.generateMessage(1000000);
            HttpPost post = new HttpPost(DefaultServer.getDefaultServerSSLAddress());
            post.setEntity(new StringEntity(ComplexSSLTestCase.message));
            HttpResponse resultList = client.execute(post);
            Assert.assertEquals(OK, resultList.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(resultList);
            Assert.assertEquals(ComplexSSLTestCase.message.length(), response.length());
            Assert.assertEquals(ComplexSSLTestCase.message, response);
            ComplexSSLTestCase.generateMessage(100000);
            post = new HttpPost(DefaultServer.getDefaultServerSSLAddress());
            post.setEntity(new StringEntity(ComplexSSLTestCase.message));
            resultList = client.execute(post);
            Assert.assertEquals(OK, resultList.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(resultList);
            Assert.assertEquals(ComplexSSLTestCase.message.length(), response.length());
            Assert.assertEquals(ComplexSSLTestCase.message, response);
        } finally {
            client.getConnectionManager().shutdown();
            DefaultServer.stopSSLServer();
        }
    }
}

