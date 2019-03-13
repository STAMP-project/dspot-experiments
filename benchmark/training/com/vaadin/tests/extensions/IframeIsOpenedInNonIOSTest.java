package com.vaadin.tests.extensions;


import IframeIsOpenedInNonIOS.FILE_NAME;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class IframeIsOpenedInNonIOSTest extends MultiBrowserTest {
    @Test
    public void fileOpenedInNewTab() {
        openTestURL();
        $(ButtonElement.class).caption("Download").first().click();
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        boolean containsFileIframe = false;
        for (WebElement iframe : iframes) {
            containsFileIframe = containsFileIframe | (iframe.getAttribute("src").contains(FILE_NAME));
        }
        Assert.assertTrue("page doesn't contain iframe with the file", containsFileIframe);
    }
}

