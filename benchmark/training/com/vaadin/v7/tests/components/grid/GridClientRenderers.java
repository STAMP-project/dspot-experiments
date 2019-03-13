package com.vaadin.v7.tests.components.grid;


import Keys.ENTER;
import Renderers.WIDGET_RENDERER;
import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NativeButtonElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elementsbase.ServerClass;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;


/**
 * Tests Grid client side renderers
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridClientRenderers extends MultiBrowserTest {
    private static final double SLEEP_MULTIPLIER = 1.2;

    private int latency = 0;

    @ServerClass("com.vaadin.tests.widgetset.server.grid.GridClientColumnRenderers.GridController")
    public static class MyClientGridElement extends GridElement {}

    @Test
    public void addWidgetRenderer() throws Exception {
        openTestURL();
        // Add widget renderer column
        $(NativeSelectElement.class).first().selectByText(WIDGET_RENDERER.toString());
        $(NativeButtonElement.class).caption("Add").first().click();
        // Click the button in cell 1,1
        TestBenchElement cell = getGrid().getCell(1, 2);
        WebElement gwtButton = cell.findElement(By.tagName("button"));
        gwtButton.click();
        // Should be an alert visible
        Assert.assertEquals("Button did not contain text \"Clicked\"", "Clicked", gwtButton.getText());
    }

    @Test
    public void detachAndAttachGrid() {
        openTestURL();
        // Add widget renderer column
        $(NativeSelectElement.class).first().selectByText(WIDGET_RENDERER.toString());
        $(NativeButtonElement.class).caption("Add").first().click();
        // Detach and re-attach the Grid
        $(NativeButtonElement.class).caption("DetachAttach").first().click();
        // Click the button in cell 1,1
        TestBenchElement cell = getGrid().getCell(1, 2);
        WebElement gwtButton = cell.findElement(By.tagName("button"));
        gwtButton.click();
        // Should be an alert visible
        Assert.assertEquals("Button did not contain text \"Clicked\"", gwtButton.getText(), "Clicked");
    }

    @Test
    public void rowsWithDataHasStyleName() throws Exception {
        testBench().disableWaitForVaadin();
        // Simulate network latency with 2000ms
        latency = 2000;
        openTestURL();
        sleep(((int) ((latency) * (GridClientRenderers.SLEEP_MULTIPLIER))));
        TestBenchElement row = getGrid().getRow(51);
        String className = row.getAttribute("class");
        Assert.assertFalse("Row should not yet contain style name v-grid-row-has-data", className.contains("v-grid-row-has-data"));
        // Wait for data to arrive
        sleep(((int) ((latency) * (GridClientRenderers.SLEEP_MULTIPLIER))));
        row = getGrid().getRow(51);
        className = row.getAttribute("class");
        Assert.assertTrue("Row should now contain style name v-grid-row-has-data", className.contains("v-grid-row-has-data"));
    }

    @Test
    public void complexRendererSetVisibleContent() throws Exception {
        DesiredCapabilities desiredCapabilities = getDesiredCapabilities();
        // Simulate network latency with 2000ms
        latency = 2000;
        // Chrome uses RGB instead of RGBA
        String colorRed = "rgba(255, 0, 0, 1)";
        String colorWhite = "rgba(255, 255, 255, 1)";
        String colorDark = "rgba(239, 240, 241, 1)";
        openTestURL();
        getGrid();
        testBench().disableWaitForVaadin();
        // Test initial renderering with contentVisible = False
        TestBenchElement cell = getGrid().getCell(51, 1);
        String backgroundColor = cell.getCssValue("backgroundColor");
        Assert.assertEquals("Background color was not red.", colorRed, backgroundColor);
        // data arrives...
        sleep(((int) ((latency) * (GridClientRenderers.SLEEP_MULTIPLIER))));
        // Content becomes visible
        cell = getGrid().getCell(51, 1);
        backgroundColor = cell.getCssValue("backgroundColor");
        Assert.assertNotEquals("Background color was red.", colorRed, backgroundColor);
        // scroll down, new cells becomes contentVisible = False
        getGrid().scrollToRow(60);
        // Cell should be red (setContentVisible set cell red)
        cell = getGrid().getCell(55, 1);
        backgroundColor = cell.getCssValue("backgroundColor");
        Assert.assertEquals("Background color was not red.", colorRed, backgroundColor);
        // data arrives...
        sleep(((int) ((latency) * (GridClientRenderers.SLEEP_MULTIPLIER))));
        // Cell should no longer be red
        backgroundColor = cell.getCssValue("backgroundColor");
        Assert.assertTrue("Background color was not reset", ((backgroundColor.equals(colorWhite)) || (backgroundColor.equals(colorDark))));
    }

    @Test
    public void testSortingEvent() throws Exception {
        openTestURL();
        $(NativeButtonElement.class).caption("Trigger sorting event").first().click();
        String consoleText = $(LabelElement.class).id("testDebugConsole").getText();
        Assert.assertTrue("Console text as expected", consoleText.contains("Columns: 1, order: Column 1: ASCENDING"));
    }

    @Test
    public void testListSorter() throws Exception {
        openTestURL();
        $(NativeButtonElement.class).caption("Shuffle").first().click();
        GridElement gridElem = $(GridClientRenderers.MyClientGridElement.class).first();
        // XXX: DANGER! We'll need to know how many rows the Grid has!
        // XXX: Currently, this is impossible; hence the hardcoded value of 70.
        boolean shuffled = false;
        for (int i = 1, l = 70; i < l; ++i) {
            String str_a = gridElem.getCell((i - 1), 0).getAttribute("innerHTML");
            String str_b = gridElem.getCell(i, 0).getAttribute("innerHTML");
            int value_a = Integer.parseInt(str_a);
            int value_b = Integer.parseInt(str_b);
            if (value_a > value_b) {
                shuffled = true;
                break;
            }
        }
        Assert.assertTrue("Grid shuffled", shuffled);
        $(NativeButtonElement.class).caption("Test sorting").first().click();
        for (int i = 1, l = 70; i < l; ++i) {
            String str_a = gridElem.getCell((i - 1), 0).getAttribute("innerHTML");
            String str_b = gridElem.getCell(i, 0).getAttribute("innerHTML");
            int value_a = Integer.parseInt(str_a);
            int value_b = Integer.parseInt(str_b);
            if (value_a > value_b) {
                Assert.assertTrue("Grid sorted", false);
            }
        }
    }

    @Test
    public void testComplexRendererOnActivate() {
        openTestURL();
        GridCellElement cell = getGrid().getCell(3, 1);
        cell.click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        Assert.assertEquals("onActivate was not called on KeyDown Enter.", "Activated!", cell.getText());
        cell = getGrid().getCell(4, 1);
        cell.click();
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(cell).doubleClick().perform();
        Assert.assertEquals("onActivate was not called on double click.", "Activated!", cell.getText());
    }
}

