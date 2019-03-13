package com.vaadin.tests.components.nativebutton;


import NativeButtonDisableOnClick.UPDATED_CAPTION;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class NativeButtonDisableOnClickTest extends MultiBrowserTest {
    @Test
    public void testButtonIsDisabled() {
        openTestURL();
        WebElement button = findElement(By.id("buttonId"));
        Assert.assertEquals(true, button.isEnabled());
        button.click();
        Assert.assertEquals(UPDATED_CAPTION, button.getText());
        Assert.assertEquals(false, button.isEnabled());
        button.click();
        Assert.assertEquals(UPDATED_CAPTION, button.getText());
    }
}

