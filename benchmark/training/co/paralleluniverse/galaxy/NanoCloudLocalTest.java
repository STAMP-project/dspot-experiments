/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2014, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.galaxy;


import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.common.util.Debug;
import co.paralleluniverse.galaxy.example.pingpong.Ping;
import co.paralleluniverse.galaxy.example.pingpong.Pong;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;


public class NanoCloudLocalTest extends BaseCloudTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    static final NanoCloudLocalTest.GlxConfig ZK_WITH_SERVER_CFG = new NanoCloudLocalTest.GlxConfig(BaseCloudTest.PEER_WITH_ZK_SERVER_CFG, BaseCloudTest.SERVER_ZK_CFG, true, true);

    static final NanoCloudLocalTest.GlxConfig JG_WITH_SERVER_CFG = new NanoCloudLocalTest.GlxConfig(BaseCloudTest.PEER_WITH_JG_SERVER_CFG, BaseCloudTest.SERVER_JG_CFG, false, true);

    static final NanoCloudLocalTest.GlxConfig JG_NO_SERVER_CFG = new NanoCloudLocalTest.GlxConfig(BaseCloudTest.PEER_NO_SERVER_CFG, null, false, false);

    static final NanoCloudLocalTest.GlxConfig CFG = NanoCloudLocalTest.ZK_WITH_SERVER_CFG;

    // JG_WITH_SERVER_CFG;
    // JG_NO_SERVER_CFG;
    private ServerCnxnFactory zk;

    @Test(timeout = 180000)
    public void pingPongTest() throws InterruptedException, ExecutionException {
        Assume.assumeTrue((!(Debug.isCI())));
        cloud.nodes(BaseCloudTest.SERVER, "ping", "pong");
        NanoCloudLocalTest.setJvmArgs(cloud);
        if (NanoCloudLocalTest.CFG.hasServer)
            cloud.node(BaseCloudTest.SERVER).submit(startGlxServer(NanoCloudLocalTest.CFG.serverCfg, BaseCloudTest.SERVER_PROPS));

        final Future<Integer> pingFuture = cloud.node("pong").submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                return Pong.runPong();
            }
        });
        // Thread.sleep(1000);
        Future<Void> ping = cloud.node("ping").submit(new Runnable() {
            @Override
            public void run() {
                Ping.runPing();
            }
        });
        int pings = pingFuture.get();
        Assert.assertEquals("Number of pings received by pong", 3, pings);
        ping.get();
    }

    static class GlxConfig {
        String peerCfg;

        String serverCfg;

        boolean hasZK;

        boolean hasServer;

        GlxConfig(String peerCfg, String serverCfg, boolean hasZK, boolean hasServer) {
            this.peerCfg = peerCfg;
            this.serverCfg = serverCfg;
            this.hasZK = hasZK;
            this.hasServer = hasServer;
        }
    }
}

