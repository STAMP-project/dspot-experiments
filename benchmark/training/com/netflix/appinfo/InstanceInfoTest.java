package com.netflix.appinfo;


import DataCenterInfo.Name;
import InstanceInfo.InstanceStatus.UNKNOWN;
import PortType.SECURE;
import com.netflix.discovery.util.InstanceInfoGenerator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static Builder.newBuilder;


/**
 * Created by jzarfoss on 2/12/14.
 */
public class InstanceInfoTest {
    // contrived test to check copy constructor and verify behavior of builder for InstanceInfo
    @Test
    public void testCopyConstructor() {
        DataCenterInfo myDCI = new DataCenterInfo() {
            public Name getName() {
                return Name.MyOwn;
            }
        };
        InstanceInfo smallII1 = newBuilder().setAppName("test").setDataCenterInfo(myDCI).build();
        InstanceInfo smallII2 = new InstanceInfo(smallII1);
        Assert.assertNotSame(smallII1, smallII2);
        Assert.assertEquals(smallII1, smallII2);
        InstanceInfo fullII1 = newBuilder().setMetadata(null).setOverriddenStatus(UNKNOWN).setHostName("localhost").setSecureVIPAddress("testSecureVIP:22").setStatus(UNKNOWN).setStatusPageUrl("relative", "explicit/relative").setVIPAddress("testVIP:21").setAppName("test").setASGName("testASG").setDataCenterInfo(myDCI).setHealthCheckUrls("relative", "explicit/relative", "secureExplicit/relative").setHomePageUrl("relativeHP", "explicitHP/relativeHP").setIPAddr("127.0.0.1").setPort(21).setSecurePort(22).build();
        InstanceInfo fullII2 = new InstanceInfo(fullII1);
        Assert.assertNotSame(fullII1, fullII2);
        Assert.assertEquals(fullII1, fullII2);
    }

    @Test
    public void testAppGroupNameSystemProp() throws Exception {
        String appGroup = "testAppGroupSystemProp";
        setOverrideProperty("NETFLIX_APP_GROUP", appGroup);
        MyDataCenterInstanceConfig config = new MyDataCenterInstanceConfig();
        Assert.assertEquals("Unexpected app group name", appGroup, config.getAppGroupName());
    }

    @Test
    public void testAppGroupName() throws Exception {
        String appGroup = "testAppGroup";
        setOverrideProperty("eureka.appGroup", appGroup);
        MyDataCenterInstanceConfig config = new MyDataCenterInstanceConfig();
        Assert.assertEquals("Unexpected app group name", appGroup, config.getAppGroupName());
    }

    @Test
    public void testHealthCheckSetContainsValidUrlEntries() throws Exception {
        com.netflix.appinfo.InstanceInfo.Builder builder = newBuilder().setAppName("test").setNamespace("eureka.").setHostName("localhost").setPort(80).setSecurePort(443).enablePort(SECURE, true);
        // No health check URLs
        InstanceInfo noHealtcheckInstanceInfo = builder.build();
        Assert.assertThat(noHealtcheckInstanceInfo.getHealthCheckUrls().size(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        // Now when health check is defined
        InstanceInfo instanceInfo = builder.setHealthCheckUrls("/healthcheck", "http://${eureka.hostname}/healthcheck", "https://${eureka.hostname}/healthcheck").build();
        Assert.assertThat(instanceInfo.getHealthCheckUrls().size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
    }

    @Test
    public void testGetIdWithInstanceIdUsed() {
        InstanceInfo baseline = InstanceInfoGenerator.takeOne();
        String dataCenterInfoId = getId();
        Assert.assertThat(baseline.getInstanceId(), CoreMatchers.is(baseline.getId()));
        Assert.assertThat(dataCenterInfoId, CoreMatchers.is(baseline.getId()));
        String customInstanceId = "someId";
        InstanceInfo instanceInfo = new InstanceInfo.Builder(baseline).setInstanceId(customInstanceId).build();
        dataCenterInfoId = getId();
        Assert.assertThat(instanceInfo.getInstanceId(), CoreMatchers.is(instanceInfo.getId()));
        Assert.assertThat(customInstanceId, CoreMatchers.is(instanceInfo.getId()));
        Assert.assertThat(dataCenterInfoId, CoreMatchers.is(CoreMatchers.not(baseline.getId())));
    }

    // test case for backwards compatibility
    @Test
    public void testGetIdWithInstanceIdNotUsed() {
        InstanceInfo baseline = InstanceInfoGenerator.takeOne();
        // override the sid with ""
        InstanceInfo instanceInfo1 = new InstanceInfo.Builder(baseline).setInstanceId("").build();
        String dataCenterInfoId = getId();
        Assert.assertThat(instanceInfo1.getInstanceId().isEmpty(), CoreMatchers.is(true));
        Assert.assertThat(instanceInfo1.getInstanceId(), CoreMatchers.is(CoreMatchers.not(instanceInfo1.getId())));
        Assert.assertThat(dataCenterInfoId, CoreMatchers.is(instanceInfo1.getId()));
        // override the sid with null
        InstanceInfo instanceInfo2 = setInstanceId(null).build();
        dataCenterInfoId = getId();
        Assert.assertThat(instanceInfo2.getInstanceId(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(instanceInfo2.getInstanceId(), CoreMatchers.is(CoreMatchers.not(instanceInfo2.getId())));
        Assert.assertThat(dataCenterInfoId, CoreMatchers.is(instanceInfo2.getId()));
    }
}

