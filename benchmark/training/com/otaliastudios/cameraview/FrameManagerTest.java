package com.otaliastudios.cameraview;


import FrameManager.BufferCallback;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FrameManagerTest {
    private BufferCallback callback;

    @Test
    public void testAllocate() {
        FrameManager manager = new FrameManager(1, callback);
        manager.allocate(4, new Size(50, 50));
        Mockito.verify(callback, Mockito.times(1)).onBufferAvailable(ArgumentMatchers.any(byte[].class));
        Mockito.reset(callback);
        manager = new FrameManager(5, callback);
        manager.allocate(4, new Size(50, 50));
        Mockito.verify(callback, Mockito.times(5)).onBufferAvailable(ArgumentMatchers.any(byte[].class));
    }

    @Test
    public void testFrameRecycling() {
        // A 1-pool manager will always recycle the same frame.
        FrameManager manager = new FrameManager(1, callback);
        manager.allocate(4, new Size(50, 50));
        Frame first = manager.getFrame(null, 0, 0, null, 0);
        first.release();
        Frame second = manager.getFrame(null, 0, 0, null, 0);
        second.release();
        Assert.assertEquals(first, second);
    }

    @Test
    public void testOnFrameReleased_nullBuffer() {
        FrameManager manager = new FrameManager(1, callback);
        manager.allocate(4, new Size(50, 50));
        Mockito.reset(callback);
        Frame frame = manager.getFrame(null, 0, 0, null, 0);
        manager.onFrameReleased(frame);
        Mockito.verify(callback, Mockito.never()).onBufferAvailable(frame.getData());
    }

    @Test
    public void testOnFrameReleased_sameLength() {
        FrameManager manager = new FrameManager(1, callback);
        int length = manager.allocate(4, new Size(50, 50));
        // A camera preview frame comes. Request a frame.
        byte[] picture = new byte[length];
        Frame frame = manager.getFrame(picture, 0, 0, null, 0);
        // Release the frame and ensure that onBufferAvailable is called.
        Mockito.reset(callback);
        manager.onFrameReleased(frame);
        Mockito.verify(callback, Mockito.times(1)).onBufferAvailable(picture);
    }

    @Test
    public void testOnFrameReleased_differentLength() {
        FrameManager manager = new FrameManager(1, callback);
        int length = manager.allocate(4, new Size(50, 50));
        // A camera preview frame comes. Request a frame.
        byte[] picture = new byte[length];
        Frame frame = manager.getFrame(picture, 0, 0, null, 0);
        // Don't release the frame. Change the allocation size.
        manager.allocate(2, new Size(15, 15));
        // Now release the old frame and ensure that onBufferAvailable is NOT called,
        // because the released data has wrong length.
        manager.onFrameReleased(frame);
        Mockito.reset(callback);
        Mockito.verify(callback, Mockito.never()).onBufferAvailable(picture);
    }

    @Test
    public void testRelease() {
        FrameManager manager = new FrameManager(1, callback);
        int length = manager.allocate(4, new Size(50, 50));
        Frame first = manager.getFrame(new byte[length], 0, 0, null, 0);
        first.release();// Store this frame in the queue.

        // Release the whole manager and ensure it clears the frame.
        manager.release();
        Assert.assertNull(first.getData());
        Assert.assertNull(first.mManager);
    }
}

