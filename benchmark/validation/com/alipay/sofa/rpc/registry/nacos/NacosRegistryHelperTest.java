/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.rpc.registry.nacos;


import NacosRegistryHelper.DEFAULT_CLUSTER;
import RpcConstants.CONFIG_KEY_APP_NAME;
import RpcConstants.CONFIG_KEY_PROTOCOL;
import RpcConstants.CONFIG_KEY_SERIALIZATION;
import RpcConstants.CONFIG_KEY_TIMEOUT;
import RpcConstants.CONFIG_KEY_UNIQUEID;
import RpcConstants.CONFIG_KEY_WEIGHT;
import RpcConstants.PROTOCOL_TYPE_REST;
import RpcOptions.DEFAULT_PROTOCOL;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alipay.sofa.rpc.client.ProviderInfo;
import com.alipay.sofa.rpc.common.RpcConfigs;
import com.alipay.sofa.rpc.config.ApplicationConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author zhuoyu.sjw
 * @version $Id: NacosRegistryHelperTest.java, v 0.1 2018-12-07 19:22 zhuoyu.sjw Exp $$
 */
public class NacosRegistryHelperTest {
    @Test
    public void convertProviderToInstances() {
        ServerConfig serverConfig = new ServerConfig().setProtocol("bolt").setHost("0.0.0.0").setPort(12200);
        ProviderConfig<?> provider = new ProviderConfig();
        provider.setInterfaceId("com.alipay.xxx.TestService").setApplication(new ApplicationConfig().setAppName("test-server")).setUniqueId("nacos-test").setProxy("javassist").setRegister(true).setSerialization("hessian2").setServer(serverConfig).setWeight(222).setTimeout(3000);
        List<Instance> instances = NacosRegistryHelper.convertProviderToInstances(provider);
        Assert.assertNotNull(instances);
        Assert.assertEquals(1, instances.size());
        Instance instance = instances.get(0);
        Assert.assertNotNull(instance);
        Assert.assertEquals(DEFAULT_CLUSTER, instance.getClusterName());
        Assert.assertEquals(serverConfig.getPort(), instance.getPort());
        Assert.assertEquals(serverConfig.getProtocol(), instance.getMetadata().get(CONFIG_KEY_PROTOCOL));
        Assert.assertEquals(provider.getSerialization(), instance.getMetadata().get(CONFIG_KEY_SERIALIZATION));
        Assert.assertEquals(provider.getUniqueId(), instance.getMetadata().get(CONFIG_KEY_UNIQUEID));
        Assert.assertEquals(provider.getWeight(), Integer.parseInt(instance.getMetadata().get(CONFIG_KEY_WEIGHT)));
        Assert.assertEquals(provider.getTimeout(), Integer.parseInt(instance.getMetadata().get(CONFIG_KEY_TIMEOUT)));
        Assert.assertEquals(provider.getSerialization(), instance.getMetadata().get(CONFIG_KEY_SERIALIZATION));
        Assert.assertEquals(provider.getAppName(), instance.getMetadata().get(CONFIG_KEY_APP_NAME));
    }

    @Test
    public void convertInstancesToProviders() {
        Instance instance = new Instance();
        instance.setClusterName(DEFAULT_CLUSTER);
        instance.setIp("1.1.1.1");
        instance.setPort(12200);
        instance.setServiceName("com.alipay.xxx.TestService");
        List<ProviderInfo> providerInfos = NacosRegistryHelper.convertInstancesToProviders(Lists.newArrayList(instance));
        Assert.assertNotNull(providerInfos);
        Assert.assertEquals(1, providerInfos.size());
        ProviderInfo providerInfo = providerInfos.get(0);
        Assert.assertNotNull(providerInfo);
        Assert.assertEquals(instance.getIp(), providerInfo.getHost());
        Assert.assertEquals(instance.getPort(), providerInfo.getPort());
        Assert.assertEquals(RpcConfigs.getStringValue(DEFAULT_PROTOCOL), providerInfo.getProtocolType());
        Map<String, String> metaData = Maps.newHashMap();
        metaData.put(CONFIG_KEY_PROTOCOL, PROTOCOL_TYPE_REST);
        instance.setMetadata(metaData);
        providerInfos = NacosRegistryHelper.convertInstancesToProviders(Lists.newArrayList(instance));
        providerInfo = providerInfos.get(0);
        Assert.assertEquals(PROTOCOL_TYPE_REST, providerInfo.getProtocolType());
    }
}

