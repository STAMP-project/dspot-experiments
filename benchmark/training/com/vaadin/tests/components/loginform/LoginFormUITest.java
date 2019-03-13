package com.vaadin.tests.components.loginform;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class LoginFormUITest extends SingleBrowserTest {
    @Test
    public void login() {
        openTestURL();
        getUsername().sendKeys("user123");
        getPassword().sendKeys("pass123");
        getLogin().click();
        Assert.assertEquals("User 'user123', password='pass123' logged in", getInfo().getText());
    }
}

