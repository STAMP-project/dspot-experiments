/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.maps;


import MultiMapChangeHandler.ChangeSet;
import java.util.List;
import org.drools.verifier.core.index.keys.Value;
import org.junit.Assert;
import org.junit.Test;


public class ChangeHandledMultiMapPreExistingDataTest {
    private MultiMap<Value, String, List<String>> map;

    private ChangeSet<Value, String> changeSet;

    private int timesCalled = 0;

    @Test
    public void move() throws Exception {
        map.move(new org.drools.verifier.core.index.keys.Values(new Value("ok")), new org.drools.verifier.core.index.keys.Values(new Value("hello")), "b");
        Assert.assertEquals(1, timesCalled);
        // Check data moved
        Assert.assertEquals(2, map.get(new Value("hello")).size());
        Assert.assertTrue(map.get(new Value("hello")).contains("a"));
        Assert.assertTrue(map.get(new Value("hello")).contains("b"));
        Assert.assertEquals(1, map.get(new Value("ok")).size());
        Assert.assertTrue(map.get(new Value("ok")).contains("c"));
        // Updates should be up to date
        Assert.assertEquals(1, changeSet.getRemoved().get(new Value("ok")).size());
        Assert.assertEquals(1, changeSet.getAdded().get(new Value("hello")).size());
    }

    @Test
    public void testRemove() throws Exception {
        map.remove(new Value("ok"));
        Assert.assertEquals(2, changeSet.getRemoved().get(new Value("ok")).size());
        Assert.assertEquals(1, timesCalled);
    }

    @Test
    public void testRemoveValue() throws Exception {
        map.removeValue(new Value("ok"), "b");
        Assert.assertEquals(1, changeSet.getRemoved().get(new Value("ok")).size());
        Assert.assertTrue(changeSet.getRemoved().get(new Value("ok")).contains("b"));
        Assert.assertEquals(1, timesCalled);
    }

    @Test
    public void testClear() throws Exception {
        map.clear();
        Assert.assertEquals(1, changeSet.getRemoved().get(new Value("hello")).size());
        Assert.assertTrue(changeSet.getRemoved().get(new Value("hello")).contains("a"));
        Assert.assertEquals(2, changeSet.getRemoved().get(new Value("ok")).size());
        Assert.assertTrue(changeSet.getRemoved().get(new Value("ok")).contains("b"));
        Assert.assertTrue(changeSet.getRemoved().get(new Value("ok")).contains("c"));
        Assert.assertEquals(1, timesCalled);
    }

    @Test
    public void testMerge() throws Exception {
        final MultiMap<Value, String, List<String>> other = MultiMapFactory.make();
        other.put(new Value("hello"), "d");
        other.put(new Value("ok"), "e");
        other.put(new Value("newOne"), "f");
        MultiMap.merge(map, other);
        Assert.assertEquals(1, changeSet.getAdded().get(new Value("hello")).size());
        Assert.assertTrue(changeSet.getAdded().get(new Value("hello")).contains("d"));
        Assert.assertEquals(1, changeSet.getAdded().get(new Value("ok")).size());
        Assert.assertTrue(changeSet.getAdded().get(new Value("ok")).contains("e"));
        Assert.assertEquals(1, changeSet.getAdded().get(new Value("newOne")).size());
        Assert.assertTrue(changeSet.getAdded().get(new Value("newOne")).contains("f"));
        Assert.assertEquals(1, timesCalled);
    }
}

