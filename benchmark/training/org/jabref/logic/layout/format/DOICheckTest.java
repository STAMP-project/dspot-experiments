package org.jabref.logic.layout.format;


import org.jabref.logic.layout.LayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DOICheckTest {
    @Test
    public void testFormat() {
        LayoutFormatter lf = new DOICheck();
        Assertions.assertEquals("", lf.format(""));
        Assertions.assertEquals(null, lf.format(null));
        Assertions.assertEquals("https://doi.org/10.1000/ISBN1-900512-44-0", lf.format("10.1000/ISBN1-900512-44-0"));
        Assertions.assertEquals("https://doi.org/10.1000/ISBN1-900512-44-0", lf.format("http://dx.doi.org/10.1000/ISBN1-900512-44-0"));
        Assertions.assertEquals("https://doi.org/10.1000/ISBN1-900512-44-0", lf.format("http://doi.acm.org/10.1000/ISBN1-900512-44-0"));
        Assertions.assertEquals("https://doi.org/10.1145/354401.354407", lf.format("http://doi.acm.org/10.1145/354401.354407"));
        Assertions.assertEquals("https://doi.org/10.1145/354401.354407", lf.format("10.1145/354401.354407"));
        // Works even when having a / at the front
        Assertions.assertEquals("https://doi.org/10.1145/354401.354407", lf.format("/10.1145/354401.354407"));
        // Obviously a wrong doi, will not change anything.
        Assertions.assertEquals("10", lf.format("10"));
        // Obviously a wrong doi, will not change anything.
        Assertions.assertEquals("1", lf.format("1"));
    }
}

