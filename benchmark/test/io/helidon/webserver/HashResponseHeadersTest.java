/**
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver;


import Http.Header.CONTENT_TYPE;
import Http.Header.SET_COOKIE;
import Http.Header.TRANSFER_ENCODING;
import Http.Status.NO_CONTENT_204;
import MediaType.APPLICATION_JSON;
import MediaType.TEXT_XML;
import io.helidon.common.http.AlreadyCompletedException;
import io.helidon.webserver.utils.TestUtils;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static java.time.ZoneId.of;


/**
 * Tests {@link HashResponseHeaders}.
 */
public class HashResponseHeadersTest {
    @Test
    public void acceptPatches() throws Exception {
        HashResponseHeaders h = new HashResponseHeaders(null);
        h.addAcceptPatches(APPLICATION_JSON, TEXT_XML);
        MatcherAssert.assertThat(h.acceptPatches(), IsIterableContainingInOrder.contains(APPLICATION_JSON, TEXT_XML));
    }

    @Test
    public void contentType() throws Exception {
        HashResponseHeaders h = new HashResponseHeaders(null);
        h.contentType(APPLICATION_JSON);
        MatcherAssert.assertThat(h.contentType().orElse(null), CoreMatchers.is(APPLICATION_JSON));
        h.contentType(null);
        MatcherAssert.assertThat(h.contentType().isPresent(), CoreMatchers.is(false));
    }

    @Test
    public void expires() throws Exception {
        HashResponseHeaders h = new HashResponseHeaders(null);
        ZonedDateTime now = ZonedDateTime.now();
        h.expires(now);
        MatcherAssert.assertThat(h.expires().orElse(null), CoreMatchers.is(now.truncatedTo(ChronoUnit.SECONDS).withFixedOffsetZone()));
        h.expires(((ZonedDateTime) (null)));
        MatcherAssert.assertThat(h.expires().isPresent(), CoreMatchers.is(false));
        Instant instant = Instant.now();
        h.expires(instant);
        MatcherAssert.assertThat(h.expires().map(ZonedDateTime::toInstant).orElse(null), CoreMatchers.is(instant.truncatedTo(ChronoUnit.SECONDS)));
    }

    @Test
    public void lastModified() throws Exception {
        HashResponseHeaders h = new HashResponseHeaders(null);
        ZonedDateTime now = ZonedDateTime.now();
        h.lastModified(now);
        MatcherAssert.assertThat(h.lastModified().orElse(null), CoreMatchers.is(now.truncatedTo(ChronoUnit.SECONDS).withFixedOffsetZone()));
        h.lastModified(((ZonedDateTime) (null)));
        MatcherAssert.assertThat(h.lastModified().isPresent(), CoreMatchers.is(false));
        Instant instant = Instant.now();
        h.lastModified(instant);
        MatcherAssert.assertThat(h.lastModified().map(ZonedDateTime::toInstant).orElse(null), CoreMatchers.is(instant.truncatedTo(ChronoUnit.SECONDS)));
    }

    @Test
    public void location() throws Exception {
        HashResponseHeaders h = new HashResponseHeaders(null);
        URI uri = URI.create("http://www.oracle.com");
        h.location(uri);
        MatcherAssert.assertThat(h.location().orElse(null), CoreMatchers.is(uri));
        h.location(null);
        MatcherAssert.assertThat(h.location().isPresent(), CoreMatchers.is(false));
    }

    @Test
    public void addCookie() {
        HashResponseHeaders h = new HashResponseHeaders(null);
        h.addCookie("foo", "bar");
        h.addCookie("aaa", "bbbb", Duration.ofMinutes(10));
        h.addCookie("who", "me", Duration.ofMinutes(0));
        h.addCookie(io.helidon.common.http.SetCookie.builder("itis", "cool").domainAndPath(URI.create("http://oracle.com/foo")).expires(ZonedDateTime.of(2080, 1, 1, 0, 0, 0, 0, of("Z"))).secure(true).build());
        MatcherAssert.assertThat(h.all(SET_COOKIE), contains("foo=bar", "aaa=bbbb; Max-Age=600", "who=me", ("itis=cool; Expires=Mon, 1 Jan 2080 00:00:00 GMT; Domain=oracle.com;" + " Path=/foo; Secure")));
    }

    @Test
    public void immutableWhenCompleted() throws Exception {
        HashResponseHeaders h = new HashResponseHeaders(mockBareResponse());
        h.put("a", "b");
        h.put("a", Arrays.asList("b"));
        h.add("a", "b");
        h.add("a", Arrays.asList("b"));
        h.putIfAbsent("a", "b");
        h.putIfAbsent("a", Arrays.asList("b"));
        h.computeIfAbsent("a", ( k) -> Arrays.asList("b"));
        h.computeSingleIfAbsent("a", ( k) -> "b");
        h.putAll(h);
        h.addAll(h);
        h.remove("a");
        h.send().toCompletableFuture().get();
        TestUtils.assertException(() -> h.put("a", "b"), AlreadyCompletedException.class);
        TestUtils.assertException(() -> h.put("a", Arrays.asList("b")), AlreadyCompletedException.class);
        TestUtils.assertException(() -> h.add("a", "b"), AlreadyCompletedException.class);
        TestUtils.assertException(() -> h.add("a", Arrays.asList("b")), AlreadyCompletedException.class);
        TestUtils.assertException(() -> h.putIfAbsent("a", "b"), AlreadyCompletedException.class);
        TestUtils.assertException(() -> h.putIfAbsent("a", Arrays.asList("b")), AlreadyCompletedException.class);
        TestUtils.assertException(() -> h.computeIfAbsent("a", ( k) -> Arrays.asList("b")), AlreadyCompletedException.class);
        TestUtils.assertException(() -> h.computeSingleIfAbsent("a", ( k) -> "b"), AlreadyCompletedException.class);
        TestUtils.assertException(() -> h.putAll(h), AlreadyCompletedException.class);
        TestUtils.assertException(() -> h.addAll(h), AlreadyCompletedException.class);
        TestUtils.assertException(() -> h.remove("a"), AlreadyCompletedException.class);
    }

    @Test
    public void beforeSent() throws Exception {
        StringBuffer sb = new StringBuffer();
        HashResponseHeaders h = new HashResponseHeaders(mockBareResponse());
        h.beforeSend(( headers) -> sb.append(("B:" + (h == headers))));
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is(""));
        h.send();
        h.send();
        h.send();
        h.send().toCompletableFuture().get();
        MatcherAssert.assertThat(sb.toString(), CoreMatchers.is("B:true"));
    }

    @Test
    public void headersFiltrationFor204() throws Exception {
        BareResponse bareResponse = mockBareResponse();
        HashResponseHeaders h = new HashResponseHeaders(bareResponse);
        h.put(CONTENT_TYPE, "text/plain");
        h.put("some", "some_value");
        h.put(TRANSFER_ENCODING, "custom");
        h.httpStatus(NO_CONTENT_204);
        h.send().toCompletableFuture().get();
        Mockito.verify(bareResponse).writeStatusAndHeaders(ArgumentMatchers.any(), ArgumentMatchers.argThat(( m) -> ((m.containsKey("some")) && (!(m.containsKey(Http.Header.CONTENT_TYPE)))) && (!(m.containsKey(Http.Header.TRANSFER_ENCODING)))));
    }
}

