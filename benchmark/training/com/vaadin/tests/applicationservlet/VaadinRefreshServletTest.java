package com.vaadin.tests.applicationservlet;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class VaadinRefreshServletTest extends SingleBrowserTest {
    @Test
    public void redirectWorksWithContextPath() {
        getDriver().get(((getBaseURL()) + "/vaadinrefresh/"));
        waitUntil((WebDriver d) -> "Please login".equals(findElement(By.tagName("body")).getText()));
        Assert.assertEquals(((getBaseURL()) + "/statictestfiles/login.html"), getDriver().getCurrentUrl());
    }
}

