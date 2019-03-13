package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Verifies SlowComboBox filtering works when user inputs text. Also verifies
 * pagination works when the matching results number more than those that can be
 * displayed.
 *
 * @author Vaadin Ltd
 */
public class ComboBoxSlowTest extends MultiBrowserTest {
    @Test
    public void testZeroMatches() throws InterruptedException {
        clickComboBoxTextArea();
        sleep(250);
        typeString("1");
        Assert.assertEquals(0, getNumberOfSuggestions());
    }

    @Test
    public void testElevenMatchesAndPaging() throws InterruptedException {
        clickComboBoxTextArea();
        sleep(250);
        typeString("Item 12");
        final WebElement popup = driver.findElement(By.className("v-filterselect-suggestpopup"));
        List<WebElement> filteredItems = getFilteredItems(popup);
        Assert.assertEquals("unexpected amount of suggestions found on first page", 10, filteredItems.size());
        Assert.assertEquals("wrong filtering result", "Item 12", filteredItems.get(0).getText());
        Assert.assertEquals("wrong filtering result", "Item 128", filteredItems.get(9).getText());
        Assert.assertTrue(isPagingActive());
        goToNextPage();
        waitUntil(( input) -> {
            List<WebElement> items = getFilteredItems(popup);
            return ((items.size()) == 1) && ("Item 129".equals(items.get(0).getText()));
        });
    }

    @Test
    public void testTwoMatchesNoPaging() {
        clickComboBoxTextArea();
        typeString("Item 100");
        Assert.assertFalse(isPagingActive());
        WebElement popup = driver.findElement(By.className("v-filterselect-suggestpopup"));
        List<WebElement> filteredItems = getFilteredItems(popup);
        Assert.assertEquals("unexpected amount of suggestions found", 2, filteredItems.size());
        Assert.assertEquals("wrong filtering result", "Item 100", filteredItems.get(0).getText());
        Assert.assertEquals("wrong filtering result", "Item 1000", filteredItems.get(1).getText());
    }
}

