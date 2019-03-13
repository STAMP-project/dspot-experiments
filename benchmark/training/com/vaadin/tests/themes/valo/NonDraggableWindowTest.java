package com.vaadin.tests.themes.valo;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class NonDraggableWindowTest extends MultiBrowserTest {
    @Test
    public void cursorIsDefault() {
        openTestURL();
        WebElement header = findElement(By.className("v-window-header"));
        Assert.assertThat(header.getCssValue("cursor"), CoreMatchers.is("default"));
    }
}

