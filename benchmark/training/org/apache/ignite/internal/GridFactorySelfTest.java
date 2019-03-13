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
package org.apache.ignite.internal;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.IgniteState;
import org.apache.ignite.IgniteSystemProperties;
import org.apache.ignite.Ignition;
import org.apache.ignite.IgnitionListener;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.Event;
import org.apache.ignite.internal.util.lang.GridTuple;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.lifecycle.LifecycleEventType;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.resources.SpringApplicationContextResource;
import org.apache.ignite.spi.IgniteSpiAdapter;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.IgniteSpiMultipleInstancesSupport;
import org.apache.ignite.spi.collision.CollisionContext;
import org.apache.ignite.spi.collision.CollisionExternalListener;
import org.apache.ignite.spi.collision.CollisionSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.config.GridTestProperties;
import org.apache.ignite.testframework.http.GridEmbeddedHttpServer;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.ignite.testframework.junits.common.GridCommonTest;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.springframework.context.ApplicationContext;


/**
 * Tests for {@link org.apache.ignite.Ignition}.
 *
 * @see GridFactoryVmShutdownTest
 */
@GridCommonTest(group = "NonDistributed Kernal Self")
public class GridFactorySelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final AtomicInteger cnt = new AtomicInteger();

    /**
     * Concurrency.
     */
    private static final int CONCURRENCY = 10;

    /**
     *
     */
    private static final String CUSTOM_CFG_PATH = "modules/core/src/test/config/factory/custom-grid-name-spring-test.xml";

    /**
     *
     */
    public GridFactorySelfTest() {
        super(false);
        System.setProperty(IgniteSystemProperties.IGNITE_OVERRIDE_MCAST_GRP, GridTestUtils.getNextMulticastGroup(GridFactorySelfTest.class));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testIgnitionStartDefault() throws Exception {
        try (Ignite ignite = Ignition.start()) {
            log.info(("Started1: " + (ignite.name())));
            try {
                Ignition.start();
                fail();
            } catch (IgniteException ignored) {
                // No-op.
            }
        }
        try (Ignite ignite = Ignition.start()) {
            log.info(("Started2: " + (ignite.name())));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartDefault() throws Exception {
        try (Ignite ignite = Ignition.start("config/default-config.xml")) {
            log.info(("Started: " + (ignite.name())));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartGridWithConfigUrlString() throws Exception {
        GridEmbeddedHttpServer srv = null;
        String igniteInstanceName = "grid_with_url_config";
        try {
            srv = GridEmbeddedHttpServer.startHttpServer().withFileDownloadingHandler(null, GridTestUtils.resolveIgnitePath("/modules/core/src/test/config/default-spring-url-testing.xml"));
            Ignite ignite = G.start(srv.getBaseUrl());
            assert igniteInstanceName.equals(ignite.name()) : "Unexpected Ignite instance name: " + (ignite.name());
        } finally {
            if (srv != null)
                srv.stop(1);

            G.stop(igniteInstanceName, false);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartGridWithConfigUrl() throws Exception {
        GridEmbeddedHttpServer srv = null;
        String igniteInstanceName = "grid_with_url_config";
        try {
            srv = GridEmbeddedHttpServer.startHttpServer().withFileDownloadingHandler(null, GridTestUtils.resolveIgnitePath("modules/core/src/test/config/default-spring-url-testing.xml"));
            Ignite ignite = G.start(new URL(srv.getBaseUrl()));
            assert igniteInstanceName.equals(ignite.name()) : "Unexpected Ignite instance name: " + (ignite.name());
        } finally {
            if (srv != null)
                srv.stop(1);

            G.stop(igniteInstanceName, false);
        }
    }

    /**
     * Tests default grid
     */
    @Test
    public void testDefaultGridGetOrStart() throws Exception {
        IgniteConfiguration cfg = getConfiguration(null);
        try (Ignite ignite = Ignition.getOrStart(cfg)) {
            try {
                Ignition.start(cfg);
                fail("Expected exception after grid started");
            } catch (IgniteException ignored) {
            }
            Ignite ignite2 = Ignition.getOrStart(cfg);
            assertEquals("Must return same instance", ignite, ignite2);
        }
        assertTrue(G.allGrids().isEmpty());
    }

    /**
     * Tests named grid
     */
    @Test
    public void testNamedGridGetOrStart() throws Exception {
        IgniteConfiguration cfg = getConfiguration("test");
        try (Ignite ignite = Ignition.getOrStart(cfg)) {
            try {
                Ignition.start(cfg);
                fail("Expected exception after grid started");
            } catch (IgniteException ignored) {
                // No-op.
            }
            Ignite ignite2 = Ignition.getOrStart(cfg);
            assertEquals("Must return same instance", ignite, ignite2);
        }
        assertTrue(G.allGrids().isEmpty());
    }

    /**
     * Tests concurrent grid initialization
     */
    @Test
    public void testConcurrentGridGetOrStartCon() throws Exception {
        final IgniteConfiguration cfg = getConfiguration(null);
        final AtomicReference<Ignite> ref = new AtomicReference<>();
        try {
            GridTestUtils.runMultiThreaded(new Runnable() {
                @Override
                public void run() {
                    // must return same instance in each thread
                    try {
                        Ignite ignite = Ignition.getOrStart(cfg);
                        boolean set = ref.compareAndSet(null, ignite);
                        if (!set)
                            assertEquals(ref.get(), ignite);

                    } catch (IgniteException e) {
                        throw new RuntimeException("Ignite error", e);
                    }
                }
            }, GridFactorySelfTest.CONCURRENCY, "GridCreatorThread");
        } catch (Exception ignored) {
            fail("Exception is not expected");
        }
        G.stopAll(true);
        assertTrue(G.allGrids().isEmpty());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLifecycleBeansNullIgniteInstanceName() throws Exception {
        checkLifecycleBeans(null);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLifecycleBeansNotNullIgniteInstanceName() throws Exception {
        checkLifecycleBeans("testGrid");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartMultipleGridsFromSpring() throws Exception {
        File cfgFile = GridTestUtils.resolveIgnitePath(GridTestProperties.getProperty("loader.self.multipletest.config"));
        assert cfgFile != null;
        String path = cfgFile.getAbsolutePath();
        info(("Loading Grid from configuration file: " + path));
        final GridTuple<IgniteState> gridState1 = F.t(null);
        final GridTuple<IgniteState> gridState2 = F.t(null);
        final Object mux = new Object();
        IgnitionListener factoryLsnr = new IgnitionListener() {
            @Override
            public void onStateChange(String name, IgniteState state) {
                synchronized(mux) {
                    if ("grid-factory-test-1".equals(name))
                        gridState1.set(state);
                    else
                        if ("grid-factory-test-2".equals(name))
                            gridState2.set(state);


                }
            }
        };
        G.addListener(factoryLsnr);
        G.start(path);
        assert (G.ignite("grid-factory-test-1")) != null;
        assert (G.ignite("grid-factory-test-2")) != null;
        synchronized(mux) {
            assert (gridState1.get()) == (IgniteState.STARTED) : ((("Invalid grid state [expected=" + (IgniteState.STARTED)) + ", returned=") + gridState1) + ']';
            assert (gridState2.get()) == (IgniteState.STARTED) : ((("Invalid grid state [expected=" + (IgniteState.STARTED)) + ", returned=") + gridState2) + ']';
        }
        G.stop("grid-factory-test-1", true);
        G.stop("grid-factory-test-2", true);
        synchronized(mux) {
            assert (gridState1.get()) == (IgniteState.STOPPED) : ((("Invalid grid state [expected=" + (IgniteState.STOPPED)) + ", returned=") + gridState1) + ']';
            assert (gridState2.get()) == (IgniteState.STOPPED) : ((("Invalid grid state [expected=" + (IgniteState.STOPPED)) + ", returned=") + gridState2) + ']';
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartMultipleDefaultGrids() throws Exception {
        try {
            multithreaded(new Callable<Object>() {
                @Nullable
                @Override
                public Object call() throws Exception {
                    try {
                        IgniteConfiguration cfg = new IgniteConfiguration();
                        cfg.setConnectorConfiguration(null);
                        G.start(cfg);
                    } catch (Throwable t) {
                        error("Caught exception while starting grid.", t);
                    }
                    info("Thread finished.");
                    return null;
                }
            }, 5, "grid-starter");
            assert (G.allGrids().size()) == 1;
            assert (G.ignite()) != null;
        } finally {
            G.stopAll(true);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartMultipleNonDefaultGrids() throws Exception {
        try {
            multithreaded(new Callable<Object>() {
                @Nullable
                @Override
                public Object call() throws Exception {
                    try {
                        IgniteConfiguration cfg = new IgniteConfiguration();
                        cfg.setIgniteInstanceName("TEST_NAME");
                        cfg.setConnectorConfiguration(null);
                        G.start(cfg);
                    } catch (Throwable t) {
                        error("Caught exception while starting grid.", t);
                    }
                    info("Thread finished.");
                    return null;
                }
            }, 5, "grid-starter");
            assert (G.allGrids().size()) == 1;
            assert (G.ignite("TEST_NAME")) != null;
        } finally {
            G.stopAll(true);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConcurrentStartStop() throws Exception {
        checkConcurrentStartStop("TEST_NAME");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConcurrentStartStopDefaultGrid() throws Exception {
        checkConcurrentStartStop(null);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGridStartRollback() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Nullable
            @Override
            public Object call() throws Exception {
                IgniteConfiguration cfg = new IgniteConfiguration();
                cfg.setConnectorConfiguration(null);
                cfg.setDiscoverySpi(new TcpDiscoverySpi() {
                    @Override
                    public void spiStart(String igniteInstanceName) throws IgniteSpiException {
                        throw new IgniteSpiException("This SPI will never start.");
                    }
                });
                G.start(cfg);
                info("Thread finished.");
                return null;
            }
        }, IgniteException.class, null);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartMultipleInstanceSpi() throws Exception {
        IgniteConfiguration cfg1 = getConfiguration();
        IgniteConfiguration cfg2 = getConfiguration();
        IgniteConfiguration cfg3 = getConfiguration();
        cfg1.setCollisionSpi(new GridFactorySelfTest.TestMultipleInstancesCollisionSpi());
        cfg2.setCollisionSpi(new GridFactorySelfTest.TestMultipleInstancesCollisionSpi());
        cfg3.setCollisionSpi(new GridFactorySelfTest.TestMultipleInstancesCollisionSpi());
        cfg2.setIgniteInstanceName(((getTestIgniteInstanceName()) + '1'));
        G.start(cfg2);
        G.start(cfg1);
        cfg3.setIgniteInstanceName(((getTestIgniteInstanceName()) + '2'));
        G.start(cfg3);
        assert (G.state(cfg1.getIgniteInstanceName())) == (IgniteState.STARTED);
        assert (G.state(((getTestIgniteInstanceName()) + '1'))) == (IgniteState.STARTED);
        assert (G.state(((getTestIgniteInstanceName()) + '2'))) == (IgniteState.STARTED);
        G.stop(((getTestIgniteInstanceName()) + '2'), false);
        G.stop(cfg1.getIgniteInstanceName(), false);
        G.stop(((getTestIgniteInstanceName()) + '1'), false);
        assert (G.state(cfg1.getIgniteInstanceName())) == (IgniteState.STOPPED);
        assert (G.state(((getTestIgniteInstanceName()) + '1'))) == (IgniteState.STOPPED);
        assert (G.state(((getTestIgniteInstanceName()) + '2'))) == (IgniteState.STOPPED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLoadBean() throws Exception {
        final String path = "modules/spring/src/test/java/org/apache/ignite/internal/cache.xml";
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Ignition.loadSpringBean(path, "wrongName");
                return null;
            }
        }, IgniteException.class, null);
        CacheConfiguration cfg = Ignition.loadSpringBean(path, "cache-configuration");
        assertEquals("TestDynamicCache", cfg.getName());
    }

    /**
     *
     */
    @IgniteSpiMultipleInstancesSupport(true)
    private static class TestMultipleInstancesCollisionSpi extends IgniteSpiAdapter implements CollisionSpi {
        /**
         * Grid logger.
         */
        @LoggerResource
        private IgniteLogger log;

        /**
         * {@inheritDoc }
         */
        @Override
        public void onCollision(CollisionContext ctx) {
            // No-op.
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void spiStart(String igniteInstanceName) throws IgniteSpiException {
            // Start SPI start stopwatch.
            startStopwatch();
            // Ack start.
            if (log.isInfoEnabled())
                log.info(startInfo());

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void spiStop() throws IgniteSpiException {
            // Ack stop.
            if (log.isInfoEnabled())
                log.info(stopInfo());

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setExternalCollisionListener(CollisionExternalListener lsnr) {
            // No-op.
        }
    }

    /**
     * DO NOT CHANGE MULTIPLE INSTANCES SUPPORT.
     * This test might be working on distributed environment.
     */
    @IgniteSpiMultipleInstancesSupport(true)
    private static class TestSingleInstancesCollisionSpi extends IgniteSpiAdapter implements CollisionSpi {
        /**
         * Grid logger.
         */
        @LoggerResource
        private IgniteLogger log;

        /**
         * {@inheritDoc }
         */
        @Override
        public void onCollision(CollisionContext ctx) {
            // No-op.
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void spiStart(String igniteInstanceName) throws IgniteSpiException {
            // Start SPI start stopwatch.
            startStopwatch();
            // Ack start.
            if (log.isInfoEnabled())
                log.info(startInfo());

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void spiStop() throws IgniteSpiException {
            // Ack stop.
            if (log.isInfoEnabled())
                log.info(stopInfo());

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setExternalCollisionListener(CollisionExternalListener lsnr) {
            // No-op.
        }
    }

    /**
     * Lifecycle bean for testing.
     */
    private static class TestLifecycleBean implements LifecycleBean {
        /**
         * Grid logger.
         */
        @LoggerResource
        private IgniteLogger log;

        /**
         *
         */
        @SpringApplicationContextResource
        private ApplicationContext appCtx;

        /**
         *
         */
        @IgniteInstanceResource
        private Ignite ignite;

        /**
         * Lifecycle events.
         */
        private final List<LifecycleEventType> evts = new ArrayList<>();

        /**
         * Ignite instance names.
         */
        private final List<String> igniteInstanceNames = new ArrayList<>();

        /**
         *
         */
        private final AtomicReference<Throwable> err = new AtomicReference<>();

        /**
         * {@inheritDoc }
         */
        @Override
        public void onLifecycleEvent(LifecycleEventType evt) {
            evts.add(evt);
            igniteInstanceNames.add(ignite.name());
            try {
                checkState(ignite.name(), ((evt == (LifecycleEventType.AFTER_NODE_START)) || (evt == (LifecycleEventType.BEFORE_NODE_STOP))));
            } catch (Throwable e) {
                log.error(("Lifecycle bean failed state check: " + (this)), e);
                err.compareAndSet(null, e);
            }
        }

        /**
         * Checks state of the bean.
         *
         * @param igniteInstanceName
         * 		Ignite instance name.
         * @param exec
         * 		Try to execute something on the grid.
         */
        void checkState(String igniteInstanceName, boolean exec) {
            assert (log) != null;
            assert (appCtx) != null;
            assert F.eq(igniteInstanceName, ignite.name());
            // Execute any grid method.
            if (exec)
                G.ignite(igniteInstanceName).events().localQuery(F.<Event>alwaysTrue());

        }

        /**
         * Gets ordered list of lifecycle events.
         *
         * @return Ordered list of lifecycle events.
         */
        List<LifecycleEventType> getLifecycleEvents() {
            return evts;
        }

        /**
         * Gets ordered list of Ignite instance names.
         *
         * @return Ordered list of Ignite instance names.
         */
        List<String> getIgniteInstanceNames() {
            return igniteInstanceNames;
        }

        /**
         *
         */
        void checkErrors() {
            if ((err.get()) != null)
                fail(("Exception has been caught by listener: " + (err.get().getMessage())));

        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStopCancel() throws Exception {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setConnectorConfiguration(null);
        Ignite ignite = G.start(cfg);
        ignite.compute().execute(GridFactorySelfTest.TestTask.class, null);
        G.stop(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConfigInClassPath() throws Exception {
        try (Ignite ignite = Ignition.start("config/ignite-test-config.xml")) {
            assert "config-in-classpath".equals(ignite.name());
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCurrentIgnite() throws Exception {
        final String LEFT = "LEFT";
        final String RIGHT = "RIGHT";
        try {
            Ignite iLEFT = startGrid(LEFT);
            Ignite iRIGHT = startGrid(RIGHT);
            waitForDiscovery(iLEFT, iRIGHT);
            iLEFT.compute(iLEFT.cluster().forRemotes()).run(new org.apache.ignite.lang.IgniteRunnable() {
                @Override
                public void run() {
                    assert Ignition.localIgnite().name().equals(RIGHT);
                }
            });
            iRIGHT.compute(iRIGHT.cluster().forRemotes()).run(new org.apache.ignite.lang.IgniteRunnable() {
                @Override
                public void run() {
                    assert Ignition.localIgnite().name().equals(LEFT);
                }
            });
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRepeatingStart() throws Exception {
        try {
            IgniteConfiguration c = getConfiguration("1");
            startGrid("1", c);
            if (tcpDiscovery())
                assert started();

            try {
                startGrid("2", c);
                fail("Should not be able to start grid using same configuration instance.");
            } catch (Exception e) {
                info(("Caught expected exception: " + e));
            }
        } finally {
            stopAllGrids();
        }
    }

    /**
     * Test task.
     */
    private static class TestTask extends ComputeTaskSplitAdapter<Void, Void> {
        /**
         * {@inheritDoc }
         */
        @Override
        protected Collection<? extends ComputeJob> split(int gridSize, Void arg) {
            return F.asSet(new GridFactorySelfTest.TestJob());
        }

        /**
         * {@inheritDoc }
         */
        @Nullable
        @Override
        public Void reduce(List<ComputeJobResult> results) {
            return null;
        }
    }

    /**
     * Test job.
     */
    private static class TestJob extends ComputeJobAdapter {
        /**
         * {@inheritDoc }
         */
        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public Object execute() {
            long start = System.currentTimeMillis();
            while (((System.currentTimeMillis()) - start) < 3000);
            return null;
        }
    }
}

