package com.vaadin.tests.components.abstractcomponent;


import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.ParameterizedTB3Runner;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ParameterizedTB3Runner.class)
public class TooltipStylingTest extends SingleBrowserTest {
    private String theme;

    @Test
    public void tooltipStyling() throws IOException {
        openTestURL(("theme=" + (theme)));
        $(LabelElement.class).id("default").showTooltip();
        compareScreen("default");
        $(LabelElement.class).id("html").showTooltip();
        compareScreen("html");
    }
}

