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


import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.ignite.IgniteException;
import org.apache.ignite.hadoop.fs.CachingHadoopFileSystemFactory;
import org.apache.ignite.hadoop.fs.HadoopFileSystemFactory;
import org.apache.ignite.igfs.IgfsPath;
import org.apache.ignite.internal.processors.hadoop.delegate.HadoopDelegateUtils;
import org.apache.ignite.internal.processors.hadoop.delegate.HadoopFileSystemFactoryDelegate;
import org.apache.ignite.internal.processors.igfs.IgfsCommonAbstractTest;
import org.apache.ignite.internal.processors.igfs.IgfsEx;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.lifecycle.LifecycleAware;
import org.junit.Test;


/**
 * Tests for Hadoop file system factory.
 */
public class HadoopFIleSystemFactorySelfTest extends IgfsCommonAbstractTest {
    /**
     * Amount of "start" invocations
     */
    private static final AtomicInteger START_CNT = new AtomicInteger();

    /**
     * Amount of "stop" invocations
     */
    private static final AtomicInteger STOP_CNT = new AtomicInteger();

    /**
     * Path to secondary file system configuration.
     */
    private static final String SECONDARY_CFG_PATH = "/work/core-site-HadoopFIleSystemFactorySelfTest.xml";

    /**
     * IGFS path for DUAL mode.
     */
    private static final Path PATH_DUAL = new Path("/ignite/sync/test_dir");

    /**
     * IGFS path for PROXY mode.
     */
    private static final Path PATH_PROXY = new Path("/ignite/proxy/test_dir");

    /**
     * IGFS path for DUAL mode.
     */
    private static final IgfsPath IGFS_PATH_DUAL = new IgfsPath("/ignite/sync/test_dir");

    /**
     * IGFS path for PROXY mode.
     */
    private static final IgfsPath IGFS_PATH_PROXY = new IgfsPath("/ignite/proxy/test_dir");

    /**
     * Secondary IGFS.
     */
    private IgfsEx secondary;

    /**
     * Primary IGFS.
     */
    private IgfsEx primary;

    /**
     * Test custom factory.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCustomFactory() throws Exception {
        assert (HadoopFIleSystemFactorySelfTest.START_CNT.get()) == 1;
        assert (HadoopFIleSystemFactorySelfTest.STOP_CNT.get()) == 0;
        // Use IGFS directly.
        primary.mkdirs(HadoopFIleSystemFactorySelfTest.IGFS_PATH_DUAL);
        assert primary.exists(HadoopFIleSystemFactorySelfTest.IGFS_PATH_DUAL);
        assert secondary.exists(HadoopFIleSystemFactorySelfTest.IGFS_PATH_DUAL);
        // Create remote instance.
        FileSystem fs = FileSystem.get(URI.create("igfs://primary@127.0.0.1:10500/"), HadoopFIleSystemFactorySelfTest.baseConfiguration());
        assertEquals(1, HadoopFIleSystemFactorySelfTest.START_CNT.get());
        assertEquals(0, HadoopFIleSystemFactorySelfTest.STOP_CNT.get());
        // Check file system operations.
        assert fs.exists(HadoopFIleSystemFactorySelfTest.PATH_DUAL);
        assert fs.delete(HadoopFIleSystemFactorySelfTest.PATH_DUAL, true);
        assert !(primary.exists(HadoopFIleSystemFactorySelfTest.IGFS_PATH_DUAL));
        assert !(secondary.exists(HadoopFIleSystemFactorySelfTest.IGFS_PATH_DUAL));
        assert !(fs.exists(HadoopFIleSystemFactorySelfTest.PATH_DUAL));
        assert fs.mkdirs(HadoopFIleSystemFactorySelfTest.PATH_DUAL);
        assert primary.exists(HadoopFIleSystemFactorySelfTest.IGFS_PATH_DUAL);
        assert secondary.exists(HadoopFIleSystemFactorySelfTest.IGFS_PATH_DUAL);
        assert fs.exists(HadoopFIleSystemFactorySelfTest.PATH_DUAL);
        assert fs.mkdirs(HadoopFIleSystemFactorySelfTest.PATH_PROXY);
        assert secondary.exists(HadoopFIleSystemFactorySelfTest.IGFS_PATH_PROXY);
        assert fs.exists(HadoopFIleSystemFactorySelfTest.PATH_PROXY);
        fs.close();
        assertEquals(1, HadoopFIleSystemFactorySelfTest.START_CNT.get());
        assertEquals(0, HadoopFIleSystemFactorySelfTest.STOP_CNT.get());
        // Stop primary node and ensure that base factory was notified.
        G.stop(primary.context().kernalContext().grid().name(), true);
        assertEquals(1, HadoopFIleSystemFactorySelfTest.START_CNT.get());
        assertEquals(1, HadoopFIleSystemFactorySelfTest.STOP_CNT.get());
    }

    /**
     * Test factory.
     */
    private static class TestFactory implements HadoopFileSystemFactory , LifecycleAware {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         * File system factory.
         */
        private CachingHadoopFileSystemFactory factory;

        /**
         * File system.
         */
        private transient HadoopFileSystemFactoryDelegate delegate;

        /**
         * Constructor.
         *
         * @param factory
         * 		File system factory.
         */
        public TestFactory(CachingHadoopFileSystemFactory factory) {
            this.factory = factory;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Object get(String usrName) throws IOException {
            return delegate.get(usrName);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void start() throws IgniteException {
            delegate = HadoopDelegateUtils.fileSystemFactoryDelegate(getClass().getClassLoader(), factory);
            delegate.start();
            HadoopFIleSystemFactorySelfTest.START_CNT.incrementAndGet();
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void stop() throws IgniteException {
            HadoopFIleSystemFactorySelfTest.STOP_CNT.incrementAndGet();
        }
    }
}

