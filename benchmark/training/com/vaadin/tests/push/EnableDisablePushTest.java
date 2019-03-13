package com.vaadin.tests.push;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("push")
public class EnableDisablePushTest extends MultiBrowserTest {
    @Test
    public void testEnablePushWhenUsingPolling() throws Exception {
        openTestURL();
        Assert.assertEquals("1. Push enabled", getLogRow(0));
        getDisablePushButton().click();
        Assert.assertEquals("3. Push disabled", getLogRow(0));
        getEnablePollButton().click();
        Assert.assertEquals("5. Poll enabled", getLogRow(0));
        getEnablePushButton().click();
        Assert.assertEquals("7. Push enabled", getLogRow(0));
        getDisablePollButton().click();
        Assert.assertEquals("9. Poll disabled", getLogRow(0));
        getDisablePushButtonAndReenableFromBackground().click();
        Thread.sleep(2500);
        Assert.assertEquals("16. Polling disabled, push enabled", getLogRow(0));
        getDisablePushButton().click();
        Assert.assertEquals("18. Push disabled", getLogRow(0));
    }
}

