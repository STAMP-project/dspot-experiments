package com.vaadin.tests.components.textfield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class TextFieldTruncatesUnderscoresInModalDialogsTest extends MultiBrowserTest {
    @Test
    public void testWindowRepositioning() throws Exception {
        openTestURL();
        compareScreen("init");
    }
}

