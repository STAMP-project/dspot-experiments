package com.vaadin.tests.components.window;


import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;


public class ShortcutInWindowTest extends SingleBrowserTest {
    @Test
    public void shortcutFlushesActiveField() {
        openTestURL();
        TextFieldElement tf = $(TextFieldElement.class).first();
        tf.sendKeys(("foo" + (Keys.ENTER)));
        Assert.assertEquals("2. Submitted value: foo", getLogRow(0));
    }
}

