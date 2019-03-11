/**
 * Copyright 2018 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.resolver.dns;


import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class NameServerComparatorTest {
    private static InetSocketAddress IPV4ADDRESS1;

    private static InetSocketAddress IPV4ADDRESS2;

    private static InetSocketAddress IPV4ADDRESS3;

    private static InetSocketAddress IPV6ADDRESS1;

    private static InetSocketAddress IPV6ADDRESS2;

    private static InetSocketAddress UNRESOLVED1;

    private static InetSocketAddress UNRESOLVED2;

    private static InetSocketAddress UNRESOLVED3;

    @Test
    public void testCompareResolvedOnly() {
        NameServerComparator comparator = new NameServerComparator(Inet4Address.class);
        int x = comparator.compare(NameServerComparatorTest.IPV4ADDRESS1, NameServerComparatorTest.IPV6ADDRESS1);
        int y = comparator.compare(NameServerComparatorTest.IPV6ADDRESS1, NameServerComparatorTest.IPV4ADDRESS1);
        Assert.assertEquals((-1), x);
        Assert.assertEquals(x, (-y));
        Assert.assertEquals(0, comparator.compare(NameServerComparatorTest.IPV4ADDRESS1, NameServerComparatorTest.IPV4ADDRESS1));
        Assert.assertEquals(0, comparator.compare(NameServerComparatorTest.IPV6ADDRESS1, NameServerComparatorTest.IPV6ADDRESS1));
    }

    @Test
    public void testCompareUnresolvedSimple() {
        NameServerComparator comparator = new NameServerComparator(Inet4Address.class);
        int x = comparator.compare(NameServerComparatorTest.IPV4ADDRESS1, NameServerComparatorTest.UNRESOLVED1);
        int y = comparator.compare(NameServerComparatorTest.UNRESOLVED1, NameServerComparatorTest.IPV4ADDRESS1);
        Assert.assertEquals((-1), x);
        Assert.assertEquals(x, (-y));
        Assert.assertEquals(0, comparator.compare(NameServerComparatorTest.IPV4ADDRESS1, NameServerComparatorTest.IPV4ADDRESS1));
        Assert.assertEquals(0, comparator.compare(NameServerComparatorTest.UNRESOLVED1, NameServerComparatorTest.UNRESOLVED1));
    }

    @Test
    public void testCompareUnresolvedOnly() {
        NameServerComparator comparator = new NameServerComparator(Inet4Address.class);
        int x = comparator.compare(NameServerComparatorTest.UNRESOLVED1, NameServerComparatorTest.UNRESOLVED2);
        int y = comparator.compare(NameServerComparatorTest.UNRESOLVED2, NameServerComparatorTest.UNRESOLVED1);
        Assert.assertEquals(0, x);
        Assert.assertEquals(x, (-y));
        Assert.assertEquals(0, comparator.compare(NameServerComparatorTest.UNRESOLVED1, NameServerComparatorTest.UNRESOLVED1));
        Assert.assertEquals(0, comparator.compare(NameServerComparatorTest.UNRESOLVED2, NameServerComparatorTest.UNRESOLVED2));
    }

    @Test
    public void testSortAlreadySortedPreferred() {
        List<InetSocketAddress> expected = Arrays.asList(NameServerComparatorTest.IPV4ADDRESS1, NameServerComparatorTest.IPV4ADDRESS2, NameServerComparatorTest.IPV4ADDRESS3);
        List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(expected);
        NameServerComparator comparator = new NameServerComparator(Inet4Address.class);
        Collections.sort(addresses, comparator);
        Assert.assertEquals(expected, addresses);
    }

    @Test
    public void testSortAlreadySortedNotPreferred() {
        List<InetSocketAddress> expected = Arrays.asList(NameServerComparatorTest.IPV4ADDRESS1, NameServerComparatorTest.IPV4ADDRESS2, NameServerComparatorTest.IPV4ADDRESS3);
        List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(expected);
        NameServerComparator comparator = new NameServerComparator(Inet6Address.class);
        Collections.sort(addresses, comparator);
        Assert.assertEquals(expected, addresses);
    }

    @Test
    public void testSortAlreadySortedUnresolved() {
        List<InetSocketAddress> expected = Arrays.asList(NameServerComparatorTest.UNRESOLVED1, NameServerComparatorTest.UNRESOLVED2, NameServerComparatorTest.UNRESOLVED3);
        List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(expected);
        NameServerComparator comparator = new NameServerComparator(Inet6Address.class);
        Collections.sort(addresses, comparator);
        Assert.assertEquals(expected, addresses);
    }

    @Test
    public void testSortAlreadySortedMixed() {
        List<InetSocketAddress> expected = Arrays.asList(NameServerComparatorTest.IPV4ADDRESS1, NameServerComparatorTest.IPV4ADDRESS2, NameServerComparatorTest.IPV6ADDRESS1, NameServerComparatorTest.IPV6ADDRESS2, NameServerComparatorTest.UNRESOLVED1, NameServerComparatorTest.UNRESOLVED2);
        List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(expected);
        NameServerComparator comparator = new NameServerComparator(Inet4Address.class);
        Collections.sort(addresses, comparator);
        Assert.assertEquals(expected, addresses);
    }

    @Test
    public void testSort1() {
        List<InetSocketAddress> expected = Arrays.asList(NameServerComparatorTest.IPV4ADDRESS1, NameServerComparatorTest.IPV4ADDRESS2, NameServerComparatorTest.IPV6ADDRESS1, NameServerComparatorTest.IPV6ADDRESS2, NameServerComparatorTest.UNRESOLVED1, NameServerComparatorTest.UNRESOLVED2);
        List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(Arrays.asList(NameServerComparatorTest.IPV6ADDRESS1, NameServerComparatorTest.IPV4ADDRESS1, NameServerComparatorTest.IPV6ADDRESS2, NameServerComparatorTest.UNRESOLVED1, NameServerComparatorTest.UNRESOLVED2, NameServerComparatorTest.IPV4ADDRESS2));
        NameServerComparator comparator = new NameServerComparator(Inet4Address.class);
        Collections.sort(addresses, comparator);
        Assert.assertEquals(expected, addresses);
    }

    @Test
    public void testSort2() {
        List<InetSocketAddress> expected = Arrays.asList(NameServerComparatorTest.IPV4ADDRESS1, NameServerComparatorTest.IPV4ADDRESS2, NameServerComparatorTest.IPV6ADDRESS1, NameServerComparatorTest.IPV6ADDRESS2, NameServerComparatorTest.UNRESOLVED1, NameServerComparatorTest.UNRESOLVED2);
        List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(Arrays.asList(NameServerComparatorTest.IPV4ADDRESS1, NameServerComparatorTest.IPV6ADDRESS1, NameServerComparatorTest.IPV6ADDRESS2, NameServerComparatorTest.UNRESOLVED1, NameServerComparatorTest.IPV4ADDRESS2, NameServerComparatorTest.UNRESOLVED2));
        NameServerComparator comparator = new NameServerComparator(Inet4Address.class);
        Collections.sort(addresses, comparator);
        Assert.assertEquals(expected, addresses);
    }
}

