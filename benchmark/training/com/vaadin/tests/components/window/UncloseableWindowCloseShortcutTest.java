package com.vaadin.tests.components.window;


import Keys.ESCAPE;
import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class UncloseableWindowCloseShortcutTest extends SingleBrowserTest {
    @Test
    public void testEscShortcut() {
        openTestURL();
        // Hit esc and verify that the Window was not closed.
        driver.findElement(By.cssSelector(".v-window-contents .v-scrollable")).sendKeys(ESCAPE);
        Assert.assertTrue("Uncloseable Window should remain open after esc is pressed.", isWindowOpen());
    }
}

