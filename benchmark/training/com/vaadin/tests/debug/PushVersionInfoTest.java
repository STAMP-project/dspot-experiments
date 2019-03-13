package com.vaadin.tests.debug;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 * Test for PUSH version string in debug window.
 *
 * @author Vaadin Ltd
 */
@TestCategory("push")
public class PushVersionInfoTest extends SingleBrowserTest {
    @Test
    public void testDisabledPush() throws InterruptedException {
        setDebug(true);
        openTestURL();
        selectInfoTab();
        Thread.sleep(500);
        Assert.assertNull("Found push info server string for disabled Push", getPushRowValue("Push server version"));
        Assert.assertNull("Found push info client string for disabled Push", getPushRowValue("Push client version"));
    }

    @Test
    public void testEnabledPush() throws InterruptedException {
        setDebug(true);
        openTestURL("enablePush=true");
        selectInfoTab();
        Thread.sleep(500);
        WebElement pushRow = getPushRowValue("Push server version");
        String atmVersion = findElement(By.className("atmosphere-version")).getText();
        Assert.assertTrue("Push row doesn't contain Atmosphere version", pushRow.getText().contains(atmVersion));
        String jsString = getPushRowValue("Push client version").getText();
        Assert.assertTrue("Push client version doesn't contain 'vaadin' string", jsString.contains("vaadin"));
        Assert.assertTrue("Push client version doesn't contain 'javascript' string", jsString.contains("javascript"));
    }
}

