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
package org.eclipse.jetty.start;


import Props.ORIGIN_SYSPROP;
import org.eclipse.jetty.start.Props.Prop;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class PropsTest {
    private static final String FROM_TEST = "(test)";

    @Test
    public void testSystemPropsOnly() {
        Props props = new Props();
        String expected = System.getProperty("java.io.tmpdir");
        MatcherAssert.assertThat("System Property", props.getString("java.io.tmpdir"), Matchers.is(expected));
        Prop prop = props.getProp("java.io.tmpdir");
        assertProp("System Prop", prop, "java.io.tmpdir", expected, ORIGIN_SYSPROP);
    }

    @Test
    public void testBasic() {
        Props props = new Props();
        props.setProperty("name", "jetty", PropsTest.FROM_TEST);
        String prefix = "Basic";
        MatcherAssert.assertThat(prefix, props.getString("name"), Matchers.is("jetty"));
        Prop prop = props.getProp("name");
        assertProp(prefix, prop, "name", "jetty", PropsTest.FROM_TEST);
    }

    @Test
    public void testSimpleExpand() {
        Props props = new Props();
        props.setProperty("name", "jetty", PropsTest.FROM_TEST);
        props.setProperty("version", "9.1", PropsTest.FROM_TEST);
        MatcherAssert.assertThat(props.expand("port=8080"), Matchers.is("port=8080"));
        MatcherAssert.assertThat(props.expand("jdk=${java.version}"), Matchers.is(("jdk=" + (System.getProperty("java.version")))));
        MatcherAssert.assertThat(props.expand("id=${name}-${version}"), Matchers.is("id=jetty-9.1"));
        MatcherAssert.assertThat(props.expand("id=${unknown}-${wibble}"), Matchers.is("id=${unknown}-${wibble}"));
    }

    @Test
    public void testNoExpandDoubleDollar() {
        Props props = new Props();
        props.setProperty("aa", "123", PropsTest.FROM_TEST);
        // Should NOT expand double $$ symbols
        MatcherAssert.assertThat(props.expand("zz=$${aa}"), Matchers.is("zz=${aa}"));
        // Should expand
        MatcherAssert.assertThat(props.expand("zz=${aa}"), Matchers.is("zz=123"));
    }

    @Test
    public void testExpandDeep() {
        Props props = new Props();
        props.setProperty("name", "jetty", PropsTest.FROM_TEST);
        props.setProperty("version", "9.1", PropsTest.FROM_TEST);
        props.setProperty("id", "${name}-${version}", PropsTest.FROM_TEST);
        // Should expand
        MatcherAssert.assertThat(props.expand("server-id=corporate-${id}"), Matchers.is("server-id=corporate-jetty-9.1"));
    }

    @Test
    public void testExpandDouble() {
        Props props = new Props();
        props.setProperty("bar", "apple", PropsTest.FROM_TEST);
        props.setProperty("foo", "foo/${bar}/${bar}-xx", PropsTest.FROM_TEST);
        // Should expand
        MatcherAssert.assertThat(props.expand("foo/${bar}/${bar}-xx"), Matchers.is("foo/apple/apple-xx"));
    }

    @Test
    public void testExpandLoop() {
        Props props = new Props();
        props.setProperty("aa", "${bb}", PropsTest.FROM_TEST);
        props.setProperty("bb", "${cc}", PropsTest.FROM_TEST);
        props.setProperty("cc", "${aa}", PropsTest.FROM_TEST);
        try {
            // Should throw exception
            props.expand("val=${aa}");
            Assertions.fail(("Should have thrown a " + (PropsException.class)));
        } catch (PropsException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.is("Property expansion loop detected: aa -> bb -> cc -> aa"));
        }
    }
}

