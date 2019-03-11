/**
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt.consul;


import com.networknt.registry.URL;
import com.networknt.registry.support.command.ServiceListener;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ConsulRegistryTest {
    private MockConsulClient client;

    private ConsulRegistry registry;

    private URL serviceUrl;

    private URL serviceUrl2;

    private URL clientUrl;

    private URL clientUrl2;

    private String serviceid;

    private String serviceid2;

    private long sleepTime;

    @Test
    public void doRegisterAndAvailable() throws Exception {
        // register
        registry.doRegister(serviceUrl);
        registry.doRegister(serviceUrl2);
        Assert.assertTrue(client.isRegistered(serviceid));
        Assert.assertFalse(client.isWorking(serviceid));
        Assert.assertTrue(client.isRegistered(serviceid2));
        Assert.assertFalse(client.isWorking(serviceid2));
        // available
        registry.doAvailable(null);
        Thread.sleep(sleepTime);
        Assert.assertTrue(client.isWorking(serviceid));
        Assert.assertTrue(client.isWorking(serviceid2));
        // unavailable
        registry.doUnavailable(null);
        Thread.sleep(sleepTime);
        Assert.assertFalse(client.isWorking(serviceid));
        Assert.assertFalse(client.isWorking(serviceid2));
        // unregister
        registry.doUnregister(serviceUrl);
        Assert.assertFalse(client.isRegistered(serviceid));
        Assert.assertTrue(client.isRegistered(serviceid2));
        registry.doUnregister(serviceUrl2);
        Assert.assertFalse(client.isRegistered(serviceid2));
    }

    @Test
    public void subAndUnsubService() throws Exception {
        ServiceListener serviceListener = createNewServiceListener(serviceUrl);
        ServiceListener serviceListener2 = createNewServiceListener(serviceUrl);
        registry.subscribeService(clientUrl, serviceListener);
        registry.subscribeService(clientUrl2, serviceListener2);
        Assert.assertTrue(containsServiceListener(serviceUrl, clientUrl, serviceListener));
        Assert.assertTrue(containsServiceListener(serviceUrl, clientUrl2, serviceListener2));
        registry.doRegister(serviceUrl);
        registry.doRegister(serviceUrl2);
        registry.doAvailable(null);
        Thread.sleep(sleepTime);
        registry.unsubscribeService(clientUrl, serviceListener);
        Assert.assertFalse(containsServiceListener(serviceUrl, clientUrl, serviceListener));
        Assert.assertTrue(containsServiceListener(serviceUrl, clientUrl2, serviceListener2));
        registry.unsubscribeService(clientUrl2, serviceListener2);
        Assert.assertFalse(containsServiceListener(serviceUrl, clientUrl2, serviceListener2));
    }

    @Test
    public void discoverService() throws Exception {
        registry.doRegister(serviceUrl);
        List<URL> urls = registry.discoverService(serviceUrl);
        Assert.assertNull(urls);
        registry.doAvailable(null);
        Thread.sleep(sleepTime);
        urls = registry.discoverService(serviceUrl);
        Assert.assertTrue(urls.contains(serviceUrl));
    }
}

