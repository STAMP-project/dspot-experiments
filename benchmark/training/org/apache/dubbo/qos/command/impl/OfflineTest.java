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


import org.apache.dubbo.common.URL;
import org.apache.dubbo.qos.command.CommandContext;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.support.ProviderConsumerRegTable;
import org.apache.dubbo.registry.support.ProviderInvokerWrapper;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.model.ProviderModel;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class OfflineTest {
    @Test
    public void testExecute() throws Exception {
        ProviderModel providerModel = Mockito.mock(ProviderModel.class);
        Mockito.when(providerModel.getServiceName()).thenReturn("org.apache.dubbo.BarService");
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
        Registry registry = Mockito.mock(Registry.class);
        TestRegistryFactory.registry = registry;
        Offline offline = new Offline();
        String output = offline.execute(Mockito.mock(CommandContext.class), new String[]{ "org.apache.dubbo.BarService" });
        MatcherAssert.assertThat(output, Matchers.containsString("OK"));
        Mockito.verify(registry).unregister(providerUrl);
        for (ProviderInvokerWrapper wrapper : getProviderInvoker("org.apache.dubbo.BarService")) {
            MatcherAssert.assertThat(wrapper.isReg(), Is.is(false));
        }
        output = offline.execute(Mockito.mock(CommandContext.class), new String[]{ "org.apache.dubbo.FooService" });
        MatcherAssert.assertThat(output, Matchers.containsString("service not found"));
    }
}

