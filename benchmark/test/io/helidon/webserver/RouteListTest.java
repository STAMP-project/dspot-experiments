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
package io.helidon.webserver;


import Http.Method;
import Http.Method.DELETE;
import Http.Method.GET;
import Http.Method.OPTIONS;
import Http.Method.POST;
import Http.Method.PUT;
import Http.RequestMethod;
import io.helidon.common.CollectionsHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link RouteList}.
 */
public class RouteListTest {
    private static final Handler VOID_HANDLER = ( req, res) -> {
    };

    @Test
    public void testImmutable1() throws Exception {
        RouteList r = new RouteList(Collections.emptyList());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            r.add(new HandlerRoute(null, RouteListTest.VOID_HANDLER));
        });
    }

    @Test
    public void testImmutable2() throws Exception {
        RouteList r = new RouteList(Collections.emptyList());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            r.remove(new HandlerRoute(null, RouteListTest.VOID_HANDLER));
        });
    }

    @Test
    public void testAcceptMethod() throws Exception {
        Collection<Route> routes = new ArrayList<>();
        routes.add(new HandlerRoute(null, RouteListTest.VOID_HANDLER, Method.POST, Method.PUT));
        routes.add(new HandlerRoute(null, RouteListTest.VOID_HANDLER, Method.POST, Method.DELETE));
        routes.add(new RouteList(CollectionsHelper.listOf(new HandlerRoute(null, RouteListTest.VOID_HANDLER, Method.GET, RequestMethod.create("FOO")))));
        routes.add(new HandlerRoute(null, RouteListTest.VOID_HANDLER, RequestMethod.create("BAR")));
        RouteList r = new RouteList(routes);
        // assertion
        MatcherAssert.assertThat(r.accepts(POST), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.accepts(PUT), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.accepts(DELETE), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.accepts(GET), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.accepts(RequestMethod.create("FOO")), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.accepts(RequestMethod.create("BAR")), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.accepts(OPTIONS), CoreMatchers.is(false));
        MatcherAssert.assertThat(r.acceptedMethods().size(), CoreMatchers.is(6));
    }

    @Test
    public void testAcceptMethodAny() throws Exception {
        Collection<Route> routes = new ArrayList<>();
        routes.add(new HandlerRoute(null, RouteListTest.VOID_HANDLER, Method.POST, Method.PUT));
        routes.add(new HandlerRoute(null, RouteListTest.VOID_HANDLER, Method.POST, Method.DELETE));
        routes.add(new RouteList(CollectionsHelper.listOf(new HandlerRoute(null, RouteListTest.VOID_HANDLER, Method.GET, RequestMethod.create("FOO")), new HandlerRoute(null, RouteListTest.VOID_HANDLER))));
        routes.add(new HandlerRoute(null, RouteListTest.VOID_HANDLER, RequestMethod.create("BAR")));
        RouteList r = new RouteList(routes);
        // assertion
        MatcherAssert.assertThat(r.accepts(POST), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.accepts(PUT), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.accepts(DELETE), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.accepts(GET), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.accepts(RequestMethod.create("FOO")), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.accepts(RequestMethod.create("BAR")), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.accepts(RequestMethod.create("BAZ")), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.accepts(OPTIONS), CoreMatchers.is(true));
        MatcherAssert.assertThat(r.acceptedMethods().size(), CoreMatchers.is(0));
    }
}

