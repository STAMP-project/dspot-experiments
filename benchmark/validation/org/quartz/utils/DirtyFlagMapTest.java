/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.quartz.utils;


import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;


/**
 * Unit test for DirtyFlagMap.  These tests focus on making
 * sure the isDirty flag is set correctly.
 */
public class DirtyFlagMapTest extends TestCase {
    public void testClear() {
        DirtyFlagMap<String, String> dirtyFlagMap = new DirtyFlagMap<String, String>();
        TestCase.assertFalse(dirtyFlagMap.isDirty());
        dirtyFlagMap.clear();
        TestCase.assertFalse(dirtyFlagMap.isDirty());
        dirtyFlagMap.put("X", "Y");
        dirtyFlagMap.clearDirtyFlag();
        dirtyFlagMap.clear();
        TestCase.assertTrue(dirtyFlagMap.isDirty());
    }

    public void testPut() {
        DirtyFlagMap<String, String> dirtyFlagMap = new DirtyFlagMap<String, String>();
        dirtyFlagMap.put("a", "Y");
        TestCase.assertTrue(dirtyFlagMap.isDirty());
    }

    public void testRemove() {
        DirtyFlagMap<String, String> dirtyFlagMap = new DirtyFlagMap<String, String>();
        dirtyFlagMap.put("a", "Y");
        dirtyFlagMap.clearDirtyFlag();
        dirtyFlagMap.remove("b");
        TestCase.assertFalse(dirtyFlagMap.isDirty());
        dirtyFlagMap.remove("a");
        TestCase.assertTrue(dirtyFlagMap.isDirty());
    }

    public void testEntrySetRemove() {
        DirtyFlagMap<String, String> dirtyFlagMap = new DirtyFlagMap<String, String>();
        Set<Map.Entry<String, String>> entrySet = dirtyFlagMap.entrySet();
        dirtyFlagMap.remove("a");
        TestCase.assertFalse(dirtyFlagMap.isDirty());
        dirtyFlagMap.put("a", "Y");
        dirtyFlagMap.clearDirtyFlag();
        entrySet.remove("b");
        TestCase.assertFalse(dirtyFlagMap.isDirty());
        entrySet.remove(entrySet.iterator().next());
        TestCase.assertTrue(dirtyFlagMap.isDirty());
    }

    public void testEntrySetRetainAll() {
        DirtyFlagMap<String, String> dirtyFlagMap = new DirtyFlagMap<String, String>();
        Set<Map.Entry<String, String>> entrySet = dirtyFlagMap.entrySet();
        entrySet.retainAll(Collections.EMPTY_LIST);
        TestCase.assertFalse(dirtyFlagMap.isDirty());
        dirtyFlagMap.put("a", "Y");
        dirtyFlagMap.clearDirtyFlag();
        entrySet.retainAll(Collections.singletonList(entrySet.iterator().next()));
        TestCase.assertFalse(dirtyFlagMap.isDirty());
        entrySet.retainAll(Collections.EMPTY_LIST);
        TestCase.assertTrue(dirtyFlagMap.isDirty());
    }

    public void testEntrySetRemoveAll() {
        DirtyFlagMap<String, String> dirtyFlagMap = new DirtyFlagMap<String, String>();
        Set<Map.Entry<String, String>> entrySet = dirtyFlagMap.entrySet();
        entrySet.removeAll(Collections.EMPTY_LIST);
        TestCase.assertFalse(dirtyFlagMap.isDirty());
        dirtyFlagMap.put("a", "Y");
        dirtyFlagMap.clearDirtyFlag();
        entrySet.removeAll(Collections.EMPTY_LIST);
        TestCase.assertFalse(dirtyFlagMap.isDirty());
        entrySet.removeAll(Collections.singletonList(entrySet.iterator().next()));
        TestCase.assertTrue(dirtyFlagMap.isDirty());
    }

    public void testEntrySetClear() {
        DirtyFlagMap<String, String> dirtyFlagMap = new DirtyFlagMap<String, String>();
        Set<Map.Entry<String, String>> entrySet = dirtyFlagMap.entrySet();
        entrySet.clear();
        TestCase.assertFalse(dirtyFlagMap.isDirty());
        dirtyFlagMap.put("a", "Y");
        dirtyFlagMap.clearDirtyFlag();
        entrySet.clear();
        TestCase.assertTrue(dirtyFlagMap.isDirty());
    }

    public void testKeySetClear() {
        DirtyFlagMap<String, String> dirtyFlagMap = new DirtyFlagMap<String, String>();
        Set<?> keySet = dirtyFlagMap.keySet();
        keySet.clear();
        TestCase.assertFalse(dirtyFlagMap.isDirty());
        dirtyFlagMap.put("a", "Y");
        dirtyFlagMap.clearDirtyFlag();
        keySet.clear();
        TestCase.assertTrue(dirtyFlagMap.isDirty());
        TestCase.assertEquals(0, dirtyFlagMap.size());
    }

    public void testValuesClear() {
        DirtyFlagMap<String, String> dirtyFlagMap = new DirtyFlagMap<String, String>();
        Collection<?> values = dirtyFlagMap.values();
        values.clear();
        TestCase.assertFalse(dirtyFlagMap.isDirty());
        dirtyFlagMap.put("a", "Y");
        dirtyFlagMap.clearDirtyFlag();
        values.clear();
        TestCase.assertTrue(dirtyFlagMap.isDirty());
        TestCase.assertEquals(0, dirtyFlagMap.size());
    }
}

