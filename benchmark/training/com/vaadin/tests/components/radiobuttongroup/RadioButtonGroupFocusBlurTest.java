package com.vaadin.tests.components.radiobuttongroup;


import Keys.ARROW_DOWN;
import Keys.TAB;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class RadioButtonGroupFocusBlurTest extends MultiBrowserTest {
    @Test
    public void focusBlurEvents() {
        openTestURL();
        RadioButtonGroupElement radioButtonGroup = $(RadioButtonGroupElement.class).first();
        List<WebElement> radioButtons = radioButtonGroup.findElements(By.tagName("input"));
        radioButtonGroup.selectByText("1");
        // Focus event is fired
        Assert.assertTrue(logContainsText("1. Focus Event"));
        radioButtonGroup.selectByText("2");
        // click on the second radio button doesn't fire anything
        Assert.assertFalse(logContainsText("2."));
        // move the cursor to the middle of the first element,
        // offset to the middle of the two and perform click
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(radioButtons.get(0)).moveByOffset(0, (((radioButtons.get(1).getLocation().y) - (radioButtons.get(0).getLocation().y)) / 2)).click().build().perform();
        // no new events
        Assert.assertFalse(logContainsText("2."));
        // click to label of a radio button
        radioButtonGroup.findElements(By.tagName("label")).get(2).click();
        // no new events
        Assert.assertFalse(logContainsText("2."));
        // click on log label => blur
        $(LabelElement.class).first().click();
        // blur event is fired
        Assert.assertTrue(logContainsText("2. Blur Event"));
        radioButtonGroup.selectByText("4");
        // Focus event is fired
        Assert.assertTrue(logContainsText("3. Focus Event"));
        // move keyboard focus to the next radio button
        radioButtons.get(3).sendKeys(ARROW_DOWN);
        // no new events
        Assert.assertFalse(logContainsText("4."));
        // select the next radio button
        radioButtons.get(4).sendKeys(TAB);
        // focus has gone away
        waitUntil(( driver) -> logContainsText("4. Blur Event"), 5);
    }
}

