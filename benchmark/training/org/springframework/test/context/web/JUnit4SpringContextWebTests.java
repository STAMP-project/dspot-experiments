/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.test.context.web;


import WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import java.io.File;
import javax.servlet.ServletContext;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;


/**
 * JUnit-based integration tests that verify support for loading a
 * {@link WebApplicationContext} when extending {@link AbstractJUnit4SpringContextTests}.
 *
 * @author Sam Brannen
 * @since 3.2.7
 */
@ContextConfiguration
@WebAppConfiguration
public class JUnit4SpringContextWebTests extends AbstractJUnit4SpringContextTests implements ServletContextAware {
    @Configuration
    static class Config {
        @Bean
        public String foo() {
            return "enigma";
        }
    }

    protected ServletContext servletContext;

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected MockServletContext mockServletContext;

    @Autowired
    protected MockHttpServletRequest request;

    @Autowired
    protected MockHttpServletResponse response;

    @Autowired
    protected MockHttpSession session;

    @Autowired
    protected ServletWebRequest webRequest;

    @Autowired
    protected String foo;

    @Test
    public void basicWacFeatures() throws Exception {
        Assert.assertNotNull("ServletContext should be set in the WAC.", wac.getServletContext());
        Assert.assertNotNull("ServletContext should have been set via ServletContextAware.", servletContext);
        Assert.assertNotNull("ServletContext should have been autowired from the WAC.", mockServletContext);
        Assert.assertNotNull("MockHttpServletRequest should have been autowired from the WAC.", request);
        Assert.assertNotNull("MockHttpServletResponse should have been autowired from the WAC.", response);
        Assert.assertNotNull("MockHttpSession should have been autowired from the WAC.", session);
        Assert.assertNotNull("ServletWebRequest should have been autowired from the WAC.", webRequest);
        Object rootWac = mockServletContext.getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        Assert.assertNotNull(("Root WAC must be stored in the ServletContext as: " + (WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)), rootWac);
        Assert.assertSame("test WAC and Root WAC in ServletContext must be the same object.", wac, rootWac);
        Assert.assertSame("ServletContext instances must be the same object.", mockServletContext, wac.getServletContext());
        Assert.assertSame("ServletContext in the WAC and in the mock request", mockServletContext, request.getServletContext());
        Assert.assertEquals("Getting real path for ServletContext resource.", new File("src/main/webapp/index.jsp").getCanonicalPath(), mockServletContext.getRealPath("index.jsp"));
    }

    @Test
    public void fooEnigmaAutowired() {
        Assert.assertEquals("enigma", foo);
    }
}

