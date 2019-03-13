package org.altbeacon.beacon.service.scanner;


import BeaconParser.EDDYSTONE_UID_LAYOUT;
import ScanFilterUtils.ScanFilterData;
import java.util.List;
import org.altbeacon.beacon.AltBeaconParser;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;


@Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class ScanFilterUtilsTest {
    @Test
    public void testGetAltBeaconScanFilter() throws Exception {
        ShadowLog.stream = System.err;
        BeaconParser parser = new AltBeaconParser();
        BeaconManager.setsManifestCheckingDisabled(true);// no manifest available in robolectric

        List<ScanFilterUtils.ScanFilterData> scanFilterDatas = new ScanFilterUtils().createScanFilterDataForBeaconParser(parser);
        Assert.assertEquals("scanFilters should be of correct size", 1, scanFilterDatas.size());
        ScanFilterUtils.ScanFilterData sfd = scanFilterDatas.get(0);
        Assert.assertEquals("manufacturer should be right", 280, sfd.manufacturer);
        Assert.assertEquals("mask length should be right", 2, sfd.mask.length);
        Assert.assertArrayEquals("mask should be right", new byte[]{ ((byte) (255)), ((byte) (255)) }, sfd.mask);
        Assert.assertArrayEquals("filter should be right", new byte[]{ ((byte) (190)), ((byte) (172)) }, sfd.filter);
    }

    @Test
    public void testGenericScanFilter() throws Exception {
        ShadowLog.stream = System.err;
        BeaconParser parser = new BeaconParser();
        parser.setBeaconLayout("m:2-3=1111,i:4-6,p:24-24");
        BeaconManager.setsManifestCheckingDisabled(true);// no manifest available in robolectric

        List<ScanFilterUtils.ScanFilterData> scanFilterDatas = new ScanFilterUtils().createScanFilterDataForBeaconParser(parser);
        Assert.assertEquals("scanFilters should be of correct size", 1, scanFilterDatas.size());
        ScanFilterUtils.ScanFilterData sfd = scanFilterDatas.get(0);
        Assert.assertEquals("manufacturer should be right", 76, sfd.manufacturer);
        Assert.assertEquals("mask length should be right", 2, sfd.mask.length);
        Assert.assertArrayEquals("mask should be right", new byte[]{ ((byte) (255)), ((byte) (255)) }, sfd.mask);
        Assert.assertArrayEquals("filter should be right", new byte[]{ ((byte) (17)), ((byte) (17)) }, sfd.filter);
        Assert.assertNull("serviceUuid should be null", sfd.serviceUuid);
    }

    @Test
    public void testEddystoneScanFilterData() throws Exception {
        ShadowLog.stream = System.err;
        BeaconParser parser = new BeaconParser();
        parser.setBeaconLayout(EDDYSTONE_UID_LAYOUT);
        BeaconManager.setsManifestCheckingDisabled(true);// no manifest available in robolectric

        List<ScanFilterUtils.ScanFilterData> scanFilterDatas = new ScanFilterUtils().createScanFilterDataForBeaconParser(parser);
        Assert.assertEquals("scanFilters should be of correct size", 1, scanFilterDatas.size());
        ScanFilterUtils.ScanFilterData sfd = scanFilterDatas.get(0);
        Assert.assertEquals("serviceUuid should be right", new Long(65194), sfd.serviceUuid);
    }

    @Test
    public void testZeroOffsetScanFilter() throws Exception {
        ShadowLog.stream = System.err;
        BeaconParser parser = new BeaconParser();
        parser.setBeaconLayout("m:0-3=11223344,i:4-6,p:24-24");
        BeaconManager.setsManifestCheckingDisabled(true);// no manifest available in robolectric

        List<ScanFilterUtils.ScanFilterData> scanFilterDatas = new ScanFilterUtils().createScanFilterDataForBeaconParser(parser);
        Assert.assertEquals("scanFilters should be of correct size", 1, scanFilterDatas.size());
        ScanFilterUtils.ScanFilterData sfd = scanFilterDatas.get(0);
        Assert.assertEquals("manufacturer should be right", 76, sfd.manufacturer);
        Assert.assertEquals("mask length should be right", 2, sfd.mask.length);
        Assert.assertArrayEquals("mask should be right", new byte[]{ ((byte) (255)), ((byte) (255)) }, sfd.mask);
        Assert.assertArrayEquals("filter should be right", new byte[]{ ((byte) (51)), ((byte) (68)) }, sfd.filter);
    }
}

