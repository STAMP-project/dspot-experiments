/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.servlets;


import DispatcherType.REQUEST;
import DoSFilter.MANAGED_ATTR_INIT_PARAM;
import ServletContextHandler.MANAGED_ATTRIBUTES;
import java.lang.management.ManagementFactory;
import java.util.EnumSet;
import java.util.Set;
import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DoSFilterJMXTest {
    @Test
    public void testDoSFilterJMX() throws Exception {
        Server server = new Server();
        Connector connector = new org.eclipse.jetty.server.ServerConnector(server);
        server.addConnector(connector);
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        DoSFilter filter = new DoSFilter();
        FilterHolder holder = new FilterHolder(filter);
        String name = "dos";
        holder.setName(name);
        holder.setInitParameter(MANAGED_ATTR_INIT_PARAM, "true");
        context.addFilter(holder, "/*", EnumSet.of(REQUEST));
        context.setInitParameter(MANAGED_ATTRIBUTES, name);
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        MBeanContainer mbeanContainer = new MBeanContainer(mbeanServer);
        server.addBean(mbeanContainer);
        server.start();
        String domain = DoSFilter.class.getPackage().getName();
        Set<ObjectName> mbeanNames = mbeanServer.queryNames(ObjectName.getInstance((domain + ":*")), null);
        Assertions.assertEquals(1, mbeanNames.size());
        ObjectName objectName = mbeanNames.iterator().next();
        boolean value = ((Boolean) (mbeanServer.getAttribute(objectName, "enabled")));
        mbeanServer.setAttribute(objectName, new Attribute("enabled", (!value)));
        Assertions.assertEquals((!value), filter.isEnabled());
        String whitelist = ((String) (mbeanServer.getAttribute(objectName, "whitelist")));
        String address = "127.0.0.1";
        Assertions.assertFalse(whitelist.contains(address));
        boolean result = ((Boolean) (mbeanServer.invoke(objectName, "addWhitelistAddress", new Object[]{ address }, new String[]{ String.class.getName() })));
        Assertions.assertTrue(result);
        whitelist = ((String) (mbeanServer.getAttribute(objectName, "whitelist")));
        MatcherAssert.assertThat(whitelist, Matchers.containsString(address));
        result = ((Boolean) (mbeanServer.invoke(objectName, "removeWhitelistAddress", new Object[]{ address }, new String[]{ String.class.getName() })));
        Assertions.assertTrue(result);
        whitelist = ((String) (mbeanServer.getAttribute(objectName, "whitelist")));
        MatcherAssert.assertThat(whitelist, Matchers.not(Matchers.containsString(address)));
        server.stop();
    }
}

