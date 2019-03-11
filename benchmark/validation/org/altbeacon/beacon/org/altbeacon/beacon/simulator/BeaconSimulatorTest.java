package org.altbeacon.beacon.org.altbeacon.beacon.simulator;


import java.util.ArrayList;
import org.altbeacon.beacon.AltBeaconParser;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.simulator.StaticBeaconSimulator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class BeaconSimulatorTest {
    @Test
    public void testSetBeacons() {
        StaticBeaconSimulator staticBeaconSimulator = new StaticBeaconSimulator();
        byte[] beaconBytes = BeaconSimulatorTest.hexStringToByteArray("02011a1bff1801beac2f234454cf6d4a0fadf2f4911ba9ffa600010002c509");
        Beacon beacon = new AltBeaconParser().fromScanData(beaconBytes, (-55), null);
        ArrayList<Beacon> beacons = new ArrayList<Beacon>();
        beacons.add(beacon);
        staticBeaconSimulator.setBeacons(beacons);
        Assert.assertEquals("getBeacons should match values entered with setBeacons", staticBeaconSimulator.getBeacons(), beacons);
    }

    @Test
    public void testSetBeaconsEmpty() {
        StaticBeaconSimulator staticBeaconSimulator = new StaticBeaconSimulator();
        ArrayList<Beacon> beacons = new ArrayList<Beacon>();
        staticBeaconSimulator.setBeacons(beacons);
        Assert.assertEquals("getBeacons should match values entered with setBeacons even when empty", staticBeaconSimulator.getBeacons(), beacons);
    }

    @Test
    public void testSetBeaconsNull() {
        StaticBeaconSimulator staticBeaconSimulator = new StaticBeaconSimulator();
        staticBeaconSimulator.setBeacons(null);
        Assert.assertEquals("getBeacons should return null", staticBeaconSimulator.getBeacons(), null);
    }
}

