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
package org.wildfly.mod_cluster.undertow;


import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.core.ApplicationListeners;
import io.undertow.servlet.core.ManagedListener;
import java.util.Collections;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.jboss.modcluster.container.Context;
import org.jboss.modcluster.container.Host;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class UndertowContextTestCase {
    private final Deployment deployment = Mockito.mock(Deployment.class);

    private final Host host = Mockito.mock(Host.class);

    private final Context context = new UndertowContext(this.deployment, this.host);

    @Test
    public void getHost() {
        Assert.assertSame(this.host, this.context.getHost());
    }

    @Test
    public void getPath() {
        DeploymentInfo info = new DeploymentInfo();
        String expected = "";
        info.setContextPath(expected);
        Mockito.when(this.deployment.getDeploymentInfo()).thenReturn(info);
        String result = this.context.getPath();
        Assert.assertSame(expected, result);
    }

    @Test
    public void isStarted() throws ServletException {
        ServletContext context = Mockito.mock(ServletContext.class);
        ApplicationListeners listeners = new ApplicationListeners(Collections.<ManagedListener>emptyList(), context);
        Mockito.when(this.deployment.getApplicationListeners()).thenReturn(listeners);
        Assert.assertFalse(this.context.isStarted());
        listeners.start();
        Assert.assertTrue(this.context.isStarted());
        listeners.stop();
        Assert.assertFalse(this.context.isStarted());
    }

    @Test
    public void addRequestListener() throws ServletException {
        ServletRequestListener listener = Mockito.mock(ServletRequestListener.class);
        ServletContext context = Mockito.mock(ServletContext.class);
        ServletRequest request = Mockito.mock(ServletRequest.class);
        ApplicationListeners listeners = new ApplicationListeners(Collections.<ManagedListener>emptyList(), context);
        ArgumentCaptor<ServletRequestEvent> event = ArgumentCaptor.forClass(ServletRequestEvent.class);
        Mockito.when(this.deployment.getApplicationListeners()).thenReturn(listeners);
        this.context.addRequestListener(listener);
        listeners.start();
        listeners.requestInitialized(request);
        Mockito.verify(listener).requestInitialized(event.capture());
        Assert.assertSame(request, event.getValue().getServletRequest());
        Assert.assertSame(context, event.getValue().getServletContext());
        event = ArgumentCaptor.forClass(ServletRequestEvent.class);
        listeners.requestDestroyed(request);
        Mockito.verify(listener).requestDestroyed(event.capture());
        Assert.assertSame(request, event.getValue().getServletRequest());
        Assert.assertSame(context, event.getValue().getServletContext());
    }

    @Test
    public void addSessionListener() throws ServletException {
        HttpSessionListener listener = Mockito.mock(HttpSessionListener.class);
        ServletContext context = Mockito.mock(ServletContext.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        ApplicationListeners listeners = new ApplicationListeners(Collections.<ManagedListener>emptyList(), context);
        ArgumentCaptor<HttpSessionEvent> event = ArgumentCaptor.forClass(HttpSessionEvent.class);
        Mockito.when(this.deployment.getApplicationListeners()).thenReturn(listeners);
        this.context.addSessionListener(listener);
        listeners.start();
        listeners.sessionCreated(session);
        Mockito.verify(listener).sessionCreated(event.capture());
        Assert.assertSame(session, event.getValue().getSession());
        event = ArgumentCaptor.forClass(HttpSessionEvent.class);
        listeners.sessionDestroyed(session);
        Mockito.verify(listener).sessionDestroyed(event.capture());
        Assert.assertSame(session, event.getValue().getSession());
    }
}

