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
package feign.reactive;


import Level.FULL;
import feign.Client;
import feign.Logger;
import feign.Param;
import feign.QueryMap;
import feign.QueryMapEncoder;
import feign.Request;
import feign.Request.Options;
import feign.RequestInterceptor;
import feign.RequestLine;
import feign.RequestTemplate;
import feign.Response;
import feign.ResponseMapper;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import io.reactivex.Flowable;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class ReactiveFeignIntegrationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public final MockWebServer webServer = new MockWebServer();

    @Test
    public void testDefaultMethodsNotProxied() {
        ReactiveFeignIntegrationTest.TestReactorService service = ReactorFeign.builder().target(ReactiveFeignIntegrationTest.TestReactorService.class, this.getServerUrl());
        assertThat(service).isEqualTo(service);
        assertThat(service.toString()).isNotNull();
        assertThat(service.hashCode()).isNotZero();
    }

    @Test
    public void testReactorTargetFull() throws Exception {
        this.webServer.enqueue(new MockResponse().setBody("1.0"));
        this.webServer.enqueue(new MockResponse().setBody("{ \"username\": \"test\" }"));
        ReactiveFeignIntegrationTest.TestReactorService service = ReactorFeign.builder().encoder(new JacksonEncoder()).decoder(new JacksonDecoder()).logger(new ReactiveFeignIntegrationTest.ConsoleLogger()).decode404().options(new Options()).logLevel(FULL).target(ReactiveFeignIntegrationTest.TestReactorService.class, this.getServerUrl());
        assertThat(service).isNotNull();
        String version = service.version().block();
        assertThat(version).isNotNull();
        assertThat(webServer.takeRequest().getPath()).isEqualToIgnoringCase("/version");
        /* test encoding and decoding */
        ReactiveFeignIntegrationTest.User user = service.user("test").blockFirst();
        assertThat(user).isNotNull().hasFieldOrPropertyWithValue("username", "test");
        assertThat(webServer.takeRequest().getPath()).isEqualToIgnoringCase("/users/test");
    }

    @Test
    public void testRxJavaTarget() throws Exception {
        this.webServer.enqueue(new MockResponse().setBody("1.0"));
        this.webServer.enqueue(new MockResponse().setBody("{ \"username\": \"test\" }"));
        ReactiveFeignIntegrationTest.TestReactiveXService service = RxJavaFeign.builder().encoder(new JacksonEncoder()).decoder(new JacksonDecoder()).logger(new ReactiveFeignIntegrationTest.ConsoleLogger()).logLevel(FULL).target(ReactiveFeignIntegrationTest.TestReactiveXService.class, this.getServerUrl());
        assertThat(service).isNotNull();
        String version = service.version().firstElement().blockingGet();
        assertThat(version).isNotNull();
        assertThat(webServer.takeRequest().getPath()).isEqualToIgnoringCase("/version");
        /* test encoding and decoding */
        ReactiveFeignIntegrationTest.User user = service.user("test").firstElement().blockingGet();
        assertThat(user).isNotNull().hasFieldOrPropertyWithValue("username", "test");
        assertThat(webServer.takeRequest().getPath()).isEqualToIgnoringCase("/users/test");
    }

    @Test
    public void invocationFactoryIsNotSupported() {
        this.thrown.expect(UnsupportedOperationException.class);
        ReactorFeign.builder().invocationHandlerFactory(( target, dispatch) -> null).target(ReactiveFeignIntegrationTest.TestReactiveXService.class, "http://localhost");
    }

    @Test
    public void doNotCloseUnsupported() {
        this.thrown.expect(UnsupportedOperationException.class);
        ReactorFeign.builder().doNotCloseAfterDecode().target(ReactiveFeignIntegrationTest.TestReactiveXService.class, "http://localhost");
    }

    @Test
    public void testRequestInterceptor() {
        this.webServer.enqueue(new MockResponse().setBody("1.0"));
        RequestInterceptor mockInterceptor = Mockito.mock(RequestInterceptor.class);
        ReactiveFeignIntegrationTest.TestReactorService service = ReactorFeign.builder().requestInterceptor(mockInterceptor).target(ReactiveFeignIntegrationTest.TestReactorService.class, this.getServerUrl());
        service.version().block();
        Mockito.verify(mockInterceptor, Mockito.times(1)).apply(ArgumentMatchers.any(RequestTemplate.class));
    }

    @Test
    public void testRequestInterceptors() {
        this.webServer.enqueue(new MockResponse().setBody("1.0"));
        RequestInterceptor mockInterceptor = Mockito.mock(RequestInterceptor.class);
        ReactiveFeignIntegrationTest.TestReactorService service = ReactorFeign.builder().requestInterceptors(Arrays.asList(mockInterceptor, mockInterceptor)).target(ReactiveFeignIntegrationTest.TestReactorService.class, this.getServerUrl());
        service.version().block();
        Mockito.verify(mockInterceptor, Mockito.times(2)).apply(ArgumentMatchers.any(RequestTemplate.class));
    }

    @Test
    public void testResponseMappers() throws Exception {
        this.webServer.enqueue(new MockResponse().setBody("1.0"));
        ResponseMapper responseMapper = Mockito.mock(ResponseMapper.class);
        Decoder decoder = Mockito.mock(Decoder.class);
        BDDMockito.given(responseMapper.map(ArgumentMatchers.any(Response.class), ArgumentMatchers.any(Type.class))).willAnswer(AdditionalAnswers.returnsFirstArg());
        BDDMockito.given(decoder.decode(ArgumentMatchers.any(Response.class), ArgumentMatchers.any(Type.class))).willReturn("1.0");
        ReactiveFeignIntegrationTest.TestReactorService service = ReactorFeign.builder().mapAndDecode(responseMapper, decoder).target(ReactiveFeignIntegrationTest.TestReactorService.class, this.getServerUrl());
        service.version().block();
        Mockito.verify(responseMapper, Mockito.times(1)).map(ArgumentMatchers.any(Response.class), ArgumentMatchers.any(Type.class));
        Mockito.verify(decoder, Mockito.times(1)).decode(ArgumentMatchers.any(Response.class), ArgumentMatchers.any(Type.class));
    }

    @Test
    public void testQueryMapEncoders() {
        this.webServer.enqueue(new MockResponse().setBody("No Results Found"));
        QueryMapEncoder encoder = Mockito.mock(QueryMapEncoder.class);
        BDDMockito.given(encoder.encode(ArgumentMatchers.any(Object.class))).willReturn(Collections.emptyMap());
        ReactiveFeignIntegrationTest.TestReactiveXService service = RxJavaFeign.builder().queryMapEncoder(encoder).target(ReactiveFeignIntegrationTest.TestReactiveXService.class, this.getServerUrl());
        String results = service.search(new ReactiveFeignIntegrationTest.SearchQuery()).blockingSingle();
        assertThat(results).isNotEmpty();
        Mockito.verify(encoder, Mockito.times(1)).encode(ArgumentMatchers.any(Object.class));
    }

    @SuppressWarnings({ "ResultOfMethodCallIgnored", "ThrowableNotThrown" })
    @Test
    public void testErrorDecoder() {
        this.thrown.expect(RuntimeException.class);
        this.webServer.enqueue(new MockResponse().setBody("Bad Request").setResponseCode(400));
        ErrorDecoder errorDecoder = Mockito.mock(ErrorDecoder.class);
        BDDMockito.given(errorDecoder.decode(ArgumentMatchers.anyString(), ArgumentMatchers.any(Response.class))).willReturn(new IllegalStateException("bad request"));
        ReactiveFeignIntegrationTest.TestReactiveXService service = RxJavaFeign.builder().errorDecoder(errorDecoder).target(ReactiveFeignIntegrationTest.TestReactiveXService.class, this.getServerUrl());
        service.search(new ReactiveFeignIntegrationTest.SearchQuery()).blockingSingle();
        Mockito.verify(errorDecoder, Mockito.times(1)).decode(ArgumentMatchers.anyString(), ArgumentMatchers.any(Response.class));
    }

    @Test
    public void testRetryer() {
        this.webServer.enqueue(new MockResponse().setBody("Not Available").setResponseCode((-1)));
        this.webServer.enqueue(new MockResponse().setBody("1.0"));
        Retryer retryer = new Retryer.Default();
        Retryer spy = Mockito.spy(retryer);
        Mockito.when(spy.clone()).thenReturn(spy);
        ReactiveFeignIntegrationTest.TestReactorService service = ReactorFeign.builder().retryer(spy).target(ReactiveFeignIntegrationTest.TestReactorService.class, this.getServerUrl());
        service.version().log().block();
        Mockito.verify(spy, Mockito.times(1)).continueOrPropagate(ArgumentMatchers.any(RetryableException.class));
    }

    @Test
    public void testClient() throws Exception {
        Client client = Mockito.mock(Client.class);
        BDDMockito.given(client.execute(ArgumentMatchers.any(Request.class), ArgumentMatchers.any(Options.class))).willAnswer(((Answer<Response>) (( invocation) -> Response.builder().status(200).headers(Collections.emptyMap()).body("1.0", Charset.defaultCharset()).request(((Request) (invocation.getArguments()[0]))).build())));
        ReactiveFeignIntegrationTest.TestReactorService service = ReactorFeign.builder().client(client).target(ReactiveFeignIntegrationTest.TestReactorService.class, this.getServerUrl());
        service.version().block();
        Mockito.verify(client, Mockito.times(1)).execute(ArgumentMatchers.any(Request.class), ArgumentMatchers.any(Options.class));
    }

    @Test
    public void testDifferentContract() throws Exception {
        this.webServer.enqueue(new MockResponse().setBody("1.0"));
        ReactiveFeignIntegrationTest.TestJaxRSReactorService service = ReactorFeign.builder().contract(new JAXRSContract()).target(ReactiveFeignIntegrationTest.TestJaxRSReactorService.class, this.getServerUrl());
        String version = service.version().block();
        assertThat(version).isNotNull();
        assertThat(webServer.takeRequest().getPath()).isEqualToIgnoringCase("/version");
    }

    interface TestReactorService {
        @RequestLine("GET /version")
        Mono<String> version();

        @RequestLine("GET /users/{username}")
        Flux<ReactiveFeignIntegrationTest.User> user(@Param("username")
        String username);
    }

    interface TestReactiveXService {
        @RequestLine("GET /version")
        Flowable<String> version();

        @RequestLine("GET /users/{username}")
        Flowable<ReactiveFeignIntegrationTest.User> user(@Param("username")
        String username);

        @RequestLine("GET /users/search")
        Flowable<String> search(@QueryMap
        ReactiveFeignIntegrationTest.SearchQuery query);
    }

    interface TestJaxRSReactorService {
        @Path("/version")
        @GET
        Mono<String> version();
    }

    @SuppressWarnings("unused")
    static class User {
        private String username;

        public User() {
            super();
        }

        public String getUsername() {
            return username;
        }
    }

    @SuppressWarnings("unused")
    static class SearchQuery {
        SearchQuery() {
            super();
        }

        public String query() {
            return "query";
        }
    }

    public static class ConsoleLogger extends Logger {
        @Override
        protected void log(String configKey, String format, Object... args) {
            System.out.println(String.format(((methodTag(configKey)) + format), args));
        }
    }
}

