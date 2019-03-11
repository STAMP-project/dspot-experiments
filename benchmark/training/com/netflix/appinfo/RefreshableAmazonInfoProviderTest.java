package com.netflix.appinfo;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static MetaDataKey.amiId;
import static MetaDataKey.instanceId;
import static MetaDataKey.localIpv4;


/**
 *
 *
 * @author David Liu
 */
public class RefreshableAmazonInfoProviderTest {
    private InstanceInfo instanceInfo;

    @Test
    public void testAmazonInfoNoUpdateIfEqual() {
        AmazonInfo oldInfo = ((AmazonInfo) (instanceInfo.getDataCenterInfo()));
        AmazonInfo newInfo = RefreshableAmazonInfoProviderTest.copyAmazonInfo(instanceInfo);
        Assert.assertThat(RefreshableAmazonInfoProvider.shouldUpdate(newInfo, oldInfo), CoreMatchers.is(false));
    }

    @Test
    public void testAmazonInfoNoUpdateIfEmpty() {
        AmazonInfo oldInfo = ((AmazonInfo) (instanceInfo.getDataCenterInfo()));
        AmazonInfo newInfo = new AmazonInfo();
        Assert.assertThat(RefreshableAmazonInfoProvider.shouldUpdate(newInfo, oldInfo), CoreMatchers.is(false));
    }

    @Test
    public void testAmazonInfoNoUpdateIfNoInstanceId() {
        AmazonInfo oldInfo = ((AmazonInfo) (instanceInfo.getDataCenterInfo()));
        AmazonInfo newInfo = RefreshableAmazonInfoProviderTest.copyAmazonInfo(instanceInfo);
        newInfo.getMetadata().remove(instanceId.getName());
        Assert.assertThat(newInfo.getId(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(newInfo.get(instanceId), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(CloudInstanceConfig.shouldUpdate(newInfo, oldInfo), CoreMatchers.is(false));
        newInfo.getMetadata().put(instanceId.getName(), "");
        Assert.assertThat(newInfo.getId(), CoreMatchers.is(""));
        Assert.assertThat(newInfo.get(instanceId), CoreMatchers.is(""));
        Assert.assertThat(RefreshableAmazonInfoProvider.shouldUpdate(newInfo, oldInfo), CoreMatchers.is(false));
    }

    @Test
    public void testAmazonInfoNoUpdateIfNoLocalIpv4() {
        AmazonInfo oldInfo = ((AmazonInfo) (instanceInfo.getDataCenterInfo()));
        AmazonInfo newInfo = RefreshableAmazonInfoProviderTest.copyAmazonInfo(instanceInfo);
        newInfo.getMetadata().remove(localIpv4.getName());
        Assert.assertThat(newInfo.get(localIpv4), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(CloudInstanceConfig.shouldUpdate(newInfo, oldInfo), CoreMatchers.is(false));
        newInfo.getMetadata().put(localIpv4.getName(), "");
        Assert.assertThat(newInfo.get(localIpv4), CoreMatchers.is(""));
        Assert.assertThat(RefreshableAmazonInfoProvider.shouldUpdate(newInfo, oldInfo), CoreMatchers.is(false));
    }

    @Test
    public void testAmazonInfoUpdatePositiveCase() {
        AmazonInfo oldInfo = ((AmazonInfo) (instanceInfo.getDataCenterInfo()));
        AmazonInfo newInfo = RefreshableAmazonInfoProviderTest.copyAmazonInfo(instanceInfo);
        newInfo.getMetadata().remove(amiId.getName());
        Assert.assertThat(newInfo.getMetadata().size(), CoreMatchers.is(((oldInfo.getMetadata().size()) - 1)));
        Assert.assertThat(RefreshableAmazonInfoProvider.shouldUpdate(newInfo, oldInfo), CoreMatchers.is(true));
        String newKey = "someNewKey";
        newInfo.getMetadata().put(newKey, "bar");
        Assert.assertThat(newInfo.getMetadata().size(), CoreMatchers.is(oldInfo.getMetadata().size()));
        Assert.assertThat(RefreshableAmazonInfoProvider.shouldUpdate(newInfo, oldInfo), CoreMatchers.is(true));
    }
}

