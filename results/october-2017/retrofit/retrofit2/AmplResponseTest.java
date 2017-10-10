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


public final class AmplResponseTest {
    private final okhttp3.Response successResponse = // 
    new okhttp3.Response.Builder().code(200).message("OK").protocol(okhttp3.Protocol.HTTP_1_1).request(new okhttp3.Request.Builder().url("http://localhost").build()).build();

    private final okhttp3.Response errorResponse = // 
    new okhttp3.Response.Builder().code(400).message("Broken!").protocol(okhttp3.Protocol.HTTP_1_1).request(new okhttp3.Request.Builder().url("http://localhost").build()).build();

    @org.junit.Test
    public void success() {
        java.lang.Object body = new java.lang.Object();
        retrofit2.Response<java.lang.Object> response = retrofit2.Response.success(body);
        org.assertj.core.api.Assertions.assertThat(response.raw()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(200);
        org.assertj.core.api.Assertions.assertThat(response.message()).isEqualTo("OK");
        org.assertj.core.api.Assertions.assertThat(response.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isTrue();
        org.assertj.core.api.Assertions.assertThat(response.body()).isSameAs(body);
        org.assertj.core.api.Assertions.assertThat(response.errorBody()).isNull();
    }

    @org.junit.Test
    public void successNullAllowed() {
        retrofit2.Response<java.lang.Object> response = retrofit2.Response.success(null);
        org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isTrue();
        org.assertj.core.api.Assertions.assertThat(response.body()).isNull();
    }

    @org.junit.Test
    public void successWithHeaders() {
        java.lang.Object body = new java.lang.Object();
        okhttp3.Headers headers = okhttp3.Headers.of("foo", "bar");
        retrofit2.Response<java.lang.Object> response = retrofit2.Response.success(body, headers);
        org.assertj.core.api.Assertions.assertThat(response.raw()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(200);
        org.assertj.core.api.Assertions.assertThat(response.message()).isEqualTo("OK");
        org.assertj.core.api.Assertions.assertThat(response.headers().toMultimap()).isEqualTo(headers.toMultimap());
        org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isTrue();
        org.assertj.core.api.Assertions.assertThat(response.body()).isSameAs(body);
        org.assertj.core.api.Assertions.assertThat(response.errorBody()).isNull();
    }

    @org.junit.Test
    public void successWithNullHeadersThrows() {
        try {
            retrofit2.Response.success("", ((okhttp3.Headers) (null)));
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("headers == null");
        }
    }

    @org.junit.Test
    public void successWithRawResponse() {
        java.lang.Object body = new java.lang.Object();
        retrofit2.Response<java.lang.Object> response = retrofit2.Response.success(body, successResponse);
        org.assertj.core.api.Assertions.assertThat(response.raw()).isSameAs(successResponse);
        org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(200);
        org.assertj.core.api.Assertions.assertThat(response.message()).isEqualTo("OK");
        org.assertj.core.api.Assertions.assertThat(response.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isTrue();
        org.assertj.core.api.Assertions.assertThat(response.body()).isSameAs(body);
        org.assertj.core.api.Assertions.assertThat(response.errorBody()).isNull();
    }

    @org.junit.Test
    public void successWithNullRawResponseThrows() {
        try {
            retrofit2.Response.success("", ((okhttp3.Response) (null)));
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("rawResponse == null");
        }
    }

    @org.junit.Test
    public void successWithErrorRawResponseThrows() {
        try {
            retrofit2.Response.success("", errorResponse);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("rawResponse must be successful response");
        }
    }

    @org.junit.Test
    public void error() {
        okhttp3.ResponseBody errorBody = okhttp3.ResponseBody.create(null, "Broken!");
        retrofit2.Response<?> response = retrofit2.Response.error(400, errorBody);
        org.assertj.core.api.Assertions.assertThat(response.raw()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(400);
        org.assertj.core.api.Assertions.assertThat(response.message()).isNull();
        org.assertj.core.api.Assertions.assertThat(response.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isFalse();
        org.assertj.core.api.Assertions.assertThat(response.body()).isNull();
        org.assertj.core.api.Assertions.assertThat(response.errorBody()).isSameAs(errorBody);
    }

    @org.junit.Test
    public void nullErrorThrows() {
        try {
            retrofit2.Response.error(400, null);
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("body == null");
        }
    }

    @org.junit.Test
    public void errorWithSuccessCodeThrows() {
        okhttp3.ResponseBody errorBody = okhttp3.ResponseBody.create(null, "Broken!");
        try {
            retrofit2.Response.error(200, errorBody);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("code < 400: 200");
        }
    }

    @org.junit.Test
    public void errorWithRawResponse() {
        okhttp3.ResponseBody errorBody = okhttp3.ResponseBody.create(null, "Broken!");
        retrofit2.Response<?> response = retrofit2.Response.error(errorBody, errorResponse);
        org.assertj.core.api.Assertions.assertThat(response.raw()).isSameAs(errorResponse);
        org.assertj.core.api.Assertions.assertThat(response.code()).isEqualTo(400);
        org.assertj.core.api.Assertions.assertThat(response.message()).isEqualTo("Broken!");
        org.assertj.core.api.Assertions.assertThat(response.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(response.isSuccessful()).isFalse();
        org.assertj.core.api.Assertions.assertThat(response.body()).isNull();
        org.assertj.core.api.Assertions.assertThat(response.errorBody()).isSameAs(errorBody);
    }

    @org.junit.Test
    public void nullErrorWithRawResponseThrows() {
        try {
            retrofit2.Response.error(null, errorResponse);
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("body == null");
        }
    }

    @org.junit.Test
    public void errorWithNullRawResponseThrows() {
        okhttp3.ResponseBody errorBody = okhttp3.ResponseBody.create(null, "Broken!");
        try {
            retrofit2.Response.error(errorBody, null);
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("rawResponse == null");
        }
    }

    @org.junit.Test
    public void errorWithSuccessRawResponseThrows() {
        okhttp3.ResponseBody errorBody = okhttp3.ResponseBody.create(null, "Broken!");
        try {
            retrofit2.Response.error(errorBody, successResponse);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("rawResponse should not be successful response");
        }
    }
}

