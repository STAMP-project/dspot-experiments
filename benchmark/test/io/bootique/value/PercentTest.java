/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.value;


import org.junit.Assert;
import org.junit.Test;


public class PercentTest {
    @Test
    public void testEquals() {
        Percent p1 = new Percent(1);
        Percent p2 = new Percent(2);
        Percent p3 = new Percent(1);
        Percent p4 = new Percent(1.0);
        Assert.assertTrue(p1.equals(p1));
        Assert.assertFalse(p1.equals(null));
        Assert.assertFalse(p1.equals(p2));
        Assert.assertTrue(p1.equals(p3));
        Assert.assertTrue(p1.equals(p4));
        Assert.assertTrue(p4.equals(p1));
    }

    @Test
    public void testHashCode() {
        Percent p1 = new Percent(1);
        Percent p2 = new Percent(2);
        Percent p3 = new Percent(1);
        Percent p4 = new Percent(1.0);
        Percent p5 = new Percent(1.00000001);
        Assert.assertEquals(p1.hashCode(), p1.hashCode());
        Assert.assertEquals(p1.hashCode(), p3.hashCode());
        Assert.assertEquals(p1.hashCode(), p4.hashCode());
        Assert.assertNotEquals(p1.hashCode(), p2.hashCode());
        Assert.assertNotEquals(p1.hashCode(), p5.hashCode());
    }

    @Test
    public void testCompareTo() {
        Percent p1 = new Percent(1);
        Percent p2 = new Percent(2);
        Percent p4 = new Percent(1.0);
        Assert.assertTrue(((p1.compareTo(p1)) == 0));
        Assert.assertTrue(((p1.compareTo(p2)) < 0));
        Assert.assertTrue(((p2.compareTo(p1)) > 0));
        Assert.assertTrue(((p1.compareTo(p4)) == 0));
    }

    @Test(expected = NullPointerException.class)
    public void testParse_Null() {
        Percent.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParse_Empty() {
        Percent.parse("");
    }

    @Test(expected = NumberFormatException.class)
    public void testParse_NotANumber() {
        Percent.parse("abc%");
    }

    @Test
    public void testParse() {
        Assert.assertEquals(4.0, Percent.parse("4"), 1.0E-4);
        Assert.assertEquals(4.0, Percent.parse("4."), 1.0E-4);
        Assert.assertEquals(4.0, Percent.parse("4%"), 1.0E-4);
        Assert.assertEquals(4.0, Percent.parse("4.0%"), 1.0E-4);
    }

    @Test
    public void testParse_Negative() {
        Assert.assertEquals((-4.0), Percent.parse("-4"), 1.0E-4);
        Assert.assertEquals((-4.0), Percent.parse("-4%"), 1.0E-4);
    }

    @Test
    public void testParse_Zero() {
        Assert.assertEquals(0.0, Percent.parse("0"), 1.0E-4);
        Assert.assertEquals(0.0, Percent.parse("0.0%"), 1.0E-4);
        Assert.assertEquals(0.0, Percent.parse("-0.%"), 1.0E-4);
    }

    @Test
    public void testParse_Hundred() {
        Assert.assertEquals(100.0, Percent.parse("100"), 1.0E-4);
        Assert.assertEquals(100.0, Percent.parse("100.0%"), 1.0E-4);
    }

    @Test
    public void testParse_Large() {
        Assert.assertEquals(10001.0005, Percent.parse("10001.0005"), 1.0E-9);
    }

    @Test
    public void testToString_Precision() {
        Assert.assertEquals("0.000124%", new Percent("0.0001237%").toString(3));
        Assert.assertEquals("1.01%", new Percent("1.0111111%").toString(3));
        Assert.assertEquals("1.012%", new Percent("1.011811%").toString(4));
        Assert.assertEquals("1.0110000%", new Percent("1.011%").toString(8));
        Assert.assertEquals("100.1%", new Percent("100.09%").toString(4));
    }
}

