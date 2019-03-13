/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.clustering.marshalling.spi.util;


import DefaultExternalizer.CONCURRENT_HASH_MAP;
import DefaultExternalizer.CONCURRENT_SKIP_LIST_MAP;
import DefaultExternalizer.EMPTY_MAP;
import DefaultExternalizer.EMPTY_NAVIGABLE_MAP;
import DefaultExternalizer.EMPTY_SORTED_MAP;
import DefaultExternalizer.HASH_MAP;
import DefaultExternalizer.LINKED_HASH_MAP;
import DefaultExternalizer.SINGLETON_MAP;
import DefaultExternalizer.TREE_MAP;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link MapExternalizer} externalizers
 *
 * @author Paul Ferraro
 */
public class MapExternalizerTestCase {
    @SuppressWarnings("unchecked")
    @Test
    public void test() throws IOException, ClassNotFoundException {
        Map<Object, Object> basis = Stream.of(1, 2, 3, 4, 5).collect(Collectors.<Integer, Object, Object>toMap(( i) -> i, ( i) -> Integer.toString(i)));
        new org.wildfly.clustering.marshalling.ExternalizerTester(CONCURRENT_HASH_MAP.cast(ConcurrentHashMap.class), MapExternalizerTestCase::assertMapEquals).test(new ConcurrentHashMap(basis));
        new org.wildfly.clustering.marshalling.ExternalizerTester(HASH_MAP.cast(HashMap.class), MapExternalizerTestCase::assertMapEquals).test(new HashMap(basis));
        new org.wildfly.clustering.marshalling.ExternalizerTester(LINKED_HASH_MAP.cast(LinkedHashMap.class), MapExternalizerTestCase::assertLinkedMapEquals).test(new LinkedHashMap(basis));
        LinkedHashMap<Object, Object> accessOrderMap = new LinkedHashMap<>(5, 1, true);
        accessOrderMap.putAll(basis);
        new org.wildfly.clustering.marshalling.ExternalizerTester(LINKED_HASH_MAP.cast(LinkedHashMap.class), MapExternalizerTestCase::assertLinkedMapEquals).test(accessOrderMap);
        new org.wildfly.clustering.marshalling.ExternalizerTester(EMPTY_MAP.cast(Map.class), Assert::assertSame).test(Collections.emptyMap());
        new org.wildfly.clustering.marshalling.ExternalizerTester(EMPTY_NAVIGABLE_MAP.cast(NavigableMap.class), Assert::assertSame).test(Collections.emptyNavigableMap());
        new org.wildfly.clustering.marshalling.ExternalizerTester(EMPTY_SORTED_MAP.cast(SortedMap.class), Assert::assertSame).test(Collections.emptySortedMap());
        new org.wildfly.clustering.marshalling.ExternalizerTester(SINGLETON_MAP.cast(Map.class), MapExternalizerTestCase::assertMapEquals).test(Collections.singletonMap(1, 2));
        new org.wildfly.clustering.marshalling.ExternalizerTester(CONCURRENT_SKIP_LIST_MAP.cast(ConcurrentSkipListMap.class), MapExternalizerTestCase::assertMapEquals).test(new ConcurrentSkipListMap(basis));
        new org.wildfly.clustering.marshalling.ExternalizerTester(TREE_MAP.cast(TreeMap.class), MapExternalizerTestCase::assertMapEquals).test(new TreeMap(basis));
    }
}

