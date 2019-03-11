package org.jabref.logic.l10n;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class LocalizationKeyParamsTest {
    @Test
    public void testReplacePlaceholders() {
        Assertions.assertEquals("biblatex mode", new LocalizationKeyParams("biblatex mode").replacePlaceholders());
        Assertions.assertEquals("biblatex mode", new LocalizationKeyParams("%0 mode", "biblatex").replacePlaceholders());
        Assertions.assertEquals("C:\\bla mode", new LocalizationKeyParams("%0 mode", "C:\\bla").replacePlaceholders());
        Assertions.assertEquals("What \n : %e %c a b", new LocalizationKeyParams("What \n : %e %c %0 %1", "a", "b").replacePlaceholders());
        Assertions.assertEquals("What \n : %e %c_a b", new LocalizationKeyParams("What \n : %e %c_%0 %1", "a", "b").replacePlaceholders());
    }

    @Test
    public void testTooManyParams() {
        Assertions.assertThrows(IllegalStateException.class, () -> new LocalizationKeyParams("", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"));
    }
}

