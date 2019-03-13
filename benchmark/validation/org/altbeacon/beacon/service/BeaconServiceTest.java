package org.altbeacon.beacon.service;


import android.os.AsyncTask;
import java.util.concurrent.ThreadPoolExecutor;
import org.altbeacon.beacon.service.scanner.CycledLeScanCallback;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.annotation.Config;


/**
 * Created by dyoung on 7/1/15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class BeaconServiceTest {
    /**
     * This test verifies that processing a beacon in a scan (which starts its own thread) does not
     * affect the size of the available threads in the main Android AsyncTask.THREAD_POOL_EXECUTOR
     *
     * @throws Exception
     * 		
     */
    @Test
    public void beaconScanCallbackTest() throws Exception {
        final ServiceController<BeaconService> beaconServiceServiceController = Robolectric.buildService(BeaconService.class);
        // beaconServiceServiceController.attach();
        BeaconService beaconService = beaconServiceServiceController.get();
        beaconService.onCreate();
        CycledLeScanCallback callback = beaconService.getCycledLeScanCallback();
        ThreadPoolExecutor executor = ((ThreadPoolExecutor) (AsyncTask.THREAD_POOL_EXECUTOR));
        int activeThreadCountBeforeScan = executor.getActiveCount();
        byte[] scanRecord = new byte[1];
        callback.onLeScan(null, (-59), scanRecord);
        int activeThreadCountAfterScan = executor.getActiveCount();
        Assert.assertEquals("The size of the Android thread pool should be unchanged by beacon scanning", activeThreadCountBeforeScan, activeThreadCountAfterScan);
        // Need to sleep here until the thread in the above method completes, otherwise an exception
        // is thrown.  Maybe we don't care about this exception, so we could remove this.
        Thread.sleep(100);
    }
}

