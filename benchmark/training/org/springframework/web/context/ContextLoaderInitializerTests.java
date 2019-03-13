/**
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.context;


import java.util.EventListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Test case for {@link AbstractContextLoaderInitializer}.
 *
 * @author Arjen Poutsma
 */
public class ContextLoaderInitializerTests {
    private static final String BEAN_NAME = "myBean";

    private AbstractContextLoaderInitializer initializer;

    private MockServletContext servletContext;

    private EventListener eventListener;

    @Test
    public void register() throws ServletException {
        initializer.onStartup(servletContext);
        Assert.assertTrue(((eventListener) instanceof ContextLoaderListener));
        ContextLoaderListener cll = ((ContextLoaderListener) (eventListener));
        cll.contextInitialized(new ServletContextEvent(servletContext));
        WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        Assert.assertTrue(applicationContext.containsBean(ContextLoaderInitializerTests.BEAN_NAME));
        Assert.assertTrue(((applicationContext.getBean(ContextLoaderInitializerTests.BEAN_NAME)) instanceof ContextLoaderInitializerTests.MyBean));
    }

    private class MyMockServletContext extends MockServletContext {
        @Override
        public <T extends EventListener> void addListener(T listener) {
            eventListener = listener;
        }
    }

    private static class MyContextLoaderInitializer extends AbstractContextLoaderInitializer {
        @Override
        protected WebApplicationContext createRootApplicationContext() {
            StaticWebApplicationContext rootContext = new StaticWebApplicationContext();
            rootContext.registerSingleton(ContextLoaderInitializerTests.BEAN_NAME, ContextLoaderInitializerTests.MyBean.class);
            return rootContext;
        }
    }

    private static class MyBean {}
}

