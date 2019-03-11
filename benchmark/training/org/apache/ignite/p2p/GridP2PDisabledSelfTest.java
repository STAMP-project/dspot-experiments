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


import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.ignite.testframework.junits.common.GridCommonTest;
import org.junit.Test;


/**
 * Test what happens if peer class loading is disabled.
 * <p>
 * In order for this test to run, make sure that your
 * {@code p2p.uri.cls} folder ends with {@code .gar} extension.
 */
@SuppressWarnings({ "ProhibitedExceptionDeclared" })
@GridCommonTest(group = "P2P")
public class GridP2PDisabledSelfTest extends GridCommonAbstractTest {
    /**
     * Task name.
     */
    private static final String TASK_NAME = "org.apache.ignite.tests.p2p.P2PTestTaskExternalPath1";

    /**
     * External class loader.
     */
    private static ClassLoader extLdr;

    /**
     * Current deployment mode. Used in {@link #getConfiguration(String)}.
     */
    private DeploymentMode depMode;

    /**
     *
     */
    private boolean initGar;

    /**
     * Path to GAR file.
     */
    private String garFile;

    /**
     *
     */
    public GridP2PDisabledSelfTest() {
        /* start grid */
        super(false);
    }

    /**
     * Test GridDeploymentMode.ISOLATED mode.
     *
     * @throws Exception
     * 		if error occur.
     */
    @Test
    public void testGarPrivateMode() throws Exception {
        depMode = DeploymentMode.PRIVATE;
        checkGar();
    }

    /**
     * Test GridDeploymentMode.ISOLATED mode.
     *
     * @throws Exception
     * 		if error occur.
     */
    @Test
    public void testGarIsolatedMode() throws Exception {
        depMode = DeploymentMode.ISOLATED;
        checkGar();
    }

    /**
     * Test GridDeploymentMode.CONTINUOUS mode.
     *
     * @throws Exception
     * 		if error occur.
     */
    @Test
    public void testGarContinuousMode() throws Exception {
        depMode = DeploymentMode.CONTINUOUS;
        checkGar();
    }

    /**
     * Test GridDeploymentMode.SHARED mode.
     *
     * @throws Exception
     * 		if error occur.
     */
    @Test
    public void testGarSharedMode() throws Exception {
        depMode = DeploymentMode.SHARED;
        checkGar();
    }
}

