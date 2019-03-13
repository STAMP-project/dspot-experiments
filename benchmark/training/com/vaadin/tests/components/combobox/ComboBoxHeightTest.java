package com.vaadin.tests.components.combobox;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class ComboBoxHeightTest extends SingleBrowserTest {
    @Test
    public void testPopupHeight() {
        openTestURL();
        assertPopupHeight();
    }

    @Test
    public void testPopupHeightCustomTheme() {
        openTestURL("theme=tests-valo-combobox-height");
        assertPopupHeight();
    }
}

