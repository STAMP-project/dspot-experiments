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
package com.gs.collections.impl.memory.map;


import com.carrotsearch.hppc.Containers;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.memory.TestDataFactory;
import gnu.trove.impl.Constants;
import gnu.trove.map.hash.THashMap;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import net.openhft.koloboke.collect.map.hash.HashObjObjMap;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.mutable.AnyRefMap;


public class MapMemoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapMemoryTest.class);

    @Test
    public void memoryForScaledMaps() {
        MapMemoryTest.LOGGER.info("Comparing Items: Scala {}, JDK {}, Trove {}, Trove Presized {}, GSC {}, JDK {}, HPPC {}, Koloboke {}, Scala {}", HashMap.class.getSimpleName(), HashMap.class.getSimpleName(), THashMap.class.getSimpleName(), THashMap.class.getSimpleName(), UnifiedMap.class.getSimpleName(), Hashtable.class.getSimpleName(), ObjectObjectMap.class.getSimpleName(), HashObjObjMap.class.getSimpleName(), AnyRefMap.class.getSimpleName());
        for (int size = 0; size < 1000001; size += 25000) {
            this.memoryForScaledMaps(size);
        }
        MapMemoryTest.LOGGER.info("Ending test: {}", this.getClass().getName());
    }

    public abstract static class SizedMapFactory {
        protected final int size;

        protected final float loadFactor;

        protected final ImmutableList<Integer> data;

        protected SizedMapFactory(int size, float loadFactor) {
            this.size = size;
            this.loadFactor = loadFactor;
            this.data = TestDataFactory.createRandomImmutableList(this.size);
        }

        protected <R extends Map<Integer, String>> R fill(final R map) {
            this.data.forEach(new com.gs.collections.api.block.procedure.Procedure<Integer>() {
                public void value(Integer each) {
                    map.put(each, "dummy");
                }
            });
            return map;
        }
    }

    private static final class ScalaHashMapFactory extends MapMemoryTest.SizedMapFactory implements Function0<scala.collection.mutable.HashMap<Integer, String>> {
        private ScalaHashMapFactory(int size, float loadFactor) {
            super(size, loadFactor);
        }

        @Override
        public scala.collection.mutable.HashMap<Integer, String> value() {
            /**
             *
             *
             * @see HashTable#initialSize()
             */
            int defaultInitialSize = 16;
            final scala.collection.mutable.HashMap<Integer, String> map = new PresizableHashMap(defaultInitialSize, ((int) ((this.loadFactor) * 1000)));
            this.data.forEach(new com.gs.collections.api.block.procedure.Procedure<Integer>() {
                public void value(Integer each) {
                    map.put(each, "dummy");
                }
            });
            return map;
        }
    }

    private static final class HashMapFactory extends MapMemoryTest.SizedMapFactory implements Function0<HashMap<Integer, String>> {
        private HashMapFactory(int size, float loadFactor) {
            super(size, loadFactor);
        }

        @Override
        public HashMap<Integer, String> value() {
            /**
             *
             *
             * @see HashMap#DEFAULT_INITIAL_CAPACITY
             */
            int defaultInitialCapacity = 16;
            HashMap<Integer, String> map = new HashMap<>(defaultInitialCapacity, this.loadFactor);
            return this.fill(map);
        }
    }

    private static final class THashMapFactory extends MapMemoryTest.SizedMapFactory implements Function0<THashMap<Integer, String>> {
        private THashMapFactory(int size, float loadFactor) {
            super(size, loadFactor);
        }

        @Override
        public THashMap<Integer, String> value() {
            return this.fill(new THashMap<Integer, String>(Constants.DEFAULT_CAPACITY, this.loadFactor));
        }
    }

    private static final class PresizedTHashMapFactory extends MapMemoryTest.SizedMapFactory implements Function0<THashMap<Integer, String>> {
        private PresizedTHashMapFactory(int size, float loadFactor) {
            super(size, loadFactor);
        }

        @Override
        public THashMap<Integer, String> value() {
            return this.fill(new THashMap<Integer, String>(this.size, this.loadFactor));
        }
    }

    private static final class HashtableFactory extends MapMemoryTest.SizedMapFactory implements Function0<Hashtable<Integer, String>> {
        private HashtableFactory(int size, float loadFactor) {
            super(size, loadFactor);
        }

        @Override
        @SuppressWarnings("UseOfObsoleteCollectionType")
        public Hashtable<Integer, String> value() {
            /**
             *
             *
             * @see Hashtable#Hashtable()
             */
            int defaultSize = 11;
            Hashtable<Integer, String> map = new Hashtable<>(defaultSize, this.loadFactor);
            return this.fill(map);
        }
    }

    private static final class UnifiedMapFactory extends MapMemoryTest.SizedMapFactory implements Function0<UnifiedMap<Integer, String>> {
        private UnifiedMapFactory(int size, float loadFactor) {
            super(size, loadFactor);
        }

        @Override
        public UnifiedMap<Integer, String> value() {
            /**
             *
             *
             * @see UnifiedMap#DEFAULT_INITIAL_CAPACITY
             */
            int defaultSize = 8;
            UnifiedMap<Integer, String> map = new UnifiedMap<Integer, String>(defaultSize, this.loadFactor);
            return this.fill(map);
        }
    }

    private static final class HppcMapFactory extends MapMemoryTest.SizedMapFactory implements Function0<ObjectObjectMap<Integer, String>> {
        private HppcMapFactory(int size, float loadFactor) {
            super(size, loadFactor);
        }

        @Override
        public ObjectObjectMap<Integer, String> value() {
            final ObjectObjectMap<Integer, String> map = new com.carrotsearch.hppc.ObjectObjectHashMap(Containers.DEFAULT_EXPECTED_ELEMENTS, this.loadFactor);
            this.data.forEach(new com.gs.collections.api.block.procedure.Procedure<Integer>() {
                public void value(Integer each) {
                    map.put(each, "dummy");
                }
            });
            return map;
        }
    }

    private static final class KolobokeMapFactory extends MapMemoryTest.SizedMapFactory implements Function0<HashObjObjMap<Integer, String>> {
        private KolobokeMapFactory(int size) {
            super(size, 0.0F);
        }

        @Override
        public HashObjObjMap<Integer, String> value() {
            return this.fill(HashObjObjMaps.<Integer, String>newMutableMap());
        }
    }

    private static final class ScalaAnyRefMapFactory extends MapMemoryTest.SizedMapFactory implements Function0<AnyRefMap<Integer, String>> {
        private ScalaAnyRefMapFactory(int size) {
            super(size, 0.0F);
        }

        @Override
        public AnyRefMap<Integer, String> value() {
            final AnyRefMap<Integer, String> map = new AnyRefMap();
            this.data.forEach(new com.gs.collections.api.block.procedure.Procedure<Integer>() {
                public void value(Integer each) {
                    map.put(each, "dummy");
                }
            });
            return map;
        }
    }
}

