package com.vaadin.tests.components.combobox;


import Keys.ARROW_DOWN;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.Keys;


/**
 * Test for identical item captions in ComboBox.
 *
 * @author Vaadin Ltd
 */
public class ComboBoxIdenticalItemsTest extends MultiBrowserTest {
    @Test
    public void identicalItemsKeyboardTest() {
        openTestURL();
        int delay = (BrowserUtil.isPhantomJS(getDesiredCapabilities())) ? 500 : 0;
        ComboBoxElement combobox = $(ComboBoxElement.class).first();
        combobox.sendKeys(delay, ARROW_DOWN, getReturn());
        waitUntilLogText("1. Item one-1 selected");
        Keys[] downDownEnter = new Keys[]{ Keys.ARROW_DOWN, Keys.ARROW_DOWN, getReturn() };
        combobox.sendKeys(delay, downDownEnter);
        waitUntilLogText("2. Item one-2 selected");
        combobox.sendKeys(delay, downDownEnter);
        waitUntilLogText("3. Item two selected");
        combobox.sendKeys(delay, new Keys[]{ Keys.ARROW_UP, Keys.ARROW_UP, Keys.ARROW_UP, getReturn() });
        waitUntilLogText("4. Item one-1 selected");
    }
}

