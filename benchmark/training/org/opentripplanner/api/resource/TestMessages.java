package org.opentripplanner.api.resource;


import Message.GEOCODE_FROM_AMBIGUOUS;
import java.util.Locale;
import junit.framework.TestCase;


public class TestMessages extends TestCase {
    public void testLanguages() {
        // Force default to make test work on non-US machines
        Locale.setDefault(new Locale("en", "US"));
        String e = GEOCODE_FROM_AMBIGUOUS.get();
        String f = GEOCODE_FROM_AMBIGUOUS.get(Locale.CANADA_FRENCH);
        String s = GEOCODE_FROM_AMBIGUOUS.get(new Locale("es"));
        TestCase.assertNotNull(e);
        TestCase.assertNotNull(f);
        TestCase.assertNotNull(s);
        TestCase.assertNotSame(e, f);
        TestCase.assertNotSame(e, s);
        TestCase.assertNotSame(f, s);
    }
}

