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


// Parameters inspected reflectively.
@java.lang.SuppressWarnings(value = { "UnusedParameters" , "unused" })
public final class AmplRequestBuilderTest {
    private static final okhttp3.MediaType TEXT_PLAIN = okhttp3.MediaType.parse("text/plain");

    @org.junit.Test
    public void customMethodNoBody() {
        class Example {
            @retrofit2.http.HTTP(method = "CUSTOM1", path = "/foo")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("CUSTOM1");
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Ignore(value = "https://github.com/square/okhttp/issues/229")
    @org.junit.Test
    public void customMethodWithBody() {
        class Example {
            @retrofit2.http.HTTP(method = "CUSTOM2", path = "/foo", hasBody = true)
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Body
            okhttp3.RequestBody body) {
                return null;
            }
        }
        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "hi");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, body);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("CUSTOM2");
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "hi");
    }

    @org.junit.Test
    public void onlyOneEncodingIsAllowedMultipartFirst() {
        class Example {
            // 
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Only one encoding annotation is allowed.\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void onlyOneEncodingIsAllowedFormEncodingFirst() {
        class Example {
            // 
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Only one encoding annotation is allowed.\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void invalidPathParam() throws java.lang.Exception {
        class Example {
            // 
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "hey!")
            java.lang.String thing) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("@Path parameter name must match \\{([a-zA-Z][a-zA-Z0-9_-]*)\\}." + " Found: hey! (parameter #1)\n    for method Example.method"));
        }
    }

    @org.junit.Test
    public void pathParamNotAllowedInQuery() throws java.lang.Exception {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo?bar={bar}")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "bar")
            java.lang.String thing) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("URL query string \"bar={bar}\" must not have replace block." + " For dynamic query parameters use @Query.\n    for method Example.method"));
        }
    }

    @org.junit.Test
    public void multipleParameterAnnotationsNotAllowed() throws java.lang.Exception {
        class Example {
            // 
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Body
            @retrofit2.http.Query(value = "nope")
            java.lang.String o) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Multiple Retrofit annotations found, only one allowed. (parameter #1)\n    for method Example.method");
        }
    }

    @interface NonNull {    }

    @org.junit.Test
    public void multipleParameterAnnotationsOnlyOneRetrofitAllowed() throws java.lang.Exception {
        class Example {
            // 
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Query(value = "maybe")
            @retrofit2.AmplRequestBuilderTest.NonNull
            java.lang.Object o) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "yep");
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/?maybe=yep");
    }

    @org.junit.Test
    public void twoMethodsFail() {
        class Example {
            // 
            // 
            @retrofit2.http.PATCH(value = "/foo")
            @retrofit2.http.POST(value = "/foo")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isIn("Only one HTTP method is allowed. Found: PATCH and POST.\n    for method Example.method", "Only one HTTP method is allowed. Found: POST and PATCH.\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void lackingMethod() {
        class Example {
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("HTTP method annotation is required (e.g., @GET, @POST, etc.).\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void implicitMultipartForbidden() {
        class Example {
            // 
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part(value = "a")
            int a) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("@Part parameters can only be used with multipart encoding. (parameter #1)\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void implicitMultipartWithPartMapForbidden() {
        class Example {
            // 
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.PartMap
            java.util.Map<java.lang.String, java.lang.String> params) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("@PartMap parameters can only be used with multipart encoding. (parameter #1)\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void multipartFailsOnNonBodyMethod() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Multipart can only be specified on HTTP methods with request body (e.g., @POST).\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void multipartFailsWithNoParts() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Multipart method must contain at least one @Part.\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void implicitFormEncodingByFieldForbidden() {
        class Example {
            // 
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Field(value = "a")
            int a) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("@Field parameters can only be used with form encoding. (parameter #1)\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void implicitFormEncodingByFieldMapForbidden() {
        class Example {
            // 
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.FieldMap
            java.util.Map<java.lang.String, java.lang.String> a) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("@FieldMap parameters can only be used with form encoding. (parameter #1)\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void formEncodingFailsOnNonBodyMethod() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("FormUrlEncoded can only be specified on HTTP methods with request body (e.g., @POST).\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void formEncodingFailsWithNoParts() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Form-encoded method must contain at least one @Field.\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void headersFailWhenEmptyOnMethod() {
        class Example {
            // 
            // 
            @retrofit2.http.GET(value = "/")
            @retrofit2.http.Headers(value = {  })
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("@Headers annotation is empty.\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void headersFailWhenMalformed() {
        class Example {
            // 
            // 
            @retrofit2.http.GET(value = "/")
            @retrofit2.http.Headers(value = "Malformed")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("@Headers value must be in the form \"Name: Value\". Found: \"Malformed\"\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void pathParamNonPathParamAndTypedBytes() {
        class Example {
            // 
            @retrofit2.http.PUT(value = "/{a}")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "a")
            int a, @retrofit2.http.Path(value = "b")
            int b, @retrofit2.http.Body
            int c) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("URL \"/{a}\" does not contain \"{b}\". (parameter #2)\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void parameterWithoutAnnotation() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(java.lang.String a) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("No Retrofit annotation found. (parameter #1)\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void nonBodyHttpMethodWithSingleEntity() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Body
            java.lang.String o) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Non-body HTTP method cannot contain @Body.\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void queryMapMustBeAMap() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.QueryMap
            java.util.List<java.lang.String> a) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("@QueryMap parameter type must be Map. (parameter #1)\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void queryMapSupportsSubclasses() {
        class Foo extends java.util.HashMap<java.lang.String, java.lang.String> {        }
        class Example {
            // 
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.QueryMap
            Foo a) {
                return null;
            }
        }
        Foo foo = new Foo();
        foo.put("hello", "world");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, foo);
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/?hello=world");
    }

    @org.junit.Test
    public void queryMapRejectsNull() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.QueryMap
            java.util.Map<java.lang.String, java.lang.String> a) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Query map was null.");
        }
    }

    @org.junit.Test
    public void queryMapRejectsNullKeys() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.QueryMap
            java.util.Map<java.lang.String, java.lang.String> a) {
                return null;
            }
        }
        java.util.Map<java.lang.String, java.lang.String> queryParams = new java.util.LinkedHashMap<>();
        queryParams.put("ping", "pong");
        queryParams.put(null, "kat");
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, queryParams);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Query map contained null key.");
        }
    }

    @org.junit.Test
    public void queryMapRejectsNullValues() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.QueryMap
            java.util.Map<java.lang.String, java.lang.String> a) {
                return null;
            }
        }
        java.util.Map<java.lang.String, java.lang.String> queryParams = new java.util.LinkedHashMap<>();
        queryParams.put("ping", "pong");
        queryParams.put("kit", null);
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, queryParams);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Query map contained null value for key 'kit'.");
        }
    }

    @org.junit.Test
    public void getWithHeaderMap() {
        class Example {
            @retrofit2.http.GET(value = "/search")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.HeaderMap
            java.util.Map<java.lang.String, java.lang.Object> headers) {
                return null;
            }
        }
        java.util.Map<java.lang.String, java.lang.Object> headers = new java.util.LinkedHashMap<>();
        headers.put("Accept", "text/plain");
        headers.put("Accept-Charset", "utf-8");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, headers);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/search");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(request.header("Accept")).isEqualTo("text/plain");
        org.assertj.core.api.Assertions.assertThat(request.header("Accept-Charset")).isEqualTo("utf-8");
    }

    @org.junit.Test
    public void headerMapMustBeAMap() {
        class Example {
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.HeaderMap
            java.util.List<java.lang.String> headers) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("@HeaderMap parameter type must be Map. (parameter #1)\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void headerMapSupportsSubclasses() {
        class Foo extends java.util.HashMap<java.lang.String, java.lang.String> {        }
        class Example {
            @retrofit2.http.GET(value = "/search")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.HeaderMap
            Foo headers) {
                return null;
            }
        }
        Foo headers = new Foo();
        headers.put("Accept", "text/plain");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, headers);
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/search");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(request.header("Accept")).isEqualTo("text/plain");
    }

    @org.junit.Test
    public void headerMapRejectsNull() {
        class Example {
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.HeaderMap
            java.util.Map<java.lang.String, java.lang.String> headers) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, ((java.util.Map<java.lang.String, java.lang.String>) (null)));
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Header map was null.");
        }
    }

    @org.junit.Test
    public void headerMapRejectsNullKeys() {
        class Example {
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.HeaderMap
            java.util.Map<java.lang.String, java.lang.String> headers) {
                return null;
            }
        }
        java.util.Map<java.lang.String, java.lang.String> headers = new java.util.LinkedHashMap<>();
        headers.put("Accept", "text/plain");
        headers.put(null, "utf-8");
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, headers);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Header map contained null key.");
        }
    }

    @org.junit.Test
    public void headerMapRejectsNullValues() {
        class Example {
            @retrofit2.http.GET(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.HeaderMap
            java.util.Map<java.lang.String, java.lang.String> headers) {
                return null;
            }
        }
        java.util.Map<java.lang.String, java.lang.String> headers = new java.util.LinkedHashMap<>();
        headers.put("Accept", "text/plain");
        headers.put("Accept-Charset", null);
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, headers);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Header map contained null value for key 'Accept-Charset'.");
        }
    }

    @org.junit.Test
    public void twoBodies() {
        class Example {
            // 
            @retrofit2.http.PUT(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Body
            java.lang.String o1, @retrofit2.http.Body
            java.lang.String o2) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Multiple @Body method annotations found. (parameter #2)\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void bodyInNonBodyRequest() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.PUT(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part(value = "one")
            java.lang.String o1, @retrofit2.http.Body
            java.lang.String o2) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("@Body parameters cannot be used with form or multi-part encoding. (parameter #2)\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void get() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void delete() {
        class Example {
            // 
            @retrofit2.http.DELETE(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("DELETE");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        org.junit.Assert.assertNull(request.body());
    }

    @org.junit.Test
    public void head() {
        class Example {
            // 
            @retrofit2.http.HEAD(value = "/foo/bar/")
            retrofit2.Call<java.lang.Void> method() {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("HEAD");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void headWithoutVoidThrows() {
        class Example {
            // 
            @retrofit2.http.HEAD(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("HEAD method must use Void as response type.\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void post() {
        class Example {
            // 
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Body
            okhttp3.RequestBody body) {
                return null;
            }
        }
        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "hi");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, body);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "hi");
    }

    @org.junit.Test
    public void put() {
        class Example {
            // 
            @retrofit2.http.PUT(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Body
            okhttp3.RequestBody body) {
                return null;
            }
        }
        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "hi");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, body);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("PUT");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "hi");
    }

    @org.junit.Test
    public void patch() {
        class Example {
            // 
            @retrofit2.http.PATCH(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Body
            okhttp3.RequestBody body) {
                return null;
            }
        }
        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "hi");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, body);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("PATCH");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "hi");
    }

    @org.junit.Test
    public void options() {
        class Example {
            // 
            @retrofit2.http.OPTIONS(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("OPTIONS");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithPathParam() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/{ping}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "ping")
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "po ng");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/po%20ng/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithUnusedAndInvalidNamedPathParam() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/{ping}/{kit,kat}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "ping")
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "pong");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/pong/%7Bkit,kat%7D/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithEncodedPathParam() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/{ping}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "ping", encoded = true)
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "po%20ng");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/po%20ng/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithEncodedPathSegments() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/{ping}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "ping", encoded = true)
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "baz/pong/more");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/baz/pong/more/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithUnencodedPathSegmentsPreventsRequestSplitting() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/{ping}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "ping", encoded = false)
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "baz/\r\nheader: blue");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/baz%2F%0D%0Aheader:%20blue/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithEncodedPathStillPreventsRequestSplitting() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/{ping}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "ping", encoded = true)
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "baz/\r\npong");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/baz/pong/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void pathParamRequired() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/{ping}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "ping")
            java.lang.String ping) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Path parameter \"ping\" value must not be null.");
        }
    }

    @org.junit.Test
    public void getWithQueryParam() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Query(value = "ping")
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "pong");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?ping=pong");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithEncodedQueryParam() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Query(value = "pi%20ng", encoded = true)
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "p%20o%20n%20g");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?pi%20ng=p%20o%20n%20g");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void queryParamOptionalOmitsQuery() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Query(value = "ping")
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
    }

    @org.junit.Test
    public void queryParamOptional() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Query(value = "foo")
            java.lang.String foo, @retrofit2.http.Query(value = "ping")
            java.lang.String ping, @retrofit2.http.Query(value = "kit")
            java.lang.String kit) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "bar", null, "kat");
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?foo=bar&kit=kat");
    }

    @org.junit.Test
    public void getWithQueryUrlAndParam() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/?hi=mom")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Query(value = "ping")
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "pong");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?hi=mom&ping=pong");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithQuery() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/?hi=mom")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?hi=mom");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithPathAndQueryParam() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/{ping}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "ping")
            java.lang.String ping, @retrofit2.http.Query(value = "kit")
            java.lang.String kit, @retrofit2.http.Query(value = "riff")
            java.lang.String riff) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "pong", "kat", "raff");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/pong/?kit=kat&riff=raff");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithQueryThenPathThrows() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/{ping}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Query(value = "kit")
            java.lang.String kit, @retrofit2.http.Path(value = "ping")
            java.lang.String ping) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "kat", "pong");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("A @Path parameter must not come after a @Query. (parameter #2)\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void getWithPathAndQueryQuestionMarkParam() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/{ping}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "ping")
            java.lang.String ping, @retrofit2.http.Query(value = "kit")
            java.lang.String kit) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "pong?", "kat?");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/pong%3F/?kit=kat?");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithPathAndQueryAmpersandParam() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/{ping}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "ping")
            java.lang.String ping, @retrofit2.http.Query(value = "kit")
            java.lang.String kit) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "pong&", "kat&");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/pong&/?kit=kat%26");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithPathAndQueryHashParam() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/{ping}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "ping")
            java.lang.String ping, @retrofit2.http.Query(value = "kit")
            java.lang.String kit) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "pong#", "kat#");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/pong%23/?kit=kat%23");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithQueryParamList() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Query(value = "key")
            java.util.List<java.lang.Object> keys) {
                return null;
            }
        }
        java.util.List<java.lang.Object> values = java.util.Arrays.<java.lang.Object>asList(1, 2, null, "three", "1");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, values);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?key=1&key=2&key=three&key=1");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithQueryParamArray() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Query(value = "key")
            java.lang.Object[] keys) {
                return null;
            }
        }
        java.lang.Object[] values = new java.lang.Object[]{ 1 , 2 , null , "three" , "1" };
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ values });
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?key=1&key=2&key=three&key=1");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithQueryParamPrimitiveArray() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Query(value = "key")
            int[] keys) {
                return null;
            }
        }
        int[] values = new int[]{ 1 , 2 , 3 , 1 };
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ values });
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?key=1&key=2&key=3&key=1");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithQueryNameParam() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.QueryName
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "pong");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?pong");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithEncodedQueryNameParam() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.QueryName(encoded = true)
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "p%20o%20n%20g");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?p%20o%20n%20g");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void queryNameParamOptionalOmitsQuery() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.QueryName
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
    }

    @org.junit.Test
    public void getWithQueryNameParamList() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.QueryName
            java.util.List<java.lang.Object> keys) {
                return null;
            }
        }
        java.util.List<java.lang.Object> values = java.util.Arrays.<java.lang.Object>asList(1, 2, null, "three", "1");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, values);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?1&2&three&1");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithQueryNameParamArray() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.QueryName
            java.lang.Object[] keys) {
                return null;
            }
        }
        java.lang.Object[] values = new java.lang.Object[]{ 1 , 2 , null , "three" , "1" };
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ values });
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?1&2&three&1");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithQueryNameParamPrimitiveArray() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.QueryName
            int[] keys) {
                return null;
            }
        }
        int[] values = new int[]{ 1 , 2 , 3 , 1 };
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ values });
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?1&2&3&1");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithQueryParamMap() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.QueryMap
            java.util.Map<java.lang.String, java.lang.Object> query) {
                return null;
            }
        }
        java.util.Map<java.lang.String, java.lang.Object> params = new java.util.LinkedHashMap<>();
        params.put("kit", "kat");
        params.put("ping", "pong");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, params);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?kit=kat&ping=pong");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithEncodedQueryParamMap() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.QueryMap(encoded = true)
            java.util.Map<java.lang.String, java.lang.Object> query) {
                return null;
            }
        }
        java.util.Map<java.lang.String, java.lang.Object> params = new java.util.LinkedHashMap<>();
        params.put("kit", "k%20t");
        params.put("pi%20ng", "p%20g");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, params);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?kit=k%20t&pi%20ng=p%20g");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getAbsoluteUrl() {
        class Example {
            @retrofit2.http.GET(value = "http://example2.com/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example2.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithStringUrl() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            java.lang.String url) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithJavaUriUrl() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            java.net.URI url) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, java.net.URI.create("foo/bar/"));
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithStringUrlAbsolute() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            java.lang.String url) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "https://example2.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("https://example2.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithJavaUriUrlAbsolute() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            java.net.URI url) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, java.net.URI.create("https://example2.com/foo/bar/"));
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("https://example2.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithUrlAbsoluteSameHost() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            java.lang.String url) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "http://example.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithHttpUrl() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            okhttp3.HttpUrl url) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, okhttp3.HttpUrl.parse("http://example.com/foo/bar/"));
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url()).isEqualTo(okhttp3.HttpUrl.parse("http://example.com/foo/bar/"));
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithNullUrl() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            okhttp3.HttpUrl url) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, ((okhttp3.HttpUrl) (null)));
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage("@Url parameter is null.");
        }
    }

    @org.junit.Test
    public void getWithNonStringUrlThrows() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            java.lang.Object url) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "foo/bar");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("@Url must be okhttp3.HttpUrl, String, java.net.URI, or android.net.Uri type." + (" (parameter #1)\n" + "    for method Example.method")));
        }
    }

    @org.junit.Test
    public void getUrlAndUrlParamThrows() {
        class Example {
            @retrofit2.http.GET(value = "foo/bar")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            java.lang.Object url) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "foo/bar");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("@Url cannot be used with @GET URL (parameter #1)\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void getWithoutUrlThrows() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("Missing either @GET URL or @Url parameter.\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void getWithUrlThenPathThrows() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            java.lang.String url, @retrofit2.http.Path(value = "hey")
            java.lang.String hey) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "foo/bar");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("@Path parameters may not be used with @Url. (parameter #2)\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void getWithPathThenUrlThrows() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "hey")
            java.lang.String hey, @retrofit2.http.Url
            java.lang.Object url) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "foo/bar");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("@Path can only be used with relative url on @GET (parameter #1)\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void getWithQueryThenUrlThrows() {
        class Example {
            @retrofit2.http.GET(value = "foo/bar")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Query(value = "hey")
            java.lang.String hey, @retrofit2.http.Url
            java.lang.Object url) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "hey", "foo/bar/");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("A @Url parameter must not come after a @Query (parameter #2)\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void getWithUrlThenQuery() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            java.lang.String url, @retrofit2.http.Query(value = "hey")
            java.lang.String hey) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "foo/bar/", "hey!");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/?hey=hey!");
    }

    @org.junit.Test
    public void postWithUrl() {
        class Example {
            @retrofit2.http.POST
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            java.lang.String url, @retrofit2.http.Body
            okhttp3.RequestBody body) {
                return null;
            }
        }
        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "hi");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "http://example.com/foo/bar", body);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "hi");
    }

    @org.junit.Test
    public void normalPostWithPathParam() {
        class Example {
            // 
            @retrofit2.http.POST(value = "/foo/bar/{ping}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "ping")
            java.lang.String ping, @retrofit2.http.Body
            okhttp3.RequestBody body) {
                return null;
            }
        }
        okhttp3.RequestBody body = okhttp3.RequestBody.create(retrofit2.AmplRequestBuilderTest.TEXT_PLAIN, "Hi!");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "pong", body);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/pong/");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "Hi!");
    }

    @org.junit.Test
    public void emptyBody() {
        class Example {
            // 
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "");
    }

    @org.junit.Ignore(value = "https://github.com/square/okhttp/issues/229")
    @org.junit.Test
    public void customMethodEmptyBody() {
        class Example {
            // 
            @retrofit2.http.HTTP(method = "CUSTOM", path = "/foo/bar/", hasBody = true)
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("CUSTOM");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "");
    }

    @org.junit.Test
    public void bodyRequired() {
        class Example {
            // 
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Body
            okhttp3.RequestBody body) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Body parameter value must not be null.");
        }
    }

    @org.junit.Test
    public void bodyWithPathParams() {
        class Example {
            // 
            @retrofit2.http.POST(value = "/foo/bar/{ping}/{kit}/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Path(value = "ping")
            java.lang.String ping, @retrofit2.http.Body
            okhttp3.RequestBody body, @retrofit2.http.Path(value = "kit")
            java.lang.String kit) {
                return null;
            }
        }
        okhttp3.RequestBody body = okhttp3.RequestBody.create(retrofit2.AmplRequestBuilderTest.TEXT_PLAIN, "Hi!");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "pong", body, "kat");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/pong/kat/");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "Hi!");
    }

    @org.junit.Test
    public void simpleMultipart() throws java.io.IOException {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part(value = "ping")
            java.lang.String ping, @retrofit2.http.Part(value = "kit")
            okhttp3.RequestBody kit) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "pong", okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "kat"));
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        okhttp3.RequestBody body = request.body();
        okio.Buffer buffer = new okio.Buffer();
        body.writeTo(buffer);
        java.lang.String bodyString = buffer.readUtf8();
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"ping\"\r\n").contains("\r\npong\r\n--");
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"kit\"").contains("\r\nkat\r\n--");
    }

    @org.junit.Test
    public void multipartArray() throws java.io.IOException {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part(value = "ping")
            java.lang.String[] ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ new java.lang.String[]{ "pong1" , "pong2" } });
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        okhttp3.RequestBody body = request.body();
        okio.Buffer buffer = new okio.Buffer();
        body.writeTo(buffer);
        java.lang.String bodyString = buffer.readUtf8();
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"ping\"\r\n").contains("\r\npong1\r\n--");
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"ping\"").contains("\r\npong2\r\n--");
    }

    @org.junit.Test
    public void multipartRequiresName() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part
            okhttp3.RequestBody part) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("@Part annotation must supply a name or use MultipartBody.Part parameter type. (parameter #1)\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void multipartIterableRequiresName() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part
            java.util.List<okhttp3.RequestBody> part) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("@Part annotation must supply a name or use MultipartBody.Part parameter type. (parameter #1)\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void multipartArrayRequiresName() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part
            okhttp3.RequestBody[] part) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("@Part annotation must supply a name or use MultipartBody.Part parameter type. (parameter #1)\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void multipartOkHttpPartForbidsName() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part(value = "name")
            okhttp3.MultipartBody.Part part) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("@Part parameters using the MultipartBody.Part must not include a part name in the annotation. (parameter #1)\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void multipartOkHttpPart() throws java.io.IOException {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part
            okhttp3.MultipartBody.Part part) {
                return null;
            }
        }
        okhttp3.MultipartBody.Part part = okhttp3.MultipartBody.Part.createFormData("kit", "kat");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, part);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        okhttp3.RequestBody body = request.body();
        okio.Buffer buffer = new okio.Buffer();
        body.writeTo(buffer);
        java.lang.String bodyString = buffer.readUtf8();
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"kit\"\r\n").contains("\r\nkat\r\n--");
    }

    @org.junit.Test
    public void multipartOkHttpIterablePart() throws java.io.IOException {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part
            java.util.List<okhttp3.MultipartBody.Part> part) {
                return null;
            }
        }
        okhttp3.MultipartBody.Part part1 = okhttp3.MultipartBody.Part.createFormData("foo", "bar");
        okhttp3.MultipartBody.Part part2 = okhttp3.MultipartBody.Part.createFormData("kit", "kat");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, java.util.Arrays.asList(part1, part2));
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        okhttp3.RequestBody body = request.body();
        okio.Buffer buffer = new okio.Buffer();
        body.writeTo(buffer);
        java.lang.String bodyString = buffer.readUtf8();
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"foo\"\r\n").contains("\r\nbar\r\n--");
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"kit\"\r\n").contains("\r\nkat\r\n--");
    }

    @org.junit.Test
    public void multipartOkHttpArrayPart() throws java.io.IOException {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part
            okhttp3.MultipartBody.Part[] part) {
                return null;
            }
        }
        okhttp3.MultipartBody.Part part1 = okhttp3.MultipartBody.Part.createFormData("foo", "bar");
        okhttp3.MultipartBody.Part part2 = okhttp3.MultipartBody.Part.createFormData("kit", "kat");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ new okhttp3.MultipartBody.Part[]{ part1 , part2 } });
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        okhttp3.RequestBody body = request.body();
        okio.Buffer buffer = new okio.Buffer();
        body.writeTo(buffer);
        java.lang.String bodyString = buffer.readUtf8();
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"foo\"\r\n").contains("\r\nbar\r\n--");
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"kit\"\r\n").contains("\r\nkat\r\n--");
    }

    @org.junit.Test
    public void multipartOkHttpPartWithFilename() throws java.io.IOException {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part
            okhttp3.MultipartBody.Part part) {
                return null;
            }
        }
        okhttp3.MultipartBody.Part part = okhttp3.MultipartBody.Part.createFormData("kit", "kit.txt", okhttp3.RequestBody.create(null, "kat"));
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, part);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        okhttp3.RequestBody body = request.body();
        okio.Buffer buffer = new okio.Buffer();
        body.writeTo(buffer);
        java.lang.String bodyString = buffer.readUtf8();
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"kit\"; filename=\"kit.txt\"\r\n").contains("\r\nkat\r\n--");
    }

    @org.junit.Test
    public void multipartIterable() throws java.io.IOException {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part(value = "ping")
            java.util.List<java.lang.String> ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, java.util.Arrays.asList("pong1", "pong2"));
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        okhttp3.RequestBody body = request.body();
        okio.Buffer buffer = new okio.Buffer();
        body.writeTo(buffer);
        java.lang.String bodyString = buffer.readUtf8();
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"ping\"\r\n").contains("\r\npong1\r\n--");
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"ping\"").contains("\r\npong2\r\n--");
    }

    @org.junit.Test
    public void multipartIterableOkHttpPart() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part(value = "ping")
            java.util.List<okhttp3.MultipartBody.Part> part) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("@Part parameters using the MultipartBody.Part must not include a part name in the annotation. (parameter #1)\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void multipartArrayOkHttpPart() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part(value = "ping")
            okhttp3.MultipartBody.Part[] part) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("@Part parameters using the MultipartBody.Part must not include a part name in the annotation. (parameter #1)\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void multipartWithEncoding() throws java.io.IOException {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part(value = "ping", encoding = "8-bit")
            java.lang.String ping, @retrofit2.http.Part(value = "kit", encoding = "7-bit")
            okhttp3.RequestBody kit) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "pong", okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "kat"));
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        okhttp3.RequestBody body = request.body();
        okio.Buffer buffer = new okio.Buffer();
        body.writeTo(buffer);
        java.lang.String bodyString = buffer.readUtf8();
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"ping\"\r\n").contains("Content-Transfer-Encoding: 8-bit").contains("\r\npong\r\n--");
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"kit\"").contains("Content-Transfer-Encoding: 7-bit").contains("\r\nkat\r\n--");
    }

    @org.junit.Test
    public void multipartPartMap() throws java.io.IOException {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.PartMap
            java.util.Map<java.lang.String, okhttp3.RequestBody> parts) {
                return null;
            }
        }
        java.util.Map<java.lang.String, okhttp3.RequestBody> params = new java.util.LinkedHashMap<>();
        params.put("ping", okhttp3.RequestBody.create(null, "pong"));
        params.put("kit", okhttp3.RequestBody.create(null, "kat"));
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, params);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        okhttp3.RequestBody body = request.body();
        okio.Buffer buffer = new okio.Buffer();
        body.writeTo(buffer);
        java.lang.String bodyString = buffer.readUtf8();
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"ping\"\r\n").contains("\r\npong\r\n--");
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"kit\"").contains("\r\nkat\r\n--");
    }

    @org.junit.Test
    public void multipartPartMapWithEncoding() throws java.io.IOException {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.PartMap(encoding = "8-bit")
            java.util.Map<java.lang.String, okhttp3.RequestBody> parts) {
                return null;
            }
        }
        java.util.Map<java.lang.String, okhttp3.RequestBody> params = new java.util.LinkedHashMap<>();
        params.put("ping", okhttp3.RequestBody.create(null, "pong"));
        params.put("kit", okhttp3.RequestBody.create(null, "kat"));
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, params);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        okhttp3.RequestBody body = request.body();
        okio.Buffer buffer = new okio.Buffer();
        body.writeTo(buffer);
        java.lang.String bodyString = buffer.readUtf8();
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"ping\"\r\n").contains("Content-Transfer-Encoding: 8-bit").contains("\r\npong\r\n--");
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"kit\"").contains("Content-Transfer-Encoding: 8-bit").contains("\r\nkat\r\n--");
    }

    @org.junit.Test
    public void multipartPartMapRejectsNonStringKeys() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.PartMap
            java.util.Map<java.lang.Object, okhttp3.RequestBody> parts) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("@PartMap keys must be of type String: class java.lang.Object (parameter #1)\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void multipartPartMapRejectsOkHttpPartValues() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.PartMap
            java.util.Map<java.lang.String, okhttp3.MultipartBody.Part> parts) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("@PartMap values cannot be MultipartBody.Part. Use @Part List<Part> or a different value type instead. (parameter #1)\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void multipartPartMapRejectsNull() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.PartMap
            java.util.Map<java.lang.String, okhttp3.RequestBody> parts) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Part map was null.");
        }
    }

    @org.junit.Test
    public void multipartPartMapRejectsNullKeys() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.PartMap
            java.util.Map<java.lang.String, okhttp3.RequestBody> parts) {
                return null;
            }
        }
        java.util.Map<java.lang.String, okhttp3.RequestBody> params = new java.util.LinkedHashMap<>();
        params.put("ping", okhttp3.RequestBody.create(null, "pong"));
        params.put(null, okhttp3.RequestBody.create(null, "kat"));
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, params);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Part map contained null key.");
        }
    }

    @org.junit.Test
    public void multipartPartMapRejectsNullValues() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.PartMap
            java.util.Map<java.lang.String, okhttp3.RequestBody> parts) {
                return null;
            }
        }
        java.util.Map<java.lang.String, okhttp3.RequestBody> params = new java.util.LinkedHashMap<>();
        params.put("ping", okhttp3.RequestBody.create(null, "pong"));
        params.put("kit", null);
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, params);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Part map contained null value for key 'kit'.");
        }
    }

    @org.junit.Test
    public void multipartPartMapMustBeMap() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.PartMap
            java.util.List<java.lang.Object> parts) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, java.util.Collections.emptyList());
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("@PartMap parameter type must be Map. (parameter #1)\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void multipartPartMapSupportsSubclasses() throws java.io.IOException {
        class Foo extends java.util.HashMap<java.lang.String, java.lang.String> {        }
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.PartMap
            Foo parts) {
                return null;
            }
        }
        Foo foo = new Foo();
        foo.put("hello", "world");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, foo);
        okio.Buffer buffer = new okio.Buffer();
        request.body().writeTo(buffer);
        org.assertj.core.api.Assertions.assertThat(buffer.readUtf8()).contains("name=\"hello\"").contains("\r\n\r\nworld\r\n--");
    }

    @org.junit.Test
    public void multipartNullRemovesPart() throws java.io.IOException {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part(value = "ping")
            java.lang.String ping, @retrofit2.http.Part(value = "fizz")
            java.lang.String fizz) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "pong", null);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("POST");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        okhttp3.RequestBody body = request.body();
        okio.Buffer buffer = new okio.Buffer();
        body.writeTo(buffer);
        java.lang.String bodyString = buffer.readUtf8();
        org.assertj.core.api.Assertions.assertThat(bodyString).contains("Content-Disposition: form-data;").contains("name=\"ping\"").contains("\r\npong\r\n--");
    }

    @org.junit.Test
    public void multipartPartOptional() {
        class Example {
            // 
            // 
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Part(value = "ping")
            okhttp3.RequestBody ping) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException e) {
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Multipart body must have at least one part.");
        }
    }

    @org.junit.Test
    public void simpleFormEncoded() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/foo")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Field(value = "foo")
            java.lang.String foo, @retrofit2.http.Field(value = "ping")
            java.lang.String ping) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "bar", "pong");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "foo=bar&ping=pong");
    }

    @org.junit.Test
    public void formEncodedWithEncodedNameFieldParam() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/foo")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Field(value = "na%20me", encoded = true)
            java.lang.String foo) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "ba%20r");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "na%20me=ba%20r");
    }

    @org.junit.Test
    public void formEncodedFieldOptional() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/foo")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Field(value = "foo")
            java.lang.String foo, @retrofit2.http.Field(value = "ping")
            java.lang.String ping, @retrofit2.http.Field(value = "kit")
            java.lang.String kit) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "bar", null, "kat");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "foo=bar&kit=kat");
    }

    @org.junit.Test
    public void formEncodedFieldList() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/foo")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Field(value = "foo")
            java.util.List<java.lang.Object> fields, @retrofit2.http.Field(value = "kit")
            java.lang.String kit) {
                return null;
            }
        }
        java.util.List<java.lang.Object> values = java.util.Arrays.<java.lang.Object>asList("foo", "bar", null, 3);
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, values, "kat");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "foo=foo&foo=bar&foo=3&kit=kat");
    }

    @org.junit.Test
    public void formEncodedFieldArray() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/foo")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Field(value = "foo")
            java.lang.Object[] fields, @retrofit2.http.Field(value = "kit")
            java.lang.String kit) {
                return null;
            }
        }
        java.lang.Object[] values = new java.lang.Object[]{ 1 , 2 , null , "three" };
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, values, "kat");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "foo=1&foo=2&foo=three&kit=kat");
    }

    @org.junit.Test
    public void formEncodedFieldPrimitiveArray() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/foo")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Field(value = "foo")
            int[] fields, @retrofit2.http.Field(value = "kit")
            java.lang.String kit) {
                return null;
            }
        }
        int[] values = new int[]{ 1 , 2 , 3 };
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, values, "kat");
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "foo=1&foo=2&foo=3&kit=kat");
    }

    @org.junit.Test
    public void formEncodedWithEncodedNameFieldParamMap() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/foo")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.FieldMap(encoded = true)
            java.util.Map<java.lang.String, java.lang.Object> fieldMap) {
                return null;
            }
        }
        java.util.Map<java.lang.String, java.lang.Object> fieldMap = new java.util.LinkedHashMap<>();
        fieldMap.put("k%20it", "k%20at");
        fieldMap.put("pin%20g", "po%20ng");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, fieldMap);
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "k%20it=k%20at&pin%20g=po%20ng");
    }

    @org.junit.Test
    public void formEncodedFieldMap() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/foo")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.FieldMap
            java.util.Map<java.lang.String, java.lang.Object> fieldMap) {
                return null;
            }
        }
        java.util.Map<java.lang.String, java.lang.Object> fieldMap = new java.util.LinkedHashMap<>();
        fieldMap.put("kit", "kat");
        fieldMap.put("ping", "pong");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, fieldMap);
        retrofit2.AmplRequestBuilderTest.assertBody(request.body(), "kit=kat&ping=pong");
    }

    @org.junit.Test
    public void fieldMapRejectsNull() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.FieldMap
            java.util.Map<java.lang.String, java.lang.Object> a) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.lang.Object[]{ null });
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Field map was null.");
        }
    }

    @org.junit.Test
    public void fieldMapRejectsNullKeys() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.FieldMap
            java.util.Map<java.lang.String, java.lang.Object> a) {
                return null;
            }
        }
        java.util.Map<java.lang.String, java.lang.Object> fieldMap = new java.util.LinkedHashMap<>();
        fieldMap.put("kit", "kat");
        fieldMap.put(null, "pong");
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, fieldMap);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Field map contained null key.");
        }
    }

    @org.junit.Test
    public void fieldMapRejectsNullValues() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.FieldMap
            java.util.Map<java.lang.String, java.lang.Object> a) {
                return null;
            }
        }
        java.util.Map<java.lang.String, java.lang.Object> fieldMap = new java.util.LinkedHashMap<>();
        fieldMap.put("kit", "kat");
        fieldMap.put("foo", null);
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, fieldMap);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Field map contained null value for key 'foo'.");
        }
    }

    @org.junit.Test
    public void fieldMapMustBeAMap() {
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.FieldMap
            java.util.List<java.lang.String> a) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("@FieldMap parameter type must be Map. (parameter #1)\n    for method Example.method");
        }
    }

    @org.junit.Test
    public void fieldMapSupportsSubclasses() throws java.io.IOException {
        class Foo extends java.util.HashMap<java.lang.String, java.lang.String> {        }
        class Example {
            // 
            // 
            @retrofit2.http.FormUrlEncoded
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.FieldMap
            Foo a) {
                return null;
            }
        }
        Foo foo = new Foo();
        foo.put("hello", "world");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, foo);
        okio.Buffer buffer = new okio.Buffer();
        request.body().writeTo(buffer);
        org.assertj.core.api.Assertions.assertThat(buffer.readUtf8()).isEqualTo("hello=world");
    }

    @org.junit.Test
    public void simpleHeaders() {
        class Example {
            @retrofit2.http.GET(value = "/foo/bar/")
            @retrofit2.http.Headers(value = { "ping: pong" , "kit: kat" })
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        okhttp3.Headers headers = request.headers();
        org.assertj.core.api.Assertions.assertThat(headers.size()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(headers.get("ping")).isEqualTo("pong");
        org.assertj.core.api.Assertions.assertThat(headers.get("kit")).isEqualTo("kat");
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void headerParamToString() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Header(value = "kit")
            java.math.BigInteger kit) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, new java.math.BigInteger("1234"));
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        okhttp3.Headers headers = request.headers();
        org.assertj.core.api.Assertions.assertThat(headers.size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(headers.get("kit")).isEqualTo("1234");
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void headerParam() {
        class Example {
            // 
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            @retrofit2.http.Headers(value = "ping: pong")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Header(value = "kit")
            java.lang.String kit) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "kat");
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        okhttp3.Headers headers = request.headers();
        org.assertj.core.api.Assertions.assertThat(headers.size()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(headers.get("ping")).isEqualTo("pong");
        org.assertj.core.api.Assertions.assertThat(headers.get("kit")).isEqualTo("kat");
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void headerParamList() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Header(value = "foo")
            java.util.List<java.lang.String> kit) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, java.util.Arrays.asList("bar", null, "baz"));
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        okhttp3.Headers headers = request.headers();
        org.assertj.core.api.Assertions.assertThat(headers.size()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(headers.values("foo")).containsExactly("bar", "baz");
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void headerParamArray() {
        class Example {
            // 
            @retrofit2.http.GET(value = "/foo/bar/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Header(value = "foo")
            java.lang.String[] kit) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, ((java.lang.Object) (new java.lang.String[]{ "bar" , null , "baz" })));
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        okhttp3.Headers headers = request.headers();
        org.assertj.core.api.Assertions.assertThat(headers.size()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(headers.values("foo")).containsExactly("bar", "baz");
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void contentTypeAnnotationHeaderOverrides() {
        class Example {
            // 
            // 
            @retrofit2.http.POST(value = "/")
            @retrofit2.http.Headers(value = "Content-Type: text/not-plain")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Body
            okhttp3.RequestBody body) {
                return null;
            }
        }
        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "hi");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, body);
        org.assertj.core.api.Assertions.assertThat(request.body().contentType().toString()).isEqualTo("text/not-plain");
    }

    @org.junit.Test
    public void malformedContentTypeHeaderThrows() {
        class Example {
            // 
            // 
            @retrofit2.http.POST(value = "/")
            @retrofit2.http.Headers(value = "Content-Type: hello, world!")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Body
            okhttp3.RequestBody body) {
                return null;
            }
        }
        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "hi");
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, body);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage(("Malformed content type: hello, world!\n" + "    for method Example.method"));
        }
    }

    @org.junit.Test
    public void contentTypeAnnotationHeaderAddsHeaderWithNoBody() {
        class Example {
            // 
            // 
            @retrofit2.http.DELETE(value = "/")
            @retrofit2.http.Headers(value = "Content-Type: text/not-plain")
            retrofit2.Call<okhttp3.ResponseBody> method() {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
        org.assertj.core.api.Assertions.assertThat(request.headers().get("Content-Type")).isEqualTo("text/not-plain");
    }

    @org.junit.Test
    public void contentTypeParameterHeaderOverrides() {
        class Example {
            // 
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Header(value = "Content-Type")
            java.lang.String contentType, @retrofit2.http.Body
            okhttp3.RequestBody body) {
                return null;
            }
        }
        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "Plain");
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "text/not-plain", body);
        org.assertj.core.api.Assertions.assertThat(request.body().contentType().toString()).isEqualTo("text/not-plain");
    }

    @org.junit.Test
    public void malformedContentTypeParameterThrows() {
        class Example {
            // 
            @retrofit2.http.POST(value = "/")
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Header(value = "Content-Type")
            java.lang.String contentType, @retrofit2.http.Body
            okhttp3.RequestBody body) {
                return null;
            }
        }
        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "hi");
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "hello, world!", body);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Malformed content type: hello, world!");
        }
    }

    @org.junit.Test
    public void malformedAnnotationRelativeUrlThrows() {
        class Example {
            @retrofit2.http.GET(value = "ftp://example.org")
            retrofit2.Call<okhttp3.ResponseBody> get() {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Malformed URL. Base: http://example.com/, Relative: ftp://example.org");
        }
    }

    @org.junit.Test
    public void malformedParameterRelativeUrlThrows() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> get(@retrofit2.http.Url
            java.lang.String relativeUrl) {
                return null;
            }
        }
        try {
            retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "ftp://example.org");
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Malformed URL. Base: http://example.com/, Relative: ftp://example.org");
        }
    }

    @org.junit.Test
    public void multipartPartsShouldBeInOrder() throws java.io.IOException {
        class Example {
            @retrofit2.http.Multipart
            @retrofit2.http.POST(value = "/foo")
            retrofit2.Call<okhttp3.ResponseBody> get(@retrofit2.http.Part(value = "first")
            java.lang.String data, @retrofit2.http.Part(value = "second")
            java.lang.String dataTwo, @retrofit2.http.Part(value = "third")
            java.lang.String dataThree) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.AmplRequestBuilderTest.buildRequest(Example.class, "firstParam", "secondParam", "thirdParam");
        okhttp3.MultipartBody body = ((okhttp3.MultipartBody) (request.body()));
        okio.Buffer buffer = new okio.Buffer();
        body.writeTo(buffer);
        java.lang.String readBody = buffer.readUtf8();
        org.assertj.core.api.Assertions.assertThat(readBody.indexOf("firstParam")).isLessThan(readBody.indexOf("secondParam"));
        org.assertj.core.api.Assertions.assertThat(readBody.indexOf("secondParam")).isLessThan(readBody.indexOf("thirdParam"));
    }

    private static void assertBody(okhttp3.RequestBody body, java.lang.String expected) {
        org.assertj.core.api.Assertions.assertThat(body).isNotNull();
        okio.Buffer buffer = new okio.Buffer();
        try {
            body.writeTo(buffer);
            org.assertj.core.api.Assertions.assertThat(buffer.readUtf8()).isEqualTo(expected);
        } catch (java.io.IOException e) {
            throw new java.lang.RuntimeException(e);
        }
    }

    static <T> okhttp3.Request buildRequest(java.lang.Class<T> cls, java.lang.Object... args) {
        final java.util.concurrent.atomic.AtomicReference<okhttp3.Request> requestRef = new java.util.concurrent.atomic.AtomicReference<>();
        okhttp3.Call.Factory callFactory = new okhttp3.Call.Factory() {
            @java.lang.Override
            public okhttp3.Call newCall(okhttp3.Request request) {
                requestRef.set(request);
                throw new java.lang.UnsupportedOperationException("Not implemented");
            }
        };
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("http://example.com/").addConverterFactory(new retrofit2.helpers.ToStringConverterFactory()).callFactory(callFactory).build();
        java.lang.reflect.Method method = retrofit2.TestingUtils.onlyMethod(cls);
        // noinspection unchecked
        retrofit2.ServiceMethod<T, retrofit2.Call<T>> serviceMethod = ((retrofit2.ServiceMethod<T, retrofit2.Call<T>>) (retrofit.loadServiceMethod(method)));
        retrofit2.Call<T> okHttpCall = new retrofit2.OkHttpCall<>(serviceMethod, args);
        retrofit2.Call<T> call = serviceMethod.callAdapter.adapt(okHttpCall);
        try {
            call.execute();
            throw new java.lang.AssertionError();
        } catch (java.lang.UnsupportedOperationException ignored) {
            return requestRef.get();
        } catch (java.lang.RuntimeException e) {
            throw e;
        } catch (java.lang.Exception e) {
            throw new java.lang.AssertionError(e);
        }
    }
}

