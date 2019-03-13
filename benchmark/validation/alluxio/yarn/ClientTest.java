/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.yarn;


import ExceptionMessage.YARN_NOT_ENOUGH_RESOURCES;
import PropertyKey.INTEGRATION_MASTER_RESOURCE_CPU;
import PropertyKey.INTEGRATION_MASTER_RESOURCE_MEM;
import PropertyKey.INTEGRATION_WORKER_RESOURCE_CPU;
import PropertyKey.INTEGRATION_WORKER_RESOURCE_MEM;
import PropertyKey.WORKER_MEMORY_SIZE;
import alluxio.ConfigurationTestUtils;
import alluxio.Constants;
import alluxio.conf.InstancedConfiguration;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Unit tests for {@link Client}.
 * TODO(xuan gong): ALLUXIO-1503: add more unit test for alluxio.yarn.Client
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ YarnClient.class })
public final class ClientTest {
    private YarnClient mYarnClient;

    private InstancedConfiguration mConf = ConfigurationTestUtils.defaults();

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    @Test
    public void notEnoughMemoryForApplicationMaster() throws Exception {
        int appMasterMem = 1024;
        Resource resource = Resource.newInstance((appMasterMem / 2), 4);
        generateMaxAllocation(resource);
        mThrown.expect(RuntimeException.class);
        mThrown.expectMessage(YARN_NOT_ENOUGH_RESOURCES.getMessage("ApplicationMaster", "memory", appMasterMem, resource.getMemory()));
        String[] args = new String[]{ "-resource_path", "test", "-am_memory", Integer.toString(appMasterMem), "-am_vcores", "2" };
        Client client = new Client(args, mConf);
        client.run();
    }

    @Test
    public void notEnoughVCoreForApplicationMaster() throws Exception {
        int appMasterMem = 1024;
        int appMasterCore = 2;
        Resource resource = Resource.newInstance(appMasterMem, (appMasterCore / 2));
        generateMaxAllocation(resource);
        mThrown.expect(RuntimeException.class);
        mThrown.expectMessage(YARN_NOT_ENOUGH_RESOURCES.getMessage("ApplicationMaster", "virtual cores", appMasterCore, resource.getVirtualCores()));
        String[] args = new String[]{ "-resource_path", "test", "-am_memory", Integer.toString(appMasterMem), "-am_vcores", Integer.toString(appMasterCore) };
        Client client = new Client(args, mConf);
        client.run();
    }

    @Test
    public void notEnoughMemoryForAlluxioMaster() throws Exception {
        mConf.set(INTEGRATION_MASTER_RESOURCE_MEM, "2048.00MB");
        mConf.set(INTEGRATION_MASTER_RESOURCE_CPU, "4");
        int masterMemInMB = ((int) ((mConf.getBytes(INTEGRATION_MASTER_RESOURCE_MEM)) / (Constants.MB)));
        Resource resource = Resource.newInstance((masterMemInMB / 2), 4);
        generateMaxAllocation(resource);
        mThrown.expect(RuntimeException.class);
        mThrown.expectMessage(YARN_NOT_ENOUGH_RESOURCES.getMessage("Alluxio Master", "memory", masterMemInMB, resource.getMemory()));
        Client client = new Client(mConf);
        client.run();
    }

    @Test
    public void notEnoughVCoreForAlluxioMaster() throws Exception {
        mConf.set(INTEGRATION_MASTER_RESOURCE_MEM, "2048.00MB");
        mConf.set(INTEGRATION_MASTER_RESOURCE_CPU, "4");
        int masterMemInMB = ((int) ((mConf.getBytes(INTEGRATION_MASTER_RESOURCE_MEM)) / (Constants.MB)));
        int masterVCores = mConf.getInt(INTEGRATION_MASTER_RESOURCE_CPU);
        Resource resource = Resource.newInstance(masterMemInMB, 3);
        generateMaxAllocation(resource);
        mThrown.expect(RuntimeException.class);
        mThrown.expectMessage(YARN_NOT_ENOUGH_RESOURCES.getMessage("Alluxio Master", "virtual cores", masterVCores, resource.getVirtualCores()));
        Client client = new Client(mConf);
        client.run();
    }

    @Test
    public void notEnoughMemoryForAlluxioWorker() throws Exception {
        mConf.set(INTEGRATION_WORKER_RESOURCE_MEM, "2048.00MB");
        mConf.set(WORKER_MEMORY_SIZE, "4096.00MB");
        mConf.set(INTEGRATION_WORKER_RESOURCE_CPU, "8");
        int workerMemInMB = ((int) ((mConf.getBytes(INTEGRATION_WORKER_RESOURCE_MEM)) / (Constants.MB)));
        int ramdiskMemInMB = ((int) ((mConf.getBytes(WORKER_MEMORY_SIZE)) / (Constants.MB)));
        Resource resource = Resource.newInstance(((workerMemInMB + ramdiskMemInMB) / 2), 4);
        generateMaxAllocation(resource);
        mThrown.expect(RuntimeException.class);
        mThrown.expectMessage(YARN_NOT_ENOUGH_RESOURCES.getMessage("Alluxio Worker", "memory", (workerMemInMB + ramdiskMemInMB), resource.getMemory()));
        Client client = new Client(mConf);
        client.run();
    }

    @Test
    public void notEnoughVCoreForAlluxioWorker() throws Exception {
        mConf.set(INTEGRATION_WORKER_RESOURCE_MEM, "2048.00MB");
        mConf.set(WORKER_MEMORY_SIZE, "4096.00MB");
        mConf.set(INTEGRATION_WORKER_RESOURCE_CPU, "8");
        int workerMemInMB = ((int) ((mConf.getBytes(INTEGRATION_WORKER_RESOURCE_MEM)) / (Constants.MB)));
        int ramdiskMemInMB = ((int) ((mConf.getBytes(WORKER_MEMORY_SIZE)) / (Constants.MB)));
        int workerVCore = mConf.getInt(INTEGRATION_WORKER_RESOURCE_CPU);
        Resource resource = Resource.newInstance((workerMemInMB + ramdiskMemInMB), 4);
        generateMaxAllocation(resource);
        mThrown.expect(RuntimeException.class);
        mThrown.expectMessage(YARN_NOT_ENOUGH_RESOURCES.getMessage("Alluxio Worker", "virtual cores", workerVCore, resource.getVirtualCores()));
        Client client = new Client(mConf);
        client.run();
    }
}

