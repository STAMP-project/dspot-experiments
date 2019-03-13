/**
 * Copyright 2016-2019 the original author or authors.
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
package org.springframework.cloud.gateway.webflux;


import HttpStatus.NOT_FOUND;
import MediaType.APPLICATION_JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static java.util.Arrays.asList;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = ProductionConfigurationTests.TestApplication.class)
@DirtiesContext
public class ProductionConfigurationTests {
    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ProductionConfigurationTests.TestApplication application;

    @LocalServerPort
    private int port;

    @Test
    public void get() throws Exception {
        assertThat(rest.getForObject("/proxy/0", ProductionConfigurationTests.TestApplication.Foo.class).getName()).isEqualTo("bye");
    }

    @Test
    public void path() throws Exception {
        assertThat(rest.getForObject("/proxy/path/1", ProductionConfigurationTests.TestApplication.Foo.class).getName()).isEqualTo("foo");
    }

    @Test
    public void resource() throws Exception {
        assertThat(rest.getForObject("/proxy/html/test.html", String.class)).contains("<body>Test");
    }

    @Test
    public void resourceWithNoType() throws Exception {
        assertThat(rest.getForObject("/proxy/typeless/test.html", String.class)).contains("<body>Test");
    }

    @Test
    public void missing() throws Exception {
        assertThat(rest.getForEntity("/proxy/missing/0", ProductionConfigurationTests.TestApplication.Foo.class).getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void uri() throws Exception {
        assertThat(rest.getForObject("/proxy/0", ProductionConfigurationTests.TestApplication.Foo.class).getName()).isEqualTo("bye");
    }

    @Test
    public void post() throws Exception {
        assertThat(rest.postForObject("/proxy/0", Collections.singletonMap("name", "foo"), ProductionConfigurationTests.TestApplication.Bar.class).getName()).isEqualTo("host=localhost;foo");
    }

    @Test
    public void list() throws Exception {
        ResponseEntity<List<ProductionConfigurationTests.TestApplication.Bar>> result = rest.exchange(RequestEntity.post(rest.getRestTemplate().getUriTemplateHandler().expand("/proxy")).contentType(APPLICATION_JSON).body(Collections.singletonList(Collections.singletonMap("name", "foo"))), new org.springframework.core.ParameterizedTypeReference<List<ProductionConfigurationTests.TestApplication.Bar>>() {});
        assertThat(result.getBody().iterator().next().getName()).isEqualTo("host=localhost;foo");
    }

    @Test
    public void bodyless() throws Exception {
        assertThat(rest.postForObject("/proxy/0", Collections.singletonMap("name", "foo"), ProductionConfigurationTests.TestApplication.Bar.class).getName()).isEqualTo("host=localhost;foo");
    }

    @Test
    public void entity() throws Exception {
        assertThat(rest.exchange(RequestEntity.post(rest.getRestTemplate().getUriTemplateHandler().expand("/proxy/entity")).body(Collections.singletonMap("name", "foo")), new org.springframework.core.ParameterizedTypeReference<List<ProductionConfigurationTests.TestApplication.Bar>>() {}).getBody().iterator().next().getName()).isEqualTo("host=localhost;foo");
    }

    @Test
    public void entityWithType() throws Exception {
        assertThat(rest.exchange(RequestEntity.post(rest.getRestTemplate().getUriTemplateHandler().expand("/proxy/type")).body(Collections.singletonMap("name", "foo")), new org.springframework.core.ParameterizedTypeReference<List<ProductionConfigurationTests.TestApplication.Bar>>() {}).getBody().iterator().next().getName()).isEqualTo("host=localhost;foo");
    }

    @Test
    public void single() throws Exception {
        assertThat(rest.postForObject("/proxy/single", Collections.singletonMap("name", "foobar"), ProductionConfigurationTests.TestApplication.Bar.class).getName()).isEqualTo("host=localhost;foobar");
    }

    @Test
    public void converter() throws Exception {
        assertThat(rest.postForObject("/proxy/converter", Collections.singletonMap("name", "foobar"), ProductionConfigurationTests.TestApplication.Bar.class).getName()).isEqualTo("host=localhost;foobar");
    }

    @Test
    @SuppressWarnings({ "Duplicates", "unchecked" })
    public void headers() throws Exception {
        Map<String, List<String>> headers = rest.exchange(RequestEntity.get(rest.getRestTemplate().getUriTemplateHandler().expand("/proxy/headers")).header("foo", "bar").header("abc", "xyz").build(), Map.class).getBody();
        assertThat(headers).doesNotContainKey("foo").doesNotContainKey("hello").containsKeys("bar", "abc");
        assertThat(headers.get("bar")).containsOnly("hello");
        assertThat(headers.get("abc")).containsOnly("123");
    }

    @SpringBootApplication
    static class TestApplication {
        @Autowired
        private ProductionConfigurationTests.TestApplication.ProxyController controller;

        public void setHome(URI home) {
            controller.setHome(home);
        }

        @RestController
        static class ProxyController {
            private URI home;

            public void setHome(URI home) {
                this.home = home;
            }

            @GetMapping("/proxy/{id}")
            public Mono<ResponseEntity<Object>> proxyFoos(@PathVariable
            Integer id, ProxyExchange<Object> proxy) throws Exception {
                return proxy.uri((((home.toString()) + "/foos/") + id)).get();
            }

            @GetMapping("/proxy/path/**")
            public Mono<ResponseEntity<Object>> proxyPath(ProxyExchange<Object> proxy, UriComponentsBuilder uri) throws Exception {
                String path = proxy.path("/proxy/path/");
                return proxy.uri((((home.toString()) + "/foos/") + path)).get();
            }

            @GetMapping("/proxy/html/**")
            public Mono<ResponseEntity<String>> proxyHtml(ProxyExchange<String> proxy, UriComponentsBuilder uri) throws Exception {
                String path = proxy.path("/proxy/html");
                return proxy.uri(((home.toString()) + path)).get();
            }

            @GetMapping("/proxy/typeless/**")
            public Mono<ResponseEntity<byte[]>> proxyTypeless(ProxyExchange<byte[]> proxy, UriComponentsBuilder uri) throws Exception {
                String path = proxy.path("/proxy/typeless");
                return proxy.uri(((home.toString()) + path)).get();
            }

            @GetMapping("/proxy/missing/{id}")
            public Mono<ResponseEntity<Object>> proxyMissing(@PathVariable
            Integer id, ProxyExchange<Object> proxy) throws Exception {
                return proxy.uri((((home.toString()) + "/missing/") + id)).get();
            }

            @GetMapping("/proxy")
            public Mono<ResponseEntity<Object>> proxyUri(ProxyExchange<Object> proxy) throws Exception {
                return proxy.uri(((home.toString()) + "/foos")).get();
            }

            @PostMapping("/proxy/{id}")
            public Mono<ResponseEntity<Object>> proxyBars(@PathVariable
            Integer id, @RequestBody
            Map<String, Object> body, ProxyExchange<List<Object>> proxy) throws Exception {
                body.put("id", id);
                return proxy.uri(((home.toString()) + "/bars")).body(asList(body)).post(this::first);
            }

            @PostMapping("/proxy")
            public Mono<ResponseEntity<List<Object>>> barsWithNoBody(ProxyExchange<List<Object>> proxy) throws Exception {
                return proxy.uri(((home.toString()) + "/bars")).post();
            }

            @PostMapping("/proxy/entity")
            public Mono<ResponseEntity<Object>> explicitEntity(@RequestBody
            Mono<ProductionConfigurationTests.TestApplication.Foo> foo, ProxyExchange<Object> proxy) throws Exception {
                return proxy.uri(((home.toString()) + "/bars")).body(reactor.core.publisher.Flux.from(foo)).post();
            }

            @PostMapping("/proxy/type")
            public Mono<ResponseEntity<List<ProductionConfigurationTests.TestApplication.Bar>>> explicitEntityWithType(@RequestBody
            Mono<ProductionConfigurationTests.TestApplication.Foo> foo, ProxyExchange<List<ProductionConfigurationTests.TestApplication.Bar>> proxy) throws Exception {
                return proxy.uri(((home.toString()) + "/bars")).body(reactor.core.publisher.Flux.from(foo)).post();
            }

            @PostMapping("/proxy/single")
            public Mono<ResponseEntity<Object>> implicitEntity(@RequestBody
            Mono<ProductionConfigurationTests.TestApplication.Foo> foo, ProxyExchange<List<Object>> proxy) throws Exception {
                return proxy.uri(((home.toString()) + "/bars")).body(reactor.core.publisher.Flux.from(foo)).post(this::first);
            }

            @PostMapping("/proxy/converter")
            public Mono<ResponseEntity<ProductionConfigurationTests.TestApplication.Bar>> implicitEntityWithConverter(@RequestBody
            ProductionConfigurationTests.TestApplication.Foo foo, ProxyExchange<List<ProductionConfigurationTests.TestApplication.Bar>> proxy) throws Exception {
                return proxy.uri(((home.toString()) + "/bars")).body(asList(foo)).post(( response) -> ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(response.getBody().iterator().next()));
            }

            @GetMapping("/proxy/headers")
            public Mono<ResponseEntity<Map<String, List<String>>>> headers(ProxyExchange<Map<String, List<String>>> proxy) {
                proxy.sensitive("foo");
                proxy.sensitive("hello");
                proxy.header("bar", "hello");
                proxy.header("abc", "123");
                proxy.header("hello", "world");
                return proxy.uri(((home.toString()) + "/headers")).get();
            }

            private <T> ResponseEntity<T> first(ResponseEntity<List<T>> response) {
                return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(response.getBody().iterator().next());
            }
        }

        @RestController
        static class TestController {
            @GetMapping("/foos")
            public List<ProductionConfigurationTests.TestApplication.Foo> foos() {
                return asList(new ProductionConfigurationTests.TestApplication.Foo("hello"));
            }

            @GetMapping("/foos/{id}")
            public ProductionConfigurationTests.TestApplication.Foo foo(@PathVariable
            Integer id, @RequestHeader
            HttpHeaders headers) {
                String custom = headers.getFirst("X-Custom");
                return new ProductionConfigurationTests.TestApplication.Foo((id == 1 ? "foo" : custom != null ? custom : "bye"));
            }

            @PostMapping("/bars")
            public List<ProductionConfigurationTests.TestApplication.Bar> bars(@RequestBody
            List<ProductionConfigurationTests.TestApplication.Foo> foos, @RequestHeader
            HttpHeaders headers) {
                String custom = headers.getFirst("X-Custom");
                custom = (custom == null) ? "" : custom;
                custom = ((headers.getFirst("forwarded")) == null) ? custom : ((headers.getFirst("forwarded")) + ";") + custom;
                return asList(new ProductionConfigurationTests.TestApplication.Bar((custom + (foos.iterator().next().getName()))));
            }

            @GetMapping("/headers")
            public Map<String, List<String>> headers(@RequestHeader
            HttpHeaders headers) {
                return headers;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Foo {
            private String name;

            Foo() {
            }

            Foo(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Bar {
            private String name;

            Bar() {
            }

            Bar(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}

