package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class HtmlToLatexFormatterTest {
    private HtmlToLatexFormatter formatter;

    @Test
    public void formatWithoutHtmlCharactersReturnsSameString() {
        Assertions.assertEquals("abc", formatter.format("abc"));
    }

    @Test
    public void formatMultipleHtmlCharacters() {
        Assertions.assertEquals("{{\\aa}}{\\\"{a}}{\\\"{o}}", formatter.format("&aring;&auml;&ouml;"));
    }

    @Test
    public void formatCombinedAccent() {
        Assertions.assertEquals("{\\\'{\\i}}", formatter.format("i&#x301;"));
    }

    @Test
    public void testBasic() {
        Assertions.assertEquals("aaa", formatter.format("aaa"));
    }

    @Test
    public void testHTML() {
        Assertions.assertEquals("{\\\"{a}}", formatter.format("&auml;"));
        Assertions.assertEquals("{\\\"{a}}", formatter.format("&#228;"));
        Assertions.assertEquals("{\\\"{a}}", formatter.format("&#xe4;"));
        Assertions.assertEquals("{{$\\Epsilon$}}", formatter.format("&Epsilon;"));
    }

    @Test
    public void testHTMLRemoveTags() {
        Assertions.assertEquals("aaa", formatter.format("<b>aaa</b>"));
    }

    @Test
    public void testHTMLCombiningAccents() {
        Assertions.assertEquals("{\\\"{a}}", formatter.format("a&#776;"));
        Assertions.assertEquals("{\\\"{a}}", formatter.format("a&#x308;"));
        Assertions.assertEquals("{\\\"{a}}b", formatter.format("a&#776;b"));
        Assertions.assertEquals("{\\\"{a}}b", formatter.format("a&#x308;b"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("JabRef", formatter.format(formatter.getExampleInput()));
    }
}

