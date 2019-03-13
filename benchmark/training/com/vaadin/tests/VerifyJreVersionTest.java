package com.vaadin.tests;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class VerifyJreVersionTest extends SingleBrowserTest {
    @Test
    public void verifyJreVersion() {
        openTestURL();
        WebElement jreVersionLabel = vaadinElementById("jreVersionLabel");
        MatcherAssert.assertThat(jreVersionLabel.getText(), CoreMatchers.startsWith("Using Java 1.8.0_"));
    }
}

