package com.vaadin.tests.components.javascriptcomponent;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;


public class JavaScriptSpanTest extends SingleBrowserTest {
    @Test
    public void componentShownAsSpan() {
        openTestURL();
        assertElementPresent(By.xpath("//span[text()='Hello World']"));
    }
}

