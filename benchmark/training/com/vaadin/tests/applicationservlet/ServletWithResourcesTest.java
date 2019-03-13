package com.vaadin.tests.applicationservlet;


import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class ServletWithResourcesTest extends SingleBrowserTest {
    @Test
    public void servletServesResources() {
        openTestURL();
        Assert.assertEquals("Enabled", $(CheckBoxElement.class).first().getCaption());
        List<WebElement> links = findElements(By.xpath("//head/link"));
        for (WebElement link : links) {
            String href = link.getAttribute("href");
            Assert.assertTrue((("href '" + href) + "' should contain '/servlet-with-resources/VAADIN'"), href.contains("/servlet-with-resources/VAADIN"));
        }
        List<WebElement> scripts = findElements(By.xpath("//head/script"));
        for (WebElement script : scripts) {
            String src = script.getAttribute("src");
            Assert.assertTrue((("src '" + src) + "' should contain '/servlet-with-resources/VAADIN'"), src.contains("/servlet-with-resources/VAADIN"));
        }
    }
}

