package com.vaadin.tests.components.formlayout;


import com.vaadin.testbench.elements.FormLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;


/**
 * Test for form layout click listener.
 *
 * @author Vaadin Ltd
 */
public class FormLayoutClickListenerTest extends MultiBrowserTest {
    @Test
    public void layoutClickListener_clickOnLayout_childAndClickedComponentsAreNull() {
        FormLayoutElement element = $(FormLayoutElement.class).first();
        Actions actions = new Actions(getDriver());
        actions.moveByOffset(((element.getLocation().getX()) + 2), ((element.getLocation().getY()) + 2)).click().build().perform();
        waitForLogRowUpdate();
        Assert.assertEquals("Source component for click event must be form", "3. Source component: form", getLogRow(0));
        Assert.assertEquals("Clicked component for click event must be null", "2. Clicked component: null", getLogRow(1));
        Assert.assertEquals("Child component for click event must be null", "1. Child component: null", getLogRow(2));
    }

    @Test
    public void layoutClickListener_clickOnLabel_lableIsChildAndClickedComponent() {
        findElement(By.id("label")).click();
        waitForLogRowUpdate();
        Assert.assertEquals("Source component for click event must be form", "3. Source component: form", getLogRow(0));
        Assert.assertEquals("Clicked component for click event must be label", "2. Clicked component: label", getLogRow(1));
        Assert.assertEquals("Child component for click event must be label", "1. Child component: label", getLogRow(2));
    }
}

