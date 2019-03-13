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
package com.hazelcast.spi.memberAddressProvider;


import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.MemberAddressProvider;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.RootCauseMatcher;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class MemberAddressProviderTest {
    @Rule
    public ExpectedException rule = ExpectedException.none();

    @Test
    public void testPropertiesAreInjected() {
        final Config config = new Config();
        config.getNetworkConfig().getMemberAddressProviderConfig().setEnabled(true).setClassName(MemberAddressProviderTest.MemberAddressProviderWithStaticProperties.class.getName()).getProperties().setProperty("propName", "propValue");
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        Hazelcast.newHazelcastInstance(config);
        final String property = MemberAddressProviderTest.MemberAddressProviderWithStaticProperties.properties.getProperty("propName");
        Assert.assertEquals("propValue", property);
    }

    @Test
    public void testLoggerIsInjected() {
        final Config config = new Config();
        config.getNetworkConfig().getMemberAddressProviderConfig().setEnabled(true).setClassName(MemberAddressProviderTest.MemberAddressProviderWithLogger.class.getName());
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        Hazelcast.newHazelcastInstance(config);
        Assert.assertNotNull(MemberAddressProviderTest.MemberAddressProviderWithLogger.logger);
    }

    @Test
    public void testLoggerAndPropertiesAreInjected() {
        final Config config = new Config();
        config.getNetworkConfig().getMemberAddressProviderConfig().setEnabled(true).setClassName(MemberAddressProviderTest.MemberAddressProviderWithLoggerAndProperties.class.getName()).getProperties().setProperty("propName", "propValue");
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        Hazelcast.newHazelcastInstance(config);
        Assert.assertNotNull(MemberAddressProviderTest.MemberAddressProviderWithLoggerAndProperties.logger);
        final String property = MemberAddressProviderTest.MemberAddressProviderWithLoggerAndProperties.properties.getProperty("propName");
        Assert.assertEquals("propValue", property);
    }

    @Test
    public void testPropertiesAndLoggerAreInjected() {
        final Config config = new Config();
        config.getNetworkConfig().getMemberAddressProviderConfig().setEnabled(true).setClassName(MemberAddressProviderTest.MemberAddressProviderWithPropertiesAndLogger.class.getName()).getProperties().setProperty("propName", "propValue");
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        Hazelcast.newHazelcastInstance(config);
        Assert.assertNotNull(MemberAddressProviderTest.MemberAddressProviderWithPropertiesAndLogger.logger);
        final String property = MemberAddressProviderTest.MemberAddressProviderWithPropertiesAndLogger.properties.getProperty("propName");
        Assert.assertEquals("propValue", property);
    }

    @Test(expected = ConfigurationException.class)
    public void testProviderDoesNotImplementTheInterface() {
        final Config config = new Config();
        config.getNetworkConfig().getMemberAddressProviderConfig().setEnabled(true).setClassName(MemberAddressProviderTest.MemberAddressProviderNotImplementingTheInterface.class.getName());
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        Hazelcast.newHazelcastInstance(config);
    }

    @Test(expected = ConfigurationException.class)
    public void testProviderDoesNotHaveMatchingConstructor() {
        final Config config = new Config();
        config.getNetworkConfig().getMemberAddressProviderConfig().setEnabled(true).setClassName(MemberAddressProviderTest.MemberAddressProviderWithoutMatchingConstructor.class.getName());
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        Hazelcast.newHazelcastInstance(config);
    }

    @Test(expected = ConfigurationException.class)
    public void testProviderClassDoesNotExist() {
        final Config config = new Config();
        config.getNetworkConfig().getMemberAddressProviderConfig().setEnabled(true).setClassName("com.hazelcast.clazz.does.not.Exist");
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        Hazelcast.newHazelcastInstance(config);
    }

    @Test
    public void testSimpleMemberAddressProviderIsInjected() {
        final Config config = getConfig(MemberAddressProviderTest.SimpleMemberAddressProvider.class);
        final HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);
        final InetSocketAddress inetSocketAddress = ((InetSocketAddress) (instance.getLocalEndpoint().getSocketAddress()));
        Assert.assertEquals(inetSocketAddress.getPort(), 9999);
        final Member localMember = instance.getCluster().getLocalMember();
        Assert.assertEquals("1.2.3.4", localMember.getAddress().getHost());
    }

    @Test(expected = ConfigurationException.class)
    public void testFailFastWhenNoMatchingConstructorIsFound() {
        final Config config = new Config();
        config.getNetworkConfig().getMemberAddressProviderConfig().setEnabled(true).setClassName(MemberAddressProviderTest.SimpleMemberAddressProvider.class.getName()).getProperties().setProperty("foo", "bar");// <-- this assumes MemberAddressProvider has a constructor accepting Properties

        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        // this should fail
        Hazelcast.newHazelcastInstance(config);
    }

    @Test
    public void testImplementationIsUsed() {
        final MemberAddressProvider mock = Mockito.mock(MemberAddressProvider.class);
        Mockito.when(mock.getBindAddress()).thenReturn(new InetSocketAddress("localhost", 9999));
        Mockito.when(mock.getPublicAddress()).thenReturn(new InetSocketAddress("1.2.3.4", 0));
        final Config config = getConfig(mock);
        final HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);
        final InetSocketAddress inetSocketAddress = ((InetSocketAddress) (instance.getLocalEndpoint().getSocketAddress()));
        Assert.assertEquals(inetSocketAddress.getPort(), 9999);
        final Member localMember = instance.getCluster().getLocalMember();
        Assert.assertEquals("1.2.3.4", localMember.getAddress().getHost());
        Mockito.verify(mock).getBindAddress();
        Mockito.verify(mock).getPublicAddress();
    }

    @Test(expected = RuntimeException.class)
    public void instanceFailsToStartWhenGetBindAddressThrowsException() {
        final MemberAddressProvider mock = Mockito.mock(MemberAddressProvider.class);
        Mockito.when(mock.getBindAddress()).thenThrow(new RuntimeException("Exception on get bind address"));
        Mockito.when(mock.getPublicAddress()).thenReturn(new InetSocketAddress("1.2.3.4", 0));
        final Config config = getConfig(mock);
        // this should fail
        Hazelcast.newHazelcastInstance(config);
    }

    @Test(expected = RuntimeException.class)
    public void instanceFailsToStartWhenGetPublicAddressThrowsException() {
        final MemberAddressProvider mock = Mockito.mock(MemberAddressProvider.class);
        Mockito.when(mock.getBindAddress()).thenReturn(new InetSocketAddress("localhost", 9999));
        Mockito.when(mock.getPublicAddress()).thenThrow(new RuntimeException("Exception on get public address"));
        final Config config = getConfig(mock);
        // this should fail
        Hazelcast.newHazelcastInstance(config);
    }

    @Test
    public void instanceFailsToStartWhenAssignedUnbindableAddress() {
        final MemberAddressProvider mock = Mockito.mock(MemberAddressProvider.class);
        Mockito.when(mock.getBindAddress()).thenReturn(new InetSocketAddress("1.2.3.4", 9999));
        Mockito.when(mock.getPublicAddress()).thenReturn(new InetSocketAddress("1.2.3.4", 0));
        final Config config = getConfig(mock);
        // we expect an BindException to be thrown (wrapped in an IllegalStateException)
        rule.expect(HazelcastException.class);
        rule.expect(new RootCauseMatcher(BindException.class));
        Hazelcast.newHazelcastInstance(config);
    }

    public static final class MemberAddressProviderWithStaticProperties implements MemberAddressProvider {
        public static Properties properties;

        public MemberAddressProviderWithStaticProperties(Properties properties) {
            this.properties = properties;
        }

        @Override
        public InetSocketAddress getBindAddress() {
            return new InetSocketAddress("localhost", 0);
        }

        @Override
        public InetSocketAddress getPublicAddress() {
            return new InetSocketAddress("localhost", 0);
        }

        @Override
        public InetSocketAddress getBindAddress(EndpointQualifier qualifier) {
            return new InetSocketAddress("localhost", 0);
        }

        @Override
        public InetSocketAddress getPublicAddress(EndpointQualifier qualifier) {
            return new InetSocketAddress("localhost", 0);
        }
    }

    public static final class MemberAddressProviderWithLogger implements MemberAddressProvider {
        public static ILogger logger;

        public MemberAddressProviderWithLogger(ILogger logger) {
            this.logger = logger;
        }

        @Override
        public InetSocketAddress getBindAddress() {
            return new InetSocketAddress("localhost", 0);
        }

        @Override
        public InetSocketAddress getBindAddress(EndpointQualifier qualifier) {
            return new InetSocketAddress("localhost", 0);
        }

        @Override
        public InetSocketAddress getPublicAddress() {
            return new InetSocketAddress("localhost", 0);
        }

        @Override
        public InetSocketAddress getPublicAddress(EndpointQualifier qualifier) {
            return new InetSocketAddress("localhost", 0);
        }
    }

    public static final class MemberAddressProviderWithLoggerAndProperties implements MemberAddressProvider {
        public static ILogger logger;

        public static Properties properties;

        public MemberAddressProviderWithLoggerAndProperties(ILogger logger, Properties properties) {
            this.logger = logger;
            this.properties = properties;
        }

        @Override
        public InetSocketAddress getBindAddress() {
            return new InetSocketAddress("localhost", 0);
        }

        @Override
        public InetSocketAddress getBindAddress(EndpointQualifier qualifier) {
            return new InetSocketAddress("localhost", 0);
        }

        @Override
        public InetSocketAddress getPublicAddress() {
            return new InetSocketAddress("localhost", 0);
        }

        @Override
        public InetSocketAddress getPublicAddress(EndpointQualifier qualifier) {
            return new InetSocketAddress("localhost", 0);
        }
    }

    public static final class MemberAddressProviderWithPropertiesAndLogger implements MemberAddressProvider {
        public static ILogger logger;

        public static Properties properties;

        public MemberAddressProviderWithPropertiesAndLogger(Properties properties, ILogger logger) {
            this.logger = logger;
            this.properties = properties;
        }

        @Override
        public InetSocketAddress getBindAddress() {
            return new InetSocketAddress("localhost", 0);
        }

        @Override
        public InetSocketAddress getBindAddress(EndpointQualifier qualifier) {
            return new InetSocketAddress("localhost", 0);
        }

        @Override
        public InetSocketAddress getPublicAddress() {
            return new InetSocketAddress("localhost", 0);
        }

        @Override
        public InetSocketAddress getPublicAddress(EndpointQualifier qualifier) {
            return new InetSocketAddress("localhost", 0);
        }
    }

    public static final class SimpleMemberAddressProvider implements MemberAddressProvider {
        @Override
        public InetSocketAddress getBindAddress() {
            return new InetSocketAddress("localhost", 9999);
        }

        @Override
        public InetSocketAddress getPublicAddress() {
            return new InetSocketAddress("1.2.3.4", 0);
        }

        @Override
        public InetSocketAddress getBindAddress(EndpointQualifier qualifier) {
            return new InetSocketAddress("localhost", 9999);
        }

        @Override
        public InetSocketAddress getPublicAddress(EndpointQualifier qualifier) {
            return new InetSocketAddress("1.2.3.4", 0);
        }
    }

    public static final class MemberAddressProviderNotImplementingTheInterface {}

    public static final class MemberAddressProviderWithoutMatchingConstructor implements MemberAddressProvider {
        public MemberAddressProviderWithoutMatchingConstructor(String ignored) {
        }

        @Override
        public InetSocketAddress getBindAddress() {
            return new InetSocketAddress("localhost", 9999);
        }

        @Override
        public InetSocketAddress getBindAddress(EndpointQualifier qualifier) {
            return new InetSocketAddress("localhost", 9999);
        }

        @Override
        public InetSocketAddress getPublicAddress() {
            return new InetSocketAddress("1.2.3.4", 0);
        }

        @Override
        public InetSocketAddress getPublicAddress(EndpointQualifier qualifier) {
            return new InetSocketAddress("1.2.3.4", 0);
        }
    }
}

