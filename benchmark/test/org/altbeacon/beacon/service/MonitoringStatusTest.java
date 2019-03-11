package org.altbeacon.beacon.service;


import android.content.Context;
import java.util.Collection;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


/**
 * Created by dyoung on 7/1/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class MonitoringStatusTest {
    private static final String TAG = MonitoringStatusTest.class.getSimpleName();

    @Test
    public void savesStatusOfUpTo50RegionsTest() throws Exception {
        Context context = RuntimeEnvironment.application;
        MonitoringStatus monitoringStatus = new MonitoringStatus(context);
        for (int i = 0; i < 50; i++) {
            Region region = new Region(("" + i), null, null, null);
            monitoringStatus.addRegion(region, null);
        }
        monitoringStatus.saveMonitoringStatusIfOn();
        MonitoringStatus monitoringStatus2 = new MonitoringStatus(context);
        Assert.assertEquals("restored regions should be same number as saved", 50, monitoringStatus2.regions().size());
    }

    @Test
    public void clearsStatusOfOver50RegionsTest() throws Exception {
        Context context = RuntimeEnvironment.application;
        MonitoringStatus monitoringStatus = new MonitoringStatus(context);
        for (int i = 0; i < 51; i++) {
            Region region = new Region(("" + i), null, null, null);
            monitoringStatus.addRegion(region, null);
        }
        monitoringStatus.saveMonitoringStatusIfOn();
        MonitoringStatus monitoringStatus2 = new MonitoringStatus(context);
        Assert.assertEquals("restored regions should be none", 0, monitoringStatus2.regions().size());
    }

    @Test
    public void refusesToRestoreRegionsIfTooMuchTimeHasPassedSinceSavingTest() throws Exception {
        Context context = RuntimeEnvironment.application;
        MonitoringStatus monitoringStatus = new MonitoringStatus(context);
        for (int i = 0; i < 50; i++) {
            Region region = new Region(("" + i), null, null, null);
            monitoringStatus.addRegion(region, null);
        }
        monitoringStatus.saveMonitoringStatusIfOn();
        // Set update time to one hour ago
        monitoringStatus.updateMonitoringStatusTime(((System.currentTimeMillis()) - (1000 * 3600L)));
        MonitoringStatus monitoringStatus2 = new MonitoringStatus(context);
        Assert.assertEquals("restored regions should be none", 0, monitoringStatus2.regions().size());
    }

    @Test
    public void allowsAccessToRegionsAfterRestore() throws Exception {
        Context context = RuntimeEnvironment.application;
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(context);
        MonitoringStatus monitoringStatus = MonitoringStatus.getInstanceForApplication(context);
        for (int i = 0; i < 50; i++) {
            Region region = new Region(("" + i), null, null, null);
            monitoringStatus.addRegion(region, null);
        }
        monitoringStatus.saveMonitoringStatusIfOn();
        monitoringStatus.restoreMonitoringStatus();
        Collection<Region> regions = beaconManager.getMonitoredRegions();
        Assert.assertEquals("beaconManager should return restored regions", 50, regions.size());
    }
}

