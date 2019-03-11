package com.netflix.discovery.guice;


import com.google.inject.Binding;
import com.google.inject.Key;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.transport.jersey.TransportClientFactories;
import com.netflix.governator.LifecycleInjector;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author David Liu
 */
public class EurekaModuleTest {
    private LifecycleInjector injector;

    @SuppressWarnings("deprecation")
    @Test
    public void testDI() {
        InstanceInfo instanceInfo = injector.getInstance(InstanceInfo.class);
        Assert.assertEquals(ApplicationInfoManager.getInstance().getInfo(), instanceInfo);
        EurekaClient eurekaClient = injector.getInstance(EurekaClient.class);
        DiscoveryClient discoveryClient = injector.getInstance(DiscoveryClient.class);
        Assert.assertEquals(DiscoveryManager.getInstance().getEurekaClient(), eurekaClient);
        Assert.assertEquals(DiscoveryManager.getInstance().getDiscoveryClient(), discoveryClient);
        Assert.assertEquals(eurekaClient, discoveryClient);
        EurekaClientConfig eurekaClientConfig = injector.getInstance(EurekaClientConfig.class);
        Assert.assertEquals(DiscoveryManager.getInstance().getEurekaClientConfig(), eurekaClientConfig);
        EurekaInstanceConfig eurekaInstanceConfig = injector.getInstance(EurekaInstanceConfig.class);
        Assert.assertEquals(DiscoveryManager.getInstance().getEurekaInstanceConfig(), eurekaInstanceConfig);
        Binding<TransportClientFactories> binding = injector.getExistingBinding(Key.get(TransportClientFactories.class));
        Assert.assertNull(binding);// no bindings so defaulting to default of jersey1

    }
}

