package com.netflix.eureka.util;


import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.eureka.aws.AwsAsgUtil;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author David Liu
 */
public class AwsAsgUtilTest {
    private ApplicationInfoManager applicationInfoManager;

    private PeerAwareInstanceRegistry registry;

    private DiscoveryClient client;

    private AwsAsgUtil awsAsgUtil;

    private InstanceInfo instanceInfo;

    @Test
    public void testDefaultAsgStatus() {
        Assert.assertEquals(true, awsAsgUtil.isASGEnabled(instanceInfo));
    }

    @Test
    public void testAsyncLoadingFromCache() {
        // TODO
    }
}

