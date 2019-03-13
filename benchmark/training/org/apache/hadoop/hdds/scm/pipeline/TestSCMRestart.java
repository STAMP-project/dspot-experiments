/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.hdds.scm.pipeline;


import java.io.IOException;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.container.ContainerInfo;
import org.apache.hadoop.hdds.scm.container.ContainerManager;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test SCM restart and recovery wrt pipelines.
 */
public class TestSCMRestart {
    private static MiniOzoneCluster cluster;

    private static OzoneConfiguration conf;

    private static Pipeline ratisPipeline1;

    private static Pipeline ratisPipeline2;

    private static ContainerManager containerManager;

    private static ContainerManager newContainerManager;

    private static PipelineManager pipelineManager;

    @Test
    public void testPipelineWithScmRestart() throws IOException {
        // After restart make sure that the pipeline are still present
        Pipeline ratisPipeline1AfterRestart = TestSCMRestart.pipelineManager.getPipeline(TestSCMRestart.ratisPipeline1.getId());
        Pipeline ratisPipeline2AfterRestart = TestSCMRestart.pipelineManager.getPipeline(TestSCMRestart.ratisPipeline2.getId());
        Assert.assertNotSame(ratisPipeline1AfterRestart, TestSCMRestart.ratisPipeline1);
        Assert.assertNotSame(ratisPipeline2AfterRestart, TestSCMRestart.ratisPipeline2);
        Assert.assertEquals(ratisPipeline1AfterRestart, TestSCMRestart.ratisPipeline1);
        Assert.assertEquals(ratisPipeline2AfterRestart, TestSCMRestart.ratisPipeline2);
        // Try creating a new container, it should be from the same pipeline
        // as was before restart
        ContainerInfo containerInfo = TestSCMRestart.newContainerManager.allocateContainer(RATIS, THREE, "Owner1");
        Assert.assertEquals(containerInfo.getPipelineID(), TestSCMRestart.ratisPipeline1.getId());
    }
}

