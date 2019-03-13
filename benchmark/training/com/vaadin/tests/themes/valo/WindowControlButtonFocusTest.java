package com.vaadin.tests.themes.valo;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class WindowControlButtonFocusTest extends MultiBrowserTest {
    @Test
    public void focusMaximize() throws IOException, InterruptedException {
        openTestURL();
        WebElement window = $(WindowElement.class).first();
        WebElement maximize = window.findElement(By.className("v-window-maximizebox"));
        executeScript("arguments[0].focus()", maximize);
        compareScreen(window, "maximize-focused");
    }

    @Test
    public void focusClose() throws IOException {
        openTestURL();
        WebElement window = $(WindowElement.class).first();
        WebElement close = window.findElement(By.className("v-window-closebox"));
        executeScript("arguments[0].focus()", close);
        compareScreen(window, "close-focused");
    }
}

