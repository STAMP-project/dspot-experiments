/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.netflix.zuul.filters.route.restclient;


import ClientException.ErrorType;
import HttpMethod.GET;
import HttpMethod.POST;
import HttpStatus.FORBIDDEN;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import HttpStatus.SERVICE_UNAVAILABLE;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import com.netflix.niws.client.http.RestClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.filter.ApplicationContextHeaderFilter;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.cloud.netflix.zuul.filters.route.RestClientRibbonCommand;
import org.springframework.cloud.netflix.zuul.filters.route.RestClientRibbonCommandFactory;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.cloud.netflix.zuul.filters.route.support.NoEncodingFormHttpMessageConverter;
import org.springframework.cloud.netflix.zuul.filters.route.support.ZuulProxyTestBase;
import org.springframework.cloud.netflix.zuul.test.NoSecurityConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RestClientRibbonCommandIntegrationTests.TestConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT, value = { "zuul.routes.other: /test/**=http://localhost:7777/local", "zuul.routes.another: /another/twolevel/**", "zuul.routes.simple: /simple/**", "zuul.routes.badhost: /badhost/**", "zuul.ignored-headers: X-Header", "zuul.routes.rnd: /rnd/**", "rnd.ribbon.listOfServers: ${random.value}", "zuul.remove-semicolon-content: false", "ribbon.restclient.enabled=true" })
@DirtiesContext
public class RestClientRibbonCommandIntegrationTests extends ZuulProxyTestBase {
    @Autowired
    DiscoveryClientRouteLocator routeLocator;

    @Test
    public void simpleHostRouteWithTrailingSlash() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/"));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/trailing-slash"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("/trailing-slash");
        assertThat(this.myErrorController.wasControllerUsed()).isFalse();
    }

    @Test
    public void simpleHostRouteWithNonExistentUrl() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/"));
        this.endpoint.reset();
        String uri = "/self/nonExistentUrl";
        this.myErrorController.setUriToMatch(uri);
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + uri), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(this.myErrorController.wasControllerUsed()).isFalse();
    }

    @Test
    public void simpleHostRouteIgnoredHeader() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/"));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/add-header"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getHeaders().get("X-Header")).isNull();
    }

    @Test
    public void simpleHostRouteDefaultIgnoredHeader() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/"));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/add-header"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        List<String> headers = result.getHeaders().get("X-Application-Context");
        assertThat(headers).as("header was null").isNotNull();
        assertThat(headers.toString()).isEqualTo("[application-1]");
    }

    @Test
    public void simpleHostRouteWithQuery() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/"));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/query?foo=bar"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("/query?foo=bar");
    }

    @Test
    public void simpleHostRouteWithMatrix() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/"));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/matrix/my;q=2;p=1/more;x=2"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("my=1-2;more=2");
    }

    @Test
    public void simpleHostRouteWithEncodedQuery() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/"));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/query?foo={foo}"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class, "weird#chars");
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("/query?foo=weird#chars");
    }

    @Test
    public void simpleHostRouteWithColonParamNames() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/"));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/colonquery?foo:bar={foobar0}&foobar={foobar1}"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class, "baz", "bam");
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("/colonquery?foo:bar=baz&foobar=bam");
    }

    @Test
    public void simpleHostRouteWithContentType() {
        this.routes.addRoute("/self/**", (("http://localhost:" + (this.port)) + "/"));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/content-type"), POST, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("<NONE>");
    }

    @Test
    public void ribbonCommandForbidden() {
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/simple/throwexception/403"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    public void ribbonCommandServiceUnavailable() {
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/simple/throwexception/503"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(SERVICE_UNAVAILABLE);
    }

    @Test
    public void ribbonCommandBadHost() {
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/badhost/1"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        // JSON response
        assertThat(result.getBody()).contains("\"status\":500");
    }

    @Test
    public void ribbonCommandRandomHostFromConfig() {
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/rnd/1"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        // JSON response
        assertThat(result.getBody()).contains("\"status\":500");
    }

    @Test
    public void ribbonCommandFactoryOverridden() {
        assertThat(((this.ribbonCommandFactory) instanceof RestClientRibbonCommandIntegrationTests.TestConfig.MyRibbonCommandFactory)).as("ribbonCommandFactory not a MyRibbonCommandFactory").isTrue();
    }

    @Override
    @SuppressWarnings("deprecation")
    @Test
    public void javascriptEncodedFormParams() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        ArrayList<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.addAll(Arrays.asList(new StringHttpMessageConverter(), new NoEncodingFormHttpMessageConverter()));
        testRestTemplate.getRestTemplate().setMessageConverters(converters);
        MultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap();
        map.add("foo", "(bar)");
        ResponseEntity<String> result = testRestTemplate.postForEntity((("http://localhost:" + (this.port)) + "/simple/local"), map, String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted [(bar)] and Content-Length was: -1!");
    }

    @Test
    public void routeLocatorOverridden() {
        assertThat(((this.routeLocator) instanceof RestClientRibbonCommandIntegrationTests.TestConfig.MyRouteLocator)).as("routeLocator not a MyRouteLocator").isTrue();
    }

    // Don't use @SpringBootApplication because we don't want to component scan
    @Configuration
    @EnableAutoConfiguration
    @RestController
    @EnableZuulProxy
    @RibbonClients({ @RibbonClient(name = "badhost", configuration = RestClientRibbonCommandIntegrationTests.TestConfig.BadHostRibbonClientConfiguration.class), @RibbonClient(name = "simple", configuration = ZuulProxyTestBase.SimpleRibbonClientConfiguration.class), @RibbonClient(name = "another", configuration = ZuulProxyTestBase.AnotherRibbonClientConfiguration.class) })
    @Import(NoSecurityConfiguration.class)
    static class TestConfig extends ZuulProxyTestBase.AbstractZuulProxyApplication {
        @Autowired(required = false)
        private Set<FallbackProvider> fallbackProviders = Collections.emptySet();

        @RequestMapping("/trailing-slash")
        public String trailingSlash(HttpServletRequest request) {
            return request.getRequestURI();
        }

        @RequestMapping("/content-type")
        public String contentType(HttpServletRequest request) {
            String header = request.getHeader("Content-Type");
            return header == null ? "<NONE>" : header;
        }

        @RequestMapping("/add-header")
        public ResponseEntity<String> addHeader(HttpServletRequest request) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Header", "FOO");
            ResponseEntity<String> result = new ResponseEntity(request.getRequestURI(), headers, HttpStatus.OK);
            return result;
        }

        @RequestMapping("/query")
        public String query(HttpServletRequest request, @RequestParam
        String foo) {
            return ((request.getRequestURI()) + "?foo=") + foo;
        }

        @RequestMapping("/colonquery")
        public String colonQuery(HttpServletRequest request, @RequestParam(name = "foo:bar")
        String foobar0, @RequestParam(name = "foobar")
        String foobar1) {
            return ((((request.getRequestURI()) + "?foo:bar=") + foobar0) + "&foobar=") + foobar1;
        }

        @RequestMapping("/matrix/{name}/{another}")
        public String matrix(@PathVariable("name")
        String name, @MatrixVariable(value = "p", pathVar = "name")
        int p, @MatrixVariable(value = "q", pathVar = "name")
        int q, @PathVariable("another")
        String another, @MatrixVariable(value = "x", pathVar = "another")
        int x) {
            return (((((((name + "=") + p) + "-") + q) + ";") + another) + "=") + x;
        }

        @Bean
        public RibbonCommandFactory<?> ribbonCommandFactory(SpringClientFactory clientFactory) {
            return new RestClientRibbonCommandIntegrationTests.TestConfig.MyRibbonCommandFactory(clientFactory, fallbackProviders);
        }

        @Bean
        public DiscoveryClientRouteLocator discoveryRouteLocator(DiscoveryClient discoveryClient, ZuulProperties zuulProperties) {
            return new RestClientRibbonCommandIntegrationTests.TestConfig.MyRouteLocator("/", discoveryClient, zuulProperties);
        }

        @Bean
        public ZuulProxyTestBase.MyErrorController myErrorController(ErrorAttributes errorAttributes) {
            return new ZuulProxyTestBase.MyErrorController(errorAttributes);
        }

        @Bean
        public ApplicationContextHeaderFilter applicationContextIdFilter(ApplicationContext context) {
            return new ApplicationContextHeaderFilter(context);
        }

        public static void main(String[] args) {
            SpringApplication.run(RestClientRibbonCommandIntegrationTests.TestConfig.class, args);
        }

        public static class MyRibbonCommandFactory extends RestClientRibbonCommandFactory {
            private SpringClientFactory clientFactory;

            MyRibbonCommandFactory(SpringClientFactory clientFactory, Set<FallbackProvider> fallbackProviders) {
                super(clientFactory, new ZuulProperties(), fallbackProviders);
                this.clientFactory = clientFactory;
            }

            @Override
            @SuppressWarnings("deprecation")
            public RestClientRibbonCommand create(RibbonCommandContext context) {
                String uri = context.getUri();
                if (uri.startsWith("/throwexception/")) {
                    String code = uri.replace("/throwexception/", "");
                    RestClient restClient = clientFactory.getClient(context.getServiceId(), RestClient.class);
                    return new RestClientRibbonCommandIntegrationTests.TestConfig.MyCommand(Integer.parseInt(code), context.getServiceId(), restClient, context);
                }
                return super.create(context);
            }
        }

        static class MyCommand extends RestClientRibbonCommand {
            private int errorCode;

            MyCommand(int errorCode, String commandKey, RestClient restClient, RibbonCommandContext context) {
                super(commandKey, restClient, context, new ZuulProperties());
                this.errorCode = errorCode;
            }

            @Override
            protected ClientHttpResponse run() throws Exception {
                if ((this.errorCode) == 503) {
                    throw new com.netflix.client.ClientException(ErrorType.SERVER_THROTTLED);
                }
                return new org.springframework.mock.http.client.MockClientHttpResponse(new byte[0], HttpStatus.valueOf(this.errorCode));
            }
        }

        // Load balancer with fixed server list for "simple" pointing to bad host
        @Configuration
        static class BadHostRibbonClientConfiguration {
            @Bean
            public ServerList<Server> ribbonServerList() {
                return new org.springframework.cloud.netflix.ribbon.StaticServerList(new Server(UUID.randomUUID().toString(), 4322));
            }
        }

        // This is needed to allow semicolon separators used in matrix variables
        @Configuration
        static class CustomHttpFirewallConfig implements WebSecurityConfigurer<WebSecurity> {
            @Override
            public void init(WebSecurity webSecurity) throws Exception {
            }

            @Override
            public void configure(WebSecurity builder) throws Exception {
                StrictHttpFirewall firewall = new StrictHttpFirewall();
                firewall.setAllowSemicolon(true);
                builder.httpFirewall(firewall);
            }
        }

        static class MyRouteLocator extends DiscoveryClientRouteLocator {
            MyRouteLocator(String servletPath, DiscoveryClient discovery, ZuulProperties properties) {
                super(servletPath, discovery, properties);
            }
        }
    }
}

