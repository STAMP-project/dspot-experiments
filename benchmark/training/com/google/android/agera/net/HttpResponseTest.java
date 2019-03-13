/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.agera.net;


import com.google.android.agera.Result;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class HttpResponseTest {
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String UTF16_CHARSET = "UTF-16";

    private static final String BODY_STRING = "body string?";

    private static final String CONTENT_TYPE = "content-type";

    private static final int FAILED_RESPONSE_CODE = 404;

    private static final String FAILED_RESPONSE_MESSAGE = "failure";

    private static final int SUCCESSFUL_RESPONSE_CODE = 202;

    private static final String SUCCESSFUL_RESPONSE_MESSAGE = "success";

    private static final HashMap<String, String> HEADERS = new HashMap<>();

    private static final String TEXT_PLAIN_CHARSET_INVALID = "text/plain; charset=invalid";

    private HttpResponse failedResponse;

    private HttpResponse successfulHttpResponse;

    private byte[] defaultCharsetBody;

    private byte[] utf16CharsetBody;

    @Test
    public void shouldHaveResponseCodeForFailure() {
        MatcherAssert.assertThat(failedResponse.getResponseCode(), Matchers.is(HttpResponseTest.FAILED_RESPONSE_CODE));
    }

    @Test
    public void shouldHaveResponseCodeForSuccess() {
        MatcherAssert.assertThat(successfulHttpResponse.getResponseCode(), Matchers.is(HttpResponseTest.SUCCESSFUL_RESPONSE_CODE));
    }

    @Test
    public void shouldHaveResponseMessageForFailure() {
        MatcherAssert.assertThat(failedResponse.getResponseMessage(), Matchers.is(HttpResponseTest.FAILED_RESPONSE_MESSAGE));
    }

    @Test
    public void shouldHaveResponseMessageForSuccess() {
        MatcherAssert.assertThat(successfulHttpResponse.getResponseMessage(), Matchers.is(HttpResponseTest.SUCCESSFUL_RESPONSE_MESSAGE));
    }

    @Test
    public void shouldGetSuccessBodyStringForSuccess() throws Throwable {
        MatcherAssert.assertThat(successfulHttpResponse.getBodyString().get(), Matchers.is(HttpResponseTest.BODY_STRING));
    }

    @Test
    public void shouldGetBodyStringForDefaultCharsetWithContentTypeHeader() throws Throwable {
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(HttpResponseTest.CONTENT_TYPE, "text/plain");
        final HttpResponse httpResponse = HttpResponse.httpResponse(HttpResponseTest.SUCCESSFUL_RESPONSE_CODE, HttpResponseTest.SUCCESSFUL_RESPONSE_MESSAGE, headers, defaultCharsetBody);
        MatcherAssert.assertThat(httpResponse.getBodyString().get(), Matchers.is(HttpResponseTest.BODY_STRING));
    }

    @Test
    public void shouldGetBodyStringForCustomCharsetWithContentTypeHeader() throws Throwable {
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(HttpResponseTest.CONTENT_TYPE, ("text/plain; charset=" + (HttpResponseTest.UTF16_CHARSET)));
        final HttpResponse httpResponse = HttpResponse.httpResponse(HttpResponseTest.SUCCESSFUL_RESPONSE_CODE, HttpResponseTest.SUCCESSFUL_RESPONSE_MESSAGE, headers, utf16CharsetBody);
        MatcherAssert.assertThat(httpResponse.getBodyString(), Matchers.is(Result.success(HttpResponseTest.BODY_STRING)));
    }

    @Test
    public void shouldGetFailureForInvalidCharset() {
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(HttpResponseTest.CONTENT_TYPE, HttpResponseTest.TEXT_PLAIN_CHARSET_INVALID);
        final HttpResponse httpResponse = HttpResponse.httpResponse(HttpResponseTest.SUCCESSFUL_RESPONSE_CODE, HttpResponseTest.SUCCESSFUL_RESPONSE_MESSAGE, headers, defaultCharsetBody);
        MatcherAssert.assertThat(httpResponse.getBodyString().getFailure(), Matchers.instanceOf(UnsupportedEncodingException.class));
    }

    @Test
    public void shouldGetAbsentForAbsentHeaderField() {
        MatcherAssert.assertThat(successfulHttpResponse.getHeaderFieldValue("absentfield").isAbsent(), Matchers.is(true));
    }

    @Test
    public void shouldGetCaseInsensitiveHeaders() {
        final Map<String, String> headers = new HashMap<>();
        final String headerContent = "headercontent";
        headers.put("header", headerContent);
        final HttpResponse httpResponse = HttpResponse.httpResponse(HttpResponseTest.SUCCESSFUL_RESPONSE_CODE, HttpResponseTest.SUCCESSFUL_RESPONSE_MESSAGE, headers, defaultCharsetBody);
        MatcherAssert.assertThat(httpResponse.getHeaderFieldValue("hEaDeR").get(), Matchers.is(headerContent));
    }

    @Test
    public void shouldVerifyEquals() {
        EqualsVerifier.forClass(HttpResponse.class).verify();
    }

    @Test
    public void shouldCreateStringRepresentation() {
        MatcherAssert.assertThat(successfulHttpResponse, Matchers.hasToString(Matchers.not(Matchers.isEmptyOrNullString())));
    }
}

