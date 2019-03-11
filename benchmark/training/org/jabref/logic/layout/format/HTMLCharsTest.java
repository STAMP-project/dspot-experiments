package org.jabref.logic.layout.format;


import org.jabref.logic.layout.LayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HTMLCharsTest {
    private LayoutFormatter layout;

    @Test
    public void testBasicFormat() {
        Assertions.assertEquals("", layout.format(""));
        Assertions.assertEquals("hallo", layout.format("hallo"));
        Assertions.assertEquals("R?flexions sur le timing de la quantit?", layout.format("R?flexions sur le timing de la quantit?"));
        Assertions.assertEquals("h&aacute;llo", layout.format("h\\\'allo"));
        Assertions.assertEquals("&imath; &imath;", layout.format("\\i \\i"));
        Assertions.assertEquals("&imath;", layout.format("\\i"));
        Assertions.assertEquals("&imath;", layout.format("\\{i}"));
        Assertions.assertEquals("&imath;&imath;", layout.format("\\i\\i"));
        Assertions.assertEquals("&auml;", layout.format("{\\\"{a}}"));
        Assertions.assertEquals("&auml;", layout.format("{\\\"a}"));
        Assertions.assertEquals("&auml;", layout.format("\\\"a"));
        Assertions.assertEquals("&Ccedil;", layout.format("{\\c{C}}"));
        Assertions.assertEquals("&Oogon;&imath;", layout.format("\\k{O}\\i"));
        Assertions.assertEquals("&ntilde; &ntilde; &iacute; &imath; &imath;", layout.format("\\~{n} \\~n \\\'i \\i \\i"));
    }

    @Test
    public void testLaTeXHighlighting() {
        Assertions.assertEquals("<em>hallo</em>", layout.format("\\emph{hallo}"));
        Assertions.assertEquals("<em>hallo</em>", layout.format("{\\emph hallo}"));
        Assertions.assertEquals("<em>hallo</em>", layout.format("{\\em hallo}"));
        Assertions.assertEquals("<i>hallo</i>", layout.format("\\textit{hallo}"));
        Assertions.assertEquals("<i>hallo</i>", layout.format("{\\textit hallo}"));
        Assertions.assertEquals("<i>hallo</i>", layout.format("{\\it hallo}"));
        Assertions.assertEquals("<b>hallo</b>", layout.format("\\textbf{hallo}"));
        Assertions.assertEquals("<b>hallo</b>", layout.format("{\\textbf hallo}"));
        Assertions.assertEquals("<b>hallo</b>", layout.format("{\\bf hallo}"));
        Assertions.assertEquals("<sup>hallo</sup>", layout.format("\\textsuperscript{hallo}"));
        Assertions.assertEquals("<sub>hallo</sub>", layout.format("\\textsubscript{hallo}"));
        Assertions.assertEquals("<u>hallo</u>", layout.format("\\underline{hallo}"));
        Assertions.assertEquals("<s>hallo</s>", layout.format("\\sout{hallo}"));
        Assertions.assertEquals("<tt>hallo</tt>", layout.format("\\texttt{hallo}"));
    }

    @Test
    public void testEquations() {
        Assertions.assertEquals("&dollar;", layout.format("\\$"));
        Assertions.assertEquals("&sigma;", layout.format("$\\sigma$"));
        Assertions.assertEquals("A 32&nbsp;mA &Sigma;&Delta;-modulator", layout.format("A 32~{mA} {$\\Sigma\\Delta$}-modulator"));
    }

    @Test
    public void testNewLine() {
        Assertions.assertEquals("a<br>b", layout.format("a\nb"));
        Assertions.assertEquals("a<p>b", layout.format("a\n\nb"));
    }

    /* Is missing a lot of test cases for the individual chars... */
    @Test
    public void testQuoteSingle() {
        Assertions.assertEquals("&#39;", layout.format("{\\textquotesingle}"));
    }

    @Test
    public void unknownCommandIsKept() {
        Assertions.assertEquals("aaaa", layout.format("\\aaaa"));
    }

    @Test
    public void unknownCommandKeepsArgument() {
        Assertions.assertEquals("bbbb", layout.format("\\aaaa{bbbb}"));
    }

    @Test
    public void unknownCommandWithEmptyArgumentIsKept() {
        Assertions.assertEquals("aaaa", layout.format("\\aaaa{}"));
    }
}

