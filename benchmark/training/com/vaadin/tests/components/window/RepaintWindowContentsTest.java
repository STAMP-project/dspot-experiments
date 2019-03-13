package com.vaadin.tests.components.window;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class RepaintWindowContentsTest extends MultiBrowserTest {
    @Test
    public void testRepaintWindowContents() throws Exception {
        openTestURL();
        assertWindowContents("Button 1");
        toggleWindowContents();
        assertWindowContents("Button 2");
        toggleWindowContents();
        assertWindowContents("Button 1");
        toggleWindowContents();
        assertWindowContents("Button 2");
    }
}

