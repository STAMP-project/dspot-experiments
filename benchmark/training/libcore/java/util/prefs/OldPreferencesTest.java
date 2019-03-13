/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.util.prefs;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import junit.framework.TestCase;


public final class OldPreferencesTest extends TestCase {
    private static final boolean ENCOURAGE_RACES = false;

    private static final String longKey;

    private static final String longValue;

    static {
        StringBuilder key = new StringBuilder(Preferences.MAX_KEY_LENGTH);
        for (int i = 0; i < (Preferences.MAX_KEY_LENGTH); i++) {
            key.append('a');
        }
        longKey = key.toString();
        StringBuilder value = new StringBuilder(Preferences.MAX_VALUE_LENGTH);
        for (int i = 0; i < (Preferences.MAX_VALUE_LENGTH); i++) {
            value.append('a');
        }
        longValue = value.toString();
    }

    public void testAbstractMethods() throws IOException, BackingStoreException {
        Preferences p = new OldPreferencesTest.MockPreferences();
        p.absolutePath();
        p.childrenNames();
        p.clear();
        p.exportNode(null);
        p.exportSubtree(null);
        p.flush();
        p.get(null, null);
        p.getBoolean(null, false);
        p.getByteArray(null, null);
        p.getFloat(null, 0.1F);
        p.getDouble(null, 0.1);
        p.getInt(null, 1);
        p.getLong(null, 1L);
        p.isUserNode();
        p.keys();
        p.name();
        p.node(null);
        p.nodeExists(null);
        p.parent();
        p.put(null, null);
        p.putBoolean(null, false);
        p.putByteArray(null, null);
        p.putDouble(null, 1);
        p.putFloat(null, 1.0F);
        p.putInt(null, 1);
        p.putLong(null, 1L);
        p.remove(null);
        p.removeNode();
        p.addNodeChangeListener(null);
        p.addPreferenceChangeListener(null);
        p.removeNodeChangeListener(null);
        p.removePreferenceChangeListener(null);
        p.sync();
        p.toString();
    }

    public void testConstructor() {
        OldPreferencesTest.MockPreferences mp = new OldPreferencesTest.MockPreferences();
        TestCase.assertEquals(mp.getClass(), OldPreferencesTest.MockPreferences.class);
    }

    public void testToString() {
        Preferences p1 = Preferences.userNodeForPackage(Preferences.class);
        TestCase.assertNotNull(p1.toString());
        Preferences p2 = Preferences.systemRoot();
        TestCase.assertNotNull(p2.toString());
        Preferences p3 = Preferences.userRoot();
        TestCase.assertNotNull(p3.toString());
    }

    public void testAbsolutePath() {
        Preferences p = Preferences.userNodeForPackage(Preferences.class);
        TestCase.assertEquals("/java/util/prefs", p.absolutePath());
    }

    public void testChildrenNames() throws BackingStoreException {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        Preferences child1 = pref.node("child1");
        pref.node("child2");
        pref.node("child3");
        child1.node("subchild1");
        TestCase.assertSame(pref, child1.parent());
        TestCase.assertEquals(3, pref.childrenNames().length);
        TestCase.assertEquals("child1", pref.childrenNames()[0]);
        TestCase.assertEquals(1, child1.childrenNames().length);
        TestCase.assertEquals("subchild1", child1.childrenNames()[0]);
    }

    public void testClear() throws BackingStoreException {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        pref.put("testClearKey", "testClearValue");
        pref.put("testClearKey1", "testClearValue1");
        TestCase.assertEquals("testClearValue", pref.get("testClearKey", null));
        TestCase.assertEquals("testClearValue1", pref.get("testClearKey1", null));
        pref.clear();
        TestCase.assertNull(pref.get("testClearKey", null));
        TestCase.assertNull(pref.get("testClearKey1", null));
    }

    public void testGet() throws BackingStoreException {
        Preferences root = Preferences.userNodeForPackage(Preferences.class);
        Preferences pref = root.node("mock");
        TestCase.assertNull(pref.get("", null));
        TestCase.assertEquals("default", pref.get("key", "default"));
        TestCase.assertNull(pref.get("key", null));
        pref.put("testGetkey", "value");
        TestCase.assertNull(pref.get("testGetKey", null));
        TestCase.assertEquals("value", pref.get("testGetkey", null));
        try {
            pref.get(null, "abc");
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.get("", "abc");
        pref.get("key", null);
        pref.get("key", "");
        pref.putFloat("floatKey", 1.0F);
        TestCase.assertEquals("1.0", pref.get("floatKey", null));
        pref.removeNode();
        try {
            pref.get("key", "abc");
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            pref.get(null, "abc");
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testGetBoolean() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.getBoolean(null, false);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.put("testGetBooleanKey", "false");
        pref.put("testGetBooleanKey2", "value");
        TestCase.assertFalse(pref.getBoolean("testGetBooleanKey", true));
        TestCase.assertTrue(pref.getBoolean("testGetBooleanKey2", true));
    }

    public void testGetByteArray() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.getByteArray(null, new byte[0]);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        byte[] b64Array = new byte[]{ 89, 87, 74, 106 };// BASE64

        pref.put("testGetByteArrayKey", "abc=");
        pref.put("testGetByteArrayKey2", new String(b64Array));
        pref.put("invalidKey", "<>?");
        TestCase.assertTrue(Arrays.equals(new byte[]{ 105, -73 }, pref.getByteArray("testGetByteArrayKey", new byte[0])));
        TestCase.assertTrue(Arrays.equals(new byte[]{ 'a', 'b', 'c' }, pref.getByteArray("testGetByteArrayKey2", new byte[0])));
        TestCase.assertTrue(Arrays.equals(new byte[0], pref.getByteArray("invalidKey", new byte[0])));
        pref.putByteArray("testGetByteArrayKey3", b64Array);
        pref.putByteArray("testGetByteArrayKey4", "abc".getBytes());
        TestCase.assertTrue(Arrays.equals(b64Array, pref.getByteArray("testGetByteArrayKey3", new byte[0])));
        TestCase.assertTrue(Arrays.equals("abc".getBytes(), pref.getByteArray("testGetByteArrayKey4", new byte[0])));
    }

    public void testGetDouble() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.getDouble(null, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.put("testGetDoubleKey", "1");
        pref.put("testGetDoubleKey2", "value");
        pref.putDouble("testGetDoubleKey3", 1);
        pref.putInt("testGetDoubleKey4", 1);
        TestCase.assertEquals(1.0, pref.getDouble("testGetDoubleKey", 0.0), 0);
        TestCase.assertEquals(0.0, pref.getDouble("testGetDoubleKey2", 0.0), 0);
        TestCase.assertEquals(1.0, pref.getDouble("testGetDoubleKey3", 0.0), 0);
        TestCase.assertEquals(1.0, pref.getDouble("testGetDoubleKey4", 0.0), 0);
    }

    public void testGetFloat() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.getFloat(null, 0.0F);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.put("testGetFloatKey", "1");
        pref.put("testGetFloatKey2", "value");
        TestCase.assertEquals(1.0F, pref.getFloat("testGetFloatKey", 0.0F), 0);
        TestCase.assertEquals(0.0F, pref.getFloat("testGetFloatKey2", 0.0F), 0);
    }

    public void testGetInt() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.getInt(null, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.put("testGetIntKey", "1");
        pref.put("testGetIntKey2", "value");
        TestCase.assertEquals(1, pref.getInt("testGetIntKey", 0));
        TestCase.assertEquals(0, pref.getInt("testGetIntKey2", 0));
    }

    public void testGetLong() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.getLong(null, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.put("testGetLongKey", "1");
        pref.put("testGetLongKey2", "value");
        TestCase.assertEquals(1, pref.getInt("testGetLongKey", 0));
        TestCase.assertEquals(0, pref.getInt("testGetLongKey2", 0));
    }

    public void testIsUserNode() {
        Preferences pref1 = Preferences.userNodeForPackage(Preferences.class);
        TestCase.assertTrue(pref1.isUserNode());
        Preferences pref2 = Preferences.systemNodeForPackage(Preferences.class);
        TestCase.assertFalse(pref2.isUserNode());
    }

    public void testKeys() throws BackingStoreException {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        pref.clear();
        pref.put("key0", "value");
        pref.put("key1", "value1");
        pref.put("key2", "value2");
        pref.put("key3", "value3");
        String[] keys = pref.keys();
        TestCase.assertEquals(4, keys.length);
        for (String key : keys) {
            TestCase.assertEquals(0, key.indexOf("key"));
            TestCase.assertEquals(4, key.length());
        }
    }

    public void testName() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        Preferences child = pref.node("mock");
        TestCase.assertEquals("mock", child.name());
    }

    public void testNode() throws BackingStoreException {
        StringBuilder name = new StringBuilder(Preferences.MAX_NAME_LENGTH);
        for (int i = 0; i < (Preferences.MAX_NAME_LENGTH); i++) {
            name.append('a');
        }
        String longName = name.toString();
        Preferences root = Preferences.userRoot();
        Preferences parent = Preferences.userNodeForPackage(Preferences.class);
        Preferences pref = parent.node("mock");
        try {
            pref.node(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            pref.node("/java/util/prefs/");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            pref.node("/java//util/prefs");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            pref.node((longName + "a"));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertNotNull(pref.node(longName));
        TestCase.assertSame(root, pref.node("/"));
        Preferences prefs = pref.node("/java/util/prefs");
        TestCase.assertSame(prefs, parent);
        TestCase.assertSame(pref, pref.node(""));
    }

    public void testNodeExists() throws BackingStoreException {
        Preferences parent = Preferences.userNodeForPackage(Preferences.class);
        Preferences pref = parent.node("mock");
        try {
            pref.nodeExists(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            pref.nodeExists("/java/util/prefs/");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            pref.nodeExists("/java//util/prefs");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertTrue(pref.nodeExists("/"));
        TestCase.assertTrue(pref.nodeExists("/java/util/prefs"));
        TestCase.assertTrue(pref.nodeExists(""));
        TestCase.assertFalse(pref.nodeExists("child"));
        Preferences grandchild = pref.node("child/grandchild");
        TestCase.assertTrue(pref.nodeExists("child"));
        TestCase.assertTrue(pref.nodeExists("child/grandchild"));
        grandchild.removeNode();
        TestCase.assertTrue(pref.nodeExists("child"));
        TestCase.assertFalse(pref.nodeExists("child/grandchild"));
        TestCase.assertFalse(grandchild.nodeExists(""));
        TestCase.assertFalse(pref.nodeExists("child2/grandchild"));
        pref.node("child2/grandchild");
        TestCase.assertTrue(pref.nodeExists("child2/grandchild"));
    }

    public void testParent() {
        Preferences parent = Preferences.userNodeForPackage(Preferences.class);
        Preferences pref = parent.node("mock");
        TestCase.assertSame(parent, pref.parent());
    }

    public void testPut() throws BackingStoreException {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        pref.put("", "emptyvalue");
        TestCase.assertEquals("emptyvalue", pref.get("", null));
        pref.put("testPutkey", "value1");
        TestCase.assertEquals("value1", pref.get("testPutkey", null));
        pref.put("testPutkey", "value2");
        TestCase.assertEquals("value2", pref.get("testPutkey", null));
        pref.put("", "emptyvalue");
        TestCase.assertEquals("emptyvalue", pref.get("", null));
        try {
            pref.put(null, "value");
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            pref.put("key", null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.put(OldPreferencesTest.longKey, OldPreferencesTest.longValue);
        try {
            pref.put(((OldPreferencesTest.longKey) + 1), OldPreferencesTest.longValue);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            pref.put(OldPreferencesTest.longKey, ((OldPreferencesTest.longValue) + 1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        pref.removeNode();
        try {
            pref.put(OldPreferencesTest.longKey, ((OldPreferencesTest.longValue) + 1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            pref.put(OldPreferencesTest.longKey, OldPreferencesTest.longValue);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testPutBoolean() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.putBoolean(null, false);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.putBoolean(OldPreferencesTest.longKey, false);
        try {
            pref.putBoolean(((OldPreferencesTest.longKey) + "a"), false);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        pref.putBoolean("testPutBooleanKey", false);
        TestCase.assertEquals("false", pref.get("testPutBooleanKey", null));
        TestCase.assertFalse(pref.getBoolean("testPutBooleanKey", true));
    }

    public void testPutDouble() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.putDouble(null, 3);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.putDouble(OldPreferencesTest.longKey, 3);
        try {
            pref.putDouble(((OldPreferencesTest.longKey) + "a"), 3);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        pref.putDouble("testPutDoubleKey", 3);
        TestCase.assertEquals("3.0", pref.get("testPutDoubleKey", null));
        TestCase.assertEquals(3, pref.getDouble("testPutDoubleKey", 0), 0);
    }

    public void testPutFloat() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.putFloat(null, 3.0F);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.putFloat(OldPreferencesTest.longKey, 3.0F);
        try {
            pref.putFloat(((OldPreferencesTest.longKey) + "a"), 3.0F);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        pref.putFloat("testPutFloatKey", 3.0F);
        TestCase.assertEquals("3.0", pref.get("testPutFloatKey", null));
        TestCase.assertEquals(3.0F, pref.getFloat("testPutFloatKey", 0), 0);
    }

    public void testPutInt() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.putInt(null, 3);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.putInt(OldPreferencesTest.longKey, 3);
        try {
            pref.putInt(((OldPreferencesTest.longKey) + "a"), 3);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        pref.putInt("testPutIntKey", 3);
        TestCase.assertEquals("3", pref.get("testPutIntKey", null));
        TestCase.assertEquals(3, pref.getInt("testPutIntKey", 0));
    }

    public void testPutLong() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.putLong(null, 3L);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.putLong(OldPreferencesTest.longKey, 3L);
        try {
            pref.putLong(((OldPreferencesTest.longKey) + "a"), 3L);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        pref.putLong("testPutLongKey", 3L);
        TestCase.assertEquals("3", pref.get("testPutLongKey", null));
        TestCase.assertEquals(3L, pref.getLong("testPutLongKey", 0));
    }

    public void testPutByteArray() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.putByteArray(null, new byte[0]);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            pref.putByteArray("testPutByteArrayKey4", null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.putByteArray(OldPreferencesTest.longKey, new byte[0]);
        try {
            pref.putByteArray(((OldPreferencesTest.longKey) + "a"), new byte[0]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        byte[] longArray = new byte[((int) ((Preferences.MAX_VALUE_LENGTH) * 0.74))];
        byte[] longerArray = new byte[((int) ((Preferences.MAX_VALUE_LENGTH) * 0.75)) + 1];
        pref.putByteArray(OldPreferencesTest.longKey, longArray);
        try {
            pref.putByteArray(OldPreferencesTest.longKey, longerArray);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        pref.putByteArray("testPutByteArrayKey", new byte[0]);
        TestCase.assertEquals("", pref.get("testPutByteArrayKey", null));
        TestCase.assertTrue(Arrays.equals(new byte[0], pref.getByteArray("testPutByteArrayKey", null)));
        pref.putByteArray("testPutByteArrayKey3", new byte[]{ 'a', 'b', 'c' });
        TestCase.assertEquals("YWJj", pref.get("testPutByteArrayKey3", null));
        TestCase.assertTrue(Arrays.equals(new byte[]{ 'a', 'b', 'c' }, pref.getByteArray("testPutByteArrayKey3", null)));
    }

    public void testRemove() throws BackingStoreException {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        pref.remove("key");
        pref.put("key", "value");
        TestCase.assertEquals("value", pref.get("key", null));
        pref.remove("key");
        TestCase.assertNull(pref.get("key", null));
        pref.remove("key");
        try {
            pref.remove(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        pref.removeNode();
        try {
            pref.remove("key");
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testRemoveNode() throws BackingStoreException {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        Preferences child = pref.node("child");
        Preferences child1 = pref.node("child1");
        Preferences grandchild = child.node("grandchild");
        pref.removeNode();
        TestCase.assertFalse(child.nodeExists(""));
        TestCase.assertFalse(child1.nodeExists(""));
        TestCase.assertFalse(grandchild.nodeExists(""));
        TestCase.assertFalse(pref.nodeExists(""));
    }

    public void testAddNodeChangeListener() throws BackingStoreException {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.addNodeChangeListener(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        Preferences child1 = null;
        OldPreferencesTest.MockNodeChangeListener nl = null;
        // To get existed node doesn't create the change event
        try {
            nl = new OldPreferencesTest.MockNodeChangeListener();
            pref.addNodeChangeListener(nl);
            child1 = pref.node("mock1");
            nl.waitForEvent(1);
            TestCase.assertEquals(1, nl.getAdded());
            nl.reset();
            pref.node("mock1");
            // There shouldn't be an event, but wait in case one arrives...
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            TestCase.assertEquals(0, nl.getAdded());
            nl.reset();
        } finally {
            pref.removeNodeChangeListener(nl);
            child1.removeNode();
        }
        // same listener can be added twice, and must be removed twice
        try {
            nl = new OldPreferencesTest.MockNodeChangeListener();
            pref.addNodeChangeListener(nl);
            pref.addNodeChangeListener(nl);
            child1 = pref.node("mock2");
            nl.waitForEvent(2);
            TestCase.assertEquals(2, nl.getAdded());
            nl.reset();
        } finally {
            pref.removeNodeChangeListener(nl);
            pref.removeNodeChangeListener(nl);
            child1.removeNode();
        }
        // test remove event
        try {
            nl = new OldPreferencesTest.MockNodeChangeListener();
            pref.addNodeChangeListener(nl);
            child1 = pref.node("mock3");
            child1.removeNode();
            nl.waitForEvent(2);
            TestCase.assertEquals(1, nl.getRemoved());
            nl.reset();
        } finally {
            pref.removeNodeChangeListener(nl);
        }
        // test remove event with two listeners
        try {
            nl = new OldPreferencesTest.MockNodeChangeListener();
            pref.addNodeChangeListener(nl);
            pref.addNodeChangeListener(nl);
            child1 = pref.node("mock6");
            child1.removeNode();
            nl.waitForEvent(4);
            TestCase.assertEquals(2, nl.getRemoved());
            nl.reset();
        } finally {
            pref.removeNodeChangeListener(nl);
            pref.removeNodeChangeListener(nl);
        }
        // test add/remove indirect children, or remove several children at the
        // same time
        Preferences child3;
        try {
            nl = new OldPreferencesTest.MockNodeChangeListener();
            child1 = pref.node("mock4");
            child1.addNodeChangeListener(nl);
            pref.node("mock4/mock5");
            nl.waitForEvent(1);
            TestCase.assertEquals(1, nl.getAdded());
            nl.reset();
            child3 = pref.node("mock4/mock5/mock6");
            // There shouldn't be an event, but wait in case one arrives...
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            TestCase.assertEquals(0, nl.getAdded());
            nl.reset();
            child3.removeNode();
            // There shouldn't be an event, but wait in case one arrives...
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            TestCase.assertEquals(0, nl.getRemoved());
            nl.reset();
            pref.node("mock4/mock7");
            nl.waitForEvent(1);
            TestCase.assertEquals(1, nl.getAdded());
            nl.reset();
            child1.removeNode();
            nl.waitForEvent(2);
            TestCase.assertEquals(2, nl.getRemoved());
            nl.reset();
        } finally {
            try {
                child1.removeNode();
            } catch (Exception ignored) {
            }
        }
    }

    public void testAddPreferenceChangeListener() throws Exception {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        OldPreferencesTest.MockPreferenceChangeListener pl = null;
        try {
            pref.addPreferenceChangeListener(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // To get existed node doesn't create the change event
        try {
            pl = new OldPreferencesTest.MockPreferenceChangeListener();
            pref.addPreferenceChangeListener(pl);
            pref.putInt("mock1", 123);
            pl.waitForEvent(1);
            TestCase.assertEquals(1, pl.getChanged());
            pref.putLong("long_key", Long.MAX_VALUE);
            pl.waitForEvent(2);
            TestCase.assertEquals(2, pl.getChanged());
            pl.reset();
            try {
                pref.clear();
                pl.waitForEvent(2);
                TestCase.assertEquals(2, pl.getChanged());
            } catch (BackingStoreException bse) {
                pl.reset();
                TestCase.fail("BackingStoreException is thrown");
            }
            pl.reset();
        } finally {
            pref.removePreferenceChangeListener(pl);
            // child1.removeNode();
        }
        // same listener can be added twice, and must be removed twice
        try {
            pl = new OldPreferencesTest.MockPreferenceChangeListener();
            pref.addPreferenceChangeListener(pl);
            pref.addPreferenceChangeListener(pl);
            pref.putFloat("float_key", Float.MIN_VALUE);
            pl.waitForEvent(2);
            TestCase.assertEquals(2, pl.getChanged());
            pl.reset();
        } finally {
            pref.removePreferenceChangeListener(pl);
            pref.removePreferenceChangeListener(pl);
        }
        // test remove event
        try {
            pl = new OldPreferencesTest.MockPreferenceChangeListener();
            pref.addPreferenceChangeListener(pl);
            pref.putDouble("double_key", Double.MAX_VALUE);
            pl.waitForEvent(1);
            TestCase.assertEquals(1, pl.getChanged());
            try {
                pref.clear();
                pl.waitForEvent(3);
                TestCase.assertEquals(3, pl.getChanged());
            } catch (BackingStoreException bse) {
                TestCase.fail("BackingStoreException is thrown");
            }
            pl.reset();
        } finally {
            pref.removePreferenceChangeListener(pl);
        }
        // test remove event with two listeners
        try {
            pl = new OldPreferencesTest.MockPreferenceChangeListener();
            pref.addPreferenceChangeListener(pl);
            pref.addPreferenceChangeListener(pl);
            pref.putByteArray("byte_array_key", new byte[]{ 1, 2, 3 });
            try {
                pref.clear();
                pl.waitForEvent(4);
                TestCase.assertEquals(4, pl.getChanged());
            } catch (BackingStoreException bse) {
                TestCase.fail("BackingStoreException is thrown");
            }
            pl.reset();
        } finally {
            pref.removePreferenceChangeListener(pl);
            pref.removePreferenceChangeListener(pl);
        }
    }

    public void testRemoveNodeChangeListener() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.removeNodeChangeListener(null);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        OldPreferencesTest.MockNodeChangeListener l1 = new OldPreferencesTest.MockNodeChangeListener();
        OldPreferencesTest.MockNodeChangeListener l2 = new OldPreferencesTest.MockNodeChangeListener();
        pref.addNodeChangeListener(l1);
        pref.addNodeChangeListener(l1);
        pref.removeNodeChangeListener(l1);
        pref.removeNodeChangeListener(l1);
        try {
            pref.removeNodeChangeListener(l1);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            pref.removeNodeChangeListener(l2);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testRemovePreferenceChangeListener() {
        Preferences pref = Preferences.userNodeForPackage(Preferences.class);
        try {
            pref.removePreferenceChangeListener(null);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        OldPreferencesTest.MockPreferenceChangeListener l1 = new OldPreferencesTest.MockPreferenceChangeListener();
        OldPreferencesTest.MockPreferenceChangeListener l2 = new OldPreferencesTest.MockPreferenceChangeListener();
        pref.addPreferenceChangeListener(l1);
        pref.addPreferenceChangeListener(l1);
        try {
            pref.removePreferenceChangeListener(l2);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        pref.removePreferenceChangeListener(l1);
        pref.removePreferenceChangeListener(l1);
        try {
            pref.removePreferenceChangeListener(l1);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    static class MockNodeChangeListener implements NodeChangeListener {
        private int added = 0;

        private int removed = 0;

        private int eventCount = 0;

        public void waitForEvent(int count) {
            synchronized(this) {
                while ((eventCount) < count) {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {
                    }
                } 
            }
        }

        public void childAdded(NodeChangeEvent e) {
            if (OldPreferencesTest.ENCOURAGE_RACES)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }

            synchronized(this) {
                ++(eventCount);
                ++(added);
                notifyAll();
            }
        }

        public void childRemoved(NodeChangeEvent e) {
            if (OldPreferencesTest.ENCOURAGE_RACES)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }

            synchronized(this) {
                ++(eventCount);
                ++(removed);
                notifyAll();
            }
        }

        public synchronized int getAdded() {
            return added;
        }

        public synchronized int getRemoved() {
            return removed;
        }

        public synchronized void reset() {
            eventCount = 0;
            added = 0;
            removed = 0;
        }
    }

    private static class MockPreferenceChangeListener implements PreferenceChangeListener {
        private int changed = 0;

        private int eventCount = 0;

        public void waitForEvent(int count) {
            synchronized(this) {
                while ((eventCount) < count) {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {
                    }
                } 
            }
        }

        public void preferenceChange(PreferenceChangeEvent pce) {
            if (OldPreferencesTest.ENCOURAGE_RACES)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }

            synchronized(this) {
                ++(eventCount);
                ++(changed);
                notifyAll();
            }
        }

        public synchronized int getChanged() {
            return changed;
        }

        public synchronized void reset() {
            eventCount = 0;
            changed = 0;
        }
    }

    @SuppressWarnings("unused")
    static class MockPreferences extends Preferences {
        @Override
        public String absolutePath() {
            return null;
        }

        @Override
        public String[] childrenNames() throws BackingStoreException {
            return null;
        }

        @Override
        public void clear() throws BackingStoreException {
        }

        @Override
        public void exportNode(OutputStream ostream) throws IOException, BackingStoreException {
        }

        @Override
        public void exportSubtree(OutputStream ostream) throws IOException, BackingStoreException {
        }

        @Override
        public void flush() throws BackingStoreException {
        }

        @Override
        public String get(String key, String deflt) {
            return null;
        }

        @Override
        public boolean getBoolean(String key, boolean deflt) {
            return false;
        }

        @Override
        public byte[] getByteArray(String key, byte[] deflt) {
            return null;
        }

        @Override
        public double getDouble(String key, double deflt) {
            return 0;
        }

        @Override
        public float getFloat(String key, float deflt) {
            return 0;
        }

        @Override
        public int getInt(String key, int deflt) {
            return 0;
        }

        @Override
        public long getLong(String key, long deflt) {
            return 0;
        }

        @Override
        public boolean isUserNode() {
            return false;
        }

        @Override
        public String[] keys() throws BackingStoreException {
            return null;
        }

        @Override
        public String name() {
            return null;
        }

        @Override
        public Preferences node(String name) {
            return null;
        }

        @Override
        public boolean nodeExists(String name) throws BackingStoreException {
            return false;
        }

        @Override
        public Preferences parent() {
            return null;
        }

        @Override
        public void put(String key, String value) {
        }

        @Override
        public void putBoolean(String key, boolean value) {
        }

        @Override
        public void putByteArray(String key, byte[] value) {
        }

        @Override
        public void putDouble(String key, double value) {
        }

        @Override
        public void putFloat(String key, float value) {
        }

        @Override
        public void putInt(String key, int value) {
        }

        @Override
        public void putLong(String key, long value) {
        }

        @Override
        public void remove(String key) {
        }

        @Override
        public void removeNode() throws BackingStoreException {
        }

        @Override
        public void addNodeChangeListener(NodeChangeListener ncl) {
        }

        @Override
        public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        }

        @Override
        public void removeNodeChangeListener(NodeChangeListener ncl) {
        }

        @Override
        public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        }

        @Override
        public void sync() throws BackingStoreException {
        }

        @Override
        public String toString() {
            return null;
        }
    }
}

