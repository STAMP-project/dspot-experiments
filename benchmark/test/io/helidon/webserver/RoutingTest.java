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


import Http.Method.GET;
import Http.Method.POST;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link Routing#route(BareRequest, BareResponse)}.
 */
public class RoutingTest {
    @Test
    public void basicRouting() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().post("/user", ( req, resp) -> {
            checker.handlerInvoked("defaultUserHandler");
        }).get("/user/{name}", ( req, resp) -> {
            checker.handlerInvoked("namedUserHandler");
        }).build();
        routing.route(RoutingTest.mockRequest("/user", POST), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("defaultUserHandler"));
        checker.reset();
        routing.route(RoutingTest.mockRequest("/user/john", GET), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("namedUserHandler"));
    }

    @Test
    public void routeFilters() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().any(( req, resp) -> {
            checker.handlerInvoked("anyPath1");
            req.next();
        }, ( req, resp) -> {
            checker.handlerInvoked("anyPath2");
            req.next();
        }).any("/admin", ( req, resp) -> {
            checker.handlerInvoked("anyAdmin");
            req.next();
        }).post("/admin", ( req, resp) -> {
            checker.handlerInvoked("postAdminAudit");
            req.next();
        }).post("/admin/user", ( req, resp) -> {
            checker.handlerInvoked("postAdminUser");
        }).get("/admin/user/{name}", ( req, resp) -> {
            checker.handlerInvoked("getAdminUser");
        }).build();
        routing.route(RoutingTest.mockRequest("/admin/user", POST), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("anyPath1,anyPath2,postAdminUser"));
        checker.reset();
        routing.route(RoutingTest.mockRequest("/admin/user/john", GET), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("anyPath1,anyPath2,getAdminUser"));
        checker.reset();
        routing.route(RoutingTest.mockRequest("/admin", POST), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("anyPath1,anyPath2,anyAdmin,postAdminAudit"));
    }

    @Test
    public void subRouting() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().register("/user", ( rules) -> {
            rules.get("/{name}", ( req, res) -> {
                checker.handlerInvoked("getUser");
            }).post(( req, res) -> {
                checker.handlerInvoked("createUser");
            });
        }).build();
        routing.route(RoutingTest.mockRequest("/user/john", GET), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("getUser"));
        checker.reset();
        routing.route(RoutingTest.mockRequest("/user", POST), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("createUser"));
    }

    static final class RoutingChecker {
        String str = "";

        public void handlerInvoked(String id) {
            str += (str.isEmpty()) ? id : "," + id;
        }

        public void reset() {
            str = "";
        }

        public String handlersInvoked() {
            return str;
        }
    }
}

