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


import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskAdapter;
import org.apache.ignite.compute.ComputeTaskName;
import org.apache.ignite.spi.deployment.DeploymentResource;
import org.apache.ignite.testframework.junits.spi.GridSpiAbstractTest;
import org.apache.ignite.testframework.junits.spi.GridSpiTest;
import org.junit.Test;


/**
 *
 */
@GridSpiTest(spi = UriDeploymentSpi.class, group = "Deployment SPI")
public class GridUriDeploymentSimpleSelfTest extends GridSpiAbstractTest<UriDeploymentSpi> {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimpleDeploy() throws Exception {
        UriDeploymentSpi spi = getSpi();
        spi.register(GridUriDeploymentSimpleSelfTest.TestTask.class.getClassLoader(), GridUriDeploymentSimpleSelfTest.TestTask.class);
        DeploymentResource task = spi.findResource(GridUriDeploymentSimpleSelfTest.TestTask.class.getName());
        assert task != null;
        assert (task.getResourceClass()) == (GridUriDeploymentSimpleSelfTest.TestTask.class);
        assert (spi.findResource("TestTaskWithName")) == null;
        spi.unregister(GridUriDeploymentSimpleSelfTest.TestTask.class.getName());
        assert (spi.findResource(GridUriDeploymentSimpleSelfTest.TestTask.class.getName())) == null;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimpleRedeploy() throws Exception {
        for (int i = 0; i < 100; i++)
            testSimpleDeploy();

    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimpleDeployWithName() throws Exception {
        UriDeploymentSpi spi = getSpi();
        spi.register(GridUriDeploymentSimpleSelfTest.TestTaskWithName.class.getClassLoader(), GridUriDeploymentSimpleSelfTest.TestTaskWithName.class);
        DeploymentResource task = spi.findResource("TestTaskWithName");
        assert task != null;
        assert (task.getResourceClass()) == (GridUriDeploymentSimpleSelfTest.TestTaskWithName.class);
        assert (spi.findResource(GridUriDeploymentSimpleSelfTest.TestTaskWithName.class.getName())) != null;
        spi.unregister("TestTaskWithName");
        assert (spi.findResource("TestTaskWithName")) == null;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimpleRedeployWithName() throws Exception {
        for (int i = 0; i < 100; i++)
            testSimpleDeployWithName();

    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimpleDeployTwoTasks() throws Exception {
        UriDeploymentSpi spi = getSpi();
        spi.register(GridUriDeploymentSimpleSelfTest.TestTask.class.getClassLoader(), GridUriDeploymentSimpleSelfTest.TestTask.class);
        spi.register(GridUriDeploymentSimpleSelfTest.TestTaskWithName.class.getClassLoader(), GridUriDeploymentSimpleSelfTest.TestTaskWithName.class);
        DeploymentResource task1 = spi.findResource("TestTaskWithName");
        DeploymentResource task2 = spi.findResource(GridUriDeploymentSimpleSelfTest.TestTask.class.getName());
        assert task1 != null;
        assert (task1.getResourceClass()) == (GridUriDeploymentSimpleSelfTest.TestTaskWithName.class);
        assert (spi.findResource(GridUriDeploymentSimpleSelfTest.TestTaskWithName.class.getName())) != null;
        assert task2 != null;
        assert (task2.getResourceClass()) == (GridUriDeploymentSimpleSelfTest.TestTask.class);
        assert (spi.findResource("TestTask")) == null;
        spi.unregister("TestTaskWithName");
        assert (spi.findResource("TestTaskWithName")) == null;
        spi.unregister(GridUriDeploymentSimpleSelfTest.TestTask.class.getName());
        assert (spi.findResource(GridUriDeploymentSimpleSelfTest.TestTask.class.getName())) == null;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimpleRedeployTwoTasks() throws Exception {
        for (int i = 0; i < 100; i++)
            testSimpleDeployTwoTasks();

    }

    /**
     * Test task.
     */
    private static class TestTask extends ComputeTaskAdapter<Object, Object> {
        /**
         * {@inheritDoc }
         */
        @Override
        public Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> subgrid, Object arg) {
            assert (subgrid.size()) == 1;
            return Collections.singletonMap(new ComputeJobAdapter() {
                @Override
                public Serializable execute() {
                    return "result";
                }
            }, subgrid.get(0));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Object reduce(List<ComputeJobResult> results) {
            assert (results.size()) == 1;
            return results.get(0).getData();
        }
    }

    /**
     * Named test task.
     */
    @ComputeTaskName("TestTaskWithName")
    private static class TestTaskWithName extends ComputeTaskAdapter<Object, Object> {
        /**
         * {@inheritDoc }
         */
        @Override
        public Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> subgrid, Object arg) {
            assert (subgrid.size()) == 1;
            return Collections.singletonMap(new ComputeJobAdapter() {
                @Override
                public Serializable execute() {
                    return "result";
                }
            }, subgrid.get(0));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Object reduce(List<ComputeJobResult> results) {
            assert (results.size()) == 1;
            return results.get(0).getData();
        }
    }
}

