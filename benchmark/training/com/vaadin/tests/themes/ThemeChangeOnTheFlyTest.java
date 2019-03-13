package com.vaadin.tests.themes;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class ThemeChangeOnTheFlyTest extends MultiBrowserTest {
    @Test
    public void injectedStyleAndThemeChange() throws IOException {
        openTestURL();
        $(ButtonElement.class).caption("Inject blue background").first().click();
        changeTheme("runo");
        compareScreen("runo-blue-background");
    }

    @Test
    public void reindeerToOthers() throws IOException {
        openTestURL();
        compareScreen("reindeer");
        changeThemeAndCompare("runo");
        changeThemeAndCompare("chameleon");
        changeThemeAndCompare("base");
    }

    @Test
    public void runoToReindeer() throws IOException {
        openTestURL("theme=runo");
        compareScreen("runo");
        changeThemeAndCompare("reindeer");
    }

    @Test
    public void reindeerToNullToReindeer() throws IOException {
        openTestURL();
        changeTheme("null");
        changeThemeAndCompare("reindeer");
    }
}

