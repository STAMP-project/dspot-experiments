package com.vaadin.tests.fieldgroup;


import Keys.ESCAPE;
import Keys.TAB;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


@TestCategory("grid")
public class BasicCrudGridEditorRowTest extends MultiBrowserTest {
    private GridElement grid;

    @Test
    public void lookAndFeel() throws Exception {
        GridCellElement ritaBirthdate = grid.getCell(2, 3);
        waitUntilLoadingIndicatorNotVisible();
        compareScreen("grid");
        // Open editor row
        new org.openqa.selenium.interactions.Actions(getDriver()).doubleClick(ritaBirthdate).perform();
        sleep(200);
        compareScreen("editorrow");
    }

    @Test
    public void editorRowOneInvalidValue() throws Exception {
        GridCellElement ritaBirthdate = grid.getCell(2, 3);
        // Open editor row
        new org.openqa.selenium.interactions.Actions(getDriver()).doubleClick(ritaBirthdate).perform();
        GridEditorElement editor = grid.getEditor();
        DateFieldElement dateField = editor.$(DateFieldElement.class).first();
        WebElement input = dateField.findElement(By.xpath("input"));
        // input.click();
        input.sendKeys("Invalid", TAB);
        editor.save();
        Assert.assertTrue("Editor wasn't displayed.", editor.isDisplayed());
        Assert.assertTrue("DateField wasn't displayed.", dateField.isDisplayed());
        Assert.assertTrue("DateField didn't have 'v-invalid' css class.", hasCssClass(dateField, "v-datefield-error"));
    }

    @Test
    public void testCheckboxInEditorWorks() {
        GridCellElement ritaBirthdate = grid.getCell(2, 3);
        // Open editor row
        new org.openqa.selenium.interactions.Actions(getDriver()).doubleClick(ritaBirthdate).perform();
        // Get CheckBox
        GridEditorElement editor = grid.getEditor();
        CheckBoxElement cb = editor.getField(5).wrap(CheckBoxElement.class);
        // Check values
        String value = cb.getValue();
        cb.click(5, 5);
        Assert.assertNotEquals("Checkbox value did not change", value, cb.getValue());
    }

    @Test
    public void testNoTopStyleSetOnEditorOpenWithFooterOnTop() {
        GridCellElement cell = grid.getCell(2, 3);
        // Open editor row
        new org.openqa.selenium.interactions.Actions(getDriver()).doubleClick(cell).perform();
        // Close editor
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ESCAPE).perform();
        cell = grid.getCell(14, 3);
        // Open editor row
        new org.openqa.selenium.interactions.Actions(getDriver()).doubleClick(cell).perform();
        String attribute = grid.getEditor().getAttribute("style").toLowerCase(Locale.ROOT);
        Assert.assertFalse("Style should not contain top.", attribute.contains("top:"));
    }
}

