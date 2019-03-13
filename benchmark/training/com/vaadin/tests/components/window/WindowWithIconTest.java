package com.vaadin.tests.components.window;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class WindowWithIconTest extends MultiBrowserTest {
    @Test
    public void testWindowWithIcon() throws Exception {
        openTestURL();
        compareScreen("icon-rendered-properly");
    }
}

