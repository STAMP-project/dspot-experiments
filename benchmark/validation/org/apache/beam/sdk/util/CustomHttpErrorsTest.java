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


import CustomHttpErrors.Builder;
import java.io.IOException;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for CustomHttpErrorsTest.
 */
@RunWith(JUnit4.class)
public class CustomHttpErrorsTest {
    private static final String BQ_TABLES_LIST_URL = "http://www.googleapis.com/bigquery/v2/projects/" + "myproject/datasets/mydataset/tables?maxResults=1000";

    @Test
    public void testMatchesCode() throws IOException {
        HttpRequestWrapper request = createHttpRequest(CustomHttpErrorsTest.BQ_TABLES_LIST_URL);
        HttpResponseWrapper response = createHttpResponse(403);
        HttpCallCustomError mockCustomError = Mockito.mock(HttpCallCustomError.class);
        CustomHttpErrors.Builder builder = new CustomHttpErrors.Builder();
        builder.addErrorForCode(403, "Custom Error Msg");
        CustomHttpErrors customErrors = builder.build();
        String errorMessage = customErrors.getCustomError(request, response);
        TestCase.assertEquals("Custom Error Msg", errorMessage);
    }

    @Test
    public void testNotMatchesCode() throws IOException {
        HttpRequestWrapper request = createHttpRequest(CustomHttpErrorsTest.BQ_TABLES_LIST_URL);
        HttpResponseWrapper response = createHttpResponse(404);
        HttpCallCustomError mockCustomError = Mockito.mock(HttpCallCustomError.class);
        CustomHttpErrors.Builder builder = new CustomHttpErrors.Builder();
        builder.addErrorForCode(403, "Custom Error Msg");
        CustomHttpErrors customErrors = builder.build();
        String errorMessage = customErrors.getCustomError(request, response);
        TestCase.assertNull(errorMessage);
    }

    @Test
    public void testMatchesCodeAndUrlContains() throws IOException {
        HttpRequestWrapper request = createHttpRequest(CustomHttpErrorsTest.BQ_TABLES_LIST_URL);
        HttpResponseWrapper response = createHttpResponse(403);
        HttpCallCustomError mockCustomError = Mockito.mock(HttpCallCustomError.class);
        CustomHttpErrors.Builder builder = new CustomHttpErrors.Builder();
        builder.addErrorForCodeAndUrlContains(403, "/tables?", "Custom Error Msg");
        CustomHttpErrors customErrors = builder.build();
        String errorMessage = customErrors.getCustomError(request, response);
        TestCase.assertEquals("Custom Error Msg", errorMessage);
    }

    @Test
    public void testNotMatchesCodeAndUrlContains() throws IOException {
        HttpRequestWrapper request = createHttpRequest(CustomHttpErrorsTest.BQ_TABLES_LIST_URL);
        HttpResponseWrapper response = createHttpResponse(404);
        HttpCallCustomError mockCustomError = Mockito.mock(HttpCallCustomError.class);
        CustomHttpErrors.Builder builder = new CustomHttpErrors.Builder();
        builder.addErrorForCodeAndUrlContains(403, "/doesnotmatch?", "Custom Error Msg");
        CustomHttpErrors customErrors = builder.build();
        String errorMessage = customErrors.getCustomError(request, response);
        TestCase.assertNull(errorMessage);
    }
}

