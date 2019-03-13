/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.test.web.reactive.server;


import MediaType.APPLICATION_JSON_UTF8;
import MediaType.APPLICATION_XML;
import MediaType.TEXT_XML;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


/**
 * Unit tests for {@link HeaderAssertions}.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 */
public class HeaderAssertionTests {
    @Test
    public void valueEquals() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("foo", "bar");
        HeaderAssertions assertions = headerAssertions(headers);
        // Success
        assertions.valueEquals("foo", "bar");
        try {
            assertions.valueEquals("what?!", "bar");
            Assert.fail("Missing header expected");
        } catch (AssertionError error) {
            // expected
        }
        try {
            assertions.valueEquals("foo", "what?!");
            Assert.fail("Wrong value expected");
        } catch (AssertionError error) {
            // expected
        }
        try {
            assertions.valueEquals("foo", "bar", "what?!");
            Assert.fail("Wrong # of values expected");
        } catch (AssertionError error) {
            // expected
        }
    }

    @Test
    public void valueEqualsWithMultipleValues() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("foo", "bar");
        headers.add("foo", "baz");
        HeaderAssertions assertions = headerAssertions(headers);
        // Success
        assertions.valueEquals("foo", "bar", "baz");
        try {
            assertions.valueEquals("foo", "bar", "what?!");
            Assert.fail("Wrong value expected");
        } catch (AssertionError error) {
            // expected
        }
        try {
            assertions.valueEquals("foo", "bar");
            Assert.fail("Too few values expected");
        } catch (AssertionError error) {
            // expected
        }
    }

    @Test
    public void valueMatches() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON_UTF8);
        HeaderAssertions assertions = headerAssertions(headers);
        // Success
        assertions.valueMatches("Content-Type", ".*UTF-8.*");
        try {
            assertions.valueMatches("Content-Type", ".*ISO-8859-1.*");
            Assert.fail("Wrong pattern expected");
        } catch (AssertionError error) {
            Throwable cause = error.getCause();
            Assert.assertNotNull(cause);
            Assert.assertEquals(("Response header 'Content-Type'=[application/json;charset=UTF-8] " + "does not match [.*ISO-8859-1.*]"), cause.getMessage());
        }
    }

    @Test
    public void valueMatcher() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("foo", "bar");
        HeaderAssertions assertions = headerAssertions(headers);
        assertions.value("foo", CoreMatchers.containsString("a"));
    }

    @Test
    public void exists() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON_UTF8);
        HeaderAssertions assertions = headerAssertions(headers);
        // Success
        assertions.exists("Content-Type");
        try {
            assertions.exists("Framework");
            Assert.fail("Header should not exist");
        } catch (AssertionError error) {
            Throwable cause = error.getCause();
            Assert.assertNotNull(cause);
            Assert.assertEquals("Response header 'Framework' does not exist", cause.getMessage());
        }
    }

    @Test
    public void doesNotExist() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON_UTF8);
        HeaderAssertions assertions = headerAssertions(headers);
        // Success
        assertions.doesNotExist("Framework");
        try {
            assertions.doesNotExist("Content-Type");
            Assert.fail("Existing header expected");
        } catch (AssertionError error) {
            Throwable cause = error.getCause();
            Assert.assertNotNull(cause);
            Assert.assertEquals(("Response header 'Content-Type' exists with " + "value=[application/json;charset=UTF-8]"), cause.getMessage());
        }
    }

    @Test
    public void contentTypeCompatibleWith() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_XML);
        HeaderAssertions assertions = headerAssertions(headers);
        // Success
        assertions.contentTypeCompatibleWith(MediaType.parseMediaType("application/*"));
        try {
            assertions.contentTypeCompatibleWith(TEXT_XML);
            Assert.fail("MediaTypes not compatible expected");
        } catch (AssertionError error) {
            Throwable cause = error.getCause();
            Assert.assertNotNull(cause);
            Assert.assertEquals(("Response header 'Content-Type'=[application/xml] " + "is not compatible with [text/xml]"), cause.getMessage());
        }
    }

    @Test
    public void cacheControl() {
        CacheControl control = CacheControl.maxAge(1, TimeUnit.HOURS).noTransform();
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(control.getHeaderValue());
        HeaderAssertions assertions = headerAssertions(headers);
        // Success
        assertions.cacheControl(control);
        try {
            assertions.cacheControl(CacheControl.noStore());
            Assert.fail("Wrong value expected");
        } catch (AssertionError error) {
            // Expected
        }
    }

    @Test
    public void expires() {
        HttpHeaders headers = new HttpHeaders();
        ZonedDateTime expires = ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
        headers.setExpires(expires);
        HeaderAssertions assertions = headerAssertions(headers);
        assertions.expires(expires.toInstant().toEpochMilli());
        try {
            assertions.expires(((expires.toInstant().toEpochMilli()) + 1));
            Assert.fail("Wrong value expected");
        } catch (AssertionError error) {
            // Expected
        }
    }

    @Test
    public void lastModified() {
        HttpHeaders headers = new HttpHeaders();
        ZonedDateTime lastModified = ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
        headers.setLastModified(lastModified.toInstant().toEpochMilli());
        HeaderAssertions assertions = headerAssertions(headers);
        assertions.lastModified(lastModified.toInstant().toEpochMilli());
        try {
            assertions.lastModified(((lastModified.toInstant().toEpochMilli()) + 1));
            Assert.fail("Wrong value expected");
        } catch (AssertionError error) {
            // Expected
        }
    }
}

