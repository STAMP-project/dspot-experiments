package com.netflix.appinfo;


import AmazonInfo.Builder;
import AmazonInfo.MetaDataKey.instanceId;
import AmazonInfo.MetaDataKey.localIpv4;
import AmazonInfo.MetaDataKey.publicHostname;
import AmazonInfo.MetaDataKey.spotInstanceAction;
import AmazonInfo.MetaDataKey.spotTerminationTime;
import ApplicationInfoManager.OptionalArgs;
import InstanceInfo.InstanceStatus;
import InstanceInfo.InstanceStatus.UNKNOWN;
import com.netflix.discovery.CommonConstants;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static MetaDataKey.publicHostname;


/**
 *
 *
 * @author David Liu
 */
public class ApplicationInfoManagerTest {
    private CloudInstanceConfig config;

    private String dummyDefault = "dummyDefault";

    private InstanceInfo instanceInfo;

    private ApplicationInfoManager applicationInfoManager;

    @Test
    public void testRefreshDataCenterInfoWithAmazonInfo() {
        String newPublicHostname = "newValue";
        Assert.assertThat(instanceInfo.getHostName(), CoreMatchers.is(CoreMatchers.not(newPublicHostname)));
        getMetadata().put(publicHostname.getName(), newPublicHostname);
        applicationInfoManager.refreshDataCenterInfoIfRequired();
        Assert.assertThat(instanceInfo.getHostName(), CoreMatchers.is(newPublicHostname));
    }

    @Test
    public void testSpotInstanceTermination() {
        AmazonInfo initialAmazonInfo = Builder.newBuilder().build();
        RefreshableAmazonInfoProvider refreshableAmazonInfoProvider = Mockito.spy(new RefreshableAmazonInfoProvider(initialAmazonInfo, new Archaius1AmazonInfoConfig(CommonConstants.DEFAULT_CONFIG_NAMESPACE)));
        config = Mockito.spy(new CloudInstanceConfig(CommonConstants.DEFAULT_CONFIG_NAMESPACE, refreshableAmazonInfoProvider));
        this.applicationInfoManager = new ApplicationInfoManager(config, instanceInfo, null);
        String terminationTime = "2015-01-05T18:02:00Z";
        String spotInstanceAction = "{\"action\": \"terminate\", \"time\": \"2017-09-18T08:22:00Z\"}";
        AmazonInfo newAmazonInfo = // unchanged
        // unchanged
        // unchanged
        // new property refresh
        // new property on refresh
        Builder.newBuilder().addMetadata(spotTerminationTime, terminationTime).addMetadata(spotInstanceAction, spotInstanceAction).addMetadata(publicHostname, instanceInfo.getHostName()).addMetadata(instanceId, instanceInfo.getInstanceId()).addMetadata(localIpv4, instanceInfo.getIPAddr()).build();
        Mockito.when(refreshableAmazonInfoProvider.getNewAmazonInfo()).thenReturn(newAmazonInfo);
        applicationInfoManager.refreshDataCenterInfoIfRequired();
        Assert.assertThat(getMetadata().get(spotTerminationTime.getName()), CoreMatchers.is(terminationTime));
        Assert.assertThat(getMetadata().get(spotInstanceAction.getName()), CoreMatchers.is(spotInstanceAction));
    }

    @Test
    public void testCustomInstanceStatusMapper() {
        ApplicationInfoManager.OptionalArgs optionalArgs = new ApplicationInfoManager.OptionalArgs();
        optionalArgs.setInstanceStatusMapper(new ApplicationInfoManager.InstanceStatusMapper() {
            @Override
            public InstanceStatus map(InstanceInfo.InstanceStatus prev) {
                return InstanceStatus.UNKNOWN;
            }
        });
        applicationInfoManager = new ApplicationInfoManager(config, instanceInfo, optionalArgs);
        InstanceInfo.InstanceStatus existingStatus = applicationInfoManager.getInfo().getStatus();
        Assert.assertNotEquals(existingStatus, UNKNOWN);
        applicationInfoManager.setInstanceStatus(UNKNOWN);
        existingStatus = applicationInfoManager.getInfo().getStatus();
        Assert.assertEquals(existingStatus, UNKNOWN);
    }

    @Test
    public void testNullResultInstanceStatusMapper() {
        ApplicationInfoManager.OptionalArgs optionalArgs = new ApplicationInfoManager.OptionalArgs();
        optionalArgs.setInstanceStatusMapper(new ApplicationInfoManager.InstanceStatusMapper() {
            @Override
            public InstanceStatus map(InstanceInfo.InstanceStatus prev) {
                return null;
            }
        });
        applicationInfoManager = new ApplicationInfoManager(config, instanceInfo, optionalArgs);
        InstanceInfo.InstanceStatus existingStatus1 = applicationInfoManager.getInfo().getStatus();
        Assert.assertNotEquals(existingStatus1, UNKNOWN);
        applicationInfoManager.setInstanceStatus(UNKNOWN);
        InstanceInfo.InstanceStatus existingStatus2 = applicationInfoManager.getInfo().getStatus();
        Assert.assertEquals(existingStatus2, existingStatus1);
    }
}

