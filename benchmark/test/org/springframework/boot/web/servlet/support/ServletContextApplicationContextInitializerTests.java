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
package org.springframework.boot.web.servlet.support;


import WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import javax.servlet.ServletContext;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.context.ConfigurableWebApplicationContext;


/**
 * Tests for {@link ServletContextApplicationContextInitializer}.
 *
 * @author Andy Wilkinson
 */
public class ServletContextApplicationContextInitializerTests {
    private final ServletContext servletContext = Mockito.mock(ServletContext.class);

    private final ConfigurableWebApplicationContext applicationContext = Mockito.mock(ConfigurableWebApplicationContext.class);

    @Test
    public void servletContextIsSetOnTheApplicationContext() {
        new ServletContextApplicationContextInitializer(this.servletContext).initialize(this.applicationContext);
        Mockito.verify(this.applicationContext).setServletContext(this.servletContext);
    }

    @Test
    public void applicationContextIsNotStoredInServletContextByDefault() {
        new ServletContextApplicationContextInitializer(this.servletContext).initialize(this.applicationContext);
        Mockito.verify(this.servletContext, Mockito.never()).setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.applicationContext);
    }

    @Test
    public void applicationContextCanBeStoredInServletContext() {
        new ServletContextApplicationContextInitializer(this.servletContext, true).initialize(this.applicationContext);
        Mockito.verify(this.servletContext).setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.applicationContext);
    }
}

