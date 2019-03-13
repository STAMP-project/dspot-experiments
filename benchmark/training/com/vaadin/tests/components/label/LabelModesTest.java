package com.vaadin.tests.components.label;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class LabelModesTest extends MultiBrowserTest {
    @Test
    public void testLabelModes() throws Exception {
        openTestURL();
        compareScreen("labelmodes");
    }
}

