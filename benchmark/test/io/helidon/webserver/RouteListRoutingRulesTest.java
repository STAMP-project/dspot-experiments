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


import PathMatcher.PrefixResult;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link RouteListRoutingRules}.
 */
public class RouteListRoutingRulesTest {
    private static final Handler VOID_HANDLER = ( req, res) -> {
    };

    @Test
    public void simpleRouting() throws Exception {
        RouteList routes = new RouteListRoutingRules().get(RouteListRoutingRulesTest.VOID_HANDLER).get("/foo", RouteListRoutingRulesTest.VOID_HANDLER).get(PathPattern.compile("/bar"), RouteListRoutingRulesTest.VOID_HANDLER).post(RouteListRoutingRulesTest.VOID_HANDLER).post("/foo", RouteListRoutingRulesTest.VOID_HANDLER).post(PathPattern.compile("/bar"), RouteListRoutingRulesTest.VOID_HANDLER).delete(RouteListRoutingRulesTest.VOID_HANDLER).delete("/foo", RouteListRoutingRulesTest.VOID_HANDLER).delete(PathPattern.compile("/bar"), RouteListRoutingRulesTest.VOID_HANDLER).put(RouteListRoutingRulesTest.VOID_HANDLER).put("/foo", RouteListRoutingRulesTest.VOID_HANDLER).put(PathPattern.compile("/bar"), RouteListRoutingRulesTest.VOID_HANDLER).trace(RouteListRoutingRulesTest.VOID_HANDLER).trace("/foo", RouteListRoutingRulesTest.VOID_HANDLER).trace(PathPattern.compile("/bar"), RouteListRoutingRulesTest.VOID_HANDLER).options(RouteListRoutingRulesTest.VOID_HANDLER).options("/foo", RouteListRoutingRulesTest.VOID_HANDLER).options(PathPattern.compile("/bar"), RouteListRoutingRulesTest.VOID_HANDLER).head(RouteListRoutingRulesTest.VOID_HANDLER).head("/foo", RouteListRoutingRulesTest.VOID_HANDLER).head(PathPattern.compile("/bar"), RouteListRoutingRulesTest.VOID_HANDLER).aggregate().routeList();
        MatcherAssert.assertThat(routes, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(routes.size(), CoreMatchers.is((7 * 3)));
        MatcherAssert.assertThat(routes.acceptedMethods().size(), CoreMatchers.is(7));
        MatcherAssert.assertThat(routes.get(0).accepts(GET), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(0).accepts(POST), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(1).accepts(GET), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(1).accepts(PUT), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(2).accepts(GET), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(2).accepts(DELETE), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(3).accepts(POST), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(3).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(4).accepts(POST), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(4).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(5).accepts(POST), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(5).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(6).accepts(DELETE), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(6).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(7).accepts(DELETE), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(7).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(8).accepts(DELETE), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(8).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(9).accepts(PUT), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(9).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(10).accepts(PUT), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(10).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(11).accepts(PUT), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(11).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(12).accepts(TRACE), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(12).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(13).accepts(TRACE), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(13).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(14).accepts(TRACE), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(14).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(15).accepts(OPTIONS), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(15).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(16).accepts(OPTIONS), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(16).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(17).accepts(OPTIONS), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(17).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(18).accepts(HEAD), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(18).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(19).accepts(HEAD), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(19).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(20).accepts(HEAD), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(20).accepts(GET), CoreMatchers.is(false));
        PathMatcher.PrefixResult result = routes.prefixMatch("/any");
        MatcherAssert.assertThat(result, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(result.matches(), CoreMatchers.is(true));
        MatcherAssert.assertThat(result.remainingPart(), CoreMatchers.is("/any"));
    }

    @Test
    public void anyRouting() throws Exception {
        RouteList routes = new RouteListRoutingRules().get(RouteListRoutingRulesTest.VOID_HANDLER).any("/foo", RouteListRoutingRulesTest.VOID_HANDLER).delete("/foo", RouteListRoutingRulesTest.VOID_HANDLER).post("/bar", RouteListRoutingRulesTest.VOID_HANDLER).post("/bar", ( req, res) -> {
        }, ( req, res) -> {
        }).aggregate().routeList();
        MatcherAssert.assertThat(routes, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(routes.size(), CoreMatchers.is(6));
        MatcherAssert.assertThat(routes.acceptedMethods().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(routes.get(0).accepts(GET), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(0).accepts(POST), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(1).accepts(POST), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(1).accepts(GET), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(2).accepts(DELETE), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(3).accepts(POST), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(4).accepts(POST), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(5).accepts(POST), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void nestedRouting() throws Exception {
        RouteList routes = new RouteListRoutingRules().post("/foo", RouteListRoutingRulesTest.VOID_HANDLER).register(( c) -> c.get(VOID_HANDLER).post("/foo", VOID_HANDLER)).delete("/foo", RouteListRoutingRulesTest.VOID_HANDLER).register("/bar", ( c) -> c.delete(VOID_HANDLER).put(VOID_HANDLER)).aggregate().routeList();
        MatcherAssert.assertThat(routes, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(routes.size(), CoreMatchers.is(5));
        MatcherAssert.assertThat(routes.acceptedMethods().size(), CoreMatchers.is(4));
        MatcherAssert.assertThat(routes.get(0).accepts(POST), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(0).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(1).accepts(GET), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(1).accepts(POST), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(2).accepts(POST), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(2).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(routes.get(3).accepts(DELETE), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(3).accepts(GET), CoreMatchers.is(false));
        MatcherAssert.assertThat(((routes.get(4)) instanceof RouteList), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(4).accepts(DELETE), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(4).accepts(PUT), CoreMatchers.is(true));
        MatcherAssert.assertThat(routes.get(4).accepts(GET), CoreMatchers.is(false));
    }

    @Test
    public void emptyNestedRouting() throws Exception {
        RouteList routes = new RouteListRoutingRules().get(RouteListRoutingRulesTest.VOID_HANDLER).register("/foo", ( c) -> {
        }).post("/bar", RouteListRoutingRulesTest.VOID_HANDLER).aggregate().routeList();
        MatcherAssert.assertThat(routes, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(routes.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(routes.acceptedMethods().size(), CoreMatchers.is(2));
    }
}

