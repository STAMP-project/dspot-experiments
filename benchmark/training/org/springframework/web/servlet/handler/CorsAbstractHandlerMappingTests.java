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
package org.springframework.web.servlet.handler;


import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaders.ORIGIN;
import HttpStatus.OK;
import RequestMethod.GET;
import RequestMethod.OPTIONS;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.support.WebContentGenerator;


/**
 * Unit tests for CORS-related handling in {@link AbstractHandlerMapping}.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 */
public class CorsAbstractHandlerMappingTests {
    private MockHttpServletRequest request;

    private AbstractHandlerMapping handlerMapping;

    @Test
    public void actualRequestWithoutCorsConfigurationProvider() throws Exception {
        this.request.setMethod(GET.name());
        this.request.setRequestURI("/foo");
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        HandlerExecutionChain chain = handlerMapping.getHandler(this.request);
        Assert.assertNotNull(chain);
        Assert.assertTrue(((chain.getHandler()) instanceof CorsAbstractHandlerMappingTests.SimpleHandler));
    }

    @Test
    public void preflightRequestWithoutCorsConfigurationProvider() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.setRequestURI("/foo");
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        HandlerExecutionChain chain = handlerMapping.getHandler(this.request);
        Assert.assertNotNull(chain);
        Assert.assertNotNull(chain.getHandler());
        Assert.assertTrue(chain.getHandler().getClass().getSimpleName().equals("PreFlightHandler"));
    }

    @Test
    public void actualRequestWithCorsConfigurationProvider() throws Exception {
        this.request.setMethod(GET.name());
        this.request.setRequestURI("/cors");
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        HandlerExecutionChain chain = handlerMapping.getHandler(this.request);
        Assert.assertNotNull(chain);
        Assert.assertTrue(((chain.getHandler()) instanceof CorsAbstractHandlerMappingTests.CorsAwareHandler));
        CorsConfiguration config = getCorsConfiguration(chain, false);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(config.getAllowedOrigins().toArray(), new String[]{ "*" });
    }

    @Test
    public void preflightRequestWithCorsConfigurationProvider() throws Exception {
        this.request.setMethod(OPTIONS.name());
        this.request.setRequestURI("/cors");
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        HandlerExecutionChain chain = handlerMapping.getHandler(this.request);
        Assert.assertNotNull(chain);
        Assert.assertNotNull(chain.getHandler());
        Assert.assertTrue(chain.getHandler().getClass().getSimpleName().equals("PreFlightHandler"));
        CorsConfiguration config = getCorsConfiguration(chain, true);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(config.getAllowedOrigins().toArray(), new String[]{ "*" });
    }

    @Test
    public void actualRequestWithMappedCorsConfiguration() throws Exception {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        this.handlerMapping.setCorsConfigurations(Collections.singletonMap("/foo", config));
        this.request.setMethod(GET.name());
        this.request.setRequestURI("/foo");
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        HandlerExecutionChain chain = handlerMapping.getHandler(this.request);
        Assert.assertNotNull(chain);
        Assert.assertTrue(((chain.getHandler()) instanceof CorsAbstractHandlerMappingTests.SimpleHandler));
        config = getCorsConfiguration(chain, false);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(config.getAllowedOrigins().toArray(), new String[]{ "*" });
    }

    @Test
    public void preflightRequestWithMappedCorsConfiguration() throws Exception {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        this.handlerMapping.setCorsConfigurations(Collections.singletonMap("/foo", config));
        this.request.setMethod(OPTIONS.name());
        this.request.setRequestURI("/foo");
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        HandlerExecutionChain chain = handlerMapping.getHandler(this.request);
        Assert.assertNotNull(chain);
        Assert.assertNotNull(chain.getHandler());
        Assert.assertTrue(chain.getHandler().getClass().getSimpleName().equals("PreFlightHandler"));
        config = getCorsConfiguration(chain, true);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(config.getAllowedOrigins().toArray(), new String[]{ "*" });
    }

    @Test
    public void actualRequestWithCorsConfigurationSource() throws Exception {
        this.handlerMapping.setCorsConfigurationSource(new CorsAbstractHandlerMappingTests.CustomCorsConfigurationSource());
        this.request.setMethod(GET.name());
        this.request.setRequestURI("/foo");
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        HandlerExecutionChain chain = handlerMapping.getHandler(this.request);
        Assert.assertNotNull(chain);
        Assert.assertTrue(((chain.getHandler()) instanceof CorsAbstractHandlerMappingTests.SimpleHandler));
        CorsConfiguration config = getCorsConfiguration(chain, false);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedOrigins().toArray());
        Assert.assertEquals(true, config.getAllowCredentials());
    }

    @Test
    public void preflightRequestWithCorsConfigurationSource() throws Exception {
        this.handlerMapping.setCorsConfigurationSource(new CorsAbstractHandlerMappingTests.CustomCorsConfigurationSource());
        this.request.setMethod(OPTIONS.name());
        this.request.setRequestURI("/foo");
        this.request.addHeader(ORIGIN, "http://domain2.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        HandlerExecutionChain chain = handlerMapping.getHandler(this.request);
        Assert.assertNotNull(chain);
        Assert.assertNotNull(chain.getHandler());
        Assert.assertTrue(chain.getHandler().getClass().getSimpleName().equals("PreFlightHandler"));
        CorsConfiguration config = getCorsConfiguration(chain, true);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedOrigins().toArray());
        Assert.assertEquals(true, config.getAllowCredentials());
    }

    public class TestHandlerMapping extends AbstractHandlerMapping {
        @Override
        protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
            if (request.getRequestURI().equals("/cors")) {
                return new CorsAbstractHandlerMappingTests.CorsAwareHandler();
            }
            return new CorsAbstractHandlerMappingTests.SimpleHandler();
        }
    }

    public class SimpleHandler extends WebContentGenerator implements HttpRequestHandler {
        public SimpleHandler() {
            super(METHOD_GET);
        }

        @Override
        public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setStatus(OK.value());
        }
    }

    public class CorsAwareHandler extends CorsAbstractHandlerMappingTests.SimpleHandler implements CorsConfigurationSource {
        @Override
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedOrigin("*");
            return config;
        }
    }

    public class CustomCorsConfigurationSource implements CorsConfigurationSource {
        @Override
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedOrigin("*");
            config.setAllowCredentials(true);
            return config;
        }
    }
}

