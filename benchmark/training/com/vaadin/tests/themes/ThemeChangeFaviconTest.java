package com.vaadin.tests.themes;


import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assume;
import org.junit.Test;


public class ThemeChangeFaviconTest extends SingleBrowserTest {
    @Test
    public void changeFavicon() throws InterruptedException {
        Assume.assumeFalse("PhantomJS does not send onload events for styles", BrowserUtil.isPhantomJS(getDesiredCapabilities()));
        setDebug(true);
        openTestURL();
        assertFavicon("reindeer");
        changeTheme("valo");
        assertFavicon("valo");
        changeTheme("reindeer");
        assertFavicon("reindeer");
    }
}

