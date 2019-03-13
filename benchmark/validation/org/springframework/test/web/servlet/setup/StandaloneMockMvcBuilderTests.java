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
package org.springframework.test.web.servlet.setup;


import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.impl.UnknownSerializer;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.converter.json.SpringHandlerInstantiator;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;


/**
 * Tests for {@link StandaloneMockMvcBuilder}
 *
 * @author Rossen Stoyanchev
 * @author Rob Winch
 * @author Sebastien Deleuze
 */
public class StandaloneMockMvcBuilderTests {
    // SPR-10825
    @Test
    public void placeHoldersInRequestMapping() throws Exception {
        StandaloneMockMvcBuilderTests.TestStandaloneMockMvcBuilder builder = new StandaloneMockMvcBuilderTests.TestStandaloneMockMvcBuilder(new StandaloneMockMvcBuilderTests.PlaceholderController());
        addPlaceholderValue("sys.login.ajax", "/foo");
        build();
        RequestMappingHandlerMapping hm = builder.wac.getBean(RequestMappingHandlerMapping.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
        HandlerExecutionChain chain = hm.getHandler(request);
        Assert.assertNotNull(chain);
        Assert.assertEquals("handleWithPlaceholders", getMethod().getName());
    }

    // SPR-13637
    @Test
    public void suffixPatternMatch() throws Exception {
        StandaloneMockMvcBuilderTests.TestStandaloneMockMvcBuilder builder = new StandaloneMockMvcBuilderTests.TestStandaloneMockMvcBuilder(new StandaloneMockMvcBuilderTests.PersonController());
        setUseSuffixPatternMatch(false);
        build();
        RequestMappingHandlerMapping hm = builder.wac.getBean(RequestMappingHandlerMapping.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/persons");
        HandlerExecutionChain chain = hm.getHandler(request);
        Assert.assertNotNull(chain);
        Assert.assertEquals("persons", getMethod().getName());
        request = new MockHttpServletRequest("GET", "/persons.xml");
        chain = hm.getHandler(request);
        Assert.assertNull(chain);
    }

    // SPR-12553
    @Test
    public void applicationContextAttribute() {
        StandaloneMockMvcBuilderTests.TestStandaloneMockMvcBuilder builder = new StandaloneMockMvcBuilderTests.TestStandaloneMockMvcBuilder(new StandaloneMockMvcBuilderTests.PlaceholderController());
        addPlaceholderValue("sys.login.ajax", "/foo");
        WebApplicationContext wac = builder.initWebAppContext();
        Assert.assertEquals(wac, WebApplicationContextUtils.getRequiredWebApplicationContext(wac.getServletContext()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFiltersFiltersNull() {
        StandaloneMockMvcBuilder builder = MockMvcBuilders.standaloneSetup(new StandaloneMockMvcBuilderTests.PersonController());
        builder.addFilters(((Filter[]) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFiltersFiltersContainsNull() {
        StandaloneMockMvcBuilder builder = MockMvcBuilders.standaloneSetup(new StandaloneMockMvcBuilderTests.PersonController());
        builder.addFilters(new StandaloneMockMvcBuilderTests.ContinueFilter(), ((Filter) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFilterPatternsNull() {
        StandaloneMockMvcBuilder builder = MockMvcBuilders.standaloneSetup(new StandaloneMockMvcBuilderTests.PersonController());
        builder.addFilter(new StandaloneMockMvcBuilderTests.ContinueFilter(), ((String[]) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFilterPatternContainsNull() {
        StandaloneMockMvcBuilder builder = MockMvcBuilders.standaloneSetup(new StandaloneMockMvcBuilderTests.PersonController());
        builder.addFilter(new StandaloneMockMvcBuilderTests.ContinueFilter(), ((String) (null)));
    }

    // SPR-13375
    @Test
    @SuppressWarnings("rawtypes")
    public void springHandlerInstantiator() {
        StandaloneMockMvcBuilderTests.TestStandaloneMockMvcBuilder builder = new StandaloneMockMvcBuilderTests.TestStandaloneMockMvcBuilder(new StandaloneMockMvcBuilderTests.PersonController());
        build();
        SpringHandlerInstantiator instantiator = new SpringHandlerInstantiator(builder.wac.getAutowireCapableBeanFactory());
        JsonSerializer serializer = instantiator.serializerInstance(null, null, UnknownSerializer.class);
        Assert.assertNotNull(serializer);
    }

    @Controller
    private static class PlaceholderController {
        @RequestMapping("${sys.login.ajax}")
        private void handleWithPlaceholders() {
        }
    }

    private static class TestStandaloneMockMvcBuilder extends StandaloneMockMvcBuilder {
        private WebApplicationContext wac;

        private TestStandaloneMockMvcBuilder(Object... controllers) {
            super(controllers);
        }

        @Override
        protected WebApplicationContext initWebAppContext() {
            this.wac = super.initWebAppContext();
            return this.wac;
        }
    }

    @Controller
    private static class PersonController {
        @RequestMapping("/persons")
        public String persons() {
            return null;
        }

        @RequestMapping("/forward")
        public String forward() {
            return "forward:/persons";
        }
    }

    private class ContinueFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
            filterChain.doFilter(request, response);
        }
    }
}

