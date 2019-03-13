package com.vaadin.tests.components.ui;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class UITabIndexTest extends MultiBrowserTest {
    @Test
    public void testTabIndexOnUIRoot() throws Exception {
        openTestURL();
        assertTabIndex("1");
        $(ButtonElement.class).first().click();
        assertTabIndex("-1");
        $(ButtonElement.class).get(1).click();
        assertTabIndex("0");
        $(ButtonElement.class).get(2).click();
        assertTabIndex("1");
    }
}

