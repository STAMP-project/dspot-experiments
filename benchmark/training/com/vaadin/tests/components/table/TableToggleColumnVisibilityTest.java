package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


/**
 * Tests that column keeps its header, icon, alignment after toggling visibility
 * (#6245, #12303).
 *
 * @author Vaadin Ltd
 */
public class TableToggleColumnVisibilityTest extends MultiBrowserTest {
    @Test
    public void testColumnWidthRestoredAfterTogglingVisibility() {
        openTestURL();
        ButtonElement toggleVisibilityButton = $(ButtonElement.class).id("visib-toggler");
        ButtonElement changeOrderButton = $(ButtonElement.class).id("order-toggler");
        checkHeaderAttributes(1);
        toggleVisibilityButton.click();// hide column #1

        Assert.assertEquals("One column should be visible", findElements(By.className("v-table-header-cell")).size(), 1);
        toggleVisibilityButton.click();// restore column #1

        Assert.assertEquals("Two columns should be visible", findElements(By.className("v-table-header-cell")).size(), 2);
        checkHeaderAttributes(1);
        // change column order, column #1 now becomes column #0
        changeOrderButton.click();
        checkHeaderAttributes(0);
    }
}

