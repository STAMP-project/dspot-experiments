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

    /* amplification of retrofit2.CallTest#cancelBeforeEnqueue */
    @org.junit.Test
    public void cancelBeforeEnqueue_literalMutation7() throws java.lang.Exception {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#cancelBeforeEnqueue */
    @org.junit.Test
    public void cancelBeforeEnqueue_literalMutation8_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("-")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
            // MethodAssertGenerator build local variable
            Object o_30_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).hasMessage("Canceled");
            org.junit.Assert.fail("cancelBeforeEnqueue_literalMutation8 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelBeforeEnqueue */
    @org.junit.Test(timeout = 10000)
    public void cancelBeforeEnqueue_cf28() throws java.lang.Exception {
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
        // AssertGenerator replace invocation
        okhttp3.Request o_cancelBeforeEnqueue_cf28__31 = // StatementAdderMethod cloned existing statement
call.request();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((okhttp3.Request)o_cancelBeforeEnqueue_cf28__31).method(), "GET");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((okhttp3.Request)o_cancelBeforeEnqueue_cf28__31).body());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((okhttp3.Request)o_cancelBeforeEnqueue_cf28__31).isHttps());
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        org.assertj.core.api.Assertions.assertThat(failureRef.get()).hasMessage("Canceled");
    }

    /* amplification of retrofit2.CallTest#cancelBeforeEnqueue */
    @org.junit.Test(timeout = 10000)
    public void cancelBeforeEnqueue_add3_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
            retrofit2.Call<java.lang.String> call = service.getString();
            call.cancel();
            org.assertj.core.api.Assertions.assertThat(call.isCanceled()).isTrue();
            final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
            final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
            // MethodCallAdder
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
            // MethodAssertGenerator build local variable
            Object o_42_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).hasMessage("Canceled");
            org.junit.Assert.fail("cancelBeforeEnqueue_add3 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelBeforeEnqueue */
    @org.junit.Test(timeout = 10000)
    public void cancelBeforeEnqueue_cf24_failAssert11_literalMutation293_failAssert28() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("h")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_2 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_2.isExecuted();
                // MethodAssertGenerator build local variable
                Object o_34_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                org.assertj.core.api.Assertions.assertThat(failureRef.get()).hasMessage("Canceled");
                org.junit.Assert.fail("cancelBeforeEnqueue_cf24 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cancelBeforeEnqueue_cf24_failAssert11_literalMutation293 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelBeforeEnqueue */
    @org.junit.Test(timeout = 10000)
    public void cancelBeforeEnqueue_cf24_failAssert11_literalMutation292() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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
            // StatementAdderOnAssert create null value
            retrofit2.Call vc_2 = (retrofit2.Call)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2);
            // StatementAdderMethod cloned existing statement
            vc_2.isExecuted();
            // MethodAssertGenerator build local variable
            Object o_34_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).hasMessage("Canceled");
            org.junit.Assert.fail("cancelBeforeEnqueue_cf24 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelBeforeEnqueue */
    @org.junit.Test(timeout = 10000)
    public void cancelBeforeEnqueue_cf36_failAssert16_add392_failAssert11() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
                retrofit2.Call<java.lang.String> call = service.getString();
                call.cancel();
                org.assertj.core.api.Assertions.assertThat(call.isCanceled()).isTrue();
                final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
                final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
                // MethodCallAdder
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_10 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_10.cancel();
                // MethodAssertGenerator build local variable
                Object o_34_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                org.assertj.core.api.Assertions.assertThat(failureRef.get()).hasMessage("Canceled");
                org.junit.Assert.fail("cancelBeforeEnqueue_cf36 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cancelBeforeEnqueue_cf36_failAssert16_add392 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelBeforeEnqueue */
    @org.junit.Test(timeout = 10000)
    public void cancelBeforeEnqueue_cf30_failAssert13_literalMutation347_add3308_failAssert2() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
                retrofit2.Call<java.lang.String> call = service.getString();
                call.cancel();
                org.assertj.core.api.Assertions.assertThat(call.isCanceled()).isTrue();
                final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
                final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
                // MethodCallAdder
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_6 = (retrofit2.Call)null;
                // MethodAssertGenerator build local variable
                Object o_46_0 = vc_6;
                // StatementAdderMethod cloned existing statement
                vc_6.clone();
                // MethodAssertGenerator build local variable
                Object o_34_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                org.assertj.core.api.Assertions.assertThat(failureRef.get()).hasMessage("C3nceled");
                org.junit.Assert.fail("cancelBeforeEnqueue_cf30 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cancelBeforeEnqueue_cf30_failAssert13_literalMutation347_add3308 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelBeforeEnqueue */
    @org.junit.Test(timeout = 10000)
    public void cancelBeforeEnqueue_cf33_failAssert14_literalMutation359_literalMutation751_failAssert9() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("+")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
                retrofit2.Call<java.lang.String> call = service.getString();
                call.cancel();
                org.assertj.core.api.Assertions.assertThat(call.isCanceled()).isTrue();
                final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
                final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(0);
                // MethodAssertGenerator build local variable
                Object o_22_0 = ((java.util.concurrent.CountDownLatch)latch).getCount();
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_8 = (retrofit2.Call)null;
                // MethodAssertGenerator build local variable
                Object o_37_0 = vc_8;
                // StatementAdderMethod cloned existing statement
                vc_8.execute();
                // MethodAssertGenerator build local variable
                Object o_34_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                org.assertj.core.api.Assertions.assertThat(failureRef.get()).hasMessage("Canceled");
                org.junit.Assert.fail("cancelBeforeEnqueue_cf33 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cancelBeforeEnqueue_cf33_failAssert14_literalMutation359_literalMutation751 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelBeforeExecute */
    @org.junit.Test
    public void cancelBeforeExecute_literalMutation3938() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#cancelOkHttpRequest */
    @org.junit.Test(timeout = 10000)
    public void cancelOkHttpRequest_add4831_failAssert0() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).client(client).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE));
            retrofit2.Call<java.lang.String> call = service.getString();
            final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
            final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
            // MethodCallAdder
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
            // MethodAssertGenerator build local variable
            Object o_50_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
            org.junit.Assert.fail("cancelOkHttpRequest_add4831 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelOkHttpRequest */
    @org.junit.Test
    public void cancelOkHttpRequest_literalMutation4837_failAssert1() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("@")).client(client).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
            // MethodAssertGenerator build local variable
            Object o_38_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
            org.junit.Assert.fail("cancelOkHttpRequest_literalMutation4837 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelOkHttpRequest */
    @org.junit.Test(timeout = 10000)
    public void cancelOkHttpRequest_cf4860_failAssert13_add4987_failAssert8() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).client(client).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE));
                retrofit2.Call<java.lang.String> call = service.getString();
                final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
                final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
                // MethodCallAdder
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_1526 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_1526.clone();
                // MethodAssertGenerator build local variable
                Object o_42_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
                org.junit.Assert.fail("cancelOkHttpRequest_cf4860 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cancelOkHttpRequest_cf4860_failAssert13_add4987 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelOkHttpRequest */
    @org.junit.Test(timeout = 10000)
    public void cancelOkHttpRequest_cf4860_failAssert13_literalMutation4994() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).client(client).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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
            // StatementAdderOnAssert create null value
            retrofit2.Call vc_1526 = (retrofit2.Call)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1526);
            // StatementAdderMethod cloned existing statement
            vc_1526.clone();
            // MethodAssertGenerator build local variable
            Object o_42_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
            org.junit.Assert.fail("cancelOkHttpRequest_cf4860 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelOkHttpRequest */
    @org.junit.Test(timeout = 10000)
    public void cancelOkHttpRequest_cf4866_failAssert14_literalMutation5017_failAssert11() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("C")).client(client).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_1530 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_1530.cancel();
                // MethodAssertGenerator build local variable
                Object o_42_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
                org.junit.Assert.fail("cancelOkHttpRequest_cf4866 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cancelOkHttpRequest_cf4866_failAssert14_literalMutation5017 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelOkHttpRequest */
    @org.junit.Test(timeout = 10000)
    public void cancelOkHttpRequest_cf4851_failAssert10_literalMutation4930_add5548_failAssert9() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).client(client).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE));
                retrofit2.Call<java.lang.String> call = service.getString();
                final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
                final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(2);
                // MethodAssertGenerator build local variable
                Object o_25_0 = ((java.util.concurrent.CountDownLatch)latch).getCount();
                // MethodCallAdder
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_1520 = (retrofit2.Call)null;
                // MethodAssertGenerator build local variable
                Object o_57_0 = vc_1520;
                // StatementAdderMethod cloned existing statement
                vc_1520.isCanceled();
                // MethodAssertGenerator build local variable
                Object o_42_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
                org.junit.Assert.fail("cancelOkHttpRequest_cf4851 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cancelOkHttpRequest_cf4851_failAssert10_literalMutation4930_add5548 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelOkHttpRequest */
    @org.junit.Test(timeout = 10000)
    public void cancelOkHttpRequest_cf4860_failAssert13_add4988_literalMutation7917_failAssert0() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("-")).client(client).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
                        // MethodCallAdder
                        failureRef.set(t);
                        failureRef.set(t);
                        latch.countDown();
                    }
                });
                // Cancel the underlying HTTP Call. Should be reflected accurately back in the Retrofit Call.
                client.dispatcher().cancelAll();
                org.assertj.core.api.Assertions.assertThat(call.isCanceled()).isTrue();
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_1526 = (retrofit2.Call)null;
                // MethodAssertGenerator build local variable
                Object o_44_0 = vc_1526;
                // StatementAdderMethod cloned existing statement
                vc_1526.clone();
                // MethodAssertGenerator build local variable
                Object o_42_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
                org.junit.Assert.fail("cancelOkHttpRequest_cf4860 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cancelOkHttpRequest_cf4860_failAssert13_add4988_literalMutation7917 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelRequest */
    @org.junit.Test
    public void cancelRequest_literalMutation9891_failAssert1() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("M")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
            // MethodAssertGenerator build local variable
            Object o_33_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
            org.junit.Assert.fail("cancelRequest_literalMutation9891 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelRequest */
    @org.junit.Test(timeout = 10000)
    public void cancelRequest_cf9911() throws java.lang.InterruptedException {
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
        // AssertGenerator replace invocation
        okhttp3.Request o_cancelRequest_cf9911__34 = // StatementAdderMethod cloned existing statement
call.request();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((okhttp3.Request)o_cancelRequest_cf9911__34).body());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((okhttp3.Request)o_cancelRequest_cf9911__34).isHttps());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((okhttp3.Request)o_cancelRequest_cf9911__34).method(), "GET");
        org.junit.Assert.assertTrue(latch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
    }

    /* amplification of retrofit2.CallTest#cancelRequest */
    @org.junit.Test(timeout = 10000)
    public void cancelRequest_add9884_failAssert0() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE));
            retrofit2.Call<java.lang.String> call = service.getString();
            final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
            final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
            // MethodCallAdder
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
            // MethodAssertGenerator build local variable
            Object o_45_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
            org.junit.Assert.fail("cancelRequest_add9884 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelRequest */
    @org.junit.Test
    public void cancelRequest_literalMutation9890() throws java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#cancelRequest */
    @org.junit.Test(timeout = 10000)
    public void cancelRequest_cf9905_literalMutation9981_failAssert3() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("]")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
            // AssertGenerator replace invocation
            boolean o_cancelRequest_cf9905__34 = // StatementAdderMethod cloned existing statement
call.isCanceled();
            // MethodAssertGenerator build local variable
            Object o_35_0 = o_cancelRequest_cf9905__34;
            // MethodAssertGenerator build local variable
            Object o_37_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
            org.junit.Assert.fail("cancelRequest_cf9905_literalMutation9981 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelRequest */
    @org.junit.Test(timeout = 10000)
    public void cancelRequest_cf9908_add10019_failAssert13() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE));
            retrofit2.Call<java.lang.String> call = service.getString();
            final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
            final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
            // MethodCallAdder
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
            // AssertGenerator replace invocation
            boolean o_cancelRequest_cf9908__34 = // StatementAdderMethod cloned existing statement
call.isExecuted();
            // MethodAssertGenerator build local variable
            Object o_47_0 = o_cancelRequest_cf9908__34;
            // MethodAssertGenerator build local variable
            Object o_49_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
            org.junit.Assert.fail("cancelRequest_cf9908_add10019 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelRequest */
    @org.junit.Test(timeout = 10000)
    public void cancelRequest_cf9913_failAssert13_literalMutation10227() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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
            // StatementAdderOnAssert create null value
            retrofit2.Call vc_3126 = (retrofit2.Call)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3126);
            // StatementAdderMethod cloned existing statement
            vc_3126.clone();
            // MethodAssertGenerator build local variable
            Object o_37_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
            org.junit.Assert.fail("cancelRequest_cf9913 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelRequest */
    @org.junit.Test(timeout = 10000)
    public void cancelRequest_cf9913_failAssert13_literalMutation10229_add11695_failAssert10() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE));
                retrofit2.Call<java.lang.String> call = service.getString();
                final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
                final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(2);
                // MethodAssertGenerator build local variable
                Object o_22_0 = ((java.util.concurrent.CountDownLatch)latch).getCount();
                // MethodCallAdder
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_3126 = (retrofit2.Call)null;
                // MethodAssertGenerator build local variable
                Object o_52_0 = vc_3126;
                // StatementAdderMethod cloned existing statement
                vc_3126.clone();
                // MethodAssertGenerator build local variable
                Object o_37_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
                org.junit.Assert.fail("cancelRequest_cf9913 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cancelRequest_cf9913_failAssert13_literalMutation10229_add11695 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cancelRequest */
    @org.junit.Test(timeout = 10000)
    public void cancelRequest_cf9907_failAssert11_add10178_literalMutation11058_failAssert3() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("t")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
                        // MethodCallAdder
                        latch.countDown();
                        latch.countDown();
                    }
                });
                call.cancel();
                org.assertj.core.api.Assertions.assertThat(call.isCanceled()).isTrue();
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_3122 = (retrofit2.Call)null;
                // MethodAssertGenerator build local variable
                Object o_39_0 = vc_3122;
                // StatementAdderMethod cloned existing statement
                vc_3122.isExecuted();
                // MethodAssertGenerator build local variable
                Object o_37_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.io.IOException.class).hasMessage("Canceled");
                org.junit.Assert.fail("cancelRequest_cf9907 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cancelRequest_cf9907_failAssert11_add10178_literalMutation11058 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cloningExecutedRequestDoesNotCopyState */
    @org.junit.Test
    public void cloningExecutedRequestDoesNotCopyState_literalMutation13352_failAssert2() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("2")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hello"));
            retrofit2.Call<java.lang.String> call = service.getString();
            org.assertj.core.api.Assertions.assertThat(call.execute().body()).isEqualTo("Hi");
            retrofit2.Call<java.lang.String> cloned = call.clone();
            org.assertj.core.api.Assertions.assertThat(cloned.execute().body()).isEqualTo("Hello");
            org.junit.Assert.fail("cloningExecutedRequestDoesNotCopyState_literalMutation13352 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cloningExecutedRequestDoesNotCopyState */
    @org.junit.Test
    public void cloningExecutedRequestDoesNotCopyState_literalMutation13351() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
        retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hello"));
        retrofit2.Call<java.lang.String> call = service.getString();
        org.assertj.core.api.Assertions.assertThat(call.execute().body()).isEqualTo("Hi");
        retrofit2.Call<java.lang.String> cloned = call.clone();
        org.assertj.core.api.Assertions.assertThat(cloned.execute().body()).isEqualTo("Hello");
    }

    /* amplification of retrofit2.CallTest#cloningExecutedRequestDoesNotCopyState */
    @org.junit.Test
    public void cloningExecutedRequestDoesNotCopyState_literalMutation13351_literalMutation13371_failAssert2() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("Path parameter \"ping\" value must not be null.")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
            retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hello"));
            retrofit2.Call<java.lang.String> call = service.getString();
            org.assertj.core.api.Assertions.assertThat(call.execute().body()).isEqualTo("Hi");
            retrofit2.Call<java.lang.String> cloned = call.clone();
            org.assertj.core.api.Assertions.assertThat(cloned.execute().body()).isEqualTo("Hello");
            org.junit.Assert.fail("cloningExecutedRequestDoesNotCopyState_literalMutation13351_literalMutation13371 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#cloningExecutedRequestDoesNotCopyState */
    @org.junit.Test(timeout = 10000)
    public void cloningExecutedRequestDoesNotCopyState_literalMutation13351_cf13397_failAssert9_literalMutation13675_failAssert7() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("T")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_4106 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_4106.cancel();
                // MethodAssertGenerator build local variable
                Object o_12_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
                retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hello"));
                retrofit2.Call<java.lang.String> call = service.getString();
                org.assertj.core.api.Assertions.assertThat(call.execute().body()).isEqualTo("Hi");
                retrofit2.Call<java.lang.String> cloned = call.clone();
                org.assertj.core.api.Assertions.assertThat(cloned.execute().body()).isEqualTo("Hello");
                org.junit.Assert.fail("cloningExecutedRequestDoesNotCopyState_literalMutation13351_cf13397 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cloningExecutedRequestDoesNotCopyState_literalMutation13351_cf13397_failAssert9_literalMutation13675 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#conversionProblemIncomingAsync */
    @org.junit.Test
    public void conversionProblemIncomingAsync_literalMutation13744() throws java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
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
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#conversionProblemIncomingAsync */
    @org.junit.Test
    public void conversionProblemIncomingAsync_literalMutation13745_failAssert0() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("2")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
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
            // MethodAssertGenerator build local variable
            Object o_41_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.lang.UnsupportedOperationException.class).hasMessage("I am broken!");
            org.junit.Assert.fail("conversionProblemIncomingAsync_literalMutation13745 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#conversionProblemIncomingAsync */
    @org.junit.Test(timeout = 10000)
    public void conversionProblemIncomingAsync_cf13777_failAssert18_literalMutation13977() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
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
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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
            // StatementAdderOnAssert create null value
            retrofit2.Call vc_4122 = (retrofit2.Call)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4122);
            // StatementAdderMethod cloned existing statement
            vc_4122.cancel();
            // MethodAssertGenerator build local variable
            Object o_45_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.lang.UnsupportedOperationException.class).hasMessage("I am broken!");
            org.junit.Assert.fail("conversionProblemIncomingAsync_cf13777 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#conversionProblemIncomingAsync */
    @org.junit.Test(timeout = 10000)
    public void conversionProblemIncomingAsync_cf13777_failAssert18_literalMutation13978_failAssert16() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("s")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_4122 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_4122.cancel();
                // MethodAssertGenerator build local variable
                Object o_45_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.lang.UnsupportedOperationException.class).hasMessage("I am broken!");
                org.junit.Assert.fail("conversionProblemIncomingAsync_cf13777 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("conversionProblemIncomingAsync_cf13777_failAssert18_literalMutation13978 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#conversionProblemIncomingMaskedByConverterIsUnwrapped */
    @org.junit.Test
    public void conversionProblemIncomingMaskedByConverterIsUnwrapped_literalMutation21312() throws java.io.IOException {
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
                            throw new java.lang.RuntimeException("eZ&/Vgo", e);
                        }
                    }
                };
            }
        }).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#conversionProblemIncomingSync */
    @org.junit.Test
    public void conversionProblemIncomingSync_literalMutation28210() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
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
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#conversionProblemOutgoingAsync */
    @org.junit.Test
    public void conversionProblemOutgoingAsync_literalMutation29649() throws java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
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
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#conversionProblemOutgoingAsync */
    @org.junit.Test
    public void conversionProblemOutgoingAsync_literalMutation29650_failAssert0() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("]")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
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
            // MethodAssertGenerator build local variable
            Object o_38_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.lang.UnsupportedOperationException.class).hasMessage("I am broken!");
            org.junit.Assert.fail("conversionProblemOutgoingAsync_literalMutation29650 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#conversionProblemOutgoingAsync */
    @org.junit.Test
    public void conversionProblemOutgoingAsync_literalMutation29649_literalMutation29691_failAssert27() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("g")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
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
            // MethodAssertGenerator build local variable
            Object o_20_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
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
            // MethodAssertGenerator build local variable
            Object o_40_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.lang.UnsupportedOperationException.class).hasMessage("I am broken!");
            org.junit.Assert.fail("conversionProblemOutgoingAsync_literalMutation29649_literalMutation29691 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#conversionProblemOutgoingAsync */
    @org.junit.Test(timeout = 10000)
    public void conversionProblemOutgoingAsync_cf29680_failAssert16_literalMutation29943() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
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
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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
            // StatementAdderOnAssert create null value
            retrofit2.Call vc_9194 = (retrofit2.Call)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_9194);
            // StatementAdderMethod cloned existing statement
            vc_9194.cancel();
            // MethodAssertGenerator build local variable
            Object o_42_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.lang.UnsupportedOperationException.class).hasMessage("I am broken!");
            org.junit.Assert.fail("conversionProblemOutgoingAsync_cf29680 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#conversionProblemOutgoingAsync */
    @org.junit.Test(timeout = 10000)
    public void conversionProblemOutgoingAsync_literalMutation29658_cf29803_failAssert11_literalMutation34349() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
                @java.lang.Override
                public retrofit2.Converter<?, okhttp3.RequestBody> requestBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] parameterAnnotations, java.lang.annotation.Annotation[] methodAnnotations, retrofit2.Retrofit retrofit) {
                    return new retrofit2.Converter<java.lang.String, okhttp3.RequestBody>() {
                        @java.lang.Override
                        public okhttp3.RequestBody convert(java.lang.String value) throws java.io.IOException {
                            throw new java.lang.UnsupportedOperationException("I am boken!");
                        }
                    };
                }
            }).build();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            final java.util.concurrent.atomic.AtomicReference<java.lang.Throwable> failureRef = new java.util.concurrent.atomic.AtomicReference<>();
            final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.util.concurrent.CountDownLatch)latch).getCount(), 0L);
            // MethodAssertGenerator build local variable
            Object o_27_0 = ((java.util.concurrent.CountDownLatch)latch).getCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_27_0, 0L);
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
            // StatementAdderOnAssert create null value
            retrofit2.Call vc_9242 = (retrofit2.Call)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_9242);
            // StatementAdderMethod cloned existing statement
            vc_9242.cancel();
            // MethodAssertGenerator build local variable
            Object o_45_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.lang.UnsupportedOperationException.class).hasMessage("I am broken!");
            org.junit.Assert.fail("conversionProblemOutgoingAsync_literalMutation29658_cf29803 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#conversionProblemOutgoingAsync */
    @org.junit.Test(timeout = 10000)
    public void conversionProblemOutgoingAsync_cf29674_failAssert14_literalMutation29898_literalMutation30517_failAssert5() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("d")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
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
                final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(2);
                // MethodAssertGenerator build local variable
                Object o_29_0 = ((java.util.concurrent.CountDownLatch)latch).getCount();
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_9188 = (retrofit2.Call)null;
                // MethodAssertGenerator build local variable
                Object o_45_0 = vc_9188;
                // StatementAdderMethod cloned existing statement
                vc_9188.request();
                // MethodAssertGenerator build local variable
                Object o_42_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                org.assertj.core.api.Assertions.assertThat(failureRef.get()).isInstanceOf(java.lang.UnsupportedOperationException.class).hasMessage("I am broken!");
                org.junit.Assert.fail("conversionProblemOutgoingAsync_cf29674 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("conversionProblemOutgoingAsync_cf29674_failAssert14_literalMutation29898_literalMutation30517 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#conversionProblemOutgoingSync */
    @org.junit.Test
    public void conversionProblemOutgoingSync_literalMutation34784() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
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
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        retrofit2.Call<java.lang.String> call = example.postString("Hi");
        try {
            call.execute();
            org.junit.Assert.fail();
        } catch (java.lang.UnsupportedOperationException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("I am broken!");
        }
    }

    /* amplification of retrofit2.CallTest#emptyResponse */
    @org.junit.Test
    public void emptyResponse_literalMutation36002() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("").addHeader("Content-Type", "text/stringy"));
        retrofit2.Response<java.lang.String> response = example.getString().execute();
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("");
        okhttp3.ResponseBody rawBody = response.raw().body();
        org.assertj.core.api.Assertions.assertThat(rawBody.contentLength()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(rawBody.contentType().toString()).isEqualTo("text/stringy");
    }

    /* amplification of retrofit2.CallTest#emptyResponse */
    @org.junit.Test
    public void emptyResponse_literalMutation36003_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("I")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("").addHeader("Content-Type", "text/stringy"));
            retrofit2.Response<java.lang.String> response = example.getString().execute();
            org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("");
            okhttp3.ResponseBody rawBody = response.raw().body();
            org.assertj.core.api.Assertions.assertThat(rawBody.contentLength()).isEqualTo(0);
            org.assertj.core.api.Assertions.assertThat(rawBody.contentType().toString()).isEqualTo("text/stringy");
            org.junit.Assert.fail("emptyResponse_literalMutation36003 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#emptyResponse */
    @org.junit.Test
    public void emptyResponse_literalMutation36007_failAssert2_literalMutation36130() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("").addHeader("", "text/stringy"));
            retrofit2.Response<java.lang.String> response = example.getString().execute();
            org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("");
            okhttp3.ResponseBody rawBody = response.raw().body();
            org.assertj.core.api.Assertions.assertThat(rawBody.contentLength()).isEqualTo(0);
            org.assertj.core.api.Assertions.assertThat(rawBody.contentType().toString()).isEqualTo("text/stringy");
            org.junit.Assert.fail("emptyResponse_literalMutation36007 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#emptyResponse */
    @org.junit.Test
    public void emptyResponse_literalMutation36010_failAssert5_literalMutation36212_failAssert19() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("H")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("").addHeader("+jAo{T.kU;Ra", "text/stringy"));
                retrofit2.Response<java.lang.String> response = example.getString().execute();
                org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("");
                okhttp3.ResponseBody rawBody = response.raw().body();
                org.assertj.core.api.Assertions.assertThat(rawBody.contentLength()).isEqualTo(0);
                org.assertj.core.api.Assertions.assertThat(rawBody.contentType().toString()).isEqualTo("text/stringy");
                org.junit.Assert.fail("emptyResponse_literalMutation36010 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("emptyResponse_literalMutation36010_failAssert5_literalMutation36212 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#emptyResponse */
    @org.junit.Test
    public void emptyResponse_literalMutation36011_failAssert6_literalMutation36239_literalMutation36342_failAssert5() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                // MethodAssertGenerator build local variable
                Object o_10_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("").addHeader("^Q4<.L=D<c A", ""));
                retrofit2.Response<java.lang.String> response = example.getString().execute();
                org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("");
                okhttp3.ResponseBody rawBody = response.raw().body();
                org.assertj.core.api.Assertions.assertThat(rawBody.contentLength()).isEqualTo(0);
                org.assertj.core.api.Assertions.assertThat(rawBody.contentType().toString()).isEqualTo("text/stringy");
                org.junit.Assert.fail("emptyResponse_literalMutation36011 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("emptyResponse_literalMutation36011_failAssert6_literalMutation36239_literalMutation36342 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#emptyResponse */
    @org.junit.Test
    public void emptyResponse_literalMutation36002_literalMutation36043_failAssert7_literalMutation36696_failAssert8() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("$")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                // MethodAssertGenerator build local variable
                Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("").addHeader("Content-Type", "^#M(Q8,P)E#t"));
                retrofit2.Response<java.lang.String> response = example.getString().execute();
                org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("");
                okhttp3.ResponseBody rawBody = response.raw().body();
                org.assertj.core.api.Assertions.assertThat(rawBody.contentLength()).isEqualTo(0);
                org.assertj.core.api.Assertions.assertThat(rawBody.contentType().toString()).isEqualTo("text/stringy");
                org.junit.Assert.fail("emptyResponse_literalMutation36002_literalMutation36043 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("emptyResponse_literalMutation36002_literalMutation36043_failAssert7_literalMutation36696 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#executeCallOnce */
    @org.junit.Test
    public void executeCallOnce_literalMutation37102() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#http200Async */
    @org.junit.Test
    public void http200Async_literalMutation38001_failAssert0() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("@")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
            // MethodAssertGenerator build local variable
            Object o_28_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            retrofit2.Response<java.lang.String> response = responseRef.get();
            org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isTrue();
            org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
            org.junit.Assert.fail("http200Async_literalMutation38001 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http200Async */
    @org.junit.Test
    public void http200Async_literalMutation38002() throws java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#http200Async */
    @org.junit.Test(timeout = 10000)
    public void http200Async_cf38024_failAssert8_literalMutation38219_failAssert0() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("&")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_11594 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_11594.cancel();
                // MethodAssertGenerator build local variable
                Object o_32_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                retrofit2.Response<java.lang.String> response = responseRef.get();
                org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isTrue();
                org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
                org.junit.Assert.fail("http200Async_cf38024 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("http200Async_cf38024_failAssert8_literalMutation38219 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http200Async */
    @org.junit.Test
    public void http200Async_literalMutation38002_literalMutation38043_failAssert6_literalMutation38321_failAssert7() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("Path parameter \"ping\" value must not be null.")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                // MethodAssertGenerator build local variable
                Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
                final java.util.concurrent.atomic.AtomicReference<retrofit2.Response<java.lang.String>> responseRef = new java.util.concurrent.atomic.AtomicReference<>();
                final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(0);
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
                // MethodAssertGenerator build local variable
                Object o_31_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                retrofit2.Response<java.lang.String> response = responseRef.get();
                org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isTrue();
                org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
                org.junit.Assert.fail("http200Async_literalMutation38002_literalMutation38043 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("http200Async_literalMutation38002_literalMutation38043_failAssert6_literalMutation38321 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http200Sync */
    @org.junit.Test
    public void http200Sync_literalMutation38607() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        retrofit2.Response<java.lang.String> response = example.getString().execute();
        org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isTrue();
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
    }

    /* amplification of retrofit2.CallTest#http200Sync */
    @org.junit.Test
    public void http200Sync_literalMutation38608_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("(")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
            retrofit2.Response<java.lang.String> response = example.getString().execute();
            org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isTrue();
            org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
            org.junit.Assert.fail("http200Sync_literalMutation38608 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http200Sync */
    @org.junit.Test
    public void http200Sync_literalMutation38607_literalMutation38617_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("U")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
            retrofit2.Response<java.lang.String> response = example.getString().execute();
            org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isTrue();
            org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
            org.junit.Assert.fail("http200Sync_literalMutation38607_literalMutation38617 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http200Sync */
    @org.junit.Test(timeout = 10000)
    public void http200Sync_literalMutation38607_cf38630_failAssert6_literalMutation38713_failAssert11() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("Path parameter \"ping\" value must not be null.")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_11624 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_11624.execute();
                // MethodAssertGenerator build local variable
                Object o_12_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
                retrofit2.Response<java.lang.String> response = example.getString().execute();
                org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isTrue();
                org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
                org.junit.Assert.fail("http200Sync_literalMutation38607_cf38630 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("http200Sync_literalMutation38607_cf38630_failAssert6_literalMutation38713 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http204SkipsConverter */
    @org.junit.Test
    public void http204SkipsConverter_literalMutation38733_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final retrofit2.Converter<okhttp3.ResponseBody, java.lang.String> converter = org.mockito.Mockito.spy(new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
                @java.lang.Override
                public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                    return value.string();
                }
            });
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("]")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
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
            org.junit.Assert.fail("http204SkipsConverter_literalMutation38733 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http204SkipsConverter */
    @org.junit.Test
    public void http204SkipsConverter_literalMutation38732() throws java.io.IOException {
        final retrofit2.Converter<okhttp3.ResponseBody, java.lang.String> converter = org.mockito.Mockito.spy(new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
            @java.lang.Override
            public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                return value.string();
            }
        });
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
            @java.lang.Override
            public retrofit2.Converter<okhttp3.ResponseBody, ?> responseBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                return converter;
            }
        }).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setStatus("HTTP/1.1 204 Nothin"));
        retrofit2.Response<java.lang.String> response = example.getString().execute();
        org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(204);
        org.assertj.core.api.Assertions.assertThat(response.body()).isNull();
        org.mockito.Mockito.verifyNoMoreInteractions(converter);
    }

    /* amplification of retrofit2.CallTest#http204SkipsConverter */
    @org.junit.Test
    public void http204SkipsConverter_literalMutation38736_failAssert2_literalMutation38806_failAssert15() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final retrofit2.Converter<okhttp3.ResponseBody, java.lang.String> converter = org.mockito.Mockito.spy(new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
                    @java.lang.Override
                    public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                        return value.string();
                    }
                });
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("s")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
                    @java.lang.Override
                    public retrofit2.Converter<okhttp3.ResponseBody, ?> responseBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                        return converter;
                    }
                }).build();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setStatus("HTTP/1.1 20 Nothin"));
                retrofit2.Response<java.lang.String> response = example.getString().execute();
                org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(204);
                org.assertj.core.api.Assertions.assertThat(response.body()).isNull();
                org.mockito.Mockito.verifyNoMoreInteractions(converter);
                org.junit.Assert.fail("http204SkipsConverter_literalMutation38736 should have thrown ProtocolException");
            } catch (java.net.ProtocolException eee) {
            }
            org.junit.Assert.fail("http204SkipsConverter_literalMutation38736_failAssert2_literalMutation38806 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http204SkipsConverter */
    @org.junit.Test
    public void http204SkipsConverter_literalMutation38737_failAssert3_literalMutation38820_literalMutation38895_failAssert15() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final retrofit2.Converter<okhttp3.ResponseBody, java.lang.String> converter = org.mockito.Mockito.spy(new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
                    @java.lang.Override
                    public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                        return value.string();
                    }
                });
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("&")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
                    @java.lang.Override
                    public retrofit2.Converter<okhttp3.ResponseBody, ?> responseBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                        return converter;
                    }
                }).build();
                // MethodAssertGenerator build local variable
                Object o_24_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setStatus(",t>jQ@5]Uzmk&1jeRI|"));
                retrofit2.Response<java.lang.String> response = example.getString().execute();
                org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(204);
                org.assertj.core.api.Assertions.assertThat(response.body()).isNull();
                org.mockito.Mockito.verifyNoMoreInteractions(converter);
                org.junit.Assert.fail("http204SkipsConverter_literalMutation38737 should have thrown ProtocolException");
            } catch (java.net.ProtocolException eee) {
            }
            org.junit.Assert.fail("http204SkipsConverter_literalMutation38737_failAssert3_literalMutation38820_literalMutation38895 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http205SkipsConverter */
    @org.junit.Test
    public void http205SkipsConverter_literalMutation39168() throws java.io.IOException {
        final retrofit2.Converter<okhttp3.ResponseBody, java.lang.String> converter = org.mockito.Mockito.spy(new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
            @java.lang.Override
            public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                return value.string();
            }
        });
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
            @java.lang.Override
            public retrofit2.Converter<okhttp3.ResponseBody, ?> responseBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                return converter;
            }
        }).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setStatus("HTTP/1.1 205 Nothin"));
        retrofit2.Response<java.lang.String> response = example.getString().execute();
        org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(205);
        org.assertj.core.api.Assertions.assertThat(response.body()).isNull();
        org.mockito.Mockito.verifyNoMoreInteractions(converter);
    }

    /* amplification of retrofit2.CallTest#http205SkipsConverter */
    @org.junit.Test
    public void http205SkipsConverter_literalMutation39169_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final retrofit2.Converter<okhttp3.ResponseBody, java.lang.String> converter = org.mockito.Mockito.spy(new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
                @java.lang.Override
                public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                    return value.string();
                }
            });
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("J")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
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
            org.junit.Assert.fail("http205SkipsConverter_literalMutation39169 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http205SkipsConverter */
    @org.junit.Test
    public void http205SkipsConverter_literalMutation39171_failAssert2_literalMutation39241() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final retrofit2.Converter<okhttp3.ResponseBody, java.lang.String> converter = org.mockito.Mockito.spy(new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
                @java.lang.Override
                public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                    return value.string();
                }
            });
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
                @java.lang.Override
                public retrofit2.Converter<okhttp3.ResponseBody, ?> responseBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                    return converter;
                }
            }).build();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setStatus("HTTP/1A.1 205 Nothin"));
            retrofit2.Response<java.lang.String> response = example.getString().execute();
            org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(205);
            org.assertj.core.api.Assertions.assertThat(response.body()).isNull();
            org.mockito.Mockito.verifyNoMoreInteractions(converter);
            org.junit.Assert.fail("http205SkipsConverter_literalMutation39171 should have thrown ProtocolException");
        } catch (java.net.ProtocolException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http205SkipsConverter */
    @org.junit.Test
    public void http205SkipsConverter_literalMutation39168_literalMutation39184_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final retrofit2.Converter<okhttp3.ResponseBody, java.lang.String> converter = org.mockito.Mockito.spy(new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
                @java.lang.Override
                public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                    return value.string();
                }
            });
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("M")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
                @java.lang.Override
                public retrofit2.Converter<okhttp3.ResponseBody, ?> responseBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                    return converter;
                }
            }).build();
            // MethodAssertGenerator build local variable
            Object o_22_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setStatus("HTTP/1.1 205 Nothin"));
            retrofit2.Response<java.lang.String> response = example.getString().execute();
            org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(205);
            org.assertj.core.api.Assertions.assertThat(response.body()).isNull();
            org.mockito.Mockito.verifyNoMoreInteractions(converter);
            org.junit.Assert.fail("http205SkipsConverter_literalMutation39168_literalMutation39184 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http205SkipsConverter */
    @org.junit.Test
    public void http205SkipsConverter_literalMutation39174_failAssert5_literalMutation39286_literalMutation39423_failAssert21() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final retrofit2.Converter<okhttp3.ResponseBody, java.lang.String> converter = org.mockito.Mockito.spy(new retrofit2.Converter<okhttp3.ResponseBody, java.lang.String>() {
                    @java.lang.Override
                    public java.lang.String convert(okhttp3.ResponseBody value) throws java.io.IOException {
                        return value.string();
                    }
                });
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("j")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory() {
                    @java.lang.Override
                    public retrofit2.Converter<okhttp3.ResponseBody, ?> responseBodyConverter(java.lang.reflect.Type type, java.lang.annotation.Annotation[] annotations, retrofit2.Retrofit retrofit) {
                        return converter;
                    }
                }).build();
                // MethodAssertGenerator build local variable
                Object o_24_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setStatus("HTP/1.1 205 Nothin"));
                retrofit2.Response<java.lang.String> response = example.getString().execute();
                org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(205);
                org.assertj.core.api.Assertions.assertThat(response.body()).isNull();
                org.mockito.Mockito.verifyNoMoreInteractions(converter);
                org.junit.Assert.fail("http205SkipsConverter_literalMutation39174 should have thrown ProtocolException");
            } catch (java.net.ProtocolException eee) {
            }
            org.junit.Assert.fail("http205SkipsConverter_literalMutation39174_failAssert5_literalMutation39286_literalMutation39423 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http404Async */
    @org.junit.Test
    public void http404Async_literalMutation39701_failAssert0() throws java.io.IOException, java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("l")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
            // MethodAssertGenerator build local variable
            Object o_29_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
            retrofit2.Response<java.lang.String> response = responseRef.get();
            org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isFalse();
            org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(404);
            org.assertj.core.api.Assertions.assertThat(response.errorBody().string()).isEqualTo("Hi");
            org.junit.Assert.fail("http404Async_literalMutation39701 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http404Async */
    @org.junit.Test
    public void http404Async_literalMutation39700() throws java.io.IOException, java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#http404Async */
    @org.junit.Test
    public void http404Async_literalMutation39709_failAssert2_literalMutation39820_failAssert6() throws java.io.IOException, java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("7")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setResponseCode(404).setBody("Hi"));
                final java.util.concurrent.atomic.AtomicReference<retrofit2.Response<java.lang.String>> responseRef = new java.util.concurrent.atomic.AtomicReference<>();
                final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(0);
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
                // MethodAssertGenerator build local variable
                Object o_30_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                retrofit2.Response<java.lang.String> response = responseRef.get();
                org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isFalse();
                org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(404);
                org.assertj.core.api.Assertions.assertThat(response.errorBody().string()).isEqualTo("Hi");
                org.junit.Assert.fail("http404Async_literalMutation39709 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("http404Async_literalMutation39709_failAssert2_literalMutation39820 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http404Async */
    @org.junit.Test
    public void http404Async_literalMutation39700_literalMutation39754_failAssert5_literalMutation40118_failAssert6() throws java.io.IOException, java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("y")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                // MethodAssertGenerator build local variable
                Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setResponseCode(404).setBody("Hi"));
                final java.util.concurrent.atomic.AtomicReference<retrofit2.Response<java.lang.String>> responseRef = new java.util.concurrent.atomic.AtomicReference<>();
                final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(0);
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
                // MethodAssertGenerator build local variable
                Object o_32_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                retrofit2.Response<java.lang.String> response = responseRef.get();
                org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isFalse();
                org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(404);
                org.assertj.core.api.Assertions.assertThat(response.errorBody().string()).isEqualTo("Hi");
                org.junit.Assert.fail("http404Async_literalMutation39700_literalMutation39754 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("http404Async_literalMutation39700_literalMutation39754_failAssert5_literalMutation40118 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http404Sync */
    @org.junit.Test
    public void http404Sync_literalMutation40293() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setResponseCode(404).setBody("Hi"));
        retrofit2.Response<java.lang.String> response = example.getString().execute();
        org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isFalse();
        org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(404);
        org.assertj.core.api.Assertions.assertThat(response.errorBody().string()).isEqualTo("Hi");
    }

    /* amplification of retrofit2.CallTest#http404Sync */
    @org.junit.Test
    public void http404Sync_literalMutation40294_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("!")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setResponseCode(404).setBody("Hi"));
            retrofit2.Response<java.lang.String> response = example.getString().execute();
            org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isFalse();
            org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(404);
            org.assertj.core.api.Assertions.assertThat(response.errorBody().string()).isEqualTo("Hi");
            org.junit.Assert.fail("http404Sync_literalMutation40294 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http404Sync */
    @org.junit.Test
    public void http404Sync_literalMutation40293_literalMutation40311_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("Path parameter \"ping\" value must not be null.")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setResponseCode(404).setBody("Hi"));
            retrofit2.Response<java.lang.String> response = example.getString().execute();
            org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isFalse();
            org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(404);
            org.assertj.core.api.Assertions.assertThat(response.errorBody().string()).isEqualTo("Hi");
            org.junit.Assert.fail("http404Sync_literalMutation40293_literalMutation40311 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#http404Sync */
    @org.junit.Test(timeout = 10000)
    public void http404Sync_literalMutation40293_cf40333_failAssert6_literalMutation40480_failAssert11() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("x")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_11832 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_11832.execute();
                // MethodAssertGenerator build local variable
                Object o_12_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setResponseCode(404).setBody("Hi"));
                retrofit2.Response<java.lang.String> response = example.getString().execute();
                org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isFalse();
                org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(404);
                org.assertj.core.api.Assertions.assertThat(response.errorBody().string()).isEqualTo("Hi");
                org.junit.Assert.fail("http404Sync_literalMutation40293_cf40333 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("http404Sync_literalMutation40293_cf40333_failAssert6_literalMutation40480 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#rawResponseContentTypeAndLengthButNoSource */
    @org.junit.Test
    public void rawResponseContentTypeAndLengthButNoSource_literalMutation40519() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url(".")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#reportsExecutedAsync */
    @org.junit.Test
    public void reportsExecutedAsync_literalMutation45008() throws java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#reportsExecutedAsync */
    @org.junit.Test(timeout = 10000)
    public void reportsExecutedAsync_add45006_failAssert0() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
            retrofit2.Call<java.lang.String> call = example.getString();
            org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isFalse();
            // MethodCallAdder
            call.enqueue(new retrofit2.Callback<java.lang.String>() {
                @java.lang.Override
                public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                }

                @java.lang.Override
                public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                }
            });
            call.enqueue(new retrofit2.Callback<java.lang.String>() {
                @java.lang.Override
                public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                }

                @java.lang.Override
                public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                }
            });
            org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isTrue();
            org.junit.Assert.fail("reportsExecutedAsync_add45006 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#reportsExecutedAsync */
    @org.junit.Test
    public void reportsExecutedAsync_literalMutation45009_failAssert1() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("(")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
            org.junit.Assert.fail("reportsExecutedAsync_literalMutation45009 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#reportsExecutedAsync */
    @org.junit.Test(timeout = 10000)
    public void reportsExecutedAsync_literalMutation45008_add45014_failAssert0() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
            retrofit2.Call<java.lang.String> call = example.getString();
            org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isFalse();
            // MethodCallAdder
            call.enqueue(new retrofit2.Callback<java.lang.String>() {
                @java.lang.Override
                public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                }

                @java.lang.Override
                public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                }
            });
            call.enqueue(new retrofit2.Callback<java.lang.String>() {
                @java.lang.Override
                public void onResponse(retrofit2.Call<java.lang.String> call, retrofit2.Response<java.lang.String> response) {
                }

                @java.lang.Override
                public void onFailure(retrofit2.Call<java.lang.String> call, java.lang.Throwable t) {
                }
            });
            org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isTrue();
            org.junit.Assert.fail("reportsExecutedAsync_literalMutation45008_add45014 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#reportsExecutedAsync */
    @org.junit.Test
    public void reportsExecutedAsync_literalMutation45008_literalMutation45016_failAssert1() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("Path parameter \"ping\" value must not be null.")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
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
            org.junit.Assert.fail("reportsExecutedAsync_literalMutation45008_literalMutation45016 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#reportsExecutedAsync */
    @org.junit.Test(timeout = 10000)
    public void reportsExecutedAsync_literalMutation45008_cf45022_failAssert4_literalMutation45120_failAssert13() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("Path parameter \"ping\" value must not be null.")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_13202 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_13202.isExecuted();
                // MethodAssertGenerator build local variable
                Object o_12_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
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
                org.junit.Assert.fail("reportsExecutedAsync_literalMutation45008_cf45022 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("reportsExecutedAsync_literalMutation45008_cf45022_failAssert4_literalMutation45120 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#reportsExecutedSync */
    @org.junit.Test
    public void reportsExecutedSync_literalMutation45164() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
        retrofit2.Call<java.lang.String> call = example.getString();
        org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isFalse();
        call.execute();
        org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isTrue();
    }

    /* amplification of retrofit2.CallTest#reportsExecutedSync */
    @org.junit.Test
    public void reportsExecutedSync_literalMutation45165_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("z")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
            retrofit2.Call<java.lang.String> call = example.getString();
            org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isFalse();
            call.execute();
            org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isTrue();
            org.junit.Assert.fail("reportsExecutedSync_literalMutation45165 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#reportsExecutedSync */
    @org.junit.Test
    public void reportsExecutedSync_literalMutation45164_literalMutation45172_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("Path parameter \"ping\" value must not be null.")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
            retrofit2.Call<java.lang.String> call = example.getString();
            org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isFalse();
            call.execute();
            org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isTrue();
            org.junit.Assert.fail("reportsExecutedSync_literalMutation45164_literalMutation45172 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#reportsExecutedSync */
    @org.junit.Test(timeout = 10000)
    public void reportsExecutedSync_add45162_failAssert0_add45196() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
            retrofit2.Call<java.lang.String> call = example.getString();
            org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isFalse();
            // AssertGenerator replace invocation
            retrofit2.Response<java.lang.String> o_reportsExecutedSync_add45162_failAssert0_add45196__20 = // MethodCallAdder
call.execute();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Response)o_reportsExecutedSync_add45162_failAssert0_add45196__20).errorBody());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_reportsExecutedSync_add45162_failAssert0_add45196__20).code(), 200);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_reportsExecutedSync_add45162_failAssert0_add45196__20).body(), "Hi");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((retrofit2.Response)o_reportsExecutedSync_add45162_failAssert0_add45196__20).isSuccessful());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_reportsExecutedSync_add45162_failAssert0_add45196__20).message(), "OK");
            call.execute();
            // MethodCallAdder
            org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isTrue();
            org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isTrue();
            org.junit.Assert.fail("reportsExecutedSync_add45162 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#reportsExecutedSync */
    @org.junit.Test(timeout = 10000)
    public void reportsExecutedSync_literalMutation45164_cf45176_failAssert3_literalMutation45473_failAssert20() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url(")")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_13232 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_13232.isCanceled();
                // MethodAssertGenerator build local variable
                Object o_12_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
                retrofit2.Call<java.lang.String> call = example.getString();
                org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isFalse();
                call.execute();
                org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isTrue();
                org.junit.Assert.fail("reportsExecutedSync_literalMutation45164_cf45176 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("reportsExecutedSync_literalMutation45164_cf45176_failAssert3_literalMutation45473 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#reportsExecutedSync */
    @org.junit.Test(timeout = 10000)
    public void reportsExecutedSync_add45162_failAssert0_literalMutation45197_cf45360_failAssert5() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_25_1 = 200;
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                // MethodAssertGenerator build local variable
                Object o_10_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi"));
                retrofit2.Call<java.lang.String> call = example.getString();
                org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isFalse();
                // AssertGenerator replace invocation
                retrofit2.Response<java.lang.String> o_reportsExecutedSync_add45162_failAssert0_literalMutation45197__20 = // MethodCallAdder
call.execute();
                // MethodAssertGenerator build local variable
                Object o_23_0 = ((retrofit2.Response)o_reportsExecutedSync_add45162_failAssert0_literalMutation45197__20).errorBody();
                // MethodAssertGenerator build local variable
                Object o_25_0 = ((retrofit2.Response)o_reportsExecutedSync_add45162_failAssert0_literalMutation45197__20).code();
                // MethodAssertGenerator build local variable
                Object o_27_0 = ((retrofit2.Response)o_reportsExecutedSync_add45162_failAssert0_literalMutation45197__20).body();
                // MethodAssertGenerator build local variable
                Object o_29_0 = ((retrofit2.Response)o_reportsExecutedSync_add45162_failAssert0_literalMutation45197__20).isSuccessful();
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_13318 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_13318.clone();
                // MethodAssertGenerator build local variable
                Object o_35_0 = ((retrofit2.Response)o_reportsExecutedSync_add45162_failAssert0_literalMutation45197__20).message();
                call.execute();
                org.assertj.core.api.Assertions.assertThat(call.isExecuted()).isTrue();
                org.junit.Assert.fail("reportsExecutedSync_add45162 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("reportsExecutedSync_add45162_failAssert0_literalMutation45197_cf45360 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestAfterEnqueueReturnsCachedValue */
    @org.junit.Test(timeout = 10000)
    public void requestAfterEnqueueReturnsCachedValue_cf55794_failAssert7_literalMutation56184_add58419_failAssert8() throws java.io.IOException, java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                // MethodAssertGenerator build local variable
                Object o_10_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
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
                // MethodCallAdder
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_15810 = (retrofit2.Call)null;
                // MethodAssertGenerator build local variable
                Object o_53_0 = vc_15810;
                // StatementAdderMethod cloned existing statement
                vc_15810.isExecuted();
                // MethodAssertGenerator build local variable
                Object o_40_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                call.request();
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                org.junit.Assert.fail("requestAfterEnqueueReturnsCachedValue_cf55794 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("requestAfterEnqueueReturnsCachedValue_cf55794_failAssert7_literalMutation56184_add58419 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestAfterEnqueueReturnsCachedValue */
    @org.junit.Test(timeout = 10000)
    public void requestAfterEnqueueReturnsCachedValue_cf55797_failAssert8_add56206_failAssert7_add60042() throws java.io.IOException, java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse());
                final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
                java.lang.Object a = new java.lang.Object() {
                    @java.lang.Override
                    public java.lang.String toString() {
                        // AssertGenerator replace invocation
                        int o_requestAfterEnqueueReturnsCachedValue_cf55797_failAssert8_add56206_failAssert7_add60042__24 = // MethodCallAdder
writeCount.incrementAndGet();
                        // AssertGenerator add assertion
                        org.junit.Assert.assertEquals(o_requestAfterEnqueueReturnsCachedValue_cf55797_failAssert8_add56206_failAssert7_add60042__24, 1);
                        writeCount.incrementAndGet();
                        return "Hello";
                    }
                };
                retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
                final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
                // MethodCallAdder
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_15812 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_15812.request();
                // MethodAssertGenerator build local variable
                Object o_40_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                call.request();
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                org.junit.Assert.fail("requestAfterEnqueueReturnsCachedValue_cf55797 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("requestAfterEnqueueReturnsCachedValue_cf55797_failAssert8_add56206 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestAfterEnqueueReturnsCachedValue */
    @org.junit.Test(timeout = 10000)
    public void requestAfterEnqueueReturnsCachedValue_cf55794_failAssert7_literalMutation56193_literalMutation59415_failAssert2() throws java.io.IOException, java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("@")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
                final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(0);
                // MethodAssertGenerator build local variable
                Object o_29_0 = ((java.util.concurrent.CountDownLatch)latch).getCount();
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
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_15810 = (retrofit2.Call)null;
                // MethodAssertGenerator build local variable
                Object o_43_0 = vc_15810;
                // StatementAdderMethod cloned existing statement
                vc_15810.isExecuted();
                // MethodAssertGenerator build local variable
                Object o_40_0 = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                call.request();
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                org.junit.Assert.fail("requestAfterEnqueueReturnsCachedValue_cf55794 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("requestAfterEnqueueReturnsCachedValue_cf55794_failAssert7_literalMutation56193_literalMutation59415 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestAfterExecuteReturnsCachedValue */
    @org.junit.Test(timeout = 10000)
    public void requestAfterExecuteReturnsCachedValue_add60558_cf60651_failAssert14() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
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
            // AssertGenerator replace invocation
            okhttp3.Request o_requestAfterExecuteReturnsCachedValue_add60558__28 = // MethodCallAdder
call.request();
            // MethodAssertGenerator build local variable
            Object o_29_0 = ((okhttp3.Request)o_requestAfterExecuteReturnsCachedValue_add60558__28).method();
            // StatementAdderMethod cloned existing statement
            call.execute();
            // MethodAssertGenerator build local variable
            Object o_33_0 = ((okhttp3.Request)o_requestAfterExecuteReturnsCachedValue_add60558__28).isHttps();
            call.request();
            org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
            org.junit.Assert.fail("requestAfterExecuteReturnsCachedValue_add60558_cf60651 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestAfterExecuteReturnsCachedValue */
    @org.junit.Test(timeout = 10000)
    public void requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse());
            final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
            java.lang.Object a = new java.lang.Object() {
                @java.lang.Override
                public java.lang.String toString() {
                    writeCount.incrementAndGet();
                    return "Hllo";
                }
            };
            retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
            // AssertGenerator replace invocation
            retrofit2.Response<java.lang.String> o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711__26 = // MethodCallAdder
call.execute();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711__26).message(), "OK");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711__26).errorBody());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711__26).code(), 200);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711__26).body(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711__26).isSuccessful());
            call.execute();
            org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
            call.request();
            org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
            org.junit.Assert.fail("requestAfterExecuteReturnsCachedValue_add60556 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestAfterExecuteReturnsCachedValue */
    @org.junit.Test(timeout = 10000)
    public void requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60707() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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
            // AssertGenerator replace invocation
            retrofit2.Response<java.lang.String> o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60707__26 = // MethodCallAdder
call.execute();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60707__26).body(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60707__26).code(), 200);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60707__26).errorBody());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60707__26).message(), "OK");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60707__26).isSuccessful());
            call.execute();
            org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
            call.request();
            org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
            org.junit.Assert.fail("requestAfterExecuteReturnsCachedValue_add60556 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestAfterExecuteReturnsCachedValue */
    @org.junit.Test(timeout = 10000)
    public void requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60709() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse());
            final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
            java.lang.Object a = new java.lang.Object() {
                @java.lang.Override
                public java.lang.String toString() {
                    writeCount.incrementAndGet();
                    return "";
                }
            };
            retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
            // AssertGenerator replace invocation
            retrofit2.Response<java.lang.String> o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60709__26 = // MethodCallAdder
call.execute();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60709__26).isSuccessful());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60709__26).errorBody());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60709__26).body(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60709__26).code(), 200);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60709__26).message(), "OK");
            call.execute();
            org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
            call.request();
            org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
            org.junit.Assert.fail("requestAfterExecuteReturnsCachedValue_add60556 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestAfterExecuteReturnsCachedValue */
    @org.junit.Test
    public void requestAfterExecuteReturnsCachedValue_literalMutation60560_literalMutation60671_failAssert17() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("Path parameter \"ping\" value must not be null.")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
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
            org.junit.Assert.fail("requestAfterExecuteReturnsCachedValue_literalMutation60560_literalMutation60671 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestAfterExecuteReturnsCachedValue */
    @org.junit.Test(timeout = 10000)
    public void requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711_literalMutation61215_failAssert18() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_32_1 = 200;
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("1")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse());
                final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
                java.lang.Object a = new java.lang.Object() {
                    @java.lang.Override
                    public java.lang.String toString() {
                        writeCount.incrementAndGet();
                        return "Hllo";
                    }
                };
                retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
                // AssertGenerator replace invocation
                retrofit2.Response<java.lang.String> o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711__26 = // MethodCallAdder
call.execute();
                // MethodAssertGenerator build local variable
                Object o_28_0 = ((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711__26).message();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711__26).errorBody();
                // MethodAssertGenerator build local variable
                Object o_32_0 = ((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711__26).code();
                // MethodAssertGenerator build local variable
                Object o_34_0 = ((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711__26).body();
                // MethodAssertGenerator build local variable
                Object o_36_0 = ((retrofit2.Response)o_requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711__26).isSuccessful();
                call.execute();
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                call.request();
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                org.junit.Assert.fail("requestAfterExecuteReturnsCachedValue_add60556 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("requestAfterExecuteReturnsCachedValue_add60556_failAssert0_literalMutation60711_literalMutation61215 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestBeforeExecuteCreates */
    @org.junit.Test(timeout = 10000)
    public void requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71238_cf71761_failAssert6() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_35_1 = 200;
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse());
                final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
                java.lang.Object a = new java.lang.Object() {
                    @java.lang.Override
                    public java.lang.String toString() {
                        writeCount.incrementAndGet();
                        return "He}llo";
                    }
                };
                retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
                call.request();
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                // AssertGenerator replace invocation
                retrofit2.Response<java.lang.String> o_requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71238__30 = // MethodCallAdder
call.execute();
                // MethodAssertGenerator build local variable
                Object o_31_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71238__30).errorBody();
                // MethodAssertGenerator build local variable
                Object o_33_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71238__30).message();
                // MethodAssertGenerator build local variable
                Object o_35_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71238__30).code();
                // MethodAssertGenerator build local variable
                Object o_37_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71238__30).body();
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_19768 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_19768.execute();
                // MethodAssertGenerator build local variable
                Object o_43_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71238__30).isSuccessful();
                call.execute();
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                org.junit.Assert.fail("requestBeforeExecuteCreates_add71084 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71238_cf71761 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestBeforeExecuteCreates */
    @org.junit.Test(timeout = 10000)
    public void requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71235_cf71620_failAssert26() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_35_1 = 200;
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                retrofit2.AmplCallTest.Service service = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse());
                final java.util.concurrent.atomic.AtomicInteger writeCount = new java.util.concurrent.atomic.AtomicInteger();
                java.lang.Object a = new java.lang.Object() {
                    @java.lang.Override
                    public java.lang.String toString() {
                        writeCount.incrementAndGet();
                        return "";
                    }
                };
                retrofit2.Call<java.lang.String> call = service.postRequestBody(a);
                call.request();
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                // AssertGenerator replace invocation
                retrofit2.Response<java.lang.String> o_requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71235__30 = // MethodCallAdder
call.execute();
                // MethodAssertGenerator build local variable
                Object o_31_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71235__30).errorBody();
                // MethodAssertGenerator build local variable
                Object o_33_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71235__30).body();
                // MethodAssertGenerator build local variable
                Object o_35_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71235__30).code();
                // MethodAssertGenerator build local variable
                Object o_37_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71235__30).isSuccessful();
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_19718 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_19718.clone();
                // MethodAssertGenerator build local variable
                Object o_43_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71235__30).message();
                call.execute();
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                org.junit.Assert.fail("requestBeforeExecuteCreates_add71084 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("requestBeforeExecuteCreates_add71084_failAssert0_literalMutation71235_cf71620 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestBeforeExecuteCreates */
    @org.junit.Test(timeout = 10000)
    public void requestBeforeExecuteCreates_literalMutation71086_cf71212_failAssert18_add72322() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
            // StatementAdderOnAssert create null value
            retrofit2.Call vc_19586 = (retrofit2.Call)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_19586);
            // StatementAdderMethod cloned existing statement
            vc_19586.isExecuted();
            // MethodAssertGenerator build local variable
            Object o_12_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
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
            // MethodCallAdder
            call.request();
            call.request();
            org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
            call.execute();
            org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
            org.junit.Assert.fail("requestBeforeExecuteCreates_literalMutation71086_cf71212 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestBeforeExecuteCreates */
    @org.junit.Test(timeout = 10000)
    public void requestBeforeExecuteCreates_add71084_failAssert0_add71232_literalMutation71507_failAssert13() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_31_1 = 200;
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("v")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
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
                // AssertGenerator replace invocation
                retrofit2.Response<java.lang.String> o_requestBeforeExecuteCreates_add71084_failAssert0_add71232__30 = // MethodCallAdder
call.execute();
                // MethodAssertGenerator build local variable
                Object o_31_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_add71232__30).code();
                // MethodAssertGenerator build local variable
                Object o_33_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_add71232__30).body();
                // MethodAssertGenerator build local variable
                Object o_35_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_add71232__30).message();
                // MethodAssertGenerator build local variable
                Object o_37_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_add71232__30).isSuccessful();
                // MethodAssertGenerator build local variable
                Object o_39_0 = ((retrofit2.Response)o_requestBeforeExecuteCreates_add71084_failAssert0_add71232__30).errorBody();
                call.execute();
                // MethodCallAdder
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
                org.junit.Assert.fail("requestBeforeExecuteCreates_add71084 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("requestBeforeExecuteCreates_add71084_failAssert0_add71232_literalMutation71507 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#requestBeforeExecuteCreates */
    @org.junit.Test(timeout = 10000)
    public void requestBeforeExecuteCreates_literalMutation71086_add71195_failAssert14_add72243() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_8_0);
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
            // AssertGenerator replace invocation
            retrofit2.Response<java.lang.String> o_requestBeforeExecuteCreates_literalMutation71086_add71195_failAssert14_add72243__32 = // MethodCallAdder
call.execute();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((retrofit2.Response)o_requestBeforeExecuteCreates_literalMutation71086_add71195_failAssert14_add72243__32).errorBody());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_requestBeforeExecuteCreates_literalMutation71086_add71195_failAssert14_add72243__32).code(), 200);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((retrofit2.Response)o_requestBeforeExecuteCreates_literalMutation71086_add71195_failAssert14_add72243__32).isSuccessful());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_requestBeforeExecuteCreates_literalMutation71086_add71195_failAssert14_add72243__32).message(), "OK");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((retrofit2.Response)o_requestBeforeExecuteCreates_literalMutation71086_add71195_failAssert14_add72243__32).body(), "");
            // MethodCallAdder
            call.execute();
            call.execute();
            org.assertj.core.api.Assertions.assertThat(writeCount.get()).isEqualTo(1);
            org.junit.Assert.fail("requestBeforeExecuteCreates_literalMutation71086_add71195 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#responseBody */
    @org.junit.Test
    public void responseBody_literalMutation87298() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("1234"));
        retrofit2.Response<okhttp3.ResponseBody> response = example.getBody().execute();
        org.assertj.core.api.Assertions.assertThat(response.body().string()).isEqualTo("1234");
    }

    /* amplification of retrofit2.CallTest#responseBody */
    @org.junit.Test
    public void responseBody_literalMutation87299_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("]")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("1234"));
            retrofit2.Response<okhttp3.ResponseBody> response = example.getBody().execute();
            org.assertj.core.api.Assertions.assertThat(response.body().string()).isEqualTo("1234");
            org.junit.Assert.fail("responseBody_literalMutation87299 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#responseBody */
    @org.junit.Test
    public void responseBody_literalMutation87298_literalMutation87313_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("d")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("1234"));
            retrofit2.Response<okhttp3.ResponseBody> response = example.getBody().execute();
            org.assertj.core.api.Assertions.assertThat(response.body().string()).isEqualTo("1234");
            org.junit.Assert.fail("responseBody_literalMutation87298_literalMutation87313 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#responseBody */
    @org.junit.Test(timeout = 10000)
    public void responseBody_literalMutation87298_cf87326_failAssert3_literalMutation87404_failAssert5() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("e")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_23554 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_23554.isExecuted();
                // MethodAssertGenerator build local variable
                Object o_12_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("1234"));
                retrofit2.Response<okhttp3.ResponseBody> response = example.getBody().execute();
                org.assertj.core.api.Assertions.assertThat(response.body().string()).isEqualTo("1234");
                org.junit.Assert.fail("responseBody_literalMutation87298_cf87326 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("responseBody_literalMutation87298_cf87326_failAssert3_literalMutation87404 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#responseBodyBuffers */
    @org.junit.Test
    public void responseBodyBuffers_literalMutation87478() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#responseBodyStreams */
    @org.junit.Test
    public void responseBodyStreams_literalMutation88631() throws java.io.IOException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#successfulRequestResponseWhenMimeTypeMissing */
    @org.junit.Test
    public void successfulRequestResponseWhenMimeTypeMissing_literalMutation89408() throws java.lang.Exception {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi").removeHeader("Content-Type"));
        retrofit2.Response<java.lang.String> response = example.getString().execute();
        org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
    }

    /* amplification of retrofit2.CallTest#successfulRequestResponseWhenMimeTypeMissing */
    @org.junit.Test
    public void successfulRequestResponseWhenMimeTypeMissing_literalMutation89407_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("p")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi").removeHeader("Content-Type"));
            retrofit2.Response<java.lang.String> response = example.getString().execute();
            org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
            org.junit.Assert.fail("successfulRequestResponseWhenMimeTypeMissing_literalMutation89407 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#successfulRequestResponseWhenMimeTypeMissing */
    @org.junit.Test
    public void successfulRequestResponseWhenMimeTypeMissing_literalMutation89408_literalMutation89421_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("b")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi").removeHeader("Content-Type"));
            retrofit2.Response<java.lang.String> response = example.getString().execute();
            org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
            org.junit.Assert.fail("successfulRequestResponseWhenMimeTypeMissing_literalMutation89408_literalMutation89421 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#successfulRequestResponseWhenMimeTypeMissing */
    @org.junit.Test(timeout = 10000)
    public void successfulRequestResponseWhenMimeTypeMissing_literalMutation89408_cf89437_failAssert5_literalMutation89534_failAssert8() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("Path parameter \"ping\" value must not be null.")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
                // StatementAdderOnAssert create null value
                retrofit2.Call vc_24406 = (retrofit2.Call)null;
                // StatementAdderMethod cloned existing statement
                vc_24406.clone();
                // MethodAssertGenerator build local variable
                Object o_12_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
                retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
                server.enqueue(new okhttp3.mockwebserver.MockResponse().setBody("Hi").removeHeader("Content-Type"));
                retrofit2.Response<java.lang.String> response = example.getString().execute();
                org.assertj.core.api.Assertions.assertThat(response.body()).isEqualTo("Hi");
                org.junit.Assert.fail("successfulRequestResponseWhenMimeTypeMissing_literalMutation89408_cf89437 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("successfulRequestResponseWhenMimeTypeMissing_literalMutation89408_cf89437_failAssert5_literalMutation89534 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#transportProblemAsync */
    @org.junit.Test
    public void transportProblemAsync_literalMutation89579() throws java.lang.InterruptedException {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url(".")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
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

    /* amplification of retrofit2.CallTest#transportProblemSync */
    @org.junit.Test
    public void transportProblemSync_literalMutation90913_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("K")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START));
            retrofit2.Call<java.lang.String> call = example.getString();
            try {
                call.execute();
            } catch (java.io.IOException ignored) {
            }
            org.junit.Assert.fail("transportProblemSync_literalMutation90913 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of retrofit2.CallTest#transportProblemSync */
    @org.junit.Test
    public void transportProblemSync_literalMutation90912() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((retrofit2.Retrofit)retrofit).callbackExecutor());
        retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
        server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START));
        retrofit2.Call<java.lang.String> call = example.getString();
        try {
            call.execute();
            org.junit.Assert.fail();
        } catch (java.io.IOException ignored) {
        }
    }

    /* amplification of retrofit2.CallTest#transportProblemSync */
    @org.junit.Test
    public void transportProblemSync_literalMutation90912_literalMutation90941_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl(server.url("I")).addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).build();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((retrofit2.Retrofit)retrofit).callbackExecutor();
            retrofit2.AmplCallTest.Service example = retrofit.create(retrofit2.AmplCallTest.Service.class);
            server.enqueue(new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START));
            retrofit2.Call<java.lang.String> call = example.getString();
            try {
                call.execute();
            } catch (java.io.IOException ignored) {
            }
            org.junit.Assert.fail("transportProblemSync_literalMutation90912_literalMutation90941 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

