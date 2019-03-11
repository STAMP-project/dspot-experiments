package com.netflix.discovery.guice;


import DataCenterInfo.Name.MyOwn;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaArchaius2InstanceConfig;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.providers.Archaius2VipAddressResolver;
import com.netflix.appinfo.providers.VipAddressResolver;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.governator.LifecycleInjector;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author David Liu
 */
public class NonEc2EurekaClientModuleTest {
    private LifecycleInjector injector;

    @SuppressWarnings("deprecation")
    @Test
    public void testDI() {
        InstanceInfo instanceInfo = injector.getInstance(InstanceInfo.class);
        Assert.assertEquals(ApplicationInfoManager.getInstance().getInfo(), instanceInfo);
        VipAddressResolver vipAddressResolver = injector.getInstance(VipAddressResolver.class);
        Assert.assertTrue((vipAddressResolver instanceof Archaius2VipAddressResolver));
        EurekaClient eurekaClient = injector.getInstance(EurekaClient.class);
        DiscoveryClient discoveryClient = injector.getInstance(DiscoveryClient.class);
        Assert.assertEquals(DiscoveryManager.getInstance().getEurekaClient(), eurekaClient);
        Assert.assertEquals(DiscoveryManager.getInstance().getDiscoveryClient(), discoveryClient);
        Assert.assertEquals(eurekaClient, discoveryClient);
        EurekaClientConfig eurekaClientConfig = injector.getInstance(EurekaClientConfig.class);
        Assert.assertEquals(DiscoveryManager.getInstance().getEurekaClientConfig(), eurekaClientConfig);
        EurekaInstanceConfig eurekaInstanceConfig = injector.getInstance(EurekaInstanceConfig.class);
        Assert.assertEquals(DiscoveryManager.getInstance().getEurekaInstanceConfig(), eurekaInstanceConfig);
        Assert.assertTrue((eurekaInstanceConfig instanceof EurekaArchaius2InstanceConfig));
        ApplicationInfoManager applicationInfoManager = injector.getInstance(ApplicationInfoManager.class);
        InstanceInfo myInfo = applicationInfoManager.getInfo();
        Assert.assertEquals(MyOwn, myInfo.getDataCenterInfo().getName());
    }
}

