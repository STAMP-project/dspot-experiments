/**
 * Copyright 2018 Florian Schmaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smack;


import org.junit.Assert;
import org.junit.Test;


public class ConnectionConfigurationTest {
    @Test
    public void setIp() {
        ConnectionConfigurationTest.DummyConnectionConfiguration.Builder builder = ConnectionConfigurationTest.newUnitTestBuilder();
        final String ip = "192.168.0.1";
        setHostAddressByNameOrIp(ip);
        ConnectionConfigurationTest.DummyConnectionConfiguration connectionConfiguration = builder.build();
        Assert.assertEquals(('/' + ip), getHostAddress().toString());
    }

    @Test
    public void setFqdn() {
        ConnectionConfigurationTest.DummyConnectionConfiguration.Builder builder = ConnectionConfigurationTest.newUnitTestBuilder();
        final String fqdn = "foo.example.org";
        setHostAddressByNameOrIp(fqdn);
        ConnectionConfigurationTest.DummyConnectionConfiguration connectionConfiguration = builder.build();
        Assert.assertEquals(fqdn, getHost().toString());
    }

    private static final class DummyConnectionConfiguration extends ConnectionConfiguration {
        protected DummyConnectionConfiguration(ConnectionConfigurationTest.DummyConnectionConfiguration.Builder builder) {
            super(builder);
        }

        public static ConnectionConfigurationTest.DummyConnectionConfiguration.Builder builder() {
            return new ConnectionConfigurationTest.DummyConnectionConfiguration.Builder();
        }

        private static final class Builder extends ConnectionConfiguration.Builder<ConnectionConfigurationTest.DummyConnectionConfiguration.Builder, ConnectionConfigurationTest.DummyConnectionConfiguration> {
            @Override
            public ConnectionConfigurationTest.DummyConnectionConfiguration build() {
                return new ConnectionConfigurationTest.DummyConnectionConfiguration(this);
            }

            @Override
            protected ConnectionConfigurationTest.DummyConnectionConfiguration.Builder getThis() {
                return this;
            }
        }
    }
}

