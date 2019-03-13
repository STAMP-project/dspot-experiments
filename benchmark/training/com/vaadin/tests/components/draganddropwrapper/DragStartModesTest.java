package com.vaadin.tests.components.draganddropwrapper;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class DragStartModesTest extends MultiBrowserTest {
    @Test
    public void testDragStartModes() throws IOException {
        openTestURL();
        WebElement dropTarget = vaadinElement("/VVerticalLayout[0]/VVerticalLayout[0]/VLabel[0]");
        dragToTarget("COMPONENT", dropTarget);
        dragToTarget("WRAPPER", dropTarget);
        dragToTarget("COMPONENT_OTHER", dropTarget);
    }
}

