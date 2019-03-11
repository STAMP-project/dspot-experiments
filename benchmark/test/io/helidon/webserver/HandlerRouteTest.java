/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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


import Http.Method;
import Http.Method.DELETE;
import Http.Method.GET;
import Http.Method.POST;
import Http.Method.PUT;
import Http.RequestMethod;
import PathMatcher.Result;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Tests a {@link HandlerRoute}.
 */
public class HandlerRouteTest {
    private static final Handler VOID_HANDLER = ( req, res) -> {
    };

    @Test
    public void standardMethodRouting() throws Exception {
        HandlerRoute rr = new HandlerRoute(null, HandlerRouteTest.VOID_HANDLER, Method.POST, Method.PUT);
        MatcherAssert.assertThat(rr.accepts(POST), CoreMatchers.is(true));
        MatcherAssert.assertThat(rr.accepts(PUT), CoreMatchers.is(true));
        MatcherAssert.assertThat(rr.accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(rr.accepts(DELETE), CoreMatchers.is(false));
        MatcherAssert.assertThat(rr.accepts(RequestMethod.create("FOO")), CoreMatchers.is(false));
        MatcherAssert.assertThat(rr.acceptedMethods().size(), CoreMatchers.is(2));
    }

    @Test
    public void specialMethodRouting() throws Exception {
        HandlerRoute rr = new HandlerRoute(null, HandlerRouteTest.VOID_HANDLER, RequestMethod.create("FOO"));
        MatcherAssert.assertThat(rr.accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(rr.accepts(POST), CoreMatchers.is(false));
        MatcherAssert.assertThat(rr.accepts(RequestMethod.create("FOO")), CoreMatchers.is(true));
        MatcherAssert.assertThat(rr.accepts(RequestMethod.create("BAR")), CoreMatchers.is(false));
        MatcherAssert.assertThat(rr.acceptedMethods().size(), CoreMatchers.is(1));
    }

    @Test
    public void combinedMethodRouting() throws Exception {
        HandlerRoute rr = new HandlerRoute(null, HandlerRouteTest.VOID_HANDLER, Method.POST, RequestMethod.create("FOO"), Method.PUT);
        MatcherAssert.assertThat(rr.accepts(POST), CoreMatchers.is(true));
        MatcherAssert.assertThat(rr.accepts(PUT), CoreMatchers.is(true));
        MatcherAssert.assertThat(rr.accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(rr.accepts(DELETE), CoreMatchers.is(false));
        MatcherAssert.assertThat(rr.accepts(RequestMethod.create("FOO")), CoreMatchers.is(true));
        MatcherAssert.assertThat(rr.accepts(RequestMethod.create("BAR")), CoreMatchers.is(false));
        MatcherAssert.assertThat(rr.acceptedMethods().size(), CoreMatchers.is(3));
    }

    @Test
    public void anyMethodRouting() throws Exception {
        HandlerRoute rr = new HandlerRoute(null, HandlerRouteTest.VOID_HANDLER);
        MatcherAssert.assertThat(rr.accepts(POST), CoreMatchers.is(true));
        MatcherAssert.assertThat(rr.accepts(GET), CoreMatchers.is(true));
        MatcherAssert.assertThat(rr.accepts(RequestMethod.create("FOO")), CoreMatchers.is(true));
        MatcherAssert.assertThat(rr.accepts(RequestMethod.create("BAR")), CoreMatchers.is(true));
        MatcherAssert.assertThat(rr.acceptedMethods().size(), CoreMatchers.is(0));
    }

    @Test
    public void anyPathMatcher() throws Exception {
        HandlerRoute rr = new HandlerRoute(null, HandlerRouteTest.VOID_HANDLER);
        final String path = "/foo";
        PathMatcher.Result result = rr.match(path);
        MatcherAssert.assertThat(result.matches(), CoreMatchers.is(true));
        MatcherAssert.assertThat(result.params().isEmpty(), CoreMatchers.is(true));
    }
}

