package com.vaadin.v7.tests.core;


import SpecialCharactersEncodingUI.textWithZwnj;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class SpecialCharactersEncodingUITest extends SingleBrowserTest {
    @Test
    public void checkEncoding() {
        openTestURL();
        String textFieldValue = $(TextFieldElement.class).id("textfield").getValue();
        Assert.assertEquals(textWithZwnj, textFieldValue);
        LabelElement label = $(LabelElement.class).id("label");
        String labelValue = getHtml(label);// getText() strips some characters

        Assert.assertEquals(textWithZwnj, labelValue);
        MenuBarElement menubar = $(MenuBarElement.class).first();
        WebElement menuItem = menubar.findElement(By.className("v-menubar-menuitem-caption"));
        Assert.assertEquals(textWithZwnj, getHtml(menuItem));
    }
}

