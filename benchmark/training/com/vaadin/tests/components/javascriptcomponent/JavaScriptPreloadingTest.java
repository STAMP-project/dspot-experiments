package com.vaadin.tests.components.javascriptcomponent;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class JavaScriptPreloadingTest extends MultiBrowserTest {
    @Test
    public void scriptsShouldPreloadAndExecuteInCorrectOrder() throws InterruptedException {
        openTestURL();
        try {
            waitUntil(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            Assert.assertEquals("First", alert.getText());
            alert.accept();
            waitUntil(ExpectedConditions.alertIsPresent());
            alert = driver.switchTo().alert();
            Assert.assertEquals("Second", alert.getText());
            alert.accept();
        } catch (TimeoutException te) {
            Assert.fail("@Javascript widget loading halted.");
        }
    }
}

