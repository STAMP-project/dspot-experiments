package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class UnicodeConverterTest {
    private UnicodeToLatexFormatter formatter;

    @Test
    public void testBasic() {
        Assertions.assertEquals("aaa", formatter.format("aaa"));
    }

    @Test
    public void testUnicodeCombiningAccents() {
        Assertions.assertEquals("{\\\"{a}}", formatter.format("a\u0308"));
        Assertions.assertEquals("{\\\"{a}}b", formatter.format("a\u0308b"));
    }

    @Test
    public void testUnicode() {
        Assertions.assertEquals("{\\\"{a}}", formatter.format("?"));
        Assertions.assertEquals("{{$\\Epsilon$}}", formatter.format("\u0395"));
    }

    @Test
    public void testUnicodeSingle() {
        Assertions.assertEquals("a", formatter.format("a"));
    }
}

