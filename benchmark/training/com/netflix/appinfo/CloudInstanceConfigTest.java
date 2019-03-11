package com.netflix.appinfo;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static MetaDataKey.localIpv4;
import static MetaDataKey.publicHostname;


/**
 *
 *
 * @author David Liu
 */
public class CloudInstanceConfigTest {
    private CloudInstanceConfig config;

    private String dummyDefault = "dummyDefault";

    private InstanceInfo instanceInfo;

    @Test
    public void testResolveDefaultAddress() {
        AmazonInfo info = ((AmazonInfo) (instanceInfo.getDataCenterInfo()));
        config = createConfig(info);
        Assert.assertThat(config.resolveDefaultAddress(false), CoreMatchers.is(info.get(publicHostname)));
        info.getMetadata().remove(publicHostname.getName());
        config = createConfig(info);
        Assert.assertThat(config.resolveDefaultAddress(false), CoreMatchers.is(info.get(localIpv4)));
        info.getMetadata().remove(localIpv4.getName());
        config = createConfig(info);
        Assert.assertThat(config.resolveDefaultAddress(false), CoreMatchers.is(dummyDefault));
    }
}

