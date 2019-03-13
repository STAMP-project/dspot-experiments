package com.vaadin.tests.components.listselect;


import Keys.ARROW_DOWN;
import Keys.ARROW_UP;
import com.vaadin.testbench.elements.AbstractComponentElement.ReadOnlyException;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class ListSelectTest extends SingleBrowserTest {
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
    public void clickToSelect() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectItem("Item 4");
        Assert.assertEquals("1. Selected: [Item 4]", getLogRow(0));
        selectItem("Item 2");
        Assert.assertEquals("3. Selected: [Item 2]", getLogRow(0));
        addItemsToSelection("Item 4");
        Assert.assertEquals("4. Selected: [Item 2, Item 4]", getLogRow(0));
        // will cause 3 events
        addItemsToSelection("Item 10", "Item 0", "Item 9");
        Assert.assertEquals("7. Selected: [Item 2, Item 4, Item 10, Item 0, Item 9]", getLogRow(0));
        // will cause 3 events
        removeItemsFromSelection("Item 0", "Item 2", "Item 9");
        Assert.assertEquals("10. Selected: [Item 4, Item 10]", getLogRow(0));
    }

    @Test
    public void keyboardSelect() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectItem("Item 4");
        Assert.assertEquals("1. Selected: [Item 4]", getLogRow(0));
        getListSelect().findElement(By.tagName("select")).sendKeys(ARROW_UP);
        Assert.assertEquals("2. Selected: [Item 3]", getLogRow(0));
        getListSelect().findElement(By.tagName("select")).sendKeys(ARROW_DOWN, ARROW_DOWN);
        Assert.assertEquals("4. Selected: [Item 5]", getLogRow(0));
    }

    @Test
    public void disabled_clickToSelect() {
        selectMenuPath("Component", "State", "Enabled");
        List<WebElement> select = getListSelect().findElements(By.tagName("select"));
        Assert.assertEquals(1, select.size());
        Assert.assertNotNull(select.get(0).getAttribute("disabled"));
        selectMenuPath("Component", "Listeners", "Selection listener");
        String lastLogRow = getLogRow(0);
        selectItem("Item 4");
        Assert.assertEquals(lastLogRow, getLogRow(0));
        assertNothingSelected();
        addItemsToSelection("Item 2");
        Assert.assertEquals(lastLogRow, getLogRow(0));
        assertNothingSelected();
        removeItemsFromSelection("Item 4");
        Assert.assertEquals(lastLogRow, getLogRow(0));
        assertNothingSelected();
    }

    @Test
    public void readOnly_clickToSelect() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectMenuPath("Component", "State", "Readonly");
        List<WebElement> select = getListSelect().findElements(By.tagName("select"));
        Assert.assertEquals(1, select.size());
        Assert.assertNotNull(select.get(0).getAttribute("disabled"));
        String lastLogRow = getLogRow(0);
        selectItem("Item 4");
        Assert.assertEquals(lastLogRow, getLogRow(0));
        assertNothingSelected();
    }

    @Test(expected = ReadOnlyException.class)
    public void readOnly_selectByText() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectMenuPath("Component", "State", "Readonly");
        List<WebElement> select = getListSelect().findElements(By.tagName("select"));
        Assert.assertEquals(1, select.size());
        Assert.assertNotNull(select.get(0).getAttribute("disabled"));
        addItemsToSelection("Item 2");
    }

    @Test(expected = ReadOnlyException.class)
    public void readOnly_deselectByText() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectItem("Item 4");
        selectMenuPath("Component", "State", "Readonly");
        List<WebElement> select = getListSelect().findElements(By.tagName("select"));
        Assert.assertEquals(1, select.size());
        Assert.assertNotNull(select.get(0).getAttribute("disabled"));
        removeItemsFromSelection("Item 4");
    }

    @Test
    public void clickToSelect_reenable() {
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectItem("Item 4");
        assertNothingSelected();
        selectMenuPath("Component", "State", "Enabled");
        selectItem("Item 5");
        Assert.assertEquals("3. Selected: [Item 5]", getLogRow(0));
        selectItem("Item 1");
        Assert.assertEquals("5. Selected: [Item 1]", getLogRow(0));
        addItemsToSelection("Item 2");
        Assert.assertEquals("6. Selected: [Item 1, Item 2]", getLogRow(0));
        removeItemsFromSelection("Item 1");
        Assert.assertEquals("7. Selected: [Item 2]", getLogRow(0));
    }

    @Test
    public void clickToSelect_notReadOnly() {
        selectMenuPath("Component", "State", "Readonly");
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectItem("Item 4");
        assertNothingSelected();
        selectMenuPath("Component", "State", "Readonly");
        selectItem("Item 5");
        Assert.assertEquals("3. Selected: [Item 5]", getLogRow(0));
        selectItem("Item 1");
        Assert.assertEquals("5. Selected: [Item 1]", getLogRow(0));
        addItemsToSelection("Item 2");
        Assert.assertEquals("6. Selected: [Item 1, Item 2]", getLogRow(0));
        removeItemsFromSelection("Item 1");
        Assert.assertEquals("7. Selected: [Item 2]", getLogRow(0));
    }

    @Test
    public void itemCaptionProvider() {
        selectMenuPath("Component", "Item Generator", "Item Caption Generator", "Custom Caption Generator");
        assertItems(20, " Caption");
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
    }
}

