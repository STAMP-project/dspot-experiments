/**
 * Copyright 2018 The Android Open Source Project
 *
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
 */
package androidx.collection;


import java.util.ConcurrentModificationException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class SimpleArrayMapTest {
    @Test
    public void getOrDefaultPrefersStoredValue() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", "1");
        Assert.assertEquals("1", map.getOrDefault("one", "2"));
    }

    @Test
    public void getOrDefaultUsesDefaultWhenAbsent() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        Assert.assertEquals("1", map.getOrDefault("one", "1"));
    }

    @Test
    public void getOrDefaultReturnsNullWhenNullStored() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", null);
        Assert.assertNull(map.getOrDefault("one", "1"));
    }

    @Test
    public void getOrDefaultDoesNotPersistDefault() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.getOrDefault("one", "1");
        Assert.assertFalse(map.containsKey("one"));
    }

    @Test
    public void putIfAbsentDoesNotOverwriteStoredValue() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", "1");
        map.putIfAbsent("one", "2");
        Assert.assertEquals("1", map.get("one"));
    }

    @Test
    public void putIfAbsentReturnsStoredValue() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", "1");
        Assert.assertEquals("1", map.putIfAbsent("one", "2"));
    }

    @Test
    public void putIfAbsentStoresValueWhenAbsent() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.putIfAbsent("one", "2");
        Assert.assertEquals("2", map.get("one"));
    }

    @Test
    public void putIfAbsentReturnsNullWhenAbsent() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        Assert.assertNull(map.putIfAbsent("one", "2"));
    }

    @Test
    public void replaceWhenAbsentDoesNotStore() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        Assert.assertNull(map.replace("one", "1"));
        Assert.assertFalse(map.containsKey("one"));
    }

    @Test
    public void replaceStoresAndReturnsOldValue() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", "1");
        Assert.assertEquals("1", map.replace("one", "2"));
        Assert.assertEquals("2", map.get("one"));
    }

    @Test
    public void replaceStoresAndReturnsNullWhenMappedToNull() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", null);
        Assert.assertNull(map.replace("one", "1"));
        Assert.assertEquals("1", map.get("one"));
    }

    @Test
    public void replaceValueKeyAbsent() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        Assert.assertFalse(map.replace("one", "1", "2"));
        Assert.assertFalse(map.containsKey("one"));
    }

    @Test
    public void replaceValueMismatchDoesNotReplace() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", "1");
        Assert.assertFalse(map.replace("one", "2", "3"));
        Assert.assertEquals("1", map.get("one"));
    }

    @Test
    public void replaceValueMismatchNullDoesNotReplace() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", "1");
        Assert.assertFalse(map.replace("one", null, "2"));
        Assert.assertEquals("1", map.get("one"));
    }

    @Test
    public void replaceValueMatchReplaces() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", "1");
        Assert.assertTrue(map.replace("one", "1", "2"));
        Assert.assertEquals("2", map.get("one"));
    }

    @Test
    public void replaceNullValueMismatchDoesNotReplace() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", null);
        Assert.assertFalse(map.replace("one", "1", "2"));
        Assert.assertNull(map.get("one"));
    }

    @Test
    public void replaceNullValueMatchRemoves() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", null);
        Assert.assertTrue(map.replace("one", null, "1"));
        Assert.assertEquals("1", map.get("one"));
    }

    @Test
    public void removeValueKeyAbsent() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        Assert.assertFalse(map.remove("one", "1"));
    }

    @Test
    public void removeValueMismatchDoesNotRemove() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", "1");
        Assert.assertFalse(map.remove("one", "2"));
        Assert.assertTrue(map.containsKey("one"));
    }

    @Test
    public void removeValueMismatchNullDoesNotRemove() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", "1");
        Assert.assertFalse(map.remove("one", null));
        Assert.assertTrue(map.containsKey("one"));
    }

    @Test
    public void removeValueMatchRemoves() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", "1");
        Assert.assertTrue(map.remove("one", "1"));
        Assert.assertFalse(map.containsKey("one"));
    }

    @Test
    public void removeNullValueMismatchDoesNotRemove() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", null);
        Assert.assertFalse(map.remove("one", "2"));
        Assert.assertTrue(map.containsKey("one"));
    }

    @Test
    public void removeNullValueMatchRemoves() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        map.put("one", null);
        Assert.assertTrue(map.remove("one", null));
        Assert.assertFalse(map.containsKey("one"));
    }

    /**
     * Attempt to generate a ConcurrentModificationException in ArrayMap.
     */
    @Test
    public void testConcurrentModificationException() {
        final SimpleArrayMap<String, String> map = new SimpleArrayMap();
        final AtomicBoolean done = new AtomicBoolean();
        final int TEST_LEN_MS = 5000;
        System.out.println("Starting SimpleArrayMap concurrency test");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (!(done.get())) {
                    try {
                        map.put(String.format(Locale.US, "key %d", (i++)), "B_DONT_DO_THAT");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        // SimpleArrayMap is not thread safe, so lots of concurrent modifications
                        // can still cause data corruption
                        System.err.println("concurrent modification uncaught, causing indexing failure");
                        e.printStackTrace();
                    } catch (ClassCastException e) {
                        // cache corruption should not occur as it is hard to trace and one thread
                        // may corrupt the pool for all threads in the same process.
                        System.err.println("concurrent modification uncaught, causing cache corruption");
                        e.printStackTrace();
                        Assert.fail();
                    } catch (ConcurrentModificationException e) {
                    }
                } 
            }
        }).start();
        for (int i = 0; i < (TEST_LEN_MS / 100); i++) {
            try {
                Thread.sleep(100);
                map.clear();
            } catch (InterruptedException e) {
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("concurrent modification uncaught, causing indexing failure");
            } catch (ClassCastException e) {
                System.err.println("concurrent modification uncaught, causing cache corruption");
                Assert.fail();
            } catch (ConcurrentModificationException e) {
            }
        }
        done.set(true);
    }

    /**
     * Check to make sure the same operations behave as expected in a single thread.
     */
    @Test
    public void testNonConcurrentAccesses() {
        SimpleArrayMap<String, String> map = new SimpleArrayMap();
        for (int i = 0; i < 100000; i++) {
            try {
                map.put(String.format(Locale.US, "key %d", (i++)), "B_DONT_DO_THAT");
                if ((i % 500) == 0) {
                    map.clear();
                }
            } catch (ConcurrentModificationException e) {
                System.err.println("Concurrent modification caught on single thread");
                e.printStackTrace();
                Assert.fail();
            }
        }
    }
}

