/**
 * Copyright 2002-2018 the original author or authors.
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


import HttpStatus.NOT_MODIFIED;
import HttpStatus.SEE_OTHER;
import MediaType.ALL;
import MediaType.TEXT_HTML;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.server.ServerWebExchange;


/**
 * {@code @RequestMapping} integration tests with view resolution scenarios.
 *
 * @author Rossen Stoyanchev
 */
public class RequestMappingViewResolutionIntegrationTests extends AbstractRequestMappingIntegrationTests {
    @Test
    public void html() throws Exception {
        String expected = "<html><body>Hello: Jason!</body></html>";
        Assert.assertEquals(expected, performGet("/html?name=Jason", TEXT_HTML, String.class).getBody());
    }

    @Test
    public void etagCheckWithNotModifiedResponse() throws Exception {
        URI uri = new URI((("http://localhost:" + (this.port)) + "/html"));
        RequestEntity<Void> request = RequestEntity.get(uri).ifNoneMatch("\"deadb33f8badf00d\"").build();
        ResponseEntity<String> response = getRestTemplate().exchange(request, String.class);
        Assert.assertEquals(NOT_MODIFIED, response.getStatusCode());
        Assert.assertNull(response.getBody());
    }

    // SPR-15291
    @Test
    public void redirect() throws Exception {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection conn, String method) throws IOException {
                super.prepareConnection(conn, method);
                conn.setInstanceFollowRedirects(false);
            }
        };
        URI uri = new URI((("http://localhost:" + (this.port)) + "/redirect"));
        RequestEntity<Void> request = RequestEntity.get(uri).accept(ALL).build();
        ResponseEntity<Void> response = new org.springframework.web.client.RestTemplate(factory).exchange(request, Void.class);
        Assert.assertEquals(SEE_OTHER, response.getStatusCode());
        Assert.assertEquals("/", response.getHeaders().getLocation().toString());
    }

    @Configuration
    @EnableWebFlux
    @ComponentScan(resourcePattern = "**/RequestMappingViewResolutionIntegrationTests$*.class")
    @SuppressWarnings({ "unused", "WeakerAccess" })
    static class WebConfig implements WebFluxConfigurer {
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.freeMarker();
        }

        @Bean
        public FreeMarkerConfigurer freeMarkerConfig() {
            FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
            configurer.setPreferFileSystemAccess(false);
            configurer.setTemplateLoaderPath("classpath*:org/springframework/web/reactive/view/freemarker/");
            return configurer;
        }
    }

    @Controller
    @SuppressWarnings({ "unused", "OptionalUsedAsFieldOrParameterType" })
    private static class TestController {
        @GetMapping("/html")
        public String getHtmlPage(Optional<String> name, Model model, ServerWebExchange exchange) {
            if (exchange.checkNotModified("deadb33f8badf00d")) {
                return null;
            }
            model.addAttribute("hello", (("Hello: " + (name.orElse("<no name>"))) + "!"));
            return "test";
        }

        @GetMapping("/redirect")
        public String redirect() {
            return "redirect:/";
        }
    }
}

