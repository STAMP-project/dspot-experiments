/**
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.server.adapter;


import HttpStatus.NOT_MODIFIED;
import java.text.SimpleDateFormat;
import java.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;


/**
 * "checkNotModified" unit tests for {@link DefaultServerWebExchange}.
 *
 * @author Rossen Stoyanchev
 */
@RunWith(Parameterized.class)
public class DefaultServerWebExchangeCheckNotModifiedTests {
    private static final String CURRENT_TIME = "Wed, 09 Apr 2014 09:57:42 GMT";

    private SimpleDateFormat dateFormat;

    private Instant currentDate;

    @Parameterized.Parameter
    public HttpMethod method;

    @Test
    public void checkNotModifiedNon2xxStatus() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").ifModifiedSince(this.currentDate.toEpochMilli()).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(NOT_MODIFIED);
        Assert.assertFalse(checkNotModified(this.currentDate));
        Assert.assertEquals(304, getStatusCode().value());
        Assert.assertEquals((-1), getHeaders().getLastModified());
    }

    // SPR-14559
    @Test
    public void checkNotModifiedInvalidIfNoneMatchHeader() {
        String eTag = "\"etagvalue\"";
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").ifNoneMatch("missingquotes"));
        Assert.assertFalse(exchange.checkNotModified(eTag));
        Assert.assertNull(getStatusCode());
        Assert.assertEquals(eTag, getHeaders().getETag());
    }

    @Test
    public void checkNotModifiedHeaderAlreadySet() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").ifModifiedSince(currentDate.toEpochMilli()).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        getHeaders().add("Last-Modified", DefaultServerWebExchangeCheckNotModifiedTests.CURRENT_TIME);
        Assert.assertTrue(checkNotModified(currentDate));
        Assert.assertEquals(304, getStatusCode().value());
        Assert.assertEquals(1, getHeaders().get("Last-Modified").size());
        Assert.assertEquals(DefaultServerWebExchangeCheckNotModifiedTests.CURRENT_TIME, getHeaders().getFirst("Last-Modified"));
    }

    @Test
    public void checkNotModifiedTimestamp() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").ifModifiedSince(currentDate.toEpochMilli()).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertTrue(checkNotModified(currentDate));
        Assert.assertEquals(304, getStatusCode().value());
        Assert.assertEquals(currentDate.toEpochMilli(), getHeaders().getLastModified());
    }

    @Test
    public void checkModifiedTimestamp() {
        Instant oneMinuteAgo = currentDate.minusSeconds(60);
        MockServerHttpRequest request = MockServerHttpRequest.get("/").ifModifiedSince(oneMinuteAgo.toEpochMilli()).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertFalse(checkNotModified(currentDate));
        Assert.assertNull(getStatusCode());
        Assert.assertEquals(currentDate.toEpochMilli(), getHeaders().getLastModified());
    }

    @Test
    public void checkNotModifiedETag() {
        String eTag = "\"Foo\"";
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").ifNoneMatch(eTag));
        Assert.assertTrue(exchange.checkNotModified(eTag));
        Assert.assertEquals(304, getStatusCode().value());
        Assert.assertEquals(eTag, getHeaders().getETag());
    }

    @Test
    public void checkNotModifiedETagWithSeparatorChars() {
        String eTag = "\"Foo, Bar\"";
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").ifNoneMatch(eTag));
        Assert.assertTrue(exchange.checkNotModified(eTag));
        Assert.assertEquals(304, getStatusCode().value());
        Assert.assertEquals(eTag, getHeaders().getETag());
    }

    @Test
    public void checkModifiedETag() {
        String currentETag = "\"Foo\"";
        String oldEtag = "Bar";
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").ifNoneMatch(oldEtag));
        Assert.assertFalse(exchange.checkNotModified(currentETag));
        Assert.assertNull(getStatusCode());
        Assert.assertEquals(currentETag, getHeaders().getETag());
    }

    @Test
    public void checkNotModifiedUnpaddedETag() {
        String eTag = "Foo";
        String paddedEtag = String.format("\"%s\"", eTag);
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").ifNoneMatch(paddedEtag));
        Assert.assertTrue(exchange.checkNotModified(eTag));
        Assert.assertEquals(304, getStatusCode().value());
        Assert.assertEquals(paddedEtag, getHeaders().getETag());
    }

    @Test
    public void checkModifiedUnpaddedETag() {
        String currentETag = "Foo";
        String oldEtag = "Bar";
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").ifNoneMatch(oldEtag));
        Assert.assertFalse(exchange.checkNotModified(currentETag));
        Assert.assertNull(getStatusCode());
        Assert.assertEquals(String.format("\"%s\"", currentETag), getHeaders().getETag());
    }

    @Test
    public void checkNotModifiedWildcardIsIgnored() {
        String eTag = "\"Foo\"";
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").ifNoneMatch("*"));
        Assert.assertFalse(exchange.checkNotModified(eTag));
        Assert.assertNull(getStatusCode());
        Assert.assertEquals(eTag, getHeaders().getETag());
    }

    @Test
    public void checkNotModifiedETagAndTimestamp() {
        String eTag = "\"Foo\"";
        long time = currentDate.toEpochMilli();
        MockServerHttpRequest request = MockServerHttpRequest.get("/").ifNoneMatch(eTag).ifModifiedSince(time).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertTrue(exchange.checkNotModified(eTag, currentDate));
        Assert.assertEquals(304, getStatusCode().value());
        Assert.assertEquals(eTag, getHeaders().getETag());
        Assert.assertEquals(time, getHeaders().getLastModified());
    }

    // SPR-14224
    @Test
    public void checkNotModifiedETagAndModifiedTimestamp() {
        String eTag = "\"Foo\"";
        Instant oneMinuteAgo = currentDate.minusSeconds(60);
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").ifNoneMatch(eTag).ifModifiedSince(oneMinuteAgo.toEpochMilli()));
        Assert.assertTrue(exchange.checkNotModified(eTag, currentDate));
        Assert.assertEquals(304, getStatusCode().value());
        Assert.assertEquals(eTag, getHeaders().getETag());
        Assert.assertEquals(currentDate.toEpochMilli(), getHeaders().getLastModified());
    }

    @Test
    public void checkModifiedETagAndNotModifiedTimestamp() throws Exception {
        String currentETag = "\"Foo\"";
        String oldEtag = "\"Bar\"";
        long time = currentDate.toEpochMilli();
        MockServerHttpRequest request = MockServerHttpRequest.get("/").ifNoneMatch(oldEtag).ifModifiedSince(time).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertFalse(exchange.checkNotModified(currentETag, currentDate));
        Assert.assertNull(getStatusCode());
        Assert.assertEquals(currentETag, getHeaders().getETag());
        Assert.assertEquals(time, getHeaders().getLastModified());
    }

    @Test
    public void checkNotModifiedETagWeakStrong() {
        String eTag = "\"Foo\"";
        String weakEtag = String.format("W/%s", eTag);
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").ifNoneMatch(eTag));
        Assert.assertTrue(exchange.checkNotModified(weakEtag));
        Assert.assertEquals(304, getStatusCode().value());
        Assert.assertEquals(weakEtag, getHeaders().getETag());
    }

    @Test
    public void checkNotModifiedETagStrongWeak() {
        String eTag = "\"Foo\"";
        MockServerHttpRequest request = MockServerHttpRequest.get("/").ifNoneMatch(String.format("W/%s", eTag)).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertTrue(exchange.checkNotModified(eTag));
        Assert.assertEquals(304, getStatusCode().value());
        Assert.assertEquals(eTag, getHeaders().getETag());
    }

    @Test
    public void checkNotModifiedMultipleETags() {
        String eTag = "\"Bar\"";
        String multipleETags = String.format("\"Foo\", %s", eTag);
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").ifNoneMatch(multipleETags));
        Assert.assertTrue(exchange.checkNotModified(eTag));
        Assert.assertEquals(304, getStatusCode().value());
        Assert.assertEquals(eTag, getHeaders().getETag());
    }

    @Test
    public void checkNotModifiedTimestampWithLengthPart() throws Exception {
        long epochTime = dateFormat.parse(DefaultServerWebExchangeCheckNotModifiedTests.CURRENT_TIME).getTime();
        String header = "Wed, 09 Apr 2014 09:57:42 GMT; length=13774";
        MockServerHttpRequest request = MockServerHttpRequest.get("/").header("If-Modified-Since", header).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertTrue(checkNotModified(Instant.ofEpochMilli(epochTime)));
        Assert.assertEquals(304, getStatusCode().value());
        Assert.assertEquals(epochTime, getHeaders().getLastModified());
    }

    @Test
    public void checkModifiedTimestampWithLengthPart() throws Exception {
        long epochTime = dateFormat.parse(DefaultServerWebExchangeCheckNotModifiedTests.CURRENT_TIME).getTime();
        String header = "Tue, 08 Apr 2014 09:57:42 GMT; length=13774";
        MockServerHttpRequest request = MockServerHttpRequest.get("/").header("If-Modified-Since", header).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertFalse(checkNotModified(Instant.ofEpochMilli(epochTime)));
        Assert.assertNull(getStatusCode());
        Assert.assertEquals(epochTime, getHeaders().getLastModified());
    }

    @Test
    public void checkNotModifiedTimestampConditionalPut() throws Exception {
        Instant oneMinuteAgo = currentDate.minusSeconds(60);
        long millis = currentDate.toEpochMilli();
        MockServerHttpRequest request = MockServerHttpRequest.put("/").ifUnmodifiedSince(millis).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertFalse(checkNotModified(oneMinuteAgo));
        Assert.assertNull(getStatusCode());
        Assert.assertEquals((-1), getHeaders().getLastModified());
    }

    @Test
    public void checkNotModifiedTimestampConditionalPutConflict() throws Exception {
        Instant oneMinuteAgo = currentDate.minusSeconds(60);
        long millis = oneMinuteAgo.toEpochMilli();
        MockServerHttpRequest request = MockServerHttpRequest.put("/").ifUnmodifiedSince(millis).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Assert.assertTrue(checkNotModified(currentDate));
        Assert.assertEquals(412, getStatusCode().value());
        Assert.assertEquals((-1), getHeaders().getLastModified());
    }
}

