package com.vaadin.tests.components.flash;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class FlashIsVisibleTest extends MultiBrowserTest {
    @Test
    public void testFlashIsCorrectlyDisplayed() throws Exception {
        openTestURL();
        /* Allow the flash plugin to load before taking the screenshot */
        sleep(5000);
        compareScreen("blue-circle");
    }
}

