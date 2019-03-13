package org.altbeacon.beacon.service;


import java.util.List;
import org.altbeacon.beacon.Beacon;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ExtraDataBeaconTrackerTest {
    @Test
    public void trackingManufacturerBeaconReturnsSelf() {
        Beacon beacon = getManufacturerBeacon();
        ExtraDataBeaconTracker tracker = new ExtraDataBeaconTracker();
        Beacon trackedBeacon = tracker.track(beacon);
        Assert.assertEquals("Returns itself", trackedBeacon, beacon);
    }

    @Test
    public void gattBeaconExtraDataIsNotReturned() {
        Beacon extraDataBeacon = getGattBeaconExtraData();
        ExtraDataBeaconTracker tracker = new ExtraDataBeaconTracker();
        Beacon trackedBeacon = tracker.track(extraDataBeacon);
        Assert.assertNull("trackedBeacon should be null", trackedBeacon);
    }

    @Test
    public void gattBeaconExtraDataGetUpdated() {
        Beacon beacon = getGattBeacon();
        Beacon extraDataBeacon = getGattBeaconExtraData();
        Beacon extraDataBeacon2 = getGattBeaconExtraData2();
        ExtraDataBeaconTracker tracker = new ExtraDataBeaconTracker();
        tracker.track(beacon);
        tracker.track(extraDataBeacon);
        Beacon trackedBeacon = tracker.track(beacon);
        Assert.assertEquals("rssi should be updated", extraDataBeacon.getRssi(), trackedBeacon.getRssi());
        Assert.assertEquals("extra data is updated", extraDataBeacon.getDataFields(), trackedBeacon.getExtraDataFields());
        tracker.track(extraDataBeacon2);
        trackedBeacon = tracker.track(beacon);
        Assert.assertEquals("rssi should be updated", extraDataBeacon2.getRssi(), trackedBeacon.getRssi());
        Assert.assertEquals("extra data is updated", extraDataBeacon2.getDataFields(), trackedBeacon.getExtraDataFields());
    }

    @Test
    public void gattBeaconFieldsAreNotUpdated() {
        Beacon beacon = getGattBeacon();
        final int originalRssi = beacon.getRssi();
        final List<Long> originalData = beacon.getDataFields();
        final List<Long> originalExtra = beacon.getExtraDataFields();
        Beacon beaconUpdate = getGattBeaconUpdate();
        ExtraDataBeaconTracker tracker = new ExtraDataBeaconTracker();
        tracker.track(beacon);
        tracker.track(beaconUpdate);
        Beacon trackedBeacon = tracker.track(beacon);
        Assert.assertEquals("rssi should NOT be updated", originalRssi, trackedBeacon.getRssi());
        Assert.assertEquals("data should NOT be updated", originalData, trackedBeacon.getDataFields());
        Assert.assertEquals("extra data should NOT be updated", originalExtra, trackedBeacon.getExtraDataFields());
    }

    @Test
    public void gattBeaconFieldsGetUpdated() {
        Beacon beacon = getGattBeacon();
        Beacon extraDataBeacon = getGattBeaconExtraData();
        Beacon repeatBeacon = getGattBeacon();
        repeatBeacon.setRssi((-100));
        ExtraDataBeaconTracker tracker = new ExtraDataBeaconTracker();
        tracker.track(beacon);
        tracker.track(extraDataBeacon);
        Beacon trackedBeacon = tracker.track(repeatBeacon);
        Assert.assertEquals("rssi should NOT be updated", (-100), trackedBeacon.getRssi());
        Assert.assertEquals("extra data fields should be updated", extraDataBeacon.getDataFields(), trackedBeacon.getExtraDataFields());
    }

    @Test
    public void multiFrameBeaconDifferentServiceUUIDFieldsNotUpdated() {
        Beacon beacon = getMultiFrameBeacon();
        Beacon beaconUpdate = getMultiFrameBeaconUpdateDifferentServiceUUID();
        ExtraDataBeaconTracker tracker = new ExtraDataBeaconTracker();
        tracker.track(beacon);
        tracker.track(beaconUpdate);
        Beacon trackedBeacon = tracker.track(beacon);
        Assert.assertNotEquals("rssi should NOT be updated", beaconUpdate.getRssi(), trackedBeacon.getRssi());
        Assert.assertNotEquals("data fields should NOT be updated", beaconUpdate.getDataFields(), trackedBeacon.getExtraDataFields());
    }

    @Test
    public void multiFrameBeaconProgramaticParserAssociationDifferentServiceUUIDFieldsGetUpdated() {
        Beacon beacon = getMultiFrameBeacon();
        Beacon beaconUpdate = getMultiFrameBeaconUpdateDifferentServiceUUID();
        ExtraDataBeaconTracker tracker = new ExtraDataBeaconTracker(false);
        tracker.track(beacon);
        tracker.track(beaconUpdate);
        Beacon trackedBeacon = tracker.track(beacon);
        Assert.assertEquals("rssi should be updated", beaconUpdate.getRssi(), trackedBeacon.getRssi());
        Assert.assertEquals("data fields should be updated", beaconUpdate.getDataFields(), trackedBeacon.getExtraDataFields());
    }
}

