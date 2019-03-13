package com.vaadin.tests.components.combobox;


import Keys.DOWN;
import Keys.UP;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * When pressed down key, while positioned on the last item - should show next
 * page and focus on the first item of the next page.
 */
public class ComboBoxScrollingWithArrowsTest extends MultiBrowserTest {
    private final int PAGESIZE = 10;

    @Test
    public void scrollDownArrowKeyTest() throws InterruptedException {
        WebElement dropDownComboBox = getDropDown();
        for (int i = 0; i < (PAGESIZE); i++) {
            dropDownComboBox.sendKeys(DOWN);
        }
        MatcherAssert.assertThat(getSelectedItemText(), Matchers.is(("item " + (PAGESIZE))));// item 10

    }

    @Test
    public void scrollUpArrowKeyTest() throws InterruptedException {
        WebElement dropDownComboBox = getDropDown();
        for (int i = 0; i < (PAGESIZE); i++) {
            dropDownComboBox.sendKeys(DOWN);
        }
        // move to one item up
        waitUntilNextPageIsVisible();
        dropDownComboBox.sendKeys(UP);
        // item 9
        MatcherAssert.assertThat(getSelectedItemText(), Matchers.is(("item " + ((PAGESIZE) - 1))));
    }
}

