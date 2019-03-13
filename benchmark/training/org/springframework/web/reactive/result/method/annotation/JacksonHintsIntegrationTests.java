/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.web.reactive.result.method.annotation;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_JSON_UTF8;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Sebastien Deleuze
 */
public class JacksonHintsIntegrationTests extends AbstractRequestMappingIntegrationTests {
    @Test
    public void jsonViewResponse() throws Exception {
        String expected = "{\"withView1\":\"with\"}";
        Assert.assertEquals(expected, performGet("/response/raw", APPLICATION_JSON_UTF8, String.class).getBody());
    }

    @Test
    public void jsonViewWithMonoResponse() throws Exception {
        String expected = "{\"withView1\":\"with\"}";
        Assert.assertEquals(expected, performGet("/response/mono", APPLICATION_JSON_UTF8, String.class).getBody());
    }

    // SPR-16098
    @Test
    public void jsonViewWithMonoResponseEntity() throws Exception {
        String expected = "{\"withView1\":\"with\"}";
        Assert.assertEquals(expected, performGet("/response/entity", APPLICATION_JSON_UTF8, String.class).getBody());
    }

    @Test
    public void jsonViewWithFluxResponse() throws Exception {
        String expected = "[{\"withView1\":\"with\"},{\"withView1\":\"with\"}]";
        Assert.assertEquals(expected, performGet("/response/flux", APPLICATION_JSON_UTF8, String.class).getBody());
    }

    @Test
    public void jsonViewWithRequest() throws Exception {
        String expected = "{\"withView1\":\"with\",\"withView2\":null,\"withoutView\":null}";
        Assert.assertEquals(expected, performPost("/request/raw", APPLICATION_JSON, new JacksonHintsIntegrationTests.JacksonViewBean("with", "with", "without"), APPLICATION_JSON_UTF8, String.class).getBody());
    }

    @Test
    public void jsonViewWithMonoRequest() throws Exception {
        String expected = "{\"withView1\":\"with\",\"withView2\":null,\"withoutView\":null}";
        Assert.assertEquals(expected, performPost("/request/mono", APPLICATION_JSON, new JacksonHintsIntegrationTests.JacksonViewBean("with", "with", "without"), APPLICATION_JSON_UTF8, String.class).getBody());
    }

    // SPR-16098
    @Test
    public void jsonViewWithEntityMonoRequest() throws Exception {
        String expected = "{\"withView1\":\"with\",\"withView2\":null,\"withoutView\":null}";
        Assert.assertEquals(expected, performPost("/request/entity/mono", APPLICATION_JSON, new JacksonHintsIntegrationTests.JacksonViewBean("with", "with", "without"), APPLICATION_JSON_UTF8, String.class).getBody());
    }

    // SPR-16098
    @Test
    public void jsonViewWithEntityFluxRequest() throws Exception {
        String expected = "[" + ("{\"withView1\":\"with\",\"withView2\":null,\"withoutView\":null}," + "{\"withView1\":\"with\",\"withView2\":null,\"withoutView\":null}]");
        Assert.assertEquals(expected, performPost("/request/entity/flux", APPLICATION_JSON, Arrays.asList(new JacksonHintsIntegrationTests.JacksonViewBean("with", "with", "without"), new JacksonHintsIntegrationTests.JacksonViewBean("with", "with", "without")), APPLICATION_JSON_UTF8, String.class).getBody());
    }

    @Test
    public void jsonViewWithFluxRequest() throws Exception {
        String expected = "[" + ("{\"withView1\":\"with\",\"withView2\":null,\"withoutView\":null}," + "{\"withView1\":\"with\",\"withView2\":null,\"withoutView\":null}]");
        List<JacksonHintsIntegrationTests.JacksonViewBean> beans = Arrays.asList(new JacksonHintsIntegrationTests.JacksonViewBean("with", "with", "without"), new JacksonHintsIntegrationTests.JacksonViewBean("with", "with", "without"));
        Assert.assertEquals(expected, performPost("/request/flux", APPLICATION_JSON, beans, APPLICATION_JSON_UTF8, String.class).getBody());
    }

    @Configuration
    @ComponentScan(resourcePattern = "**/JacksonHintsIntegrationTests*.class")
    @EnableWebFlux
    @SuppressWarnings({ "unused", "WeakerAccess" })
    static class WebConfig {}

    @RestController
    @SuppressWarnings("unused")
    private static class JsonViewRestController {
        @GetMapping("/response/raw")
        @JsonView(JacksonHintsIntegrationTests.MyJacksonView1.class)
        public JacksonHintsIntegrationTests.JacksonViewBean rawResponse() {
            return new JacksonHintsIntegrationTests.JacksonViewBean("with", "with", "without");
        }

        @GetMapping("/response/mono")
        @JsonView(JacksonHintsIntegrationTests.MyJacksonView1.class)
        public Mono<JacksonHintsIntegrationTests.JacksonViewBean> monoResponse() {
            return Mono.just(new JacksonHintsIntegrationTests.JacksonViewBean("with", "with", "without"));
        }

        @GetMapping("/response/entity")
        @JsonView(JacksonHintsIntegrationTests.MyJacksonView1.class)
        public Mono<ResponseEntity<JacksonHintsIntegrationTests.JacksonViewBean>> monoResponseEntity() {
            return Mono.just(ResponseEntity.ok(new JacksonHintsIntegrationTests.JacksonViewBean("with", "with", "without")));
        }

        @GetMapping("/response/flux")
        @JsonView(JacksonHintsIntegrationTests.MyJacksonView1.class)
        public Flux<JacksonHintsIntegrationTests.JacksonViewBean> fluxResponse() {
            return Flux.just(new JacksonHintsIntegrationTests.JacksonViewBean("with", "with", "without"), new JacksonHintsIntegrationTests.JacksonViewBean("with", "with", "without"));
        }

        @PostMapping("/request/raw")
        public JacksonHintsIntegrationTests.JacksonViewBean rawRequest(@JsonView(JacksonHintsIntegrationTests.MyJacksonView1.class)
        @RequestBody
        JacksonHintsIntegrationTests.JacksonViewBean bean) {
            return bean;
        }

        @PostMapping("/request/mono")
        public Mono<JacksonHintsIntegrationTests.JacksonViewBean> monoRequest(@JsonView(JacksonHintsIntegrationTests.MyJacksonView1.class)
        @RequestBody
        Mono<JacksonHintsIntegrationTests.JacksonViewBean> mono) {
            return mono;
        }

        @PostMapping("/request/entity/mono")
        public Mono<JacksonHintsIntegrationTests.JacksonViewBean> entityMonoRequest(@JsonView(JacksonHintsIntegrationTests.MyJacksonView1.class)
        HttpEntity<Mono<JacksonHintsIntegrationTests.JacksonViewBean>> entityMono) {
            return entityMono.getBody();
        }

        @PostMapping("/request/entity/flux")
        public Flux<JacksonHintsIntegrationTests.JacksonViewBean> entityFluxRequest(@JsonView(JacksonHintsIntegrationTests.MyJacksonView1.class)
        HttpEntity<Flux<JacksonHintsIntegrationTests.JacksonViewBean>> entityFlux) {
            return entityFlux.getBody();
        }

        @PostMapping("/request/flux")
        public Flux<JacksonHintsIntegrationTests.JacksonViewBean> fluxRequest(@JsonView(JacksonHintsIntegrationTests.MyJacksonView1.class)
        @RequestBody
        Flux<JacksonHintsIntegrationTests.JacksonViewBean> flux) {
            return flux;
        }
    }

    private interface MyJacksonView1 {}

    private interface MyJacksonView2 {}

    @SuppressWarnings("unused")
    private static class JacksonViewBean {
        @JsonView(JacksonHintsIntegrationTests.MyJacksonView1.class)
        private String withView1;

        @JsonView(JacksonHintsIntegrationTests.MyJacksonView2.class)
        private String withView2;

        private String withoutView;

        public JacksonViewBean() {
        }

        public JacksonViewBean(String withView1, String withView2, String withoutView) {
            this.withView1 = withView1;
            this.withView2 = withView2;
            this.withoutView = withoutView;
        }

        public String getWithView1() {
            return withView1;
        }

        public void setWithView1(String withView1) {
            this.withView1 = withView1;
        }

        public String getWithView2() {
            return withView2;
        }

        public void setWithView2(String withView2) {
            this.withView2 = withView2;
        }

        public String getWithoutView() {
            return withoutView;
        }

        public void setWithoutView(String withoutView) {
            this.withoutView = withoutView;
        }
    }
}

