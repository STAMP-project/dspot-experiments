/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.configuration;


import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 * ComponentSSLConfiguration tests.
 */
public class ComponentSSLConfigurationTest {
    @Test
    public void testGetTruststorePath() throws Exception {
        ComponentSSLConfiguration sslConfiguration = ComponentSSLConfigurationTest.getConfiguration("tspath", "tspass", "tstype", true);
        Assert.assertEquals("tspath", sslConfiguration.getTruststorePath());
    }

    @Test
    public void testGetTruststorePassword() throws Exception {
        ComponentSSLConfiguration sslConfiguration = ComponentSSLConfigurationTest.getConfiguration("tspath", "tspass", "tstype", true);
        Assert.assertEquals("tspass", sslConfiguration.getTruststorePassword());
    }

    @Test
    public void testGetTruststoreType() throws Exception {
        ComponentSSLConfiguration sslConfiguration = ComponentSSLConfigurationTest.getConfiguration("tspath", "tspass", "tstype", true);
        Assert.assertEquals("tstype", sslConfiguration.getTruststoreType());
    }

    @Test
    public void testIsGangliaSSL() throws Exception {
        ComponentSSLConfiguration sslConfiguration = ComponentSSLConfigurationTest.getConfiguration("tspath", "tspass", "tstype", true);
        Assert.assertTrue(sslConfiguration.isHttpsEnabled());
    }

    private static class TestConfiguration extends Configuration {
        private TestConfiguration(Properties properties) {
            super(properties);
        }

        @Override
        protected void loadSSLParams() {
        }
    }
}

