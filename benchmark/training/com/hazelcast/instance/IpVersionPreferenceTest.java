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
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.HazelcastSerialParametersRunnerFactory;
import com.hazelcast.test.OverridePropertyRule;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastSerialParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class IpVersionPreferenceTest {
    @Rule
    public final OverridePropertyRule ruleSysPropHazelcastLocalAddress = OverridePropertyRule.clear("hazelcast.local.localAddress");

    @Rule
    public final OverridePropertyRule ruleSysPropPreferIpv4Stack = OverridePropertyRule.clear(DefaultAddressPicker.PREFER_IPV4_STACK);

    @Rule
    public final OverridePropertyRule ruleSysPropPreferIpv6Addresses = OverridePropertyRule.clear(DefaultAddressPicker.PREFER_IPV6_ADDRESSES);

    @Rule
    public final OverridePropertyRule ruleSysPropPreferHzIpv4 = OverridePropertyRule.clear(GroupProperty.PREFER_IPv4_STACK.getName());

    @Parameterized.Parameter
    public Boolean hazelcastIpv4;

    @Parameterized.Parameter(1)
    public Boolean javaIpv4;

    @Parameterized.Parameter(2)
    public Boolean javaIpv6;

    private static final Boolean[] BOOL_VALUES = new Boolean[]{ Boolean.TRUE, Boolean.FALSE, null };

    @Test
    public void testBindAddress() throws Exception {
        ruleSysPropPreferHzIpv4.setOrClearProperty(((hazelcastIpv4) == null ? null : String.valueOf(hazelcastIpv4)));
        ruleSysPropPreferIpv4Stack.setOrClearProperty(((javaIpv4) == null ? null : String.valueOf(javaIpv4)));
        ruleSysPropPreferIpv6Addresses.setOrClearProperty(((javaIpv6) == null ? null : String.valueOf(javaIpv6)));
        boolean expectedIPv6 = ((!(getOrDefault(hazelcastIpv4, true))) && (!(getOrDefault(javaIpv4, false)))) && (getOrDefault(javaIpv6, false));
        if (expectedIPv6) {
            Assume.assumeNotNull(DefaultAddressPickerTest.findIPv6NonLoopbackInterface());
        }
        DefaultAddressPicker addressPicker = new DefaultAddressPicker(new Config(), Logger.getLogger(AddressPicker.class));
        try {
            addressPicker.pickAddress();
            Address bindAddress = addressPicker.getBindAddress();
            Assert.assertEquals(("Bind address: " + bindAddress), expectedIPv6, bindAddress.isIPv6());
        } finally {
            IOUtil.closeResource(addressPicker.getServerSocketChannel());
        }
    }
}

