package com.netflix.discovery.shared;


import DataCenterInfo.Name;
import InstanceInfo.Builder;
import InstanceStatus.DOWN;
import InstanceStatus.UP;
import MetaDataKey.availabilityZone;
import com.google.common.collect.Iterables;
import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.AzToRegionMapper;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.InstanceRegionChecker;
import com.netflix.discovery.PropertyBasedAzToRegionMapper;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ApplicationsTest {
    @Test
    public void testVersionAndAppHash() {
        Applications apps = new Applications();
        Assert.assertEquals((-1L), ((long) (apps.getVersion())));
        Assert.assertNull(apps.getAppsHashCode());
        apps.setVersion(101L);
        apps.setAppsHashCode("UP_5_DOWN_6_");
        Assert.assertEquals(101L, ((long) (apps.getVersion())));
        Assert.assertEquals("UP_5_DOWN_6_", apps.getAppsHashCode());
    }

    /**
     * Test that instancesMap in Application and shuffleVirtualHostNameMap in
     * Applications are correctly updated when the last instance is removed from
     * an application and shuffleInstances has been run.
     */
    @Test
    public void shuffleVirtualHostNameMapLastInstanceTest() {
        DataCenterInfo myDCI = new DataCenterInfo() {
            public Name getName() {
                return Name.MyOwn;
            }
        };
        InstanceInfo instanceInfo = Builder.newBuilder().setAppName("test").setVIPAddress("test.testname:1").setDataCenterInfo(myDCI).setHostName("test.hostname").build();
        Application application = new Application("TestApp");
        application.addInstance(instanceInfo);
        Applications applications = new Applications();
        applications.addApplication(application);
        applications.shuffleInstances(true);
        List<InstanceInfo> testApp = applications.getInstancesByVirtualHostName("test.testname:1");
        Assert.assertEquals(Iterables.getOnlyElement(testApp), application.getByInstanceId("test.hostname"));
        application.removeInstance(instanceInfo);
        Assert.assertEquals(0, applications.size());
        applications.shuffleInstances(true);
        testApp = applications.getInstancesByVirtualHostName("test.testname:1");
        Assert.assertTrue(testApp.isEmpty());
        Assert.assertNull(application.getByInstanceId("test.hostname"));
    }

    /**
     * Test that instancesMap in Application and shuffleVirtualHostNameMap in
     * Applications are correctly updated when the last instance is removed from
     * an application and shuffleInstances has been run.
     */
    @Test
    public void shuffleSecureVirtualHostNameMapLastInstanceTest() {
        DataCenterInfo myDCI = new DataCenterInfo() {
            public Name getName() {
                return Name.MyOwn;
            }
        };
        InstanceInfo instanceInfo = Builder.newBuilder().setAppName("test").setVIPAddress("test.testname:1").setSecureVIPAddress("securetest.testname:7102").setDataCenterInfo(myDCI).setHostName("test.hostname").build();
        Application application = new Application("TestApp");
        application.addInstance(instanceInfo);
        Applications applications = new Applications();
        Assert.assertEquals(0, applications.size());
        applications.addApplication(application);
        Assert.assertEquals(1, applications.size());
        applications.shuffleInstances(true);
        List<InstanceInfo> testApp = applications.getInstancesByVirtualHostName("test.testname:1");
        Assert.assertEquals(Iterables.getOnlyElement(testApp), application.getByInstanceId("test.hostname"));
        application.removeInstance(instanceInfo);
        Assert.assertNull(application.getByInstanceId("test.hostname"));
        Assert.assertEquals(0, applications.size());
        applications.shuffleInstances(true);
        testApp = applications.getInstancesBySecureVirtualHostName("securetest.testname:7102");
        Assert.assertTrue(testApp.isEmpty());
        Assert.assertNull(application.getByInstanceId("test.hostname"));
    }

    /**
     * Test that instancesMap in Application and shuffleVirtualHostNameMap in
     * Applications are correctly updated when the last instance is removed from
     * an application and shuffleInstances has been run.
     */
    @Test
    public void shuffleRemoteRegistryTest() throws Exception {
        AmazonInfo ai1 = AmazonInfo.Builder.newBuilder().addMetadata(availabilityZone, "us-east-1a").build();
        InstanceInfo instanceInfo1 = Builder.newBuilder().setAppName("test").setVIPAddress("test.testname:1").setSecureVIPAddress("securetest.testname:7102").setDataCenterInfo(ai1).setAppName("TestApp").setHostName("test.east.hostname").build();
        AmazonInfo ai2 = AmazonInfo.Builder.newBuilder().addMetadata(availabilityZone, "us-west-2a").build();
        InstanceInfo instanceInfo2 = Builder.newBuilder().setAppName("test").setVIPAddress("test.testname:1").setSecureVIPAddress("securetest.testname:7102").setDataCenterInfo(ai2).setAppName("TestApp").setHostName("test.west.hostname").build();
        Application application = new Application("TestApp");
        application.addInstance(instanceInfo1);
        application.addInstance(instanceInfo2);
        Applications applications = new Applications();
        Assert.assertEquals(0, applications.size());
        applications.addApplication(application);
        Assert.assertEquals(2, applications.size());
        EurekaClientConfig clientConfig = Mockito.mock(EurekaClientConfig.class);
        Mockito.when(clientConfig.getAvailabilityZones("us-east-1")).thenReturn(new String[]{ "us-east-1a", "us-east-1b", "us-east-1c", "us-east-1d", "us-east-1e", "us-east-1f" });
        Mockito.when(clientConfig.getAvailabilityZones("us-west-2")).thenReturn(new String[]{ "us-west-2a", "us-west-2b", "us-west-2c" });
        Mockito.when(clientConfig.getRegion()).thenReturn("us-east-1");
        Constructor<?> ctor = InstanceRegionChecker.class.getDeclaredConstructor(AzToRegionMapper.class, String.class);
        ctor.setAccessible(true);
        PropertyBasedAzToRegionMapper azToRegionMapper = new PropertyBasedAzToRegionMapper(clientConfig);
        azToRegionMapper.setRegionsToFetch(new String[]{ "us-east-1", "us-west-2" });
        InstanceRegionChecker instanceRegionChecker = ((InstanceRegionChecker) (ctor.newInstance(azToRegionMapper, "us-west-2")));
        Map<String, Applications> remoteRegionsRegistry = new HashMap<>();
        remoteRegionsRegistry.put("us-east-1", new Applications());
        applications.shuffleAndIndexInstances(remoteRegionsRegistry, clientConfig, instanceRegionChecker);
        Assert.assertNotNull(remoteRegionsRegistry.get("us-east-1").getRegisteredApplications("TestApp").getByInstanceId("test.east.hostname"));
        Assert.assertNull(applications.getRegisteredApplications("TestApp").getByInstanceId("test.east.hostname"));
        Assert.assertNull(remoteRegionsRegistry.get("us-east-1").getRegisteredApplications("TestApp").getByInstanceId("test.west.hostname"));
        Assert.assertNotNull(applications.getRegisteredApplications("TestApp").getByInstanceId("test.west.hostname"));
    }

    @Test
    public void testInfoDetailApplications() {
        DataCenterInfo myDCI = new DataCenterInfo() {
            public Name getName() {
                return Name.MyOwn;
            }
        };
        InstanceInfo instanceInfo = Builder.newBuilder().setInstanceId("test.id").setAppName("test").setHostName("test.hostname").setStatus(UP).setIPAddr("test.testip:1").setPort(8080).setSecurePort(443).setDataCenterInfo(myDCI).build();
        Application application = new Application("Test App");
        application.addInstance(instanceInfo);
        Applications applications = new Applications();
        applications.addApplication(application);
        List<InstanceInfo> instanceInfos = application.getInstances();
        Assert.assertEquals(1, instanceInfos.size());
        Assert.assertTrue(instanceInfos.contains(instanceInfo));
        List<Application> appsList = applications.getRegisteredApplications();
        Assert.assertEquals(1, appsList.size());
        Assert.assertTrue(appsList.contains(application));
        Assert.assertEquals(application, applications.getRegisteredApplications(application.getName()));
    }

    @Test
    public void testRegisteredApplications() {
        DataCenterInfo myDCI = new DataCenterInfo() {
            public Name getName() {
                return Name.MyOwn;
            }
        };
        InstanceInfo instanceInfo = Builder.newBuilder().setAppName("test").setVIPAddress("test.testname:1").setSecureVIPAddress("securetest.testname:7102").setDataCenterInfo(myDCI).setHostName("test.hostname").build();
        Application application = new Application("TestApp");
        application.addInstance(instanceInfo);
        Applications applications = new Applications();
        applications.addApplication(application);
        List<Application> appsList = applications.getRegisteredApplications();
        Assert.assertEquals(1, appsList.size());
        Assert.assertTrue(appsList.contains(application));
        Assert.assertEquals(application, applications.getRegisteredApplications(application.getName()));
    }

    @Test
    public void testRegisteredApplicationsConstructor() {
        DataCenterInfo myDCI = new DataCenterInfo() {
            public Name getName() {
                return Name.MyOwn;
            }
        };
        InstanceInfo instanceInfo = Builder.newBuilder().setAppName("test").setVIPAddress("test.testname:1").setSecureVIPAddress("securetest.testname:7102").setDataCenterInfo(myDCI).setHostName("test.hostname").build();
        Application application = new Application("TestApp");
        application.addInstance(instanceInfo);
        Applications applications = new Applications("UP_1_", (-1L), Arrays.asList(application));
        List<Application> appsList = applications.getRegisteredApplications();
        Assert.assertEquals(1, appsList.size());
        Assert.assertTrue(appsList.contains(application));
        Assert.assertEquals(application, applications.getRegisteredApplications(application.getName()));
    }

    @Test
    public void testApplicationsHashAndVersion() {
        Applications applications = new Applications("appsHashCode", 1L, Collections.emptyList());
        Assert.assertEquals(1L, ((long) (applications.getVersion())));
        Assert.assertEquals("appsHashCode", applications.getAppsHashCode());
    }

    @Test
    public void testPopulateInstanceCount() {
        DataCenterInfo myDCI = new DataCenterInfo() {
            public Name getName() {
                return Name.MyOwn;
            }
        };
        InstanceInfo instanceInfo = Builder.newBuilder().setAppName("test").setVIPAddress("test.testname:1").setSecureVIPAddress("securetest.testname:7102").setDataCenterInfo(myDCI).setHostName("test.hostname").setStatus(UP).build();
        Application application = new Application("TestApp");
        application.addInstance(instanceInfo);
        Applications applications = new Applications();
        applications.addApplication(application);
        TreeMap<String, AtomicInteger> instanceCountMap = new TreeMap<>();
        applications.populateInstanceCountMap(instanceCountMap);
        Assert.assertEquals(1, instanceCountMap.size());
        Assert.assertNotNull(instanceCountMap.get(UP.name()));
        Assert.assertEquals(1, instanceCountMap.get(UP.name()).get());
    }

    @Test
    public void testGetNextIndex() {
        DataCenterInfo myDCI = new DataCenterInfo() {
            public Name getName() {
                return Name.MyOwn;
            }
        };
        InstanceInfo instanceInfo = Builder.newBuilder().setAppName("test").setVIPAddress("test.testname:1").setSecureVIPAddress("securetest.testname:7102").setDataCenterInfo(myDCI).setHostName("test.hostname").setStatus(UP).build();
        Application application = new Application("TestApp");
        application.addInstance(instanceInfo);
        Applications applications = new Applications();
        applications.addApplication(application);
        Assert.assertNotNull(applications.getNextIndex("test.testname:1", false));
        Assert.assertEquals(0L, applications.getNextIndex("test.testname:1", false).get());
        Assert.assertNotNull(applications.getNextIndex("securetest.testname:7102", true));
        Assert.assertEquals(0L, applications.getNextIndex("securetest.testname:7102", true).get());
        Assert.assertNotSame(applications.getNextIndex("test.testname:1", false), applications.getNextIndex("securetest.testname:7102", true));
    }

    @Test
    public void testReconcileHashcode() {
        DataCenterInfo myDCI = new DataCenterInfo() {
            public Name getName() {
                return Name.MyOwn;
            }
        };
        InstanceInfo instanceInfo = Builder.newBuilder().setAppName("test").setVIPAddress("test.testname:1").setSecureVIPAddress("securetest.testname:7102").setDataCenterInfo(myDCI).setHostName("test.hostname").setStatus(UP).build();
        Application application = new Application("TestApp");
        application.addInstance(instanceInfo);
        Applications applications = new Applications();
        String hashCode = applications.getReconcileHashCode();
        Assert.assertTrue(hashCode.isEmpty());
        applications.addApplication(application);
        hashCode = applications.getReconcileHashCode();
        Assert.assertFalse(hashCode.isEmpty());
        Assert.assertEquals("UP_1_", hashCode);
    }

    @Test
    public void testInstanceFiltering() {
        DataCenterInfo myDCI = new DataCenterInfo() {
            public Name getName() {
                return Name.MyOwn;
            }
        };
        InstanceInfo instanceInfo = Builder.newBuilder().setAppName("test").setVIPAddress("test.testname:1").setSecureVIPAddress("securetest.testname:7102").setDataCenterInfo(myDCI).setHostName("test.hostname").setStatus(DOWN).build();
        Application application = new Application("TestApp");
        application.addInstance(instanceInfo);
        Applications applications = new Applications();
        applications.addApplication(application);
        applications.shuffleInstances(true);
        Assert.assertNotNull(applications.getRegisteredApplications("TestApp").getByInstanceId("test.hostname"));
        Assert.assertTrue(applications.getInstancesBySecureVirtualHostName("securetest.testname:7102").isEmpty());
        Assert.assertTrue(applications.getInstancesBySecureVirtualHostName("test.testname:1").isEmpty());
    }
}

