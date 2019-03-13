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
package org.apache.dubbo.monitor.dubbo;


import Constants.REFERENCE_FILTER_KEY;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.monitor.Monitor;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.ProxyFactory;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DubboMonitorFactoryTest {
    private DubboMonitorFactory dubboMonitorFactory;

    @Mock
    private ProxyFactory proxyFactory;

    @Test
    public void testCreateMonitor() {
        URL urlWithoutPath = URL.valueOf("http://10.10.10.11");
        Monitor monitor = dubboMonitorFactory.createMonitor(urlWithoutPath);
        MatcherAssert.assertThat(monitor, Matchers.not(CoreMatchers.nullValue()));
        URL urlWithFilterKey = URL.valueOf("http://10.10.10.11/").addParameter(REFERENCE_FILTER_KEY, "testFilter");
        monitor = dubboMonitorFactory.createMonitor(urlWithFilterKey);
        MatcherAssert.assertThat(monitor, Matchers.not(CoreMatchers.nullValue()));
        ArgumentCaptor<Invoker> invokerArgumentCaptor = ArgumentCaptor.forClass(Invoker.class);
        Mockito.verify(proxyFactory, Mockito.atLeastOnce()).getProxy(invokerArgumentCaptor.capture());
        Invoker invoker = invokerArgumentCaptor.getValue();
        MatcherAssert.assertThat(invoker.getUrl().getParameter(REFERENCE_FILTER_KEY), CoreMatchers.containsString("testFilter"));
    }
}

