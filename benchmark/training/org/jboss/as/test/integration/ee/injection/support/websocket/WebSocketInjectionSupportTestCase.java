/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.ee.injection.support.websocket;


import java.net.URI;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.integration.ee.injection.support.ComponentInterceptor;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Matus Abaffy
 */
@RunWith(Arquillian.class)
public class WebSocketInjectionSupportTestCase {
    @Test
    public void testWebSocketInjectionAndInterception() throws Exception {
        AnnotatedClient.reset();
        AnnotatedEndpoint.reset();
        ComponentInterceptor.resetInterceptions();
        final WebSocketContainer serverContainer = ContainerProvider.getWebSocketContainer();
        serverContainer.connectToServer(AnnotatedClient.class, new URI("ws", "", TestSuiteEnvironment.getServerAddress(), TestSuiteEnvironment.getHttpPort(), "/websocket/websocket/cruel", "", ""));
        Assert.assertEquals("Hello cruel World", AnnotatedClient.getMessage());
        Assert.assertTrue("Client endpoint's injection not correct.", AnnotatedClient.injectionOK);
        Assert.assertTrue("Server endpoint's injection not correct.", AnnotatedEndpoint.injectionOK);
        Assert.assertTrue("PostConstruct method on client endpoint instance not called.", AnnotatedClient.postConstructCalled);
        Assert.assertTrue("PostConstruct method on server endpoint instance not called.", AnnotatedEndpoint.postConstructCalled);
        Assert.assertEquals("AroundConstruct interceptor method not invoked for client endpoint.", "AroundConstructInterceptor#Joe#AnnotatedClient", AnnotatedClient.getName());
        Assert.assertEquals("AroundConstruct interceptor method not invoked for server endpoint.", "AroundConstructInterceptor#Joe#AnnotatedEndpoint", AnnotatedEndpoint.getName());
        Assert.assertEquals(2, ComponentInterceptor.getInterceptions().size());
        Assert.assertEquals("open", ComponentInterceptor.getInterceptions().get(0).getMethodName());
        Assert.assertEquals("message", ComponentInterceptor.getInterceptions().get(1).getMethodName());
    }
}

