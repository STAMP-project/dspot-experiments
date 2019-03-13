/**
 * Copyright 2012-2019 The Feign Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package feign;


import ErrorDecoder.Default;
import Feign.Builder;
import HttpMethod.GET;
import SocketPolicy.DISCONNECT_AT_START;
import Util.UTF_8;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import feign.Feign.ResponseMappingDecoder;
import feign.Request.HttpMethod;
import feign.assertj.MockWebServerAssertions;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.codec.StringDecoder;
import feign.querymap.BeanQueryMapEncoder;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class FeignTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void iterableQueryParams() throws Exception {
        server.enqueue(new MockResponse().setBody("foo"));
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        api.queryParams("user", Arrays.asList("apple", "pear"));
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?1=user&2=apple&2=pear");
    }

    @Test
    public void postTemplateParamsResolve() throws Exception {
        server.enqueue(new MockResponse().setBody("foo"));
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        api.login("netflix", "denominator", "password");
        MockWebServerAssertions.assertThat(server.takeRequest()).hasBody("{\"customer_name\": \"netflix\", \"user_name\": \"denominator\", \"password\": \"password\"}");
    }

    @Test
    public void responseCoercesToStringBody() throws Exception {
        server.enqueue(new MockResponse().setBody("foo"));
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        Response response = api.response();
        Assert.assertTrue(response.body().isRepeatable());
        Assert.assertEquals("foo", response.body().toString());
    }

    @Test
    public void postFormParams() throws Exception {
        server.enqueue(new MockResponse().setBody("foo"));
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        api.form("netflix", "denominator", "password");
        MockWebServerAssertions.assertThat(server.takeRequest()).hasBody("{\"customer_name\":\"netflix\",\"user_name\":\"denominator\",\"password\":\"password\"}");
    }

    @Test
    public void postBodyParam() throws Exception {
        server.enqueue(new MockResponse().setBody("foo"));
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        api.body(Arrays.asList("netflix", "denominator", "password"));
        MockWebServerAssertions.assertThat(server.takeRequest()).hasHeaders(entry("Content-Length", Collections.singletonList("32"))).hasBody("[netflix, denominator, password]");
    }

    /**
     * The type of a parameter value may not be the desired type to encode as. Prefer the interface
     * type.
     */
    @Test
    public void bodyTypeCorrespondsWithParameterType() throws Exception {
        server.enqueue(new MockResponse().setBody("foo"));
        final AtomicReference<Type> encodedType = new AtomicReference<Type>();
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().encoder(new Encoder.Default() {
            @Override
            public void encode(Object object, Type bodyType, RequestTemplate template) {
                encodedType.set(bodyType);
            }
        }).target(("http://localhost:" + (server.getPort())));
        api.body(Arrays.asList("netflix", "denominator", "password"));
        server.takeRequest();
        MockWebServerAssertions.assertThat(encodedType.get()).isEqualTo(new TypeToken<List<String>>() {}.getType());
    }

    @Test
    public void postGZIPEncodedBodyParam() throws Exception {
        server.enqueue(new MockResponse().setBody("foo"));
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        api.gzipBody(Arrays.asList("netflix", "denominator", "password"));
        MockWebServerAssertions.assertThat(server.takeRequest()).hasNoHeaderNamed("Content-Length").hasGzippedBody("[netflix, denominator, password]".getBytes(Util.UTF_8));
    }

    @Test
    public void postDeflateEncodedBodyParam() throws Exception {
        server.enqueue(new MockResponse().setBody("foo"));
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        api.deflateBody(Arrays.asList("netflix", "denominator", "password"));
        MockWebServerAssertions.assertThat(server.takeRequest()).hasNoHeaderNamed("Content-Length").hasDeflatedBody("[netflix, denominator, password]".getBytes(Util.UTF_8));
    }

    @Test
    public void singleInterceptor() throws Exception {
        server.enqueue(new MockResponse().setBody("foo"));
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().requestInterceptor(new FeignTest.ForwardedForInterceptor()).target(("http://localhost:" + (server.getPort())));
        api.post();
        MockWebServerAssertions.assertThat(server.takeRequest()).hasHeaders(entry("X-Forwarded-For", Collections.singletonList("origin.host.com")));
    }

    @Test
    public void multipleInterceptor() throws Exception {
        server.enqueue(new MockResponse().setBody("foo"));
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().requestInterceptor(new FeignTest.ForwardedForInterceptor()).requestInterceptor(new FeignTest.UserAgentInterceptor()).target(("http://localhost:" + (server.getPort())));
        api.post();
        MockWebServerAssertions.assertThat(server.takeRequest()).hasHeaders(entry("X-Forwarded-For", Collections.singletonList("origin.host.com")), entry("User-Agent", Collections.singletonList("Feign")));
    }

    @Test
    public void customExpander() throws Exception {
        server.enqueue(new MockResponse());
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        api.expand(new Date(1234L));
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?date=1234");
    }

    @Test
    public void customExpanderListParam() throws Exception {
        server.enqueue(new MockResponse());
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        api.expandList(Arrays.asList(new Date(1234L), new Date(12345L)));
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?date=1234&date=12345");
    }

    @Test
    public void customExpanderNullParam() throws Exception {
        server.enqueue(new MockResponse());
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        api.expandList(Arrays.asList(new Date(1234L), null));
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?date=1234");
    }

    @Test
    public void headerMap() throws Exception {
        server.enqueue(new MockResponse());
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        Map<String, Object> headerMap = new LinkedHashMap<String, Object>();
        headerMap.put("Content-Type", "myContent");
        headerMap.put("Custom-Header", "fooValue");
        api.headerMap(headerMap);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasHeaders(entry("Content-Type", Arrays.asList("myContent")), entry("Custom-Header", Arrays.asList("fooValue")));
    }

    @Test
    public void headerMapWithHeaderAnnotations() throws Exception {
        server.enqueue(new MockResponse());
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        Map<String, Object> headerMap = new LinkedHashMap<String, Object>();
        headerMap.put("Custom-Header", "fooValue");
        api.headerMapWithHeaderAnnotations(headerMap);
        // header map should be additive for headers provided by annotations
        MockWebServerAssertions.assertThat(server.takeRequest()).hasHeaders(entry("Content-Encoding", Arrays.asList("deflate")), entry("Custom-Header", Arrays.asList("fooValue")));
        server.enqueue(new MockResponse());
        headerMap.put("Content-Encoding", "overrideFromMap");
        api.headerMapWithHeaderAnnotations(headerMap);
        /* @HeaderMap map values no longer override @Header parameters. This caused confusion as it is
        valid to have more than one value for a header.
         */
        MockWebServerAssertions.assertThat(server.takeRequest()).hasHeaders(entry("Content-Encoding", Arrays.asList("deflate", "overrideFromMap")), entry("Custom-Header", Arrays.asList("fooValue")));
    }

    @Test
    public void queryMap() throws Exception {
        server.enqueue(new MockResponse());
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        Map<String, Object> queryMap = new LinkedHashMap<String, Object>();
        queryMap.put("name", "alice");
        queryMap.put("fooKey", "fooValue");
        api.queryMap(queryMap);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?name=alice&fooKey=fooValue");
    }

    @Test
    public void queryMapIterableValuesExpanded() throws Exception {
        server.enqueue(new MockResponse());
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        Map<String, Object> queryMap = new LinkedHashMap<String, Object>();
        queryMap.put("name", Arrays.asList("Alice", "Bob"));
        queryMap.put("fooKey", "fooValue");
        queryMap.put("emptyListKey", new ArrayList<String>());
        queryMap.put("emptyStringKey", "");// empty values are ignored.

        api.queryMap(queryMap);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?name=Alice&name=Bob&fooKey=fooValue&emptyStringKey");
    }

    @Test
    public void queryMapWithQueryParams() throws Exception {
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        server.enqueue(new MockResponse());
        Map<String, Object> queryMap = new LinkedHashMap<String, Object>();
        queryMap.put("fooKey", "fooValue");
        api.queryMapWithQueryParams("alice", queryMap);
        // query map should be expanded after built-in parameters
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?name=alice&fooKey=fooValue");
        server.enqueue(new MockResponse());
        queryMap = new LinkedHashMap<String, Object>();
        queryMap.put("name", "bob");
        api.queryMapWithQueryParams("alice", queryMap);
        // queries are additive
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?name=alice&name=bob");
        server.enqueue(new MockResponse());
        queryMap = new LinkedHashMap<String, Object>();
        queryMap.put("name", null);
        api.queryMapWithQueryParams("alice", queryMap);
        // null value for a query map key removes query parameter
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?name=alice");
    }

    @Test
    public void queryMapValueStartingWithBrace() throws Exception {
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        server.enqueue(new MockResponse());
        Map<String, Object> queryMap = new LinkedHashMap<String, Object>();
        queryMap.put("name", "{alice");
        api.queryMap(queryMap);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?name=%7Balice");
        server.enqueue(new MockResponse());
        queryMap = new LinkedHashMap<String, Object>();
        queryMap.put("{name", "alice");
        api.queryMap(queryMap);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?%7Bname=alice");
        server.enqueue(new MockResponse());
        queryMap = new LinkedHashMap<String, Object>();
        queryMap.put("name", "%7Balice");
        api.queryMapEncoded(queryMap);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?name=%7Balice");
        server.enqueue(new MockResponse());
        queryMap = new LinkedHashMap<String, Object>();
        queryMap.put("%7Bname", "%7Balice");
        api.queryMapEncoded(queryMap);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?%7Bname=%7Balice");
    }

    @Test
    public void queryMapPojoWithFullParams() throws Exception {
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        CustomPojo customPojo = new CustomPojo("Name", 3);
        server.enqueue(new MockResponse());
        api.queryMapPojo(customPojo);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasQueryParams(Arrays.asList("name=Name", "number=3"));
    }

    @Test
    public void queryMapPojoWithPartialParams() throws Exception {
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        CustomPojo customPojo = new CustomPojo("Name", null);
        server.enqueue(new MockResponse());
        api.queryMapPojo(customPojo);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?name=Name");
    }

    @Test
    public void queryMapPojoWithEmptyParams() throws Exception {
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        CustomPojo customPojo = new CustomPojo(null, null);
        server.enqueue(new MockResponse());
        api.queryMapPojo(customPojo);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/");
    }

    @Test
    public void configKeyFormatsAsExpected() throws Exception {
        Assert.assertEquals("TestInterface#post()", Feign.configKey(FeignTest.TestInterface.class, FeignTest.TestInterface.class.getDeclaredMethod("post")));
        Assert.assertEquals("TestInterface#uriParam(String,URI,String)", Feign.configKey(FeignTest.TestInterface.class, FeignTest.TestInterface.class.getDeclaredMethod("uriParam", String.class, URI.class, String.class)));
    }

    @Test
    public void configKeyUsesChildType() throws Exception {
        Assert.assertEquals("List#iterator()", Feign.configKey(List.class, Iterable.class.getDeclaredMethod("iterator")));
    }

    @Test
    public void canOverrideErrorDecoder() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(400).setBody("foo"));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("bad zone name");
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().errorDecoder(new FeignTest.IllegalArgumentExceptionOn400()).target(("http://localhost:" + (server.getPort())));
        api.post();
    }

    @Test
    public void retriesLostConnectionBeforeRead() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_START));
        server.enqueue(new MockResponse().setBody("success!"));
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        api.post();
        Assert.assertEquals(2, server.getRequestCount());
    }

    @Test
    public void overrideTypeSpecificDecoder() throws Exception {
        server.enqueue(new MockResponse().setBody("success!"));
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().decoder(new Decoder() {
            @Override
            public Object decode(Response response, Type type) {
                return "fail";
            }
        }).target(("http://localhost:" + (server.getPort())));
        Assert.assertEquals(api.post(), "fail");
    }

    /**
     * when you must parse a 2xx status to determine if the operation succeeded or not.
     */
    @Test
    public void retryableExceptionInDecoder() throws Exception {
        server.enqueue(new MockResponse().setBody("retry!"));
        server.enqueue(new MockResponse().setBody("success!"));
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().decoder(new StringDecoder() {
            @Override
            public Object decode(Response response, Type type) throws IOException {
                String string = super.decode(response, type).toString();
                if ("retry!".equals(string)) {
                    throw new RetryableException(response.status(), string, HttpMethod.POST, null);
                }
                return string;
            }
        }).target(("http://localhost:" + (server.getPort())));
        Assert.assertEquals(api.post(), "success!");
        Assert.assertEquals(2, server.getRequestCount());
    }

    @Test
    public void doesntRetryAfterResponseIsSent() throws Exception {
        server.enqueue(new MockResponse().setBody("success!"));
        thrown.expect(FeignException.class);
        thrown.expectMessage("timeout reading POST http://");
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().decoder(new Decoder() {
            @Override
            public Object decode(Response response, Type type) throws IOException {
                throw new IOException("timeout");
            }
        }).target(("http://localhost:" + (server.getPort())));
        api.post();
    }

    @Test
    public void throwsFeignExceptionIncludingBody() {
        server.enqueue(new MockResponse().setBody("success!"));
        FeignTest.TestInterface api = Feign.builder().decoder(( response, type) -> {
            throw new IOException("timeout");
        }).target(FeignTest.TestInterface.class, ("http://localhost:" + (server.getPort())));
        try {
            api.body("Request body");
        } catch (FeignException e) {
            MockWebServerAssertions.assertThat(e.getMessage()).isEqualTo((("timeout reading POST http://localhost:" + (server.getPort())) + "/"));
            MockWebServerAssertions.assertThat(e.contentUTF8()).isEqualTo("Request body");
        }
    }

    @Test
    public void throwsFeignExceptionWithoutBody() {
        server.enqueue(new MockResponse().setBody("success!"));
        FeignTest.TestInterface api = Feign.builder().decoder(( response, type) -> {
            throw new IOException("timeout");
        }).target(FeignTest.TestInterface.class, ("http://localhost:" + (server.getPort())));
        try {
            api.noContent();
        } catch (FeignException e) {
            MockWebServerAssertions.assertThat(e.getMessage()).isEqualTo((("timeout reading POST http://localhost:" + (server.getPort())) + "/"));
            MockWebServerAssertions.assertThat(e.contentUTF8()).isEqualTo("");
        }
    }

    @Test
    public void ensureRetryerClonesItself() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(503).setBody("foo 1"));
        server.enqueue(new MockResponse().setResponseCode(200).setBody("foo 2"));
        server.enqueue(new MockResponse().setResponseCode(503).setBody("foo 3"));
        server.enqueue(new MockResponse().setResponseCode(200).setBody("foo 4"));
        FeignTest.MockRetryer retryer = new FeignTest.MockRetryer();
        FeignTest.TestInterface api = Feign.builder().retryer(retryer).errorDecoder(new ErrorDecoder() {
            @Override
            public Exception decode(String methodKey, Response response) {
                return new RetryableException(response.status(), "play it again sam!", HttpMethod.POST, null);
            }
        }).target(FeignTest.TestInterface.class, ("http://localhost:" + (server.getPort())));
        api.post();
        api.post();// if retryer instance was reused, this statement will throw an exception

        Assert.assertEquals(4, server.getRequestCount());
    }

    @Test
    public void throwsOriginalExceptionAfterFailedRetries() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(503).setBody("foo 1"));
        server.enqueue(new MockResponse().setResponseCode(503).setBody("foo 2"));
        final String message = "the innerest";
        thrown.expect(FeignTest.TestInterfaceException.class);
        thrown.expectMessage(message);
        FeignTest.TestInterface api = Feign.builder().exceptionPropagationPolicy(ExceptionPropagationPolicy.UNWRAP).retryer(new Retryer.Default(1, 1, 2)).errorDecoder(new ErrorDecoder() {
            @Override
            public Exception decode(String methodKey, Response response) {
                return new RetryableException(response.status(), "play it again sam!", HttpMethod.POST, new FeignTest.TestInterfaceException(message), null);
            }
        }).target(FeignTest.TestInterface.class, ("http://localhost:" + (server.getPort())));
        api.post();
    }

    @Test
    public void throwsRetryableExceptionIfNoUnderlyingCause() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(503).setBody("foo 1"));
        server.enqueue(new MockResponse().setResponseCode(503).setBody("foo 2"));
        String message = "play it again sam!";
        thrown.expect(RetryableException.class);
        thrown.expectMessage(message);
        FeignTest.TestInterface api = Feign.builder().exceptionPropagationPolicy(ExceptionPropagationPolicy.UNWRAP).retryer(new Retryer.Default(1, 1, 2)).errorDecoder(new ErrorDecoder() {
            @Override
            public Exception decode(String methodKey, Response response) {
                return new RetryableException(response.status(), message, HttpMethod.POST, null);
            }
        }).target(FeignTest.TestInterface.class, ("http://localhost:" + (server.getPort())));
        api.post();
    }

    @Test
    public void whenReturnTypeIsResponseNoErrorHandling() {
        Map<String, Collection<String>> headers = new LinkedHashMap<String, Collection<String>>();
        headers.put("Location", Arrays.asList("http://bar.com"));
        final Response response = Response.builder().status(302).reason("Found").headers(headers).request(Request.create(GET, "/", Collections.emptyMap(), null, UTF_8)).body(new byte[0]).build();
        // fake client as Client.Default follows redirects.
        FeignTest.TestInterface api = Feign.builder().client(( request, options) -> response).target(FeignTest.TestInterface.class, ("http://localhost:" + (server.getPort())));
        Assert.assertEquals(api.response().headers().get("Location"), Collections.singletonList("http://bar.com"));
    }

    private static class MockRetryer implements Retryer {
        boolean tripped;

        @Override
        public void continueOrPropagate(RetryableException e) {
            if (tripped) {
                throw new RuntimeException("retryer instance should never be reused");
            }
            tripped = true;
            return;
        }

        @Override
        public Retryer clone() {
            return new FeignTest.MockRetryer();
        }
    }

    @Test
    public void okIfDecodeRootCauseHasNoMessage() throws Exception {
        server.enqueue(new MockResponse().setBody("success!"));
        thrown.expect(DecodeException.class);
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().decoder(new Decoder() {
            @Override
            public Object decode(Response response, Type type) throws IOException {
                throw new RuntimeException();
            }
        }).target(("http://localhost:" + (server.getPort())));
        api.post();
    }

    @Test
    public void decodingExceptionGetWrappedInDecode404Mode() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404));
        thrown.expect(DecodeException.class);
        thrown.expectCause(CoreMatchers.isA(NoSuchElementException.class));
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().decode404().decoder(new Decoder() {
            @Override
            public Object decode(Response response, Type type) throws IOException {
                Assert.assertEquals(404, response.status());
                throw new NoSuchElementException();
            }
        }).target(("http://localhost:" + (server.getPort())));
        api.post();
    }

    @Test
    public void decodingDoesNotSwallow404ErrorsInDecode404Mode() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404));
        thrown.expect(IllegalArgumentException.class);
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().decode404().errorDecoder(new FeignTest.IllegalArgumentExceptionOn404()).target(("http://localhost:" + (server.getPort())));
        api.queryMap(Collections.<String, Object>emptyMap());
    }

    @Test
    public void okIfEncodeRootCauseHasNoMessage() throws Exception {
        server.enqueue(new MockResponse().setBody("success!"));
        thrown.expect(EncodeException.class);
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().encoder(new Encoder() {
            @Override
            public void encode(Object object, Type bodyType, RequestTemplate template) {
                throw new RuntimeException();
            }
        }).target(("http://localhost:" + (server.getPort())));
        api.body(Arrays.asList("foo"));
    }

    @Test
    public void equalsHashCodeAndToStringWork() {
        Target<FeignTest.TestInterface> t1 = new feign.Target.HardCodedTarget<FeignTest.TestInterface>(FeignTest.TestInterface.class, "http://localhost:8080");
        Target<FeignTest.TestInterface> t2 = new feign.Target.HardCodedTarget<FeignTest.TestInterface>(FeignTest.TestInterface.class, "http://localhost:8888");
        Target<FeignTest.OtherTestInterface> t3 = new feign.Target.HardCodedTarget<FeignTest.OtherTestInterface>(FeignTest.OtherTestInterface.class, "http://localhost:8080");
        FeignTest.TestInterface i1 = Feign.builder().target(t1);
        FeignTest.TestInterface i2 = Feign.builder().target(t1);
        FeignTest.TestInterface i3 = Feign.builder().target(t2);
        FeignTest.OtherTestInterface i4 = Feign.builder().target(t3);
        MockWebServerAssertions.assertThat(i1).isEqualTo(i2).isNotEqualTo(i3).isNotEqualTo(i4);
        MockWebServerAssertions.assertThat(i1.hashCode()).isEqualTo(i2.hashCode()).isNotEqualTo(i3.hashCode()).isNotEqualTo(i4.hashCode());
        MockWebServerAssertions.assertThat(i1.toString()).isEqualTo(i2.toString()).isNotEqualTo(i3.toString()).isNotEqualTo(i4.toString());
        MockWebServerAssertions.assertThat(t1).isNotEqualTo(i1);
        MockWebServerAssertions.assertThat(t1.hashCode()).isEqualTo(i1.hashCode());
        MockWebServerAssertions.assertThat(t1.toString()).isEqualTo(i1.toString());
    }

    @Test
    public void decodeLogicSupportsByteArray() throws Exception {
        byte[] expectedResponse = new byte[]{ 12, 34, 56 };
        server.enqueue(new MockResponse().setBody(new Buffer().write(expectedResponse)));
        FeignTest.OtherTestInterface api = Feign.builder().target(FeignTest.OtherTestInterface.class, ("http://localhost:" + (server.getPort())));
        MockWebServerAssertions.assertThat(api.binaryResponseBody()).containsExactly(expectedResponse);
    }

    @Test
    public void encodeLogicSupportsByteArray() throws Exception {
        byte[] expectedRequest = new byte[]{ 12, 34, 56 };
        server.enqueue(new MockResponse());
        FeignTest.OtherTestInterface api = Feign.builder().target(FeignTest.OtherTestInterface.class, ("http://localhost:" + (server.getPort())));
        api.binaryRequestBody(expectedRequest);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasBody(expectedRequest);
    }

    @Test
    public void encodedQueryParam() throws Exception {
        server.enqueue(new MockResponse());
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().target(("http://localhost:" + (server.getPort())));
        api.encodedQueryParam("5.2FSi+");
        MockWebServerAssertions.assertThat(server.takeRequest()).hasPath("/?trim=5.2FSi%2B");
    }

    @Test
    public void responseMapperIsAppliedBeforeDelegate() throws IOException {
        ResponseMappingDecoder decoder = new ResponseMappingDecoder(upperCaseResponseMapper(), new StringDecoder());
        String output = ((String) (decoder.decode(responseWithText("response"), String.class)));
        MockWebServerAssertions.assertThat(output).isEqualTo("RESPONSE");
    }

    @Test
    public void mapAndDecodeExecutesMapFunction() throws Exception {
        server.enqueue(new MockResponse().setBody("response!"));
        FeignTest.TestInterface api = new Feign.Builder().mapAndDecode(upperCaseResponseMapper(), new StringDecoder()).target(FeignTest.TestInterface.class, ("http://localhost:" + (server.getPort())));
        Assert.assertEquals(api.post(), "RESPONSE!");
    }

    @Test
    public void beanQueryMapEncoderWithPrivateGetterIgnored() throws Exception {
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().queryMapEndcoder(new BeanQueryMapEncoder()).target(("http://localhost:" + (server.getPort())));
        PropertyPojo.ChildPojoClass propertyPojo = new PropertyPojo.ChildPojoClass();
        propertyPojo.setPrivateGetterProperty("privateGetterProperty");
        propertyPojo.setName("Name");
        propertyPojo.setNumber(1);
        server.enqueue(new MockResponse());
        api.queryMapPropertyPojo(propertyPojo);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasQueryParams(Arrays.asList("name=Name", "number=1"));
    }

    @Test
    public void beanQueryMapEncoderWithNullValueIgnored() throws Exception {
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().queryMapEndcoder(new BeanQueryMapEncoder()).target(("http://localhost:" + (server.getPort())));
        PropertyPojo.ChildPojoClass propertyPojo = new PropertyPojo.ChildPojoClass();
        propertyPojo.setName(null);
        propertyPojo.setNumber(1);
        server.enqueue(new MockResponse());
        api.queryMapPropertyPojo(propertyPojo);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasQueryParams("number=1");
    }

    @Test
    public void beanQueryMapEncoderWithEmptyParams() throws Exception {
        FeignTest.TestInterface api = new FeignTest.TestInterfaceBuilder().queryMapEndcoder(new BeanQueryMapEncoder()).target(("http://localhost:" + (server.getPort())));
        PropertyPojo.ChildPojoClass propertyPojo = new PropertyPojo.ChildPojoClass();
        server.enqueue(new MockResponse());
        api.queryMapPropertyPojo(propertyPojo);
        MockWebServerAssertions.assertThat(server.takeRequest()).hasQueryParams("/");
    }

    interface TestInterface {
        @RequestLine("POST /")
        Response response();

        @RequestLine("POST /")
        String post() throws FeignTest.TestInterfaceException;

        @RequestLine("POST /")
        @Body("%7B\"customer_name\": \"{customer_name}\", \"user_name\": \"{user_name}\", \"password\": \"{password}\"%7D")
        void login(@Param("customer_name")
        String customer, @Param("user_name")
        String user, @Param("password")
        String password);

        @RequestLine("POST /")
        void body(List<String> contents);

        @RequestLine("POST /")
        String body(String content);

        @RequestLine("POST /")
        String noContent();

        @RequestLine("POST /")
        @Headers("Content-Encoding: gzip")
        void gzipBody(List<String> contents);

        @RequestLine("POST /")
        @Headers("Content-Encoding: deflate")
        void deflateBody(List<String> contents);

        @RequestLine("POST /")
        void form(@Param("customer_name")
        String customer, @Param("user_name")
        String user, @Param("password")
        String password);

        @RequestLine("GET /{1}/{2}")
        Response uriParam(@Param("1")
        String one, URI endpoint, @Param("2")
        String two);

        @RequestLine("GET /?1={1}&2={2}")
        Response queryParams(@Param("1")
        String one, @Param("2")
        Iterable<String> twos);

        @RequestLine("POST /?date={date}")
        void expand(@Param(value = "date", expander = FeignTest.TestInterface.DateToMillis.class)
        Date date);

        @RequestLine("GET /?date={date}")
        void expandList(@Param(value = "date", expander = FeignTest.TestInterface.DateToMillis.class)
        List<Date> dates);

        @RequestLine("GET /?date={date}")
        void expandArray(@Param(value = "date", expander = FeignTest.TestInterface.DateToMillis.class)
        Date[] dates);

        @RequestLine("GET /")
        void headerMap(@HeaderMap
        Map<String, Object> headerMap);

        @RequestLine("GET /")
        @Headers("Content-Encoding: deflate")
        void headerMapWithHeaderAnnotations(@HeaderMap
        Map<String, Object> headerMap);

        @RequestLine("GET /")
        void queryMap(@QueryMap
        Map<String, Object> queryMap);

        @RequestLine("GET /")
        void queryMapEncoded(@QueryMap(encoded = true)
        Map<String, Object> queryMap);

        @RequestLine("GET /?name={name}")
        void queryMapWithQueryParams(@Param("name")
        String name, @QueryMap
        Map<String, Object> queryMap);

        @RequestLine("GET /?trim={trim}")
        void encodedQueryParam(@Param(value = "trim", encoded = true)
        String trim);

        @RequestLine("GET /")
        void queryMapPojo(@QueryMap
        CustomPojo object);

        @RequestLine("GET /")
        void queryMapPropertyPojo(@QueryMap
        PropertyPojo object);

        class DateToMillis implements Param.Expander {
            @Override
            public String expand(Object value) {
                return String.valueOf(((Date) (value)).getTime());
            }
        }
    }

    class TestInterfaceException extends Exception {
        TestInterfaceException(String message) {
            super(message);
        }
    }

    interface OtherTestInterface {
        @RequestLine("POST /")
        String post();

        @RequestLine("POST /")
        byte[] binaryResponseBody();

        @RequestLine("POST /")
        void binaryRequestBody(byte[] contents);
    }

    static class ForwardedForInterceptor implements RequestInterceptor {
        @Override
        public void apply(RequestTemplate template) {
            template.header("X-Forwarded-For", "origin.host.com");
        }
    }

    static class UserAgentInterceptor implements RequestInterceptor {
        @Override
        public void apply(RequestTemplate template) {
            template.header("User-Agent", "Feign");
        }
    }

    static class IllegalArgumentExceptionOn400 extends ErrorDecoder.Default {
        @Override
        public Exception decode(String methodKey, Response response) {
            if ((response.status()) == 400) {
                return new IllegalArgumentException("bad zone name");
            }
            return super.decode(methodKey, response);
        }
    }

    static class IllegalArgumentExceptionOn404 extends ErrorDecoder.Default {
        @Override
        public Exception decode(String methodKey, Response response) {
            if ((response.status()) == 404) {
                return new IllegalArgumentException("bad zone name");
            }
            return super.decode(methodKey, response);
        }
    }

    static final class TestInterfaceBuilder {
        private final Builder delegate = new Feign.Builder().decoder(new Decoder.Default()).encoder(new Encoder() {
            @Override
            public void encode(Object object, Type bodyType, RequestTemplate template) {
                if (object instanceof Map) {
                    template.body(new Gson().toJson(object));
                } else {
                    template.body(object.toString());
                }
            }
        });

        FeignTest.TestInterfaceBuilder requestInterceptor(RequestInterceptor requestInterceptor) {
            delegate.requestInterceptor(requestInterceptor);
            return this;
        }

        FeignTest.TestInterfaceBuilder encoder(Encoder encoder) {
            delegate.encoder(encoder);
            return this;
        }

        FeignTest.TestInterfaceBuilder decoder(Decoder decoder) {
            delegate.decoder(decoder);
            return this;
        }

        FeignTest.TestInterfaceBuilder errorDecoder(ErrorDecoder errorDecoder) {
            delegate.errorDecoder(errorDecoder);
            return this;
        }

        FeignTest.TestInterfaceBuilder decode404() {
            delegate.decode404();
            return this;
        }

        FeignTest.TestInterfaceBuilder queryMapEndcoder(QueryMapEncoder queryMapEncoder) {
            delegate.queryMapEncoder(queryMapEncoder);
            return this;
        }

        FeignTest.TestInterface target(String url) {
            return delegate.target(FeignTest.TestInterface.class, url);
        }
    }
}

