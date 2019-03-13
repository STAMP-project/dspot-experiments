/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.instance;


import com.hazelcast.config.Config;
import com.hazelcast.instance.DefaultAddressPicker.HostnameResolver;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.test.OverridePropertyRule;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


// See https://github.com/powermock/powermock/wiki/Mock-System
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DefaultAddressPicker.class })
@Category({ QuickTest.class, ParallelTest.class })
public class DefaultAddressPickerHostnameTest {
    @Rule
    public final OverridePropertyRule ruleSysPropPreferIpv4 = OverridePropertyRule.set(DefaultAddressPicker.PREFER_IPV4_STACK, "true");

    @Rule
    public final OverridePropertyRule ruleSysPropHazelcastLocalAddress = OverridePropertyRule.clear("hazelcast.local.localAddress");

    private final ILogger logger = Logger.getLogger(AddressPicker.class);

    private final Config config = new Config();

    private final String theHostname = "hazelcast.istanbul";

    private final String theAddress = "10.34.34.0";

    private final DefaultAddressPicker addressPicker = new DefaultAddressPicker(config, logger);

    private final HostnameResolver hostnameResolver = new DefaultAddressPickerHostnameTest.MockHostnameResolver();

    @Test
    public void whenHostnameIsLocal_thenSelectHostname() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(DefaultAddressPickerHostnameTest.createNetworkInterface("en0", "192.168.1.100"));
        networkInterfaces.add(DefaultAddressPickerHostnameTest.createNetworkInterface("en1", theAddress));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        addressPicker.pickAddress();
        Assert.assertEquals(theHostname, addressPicker.getBindAddress().getHost());
    }

    @Test
    public void whenHostnameIsNotLocal_thenSelectAnotherAddress() throws Exception {
        NetworkInterface en0 = DefaultAddressPickerHostnameTest.createNetworkInterface("en0", "192.168.1.100");
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(Collections.singleton(en0)));
        addressPicker.pickAddress();
        Assert.assertNotEquals(theHostname, addressPicker.getBindAddress().getHost());
    }

    @Test
    public void whenHostnameIsLocal_andInterfacesMatchingHostname_thenSelectHostname() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(DefaultAddressPickerHostnameTest.createNetworkInterface("en0", "192.168.1.100"));
        networkInterfaces.add(DefaultAddressPickerHostnameTest.createNetworkInterface("en1", theAddress));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        enableInterfacesConfig("10.34.34.*");
        addressPicker.pickAddress();
        Assert.assertEquals(theHostname, addressPicker.getBindAddress().getHost());
    }

    @Test
    public void whenHostnameIsLocal_andInterfacesNotMatchingAny_thenFail() throws Exception {
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        networkInterfaces.add(DefaultAddressPickerHostnameTest.createNetworkInterface("en0", "192.168.1.100"));
        networkInterfaces.add(DefaultAddressPickerHostnameTest.createNetworkInterface("en1", theAddress));
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(networkInterfaces));
        enableInterfacesConfig("10.34.19.*");
        try {
            addressPicker.pickAddress();
            Assert.fail("Address selection should fail, since no matching network interface found.");
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void whenHostnameIsNotLocal_andInterfacesMatchingHostname_thenFail() throws Exception {
        NetworkInterface en0 = DefaultAddressPickerHostnameTest.createNetworkInterface("en0", "192.168.1.100");
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(Collections.singleton(en0)));
        enableInterfacesConfig("10.34.*.*");
        try {
            addressPicker.pickAddress();
            Assert.fail("Address selection should fail, since no matching network interface found.");
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void whenHostnameIsNotLocal_andInterfacesMatchingAnother_thenSelectAnotherAddress() throws Exception {
        String address = "192.168.1.100";
        NetworkInterface en0 = DefaultAddressPickerHostnameTest.createNetworkInterface("en0", address);
        Mockito.when(NetworkInterface.getNetworkInterfaces()).thenReturn(Collections.enumeration(Collections.singleton(en0)));
        enableInterfacesConfig("192.168.*.*");
        addressPicker.pickAddress();
        Assert.assertEquals(address, addressPicker.getBindAddress().getHost());
    }

    private class MockHostnameResolver implements HostnameResolver {
        @Override
        public Collection<String> resolve(String hostname) throws UnknownHostException {
            if (theHostname.equals(hostname)) {
                return Collections.singleton(theAddress);
            }
            throw new UnknownHostException(hostname);
        }
    }
}

