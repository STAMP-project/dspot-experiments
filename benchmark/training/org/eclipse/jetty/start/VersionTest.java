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


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class VersionTest {
    @Test
    public void testParse() {
        assertParse("1.8.0_45", 1, 8, 0, 45);
        assertParse("1.8.0_45-internal", 1, 8, 0, 45);
        assertParse("1.8.0-debug", 1, 8, 0, (-1));
    }

    @Test
    public void testToShortString() {
        assertToShortString("1.8", "1.8");
        assertToShortString("1.8.0", "1.8.0");
        assertToShortString("1.8.0_3", "1.8.0_3");
        assertToShortString("1.8.0_03", "1.8.0_03");
        assertToShortString("1.8.0_45", "1.8.0_45");
        assertToShortString("1.8.0_45-internal", "1.8.0_45");
        assertToShortString("1.8.0-debug", "1.8.0");
    }

    @Test
    public void testToString() {
        assertToString("1.8");
        assertToString("1.8.0");
        assertToString("1.8.0_0");
        assertToString("1.8.0_3");
        assertToString("1.8.0_03");
    }

    @Test
    public void testNewerVersion() {
        assertIsNewer("0.0.0", "0.0.1");
        assertIsNewer("0.1.0", "0.1.1");
        assertIsNewer("1.5.0", "1.6.0");
        // assertIsNewer("1.6.0_12", "1.6.0_16"); // JDK version spec?
    }

    @Test
    public void testOlderVersion() {
        assertIsOlder("0.0.1", "0.0.0");
        assertIsOlder("0.1.1", "0.1.0");
        assertIsOlder("1.6.0", "1.5.0");
    }

    @Test
    public void testOlderOrEqualTo() {
        MatcherAssert.assertThat("9.2 <= 9.2", new Version("9.2").isOlderThanOrEqualTo(new Version("9.2")), Matchers.is(true));
        MatcherAssert.assertThat("9.2 <= 9.3", new Version("9.2").isOlderThanOrEqualTo(new Version("9.3")), Matchers.is(true));
        MatcherAssert.assertThat("9.3 <= 9.2", new Version("9.3").isOlderThanOrEqualTo(new Version("9.2")), Matchers.is(false));
    }

    @Test
    public void testNewerOrEqualTo() {
        MatcherAssert.assertThat("9.2 >= 9.2", new Version("9.2").isNewerThanOrEqualTo(new Version("9.2")), Matchers.is(true));
        MatcherAssert.assertThat("9.2 >= 9.3", new Version("9.2").isNewerThanOrEqualTo(new Version("9.3")), Matchers.is(false));
        MatcherAssert.assertThat("9.3 >= 9.2", new Version("9.3").isNewerThanOrEqualTo(new Version("9.2")), Matchers.is(true));
    }
}

