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
package org.eclipse.jetty.client;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ProxyConfigurationTest {
    @Test
    public void testProxyMatchesWithoutIncludesWithoutExcludes() throws Exception {
        HttpProxy proxy = new HttpProxy("host", 0);
        Assertions.assertTrue(proxy.matches(new Origin("http", "any", 0)));
    }

    @Test
    public void testProxyMatchesWithOnlyExcludes() throws Exception {
        HttpProxy proxy = new HttpProxy("host", 0);
        proxy.getExcludedAddresses().add("1.2.3.4:5");
        Assertions.assertTrue(proxy.matches(new Origin("http", "any", 0)));
        Assertions.assertTrue(proxy.matches(new Origin("http", "1.2.3.4", 0)));
        Assertions.assertFalse(proxy.matches(new Origin("http", "1.2.3.4", 5)));
    }

    @Test
    public void testProxyMatchesWithOnlyIncludes() throws Exception {
        HttpProxy proxy = new HttpProxy("host", 0);
        proxy.getIncludedAddresses().add("1.2.3.4:5");
        Assertions.assertFalse(proxy.matches(new Origin("http", "any", 0)));
        Assertions.assertFalse(proxy.matches(new Origin("http", "1.2.3.4", 0)));
        Assertions.assertTrue(proxy.matches(new Origin("http", "1.2.3.4", 5)));
    }

    @Test
    public void testProxyMatchesWithIncludesAndExcludes() throws Exception {
        HttpProxy proxy = new HttpProxy("host", 0);
        proxy.getIncludedAddresses().add("1.2.3.4");
        proxy.getExcludedAddresses().add("1.2.3.4:5");
        Assertions.assertFalse(proxy.matches(new Origin("http", "any", 0)));
        Assertions.assertTrue(proxy.matches(new Origin("http", "1.2.3.4", 0)));
        Assertions.assertFalse(proxy.matches(new Origin("http", "1.2.3.4", 5)));
    }

    @Test
    public void testProxyMatchesWithIncludesAndExcludesIPv6() throws Exception {
        HttpProxy proxy = new HttpProxy("host", 0);
        proxy.getIncludedAddresses().add("[1::2:3:4]");
        proxy.getExcludedAddresses().add("[1::2:3:4]:5");
        Assertions.assertFalse(proxy.matches(new Origin("http", "any", 0)));
        Assertions.assertTrue(proxy.matches(new Origin("http", "[1::2:3:4]", 0)));
        Assertions.assertFalse(proxy.matches(new Origin("http", "[1::2:3:4]", 5)));
    }
}

