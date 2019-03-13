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
package org.eclipse.jetty.test.jmx;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.IO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


@Disabled
public class JmxIT {
    private Server _server;

    private JMXConnector _jmxc;

    private MBeanServerConnection _mbsc;

    private int _httpPort;

    private JMXServiceURL _jmxURL;

    @Test
    public void testBasic() throws Exception {
        URI serverURI = new URI((("http://localhost:" + (String.valueOf(_httpPort))) + "/jmx-webapp/"));
        HttpURLConnection http = ((HttpURLConnection) (serverURI.resolve("ping").toURL().openConnection()));
        try (InputStream inputStream = http.getInputStream()) {
            MatcherAssert.assertThat("http response", http.getResponseCode(), Matchers.is(200));
            String resp = IO.toString(inputStream);
            MatcherAssert.assertThat(resp, Matchers.startsWith("Servlet Pong at "));
        }
    }

    @Test
    public void testObtainRunningServerVersion() throws Exception {
        ObjectName serverName = new ObjectName("org.eclipse.jetty.server:type=server,id=0");
        String version = getStringAttribute(serverName, "version");
        MatcherAssert.assertThat("Version", version, Matchers.startsWith("9.4."));
    }

    @Test
    public void testObtainJmxWebAppState() throws Exception {
        ObjectName webappName = new ObjectName("org.eclipse.jetty.webapp:context=jmx-webapp,type=webappcontext,id=0");
        String contextPath = getStringAttribute(webappName, "contextPath");
        MatcherAssert.assertThat("Context Path", contextPath, Matchers.is("/jmx-webapp"));
        String displayName = getStringAttribute(webappName, "displayName");
        MatcherAssert.assertThat("Display Name", displayName, Matchers.is("Test JMX WebApp"));
    }

    /**
     * Test for directly annotated POJOs in the JMX tree
     */
    @Test
    public void testAccessToCommonComponent() throws Exception {
        ObjectName commonName = new ObjectName("org.eclipse.jetty.test.jmx:type=commoncomponent,context=jmx-webapp,id=0");
        String name = getStringAttribute(commonName, "name");
        MatcherAssert.assertThat("Name", name, Matchers.is("i am common"));
    }

    /**
     * Test for POJO (not annotated) that is supplemented with a MBean that
     * declares the annotations.
     */
    @Test
    public void testAccessToPingerMBean() throws Exception {
        ObjectName pingerName = new ObjectName("org.eclipse.jetty.test.jmx:type=pinger,context=jmx-webapp,id=0");
        // Get initial count
        int count = getIntegerAttribute(pingerName, "count");
        // Operations
        Object val = _mbsc.invoke(pingerName, "ping", null, null);
        MatcherAssert.assertThat("ping() return", val.toString(), Matchers.startsWith("Pong"));
        // Attributes
        MatcherAssert.assertThat("count", getIntegerAttribute(pingerName, "count"), Matchers.is((count + 1)));
    }

    /**
     * Test for POJO (annotated) that is merged with a MBean that
     * declares more annotations.
     */
    @Test
    public void testAccessToEchoerMBean() throws Exception {
        ObjectName echoerName = new ObjectName("org.eclipse.jetty.test.jmx:type=echoer,context=jmx-webapp,id=0");
        // Get initial count
        int count = getIntegerAttribute(echoerName, "count");
        // Operations
        Object val = _mbsc.invoke(echoerName, "echo", new Object[]{ "Its Me" }, new String[]{ String.class.getName() });
        MatcherAssert.assertThat("echo() return", val.toString(), Matchers.is("Its Me"));
        // Attributes
        MatcherAssert.assertThat("count", getIntegerAttribute(echoerName, "count"), Matchers.is((count + 1)));
        MatcherAssert.assertThat("foo", getStringAttribute(echoerName, "foo"), Matchers.is("foo-ish"));
    }
}

