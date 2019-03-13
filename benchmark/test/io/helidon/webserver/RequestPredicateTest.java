/**
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
import Http.Method.PUT;
import MediaType.APPLICATION_JSON;
import MediaType.TEXT_PLAIN;
import io.helidon.common.http.Http;
import io.helidon.common.http.MediaType;
import java.util.function.Predicate;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link RequestPredicate}.
 */
public class RequestPredicateTest {
    @Test
    public void isOfMethod1() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().any("/getOrPost", RequestPredicate.create().isOfMethod("GET", "POST").thenApply(( req, resp) -> {
            checker.handlerInvoked("methodFound");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("methodNotFound");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().isOfMethod(((String[]) (null)));
        });
        routing.route(RequestPredicateTest.mockRequest("/getOrPost"), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("methodFound"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/getOrPost", PUT), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("methodNotFound"));
    }

    @Test
    public void isOfMethod2() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().any("/getOrPost", RequestPredicate.create().isOfMethod(GET, POST).thenApply(( req, resp) -> {
            checker.handlerInvoked("methodFound");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("methodNotFound");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().isOfMethod(((Http[]) (null)));
        });
        routing.route(RequestPredicateTest.mockRequest("/getOrPost"), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("methodFound"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/getOrPost", PUT), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("methodNotFound"));
    }

    @Test
    public void containsHeader() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/exists", RequestPredicate.create().containsHeader("my-header").thenApply(( req, resp) -> {
            checker.handlerInvoked("headerFound");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("headerNotFound");
        })).get("/valid", RequestPredicate.create().containsHeader("my-header", "abc"::equals).thenApply(( req, resp) -> {
            checker.handlerInvoked("headerIsValid");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("headerIsNotValid");
        })).get("/equals", RequestPredicate.create().containsHeader("my-header", "abc").thenApply(( req, resp) -> {
            checker.handlerInvoked("headerIsEqual");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("headerIsNotEqual");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsHeader(null);
        });
        routing.route(RequestPredicateTest.mockRequest("/exists", mapOf("my-header", listOf("abc"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("headerFound"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/exists", mapOf()), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("headerNotFound"));
    }

    @Test
    public void containsValidHeader() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/valid", RequestPredicate.create().containsHeader("my-header", "abc"::equals).thenApply(( req, resp) -> {
            checker.handlerInvoked("headerIsValid");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("headerIsNotValid");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsHeader("my-header", ((Predicate<String>) (null)));
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsHeader(null, "abc"::equals);
        });
        routing.route(RequestPredicateTest.mockRequest("/valid", mapOf("my-header", listOf("abc"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("headerIsValid"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/valid", mapOf("my-header", listOf("def"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("headerIsNotValid"));
    }

    @Test
    public void containsExactHeader() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/equals", RequestPredicate.create().containsHeader("my-header", "abc").thenApply(( req, resp) -> {
            checker.handlerInvoked("headerIsEqual");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("headerIsNotEqual");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsHeader("my-header", ((String) (null)));
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsHeader(null, "abc");
        });
        routing.route(RequestPredicateTest.mockRequest("/equals", mapOf("my-header", listOf("abc"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("headerIsEqual"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/equals", mapOf("my-header", listOf("def"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("headerIsNotEqual"));
    }

    @Test
    public void containsQueryParam() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/exists", RequestPredicate.create().containsQueryParameter("my-param").thenApply(( req, resp) -> {
            checker.handlerInvoked("queryParamFound");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("queryParamNotFound");
        })).get("/valid", RequestPredicate.create().containsQueryParameter("my-param", "abc"::equals).thenApply(( req, resp) -> {
            checker.handlerInvoked("queryParamIsValid");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("queryParamIsNotValid");
        })).get("/equals", RequestPredicate.create().containsQueryParameter("my-param", "abc").thenApply(( req, resp) -> {
            checker.handlerInvoked("queryParamIsEqual");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("queryParamIsNotEqual");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsQueryParameter(null);
        });
        routing.route(RequestPredicateTest.mockRequest("/exists?my-param=abc"), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("queryParamFound"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/exists"), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("queryParamNotFound"));
    }

    @Test
    public void containsValidQueryParam() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/valid", RequestPredicate.create().containsQueryParameter("my-param", "abc"::equals).thenApply(( req, resp) -> {
            checker.handlerInvoked("queryParamIsValid");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("queryParamIsNotValid");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsQueryParameter("my-param", ((Predicate<String>) (null)));
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsQueryParameter(null, "abc");
        });
        routing.route(RequestPredicateTest.mockRequest("/valid?my-param=abc"), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("queryParamIsValid"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/valid?my-param=def"), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("queryParamIsNotValid"));
    }

    @Test
    public void containsExactQueryParam() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/equals", RequestPredicate.create().containsQueryParameter("my-param", "abc").thenApply(( req, resp) -> {
            checker.handlerInvoked("queryParamIsEqual");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("queryParamIsNotEqual");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsQueryParameter("my-param", ((String) (null)));
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsQueryParameter(null, "abc");
        });
        routing.route(RequestPredicateTest.mockRequest("/equals?my-param=abc"), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("queryParamIsEqual"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/equals?my-param=def"), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("queryParamIsNotEqual"));
    }

    @Test
    public void containsCookie() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/exists", RequestPredicate.create().containsCookie("my-cookie").thenApply(( req, resp) -> {
            checker.handlerInvoked("cookieFound");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("cookieNotFound");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsCookie(null);
        });
        routing.route(RequestPredicateTest.mockRequest("/exists", mapOf("cookie", listOf("my-cookie=abc"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("cookieFound"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/exists", mapOf("cookie", listOf("other-cookie=abc"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("cookieNotFound"));
    }

    @Test
    public void containsValidCookie() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/valid", RequestPredicate.create().containsCookie("my-cookie", "abc"::equals).thenApply(( req, resp) -> {
            checker.handlerInvoked("cookieIsValid");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("cookieIsNotValid");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsCookie("my-cookie", ((Predicate<String>) (null)));
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsCookie(null, "abc");
        });
        routing.route(RequestPredicateTest.mockRequest("/valid", mapOf("cookie", listOf("my-cookie=abc"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("cookieIsValid"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/valid", mapOf("cookie", listOf("my-cookie=def"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("cookieIsNotValid"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/valid", mapOf("cookie", listOf("my-cookie="))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("cookieIsNotValid"));
    }

    @Test
    public void containsExactCookie() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/equals", RequestPredicate.create().containsCookie("my-cookie", "abc").thenApply(( req, resp) -> {
            checker.handlerInvoked("cookieIsEqual");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("cookieIsNotEqual");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsCookie("my-cookie", ((String) (null)));
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().containsCookie(null, "abc");
        });
        routing.route(RequestPredicateTest.mockRequest("/equals", mapOf("cookie", listOf("my-cookie=abc"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("cookieIsEqual"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/equals", mapOf("cookie", listOf("my-cookie=def"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("cookieIsNotEqual"));
    }

    @Test
    public void accepts1() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/accepts1", RequestPredicate.create().accepts("text/plain", "application/json").thenApply(( req, resp) -> {
            checker.handlerInvoked("acceptsMediaType");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("doesNotAcceptMediaType");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().accepts(((String[]) (null)));
        });
        routing.route(RequestPredicateTest.mockRequest("/accepts1", mapOf("Accept", listOf("application/json"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("acceptsMediaType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/accepts1", mapOf("Accept", listOf("text/plain"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("acceptsMediaType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/accepts1", mapOf("Accept", listOf("text/plain", "application/xml"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("acceptsMediaType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/accepts1", mapOf("Accept", listOf())), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("acceptsMediaType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/accepts1", mapOf()), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("acceptsMediaType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/accepts1", mapOf("Accept", listOf("application/xml"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("doesNotAcceptMediaType"));
    }

    @Test
    public void accepts2() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/accepts2", RequestPredicate.create().accepts(TEXT_PLAIN, APPLICATION_JSON).thenApply(( req, resp) -> {
            checker.handlerInvoked("acceptsMediaType");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("doesNotAcceptMediaType");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().accepts(((MediaType[]) (null)));
        });
        routing.route(RequestPredicateTest.mockRequest("/accepts2", mapOf("Accept", listOf("application/json"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("acceptsMediaType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/accepts2", mapOf("Accept", listOf("text/plain"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("acceptsMediaType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/accepts2", mapOf("Accept", listOf("text/plain", "application/xml"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("acceptsMediaType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/accepts2", mapOf("Accept", listOf())), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("acceptsMediaType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/accepts2", mapOf()), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("acceptsMediaType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/accepts2", mapOf("Accept", listOf("application/xml"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("doesNotAcceptMediaType"));
    }

    @Test
    public void hasContentType1() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/contentType1", RequestPredicate.create().hasContentType("text/plain", "application/json").thenApply(( req, resp) -> {
            checker.handlerInvoked("hasContentType");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("doesNotHaveContentType");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().hasContentType(((String[]) (null)));
        });
        routing.route(RequestPredicateTest.mockRequest("/contentType1", mapOf("Content-Type", listOf("text/plain"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("hasContentType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/contentType1", mapOf("Content-Type", listOf("text/plain"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("hasContentType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/contentType1", mapOf("Content-Type", listOf("application/json"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("hasContentType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/contentType1", mapOf("Content-Type", listOf("application/xml"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("doesNotHaveContentType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/contentType1", mapOf("Content-Type", listOf())), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("doesNotHaveContentType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/contentType1", mapOf()), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("doesNotHaveContentType"));
    }

    @Test
    public void hasContentType2() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/contentType2", RequestPredicate.create().hasContentType(TEXT_PLAIN, APPLICATION_JSON).thenApply(( req, resp) -> {
            checker.handlerInvoked("hasContentType");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("doesNotHaveContentType");
        })).build();
        Assertions.assertThrows(NullPointerException.class, () -> {
            RequestPredicate.create().hasContentType(((MediaType[]) (null)));
        });
        routing.route(RequestPredicateTest.mockRequest("/contentType2", mapOf("Content-Type", listOf("text/plain"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("hasContentType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/contentType2", mapOf("Content-Type", listOf("text/plain"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("hasContentType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/contentType2", mapOf("Content-Type", listOf("application/json"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("hasContentType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/contentType2", mapOf("Content-Type", listOf("application/xml"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("doesNotHaveContentType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/contentType2", mapOf("Content-Type", listOf())), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("doesNotHaveContentType"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/contentType2", mapOf()), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("doesNotHaveContentType"));
    }

    @Test
    public void multipleConditions() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().any("/multiple", RequestPredicate.create().accepts(TEXT_PLAIN).hasContentType(TEXT_PLAIN).containsQueryParameter("my-param").containsCookie("my-cookie").isOfMethod(GET).thenApply(( req, resp) -> {
            checker.handlerInvoked("hasAllConditions");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("doesNotHaveAllConditions");
        })).build();
        routing.route(RequestPredicateTest.mockRequest("/multiple?my-param=abc", mapOf("Content-Type", listOf("text/plain"), "Accept", listOf("text/plain"), "Cookie", listOf("my-cookie=abc"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("hasAllConditions"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/multiple?my-param=abc", mapOf("Accept", listOf("text/plain"), "Cookie", listOf("my-cookie=abc"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("doesNotHaveAllConditions"));
    }

    @Test
    public void and() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/and", RequestPredicate.create().accepts(TEXT_PLAIN).and(( req) -> req.headers().first("my-header").isPresent()).thenApply(( req, resp) -> {
            checker.handlerInvoked("hasAllConditions");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("doesNotHaveAllConditions");
        })).build();
        routing.route(RequestPredicateTest.mockRequest("/and", mapOf("Accept", listOf("text/plain"), "my-header", listOf("abc"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("hasAllConditions"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/and", mapOf("Accept", listOf("text/plain"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("doesNotHaveAllConditions"));
    }

    @Test
    public void or() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/or", RequestPredicate.create().hasContentType(TEXT_PLAIN).or(( req) -> req.headers().first("my-header").isPresent()).thenApply(( req, resp) -> {
            checker.handlerInvoked("hasAnyCondition");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("doesNotHaveAnyCondition");
        })).build();
        routing.route(RequestPredicateTest.mockRequest("/or", mapOf("Content-Type", listOf("text/plain"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("hasAnyCondition"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/or", mapOf("my-header", listOf("abc"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("hasAnyCondition"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/or", mapOf()), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("doesNotHaveAnyCondition"));
    }

    @Test
    public void negate() {
        final RoutingTest.RoutingChecker checker = new RoutingTest.RoutingChecker();
        Routing routing = Routing.builder().get("/negate", RequestPredicate.create().hasContentType(TEXT_PLAIN).containsCookie("my-cookie").negate().thenApply(( req, resp) -> {
            checker.handlerInvoked("hasAllConditions");
        }).otherwise(( req, res) -> {
            checker.handlerInvoked("doesNotHaveAllConditions");
        })).build();
        routing.route(RequestPredicateTest.mockRequest("/negate", mapOf("Content-Type", listOf("application/json"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("hasAllConditions"));
        checker.reset();
        routing.route(RequestPredicateTest.mockRequest("/negate", mapOf("Content-Type", listOf("text/plain"), "Cookie", listOf("my-cookie=abc"))), RoutingTest.mockResponse());
        MatcherAssert.assertThat(checker.handlersInvoked(), CoreMatchers.is("doesNotHaveAllConditions"));
    }

    @Test
    public void nextAlreadySet() {
        RequestPredicate requestPredicate = RequestPredicate.create().containsCookie("my-cookie");
        requestPredicate.containsHeader("my-header");
        Assertions.assertThrows(IllegalStateException.class, () -> {
            requestPredicate.containsHeader("my-param");
        });
    }
}

