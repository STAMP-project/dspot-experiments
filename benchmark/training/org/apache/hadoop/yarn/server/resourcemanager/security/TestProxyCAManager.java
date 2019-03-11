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
package org.apache.hadoop.yarn.server.resourcemanager.security;


import RMStateStore.ProxyCAState;
import RMStateStore.RMState;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.yarn.server.webproxy.ProxyCA;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestProxyCAManager {
    @Test
    public void testBasics() throws Exception {
        ProxyCA proxyCA = Mockito.spy(new ProxyCA());
        RMContext rmContext = Mockito.mock(RMContext.class);
        RMStateStore rmStateStore = Mockito.mock(RMStateStore.class);
        Mockito.when(rmContext.getStateStore()).thenReturn(rmStateStore);
        ProxyCAManager proxyCAManager = new ProxyCAManager(proxyCA, rmContext);
        proxyCAManager.init(new YarnConfiguration());
        Assert.assertEquals(proxyCA, proxyCAManager.getProxyCA());
        Mockito.verify(rmContext, Mockito.times(0)).getStateStore();
        Mockito.verify(rmStateStore, Mockito.times(0)).storeProxyCACert(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(proxyCA, Mockito.times(0)).init();
        Assert.assertNull(proxyCA.getCaCert());
        Assert.assertNull(proxyCA.getCaKeyPair());
        proxyCAManager.start();
        Mockito.verify(rmContext, Mockito.times(1)).getStateStore();
        Mockito.verify(rmStateStore, Mockito.times(1)).storeProxyCACert(proxyCA.getCaCert(), proxyCA.getCaKeyPair().getPrivate());
        Mockito.verify(proxyCA, Mockito.times(1)).init();
        Assert.assertNotNull(proxyCA.getCaCert());
        Assert.assertNotNull(proxyCA.getCaKeyPair());
    }

    @Test
    public void testRecover() throws Exception {
        ProxyCA proxyCA = Mockito.spy(new ProxyCA());
        RMContext rmContext = Mockito.mock(RMContext.class);
        RMStateStore rmStateStore = Mockito.mock(RMStateStore.class);
        Mockito.when(rmContext.getStateStore()).thenReturn(rmStateStore);
        ProxyCAManager proxyCAManager = new ProxyCAManager(proxyCA, rmContext);
        proxyCAManager.init(new YarnConfiguration());
        Assert.assertEquals(proxyCA, proxyCAManager.getProxyCA());
        Mockito.verify(rmContext, Mockito.times(0)).getStateStore();
        Mockito.verify(rmStateStore, Mockito.times(0)).storeProxyCACert(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(proxyCA, Mockito.times(0)).init();
        Assert.assertNull(proxyCA.getCaCert());
        Assert.assertNull(proxyCA.getCaKeyPair());
        RMStateStore.RMState rmState = Mockito.mock(RMState.class);
        RMStateStore.ProxyCAState proxyCAState = Mockito.mock(ProxyCAState.class);
        // We need to use a real certificate + private key because of validation
        // so just grab them from another ProxyCA
        ProxyCA otherProxyCA = new ProxyCA();
        otherProxyCA.init();
        X509Certificate certificate = otherProxyCA.getCaCert();
        Mockito.when(proxyCAState.getCaCert()).thenReturn(certificate);
        PrivateKey privateKey = otherProxyCA.getCaKeyPair().getPrivate();
        Mockito.when(proxyCAState.getCaPrivateKey()).thenReturn(privateKey);
        Mockito.when(rmState.getProxyCAState()).thenReturn(proxyCAState);
        proxyCAManager.recover(rmState);
        Mockito.verify(proxyCA, Mockito.times(1)).init(certificate, privateKey);
        Assert.assertEquals(certificate, proxyCA.getCaCert());
        Assert.assertEquals(privateKey, proxyCA.getCaKeyPair().getPrivate());
        proxyCAManager.start();
        Mockito.verify(rmContext, Mockito.times(1)).getStateStore();
        Mockito.verify(rmStateStore, Mockito.times(1)).storeProxyCACert(proxyCA.getCaCert(), proxyCA.getCaKeyPair().getPrivate());
        Mockito.verify(proxyCA, Mockito.times(0)).init();
        Assert.assertEquals(certificate, proxyCA.getCaCert());
        Assert.assertEquals(privateKey, proxyCA.getCaKeyPair().getPrivate());
    }
}

