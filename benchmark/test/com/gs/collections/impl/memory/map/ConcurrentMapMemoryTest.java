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


import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.impl.map.mutable.ConcurrentHashMapUnsafe;
import com.gs.collections.impl.memory.TestDataFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.concurrent.TrieMap;


public class ConcurrentMapMemoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentMapMemoryTest.class);

    @Test
    public void memoryForScaledConcurrentMaps() {
        ConcurrentMapMemoryTest.LOGGER.info("Comparing Items: JDK {}, GSC {}, Scala {}, GSC {}", ConcurrentHashMap.class.getSimpleName(), ConcurrentHashMap.class.getSimpleName(), TrieMap.class.getSimpleName(), ConcurrentHashMapUnsafe.class.getSimpleName());
        for (int size = 0; size < 1000001; size += 25000) {
            this.memoryForScaledConcurrentMaps(size);
        }
        ConcurrentMapMemoryTest.LOGGER.info("Ending test: {}", this.getClass().getName());
    }

    public abstract static class SizedConcurrentMapFactory {
        protected final ImmutableList<Integer> data;

        protected SizedConcurrentMapFactory(int size) {
            this.data = TestDataFactory.createRandomImmutableList(size);
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

    public static class JDKConcurrentMapFactory extends ConcurrentMapMemoryTest.SizedConcurrentMapFactory implements Function0<ConcurrentHashMap<Integer, String>> {
        protected JDKConcurrentMapFactory(int size) {
            super(size);
        }

        @Override
        public ConcurrentHashMap<Integer, String> value() {
            return this.fill(new ConcurrentHashMap<Integer, String>());
        }
    }

    public static class GSCConcurrentMapFactory extends ConcurrentMapMemoryTest.SizedConcurrentMapFactory implements Function0<com.gs.collections.impl.map.mutable.ConcurrentHashMap<Integer, String>> {
        protected GSCConcurrentMapFactory(int size) {
            super(size);
        }

        @Override
        public com.gs.collections.impl.map.mutable.ConcurrentHashMap<Integer, String> value() {
            return this.fill(new com.gs.collections.impl.map.mutable.ConcurrentHashMap<Integer, String>());
        }
    }

    public static class ScalaCtrieFactory extends ConcurrentMapMemoryTest.SizedConcurrentMapFactory implements Function0<TrieMap<Integer, String>> {
        protected ScalaCtrieFactory(int size) {
            super(size);
        }

        @Override
        public TrieMap<Integer, String> value() {
            final TrieMap<Integer, String> map = new TrieMap<Integer, String>();
            this.data.forEach(new com.gs.collections.api.block.procedure.Procedure<Integer>() {
                public void value(Integer each) {
                    map.put(each, "dummy");
                }
            });
            return map;
        }
    }

    public static class ConcurrentHashMapUnsafeFactory extends ConcurrentMapMemoryTest.SizedConcurrentMapFactory implements Function0<ConcurrentHashMapUnsafe<Integer, String>> {
        protected ConcurrentHashMapUnsafeFactory(int size) {
            super(size);
        }

        @Override
        public ConcurrentHashMapUnsafe<Integer, String> value() {
            return this.fill(new ConcurrentHashMapUnsafe<Integer, String>());
        }
    }
}

