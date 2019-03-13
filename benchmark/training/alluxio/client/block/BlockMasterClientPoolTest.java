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
package alluxio.client.block;


import BlockMasterClient.Factory;
import alluxio.ClientContext;
import alluxio.ConfigurationTestUtils;
import alluxio.conf.InstancedConfiguration;
import alluxio.master.MasterClientContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Factory.class)
public class BlockMasterClientPoolTest {
    private InstancedConfiguration mConf = ConfigurationTestUtils.defaults();

    @Test
    public void create() throws Exception {
        BlockMasterClient expectedClient = Mockito.mock(BlockMasterClient.class);
        PowerMockito.mockStatic(Factory.class);
        Mockito.when(Factory.create(ArgumentMatchers.any(MasterClientContext.class))).thenReturn(expectedClient);
        BlockMasterClient client;
        try (BlockMasterClientPool pool = new BlockMasterClientPool(ClientContext.create(mConf), null)) {
            client = pool.acquire();
            Assert.assertEquals(expectedClient, client);
            pool.release(client);
        }
        Mockito.verify(client).close();
    }
}

