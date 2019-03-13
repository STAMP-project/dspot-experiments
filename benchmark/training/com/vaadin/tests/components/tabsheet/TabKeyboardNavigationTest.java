package com.vaadin.tests.components.tabsheet;


import Keys.ARROW_LEFT;
import Keys.ARROW_RIGHT;
import Keys.SPACE;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;
import org.openqa.selenium.By;


/**
 * Add TB3 test as the TB2 one failed on keyboard events.
 *
 * @author Vaadin Ltd
 */
public class TabKeyboardNavigationTest extends MultiBrowserTest {
    @Test
    public void testFocus() throws IOException, InterruptedException {
        openTestURL();
        click(1);
        sendKeys(1, ARROW_RIGHT);
        assertSheet(1);
        sendKeys(2, SPACE);
        assertSheet(2);
        compareScreen("tab2");
        sendKeys(2, ARROW_RIGHT);
        sendKeys(3, ARROW_RIGHT);
        assertSheet(2);
        sendKeys(5, SPACE);
        assertSheet(5);
        compareScreen("skip-disabled-to-tab5");
        TestBenchElement addTabButton = ((TestBenchElement) (getDriver().findElements(By.className("v-button")).get(0)));
        click(addTabButton);
        click(5);
        sendKeys(5, ARROW_RIGHT);
        assertSheet(5);
        sendKeys(6, SPACE);
        assertSheet(6);
        click(addTabButton);
        click(addTabButton);
        click(addTabButton);
        click(addTabButton);
        click(addTabButton);
        click(addTabButton);
        click(8);
        compareScreen("click-tab-8");
        sendKeys(8, ARROW_RIGHT);
        sendKeys(9, SPACE);
        click(9);
        compareScreen("tab-9");
        sendKeys(9, ARROW_RIGHT);
        Thread.sleep(TabKeyboardNavigationTest.DELAY);
        sendKeys(10, ARROW_RIGHT);
        // Here PhantomJS used to fail. Or when accessing tab2. The fix was to
        // call the elem.click(x, y) using the (x, y) position instead of the
        // elem.click() without any arguments.
        sendKeys(11, ARROW_RIGHT);
        assertSheet(9);
        sendKeys(12, SPACE);
        assertSheet(12);
        compareScreen("scrolled-right-to-tab-12");
        click(5);
        sendKeys(5, ARROW_LEFT);
        // Here IE8 used to fail. A hidden <div> in IE8 would have the bounds of
        // it's parent, and when trying to see in which direction to scroll
        // (left or right) to make the key selected tab visible, the
        // VTabSheet.scrollIntoView(Tab) used to check first whether the tab
        // isClipped. On IE8 this will always return true for both hidden tabs
        // on the left and clipped tabs on the right. So instead of going to
        // left, it'll search all the way to the right.
        sendKeys(3, ARROW_LEFT);
        sendKeys(2, ARROW_LEFT);
        assertSheet(5);
        sendKeys(1, SPACE);
        assertSheet(1);
        compareScreen("scrolled-left-to-tab-1");
    }

    /* Delay for PhantomJS. */
    private static final int DELAY = 10;
}

