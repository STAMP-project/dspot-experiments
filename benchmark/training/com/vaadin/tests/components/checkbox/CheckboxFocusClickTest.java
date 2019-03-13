package com.vaadin.tests.components.checkbox;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class CheckboxFocusClickTest extends MultiBrowserTest {
    @Test
    public void contextClickCheckboxAndText() {
        openTestURL();
        CheckBoxElement checkbox = $(CheckBoxElement.class).first();
        Assert.assertEquals("checked", checkbox.getValue());
        WebElement label = checkbox.findElement(By.xpath("label"));
        label.click();
        Assert.assertEquals("1. checkbox focused", getLogRow(0));
    }
}

