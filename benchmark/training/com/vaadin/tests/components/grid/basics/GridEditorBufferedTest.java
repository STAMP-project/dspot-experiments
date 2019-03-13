package com.vaadin.tests.components.grid.basics;


import GridConstants.DEFAULT_CANCEL_CAPTION;
import GridConstants.DEFAULT_SAVE_CAPTION;
import Keys.END;
import Keys.ENTER;
import Keys.ESCAPE;
import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.testbench.elements.NotificationElement;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import static GridBasics.COLUMN_CAPTIONS;


public class GridEditorBufferedTest extends GridEditorTest {
    @Test
    public void testKeyboardSave() {
        editRow(100);
        WebElement textField = getEditor().getField(0);
        textField.click();
        // without this, the click in the middle of the field might not be after
        // the old text on some browsers
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(END).perform();
        textField.sendKeys(" changed");
        // Save from keyboard
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        assertEditorClosed();
        Assert.assertEquals("(100, 0) changed", getGridElement().getCell(100, 0).getText());
    }

    @Test
    public void testKeyboardSaveWithHiddenColumn() {
        selectMenuPath("Component", "Columns", "Column 0", "Hidden");
        editRow(100);
        WebElement textField = getEditor().getField(5);
        textField.click();
        // without this, the click in the middle of the field might not be after
        // the old text on some browsers
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(END).perform();
        textField.sendKeys(" changed");
        // Save from keyboard
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        assertEditorClosed();
        Assert.assertEquals("100 changed", getGridElement().getCell(100, 4).getText());
    }

    @Test
    public void testKeyboardSaveWithInvalidEdition() {
        makeInvalidEdition();
        GridEditorElement editor = getGridElement().getEditor();
        TestBenchElement field = editor.getField(7);
        field.click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        assertEditorOpen();
        Assert.assertEquals(((COLUMN_CAPTIONS[7]) + ": Could not convert value to Integer"), editor.getErrorMessage());
        Assert.assertTrue("Field 7 should have been marked with an error after error", isEditorCellErrorMarked(7));
        editor.cancel();
        editRow(100);
        Assert.assertFalse("Exception should not exist", isElementPresent(NotificationElement.class));
        Assert.assertEquals("There should be no editor error message", null, getGridElement().getEditor().getErrorMessage());
    }

    @Test
    public void testSave() {
        editRow(100);
        WebElement textField = getEditor().getField(0);
        textField.click();
        // without this, the click in the middle of the field might not be after
        // the old text on some browsers
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(END).perform();
        textField.sendKeys(" changed");
        WebElement saveButton = getEditor().findElement(By.className("v-grid-editor-save"));
        saveButton.click();
        Assert.assertEquals("(100, 0) changed", getGridElement().getCell(100, 0).getText());
    }

    @Test
    public void testProgrammaticSave() {
        editRow(100);
        WebElement textField = getEditor().getField(0);
        textField.click();
        // without this, the click in the middle of the field might not be after
        // the old text on some browsers
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(END).perform();
        textField.sendKeys(" changed");
        selectMenuPath("Component", "Editor", "Save");
        Assert.assertEquals("(100, 0) changed", getGridElement().getCell(100, 0).getText());
    }

    @Test
    public void testInvalidEdition() {
        makeInvalidEdition();
        GridEditorElement editor = getGridElement().getEditor();
        editor.save();
        Assert.assertEquals(((COLUMN_CAPTIONS[7]) + ": Could not convert value to Integer"), editor.getErrorMessage());
        Assert.assertTrue("Field 7 should have been marked with an error after error", isEditorCellErrorMarked(7));
        editor.cancel();
        editRow(100);
        Assert.assertFalse("Exception should not exist", isElementPresent(NotificationElement.class));
        Assert.assertEquals("There should be no editor error message", null, getGridElement().getEditor().getErrorMessage());
    }

    @Test
    public void testEditorInDisabledGrid() {
        int originalScrollPos = getGridVerticalScrollPos();
        editRow(5);
        assertEditorOpen();
        selectMenuPath("Component", "State", "Enabled");
        assertEditorOpen();
        GridEditorElement editor = getGridElement().getEditor();
        editor.save();
        assertEditorOpen();
        editor.cancel();
        assertEditorOpen();
        selectMenuPath("Component", "State", "Enabled");
        scrollGridVerticallyTo(100);
        Assert.assertEquals("Grid shouldn't scroll vertically while editing in buffered mode", originalScrollPos, getGridVerticalScrollPos());
    }

    @Test
    public void testCaptionChange() {
        editRow(5);
        Assert.assertEquals((("Save button caption should\'ve been \"" + (GridConstants.DEFAULT_SAVE_CAPTION)) + "\" to begin with"), DEFAULT_SAVE_CAPTION, getSaveButton().getText());
        Assert.assertEquals((("Cancel button caption should\'ve been \"" + (GridConstants.DEFAULT_CANCEL_CAPTION)) + "\" to begin with"), DEFAULT_CANCEL_CAPTION, getCancelButton().getText());
        selectMenuPath("Component", "Editor", "Change save caption");
        Assert.assertNotEquals("Save button caption should've changed while editor is open", DEFAULT_SAVE_CAPTION, getSaveButton().getText());
        getCancelButton().click();
        selectMenuPath("Component", "Editor", "Change cancel caption");
        editRow(5);
        Assert.assertNotEquals("Cancel button caption should've changed while editor is closed", DEFAULT_CANCEL_CAPTION, getCancelButton().getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void testVerticalScrollLocking() {
        editRow(5);
        getGridElement().getCell(200, 0);
    }

    @Test
    public void testScrollDisabledOnMouseOpen() {
        int originalScrollPos = getGridVerticalScrollPos();
        GridCellElement cell_5_0 = getGridElement().getCell(5, 0);
        new org.openqa.selenium.interactions.Actions(getDriver()).doubleClick(cell_5_0).perform();
        scrollGridVerticallyTo(100);
        Assert.assertEquals("Grid shouldn't scroll vertically while editing in buffered mode", originalScrollPos, getGridVerticalScrollPos());
    }

    @Test
    public void testScrollDisabledOnKeyboardOpen() {
        int originalScrollPos = getGridVerticalScrollPos();
        GridCellElement cell_5_0 = getGridElement().getCell(5, 0);
        cell_5_0.click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        scrollGridVerticallyTo(100);
        Assert.assertEquals("Grid shouldn't scroll vertically while editing in buffered mode", originalScrollPos, getGridVerticalScrollPos());
    }

    @Test
    public void testMouseOpeningClosing() {
        getGridElement().getCell(4, 0).doubleClick();
        assertEditorOpen();
        getCancelButton().click();
        assertEditorClosed();
        selectMenuPath(GridEditorTest.TOGGLE_EDIT_ENABLED);
        getGridElement().getCell(4, 0).doubleClick();
        assertEditorClosed();
    }

    @Test
    public void testMouseOpeningDisabledWhenOpen() {
        editRow(5);
        getGridElement().getCell(2, 0).doubleClick();
        Assert.assertEquals("Editor should still edit row 5", "(5, 0)", getEditor().getField(0).getAttribute("value"));
    }

    @Test
    public void testUserSortDisabledWhenOpen() {
        editRow(5);
        getGridElement().getHeaderCell(0, 0).click();
        assertEditorOpen();
        Assert.assertEquals("(2, 0)", getGridElement().getCell(2, 0).getText());
    }

    @Test
    public void testFocusWhenCancelByKeyboard() {
        editRow(5);
        getGridElement().getEditor().getField(0).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ESCAPE).perform();
        Assert.assertTrue("Focus should be in the Grid", getFocusedElement().getAttribute("class").contains("v-grid"));
    }

    @Test
    public void testFocusWhenSaveByKeyboard() {
        editRow(5);
        getGridElement().getEditor().getField(0).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        Assert.assertTrue("Focus should be in the Grid", getFocusedElement().getAttribute("class").contains("v-grid"));
    }

    @Test
    public void testFocusWhenSaveByClick() {
        editRow(5);
        getGridElement().getEditor().findElement(By.className("v-grid-editor-save")).click();
        Assert.assertTrue("Focus should be in the Grid", getFocusedElement().getAttribute("class").contains("v-grid"));
    }

    @Test
    public void testFocusWhenCancelByClick() {
        editRow(5);
        getGridElement().getEditor().findElement(By.className("v-grid-editor-cancel")).click();
        Assert.assertTrue("Focus should be in the Grid", getFocusedElement().getAttribute("class").contains("v-grid"));
    }
}

