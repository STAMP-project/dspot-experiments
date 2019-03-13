package com.vaadin.tests.layouts.gridlayout;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class GridLayoutMoveComponentTest extends MultiBrowserTest {
    @Test
    public void componentsShouldMoveRight() throws IOException {
        openTestURL();
        compareScreen("all-left");
        clickButtonWithCaption("Shift label right");
        compareScreen("label-right");
        clickButtonWithCaption("Shift button right");
        compareScreen("label-button-right");
        clickButtonWithCaption("Shift text field right");
        compareScreen("label-button-textfield-right");
    }
}

