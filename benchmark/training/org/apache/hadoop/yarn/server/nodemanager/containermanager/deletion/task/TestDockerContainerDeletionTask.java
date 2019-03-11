/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.deletion.task;


import DeletionTaskType.DOCKER_CONTAINER;
import YarnServerNodemanagerRecoveryProtos.DeletionServiceDeleteTaskProto;
import org.apache.hadoop.yarn.proto.YarnServerNodemanagerRecoveryProtos;
import org.apache.hadoop.yarn.server.nodemanager.DeletionService;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the attributes of the {@link DockerContainerDeletionTask} class.
 */
public class TestDockerContainerDeletionTask {
    private static final int ID = 0;

    private static final String USER = "user";

    private static final String CONTAINER_ID = "container_e123_123456_000001";

    private DeletionService deletionService;

    private DockerContainerDeletionTask deletionTask;

    @Test
    public void testGetUser() {
        Assert.assertEquals(TestDockerContainerDeletionTask.USER, deletionTask.getUser());
    }

    @Test
    public void testGetContainerId() {
        Assert.assertEquals(TestDockerContainerDeletionTask.CONTAINER_ID, deletionTask.getContainerId());
    }

    @Test
    public void testConvertDeletionTaskToProto() {
        YarnServerNodemanagerRecoveryProtos.DeletionServiceDeleteTaskProto proto = deletionTask.convertDeletionTaskToProto();
        Assert.assertEquals(TestDockerContainerDeletionTask.ID, proto.getId());
        Assert.assertEquals(TestDockerContainerDeletionTask.USER, proto.getUser());
        Assert.assertEquals(TestDockerContainerDeletionTask.CONTAINER_ID, proto.getDockerContainerId());
        Assert.assertEquals(DOCKER_CONTAINER.name(), proto.getTaskType());
    }
}

