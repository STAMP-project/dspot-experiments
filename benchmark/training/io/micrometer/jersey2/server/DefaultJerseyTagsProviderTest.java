/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.jersey2.server;


import io.micrometer.core.instrument.Tag;
import javax.ws.rs.NotAcceptableException;
import org.junit.Test;


/**
 * Tests for {@link DefaultJerseyTagsProvider}.
 *
 * @author Michael Weirauch
 * @author Johnny Lim
 */
public class DefaultJerseyTagsProviderTest {
    private final DefaultJerseyTagsProvider tagsProvider = new DefaultJerseyTagsProvider();

    @Test
    public void testRootPath() {
        assertThat(tagsProvider.httpRequestTags(DefaultJerseyTagsProviderTest.event(200, null, "/", ((String[]) (null))))).containsExactlyInAnyOrder(DefaultJerseyTagsProviderTest.tagsFrom("root", 200, null, "SUCCESS"));
    }

    @Test
    public void templatedPathsAreReturned() {
        assertThat(tagsProvider.httpRequestTags(DefaultJerseyTagsProviderTest.event(200, null, "/", "/", "/hello/{name}"))).containsExactlyInAnyOrder(DefaultJerseyTagsProviderTest.tagsFrom("/hello/{name}", 200, null, "SUCCESS"));
    }

    @Test
    public void applicationPathIsPresent() {
        assertThat(tagsProvider.httpRequestTags(DefaultJerseyTagsProviderTest.event(200, null, "/app", "/", "/hello"))).containsExactlyInAnyOrder(DefaultJerseyTagsProviderTest.tagsFrom("/app/hello", 200, null, "SUCCESS"));
    }

    @Test
    public void notFoundsAreShunted() {
        assertThat(tagsProvider.httpRequestTags(DefaultJerseyTagsProviderTest.event(404, null, "/app", "/", "/not-found"))).containsExactlyInAnyOrder(DefaultJerseyTagsProviderTest.tagsFrom("NOT_FOUND", 404, null, "CLIENT_ERROR"));
    }

    @Test
    public void redirectsAreShunted() {
        assertThat(tagsProvider.httpRequestTags(DefaultJerseyTagsProviderTest.event(301, null, "/app", "/", "/redirect301"))).containsExactlyInAnyOrder(DefaultJerseyTagsProviderTest.tagsFrom("REDIRECTION", 301, null, "REDIRECTION"));
        assertThat(tagsProvider.httpRequestTags(DefaultJerseyTagsProviderTest.event(302, null, "/app", "/", "/redirect302"))).containsExactlyInAnyOrder(DefaultJerseyTagsProviderTest.tagsFrom("REDIRECTION", 302, null, "REDIRECTION"));
        assertThat(tagsProvider.httpRequestTags(DefaultJerseyTagsProviderTest.event(399, null, "/app", "/", "/redirect399"))).containsExactlyInAnyOrder(DefaultJerseyTagsProviderTest.tagsFrom("REDIRECTION", 399, null, "REDIRECTION"));
    }

    @Test
    @SuppressWarnings("serial")
    public void exceptionsAreMappedCorrectly() {
        assertThat(tagsProvider.httpRequestTags(DefaultJerseyTagsProviderTest.event(500, new IllegalArgumentException(), "/app", ((String[]) (null))))).containsExactlyInAnyOrder(DefaultJerseyTagsProviderTest.tagsFrom("/app", 500, "IllegalArgumentException", "SERVER_ERROR"));
        assertThat(tagsProvider.httpRequestTags(DefaultJerseyTagsProviderTest.event(500, new IllegalArgumentException(new NullPointerException()), "/app", ((String[]) (null))))).containsExactlyInAnyOrder(DefaultJerseyTagsProviderTest.tagsFrom("/app", 500, "NullPointerException", "SERVER_ERROR"));
        assertThat(tagsProvider.httpRequestTags(DefaultJerseyTagsProviderTest.event(406, new NotAcceptableException(), "/app", ((String[]) (null))))).containsExactlyInAnyOrder(DefaultJerseyTagsProviderTest.tagsFrom("/app", 406, "NotAcceptableException", "CLIENT_ERROR"));
        assertThat(tagsProvider.httpRequestTags(DefaultJerseyTagsProviderTest.event(500, new Exception("anonymous") {}, "/app", ((String[]) (null))))).containsExactlyInAnyOrder(DefaultJerseyTagsProviderTest.tagsFrom("/app", 500, "io.micrometer.jersey2.server.DefaultJerseyTagsProviderTest$1", "SERVER_ERROR"));
    }

    @Test
    public void longRequestTags() {
        assertThat(tagsProvider.httpLongRequestTags(DefaultJerseyTagsProviderTest.event(0, null, "/app", ((String[]) (null))))).containsExactlyInAnyOrder(Tag.of("method", "GET"), Tag.of("uri", "/app"));
    }
}

