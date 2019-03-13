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
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.mutable.MutableObject;
import org.ethereum.config.SystemProperties;
import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;


/**
 * Sync with sanity check
 *
 * Runs sync with defined config
 * - checks that State Trie is not broken
 * - checks whether all blocks are in blockstore, validates parent connection and bodies
 * - checks and validates transaction receipts
 *
 * Stopped from time to time via process killing to replicate
 * most complicated conditions for application
 *
 * Run with '-Dlogback.configurationFile=longrun/logback.xml' for proper logging
 * *** NOTE: this test uses standard output for discovering node process pid, but if you run test using Gradle,
 *           it will put away standard streams, so test is unable to work. To solve the issue, you need to add
 *           "showStandardStreams = true" line and extend events with 'standard_out', 'standard_error'
 *           in test.testLogging section of build.gradle
 * Also following flags are supported:
 *     -Doverride.config.res=longrun/conf/live.conf
 *     -Dnode.run.cmd="./gradlew run"
 */
@Ignore
public class HardExitSanityTest {
    private Ethereum checkNode;

    private static AtomicBoolean checkInProgress = new AtomicBoolean(false);

    private static final Logger testLogger = LoggerFactory.getLogger("TestLogger");

    // Database path and type of two following configurations should match, so check will run over the same DB
    private static final MutableObject<String> configPath = new MutableObject<>("longrun/conf/live.conf");

    private String nodeRunCmd = "./gradlew run";// Test made to use configuration started from Gradle


    private Process proc;

    private static final AtomicInteger fatalErrors = new AtomicInteger(0);

    private static final long MAX_RUN_MINUTES = (3 * 24) * 60L;// Maximum running time


    private static ScheduledExecutorService statTimer = Executors.newSingleThreadScheduledExecutor(( r) -> new Thread(r, "StatTimer"));

    public HardExitSanityTest() throws Exception {
        String overrideNodeRunCmd = System.getProperty("node.run.cmd");
        if (overrideNodeRunCmd != null) {
            nodeRunCmd = overrideNodeRunCmd;
        }
        HardExitSanityTest.testLogger.info("Test will run EthereumJ using command: {}", nodeRunCmd);
        String overrideConfigPath = System.getProperty("override.config.res");
        if (overrideConfigPath != null) {
            HardExitSanityTest.configPath.setValue(overrideConfigPath);
        }
        // Catching errors in separate thread
        HardExitSanityTest.statTimer.scheduleAtFixedRate(() -> {
            try {
                if ((HardExitSanityTest.fatalErrors.get()) > 0) {
                    HardExitSanityTest.statTimer.shutdownNow();
                }
            } catch (Throwable t) {
                HardExitSanityTest.testLogger.error("Unhandled exception", t);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Spring configuration class for the Regular peer
     * - Peer will not sync
     * - Peer will run sanity check
     */
    private static class SanityCheckConfig {
        @Bean
        public SyncSanityTest.RegularNode node() {
            return new SyncSanityTest.RegularNode() {
                @Override
                public void run() {
                    HardExitSanityTest.testLogger.info("Begin sanity check for EthereumJ, best block [{}]", ethereum.getBlockchain().getBestBlock().getNumber());
                    HardExitSanityTest.fullSanityCheck(ethereum, commonConfig);
                    HardExitSanityTest.checkInProgress.set(false);
                }
            };
        }

        /**
         * Instead of supplying properties via config file for the peer
         * we are substituting the corresponding bean which returns required
         * config for this instance.
         */
        @Bean
        public SystemProperties systemProperties() {
            SystemProperties props = new SystemProperties();
            props.overrideParams(ConfigFactory.parseResources(HardExitSanityTest.configPath.getValue()));
            props.setDatabaseReset(false);
            props.setSyncEnabled(false);
            props.setDiscoveryEnabled(false);
            return props;
        }
    }

    @Test
    public void testMain() throws Exception {
        System.out.println("Test started");
        Thread main = new Thread(() -> {
            try {
                while (true) {
                    Random rnd = new Random();
                    int runDistance = (60 * 5) + (rnd.nextInt((60 * 5)));// 5 - 10 minutes

                    HardExitSanityTest.testLogger.info("Running EthereumJ node for {} seconds", runDistance);
                    startEthereumJ();
                    TimeUnit.SECONDS.sleep(runDistance);
                    killEthereumJ();
                    Thread.sleep(2000);
                    HardExitSanityTest.checkInProgress.set(true);
                    HardExitSanityTest.testLogger.info("Starting EthereumJ sanity check instance");
                    this.checkNode = EthereumFactory.createEthereum(HardExitSanityTest.SanityCheckConfig.class);
                    while (HardExitSanityTest.checkInProgress.get()) {
                        Thread.sleep(1000);
                    } 
                    checkNode.close();
                    HardExitSanityTest.testLogger.info("Sanity check is over", runDistance);
                } 
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        main.start();
        if (HardExitSanityTest.statTimer.awaitTermination(HardExitSanityTest.MAX_RUN_MINUTES, TimeUnit.MINUTES)) {
            if (!(HardExitSanityTest.checkInProgress.get())) {
                killEthereumJ();
            }
            while (HardExitSanityTest.checkInProgress.get()) {
                Thread.sleep(1000);
            } 
            assert HardExitSanityTest.logStats();
        }
    }
}

