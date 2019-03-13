package com.vaadin.tests.elements.window;


import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;


public class WindowButtonsTest extends MultiBrowserTest {
    private WindowElement windowElement;

    @Test
    public void window_clickCloseButton_windowClosed() {
        windowElement.close();
        Assert.assertFalse($(WindowElement.class).exists());
    }

    @Test
    public void window_maximizeAndRestore_windowOriginalSize() throws IOException, InterruptedException {
        Assert.assertFalse(windowElement.isMaximized());
        final Dimension originalSize = windowElement.getSize();
        windowElement.maximize();
        Assert.assertTrue(windowElement.isMaximized());
        Assert.assertNotEquals(originalSize, windowElement.getSize());
        windowElement.restore();
        Assert.assertFalse(windowElement.isMaximized());
        Assert.assertEquals(originalSize, windowElement.getSize());
    }
}

