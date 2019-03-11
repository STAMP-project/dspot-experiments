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
package io.undertow.servlet.test.charset;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
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
public class CharacterEncodingTestCase {
    private static final byte[] UTF16 = CharacterEncodingTestCase.toByteArray(new int[]{ 0, 65, 0, 169, 0, 233, 3, 1, 9, 65, 216, 53, 221, 10 });

    private static final byte[] UTF8 = CharacterEncodingTestCase.toByteArray(new int[]{ 65, 194, 169, 195, 169, 204, 129, 224, 165, 129, 240, 157, 148, 138 });

    @Test
    public void testCharacterEncoding() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext?charset=UTF-16BE"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            byte[] response = HttpClientUtils.readRawResponse(result);
            Assert.assertArrayEquals(CharacterEncodingTestCase.UTF16, response);
            get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext?charset=UTF-8"));
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readRawResponse(result);
            Assert.assertArrayEquals(CharacterEncodingTestCase.UTF8, response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

