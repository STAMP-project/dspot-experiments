package com.vaadin.tests.layouts.layouttester;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public abstract class BaseCaptionTest extends MultiBrowserTest {
    @Test
    public void LayoutCaption() throws IOException, InterruptedException {
        openTestURL();
        compareScreen("caption");
    }
}

