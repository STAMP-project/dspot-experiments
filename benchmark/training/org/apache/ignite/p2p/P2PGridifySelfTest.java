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


import DeploymentMode.CONTINUOUS;
import DeploymentMode.ISOLATED;
import DeploymentMode.PRIVATE;
import DeploymentMode.SHARED;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.ignite.testframework.junits.common.GridCommonTest;
import org.junit.Test;


/**
 *
 */
@SuppressWarnings({ "ProhibitedExceptionDeclared" })
@GridCommonTest(group = "P2P")
public class P2PGridifySelfTest extends GridCommonAbstractTest {
    /**
     * Current deployment mode. Used in {@link #getConfiguration(String)}.
     */
    private DeploymentMode depMode;

    /**
     * Test GridDeploymentMode.ISOLATED mode.
     *
     * @throws Exception
     * 		if error occur.
     */
    @Test
    public void testPrivateMode() throws Exception {
        processTestBothNodesDeploy(PRIVATE);
    }

    /**
     * Test GridDeploymentMode.ISOLATED mode.
     *
     * @throws Exception
     * 		if error occur.
     */
    @Test
    public void testIsolatedMode() throws Exception {
        processTestBothNodesDeploy(ISOLATED);
    }

    /**
     * Test GridDeploymentMode.CONTINUOUS mode.
     *
     * @throws Exception
     * 		if error occur.
     */
    @Test
    public void testContinuousMode() throws Exception {
        processTestBothNodesDeploy(CONTINUOUS);
    }

    /**
     * Test GridDeploymentMode.SHARED mode.
     *
     * @throws Exception
     * 		if error occur.
     */
    @Test
    public void testSharedMode() throws Exception {
        processTestBothNodesDeploy(SHARED);
    }

    /**
     * Test GridDeploymentMode.ISOLATED mode.
     *
     * @throws Exception
     * 		if error occur.
     */
    @Test
    public void testResourcePrivateMode() throws Exception {
        processTestGridifyResource(PRIVATE);
    }

    /**
     * Test GridDeploymentMode.ISOLATED mode.
     *
     * @throws Exception
     * 		if error occur.
     */
    @Test
    public void testResourceIsolatedMode() throws Exception {
        processTestGridifyResource(ISOLATED);
    }

    /**
     * Test GridDeploymentMode.CONTINUOUS mode.
     *
     * @throws Exception
     * 		if error occur.
     */
    @Test
    public void testResourceContinuousMode() throws Exception {
        processTestGridifyResource(CONTINUOUS);
    }

    /**
     * Test GridDeploymentMode.SHARED mode.
     *
     * @throws Exception
     * 		if error occur.
     */
    @Test
    public void testResourceSharedMode() throws Exception {
        processTestGridifyResource(SHARED);
    }
}

