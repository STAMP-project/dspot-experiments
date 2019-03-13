package com.otaliastudios.cameraview;


import ImageFormat.JPEG;
import ImageFormat.NV21;
import android.graphics.ImageFormat;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.Assert.assertNotNull;


public class FrameTest {
    private FrameManager manager;

    @Test
    public void testDefaults() {
        Frame frame = new Frame(manager);
        Assert.assertEquals(frame.getTime(), (-1));
        Assert.assertEquals(frame.getFormat(), (-1));
        Assert.assertEquals(frame.getRotation(), 0);
        Assert.assertNull(frame.getData());
        Assert.assertNull(frame.getSize());
    }

    @Test
    public void testEquals() {
        Frame f1 = new Frame(manager);
        long time = 1000;
        f1.set(null, time, 90, null, NV21);
        Frame f2 = new Frame(manager);
        f2.set(new byte[2], time, 0, new Size(10, 10), NV21);
        Assert.assertEquals(f1, f2);
        f2.set(new byte[2], (time + 1), 0, new Size(10, 10), NV21);
        Assert.assertNotEquals(f1, f2);
    }

    @Test
    public void testRelease() {
        Frame frame = new Frame(manager);
        frame.set(new byte[2], 1000, 90, new Size(10, 10), NV21);
        frame.release();
        Assert.assertEquals(frame.getTime(), (-1));
        Assert.assertEquals(frame.getFormat(), (-1));
        Assert.assertEquals(frame.getRotation(), 0);
        Assert.assertNull(frame.getData());
        Assert.assertNull(frame.getSize());
        Mockito.verify(manager, Mockito.times(1)).onFrameReleased(frame);
    }

    @Test
    public void testReleaseManager() {
        Frame frame = new Frame(manager);
        assertNotNull(frame.mManager);
        frame.releaseManager();
        Assert.assertNull(frame.mManager);
    }

    @Test
    public void testFreeze() {
        Frame frame = new Frame(manager);
        byte[] data = new byte[]{ 0, 1, 5, 0, 7, 3, 4, 5 };
        long time = 1000;
        int rotation = 90;
        Size size = new Size(10, 10);
        int format = ImageFormat.NV21;
        frame.set(data, time, rotation, size, format);
        Frame frozen = frame.freeze();
        Assert.assertArrayEquals(data, frozen.getData());
        Assert.assertEquals(time, frozen.getTime());
        Assert.assertEquals(rotation, frozen.getRotation());
        Assert.assertEquals(size, frozen.getSize());
        // Mutate the first, ensure that frozen is not affected
        frame.set(new byte[]{ 3, 2, 1 }, 50, 180, new Size(1, 1), JPEG);
        Assert.assertArrayEquals(data, frozen.getData());
        Assert.assertEquals(time, frozen.getTime());
        Assert.assertEquals(rotation, frozen.getRotation());
        Assert.assertEquals(size, frozen.getSize());
        Assert.assertEquals(format, frozen.getFormat());
    }
}

