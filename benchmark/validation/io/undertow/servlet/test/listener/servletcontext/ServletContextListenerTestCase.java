/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.servlet.test.listener.servletcontext;


import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.spec.ServletContextImpl;
import io.undertow.testutils.DefaultServer;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class ServletContextListenerTestCase {
    static DeploymentManager manager;

    @Test
    public void testServletContextInitialized() throws IOException {
        Assert.assertNotNull(ServletContextTestListener.servletContextInitializedEvent);
    }

    @Test
    public void testServletContextAttributeListener() throws IOException {
        ServletContextImpl sc = ServletContextListenerTestCase.manager.getDeployment().getServletContext();
        sc.setAttribute("test", "1");
        Assert.assertNotNull(ServletContextTestListener.servletContextAttributeEvent);
        Assert.assertEquals(ServletContextTestListener.servletContextAttributeEvent.getName(), "test");
        Assert.assertEquals(ServletContextTestListener.servletContextAttributeEvent.getValue(), "1");
        sc.setAttribute("test", "2");
        Assert.assertEquals(ServletContextTestListener.servletContextAttributeEvent.getName(), "test");
        Assert.assertEquals(ServletContextTestListener.servletContextAttributeEvent.getValue(), "1");
        sc.setAttribute("test", "3");
        Assert.assertEquals(ServletContextTestListener.servletContextAttributeEvent.getName(), "test");
        Assert.assertEquals(ServletContextTestListener.servletContextAttributeEvent.getValue(), "2");
        sc.removeAttribute("test");
        Assert.assertEquals(ServletContextTestListener.servletContextAttributeEvent.getName(), "test");
        Assert.assertEquals(ServletContextTestListener.servletContextAttributeEvent.getValue(), "3");
    }
}

