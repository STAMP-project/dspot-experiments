package com.vaadin.tests.applicationservlet;


import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class SystemMessagesTest extends MultiBrowserTest {
    @Test
    public void testFinnishLocaleInSystemErrorMessage() throws Exception {
        openTestURL();
        verifyError("fi_FI");
    }

    @Test
    public void testGermanLocaleInSystemErrorMessage() throws Exception {
        openTestURL();
        $(NativeSelectElement.class).first().selectByText("de_DE");
        verifyError("de_DE");
    }
}

