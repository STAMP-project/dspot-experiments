package com.vaadin.tests.layouts.layouttester;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public abstract class BaseAlignmentTest extends MultiBrowserTest {
    @Test
    public void layoutAlignment() throws IOException {
        openTestURL();
        compareScreen("alignment");
    }
}

