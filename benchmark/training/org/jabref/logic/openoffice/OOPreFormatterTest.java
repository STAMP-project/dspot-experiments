package org.jabref.logic.openoffice;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class OOPreFormatterTest {
    @Test
    public void testPlainFormat() {
        Assertions.assertEquals("aaa", new OOPreFormatter().format("aaa"));
        Assertions.assertEquals("$", new OOPreFormatter().format("\\$"));
        Assertions.assertEquals("%", new OOPreFormatter().format("\\%"));
        Assertions.assertEquals("\\", new OOPreFormatter().format("\\\\"));
    }

    @Test
    public void testFormatAccents() {
        Assertions.assertEquals("?", new OOPreFormatter().format("{\\\"{a}}"));
        Assertions.assertEquals("?", new OOPreFormatter().format("{\\\"{A}}"));
        Assertions.assertEquals("?", new OOPreFormatter().format("{\\c{C}}"));
    }

    @Test
    public void testSpecialCommands() {
        Assertions.assertEquals("?", new OOPreFormatter().format("{\\aa}"));
        Assertions.assertEquals("bb", new OOPreFormatter().format("{\\bb}"));
        Assertions.assertEquals("? a", new OOPreFormatter().format("\\aa a"));
        Assertions.assertEquals("? a", new OOPreFormatter().format("{\\aa a}"));
        Assertions.assertEquals("??", new OOPreFormatter().format("\\aa\\AA"));
        Assertions.assertEquals("bb a", new OOPreFormatter().format("\\bb a"));
    }

    @Test
    public void testUnsupportedSpecialCommands() {
        Assertions.assertEquals("ftmch", new OOPreFormatter().format("\\ftmch"));
        Assertions.assertEquals("ftmch", new OOPreFormatter().format("{\\ftmch}"));
        Assertions.assertEquals("ftmchaaa", new OOPreFormatter().format("{\\ftmch\\aaa}"));
    }

    @Test
    public void testEquations() {
        Assertions.assertEquals("?", new OOPreFormatter().format("$\\Sigma$"));
    }

    @Test
    public void testFormatStripLatexCommands() {
        Assertions.assertEquals("-", new OOPreFormatter().format("\\mbox{-}"));
    }

    @Test
    public void testFormatting() {
        Assertions.assertEquals("<i>kkk</i>", new OOPreFormatter().format("\\textit{kkk}"));
        Assertions.assertEquals("<i>kkk</i>", new OOPreFormatter().format("{\\it kkk}"));
        Assertions.assertEquals("<i>kkk</i>", new OOPreFormatter().format("\\emph{kkk}"));
        Assertions.assertEquals("<b>kkk</b>", new OOPreFormatter().format("\\textbf{kkk}"));
        Assertions.assertEquals("<smallcaps>kkk</smallcaps>", new OOPreFormatter().format("\\textsc{kkk}"));
        Assertions.assertEquals("<s>kkk</s>", new OOPreFormatter().format("\\sout{kkk}"));
        Assertions.assertEquals("<u>kkk</u>", new OOPreFormatter().format("\\underline{kkk}"));
        Assertions.assertEquals("<tt>kkk</tt>", new OOPreFormatter().format("\\texttt{kkk}"));
        Assertions.assertEquals("<sup>kkk</sup>", new OOPreFormatter().format("\\textsuperscript{kkk}"));
        Assertions.assertEquals("<sub>kkk</sub>", new OOPreFormatter().format("\\textsubscript{kkk}"));
    }
}

