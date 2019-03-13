package com.vaadin.v7.tests.components.grid.basicfeatures.client;


import Keys.ARROW_DOWN;
import Keys.ENTER;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class GridClientKeyEventsTest extends GridBasicClientFeaturesTest {
    private List<String> eventOrder = Arrays.asList("Down", "Up", "Press");

    @Test
    public void testBodyKeyEvents() throws IOException {
        openTestURL();
        getGridElement().getCell(2, 2).click();
        sendKeys("a").perform();
        for (int i = 0; i < 3; ++i) {
            Assert.assertEquals("Body key event handler was not called.", (((("(2, 2) event: GridKey" + (eventOrder.get(i))) + "Event:[") + (eventOrder.get(i).equals("Press") ? "a" : 65)) + "]"), findElements(By.className("v-label")).get((i * 3)).getText());
            Assert.assertTrue("Header key event handler got called unexpectedly.", findElements(By.className("v-label")).get(((i * 3) + 1)).getText().isEmpty());
            Assert.assertTrue("Footer key event handler got called unexpectedly.", findElements(By.className("v-label")).get(((i * 3) + 2)).getText().isEmpty());
        }
    }

    @Test
    public void testHeaderKeyEvents() throws IOException {
        openTestURL();
        getGridElement().getHeaderCell(0, 2).click();
        sendKeys("a").perform();
        for (int i = 0; i < 3; ++i) {
            Assert.assertEquals("Header key event handler was not called.", (((("(0, 2) event: GridKey" + (eventOrder.get(i))) + "Event:[") + (eventOrder.get(i).equals("Press") ? "a" : 65)) + "]"), findElements(By.className("v-label")).get(((i * 3) + 1)).getText());
            Assert.assertTrue("Body key event handler got called unexpectedly.", findElements(By.className("v-label")).get((i * 3)).getText().isEmpty());
            Assert.assertTrue("Footer key event handler got called unexpectedly.", findElements(By.className("v-label")).get(((i * 3) + 2)).getText().isEmpty());
        }
    }

    @Test
    public void selectAllUsingKeyboard() {
        openTestURL();
        selectMenuPath("Component", "Header", "Prepend row");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "State", "Selection mode", "multi");
        // IE8 does not handle 1k rows well with assertions enabled. Less rows!
        selectMenuPath("Component", "DataSource", "Reset with 100 rows of Data");
        // Focus cell above select all checkbox
        getGridElement().getHeaderCell(0, 0).click();
        Assert.assertFalse(isRowSelected(1));
        sendKeys(" ").perform();
        Assert.assertFalse(isRowSelected(1));
        // Move down to select all checkbox cell
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_DOWN).perform();
        Assert.assertFalse(isRowSelected(1));
        sendKeys(" ").perform();// select all

        Assert.assertTrue(isRowSelected(1));
        sendKeys(" ").perform();// deselect all

        Assert.assertFalse(isRowSelected(1));
        // Move down to header below select all checkbox cell
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_DOWN).perform();
        Assert.assertFalse(isRowSelected(1));
        sendKeys(" ").perform();// deselect all

        Assert.assertFalse(isRowSelected(1));
    }

    @Test
    public void testFooterKeyEvents() throws IOException {
        openTestURL();
        selectMenuPath("Component", "Footer", "Append row");
        getGridElement().getFooterCell(0, 2).click();
        sendKeys("a").perform();
        for (int i = 0; i < 3; ++i) {
            Assert.assertEquals("Footer key event handler was not called.", (((("(0, 2) event: GridKey" + (eventOrder.get(i))) + "Event:[") + (eventOrder.get(i).equals("Press") ? "a" : 65)) + "]"), findElements(By.className("v-label")).get(((i * 3) + 2)).getText());
            Assert.assertTrue("Body key event handler got called unexpectedly.", findElements(By.className("v-label")).get((i * 3)).getText().isEmpty());
            Assert.assertTrue("Header key event handler got called unexpectedly.", findElements(By.className("v-label")).get(((i * 3) + 1)).getText().isEmpty());
        }
    }

    @Test
    public void testNoKeyEventsFromWidget() {
        openTestURL();
        selectMenuPath("Component", "Columns", "Column 2", "Header Type", "Widget Header");
        GridCellElement header = getGridElement().getHeaderCell(0, 2);
        header.findElement(By.tagName("button")).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        for (int i = 0; i < 3; ++i) {
            Assert.assertTrue("Header key event handler got called unexpectedly.", findElements(By.className("v-label")).get(((i * 3) + 1)).getText().isEmpty());
        }
    }
}

