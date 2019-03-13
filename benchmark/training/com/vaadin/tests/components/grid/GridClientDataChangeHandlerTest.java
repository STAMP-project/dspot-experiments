package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class GridClientDataChangeHandlerTest extends SingleBrowserTest {
    @Test
    public void testNoErrorsOnGridInit() throws InterruptedException {
        setDebug(true);
        openTestURL();
        // Wait for delayed functionality.
        sleep(1000);
        Assert.assertFalse("Unexpected exception is visible.", $(NotificationElement.class).exists());
    }
}

