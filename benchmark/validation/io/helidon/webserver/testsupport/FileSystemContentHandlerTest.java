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
import Http.Header.LOCATION;
import Http.Status.MOVED_PERMANENTLY_301;
import Http.Status.NOT_FOUND_404;
import Http.Status.OK_200;
import MediaType.TEXT_HTML;
import MediaType.TEXT_PLAIN;
import io.helidon.webserver.Routing;
import io.helidon.webserver.StaticContentSupport;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


/**
 * Tests {@link io.helidon.webserver.FileSystemContentHandler}.
 */
@ExtendWith(TemporaryFolderExtension.class)
public class FileSystemContentHandlerTest {
    private TemporaryFolder folder;

    @Test
    public void serveFile() throws Exception {
        try {
            Routing routing = Routing.builder().register("/some", StaticContentSupport.create(folder.root().toPath())).build();
            // /some/foo.txt
            TestResponse response = TestClient.create(routing).path("/some/foo.txt").get();
            MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
            MatcherAssert.assertThat(FileSystemContentHandlerTest.responseToString(response), CoreMatchers.is("Foo TXT"));
            MatcherAssert.assertThat(response.headers().first(CONTENT_TYPE).orElse(null), CoreMatchers.is(TEXT_PLAIN.toString()));
            // /some/css/b.css
            response = TestClient.create(routing).path("/some/css/b.css").get();
            MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
            MatcherAssert.assertThat(FileSystemContentHandlerTest.responseToString(response), CoreMatchers.is("B CSS"));
            // /some/css/not.exists
            response = TestClient.create(routing).path("/some/css/not.exists").get();
            MatcherAssert.assertThat(response.status(), CoreMatchers.is(NOT_FOUND_404));
            // /some/css
            response = TestClient.create(routing).path("/some/css").get();
            MatcherAssert.assertThat(response.status(), CoreMatchers.is(MOVED_PERMANENTLY_301));
            MatcherAssert.assertThat(response.headers().first(LOCATION).orElse(null), CoreMatchers.is("/some/css/"));
            // /some/css/
            response = TestClient.create(routing).path("/some/css/").get();
            MatcherAssert.assertThat(response.status(), CoreMatchers.is(NOT_FOUND_404));
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void serveIndex() throws Exception {
        Routing routing = Routing.builder().register(StaticContentSupport.builder(folder.root().toPath()).welcomeFileName("index.html").contentType("css", TEXT_PLAIN).build()).build();
        // /
        TestResponse response = TestClient.create(routing).path("/").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
        MatcherAssert.assertThat(FileSystemContentHandlerTest.responseToString(response), CoreMatchers.is("Index HTML"));
        MatcherAssert.assertThat(response.headers().first(CONTENT_TYPE).orElse(null), CoreMatchers.is(TEXT_HTML.toString()));
        // /other
        response = TestClient.create(routing).path("/other").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(MOVED_PERMANENTLY_301));
        MatcherAssert.assertThat(response.headers().first(LOCATION).orElse(null), CoreMatchers.is("/other/"));
        // /other/
        response = TestClient.create(routing).path("/other/").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
        MatcherAssert.assertThat(FileSystemContentHandlerTest.responseToString(response), CoreMatchers.is("Index HTML"));
        MatcherAssert.assertThat(response.headers().first(CONTENT_TYPE).orElse(null), CoreMatchers.is(TEXT_HTML.toString()));
        // /css/
        response = TestClient.create(routing).path("/css/").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(NOT_FOUND_404));
        // /css/a.css
        response = TestClient.create(routing).path("/css/a.css").get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
        MatcherAssert.assertThat(FileSystemContentHandlerTest.responseToString(response), CoreMatchers.is("A CSS"));
        MatcherAssert.assertThat(response.headers().first(CONTENT_TYPE).orElse(null), CoreMatchers.is(TEXT_PLAIN.toString()));
    }
}

