package com.vaadin.v7.tests.components.grid;


import WidgetRenderers.PROPERTY_ID;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * TB tests for the various builtin widget-based renderers.
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class WidgetRenderersTest extends MultiBrowserTest {
    @Test
    public void testProgressBarRenderer() {
        Assert.assertTrue(getGridCell(0, 0).isElementPresent(By.className("v-progressbar")));
    }

    @Test
    public void testButtonRenderer() {
        WebElement button = getGridCell(0, 1).findElement(By.className("v-nativebutton"));
        button.click();
        waitUntilTextUpdated(button, "Clicked!");
    }

    @Test
    public void testButtonRendererAfterCellBeingFocused() {
        GridCellElement buttonCell = getGridCell(0, 1);
        Assert.assertFalse("cell should not be focused before focusing", buttonCell.isFocused());
        // avoid clicking on the button
        buttonCell.click(((buttonCell.getSize().getWidth()) - 10), 5);
        Assert.assertTrue("cell should be focused after focusing", buttonCell.isFocused());
        WebElement button = buttonCell.findElement(By.className("v-nativebutton"));
        Assert.assertNotEquals("Button should not be clicked before click", "Clicked!", button.getText());
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(button).click().perform();
        waitUntilTextUpdated(button, "Clicked!");
    }

    @Test
    public void testImageRenderer() {
        final WebElement image = getGridCell(0, 2).findElement(By.className("gwt-Image"));
        waitUntilmageSrcEndsWith(image, "window/img/close.png");
        image.click();
        waitUntilmageSrcEndsWith(image, "window/img/maximize.png");
    }

    @Test
    public void testColumnReorder() {
        $(ButtonElement.class).caption("Change column order").first().click();
        Assert.assertFalse("Notification was present", isElementPresent(NotificationElement.class));
        Assert.assertTrue(getGridCell(0, 0).isElementPresent(By.className("gwt-Image")));
        Assert.assertTrue(getGridCell(0, 1).isElementPresent(By.className("v-progressbar")));
        Assert.assertTrue(getGridCell(0, 2).isElementPresent(By.className("v-nativebutton")));
    }

    @Test
    public void testPropertyIdInEvent() {
        WebElement button = getGridCell(0, 3).findElement(By.className("v-nativebutton"));
        button.click();
        waitUntilTextUpdated(button, PROPERTY_ID);
    }
}

