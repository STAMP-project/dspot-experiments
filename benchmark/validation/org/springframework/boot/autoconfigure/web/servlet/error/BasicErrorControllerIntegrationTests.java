/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.web.servlet.error;


import MediaType.APPLICATION_JSON;
import MediaType.TEXT_HTML;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;


/**
 * Tests for {@link BasicErrorController} using a real HTTP server.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class BasicErrorControllerIntegrationTests {
    private ConfigurableApplicationContext context;

    @Test
    @SuppressWarnings("rawtypes")
    public void testErrorForMachineClient() {
        load();
        ResponseEntity<Map> entity = new TestRestTemplate().getForEntity(createUrl("?trace=true"), Map.class);
        assertErrorAttributes(entity.getBody(), "500", "Internal Server Error", null, "Expected!", "/");
        assertThat(entity.getBody().containsKey("trace")).isFalse();
    }

    @Test
    public void testErrorForMachineClientTraceParamTrue() {
        errorForMachineClientOnTraceParam("?trace=true", true);
    }

    @Test
    public void testErrorForMachineClientTraceParamFalse() {
        errorForMachineClientOnTraceParam("?trace=false", false);
    }

    @Test
    public void testErrorForMachineClientTraceParamAbsent() {
        errorForMachineClientOnTraceParam("", false);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testErrorForMachineClientNoStacktrace() {
        load("--server.error.include-stacktrace=never");
        ResponseEntity<Map> entity = new TestRestTemplate().getForEntity(createUrl("?trace=true"), Map.class);
        assertErrorAttributes(entity.getBody(), "500", "Internal Server Error", null, "Expected!", "/");
        assertThat(entity.getBody().containsKey("trace")).isFalse();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testErrorForMachineClientAlwaysStacktrace() {
        load("--server.error.include-stacktrace=always");
        ResponseEntity<Map> entity = new TestRestTemplate().getForEntity(createUrl("?trace=false"), Map.class);
        assertErrorAttributes(entity.getBody(), "500", "Internal Server Error", null, "Expected!", "/");
        assertThat(entity.getBody().containsKey("trace")).isTrue();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testErrorForAnnotatedException() {
        load("--server.error.include-exception=true");
        ResponseEntity<Map> entity = new TestRestTemplate().getForEntity(createUrl("/annotated"), Map.class);
        assertErrorAttributes(entity.getBody(), "400", "Bad Request", BasicErrorControllerIntegrationTests.TestConfiguration.Errors.ExpectedException.class, "Expected!", "/annotated");
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testErrorForAnnotatedNoReasonException() {
        load("--server.error.include-exception=true");
        ResponseEntity<Map> entity = new TestRestTemplate().getForEntity(createUrl("/annotatedNoReason"), Map.class);
        assertErrorAttributes(entity.getBody(), "406", "Not Acceptable", BasicErrorControllerIntegrationTests.TestConfiguration.Errors.NoReasonExpectedException.class, "Expected message", "/annotatedNoReason");
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testBindingExceptionForMachineClient() {
        load("--server.error.include-exception=true");
        RequestEntity request = RequestEntity.get(URI.create(createUrl("/bind"))).accept(APPLICATION_JSON).build();
        ResponseEntity<Map> entity = new TestRestTemplate().exchange(request, Map.class);
        String resp = entity.getBody().toString();
        assertThat(resp).contains("Error count: 1");
        assertThat(resp).contains("errors=[{");
        assertThat(resp).contains("codes=[");
        assertThat(resp).contains("org.springframework.validation.BindException");
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testRequestBodyValidationForMachineClient() {
        load("--server.error.include-exception=true");
        RequestEntity request = RequestEntity.post(URI.create(createUrl("/bodyValidation"))).accept(APPLICATION_JSON).contentType(APPLICATION_JSON).body("{}");
        ResponseEntity<Map> entity = new TestRestTemplate().exchange(request, Map.class);
        String resp = entity.getBody().toString();
        assertThat(resp).contains("Error count: 1");
        assertThat(resp).contains("errors=[{");
        assertThat(resp).contains("codes=[");
        assertThat(resp).contains(MethodArgumentNotValidException.class.getName());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testNoExceptionByDefaultForMachineClient() {
        load();
        RequestEntity request = RequestEntity.get(URI.create(createUrl("/bind"))).accept(APPLICATION_JSON).build();
        ResponseEntity<Map> entity = new TestRestTemplate().exchange(request, Map.class);
        String resp = entity.getBody().toString();
        assertThat(resp).doesNotContain("org.springframework.validation.BindException");
    }

    @Test
    public void testConventionTemplateMapping() {
        load();
        RequestEntity<?> request = RequestEntity.get(URI.create(createUrl("/noStorage"))).accept(TEXT_HTML).build();
        ResponseEntity<String> entity = new TestRestTemplate().exchange(request, String.class);
        String resp = entity.getBody();
        assertThat(resp).contains("We are out of storage");
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ImportAutoConfiguration({ ServletWebServerFactoryAutoConfiguration.class, DispatcherServletAutoConfiguration.class, WebMvcAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, ErrorMvcAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class })
    private @interface MinimalWebConfiguration {}

    @Configuration
    @BasicErrorControllerIntegrationTests.MinimalWebConfiguration
    @ImportAutoConfiguration(FreeMarkerAutoConfiguration.class)
    public static class TestConfiguration {
        // For manual testing
        public static void main(String[] args) {
            SpringApplication.run(BasicErrorControllerIntegrationTests.TestConfiguration.class, args);
        }

        @Bean
        public View error() {
            return new AbstractView() {
                @Override
                protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                    response.getWriter().write("ERROR_BEAN");
                }
            };
        }

        @RestController
        protected static class Errors {
            public String getFoo() {
                return "foo";
            }

            @RequestMapping("/")
            public String home() {
                throw new IllegalStateException("Expected!");
            }

            @RequestMapping("/annotated")
            public String annotated() {
                throw new BasicErrorControllerIntegrationTests.TestConfiguration.Errors.ExpectedException();
            }

            @RequestMapping("/annotatedNoReason")
            public String annotatedNoReason() {
                throw new BasicErrorControllerIntegrationTests.TestConfiguration.Errors.NoReasonExpectedException("Expected message");
            }

            @RequestMapping("/bind")
            public String bind() throws Exception {
                BindException error = new BindException(this, "test");
                error.rejectValue("foo", "bar.error");
                throw error;
            }

            @PostMapping(path = "/bodyValidation", produces = "application/json")
            public String bodyValidation(@Valid
            @RequestBody
            BasicErrorControllerIntegrationTests.TestConfiguration.Errors.DummyBody body) {
                return body.content;
            }

            @RequestMapping(path = "/noStorage")
            public String noStorage() {
                throw new BasicErrorControllerIntegrationTests.TestConfiguration.Errors.InsufficientStorageException();
            }

            @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Expected!")
            @SuppressWarnings("serial")
            private static class ExpectedException extends RuntimeException {}

            @ResponseStatus(HttpStatus.INSUFFICIENT_STORAGE)
            private static class InsufficientStorageException extends RuntimeException {}

            @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
            @SuppressWarnings("serial")
            private static class NoReasonExpectedException extends RuntimeException {
                NoReasonExpectedException(String message) {
                    super(message);
                }
            }

            static class DummyBody {
                @NotNull
                private String content;

                public String getContent() {
                    return this.content;
                }

                public void setContent(String content) {
                    this.content = content;
                }
            }
        }
    }
}

