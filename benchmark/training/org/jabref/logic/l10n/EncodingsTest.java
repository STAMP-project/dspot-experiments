package org.jabref.logic.l10n;


import Encodings.ENCODINGS;
import Encodings.ENCODINGS.length;
import Encodings.ENCODINGS_DISPLAYNAMES;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class EncodingsTest {
    @Test
    public void charsetsShouldNotBeNull() {
        Assertions.assertNotNull(ENCODINGS);
    }

    @Test
    public void displayNamesShouldNotBeNull() {
        Assertions.assertNotNull(ENCODINGS_DISPLAYNAMES);
    }

    @Test
    public void charsetsShouldNotBeEmpty() {
        Assertions.assertNotEquals(0, length);
    }

    @Test
    public void displayNamesShouldNotBeEmpty() {
        Assertions.assertNotEquals(0, Encodings.ENCODINGS_DISPLAYNAMES.length);
    }
}

