package com.github.sarxos.webcam.ds.vlcj;


import VlcjDriver.DEFAULT_SCAN_INTERVAL;
import WebcamResolution.VGA;
import com.github.sarxos.webcam.WebcamDevice;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.discoverer.MediaDiscoverer;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ MediaList.class, MediaDiscoverer.class// 
 })
public class VlcjDeviceTest {
    private static class DummyDevice implements WebcamDevice {
        private static final AtomicInteger N = new AtomicInteger();

        private final int number;

        public DummyDevice() {
            this.number = VlcjDeviceTest.DummyDevice.N.incrementAndGet();
        }

        @Override
        public String getName() {
            return "Dummy device " + (number);
        }

        @Override
        public Dimension[] getResolutions() {
            return new Dimension[]{ VGA.getSize() };
        }

        @Override
        public Dimension getResolution() {
            return VGA.getSize();
        }

        @Override
        public void setResolution(Dimension size) {
            throw new IllegalStateException("Not supported");
        }

        @Override
        public BufferedImage getImage() {
            return EasyMock.createMock(BufferedImage.class);
        }

        @Override
        public void open() {
            // ignore
        }

        @Override
        public void close() {
            // ignore
        }

        @Override
        public void dispose() {
            // ignore
        }

        @Override
        public boolean isOpen() {
            return true;
        }
    }

    @Test
    public void test_getDevices() {
        VlcjDriver driver = getDriverMock();
        List<WebcamDevice> devices = driver.getDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(4, devices.size());
    }

    @Test
    public void test_isThreadSafe() {
        VlcjDriver driver = getDriverMock();
        Assert.assertFalse(driver.isThreadSafe());
    }

    @Test
    public void test_isScanPossible() {
        VlcjDriver driver = getDriverMock();
        Assert.assertTrue(driver.isScanPossible());
    }

    @Test
    public void test_getSetScanInterval() {
        VlcjDriver driver = getDriverMock();
        Assert.assertEquals(DEFAULT_SCAN_INTERVAL, driver.getScanInterval());
        driver.setScanInterval(12345);
        Assert.assertEquals(12345, driver.getScanInterval());
    }

    @Test
    public void test_toString() {
        VlcjDriver driver = getDriverMock();
        Assert.assertNotNull(driver.toString());
    }
}

