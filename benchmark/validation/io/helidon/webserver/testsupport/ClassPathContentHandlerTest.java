/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver.testsupport;


import Http.Header.CONTENT_TYPE;
import Http.Status.MOVED_PERMANENTLY_301;
import Http.Status.NOT_FOUND_404;
import Http.Status.OK_200;
import MediaType.TEXT_PLAIN;
import io.helidon.webserver.Routing;
import io.helidon.webserver.StaticContentSupport;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link io.helidon.webserver.ClassPathContentHandler}.
 */
public class ClassPathContentHandlerTest {
    @Test
    public void resourceSlashAgnostic() throws Exception {
        // Without slash
        Routing routing = Routing.builder().register("/some", StaticContentSupport.create("content")).build();
        // /some/root-a.txt
        TestResponse response = TestClient.create(routing).path("/some/root-a.txt").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
        // With slash
        routing = Routing.builder().register("/some", StaticContentSupport.create("/content")).build();
        // /some/root-a.txt
        response = TestClient.create(routing).path("/some/root-a.txt").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
    }

    @Test
    public void serveFromFiles() throws Exception {
        Routing routing = Routing.builder().register("/some", StaticContentSupport.create("content")).build();
        // /some/root-a.txt
        TestResponse response = TestClient.create(routing).path("/some/root-a.txt").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
        MatcherAssert.assertThat(filterResponse(response), CoreMatchers.is("- root A TXT"));
        MatcherAssert.assertThat(response.headers().first(CONTENT_TYPE), CoreMatchers.is(Optional.of(TEXT_PLAIN.toString())));
        // /some/bar/root-a.txt
        response = TestClient.create(routing).path("/some/bar/root-b.txt").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
        MatcherAssert.assertThat(filterResponse(response), CoreMatchers.is("- root B TXT"));
        MatcherAssert.assertThat(response.headers().first(CONTENT_TYPE), CoreMatchers.is(Optional.of(TEXT_PLAIN.toString())));
        // /some/bar/not.exist
        response = TestClient.create(routing).path("/some/bar/not.exist").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(NOT_FOUND_404));
    }

    @Test
    public void serveFromFilesWithWelcome() throws Exception {
        Routing routing = Routing.builder().register(StaticContentSupport.builder("content").welcomeFileName("index.txt")).build();
        // /
        TestResponse response = TestClient.create(routing).path("/").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
        MatcherAssert.assertThat(filterResponse(response), CoreMatchers.is("- index TXT"));
        MatcherAssert.assertThat(response.headers().first(CONTENT_TYPE), CoreMatchers.is(Optional.of(TEXT_PLAIN.toString())));
        // /bar/
        response = TestClient.create(routing).path("/bar/").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(NOT_FOUND_404));
    }

    @Test
    public void serveFromJar() throws Exception {
        Routing routing = Routing.builder().register("/some", StaticContentSupport.create("s-internal")).build();
        // /some/example-a.txt
        TestResponse response = TestClient.create(routing).path("/some/example-a.txt").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
        MatcherAssert.assertThat(filterResponse(response), CoreMatchers.is("Example A TXT"));
        MatcherAssert.assertThat(response.headers().first(CONTENT_TYPE), CoreMatchers.is(Optional.of(TEXT_PLAIN.toString())));
        // /some/example-a.txt
        response = TestClient.create(routing).path("/some/a/example-a.txt").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
        MatcherAssert.assertThat(filterResponse(response), CoreMatchers.is("A / Example A TXT"));
        MatcherAssert.assertThat(response.headers().first(CONTENT_TYPE), CoreMatchers.is(Optional.of(TEXT_PLAIN.toString())));
        // /some/a/not.exist
        response = TestClient.create(routing).path("/some/a/not.exist").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(NOT_FOUND_404));
    }

    @Test
    public void serveFromJarWithWelcome() throws Exception {
        Routing routing = Routing.builder().register(StaticContentSupport.builder("/s-internal").welcomeFileName("example-a.txt")).build();
        // /
        TestResponse response = TestClient.create(routing).path("/").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
        MatcherAssert.assertThat(filterResponse(response), CoreMatchers.is("Example A TXT"));
        MatcherAssert.assertThat(response.headers().first(CONTENT_TYPE), CoreMatchers.is(Optional.of(TEXT_PLAIN.toString())));
        // /a
        response = TestClient.create(routing).path("/a/").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
        // redirect to /a/
        response = TestClient.create(routing).path("/a").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(MOVED_PERMANENTLY_301));
        MatcherAssert.assertThat(response.headers().first("Location"), CoreMatchers.is(Optional.of("/a/")));
        // another index
        routing = Routing.builder().register(StaticContentSupport.builder("/s-internal").welcomeFileName("example-b.txt")).build();
        // /a/
        response = TestClient.create(routing).path("/a/").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(NOT_FOUND_404));
    }
}

