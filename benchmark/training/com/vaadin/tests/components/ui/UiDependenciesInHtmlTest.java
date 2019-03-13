package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class UiDependenciesInHtmlTest extends SingleBrowserTest {
    @Test
    public void testUiDependencisInHtml() {
        openTestURL();
        String statusText = findElement(By.id("statusBox")).getText();
        Assert.assertEquals("Script loaded before vaadinBootstrap.js: true\nStyle tag before vaadin theme: true\nStyle tags in correct order: true", statusText);
    }
}

