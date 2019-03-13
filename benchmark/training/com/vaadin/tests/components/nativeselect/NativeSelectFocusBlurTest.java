package com.vaadin.tests.components.nativeselect;


import Keys.ARROW_UP;
import Keys.ENTER;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class NativeSelectFocusBlurTest extends MultiBrowserTest {
    @Test
    public void focusBlurEvents() {
        openTestURL();
        NativeSelectElement nativeSelect = $(NativeSelectElement.class).first();
        nativeSelect.click();
        // Focus event is fired
        Assert.assertTrue(logContainsText("1. Focus Event"));
        List<TestBenchElement> options = nativeSelect.getOptions();
        options.get(1).click();
        // No any new event
        Assert.assertFalse(logContainsText("2."));
        // click on log label => blur
        $(LabelElement.class).first().click();
        // blur event is fired
        Assert.assertTrue(logContainsText("2. Blur Event"));
        nativeSelect.click();
        // Focus event is fired
        Assert.assertTrue(logContainsText("3. Focus Event"));
        nativeSelect.sendKeys(ARROW_UP, ENTER);
        // No any new event
        Assert.assertFalse(logContainsText("4."));
    }
}

