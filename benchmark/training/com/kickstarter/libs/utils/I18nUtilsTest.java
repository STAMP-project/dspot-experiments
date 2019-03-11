package com.kickstarter.libs.utils;


import com.kickstarter.mock.factories.LocationFactory;
import java.util.Locale;
import junit.framework.TestCase;


public final class I18nUtilsTest extends TestCase {
    public void testLanguage() {
        TestCase.assertEquals("en", Locale.US.getLanguage());
        TestCase.assertEquals("de", Locale.GERMANY.getLanguage());
    }

    public void testIsCountryGermany() {
        TestCase.assertFalse(I18nUtils.isCountryGermany(LocationFactory.unitedStates().country()));
        TestCase.assertTrue(I18nUtils.isCountryGermany(LocationFactory.germany().country()));
    }

    public void testIsCountryUS() {
        TestCase.assertTrue(I18nUtils.isCountryUS(LocationFactory.unitedStates().country()));
        TestCase.assertFalse(I18nUtils.isCountryUS(LocationFactory.germany().country()));
    }
}

