/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.servicescommon;


import com.spotify.helios.ZooKeeperTestingServerManager;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.servicescommon.coordination.Paths;
import com.spotify.helios.servicescommon.coordination.ZooKeeperClient;
import java.util.UUID;
import org.apache.zookeeper.data.Stat;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ZooKeeperRegistrarServiceUtilTest {
    private static final String HOSTNAME = "host";

    private static final String ID = UUID.randomUUID().toString();

    private static final JobId JOB_ID1 = JobId.newBuilder().setName("job1").setVersion("0.1.0").build();

    private ZooKeeperTestingServerManager testingServerManager;

    private ZooKeeperClient zkClient;

    @Test
    public void testRegisterHost() throws Exception {
        final String idPath = Paths.configHostId(ZooKeeperRegistrarServiceUtilTest.HOSTNAME);
        ZooKeeperRegistrarUtil.registerHost(zkClient, idPath, ZooKeeperRegistrarServiceUtilTest.HOSTNAME, ZooKeeperRegistrarServiceUtilTest.ID);
        Assert.assertNotNull(zkClient.exists(Paths.configHost(ZooKeeperRegistrarServiceUtilTest.HOSTNAME)));
        Assert.assertNotNull(zkClient.exists(Paths.configHostJobs(ZooKeeperRegistrarServiceUtilTest.HOSTNAME)));
        Assert.assertNotNull(zkClient.exists(Paths.configHostPorts(ZooKeeperRegistrarServiceUtilTest.HOSTNAME)));
        Assert.assertNotNull(zkClient.exists(Paths.statusHost(ZooKeeperRegistrarServiceUtilTest.HOSTNAME)));
        Assert.assertNotNull(zkClient.exists(Paths.statusHostJobs(ZooKeeperRegistrarServiceUtilTest.HOSTNAME)));
        Assert.assertEquals(ZooKeeperRegistrarServiceUtilTest.ID, new String(zkClient.getData(idPath)));
    }

    @Test
    public void testDeregisterHost() throws Exception {
        final String idPath = Paths.configHostId(ZooKeeperRegistrarServiceUtilTest.HOSTNAME);
        ZooKeeperRegistrarUtil.registerHost(zkClient, idPath, ZooKeeperRegistrarServiceUtilTest.HOSTNAME, ZooKeeperRegistrarServiceUtilTest.ID);
        ZooKeeperRegistrarUtil.deregisterHost(zkClient, ZooKeeperRegistrarServiceUtilTest.HOSTNAME);
        Assert.assertNull(zkClient.exists(Paths.configHost(ZooKeeperRegistrarServiceUtilTest.HOSTNAME)));
        Assert.assertNull(zkClient.exists(Paths.statusHost(ZooKeeperRegistrarServiceUtilTest.HOSTNAME)));
    }

    // Verify that the re-registering:
    // * does not change the /config/hosts/<host> subtree, except the host-id.
    // * deletes everything under /status/hosts/<host> subtree.
    @Test
    public void testReRegisterHost() throws Exception {
        // Register the host & add some fake data to its status & config dirs
        final String idPath = Paths.configHostId(ZooKeeperRegistrarServiceUtilTest.HOSTNAME);
        ZooKeeperRegistrarUtil.registerHost(zkClient, idPath, ZooKeeperRegistrarServiceUtilTest.HOSTNAME, ZooKeeperRegistrarServiceUtilTest.ID);
        zkClient.ensurePath(Paths.statusHostJob(ZooKeeperRegistrarServiceUtilTest.HOSTNAME, ZooKeeperRegistrarServiceUtilTest.JOB_ID1));
        zkClient.ensurePath(Paths.configHostJob(ZooKeeperRegistrarServiceUtilTest.HOSTNAME, ZooKeeperRegistrarServiceUtilTest.JOB_ID1));
        final Stat jobConfigStat = zkClient.stat(Paths.configHostJob(ZooKeeperRegistrarServiceUtilTest.HOSTNAME, ZooKeeperRegistrarServiceUtilTest.JOB_ID1));
        // ... and then re-register it
        final String newId = UUID.randomUUID().toString();
        ZooKeeperRegistrarUtil.reRegisterHost(zkClient, ZooKeeperRegistrarServiceUtilTest.HOSTNAME, newId);
        // Verify that the host-id was updated
        Assert.assertEquals(newId, new String(zkClient.getData(idPath)));
        // Verify that /status/hosts/<host>/jobs exists and is EMPTY
        Assert.assertNotNull(zkClient.exists(Paths.statusHostJobs(ZooKeeperRegistrarServiceUtilTest.HOSTNAME)));
        Assert.assertThat(zkClient.listRecursive(Paths.statusHostJobs(ZooKeeperRegistrarServiceUtilTest.HOSTNAME)), Matchers.contains(Paths.statusHostJobs(ZooKeeperRegistrarServiceUtilTest.HOSTNAME)));
        // Verify that re-registering didn't change the nodes in /config/hosts/<host>/jobs
        Assert.assertEquals(jobConfigStat, zkClient.stat(Paths.configHostJob(ZooKeeperRegistrarServiceUtilTest.HOSTNAME, ZooKeeperRegistrarServiceUtilTest.JOB_ID1)));
    }
}

