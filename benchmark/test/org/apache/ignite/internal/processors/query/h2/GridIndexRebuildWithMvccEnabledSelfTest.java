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
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.cache.IgniteInternalCache;
import org.apache.ignite.internal.processors.cache.index.AbstractSchemaSelfTest;
import org.apache.ignite.internal.processors.cache.persistence.file.FilePageStoreManager;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.junit.Test;


/**
 * Index rebuild after node restart test.
 */
public class GridIndexRebuildWithMvccEnabledSelfTest extends GridIndexRebuildSelfTest {
    /**
     * {@inheritDoc }
     */
    @Test
    public void testIndexRebuild() throws Exception {
        IgniteEx srv = startServer();
        AbstractSchemaSelfTest.execute(srv, ("CREATE TABLE T(k int primary key, v int) WITH \"cache_name=T,wrap_value=false," + "atomicity=transactional_snapshot\""));
        AbstractSchemaSelfTest.execute(srv, "CREATE INDEX IDX ON T(v)");
        IgniteInternalCache cc = srv.cachex(GridIndexRebuildSelfTest.CACHE_NAME);
        assertNotNull(cc);
        GridIndexRebuildWithMvccEnabledSelfTest.lockVersion(srv);
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
}

