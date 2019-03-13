package com.vaadin.tests.components.uitest;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public abstract class ThemeTest extends MultiBrowserTest {
    @Test
    public void testTheme() throws Exception {
        openTestURL(("theme=" + (getTheme())));
        runThemeTest();
    }
}

