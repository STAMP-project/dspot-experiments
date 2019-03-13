package com.vaadin.tests.components.window;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;


public class CloseSubWindowTest extends MultiBrowserTest {
    @Test
    public void testClosingFromClickHandler() throws Exception {
        $(ButtonElement.class).first().click();
        assertLogText();
    }

    @Test
    public void testClosingFromTitleBar() throws Exception {
        $(WindowElement.class).first().findElement(By.className("v-window-closebox")).click();
        assertLogText();
    }

    @Test
    public void testClosingByRemovingFromUI() throws Exception {
        $(ButtonElement.class).get(1).click();
        assertLogText();
    }
}

