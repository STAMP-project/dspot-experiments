package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.logging.Level;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


@TestCategory("grid")
public class InitialFrozenColumnsTest extends MultiBrowserTest {
    @Test
    public void testInitialFrozenColumns() {
        setDebug(true);
        openTestURL();
        Assert.assertFalse("Notification was present", isElementPresent(NotificationElement.class));
        WebElement cell = $(GridElement.class).first().getCell(0, 0);
        Assert.assertTrue(cell.getAttribute("class").contains("frozen"));
    }

    @Test
    public void testInitialAllColumnsFrozen() {
        setDebug(true);
        openTestURL("frozen=3");
        Assert.assertFalse("Notification was present", isElementPresent(NotificationElement.class));
        assertNoDebugMessage(Level.SEVERE);
        WebElement cell = $(GridElement.class).first().getCell(0, 2);
        Assert.assertTrue(cell.getAttribute("class").contains("frozen"));
    }
}

