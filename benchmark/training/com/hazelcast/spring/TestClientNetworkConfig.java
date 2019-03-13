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
package com.hazelcast.spring;


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.impl.clientside.HazelcastClientProxy;
import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.AzureConfig;
import com.hazelcast.config.EurekaConfig;
import com.hazelcast.config.GcpConfig;
import com.hazelcast.config.KubernetesConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;


@RunWith(CustomSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "clientNetworkConfig-applicationContext.xml" })
@Category(QuickTest.class)
public class TestClientNetworkConfig {
    @Resource(name = "client")
    private HazelcastClientProxy client;

    @Test
    public void smokeMember() {
        int memberCountInConfigurationXml = 2;
        ClientConfig config = client.getClientConfig();
        Assert.assertEquals(memberCountInConfigurationXml, config.getNetworkConfig().getAddresses().size());
    }

    @Test
    public void smokeSocketOptions() {
        int bufferSizeInConfigurationXml = 32;
        ClientConfig config = client.getClientConfig();
        Assert.assertEquals(bufferSizeInConfigurationXml, config.getNetworkConfig().getSocketOptions().getBufferSize());
    }

    @Test
    public void smokeSocketInterceptor() {
        ClientConfig config = client.getClientConfig();
        SocketInterceptorConfig socketInterceptorConfig = config.getNetworkConfig().getSocketInterceptorConfig();
        Assert.assertFalse(socketInterceptorConfig.isEnabled());
        Assert.assertEquals(DummySocketInterceptor.class.getName(), socketInterceptorConfig.getClassName());
    }

    @Test
    public void smokeSSLConfig() {
        ClientConfig config = client.getClientConfig();
        Assert.assertEquals("com.hazelcast.nio.ssl.BasicSSLContextFactory", config.getNetworkConfig().getSSLConfig().getFactoryClassName());
    }

    @Test
    public void smokeAwsConfig() {
        AwsConfig aws = client.getClientConfig().getNetworkConfig().getAwsConfig();
        Assert.assertFalse(aws.isEnabled());
        Assert.assertEquals("sample-access-key", aws.getAccessKey());
        Assert.assertEquals("sample-secret-key", aws.getSecretKey());
        Assert.assertEquals("sample-region", aws.getRegion());
        Assert.assertEquals("sample-header", aws.getHostHeader());
        Assert.assertEquals("sample-group", aws.getSecurityGroupName());
        Assert.assertEquals("sample-tag-key", aws.getTagKey());
        Assert.assertEquals("sample-tag-value", aws.getTagValue());
        Assert.assertEquals("sample-role", aws.getIamRole());
    }

    @Test
    public void smokeGcpConfig() {
        GcpConfig gcp = client.getClientConfig().getNetworkConfig().getGcpConfig();
        Assert.assertFalse(gcp.isEnabled());
        Assert.assertEquals("us-east1-b,us-east1-c", gcp.getProperty("zones"));
    }

    @Test
    public void smokeAzureConfig() {
        AzureConfig azure = client.getClientConfig().getNetworkConfig().getAzureConfig();
        Assert.assertFalse(azure.isEnabled());
        Assert.assertEquals("CLIENT_ID", azure.getProperty("client-id"));
        Assert.assertEquals("CLIENT_SECRET", azure.getProperty("client-secret"));
        Assert.assertEquals("TENANT_ID", azure.getProperty("tenant-id"));
        Assert.assertEquals("SUB_ID", azure.getProperty("subscription-id"));
        Assert.assertEquals("HZLCAST001", azure.getProperty("cluster-id"));
        Assert.assertEquals("GROUP-NAME", azure.getProperty("group-name"));
    }

    @Test
    public void smokeKubernetesConfig() {
        KubernetesConfig kubernetes = client.getClientConfig().getNetworkConfig().getKubernetesConfig();
        Assert.assertFalse(kubernetes.isEnabled());
        Assert.assertEquals("MY-KUBERNETES-NAMESPACE", kubernetes.getProperty("namespace"));
        Assert.assertEquals("MY-SERVICE-NAME", kubernetes.getProperty("service-name"));
        Assert.assertEquals("MY-SERVICE-LABEL-NAME", kubernetes.getProperty("service-label-name"));
        Assert.assertEquals("MY-SERVICE-LABEL-VALUE", kubernetes.getProperty("service-label-value"));
    }

    @Test
    public void smokeEurekaConfig() {
        EurekaConfig eureka = client.getClientConfig().getNetworkConfig().getEurekaConfig();
        Assert.assertFalse(eureka.isEnabled());
        Assert.assertEquals("true", eureka.getProperty("self-registration"));
        Assert.assertEquals("hazelcast", eureka.getProperty("namespace"));
    }

    @Test
    public void smokeOutboundPorts() {
        Collection<String> allowedPorts = client.getClientConfig().getNetworkConfig().getOutboundPortDefinitions();
        Assert.assertEquals(2, allowedPorts.size());
        Assert.assertTrue(allowedPorts.contains("34600"));
        Assert.assertTrue(allowedPorts.contains("34700-34710"));
    }
}

