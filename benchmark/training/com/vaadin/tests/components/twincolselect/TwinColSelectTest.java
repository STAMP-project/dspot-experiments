package com.vaadin.tests.components.twincolselect;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class TwinColSelectTest extends MultiBrowserTest {
    @Test
    public void initialLoad_containsCorrectItems() {
        assertItems(20);
    }

    @Test
    public void initialItems_reduceItemCount_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "5");
        assertItems(5);
    }

    @Test
    public void initialItems_increaseItemCount_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "100");
        assertItems(100);
    }

    @Test
    public void itemsMovedFromLeftToRight() {
        selectMenuPath("Component", "Data provider", "Items", "5");
        assertItems(5);
        selectItems("Item 1", "Item 2", "Item 4");
        assertSelected("Item 1", "Item 2", "Item 4");
        assertOptionTexts("Item 0", "Item 3");
        deselectItems("Item 1", "Item 4");
        assertSelected("Item 2");
        assertOptionTexts("Item 0", "Item 1", "Item 3", "Item 4");
        selectItems("Item 0");
        assertSelected("Item 0", "Item 2");
        assertOptionTexts("Item 1", "Item 3", "Item 4");
    }

    @Test
    public void clickToSelect() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectItems("Item 4");
        Assert.assertEquals("1. Selected: [Item 4]", getLogRow(0));
        assertSelected("Item 4");
        // the previous item stays selected
        selectItems("Item 2");
        // Selection order (most recently selected is last)
        Assert.assertEquals("2. Selected: [Item 4, Item 2]", getLogRow(0));
        assertSelected("Item 2", "Item 4");
        deselectItems("Item 4");
        Assert.assertEquals("3. Selected: [Item 2]", getLogRow(0));
        assertSelected("Item 2");
        selectItems("Item 10", "Item 0", "Item 9", "Item 4");
        Assert.assertEquals("4. Selected: [Item 2, Item 0, Item 4, Item 10, Item 9]", getLogRow(0));
        assertSelected("Item 0", "Item 2", "Item 4", "Item 9", "Item 10");
        deselectItems("Item 0", "Item 2", "Item 9");
        Assert.assertEquals("5. Selected: [Item 4, Item 10]", getLogRow(0));
        assertSelected("Item 4", "Item 10");
    }

    @Test
    public void disabled_clickToSelect() {
        selectMenuPath("Component", "State", "Enabled");
        List<WebElement> selects = getTwinColSelect().findElements(By.tagName("select"));
        Assert.assertEquals(2, selects.size());
        Assert.assertTrue(selects.stream().allMatch(( element) -> (element.getAttribute("disabled")) != null));
        List<WebElement> buttons = getTwinColSelect().findElements(By.className("v-button"));
        Assert.assertEquals(2, buttons.size());
        buttons.forEach(( button) -> assertEquals("v-button v-disabled", button.getAttribute("className")));
        selectMenuPath("Component", "Listeners", "Selection listener");
        String lastLogRow = getLogRow(0);
        selectItems("Item 4");
        Assert.assertEquals(lastLogRow, getLogRow(0));
        assertNothingSelected();
        selectItems("Item 2");
        // Selection order (most recently selected is last)
        Assert.assertEquals(lastLogRow, getLogRow(0));
        assertNothingSelected();
        selectItems("Item 4");
        Assert.assertEquals(lastLogRow, getLogRow(0));
        assertNothingSelected();
    }

    @Test
    public void clickToSelect_reenable() {
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Listeners", "Selection listener");
        List<WebElement> selects = getTwinColSelect().findElements(By.tagName("select"));
        Assert.assertEquals(2, selects.size());
        Assert.assertTrue(selects.stream().allMatch(( element) -> (element.getAttribute("disabled")) != null));
        List<WebElement> buttons = getTwinColSelect().findElements(By.className("v-button"));
        Assert.assertEquals(2, buttons.size());
        buttons.forEach(( button) -> assertEquals("v-button v-disabled", button.getAttribute("className")));
        selectItems("Item 4");
        assertNothingSelected();
        selectMenuPath("Component", "State", "Enabled");
        assertElementNotPresent(By.className("v-disabled"));
        selectItems("Item 5");
        Assert.assertEquals("3. Selected: [Item 5]", getLogRow(0));
        assertSelected("Item 5");
        selectItems("Item 2");
        Assert.assertEquals("4. Selected: [Item 5, Item 2]", getLogRow(0));
        assertSelected("Item 2", "Item 5");
        deselectItems("Item 5");
        Assert.assertEquals("5. Selected: [Item 2]", getLogRow(0));
        assertSelected("Item 2");
    }

    @Test
    public void itemCaptionGenerator() {
        selectMenuPath("Component", "Item Generator", "Item Caption Generator", "Custom Caption Generator");
        assertItems(20, " Caption");
    }

    @Test
    public void nullItemCaptionGenerator() {
        selectMenuPath("Component", "Item Generator", "Item Caption Generator", "Null Caption Generator");
        for (String text : getTwinColSelect().getOptions()) {
            Assert.assertEquals("", text);
        }
    }

    @Test
    public void selectProgramatically() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectMenuPath("Component", "Selection", "Toggle Item 5");
        Assert.assertEquals("2. Selected: [Item 5]", getLogRow(0));
        assertSelected("Item 5");
        selectMenuPath("Component", "Selection", "Toggle Item 1");
        // Selection order (most recently selected is last)
        Assert.assertEquals("4. Selected: [Item 5, Item 1]", getLogRow(0));
        // DOM order
        assertSelected("Item 1", "Item 5");
        selectMenuPath("Component", "Selection", "Toggle Item 5");
        Assert.assertEquals("6. Selected: [Item 1]", getLogRow(0));
        assertSelected("Item 1");
        selectMenuPath("Component", "Selection", "Toggle items 0, 1, 5, 10, 25");
        // currently non-existing items are added to selection!
        Assert.assertEquals("8. Selected: [Item 1, Item 0, Item 5, Item 10, Item 25]", getLogRow(0));
        assertSelected("Item 0", "Item 1", "Item 5", "Item 10");
    }
}

