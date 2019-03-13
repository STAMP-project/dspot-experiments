/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone;


import HddsProtos.ReplicationFactor.ONE;
import HddsProtos.ReplicationType.STAND_ALONE;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.client.ScmClient;
import org.apache.hadoop.hdds.scm.container.common.helpers.ContainerWithPipeline;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests container operations (TODO currently only supports create)
 * from cblock clients.
 */
public class TestContainerOperations {
    private static ScmClient storageClient;

    private static MiniOzoneCluster cluster;

    private static OzoneConfiguration ozoneConf;

    /**
     * A simple test to create a container with {@link ContainerOperationClient}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreate() throws Exception {
        ContainerWithPipeline container = TestContainerOperations.storageClient.createContainer(STAND_ALONE, ONE, "OZONE");
        Assert.assertEquals(container.getContainerInfo().getContainerID(), TestContainerOperations.storageClient.getContainer(container.getContainerInfo().getContainerID()).getContainerID());
    }
}

