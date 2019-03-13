package com.vaadin.tests.components.ui;


import Keys.CONTROL;
import Keys.ENTER;
import Keys.SHIFT;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;


public class TextAreaEventPropagationModifierKeysTest extends MultiBrowserTest {
    @Test
    public void textAreaShiftEnterEventPropagation() throws InterruptedException {
        openTestURL();
        WebElement textArea = $(TextAreaElement.class).first();
        Actions builder = new Actions(driver);
        builder.click(textArea);
        builder.sendKeys(textArea, "first line asdf");
        builder.sendKeys(Keys.chord(SHIFT, ENTER));
        builder.sendKeys(textArea, "second line jkl;");
        builder.perform();
        // Should have triggered shortcut
        Assert.assertEquals("1. Shift-Enter button pressed", getLogRow(0));
    }

    @Test
    public void textAreaCtrlEnterEventPropagation() throws InterruptedException {
        openTestURL();
        WebElement textArea = $(TextAreaElement.class).first();
        Actions builder = new Actions(driver);
        builder.click(textArea);
        builder.sendKeys(textArea, "first line asdf");
        builder.sendKeys(Keys.chord(CONTROL, ENTER));
        builder.sendKeys(textArea, "second line jkl;");
        builder.perform();
        // Should have triggered shortcut
        Assert.assertEquals("1. Ctrl-Enter button pressed", getLogRow(0));
    }
}

