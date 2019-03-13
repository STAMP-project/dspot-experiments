package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import GridBasicFeatures.EDITABLE_COLUMNS;
import Keys.ENTER;
import Keys.ESCAPE;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public abstract class GridEditorTest extends GridBasicFeaturesTest {
    protected static final By BY_EDITOR_CANCEL = By.className("v-grid-editor-cancel");

    protected static final By BY_EDITOR_SAVE = By.className("v-grid-editor-save");

    protected static final String[] EDIT_ITEM_5 = new String[]{ "Component", "Editor", "Edit item 5" };

    protected static final String[] EDIT_ITEM_100 = new String[]{ "Component", "Editor", "Edit item 100" };

    protected static final String[] TOGGLE_EDIT_ENABLED = new String[]{ "Component", "Editor", "Enabled" };

    @Test
    public void testProgrammaticOpeningClosing() {
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        assertEditorOpen();
        selectMenuPath("Component", "Editor", "Cancel edit");
        assertEditorClosed();
    }

    @Test
    public void testProgrammaticOpeningWhenDisabled() {
        selectMenuPath(GridEditorTest.TOGGLE_EDIT_ENABLED);
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        assertEditorClosed();
        boolean thrown = logContainsText("Exception occurred, java.lang.IllegalStateException");
        Assert.assertTrue("IllegalStateException thrown", thrown);
    }

    @Test
    public void testDisablingWhileOpen() {
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        selectMenuPath(GridEditorTest.TOGGLE_EDIT_ENABLED);
        assertEditorOpen();
        boolean thrown = logContainsText("Exception occurred, java.lang.IllegalStateException");
        Assert.assertTrue("IllegalStateException thrown", thrown);
    }

    @Test
    public void testProgrammaticOpeningWithScroll() {
        selectMenuPath(GridEditorTest.EDIT_ITEM_100);
        assertEditorOpen();
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
    public void testComponentBinding() {
        selectMenuPath(GridEditorTest.EDIT_ITEM_100);
        List<WebElement> widgets = getEditorWidgets();
        Assert.assertEquals("Number of widgets", EDITABLE_COLUMNS, widgets.size());
        Assert.assertEquals("(100, 0)", widgets.get(0).getAttribute("value"));
        Assert.assertEquals("(100, 1)", widgets.get(1).getAttribute("value"));
        Assert.assertEquals("(100, 2)", widgets.get(2).getAttribute("value"));
        Assert.assertEquals("<b>100</b>", widgets.get(8).getAttribute("value"));
    }

    @Test
    public void testFocusOnMouseOpen() {
        GridCellElement cell = getGridElement().getCell(4, 2);
        cell.doubleClick();
        WebElement focused = getFocusedElement();
        Assert.assertEquals("", "input", focused.getTagName());
        Assert.assertEquals("", cell.getText(), focused.getAttribute("value"));
    }

    @Test
    public void testFocusOnKeyboardOpen() {
        GridCellElement cell = getGridElement().getCell(4, 2);
        cell.click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        WebElement focused = getFocusedElement();
        Assert.assertEquals("", "input", focused.getTagName());
        Assert.assertEquals("", cell.getText(), focused.getAttribute("value"));
    }

    @Test
    public void testFocusOnProgrammaticOpenOnItemClick() {
        selectMenuPath("Component", "State", "EditorOpeningItemClickListener");
        GridCellElement cell = getGridElement().getCell(4, 2);
        cell.click();
        WebElement focused = getFocusedElement();
        Assert.assertEquals("", "input", focused.getTagName());
        Assert.assertEquals("", cell.getText(), focused.getAttribute("value"));
    }

    @Test
    public void testNoFocusOnProgrammaticOpen() {
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        WebElement focused = getFocusedElement();
        Assert.assertEquals("Focus should remain in the menu", "menu", focused.getAttribute("id"));
    }

    @Test
    public void testUneditableColumn() {
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        assertEditorOpen();
        GridEditorElement editor = getGridElement().getEditor();
        Assert.assertFalse("Uneditable column should not have an editor widget", editor.isEditable(3));
        String classNames = editor.findElements(By.className("v-grid-editor-cells")).get(1).findElements(By.xpath("./div")).get(3).getAttribute("class");
        Assert.assertTrue("Noneditable cell should contain not-editable classname", classNames.contains("not-editable"));
        Assert.assertTrue("Noneditable cell should contain v-grid-cell classname", classNames.contains("v-grid-cell"));
        assertNoErrorNotifications();
    }

    @Test
    public void testNoOpenFromHeaderOrFooter() {
        selectMenuPath("Component", "Footer", "Visible");
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
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        selectMenuPath("Component", "State", "Sort by column", "Column 0, ASC");
        assertEditorClosed();
    }

    @Test
    public void testEditorClosedOnFilter() {
        selectMenuPath(GridEditorTest.EDIT_ITEM_5);
        selectMenuPath("Component", "Filter", "Column 1 starts with \"(23\"");
        assertEditorClosed();
    }
}

