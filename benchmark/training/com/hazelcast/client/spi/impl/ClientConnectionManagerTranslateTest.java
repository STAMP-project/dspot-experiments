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
package com.hazelcast.client.spi.impl;


import com.google.common.collect.ImmutableList;
import com.hazelcast.client.connection.AddressProvider;
import com.hazelcast.client.connection.AddressTranslator;
import com.hazelcast.client.connection.Addresses;
import com.hazelcast.client.connection.nio.ClientConnectionManagerImpl;
import com.hazelcast.client.test.ClientTestSupport;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.net.UnknownHostException;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class ClientConnectionManagerTranslateTest extends ClientTestSupport {
    private Address privateAddress;

    private Address publicAddress;

    private ClientConnectionManagerImpl clientConnectionManager;

    private class TestAddressTranslator implements AddressTranslator {
        volatile boolean shouldTranslate = false;

        @Override
        public Address translate(Address address) {
            if (!(shouldTranslate)) {
                return address;
            }
            if (address.equals(privateAddress)) {
                return publicAddress;
            }
            return null;
        }

        @Override
        public void refresh() {
        }
    }

    private class TestAddressProvider implements AddressProvider {
        @Override
        public Addresses loadAddresses() {
            try {
                return new Addresses(ImmutableList.of(new Address("127.0.0.1", 5701)));
            } catch (UnknownHostException e) {
                return null;
            }
        }
    }

    @Test
    public void testTranslatorIsNotUsedGetActiveConnection() throws Exception {
        Connection connection = clientConnectionManager.getActiveConnection(privateAddress);
        TestCase.assertNotNull(connection);
    }

    @Test
    public void testTranslatorIsNotUsedOnTriggerConnect() throws Exception {
        Connection connection = clientConnectionManager.getOrTriggerConnect(privateAddress, false);
        TestCase.assertNotNull(connection);
    }

    @Test
    public void testTranslatorIsNotUsedOnGetConnection() throws Exception {
        Connection connection = clientConnectionManager.getOrConnect(privateAddress);
        TestCase.assertNotNull(connection);
    }
}

