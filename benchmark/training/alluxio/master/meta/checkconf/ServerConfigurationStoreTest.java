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
package alluxio.master.meta.checkconf;


import alluxio.grpc.ConfigProperty;
import alluxio.wire.Address;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link ServerConfigurationStore}.
 */
public class ServerConfigurationStoreTest {
    private List<ConfigProperty> mConfigListOne;

    private List<ConfigProperty> mConfigListTwo;

    private Address mAddressOne;

    private Address mAddressTwo;

    @Test
    public void registerNewConf() {
        ServerConfigurationStore configStore = createConfigStore();
        Map<Address, List<ConfigRecord>> confMap = configStore.getConfMap();
        Assert.assertTrue(confMap.containsKey(mAddressOne));
        Assert.assertTrue(confMap.containsKey(mAddressTwo));
    }

    @Test
    public void registerNewConfUnknownProperty() {
        Address testAddress = new Address("test", 0);
        ServerConfigurationStore configStore = new ServerConfigurationStore();
        configStore.registerNewConf(testAddress, Arrays.asList(ConfigProperty.newBuilder().setName("unknown.property").build()));
        Map<Address, List<ConfigRecord>> confMap = configStore.getConfMap();
        Assert.assertTrue(confMap.containsKey(testAddress));
        Assert.assertEquals("unknown.property", confMap.get(testAddress).get(0).getKey().getName());
    }

    @Test
    public void detectNodeLost() {
        ServerConfigurationStore configStore = createConfigStore();
        configStore.handleNodeLost(mAddressOne);
        Map<Address, List<ConfigRecord>> confMap = configStore.getConfMap();
        Assert.assertFalse(confMap.containsKey(mAddressOne));
        Assert.assertTrue(confMap.containsKey(mAddressTwo));
    }

    @Test
    public void lostNodeFound() {
        ServerConfigurationStore configStore = createConfigStore();
        configStore.handleNodeLost(mAddressOne);
        configStore.handleNodeLost(mAddressTwo);
        Map<Address, List<ConfigRecord>> confMap = configStore.getConfMap();
        Assert.assertFalse(confMap.containsKey(mAddressOne));
        Assert.assertFalse(confMap.containsKey(mAddressTwo));
        configStore.lostNodeFound(mAddressTwo);
        confMap = configStore.getConfMap();
        Assert.assertFalse(confMap.containsKey(mAddressOne));
        Assert.assertTrue(confMap.containsKey(mAddressTwo));
    }
}

