package com.vaadin.tests.components.tabsheet;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class PreventTabChangeTest extends MultiBrowserTest {
    @Test
    public void preventTabChange() throws Exception {
        openTestURL();
        clickTab(1);
        clickTab(2);
        Thread.sleep(2000);
        assertTabSelected(2);
        Assert.assertEquals("Tab 3 contents", getSelectedTabContent().getText());
        clickTab(0);
        clickTab(2);
        assertTabSelected(0);
        Assert.assertEquals("Tab 1 contents", getSelectedTabContent().getText());
    }
}

