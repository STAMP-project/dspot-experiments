package com.vaadin.tests.components.orderedlayout;


import HorizontalLayoutFullsizeContentWithErrorMsg.BUTTON_ID;
import HorizontalLayoutFullsizeContentWithErrorMsg.FIELD_ID;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;


public class HorizontalLayoutFullsizeContentWithErrorMsgTest extends MultiBrowserTest {
    @Test
    public void test() {
        openTestURL();
        WebElement element = getDriver().findElement(By.id(FIELD_ID));
        Point location = element.getLocation();
        WebElement errorToggleButton = getDriver().findElement(By.id(BUTTON_ID));
        errorToggleButton.click();
        Assert.assertEquals(location, element.getLocation());
        errorToggleButton.click();
        Assert.assertEquals(location, element.getLocation());
    }
}

