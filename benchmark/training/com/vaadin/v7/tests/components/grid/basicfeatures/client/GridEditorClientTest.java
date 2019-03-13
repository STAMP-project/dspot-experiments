package com.vaadin.v7.tests.components.grid.basicfeatures.client;


import GridBasicFeatures.EDITABLE_COLUMNS;
import GridConstants.DEFAULT_CANCEL_CAPTION;
import GridConstants.DEFAULT_SAVE_CAPTION;
import Keys.ENTER;
import Keys.ESCAPE;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.v7.shared.ui.grid.GridConstants;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


public class GridEditorClientTest extends GridBasicClientFeaturesTest {
    private static final String[] EDIT_ROW_100 = new String[]{ "Component", "Editor", "Edit row 100" };

    private static final String[] EDIT_ROW_5 = new String[]{ "Component", "Editor", "Edit row 5" };

    @Test
    public void testProgrammaticOpeningClosing() {
        selectMenuPath(GridEditorClientTest.EDIT_ROW_5);
        Assert.assertNotNull(getEditor());
        selectMenuPath("Component", "Editor", "Cancel edit");
        Assert.assertNull(getEditor());
        Assert.assertEquals("Row 5 edit cancelled", findElement(By.className("grid-editor-log")).getText());
    }

    @Test
    public void testProgrammaticOpeningWithScroll() {
        selectMenuPath(GridEditorClientTest.EDIT_ROW_100);
        Assert.assertNotNull(getEditor());
    }

    @Test(expected = NoSuchElementException.class)
    public void testVerticalScrollLocking() {
        selectMenuPath(GridEditorClientTest.EDIT_ROW_5);
        getGridElement().getCell(200, 0);
    }

    @Test
    public void testMouseOpeningClosing() {
        getGridElement().getCell(4, 0).doubleClick();
        Assert.assertNotNull(getEditor());
        // Move focus to the third input field
        getEditor().findElements(By.className("gwt-TextBox")).get(2).click();
        // Press save button
        getSaveButton().click();
        // Make sure the editor went away
        Assert.assertNull(getEditor());
        // Check that focus has moved to cell 4,2 - the last one that was
        // focused in Editor
        Assert.assertTrue(getGridElement().getCell(4, 2).isFocused());
        // Disable editor
        selectMenuPath("Component", "Editor", "Enabled");
        getGridElement().getCell(4, 0).doubleClick();
        Assert.assertNull(getEditor());
    }

    @Test
    public void testKeyboardOpeningClosing() {
        getGridElement().getCell(4, 0).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        Assert.assertNotNull(getEditor());
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ESCAPE).perform();
        Assert.assertNull(getEditor());
        Assert.assertEquals("Row 4 edit cancelled", findElement(By.className("grid-editor-log")).getText());
        // Disable editor
        selectMenuPath("Component", "Editor", "Enabled");
        getGridElement().getCell(5, 0).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ENTER).perform();
        Assert.assertNull(getEditor());
    }

    @Test
    public void testWidgetBinding() throws Exception {
        selectMenuPath(GridEditorClientTest.EDIT_ROW_100);
        WebElement editor = getEditor();
        List<WebElement> widgets = editor.findElements(By.className("gwt-TextBox"));
        Assert.assertEquals(EDITABLE_COLUMNS, widgets.size());
        Assert.assertEquals("(100, 0)", widgets.get(0).getAttribute("value"));
        Assert.assertEquals("(100, 1)", widgets.get(1).getAttribute("value"));
        Assert.assertEquals("(100, 2)", widgets.get(2).getAttribute("value"));
        Assert.assertEquals("100", widgets.get(6).getAttribute("value"));
        Assert.assertEquals("<b>100</b>", widgets.get(8).getAttribute("value"));
    }

    @Test
    public void testWithSelectionColumn() throws Exception {
        selectMenuPath("Component", "State", "Selection mode", "multi");
        selectMenuPath("Component", "State", "Frozen column count", "-1 columns");
        selectMenuPath(GridEditorClientTest.EDIT_ROW_5);
        WebElement editorCells = findElements(By.className("v-grid-editor-cells")).get(1);
        List<WebElement> selectorDivs = editorCells.findElements(By.cssSelector("div"));
        Assert.assertFalse("selector column cell should've had contents", selectorDivs.get(0).getAttribute("innerHTML").isEmpty());
        Assert.assertFalse("normal column cell shoul've had contents", selectorDivs.get(1).getAttribute("innerHTML").isEmpty());
    }

    @Test
    public void testSave() {
        selectMenuPath(GridEditorClientTest.EDIT_ROW_100);
        WebElement textField = getEditor().findElements(By.className("gwt-TextBox")).get(0);
        textField.clear();
        textField.sendKeys("Changed");
        WebElement saveButton = getEditor().findElement(By.className("v-grid-editor-save"));
        saveButton.click();
        Assert.assertEquals("Changed", getGridElement().getCell(100, 0).getText());
    }

    @Test
    public void testProgrammaticSave() {
        selectMenuPath(GridEditorClientTest.EDIT_ROW_100);
        WebElement textField = getEditor().findElements(By.className("gwt-TextBox")).get(0);
        textField.clear();
        textField.sendKeys("Changed");
        selectMenuPath("Component", "Editor", "Save");
        Assert.assertEquals("Changed", getGridElement().getCell(100, 0).getText());
    }

    @Test
    public void testCaptionChange() {
        selectMenuPath(GridEditorClientTest.EDIT_ROW_5);
        Assert.assertEquals((("Save button caption should\'ve been \"" + (GridConstants.DEFAULT_SAVE_CAPTION)) + "\" to begin with"), DEFAULT_SAVE_CAPTION, getSaveButton().getText());
        Assert.assertEquals((("Cancel button caption should\'ve been \"" + (GridConstants.DEFAULT_CANCEL_CAPTION)) + "\" to begin with"), DEFAULT_CANCEL_CAPTION, getCancelButton().getText());
        selectMenuPath("Component", "Editor", "Change Save Caption");
        Assert.assertNotEquals("Save button caption should've changed while editor is open", DEFAULT_SAVE_CAPTION, getSaveButton().getText());
        getCancelButton().click();
        selectMenuPath("Component", "Editor", "Change Cancel Caption");
        selectMenuPath(GridEditorClientTest.EDIT_ROW_5);
        Assert.assertNotEquals("Cancel button caption should've changed while editor is closed", DEFAULT_CANCEL_CAPTION, getCancelButton().getText());
    }

    @Test
    public void testUneditableColumn() {
        selectMenuPath("Component", "Editor", "Edit row 5");
        Assert.assertFalse("Uneditable column should not have an editor widget", getGridElement().getEditor().isEditable(3));
    }

    @Test
    public void testErrorField() {
        selectMenuPath(GridEditorClientTest.EDIT_ROW_5);
        GridEditorElement editor = getGridElement().getEditor();
        Assert.assertTrue("No errors should be present", editor.findElements(By.className("error")).isEmpty());
        Assert.assertEquals("No error message should be present", null, editor.getErrorMessage());
        selectMenuPath("Component", "Editor", "Toggle second editor error");
        getSaveButton().click();
        Assert.assertEquals("Unexpected amount of error fields", 1, editor.findElements(By.className("error")).size());
        Assert.assertEquals("Unexpedted error message", ("Syntethic fail of editor in column 2. " + "This message is so long that it doesn't fit into its box"), editor.getErrorMessage());
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
    public void testNoFocusOnProgrammaticOpen() {
        selectMenuPath(GridEditorClientTest.EDIT_ROW_5);
        WebElement focused = getFocusedElement();
        if (BrowserUtil.isIE(getDesiredCapabilities())) {
            Assert.assertEquals("Focus should be nowhere", null, focused);
        } else {
            // GWT menubar loses focus after clicking a menuitem
            Assert.assertEquals("Focus should be in body", "body", focused.getTagName());
        }
    }
}

