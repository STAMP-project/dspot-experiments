/**
 * Copyright (C) 2015 Square, Inc.
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


public final class AmplCallTest {
    @org.junit.Rule
    public final okhttp3.mockwebserver.MockWebServer server = new okhttp3.mockwebserver.MockWebServer();

    interface Service {
        @retrofit2.http.GET(value = "/")
        retrofit2.Call<java.lang.String> getString();

        @retrofit2.http.GET(value = "/")
        retrofit2.Call<okhttp3.ResponseBody> getBody();

        @retrofit2.http.GET(value = "/")
        @retrofit2.http.Streaming
        retrofit2.Call<okhttp3.ResponseBody> getStreamingBody();

        @retrofit2.http.POST(value = "/")
        retrofit2.Call<java.lang.String> postString(@retrofit2.http.Body
        java.lang.String body);

        @retrofit2.http.POST(value = "/{a}")
        retrofit2.Call<java.lang.String> postRequestBody(@retrofit2.http.Path(value = "a")
        java.lang.Object a);
    }

    @org.junit.Test
    public void http200Sync() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        retrofit2.Response<java.lang.String> response = example.getString().execute();
        org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isTrue();
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
    }

    @org.junit.Test
    public void http200Async() throws java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        final java.util.concurrent.atomic.AtomicReference<retrofit2.Response<java.lang.String>> responseRef = new java.util.concurrent.atomic.AtomicReference<>();
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        example.getString().enqueue(new retrofit2.Callback<java.lang.String>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                responseRef.set(response);
                latch.countDown();
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                t.printStackTrace();
            }
        });
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        retrofit2.Response<java.lang.String> response = responseRef.get();
        org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isTrue();
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
    }

    @org.junit.Test
    public void http404Sync() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setResponseCode(404).setBody("Hi"));
        retrofit2.Response<java.lang.String> response = example.getString().execute();
        org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isFalse();
        org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(404);
        org.assertj.core.api.Assertions.assertThat(response.errorBody().string()).isEqualTo("Hi");
    }

    @org.junit.Test
    public void http404Async() throws java.io.IOException, java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setResponseCode(404).setBody("Hi"));
        final java.util.concurrent.atomic.AtomicReference<retrofit2.Response<java.lang.String>> responseRef = new java.util.concurrent.atomic.AtomicReference<>();
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        example.getString().enqueue(new retrofit2.Callback<java.lang.String>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                responseRef.set(response);
                latch.countDown();
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                t.printStackTrace();
            }
        });
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        retrofit2.Response<java.lang.String> response = responseRef.get();
        org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isFalse();
        org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(404);
        org.assertj.core.api.Assertions.assertThat(response.errorBody().string()).isEqualTo("Hi");
    }

    @org.junit.Test
    public void transportProblemSync() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START));
        retrofit2.Call<java.lang.String> call = example.getString();
        try {
            call.execute();
            org.junit.Assert.fail();
        } catch (java.io.IOException ignored) {
        }
    }

    @org.junit.Test
    public void transportProblemAsync() throws java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START));
        final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        example.getString().enqueue(new retrofit2.Callback<java.lang.String>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                failureRef.set(t);
                latch.countDown();
            }
        });
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        java.lang.Throwable failure = failureRef.get();
        org.assertj.core.api.Assertions.assertThat(failure).isInstanceOf(java.io.IOException.class);
    }

    @org.junit.Test
    public void conversionProblemOutgoingSync() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
            @java.lang.Override
            public retrofit2.Converter<?, okhttp3.RequestBody> requestBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] parameterAnnotations, java.lang.annotation.Annotation[] methodAnnotations, retrofit2.Retrofit retrofit) {
                return new retrofit2.Converter<java.lang.String, okhttp3.RequestBody>() {
                    @java.lang.Override
                    public okhttp3.RequestBody convert(java.lang.String value) throws java.io.IOException {
                        throw new java.lang.UnsupportedOperationException("I am broken!");
                    }
                };
            }
        }).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        retrofit2.Call<java.lang.String> call = example.postString("Hi");
        try {
            call.execute();
            org.junit.Assert.fail();
        } catch (java.lang.UnsupportedOperationException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("I am broken!");
        }
    }

    @org.junit.Test
    public void conversionProblemOutgoingAsync() throws java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
            @java.lang.Override
            public retrofit2.Converter<?, okhttp3.RequestBody> requestBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] parameterAnnotations, java.lang.annotation.Annotation[] methodAnnotations, retrofit2.Retrofit retrofit) {
                return new retrofit2.Converter<java.lang.String, okhttp3.RequestBody>() {
                    @java.lang.Override
                    public okhttp3.RequestBody convert(java.lang.String value) throws java.io.IOException {
                        throw new java.lang.UnsupportedOperationException("I am broken!");
                    }
                };
            }
        }).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        example.postString("Hi").enqueue(new retrofit2.Callback<java.lang.String>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                failureRef.set(t);
                latch.countDown();
            }
        });
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.lang.UnsupportedOperationException.class).hasMessage("I am broken!");
    }

    @org.junit.Test
    public void conversionProblemIncomingSync() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
            @java.lang.Override
            public retrofit2.Converter<okhttp3.ResponseBody, ?> responseBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                return new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
                    @java.lang.Override
                    public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                        throw new java.lang.UnsupportedOperationException("I am broken!");
                    }
                };
            }
        }).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        retrofit2.Call<java.lang.String> call = example.postString("Hi");
        try {
            call.execute();
            org.junit.Assert.fail();
        } catch (java.lang.UnsupportedOperationException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("I am broken!");
        }
    }

    @org.junit.Test
    public void conversionProblemIncomingMaskedByConverterIsUnwrapped() throws java.io.IOException {
        // MWS has no way to trigger IOExceptions during the response body so use an interceptor.
        okhttp3.OkHttpClient client = // 
        new okhttp3.OkHttpClient.Builder().addInterceptor(new okhttp3.Interceptor() {
            @java.lang.Override
            public okhttp3.Response intercept(okhttp3.Interceptor.Chain chain) throws java.io.IOException {
                okhttp3.Response response = chain.proceed(chain.request());
                okhttp3.ResponseBody body = response.body();
                okio.BufferedSource source = okio.Okio.buffer(new okio.ForwardingSource(body.source()) {
                    @java.lang.Override
                    public long read(okio.Buffer sink, long byteCount) throws java.io.IOException {
                        throw new java.io.IOException("cause");
                    }
                });
                body = okhttp3.ResponseBody.create(body.contentType(), body.contentLength(), source);
                return response.newBuilder().body(body).build();
            }
        }).build();
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).client(client).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
            @java.lang.Override
            public retrofit2.Converter<okhttp3.ResponseBody, ?> responseBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                return new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
                    @java.lang.Override
                    public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                        try {
                            return value.string();
                        } catch (java.io.IOException e) {
                            // Some serialization libraries mask transport problems in runtime exceptions. Bad!
                            throw new java.lang.RuntimeException("wrapper", e);
                        }
                    }
                };
            }
        }).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        retrofit2.Call<java.lang.String> call = example.getString();
        try {
            call.execute();
            org.junit.Assert.fail();
        } catch (java.io.IOException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("cause");
        }
    }

    @org.junit.Test
    public void conversionProblemIncomingAsync() throws java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
            @java.lang.Override
            public retrofit2.Converter<okhttp3.ResponseBody, ?> responseBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                return new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
                    @java.lang.Override
                    public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                        throw new java.lang.UnsupportedOperationException("I am broken!");
                    }
                };
            }
        }).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        example.postString("Hi").enqueue(new retrofit2.Callback<java.lang.String>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                failureRef.set(t);
                latch.countDown();
            }
        });
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.lang.UnsupportedOperationException.class).hasMessage("I am broken!");
    }

    @org.junit.Test
    public void http204SkipsConverter() throws java.io.IOException {
        final retrofit2.Converter<okhttp3.ResponseBody, java.lang.String> converter = org.mockito.Mockito.spy(new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
            @java.lang.Override
            public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                return value.string();
            }
        });
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
            @java.lang.Override
            public retrofit2.Converter<okhttp3.ResponseBody, ?> responseBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                return converter;
            }
        }).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setStatus("HTTP/1.1 204 Nothin"));
        retrofit2.Response<java.lang.String> response = example.getString().execute();
        org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(204);
        org.assertj.core.api.Assertions.assertThat(response.body()).isNull();
        org.mockito.Mockito.verifyNoMoreInteractions(converter);
    }

    @org.junit.Test
    public void http205SkipsConverter() throws java.io.IOException {
        final retrofit2.Converter<okhttp3.ResponseBody, java.lang.String> converter = org.mockito.Mockito.spy(new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
            @java.lang.Override
            public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                return value.string();
            }
        });
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
            @java.lang.Override
            public retrofit2.Converter<okhttp3.ResponseBody, ?> responseBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                return converter;
            }
        }).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setStatus("HTTP/1.1 205 Nothin"));
        retrofit2.Response<java.lang.String> response = example.getString().execute();
        org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(205);
        org.assertj.core.api.Assertions.assertThat(response.body()).isNull();
        org.mockito.Mockito.verifyNoMoreInteractions(converter);
    }

    @org.junit.Test
    public void executeCallOnce() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse());
        retrofit2.Call<java.lang.String> call = example.getString();
        call.execute();
        try {
            call.execute();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Already executed.");
        }
    }

    @org.junit.Test
    public void successfulRequestResponseWhenMimeTypeMissing() throws java.lang.Exception {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi").removeHeader("Content-Type"));
        retrofit2.Response<java.lang.String> response = example.getString().execute();
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
    }

    @org.junit.Test
    public void responseBody() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("1234"));
        retrofit2.Response<okhttp3.ResponseBody> response = example.getBody().execute();
        org.assertj.core.api.Assertions.assertThat(response.body().string()).isEqualTo("1234");
    }

    @org.junit.Test
    public void responseBodyBuffers() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("1234").setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY));
        retrofit2.Call<okhttp3.ResponseBody> buffered = example.getBody();
        // When buffering we will detect all socket problems before returning the Response.
        try {
            buffered.execute();
            org.junit.Assert.fail();
        } catch (java.io.IOException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("unexpected end of stream");
        }
    }

    @org.junit.Test
    public void responseBodyStreams() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("1234").setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY));
        retrofit2.Response<okhttp3.ResponseBody> response = example.getStreamingBody().execute();
        okhttp3.ResponseBody streamedBody = response.body();
        // When streaming we only detect socket problems as the ResponseBody is read.
        try {
            streamedBody.string();
            org.junit.Assert.fail();
        } catch (java.io.IOException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("unexpected end of stream");
        }
    }

    @org.junit.Test
    public void rawResponseContentTypeAndLengthButNoSource() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi").addHeader("Content-Type", "text/greeting"));
        retrofit2.Response<java.lang.String> response = example.getString().execute();
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
        okhttp3.ResponseBody rawBody = response.raw().body();
        org.assertj.core.api.Assertions.assertThat(rawBody.contentLength()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(rawBody.contentType().toString()).isEqualTo("text/greeting");
        try {
            rawBody.source();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Cannot read raw response body of a converted body.");
        }
    }

    @org.junit.Test
    public void emptyResponse() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("").addHeader("Content-Type", "text/stringy"));
        retrofit2.Response<java.lang.String> response = example.getString().execute();
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("");
        okhttp3.ResponseBody rawBody = response.raw().body();
        org.assertj.core.api.Assertions.assertThat(rawBody.contentLength()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(rawBody.contentType().toString()).isEqualTo("text/stringy");
    }

    @org.junit.Test
    public void reportsExecutedSync() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        retrofit2.Call<java.lang.String> call = example.getString();
        org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isFalse();
        call.execute();
        org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isTrue();
    }

    @org.junit.Test
    public void reportsExecutedAsync() throws java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        retrofit2.Call<java.lang.String> call = example.getString();
        org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isFalse();
        call.enqueue(new retrofit2.Callback<java.lang.String>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
            }
        });
        org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isTrue();
    }

    @org.junit.Test
    public void cancelBeforeExecute() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        retrofit2.Call<java.lang.String> call = service.getString();
        call.cancel();
        org.assertj.core.api.Assertions.assertThat(call.isCanceled()).isTrue();
        try {
            call.execute();
            org.junit.Assert.fail();
        } catch (java.io.IOException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Canceled");
        }
    }

    @org.junit.Test
    public void cancelBeforeEnqueue() throws java.lang.Exception {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        retrofit2.Call<java.lang.String> call = service.getString();
        call.cancel();
        org.assertj.core.api.Assertions.assertThat(call.isCanceled()).isTrue();
        final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        call.enqueue(new retrofit2.Callback<java.lang.String>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                failureRef.set(t);
                latch.countDown();
            }
        });
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        org.assertj.core.api.Assertions.assertThat(failureRef.get()).hasMessage("Canceled");
    }

    @org.junit.Test
    public void cloningExecutedRequestDoesNotCopyState() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hello"));
        retrofit2.Call<java.lang.String> call = service.getString();
        org.assertj.core.api.Assertions.assertThat(call.execute().body()).isEqualTo("Hi");
        retrofit2.Call<java.lang.String> cloned = call.clone();
        org.assertj.core.api.Assertions.assertThat(cloned.execute().body()).isEqualTo("Hello");
    }

    @org.junit.Test
    public void cancelRequest() throws java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE));
        retrofit2.Call<java.lang.String> call = service.getString();
        final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        call.enqueue(new retrofit2.Callback<java.lang.String>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                failureRef.set(t);
                latch.countDown();
            }
        });
        call.cancel();
        org.assertj.core.api.Assertions.assertThat(call.isCanceled()).isTrue();
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
    }

    @org.junit.Test
    public void cancelOkHttpRequest() throws java.lang.InterruptedException {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).client(client).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE));
        retrofit2.Call<java.lang.String> call = service.getString();
        final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        call.enqueue(new retrofit2.Callback<java.lang.String>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                failureRef.set(t);
                latch.countDown();
            }
        });
        // Cancel the underlying HTTP Call. Should be reflected accurately back in the Retrofit Call.
        client.dispatcher().cancelAll();
        org.assertj.core.api.Assertions.assertThat(call.isCanceled()).isTrue();
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
    }

    @org.junit.Test
    public void requestBeforeExecuteCreates() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse());
        final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
        java.lang.Object a = new java.lang.Object() {
            @java.lang.Override
            public java.lang.String toString() {
                writeCount.incrementAndGet();
                return "Hello";
            }
        };
        retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
        call.request();
        org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
        call.execute();
        org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
    }

    @org.junit.Test
    public void requestThrowingBeforeExecuteFailsExecute() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse());
        final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
        java.lang.Object a = new java.lang.Object() {
            @java.lang.Override
            public java.lang.String toString() {
                writeCount.incrementAndGet();
                throw new java.lang.RuntimeException("Broken!");
            }
        };
        retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
        try {
            call.request();
            org.junit.Assert.fail();
        } catch (java.lang.RuntimeException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Broken!");
        }
        org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
        try {
            call.execute();
            org.junit.Assert.fail();
        } catch (java.lang.RuntimeException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Broken!");
        }
        org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
    }

    @org.junit.Test
    public void requestAfterExecuteReturnsCachedValue() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse());
        final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
        java.lang.Object a = new java.lang.Object() {
            @java.lang.Override
            public java.lang.String toString() {
                writeCount.incrementAndGet();
                return "Hello";
            }
        };
        retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
        call.execute();
        org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
        call.request();
        org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
    }

    @org.junit.Test
    public void requestAfterExecuteThrowingAlsoThrows() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse());
        final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
        java.lang.Object a = new java.lang.Object() {
            @java.lang.Override
            public java.lang.String toString() {
                writeCount.incrementAndGet();
                throw new java.lang.RuntimeException("Broken!");
            }
        };
        retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
        try {
            call.execute();
            org.junit.Assert.fail();
        } catch (java.lang.RuntimeException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Broken!");
        }
        org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
        try {
            call.request();
            org.junit.Assert.fail();
        } catch (java.lang.RuntimeException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Broken!");
        }
        org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
    }

    @org.junit.Test
    public void requestBeforeEnqueueCreates() throws java.io.IOException, java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse());
        final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
        java.lang.Object a = new java.lang.Object() {
            @java.lang.Override
            public java.lang.String toString() {
                writeCount.incrementAndGet();
                return "Hello";
            }
        };
        retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
        call.request();
        org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        call.enqueue(new retrofit2.Callback<java.lang.String>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                latch.countDown();
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
            }
        });
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
    }

    @org.junit.Test
    public void requestThrowingBeforeEnqueueFailsEnqueue() throws java.io.IOException, java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse());
        final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
        java.lang.Object a = new java.lang.Object() {
            @java.lang.Override
            public java.lang.String toString() {
                writeCount.incrementAndGet();
                throw new java.lang.RuntimeException("Broken!");
            }
        };
        retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
        try {
            call.request();
            org.junit.Assert.fail();
        } catch (java.lang.RuntimeException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Broken!");
        }
        org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        call.enqueue(new retrofit2.Callback<java.lang.String>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                org.assertj.core.api.Assertions.assertThat(t).isExactlyInstanceOf(java.lang.RuntimeException.class).hasMessage("Broken!");
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                latch.countDown();
            }
        });
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
    }

    @org.junit.Test
    public void requestAfterEnqueueReturnsCachedValue() throws java.io.IOException, java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse());
        final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
        java.lang.Object a = new java.lang.Object() {
            @java.lang.Override
            public java.lang.String toString() {
                writeCount.incrementAndGet();
                return "Hello";
            }
        };
        retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        call.enqueue(new retrofit2.Callback<java.lang.String>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                latch.countDown();
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
            }
        });
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        call.request();
        org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
    }

    @org.junit.Test
    public void requestAfterEnqueueFailingThrows() throws java.io.IOException, java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse());
        final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
        java.lang.Object a = new java.lang.Object() {
            @java.lang.Override
            public java.lang.String toString() {
                writeCount.incrementAndGet();
                throw new java.lang.RuntimeException("Broken!");
            }
        };
        retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        call.enqueue(new retrofit2.Callback<java.lang.String>() {
            @java.lang.Override
            public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
            }

            @java.lang.Override
            public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                org.assertj.core.api.Assertions.assertThat(t).isExactlyInstanceOf(java.lang.RuntimeException.class).hasMessage("Broken!");
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                latch.countDown();
            }
        });
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        try {
            call.request();
            org.junit.Assert.fail();
        } catch (java.lang.RuntimeException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Broken!");
        }
        org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
    }
}

