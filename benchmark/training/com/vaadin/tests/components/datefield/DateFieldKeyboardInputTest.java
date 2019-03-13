package com.vaadin.tests.components.datefield;


import Keys.RETURN;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class DateFieldKeyboardInputTest extends MultiBrowserTest {
    @Test
    public void testValueChangeEvent() {
        openTestURL();
        WebElement dateFieldText = $(DateFieldElement.class).first().findElement(By.tagName("input"));
        dateFieldText.clear();
        int numLabelsBeforeUpdate = $(LabelElement.class).all().size();
        dateFieldText.sendKeys("20.10.2013", RETURN);
        int numLabelsAfterUpdate = $(LabelElement.class).all().size();
        Assert.assertTrue("Changing the date failed.", (numLabelsAfterUpdate == (numLabelsBeforeUpdate + 1)));
    }
}

