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
package org.springframework.web.context.request;


import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;


/**
 * Parameterized tests for {@link ServletWebRequest}.
 *
 * @author Juergen Hoeller
 * @author Brian Clozel
 * @author Markus Malkusch
 */
@RunWith(Parameterized.class)
public class ServletWebRequestHttpMethodsTests {
    private static final String CURRENT_TIME = "Wed, 9 Apr 2014 09:57:42 GMT";

    private MockHttpServletRequest servletRequest;

    private MockHttpServletResponse servletResponse;

    private ServletWebRequest request;

    private Date currentDate;

    @Parameterized.Parameter
    public String method;

    @Test
    public void checkNotModifiedNon2xxStatus() {
        long epochTime = currentDate.getTime();
        servletRequest.addHeader("If-Modified-Since", epochTime);
        servletResponse.setStatus(304);
        Assert.assertFalse(request.checkNotModified(epochTime));
        Assert.assertEquals(304, servletResponse.getStatus());
        Assert.assertNull(servletResponse.getHeader("Last-Modified"));
    }

    // SPR-13516
    @Test
    public void checkNotModifiedInvalidStatus() {
        long epochTime = currentDate.getTime();
        servletRequest.addHeader("If-Modified-Since", epochTime);
        servletResponse.setStatus(0);
        Assert.assertFalse(request.checkNotModified(epochTime));
    }

    // SPR-14559
    @Test
    public void checkNotModifiedInvalidIfNoneMatchHeader() {
        String etag = "\"etagvalue\"";
        servletRequest.addHeader("If-None-Match", "missingquotes");
        Assert.assertFalse(request.checkNotModified(etag));
        Assert.assertEquals(200, servletResponse.getStatus());
        Assert.assertEquals(etag, servletResponse.getHeader("ETag"));
    }

    @Test
    public void checkNotModifiedHeaderAlreadySet() {
        long epochTime = currentDate.getTime();
        servletRequest.addHeader("If-Modified-Since", epochTime);
        servletResponse.addHeader("Last-Modified", ServletWebRequestHttpMethodsTests.CURRENT_TIME);
        Assert.assertTrue(request.checkNotModified(epochTime));
        Assert.assertEquals(304, servletResponse.getStatus());
        Assert.assertEquals(1, servletResponse.getHeaders("Last-Modified").size());
        Assert.assertEquals(ServletWebRequestHttpMethodsTests.CURRENT_TIME, servletResponse.getHeader("Last-Modified"));
    }

    @Test
    public void checkNotModifiedTimestamp() {
        long epochTime = currentDate.getTime();
        servletRequest.addHeader("If-Modified-Since", epochTime);
        Assert.assertTrue(request.checkNotModified(epochTime));
        Assert.assertEquals(304, servletResponse.getStatus());
        Assert.assertEquals(((currentDate.getTime()) / 1000), ((servletResponse.getDateHeader("Last-Modified")) / 1000));
    }

    @Test
    public void checkModifiedTimestamp() {
        long oneMinuteAgo = (currentDate.getTime()) - (1000 * 60);
        servletRequest.addHeader("If-Modified-Since", oneMinuteAgo);
        Assert.assertFalse(request.checkNotModified(currentDate.getTime()));
        Assert.assertEquals(200, servletResponse.getStatus());
        Assert.assertEquals(((currentDate.getTime()) / 1000), ((servletResponse.getDateHeader("Last-Modified")) / 1000));
    }

    @Test
    public void checkNotModifiedETag() {
        String etag = "\"Foo\"";
        servletRequest.addHeader("If-None-Match", etag);
        Assert.assertTrue(request.checkNotModified(etag));
        Assert.assertEquals(304, servletResponse.getStatus());
        Assert.assertEquals(etag, servletResponse.getHeader("ETag"));
    }

    @Test
    public void checkNotModifiedETagWithSeparatorChars() {
        String etag = "\"Foo, Bar\"";
        servletRequest.addHeader("If-None-Match", etag);
        Assert.assertTrue(request.checkNotModified(etag));
        Assert.assertEquals(304, servletResponse.getStatus());
        Assert.assertEquals(etag, servletResponse.getHeader("ETag"));
    }

    @Test
    public void checkModifiedETag() {
        String currentETag = "\"Foo\"";
        String oldETag = "Bar";
        servletRequest.addHeader("If-None-Match", oldETag);
        Assert.assertFalse(request.checkNotModified(currentETag));
        Assert.assertEquals(200, servletResponse.getStatus());
        Assert.assertEquals(currentETag, servletResponse.getHeader("ETag"));
    }

    @Test
    public void checkNotModifiedUnpaddedETag() {
        String etag = "Foo";
        String paddedETag = String.format("\"%s\"", etag);
        servletRequest.addHeader("If-None-Match", paddedETag);
        Assert.assertTrue(request.checkNotModified(etag));
        Assert.assertEquals(304, servletResponse.getStatus());
        Assert.assertEquals(paddedETag, servletResponse.getHeader("ETag"));
    }

    @Test
    public void checkModifiedUnpaddedETag() {
        String currentETag = "Foo";
        String oldETag = "Bar";
        servletRequest.addHeader("If-None-Match", oldETag);
        Assert.assertFalse(request.checkNotModified(currentETag));
        Assert.assertEquals(200, servletResponse.getStatus());
        Assert.assertEquals(String.format("\"%s\"", currentETag), servletResponse.getHeader("ETag"));
    }

    @Test
    public void checkNotModifiedWildcardIsIgnored() {
        String etag = "\"Foo\"";
        servletRequest.addHeader("If-None-Match", "*");
        Assert.assertFalse(request.checkNotModified(etag));
        Assert.assertEquals(200, servletResponse.getStatus());
        Assert.assertEquals(etag, servletResponse.getHeader("ETag"));
    }

    @Test
    public void checkNotModifiedETagAndTimestamp() {
        String etag = "\"Foo\"";
        servletRequest.addHeader("If-None-Match", etag);
        servletRequest.addHeader("If-Modified-Since", currentDate.getTime());
        Assert.assertTrue(request.checkNotModified(etag, currentDate.getTime()));
        Assert.assertEquals(304, servletResponse.getStatus());
        Assert.assertEquals(etag, servletResponse.getHeader("ETag"));
        Assert.assertEquals(((currentDate.getTime()) / 1000), ((servletResponse.getDateHeader("Last-Modified")) / 1000));
    }

    // SPR-14224
    @Test
    public void checkNotModifiedETagAndModifiedTimestamp() {
        String etag = "\"Foo\"";
        servletRequest.addHeader("If-None-Match", etag);
        long currentEpoch = currentDate.getTime();
        long oneMinuteAgo = currentEpoch - (1000 * 60);
        servletRequest.addHeader("If-Modified-Since", oneMinuteAgo);
        Assert.assertTrue(request.checkNotModified(etag, currentEpoch));
        Assert.assertEquals(304, servletResponse.getStatus());
        Assert.assertEquals(etag, servletResponse.getHeader("ETag"));
        Assert.assertEquals(((currentDate.getTime()) / 1000), ((servletResponse.getDateHeader("Last-Modified")) / 1000));
    }

    @Test
    public void checkModifiedETagAndNotModifiedTimestamp() {
        String currentETag = "\"Foo\"";
        String oldETag = "\"Bar\"";
        servletRequest.addHeader("If-None-Match", oldETag);
        long epochTime = currentDate.getTime();
        servletRequest.addHeader("If-Modified-Since", epochTime);
        Assert.assertFalse(request.checkNotModified(currentETag, epochTime));
        Assert.assertEquals(200, servletResponse.getStatus());
        Assert.assertEquals(currentETag, servletResponse.getHeader("ETag"));
        Assert.assertEquals(((currentDate.getTime()) / 1000), ((servletResponse.getDateHeader("Last-Modified")) / 1000));
    }

    @Test
    public void checkNotModifiedETagWeakStrong() {
        String etag = "\"Foo\"";
        String weakETag = String.format("W/%s", etag);
        servletRequest.addHeader("If-None-Match", etag);
        Assert.assertTrue(request.checkNotModified(weakETag));
        Assert.assertEquals(304, servletResponse.getStatus());
        Assert.assertEquals(weakETag, servletResponse.getHeader("ETag"));
    }

    @Test
    public void checkNotModifiedETagStrongWeak() {
        String etag = "\"Foo\"";
        servletRequest.addHeader("If-None-Match", String.format("W/%s", etag));
        Assert.assertTrue(request.checkNotModified(etag));
        Assert.assertEquals(304, servletResponse.getStatus());
        Assert.assertEquals(etag, servletResponse.getHeader("ETag"));
    }

    @Test
    public void checkNotModifiedMultipleETags() {
        String etag = "\"Bar\"";
        String multipleETags = String.format("\"Foo\", %s", etag);
        servletRequest.addHeader("If-None-Match", multipleETags);
        Assert.assertTrue(request.checkNotModified(etag));
        Assert.assertEquals(304, servletResponse.getStatus());
        Assert.assertEquals(etag, servletResponse.getHeader("ETag"));
    }

    @Test
    public void checkNotModifiedTimestampWithLengthPart() {
        long epochTime = ZonedDateTime.parse(ServletWebRequestHttpMethodsTests.CURRENT_TIME, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant().toEpochMilli();
        servletRequest.setMethod("GET");
        servletRequest.addHeader("If-Modified-Since", "Wed, 09 Apr 2014 09:57:42 GMT; length=13774");
        Assert.assertTrue(request.checkNotModified(epochTime));
        Assert.assertEquals(304, servletResponse.getStatus());
        Assert.assertEquals((epochTime / 1000), ((servletResponse.getDateHeader("Last-Modified")) / 1000));
    }

    @Test
    public void checkModifiedTimestampWithLengthPart() {
        long epochTime = ZonedDateTime.parse(ServletWebRequestHttpMethodsTests.CURRENT_TIME, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant().toEpochMilli();
        servletRequest.setMethod("GET");
        servletRequest.addHeader("If-Modified-Since", "Wed, 08 Apr 2014 09:57:42 GMT; length=13774");
        Assert.assertFalse(request.checkNotModified(epochTime));
        Assert.assertEquals(200, servletResponse.getStatus());
        Assert.assertEquals((epochTime / 1000), ((servletResponse.getDateHeader("Last-Modified")) / 1000));
    }

    @Test
    public void checkNotModifiedTimestampConditionalPut() {
        long currentEpoch = currentDate.getTime();
        long oneMinuteAgo = currentEpoch - (1000 * 60);
        servletRequest.setMethod("PUT");
        servletRequest.addHeader("If-UnModified-Since", currentEpoch);
        Assert.assertFalse(request.checkNotModified(oneMinuteAgo));
        Assert.assertEquals(200, servletResponse.getStatus());
        Assert.assertEquals(null, servletResponse.getHeader("Last-Modified"));
    }

    @Test
    public void checkNotModifiedTimestampConditionalPutConflict() {
        long currentEpoch = currentDate.getTime();
        long oneMinuteAgo = currentEpoch - (1000 * 60);
        servletRequest.setMethod("PUT");
        servletRequest.addHeader("If-UnModified-Since", oneMinuteAgo);
        Assert.assertTrue(request.checkNotModified(currentEpoch));
        Assert.assertEquals(412, servletResponse.getStatus());
        Assert.assertEquals(null, servletResponse.getHeader("Last-Modified"));
    }
}

