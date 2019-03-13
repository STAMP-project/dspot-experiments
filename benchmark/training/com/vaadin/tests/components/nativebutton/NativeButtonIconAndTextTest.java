package com.vaadin.tests.components.nativebutton;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class NativeButtonIconAndTextTest extends MultiBrowserTest {
    @Test
    public void testNativeButtonIconAltText() {
        openTestURL();
        assertAltText(NativeButtonIconAndText.BUTTON_TEXT, "");
        assertAltText(NativeButtonIconAndText.BUTTON_TEXT_ICON, "");
        assertAltText(NativeButtonIconAndText.BUTTON_TEXT_ICON_ALT, NativeButtonIconAndText.INITIAL_ALTERNATE_TEXT);
        assertAltText(NativeButtonIconAndText.NATIVE_BUTTON_TEXT, "");
        assertAltText(NativeButtonIconAndText.NATIVE_BUTTON_TEXT_ICON, "");
        assertAltText(NativeButtonIconAndText.NATIVE_BUTTON_TEXT_ICON_ALT, NativeButtonIconAndText.INITIAL_ALTERNATE_TEXT);
        clickElements(NativeButtonIconAndText.BUTTON_TEXT, NativeButtonIconAndText.BUTTON_TEXT_ICON, NativeButtonIconAndText.BUTTON_TEXT_ICON_ALT, NativeButtonIconAndText.NATIVE_BUTTON_TEXT, NativeButtonIconAndText.NATIVE_BUTTON_TEXT_ICON, NativeButtonIconAndText.NATIVE_BUTTON_TEXT_ICON_ALT);
        // Button without icon - should not get alt text
        assertAltText(NativeButtonIconAndText.BUTTON_TEXT, "");
        assertAltText(NativeButtonIconAndText.BUTTON_TEXT_ICON, NativeButtonIconAndText.UPDATED_ALTERNATE_TEXT);
        assertAltText(NativeButtonIconAndText.BUTTON_TEXT_ICON_ALT, "");
        // Button without icon - should not get alt text
        assertAltText(NativeButtonIconAndText.NATIVE_BUTTON_TEXT, "");
        assertAltText(NativeButtonIconAndText.NATIVE_BUTTON_TEXT_ICON, NativeButtonIconAndText.UPDATED_ALTERNATE_TEXT);
        assertAltText(NativeButtonIconAndText.NATIVE_BUTTON_TEXT_ICON_ALT, "");
    }
}

