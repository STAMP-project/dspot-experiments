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
package org.apache.dubbo.qos.command.impl;


import java.util.Map;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.qos.command.CommandContext;
import org.apache.dubbo.registry.integration.RegistryDirectory;
import org.apache.dubbo.registry.support.ProviderConsumerRegTable;
import org.apache.dubbo.registry.support.ProviderInvokerWrapper;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.model.ConsumerModel;
import org.apache.dubbo.rpc.model.ProviderModel;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class LsTest {
    @Test
    public void testExecute() throws Exception {
        ConsumerModel consumerModel = Mockito.mock(ConsumerModel.class);
        Mockito.when(consumerModel.getServiceName()).thenReturn("org.apache.dubbo.FooService");
        ProviderModel providerModel = Mockito.mock(ProviderModel.class);
        Mockito.when(providerModel.getServiceName()).thenReturn("org.apache.dubbo.BarService");
        ApplicationModel.initConsumerModel("org.apache.dubbo.FooService", consumerModel);
        ApplicationModel.initProviderModel("org.apache.dubbo.BarService", providerModel);
        Invoker providerInvoker = Mockito.mock(Invoker.class);
        URL registryUrl = Mockito.mock(URL.class);
        Mockito.when(registryUrl.toFullString()).thenReturn("test://localhost:8080");
        URL providerUrl = Mockito.mock(URL.class);
        Mockito.when(providerUrl.getServiceKey()).thenReturn("org.apache.dubbo.BarService");
        Mockito.when(providerUrl.toFullString()).thenReturn("dubbo://localhost:8888/org.apache.dubbo.BarService");
        Mockito.when(providerInvoker.getUrl()).thenReturn(providerUrl);
        ProviderConsumerRegTable.registerProvider(providerInvoker, registryUrl, providerUrl);
        for (ProviderInvokerWrapper wrapper : getProviderInvoker("org.apache.dubbo.BarService")) {
            wrapper.setReg(true);
        }
        Invoker consumerInvoker = Mockito.mock(Invoker.class);
        URL consumerUrl = Mockito.mock(URL.class);
        Mockito.when(consumerUrl.getServiceKey()).thenReturn("org.apache.dubbo.FooService");
        Mockito.when(consumerUrl.toFullString()).thenReturn("dubbo://localhost:8888/org.apache.dubbo.FooService");
        Mockito.when(consumerInvoker.getUrl()).thenReturn(consumerUrl);
        RegistryDirectory directory = Mockito.mock(RegistryDirectory.class);
        Map invokers = Mockito.mock(Map.class);
        Mockito.when(invokers.size()).thenReturn(100);
        Mockito.when(directory.getUrlInvokerMap()).thenReturn(invokers);
        ProviderConsumerRegTable.registerConsumer(consumerInvoker, registryUrl, consumerUrl, directory);
        Ls ls = new Ls();
        String output = ls.execute(Mockito.mock(CommandContext.class), null);
        MatcherAssert.assertThat(output, Matchers.containsString("org.apache.dubbo.FooService|100"));
        MatcherAssert.assertThat(output, Matchers.containsString("org.apache.dubbo.BarService| Y"));
    }
}

