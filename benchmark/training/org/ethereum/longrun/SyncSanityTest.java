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


import com.typesafe.config.ConfigFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.mutable.MutableObject;
import org.ethereum.config.SystemProperties;
import org.ethereum.facade.Ethereum;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;


/**
 * Sync with sanity check
 *
 * Runs sync with defined config
 * - checks State Trie is not broken
 * - checks whether all blocks are in blockstore, validates parent connection and bodies
 * - checks and validate transaction receipts
 * Stopped, than restarts in 1 minute, syncs and pass all checks again
 *
 * Run with '-Dlogback.configurationFile=longrun/logback.xml' for proper logging
 * Also following flags are available:
 *     -Dreset.db.onFirstRun=true
 *     -Doverride.config.res=longrun/conf/live.conf
 */
@Ignore
public class SyncSanityTest {
    private Ethereum regularNode;

    private static AtomicBoolean firstRun = new AtomicBoolean(true);

    private static final Logger testLogger = LoggerFactory.getLogger("TestLogger");

    private static final MutableObject<String> configPath = new MutableObject<>("longrun/conf/ropsten.conf");

    private static final MutableObject<Boolean> resetDBOnFirstRun = new MutableObject<>(null);

    private static final AtomicBoolean allChecksAreOver = new AtomicBoolean(false);

    public SyncSanityTest() throws Exception {
        String resetDb = System.getProperty("reset.db.onFirstRun");
        String overrideConfigPath = System.getProperty("override.config.res");
        if (Boolean.parseBoolean(resetDb)) {
            SyncSanityTest.resetDBOnFirstRun.setValue(true);
        } else
            if ((resetDb != null) && (resetDb.equalsIgnoreCase("false"))) {
                SyncSanityTest.resetDBOnFirstRun.setValue(false);
            }

        if (overrideConfigPath != null)
            SyncSanityTest.configPath.setValue(overrideConfigPath);

        SyncSanityTest.statTimer.scheduleAtFixedRate(() -> {
            try {
                if ((SyncSanityTest.fatalErrors.get()) > 0) {
                    SyncSanityTest.statTimer.shutdownNow();
                }
            } catch (Throwable t) {
                SyncSanityTest.testLogger.error("Unhandled exception", t);
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

    /**
     * Spring configuration class for the Regular peer
     */
    private static class RegularConfig {
        @Bean
        public SyncSanityTest.RegularNode node() {
            return new SyncSanityTest.RegularNode();
        }

        /**
         * Instead of supplying properties via config file for the peer
         * we are substituting the corresponding bean which returns required
         * config for this instance.
         */
        @Bean
        public SystemProperties systemProperties() {
            SystemProperties props = new SystemProperties();
            props.overrideParams(ConfigFactory.parseResources(SyncSanityTest.configPath.getValue()));
            if ((SyncSanityTest.firstRun.get()) && ((SyncSanityTest.resetDBOnFirstRun.getValue()) != null)) {
                props.setDatabaseReset(SyncSanityTest.resetDBOnFirstRun.getValue());
            }
            return props;
        }
    }

    /**
     * Just regular EthereumJ node
     */
    static class RegularNode extends BasicNode {
        public RegularNode() {
            super("sampleNode");
        }

        @Override
        public void waitForSync() throws Exception {
            SyncSanityTest.testLogger.info("Waiting for the whole blockchain sync (will take up to an hour on fast sync for the whole chain)...");
            while (true) {
                Thread.sleep(10000);
                if (syncComplete) {
                    SyncSanityTest.testLogger.info(("[v] Sync complete! The best block: " + (bestBlock.getShortDescr())));
                    // Stop syncing
                    config.setSyncEnabled(false);
                    config.setDiscoveryEnabled(false);
                    ethereum.getChannelManager().close();
                    syncPool.close();
                    return;
                }
            } 
        }

        @Override
        public void onSyncDone() throws Exception {
            // Full sanity check
            SyncSanityTest.fullSanityCheck(ethereum, commonConfig);
        }
    }

    private static final AtomicInteger fatalErrors = new AtomicInteger(0);

    private static final long MAX_RUN_MINUTES = 180L;

    private static ScheduledExecutorService statTimer = Executors.newSingleThreadScheduledExecutor(( r) -> new Thread(r, "StatTimer"));

    @Test
    public void testDoubleCheck() throws Exception {
        runEthereum();
        new Thread(() -> {
            try {
                while (SyncSanityTest.firstRun.get()) {
                    Thread.sleep(1000);
                } 
                SyncSanityTest.testLogger.info("Stopping first run");
                regularNode.close();
                SyncSanityTest.testLogger.info("First run stopped");
                Thread.sleep(60000);
                SyncSanityTest.testLogger.info("Starting second run");
                runEthereum();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }).start();
        if (SyncSanityTest.statTimer.awaitTermination(SyncSanityTest.MAX_RUN_MINUTES, TimeUnit.MINUTES)) {
            SyncSanityTest.logStats();
            // Checking for errors
            assert SyncSanityTest.allChecksAreOver.get();
            if (!(SyncSanityTest.logStats()))
                assert false;

        }
    }
}

