/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.itest.java;


import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.itest.java.support.WithJetty;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.Test;


public class HttpClientConfigITest extends WithJetty {
    @Test
    public void doesntFollowRedirectsIfSpecifiedInTheHttpClientConfig() throws Exception {
        final List<Header> httpClientHeaders = new ArrayList<Header>();
        httpClientHeaders.add(new BasicHeader("header1", "value1"));
        httpClientHeaders.add(new BasicHeader("header2", "value2"));
        RestAssured.config = RestAssuredConfig.newConfig().httpClient(HttpClientConfig.httpClientConfig().setParam(ClientPNames.DEFAULT_HEADERS, httpClientHeaders));
        try {
            RestAssured.given().param("url", "/hello").expect().statusCode(200).header("header1", "value1").header("header2", "value2").when().get("/multiHeaderReflect");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void doesntThrowClientProtocolExceptionIfMaxNumberOfRedirectAreExceededInHttpClientConfigBecauseRedirectConfigHasPrecedence() throws Exception {
        RestAssured.config = RestAssuredConfig.newConfig().httpClient(HttpClientConfig.httpClientConfig().setParam(ClientPNames.HANDLE_REDIRECTS, true).and().setParam(ClientPNames.MAX_REDIRECTS, 0));
        try {
            RestAssured.given().param("url", "/hello").expect().body("hello", equalTo("Hello Scalatra")).when().get("/redirect");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void httpClientIsConfigurableFromANonStaticHttpClientConfig() {
        // Given
        final MutableObject<HttpClient> client = new MutableObject<>();
        // When
        RestAssured.given().config(RestAssuredConfig.newConfig().httpClient(HttpClientConfig.httpClientConfig().httpClientFactory(SystemDefaultHttpClient::new))).filter(new Filter() {
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                client.setValue(requestSpec.getHttpClient());
                return new io.restassured.builder.ResponseBuilder().setStatusCode(200).setContentType("application/json").setBody("{ \"message\" : \"hello\"}").build();
            }
        }).expect().body("message", equalTo("hello")).when().get("/something");
        // Then
        Assert.assertThat(client.getValue(), instanceOf(SystemDefaultHttpClient.class));
    }

    @Test
    public void httpClientIsConfigurableFromAStaticHttpClientConfigWithOtherConfigurations() {
        // Given
        final MutableObject<HttpClient> client = new MutableObject<>();
        RestAssured.config = RestAssuredConfig.newConfig().httpClient(HttpClientConfig.httpClientConfig().setParam(ClientPNames.HANDLE_REDIRECTS, true).and().setParam(ClientPNames.MAX_REDIRECTS, 0).and().httpClientFactory(SystemDefaultHttpClient::new));
        // When
        try {
            RestAssured.given().param("url", "/hello").filter(new Filter() {
                public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                    client.setValue(requestSpec.getHttpClient());
                    return ctx.next(requestSpec, responseSpec);
                }
            }).expect().body("hello", equalTo("Hello Scalatra")).when().get("/redirect");
        } finally {
            RestAssured.reset();
        }
        // Then
        Assert.assertThat(client.getValue(), instanceOf(SystemDefaultHttpClient.class));
    }

    @Test
    public void http_client_config_allows_specifying_that_the_http_client_instance_is_reused_in_multiple_requests() {
        final MutableObject<HttpClient> client1 = new MutableObject<HttpClient>();
        final MutableObject<HttpClient> client2 = new MutableObject<HttpClient>();
        RestAssured.config = RestAssuredConfig.newConfig().httpClient(HttpClientConfig.httpClientConfig().reuseHttpClientInstance());
        // When
        try {
            RestAssured.given().param("url", "/hello").filter(new Filter() {
                public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                    client1.setValue(requestSpec.getHttpClient());
                    return ctx.next(requestSpec, responseSpec);
                }
            }).expect().body("hello", equalTo("Hello Scalatra")).when().get("/redirect");
            RestAssured.given().header("name", "value").filter(( requestSpec, responseSpec, ctx) -> {
                client2.setValue(requestSpec.getHttpClient());
                return ctx.next(requestSpec, responseSpec);
            }).when().post("/reflect");
        } finally {
            RestAssured.reset();
        }
        Assert.assertThat(client1.getValue(), sameInstance(client2.getValue()));
    }

    @Test
    public void local_http_client_config_doesnt_reuse_static_http_client_instance_when_local_config_specifies_reuse() {
        final MutableObject<HttpClient> client1 = new MutableObject<HttpClient>();
        final MutableObject<HttpClient> client2 = new MutableObject<HttpClient>();
        RestAssured.config = RestAssuredConfig.newConfig().httpClient(HttpClientConfig.httpClientConfig().reuseHttpClientInstance());
        // When
        try {
            RestAssured.given().param("url", "/hello").filter(new Filter() {
                public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                    client1.setValue(requestSpec.getHttpClient());
                    return ctx.next(requestSpec, responseSpec);
                }
            }).expect().body("hello", equalTo("Hello Scalatra")).when().get("/redirect");
            RestAssured.given().config(RestAssuredConfig.newConfig().httpClient(HttpClientConfig.httpClientConfig().reuseHttpClientInstance())).header("name", "value").filter(new Filter() {
                public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                    client2.setValue(requestSpec.getHttpClient());
                    return ctx.next(requestSpec, responseSpec);
                }
            }).when().post("/reflect");
        } finally {
            RestAssured.reset();
        }
        Assert.assertThat(client1.getValue(), not(sameInstance(client2.getValue())));
    }

    @Test
    public void local_http_client_config_reuse_reuse_static_http_client_instance_when_local_config_changes_other_configs_than_http_client_config() {
        final MutableObject<HttpClient> client1 = new MutableObject<HttpClient>();
        final MutableObject<HttpClient> client2 = new MutableObject<HttpClient>();
        RestAssured.config = RestAssuredConfig.newConfig().httpClient(HttpClientConfig.httpClientConfig().reuseHttpClientInstance());
        // When
        try {
            RestAssured.given().param("url", "/hello").filter(new Filter() {
                public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                    client1.setValue(requestSpec.getHttpClient());
                    return ctx.next(requestSpec, responseSpec);
                }
            }).expect().body("hello", equalTo("Hello Scalatra")).when().get("/redirect");
            // Here we only change the decoder config
            RestAssured.given().config(RestAssured.config.decoderConfig(io.restassured.config.DecoderConfig.decoderConfig().with().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE))).filter(new Filter() {
                public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                    client2.setValue(requestSpec.getHttpClient());
                    return ctx.next(requestSpec, responseSpec);
                }
            }).expect().body("Accept-Encoding", contains("deflate")).when().get("/headersWithValues");
        } finally {
            RestAssured.reset();
        }
        Assert.assertThat(client1.getValue(), sameInstance(client2.getValue()));
    }
}

