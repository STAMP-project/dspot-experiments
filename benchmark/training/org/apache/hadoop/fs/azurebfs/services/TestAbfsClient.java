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
package org.apache.hadoop.fs.azurebfs.services;


import ConfigurationKeys.FS_AZURE_SSL_CHANNEL_MODE_KEY;
import ConfigurationKeys.FS_AZURE_USER_AGENT_PREFIX_KEY;
import SSLSocketFactoryEx.SSLChannelMode.Default_JSSE;
import java.net.URL;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.azurebfs.AbfsConfiguration;
import org.apache.hadoop.util.VersionInfo;
import org.junit.Test;


/**
 * Test useragent of abfs client.
 */
public final class TestAbfsClient {
    private final String accountName = "bogusAccountName";

    @Test
    public void verifyUnknownUserAgent() throws Exception {
        String clientVersion = "Azure Blob FS/" + (VersionInfo.getVersion());
        String expectedUserAgentPattern = String.format((clientVersion + " %s"), "\\(JavaJRE ([^\\)]+)\\)");
        final Configuration configuration = new Configuration();
        configuration.unset(FS_AZURE_USER_AGENT_PREFIX_KEY);
        AbfsConfiguration abfsConfiguration = new AbfsConfiguration(configuration, accountName);
        validateUserAgent(expectedUserAgentPattern, new URL("http://azure.com"), abfsConfiguration, false);
    }

    @Test
    public void verifyUserAgent() throws Exception {
        String clientVersion = "Azure Blob FS/" + (VersionInfo.getVersion());
        String expectedUserAgentPattern = String.format((clientVersion + " %s"), "\\(JavaJRE ([^\\)]+)\\) Partner Service");
        final Configuration configuration = new Configuration();
        configuration.set(FS_AZURE_USER_AGENT_PREFIX_KEY, "Partner Service");
        AbfsConfiguration abfsConfiguration = new AbfsConfiguration(configuration, accountName);
        validateUserAgent(expectedUserAgentPattern, new URL("http://azure.com"), abfsConfiguration, false);
    }

    @Test
    public void verifyUserAgentWithSSLProvider() throws Exception {
        String clientVersion = "Azure Blob FS/" + (VersionInfo.getVersion());
        String expectedUserAgentPattern = String.format((clientVersion + " %s"), "\\(JavaJRE ([^\\)]+)\\) Partner Service");
        final Configuration configuration = new Configuration();
        configuration.set(FS_AZURE_USER_AGENT_PREFIX_KEY, "Partner Service");
        configuration.set(FS_AZURE_SSL_CHANNEL_MODE_KEY, Default_JSSE.name());
        AbfsConfiguration abfsConfiguration = new AbfsConfiguration(configuration, accountName);
        validateUserAgent(expectedUserAgentPattern, new URL("https://azure.com"), abfsConfiguration, true);
    }
}

