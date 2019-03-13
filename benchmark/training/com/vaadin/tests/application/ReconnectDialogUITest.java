package com.vaadin.tests.application;


import com.vaadin.tests.tb3.MultiBrowserTestWithProxy;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class ReconnectDialogUITest extends MultiBrowserTestWithProxy {
    @Test
    public void reconnectTogglesBodyStyle() throws IOException {
        openTestURL();
        getButton().click();
        disconnectProxy();
        getButton().click();
        waitForReconnectDialogPresent();
        WebElement body = findElement(By.xpath("//body"));
        Assert.assertTrue("Body should have a style name when reconnecting", hasCssClass(body, "v-reconnecting"));
        connectProxy();
        waitForReconnectDialogToDisappear();
        Assert.assertFalse("Body should no longer have a style name when reconnected", hasCssClass(body, "v-reconnecting"));
    }

    @Test
    public void reconnectDialogShownAndDisappears() throws IOException {
        openTestURL();
        getButton().click();
        Assert.assertEquals("1. Hello from the server", getLogRow(0));
        disconnectProxy();
        getButton().click();
        waitForReconnectDialogWithText("Server connection lost, trying to reconnect...");
        connectProxy();
        waitForReconnectDialogToDisappear();
        Assert.assertEquals("2. Hello from the server", getLogRow(0));
    }
}

