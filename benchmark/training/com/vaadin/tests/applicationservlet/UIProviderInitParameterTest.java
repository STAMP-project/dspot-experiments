package com.vaadin.tests.applicationservlet;


import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class UIProviderInitParameterTest extends MultiBrowserTest {
    @Test
    public void testDefault() {
        // Test that UI parameter is used by default
        openTestURL();
        List<LabelElement> labels = $(LabelElement.class).all();
        Assert.assertTrue("unexpected amount of labels", ((labels.size()) > 2));
        LabelElement label = labels.get(((labels.size()) - 1));
        String message = "Tests whether the testing server is run with assertions enabled.";
        Assert.assertEquals("unexpected text found", message, label.getText());
    }

    @Test
    public void testExtended() {
        // Test that UIProvider parameter is more important than UI parameter
        driver.get(getTestUrl().replace("uiprovider", "uiprovider/test"));
        LabelElement label = $(LabelElement.class).first();
        String message = "Test for basic JavaScript component functionality.";
        Assert.assertEquals("unexpected text found", message, label.getText());
    }
}

