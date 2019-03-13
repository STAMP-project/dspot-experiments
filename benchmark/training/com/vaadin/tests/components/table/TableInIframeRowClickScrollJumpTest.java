package com.vaadin.tests.components.table;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * For testing that UI scroll does not jump back to up when: 1. UI is in iframe
 * 2. the window scrolled down 3. and table is clicked
 *
 * @author Vaadin Ltd
 */
public class TableInIframeRowClickScrollJumpTest extends MultiBrowserTest {
    private static final String TEST_URL = "statictestfiles/TableInIframeRowClickScrollJumpTest.html";

    @Test
    public void testRowClicking_WhenScrolledDown_shouldMaintainScrollPosition() throws InterruptedException {
        System.out.println(((">>>" + (getBaseURL())) + (TableInIframeRowClickScrollJumpTest.TEST_URL)));
        driver.get(getUrl());
        // using non-standard way because of iframe
        sleep(4000);
        // make sure we are in the "main content"
        driver.switchTo().defaultContent();
        sleep(2000);
        switchIntoIframe();
        // using non-standard way because of iframe
        waitForElementVisible(By.id("scroll-button"));
        ButtonElement scrollbutton = $(ButtonElement.class).id("scroll-button");
        scrollbutton.click();
        // using non-standard way because of iframe
        sleep(1000);
        Long scrollPosition = getWindowsScrollPosition();
        MatcherAssert.assertThat((("Scroll position should be greater than 100 (it was " + scrollPosition) + ")"), (scrollPosition > 100));
        TableElement table = $(TableElement.class).first();
        table.getRow(13).getCell(0).click();
        // using non-standard way because of iframe
        sleep(1000);
        Long scrollPosition2 = getWindowsScrollPosition();
        MatcherAssert.assertThat(((("Scroll position should stay about the same. Old was " + scrollPosition) + " and new one ") + scrollPosition2), ((Math.abs((scrollPosition - scrollPosition2))) < 10));
    }
}

