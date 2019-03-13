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


import EndpointQualifier.CLIENT;
import EndpointQualifier.MEMBER;
import EndpointQualifier.REST;
import ProtocolType.WAN;
import com.hazelcast.config.Config;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.MemberAddressProvider;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.net.InetSocketAddress;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class DelegatingAddressPickerTest {
    private Address memberBindAddress;

    private Address clientBindAddress;

    private Address textBindAddress;

    private Address wan1BindAddress;

    private Address memberPublicAddress;

    private Address clientPublicAddress;

    private Address textPublicAddress;

    private Address wan1PublicAddress;

    private ILogger logger;

    private DelegatingAddressPicker picker;

    @Test
    public void testPickAddress_fromAdvancedNetworkConfig() throws Exception {
        Config config = createAdvancedNetworkConfig();
        picker = new DelegatingAddressPicker(new DelegatingAddressPickerTest.AnAddressProvider(), config, logger);
        picker.pickAddress();
        Assert.assertEquals(memberBindAddress, picker.getBindAddress());
        Assert.assertEquals(memberBindAddress, picker.getBindAddress(MEMBER));
        Assert.assertEquals(clientBindAddress, picker.getBindAddress(CLIENT));
        Assert.assertEquals(textBindAddress, picker.getBindAddress(REST));
        Assert.assertEquals(wan1BindAddress, picker.getBindAddress(EndpointQualifier.resolve(WAN, "wan1")));
        Assert.assertEquals(memberPublicAddress, picker.getPublicAddress());
        Assert.assertEquals(memberPublicAddress, picker.getPublicAddress(MEMBER));
        Assert.assertEquals(clientPublicAddress, picker.getPublicAddress(CLIENT));
        Assert.assertEquals(textPublicAddress, picker.getPublicAddress(REST));
        Assert.assertEquals(wan1PublicAddress, picker.getPublicAddress(EndpointQualifier.resolve(WAN, "wan1")));
    }

    @Test
    public void testPickAddress_fromNetworkConfig() throws Exception {
        Config config = createNetworkingConfig();
        picker = new DelegatingAddressPicker(new DelegatingAddressPickerTest.AnAddressProvider(), config, logger);
        picker.pickAddress();
        Assert.assertEquals(memberBindAddress, picker.getBindAddress());
        Assert.assertEquals(memberBindAddress, picker.getBindAddress(MEMBER));
        Assert.assertEquals(memberBindAddress, picker.getBindAddress(CLIENT));
        Assert.assertEquals(memberBindAddress, picker.getBindAddress(REST));
        Assert.assertEquals(memberBindAddress, picker.getBindAddress(EndpointQualifier.resolve(WAN, "wan1")));
        Assert.assertEquals(memberPublicAddress, picker.getPublicAddress());
        Assert.assertEquals(memberPublicAddress, picker.getPublicAddress(MEMBER));
        Assert.assertEquals(memberPublicAddress, picker.getPublicAddress(CLIENT));
        Assert.assertEquals(memberPublicAddress, picker.getPublicAddress(REST));
        Assert.assertEquals(memberPublicAddress, picker.getPublicAddress(EndpointQualifier.resolve(WAN, "wan1")));
    }

    public static class AnAddressProvider implements MemberAddressProvider {
        @Override
        public InetSocketAddress getBindAddress() {
            return new InetSocketAddress("127.0.0.1", 2000);
        }

        @Override
        public InetSocketAddress getBindAddress(EndpointQualifier qualifier) {
            switch (qualifier.getType()) {
                case MEMBER :
                    return new InetSocketAddress("127.0.0.1", 2000);
                case CLIENT :
                    return new InetSocketAddress("127.0.0.1", 2001);
                case REST :
                    return new InetSocketAddress("127.0.0.1", 2002);
                case WAN :
                    return new InetSocketAddress("127.0.0.1", 2003);
                default :
                    throw new IllegalStateException();
            }
        }

        @Override
        public InetSocketAddress getPublicAddress() {
            return new InetSocketAddress("10.10.10.10", 3000);
        }

        @Override
        public InetSocketAddress getPublicAddress(EndpointQualifier qualifier) {
            switch (qualifier.getType()) {
                case MEMBER :
                    return new InetSocketAddress("10.10.10.10", 3000);
                case CLIENT :
                    return new InetSocketAddress("10.10.10.10", 3001);
                case REST :
                    return new InetSocketAddress("10.10.10.10", 3002);
                case WAN :
                    return new InetSocketAddress("10.10.10.10", 3003);
                default :
                    throw new IllegalStateException();
            }
        }
    }
}

