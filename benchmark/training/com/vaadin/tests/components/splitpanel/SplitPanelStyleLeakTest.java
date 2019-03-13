package com.vaadin.tests.components.splitpanel;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class SplitPanelStyleLeakTest extends MultiBrowserTest {
    @Test
    public void checkScreenshot() throws IOException {
        openTestURL();
        compareScreen("all");
    }
}

