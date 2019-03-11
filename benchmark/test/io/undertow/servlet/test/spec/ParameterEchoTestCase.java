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
package io.undertow.servlet.test.spec;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Istvan Szabo
 */
@RunWith(DefaultServer.class)
public class ParameterEchoTestCase {
    public static final String RESPONSE = "param1=\'1\'param2=\'2\'param3=\'3\'";

    @Test
    public void testPostInUrl() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpPost post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/servletContext/aaa?param1=1&param2=2&param3=3"));
            final List<NameValuePair> values = new ArrayList<>();
            UrlEncodedFormEntity data = new UrlEncodedFormEntity(values, "UTF-8");
            post.setEntity(data);
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(ParameterEchoTestCase.RESPONSE, response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testPostInStream() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpPost post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/servletContext/aaa"));
            final List<NameValuePair> values = new ArrayList<>();
            values.add(new BasicNameValuePair("param1", "1"));
            values.add(new BasicNameValuePair("param2", "2"));
            values.add(new BasicNameValuePair("param3", "3"));
            UrlEncodedFormEntity data = new UrlEncodedFormEntity(values, "UTF-8");
            post.setEntity(data);
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(ParameterEchoTestCase.RESPONSE, response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testPostBoth() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpPost post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/servletContext/aaa?param1=1&param2=2"));
            final List<NameValuePair> values = new ArrayList<>();
            values.add(new BasicNameValuePair("param3", "3"));
            UrlEncodedFormEntity data = new UrlEncodedFormEntity(values, "UTF-8");
            post.setEntity(data);
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(ParameterEchoTestCase.RESPONSE, response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

