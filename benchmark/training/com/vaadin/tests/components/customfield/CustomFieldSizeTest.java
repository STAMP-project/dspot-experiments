package com.vaadin.tests.components.customfield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class CustomFieldSizeTest extends MultiBrowserTest {
    @Test
    public void checkScreenshot() throws IOException {
        openTestURL();
        compareScreen("size");
    }
}

