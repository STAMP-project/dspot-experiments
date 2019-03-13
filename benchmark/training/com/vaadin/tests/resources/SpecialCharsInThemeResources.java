package com.vaadin.tests.resources;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class SpecialCharsInThemeResources extends SingleBrowserTest {
    @Test
    public void loadThemeResource() {
        loadResource("/VAADIN/themes/tests-tickets/ordinary.txt");
        checkSource();
    }

    @Test
    public void loadThemeResourceWithPercentage() {
        loadResource("/VAADIN/themes/tests-tickets/percentagein%2520name.txt");
        checkSource();
    }

    @Test
    public void loadThemeResourceWithSpecialChars() {
        loadResource("/VAADIN/themes/tests-tickets/folder%20with%20space/resource%20with%20special%20$chars@.txt");
        checkSource();
    }
}

