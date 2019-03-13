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
package org.springframework.web.reactive.function.server;


import HttpMethod.GET;
import HttpMethod.POST;
import MediaType.APPLICATION_JSON;
import MediaType.TEXT_PLAIN;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class ToStringVisitorTests {
    @Test
    public void nested() {
        HandlerFunction<ServerResponse> handler = new ToStringVisitorTests.SimpleHandlerFunction();
        RouterFunction<ServerResponse> routerFunction = RouterFunctions.route().path("/foo", ( builder) -> {
            builder.path("/bar", () -> route().GET("/baz", handler).build());
        }).build();
        ToStringVisitor visitor = new ToStringVisitor();
        routerFunction.accept(visitor);
        String result = visitor.toString();
        String expected = "/foo => {\n" + (((" /bar => {\n" + "  (GET && /baz) -> \n") + " }\n") + "}");
        Assert.assertEquals(expected, result);
    }

    @Test
    public void predicates() {
        testPredicate(RequestPredicates.methods(GET), "GET");
        testPredicate(RequestPredicates.methods(GET, POST), "[GET, POST]");
        testPredicate(RequestPredicates.path("/foo"), "/foo");
        testPredicate(RequestPredicates.pathExtension("foo"), "*.foo");
        testPredicate(RequestPredicates.contentType(APPLICATION_JSON), "Content-Type: application/json");
        testPredicate(RequestPredicates.contentType(APPLICATION_JSON, TEXT_PLAIN), "Content-Type: [application/json, text/plain]");
        testPredicate(RequestPredicates.accept(APPLICATION_JSON), "Accept: application/json");
        testPredicate(RequestPredicates.queryParam("foo", "bar"), "?foo == bar");
        testPredicate(RequestPredicates.method(GET).and(RequestPredicates.path("/foo")), "(GET && /foo)");
        testPredicate(RequestPredicates.method(GET).or(RequestPredicates.path("/foo")), "(GET || /foo)");
        testPredicate(RequestPredicates.method(GET).negate(), "!(GET)");
        testPredicate(RequestPredicates.GET("/foo").or(RequestPredicates.contentType(TEXT_PLAIN)).and(RequestPredicates.accept(APPLICATION_JSON).negate()), "(((GET && /foo) || Content-Type: text/plain) && !(Accept: application/json))");
    }

    private static class SimpleHandlerFunction implements HandlerFunction<ServerResponse> {
        @Override
        public Mono<ServerResponse> handle(ServerRequest request) {
            return ServerResponse.ok().build();
        }

        @Override
        public String toString() {
            return "";
        }
    }
}

