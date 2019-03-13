package com.netflix.eureka.registry;


import InstanceStatus.DOWN;
import InstanceStatus.OUT_OF_SERVICE;
import InstanceStatus.STARTING;
import InstanceStatus.UP;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.eureka.AbstractTester;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Nitesh Kant
 */
public class InstanceRegistryTest extends AbstractTester {
    @Test
    public void testSoftDepRemoteUp() throws Exception {
        Assert.assertTrue("Registry access disallowed when remote region is UP.", registry.shouldAllowAccess(false));
        Assert.assertTrue("Registry access disallowed when remote region is UP.", registry.shouldAllowAccess(true));
    }

    @Test
    public void testGetAppsFromAllRemoteRegions() throws Exception {
        Applications apps = registry.getApplicationsFromAllRemoteRegions();
        List<Application> registeredApplications = apps.getRegisteredApplications();
        Assert.assertEquals("Apps size from remote regions do not match", 1, registeredApplications.size());
        Application app = registeredApplications.iterator().next();
        Assert.assertEquals("Added app did not return from remote registry", AbstractTester.REMOTE_REGION_APP_NAME, app.getName());
        Assert.assertEquals("Returned app did not have the instance", 1, app.getInstances().size());
    }

    @Test
    public void testGetAppsDeltaFromAllRemoteRegions() throws Exception {
        registerInstanceLocally(AbstractTester.createLocalInstance(AbstractTester.LOCAL_REGION_INSTANCE_2_HOSTNAME));// / local delta

        waitForDeltaToBeRetrieved();
        Applications appDelta = registry.getApplicationDeltasFromMultipleRegions(null);
        List<Application> registeredApplications = appDelta.getRegisteredApplications();
        Assert.assertEquals("Apps size from remote regions do not match", 2, registeredApplications.size());
        Application localApplication = null;
        Application remApplication = null;
        for (Application registeredApplication : registeredApplications) {
            if (registeredApplication.getName().equalsIgnoreCase(AbstractTester.LOCAL_REGION_APP_NAME)) {
                localApplication = registeredApplication;
            }
            if (registeredApplication.getName().equalsIgnoreCase(AbstractTester.REMOTE_REGION_APP_NAME)) {
                remApplication = registeredApplication;
            }
        }
        Assert.assertNotNull("Did not find local registry app in delta.", localApplication);
        Assert.assertEquals("Local registry app instance count in delta not as expected.", 1, localApplication.getInstances().size());
        Assert.assertNotNull("Did not find remote registry app in delta", remApplication);
        Assert.assertEquals("Remote registry app instance count  in delta not as expected.", 1, remApplication.getInstances().size());
    }

    @Test
    public void testAppsHashCodeAfterRefresh() throws InterruptedException {
        Assert.assertEquals("UP_1_", registry.getApplicationsFromAllRemoteRegions().getAppsHashCode());
        registerInstanceLocally(AbstractTester.createLocalInstance(AbstractTester.LOCAL_REGION_INSTANCE_2_HOSTNAME));
        waitForDeltaToBeRetrieved();
        Assert.assertEquals("UP_2_", registry.getApplicationsFromAllRemoteRegions().getAppsHashCode());
    }

    @Test
    public void testGetAppsFromLocalRegionOnly() throws Exception {
        registerInstanceLocally(AbstractTester.createLocalInstance(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME));
        Applications apps = registry.getApplicationsFromLocalRegionOnly();
        List<Application> registeredApplications = apps.getRegisteredApplications();
        Assert.assertEquals("Apps size from local region do not match", 1, registeredApplications.size());
        Application app = registeredApplications.iterator().next();
        Assert.assertEquals("Added app did not return from local registry", AbstractTester.LOCAL_REGION_APP_NAME, app.getName());
        Assert.assertEquals("Returned app did not have the instance", 1, app.getInstances().size());
    }

    @Test
    public void testGetAppsFromBothRegions() throws Exception {
        registerInstanceLocally(AbstractTester.createRemoteInstance(AbstractTester.LOCAL_REGION_INSTANCE_2_HOSTNAME));
        registerInstanceLocally(AbstractTester.createLocalInstance(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME));
        Applications apps = registry.getApplicationsFromAllRemoteRegions();
        List<Application> registeredApplications = apps.getRegisteredApplications();
        Assert.assertEquals("Apps size from both regions do not match", 2, registeredApplications.size());
        Application locaApplication = null;
        Application remApplication = null;
        for (Application registeredApplication : registeredApplications) {
            if (registeredApplication.getName().equalsIgnoreCase(AbstractTester.LOCAL_REGION_APP_NAME)) {
                locaApplication = registeredApplication;
            }
            if (registeredApplication.getName().equalsIgnoreCase(AbstractTester.REMOTE_REGION_APP_NAME)) {
                remApplication = registeredApplication;
            }
        }
        Assert.assertNotNull("Did not find local registry app", locaApplication);
        Assert.assertEquals("Local registry app instance count not as expected.", 1, locaApplication.getInstances().size());
        Assert.assertNotNull("Did not find remote registry app", remApplication);
        Assert.assertEquals("Remote registry app instance count not as expected.", 2, remApplication.getInstances().size());
    }

    @Test
    public void testStatusOverrideSetAndRemoval() throws Exception {
        InstanceInfo seed = AbstractTester.createLocalInstance(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);
        seed.setLastDirtyTimestamp(100L);
        // Regular registration first
        InstanceInfo myInstance1 = new InstanceInfo(seed);
        registerInstanceLocally(myInstance1);
        verifyLocalInstanceStatus(myInstance1.getId(), UP);
        // Override status
        boolean statusResult = registry.statusUpdate(AbstractTester.LOCAL_REGION_APP_NAME, seed.getId(), OUT_OF_SERVICE, "0", false);
        Assert.assertThat("Couldn't override instance status", statusResult, CoreMatchers.is(true));
        verifyLocalInstanceStatus(seed.getId(), OUT_OF_SERVICE);
        // Register again with status UP to verify that the override is still in place even if the dirtytimestamp is higher
        InstanceInfo myInstance2 = new InstanceInfo(seed);// clone to avoid object state in this test

        myInstance2.setLastDirtyTimestamp(200L);// use a later 'client side' dirty timestamp

        registry.register(myInstance2, 10000000, false);
        verifyLocalInstanceStatus(seed.getId(), OUT_OF_SERVICE);
        // Now remove override
        statusResult = registry.deleteStatusOverride(AbstractTester.LOCAL_REGION_APP_NAME, seed.getId(), DOWN, "0", false);
        Assert.assertThat("Couldn't remove status override", statusResult, CoreMatchers.is(true));
        verifyLocalInstanceStatus(seed.getId(), DOWN);
        // Register again with status UP after the override deletion, keeping myInstance2's dirtyTimestamp (== no client side change)
        InstanceInfo myInstance3 = new InstanceInfo(seed);// clone to avoid object state in this test

        myInstance3.setLastDirtyTimestamp(200L);// use a later 'client side' dirty timestamp

        registry.register(myInstance3, 10000000, false);
        verifyLocalInstanceStatus(seed.getId(), UP);
    }

    @Test
    public void testStatusOverrideWithRenewAppliedToAReplica() throws Exception {
        InstanceInfo seed = AbstractTester.createLocalInstance(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);
        seed.setLastDirtyTimestamp(100L);
        // Regular registration first
        InstanceInfo myInstance1 = new InstanceInfo(seed);
        registerInstanceLocally(myInstance1);
        verifyLocalInstanceStatus(myInstance1.getId(), UP);
        // Override status
        boolean statusResult = registry.statusUpdate(AbstractTester.LOCAL_REGION_APP_NAME, seed.getId(), OUT_OF_SERVICE, "0", false);
        Assert.assertThat("Couldn't override instance status", statusResult, CoreMatchers.is(true));
        verifyLocalInstanceStatus(seed.getId(), OUT_OF_SERVICE);
        // Send a renew to ensure timestamps are consistent even with the override in existence.
        // To do this, we get hold of the registry local InstanceInfo and reset its status to before an override
        // has been applied
        // (this is to simulate a case in a replica server where the override has been replicated, but not yet
        // applied to the local InstanceInfo)
        InstanceInfo registeredInstance = registry.getInstanceByAppAndId(seed.getAppName(), seed.getId());
        registeredInstance.setStatusWithoutDirty(UP);
        verifyLocalInstanceStatus(seed.getId(), UP);
        registry.renew(seed.getAppName(), seed.getId(), false);
        verifyLocalInstanceStatus(seed.getId(), OUT_OF_SERVICE);
        // Now remove override
        statusResult = registry.deleteStatusOverride(AbstractTester.LOCAL_REGION_APP_NAME, seed.getId(), DOWN, "0", false);
        Assert.assertThat("Couldn't remove status override", statusResult, CoreMatchers.is(true));
        verifyLocalInstanceStatus(seed.getId(), DOWN);
        // Register again with status UP after the override deletion, keeping myInstance2's dirtyTimestamp (== no client side change)
        InstanceInfo myInstance3 = new InstanceInfo(seed);// clone to avoid object state in this test

        myInstance3.setLastDirtyTimestamp(200L);// use a later 'client side' dirty timestamp

        registry.register(myInstance3, 10000000, false);
        verifyLocalInstanceStatus(seed.getId(), UP);
    }

    @Test
    public void testStatusOverrideStartingStatus() throws Exception {
        // Regular registration first
        InstanceInfo myInstance = AbstractTester.createLocalInstance(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);
        registerInstanceLocally(myInstance);
        verifyLocalInstanceStatus(myInstance.getId(), UP);
        // Override status
        boolean statusResult = registry.statusUpdate(AbstractTester.LOCAL_REGION_APP_NAME, myInstance.getId(), OUT_OF_SERVICE, "0", false);
        Assert.assertThat("Couldn't override instance status", statusResult, CoreMatchers.is(true));
        verifyLocalInstanceStatus(myInstance.getId(), OUT_OF_SERVICE);
        // If we are not UP or OUT_OF_SERVICE, the OUT_OF_SERVICE override does not apply. It gets trumped by the current
        // status (STARTING or DOWN). Here we test with STARTING.
        myInstance = AbstractTester.createLocalStartingInstance(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);
        registerInstanceLocally(myInstance);
        verifyLocalInstanceStatus(myInstance.getId(), STARTING);
    }

    @Test
    public void testStatusOverrideWithExistingLeaseUp() throws Exception {
        // Without an override we expect to get the existing UP lease when we re-register with OUT_OF_SERVICE.
        // First, we are "up".
        InstanceInfo myInstance = AbstractTester.createLocalInstance(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);
        registerInstanceLocally(myInstance);
        verifyLocalInstanceStatus(myInstance.getId(), UP);
        // Then, we re-register with "out of service".
        InstanceInfo sameInstance = AbstractTester.createLocalOutOfServiceInstance(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);
        registry.register(sameInstance, 10000000, false);
        verifyLocalInstanceStatus(myInstance.getId(), UP);
        // Let's try again. We shouldn't see a difference.
        sameInstance = AbstractTester.createLocalOutOfServiceInstance(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);
        registry.register(sameInstance, 10000000, false);
        verifyLocalInstanceStatus(myInstance.getId(), UP);
    }

    @Test
    public void testStatusOverrideWithExistingLeaseOutOfService() throws Exception {
        // Without an override we expect to get the existing OUT_OF_SERVICE lease when we re-register with UP.
        // First, we are "out of service".
        InstanceInfo myInstance = AbstractTester.createLocalOutOfServiceInstance(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);
        registerInstanceLocally(myInstance);
        verifyLocalInstanceStatus(myInstance.getId(), OUT_OF_SERVICE);
        // Then, we re-register with "UP".
        InstanceInfo sameInstance = AbstractTester.createLocalInstance(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);
        registry.register(sameInstance, 10000000, false);
        verifyLocalInstanceStatus(myInstance.getId(), OUT_OF_SERVICE);
        // Let's try again. We shouldn't see a difference.
        sameInstance = AbstractTester.createLocalInstance(AbstractTester.LOCAL_REGION_INSTANCE_1_HOSTNAME);
        registry.register(sameInstance, 10000000, false);
        verifyLocalInstanceStatus(myInstance.getId(), OUT_OF_SERVICE);
    }

    @Test
    public void testEvictionTaskCompensationTime() throws Exception {
        long evictionTaskPeriodNanos = (serverConfig.getEvictionIntervalTimerInMs()) * 1000000;
        AbstractInstanceRegistry.EvictionTask testTask = Mockito.spy(registry.new EvictionTask());
        // 10ms longer than 1 period
        // exactly 1 period
        // less than the period
        Mockito.when(testTask.getCurrentTimeNano()).thenReturn(1L).thenReturn((1L + evictionTaskPeriodNanos)).thenReturn(((1L + (evictionTaskPeriodNanos * 2)) + 10000000L)).thenReturn(((1L + (evictionTaskPeriodNanos * 3)) - 1L));// less than 1 period

        Assert.assertThat(testTask.getCompensationTimeMs(), CoreMatchers.is(0L));
        Assert.assertThat(testTask.getCompensationTimeMs(), CoreMatchers.is(0L));
        Assert.assertThat(testTask.getCompensationTimeMs(), CoreMatchers.is(10L));
        Assert.assertThat(testTask.getCompensationTimeMs(), CoreMatchers.is(0L));
    }
}

