package com.vaadin.tests.push;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("push")
public class RefreshCloseConnectionTest extends MultiBrowserTest {
    @Test
    public void testSessionRefresh() {
        openTestURL("restartApplication");
        Assert.assertEquals("1. Init", getLogRow(0));
        openTestURL();
        Assert.assertEquals("2. Refresh", getLogRow(1));
        Assert.assertEquals("3. Push", getLogRow(0));
    }
}

