package com.vaadin.v7.tests.components.grid.basicfeatures.client;


import Keys.ENTER;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


public class DisabledGridClientTest extends GridBasicClientFeaturesTest {
    @Test
    public void testSelection() {
        selectMenuPath("Component", "State", "Selection mode", "single");
        GridRowElement row = getGridElement().getRow(0);
        GridCellElement cell = getGridElement().getCell(0, 0);
        cell.click();
        Assert.assertFalse("disabled row should not be selected", row.isSelected());
    }

    @Test
    public void testEditorOpening() {
        selectMenuPath("Component", "Editor", "Enabled");
        GridRowElement row = getGridElement().getRow(0);
        GridCellElement cell = getGridElement().getCell(0, 0);
        cell.click();
        Assert.assertNull("Editor should not open", getEditor());
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        Assert.assertNull("Editor should not open", getEditor());
    }
}

