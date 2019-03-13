package com.vaadin.tests.components.panel;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class UndefinedSizeScrollbarsTest extends MultiBrowserTest {
    @Test
    public void testNoScrollbars() throws IOException {
        openTestURL();
        compareScreen("noscrollbars");
    }
}

