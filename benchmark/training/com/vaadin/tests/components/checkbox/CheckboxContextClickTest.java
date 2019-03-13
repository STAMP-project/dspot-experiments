package com.vaadin.tests.components.checkbox;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class CheckboxContextClickTest extends MultiBrowserTest {
    @Test
    public void contextClickCheckboxAndText() {
        openTestURL();
        CheckBoxElement checkbox = $(CheckBoxElement.class).first();
        Assert.assertEquals("checked", checkbox.getValue());
        WebElement input = checkbox.findElement(By.xpath("input"));
        WebElement label = checkbox.findElement(By.xpath("label"));
        contextClickElement(input);
        Assert.assertEquals("1. checkbox context clicked", getLogRow(0));
        Assert.assertEquals("checked", checkbox.getValue());
        contextClickElement(label);
        Assert.assertEquals("2. checkbox context clicked", getLogRow(0));
        Assert.assertEquals("checked", checkbox.getValue());
    }
}

