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


public class BytesTest {
    @Test
    public void testParse_byte() {
        String checkString = (5 + " ") + (BYTES.getName());
        Assert.assertEquals(checkString, new Bytes("5b").toString());
        Assert.assertEquals(checkString, new Bytes("5 b").toString());
        Assert.assertEquals(checkString, new Bytes("5byte").toString());
        Assert.assertEquals(checkString, new Bytes("5 byte").toString());
        Assert.assertEquals(checkString, new Bytes("5bytes").toString());
        Assert.assertEquals(checkString, new Bytes("5 bytes").toString());
    }

    @Test
    public void testParse_KB() {
        Assert.assertEquals(((5 + " ") + (KB.getName())), new Bytes("5kb").toString());
        Assert.assertEquals(5120, new Bytes("5kb").getBytes());
        Assert.assertEquals(5120, new Bytes("5KB").getBytes());
        Assert.assertEquals(5120, new Bytes("5 kb").getBytes());
        Assert.assertEquals(5120, new Bytes("5kilobyte").getBytes());
        Assert.assertEquals(5120, new Bytes("5 kilobyte").getBytes());
        Assert.assertEquals(5120, new Bytes("5kilobytes").getBytes());
        Assert.assertEquals(5120, new Bytes("5 kilobytes").getBytes());
    }

    @Test
    public void testParse_MB() {
        Assert.assertEquals(((5 + " ") + (MB.getName())), new Bytes("5mb").toString());
        Assert.assertEquals(5242880, new Bytes("5mb").getBytes());
        Assert.assertEquals(5242880, new Bytes("5MB").getBytes());
        Assert.assertEquals(5242880, new Bytes("5 mb").getBytes());
        Assert.assertEquals(5242880, new Bytes("5megabyte").getBytes());
        Assert.assertEquals(5242880, new Bytes("5 megabyte").getBytes());
        Assert.assertEquals(5242880, new Bytes("5megabytes").getBytes());
        Assert.assertEquals(5242880, new Bytes("5 megabytes").getBytes());
    }

    @Test
    public void testParse_GB() {
        Assert.assertEquals(((5 + " ") + (GB.getName())), new Bytes("5gb").toString());
        Assert.assertEquals(5368709120L, new Bytes("5gb").getBytes());
        Assert.assertEquals(5368709120L, new Bytes("5GB").getBytes());
        Assert.assertEquals(5368709120L, new Bytes("5 gb").getBytes());
        Assert.assertEquals(5368709120L, new Bytes("5gigabyte").getBytes());
        Assert.assertEquals(5368709120L, new Bytes("5 gigabyte").getBytes());
        Assert.assertEquals(5368709120L, new Bytes("5gigabytes").getBytes());
        Assert.assertEquals(5368709120L, new Bytes("5 gigabytes").getBytes());
    }

    @Test(expected = NullPointerException.class)
    public void testParse_Null() {
        new Bytes(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParse_Empty() {
        new Bytes("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParse_Invalid1() {
        new Bytes("4 nosuchthing");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParse_Invalid2() {
        new Bytes("not_a_number sec");
    }

    @Test
    public void testCompareTo() {
        Bytes b1 = new Bytes("1b");
        Bytes b2 = new Bytes("2b");
        Bytes b3 = new Bytes("2 bytes");
        Bytes b4 = new Bytes("2kb");
        Bytes b5 = new Bytes("2mb");
        Bytes b6 = new Bytes("2gb");
        Assert.assertTrue(((b1.compareTo(b1)) == 0));
        Assert.assertTrue(((b1.compareTo(b2)) < 0));
        Assert.assertTrue(((b2.compareTo(b1)) > 0));
        Assert.assertTrue(((b2.compareTo(b3)) == 0));
        Assert.assertTrue(((b4.compareTo(b2)) > 0));
        Assert.assertTrue(((b5.compareTo(b4)) > 0));
        Assert.assertTrue(((b6.compareTo(b5)) > 0));
    }

    @Test
    public void testEquals() {
        Bytes b1 = new Bytes("5kb");
        Bytes b2 = new Bytes("5368709120b");
        Bytes b3 = new Bytes("5gb");
        Bytes b4 = new Bytes("5120 b");
        Assert.assertTrue(b1.equals(b4));
        Assert.assertFalse(b2.equals(null));
        Assert.assertTrue(b2.equals(b3));
        Assert.assertFalse(b1.equals(b2));
    }

    @Test
    public void testHashCode() {
        Bytes b1 = new Bytes("5kb");
        Bytes b2 = new Bytes("5368709120b");
        Bytes b3 = new Bytes("5gb");
        Bytes b4 = new Bytes("5120 b");
        Assert.assertEquals(b1.hashCode(), b4.hashCode());
        Assert.assertEquals(b2.hashCode(), b3.hashCode());
        Assert.assertNotEquals(b1.hashCode(), b3.hashCode());
    }

    @Test
    public void testUnitConversion() {
        Assert.assertEquals(5120, new Bytes("5kb").valueOfUnit(BYTES));
        Assert.assertEquals(5, new Bytes("5kb").valueOfUnit(KB));
        Assert.assertEquals(5, new Bytes("5mb").valueOfUnit(MB));
        Assert.assertEquals(5, new Bytes("5120mb").valueOfUnit(GB));
    }
}

