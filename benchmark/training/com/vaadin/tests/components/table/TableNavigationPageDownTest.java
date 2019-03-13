package com.vaadin.tests.components.table;


import Keys.END;
import Keys.HOME;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Tests that navigation with PageDown/PageUp/Home/End in Table works
 *
 * @author Vaadin Ltd
 */
public class TableNavigationPageDownTest extends MultiBrowserTest {
    private static final int ROW_NUMBER = 50;

    private int lowerWrapperY = -1;

    private int pageHeight = -1;

    private int rowHeight = -1;

    private WebElement wrapper;

    @Test
    public void navigatePageDown() {
        // Scroll to a point where you can reach the bottom with a couple of
        // page downs.
        // Can't use v-table-body height because lower rows haven't been
        // fetched yet.
        testBenchElement(wrapper).scroll((((TableNavigationPageDownTest.ROW_NUMBER) * (rowHeight)) - ((int) (2.8 * (pageHeight)))));
        waitForScrollToFinish();
        getLastVisibleRow().click();
        sendPageDownUntilBottomIsReached();
        Assert.assertEquals("Last table row should be visible", ((TableNavigationPageDownTest.ROW_NUMBER) - 1), getLastVisibleRowNumber());
    }

    @Test
    public void navigatePageUp() {
        // Scroll to a point where you can reach the top with a couple of page
        // ups.
        testBenchElement(wrapper).scroll(((int) (2.8 * (pageHeight))));
        waitForScrollToFinish();
        getFirstVisibleRow().click();
        sendPageUpUntilTopIsReached();
        Assert.assertEquals("First table row should be visible", 0, getRowNumber(getFirstVisibleRow()));
    }

    @Test
    public void navigateEndAndHome() {
        getLastVisibleRow().click();
        new org.openqa.selenium.interactions.Actions(driver).sendKeys(END).build().perform();
        waitForScrollToFinish();
        Assert.assertEquals("Last table row should be visible", ((TableNavigationPageDownTest.ROW_NUMBER) - 1), getRowNumber(getLastVisibleRow()));
        new org.openqa.selenium.interactions.Actions(driver).sendKeys(HOME).build().perform();
        waitForScrollToFinish();
        Assert.assertEquals("First table row should be visible", 0, getRowNumber(getFirstVisibleRow()));
    }
}

