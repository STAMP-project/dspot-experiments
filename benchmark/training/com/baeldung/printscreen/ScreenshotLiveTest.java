package com.baeldung.printscreen;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class ScreenshotLiveTest {
    private Screenshot screenshot = new Screenshot("Screenshot.jpg");

    private File file = new File("Screenshot.jpg");

    @Test
    public void testGetScreenshot() throws Exception {
        screenshot.getScreenshot(2000);
        Assert.assertTrue(file.exists());
    }
}

