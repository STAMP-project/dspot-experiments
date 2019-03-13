package com.vaadin.tests.elements.tabsheet;


import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;


/**
 * Tests selecting tabs in a nested tab sheet. TabSheetElement.openTab should
 * not open tabs that are in a tab sheet that is itself contained in the current
 * tab sheet. Only the tabs in the current tab sheet should be candidates for
 * selection.
 */
public class NestedTabSheetTest extends MultiBrowserTest {
    @Test
    public void openOuterTabInNestedTabSheet() {
        openTestURL();
        // Open a tab in the outer tab sheet. No errors should occur.
        TabSheetElement outer = $(TabSheetElement.class).first();
        outer.openTab("Tab 2");
        checkForSelectedTab(outer, "Tab 2");
    }

    @Test
    public void openInnerTabInNestedTabSheet() {
        openTestURL();
        // Properly open a tab that is in an inner tab sheet.
        TabSheetElement outer = $(TabSheetElement.class).first();
        outer.openTab("Tab 3");
        TabSheetElement thirdInner = outer.$(TabSheetElement.class).first();
        thirdInner.openTab("Tab 3.2");
        checkForSelectedTab(thirdInner, "Tab 3.2");
    }

    @Test
    public void testThatOpeningInnerTabFails() {
        openTestURL();
        // Attempt to improperly open an inner tab. This should fail.
        TabSheetElement outer = $(TabSheetElement.class).first();
        try {
            outer.openTab("Tab 1.3");
        } catch (NoSuchElementException e) {
            // openTab may throw an exception when the tab is not found.
        }
        // Check that inner tab 1.3 is not selected.
        TabSheetElement inner = outer.$(TabSheetElement.class).first();
        Assert.assertFalse("Tab 1.3 is selected, but it should not be.", isTabSelected(inner, "Tab 1.3"));
    }
}

