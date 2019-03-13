package com.otaliastudios.cameraview;


import org.junit.Assert;
import org.junit.Test;


public class CameraExceptionTest {
    @Test
    public void testConstructor() {
        RuntimeException cause = new RuntimeException("Error");
        CameraException camera = new CameraException(cause);
        Assert.assertEquals(cause, camera.getCause());
    }
}

