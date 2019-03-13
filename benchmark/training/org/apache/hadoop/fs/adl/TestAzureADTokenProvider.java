/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.adl;


import CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH;
import TokenProviderType.Custom;
import com.microsoft.azure.datalake.store.oauth2.AccessTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.ClientCredsTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.DeviceCodeTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.MsiTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.RefreshTokenBasedTokenProvider;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.adl.common.CustomMockTokenProvider;
import org.apache.hadoop.fs.adl.oauth2.AzureADTokenProvider;
import org.apache.hadoop.security.alias.CredentialProvider;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Test appropriate token provider is loaded as per configuration.
 */
public class TestAzureADTokenProvider {
    private static final String CLIENT_ID = "MY_CLIENT_ID";

    private static final String REFRESH_TOKEN = "MY_REFRESH_TOKEN";

    private static final String CLIENT_SECRET = "MY_CLIENT_SECRET";

    private static final String REFRESH_URL = "http://localhost:8080/refresh";

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testRefreshTokenProvider() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        conf.set(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY, "MY_CLIENTID");
        conf.set(AdlConfKeys.AZURE_AD_REFRESH_TOKEN_KEY, "XYZ");
        conf.setEnum(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_TYPE_KEY, RefreshToken);
        conf.set(AdlConfKeys.AZURE_AD_REFRESH_URL_KEY, "http://localhost:8080/refresh");
        URI uri = new URI("adl://localhost:8080");
        AdlFileSystem fileSystem = new AdlFileSystem();
        fileSystem.initialize(uri, conf);
        AccessTokenProvider tokenProvider = fileSystem.getTokenProvider();
        Assert.assertTrue((tokenProvider instanceof RefreshTokenBasedTokenProvider));
    }

    @Test
    public void testClientCredTokenProvider() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        conf.set(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY, "MY_CLIENTID");
        conf.set(AdlConfKeys.AZURE_AD_CLIENT_SECRET_KEY, "XYZ");
        conf.setEnum(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_TYPE_KEY, ClientCredential);
        conf.set(AdlConfKeys.AZURE_AD_REFRESH_URL_KEY, "http://localhost:8080/refresh");
        URI uri = new URI("adl://localhost:8080");
        AdlFileSystem fileSystem = new AdlFileSystem();
        fileSystem.initialize(uri, conf);
        AccessTokenProvider tokenProvider = fileSystem.getTokenProvider();
        Assert.assertTrue((tokenProvider instanceof ClientCredsTokenProvider));
    }

    @Test
    public void testMSITokenProvider() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        conf.setEnum(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_TYPE_KEY, MSI);
        URI uri = new URI("adl://localhost:8080");
        AdlFileSystem fileSystem = new AdlFileSystem();
        fileSystem.initialize(uri, conf);
        AccessTokenProvider tokenProvider = fileSystem.getTokenProvider();
        Assert.assertTrue((tokenProvider instanceof MsiTokenProvider));
    }

    @Test
    public void testDeviceCodeTokenProvider() throws IOException, URISyntaxException {
        boolean runTest = false;
        if (runTest) {
            // Device code auth method causes an interactive prompt, so run this only
            // when running the test interactively at a local terminal. Disabling
            // test by default, to not break any automation.
            Configuration conf = new Configuration();
            conf.setEnum(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_TYPE_KEY, DeviceCode);
            conf.set(AdlConfKeys.DEVICE_CODE_CLIENT_APP_ID, "CLIENT_APP_ID_GUID");
            URI uri = new URI("adl://localhost:8080");
            AdlFileSystem fileSystem = new AdlFileSystem();
            fileSystem.initialize(uri, conf);
            AccessTokenProvider tokenProvider = fileSystem.getTokenProvider();
            Assert.assertTrue((tokenProvider instanceof DeviceCodeTokenProvider));
        }
    }

    @Test
    public void testCustomCredTokenProvider() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        conf.setEnum(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_TYPE_KEY, Custom);
        conf.setClass(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_CLASS_KEY, CustomMockTokenProvider.class, AzureADTokenProvider.class);
        URI uri = new URI("adl://localhost:8080");
        AdlFileSystem fileSystem = new AdlFileSystem();
        fileSystem.initialize(uri, conf);
        AccessTokenProvider tokenProvider = fileSystem.getTokenProvider();
        Assert.assertTrue((tokenProvider instanceof SdkTokenProviderAdapter));
    }

    @Test
    public void testInvalidProviderConfigurationForType() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        conf.setEnum(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_TYPE_KEY, Custom);
        URI uri = new URI("adl://localhost:8080");
        AdlFileSystem fileSystem = new AdlFileSystem();
        try {
            fileSystem.initialize(uri, conf);
            Assert.fail(("Initialization should have failed due no token provider " + "configuration"));
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_CLASS_KEY, e);
        }
        conf.setClass(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_CLASS_KEY, CustomMockTokenProvider.class, AzureADTokenProvider.class);
        fileSystem.initialize(uri, conf);
    }

    @Test
    public void testInvalidProviderConfigurationForClassPath() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        URI uri = new URI("adl://localhost:8080");
        AdlFileSystem fileSystem = new AdlFileSystem();
        conf.setEnum(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_TYPE_KEY, Custom);
        conf.set(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_CLASS_KEY, "wrong.classpath.CustomMockTokenProvider");
        try {
            fileSystem.initialize(uri, conf);
            Assert.fail(("Initialization should have failed due invalid provider " + "configuration"));
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("wrong.classpath.CustomMockTokenProvider"));
        }
    }

    @Test
    public void testRefreshTokenWithCredentialProvider() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        conf.set(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY, "DUMMY");
        conf.set(AdlConfKeys.AZURE_AD_REFRESH_TOKEN_KEY, "DUMMY");
        conf.setEnum(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_TYPE_KEY, RefreshToken);
        CredentialProvider provider = createTempCredProvider(conf);
        provider.createCredentialEntry(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY, TestAzureADTokenProvider.CLIENT_ID.toCharArray());
        provider.createCredentialEntry(AdlConfKeys.AZURE_AD_REFRESH_TOKEN_KEY, TestAzureADTokenProvider.REFRESH_TOKEN.toCharArray());
        provider.flush();
        URI uri = new URI("adl://localhost:8080");
        AdlFileSystem fileSystem = new AdlFileSystem();
        fileSystem.initialize(uri, conf);
        RefreshTokenBasedTokenProvider expected = new RefreshTokenBasedTokenProvider(TestAzureADTokenProvider.CLIENT_ID, TestAzureADTokenProvider.REFRESH_TOKEN);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, fileSystem.getTokenProvider()));
    }

    @Test
    public void testRefreshTokenWithCredentialProviderFallback() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        conf.set(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY, TestAzureADTokenProvider.CLIENT_ID);
        conf.set(AdlConfKeys.AZURE_AD_REFRESH_TOKEN_KEY, TestAzureADTokenProvider.REFRESH_TOKEN);
        conf.setEnum(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_TYPE_KEY, RefreshToken);
        createTempCredProvider(conf);
        URI uri = new URI("adl://localhost:8080");
        AdlFileSystem fileSystem = new AdlFileSystem();
        fileSystem.initialize(uri, conf);
        RefreshTokenBasedTokenProvider expected = new RefreshTokenBasedTokenProvider(TestAzureADTokenProvider.CLIENT_ID, TestAzureADTokenProvider.REFRESH_TOKEN);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, fileSystem.getTokenProvider()));
    }

    @Test
    public void testClientCredWithCredentialProvider() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        conf.set(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY, "DUMMY");
        conf.set(AdlConfKeys.AZURE_AD_CLIENT_SECRET_KEY, "DUMMY");
        conf.set(AdlConfKeys.AZURE_AD_REFRESH_URL_KEY, "DUMMY");
        conf.setEnum(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_TYPE_KEY, ClientCredential);
        CredentialProvider provider = createTempCredProvider(conf);
        provider.createCredentialEntry(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY, TestAzureADTokenProvider.CLIENT_ID.toCharArray());
        provider.createCredentialEntry(AdlConfKeys.AZURE_AD_CLIENT_SECRET_KEY, TestAzureADTokenProvider.CLIENT_SECRET.toCharArray());
        provider.createCredentialEntry(AdlConfKeys.AZURE_AD_REFRESH_URL_KEY, TestAzureADTokenProvider.REFRESH_URL.toCharArray());
        provider.flush();
        URI uri = new URI("adl://localhost:8080");
        AdlFileSystem fileSystem = new AdlFileSystem();
        fileSystem.initialize(uri, conf);
        ClientCredsTokenProvider expected = new ClientCredsTokenProvider(TestAzureADTokenProvider.REFRESH_URL, TestAzureADTokenProvider.CLIENT_ID, TestAzureADTokenProvider.CLIENT_SECRET);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, fileSystem.getTokenProvider()));
    }

    @Test
    public void testClientCredWithCredentialProviderFallback() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        conf.set(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY, TestAzureADTokenProvider.CLIENT_ID);
        conf.set(AdlConfKeys.AZURE_AD_CLIENT_SECRET_KEY, TestAzureADTokenProvider.CLIENT_SECRET);
        conf.set(AdlConfKeys.AZURE_AD_REFRESH_URL_KEY, TestAzureADTokenProvider.REFRESH_URL);
        conf.setEnum(AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_TYPE_KEY, ClientCredential);
        createTempCredProvider(conf);
        URI uri = new URI("adl://localhost:8080");
        AdlFileSystem fileSystem = new AdlFileSystem();
        fileSystem.initialize(uri, conf);
        ClientCredsTokenProvider expected = new ClientCredsTokenProvider(TestAzureADTokenProvider.REFRESH_URL, TestAzureADTokenProvider.CLIENT_ID, TestAzureADTokenProvider.CLIENT_SECRET);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, fileSystem.getTokenProvider()));
    }

    @Test
    public void testCredentialProviderPathExclusions() throws Exception {
        String providerPath = "user:///,jceks://adl/user/hrt_qa/sqoopdbpasswd.jceks," + "jceks://hdfs@nn1.example.com/my/path/test.jceks";
        Configuration config = new Configuration();
        config.set(CREDENTIAL_PROVIDER_PATH, providerPath);
        String newPath = "user:///,jceks://hdfs@nn1.example.com/my/path/test.jceks";
        excludeAndTestExpectations(config, newPath);
    }

    @Test
    public void testExcludeAllProviderTypesFromConfig() throws Exception {
        String providerPath = "jceks://adl/tmp/test.jceks," + "jceks://adl@/my/path/test.jceks";
        Configuration config = new Configuration();
        config.set(CREDENTIAL_PROVIDER_PATH, providerPath);
        String newPath = null;
        excludeAndTestExpectations(config, newPath);
    }
}

