package com.vaadin.tests.components.tabsheet;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


/**
 * Tests that when closing the last tab on a TabSheet, another tab gets selected
 * with no error. Only the last tab should be visible, so the actual TabSheet
 * width should be small.
 *
 * @author Vaadin Ltd
 */
public class TabSheetCloseTest extends MultiBrowserTest {
    private static final String TAB_CLOSE = "//span[@class = 'v-tabsheet-caption-close']";

    private static final String LAST_TAB = "//*[@id = 'tab2']/div/div";

    private static final String SCROLLER_NEXT = "//button[@class = 'v-tabsheet-scrollerNext']";

    private static final String FIRST_TAB = "//*[@id = 'tab0']";

    private static final String SECOND_TAB = "//*[@id = 'tab1']";

    @Test
    public void testClosingOfLastTab() throws Exception {
        openTestURL();
        // Click next button twice to get to the last tab
        findElement(By.xpath(TabSheetCloseTest.SCROLLER_NEXT)).click();
        findElement(By.xpath(TabSheetCloseTest.SCROLLER_NEXT)).click();
        findElement(By.xpath(TabSheetCloseTest.LAST_TAB)).click();
        // Closing last tab will take back to the second tab. Closing that
        // will leave the first tab visible.
        findElements(By.xpath(TabSheetCloseTest.TAB_CLOSE)).get(2).click();
        Assert.assertTrue(findElement(By.xpath(TabSheetCloseTest.SECOND_TAB)).isDisplayed());
        findElements(By.xpath(TabSheetCloseTest.TAB_CLOSE)).get(1).click();
        Assert.assertTrue(findElement(By.xpath(TabSheetCloseTest.FIRST_TAB)).isDisplayed());
    }
}

