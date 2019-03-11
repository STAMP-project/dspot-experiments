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


import MediaType.JSON_UTF_8;
import MediaType.PLAIN_TEXT_UTF_8;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.linecorp.armeria.server.PathMapping;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Header;
import com.linecorp.armeria.server.annotation.Param;
import com.linecorp.armeria.server.annotation.RequestObject;
import com.linecorp.armeria.server.docs.EndpointInfo;
import com.linecorp.armeria.server.docs.EndpointInfoBuilder;
import com.linecorp.armeria.server.docs.ServiceInfo;
import com.linecorp.armeria.server.docs.ServiceSpecification;
import com.linecorp.armeria.server.docs.TypeSignature;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.junit.Test;


public class AnnotatedHttpDocServicePluginTest {
    private final AnnotatedHttpDocServicePlugin plugin = new AnnotatedHttpDocServicePlugin();

    @Test
    public void testToTypeSignature() throws Exception {
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(Void.class)).isEqualTo(TypeSignature.ofBase("void"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(void.class)).isEqualTo(TypeSignature.ofBase("void"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(Boolean.class)).isEqualTo(TypeSignature.ofBase("boolean"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(boolean.class)).isEqualTo(TypeSignature.ofBase("boolean"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(Byte.class)).isEqualTo(TypeSignature.ofBase("byte"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(byte.class)).isEqualTo(TypeSignature.ofBase("byte"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(Short.class)).isEqualTo(TypeSignature.ofBase("short"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(short.class)).isEqualTo(TypeSignature.ofBase("short"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(Integer.class)).isEqualTo(TypeSignature.ofBase("int"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(int.class)).isEqualTo(TypeSignature.ofBase("int"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(Long.class)).isEqualTo(TypeSignature.ofBase("long"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(long.class)).isEqualTo(TypeSignature.ofBase("long"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(Float.class)).isEqualTo(TypeSignature.ofBase("float"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(float.class)).isEqualTo(TypeSignature.ofBase("float"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(Double.class)).isEqualTo(TypeSignature.ofBase("double"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(double.class)).isEqualTo(TypeSignature.ofBase("double"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(String.class)).isEqualTo(TypeSignature.ofBase("string"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(Byte[].class)).isEqualTo(TypeSignature.ofBase("binary"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(byte[].class)).isEqualTo(TypeSignature.ofBase("binary"));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(int[].class)).isEqualTo(TypeSignature.ofList(TypeSignature.ofBase("int")));
        final TypeSignature typeVariable = AnnotatedHttpDocServicePlugin.toTypeSignature(AnnotatedHttpDocServicePluginTest.FieldContainer.class.getDeclaredField("typeVariable").getGenericType());
        assertThat(typeVariable).isEqualTo(TypeSignature.ofBase("T"));
        // Container types.
        final TypeSignature list = AnnotatedHttpDocServicePlugin.toTypeSignature(AnnotatedHttpDocServicePluginTest.FieldContainer.class.getDeclaredField("list").getGenericType());
        assertThat(list).isEqualTo(TypeSignature.ofList(TypeSignature.ofBase("string")));
        final TypeSignature set = AnnotatedHttpDocServicePlugin.toTypeSignature(AnnotatedHttpDocServicePluginTest.FieldContainer.class.getDeclaredField("set").getGenericType());
        assertThat(set).isEqualTo(TypeSignature.ofSet(TypeSignature.ofBase("float")));
        final TypeSignature map = AnnotatedHttpDocServicePlugin.toTypeSignature(AnnotatedHttpDocServicePluginTest.FieldContainer.class.getDeclaredField("map").getGenericType());
        assertThat(map).isEqualTo(TypeSignature.ofMap(TypeSignature.ofBase("long"), TypeSignature.ofUnresolved("")));
        final TypeSignature future = AnnotatedHttpDocServicePlugin.toTypeSignature(AnnotatedHttpDocServicePluginTest.FieldContainer.class.getDeclaredField("future").getGenericType());
        assertThat(future).isEqualTo(TypeSignature.ofContainer("CompletableFuture", TypeSignature.ofBase("double")));
        final TypeSignature typeVariableFuture = AnnotatedHttpDocServicePlugin.toTypeSignature(AnnotatedHttpDocServicePluginTest.FieldContainer.class.getDeclaredField("typeVariableFuture").getGenericType());
        assertThat(typeVariableFuture).isEqualTo(TypeSignature.ofContainer("CompletableFuture", TypeSignature.ofBase("T")));
        final TypeSignature genericArray = AnnotatedHttpDocServicePlugin.toTypeSignature(AnnotatedHttpDocServicePluginTest.FieldContainer.class.getDeclaredField("genericArray").getGenericType());
        assertThat(genericArray).isEqualTo(TypeSignature.ofList(TypeSignature.ofList(TypeSignature.ofBase("string"))));
        final TypeSignature biFunction = AnnotatedHttpDocServicePlugin.toTypeSignature(AnnotatedHttpDocServicePluginTest.FieldContainer.class.getDeclaredField("biFunction").getGenericType());
        assertThat(biFunction).isEqualTo(TypeSignature.ofContainer("BiFunction", TypeSignature.ofBase("JsonNode"), TypeSignature.ofUnresolved(""), TypeSignature.ofBase("string")));
        assertThat(AnnotatedHttpDocServicePlugin.toTypeSignature(AnnotatedHttpDocServicePluginTest.FieldContainer.class)).isEqualTo(TypeSignature.ofBase("FieldContainer"));
    }

    @Test
    public void testNewEndpointInfo() {
        final String hostnamePattern = "*";
        PathMapping mapping = AnnotatedHttpDocServicePluginTest.newHttpHeaderPathMapping(PathMapping.of("/path"));
        EndpointInfo endpointInfo = AnnotatedHttpDocServicePlugin.endpointInfo(mapping, hostnamePattern);
        assertThat(endpointInfo).isEqualTo(new EndpointInfoBuilder("*", "exact:/path").availableMimeTypes(PLAIN_TEXT_UTF_8, JSON_UTF_8).build());
        mapping = AnnotatedHttpDocServicePluginTest.newHttpHeaderPathMapping(PathMapping.of("prefix:/bar/baz"));
        endpointInfo = AnnotatedHttpDocServicePlugin.endpointInfo(mapping, hostnamePattern);
        assertThat(endpointInfo).isEqualTo(new EndpointInfoBuilder("*", "prefix:/bar/baz/").availableMimeTypes(PLAIN_TEXT_UTF_8, JSON_UTF_8).build());
        mapping = AnnotatedHttpDocServicePluginTest.newHttpHeaderPathMapping(PathMapping.of("glob:/home/*/files/**"));
        endpointInfo = AnnotatedHttpDocServicePlugin.endpointInfo(mapping, hostnamePattern);
        assertThat(endpointInfo).isEqualTo(new EndpointInfoBuilder("*", "regex:^/home/([^/]+)/files/(.*)$").availableMimeTypes(PLAIN_TEXT_UTF_8, JSON_UTF_8).build());
        mapping = AnnotatedHttpDocServicePluginTest.newHttpHeaderPathMapping(PathMapping.of("glob:/foo"));
        endpointInfo = AnnotatedHttpDocServicePlugin.endpointInfo(mapping, hostnamePattern);
        assertThat(endpointInfo).isEqualTo(new EndpointInfoBuilder("*", "exact:/foo").availableMimeTypes(PLAIN_TEXT_UTF_8, JSON_UTF_8).build());
        mapping = AnnotatedHttpDocServicePluginTest.newHttpHeaderPathMapping(PathMapping.of("regex:^/files/(?<filePath>.*)$"));
        endpointInfo = AnnotatedHttpDocServicePlugin.endpointInfo(mapping, hostnamePattern);
        assertThat(endpointInfo).isEqualTo(new EndpointInfoBuilder("*", "regex:^/files/(?<filePath>.*)$").availableMimeTypes(PLAIN_TEXT_UTF_8, JSON_UTF_8).build());
        mapping = AnnotatedHttpDocServicePluginTest.newHttpHeaderPathMapping(PathMapping.of("/service/{value}/test/:value2/something"));
        endpointInfo = AnnotatedHttpDocServicePlugin.endpointInfo(mapping, hostnamePattern);
        assertThat(endpointInfo).isEqualTo(new EndpointInfoBuilder("*", "/service/{value}/test/{value2}/something").availableMimeTypes(PLAIN_TEXT_UTF_8, JSON_UTF_8).build());
        // PrefixAddingPathMapping
        mapping = AnnotatedHttpDocServicePluginTest.newHttpHeaderPathMapping(new com.linecorp.armeria.internal.annotation.AnnotatedHttpServiceFactory.PrefixAddingPathMapping("/glob/", PathMapping.of("glob:/home/*/files/**")));
        endpointInfo = AnnotatedHttpDocServicePlugin.endpointInfo(mapping, hostnamePattern);
        assertThat(endpointInfo).isEqualTo(new EndpointInfoBuilder("*", "regex:^/home/([^/]+)/files/(.*)$").regexPathPrefix("prefix:/glob/").availableMimeTypes(PLAIN_TEXT_UTF_8, JSON_UTF_8).build());
        mapping = AnnotatedHttpDocServicePluginTest.newHttpHeaderPathMapping(new com.linecorp.armeria.internal.annotation.AnnotatedHttpServiceFactory.PrefixAddingPathMapping("/prefix: regex:/", PathMapping.of("regex:^/files/(?<filePath>.*)$")));
        endpointInfo = AnnotatedHttpDocServicePlugin.endpointInfo(mapping, hostnamePattern);
        assertThat(endpointInfo).isEqualTo(new EndpointInfoBuilder("*", "regex:^/files/(?<filePath>.*)$").regexPathPrefix("prefix:/prefix: regex:/").availableMimeTypes(PLAIN_TEXT_UTF_8, JSON_UTF_8).build());
    }

    @Test
    public void testGenerateSpecification() {
        final ServiceSpecification specification = plugin.generateSpecification(ImmutableSet.copyOf(AnnotatedHttpDocServicePluginTest.serviceConfigs()));
        // Ensure the specification contains all services.
        final Map<String, ServiceInfo> services = specification.services().stream().collect(ImmutableMap.toImmutableMap(ServiceInfo::name, Function.identity()));
        assertThat(services).containsOnlyKeys(AnnotatedHttpDocServicePluginTest.FooClass.class.getName(), AnnotatedHttpDocServicePluginTest.BarClass.class.getName());
        AnnotatedHttpDocServicePluginTest.checkFooService(services.get(AnnotatedHttpDocServicePluginTest.FooClass.class.getName()));
        AnnotatedHttpDocServicePluginTest.checkBarService(services.get(AnnotatedHttpDocServicePluginTest.BarClass.class.getName()));
    }

    private static class FieldContainer<T> {
        T typeVariable;

        List<String> list;

        Set<Float> set;

        Map<Long, ?> map;

        CompletableFuture<Double> future;

        CompletableFuture<T> typeVariableFuture;

        List<String>[] genericArray;

        BiFunction<JsonNode, ?, String> biFunction;
    }

    private static class FooClass {
        @Get("/foo")
        public void fooMethod(@Param
        String foo, @Header
        long foo1) {
        }

        @Get("/foo2")
        public long foo2Method(@Param
        String foo2) {
            return 0;
        }
    }

    private static class BarClass {
        @Get("/bar")
        public void barMethod(@Param
        String bar, AnnotatedHttpDocServicePluginTest.CompositeBean compositeBean) {
        }
    }

    static class CompositeBean {
        @RequestObject
        private AnnotatedHttpDocServicePluginTest.RequestBean1 bean1;

        @RequestObject
        private AnnotatedHttpDocServicePluginTest.RequestBean2 bean2;
    }

    static class RequestBean1 {
        @Nullable
        @JsonProperty
        @Param
        private Long seqNum;

        @JsonProperty
        private String uid;

        @Nullable
        private String notPopulatedStr;

        RequestBean1(@Header
        String uid) {
            this.uid = uid;
        }
    }

    static class RequestBean2 {
        @JsonProperty
        private AnnotatedHttpDocServicePluginTest.RequestBean2.InsideBean insideBean;

        public void setInsideBean(@RequestObject
        AnnotatedHttpDocServicePluginTest.RequestBean2.InsideBean insideBean) {
            this.insideBean = insideBean;
        }

        static class InsideBean {
            @JsonProperty
            @Param
            private Long inside1;

            @JsonProperty
            @Param
            private int inside2;
        }
    }
}

