package com.vaadin.v7.tests.components.grid.basicfeatures.server;


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
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        assertEditorOpen();
        Assert.assertFalse("Save button should not be visible in unbuffered mode.", isElementPresent(GridEditorTest.BY_EDITOR_SAVE));
        Assert.assertFalse("Cancel button should not be visible in unbuffered mode.", isElementPresent(GridEditorTest.BY_EDITOR_CANCEL));
    }

    @Test
    public void testToggleEditorUnbufferedWhileOpen() {
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        assertEditorOpen();
        selectMenuPath(GridEditorUnbufferedTest.TOGGLE_EDITOR_BUFFERED);
        boolean thrown = logContainsText("Exception occurred, java.lang.IllegalStateException");
        Assert.assertTrue("IllegalStateException thrown", thrown);
    }

    @Test
    public void testEditorMoveWithMouse() {
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        assertEditorOpen();
        String firstFieldValue = getEditorWidgets().get(0).getAttribute("value");
        Assert.assertEquals("Editor should be at row 5", "(5, 0)", firstFieldValue);
        getGridElement().getCell(10, 0).click();
        firstFieldValue = getEditorWidgets().get(0).getAttribute("value");
        Assert.assertEquals("Editor should be at row 10", "(10, 0)", firstFieldValue);
    }

    @Test
    public void testEditorMoveWithKeyboard() throws InterruptedException {
        selectMenuPath(GridEditorTest.EDIT_ITEM_100);
        assertEditorOpen();
        getEditorWidgets().get(0).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        String firstFieldValue = getEditorWidgets().get(0).getAttribute("value");
        Assert.assertEquals("Editor should move to row 101", "(101, 0)", firstFieldValue);
        for (int i = 0; i < 10; i++) {
            new org.openqa.selenium.interactions.Actions(getDriver()).keyDown(SHIFT).sendKeys(ENTER).keyUp(SHIFT).perform();
            firstFieldValue = getEditorWidgets().get(0).getAttribute("value");
            int row = 100 - i;
            Assert.assertEquals(("Editor should move to row " + row), (("(" + row) + ", 0)"), firstFieldValue);
        }
    }

    @Test
    public void testValidationErrorPreventsMove() throws InterruptedException {
        // Because of "out of view" issues, we need to move this for easy access
        selectMenuPath("Component", "Columns", "Column 7", "Column 7 Width", "50px");
        for (int i = 0; i < 6; ++i) {
            selectMenuPath("Component", "Columns", "Column 7", "Move left");
        }
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        getEditorWidgets().get(1).click();
        String faultyInt = "not a number";
        getEditorWidgets().get(1).sendKeys(faultyInt);
        getGridElement().getCell(10, 0).click();
        Assert.assertEquals("Editor should not move from row 5", "(5, 0)", getEditorWidgets().get(0).getAttribute("value"));
        getEditorWidgets().get(1).sendKeys(Keys.chord(CONTROL, "a"));
        getEditorWidgets().get(1).sendKeys("5");
        // FIXME: Needs to trigger one extra validation round-trip for now
        getGridElement().sendKeys(ENTER);
        getGridElement().getCell(10, 0).click();
        Assert.assertEquals("Editor should move to row 10", "(10, 0)", getEditorWidgets().get(0).getAttribute("value"));
    }

    @Test
    public void testErrorMessageWrapperHidden() {
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        assertEditorOpen();
        WebElement editorFooter = getEditor().findElement(By.className("v-grid-editor-footer"));
        Assert.assertTrue("Editor footer should not be visible when there's no error", editorFooter.getCssValue("display").equalsIgnoreCase("none"));
    }

    @Test
    public void testScrollEnabledOnProgrammaticOpen() {
        int originalScrollPos = getGridVerticalScrollPos();
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        scrollGridVerticallyTo(100);
        AbstractTB3Test.assertGreater("Grid should scroll vertically while editing in unbuffered mode", getGridVerticalScrollPos(), originalScrollPos);
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
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        selectMenuPath("Component", "State", "Enabled");
        assertEditorOpen();
        Assert.assertTrue("Editor text field should be disabled", (null != (getEditorWidgets().get(2).getAttribute("disabled"))));
        selectMenuPath("Component", "State", "Enabled");
        assertEditorOpen();
        Assert.assertFalse("Editor text field should not be disabled", (null != (getEditorWidgets().get(2).getAttribute("disabled"))));
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
    public void testProgrammaticOpeningWhenOpen() {
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        assertEditorOpen();
        Assert.assertEquals("Editor should edit row 5", "(5, 0)", getEditorWidgets().get(0).getAttribute("value"));
        selectMenuPath(GridEditorTest.EDIT_ITEM_100);
        assertEditorOpen();
        Assert.assertEquals("Editor should edit row 100", "(100, 0)", getEditorWidgets().get(0).getAttribute("value"));
    }

    @Test
    public void testExternalValueChangePassesToEditor() {
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        assertEditorOpen();
        selectMenuPath("Component", "State", "ReactiveValueChanger");
        getEditorWidgets().get(0).click();
        getEditorWidgets().get(0).sendKeys("changing value");
        // Focus another field to cause the value to be sent to the server
        getEditorWidgets().get(2).click();
        Assert.assertEquals("Value of Column 2 in the editor was not changed", "Modified", getEditorWidgets().get(2).getAttribute("value"));
    }

    @Test
    public void testEditorClosedOnUserSort() {
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        getGridElement().getHeaderCell(0, 0).click();
        assertEditorClosed();
    }

    @Test
    public void testEditorSaveOnRowChange() {
        // Double click sets the focus programmatically
        getGridElement().getCell(5, 2).doubleClick();
        TestBenchElement editor = getGridElement().getEditor().getField(2);
        editor.clear();
        // Click to ensure IE focus...
        editor.click(5, 5);
        editor.sendKeys("Foo", ENTER);
        Assert.assertEquals("Editor did not move.", "(6, 0)", getGridElement().getEditor().getField(0).getAttribute("value"));
        Assert.assertEquals("Editor field value did not update from server.", "(6, 2)", getGridElement().getEditor().getField(2).getAttribute("value"));
        Assert.assertEquals("Edited value was not saved.", "Foo", getGridElement().getCell(5, 2).getText());
    }
}

