package com.vaadin.tests.components.grid.basics;


import Keys.CONTROL;
import Keys.ENTER;
import Keys.SHIFT;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.AbstractTB3Test;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;


public class GridEditorUnbufferedTest extends GridEditorTest {
    private static final String[] TOGGLE_EDITOR_BUFFERED = new String[]{ "Component", "Editor", "Buffered mode" };

    private static final String[] CANCEL_EDIT = new String[]{ "Component", "Editor", "Cancel edit" };

    @Test
    public void testEditorShowsNoButtons() {
        editRow(5);
        assertEditorOpen();
        Assert.assertFalse("Save button should not be visible in unbuffered mode.", isElementPresent(GridEditorTest.BY_EDITOR_SAVE));
        Assert.assertFalse("Cancel button should not be visible in unbuffered mode.", isElementPresent(GridEditorTest.BY_EDITOR_CANCEL));
    }

    @Test
    public void testToggleEditorUnbufferedWhileOpen() {
        editRow(5);
        assertEditorOpen();
        selectMenuPath(GridEditorUnbufferedTest.TOGGLE_EDITOR_BUFFERED);
        boolean thrown = logContainsText("Exception occurred, java.lang.IllegalStateException");
        Assert.assertTrue("IllegalStateException was not thrown", thrown);
    }

    @Test
    public void testEditorMoveWithMouse() {
        editRow(5);
        assertEditorOpen();
        String firstFieldValue = getEditor().getField(0).getAttribute("value");
        Assert.assertEquals("Editor should be at row 5", "(5, 0)", firstFieldValue);
        getGridElement().getCell(6, 0).click();
        firstFieldValue = getEditor().getField(0).getAttribute("value");
        Assert.assertEquals("Editor should be at row 6", "(6, 0)", firstFieldValue);
    }

    @Test
    public void testEditorMoveWithKeyboard() throws InterruptedException {
        editRow(100);
        getEditor().getField(0).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        String firstFieldValue = getEditor().getField(0).getAttribute("value");
        Assert.assertEquals("Editor should move to row 101", "(101, 0)", firstFieldValue);
        for (int i = 0; i < 10; i++) {
            new org.openqa.selenium.interactions.Actions(getDriver()).keyDown(SHIFT).sendKeys(ENTER).keyUp(SHIFT).perform();
            firstFieldValue = getEditor().getField(0).getAttribute("value");
            int row = 100 - i;
            Assert.assertEquals(("Editor should move to row " + row), (("(" + row) + ", 0)"), firstFieldValue);
        }
    }

    @Test
    public void testValidationErrorPreventsMove() throws InterruptedException {
        editRow(5);
        getEditor().getField(7).click();
        String faultyInt = "not a number";
        getEditor().getField(7).sendKeys(faultyInt);
        getGridElement().getCell(7, 7).click();
        Assert.assertEquals("Editor should not move from row 5", "(5, 0)", getEditor().getField(0).getAttribute("value"));
        getEditor().getField(7).sendKeys(Keys.chord(CONTROL, "a"));
        getEditor().getField(7).sendKeys("4");
        getGridElement().getCell(7, 0).click();
        Assert.assertEquals("Editor should move to row 7", "(7, 0)", getEditor().getField(0).getAttribute("value"));
    }

    @Test
    public void testErrorMessageWrapperHidden() {
        editRow(5);
        assertEditorOpen();
        WebElement editorFooter = getEditor().findElement(By.className("v-grid-editor-footer"));
        Assert.assertTrue("Editor footer should not be visible when there's no error", editorFooter.getCssValue("display").equalsIgnoreCase("none"));
    }

    @Test
    public void testScrollEnabledOnMouseOpen() {
        int originalScrollPos = getGridVerticalScrollPos();
        GridCellElement cell_5_0 = getGridElement().getCell(5, 0);
        new org.openqa.selenium.interactions.Actions(getDriver()).doubleClick(cell_5_0).perform();
        scrollGridVerticallyTo(100);
        AbstractTB3Test.assertGreater("Grid should scroll vertically while editing in unbuffered mode", getGridVerticalScrollPos(), originalScrollPos);
    }

    @Test
    public void testScrollEnabledOnKeyboardOpen() {
        int originalScrollPos = getGridVerticalScrollPos();
        GridCellElement cell_5_0 = getGridElement().getCell(5, 0);
        cell_5_0.click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        scrollGridVerticallyTo(100);
        AbstractTB3Test.assertGreater("Grid should scroll vertically while editing in unbuffered mode", getGridVerticalScrollPos(), originalScrollPos);
    }

    @Test
    public void testEditorInDisabledGrid() {
        editRow(5);
        selectMenuPath("Component", "State", "Enabled");
        assertEditorOpen();
        Assert.assertTrue("Editor text field should be disabled", (null != (getEditor().getField(0).getAttribute("disabled"))));
        selectMenuPath("Component", "State", "Enabled");
        assertEditorOpen();
        Assert.assertFalse("Editor text field should not be disabled", (null != (getEditor().getField(0).getAttribute("disabled"))));
    }

    @Test
    public void testMouseOpeningClosing() {
        getGridElement().getCell(4, 0).doubleClick();
        assertEditorOpen();
        selectMenuPath(GridEditorUnbufferedTest.CANCEL_EDIT);
        selectMenuPath(GridEditorTest.TOGGLE_EDIT_ENABLED);
        getGridElement().getCell(4, 0).doubleClick();
        assertEditorClosed();
    }

    @Test
    public void testEditorClosedOnUserSort() {
        editRow(5);
        getGridElement().getHeaderCell(0, 0).click();
        assertEditorClosed();
    }

    @Test
    public void testEditorSaveOnRowChange() {
        // Double click sets the focus programmatically
        getGridElement().getCell(5, 0).doubleClick();
        TestBenchElement editor = getGridElement().getEditor().getField(0);
        editor.clear();
        // Click to ensure IE focus...
        editor.click(5, 5);
        editor.sendKeys("Foo Bar", ENTER);
        Assert.assertEquals("Editor did not move.", "(6, 0)", getGridElement().getEditor().getField(0).getAttribute("value"));
        Assert.assertEquals("Editor field value did not update from server.", "6", getGridElement().getEditor().getField(3).getAttribute("value"));
        Assert.assertEquals("Edited value was not saved.", "Foo Bar", getGridElement().getCell(5, 0).getText());
    }
}

