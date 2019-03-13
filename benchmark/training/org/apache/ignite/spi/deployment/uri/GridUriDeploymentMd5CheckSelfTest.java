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
package org.apache.ignite.spi.deployment.uri;


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.spi.deployment.DeploymentResource;
import org.apache.ignite.testframework.junits.spi.GridSpiTest;
import org.junit.Test;


/**
 * Redundancy for URI deployment test
 */
@GridSpiTest(spi = UriDeploymentSpi.class, group = "Deployment SPI")
public class GridUriDeploymentMd5CheckSelfTest extends GridUriDeploymentAbstractSelfTest {
    /**
     * Used to count number of unit undeployments.
     */
    private AtomicInteger undeployCntr = new AtomicInteger();

    /**
     * Test skipping fresh deployment of duplicated .gar files.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testMd5FileCheck() throws Exception {
        undeployCntr.set(0);
        String taskName = "GridUriDeploymentTestWithNameTask7";
        DeploymentResource task = getSpi().findResource(taskName);
        assert task == null;
        GridUriDeploymentMd5CheckSelfTest.atomicCopy(getGarFile(), getDeployDir(), "uri1.gar");
        waitForTask(taskName, true, 10000);
        assert (undeployCntr.get()) == 0;
        GridUriDeploymentMd5CheckSelfTest.atomicCopy(getGarFile(), getDeployDir(), "uri2.gar");
        waitForTask(taskName, true, 10000);
        assert (undeployCntr.get()) == 0;
    }

    /**
     * Test skipping fresh deployment of .gar directories with equal content.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testMd5DirectoryCheck() throws Exception {
        undeployCntr.set(0);
        String taskName = "GridUriDeploymentTestWithNameTask6";
        DeploymentResource task = getSpi().findResource(taskName);
        assert task == null;
        GridUriDeploymentMd5CheckSelfTest.atomicCopy(getGarDir(), getDeployDir(), "uri1.gar");
        waitForTask(taskName, true, 10000);
        assert (undeployCntr.get()) == 0;
        GridUriDeploymentMd5CheckSelfTest.atomicCopy(getGarDir(), getDeployDir(), "uri2.gar");
        waitForTask(taskName, true, 10000);
        assert (undeployCntr.get()) == 0;
    }
}

