package com.vaadin.tests.components.grid.basics;


import Keys.ENTER;
import Keys.ESCAPE;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public abstract class GridEditorTest extends GridBasicsTest {
    protected static final By BY_EDITOR_CANCEL = By.className("v-grid-editor-cancel");

    protected static final By BY_EDITOR_SAVE = By.className("v-grid-editor-save");

    protected static final String[] TOGGLE_EDIT_ENABLED = new String[]{ "Component", "Editor", "Enabled" };

    @Test
    public void testProgrammaticClosing() {
        editRow(5);
        assertEditorOpen();
        selectMenuPath("Component", "Editor", "Cancel edit");
        assertEditorClosed();
    }

    @Test
    public void testKeyboardOpeningClosing() {
        getGridElement().getCell(4, 0).click();
        assertEditorClosed();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        assertEditorOpen();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ESCAPE).perform();
        assertEditorClosed();
        // Disable Editor
        selectMenuPath(GridEditorTest.TOGGLE_EDIT_ENABLED);
        getGridElement().getCell(5, 0).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        assertEditorClosed();
    }

    @Test
    public void testFocusOnMouseOpen() {
        GridCellElement cell = getGridElement().getCell(4, 0);
        cell.doubleClick();
        WebElement focused = getFocusedElement();
        Assert.assertEquals("", "input", focused.getTagName());
        Assert.assertEquals("", cell.getText(), focused.getAttribute("value"));
    }

    @Test
    public void testFocusOnKeyboardOpen() {
        GridCellElement cell = getGridElement().getCell(4, 0);
        cell.click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        WebElement focused = getFocusedElement();
        Assert.assertEquals("", "input", focused.getTagName());
        Assert.assertEquals("", cell.getText(), focused.getAttribute("value"));
    }

    @Test
    public void testUneditableColumn() {
        editRow(5);
        assertEditorOpen();
        GridEditorElement editor = getGridElement().getEditor();
        Assert.assertFalse("Uneditable column should not have an editor widget", editor.isEditable(2));
        String classNames = editor.findElements(By.className("v-grid-editor-cells")).get(1).findElements(By.xpath("./div")).get(2).getAttribute("class");
        Assert.assertTrue("Noneditable cell should contain not-editable classname", classNames.contains("not-editable"));
        Assert.assertTrue("Noneditable cell should contain v-grid-cell classname", classNames.contains("v-grid-cell"));
        assertNoErrorNotifications();
    }

    @Test
    public void testNoOpenFromHeaderOrFooter() {
        selectMenuPath("Component", "Footer", "Append footer row");
        getGridElement().getHeaderCell(0, 0).doubleClick();
        assertEditorClosed();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        assertEditorClosed();
        getGridElement().getFooterCell(0, 0).doubleClick();
        assertEditorClosed();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        assertEditorClosed();
    }

    @Test
    public void testEditorClosedOnSort() {
        editRow(5);
        selectMenuPath("Component", "Columns", "Column 0", "Sort ASC");
        assertEditorClosed();
    }

    @Test
    public void testEditorOpeningFromServer() {
        selectMenuPath("Component", "Editor", "Edit row 5");
        assertEditorOpen();
        Assert.assertEquals("Unexpected editor field content", "5", getEditor().getField(3).getAttribute("value"));
        Assert.assertEquals("Unexpected not-editable column content", "(5, 1)", getEditor().findElement(By.className("not-editable")).getText());
    }

    @Test
    public void testEditorOpenWithScrollFromServer() {
        selectMenuPath("Component", "Editor", "Edit last row");
        assertEditorOpen();
        Assert.assertEquals("Unexpected editor field content", "999", getEditor().getField(3).getAttribute("value"));
        Assert.assertEquals("Unexpected not-editable column content", "(999, 1)", getEditor().findElement(By.className("not-editable")).getText());
    }

    @Test
    public void testEditorCancelOnOpen() {
        editRow(2);
        getGridElement().sendKeys(ESCAPE);
        selectMenuPath("Component", "Editor", "Cancel next edit");
        getGridElement().getCell(2, 0).doubleClick();
        assertEditorClosed();
        editRow(2);
        assertNoErrorNotifications();
    }
}

