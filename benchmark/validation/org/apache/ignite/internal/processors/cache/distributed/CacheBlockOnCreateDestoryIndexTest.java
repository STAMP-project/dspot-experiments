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
package org.apache.ignite.internal.processors.cache.distributed;


import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.internal.processors.cache.distributed.CacheBlockOnReadAbstractTest.Params;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


/**
 *
 */
public class CacheBlockOnCreateDestoryIndexTest extends GridCommonAbstractTest {
    /**
     *
     */
    private final List<? extends CacheBlockOnReadAbstractTest> tests = Arrays.asList(new CacheBlockOnSingleGetTest() {
        /**
         * {@inheritDoc }
         */
        @Nullable
        @Override
        protected <A extends Annotation> A currentTestAnnotation(Class<A> annotationCls) {
            return CacheBlockOnCreateDestoryIndexTest.this.currentTestAnnotation(annotationCls);
        }
    }, new CacheBlockOnGetAllTest() {
        /**
         * {@inheritDoc }
         */
        @Nullable
        @Override
        protected <A extends Annotation> A currentTestAnnotation(Class<A> annotationCls) {
            return CacheBlockOnCreateDestoryIndexTest.this.currentTestAnnotation(annotationCls);
        }
    }, new CacheBlockOnScanTest() {
        /**
         * {@inheritDoc }
         */
        @Nullable
        @Override
        protected <A extends Annotation> A currentTestAnnotation(Class<A> annotationCls) {
            return CacheBlockOnCreateDestoryIndexTest.this.currentTestAnnotation(annotationCls);
        }
    }, new CacheBlockOnSqlQueryTest() {
        /**
         * {@inheritDoc }
         */
        @Nullable
        @Override
        protected <A extends Annotation> A currentTestAnnotation(Class<A> annotationCls) {
            return CacheBlockOnCreateDestoryIndexTest.this.currentTestAnnotation(annotationCls);
        }
    });

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(0)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testCreateIndexAtomicPartitionedGet() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(0)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testCreateIndexAtomicReplicatedGet() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(0)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testCreateIndexTransactionalPartitionedGet() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(0)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testCreateIndexTransactionalReplicatedGet() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(1)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testCreateIndexAtomicPartitionedGetAll() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(1)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testCreateIndexAtomicReplicatedGetAll() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(1)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testCreateIndexTransactionalPartitionedGetAll() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(1)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testCreateIndexTransactionalReplicatedGetAll() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(2)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testCreateIndexAtomicPartitionedScan() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(2)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testCreateIndexAtomicReplicatedScan() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(2)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testCreateIndexTransactionalPartitionedScan() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(2)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testCreateIndexTransactionalReplicatedScan() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(3)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testCreateIndexAtomicPartitionedSqlQuery() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(3)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testCreateIndexAtomicReplicatedSqlQuery() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(3)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testCreateIndexTransactionalPartitionedSqlQuery() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(3)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testCreateIndexTransactionalReplicatedSqlQuery() throws Exception {
        doTestCreateIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(0)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testDestroyIndexAtomicPartitionedGet() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(0)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testDestroyIndexAtomicReplicatedGet() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(0)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testDestroyIndexTransactionalPartitionedGet() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(0)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testDestroyIndexTransactionalReplicatedGet() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(1)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testDestroyIndexAtomicPartitionedGetAll() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(1)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testDestroyIndexAtomicReplicatedGetAll() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(1)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testDestroyIndexTransactionalPartitionedGetAll() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(1)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testDestroyIndexTransactionalReplicatedGetAll() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(2)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testDestroyIndexAtomicPartitionedScan() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(2)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testDestroyIndexAtomicReplicatedScan() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(2)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testDestroyIndexTransactionalPartitionedScan() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(2)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testDestroyIndexTransactionalReplicatedScan() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(3)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testDestroyIndexAtomicPartitionedSqlQuery() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(3)
    @Params(atomicityMode = CacheAtomicityMode.ATOMIC, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testDestroyIndexAtomicReplicatedSqlQuery() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(3)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.PARTITIONED)
    @Test
    public void testDestroyIndexTransactionalPartitionedSqlQuery() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @CacheBlockOnCreateDestoryIndexTest.TestIndex(3)
    @Params(atomicityMode = CacheAtomicityMode.TRANSACTIONAL, cacheMode = CacheMode.REPLICATED)
    @Test
    public void testDestroyIndexTransactionalReplicatedSqlQuery() throws Exception {
        doTestDestroyIndex();
    }

    /**
     *
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface TestIndex {
        /**
         * Index in {@link CacheBlockOnCreateDestoryIndexTest#tests} list.
         */
        int value();
    }
}

