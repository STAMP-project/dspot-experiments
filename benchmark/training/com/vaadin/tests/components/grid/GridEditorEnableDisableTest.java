package com.vaadin.tests.components.grid;


import Keys.ESCAPE;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class GridEditorEnableDisableTest extends SingleBrowserTest {
    @Test
    public void testEnabledEditor() {
        GridElement grid = $(GridElement.class).first();
        grid.getCell(0, 0).doubleClick();
        Assert.assertTrue("Editor must work when it is enabled!", isElementPresent(TextFieldElement.class));
    }

    @Test
    public void testDisabledEditor() {
        GridElement grid = $(GridElement.class).first();
        ButtonElement disableButton = $(ButtonElement.class).caption("Disable").first();
        disableButton.click();
        grid.getCell(0, 0).doubleClick();
        Assert.assertTrue("Editor must not work when it is disabled!", (!(isElementPresent(TextFieldElement.class))));
    }

    @Test
    public void testCancelAndDisableEditorWhenEditing() {
        GridElement grid = $(GridElement.class).first();
        ButtonElement cancelAndDisableButton = $(ButtonElement.class).caption("Cancel & Disable").first();
        grid.getCell(0, 0).doubleClick();
        cancelAndDisableButton.click();
        Assert.assertTrue("Editing must be canceled after calling cancel method!", (!(isElementPresent(TextFieldElement.class))));
        grid.getCell(0, 0).doubleClick();
        Assert.assertTrue("Editor must not work when it is disabled!", (!(isElementPresent(TextFieldElement.class))));
    }

    @Test
    public void testDisableEditorAfterCancelEditing() {
        GridElement grid = $(GridElement.class).first();
        ButtonElement disableButton = $(ButtonElement.class).caption("Disable").first();
        grid.getCell(0, 0).doubleClick();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ESCAPE).perform();
        Assert.assertTrue("Editing must be canceled after pressing Escape key!", (!(isElementPresent(TextFieldElement.class))));
        disableButton.click();
        grid.getCell(0, 0).doubleClick();
        Assert.assertTrue("Editor must not work when it is disabled!", (!(isElementPresent(TextFieldElement.class))));
    }

    @Test
    public void testReenableEditorAfterCancelEditing() {
        GridElement grid = $(GridElement.class).first();
        ButtonElement cancelAndDisableButton = $(ButtonElement.class).caption("Cancel & Disable").first();
        ButtonElement enableButton = $(ButtonElement.class).caption("Enable").first();
        grid.getCell(0, 0).doubleClick();
        cancelAndDisableButton.click();
        enableButton.click();
        grid.getCell(0, 0).doubleClick();
        Assert.assertTrue("Editor must work after re-enabling!", isElementPresent(TextFieldElement.class));
    }

    @Test
    public void testEnableAndEditRow() {
        ButtonElement disableButton = $(ButtonElement.class).caption("Disable").first();
        ButtonElement enableAndEditRowButton = $(ButtonElement.class).caption("Enable & Edit Row").first();
        disableButton.click();
        enableAndEditRowButton.click();
        Assert.assertTrue("Editor must be open after calling editRow method!", isElementPresent(TextFieldElement.class));
    }
}

