package com.github.sarxos.webcam.util;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dan Rollo
Date: 3/8/14
Time: 10:44 PM
 */
public class NixVideoDevUtilsTest {
    /**
     * Accept method was failing with exception: String index out of range: 5
     * This occurs on opensuse 11 where video device files do not all have a suffix. The files are created like so:
     * $ ls -l /dev/video*
     * /dev/video -&gt; video0
     * /dev/video0
     *
     * In this case, the link name 'video' is less that 6 characters long, so the filter statement:
     * Character.isDigit(name.charAt(5))
     * causes the exception.
     *
     * Fix is to also check for length before checking for isDigit().
     */
    @Test
    public void testAcceptHandlesShortVideoDeviceFilename() {
        final NixVideoDevUtils videoDeviceFilenameFilter = new NixVideoDevUtils();
        Assert.assertFalse(videoDeviceFilenameFilter.accept(new File("/dev"), "video"));
        Assert.assertTrue(videoDeviceFilenameFilter.accept(new File("/dev"), "video0"));
    }
}

