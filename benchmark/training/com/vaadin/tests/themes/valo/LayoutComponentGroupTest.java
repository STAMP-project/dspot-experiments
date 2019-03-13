package com.vaadin.tests.themes.valo;


import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class LayoutComponentGroupTest extends SingleBrowserTest {
    @Test
    public void renderedWithoutRoundedBordersInTheMiddle() throws Exception {
        openTestURL();
        compareScreen($(VerticalLayoutElement.class).id("container"), "buttongroups");
    }
}

