/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.util;


import com.google.api.client.http.GenericUrl;
import com.google.api.client.testing.http.HttpTesting;
import java.io.IOException;
import org.apache.beam.sdk.testing.ExpectedLogs;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A test for {@link UploadIdResponseInterceptor}.
 */
@RunWith(JUnit4.class)
public class UploadIdResponseInterceptorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // Note that expected logs also turns on debug logging.
    @Rule
    public ExpectedLogs expectedLogs = ExpectedLogs.none(UploadIdResponseInterceptor.class);

    /**
     * Tests the responses that should not log.
     */
    @Test
    public void testResponseNoLogging() throws IOException {
        new UploadIdResponseInterceptor().interceptResponse(buildHttpResponse(null, null, null));
        new UploadIdResponseInterceptor().interceptResponse(buildHttpResponse("hh", "a", null));
        new UploadIdResponseInterceptor().interceptResponse(buildHttpResponse(null, "h", null));
        new UploadIdResponseInterceptor().interceptResponse(buildHttpResponse("hh", null, null));
        new UploadIdResponseInterceptor().interceptResponse(buildHttpResponse(null, null, "type"));
        new UploadIdResponseInterceptor().interceptResponse(buildHttpResponse("hh", "a", "type"));
        new UploadIdResponseInterceptor().interceptResponse(buildHttpResponse(null, "h", "type"));
        expectedLogs.verifyNotLogged("");
    }

    /**
     * Check that a response logs with the correct log.
     */
    @Test
    public void testResponseLogs() throws IOException {
        new UploadIdResponseInterceptor().interceptResponse(buildHttpResponse("abc", null, "type"));
        GenericUrl url = new GenericUrl(HttpTesting.SIMPLE_URL);
        url.put("uploadType", "type");
        String worker = System.getProperty("worker_id");
        expectedLogs.verifyDebug((((("Upload ID for url " + url) + " on worker ") + worker) + " is abc"));
    }
}

