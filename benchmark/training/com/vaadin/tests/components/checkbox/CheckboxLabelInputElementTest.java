package com.vaadin.tests.components.checkbox;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class CheckboxLabelInputElementTest extends MultiBrowserTest {
    @Test
    public void contextClickCheckboxAndText() {
        openTestURL();
        CheckBoxElement checkBoxElement = $(CheckBoxElement.class).first();
        WebElement labelElem = checkBoxElement.findElement(By.tagName("label"));
        WebElement inputElem = checkBoxElement.findElement(By.tagName("input"));
        Assert.assertEquals("my-label-class", labelElem.getAttribute("class"));
        Assert.assertEquals("my-input-class", inputElem.getAttribute("class"));
        Assert.assertTrue(("The Checkbox Widget should not contain the classes that are " + "defined as style names for the input or label."), ((!(checkBoxElement.getAttribute("class").contains("my-label-class"))) && (!(checkBoxElement.getAttribute("class").contains("my-input-class")))));
        $(ButtonElement.class).caption("add-style").first().click();
        Assert.assertEquals("my-label-class later-applied-label-class", labelElem.getAttribute("class"));
        Assert.assertEquals("my-input-class later-applied-input-class", inputElem.getAttribute("class"));
        Assert.assertTrue(("The Checkbox Widget should not contain the classes that are " + "defined as style names for the input or label."), ((!(checkBoxElement.getAttribute("class").contains("later-applied-label-class"))) && (!(checkBoxElement.getAttribute("class").contains("later-applied-input-class")))));
        $(ButtonElement.class).caption("remove-style").first().click();
        Assert.assertEquals("later-applied-label-class", labelElem.getAttribute("class"));
        Assert.assertEquals("later-applied-input-class", inputElem.getAttribute("class"));
        $(ButtonElement.class).caption("remove-style-2").first().click();
        Assert.assertTrue(labelElem.getAttribute("class").isEmpty());
        Assert.assertTrue(inputElem.getAttribute("class").isEmpty());
    }
}

