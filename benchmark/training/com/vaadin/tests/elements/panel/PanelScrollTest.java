package com.vaadin.tests.elements.panel;


import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class PanelScrollTest extends MultiBrowserTest {
    private static final int SCROLL_VALUE = 300;

    @Test
    public void testScrollLeft() throws InterruptedException {
        openTestURL();
        PanelElement panel = $(PanelElement.class).get(0);
        panel.scrollLeft(PanelScrollTest.SCROLL_VALUE);
        Assert.assertEquals(PanelScrollTest.SCROLL_VALUE, getScrollLeftValue(panel));
    }

    @Test
    public void testScrollTop() {
        openTestURL();
        PanelElement panel = $(PanelElement.class).get(0);
        panel.scroll(PanelScrollTest.SCROLL_VALUE);
        Assert.assertEquals(PanelScrollTest.SCROLL_VALUE, getScrollTopValue(panel));
    }
}

