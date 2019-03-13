package com.vaadin.tests.components.tabsheet;


import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class VetoTabChangeTest extends SingleBrowserTest {
    @Test
    public void testReselectTabAfterVeto() {
        openTestURL();
        TabSheetElement tabSheet = $(TabSheetElement.class).first();
        Assert.assertEquals("Tab 1 should be there by default", "Tab 1", getTabContent(tabSheet));
        tabSheet.openTab(1);
        Assert.assertEquals("Tab should not have changed", "Tab 1", getTabContent(tabSheet));
        tabSheet.openTab(0);
        Assert.assertEquals("Tab should still be there", "Tab 1", getTabContent(tabSheet));
    }
}

