/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.internal.annotation;


import HttpHeaderNames.CACHE_CONTROL;
import HttpStatus.OK;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.TestConverters;
import com.linecorp.armeria.server.annotation.ConsumesBinary;
import com.linecorp.armeria.server.annotation.Delete;
import com.linecorp.armeria.server.annotation.Description;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Head;
import com.linecorp.armeria.server.annotation.Header;
import com.linecorp.armeria.server.annotation.Options;
import com.linecorp.armeria.server.annotation.Param;
import com.linecorp.armeria.server.annotation.Patch;
import com.linecorp.armeria.server.annotation.Path;
import com.linecorp.armeria.server.annotation.Post;
import com.linecorp.armeria.server.annotation.Put;
import com.linecorp.armeria.server.annotation.ResponseConverter;
import com.linecorp.armeria.server.annotation.Trace;
import com.linecorp.armeria.server.docs.DocServiceBuilder;
import com.linecorp.armeria.server.docs.MethodInfo;
import com.linecorp.armeria.testing.server.ServerRule;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import org.junit.ClassRule;
import org.junit.Test;


public class AnnotatedHttpDocServiceTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final HttpHeaders EXAMPLE_HEADERS_ALL = HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("a"), "b");

    private static final HttpHeaders EXAMPLE_HEADERS_SERVICE = HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("c"), "d");

    private static final HttpHeaders EXAMPLE_HEADERS_METHOD = HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("e"), "f");

    private static final boolean DEMO_MODE = false;

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            if (AnnotatedHttpDocServiceTest.DEMO_MODE) {
                sb.http(8080);
            }
            sb.annotatedService("/service", new AnnotatedHttpDocServiceTest.MyService());
            sb.serviceUnder("/docs", new DocServiceBuilder().exampleHttpHeaders(AnnotatedHttpDocServiceTest.EXAMPLE_HEADERS_ALL).exampleHttpHeaders(AnnotatedHttpDocServiceTest.MyService.class, AnnotatedHttpDocServiceTest.EXAMPLE_HEADERS_SERVICE).exampleHttpHeaders(AnnotatedHttpDocServiceTest.MyService.class, "pathParams", AnnotatedHttpDocServiceTest.EXAMPLE_HEADERS_METHOD).exampleRequestForMethod(AnnotatedHttpDocServiceTest.MyService.class, "pathParams", ImmutableList.of(AnnotatedHttpDocServiceTest.mapper.readTree("{\"hello\":\"armeria\"}"))).build());
        }
    };

    @Test
    public void jsonSpecification() throws InterruptedException {
        if (AnnotatedHttpDocServiceTest.DEMO_MODE) {
            Thread.sleep(Long.MAX_VALUE);
        }
        final Map<Class<?>, Set<MethodInfo>> methodInfos = new HashMap<>();
        AnnotatedHttpDocServiceTest.addFooMethodInfo(methodInfos);
        AnnotatedHttpDocServiceTest.addAllMethodsMethodInfos(methodInfos);
        AnnotatedHttpDocServiceTest.addIntsMethodInfo(methodInfos);
        AnnotatedHttpDocServiceTest.addPathParamsMethodInfo(methodInfos);
        AnnotatedHttpDocServiceTest.addRegexMethodInfo(methodInfos);
        AnnotatedHttpDocServiceTest.addPrefixMethodInfo(methodInfos);
        AnnotatedHttpDocServiceTest.addConsumesMethodInfo(methodInfos);
        AnnotatedHttpDocServiceTest.addBeanMethodInfo(methodInfos);
        final Map<Class<?>, String> serviceDescription = ImmutableMap.of(AnnotatedHttpDocServiceTest.MyService.class, "My service class");
        final JsonNode expectedJson = AnnotatedHttpDocServiceTest.mapper.valueToTree(AnnotatedHttpDocServicePlugin.generate(serviceDescription, methodInfos));
        AnnotatedHttpDocServiceTest.addExamples(expectedJson);
        final HttpClient client = HttpClient.of(AnnotatedHttpDocServiceTest.server.uri("/"));
        final AggregatedHttpMessage msg = client.get("/docs/specification.json").aggregate().join();
        assertThat(msg.status()).isEqualTo(OK);
        assertThat(msg.headers().get(CACHE_CONTROL)).isEqualTo("no-cache, must-revalidate");
        assertThatJson(msg.contentUtf8()).when(IGNORING_ARRAY_ORDER).isEqualTo(expectedJson);
    }

    @Description("My service class")
    @ResponseConverter(TestConverters.UnformattedStringConverterFunction.class)
    private static class MyService {
        @Get("/foo")
        @Description("foo method")
        public <T> T foo(@Header
        @Description("header parameter")
        int header, @Param
        @Description("query parameter")
        long query) {
            @SuppressWarnings("unchecked")
            final T result = ((T) ((("header: " + header) + ", query: ") + query));
            return result;
        }

        @Options
        @Get
        @Head
        @Post
        @Put
        @Patch
        @Delete
        @Trace
        @Path("/allMethods")
        public CompletableFuture<?> allMethods() {
            return CompletableFuture.completedFuture(HttpResponse.of("allMethods"));
        }

        @Get("/ints")
        public List<Integer> ints(@Param
        List<Integer> ints) {
            return ints;
        }

        @Get("/hello1/:hello2/hello3/:hello4")
        public String pathParams(@Param
        String hello2, @Param
        String hello4) {
            return (hello2 + ' ') + hello4;
        }

        @Get("regex:/(bar|baz)")
        public List<String>[] regex(@Param
        AnnotatedHttpDocServiceTest.MyEnum myEnum) {
            final AnnotatedHttpDocServiceTest.MyEnum[] values = AnnotatedHttpDocServiceTest.MyEnum.values();
            @SuppressWarnings("unchecked")
            final List<String>[] genericArray = ((List<String>[]) (Array.newInstance(List.class, values.length)));
            for (int i = 0; i < (genericArray.length); i++) {
                genericArray[i] = ImmutableList.of(values[i].toString());
            }
            return genericArray;
        }

        @Get("prefix:/prefix")
        public String prefix(ServiceRequestContext ctx) {
            return "prefix";
        }

        @Get("/consumes")
        @ConsumesBinary
        public BiFunction<JsonNode, ?, String> consumes() {
            return new BiFunction<JsonNode, Object, String>() {
                @Override
                public String apply(JsonNode jsonNode, Object o) {
                    return null;
                }

                @Override
                public String toString() {
                    return "consumes";
                }
            };
        }

        @Get("/bean")
        public HttpResponse bean(AnnotatedHttpDocServicePluginTest.CompositeBean compositeBean) throws JsonProcessingException {
            final ObjectMapper mapper = new ObjectMapper();
            return HttpResponse.of(mapper.writeValueAsString(compositeBean));
        }
    }

    private enum MyEnum {

        A,
        B,
        C;}
}

