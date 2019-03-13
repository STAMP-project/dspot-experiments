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


import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;


public class DurationTest {
    @Test
    public void testParse_Millis() {
        Assert.assertEquals(Duration.ofMillis(4), Duration.parse("4ms"));
        Assert.assertEquals(Duration.ofSeconds(4), Duration.parse("4000   ms"));
    }

    @Test
    public void testParse_Seconds() {
        Assert.assertEquals(Duration.ofSeconds(4), Duration.parse("4sec"));
        Assert.assertEquals(Duration.ofSeconds(4), Duration.parse("4 sec"));
        Assert.assertEquals(Duration.ofSeconds(4), Duration.parse("4s"));
        Assert.assertEquals(Duration.ofSeconds(40), Duration.parse("40 seconds"));
    }

    @Test
    public void testParse_Minutes() {
        Assert.assertEquals(Duration.ofMinutes(4), Duration.parse("4min"));
        Assert.assertEquals(Duration.ofMinutes(1), Duration.parse("1 minute"));
        Assert.assertEquals(Duration.ofHours(1), Duration.parse("60 minutes"));
    }

    @Test
    public void testParse_Hours() {
        Assert.assertEquals(Duration.ofHours(4), Duration.parse("4hours"));
        Assert.assertEquals(Duration.ofMinutes(60), Duration.parse("1 hr"));
        Assert.assertEquals(Duration.ofHours(1), Duration.parse("1 hour"));
        Assert.assertEquals(Duration.ofHours(5), Duration.parse("5 hrs"));
        Assert.assertEquals(Duration.ofHours(5), Duration.parse("5 h"));
    }

    @Test
    public void testParse_Days() {
        Assert.assertEquals(Duration.ofDays(4), Duration.parse("4d"));
        Assert.assertEquals(Duration.ofDays(60), Duration.parse("60 days"));
        Assert.assertEquals(Duration.ofHours(24), Duration.parse("1 day"));
    }

    @Test(expected = NullPointerException.class)
    public void testParse_Null() {
        Duration.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParse_Empty() {
        Duration.parse("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParse_Invalid1() {
        Duration.parse("4 nosuchthing");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParse_Invalid2() {
        Duration.parse("not_a_number sec");
    }

    @Test
    public void testCompareTo() {
        Duration d1 = new Duration("1s");
        Duration d2 = new Duration("2s");
        Duration d3 = new Duration("1 sec");
        Duration d4 = new Duration("1day");
        Assert.assertTrue(((d1.compareTo(d1)) == 0));
        Assert.assertTrue(((d1.compareTo(d2)) < 0));
        Assert.assertTrue(((d2.compareTo(d1)) > 0));
        Assert.assertTrue(((d1.compareTo(d3)) == 0));
        Assert.assertTrue(((d4.compareTo(d1)) > 0));
    }

    @Test
    public void testEquals() {
        Duration d1 = new Duration("1s");
        Duration d2 = new Duration("2s");
        Duration d3 = new Duration("1 sec");
        Duration d4 = new Duration("1000ms");
        Assert.assertTrue(d1.equals(d1));
        Assert.assertFalse(d1.equals(null));
        Assert.assertFalse(d1.equals(d2));
        Assert.assertTrue(d1.equals(d3));
        Assert.assertTrue(d1.equals(d4));
        Assert.assertTrue(d4.equals(d1));
    }

    @Test
    public void testHashCode() {
        Duration d1 = new Duration("1s");
        Duration d2 = new Duration("2s");
        Duration d3 = new Duration("1 sec");
        Duration d4 = new Duration("1000ms");
        Assert.assertEquals(d1.hashCode(), d1.hashCode());
        Assert.assertEquals(d1.hashCode(), d3.hashCode());
        Assert.assertEquals(d1.hashCode(), d4.hashCode());
        Assert.assertNotEquals(d1.hashCode(), d2.hashCode());
    }
}

