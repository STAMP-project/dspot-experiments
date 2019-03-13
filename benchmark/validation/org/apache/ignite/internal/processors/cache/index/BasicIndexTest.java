/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.cache.index;


import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.junit.Test;


/**
 * A set of basic tests for caches with indexes.
 */
public class BasicIndexTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private Collection<QueryIndex> indexes = Collections.emptyList();

    /**
     *
     */
    private Integer inlineSize;

    /**
     *
     */
    private boolean isPersistenceEnabled;

    /**
     *
     */
    private int gridCount = 1;

    /**
     *
     */
    @Test
    public void testNoIndexesNoPersistence() throws Exception {
        int[] inlineSizes = new int[]{ 0, 10, 20, 50, 100 };
        for (int i : inlineSizes) {
            log().info(("Checking inlineSize=" + i));
            inlineSize = i;
            startGridsMultiThreaded(gridCount());
            populateCache();
            checkAll();
            stopAllGrids();
        }
    }

    /**
     *
     */
    @Test
    public void testAllIndexesNoPersistence() throws Exception {
        indexes = Arrays.asList(new QueryIndex("keyStr"), new QueryIndex("keyLong"), new QueryIndex("keyPojo"), new QueryIndex("valStr"), new QueryIndex("valLong"), new QueryIndex("valPojo"));
        int[] inlineSizes = new int[]{ 0, 10, 20, 50, 100 };
        for (int i : inlineSizes) {
            log().info(("Checking inlineSize=" + i));
            inlineSize = i;
            startGridsMultiThreaded(gridCount());
            populateCache();
            checkAll();
            stopAllGrids();
        }
    }

    /**
     *
     */
    @Test
    public void testDynamicIndexesNoPersistence() throws Exception {
        int[] inlineSizes = new int[]{ 0, 10, 20, 50, 100 };
        for (int i : inlineSizes) {
            log().info(("Checking inlineSize=" + i));
            inlineSize = i;
            startGridsMultiThreaded(gridCount());
            populateCache();
            createDynamicIndexes("keyStr", "keyLong", "keyPojo", "valStr", "valLong", "valPojo");
            checkAll();
            stopAllGrids();
        }
    }

    /**
     *
     */
    @Test
    public void testNoIndexesWithPersistence() throws Exception {
        isPersistenceEnabled = true;
        int[] inlineSizes = new int[]{ 0, 10, 20, 50, 100 };
        for (int i : inlineSizes) {
            log().info(("Checking inlineSize=" + i));
            inlineSize = i;
            startGridsMultiThreaded(gridCount());
            populateCache();
            checkAll();
            stopAllGrids();
            startGridsMultiThreaded(gridCount());
            checkAll();
            stopAllGrids();
            cleanPersistenceDir();
        }
    }

    /**
     *
     */
    @Test
    public void testAllIndexesWithPersistence() throws Exception {
        indexes = Arrays.asList(new QueryIndex("keyStr"), new QueryIndex("keyLong"), new QueryIndex("keyPojo"), new QueryIndex("valStr"), new QueryIndex("valLong"), new QueryIndex("valPojo"));
        isPersistenceEnabled = true;
        int[] inlineSizes = new int[]{ 0, 10, 20, 50, 100 };
        for (int i : inlineSizes) {
            log().info(("Checking inlineSize=" + i));
            inlineSize = i;
            startGridsMultiThreaded(gridCount());
            populateCache();
            checkAll();
            stopAllGrids();
            startGridsMultiThreaded(gridCount());
            checkAll();
            stopAllGrids();
            cleanPersistenceDir();
        }
    }

    /**
     *
     */
    @Test
    public void testDynamicIndexesWithPersistence() throws Exception {
        isPersistenceEnabled = true;
        int[] inlineSizes = new int[]{ 0, 10, 20, 50, 100 };
        for (int i : inlineSizes) {
            log().info(("Checking inlineSize=" + i));
            inlineSize = i;
            startGridsMultiThreaded(gridCount());
            populateCache();
            createDynamicIndexes("keyStr", "keyLong", "keyPojo", "valStr", "valLong", "valPojo");
            checkAll();
            stopAllGrids();
            startGridsMultiThreaded(gridCount());
            checkAll();
            stopAllGrids();
            cleanPersistenceDir();
        }
    }

    /**
     *
     */
    @Test
    public void testDynamicIndexesDropWithPersistence() throws Exception {
        isPersistenceEnabled = true;
        int[] inlineSizes = new int[]{ 0, 10, 20, 50, 100 };
        for (int i : inlineSizes) {
            log().info(("Checking inlineSize=" + i));
            inlineSize = i;
            startGridsMultiThreaded(gridCount());
            populateCache();
            String[] cols = new String[]{ "keyStr", "keyLong", "keyPojo", "valStr", "valLong", "valPojo" };
            createDynamicIndexes(cols);
            checkAll();
            dropDynamicIndexes(cols);
            checkAll();
            stopAllGrids();
            startGridsMultiThreaded(gridCount());
            checkAll();
            stopAllGrids();
            cleanPersistenceDir();
        }
    }

    /**
     *
     */
    @Test
    public void testNoIndexesWithPersistenceIndexRebuild() throws Exception {
        isPersistenceEnabled = true;
        int[] inlineSizes = new int[]{ 0, 10, 20, 50, 100 };
        for (int i : inlineSizes) {
            log().info(("Checking inlineSize=" + i));
            inlineSize = i;
            startGridsMultiThreaded(gridCount());
            populateCache();
            checkAll();
            List<Path> idxPaths = getIndexBinPaths();
            // Shutdown gracefully to ensure there is a checkpoint with index.bin.
            // Otherwise index.bin rebuilding may not work.
            grid(0).cluster().active(false);
            stopAllGrids();
            idxPaths.forEach(( idxPath) -> assertTrue(U.delete(idxPath)));
            startGridsMultiThreaded(gridCount());
            grid(0).cache(DEFAULT_CACHE_NAME).indexReadyFuture().get();
            checkAll();
            stopAllGrids();
            cleanPersistenceDir();
        }
    }

    /**
     *
     */
    @Test
    public void testAllIndexesWithPersistenceIndexRebuild() throws Exception {
        indexes = Arrays.asList(new QueryIndex("keyStr"), new QueryIndex("keyLong"), new QueryIndex("keyPojo"), new QueryIndex("valStr"), new QueryIndex("valLong"), new QueryIndex("valPojo"));
        isPersistenceEnabled = true;
        int[] inlineSizes = new int[]{ 0, 10, 20, 50, 100 };
        for (int i : inlineSizes) {
            log().info(("Checking inlineSize=" + i));
            inlineSize = i;
            startGridsMultiThreaded(gridCount());
            populateCache();
            checkAll();
            List<Path> idxPaths = getIndexBinPaths();
            // Shutdown gracefully to ensure there is a checkpoint with index.bin.
            // Otherwise index.bin rebuilding may not work.
            grid(0).cluster().active(false);
            stopAllGrids();
            idxPaths.forEach(( idxPath) -> assertTrue(U.delete(idxPath)));
            startGridsMultiThreaded(gridCount());
            grid(0).cache(DEFAULT_CACHE_NAME).indexReadyFuture().get();
            checkAll();
            stopAllGrids();
            cleanPersistenceDir();
        }
    }

    /**
     *
     */
    @Test
    public void testDynamicIndexesWithPersistenceIndexRebuild() throws Exception {
        isPersistenceEnabled = true;
        int[] inlineSizes = new int[]{ 0, 10, 20, 50, 100 };
        for (int i : inlineSizes) {
            log().info(("Checking inlineSize=" + i));
            inlineSize = i;
            startGridsMultiThreaded(gridCount());
            populateCache();
            createDynamicIndexes("keyStr", "keyLong", "keyPojo", "valStr", "valLong", "valPojo");
            checkAll();
            List<Path> idxPaths = getIndexBinPaths();
            // Shutdown gracefully to ensure there is a checkpoint with index.bin.
            // Otherwise index.bin rebuilding may not work.
            grid(0).cluster().active(false);
            stopAllGrids();
            idxPaths.forEach(( idxPath) -> assertTrue(U.delete(idxPath)));
            startGridsMultiThreaded(gridCount());
            grid(0).cache(DEFAULT_CACHE_NAME).indexReadyFuture().get();
            checkAll();
            stopAllGrids();
            cleanPersistenceDir();
        }
    }

    /**
     *
     */
    private static class Key {
        /**
         *
         */
        private String keyStr;

        /**
         *
         */
        private long keyLong;

        /**
         *
         */
        private BasicIndexTest.Pojo keyPojo;

        /**
         *
         */
        private Key(String str, long aLong, BasicIndexTest.Pojo pojo) {
            keyStr = str;
            keyLong = aLong;
            keyPojo = pojo;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            BasicIndexTest.Key key = ((BasicIndexTest.Key) (o));
            return (((keyLong) == (key.keyLong)) && (Objects.equals(keyStr, key.keyStr))) && (Objects.equals(keyPojo, key.keyPojo));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return Objects.hash(keyStr, keyLong, keyPojo);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(BasicIndexTest.Key.class, this);
        }
    }

    /**
     *
     */
    private static class Val {
        /**
         *
         */
        private String valStr;

        /**
         *
         */
        private long valLong;

        /**
         *
         */
        private BasicIndexTest.Pojo valPojo;

        /**
         *
         */
        private Val(String str, long aLong, BasicIndexTest.Pojo pojo) {
            valStr = str;
            valLong = aLong;
            valPojo = pojo;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            BasicIndexTest.Val val = ((BasicIndexTest.Val) (o));
            return (((valLong) == (val.valLong)) && (Objects.equals(valStr, val.valStr))) && (Objects.equals(valPojo, val.valPojo));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return Objects.hash(valStr, valLong, valPojo);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(BasicIndexTest.Val.class, this);
        }
    }

    /**
     *
     */
    private static class Pojo {
        /**
         *
         */
        private long pojoLong;

        /**
         *
         */
        private Pojo(long pojoLong) {
            this.pojoLong = pojoLong;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            BasicIndexTest.Pojo pojo = ((BasicIndexTest.Pojo) (o));
            return (pojoLong) == (pojo.pojoLong);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return Objects.hash(pojoLong);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(BasicIndexTest.Pojo.class, this);
        }
    }
}

