/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.master.file.meta;


import PropertyKey.MASTER_UFS_PATH_CACHE_THREADS;
import UfsAbsentPathCache.Factory;
import alluxio.conf.ServerConfiguration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link UfsAbsentPathCache}.
 */
public class UfsAbsentPathCacheTest {
    @Test
    public void defaultAsyncPathThreads() throws Exception {
        UfsAbsentPathCache cache = Factory.create(null);
        Assert.assertTrue((cache instanceof AsyncUfsAbsentPathCache));
    }

    @Test
    public void noAsyncPathThreads() throws Exception {
        ServerConfiguration.set(MASTER_UFS_PATH_CACHE_THREADS, 0);
        UfsAbsentPathCache cache = Factory.create(null);
        Assert.assertTrue((cache instanceof NoopUfsAbsentPathCache));
    }

    @Test
    public void negativeAsyncPathThreads() throws Exception {
        ServerConfiguration.set(MASTER_UFS_PATH_CACHE_THREADS, (-1));
        UfsAbsentPathCache cache = Factory.create(null);
        Assert.assertTrue((cache instanceof NoopUfsAbsentPathCache));
    }
}

