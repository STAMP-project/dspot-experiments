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


import DefaultAddressPicker.PREFER_IPV4_STACK;
import DefaultAddressPicker.PREFER_IPV6_ADDRESSES;
import GroupProperty.PREFER_IPv4_STACK;
import com.hazelcast.config.Config;
import com.hazelcast.instance.DefaultAddressPicker.AddressDefinition;
import com.hazelcast.instance.DefaultAddressPicker.InterfaceDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.OverridePropertyRule;
import com.hazelcast.test.annotation.QuickTest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class })
public class DefaultAddressPickerTest {
    private static final String PUBLIC_HOST = "www.hazelcast.org";

    private static final String HAZELCAST_LOCAL_ADDRESS_PROP = "hazelcast.local.localAddress";

    @Rule
    public final OverridePropertyRule ruleSysPropHazelcastLocalAddress = OverridePropertyRule.clear(DefaultAddressPickerTest.HAZELCAST_LOCAL_ADDRESS_PROP);

    @Rule
    public final OverridePropertyRule ruleSysPropPreferIpv4Stack = OverridePropertyRule.set(DefaultAddressPicker.PREFER_IPV4_STACK, "false");

    @Rule
    public final OverridePropertyRule ruleSysPropPreferIpv6Addresses = OverridePropertyRule.clear(DefaultAddressPicker.PREFER_IPV6_ADDRESSES);

    private ILogger logger = Logger.getLogger(AddressPicker.class);

    private Config config = new Config();

    private AddressPicker addressPicker;

    private InetAddress loopback;

    @Test
    public void testBindAddress_withDefaultPortAndLoopbackAddress() throws Exception {
        config.setProperty(DefaultAddressPickerTest.HAZELCAST_LOCAL_ADDRESS_PROP, loopback.getHostAddress());
        testBindAddress(loopback);
    }

    @Test
    public void testBindAddress_withCustomPortAndLoopbackAddress() throws Exception {
        config.setProperty(DefaultAddressPickerTest.HAZELCAST_LOCAL_ADDRESS_PROP, loopback.getHostAddress());
        int port = 6789;
        config.getNetworkConfig().setPort(port);
        testBindAddress(loopback);
    }

    @Test
    public void testBindAddress_withIPv4NonLoopbackAddressViaInterfaces() throws Exception {
        InetAddress address = DefaultAddressPickerTest.findIPv4NonLoopbackInterface();
        Assume.assumeNotNull(address);
        config.getNetworkConfig().getInterfaces().setEnabled(true).clear().addInterface(address.getHostAddress());
        testBindAddress(address);
    }

    @Test
    public void testBindAddress_withIPv6NonLoopbackAddressViaInterfaces() throws Exception {
        InetAddress address = DefaultAddressPickerTest.findIPv6NonLoopbackInterface();
        Assume.assumeNotNull(address);
        config.setProperty(PREFER_IPv4_STACK.getName(), "false");
        config.getNetworkConfig().getInterfaces().setEnabled(true).clear().addInterface(address.getHostAddress());
        testBindAddress(address);
    }

    @Test
    public void testBindAddress_withIpv4NonLoopbackAddressViaTCPMembers() throws Exception {
        InetAddress address = DefaultAddressPickerTest.findIPv4NonLoopbackInterface();
        Assume.assumeNotNull(address);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true).clear().addMember(address.getHostAddress());
        testBindAddress(address);
    }

    @Test
    public void testBindAddress_withIPv6NonLoopbackAddressViaTCPMembers() throws Exception {
        InetAddress address = DefaultAddressPickerTest.findIPv6NonLoopbackInterface();
        Assume.assumeNotNull(address);
        config.setProperty(PREFER_IPv4_STACK.getName(), "false");
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true).clear().addMember(DefaultAddressPickerTest.getHostAddress(address));
        testBindAddress(address);
    }

    @Test
    public void testBindAddress_withNonLoopbackAddressViaSystemProperty() throws Exception {
        InetAddress address = DefaultAddressPickerTest.findAnyNonLoopbackInterface();
        Assume.assumeNotNull(address);
        config.setProperty(DefaultAddressPickerTest.HAZELCAST_LOCAL_ADDRESS_PROP, DefaultAddressPickerTest.getHostAddress(address));
        testBindAddress(address);
    }

    @Test
    public void testBindAddress_withEphemeralPort() throws Exception {
        config.setProperty(DefaultAddressPickerTest.HAZELCAST_LOCAL_ADDRESS_PROP, loopback.getHostAddress());
        config.getNetworkConfig().setPort(0);
        addressPicker = new DefaultAddressPicker(config, logger);
        addressPicker.pickAddress();
        int port = addressPicker.getServerSocketChannel(null).socket().getLocalPort();
        Assert.assertEquals(new Address(loopback, port), addressPicker.getBindAddress(null));
        Assert.assertEquals(addressPicker.getBindAddress(null), addressPicker.getPublicAddress(null));
    }

    @Test
    public void testBindAddress_whenAddressAlreadyInUse() throws Exception {
        int port = 6789;
        config.getNetworkConfig().setPort(port);
        config.getNetworkConfig().setPortAutoIncrement(false);
        addressPicker = new DefaultAddressPicker(config, logger);
        addressPicker.pickAddress();
        try {
            pickAddress();
            Assert.fail("Should fail with 'java.net.BindException: Address already in use'");
        } catch (Exception expected) {
            // expected exception
        }
    }

    @Test
    public void testBindAddress_whenAddressAlreadyInUse_WithPortAutoIncrement() throws Exception {
        int port = 6789;
        config.getNetworkConfig().setPort(port);
        config.getNetworkConfig().setPortAutoIncrement(true);
        addressPicker = new DefaultAddressPicker(config, logger);
        addressPicker.pickAddress();
        pickAddress();
    }

    @Test
    public void testPublicAddress_withDefaultPortAndLoopbackAddress() throws Exception {
        testPublicAddress("127.0.0.1", (-1));
    }

    @Test
    public void testPublicAddress_withDefaultPortAndLocalhost() throws Exception {
        testPublicAddress("localhost", (-1));
    }

    @Test
    public void testPublicAddress_withSpecifiedHost() throws Exception {
        testPublicAddress(DefaultAddressPickerTest.PUBLIC_HOST, (-1));
    }

    @Test
    public void testPublicAddress_withSpecifiedHostAndPort() throws Exception {
        testPublicAddress(DefaultAddressPickerTest.PUBLIC_HOST, 6789);
    }

    @Test
    public void testPublicAddress_withSpecifiedHostAndPortViaProperty() throws Exception {
        String host = DefaultAddressPickerTest.PUBLIC_HOST;
        int port = 6789;
        config.setProperty("hazelcast.local.publicAddress", ((host + ":") + port));
        addressPicker = new DefaultAddressPicker(config, logger);
        addressPicker.pickAddress();
        Assert.assertEquals(new Address(host, port), addressPicker.getPublicAddress(null));
    }

    @Test(expected = UnknownHostException.class)
    public void testPublicAddress_withInvalidAddress() throws Exception {
        config.getNetworkConfig().setPublicAddress("invalid");
        addressPicker = new DefaultAddressPicker(config, logger);
        addressPicker.pickAddress();
    }

    @Test
    public void testBindAddress_withIPv6Address() throws Exception {
        Assume.assumeNotNull(DefaultAddressPickerTest.findIPv6NonLoopbackInterface());
        System.setProperty(PREFER_IPV4_STACK, "false");
        System.setProperty(PREFER_IPV6_ADDRESSES, "true");
        config.setProperty(PREFER_IPv4_STACK.getName(), "false");
        addressPicker = new DefaultAddressPicker(config, logger);
        addressPicker.pickAddress();
        Address bindAddress = addressPicker.getBindAddress(null);
        Assert.assertTrue(("Bind address: " + bindAddress), bindAddress.isIPv6());
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {
        InterfaceDefinition interfaceDefinition = new InterfaceDefinition("localhost", "127.0.0.1");
        InterfaceDefinition interfaceDefinitionSameAttributes = new InterfaceDefinition("localhost", "127.0.0.1");
        InterfaceDefinition interfaceDefinitionOtherHost = new InterfaceDefinition("otherHost", "127.0.0.1");
        InterfaceDefinition interfaceDefinitionOtherAddress = new InterfaceDefinition("localhost", "198.168.1.1");
        InetAddress otherInetAddress = InetAddress.getByName("198.168.1.1");
        AddressDefinition addressDefinition = new AddressDefinition("localhost", 5701, loopback);
        AddressDefinition addressDefinitionSameAttributes = new AddressDefinition("localhost", 5701, loopback);
        AddressDefinition addressDefinitionOtherHost = new AddressDefinition("otherHost", 5701, loopback);
        AddressDefinition addressDefinitionOtherPort = new AddressDefinition("localhost", 5702, loopback);
        AddressDefinition addressDefinitionOtherInetAddress = new AddressDefinition("localhost", 5701, otherInetAddress);
        // InterfaceDefinition.equals()
        Assert.assertEquals(interfaceDefinition, interfaceDefinition);
        Assert.assertEquals(interfaceDefinition, interfaceDefinitionSameAttributes);
        Assert.assertNotEquals(interfaceDefinition, null);
        Assert.assertNotEquals(interfaceDefinition, new Object());
        Assert.assertNotEquals(interfaceDefinition, interfaceDefinitionOtherHost);
        Assert.assertNotEquals(interfaceDefinition, interfaceDefinitionOtherAddress);
        // InterfaceDefinition.hashCode()
        Assert.assertEquals(interfaceDefinition.hashCode(), interfaceDefinition.hashCode());
        Assert.assertEquals(interfaceDefinition.hashCode(), interfaceDefinitionSameAttributes.hashCode());
        Assert.assertNotEquals(interfaceDefinition.hashCode(), interfaceDefinitionOtherHost.hashCode());
        Assert.assertNotEquals(interfaceDefinition.hashCode(), interfaceDefinitionOtherAddress.hashCode());
        // AddressDefinition.equals()
        Assert.assertEquals(addressDefinition, addressDefinition);
        Assert.assertEquals(addressDefinition, addressDefinitionSameAttributes);
        Assert.assertNotEquals(addressDefinition, null);
        Assert.assertNotEquals(addressDefinition, new Object());
        Assert.assertNotEquals(addressDefinition, addressDefinitionOtherHost);
        Assert.assertNotEquals(addressDefinition, addressDefinitionOtherPort);
        Assert.assertNotEquals(addressDefinition, addressDefinitionOtherInetAddress);
        // AddressDefinition.hashCode()
        Assert.assertEquals(addressDefinition.hashCode(), addressDefinition.hashCode());
        Assert.assertEquals(addressDefinition.hashCode(), addressDefinitionSameAttributes.hashCode());
        Assert.assertNotEquals(addressDefinition.hashCode(), addressDefinitionOtherHost.hashCode());
        Assert.assertNotEquals(addressDefinition.hashCode(), addressDefinitionOtherPort.hashCode());
        Assert.assertNotEquals(addressDefinition.hashCode(), addressDefinitionOtherInetAddress.hashCode());
    }
}

