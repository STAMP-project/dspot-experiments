package com.vaadin.tests.widgetset.server;


import ClientRpcClass.TEST_COMPONENT_ID;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class ClientRpcClassTest extends MultiBrowserTest {
    @Test
    public void pauseDisplayed() {
        openTestURL();
        WebElement element = getDriver().findElement(By.id(TEST_COMPONENT_ID));
        Assert.assertEquals("pause", element.getText());
    }
}

