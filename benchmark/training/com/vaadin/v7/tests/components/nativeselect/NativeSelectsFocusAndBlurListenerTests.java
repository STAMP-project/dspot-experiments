package com.vaadin.v7.tests.components.nativeselect;


import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class NativeSelectsFocusAndBlurListenerTests extends MultiBrowserTest {
    @Test
    public void testFocusAndBlurListener() throws InterruptedException {
        setDebug(true);
        openTestURL();
        Thread.sleep(200);
        menu("Component");
        menuSub("Listeners");
        menuSub("Focus listener");
        menu("Component");
        menuSub("Listeners");
        menuSub("Blur listener");
        findElement(By.tagName("body")).click();
        NativeSelectElement s = $(NativeSelectElement.class).first();
        s.selectByText("Item 3");
        getDriver().findElement(By.tagName("body")).click();
        // Somehow selectByText causes focus + blur + focus + blur on
        // Chrome/PhantomJS
        if ((BrowserUtil.isChrome(getDesiredCapabilities())) || (BrowserUtil.isPhantomJS(getDesiredCapabilities()))) {
            Assert.assertEquals("4. FocusEvent", getLogRow(1));
            Assert.assertEquals("5. BlurEvent", getLogRow(0));
        } else {
            Assert.assertEquals("2. FocusEvent", getLogRow(1));
            Assert.assertEquals("3. BlurEvent", getLogRow(0));
        }
    }
}

