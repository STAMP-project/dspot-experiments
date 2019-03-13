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


import HttpHeaderNames.COOKIE;
import HttpMethod.GET;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.internal.annotation.AnnotatedValueResolver.RequestObjectResolver;
import com.linecorp.armeria.internal.annotation.AnnotatedValueResolver.ResolverContext;
import com.linecorp.armeria.server.PathMappingResult;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.ServiceRequestContextBuilder;
import com.linecorp.armeria.server.annotation.Cookies;
import com.linecorp.armeria.server.annotation.Default;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Header;
import com.linecorp.armeria.server.annotation.Param;
import com.linecorp.armeria.server.annotation.RequestObject;
import io.netty.util.AsciiString;
import java.lang.reflect.Constructor;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AnnotatedValueResolverTest {
    private static final Logger logger = LoggerFactory.getLogger(AnnotatedValueResolverTest.class);

    static final List<RequestObjectResolver> objectResolvers = AnnotatedValueResolver.toRequestObjectResolvers(ImmutableList.of());

    // A string which is the same as the parameter will be returned.
    static final Set<String> pathParams = ImmutableSet.of("var1");

    static final Set<String> existingHttpParameters = ImmutableSet.of("param1", "enum1", "sensitive");

    // 'headerValues' will be returned.
    static final Set<AsciiString> existingHttpHeaders = ImmutableSet.of(com.linecorp.armeria.common.HttpHeaderNames.of("header1"), com.linecorp.armeria.common.HttpHeaderNames.of("header2"));

    static final List<String> headerValues = ImmutableList.of("value1", "value3", "value2");

    static final ResolverContext resolverContext;

    static final ServiceRequestContext context;

    static final HttpRequest request;

    static final HttpHeaders originalHeaders;

    static {
        final String path = "/";
        final String query = AnnotatedValueResolverTest.existingHttpParameters.stream().map(( p) -> (p + '=') + p).collect(Collectors.joining("&"));
        final HttpHeaders headers = HttpHeaders.of(GET, ((path + '?') + query));
        headers.set(COOKIE, "a=1;b=2", "c=3", "a=4");
        AnnotatedValueResolverTest.existingHttpHeaders.forEach(( name) -> headers.set(name, headerValues));
        request = HttpRequest.of(headers);
        originalHeaders = HttpHeaders.copyOf(AnnotatedValueResolverTest.request.headers()).asImmutable();
        final PathMappingResult pathMappingResult = PathMappingResult.of(path, query, AnnotatedValueResolverTest.pathParams.stream().map(( v) -> new AbstractMap.SimpleImmutableEntry<>(v, v)).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)));
        context = ServiceRequestContextBuilder.of(AnnotatedValueResolverTest.request).pathMappingResult(pathMappingResult).build();
        resolverContext = new ResolverContext(AnnotatedValueResolverTest.context, AnnotatedValueResolverTest.request, null);
    }

    @Test
    public void ofMethods() {
        getAllMethods(AnnotatedValueResolverTest.Service.class).forEach(( method) -> {
            try {
                final List<AnnotatedValueResolver> elements = AnnotatedValueResolver.ofServiceMethod(method, pathParams, objectResolvers);
                elements.forEach(AnnotatedValueResolverTest::testResolver);
            } catch ( ignored) {
                // Ignore this exception because MixedBean class has not annotated method.
            }
        });
    }

    @Test
    public void ofFieldBean() throws NoSuchFieldException {
        final AnnotatedValueResolverTest.FieldBean bean = new AnnotatedValueResolverTest.FieldBean();
        getAllFields(AnnotatedValueResolverTest.FieldBean.class).forEach(( field) -> {
            final Optional<AnnotatedValueResolver> resolver = AnnotatedValueResolver.ofBeanField(field, pathParams, objectResolvers);
            if (resolver.isPresent()) {
                testResolver(resolver.get());
                try {
                    field.setAccessible(true);
                    field.set(bean, resolver.get().resolve(resolverContext));
                } catch ( e) {
                    throw new <e>Error("should not reach here");
                }
            }
        });
        AnnotatedValueResolverTest.testBean(bean);
    }

    @Test
    public void ofConstructorBean() {
        @SuppressWarnings("rawtypes")
        final Set<Constructor> constructors = getAllConstructors(AnnotatedValueResolverTest.ConstructorBean.class);
        assertThat(constructors.size()).isOne();
        constructors.forEach(( constructor) -> {
            final List<AnnotatedValueResolver> elements = AnnotatedValueResolver.ofBeanConstructorOrMethod(constructor, AnnotatedValueResolverTest.pathParams, AnnotatedValueResolverTest.objectResolvers);
            elements.forEach(AnnotatedValueResolverTest::testResolver);
            final AnnotatedValueResolverTest.ConstructorBean bean;
            try {
                // Use mock instance of ResolverContext.
                constructor.setAccessible(true);
                bean = ((AnnotatedValueResolverTest.ConstructorBean) (constructor.newInstance(AnnotatedValueResolver.toArguments(elements, AnnotatedValueResolverTest.resolverContext))));
            } catch (Throwable cause) {
                throw new Error("should not reach here", cause);
            }
            AnnotatedValueResolverTest.testBean(bean);
        });
    }

    @Test
    public void ofSetterBean() throws Exception {
        final AnnotatedValueResolverTest.SetterBean bean = AnnotatedValueResolverTest.SetterBean.class.getDeclaredConstructor().newInstance();
        getAllMethods(AnnotatedValueResolverTest.SetterBean.class).forEach(( method) -> testMethod(method, bean));
        AnnotatedValueResolverTest.testBean(bean);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void ofMixedBean() throws Exception {
        final Set<Constructor> constructors = getAllConstructors(AnnotatedValueResolverTest.MixedBean.class);
        assertThat(constructors.size()).isOne();
        final Constructor constructor = Iterables.getFirst(constructors, null);
        final List<AnnotatedValueResolver> initArgs = AnnotatedValueResolver.ofBeanConstructorOrMethod(constructor, AnnotatedValueResolverTest.pathParams, AnnotatedValueResolverTest.objectResolvers);
        initArgs.forEach(AnnotatedValueResolverTest::testResolver);
        final AnnotatedValueResolverTest.MixedBean bean = ((AnnotatedValueResolverTest.MixedBean) (constructor.newInstance(AnnotatedValueResolver.toArguments(initArgs, AnnotatedValueResolverTest.resolverContext))));
        getAllMethods(AnnotatedValueResolverTest.MixedBean.class).forEach(( method) -> testMethod(method, bean));
        AnnotatedValueResolverTest.testBean(bean);
    }

    enum CaseInsensitiveEnum {

        ENUM1,
        ENUM2;}

    enum CaseSensitiveEnum {

        sensitive,
        SENSITIVE;}

    enum ValueEnum {

        VALUE1,
        VALUE2,
        VALUE3;}

    static class Service {
        public void method1(@Param
        String var1, @Param
        String param1, @Param
        @Default("1")
        int param2, @Param
        @Default("1")
        List<Integer> param3, @Header
        List<String> header1, @Header("header1")
        java.util.Optional<List<AnnotatedValueResolverTest.ValueEnum>> optionalHeader1, @Header
        String header2, @Header
        @Default("defaultValue")
        List<String> header3, @Header
        @Default("defaultValue")
        String header4, @Param
        AnnotatedValueResolverTest.CaseInsensitiveEnum enum1, @Param
        @Default("enum2")
        AnnotatedValueResolverTest.CaseInsensitiveEnum enum2, @Param("sensitive")
        AnnotatedValueResolverTest.CaseSensitiveEnum enum3, @Param("SENSITIVE")
        @Default("SENSITIVE")
        AnnotatedValueResolverTest.CaseSensitiveEnum enum4, ServiceRequestContext ctx, HttpRequest request, @RequestObject
        AnnotatedValueResolverTest.OuterBean outerBean, Cookies cookies) {
        }

        public void dummy1() {
        }

        public void redundant1(@Param
        @Default("defaultValue")
        java.util.Optional<String> value) {
        }

        @Get("/r2/:var1")
        public void redundant2(@Param
        @Default("defaultValue")
        String var1) {
        }

        @Get("/r3/:var1")
        public void redundant3(@Param
        java.util.Optional<String> var1) {
        }
    }

    interface Bean {
        String var1();

        String param1();

        int param2();

        List<Integer> param3();

        List<String> header1();

        java.util.Optional<List<AnnotatedValueResolverTest.ValueEnum>> optionalHeader1();

        String header2();

        List<String> header3();

        String header4();

        AnnotatedValueResolverTest.CaseInsensitiveEnum enum1();

        AnnotatedValueResolverTest.CaseInsensitiveEnum enum2();

        AnnotatedValueResolverTest.CaseSensitiveEnum enum3();

        AnnotatedValueResolverTest.CaseSensitiveEnum enum4();

        ServiceRequestContext ctx();

        HttpRequest request();

        AnnotatedValueResolverTest.OuterBean outerBean();

        Cookies cookies();
    }

    static class FieldBean implements AnnotatedValueResolverTest.Bean {
        @Param
        String var1;

        @Param
        String param1;

        @Param
        @Default("1")
        int param2;

        @Param
        @Default("1")
        List<Integer> param3;

        @Header
        List<String> header1;

        @Header("header1")
        java.util.Optional<List<AnnotatedValueResolverTest.ValueEnum>> optionalHeader1;

        @Header
        String header2;

        @Header
        @Default("defaultValue")
        List<String> header3;

        @Header
        @Default("defaultValue")
        String header4;

        @Param
        AnnotatedValueResolverTest.CaseInsensitiveEnum enum1;

        @Param
        @Default("enum2")
        AnnotatedValueResolverTest.CaseInsensitiveEnum enum2;

        @Param("sensitive")
        AnnotatedValueResolverTest.CaseSensitiveEnum enum3;

        @Param("SENSITIVE")
        @Default("SENSITIVE")
        AnnotatedValueResolverTest.CaseSensitiveEnum enum4;

        ServiceRequestContext ctx;

        HttpRequest request;

        @RequestObject
        AnnotatedValueResolverTest.OuterBean outerBean;

        Cookies cookies;

        String notInjected1;

        @Override
        public String var1() {
            return var1;
        }

        @Override
        public String param1() {
            return param1;
        }

        @Override
        public int param2() {
            return param2;
        }

        @Override
        public List<Integer> param3() {
            return param3;
        }

        @Override
        public List<String> header1() {
            return header1;
        }

        @Override
        public java.util.Optional<List<AnnotatedValueResolverTest.ValueEnum>> optionalHeader1() {
            return optionalHeader1;
        }

        @Override
        public String header2() {
            return header2;
        }

        @Override
        public List<String> header3() {
            return header3;
        }

        @Override
        public String header4() {
            return header4;
        }

        @Override
        public AnnotatedValueResolverTest.CaseInsensitiveEnum enum1() {
            return enum1;
        }

        @Override
        public AnnotatedValueResolverTest.CaseInsensitiveEnum enum2() {
            return enum2;
        }

        @Override
        public AnnotatedValueResolverTest.CaseSensitiveEnum enum3() {
            return enum3;
        }

        @Override
        public AnnotatedValueResolverTest.CaseSensitiveEnum enum4() {
            return enum4;
        }

        @Override
        public ServiceRequestContext ctx() {
            return ctx;
        }

        @Override
        public HttpRequest request() {
            return request;
        }

        @Override
        public AnnotatedValueResolverTest.OuterBean outerBean() {
            return outerBean;
        }

        @Override
        public Cookies cookies() {
            return cookies;
        }
    }

    static class ConstructorBean implements AnnotatedValueResolverTest.Bean {
        final String var1;

        final String param1;

        final int param2;

        final List<Integer> param3;

        final List<String> header1;

        final java.util.Optional<List<AnnotatedValueResolverTest.ValueEnum>> optionalHeader1;

        final String header2;

        final List<String> header3;

        final String header4;

        final AnnotatedValueResolverTest.CaseInsensitiveEnum enum1;

        final AnnotatedValueResolverTest.CaseInsensitiveEnum enum2;

        final AnnotatedValueResolverTest.CaseSensitiveEnum enum3;

        final AnnotatedValueResolverTest.CaseSensitiveEnum enum4;

        final ServiceRequestContext ctx;

        final HttpRequest request;

        final AnnotatedValueResolverTest.OuterBean outerBean;

        final Cookies cookies;

        ConstructorBean(@Param
        String var1, @Param
        String param1, @Param
        @Default("1")
        int param2, @Param
        @Default("1")
        List<Integer> param3, @Header
        List<String> header1, @Header("header1")
        java.util.Optional<List<AnnotatedValueResolverTest.ValueEnum>> optionalHeader1, @Header
        String header2, @Header
        @Default("defaultValue")
        List<String> header3, @Header
        @Default("defaultValue")
        String header4, @Param
        AnnotatedValueResolverTest.CaseInsensitiveEnum enum1, @Param
        @Default("enum2")
        AnnotatedValueResolverTest.CaseInsensitiveEnum enum2, @Param("sensitive")
        AnnotatedValueResolverTest.CaseSensitiveEnum enum3, @Param("SENSITIVE")
        @Default("SENSITIVE")
        AnnotatedValueResolverTest.CaseSensitiveEnum enum4, ServiceRequestContext ctx, HttpRequest request, @RequestObject
        AnnotatedValueResolverTest.OuterBean outerBean, Cookies cookies) {
            this.var1 = var1;
            this.param1 = param1;
            this.param2 = param2;
            this.param3 = param3;
            this.header1 = header1;
            this.optionalHeader1 = optionalHeader1;
            this.header2 = header2;
            this.header3 = header3;
            this.header4 = header4;
            this.enum1 = enum1;
            this.enum2 = enum2;
            this.enum3 = enum3;
            this.enum4 = enum4;
            this.ctx = ctx;
            this.request = request;
            this.outerBean = outerBean;
            this.cookies = cookies;
        }

        @Override
        public String var1() {
            return var1;
        }

        @Override
        public String param1() {
            return param1;
        }

        @Override
        public int param2() {
            return param2;
        }

        @Override
        public List<Integer> param3() {
            return param3;
        }

        @Override
        public List<String> header1() {
            return header1;
        }

        @Override
        public java.util.Optional<List<AnnotatedValueResolverTest.ValueEnum>> optionalHeader1() {
            return optionalHeader1;
        }

        @Override
        public String header2() {
            return header2;
        }

        @Override
        public List<String> header3() {
            return header3;
        }

        @Override
        public String header4() {
            return header4;
        }

        @Override
        public AnnotatedValueResolverTest.CaseInsensitiveEnum enum1() {
            return enum1;
        }

        @Override
        public AnnotatedValueResolverTest.CaseInsensitiveEnum enum2() {
            return enum2;
        }

        @Override
        public AnnotatedValueResolverTest.CaseSensitiveEnum enum3() {
            return enum3;
        }

        @Override
        public AnnotatedValueResolverTest.CaseSensitiveEnum enum4() {
            return enum4;
        }

        @Override
        public ServiceRequestContext ctx() {
            return ctx;
        }

        @Override
        public HttpRequest request() {
            return request;
        }

        @Override
        public AnnotatedValueResolverTest.OuterBean outerBean() {
            return outerBean;
        }

        @Override
        public Cookies cookies() {
            return cookies;
        }
    }

    static class SetterBean implements AnnotatedValueResolverTest.Bean {
        String var1;

        String param1;

        int param2;

        List<Integer> param3;

        List<String> header1;

        java.util.Optional<List<AnnotatedValueResolverTest.ValueEnum>> optionalHeader1;

        String header2;

        List<String> header3;

        String header4;

        AnnotatedValueResolverTest.CaseInsensitiveEnum enum1;

        AnnotatedValueResolverTest.CaseInsensitiveEnum enum2;

        AnnotatedValueResolverTest.CaseSensitiveEnum enum3;

        AnnotatedValueResolverTest.CaseSensitiveEnum enum4;

        ServiceRequestContext ctx;

        HttpRequest request;

        AnnotatedValueResolverTest.OuterBean outerBean;

        Cookies cookies;

        @Param
        void setVar1(String var1) {
            this.var1 = var1;
        }

        void setParam1(@Param
        String param1) {
            this.param1 = param1;
        }

        @Param
        @Default("1")
        void setParam2(int param2) {
            this.param2 = param2;
        }

        @Param
        @Default("1")
        void setParam3(List<Integer> param3) {
            this.param3 = param3;
        }

        void setHeader1(@Header
        List<String> header1) {
            this.header1 = header1;
        }

        public void setOptionalHeader1(@Header("header1")
        java.util.Optional<List<AnnotatedValueResolverTest.ValueEnum>> optionalHeader1) {
            this.optionalHeader1 = optionalHeader1;
        }

        void setHeader2(@Header
        String header2) {
            this.header2 = header2;
        }

        void setHeader3(@Header
        @Default("defaultValue")
        List<String> header3) {
            this.header3 = header3;
        }

        void setHeader4(@Header
        @Default("defaultValue")
        String header4) {
            this.header4 = header4;
        }

        void setEnum1(@Param
        AnnotatedValueResolverTest.CaseInsensitiveEnum enum1) {
            this.enum1 = enum1;
        }

        void setEnum2(@Param
        @Default("enum2")
        AnnotatedValueResolverTest.CaseInsensitiveEnum enum2) {
            this.enum2 = enum2;
        }

        void setEnum3(@Param("sensitive")
        AnnotatedValueResolverTest.CaseSensitiveEnum enum3) {
            this.enum3 = enum3;
        }

        void setEnum4(@Param("SENSITIVE")
        @Default("SENSITIVE")
        AnnotatedValueResolverTest.CaseSensitiveEnum enum4) {
            this.enum4 = enum4;
        }

        void setCtx(ServiceRequestContext ctx) {
            this.ctx = ctx;
        }

        void setRequest(HttpRequest request) {
            this.request = request;
        }

        void setOuterBean(@RequestObject
        AnnotatedValueResolverTest.OuterBean outerBean) {
            this.outerBean = outerBean;
        }

        void setCookies(Cookies cookies) {
            this.cookies = cookies;
        }

        @Override
        public String var1() {
            return var1;
        }

        @Override
        public String param1() {
            return param1;
        }

        @Override
        public int param2() {
            return param2;
        }

        @Override
        public List<Integer> param3() {
            return param3;
        }

        @Override
        public List<String> header1() {
            return header1;
        }

        @Override
        public java.util.Optional<List<AnnotatedValueResolverTest.ValueEnum>> optionalHeader1() {
            return optionalHeader1;
        }

        @Override
        public String header2() {
            return header2;
        }

        @Override
        public List<String> header3() {
            return header3;
        }

        @Override
        public String header4() {
            return header4;
        }

        @Override
        public AnnotatedValueResolverTest.CaseInsensitiveEnum enum1() {
            return enum1;
        }

        @Override
        public AnnotatedValueResolverTest.CaseInsensitiveEnum enum2() {
            return enum2;
        }

        @Override
        public AnnotatedValueResolverTest.CaseSensitiveEnum enum3() {
            return enum3;
        }

        @Override
        public AnnotatedValueResolverTest.CaseSensitiveEnum enum4() {
            return enum4;
        }

        @Override
        public ServiceRequestContext ctx() {
            return ctx;
        }

        @Override
        public HttpRequest request() {
            return request;
        }

        @Override
        public AnnotatedValueResolverTest.OuterBean outerBean() {
            return outerBean;
        }

        @Override
        public Cookies cookies() {
            return cookies;
        }
    }

    static class MixedBean implements AnnotatedValueResolverTest.Bean {
        final String var1;

        final String param1;

        int param2;

        List<Integer> param3;

        final List<String> header1;

        final java.util.Optional<List<AnnotatedValueResolverTest.ValueEnum>> optionalHeader1;

        String header2;

        final List<String> header3;

        String header4;

        final AnnotatedValueResolverTest.CaseInsensitiveEnum enum1;

        AnnotatedValueResolverTest.CaseInsensitiveEnum enum2;

        final AnnotatedValueResolverTest.CaseSensitiveEnum enum3;

        AnnotatedValueResolverTest.CaseSensitiveEnum enum4;

        final ServiceRequestContext ctx;

        HttpRequest request;

        final AnnotatedValueResolverTest.OuterBean outerBean;

        final Cookies cookies;

        MixedBean(@Param
        String var1, @Param
        String param1, @Header
        List<String> header1, @Header("header1")
        java.util.Optional<List<AnnotatedValueResolverTest.ValueEnum>> optionalHeader1, @Header
        @Default("defaultValue")
        List<String> header3, @Param
        AnnotatedValueResolverTest.CaseInsensitiveEnum enum1, @Param("sensitive")
        AnnotatedValueResolverTest.CaseSensitiveEnum enum3, ServiceRequestContext ctx, @RequestObject
        AnnotatedValueResolverTest.OuterBean outerBean, Cookies cookies) {
            this.var1 = var1;
            this.param1 = param1;
            this.header1 = header1;
            this.optionalHeader1 = optionalHeader1;
            this.header3 = header3;
            this.enum1 = enum1;
            this.enum3 = enum3;
            this.ctx = ctx;
            this.outerBean = outerBean;
            this.cookies = cookies;
        }

        void setParam2(@Param
        @Default("1")
        int param2) {
            this.param2 = param2;
        }

        void setParam3(@Param
        @Default("1")
        List<Integer> param3) {
            this.param3 = param3;
        }

        void setHeader2(@Header
        String header2) {
            this.header2 = header2;
        }

        void setHeader4(@Header
        @Default("defaultValue")
        String header4) {
            this.header4 = header4;
        }

        void setEnum2(@Param
        @Default("enum2")
        AnnotatedValueResolverTest.CaseInsensitiveEnum enum2) {
            this.enum2 = enum2;
        }

        void setEnum4(@Param("SENSITIVE")
        @Default("SENSITIVE")
        AnnotatedValueResolverTest.CaseSensitiveEnum enum4) {
            this.enum4 = enum4;
        }

        void setRequest(HttpRequest request) {
            this.request = request;
        }

        @Override
        public String var1() {
            return var1;
        }

        @Override
        public String param1() {
            return param1;
        }

        @Override
        public int param2() {
            return param2;
        }

        @Override
        public List<Integer> param3() {
            return param3;
        }

        @Override
        public List<String> header1() {
            return header1;
        }

        @Override
        public java.util.Optional<List<AnnotatedValueResolverTest.ValueEnum>> optionalHeader1() {
            return optionalHeader1;
        }

        @Override
        public String header2() {
            return header2;
        }

        @Override
        public List<String> header3() {
            return header3;
        }

        @Override
        public String header4() {
            return header4;
        }

        @Override
        public AnnotatedValueResolverTest.CaseInsensitiveEnum enum1() {
            return enum1;
        }

        @Override
        public AnnotatedValueResolverTest.CaseInsensitiveEnum enum2() {
            return enum2;
        }

        @Override
        public AnnotatedValueResolverTest.CaseSensitiveEnum enum3() {
            return enum3;
        }

        @Override
        public AnnotatedValueResolverTest.CaseSensitiveEnum enum4() {
            return enum4;
        }

        @Override
        public ServiceRequestContext ctx() {
            return ctx;
        }

        @Override
        public HttpRequest request() {
            return request;
        }

        @Override
        public AnnotatedValueResolverTest.OuterBean outerBean() {
            return outerBean;
        }

        @Override
        public Cookies cookies() {
            return cookies;
        }
    }

    static class OuterBean {
        @Param
        String var1;

        // constructor
        final AnnotatedValueResolverTest.InnerBean innerBean1;

        // field
        @RequestObject
        AnnotatedValueResolverTest.InnerBean innerBean2;

        // setter
        AnnotatedValueResolverTest.InnerBean innerBean3;

        OuterBean(@RequestObject
        AnnotatedValueResolverTest.InnerBean innerBean1) {
            this.innerBean1 = innerBean1;
        }

        void setInnerBean3(@RequestObject
        AnnotatedValueResolverTest.InnerBean innerBean3) {
            this.innerBean3 = innerBean3;
        }
    }

    static class InnerBean {
        // constructor
        final String var1;

        // field
        @Param
        String param1;

        // setter
        List<String> header1;

        InnerBean(@Param
        String var1) {
            this.var1 = var1;
        }

        void setHeader1(@Header
        List<String> header1) {
            this.header1 = header1;
        }
    }
}

