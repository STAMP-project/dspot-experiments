package com.vaadin.tests.layouts.layouttester;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public abstract class BaseIconTest extends MultiBrowserTest {
    @Test
    public void LayoutIcon() throws IOException {
        openTestURL();
        compareScreen("icon");
    }
}

