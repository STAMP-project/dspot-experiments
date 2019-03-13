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
package org.springframework.web.reactive.handler;


import HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import HttpMethod.GET;
import HttpMethod.OPTIONS;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;


/**
 * Unit tests for CORS support at {@link AbstractUrlHandlerMapping} level.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 */
public class CorsUrlHandlerMappingTests {
    private AbstractUrlHandlerMapping handlerMapping;

    private Object welcomeController = new Object();

    private CorsUrlHandlerMappingTests.CorsAwareHandler corsController = new CorsUrlHandlerMappingTests.CorsAwareHandler();

    @Test
    public void actualRequestWithoutCorsConfigurationProvider() throws Exception {
        String origin = "http://domain2.com";
        ServerWebExchange exchange = createExchange(GET, "/welcome.html", origin);
        Object actual = this.handlerMapping.getHandler(exchange).block();
        Assert.assertNotNull(actual);
        Assert.assertSame(this.welcomeController, actual);
    }

    @Test
    public void preflightRequestWithoutCorsConfigurationProvider() throws Exception {
        String origin = "http://domain2.com";
        ServerWebExchange exchange = createExchange(OPTIONS, "/welcome.html", origin);
        Object actual = this.handlerMapping.getHandler(exchange).block();
        Assert.assertNotNull(actual);
        Assert.assertNotSame(this.welcomeController, actual);
        Assert.assertNull(exchange.getResponse().getHeaders().getFirst(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void actualRequestWithCorsAwareHandler() throws Exception {
        String origin = "http://domain2.com";
        ServerWebExchange exchange = createExchange(GET, "/cors.html", origin);
        Object actual = this.handlerMapping.getHandler(exchange).block();
        Assert.assertNotNull(actual);
        Assert.assertSame(this.corsController, actual);
        Assert.assertEquals("*", exchange.getResponse().getHeaders().getFirst(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void preFlightWithCorsAwareHandler() throws Exception {
        String origin = "http://domain2.com";
        ServerWebExchange exchange = createExchange(OPTIONS, "/cors.html", origin);
        Object actual = this.handlerMapping.getHandler(exchange).block();
        Assert.assertNotNull(actual);
        Assert.assertNotSame(this.corsController, actual);
        Assert.assertEquals("*", exchange.getResponse().getHeaders().getFirst(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void actualRequestWithGlobalCorsConfig() throws Exception {
        CorsConfiguration mappedConfig = new CorsConfiguration();
        mappedConfig.addAllowedOrigin("*");
        this.handlerMapping.setCorsConfigurations(Collections.singletonMap("/welcome.html", mappedConfig));
        String origin = "http://domain2.com";
        ServerWebExchange exchange = createExchange(GET, "/welcome.html", origin);
        Object actual = this.handlerMapping.getHandler(exchange).block();
        Assert.assertNotNull(actual);
        Assert.assertSame(this.welcomeController, actual);
        Assert.assertEquals("*", exchange.getResponse().getHeaders().getFirst(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void preFlightRequestWithGlobalCorsConfig() throws Exception {
        CorsConfiguration mappedConfig = new CorsConfiguration();
        mappedConfig.addAllowedOrigin("*");
        this.handlerMapping.setCorsConfigurations(Collections.singletonMap("/welcome.html", mappedConfig));
        String origin = "http://domain2.com";
        ServerWebExchange exchange = createExchange(OPTIONS, "/welcome.html", origin);
        Object actual = this.handlerMapping.getHandler(exchange).block();
        Assert.assertNotNull(actual);
        Assert.assertNotSame(this.welcomeController, actual);
        Assert.assertEquals("*", exchange.getResponse().getHeaders().getFirst(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void actualRequestWithCorsConfigurationSource() throws Exception {
        this.handlerMapping.setCorsConfigurationSource(new CorsUrlHandlerMappingTests.CustomCorsConfigurationSource());
        String origin = "http://domain2.com";
        ServerWebExchange exchange = createExchange(GET, "/welcome.html", origin);
        Object actual = this.handlerMapping.getHandler(exchange).block();
        Assert.assertNotNull(actual);
        Assert.assertSame(this.welcomeController, actual);
        Assert.assertEquals("http://domain2.com", exchange.getResponse().getHeaders().getFirst(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertEquals("true", exchange.getResponse().getHeaders().getFirst(ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }

    @Test
    public void preFlightRequestWithCorsConfigurationSource() throws Exception {
        this.handlerMapping.setCorsConfigurationSource(new CorsUrlHandlerMappingTests.CustomCorsConfigurationSource());
        String origin = "http://domain2.com";
        ServerWebExchange exchange = createExchange(OPTIONS, "/welcome.html", origin);
        Object actual = this.handlerMapping.getHandler(exchange).block();
        Assert.assertNotNull(actual);
        Assert.assertNotSame(this.welcomeController, actual);
        Assert.assertEquals("http://domain2.com", exchange.getResponse().getHeaders().getFirst(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertEquals("true", exchange.getResponse().getHeaders().getFirst(ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }

    private class CorsAwareHandler implements CorsConfigurationSource {
        @Override
        public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedOrigin("*");
            return config;
        }
    }

    public class CustomCorsConfigurationSource implements CorsConfigurationSource {
        @Override
        public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedOrigin("*");
            config.setAllowCredentials(true);
            return config;
        }
    }
}

