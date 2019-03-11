/**
 * Copyright 2019 Google LLC.  All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.examples.bigtable;


import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.bigtable.admin.v2.BigtableInstanceAdminClient;
import com.google.cloud.bigtable.admin.v2.models.Cluster;
import com.google.cloud.bigtable.admin.v2.models.Instance;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for {@link InstanceAdminExample}
 */
public class ITInstanceAdminExample {
    private static final String PROJECT_PROPERTY_NAME = "bigtable.project";

    private static final String ID_PREFIX = "instanceadmin";

    private static final String CLUSTER = "cluster";

    private static String projectId;

    private static BigtableInstanceAdminClient adminClient;

    private String clusterId;

    private String instanceId;

    private InstanceAdminExample instanceAdmin;

    @Test
    public void testCreateAndDeleteInstance() throws IOException {
        // Creates an instance.
        String testInstance = ITInstanceAdminExample.generateId();
        String testCluster = ITInstanceAdminExample.generateId();
        InstanceAdminExample testInstanceAdmin = new InstanceAdminExample(ITInstanceAdminExample.projectId, testInstance, testCluster);
        testInstanceAdmin.createProdInstance();
        Assert.assertTrue(ITInstanceAdminExample.adminClient.exists(testInstance));
        // Deletes an instance.
        testInstanceAdmin.deleteInstance();
        Assert.assertFalse(ITInstanceAdminExample.adminClient.exists(testInstance));
    }

    @Test
    public void testGetInstance() {
        // Gets an instance.
        Instance instance = instanceAdmin.getInstance();
        Assert.assertNotNull(instance);
    }

    @Test(expected = NotFoundException.class)
    public void testAddAndDeleteCluster() {
        // Adds a cluster.
        instanceAdmin.addCluster();
        Cluster cluster = ITInstanceAdminExample.adminClient.getCluster(instanceId, ITInstanceAdminExample.CLUSTER);
        Assert.assertNotNull(cluster);
        // Deletes a cluster.
        instanceAdmin.deleteCluster();
        ITInstanceAdminExample.adminClient.getCluster(instanceId, ITInstanceAdminExample.CLUSTER);
    }

    // TODO: add test for instanceAdmin.listInstances()
    // TODO: and test for instanceAdmin.listClusters()
    @Test
    public void testRunDoesNotFail() {
        instanceAdmin.run();
    }
}

