/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.ozone.web;


import java.io.IOException;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.TestOzoneHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test ozone volume in the distributed storage handler scenario.
 */
public class TestOzoneVolumes extends TestOzoneHelper {
    private static final Logger LOG = LoggerFactory.getLogger(TestOzoneVolumes.class);

    /**
     * Set the timeout for every test.
     */
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    private static MiniOzoneCluster cluster = null;

    private static int port = 0;

    /**
     * Creates Volumes on Ozone Store.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateVolumes() throws IOException {
        super.testCreateVolumes(TestOzoneVolumes.port);
        Assert.assertEquals(0, TestOzoneVolumes.cluster.getOzoneManager().getMetrics().getNumVolumeCreateFails());
    }

    /**
     * Create Volumes with Quota.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateVolumesWithQuota() throws IOException {
        super.testCreateVolumesWithQuota(TestOzoneVolumes.port);
        Assert.assertEquals(0, TestOzoneVolumes.cluster.getOzoneManager().getMetrics().getNumVolumeCreateFails());
    }

    /**
     * Create Volumes with Invalid Quota.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateVolumesWithInvalidQuota() throws IOException {
        super.testCreateVolumesWithInvalidQuota(TestOzoneVolumes.port);
        Assert.assertEquals(0, TestOzoneVolumes.cluster.getOzoneManager().getMetrics().getNumVolumeCreateFails());
    }

    /**
     * To create a volume a user name must be specified using OZONE_USER header.
     * This test verifies that we get an error in case we call without a OZONE
     * user name.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateVolumesWithInvalidUser() throws IOException {
        super.testCreateVolumesWithInvalidUser(TestOzoneVolumes.port);
        Assert.assertEquals(0, TestOzoneVolumes.cluster.getOzoneManager().getMetrics().getNumVolumeCreateFails());
    }

    /**
     * Only Admins can create volumes in Ozone. This test uses simple userauth as
     * backend and hdfs and root are admin users in the simple backend.
     * <p>
     * This test tries to create a volume as user bilbo.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateVolumesWithOutAdminRights() throws IOException {
        super.testCreateVolumesWithOutAdminRights(TestOzoneVolumes.port);
        Assert.assertEquals(0, TestOzoneVolumes.cluster.getOzoneManager().getMetrics().getNumVolumeCreateFails());
    }

    /**
     * Create a bunch of volumes in a loop.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateVolumesInLoop() throws IOException {
        super.testCreateVolumesInLoop(TestOzoneVolumes.port);
        Assert.assertEquals(0, TestOzoneVolumes.cluster.getOzoneManager().getMetrics().getNumVolumeCreateFails());
    }
}

