/**
 * Created by dyoung on 7/30/17.
 */
package org.altbeacon.beacon.service;


import android.content.Context;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


/**
 * Created by dyoung on 7/1/15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ScanStateTest {
    @Test
    public void serializationTest() throws Exception {
        Context context = RuntimeEnvironment.application;
        ScanState scanState = new ScanState(context);
        MonitoringStatus monitoringStatus = new MonitoringStatus(context);
        scanState.setMonitoringStatus(monitoringStatus);
        scanState.setLastScanStartTimeMillis(1234);
        scanState.save();
        ScanState scanState2 = ScanState.restore(context);
        Assert.assertEquals("Scan start time should be restored", scanState.getLastScanStartTimeMillis(), scanState2.getLastScanStartTimeMillis());
    }
}

