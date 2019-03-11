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
package org.apache.ignite.internal.processors.hadoop.impl.igfs;


import HadoopIgfsUtils.PARAM_IGFS_ENDPOINT_NO_EMBED;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.ignite.internal.processors.igfs.IgfsCommonAbstractTest;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.junit.Test;


/**
 * IPC cache test.
 */
public class IgniteHadoopFileSystemIpcCacheSelfTest extends IgfsCommonAbstractTest {
    /**
     * Path to test hadoop configuration.
     */
    private static final String HADOOP_FS_CFG = "modules/core/src/test/config/hadoop/core-site.xml";

    /**
     * Group size.
     */
    public static final int GRP_SIZE = 128;

    /**
     * Started grid counter.
     */
    private static int cnt;

    /**
     * Test how IPC cache map works.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testIpcCache() throws Exception {
        Field cacheField = HadoopIgfsIpcIo.class.getDeclaredField("ipcCache");
        cacheField.setAccessible(true);
        Field activeCntField = HadoopIgfsIpcIo.class.getDeclaredField("activeCnt");
        activeCntField.setAccessible(true);
        Map<String, HadoopIgfsIpcIo> cache = ((Map<String, HadoopIgfsIpcIo>) (cacheField.get(null)));
        cache.clear();// avoid influence of previous tests in the same process.

        String name = ("igfs:" + (getTestIgniteInstanceName(0))) + "@";
        Configuration cfg = new Configuration();
        cfg.addResource(U.resolveIgniteUrl(IgniteHadoopFileSystemIpcCacheSelfTest.HADOOP_FS_CFG));
        cfg.setBoolean("fs.igfs.impl.disable.cache", true);
        cfg.setBoolean(String.format(PARAM_IGFS_ENDPOINT_NO_EMBED, name), true);
        // Ensure that existing IO is reused.
        FileSystem fs1 = FileSystem.get(new URI((("igfs://" + name) + "/")), cfg);
        assertEquals(1, cache.size());
        HadoopIgfsIpcIo io = null;
        System.out.println(("CACHE: " + cache));
        for (String key : cache.keySet()) {
            if (key.contains("10500")) {
                io = cache.get(key);
                break;
            }
        }
        assert io != null;
        assertEquals(1, ((AtomicInteger) (activeCntField.get(io))).get());
        // Ensure that when IO is used by multiple file systems and one of them is closed, IO is not stopped.
        FileSystem fs2 = FileSystem.get(new URI((("igfs://" + name) + "/abc")), cfg);
        assertEquals(1, cache.size());
        assertEquals(2, ((AtomicInteger) (activeCntField.get(io))).get());
        fs2.close();
        assertEquals(1, cache.size());
        assertEquals(1, ((AtomicInteger) (activeCntField.get(io))).get());
        Field stopField = HadoopIgfsIpcIo.class.getDeclaredField("stopping");
        stopField.setAccessible(true);
        assert !((Boolean) (stopField.get(io)));
        // Ensure that IO is stopped when nobody else is need it.
        fs1.close();
        assert cache.isEmpty();
        assert ((Boolean) (stopField.get(io)));
    }
}

