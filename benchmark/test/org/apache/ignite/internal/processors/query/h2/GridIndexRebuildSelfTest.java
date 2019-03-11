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
package org.apache.ignite.internal.processors.query.h2;


import java.io.File;
import java.util.concurrent.CountDownLatch;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.cache.GridCacheContext;
import org.apache.ignite.internal.processors.cache.IgniteInternalCache;
import org.apache.ignite.internal.processors.cache.index.AbstractSchemaSelfTest;
import org.apache.ignite.internal.processors.cache.index.DynamicIndexAbstractSelfTest;
import org.apache.ignite.internal.processors.cache.persistence.file.FilePageStoreManager;
import org.apache.ignite.internal.processors.query.schema.SchemaIndexCacheVisitorClosure;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.junit.Test;


/**
 * Index rebuild after node restart test.
 */
public class GridIndexRebuildSelfTest extends DynamicIndexAbstractSelfTest {
    /**
     * Data size.
     */
    protected static final int AMOUNT = 50;

    /**
     * Data size.
     */
    protected static final String CACHE_NAME = "T";

    /**
     * Test instance to allow interaction with static context.
     */
    private static GridIndexRebuildSelfTest INSTANCE;

    /**
     * Latch to signal that rebuild may start.
     */
    private final CountDownLatch rebuildLatch = new CountDownLatch(1);

    /**
     * Do test.
     * <p>
     * Steps are as follows:
     * <ul>
     *     <li>Put some data;</li>
     *     <li>Stop the node;</li>
     *     <li>Remove index file;</li>
     *     <li>Restart the node and block index rebuild;</li>
     *     <li>For half of the keys do cache puts <b>before</b> corresponding key
     *     has been processed during index rebuild;</li>
     *     <li>Check that:
     *         <ul>
     *             <li>For MVCC case: some keys have all versions that existed before restart, while those
     *             updated concurrently have only put version (one with mark value -1)
     *             and latest version present before node restart;</li>
     *             <li>For non MVCC case: keys updated concurrently must have mark values of -1 despite that
     *             index rebuild for them has happened after put.</li>
     *         </ul>
     *     </li>
     * </ul></p>
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testIndexRebuild() throws Exception {
        IgniteEx srv = startServer();
        AbstractSchemaSelfTest.execute(srv, ("CREATE TABLE T(k int primary key, v int) WITH \"cache_name=T,wrap_value=false," + "atomicity=transactional\""));
        AbstractSchemaSelfTest.execute(srv, "CREATE INDEX IDX ON T(v)");
        IgniteInternalCache cc = srv.cachex(GridIndexRebuildSelfTest.CACHE_NAME);
        assertNotNull(cc);
        putData(srv, false);
        checkDataState(srv, false);
        File cacheWorkDir = ((FilePageStoreManager) (cc.context().shared().pageStore())).cacheWorkDir(cc.configuration());
        File idxPath = cacheWorkDir.toPath().resolve("index.bin").toFile();
        stopAllGrids();
        assertTrue(U.delete(idxPath));
        srv = startServer();
        putData(srv, true);
        checkDataState(srv, true);
    }

    /**
     * Blocking indexing processor.
     */
    private static class BlockingIndexing extends IgniteH2Indexing {
        /**
         * Flag to ignore first rebuild performed on initial node start.
         */
        private boolean firstRbld = true;

        /**
         * {@inheritDoc }
         */
        @Override
        protected void rebuildIndexesFromHash0(GridCacheContext cctx, SchemaIndexCacheVisitorClosure clo) throws IgniteCheckedException {
            if (!(firstRbld))
                U.await(GridIndexRebuildSelfTest.INSTANCE.rebuildLatch);
            else
                firstRbld = false;

            super.rebuildIndexesFromHash0(cctx, clo);
        }
    }
}

