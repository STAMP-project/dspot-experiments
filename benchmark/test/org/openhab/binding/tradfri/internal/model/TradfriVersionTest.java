/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.tradfri.internal.model;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link TradfriVersion}.
 *
 * @author Christoph Weitkamp - Initial contribution
 */
public class TradfriVersionTest {
    private static final int LESS_THAN = -1;

    private static final int EQUAL_TO = 0;

    private static final int GREATER_THAN = 1;

    private static final String VERSION_STRING = "1.2.42";

    private static final TradfriVersion VERSION = new TradfriVersion(TradfriVersionTest.VERSION_STRING);

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentException() throws IllegalArgumentException {
        new TradfriVersion("FAILURE");
    }

    @Test
    public void testParts() {
        Assert.assertEquals(Arrays.asList(1, 2, 42), TradfriVersionTest.VERSION.parts);
    }

    @Test
    public void testCompareToEqualTo() {
        Assert.assertEquals(TradfriVersionTest.EQUAL_TO, TradfriVersionTest.VERSION.compareTo(TradfriVersionTest.VERSION));
        Assert.assertEquals(TradfriVersionTest.EQUAL_TO, TradfriVersionTest.VERSION.compareTo(new TradfriVersion(TradfriVersionTest.VERSION_STRING)));
    }

    @Test
    public void testCompareToLessThan() {
        Assert.assertEquals(TradfriVersionTest.LESS_THAN, TradfriVersionTest.VERSION.compareTo(new TradfriVersion("2")));
        Assert.assertEquals(TradfriVersionTest.LESS_THAN, TradfriVersionTest.VERSION.compareTo(new TradfriVersion("1.3")));
        Assert.assertEquals(TradfriVersionTest.LESS_THAN, TradfriVersionTest.VERSION.compareTo(new TradfriVersion("1.2.50")));
        Assert.assertEquals(TradfriVersionTest.LESS_THAN, TradfriVersionTest.VERSION.compareTo(new TradfriVersion("1.2.42.5")));
    }

    @Test
    public void testCompareToGreaterThan() {
        Assert.assertEquals(TradfriVersionTest.GREATER_THAN, TradfriVersionTest.VERSION.compareTo(new TradfriVersion("1")));
        Assert.assertEquals(TradfriVersionTest.GREATER_THAN, TradfriVersionTest.VERSION.compareTo(new TradfriVersion("1.1")));
        Assert.assertEquals(TradfriVersionTest.GREATER_THAN, TradfriVersionTest.VERSION.compareTo(new TradfriVersion("1.2.30")));
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        Assert.assertTrue(TradfriVersionTest.VERSION.equals(TradfriVersionTest.VERSION));
        Assert.assertTrue(TradfriVersionTest.VERSION.equals(new TradfriVersion(TradfriVersionTest.VERSION_STRING)));
        Assert.assertFalse(TradfriVersionTest.VERSION.equals(((TradfriVersion) (null))));
        Assert.assertFalse(TradfriVersionTest.VERSION.equals(new Integer("1")));
        Assert.assertFalse(TradfriVersionTest.VERSION.equals(new TradfriVersion("1.2.5")));
    }

    @Test
    public void testToString() {
        Assert.assertEquals(TradfriVersionTest.VERSION_STRING, TradfriVersionTest.VERSION.toString());
    }
}

