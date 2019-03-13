package com.vaadin.tests.applicationservlet;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class NoApplicationClassTest extends SingleBrowserTest {
    @Test
    public void testInvalidApplicationClass() {
        openTestURL();
        String exceptionMessage = getDriver().findElement(By.xpath("//pre[2]")).getText();
        String expected = "ServletException: java.lang.ClassNotFoundException: ClassThatIsNotPresent";
        Assert.assertTrue(String.format("Unexpected error message.\n expected to contain: \'%s\'\n was: %s", expected, exceptionMessage), exceptionMessage.contains(expected));
    }
}

