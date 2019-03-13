package com.vaadin.tests.components.tabsheet;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Tests removing tabs that have been scrolled out of view. This should cause no
 * change to the scroll position.
 *
 * @author Vaadin Ltd
 */
public class TabSheetScrollOnTabCloseTest extends MultiBrowserTest {
    @Test
    public void testScrollPositionAfterClosing() throws Exception {
        openTestURL();
        TabSheetElement ts = $(TabSheetElement.class).first();
        WebElement tabSheetScroller = ts.findElement(By.className("v-tabsheet-scrollerNext"));
        // scroll to the right
        for (int i = 0; i < 4; i++) {
            tabSheetScroller.click();
        }
        // check that tab 4 is the first visible tab
        checkDisplayedStatus(ts, "tab3", false);
        checkDisplayedStatus(ts, "tab4", true);
        // remove tabs from the left, check that tab4 is still the first visible
        // tab
        for (int i = 0; i < 4; i++) {
            $(ButtonElement.class).get(i).click();
            checkDisplayedStatus(ts, ("tab3" + i), false);
            checkDisplayedStatus(ts, "tab4", true);
            checkDisplayedStatus(ts, "tab6", true);
        }
        // remove tabs from the right and check scroll position
        for (int i = 7; i < 10; i++) {
            $(ButtonElement.class).get(i).click();
            checkFirstTab(ts, "tab4");
            checkDisplayedStatus(ts, "tab6", true);
        }
    }
}

