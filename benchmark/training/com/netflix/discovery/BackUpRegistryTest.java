package com.netflix.discovery;


import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Nitesh Kant
 */
public class BackUpRegistryTest {
    public static final String ALL_REGIONS_VIP_ADDR = "myvip";

    public static final String REMOTE_REGION_INSTANCE_1_HOSTNAME = "blah";

    public static final String REMOTE_REGION_INSTANCE_2_HOSTNAME = "blah2";

    public static final String LOCAL_REGION_APP_NAME = "MYAPP_LOC";

    public static final String LOCAL_REGION_INSTANCE_1_HOSTNAME = "blahloc";

    public static final String LOCAL_REGION_INSTANCE_2_HOSTNAME = "blahloc2";

    public static final String REMOTE_REGION_APP_NAME = "MYAPP";

    public static final String REMOTE_REGION = "myregion";

    public static final String REMOTE_ZONE = "myzone";

    public static final int CLIENT_REFRESH_RATE = 10;

    public static final int NOT_AVAILABLE_EUREKA_PORT = 756473;

    private EurekaClient client;

    private MockBackupRegistry backupRegistry;

    @Test
    public void testLocalOnly() throws Exception {
        setUp(false);
        Applications applications = client.getApplications();
        List<Application> registeredApplications = applications.getRegisteredApplications();
        System.out.println(("***" + registeredApplications));
        Assert.assertNotNull("Local region apps not found.", registeredApplications);
        Assert.assertEquals("Local apps size not as expected.", 1, registeredApplications.size());
        Assert.assertEquals("Local region apps not present.", BackUpRegistryTest.LOCAL_REGION_APP_NAME, registeredApplications.get(0).getName());
    }

    @Test
    public void testRemoteEnabledButLocalOnlyQueried() throws Exception {
        setUp(true);
        Applications applications = client.getApplications();
        List<Application> registeredApplications = applications.getRegisteredApplications();
        Assert.assertNotNull("Local region apps not found.", registeredApplications);
        Assert.assertEquals("Local apps size not as expected.", 2, registeredApplications.size());// Remote region comes with no instances.

        Application localRegionApp = null;
        Application remoteRegionApp = null;
        for (Application registeredApplication : registeredApplications) {
            if (registeredApplication.getName().equals(BackUpRegistryTest.LOCAL_REGION_APP_NAME)) {
                localRegionApp = registeredApplication;
            } else
                if (registeredApplication.getName().equals(BackUpRegistryTest.REMOTE_REGION_APP_NAME)) {
                    remoteRegionApp = registeredApplication;
                }

        }
        Assert.assertNotNull("Local region apps not present.", localRegionApp);
        Assert.assertTrue("Remote region instances returned for local query.", ((null == remoteRegionApp) || (remoteRegionApp.getInstances().isEmpty())));
    }

    @Test
    public void testRemoteEnabledAndQueried() throws Exception {
        setUp(true);
        Applications applications = client.getApplicationsForARegion(BackUpRegistryTest.REMOTE_REGION);
        List<Application> registeredApplications = applications.getRegisteredApplications();
        Assert.assertNotNull("Remote region apps not found.", registeredApplications);
        Assert.assertEquals("Remote apps size not as expected.", 1, registeredApplications.size());
        Assert.assertEquals("Remote region apps not present.", BackUpRegistryTest.REMOTE_REGION_APP_NAME, registeredApplications.get(0).getName());
    }

    @Test
    public void testAppsHashCode() throws Exception {
        setUp(true);
        Applications applications = client.getApplications();
        Assert.assertEquals("UP_1_", applications.getAppsHashCode());
    }
}

