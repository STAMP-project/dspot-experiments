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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.web.context.WebApplicationContext;


/**
 *
 *
 * @author Sam Brannen
 * @since 3.2.2
 */
@ContextHierarchy(@ContextConfiguration)
public class DispatcherWacRootWacEarTests extends RootWacEarTests {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private String ear;

    @Autowired
    private String root;

    @Autowired
    private String dispatcher;

    @Test
    public void verifyDispatcherWacConfig() {
        ApplicationContext parent = wac.getParent();
        Assert.assertNotNull(parent);
        Assert.assertTrue((parent instanceof WebApplicationContext));
        ApplicationContext grandParent = parent.getParent();
        Assert.assertNotNull(grandParent);
        Assert.assertFalse((grandParent instanceof WebApplicationContext));
        ServletContext dispatcherServletContext = wac.getServletContext();
        Assert.assertNotNull(dispatcherServletContext);
        ServletContext rootServletContext = getServletContext();
        Assert.assertNotNull(rootServletContext);
        Assert.assertSame(dispatcherServletContext, rootServletContext);
        Assert.assertSame(parent, rootServletContext.getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
        Assert.assertSame(parent, dispatcherServletContext.getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
        Assert.assertEquals("ear", ear);
        Assert.assertEquals("root", root);
        Assert.assertEquals("dispatcher", dispatcher);
    }
}

