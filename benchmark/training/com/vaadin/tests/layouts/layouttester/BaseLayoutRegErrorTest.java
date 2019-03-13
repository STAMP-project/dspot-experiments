package com.vaadin.tests.layouts.layouttester;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public abstract class BaseLayoutRegErrorTest extends MultiBrowserTest {
    @Test
    public void LayoutRegError() throws IOException {
        openTestURL();
        compareScreen("RegError");
    }
}

