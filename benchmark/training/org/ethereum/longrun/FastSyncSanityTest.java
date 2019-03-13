/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.longrun;


import EthereumListener.SyncState;
import EthereumListener.SyncState.SECURE;
import EthereumListener.SyncState.UNSECURE;
import com.typesafe.config.ConfigFactory;
import java.util.EnumSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.mutable.MutableObject;
import org.ethereum.config.SystemProperties;
import org.ethereum.facade.Ethereum;
import org.ethereum.listener.EthereumListener;
import org.ethereum.sync.SyncManager;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;


/**
 * Fast sync with sanity check
 *
 * Runs sync with defined config. Stops when all nodes are downloaded in 1 minute.
 * - checks State Trie is not broken
 * Restarts, waits until SECURE sync state (all headers are downloaded) in 1 minute.
 * - checks block headers
 * Restarts, waits until full sync is over
 * - checks nodes/headers/blocks/tx receipts
 *
 * Run with '-Dlogback.configurationFile=longrun/logback.xml' for proper logging
 * Also following flags are available:
 *     -Dreset.db.onFirstRun=true
 *     -Doverride.config.res=longrun/conf/live.conf
 */
@Ignore
public class FastSyncSanityTest {
    private Ethereum regularNode;

    private static AtomicBoolean firstRun = new AtomicBoolean(true);

    private static EnumSet<EthereumListener.SyncState> statesCompleted = EnumSet.noneOf(SyncState.class);

    private static final Logger testLogger = LoggerFactory.getLogger("TestLogger");

    private static final MutableObject<String> configPath = new MutableObject<>("longrun/conf/ropsten-fast.conf");

    private static final MutableObject<Boolean> resetDBOnFirstRun = new MutableObject<>(null);

    private static final AtomicBoolean allChecksAreOver = new AtomicBoolean(false);

    public FastSyncSanityTest() throws Exception {
        String resetDb = System.getProperty("reset.db.onFirstRun");
        String overrideConfigPath = System.getProperty("override.config.res");
        if (Boolean.parseBoolean(resetDb)) {
            FastSyncSanityTest.resetDBOnFirstRun.setValue(true);
        } else
            if ((resetDb != null) && (resetDb.equalsIgnoreCase("false"))) {
                FastSyncSanityTest.resetDBOnFirstRun.setValue(false);
            }

        if (overrideConfigPath != null)
            FastSyncSanityTest.configPath.setValue(overrideConfigPath);

        FastSyncSanityTest.statTimer.scheduleAtFixedRate(() -> {
            try {
                if ((FastSyncSanityTest.fatalErrors.get()) > 0) {
                    FastSyncSanityTest.statTimer.shutdownNow();
                }
            } catch (Throwable t) {
                FastSyncSanityTest.testLogger.error("Unhandled exception", t);
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

    /**
     * Spring configuration class for the Regular peer
     */
    private static class RegularConfig {
        @Bean
        public FastSyncSanityTest.RegularNode node() {
            return new FastSyncSanityTest.RegularNode();
        }

        /**
         * Instead of supplying properties via config file for the peer
         * we are substituting the corresponding bean which returns required
         * config for this instance.
         */
        @Bean
        public SystemProperties systemProperties() {
            SystemProperties props = new SystemProperties();
            props.overrideParams(ConfigFactory.parseResources(FastSyncSanityTest.configPath.getValue()));
            if ((FastSyncSanityTest.firstRun.get()) && ((FastSyncSanityTest.resetDBOnFirstRun.getValue()) != null)) {
                props.setDatabaseReset(FastSyncSanityTest.resetDBOnFirstRun.getValue());
            }
            return props;
        }
    }

    /**
     * Just regular EthereumJ node
     */
    static class RegularNode extends BasicNode {
        @Autowired
        SyncManager syncManager;

        public RegularNode() {
            super("sampleNode");
        }

        private void stopSync() {
            config.setSyncEnabled(false);
            config.setDiscoveryEnabled(false);
            ethereum.getChannelManager().close();
            syncPool.close();
            syncManager.close();
        }

        private void firstRunChecks() throws InterruptedException {
            if (!(FastSyncSanityTest.statesCompleted.containsAll(EnumSet.of(UNSECURE, SECURE))))
                return;

            Thread.sleep(60000);
            stopSync();
            FastSyncSanityTest.testLogger.info("Validating nodes: Start");
            BlockchainValidation.checkNodes(ethereum, commonConfig, FastSyncSanityTest.fatalErrors);
            FastSyncSanityTest.testLogger.info("Validating nodes: End");
            FastSyncSanityTest.testLogger.info("Validating block headers: Start");
            BlockchainValidation.checkFastHeaders(ethereum, commonConfig, FastSyncSanityTest.fatalErrors);
            FastSyncSanityTest.testLogger.info("Validating block headers: End");
            FastSyncSanityTest.firstRun.set(false);
        }

        @Override
        public void waitForSync() throws Exception {
            FastSyncSanityTest.testLogger.info("Waiting for the complete blockchain sync (will take up to an hour on fast sync for the whole chain)...");
            while (true) {
                Thread.sleep(10000);
                if ((syncState) == null)
                    continue;

                switch (syncState) {
                    case UNSECURE :
                        if ((!(FastSyncSanityTest.firstRun.get())) || (FastSyncSanityTest.statesCompleted.contains(UNSECURE)))
                            break;

                        FastSyncSanityTest.testLogger.info("[v] Unsecure sync completed");
                        FastSyncSanityTest.statesCompleted.add(UNSECURE);
                        firstRunChecks();
                        break;
                    case SECURE :
                        if ((!(FastSyncSanityTest.firstRun.get())) || (FastSyncSanityTest.statesCompleted.contains(SECURE)))
                            break;

                        FastSyncSanityTest.testLogger.info("[v] Secure sync completed");
                        FastSyncSanityTest.statesCompleted.add(SECURE);
                        firstRunChecks();
                        break;
                    case COMPLETE :
                        FastSyncSanityTest.testLogger.info(("[v] Sync complete! The best block: " + (bestBlock.getShortDescr())));
                        stopSync();
                        return;
                }
            } 
        }

        @Override
        public void onSyncDone() throws Exception {
            // Full sanity check
            FastSyncSanityTest.fullSanityCheck(ethereum, commonConfig);
        }
    }

    private static final AtomicInteger fatalErrors = new AtomicInteger(0);

    private static final long MAX_RUN_MINUTES = 1440L;

    private static ScheduledExecutorService statTimer = Executors.newSingleThreadScheduledExecutor(( r) -> new Thread(r, "StatTimer"));

    @Test
    public void testTripleCheck() throws Exception {
        runEthereum();
        new Thread(() -> {
            try {
                while (FastSyncSanityTest.firstRun.get()) {
                    Thread.sleep(1000);
                } 
                FastSyncSanityTest.testLogger.info("Stopping first run");
                regularNode.close();
                FastSyncSanityTest.testLogger.info("First run stopped");
                Thread.sleep(10000);
                FastSyncSanityTest.testLogger.info("Starting second run");
                runEthereum();
                while (!(FastSyncSanityTest.allChecksAreOver.get())) {
                    Thread.sleep(1000);
                } 
                FastSyncSanityTest.testLogger.info("Stopping second run");
                regularNode.close();
                FastSyncSanityTest.testLogger.info("All checks are finished");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }).start();
        if (FastSyncSanityTest.statTimer.awaitTermination(FastSyncSanityTest.MAX_RUN_MINUTES, TimeUnit.MINUTES)) {
            FastSyncSanityTest.logStats();
            // Checking for errors
            assert FastSyncSanityTest.allChecksAreOver.get();
            if (!(FastSyncSanityTest.logStats()))
                assert false;

        }
    }
}

