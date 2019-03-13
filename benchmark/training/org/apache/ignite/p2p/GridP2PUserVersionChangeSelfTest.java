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
package org.apache.ignite.p2p;


import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.events.Event;
import org.apache.ignite.testframework.GridTestExternalClassLoader;
import org.apache.ignite.testframework.config.GridTestProperties;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * The test does the following:
 *
 * 1. The test should execute a task in SHARED_DEPLOY mode, restart a node with new version and make sure that a
 *      new class loader is created on remote node.
 * 2. The test should execute a task in SHARED_DEPLOY mode, restart a node with same version and make sure
 *      that the same class loader is created on remote node.
 * 3. The test should execute a task in SHARED_UNDEPLOY mode, restart a node with same version and
 *      make sure that a new class loader is created on remote node.
 */
@SuppressWarnings({ "ProhibitedExceptionDeclared", "unchecked" })
public class GridP2PUserVersionChangeSelfTest extends GridCommonAbstractTest {
    /**
     * Current deployment mode.
     */
    private DeploymentMode depMode;

    /**
     * Test task class name.
     */
    private static final String TEST_TASK_NAME = "org.apache.ignite.tests.p2p.P2PTestTaskExternalPath1";

    /**
     * Test resource class name.
     */
    private static final String TEST_RCRS_NAME = "org.apache.ignite.tests.p2p.TestUserResource";

    /**
     *
     */
    public GridP2PUserVersionChangeSelfTest() {
        /* start grid */
        super(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testRedeployVersionChangeContinuousMode() throws Exception {
        depMode = DeploymentMode.CONTINUOUS;
        checkRedeployVersionChange();
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testRedeployVersionChangeSharedMode() throws Exception {
        depMode = DeploymentMode.SHARED;
        checkRedeployVersionChange();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRedeployOnNodeRestartContinuousMode() throws Exception {
        depMode = DeploymentMode.CONTINUOUS;
        try {
            Ignite ignite1 = startGrid(1);
            Ignite ignite2 = startGrid(2);
            GridTestExternalClassLoader ldr = new GridTestExternalClassLoader(new URL[]{ new URL(GridTestProperties.getProperty("p2p.uri.cls")) });
            Class task1 = ldr.loadClass(GridP2PUserVersionChangeSelfTest.TEST_TASK_NAME);
            final CountDownLatch undeployed = new CountDownLatch(1);
            ignite2.events().localListen(new org.apache.ignite.lang.IgnitePredicate<Event>() {
                @Override
                public boolean apply(Event evt) {
                    if (((evt.type()) == (EVT_TASK_UNDEPLOYED)) && (alias().equals(GridP2PUserVersionChangeSelfTest.TEST_TASK_NAME)))
                        undeployed.countDown();

                    return true;
                }
            }, EVT_TASK_UNDEPLOYED);
            Integer res1 = ((Integer) (ignite1.compute().execute(task1, ignite2.cluster().localNode().id())));
            stopGrid(1);
            ignite1 = startGrid(1);
            Integer res2 = ((Integer) (ignite1.compute().execute(task1, ignite2.cluster().localNode().id())));
            assert !(undeployed.await(3000, TimeUnit.MILLISECONDS));
            assert res1.equals(res2);
        } finally {
            stopGrid(1);
            stopGrid(2);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRedeployOnNodeRestartSharedMode() throws Exception {
        depMode = DeploymentMode.SHARED;
        try {
            Ignite ignite1 = startGrid(1);
            Ignite ignite2 = startGrid(2);
            GridTestExternalClassLoader ldr = new GridTestExternalClassLoader(new URL[]{ new URL(GridTestProperties.getProperty("p2p.uri.cls")) });
            Class task1 = ldr.loadClass(GridP2PUserVersionChangeSelfTest.TEST_TASK_NAME);
            final CountDownLatch undeployed = new CountDownLatch(1);
            ignite2.events().localListen(new org.apache.ignite.lang.IgnitePredicate<Event>() {
                @Override
                public boolean apply(Event evt) {
                    if (((evt.type()) == (EVT_TASK_UNDEPLOYED)) && (alias().equals(GridP2PUserVersionChangeSelfTest.TEST_TASK_NAME)))
                        undeployed.countDown();

                    return true;
                }
            }, EVT_TASK_UNDEPLOYED);
            final CountDownLatch discoLatch = new CountDownLatch(1);
            ignite2.events().localListen(new org.apache.ignite.lang.IgnitePredicate<Event>() {
                @Override
                public boolean apply(Event evt) {
                    if (((evt.type()) == (EVT_NODE_LEFT)) || ((evt.type()) == (EVT_NODE_FAILED)))
                        discoLatch.countDown();

                    return true;
                }
            }, EVT_NODE_LEFT, EVT_NODE_FAILED);
            Integer res1 = ((Integer) (ignite1.compute().execute(task1, ignite2.cluster().localNode().id())));
            stopGrid(1);
            assert discoLatch.await(1000, TimeUnit.MILLISECONDS);
            assert undeployed.await(1000, TimeUnit.MILLISECONDS);
            ignite1 = startGrid(1);
            Integer res2 = ((Integer) (ignite1.compute().execute(task1, ignite2.cluster().localNode().id())));
            assert !(res1.equals(res2));
        } finally {
            stopGrid(1);
            stopGrid(2);
        }
    }
}

