package com.vaadin.tests.components.button;


import ButtonClick.SUCCESS_TEXT;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class ButtonClickTest extends MultiBrowserTest {
    @Test
    public void buttonMouseDownOutOverUp() {
        openTestURL();
        WebElement clickedButton = vaadinElement("/VVerticalLayout[0]/VButton[0]");
        WebElement visitedButton = vaadinElement("/VVerticalLayout[0]/VButton[1]");
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(clickedButton).clickAndHold().moveToElement(visitedButton).moveToElement(clickedButton).release().perform();
        Assert.assertEquals(SUCCESS_TEXT, vaadinElement("/VVerticalLayout[0]/VLabel[0]").getText());
    }
}

