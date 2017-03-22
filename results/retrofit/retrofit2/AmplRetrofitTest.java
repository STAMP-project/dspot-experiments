/**
 * Copyright (C) 2013 Square, Inc.
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


package retrofit2;


public final class AmplRetrofitTest {
    @org.junit.Rule
    public final okhttp3.mockwebserver.MockWebServer server = new okhttp3.mockwebserver.MockWebServer();

    interface CallMethod {
        @retrofit2.http.GET(value = "/")
        retrofit2.Call<java.lang.String> disallowed();

        @retrofit2.http.POST(value = "/")
        retrofit2.Call<okhttp3.ResponseBody> disallowed(@retrofit2.http.Body
        java.lang.String body);

        @retrofit2.http.GET(value = "/")
        retrofit2.Call<retrofit2.Response> badType1();

        @retrofit2.http.GET(value = "/")
        retrofit2.Call<okhttp3.Response> badType2();

        @retrofit2.http.GET(value = "/")
        retrofit2.Call<okhttp3.ResponseBody> getResponseBody();

        @retrofit2.http.GET(value = "/")
        retrofit2.Call<java.lang.Void> getVoid();

        @retrofit2.http.POST(value = "/")
        retrofit2.Call<okhttp3.ResponseBody> postRequestBody(@retrofit2.http.Body
        okhttp3.RequestBody body);

        @retrofit2.http.GET(value = "/")
        retrofit2.Call<okhttp3.ResponseBody> queryString(@retrofit2.http.Query(value = "foo")
        java.lang.String foo);

        @retrofit2.http.GET(value = "/")
        retrofit2.Call<okhttp3.ResponseBody> queryObject(@retrofit2.http.Query(value = "foo")
        java.lang.Object foo);
    }

    interface FutureMethod {
        @retrofit2.http.GET(value = "/")
        java.util.concurrent.Future<java.lang.String> method();
    }

    interface Extending extends retrofit2.AmplRetrofitTest.CallMethod {    }

    interface StringService {
        @retrofit2.http.GET(value = "/")
        java.lang.String get();
    }

    interface UnresolvableResponseType {
        @retrofit2.http.GET(value = "/")
        <T> retrofit2.Call<T> typeVariable();

        @retrofit2.http.GET(value = "/")
        <T extends okhttp3.ResponseBody> retrofit2.Call<T> typeVariableUpperBound();

        @retrofit2.http.GET(value = "/")
        <T> retrofit2.Call<java.util.List<java.util.Map<java.lang.String, java.util.Set<T[]>>>> crazy();

        @retrofit2.http.GET(value = "/")
        retrofit2.Call<?> wildcard();

        @retrofit2.http.GET(value = "/")
        retrofit2.Call<? extends okhttp3.ResponseBody> wildcardUpperBound();
    }

    interface UnresolvableParameterType {
        @retrofit2.http.POST(value = "/")
        <T> retrofit2.Call<okhttp3.ResponseBody> typeVariable(@retrofit2.http.Body
        T body);

        @retrofit2.http.POST(value = "/")
        <T extends okhttp3.RequestBody> retrofit2.Call<okhttp3.ResponseBody> typeVariableUpperBound(@retrofit2.http.Body
        T body);

        @retrofit2.http.POST(value = "/")
        <T> retrofit2.Call<okhttp3.ResponseBody> crazy(@retrofit2.http.Body
        java.util.List<java.util.Map<java.lang.String, java.util.Set<T[]>>> body);

        @retrofit2.http.POST(value = "/")
        retrofit2.Call<okhttp3.ResponseBody> wildcard(@retrofit2.http.Body
        java.util.List<?> body);

        @retrofit2.http.POST(value = "/")
        retrofit2.Call<okhttp3.ResponseBody> wildcardUpperBound(@retrofit2.http.Body
        java.util.List<? extends okhttp3.RequestBody> body);
    }

    interface VoidService {
        @retrofit2.http.GET(value = "/")
        void nope();
    }

    interface Annotated {
        @retrofit2.http.GET(value = "/")
        @retrofit2.AmplRetrofitTest.Annotated.Foo
        retrofit2.Call<java.lang.String> method();

        @retrofit2.http.POST(value = "/")
        retrofit2.Call<okhttp3.ResponseBody> bodyParameter(@retrofit2.AmplRetrofitTest.Annotated.Foo
        @retrofit2.http.Body
        java.lang.String param);

        @retrofit2.http.GET(value = "/")
        retrofit2.Call<okhttp3.ResponseBody> queryParameter(@retrofit2.AmplRetrofitTest.Annotated.Foo
        @retrofit2.http.Query(value = "foo")
        java.lang.Object foo);

        @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
        @interface Foo {        }
    }

    interface MutableParameters {
        @retrofit2.http.GET(value = "/")
        retrofit2.Call<java.lang.String> method(@retrofit2.http.Query(value = "i")
        java.util.concurrent.atomic.AtomicInteger value);
    }

    // We are explicitly testing this behavior.
    @java.lang.SuppressWarnings(value = "EqualsBetweenInconvertibleTypes")
    @org.junit.Test
    public void objectMethodsStillWork() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        retrofit2.AmplRetrofitTest.CallMethod example = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        org.assertj.core.api.Assertions.assertThat(example.hashCode()).isNotZero();
        org.assertj.core.api.Assertions.assertThat(example.equals(this)).isFalse();
        org.assertj.core.api.Assertions.assertThat(example.toString()).isNotEmpty();
    }

    @org.junit.Test
    public void interfaceWithExtendIsNotSupported() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        try {
            retrofit.create(retrofit2.AmplRetrofitTest.Extending.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("API interfaces must not extend other interfaces.");
        }
    }

    @org.junit.Test
    public void cloneSharesStatefulInstances() {
        retrofit2.CallAdapter.Factory callAdapter = org.mockito.Mockito.mock(retrofit2.CallAdapter.Factory.class);
        retrofit2.Converter.Factory converter = org.mockito.Mockito.mock(retrofit2.Converter.Factory.class);
        okhttp3.HttpUrl baseUrl = server.url("/");
        java.util.concurrent.Executor executor = org.mockito.Mockito.mock(java.util.concurrent.Executor.class);
        okhttp3.Call.Factory callFactory = org.mockito.Mockito.mock(okhttp3.Call.Factory.class);
        retrofit2.Retrofit one = new retrofit2.Retrofit.Builder().addCallAdapterFactory(callAdapter).addConverterFactory(converter).baseUrl(baseUrl).callbackExecutor(executor).callFactory(callFactory).build();
        retrofit2.CallAdapter.Factory callAdapter2 = org.mockito.Mockito.mock(retrofit2.CallAdapter.Factory.class);
        retrofit2.Converter.Factory converter2 = org.mockito.Mockito.mock(retrofit2.Converter.Factory.class);
        retrofit2.Retrofit two = one.newBuilder().addCallAdapterFactory(callAdapter2).addConverterFactory(converter2).build();
        org.junit.Assert.assertEquals(((one.callAdapterFactories().size()) + 1), two.callAdapterFactories().size());
        org.assertj.core.api.Assertions.assertThat(two.callAdapterFactories()).contains(callAdapter, callAdapter2);
        org.junit.Assert.assertEquals(((one.converterFactories().size()) + 1), two.converterFactories().size());
        org.assertj.core.api.Assertions.assertThat(two.converterFactories()).contains(converter, converter2);
        org.junit.Assert.assertSame(baseUrl, two.baseUrl());
        org.junit.Assert.assertSame(executor, two.callbackExecutor());
        org.junit.Assert.assertSame(callFactory, two.callFactory());
    }

    @org.junit.Test
    public void responseTypeCannotBeRetrofitResponse() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        retrofit2.AmplRetrofitTest.CallMethod service = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        try {
            service.badType1();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("\'retrofit2.Response\' is not a valid response body type. Did you mean ResponseBody?\n" + "    for method CallMethod.badType1"));
        }
    }

    @org.junit.Test
    public void responseTypeCannotBeOkHttpResponse() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        retrofit2.AmplRetrofitTest.CallMethod service = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        try {
            service.badType2();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("\'okhttp3.Response\' is not a valid response body type. Did you mean ResponseBody?\n" + "    for method CallMethod.badType2"));
        }
    }

    @org.junit.Test
    public void voidReturnTypeNotAllowed() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        retrofit2.AmplRetrofitTest.VoidService service = retrofit.create(retrofit2.AmplRetrofitTest.VoidService.class);
        try {
            service.nope();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessageStartingWith("Service methods cannot return void.\n    for method VoidService.nope");
        }
    }

    @org.junit.Test
    public void validateEagerlyDisabledByDefault() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        // Should not throw exception about incorrect configuration of the VoidService
        retrofit.create(retrofit2.AmplRetrofitTest.VoidService.class);
    }

    @org.junit.Test
    public void validateEagerlyDisabledByUser() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).validateEagerly(false).build();
        // Should not throw exception about incorrect configuration of the VoidService
        retrofit.create(retrofit2.AmplRetrofitTest.VoidService.class);
    }

    @org.junit.Test
    public void validateEagerlyFailsAtCreation() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).validateEagerly(true).build();
        try {
            retrofit.create(retrofit2.AmplRetrofitTest.VoidService.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessageStartingWith("Service methods cannot return void.\n    for method VoidService.nope");
        }
    }

    @org.junit.Test
    public void callCallAdapterAddedByDefault() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        retrofit2.AmplRetrofitTest.CallMethod example = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        org.assertj.core.api.Assertions.assertThat(example.getResponseBody()).isNotNull();
    }

    @org.junit.Test
    public void callCallCustomAdapter() {
        final java.util.concurrent.atomic.AtomicBoolean factoryCalled = new java.util.concurrent.atomic.AtomicBoolean();
        final java.util.concurrent.atomic.AtomicBoolean adapterCalled = new java.util.concurrent.atomic.AtomicBoolean();
        class MyCallAdapterFactory extends retrofit2.CallAdapter.Factory {
            @java.lang.Override
            public retrofit2.CallAdapter<?, ?> get(final java.lang.reflect.Type returnType, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                factoryCalled.set(true);
                if ((retrofit2.CallAdapter.Factory.getRawType(returnType)) != (retrofit2.Call.class)) {
                    return null;
                }
                return new retrofit2.CallAdapter<java.lang.Object, retrofit2.Call<?>>() {
                    @java.lang.Override
                    public java.lang.reflect.Type responseType() {
                        return retrofit2.CallAdapter.Factory.getParameterUpperBound(0, ((java.lang.reflect.ParameterizedType) (returnType)));
                    }

                    @java.lang.Override
                    public retrofit2.Call<java.lang.Object> adapt(retrofit2.Call<java.lang.Object> call) {
                        adapterCalled.set(true);
                        return call;
                    }
                };
            }
        }
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addCallAdapterFactory(new MyCallAdapterFactory()).build();
        retrofit2.AmplRetrofitTest.CallMethod example = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        org.assertj.core.api.Assertions.assertThat(example.getResponseBody()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(factoryCalled.get()).isTrue();
        org.assertj.core.api.Assertions.assertThat(adapterCalled.get()).isTrue();
    }

    @org.junit.Test
    public void customCallAdapter() {
        class GreetingCallAdapterFactory extends retrofit2.CallAdapter.Factory {
            @java.lang.Override
            public retrofit2.CallAdapter<java.lang.Object, java.lang.String> get(java.lang.reflect.Type returnType, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                if ((retrofit2.CallAdapter.Factory.getRawType(returnType)) != (java.lang.String.class)) {
                    return null;
                }
                return new retrofit2.CallAdapter<java.lang.Object, java.lang.String>() {
                    @java.lang.Override
                    public java.lang.reflect.Type responseType() {
                        return java.lang.String.class;
                    }

                    @java.lang.Override
                    public java.lang.String adapt(retrofit2.Call<java.lang.Object> call) {
                        return "Hi!";
                    }
                };
            }
        }
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).addCallAdapterFactory(new GreetingCallAdapterFactory()).build();
        retrofit2.AmplRetrofitTest.StringService example = retrofit.create(retrofit2.AmplRetrofitTest.StringService.class);
        org.assertj.core.api.Assertions.assertThat(example.get()).isEqualTo("Hi!");
    }

    @org.junit.Test
    public void methodAnnotationsPassedToCallAdapter() {
        final java.util.concurrent.atomic.AtomicReference<java.lang.annotation.Annotation[]> annotationsRef = new java.util.concurrent.atomic.AtomicReference<>();
        class MyCallAdapterFactory extends retrofit2.CallAdapter.Factory {
            @java.lang.Override
            public retrofit2.CallAdapter<?, ?> get(java.lang.reflect.Type returnType, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                annotationsRef.set(annotations);
                return null;
            }
        }
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).addCallAdapterFactory(new MyCallAdapterFactory()).build();
        retrofit2.AmplRetrofitTest.Annotated annotated = retrofit.create(retrofit2.AmplRetrofitTest.Annotated.class);
        annotated.method();// Trigger internal setup.
        
        java.lang.annotation.Annotation[] annotations = annotationsRef.get();
        org.assertj.core.api.Assertions.assertThat(annotations).hasAtLeastOneElementOfType(retrofit2.AmplRetrofitTest.Annotated.Foo.class);
    }

    @org.junit.Test
    public void customCallAdapterMissingThrows() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        retrofit2.AmplRetrofitTest.FutureMethod example = retrofit.create(retrofit2.AmplRetrofitTest.FutureMethod.class);
        try {
            example.method();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("" + ("Unable to create call adapter for java.util.concurrent.Future<java.lang.String>\n" + "    for method FutureMethod.method")));
            org.assertj.core.api.Assertions.assertThat(e.getCause()).hasMessage(("" + (("Could not locate call adapter for java.util.concurrent.Future<java.lang.String>.\n" + "  Tried:\n") + "   * retrofit2.DefaultCallAdapterFactory")));
        }
    }

    @org.junit.Test
    public void methodAnnotationsPassedToResponseBodyConverter() {
        final java.util.concurrent.atomic.AtomicReference<java.lang.annotation.Annotation[]> annotationsRef = new java.util.concurrent.atomic.AtomicReference<>();
        class MyConverterFactory extends retrofit2.Converter.Factory {
            @java.lang.Override
            public retrofit2.Converter<okhttp3.ResponseBody, ?> responseBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                annotationsRef.set(annotations);
                return new retrofit2.helpers.ToStringConverterFactory().responseBodyConverter(type, annotations, retrofit);
            }
        }
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new MyConverterFactory()).build();
        retrofit2.AmplRetrofitTest.Annotated annotated = retrofit.create(retrofit2.AmplRetrofitTest.Annotated.class);
        annotated.method();// Trigger internal setup.
        
        java.lang.annotation.Annotation[] annotations = annotationsRef.get();
        org.assertj.core.api.Assertions.assertThat(annotations).hasAtLeastOneElementOfType(retrofit2.AmplRetrofitTest.Annotated.Foo.class);
    }

    @org.junit.Test
    public void methodAndParameterAnnotationsPassedToRequestBodyConverter() {
        final java.util.concurrent.atomic.AtomicReference<java.lang.annotation.Annotation[]> parameterAnnotationsRef = new java.util.concurrent.atomic.AtomicReference<>();
        final java.util.concurrent.atomic.AtomicReference<java.lang.annotation.Annotation[]> methodAnnotationsRef = new java.util.concurrent.atomic.AtomicReference<>();
        class MyConverterFactory extends retrofit2.Converter.Factory {
            @java.lang.Override
            public retrofit2.Converter<?, okhttp3.RequestBody> requestBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] parameterAnnotations, java.lang.annotation.Annotation[] methodAnnotations, retrofit2.Retrofit retrofit) {
                parameterAnnotationsRef.set(parameterAnnotations);
                methodAnnotationsRef.set(methodAnnotations);
                return new retrofit2.helpers.ToStringConverterFactory().requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
            }
        }
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new MyConverterFactory()).build();
        retrofit2.AmplRetrofitTest.Annotated annotated = retrofit.create(retrofit2.AmplRetrofitTest.Annotated.class);
        annotated.bodyParameter(null);// Trigger internal setup.
        
        org.assertj.core.api.Assertions.assertThat(parameterAnnotationsRef.get()).hasAtLeastOneElementOfType(retrofit2.AmplRetrofitTest.Annotated.Foo.class);
        org.assertj.core.api.Assertions.assertThat(methodAnnotationsRef.get()).hasAtLeastOneElementOfType(retrofit2.http.POST.class);
    }

    @org.junit.Test
    public void parameterAnnotationsPassedToStringConverter() {
        final java.util.concurrent.atomic.AtomicReference<java.lang.annotation.Annotation[]> annotationsRef = new java.util.concurrent.atomic.AtomicReference<>();
        class MyConverterFactory extends retrofit2.Converter.Factory {
            @java.lang.Override
            public retrofit2.Converter<?, java.lang.String> stringConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                annotationsRef.set(annotations);
                return new retrofit2.Converter<java.lang.Object, java.lang.String>() {
                    @java.lang.Override
                    public java.lang.String convert(java.lang.Object value) throws java.io.IOException {
                        return java.lang.String.valueOf(value);
                    }
                };
            }
        }
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new MyConverterFactory()).build();
        retrofit2.AmplRetrofitTest.Annotated annotated = retrofit.create(retrofit2.AmplRetrofitTest.Annotated.class);
        annotated.queryParameter(null);// Trigger internal setup.
        
        java.lang.annotation.Annotation[] annotations = annotationsRef.get();
        org.assertj.core.api.Assertions.assertThat(annotations).hasAtLeastOneElementOfType(retrofit2.AmplRetrofitTest.Annotated.Foo.class);
    }

    @org.junit.Test
    public void stringConverterCalledForString() {
        final java.util.concurrent.atomic.AtomicBoolean factoryCalled = new java.util.concurrent.atomic.AtomicBoolean();
        class MyConverterFactory extends retrofit2.Converter.Factory {
            @java.lang.Override
            public retrofit2.Converter<?, java.lang.String> stringConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                factoryCalled.set(true);
                return null;
            }
        }
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new MyConverterFactory()).build();
        retrofit2.AmplRetrofitTest.CallMethod service = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        retrofit2.Call<okhttp3.ResponseBody> call = service.queryString(null);
        org.assertj.core.api.Assertions.assertThat(call).isNotNull();
        org.assertj.core.api.Assertions.assertThat(factoryCalled.get()).isTrue();
    }

    @org.junit.Test
    public void stringConverterReturningNullResultsInDefault() {
        final java.util.concurrent.atomic.AtomicBoolean factoryCalled = new java.util.concurrent.atomic.AtomicBoolean();
        class MyConverterFactory extends retrofit2.Converter.Factory {
            @java.lang.Override
            public retrofit2.Converter<?, java.lang.String> stringConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                factoryCalled.set(true);
                return null;
            }
        }
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new MyConverterFactory()).build();
        retrofit2.AmplRetrofitTest.CallMethod service = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        retrofit2.Call<okhttp3.ResponseBody> call = service.queryObject(null);
        org.assertj.core.api.Assertions.assertThat(call).isNotNull();
        org.assertj.core.api.Assertions.assertThat(factoryCalled.get()).isTrue();
    }

    @org.junit.Test
    public void missingConverterThrowsOnNonRequestBody() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        retrofit2.AmplRetrofitTest.CallMethod example = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        try {
            example.disallowed("Hi!");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("" + ("Unable to create @Body converter for class java.lang.String (parameter #1)\n" + "    for method CallMethod.disallowed")));
            org.assertj.core.api.Assertions.assertThat(e.getCause()).hasMessage(("" + (("Could not locate RequestBody converter for class java.lang.String.\n" + "  Tried:\n") + "   * retrofit2.BuiltInConverters")));
        }
    }

    @org.junit.Test
    public void missingConverterThrowsOnNonResponseBody() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        retrofit2.AmplRetrofitTest.CallMethod example = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        try {
            example.disallowed();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("" + ("Unable to create converter for class java.lang.String\n" + "    for method CallMethod.disallowed")));
            org.assertj.core.api.Assertions.assertThat(e.getCause()).hasMessage(("" + (("Could not locate ResponseBody converter for class java.lang.String.\n" + "  Tried:\n") + "   * retrofit2.BuiltInConverters")));
        }
    }

    @org.junit.Test
    public void requestBodyOutgoingAllowed() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        retrofit2.AmplRetrofitTest.CallMethod example = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        retrofit2.Response<okhttp3.ResponseBody> response = example.getResponseBody().execute();
        org.assertj.core.api.Assertions.assertThat(response.body().string()).isEqualTo("Hi");
    }

    @org.junit.Test
    public void voidOutgoingAllowed() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        retrofit2.AmplRetrofitTest.CallMethod example = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        retrofit2.Response<java.lang.Void> response = example.getVoid().execute();
        org.assertj.core.api.Assertions.assertThat(response.body()).isNull();
    }

    @org.junit.Test
    public void voidResponsesArePooled() throws java.lang.Exception {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        retrofit2.AmplRetrofitTest.CallMethod example = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("abc"));
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("def"));
        example.getVoid().execute();
        example.getVoid().execute();
        org.assertj.core.api.Assertions.assertThat(server.takeRequest().getSequenceNumber()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(server.takeRequest().getSequenceNumber()).isEqualTo(1);
    }

    @org.junit.Test
    public void responseBodyIncomingAllowed() throws java.io.IOException, java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).build();
        retrofit2.AmplRetrofitTest.CallMethod example = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "Hey");
        retrofit2.Response<okhttp3.ResponseBody> response = example.postRequestBody(body).execute();
        org.assertj.core.api.Assertions.assertThat(response.body().string()).isEqualTo("Hi");
        org.assertj.core.api.Assertions.assertThat(server.takeRequest().getBody().readUtf8()).isEqualTo("Hey");
    }

    @org.junit.Test
    public void unresolvableResponseTypeThrows() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplRetrofitTest.UnresolvableResponseType example = retrofit.create(retrofit2.AmplRetrofitTest.UnresolvableResponseType.class);
        try {
            example.typeVariable();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("Method return type must not include a type variable or wildcard: " + "retrofit2.Call<T>\n    for method UnresolvableResponseType.typeVariable"));
        }
        try {
            example.typeVariableUpperBound();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("Method return type must not include a type variable or wildcard: " + "retrofit2.Call<T>\n    for method UnresolvableResponseType.typeVariableUpperBound"));
        }
        try {
            example.crazy();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("Method return type must not include a type variable or wildcard: " + ("retrofit2.Call<java.util.List<java.util.Map<java.lang.String, java.util.Set<T[]>>>>\n" + "    for method UnresolvableResponseType.crazy")));
        }
        try {
            example.wildcard();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("Method return type must not include a type variable or wildcard: " + "retrofit2.Call<?>\n    for method UnresolvableResponseType.wildcard"));
        }
        try {
            example.wildcardUpperBound();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("Method return type must not include a type variable or wildcard: " + ("retrofit2.Call<? extends okhttp3.ResponseBody>\n" + "    for method UnresolvableResponseType.wildcardUpperBound")));
        }
    }

    @org.junit.Test
    public void unresolvableParameterTypeThrows() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplRetrofitTest.UnresolvableParameterType example = retrofit.create(retrofit2.AmplRetrofitTest.UnresolvableParameterType.class);
        try {
            example.typeVariable(null);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("Parameter type must not include a type variable or wildcard: " + "T (parameter #1)\n    for method UnresolvableParameterType.typeVariable"));
        }
        try {
            example.typeVariableUpperBound(null);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("Parameter type must not include a type variable or wildcard: " + "T (parameter #1)\n    for method UnresolvableParameterType.typeVariableUpperBound"));
        }
        try {
            example.crazy(null);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("Parameter type must not include a type variable or wildcard: " + ("java.util.List<java.util.Map<java.lang.String, java.util.Set<T[]>>> (parameter #1)\n" + "    for method UnresolvableParameterType.crazy")));
        }
        try {
            example.wildcard(null);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("Parameter type must not include a type variable or wildcard: " + "java.util.List<?> (parameter #1)\n    for method UnresolvableParameterType.wildcard"));
        }
        try {
            example.wildcardUpperBound(null);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("Parameter type must not include a type variable or wildcard: " + ("java.util.List<? extends okhttp3.RequestBody> (parameter #1)\n" + "    for method UnresolvableParameterType.wildcardUpperBound")));
        }
    }

    @org.junit.Test
    public void baseUrlRequired() {
        try {
            new retrofit2.Retrofit.Builder().build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Base URL required.");
        }
    }

    @org.junit.Test
    public void baseUrlNullThrows() {
        try {
            new retrofit2.Retrofit.Builder().baseUrl(((java.lang.String) (null)));
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("baseUrl == null");
        }
        try {
            new retrofit2.Retrofit.Builder().baseUrl(((okhttp3.HttpUrl) (null)));
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("baseUrl == null");
        }
    }

    @org.junit.Test
    public void baseUrlInvalidThrows() {
        try {
            new retrofit2.Retrofit.Builder().baseUrl("ftp://foo/bar");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Illegal URL: ftp://foo/bar");
        }
    }

    @org.junit.Test
    public void baseUrlNoTrailingSlashThrows() {
        try {
            new retrofit2.Retrofit.Builder().baseUrl("http://example.com/api");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("baseUrl must end in /: http://example.com/api");
        }
        okhttp3.HttpUrl parsed = okhttp3.HttpUrl.parse("http://example.com/api");
        try {
            new retrofit2.Retrofit.Builder().baseUrl(parsed);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("baseUrl must end in /: http://example.com/api");
        }
    }

    @org.junit.Test
    public void baseUrlStringPropagated() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").build();
        okhttp3.HttpUrl baseUrl = retrofit.baseUrl();
        org.assertj.core.api.Assertions.assertThat(baseUrl).isEqualTo(okhttp3.HttpUrl.parse("http://example.com/"));
    }

    @org.junit.Test
    public void baseHttpUrlPropagated() {
        okhttp3.HttpUrl url = okhttp3.HttpUrl.parse("http://example.com/");
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(url).build();
        org.assertj.core.api.Assertions.assertThat(retrofit.baseUrl()).isSameAs(url);
    }

    @org.junit.Test
    public void clientNullThrows() {
        try {
            new retrofit2.Retrofit.Builder().client(null);
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("client == null");
        }
    }

    @org.junit.Test
    public void callFactoryDefault() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com").build();
        org.assertj.core.api.Assertions.assertThat(retrofit.callFactory()).isNotNull();
    }

    @org.junit.Test
    public void callFactoryPropagated() {
        okhttp3.Call.Factory callFactory = org.mockito.Mockito.mock(okhttp3.Call.Factory.class);
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").callFactory(callFactory).build();
        org.assertj.core.api.Assertions.assertThat(retrofit.callFactory()).isSameAs(callFactory);
    }

    @org.junit.Test
    public void callFactoryClientPropagated() {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").client(client).build();
        org.assertj.core.api.Assertions.assertThat(retrofit.callFactory()).isSameAs(client);
    }

    @org.junit.Test
    public void callFactoryUsed() throws java.io.IOException {
        okhttp3.Call.Factory callFactory = org.mockito.Mockito.spy(new okhttp3.Call.Factory() {
            @java.lang.Override
            public okhttp3.Call newCall(okhttp3.Request request) {
                return new okhttp3.OkHttpClient().newCall(request);
            }
        });
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).callFactory(callFactory).build();
        server.enqueue(new okhttp3.mockwebserver.MockResponse());
        retrofit2.AmplRetrofitTest.CallMethod service = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        service.getResponseBody().execute();
        org.mockito.Mockito.verify(callFactory).newCall(org.mockito.Matchers.any(okhttp3.Request.class));
        org.mockito.Mockito.verifyNoMoreInteractions(callFactory);
    }

    @org.junit.Test
    public void callFactoryReturningNullThrows() throws java.io.IOException {
        okhttp3.Call.Factory callFactory = new okhttp3.Call.Factory() {
            @java.lang.Override
            public okhttp3.Call newCall(okhttp3.Request request) {
                return null;
            }
        };
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").callFactory(callFactory).build();
        server.enqueue(new okhttp3.mockwebserver.MockResponse());
        retrofit2.AmplRetrofitTest.CallMethod service = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        retrofit2.Call<okhttp3.ResponseBody> call = service.getResponseBody();
        try {
            call.execute();
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Call.Factory returned null.");
        }
    }

    @org.junit.Test
    public void callFactoryThrowingPropagates() {
        final java.lang.RuntimeException cause = new java.lang.RuntimeException("Broken!");
        okhttp3.Call.Factory callFactory = new okhttp3.Call.Factory() {
            @java.lang.Override
            public okhttp3.Call newCall(okhttp3.Request request) {
                throw cause;
            }
        };
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").callFactory(callFactory).build();
        server.enqueue(new okhttp3.mockwebserver.MockResponse());
        retrofit2.AmplRetrofitTest.CallMethod service = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        retrofit2.Call<okhttp3.ResponseBody> call = service.getResponseBody();
        try {
            call.execute();
            org.junit.Assert.fail();
        } catch (java.lang.Exception e) {
            org.assertj.core.api.Assertions.assertThat(e).isSameAs(cause);
        }
    }

    @org.junit.Test
    public void converterNullThrows() {
        try {
            new retrofit2.Retrofit.Builder().addConverterFactory(null);
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("factory == null");
        }
    }

    @org.junit.Test
    public void converterFactoryDefault() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").build();
        java.util.List<retrofit2.Converter.Factory> converterFactories = retrofit.converterFactories();
        org.assertj.core.api.Assertions.assertThat(converterFactories).hasSize(1);
        org.assertj.core.api.Assertions.assertThat(converterFactories.get(0)).isInstanceOf(retrofit2.BuiltInConverters.class);
    }

    @org.junit.Test
    public void requestConverterFactoryQueried() {
        java.lang.reflect.Type type = java.lang.String.class;
        java.lang.annotation.Annotation[] parameterAnnotations = new java.lang.annotation.Annotation[0];
        java.lang.annotation.Annotation[] methodAnnotations = new java.lang.annotation.Annotation[1];
        retrofit2.Converter<?, okhttp3.RequestBody> expectedAdapter = org.mockito.Mockito.mock(retrofit2.Converter.class);
        retrofit2.Converter.Factory factory = org.mockito.Mockito.mock(retrofit2.Converter.Factory.class);
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addConverterFactory(factory).build();
        org.mockito.Mockito.doReturn(expectedAdapter).when(factory).requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
        retrofit2.Converter<?, okhttp3.RequestBody> actualAdapter = retrofit.requestBodyConverter(type, parameterAnnotations, methodAnnotations);
        org.assertj.core.api.Assertions.assertThat(actualAdapter).isSameAs(expectedAdapter);
        org.mockito.Mockito.verify(factory).requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
        org.mockito.Mockito.verifyNoMoreInteractions(factory);
    }

    @org.junit.Test
    public void requestConverterFactoryNoMatchThrows() {
        java.lang.reflect.Type type = java.lang.String.class;
        java.lang.annotation.Annotation[] annotations = new java.lang.annotation.Annotation[0];
        retrofit2.helpers.NonMatchingConverterFactory nonMatchingFactory = new retrofit2.helpers.NonMatchingConverterFactory();
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addConverterFactory(nonMatchingFactory).build();
        try {
            retrofit.requestBodyConverter(type, annotations, annotations);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("" + ((("Could not locate RequestBody converter for class java.lang.String.\n" + "  Tried:\n") + "   * retrofit2.BuiltInConverters\n") + "   * retrofit2.helpers.NonMatchingConverterFactory")));
        }
        org.assertj.core.api.Assertions.assertThat(nonMatchingFactory.called).isTrue();
    }

    @org.junit.Test
    public void requestConverterFactorySkippedNoMatchThrows() {
        java.lang.reflect.Type type = java.lang.String.class;
        java.lang.annotation.Annotation[] annotations = new java.lang.annotation.Annotation[0];
        retrofit2.helpers.NonMatchingConverterFactory nonMatchingFactory1 = new retrofit2.helpers.NonMatchingConverterFactory();
        retrofit2.helpers.NonMatchingConverterFactory nonMatchingFactory2 = new retrofit2.helpers.NonMatchingConverterFactory();
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addConverterFactory(nonMatchingFactory1).addConverterFactory(nonMatchingFactory2).build();
        try {
            retrofit.nextRequestBodyConverter(nonMatchingFactory1, type, annotations, annotations);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("" + ((((("Could not locate RequestBody converter for class java.lang.String.\n" + "  Skipped:\n") + "   * retrofit2.BuiltInConverters\n") + "   * retrofit2.helpers.NonMatchingConverterFactory\n") + "  Tried:\n") + "   * retrofit2.helpers.NonMatchingConverterFactory")));
        }
        org.assertj.core.api.Assertions.assertThat(nonMatchingFactory1.called).isFalse();
        org.assertj.core.api.Assertions.assertThat(nonMatchingFactory2.called).isTrue();
    }

    @org.junit.Test
    public void responseConverterFactoryQueried() {
        java.lang.reflect.Type type = java.lang.String.class;
        java.lang.annotation.Annotation[] annotations = new java.lang.annotation.Annotation[0];
        retrofit2.Converter<okhttp3.ResponseBody, ?> expectedAdapter = org.mockito.Mockito.mock(retrofit2.Converter.class);
        retrofit2.Converter.Factory factory = org.mockito.Mockito.mock(retrofit2.Converter.Factory.class);
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addConverterFactory(factory).build();
        org.mockito.Mockito.doReturn(expectedAdapter).when(factory).responseBodyConverter(type, annotations, retrofit);
        retrofit2.Converter<okhttp3.ResponseBody, ?> actualAdapter = retrofit.responseBodyConverter(type, annotations);
        org.assertj.core.api.Assertions.assertThat(actualAdapter).isSameAs(expectedAdapter);
        org.mockito.Mockito.verify(factory).responseBodyConverter(type, annotations, retrofit);
        org.mockito.Mockito.verifyNoMoreInteractions(factory);
    }

    @org.junit.Test
    public void responseConverterFactoryNoMatchThrows() {
        java.lang.reflect.Type type = java.lang.String.class;
        java.lang.annotation.Annotation[] annotations = new java.lang.annotation.Annotation[0];
        retrofit2.helpers.NonMatchingConverterFactory nonMatchingFactory = new retrofit2.helpers.NonMatchingConverterFactory();
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addConverterFactory(nonMatchingFactory).build();
        try {
            retrofit.responseBodyConverter(type, annotations);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("" + ((("Could not locate ResponseBody converter for class java.lang.String.\n" + "  Tried:\n") + "   * retrofit2.BuiltInConverters\n") + "   * retrofit2.helpers.NonMatchingConverterFactory")));
        }
        org.assertj.core.api.Assertions.assertThat(nonMatchingFactory.called).isTrue();
    }

    @org.junit.Test
    public void responseConverterFactorySkippedNoMatchThrows() {
        java.lang.reflect.Type type = java.lang.String.class;
        java.lang.annotation.Annotation[] annotations = new java.lang.annotation.Annotation[0];
        retrofit2.helpers.NonMatchingConverterFactory nonMatchingFactory1 = new retrofit2.helpers.NonMatchingConverterFactory();
        retrofit2.helpers.NonMatchingConverterFactory nonMatchingFactory2 = new retrofit2.helpers.NonMatchingConverterFactory();
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addConverterFactory(nonMatchingFactory1).addConverterFactory(nonMatchingFactory2).build();
        try {
            retrofit.nextResponseBodyConverter(nonMatchingFactory1, type, annotations);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("" + ((((("Could not locate ResponseBody converter for class java.lang.String.\n" + "  Skipped:\n") + "   * retrofit2.BuiltInConverters\n") + "   * retrofit2.helpers.NonMatchingConverterFactory\n") + "  Tried:\n") + "   * retrofit2.helpers.NonMatchingConverterFactory")));
        }
        org.assertj.core.api.Assertions.assertThat(nonMatchingFactory1.called).isFalse();
        org.assertj.core.api.Assertions.assertThat(nonMatchingFactory2.called).isTrue();
    }

    @org.junit.Test
    public void stringConverterFactoryQueried() {
        java.lang.reflect.Type type = java.lang.Object.class;
        java.lang.annotation.Annotation[] annotations = new java.lang.annotation.Annotation[0];
        retrofit2.Converter<?, java.lang.String> expectedAdapter = org.mockito.Mockito.mock(retrofit2.Converter.class);
        retrofit2.Converter.Factory factory = org.mockito.Mockito.mock(retrofit2.Converter.Factory.class);
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addConverterFactory(factory).build();
        org.mockito.Mockito.doReturn(expectedAdapter).when(factory).stringConverter(type, annotations, retrofit);
        retrofit2.Converter<?, java.lang.String> actualAdapter = retrofit.stringConverter(type, annotations);
        org.assertj.core.api.Assertions.assertThat(actualAdapter).isSameAs(expectedAdapter);
        org.mockito.Mockito.verify(factory).stringConverter(type, annotations, retrofit);
        org.mockito.Mockito.verifyNoMoreInteractions(factory);
    }

    @org.junit.Test
    public void converterFactoryPropagated() {
        retrofit2.Converter.Factory factory = org.mockito.Mockito.mock(retrofit2.Converter.Factory.class);
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addConverterFactory(factory).build();
        org.assertj.core.api.Assertions.assertThat(retrofit.converterFactories()).contains(factory);
    }

    @org.junit.Test
    public void callAdapterFactoryNullThrows() {
        try {
            new retrofit2.Retrofit.Builder().addCallAdapterFactory(null);
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("factory == null");
        }
    }

    @org.junit.Test
    public void callAdapterFactoryDefault() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").build();
        org.assertj.core.api.Assertions.assertThat(retrofit.callAdapterFactories()).isNotEmpty();
    }

    @org.junit.Test
    public void callAdapterFactoryPropagated() {
        retrofit2.CallAdapter.Factory factory = org.mockito.Mockito.mock(retrofit2.CallAdapter.Factory.class);
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addCallAdapterFactory(factory).build();
        org.assertj.core.api.Assertions.assertThat(retrofit.callAdapterFactories()).contains(factory);
    }

    @org.junit.Test
    public void callAdapterFactoryQueried() {
        java.lang.reflect.Type type = java.lang.String.class;
        java.lang.annotation.Annotation[] annotations = new java.lang.annotation.Annotation[0];
        retrofit2.CallAdapter<?, ?> expectedAdapter = org.mockito.Mockito.mock(retrofit2.CallAdapter.class);
        retrofit2.CallAdapter.Factory factory = org.mockito.Mockito.mock(retrofit2.CallAdapter.Factory.class);
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addCallAdapterFactory(factory).build();
        org.mockito.Mockito.doReturn(expectedAdapter).when(factory).get(type, annotations, retrofit);
        retrofit2.CallAdapter<?, ?> actualAdapter = retrofit.callAdapter(type, annotations);
        org.assertj.core.api.Assertions.assertThat(actualAdapter).isSameAs(expectedAdapter);
        org.mockito.Mockito.verify(factory).get(type, annotations, retrofit);
        org.mockito.Mockito.verifyNoMoreInteractions(factory);
    }

    @org.junit.Test
    public void callAdapterFactoryQueriedCanDelegate() {
        java.lang.reflect.Type type = java.lang.String.class;
        java.lang.annotation.Annotation[] annotations = new java.lang.annotation.Annotation[0];
        retrofit2.CallAdapter<?, ?> expectedAdapter = org.mockito.Mockito.mock(retrofit2.CallAdapter.class);
        retrofit2.CallAdapter.Factory factory2 = org.mockito.Mockito.mock(retrofit2.CallAdapter.Factory.class);
        retrofit2.CallAdapter.Factory factory1 = org.mockito.Mockito.spy(new retrofit2.CallAdapter.Factory() {
            @java.lang.Override
            public retrofit2.CallAdapter<?, ?> get(java.lang.reflect.Type returnType, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                return retrofit.nextCallAdapter(this, returnType, annotations);
            }
        });
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addCallAdapterFactory(factory1).addCallAdapterFactory(factory2).build();
        org.mockito.Mockito.doReturn(expectedAdapter).when(factory2).get(type, annotations, retrofit);
        retrofit2.CallAdapter<?, ?> actualAdapter = retrofit.callAdapter(type, annotations);
        org.assertj.core.api.Assertions.assertThat(actualAdapter).isSameAs(expectedAdapter);
        org.mockito.Mockito.verify(factory1).get(type, annotations, retrofit);
        org.mockito.Mockito.verifyNoMoreInteractions(factory1);
        org.mockito.Mockito.verify(factory2).get(type, annotations, retrofit);
        org.mockito.Mockito.verifyNoMoreInteractions(factory2);
    }

    @org.junit.Test
    public void callAdapterFactoryQueriedCanDelegateTwiceWithoutRecursion() {
        java.lang.reflect.Type type = java.lang.String.class;
        java.lang.annotation.Annotation[] annotations = new java.lang.annotation.Annotation[0];
        retrofit2.CallAdapter<?, ?> expectedAdapter = org.mockito.Mockito.mock(retrofit2.CallAdapter.class);
        retrofit2.CallAdapter.Factory factory3 = org.mockito.Mockito.mock(retrofit2.CallAdapter.Factory.class);
        retrofit2.CallAdapter.Factory factory2 = org.mockito.Mockito.spy(new retrofit2.CallAdapter.Factory() {
            @java.lang.Override
            public retrofit2.CallAdapter<?, ?> get(java.lang.reflect.Type returnType, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                return retrofit.nextCallAdapter(this, returnType, annotations);
            }
        });
        retrofit2.CallAdapter.Factory factory1 = org.mockito.Mockito.spy(new retrofit2.CallAdapter.Factory() {
            @java.lang.Override
            public retrofit2.CallAdapter<?, ?> get(java.lang.reflect.Type returnType, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                return retrofit.nextCallAdapter(this, returnType, annotations);
            }
        });
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addCallAdapterFactory(factory1).addCallAdapterFactory(factory2).addCallAdapterFactory(factory3).build();
        org.mockito.Mockito.doReturn(expectedAdapter).when(factory3).get(type, annotations, retrofit);
        retrofit2.CallAdapter<?, ?> actualAdapter = retrofit.callAdapter(type, annotations);
        org.assertj.core.api.Assertions.assertThat(actualAdapter).isSameAs(expectedAdapter);
        org.mockito.Mockito.verify(factory1).get(type, annotations, retrofit);
        org.mockito.Mockito.verifyNoMoreInteractions(factory1);
        org.mockito.Mockito.verify(factory2).get(type, annotations, retrofit);
        org.mockito.Mockito.verifyNoMoreInteractions(factory2);
        org.mockito.Mockito.verify(factory3).get(type, annotations, retrofit);
        org.mockito.Mockito.verifyNoMoreInteractions(factory3);
    }

    @org.junit.Test
    public void callAdapterFactoryNoMatchThrows() {
        java.lang.reflect.Type type = java.lang.String.class;
        java.lang.annotation.Annotation[] annotations = new java.lang.annotation.Annotation[0];
        retrofit2.helpers.NonMatchingCallAdapterFactory nonMatchingFactory = new retrofit2.helpers.NonMatchingCallAdapterFactory();
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addCallAdapterFactory(nonMatchingFactory).build();
        try {
            retrofit.callAdapter(type, annotations);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("" + ((("Could not locate call adapter for class java.lang.String.\n" + "  Tried:\n") + "   * retrofit2.helpers.NonMatchingCallAdapterFactory\n") + "   * retrofit2.DefaultCallAdapterFactory")));
        }
        org.assertj.core.api.Assertions.assertThat(nonMatchingFactory.called).isTrue();
    }

    @org.junit.Test
    public void callAdapterFactoryDelegateNoMatchThrows() {
        java.lang.reflect.Type type = java.lang.String.class;
        java.lang.annotation.Annotation[] annotations = new java.lang.annotation.Annotation[0];
        retrofit2.helpers.DelegatingCallAdapterFactory delegatingFactory1 = new retrofit2.helpers.DelegatingCallAdapterFactory();
        retrofit2.helpers.DelegatingCallAdapterFactory delegatingFactory2 = new retrofit2.helpers.DelegatingCallAdapterFactory();
        retrofit2.helpers.NonMatchingCallAdapterFactory nonMatchingFactory = new retrofit2.helpers.NonMatchingCallAdapterFactory();
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addCallAdapterFactory(delegatingFactory1).addCallAdapterFactory(delegatingFactory2).addCallAdapterFactory(nonMatchingFactory).build();
        try {
            retrofit.callAdapter(type, annotations);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("" + (((((("Could not locate call adapter for class java.lang.String.\n" + "  Skipped:\n") + "   * retrofit2.helpers.DelegatingCallAdapterFactory\n") + "   * retrofit2.helpers.DelegatingCallAdapterFactory\n") + "  Tried:\n") + "   * retrofit2.helpers.NonMatchingCallAdapterFactory\n") + "   * retrofit2.DefaultCallAdapterFactory")));
        }
        org.assertj.core.api.Assertions.assertThat(delegatingFactory1.called).isTrue();
        org.assertj.core.api.Assertions.assertThat(delegatingFactory2.called).isTrue();
        org.assertj.core.api.Assertions.assertThat(nonMatchingFactory.called).isTrue();
    }

    @org.junit.Test
    public void callbackExecutorNullThrows() {
        try {
            new retrofit2.Retrofit.Builder().callbackExecutor(null);
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("executor == null");
        }
    }

    @org.junit.Test
    public void callbackExecutorPropagatesDefaultJvm() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").build();
        org.assertj.core.api.Assertions.assertThat(retrofit.callbackExecutor()).isNull();
    }

    @org.junit.Test
    public void callbackExecutorPropagatesDefaultAndroid() {
        final java.util.concurrent.Executor executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        retrofit2.Platform platform = new retrofit2.Platform() {
            @java.lang.Override
            java.util.concurrent.Executor defaultCallbackExecutor() {
                return executor;
            }
        };
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder(platform).baseUrl("http://example.com/").build();
        org.assertj.core.api.Assertions.assertThat(retrofit.callbackExecutor()).isSameAs(executor);
    }

    @org.junit.Test
    public void callbackExecutorPropagated() {
        java.util.concurrent.Executor executor = org.mockito.Mockito.mock(java.util.concurrent.Executor.class);
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").callbackExecutor(executor).build();
        org.assertj.core.api.Assertions.assertThat(retrofit.callbackExecutor()).isSameAs(executor);
    }

    @org.junit.Test
    public void callbackExecutorUsedForSuccess() throws java.lang.InterruptedException {
        java.util.concurrent.Executor executor = org.mockito.Mockito.spy(new java.util.concurrent.Executor() {
            @java.lang.Override
            public void execute(java.lang.Runnable command) {
                command.run();
            }
        });
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).callbackExecutor(executor).build();
        retrofit2.AmplRetrofitTest.CallMethod service = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        retrofit2.Call<okhttp3.ResponseBody> call = service.getResponseBody();
        server.enqueue(new okhttp3.mockwebserver.MockResponse());
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        call.enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                latch.countDown();
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, java.lang.Throwable t) {
                t.printStackTrace();
            }
        });
        org.junit.Assert.assertTrue(latch.await(2, java.util.concurrent.TimeUnit.SECONDS));
        org.mockito.Mockito.verify(executor).execute(org.mockito.Matchers.any(java.lang.Runnable.class));
        org.mockito.Mockito.verifyNoMoreInteractions(executor);
    }

    @org.junit.Test
    public void callbackExecutorUsedForFailure() throws java.lang.InterruptedException {
        java.util.concurrent.Executor executor = org.mockito.Mockito.spy(new java.util.concurrent.Executor() {
            @java.lang.Override
            public void execute(java.lang.Runnable command) {
                command.run();
            }
        });
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).callbackExecutor(executor).build();
        retrofit2.AmplRetrofitTest.CallMethod service = retrofit.create(retrofit2.AmplRetrofitTest.CallMethod.class);
        retrofit2.Call<okhttp3.ResponseBody> call = service.getResponseBody();
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START));
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        call.enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, java.lang.Throwable t) {
                latch.countDown();
            }
        });
        org.junit.Assert.assertTrue(latch.await(2, java.util.concurrent.TimeUnit.SECONDS));
        org.mockito.Mockito.verify(executor).execute(org.mockito.Matchers.any(java.lang.Runnable.class));
        org.mockito.Mockito.verifyNoMoreInteractions(executor);
    }

    /**
     * * Confirm that Retrofit encodes parameters when the call is executed, and not earlier.
     */
    @org.junit.Test
    public void argumentCapture() throws java.lang.Exception {
        java.util.concurrent.atomic.AtomicInteger i = new java.util.concurrent.atomic.AtomicInteger();
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("a"));
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("b"));
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplRetrofitTest.MutableParameters mutableParameters = retrofit.create(retrofit2.AmplRetrofitTest.MutableParameters.class);
        i.set(100);
        retrofit2.Call<java.lang.String> call1 = mutableParameters.method(i);
        i.set(101);
        retrofit2.Response<java.lang.String> response1 = call1.execute();
        i.set(102);
        org.junit.Assert.assertEquals("a", response1.body());
        org.junit.Assert.assertEquals("/?i=101", server.takeRequest().getPath());
        i.set(200);
        retrofit2.Call<java.lang.String> call2 = call1.clone();
        i.set(201);
        retrofit2.Response<java.lang.String> response2 = call2.execute();
        i.set(202);
        org.junit.Assert.assertEquals("b", response2.body());
        org.junit.Assert.assertEquals("/?i=201", server.takeRequest().getPath());
    }
}

