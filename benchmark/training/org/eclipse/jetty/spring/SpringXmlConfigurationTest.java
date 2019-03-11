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
package org.eclipse.jetty.spring;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SpringXmlConfigurationTest {
    protected String _configure = "org/eclipse/jetty/spring/configure.xml";

    @Test
    public void testPassedObject() throws Exception {
        TestConfiguration.VALUE = 77;
        URL url = SpringXmlConfigurationTest.class.getClassLoader().getResource(_configure);
        XmlConfiguration configuration = new XmlConfiguration(url);
        Map<String, String> properties = new HashMap<>();
        properties.put("test", "xxx");
        TestConfiguration nested = new TestConfiguration();
        nested.setTestString0("nested");
        configuration.getIdMap().put("nested", nested);
        TestConfiguration tc = new TestConfiguration();
        tc.setTestString0("preconfig");
        tc.setTestInt0(42);
        configuration.getProperties().putAll(properties);
        tc = ((TestConfiguration) (configuration.configure(tc)));
        Assertions.assertEquals("preconfig", tc.getTestString0());
        Assertions.assertEquals(42, tc.getTestInt0());
        Assertions.assertEquals("SetValue", tc.getTestString1());
        Assertions.assertEquals(1, tc.getTestInt1());
        Assertions.assertEquals("nested", tc.getNested().getTestString0());
        Assertions.assertEquals("nested", tc.getNested().getTestString1());
        Assertions.assertEquals("default", tc.getNested().getNested().getTestString0());
        Assertions.assertEquals("deep", tc.getNested().getNested().getTestString1());
        Assertions.assertEquals("deep", ((TestConfiguration) (configuration.getIdMap().get("nestedDeep"))).getTestString1());
        Assertions.assertEquals(2, ((TestConfiguration) (configuration.getIdMap().get("nestedDeep"))).getTestInt2());
        Assertions.assertEquals("xxx", tc.getTestString2());
    }

    @Test
    public void testNewObject() throws Exception {
        final String newDefaultValue = "NEW DEFAULT";
        TestConfiguration.VALUE = 71;
        URL url = SpringXmlConfigurationTest.class.getClassLoader().getResource(_configure);
        final AtomicInteger count = new AtomicInteger(0);
        XmlConfiguration configuration = new XmlConfiguration(url) {
            @Override
            public void initializeDefaults(Object object) {
                super.initializeDefaults(object);
                if (object instanceof TestConfiguration) {
                    count.incrementAndGet();
                    ((TestConfiguration) (object)).setTestString0(newDefaultValue);
                    ((TestConfiguration) (object)).setTestString1("WILL BE OVERRIDDEN");
                }
            }
        };
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("test", "xxx");
        TestConfiguration nested = new TestConfiguration();
        nested.setTestString0("nested");
        configuration.getIdMap().put("nested", nested);
        configuration.getProperties().putAll(properties);
        TestConfiguration tc = ((TestConfiguration) (configuration.configure()));
        Assertions.assertEquals(3, count.get());
        Assertions.assertEquals(newDefaultValue, tc.getTestString0());
        Assertions.assertEquals((-1), tc.getTestInt0());
        Assertions.assertEquals("SetValue", tc.getTestString1());
        Assertions.assertEquals(1, tc.getTestInt1());
        Assertions.assertEquals(newDefaultValue, tc.getNested().getTestString0());
        Assertions.assertEquals("nested", tc.getNested().getTestString1());
        Assertions.assertEquals(newDefaultValue, tc.getNested().getNested().getTestString0());
        Assertions.assertEquals("deep", tc.getNested().getNested().getTestString1());
        Assertions.assertEquals("deep", ((TestConfiguration) (configuration.getIdMap().get("nestedDeep"))).getTestString1());
        Assertions.assertEquals(2, ((TestConfiguration) (configuration.getIdMap().get("nestedDeep"))).getTestInt2());
        Assertions.assertEquals("xxx", tc.getTestString2());
    }

    @Test
    public void testJettyXml() throws Exception {
        URL url = SpringXmlConfigurationTest.class.getClassLoader().getResource("org/eclipse/jetty/spring/jetty.xml");
        XmlConfiguration configuration = new XmlConfiguration(url);
        Server server = ((Server) (configuration.configure()));
        server.dumpStdErr();
    }

    @Test
    public void XmlConfigurationMain() throws Exception {
        XmlConfiguration.main("src/test/resources/org/eclipse/jetty/spring/jetty.xml");
    }
}

