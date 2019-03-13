package com.vaadin.tests.themes;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


// Extending SingleBrowserTest just to include the test into our test suites.
public class FaviconTest extends SingleBrowserTest {
    @Test
    public void chameleonHasFavicon() {
        assertThatThemeHasFavicon("chameleon");
    }

    @Test
    public void runoHasFavicon() {
        assertThatThemeHasFavicon("runo");
    }

    @Test
    public void reindeerHasFavicon() {
        assertThatThemeHasFavicon("reindeer");
    }

    @Test
    public void valoHasFavicon() {
        assertThatThemeHasFavicon("valo");
    }
}

