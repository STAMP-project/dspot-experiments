package com.vaadin.tests.components.browserframe;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class BrowserFrameDoubleScrollbarsTest extends MultiBrowserTest {
    @Test
    public void testWindowRepositioning() throws Exception {
        openTestURL();
        compareScreen("BrowserFrameDoubleScrollbars");
    }
}

