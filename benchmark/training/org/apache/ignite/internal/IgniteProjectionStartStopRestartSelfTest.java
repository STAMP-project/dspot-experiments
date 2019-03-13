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
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.cluster.ClusterStartNodeResult;
import org.apache.ignite.internal.util.nodestart.IgniteNodeStartUtils;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.X;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests for {@code startNodes(..)}, {@code stopNodes(..)}
 * and {@code restartNodes(..)} methods.
 * <p>
 * Environment (obtained via {@link System#getenv(String)}) or, alternatively, {@code tests.properties} file must
 * specify either username and password or private key path in the environment properties (@code test.ssh.username},
 * {@code test.ssh.password}, {@code ssh.key} or in test file entries {@code ssh.username} {@code ssh.password},
 * {@code ssh.key}respectively.</p>
 * <p>
 * Configured target host must run ssh server and accept ssh connections at configured port from user with specified
 * credentials.</p>
 */
@SuppressWarnings("ConstantConditions")
public class IgniteProjectionStartStopRestartSelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final String SSH_UNAME = IgniteProjectionStartStopRestartSelfTest.getProperty("test.ssh.username", "ssh.username");

    /**
     *
     */
    private static final String SSH_PWD = IgniteProjectionStartStopRestartSelfTest.getProperty("test.ssh.password", "ssh.password");

    /**
     *
     */
    private static final String SSH_KEY = IgniteProjectionStartStopRestartSelfTest.getProperty("ssh.key", "ssh.key");

    /**
     *
     */
    private static final String CUSTOM_SCRIPT_WIN = "modules/core/src/test/bin/start-nodes-custom.bat";

    /**
     *
     */
    private static final String CUSTOM_SCRIPT_LINUX = "modules/core/src/test/bin/start-nodes-custom.sh";

    /**
     *
     */
    private static final String CFG_NO_ATTR = "modules/core/src/test/config/spring-start-nodes.xml";

    /**
     *
     */
    private static final String CFG_ATTR = "modules/core/src/test/config/spring-start-nodes-attr.xml";

    /**
     *
     */
    private static final String CUSTOM_CFG_ATTR_KEY = "grid.node.ssh.started";

    /**
     *
     */
    private static final String CUSTOM_CFG_ATTR_VAL = "true";

    /**
     *
     */
    private static final long WAIT_TIMEOUT = 90 * 1000;

    /**
     *
     */
    private String pwd;

    /**
     *
     */
    private File key;

    /**
     *
     */
    private Ignite ignite;

    /**
     *
     */
    private static final String HOST = "127.0.0.1";

    /**
     *
     */
    private final AtomicInteger joinedCnt = new AtomicInteger();

    /**
     *
     */
    private final AtomicInteger leftCnt = new AtomicInteger();

    /**
     *
     */
    private volatile CountDownLatch joinedLatch;

    /**
     *
     */
    private volatile CountDownLatch leftLatch;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartOneNode() throws Exception {
        joinedLatch = new CountDownLatch(1);
        Collection<ClusterStartNodeResult> res = startNodes(ignite.cluster(), maps(Collections.singleton(IgniteProjectionStartStopRestartSelfTest.HOST), pwd, key, 1, U.getIgniteHome(), IgniteProjectionStartStopRestartSelfTest.CFG_NO_ATTR, null), false, 0, 16);
        assert (res.size()) == 1;
        res.forEach(( t) -> {
            assert t.getHostName().equals(HOST);
            if (!(t.isSuccess()))
                throw new IgniteException(t.getError());

        });
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (joinedCnt.get()) == 1;
        assert (leftCnt.get()) == 0;
        assert (ignite.cluster().nodes().size()) == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartThreeNodes() throws Exception {
        joinedLatch = new CountDownLatch(3);
        Collection<ClusterStartNodeResult> res = startNodes(ignite.cluster(), maps(Collections.singleton(IgniteProjectionStartStopRestartSelfTest.HOST), pwd, key, 3, U.getIgniteHome(), IgniteProjectionStartStopRestartSelfTest.CFG_NO_ATTR, null), false, IgniteNodeStartUtils.DFLT_TIMEOUT, 1);
        assert (res.size()) == 3;
        res.forEach(( t) -> {
            assert t.getHostName().equals(HOST);
            if (!(t.isSuccess()))
                throw new IgniteException(t.getError());

        });
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (joinedCnt.get()) == 3;
        assert (leftCnt.get()) == 0;
        assert (ignite.cluster().nodes().size()) == 3;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartThreeNodesAndDoEmptyCall() throws Exception {
        joinedLatch = new CountDownLatch(3);
        startCheckNodes();
        assert (joinedCnt.get()) == 3;
        assert (leftCnt.get()) == 0;
        assert (ignite.cluster().nodes().size()) == 3;
        Collection<ClusterStartNodeResult> res = startNodes(ignite.cluster(), maps(Collections.singleton(IgniteProjectionStartStopRestartSelfTest.HOST), pwd, key, 3, U.getIgniteHome(), IgniteProjectionStartStopRestartSelfTest.CFG_NO_ATTR, null), false, 0, 16);
        assert res.isEmpty();
        assert (joinedCnt.get()) == 3;
        assert (leftCnt.get()) == 0;
        assert (ignite.cluster().nodes().size()) == 3;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartThreeNodesAndTryToStartOneNode() throws Exception {
        joinedLatch = new CountDownLatch(3);
        startCheckNodes();
        assert (joinedCnt.get()) == 3;
        assert (leftCnt.get()) == 0;
        Collection<ClusterStartNodeResult> res = startNodes(ignite.cluster(), maps(Collections.singleton(IgniteProjectionStartStopRestartSelfTest.HOST), pwd, key, 1, U.getIgniteHome(), IgniteProjectionStartStopRestartSelfTest.CFG_NO_ATTR, null), false, 0, 16);
        assert res.isEmpty();
        assert (joinedCnt.get()) == 3;
        assert (leftCnt.get()) == 0;
        assert (ignite.cluster().nodes().size()) == 3;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartFiveNodesInTwoCalls() throws Exception {
        joinedLatch = new CountDownLatch(3);
        startCheckNodes();
        assert (joinedCnt.get()) == 3;
        assert (leftCnt.get()) == 0;
        joinedLatch = new CountDownLatch(2);
        Collection<ClusterStartNodeResult> res = startNodes(ignite.cluster(), maps(Collections.singleton(IgniteProjectionStartStopRestartSelfTest.HOST), pwd, key, 5, U.getIgniteHome(), IgniteProjectionStartStopRestartSelfTest.CFG_NO_ATTR, null), false, 0, 16);
        assert (res.size()) == 2;
        res.forEach(( t) -> {
            assert t.getHostName().equals(HOST);
            if (!(t.isSuccess()))
                throw new IgniteException(t.getError());

        });
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (joinedCnt.get()) == 5;
        assert (leftCnt.get()) == 0;
        assert (ignite.cluster().nodes().size()) == 5;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartFiveWithTwoSpecs() throws Exception {
        joinedLatch = new CountDownLatch(5);
        Collection<ClusterStartNodeResult> res = startNodes(ignite.cluster(), F.asList(map(pwd, key, 2, U.getIgniteHome()), map(pwd, key, 3, U.getIgniteHome())), false, 0, 16);
        assert (res.size()) == 5;
        res.forEach(( t) -> {
            assert t.getHostName().equals(HOST);
            if (!(t.isSuccess()))
                throw new IgniteException(t.getError());

        });
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (joinedCnt.get()) == 5;
        assert (leftCnt.get()) == 0;
        assert (ignite.cluster().nodes().size()) == 5;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartThreeNodesAndRestart() throws Exception {
        joinedLatch = new CountDownLatch(3);
        startCheckNodes();
        assert (joinedCnt.get()) == 3;
        assert (leftCnt.get()) == 0;
        joinedLatch = new CountDownLatch(3);
        leftLatch = new CountDownLatch(3);
        Collection<ClusterStartNodeResult> res = startNodes(ignite.cluster(), maps(Collections.singleton(IgniteProjectionStartStopRestartSelfTest.HOST), pwd, key, 3, U.getIgniteHome(), IgniteProjectionStartStopRestartSelfTest.CFG_NO_ATTR, null), true, 0, 16);
        assert (res.size()) == 3;
        res.forEach(( t) -> {
            assert t.getHostName().equals(HOST);
            if (!(t.isSuccess()))
                throw new IgniteException(t.getError());

        });
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert leftLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (joinedCnt.get()) == 6;
        assert (leftCnt.get()) == 3;
        assert (ignite.cluster().nodes().size()) == 3;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCustomScript() throws Exception {
        joinedLatch = new CountDownLatch(1);
        String script = (U.isWindows()) ? IgniteProjectionStartStopRestartSelfTest.CUSTOM_SCRIPT_WIN : IgniteProjectionStartStopRestartSelfTest.CUSTOM_SCRIPT_LINUX;
        script = Paths.get(U.getIgniteHome()).relativize(U.resolveIgnitePath(script).toPath()).toString();
        Collection<ClusterStartNodeResult> res = startNodes(ignite.cluster(), maps(Collections.singleton(IgniteProjectionStartStopRestartSelfTest.HOST), pwd, key, 1, U.getIgniteHome(), null, script), false, 0, 16);
        assert (res.size()) == 1;
        res.forEach(( t) -> {
            assert t.getHostName().equals(HOST);
            if (!(t.isSuccess()))
                throw new IgniteException(t.getError());

        });
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (joinedCnt.get()) == 1;
        assert (leftCnt.get()) == 0;
        assert (ignite.cluster().nodes().size()) == 1;
        assert IgniteProjectionStartStopRestartSelfTest.CUSTOM_CFG_ATTR_VAL.equals(F.first(ignite.cluster().nodes()).<String>attribute(IgniteProjectionStartStopRestartSelfTest.CUSTOM_CFG_ATTR_KEY));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStopNodes() throws Exception {
        joinedLatch = new CountDownLatch(3);
        Collection<ClusterStartNodeResult> res = startNodes(ignite.cluster(), maps(Collections.singleton(IgniteProjectionStartStopRestartSelfTest.HOST), pwd, null, 3, U.getIgniteHome(), IgniteProjectionStartStopRestartSelfTest.CFG_NO_ATTR, null), false, 0, 16);
        assert (res.size()) == 3;
        res.forEach(( t) -> {
            assert t.getHostName().equals(HOST);
            if (!(t.isSuccess()))
                throw new IgniteException(t.getError());

        });
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (ignite.cluster().nodes().size()) == 3;
        leftLatch = new CountDownLatch(3);
        ignite.cluster().stopNodes();
        assert leftLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert ignite.cluster().nodes().isEmpty();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStopNodesFiltered() throws Exception {
        joinedLatch = new CountDownLatch(2);
        Collection<ClusterStartNodeResult> res = startNodes(ignite.cluster(), maps(Collections.singleton(IgniteProjectionStartStopRestartSelfTest.HOST), pwd, key, 2, U.getIgniteHome(), IgniteProjectionStartStopRestartSelfTest.CFG_ATTR, null), false, 0, 16);
        assert (res.size()) == 2;
        res.forEach(( t) -> {
            assert t.getHostName().equals(HOST);
            if (!(t.isSuccess()))
                throw new IgniteException(t.getError());

        });
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        joinedLatch = new CountDownLatch(1);
        res = startNodes(ignite.cluster(), maps(Collections.singleton(IgniteProjectionStartStopRestartSelfTest.HOST), pwd, key, 3, U.getIgniteHome(), IgniteProjectionStartStopRestartSelfTest.CFG_NO_ATTR, null), false, 0, 16);
        assert (res.size()) == 1;
        res.forEach(( t) -> {
            assert t.getHostName().equals(HOST);
            if (!(t.isSuccess()))
                throw new IgniteException(t.getError());

        });
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (ignite.cluster().nodes().size()) == 3;
        leftLatch = new CountDownLatch(2);
        Collection<UUID> ids = F.transform(ignite.cluster().forAttribute(IgniteProjectionStartStopRestartSelfTest.CUSTOM_CFG_ATTR_KEY, IgniteProjectionStartStopRestartSelfTest.CUSTOM_CFG_ATTR_VAL).nodes(), ((IgniteClosure<ClusterNode, UUID>) (ClusterNode::id)));
        ignite.cluster().forAttribute(IgniteProjectionStartStopRestartSelfTest.CUSTOM_CFG_ATTR_KEY, IgniteProjectionStartStopRestartSelfTest.CUSTOM_CFG_ATTR_VAL).nodes();
        ignite.cluster().stopNodes(ids);
        assert leftLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (ignite.cluster().nodes().size()) == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStopNodeById() throws Exception {
        joinedLatch = new CountDownLatch(3);
        startCheckNodes();
        leftLatch = new CountDownLatch(1);
        ignite.cluster().stopNodes(Collections.singleton(F.first(ignite.cluster().forRemotes().nodes()).id()));
        assert leftLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (ignite.cluster().nodes().size()) == 2;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStopNodesByIds() throws Exception {
        joinedLatch = new CountDownLatch(3);
        startCheckNodes();
        leftLatch = new CountDownLatch(2);
        Iterator<ClusterNode> it = ignite.cluster().nodes().iterator();
        Collection<UUID> ids = new HashSet<>();
        ids.add(it.next().id());
        ids.add(it.next().id());
        ignite.cluster().stopNodes(ids);
        assert leftLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (ignite.cluster().nodes().size()) == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRestartNodes() throws Exception {
        joinedLatch = new CountDownLatch(3);
        startCheckNodes();
        joinedLatch = new CountDownLatch(3);
        leftLatch = new CountDownLatch(3);
        ignite.cluster().restartNodes();
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert leftLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (ignite.cluster().nodes().size()) == 3;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRestartNodesFiltered() throws Exception {
        joinedLatch = new CountDownLatch(2);
        Collection<ClusterStartNodeResult> res = startNodes(ignite.cluster(), maps(Collections.singleton(IgniteProjectionStartStopRestartSelfTest.HOST), pwd, key, 2, U.getIgniteHome(), IgniteProjectionStartStopRestartSelfTest.CFG_ATTR, null), false, 0, 16);
        assert (res.size()) == 2;
        res.forEach(( t) -> {
            assert t.getHostName().equals(HOST);
            if (!(t.isSuccess()))
                throw new IgniteException(t.getError());

        });
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        joinedLatch = new CountDownLatch(1);
        res = startNodes(ignite.cluster(), maps(Collections.singleton(IgniteProjectionStartStopRestartSelfTest.HOST), pwd, key, 3, U.getIgniteHome(), IgniteProjectionStartStopRestartSelfTest.CFG_NO_ATTR, null), false, 0, 16);
        assert (res.size()) == 1;
        res.forEach(( t) -> {
            assert t.getHostName().equals(HOST);
            if (!(t.isSuccess()))
                throw new IgniteException(t.getError());

        });
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (ignite.cluster().nodes().size()) == 3;
        joinedLatch = new CountDownLatch(2);
        leftLatch = new CountDownLatch(2);
        X.println(("Restarting nodes with " + (IgniteProjectionStartStopRestartSelfTest.CUSTOM_CFG_ATTR_KEY)));
        Collection<UUID> ids = F.transform(ignite.cluster().forAttribute(IgniteProjectionStartStopRestartSelfTest.CUSTOM_CFG_ATTR_KEY, IgniteProjectionStartStopRestartSelfTest.CUSTOM_CFG_ATTR_VAL).nodes(), ((IgniteClosure<ClusterNode, UUID>) (ClusterNode::id)));
        ignite.cluster().restartNodes(ids);
        assert leftLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (ignite.cluster().nodes().size()) == 3;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRestartNodeById() throws Exception {
        joinedLatch = new CountDownLatch(3);
        startCheckNodes();
        joinedLatch = new CountDownLatch(1);
        leftLatch = new CountDownLatch(1);
        ignite.cluster().restartNodes(Collections.singleton(F.first(ignite.cluster().forRemotes().nodes()).id()));
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert leftLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (ignite.cluster().nodes().size()) == 3;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRestartNodesByIds() throws Exception {
        joinedLatch = new CountDownLatch(3);
        startCheckNodes();
        joinedLatch = new CountDownLatch(2);
        leftLatch = new CountDownLatch(2);
        Iterator<ClusterNode> it = ignite.cluster().nodes().iterator();
        ignite.cluster().restartNodes(F.asList(it.next().id(), it.next().id()));
        assert joinedLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert leftLatch.await(IgniteProjectionStartStopRestartSelfTest.WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        assert (ignite.cluster().nodes().size()) == 3;
    }
}

