/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.internal.annotation;


import HttpMethod.POST;
import HttpStatus.NOT_IMPLEMENTED;
import HttpStatus.OK;
import MediaType.JSON;
import MediaType.PLAIN_TEXT_UTF_8;
import com.fasterxml.jackson.databind.JsonNode;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.server.HttpResponseException;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.ExceptionHandler;
import com.linecorp.armeria.server.annotation.ExceptionHandlerFunction;
import com.linecorp.armeria.server.annotation.Post;
import com.linecorp.armeria.server.annotation.RequestConverter;
import com.linecorp.armeria.server.annotation.RequestConverterFunction;
import com.linecorp.armeria.server.annotation.RequestObject;
import com.linecorp.armeria.server.annotation.ResponseConverter;
import com.linecorp.armeria.server.annotation.ResponseConverterFunction;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.testing.server.ServerRule;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import org.junit.ClassRule;
import org.junit.Test;


public class AnnotatedHttpServiceHandlersOrderTest {
    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.annotatedService("/1", new AnnotatedHttpServiceHandlersOrderTest.MyDecorationService1(), LoggingService.newDecorator(), new AnnotatedHttpServiceHandlersOrderTest.ServiceLevelRequestConverter(), new AnnotatedHttpServiceHandlersOrderTest.ServiceLevelResponseConverter(), new AnnotatedHttpServiceHandlersOrderTest.ServiceLevelExceptionHandler());
        }
    };

    private static final AtomicInteger requestCounter = new AtomicInteger();

    private static final AtomicInteger responseCounter = new AtomicInteger();

    private static final AtomicInteger exceptionCounter = new AtomicInteger();

    @RequestConverter(AnnotatedHttpServiceHandlersOrderTest.ClassLevelRequestConverter.class)
    @ResponseConverter(AnnotatedHttpServiceHandlersOrderTest.ClassLevelResponseConverter.class)
    @ExceptionHandler(AnnotatedHttpServiceHandlersOrderTest.ClassLevelExceptionHandler.class)
    private static class MyDecorationService1 {
        @Post("/requestConverterOrder")
        @RequestConverter(AnnotatedHttpServiceHandlersOrderTest.MethodLevelRequestConverter.class)
        public HttpResponse requestConverterOrder(@RequestConverter(AnnotatedHttpServiceHandlersOrderTest.ParameterLevelRequestConverter.class)
        JsonNode node) {
            assertThat(node).isNotNull();
            return HttpResponse.of(OK, PLAIN_TEXT_UTF_8, HttpData.ofUtf8(node.toString()));
        }

        @Post("/responseConverterOrder")
        @ResponseConverter(AnnotatedHttpServiceHandlersOrderTest.MethodLevelResponseConverter.class)
        public String responseConverterOrder(@RequestObject
        String name) {
            assertThat(name).isEqualTo("foo");
            return "hello " + name;
        }

        @Post("/exceptionHandlerOrder")
        @ExceptionHandler(AnnotatedHttpServiceHandlersOrderTest.MethodLevelExceptionHandler.class)
        public HttpResponse exceptionHandlerOrder(@RequestObject
        String name) {
            assertThat(name).isEqualTo("foo");
            final AggregatedHttpMessage message = AggregatedHttpMessage.of(NOT_IMPLEMENTED, PLAIN_TEXT_UTF_8, ("hello " + name));
            throw HttpResponseException.of(message);
        }
    }

    // RequestConverterFunction starts
    private static class ParameterLevelRequestConverter implements RequestConverterFunction {
        @Override
        public Object convertRequest(ServiceRequestContext ctx, AggregatedHttpMessage request, Class<?> expectedResultType) throws Exception {
            if (expectedResultType == (JsonNode.class)) {
                assertThat(AnnotatedHttpServiceHandlersOrderTest.requestCounter.getAndIncrement()).isZero();
            }
            return RequestConverterFunction.fallthrough();
        }
    }

    private static class MethodLevelRequestConverter implements RequestConverterFunction {
        @Override
        public Object convertRequest(ServiceRequestContext ctx, AggregatedHttpMessage request, Class<?> expectedResultType) throws Exception {
            if (expectedResultType == (JsonNode.class)) {
                assertThat(AnnotatedHttpServiceHandlersOrderTest.requestCounter.getAndIncrement()).isOne();
            }
            return RequestConverterFunction.fallthrough();
        }
    }

    private static class ClassLevelRequestConverter implements RequestConverterFunction {
        @Override
        public Object convertRequest(ServiceRequestContext ctx, AggregatedHttpMessage request, Class<?> expectedResultType) throws Exception {
            if (expectedResultType == (JsonNode.class)) {
                assertThat(AnnotatedHttpServiceHandlersOrderTest.requestCounter.getAndIncrement()).isEqualTo(2);
            }
            return RequestConverterFunction.fallthrough();
        }
    }

    private static class ServiceLevelRequestConverter implements RequestConverterFunction {
        @Override
        public Object convertRequest(ServiceRequestContext ctx, AggregatedHttpMessage request, Class<?> expectedResultType) throws Exception {
            if (expectedResultType == (JsonNode.class)) {
                assertThat(AnnotatedHttpServiceHandlersOrderTest.requestCounter.getAndIncrement()).isEqualTo(3);
            }
            return RequestConverterFunction.fallthrough();
        }
    }

    // RequestConverterFunction ends
    // ResponseConverterFunction starts
    private static class MethodLevelResponseConverter implements ResponseConverterFunction {
        @Override
        public HttpResponse convertResponse(ServiceRequestContext ctx, HttpHeaders headers, @Nullable
        Object result, HttpHeaders trailingHeaders) throws Exception {
            if ((result instanceof String) && ("hello foo".equals(result))) {
                assertThat(AnnotatedHttpServiceHandlersOrderTest.responseCounter.getAndIncrement()).isZero();
            }
            return ResponseConverterFunction.fallthrough();
        }
    }

    private static class ClassLevelResponseConverter implements ResponseConverterFunction {
        @Override
        public HttpResponse convertResponse(ServiceRequestContext ctx, HttpHeaders headers, @Nullable
        Object result, HttpHeaders trailingHeaders) throws Exception {
            if ((result instanceof String) && ("hello foo".equals(result))) {
                assertThat(AnnotatedHttpServiceHandlersOrderTest.responseCounter.getAndIncrement()).isOne();
            }
            return ResponseConverterFunction.fallthrough();
        }
    }

    private static class ServiceLevelResponseConverter implements ResponseConverterFunction {
        @Override
        public HttpResponse convertResponse(ServiceRequestContext ctx, HttpHeaders headers, @Nullable
        Object result, HttpHeaders trailingHeaders) throws Exception {
            if ((result instanceof String) && ("hello foo".equals(result))) {
                assertThat(AnnotatedHttpServiceHandlersOrderTest.responseCounter.getAndIncrement()).isEqualTo(2);
                return HttpResponse.of(OK, PLAIN_TEXT_UTF_8, HttpData.ofUtf8(((String) (result))));
            }
            return ResponseConverterFunction.fallthrough();
        }
    }

    // ResponseConverterFunction ends
    // ExceptionHandlerFunction starts
    private static class MethodLevelExceptionHandler implements ExceptionHandlerFunction {
        @Override
        public HttpResponse handleException(RequestContext ctx, HttpRequest req, Throwable cause) {
            assertThat(AnnotatedHttpServiceHandlersOrderTest.exceptionCounter.getAndIncrement()).isZero();
            return ExceptionHandlerFunction.fallthrough();
        }
    }

    private static class ClassLevelExceptionHandler implements ExceptionHandlerFunction {
        @Override
        public HttpResponse handleException(RequestContext ctx, HttpRequest req, Throwable cause) {
            assertThat(AnnotatedHttpServiceHandlersOrderTest.exceptionCounter.getAndIncrement()).isOne();
            return ExceptionHandlerFunction.fallthrough();
        }
    }

    private static class ServiceLevelExceptionHandler implements ExceptionHandlerFunction {
        @Override
        public HttpResponse handleException(RequestContext ctx, HttpRequest req, Throwable cause) {
            assertThat(AnnotatedHttpServiceHandlersOrderTest.exceptionCounter.getAndIncrement()).isEqualTo(2);
            return ExceptionHandlerFunction.fallthrough();
        }
    }

    // ExceptionHandlerFunction ends
    @Test
    public void requestConverterOrder() throws Exception {
        final String body = "{\"foo\":\"bar\"}";
        final AggregatedHttpMessage aReq = AggregatedHttpMessage.of(POST, "/1/requestConverterOrder", JSON, body);
        final AggregatedHttpMessage aRes = AnnotatedHttpServiceHandlersOrderTest.executeRequest(aReq);
        assertThat(aRes.status()).isEqualTo(OK);
        // Converted from the default converter which is JacksonRequestConverterFunction.
        assertThat(aRes.contentUtf8()).isEqualTo(body);
        // parameter level(+1) -> method level(+1) -> class level(+1) -> service level(+1) -> default
        assertThat(AnnotatedHttpServiceHandlersOrderTest.requestCounter.get()).isEqualTo(4);
    }

    @Test
    public void responseConverterOrder() throws Exception {
        final AggregatedHttpMessage aReq = AggregatedHttpMessage.of(POST, "/1/responseConverterOrder", PLAIN_TEXT_UTF_8, "foo");
        final AggregatedHttpMessage aRes = AnnotatedHttpServiceHandlersOrderTest.executeRequest(aReq);
        assertThat(aRes.status()).isEqualTo(OK);
        // Converted from the ServiceLevelResponseConverter.
        assertThat(aRes.contentUtf8()).isEqualTo("hello foo");
        // method level(+1) -> class level(+1) -> service level(+1)
        assertThat(AnnotatedHttpServiceHandlersOrderTest.responseCounter.get()).isEqualTo(3);
    }

    @Test
    public void exceptionHandlerOrder() throws Exception {
        final AggregatedHttpMessage aReq = AggregatedHttpMessage.of(POST, "/1/exceptionHandlerOrder", PLAIN_TEXT_UTF_8, "foo");
        final AggregatedHttpMessage aRes = AnnotatedHttpServiceHandlersOrderTest.executeRequest(aReq);
        assertThat(aRes.status()).isEqualTo(NOT_IMPLEMENTED);
        // Converted from the default Handler which is DefaultExceptionHandler in AnnotatedHttpServices.
        assertThat(aRes.contentUtf8()).isEqualTo("hello foo");
        // method level(+1) -> class level(+1) -> service level(+1) -> default
        assertThat(AnnotatedHttpServiceHandlersOrderTest.exceptionCounter.get()).isEqualTo(3);
    }
}

