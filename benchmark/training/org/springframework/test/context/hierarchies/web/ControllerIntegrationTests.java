/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.test.context.hierarchies.web;


import WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import javax.servlet.ServletContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;


/**
 *
 *
 * @author Sam Brannen
 * @since 3.2.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({ @ContextConfiguration(name = "root", classes = ControllerIntegrationTests.AppConfig.class), @ContextConfiguration(name = "dispatcher", classes = ControllerIntegrationTests.WebConfig.class) })
public class ControllerIntegrationTests {
    @Configuration
    static class AppConfig {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    static class WebConfig {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    // -------------------------------------------------------------------------
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private String foo;

    @Autowired
    private String bar;

    @Test
    public void verifyRootWacSupport() {
        Assert.assertEquals("foo", foo);
        Assert.assertEquals("bar", bar);
        ApplicationContext parent = wac.getParent();
        Assert.assertNotNull(parent);
        Assert.assertTrue((parent instanceof WebApplicationContext));
        WebApplicationContext root = ((WebApplicationContext) (parent));
        Assert.assertFalse(root.getBeansOfType(String.class).containsKey("bar"));
        ServletContext childServletContext = wac.getServletContext();
        Assert.assertNotNull(childServletContext);
        ServletContext rootServletContext = root.getServletContext();
        Assert.assertNotNull(rootServletContext);
        Assert.assertSame(childServletContext, rootServletContext);
        Assert.assertSame(root, rootServletContext.getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
        Assert.assertSame(root, childServletContext.getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
    }
}

