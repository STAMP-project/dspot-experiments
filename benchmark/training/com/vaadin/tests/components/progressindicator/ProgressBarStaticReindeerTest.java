package com.vaadin.tests.components.progressindicator;


import com.vaadin.testbench.elements.ProgressBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class ProgressBarStaticReindeerTest extends MultiBrowserTest {
    @Test
    public void compareScreenshot() throws Exception {
        openTestURL();
        compareScreen($(ProgressBarElement.class).first(), "screen");
    }
}

