/**
 * Copyright 2012-2018 Chronicle Map Contributors
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
package net.openhft.chronicle.map;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.IntConsumer;
import net.openhft.chronicle.hash.Data;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;


public class BiMapTest {
    @Test
    public void biMapTest() throws InterruptedException, ExecutionException {
        BiMapTest.BiMapEntryOperations<Integer, CharSequence> biMapOps1 = new BiMapTest.BiMapEntryOperations<>();
        ChronicleMap<Integer, CharSequence> map1 = ChronicleMapBuilder.of(Integer.class, CharSequence.class).entries(100).actualSegments(1).averageValueSize(10).entryOperations(biMapOps1).mapMethods(new BiMapTest.BiMapMethods()).create();
        BiMapTest.BiMapEntryOperations<CharSequence, Integer> biMapOps2 = new BiMapTest.BiMapEntryOperations<>();
        ChronicleMap<CharSequence, Integer> map2 = ChronicleMapBuilder.of(CharSequence.class, Integer.class).entries(100).actualSegments(1).averageKeySize(10).entryOperations(biMapOps2).mapMethods(new BiMapTest.BiMapMethods()).create();
        biMapOps1.setReverse(map2);
        biMapOps2.setReverse(map1);
        map1.put(1, "1");
        BiMapTest.verifyBiMapConsistent(map1, map2);
        map2.remove("1");
        Assert.assertTrue(map2.isEmpty());
        BiMapTest.verifyBiMapConsistent(map1, map2);
        map1.put(3, "4");
        map2.put("5", 6);
        BiMapTest.verifyBiMapConsistent(map1, map2);
        try (ExternalMapQueryContext<CharSequence, Integer, ?> q = map2.queryContext("4")) {
            q.updateLock().lock();
            q.entry().doRemove();
        }
        try {
            map1.remove(3);
            throw new AssertionError("expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
        try {
            map2.put("4", 6);
            throw new AssertionError("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        map2.put("4", 3);// recover

        BiMapTest.verifyBiMapConsistent(map1, map2);
        map1.clear();
        BiMapTest.verifyBiMapConsistent(map1, map2);
        ForkJoinPool pool = new ForkJoinPool(8);
        try {
            pool.submit(() -> {
                ThreadLocalRandom.current().ints().limit(10000).parallel().forEach(( i) -> {
                    int v = Math.abs((i % 10));
                    if ((i & 1) == 0) {
                        if ((i & 2) == 0) {
                            map1.putIfAbsent(v, ("" + v));
                        } else {
                            map1.remove(v, ("" + v));
                        }
                    } else {
                        if ((i & 2) == 0) {
                            map2.putIfAbsent(("" + v), v);
                        } else {
                            map2.remove(("" + v), v);
                        }
                    }
                });
            }).get();
            BiMapTest.verifyBiMapConsistent(map1, map2);
        } finally {
            pool.shutdownNow();
        }
    }

    enum DualLockSuccess {

        SUCCESS,
        FAIL;}

    static class BiMapMethods<K, V> implements MapMethods<K, V, BiMapTest.DualLockSuccess> {
        @Override
        public void remove(MapQueryContext<K, V, BiMapTest.DualLockSuccess> q, ReturnValue<V> returnValue) {
            while (true) {
                q.updateLock().lock();
                try {
                    MapEntry<K, V> entry = q.entry();
                    if (entry != null) {
                        returnValue.returnValue(entry.value());
                        if ((q.remove(entry)) == (BiMapTest.DualLockSuccess.SUCCESS))
                            return;

                    }
                } finally {
                    q.readLock().unlock();
                }
            } 
        }

        @Override
        public void put(MapQueryContext<K, V, BiMapTest.DualLockSuccess> q, Data<V> value, ReturnValue<V> returnValue) {
            while (true) {
                q.updateLock().lock();
                try {
                    MapEntry<K, V> entry = q.entry();
                    if (entry != null) {
                        throw new IllegalStateException();
                    } else {
                        if ((q.insert(q.absentEntry(), value)) == (BiMapTest.DualLockSuccess.SUCCESS))
                            return;

                    }
                } finally {
                    q.readLock().unlock();
                }
            } 
        }

        @Override
        public void putIfAbsent(MapQueryContext<K, V, BiMapTest.DualLockSuccess> q, Data<V> value, ReturnValue<V> returnValue) {
            while (true) {
                try {
                    if (q.readLock().tryLock()) {
                        MapEntry<?, V> entry = q.entry();
                        if (entry != null) {
                            returnValue.returnValue(entry.value());
                            return;
                        }
                        // Key is absent
                        q.readLock().unlock();
                    }
                    q.updateLock().lock();
                    MapEntry<?, V> entry = q.entry();
                    if (entry != null) {
                        returnValue.returnValue(entry.value());
                        return;
                    }
                    // Key is absent
                    if ((q.insert(q.absentEntry(), value)) == (BiMapTest.DualLockSuccess.SUCCESS))
                        return;

                } finally {
                    q.readLock().unlock();
                }
            } 
        }

        @Override
        public boolean remove(MapQueryContext<K, V, BiMapTest.DualLockSuccess> q, Data<V> value) {
            while (true) {
                q.updateLock().lock();
                MapEntry<K, V> entry = q.entry();
                try {
                    if ((entry != null) && (Data.bytesEquivalent(entry.value(), value))) {
                        if ((q.remove(entry)) == (BiMapTest.DualLockSuccess.SUCCESS)) {
                            return true;
                        } else {
                            // noinspection UnnecessaryContinue
                            continue;
                        }
                    } else {
                        return false;
                    }
                } finally {
                    q.readLock().unlock();
                }
            } 
        }

        @Override
        public void acquireUsing(MapQueryContext<K, V, BiMapTest.DualLockSuccess> q, ReturnValue<V> returnValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replace(MapQueryContext<K, V, BiMapTest.DualLockSuccess> q, Data<V> value, ReturnValue<V> returnValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(MapQueryContext<K, V, BiMapTest.DualLockSuccess> q, Data<V> oldValue, Data<V> newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void compute(MapQueryContext<K, V, BiMapTest.DualLockSuccess> q, BiFunction<? super K, ? super V, ? extends V> remappingFunction, ReturnValue<V> returnValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void merge(MapQueryContext<K, V, BiMapTest.DualLockSuccess> q, Data<V> value, BiFunction<? super V, ? super V, ? extends V> remappingFunction, ReturnValue<V> returnValue) {
            throw new UnsupportedOperationException();
        }
    }

    static class BiMapEntryOperations<K, V> implements MapEntryOperations<K, V, BiMapTest.DualLockSuccess> {
        ChronicleMap<V, K> reverse;

        public void setReverse(ChronicleMap<V, K> reverse) {
            this.reverse = reverse;
        }

        @Override
        public BiMapTest.DualLockSuccess remove(@NotNull
        MapEntry<K, V> entry) {
            try (ExternalMapQueryContext<V, K, ?> rq = reverse.queryContext(entry.value())) {
                if (!(rq.updateLock().tryLock())) {
                    if ((entry.context()) instanceof MapQueryContext)
                        return BiMapTest.DualLockSuccess.FAIL;

                    throw new IllegalStateException(("Concurrent modifications to reverse map " + "during remove during iteration"));
                }
                MapEntry<V, K> reverseEntry = rq.entry();
                if (reverseEntry != null) {
                    entry.doRemove();
                    reverseEntry.doRemove();
                    return BiMapTest.DualLockSuccess.SUCCESS;
                } else {
                    throw new IllegalStateException(((((entry.key()) + " maps to ") + (entry.value())) + ", but in the reverse map this value is absent"));
                }
            }
        }

        @Override
        public BiMapTest.DualLockSuccess replaceValue(@NotNull
        MapEntry<K, V> entry, Data<V> newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BiMapTest.DualLockSuccess insert(@NotNull
        MapAbsentEntry<K, V> absentEntry, Data<V> value) {
            try (ExternalMapQueryContext<V, K, ?> rq = reverse.queryContext(value)) {
                if (!(rq.updateLock().tryLock()))
                    return BiMapTest.DualLockSuccess.FAIL;

                MapAbsentEntry<V, K> reverseAbsentEntry = rq.absentEntry();
                if (reverseAbsentEntry != null) {
                    absentEntry.doInsert(value);
                    reverseAbsentEntry.doInsert(absentEntry.absentKey());
                    return BiMapTest.DualLockSuccess.SUCCESS;
                } else {
                    Data<K> reverseKey = rq.entry().value();
                    if (reverseKey.equals(absentEntry.absentKey())) {
                        // recover
                        absentEntry.doInsert(value);
                        return BiMapTest.DualLockSuccess.SUCCESS;
                    }
                    throw new IllegalArgumentException((((((("Try to associate " + (absentEntry.absentKey())) + " with ") + value) + ", but in the reverse ") + "map this value already maps to ") + reverseKey));
                }
            }
        }
    }
}

