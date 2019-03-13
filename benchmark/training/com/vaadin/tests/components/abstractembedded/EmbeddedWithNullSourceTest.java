package com.vaadin.tests.components.abstractembedded;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;
import org.openqa.selenium.By;


public class EmbeddedWithNullSourceTest extends MultiBrowserTest {
    @Test
    public void testEmbeddedWithNullSource() throws IOException {
        openTestURL();
        waitForElementPresent(By.className("v-image"));
        compareScreen("nullSources");
    }
}

