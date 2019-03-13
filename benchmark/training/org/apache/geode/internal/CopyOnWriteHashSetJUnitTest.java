/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class CopyOnWriteHashSetJUnitTest {
    @Test
    public void testSnapshot() {
        CopyOnWriteHashSet<String> set = new CopyOnWriteHashSet<String>();
        set.add("a");
        Set<String> snap = set.getSnapshot();
        Set<String> copy = new HashSet<String>(set);
        set.add("b");
        Assert.assertEquals(copy, snap);
    }

    @Test
    public void testAllMethods() throws Exception {
        CopyOnWriteHashSet<String> set = new CopyOnWriteHashSet<String>();
        Assert.assertTrue(set.add("a"));
        Assert.assertFalse(set.add("a"));
        Iterator itr = set.iterator();
        Assert.assertTrue(itr.hasNext());
        Assert.assertEquals("a", itr.next());
        Assert.assertFalse(itr.hasNext());
        Assert.assertEquals(1, set.size());
        Assert.assertTrue(set.addAll(Arrays.asList(new String[]{ "b", "c", "d" })));
        Assert.assertTrue(set.contains("b"));
        Assert.assertTrue(set.contains("c"));
        Assert.assertTrue(set.contains("d"));
        Assert.assertTrue(set.retainAll(Arrays.asList(new String[]{ "a", "b", "c" })));
        Assert.assertFalse(set.retainAll(Arrays.asList(new String[]{ "a", "b", "c" })));
        HashSet<String> test = new HashSet<String>();
        test.addAll(Arrays.asList(new String[]{ "a", "b", "c" }));
        Assert.assertEquals(test, set);
        Assert.assertEquals(set, test);
        Assert.assertEquals(test.toString(), set.toString());
        Assert.assertEquals(Arrays.asList(test.toArray()), Arrays.asList(set.toArray()));
        Assert.assertEquals(Arrays.asList(test.toArray(new String[0])), Arrays.asList(set.toArray(new String[0])));
        Assert.assertTrue(set.containsAll(test));
        Assert.assertTrue(set.containsAll(test));
        set.remove("b");
        Assert.assertFalse(set.containsAll(test));
        set.clear();
        set.addAll(Arrays.asList(new String[]{ "b", "c", "d" }));
        Assert.assertTrue(set.removeAll(Arrays.asList(new String[]{ "b", "c" })));
        Assert.assertFalse(set.removeAll(Arrays.asList(new String[]{ "b", "c" })));
        Assert.assertEquals(new HashSet(Arrays.asList(new String[]{ "d" })), set);
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(boas);
        out.writeObject(set);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(boas.toByteArray()));
        Set<String> result = ((Set<String>) (in.readObject()));
        Assert.assertEquals(set, result);
        Assert.assertTrue((result instanceof CopyOnWriteHashSet));
    }
}

