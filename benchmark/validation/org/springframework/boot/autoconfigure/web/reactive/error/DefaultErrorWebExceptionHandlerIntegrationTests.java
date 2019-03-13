/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.autoconfigure.web.reactive.error;


import javax.validation.Valid;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * Integration tests for {@link DefaultErrorWebExceptionHandler}
 *
 * @author Brian Clozel
 */
public class DefaultErrorWebExceptionHandlerIntegrationTests {
    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    private ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner().withConfiguration(AutoConfigurations.of(ReactiveWebServerFactoryAutoConfiguration.class, HttpHandlerAutoConfiguration.class, WebFluxAutoConfiguration.class, ErrorWebFluxAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class, MustacheAutoConfiguration.class)).withPropertyValues("spring.main.web-application-type=reactive", "server.port=0").withUserConfiguration(DefaultErrorWebExceptionHandlerIntegrationTests.Application.class);

    @Test
    public void jsonError() {
        this.contextRunner.run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            client.get().uri("/").exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR).expectBody().jsonPath("status").isEqualTo("500").jsonPath("error").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).jsonPath("path").isEqualTo("/").jsonPath("message").isEqualTo("Expected!").jsonPath("exception").doesNotExist().jsonPath("trace").doesNotExist();
            this.outputCapture.expect(Matchers.allOf(containsString("500 Server Error for HTTP GET \"/\""), containsString("java.lang.IllegalStateException: Expected!")));
        });
    }

    @Test
    public void notFound() {
        this.contextRunner.run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            client.get().uri("/notFound").exchange().expectStatus().isNotFound().expectBody().jsonPath("status").isEqualTo("404").jsonPath("error").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase()).jsonPath("path").isEqualTo("/notFound").jsonPath("exception").doesNotExist();
        });
    }

    @Test
    public void htmlError() {
        this.contextRunner.run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            String body = client.get().uri("/").accept(MediaType.TEXT_HTML).exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR).expectHeader().contentType(MediaType.TEXT_HTML).expectBody(.class).returnResult().getResponseBody();
            assertThat(body).contains("status: 500").contains("message: Expected!");
            this.outputCapture.expect(Matchers.allOf(containsString("500 Server Error for HTTP GET \"/\""), containsString("java.lang.IllegalStateException: Expected!")));
        });
    }

    @Test
    public void bindingResultError() {
        this.contextRunner.run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            client.post().uri("/bind").contentType(MediaType.APPLICATION_JSON).syncBody("{}").exchange().expectStatus().isBadRequest().expectBody().jsonPath("status").isEqualTo("400").jsonPath("error").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase()).jsonPath("path").isEqualTo("/bind").jsonPath("exception").doesNotExist().jsonPath("errors").isArray().jsonPath("message").isNotEmpty();
        });
    }

    @Test
    public void includeStackTraceOnParam() {
        this.contextRunner.withPropertyValues("server.error.include-exception=true", "server.error.include-stacktrace=on-trace-param").run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            client.get().uri("/?trace=true").exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR).expectBody().jsonPath("status").isEqualTo("500").jsonPath("error").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).jsonPath("exception").isEqualTo(.class.getName()).jsonPath("trace").exists();
        });
    }

    @Test
    public void alwaysIncludeStackTrace() throws Exception {
        this.contextRunner.withPropertyValues("server.error.include-exception=true", "server.error.include-stacktrace=always").run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            client.get().uri("/?trace=false").exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR).expectBody().jsonPath("status").isEqualTo("500").jsonPath("error").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).jsonPath("exception").isEqualTo(.class.getName()).jsonPath("trace").exists();
        });
    }

    @Test
    public void neverIncludeStackTrace() {
        this.contextRunner.withPropertyValues("server.error.include-exception=true", "server.error.include-stacktrace=never").run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            client.get().uri("/?trace=true").exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR).expectBody().jsonPath("status").isEqualTo("500").jsonPath("error").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).jsonPath("exception").isEqualTo(.class.getName()).jsonPath("trace").doesNotExist();
        });
    }

    @Test
    public void statusException() {
        this.contextRunner.withPropertyValues("server.error.include-exception=true").run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            client.get().uri("/badRequest").exchange().expectStatus().isBadRequest().expectBody().jsonPath("status").isEqualTo("400").jsonPath("error").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase()).jsonPath("exception").isEqualTo(.class.getName());
        });
    }

    @Test
    public void defaultErrorView() {
        this.contextRunner.withPropertyValues("spring.mustache.prefix=classpath:/unknown/", "server.error.include-stacktrace=always").run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            String body = client.get().uri("/").accept(MediaType.TEXT_HTML).exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR).expectHeader().contentType(MediaType.TEXT_HTML).expectBody(.class).returnResult().getResponseBody();
            assertThat(body).contains("Whitelabel Error Page").contains("<div>Expected!</div>").contains("<div style='white-space:pre-wrap;'>java.lang.IllegalStateException");
        });
    }

    @Test
    public void escapeHtmlInDefaultErrorView() {
        this.contextRunner.withPropertyValues("spring.mustache.prefix=classpath:/unknown/").run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            String body = client.get().uri("/html").accept(MediaType.TEXT_HTML).exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR).expectHeader().contentType(MediaType.TEXT_HTML).expectBody(.class).returnResult().getResponseBody();
            assertThat(body).contains("Whitelabel Error Page").doesNotContain("<script>").contains("&lt;script&gt;");
        });
    }

    @Test
    public void testExceptionWithNullMessage() {
        this.contextRunner.withPropertyValues("spring.mustache.prefix=classpath:/unknown/").run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            String body = client.get().uri("/notfound").accept(MediaType.TEXT_HTML).exchange().expectStatus().isNotFound().expectHeader().contentType(MediaType.TEXT_HTML).expectBody(.class).returnResult().getResponseBody();
            assertThat(body).contains("Whitelabel Error Page").contains("type=Not Found, status=404");
        });
    }

    @Test
    public void responseCommitted() {
        this.contextRunner.run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            assertThatExceptionOfType(.class).isThrownBy(() -> client.get().uri("/commit").exchange().expectStatus()).withCauseInstanceOf(.class).withMessageContaining("already committed!");
        });
    }

    @Test
    public void whitelabelDisabled() {
        this.contextRunner.withPropertyValues("server.error.whitelabel.enabled=false", "spring.mustache.prefix=classpath:/unknown/").run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            client.get().uri("/notfound").accept(MediaType.TEXT_HTML).exchange().expectStatus().isNotFound().expectBody().isEmpty();
        });
    }

    @Test
    public void exactStatusTemplateErrorPage() {
        this.contextRunner.withPropertyValues("server.error.whitelabel.enabled=false", ("spring.mustache.prefix=" + (getErrorTemplatesLocation()))).run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            String body = client.get().uri("/notfound").accept(MediaType.TEXT_HTML).exchange().expectStatus().isNotFound().expectBody(.class).returnResult().getResponseBody();
            assertThat(body).contains("404 page");
        });
    }

    @Test
    public void seriesStatusTemplateErrorPage() {
        this.contextRunner.withPropertyValues("server.error.whitelabel.enabled=false", ("spring.mustache.prefix=" + (getErrorTemplatesLocation()))).run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            String body = client.get().uri("/badRequest").accept(MediaType.TEXT_HTML).exchange().expectStatus().isBadRequest().expectBody(.class).returnResult().getResponseBody();
            assertThat(body).contains("4xx page");
        });
    }

    @Test
    public void invalidAcceptMediaType() {
        this.contextRunner.run(( context) -> {
            WebTestClient client = WebTestClient.bindToApplicationContext(context).build();
            client.get().uri("/notfound").header("Accept", "v=3.0").exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
        });
    }

    @Configuration
    public static class Application {
        @RestController
        protected static class ErrorController {
            @GetMapping("/")
            public String home() {
                throw new IllegalStateException("Expected!");
            }

            @GetMapping("/badRequest")
            public Mono<String> badRequest() {
                return Mono.error(new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST));
            }

            @GetMapping("/commit")
            public Mono<Void> commit(ServerWebExchange exchange) {
                return exchange.getResponse().writeWith(Mono.empty()).then(Mono.error(new IllegalStateException("already committed!")));
            }

            @GetMapping("/html")
            public String htmlEscape() {
                throw new IllegalStateException("<script>");
            }

            @PostMapping(path = "/bind", produces = "application/json")
            @ResponseBody
            public String bodyValidation(@Valid
            @RequestBody
            DummyBody body) {
                return body.getContent();
            }
        }
    }
}

