/**
 * -
 * -\-\-
 * Helios Client
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.common;


import org.junit.Assert;
import org.junit.Test;


public class PomVersionTest {
    @Test
    public void testNormal() {
        PomVersion pv = PomVersion.parse("1.2.9");
        Assert.assertEquals(9, pv.getPatch());
        Assert.assertEquals(2, pv.getMinor());
        Assert.assertEquals(1, pv.getMajor());
        Assert.assertFalse(pv.isSnapshot());
        Assert.assertEquals("1.2.9", pv.toString());
        pv = PomVersion.parse("8.6.5-SNAPSHOT");
        Assert.assertEquals(5, pv.getPatch());
        Assert.assertEquals(6, pv.getMinor());
        Assert.assertEquals(8, pv.getMajor());
        Assert.assertTrue(pv.isSnapshot());
        Assert.assertEquals("8.6.5-SNAPSHOT", pv.toString());
    }

    @Test
    public void testFailures() {
        try {
            PomVersion.parse("1.2");
            Assert.fail("should throw");
        } catch (RuntimeException e) {
            // ok
            Assert.assertTrue("should contain something about format", e.getMessage().contains("format"));
        }
        try {
            PomVersion.parse("1.2.3.4");
            Assert.fail("should throw");
        } catch (RuntimeException e) {
            // ok
            Assert.assertTrue("should contain something about format", e.getMessage().contains("format"));
        }
        try {
            PomVersion.parse("x.y.z");
            Assert.fail("should throw");
        } catch (RuntimeException e) {
            // ok
            Assert.assertTrue("should contain something about must be numbers", e.getMessage().contains("number"));
        }
    }

    @Test
    public void testComparable() {
        final PomVersion v1 = new PomVersion(false, 1, 0, 5);
        final PomVersion v2 = new PomVersion(false, 2, 1, 0);
        Assert.assertTrue(((v1.compareTo(v2)) < 0));
        Assert.assertTrue(((v2.compareTo(v1)) > 0));
    }

    @Test
    public void testComparable_Equals() {
        final PomVersion v1 = new PomVersion(false, 1, 0, 0);
        final PomVersion v2 = new PomVersion(false, 1, 0, 0);
        Assert.assertEquals(0, v1.compareTo(v2));
        Assert.assertEquals(0, v2.compareTo(v1));
    }
}

