package com.vaadin.tests.components.tabsheet;


import Keys.ARROW_RIGHT;
import Keys.SPACE;
import TabsheetScrolling.SELECT_FIRST;
import TabsheetScrolling.SELECT_LAST;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TabsheetScrollingTest extends MultiBrowserTest {
    @Test
    public void keyboardScrolling() {
        openTestURL();
        getTab(1).click();
        for (int i = 0; i < 10; i++) {
            sendKey(ARROW_RIGHT);
        }
        sendKey(SPACE);
        Assert.assertEquals("Hide this tab (21)", getHideButtonText());
    }

    @Test
    public void serverChangeShouldShowTab() {
        openTestURL();
        $(ButtonElement.class).id(SELECT_LAST).click();
        TabSheetElement tabsheetFixed = $(TabSheetElement.class).first();
        Assert.assertTrue("Select last should scroll last tab into view", isTabVisible(tabsheetFixed, "Tab 99"));
        $(ButtonElement.class).id(SELECT_FIRST).click();
        Assert.assertTrue("Select first should scroll first tab into view", isTabVisible(tabsheetFixed, "Tab 1"));
    }
}

