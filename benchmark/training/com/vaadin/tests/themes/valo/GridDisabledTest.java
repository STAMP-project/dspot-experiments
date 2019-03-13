package com.vaadin.tests.themes.valo;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class GridDisabledTest extends MultiBrowserTest {
    @Test
    public void disabledGrid() throws IOException {
        openTestURL();
        waitUntilLoadingIndicatorNotVisible();
        $(ButtonElement.class).caption("Disable").first().click();
        compareScreen("disabled");
    }
}

