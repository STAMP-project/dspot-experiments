/**
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
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


import Request.Path;
import io.helidon.common.CollectionsHelper;
import java.net.URI;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests {@link Request} and {@link Request.Path}.
 */
public class RequestTest {
    @Test
    public void createPathTest() throws Exception {
        Request.Path path = Path.create(null, "/foo/bar/baz", CollectionsHelper.mapOf("a", "va", "b", "vb", "var", "1"));
        MatcherAssert.assertThat(path.toString(), Is.is("/foo/bar/baz"));
        MatcherAssert.assertThat(path.absolute().toString(), Is.is("/foo/bar/baz"));
        MatcherAssert.assertThat("va", Is.is(path.param("a")));
        MatcherAssert.assertThat("vb", Is.is(path.param("b")));
        MatcherAssert.assertThat("1", Is.is(path.param("var")));
        MatcherAssert.assertThat(path.segments(), contains("foo", "bar", "baz"));
        // Sub path
        path = Path.create(path, "/bar/baz", CollectionsHelper.mapOf("c", "vc", "var", "2"));
        MatcherAssert.assertThat(path.toString(), Is.is("/bar/baz"));
        MatcherAssert.assertThat(path.absolute().toString(), Is.is("/foo/bar/baz"));
        MatcherAssert.assertThat("vc", Is.is(path.param("c")));
        MatcherAssert.assertThat("2", Is.is(path.param("var")));
        MatcherAssert.assertThat(path.param("a"), CoreMatchers.nullValue());
        MatcherAssert.assertThat("va", Is.is(path.absolute().param("a")));
        MatcherAssert.assertThat("vb", Is.is(path.absolute().param("b")));
        MatcherAssert.assertThat("2", Is.is(path.absolute().param("var")));
        // Sub Sub Path
        path = Path.create(path, "/baz", CollectionsHelper.mapOf("d", "vd", "a", "a2"));
        MatcherAssert.assertThat(path.toString(), Is.is("/baz"));
        MatcherAssert.assertThat(path.absolute().toString(), Is.is("/foo/bar/baz"));
        MatcherAssert.assertThat("vd", Is.is(path.param("d")));
        MatcherAssert.assertThat("a2", Is.is(path.param("a")));
        MatcherAssert.assertThat(path.param("c"), CoreMatchers.nullValue());
        MatcherAssert.assertThat("a2", Is.is(path.absolute().param("a")));
        MatcherAssert.assertThat("vb", Is.is(path.absolute().param("b")));
        MatcherAssert.assertThat("vc", Is.is(path.absolute().param("c")));
        MatcherAssert.assertThat("2", Is.is(path.absolute().param("var")));
    }

    @Test
    public void queryEncodingTest() throws Exception {
        BareRequest mock = Mockito.mock(BareRequest.class);
        Mockito.when(mock.uri()).thenReturn(new URI("http://localhost:123/one/two?a=b%26c=d&e=f&e=g&h=x%63%23e%3c#a%20frag%23ment"));
        Request request = new RequestTestStub(mock, Mockito.mock(WebServer.class));
        MatcherAssert.assertThat("The query string must remain encoded otherwise no-one could tell whether a '&' was really a '&' or '%26'", request.query(), Is.is("a=b%26c=d&e=f&e=g&h=x%63%23e%3c"));
        MatcherAssert.assertThat(request.fragment(), Is.is("a frag#ment"));
        MatcherAssert.assertThat(request.queryParams().toMap(), hasEntry(Is.is("e"), IsCollectionContaining.hasItems("f", "g")));
        MatcherAssert.assertThat(request.queryParams().toMap(), hasEntry(Is.is("h"), IsCollectionContaining.hasItem("xc#e<")));
        MatcherAssert.assertThat(request.queryParams().toMap(), hasEntry(Is.is("a"), IsCollectionContaining.hasItem("b&c=d")));
    }
}

