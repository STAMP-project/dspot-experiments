package com.vaadin.tests.components.window;


import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class WindowCaptionTest extends SingleBrowserTest {
    private WindowElement htmlWindow;

    private WindowElement textWindow;

    @Test
    public void htmlCaption() {
        Assert.assertEquals("HtmlWindow's caption didn't match,", "This may or may not be red", htmlWindow.getCaption());
        Assert.assertEquals("TextWindow's caption didn't match,", "<font style='color: red;'>This may or may not be red</font>", textWindow.getCaption());
    }

    @Test
    public void textCaption() {
        clickButton("Plain text");
        ensureCaptionsEqual("This is just text");
    }

    @Test
    public void nullCaption() {
        clickButton("Null");
        ensureCaptionsEqual("");
    }

    @Test
    public void emptyCaption() {
        clickButton("Empty");
        ensureCaptionsEqual("");
    }
}

