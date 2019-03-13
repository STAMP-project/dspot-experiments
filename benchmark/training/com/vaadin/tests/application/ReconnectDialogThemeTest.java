package com.vaadin.tests.application;


import OutputType.BYTES;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.CustomTestBenchCommandExecutor;
import com.vaadin.tests.tb3.MultiBrowserThemeTestWithProxy;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;


@TestCategory("communication")
public class ReconnectDialogThemeTest extends MultiBrowserThemeTestWithProxy {
    static By reconnectDialogBy = By.className("v-reconnect-dialog");

    @Test
    public void reconnectDialogTheme() throws IOException {
        openTestURL();
        ButtonElement helloButton = $(ButtonElement.class).caption("Say hello").first();
        helloButton.click();
        Assert.assertEquals("1. Hello from the server", getLogRow(0));
        disconnectProxy();
        helloButton.click();
        testBench().disableWaitForVaadin();
        waitUntil(( driver) -> isElementPresent(ReconnectDialogThemeTest.reconnectDialogBy));
        WebElement dialog = findElement(ReconnectDialogThemeTest.reconnectDialogBy);
        WebElement spinner = dialog.findElement(By.className("spinner"));
        // Hide spinner to make screenshot stable
        executeScript("arguments[0].style.visibility='hidden';", spinner);
        compareScreen("onscreen-without-spinner");
        // Show spinner and make sure it is shown by comparing to the screenshot
        // without a spinner
        executeScript("arguments[0].style.visibility='visible';", spinner);
        BufferedImage fullScreen = ImageIO.read(new ByteArrayInputStream(((TakesScreenshot) (getDriver())).getScreenshotAs(BYTES)));
        BufferedImage spinnerImage = CustomTestBenchCommandExecutor.cropToElement(spinner, fullScreen);
        assertHasManyColors("Spinner is not shown", spinnerImage);
    }

    @Test
    public void gaveUpTheme() throws IOException {
        openTestURL("reconnectAttempts=3");
        waitUntil(( input) -> {
            try {
                return ($(.class).first()) != null;
            } catch ( e) {
                return false;
            }
        });
        disconnectProxy();
        $(ButtonElement.class).first().click();
        waitForReconnectDialogWithText("Server connection lost.");
        compareScreen("gaveupdialog");
    }
}

