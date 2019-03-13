/**
 * Copyright 2015 Goldman Sachs.
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
package com.gs.collections.impl.test;


import Lists.mutable;
import Maps.immutable;
import com.gs.collections.api.multimap.list.MutableListMultimap;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.tuple.Tuples;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for our extensions to JUnit.  These tests make sure that methods in {@link Verify} really fail when they
 * ought to.
 */
public class VerifyTest {
    @Test
    public void assertThrowsWithCause() {
        Verify.assertThrowsWithCause(RuntimeException.class, NullPointerException.class, new Callable<Void>() {
            public Void call() throws Exception {
                throw new RuntimeException(new NullPointerException());
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertThrowsWithCause(RuntimeException.class, NullPointerException.class, new Callable<Void>() {
                    public Void call() throws Exception {
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void assertBefore() {
        Verify.assertBefore("numbers", Integer.valueOf(1), Integer.valueOf(2), FastList.newListWith(1, 2));
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertBefore("numbers", Integer.valueOf(2), Integer.valueOf(1), FastList.newListWith(1, 2));
            }
        });
    }

    @Test
    public void assertEndsWithArray() {
        Verify.assertEndsWith(new Integer[]{ 1, 2, 3, 4 }, 3, 4);
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertEndsWith(new Integer[]{ 1, 2, 3, 4 }, 3, 2);
            }
        });
    }

    @Test
    public void assertStartsWithArray() {
        Verify.assertStartsWith(new Integer[]{ 1, 2, 3, 4 }, 1, 2);
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertStartsWith(new Integer[]{ 1, 2, 3, 4 }, 3, 2);
            }
        });
    }

    @Test
    public void assertStartsWithList() {
        Verify.assertStartsWith(FastList.newListWith(1, 2, 3, 4), 1, 2);
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertStartsWith(FastList.newListWith(1, 2, 3, 4), 3, 2);
            }
        });
    }

    @Test
    public void assertEndsWithList() {
        Verify.assertEndsWith(FastList.newListWith(1, 2, 3, 4), 3, 4);
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertEndsWith(FastList.newListWith(1, 2, 3, 4), 3, 2);
            }
        });
    }

    @Test
    public void assertNotEqualsString() {
        Verify.assertNotEquals("yes", "no");
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals("yes", "yes");
            }
        });
    }

    @Test
    public void assertNotEqualsDouble() {
        Verify.assertNotEquals(0.5, 0.6, 1.0E-4);
        Verify.assertNotEquals("message", 0.5, 0.6, 1.0E-4);
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals(0.5, 0.5, 1.0E-4);
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals("message", 0.5, 0.5, 1.0E-4);
            }
        });
    }

    @Test
    public void assertNotEqualsFloat() {
        Verify.assertNotEquals(0.5F, 0.6F, 1.0E-4F);
        Verify.assertNotEquals("message", 0.5F, 0.6F, 1.0E-4F);
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals(0.5F, 0.5F, 1.0E-4F);
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals("message", 0.5F, 0.5F, 1.0E-4F);
            }
        });
    }

    @Test
    public void assertNotEqualsLong() {
        Verify.assertNotEquals(5L, 6L);
        Verify.assertNotEquals("message", 5L, 6L);
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals(5L, 5L);
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals("message", 5L, 5L);
            }
        });
    }

    @Test
    public void assertNotEqualsBoolean() {
        Verify.assertNotEquals(true, false);
        Verify.assertNotEquals("message", true, false);
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals(true, true);
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals("message", true, true);
            }
        });
    }

    @Test
    public void assertNotEqualsByte() {
        Verify.assertNotEquals(((byte) (1)), ((byte) (2)));
        Verify.assertNotEquals("message", ((byte) (1)), ((byte) (2)));
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals(((byte) (1)), ((byte) (1)));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals("message", ((byte) (1)), ((byte) (1)));
            }
        });
    }

    @Test
    public void assertNotEqualsChar() {
        Verify.assertNotEquals(((char) (1)), ((char) (2)));
        Verify.assertNotEquals("message", ((char) (1)), ((char) (2)));
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals(((char) (1)), ((char) (1)));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals("message", ((char) (1)), ((byte) (1)));
            }
        });
    }

    @Test
    public void assertNotEqualsShort() {
        Verify.assertNotEquals(((short) (1)), ((short) (2)));
        Verify.assertNotEquals("message", ((short) (1)), ((short) (2)));
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals(((short) (1)), ((short) (1)));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals("message", ((short) (1)), ((short) (1)));
            }
        });
    }

    @Test
    public void assertNotEqualsInt() {
        Verify.assertNotEquals(1, 2);
        Verify.assertNotEquals("message", 1, 2);
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals(1, 1);
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotEquals("message", 1, 1);
            }
        });
    }

    @Test
    public void assertNotContainsString() {
        Verify.assertNotContains("1", "0");
        Verify.assertNotContains("message", "1", "0");
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotContains("1", "1");
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertNotContains("message", "1", "1");
            }
        });
    }

    @Test
    public void assertListsEqual() {
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2, 3));
        Verify.assertListsEqual("message", FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2, 3));
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertListsEqual(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertListsEqual("message", FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2));
            }
        });
    }

    @Test
    public void assertBagsEqual() {
        Verify.assertBagsEqual(HashBag.newBagWith(1, 1, 2, 2), HashBag.newBagWith(1, 2, 2, 1));
        Verify.assertBagsEqual("message", HashBag.newBagWith(1, 1, 2, 2), HashBag.newBagWith(1, 1, 2, 2));
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertBagsEqual(HashBag.newBagWith(1, 1, 2, 2), HashBag.newBagWith(1, 1, 2, 2, 3, 3));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertBagsEqual("message", HashBag.newBagWith(1, 1, 2, 2, 3, 3), HashBag.newBagWith(1, 1, 2, 2));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertBagsEqual("message", HashBag.newBagWith(1, 1, 2, 2, 4, 4), HashBag.newBagWith(1, 1, 2, 2, 3, 3));
            }
        });
    }

    @Test
    public void assertSetsEqual() {
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSetWith(1, 2, 3));
        Verify.assertSetsEqual("message", UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSetWith(1, 2, 3));
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSetWith(1, 2));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertSetsEqual("message", UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSetWith(1, 2));
            }
        });
    }

    @Test
    public void assertMapsEqual() {
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, 1, 2, 2), UnifiedMap.newWithKeysValues(1, 1, 2, 2));
        Verify.assertMapsEqual("message", UnifiedMap.newWithKeysValues(1, 1, 2, 2), UnifiedMap.newWithKeysValues(1, 1, 2, 2));
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, 1, 2, 2), UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertMapsEqual("message", UnifiedMap.newWithKeysValues(1, 1, 2, 2), UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3));
            }
        });
    }

    @Test
    public void assertIterablesEqual() {
        Verify.assertIterablesEqual(FastList.newListWith(1, 2, 3), TreeSortedSet.newSetWith(1, 2, 3));
        Verify.assertIterablesEqual("message", FastList.newListWith(1, 2, 3), TreeSortedSet.newSetWith(1, 2, 3));
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertIterablesEqual(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertIterablesEqual("message", FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2));
            }
        });
    }

    @Test
    public void assertError() {
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                throw new AssertionError();
            }
        });
        Verify.assertError(AssertionError.class, new Runnable() {
            public void run() {
                Verify.assertError(AssertionError.class, new Runnable() {
                    public void run() {
                        // do nothing
                    }
                });
            }
        });
    }

    @Test
    public void shallowClone1() {
        try {
            Cloneable unclonable = new Cloneable() {};
            Verify.assertShallowClone(unclonable);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void shallowClone2() {
        Cloneable simpleCloneable = new VerifyTest.SimpleCloneable();
        Verify.assertShallowClone(simpleCloneable);
    }

    private static class SimpleCloneable implements Cloneable {
        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            return !((obj == null) || ((this.getClass()) != (obj.getClass())));
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }

    @Test
    public void assertNotEquals() {
        Object object = new Object() {
            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };
        Verify.assertNotEquals(object, object);
    }

    @Test
    public void assertNotEqualsFailsOnSameReference() {
        try {
            Object object = new Object();
            Verify.assertNotEquals(object, object);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertNotEqualsFailsOnDifferentReference() {
        try {
            // noinspection CachedNumberConstructorCall,UnnecessaryBoxing
            Integer integer1 = new Integer(12345);
            // noinspection CachedNumberConstructorCall,UnnecessaryBoxing
            Integer integer2 = new Integer(12345);
            Verify.assertNotEquals(integer1, integer2);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertEqualsAndHashCode() {
        try {
            Verify.assertEqualsAndHashCode(new VerifyTest.ConstantHashCode(), new VerifyTest.ConstantHashCode());
            Assert.fail();
        } catch (AssertionError e) {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertEqualsAndHashCode(new VerifyTest.AlwaysEqualWithHashCodeOf(1), new VerifyTest.AlwaysEqualWithHashCodeOf(2));
            Assert.fail();
        } catch (AssertionError e) {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    private static class ConstantHashCode {
        @Override
        public int hashCode() {
            return 1;
        }
    }

    private static final class AlwaysEqualWithHashCodeOf {
        private final int hashcode;

        private AlwaysEqualWithHashCodeOf(int hashcode) {
            this.hashcode = hashcode;
        }

        @Override
        public int hashCode() {
            return this.hashcode;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null;
        }
    }

    @Test
    public void assertContainsAllEntries() {
        try {
            MutableListMultimap<String, Integer> multimap = FastListMultimap.newMultimap(Tuples.pair("one", 1), Tuples.pair("two", 2));
            Verify.assertContainsAllEntries(multimap, "one", 1, "three", 3);
            Assert.fail();
        } catch (AssertionError e) {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsAllEntries_OddArgumentCount() {
        try {
            MutableListMultimap<String, Integer> multimap = FastListMultimap.newMultimap(Tuples.pair("one", 1), Tuples.pair("two", 2));
            Verify.assertContainsAllEntries(multimap, "one", 1, "three");
            Assert.fail();
        } catch (AssertionError e) {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsAll() {
        try {
            Collection<String> list = FastList.newListWith("One", "Two", "Three");
            Verify.assertContainsAll(list, "Foo", "Bar", "Baz");
            Assert.fail();
        } catch (AssertionError e) {
            Verify.assertContains("these items", e.getMessage());
        }
    }

    @Test
    public void assertInstanceOf() {
        try {
            Verify.assertInstanceOf(Integer.class, 1L);
            Assert.fail();
        } catch (AssertionError e) {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertSortedSetsEqual() {
        TreeSortedSet<Integer> integers = TreeSortedSet.newSetWith(Comparators.<Integer>reverseNaturalOrder(), 1, 2, 3, 4);
        Verify.assertSortedSetsEqual(null, null);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(), new TreeSet<Object>());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), new TreeSet<Integer>(FastList.newListWith(1, 2, 3)));
        Verify.assertSortedSetsEqual(new TreeSet<Integer>(integers), integers);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(integers), integers);
        try {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), new TreeSet<Object>(FastList.newListWith()));
            Assert.fail();
        } catch (AssertionError e) {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), integers);
            Assert.fail();
        } catch (AssertionError e) {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.<Integer>reverseNaturalOrder(), 1, 2, 3, 4, 5), integers);
            Assert.fail();
        } catch (AssertionError e) {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.<Integer>reverseNaturalOrder(), 3, 4), integers);
            Assert.fail();
        } catch (AssertionError e) {
            Verify.assertContains(VerifyTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertEmpty() {
        try {
            Verify.assertEmpty(FastList.newListWith("foo"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("actual size:<1>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertEmpty_PrimitiveIterable() {
        try {
            Verify.assertEmpty(IntArrayList.newListWith(1));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("actual size:<1>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertEmpty_Iterable() {
        try {
            Verify.assertIterableEmpty(FastList.newListWith("foo"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("actual size:<1>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertEmpty_Map() {
        try {
            Verify.assertEmpty(UnifiedMap.newWithKeysValues("foo", "bar"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("actual size:<1>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertEmpty_ImmutableMap() {
        try {
            Verify.assertEmpty(immutable.of("foo", "bar"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("actual size:<1>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertEmpty_Multimap() {
        try {
            Verify.assertEmpty(FastListMultimap.newMultimap(Tuples.pair("foo", "1"), Tuples.pair("foo", "2")));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("actual size:<2>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertNotEmpty() {
        try {
            Verify.assertNotEmpty(mutable.of());
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("should be non-empty", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertNotEmpty_PrimitiveIterable() {
        try {
            Verify.assertNotEmpty(new IntArrayList());
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("should be non-empty", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertNotEmpty_Iterable() {
        try {
            Verify.assertIterableNotEmpty(mutable.of());
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("should be non-empty", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertNotEmpty_Map() {
        try {
            Verify.assertNotEmpty(UnifiedMap.newMap());
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("should be non-empty", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertNotEmpty_Multimap() {
        try {
            Verify.assertNotEmpty(FastListMultimap.newMultimap());
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("should be non-empty", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertNotEmpty_Array() {
        Verify.assertNotEmpty(new Object[]{ new Object() });
        try {
            Verify.assertNotEmpty(new Object[0]);
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("items should not be equal", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertSize() {
        try {
            Verify.assertSize(3, FastList.newListWith("foo", "bar"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertCount() {
        try {
            Verify.assertSize(3, FastList.newListWith("foo", "bar"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertSize_Array() {
        try {
            Verify.assertSize(3, new Object[]{ new Object() });
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertSize_Iterable() {
        try {
            Verify.assertIterableSize(3, FastList.newListWith("foo", "bar"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertSize_PrimitiveIterable() {
        try {
            Verify.assertSize(3, IntArrayList.newListWith(1, 2));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertSize_Map() {
        try {
            Verify.assertSize(3, UnifiedMap.newWithKeysValues("foo", "bar"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertSize_Multimap() {
        try {
            Verify.assertSize(3, FastListMultimap.newMultimap());
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertSize_ImmutableMap() {
        try {
            Verify.assertSize(3, immutable.of("foo", "bar"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertSize_ImmutableSet() {
        try {
            Verify.assertSize(3, Sets.immutable.of("foo", "bar"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("Incorrect size", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContains_String() {
        try {
            Verify.assertContains("foo", "bar");
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertAllSatisfy() {
        try {
            Verify.assertAllSatisfy(FastList.newListWith(1, 3), IntegerPredicates.isEven());
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("failed to satisfy the condition", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertAllSatisfy_Map() {
        try {
            Verify.assertAllSatisfy(((Map<?, Integer>) (UnifiedMap.newWithKeysValues(1, 1, 3, 3))), IntegerPredicates.isEven());
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("failed to satisfy the condition", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertNoneSatisfy() {
        try {
            Verify.assertNoneSatisfy(FastList.newListWith(1, 3), IntegerPredicates.isOdd());
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("satisfied the condition", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertNoneSatisfy_Map() {
        try {
            Verify.assertNoneSatisfy(((Map<?, Integer>) (UnifiedMap.newWithKeysValues(1, 1, 3, 3))), IntegerPredicates.isOdd());
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("satisfied the condition", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertAnySatisfy() {
        try {
            Verify.assertAnySatisfy(FastList.newListWith(1, 3), IntegerPredicates.isEven());
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("No items satisfied the condition", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertAnySatisfy_Map() {
        try {
            Verify.assertAnySatisfy(((Map<?, Integer>) (UnifiedMap.newWithKeysValues(1, 1, 3, 3))), IntegerPredicates.isEven());
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("No items satisfied the condition", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsAllKeyValues_MissingKeys() {
        try {
            Verify.assertContainsAllKeyValues(UnifiedMap.newWithKeysValues("foo", "bar"), "baz", "quaz");
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsAllKeyValues_MissingValues() {
        try {
            Verify.assertContainsAllKeyValues(UnifiedMap.newWithKeysValues("foo", "bar"), "foo", "quaz");
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("map.valuesView() did not contain these items:<[quaz]>", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsAllKeyValues_OddVarArgCount() {
        try {
            Verify.assertContainsAllKeyValues(UnifiedMap.newWithKeysValues("foo", "bar"), "baz");
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("Odd number of keys and values", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsAllKeyValues_ImmutableMap_MissingKey() {
        try {
            Verify.assertContainsAllKeyValues(immutable.of("foo", "bar"), "baz", "quaz");
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("did not contain these items", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsAllKeyValues_ImmutableMap_MissingValue() {
        try {
            Verify.assertContainsAllKeyValues(immutable.of("foo", "bar"), "foo", "quaz");
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("did not contain these items", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsAllKeyValues_ImmutableMap_OddVarArgCount() {
        try {
            Verify.assertContainsAllKeyValues(immutable.of("foo", "bar"), "baz");
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("Odd number of keys and values", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsNone() {
        try {
            Verify.assertContainsNone(FastList.newListWith("foo", "bar"), "foo", "bar");
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("has an intersection with", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void denyContainsAny() {
        try {
            Verify.denyContainsAny(FastList.newListWith("foo", "bar"), "foo", "bar");
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("has an intersection with", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContains_Collection() {
        try {
            Verify.assertContains("baz", FastList.newListWith("foo", "bar"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContains_ImmutableSet() {
        try {
            Verify.assertContains("bar", Sets.immutable.of("foo"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsEntry() {
        try {
            Verify.assertContainsEntry("foo", "bar", FastListMultimap.newMultimap(Tuples.pair("foo", "baz")));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsKey() {
        try {
            Verify.assertContainsKey("foo", UnifiedMap.newWithKeysValues("foozle", "bar"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsKey_ImmutableMap() {
        try {
            Verify.assertContainsKey("foo", immutable.of("foozle", "bar"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void denyContainsKey() {
        try {
            Verify.denyContainsKey("foo", UnifiedMap.newWithKeysValues("foo", "bar"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("contained unexpected", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsKeyValue_MissingKey() {
        try {
            Verify.assertContainsKeyValue("foo", "bar", UnifiedMap.newWithKeysValues("baz", "quaz"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsKeyValue_MissingValue() {
        try {
            Verify.assertContainsKeyValue("foo", "bar", UnifiedMap.newWithKeysValues("foo", "quaz"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsKeyValue_ImmutableMap_MissingKey() {
        try {
            Verify.assertContainsKeyValue("foo", "bar", immutable.of("baz", "quaz"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertContainsKeyValue_ImmutableMap_MissingValue() {
        try {
            Verify.assertContainsKeyValue("foo", "bar", immutable.of("baz", "quaz"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("did not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertNotContains_Collection() {
        try {
            Verify.assertNotContains("foo", FastList.newListWith("foo"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("should not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertNotContains_Iterable() {
        try {
            Verify.assertNotContains("foo", ((Iterable<?>) (FastList.newListWith("foo"))));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("should not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertNotContainsKey() {
        try {
            Verify.assertNotContainsKey("foo", UnifiedMap.newWithKeysValues("foo", "bar"));
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("should not contain", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }

    @Test
    public void assertClassNonInstantiable() {
        Verify.assertClassNonInstantiable(SerializeTestHelper.class);
        try {
            Verify.assertClassNonInstantiable(VerifyTest.class);
            Assert.fail();
        } catch (AssertionError ex) {
            Verify.assertContains("to be non-instantiable", ex.getMessage());
            Verify.assertContains(VerifyTest.class.getName(), ex.getStackTrace()[0].toString());
        }
    }
}

