package com.vaadin.tests.push;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("push")
public class TogglePushTest extends MultiBrowserTest {
    @Test
    public void togglePushInInit() throws Exception {
        setPush(true);
        String url = getTestUrl();
        // Open with push disabled
        driver.get(addParameter(url, "push=disabled"));
        Assert.assertFalse(getPushToggle().isSelected());
        getDelayedCounterUpdateButton().click();
        sleep(2000);
        Assert.assertEquals("Counter has been updated 0 times", getCounterText());
        // Open with push enabled
        driver.get(addParameter(url, "push=enabled"));
        Assert.assertTrue(getPushToggle().isSelected());
        getDelayedCounterUpdateButton().click();
        sleep(2000);
        Assert.assertEquals("Counter has been updated 1 times", getCounterText());
    }

    @Test
    public void togglePush() throws InterruptedException {
        setPush(true);
        openTestURL();
        getDelayedCounterUpdateButton().click();
        sleep(2000);
        // Push is enabled, so text gets updated
        Assert.assertEquals("Counter has been updated 1 times", getCounterText());
        // Disable push
        getPushToggle().click();
        getDelayedCounterUpdateButton().click();
        sleep(2000);
        // Push is disabled, so text is not updated
        Assert.assertEquals("Counter has been updated 1 times", getCounterText());
        getDirectCounterUpdateButton().click();
        // Direct update is visible, and includes previous update
        Assert.assertEquals("Counter has been updated 3 times", getCounterText());
        // Re-enable push
        getPushToggle().click();
        getDelayedCounterUpdateButton().click();
        sleep(2000);
        // Push is enabled again, so text gets updated
        Assert.assertEquals("Counter has been updated 4 times", getCounterText());
    }
}

