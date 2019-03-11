package org.jabref.logic.layout.format;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class LatexToUnicodeFormatterTest {
    public final LatexToUnicodeFormatter formatter = new LatexToUnicodeFormatter();

    @Test
    public void testPlainFormat() {
        Assertions.assertEquals("aaa", formatter.format("aaa"));
    }

    @Test
    public void testFormatUmlaut() {
        Assertions.assertEquals("?", formatter.format("{\\\"{a}}"));
        Assertions.assertEquals("?", formatter.format("{\\\"{A}}"));
    }

    @Test
    public void testFormatStripLatexCommands() {
        Assertions.assertEquals("-", formatter.format("\\mbox{-}"));
    }

    @Test
    public void testFormatTextit() {
        // See #1464
        Assertions.assertEquals("\ud835\udc61\ud835\udc52\ud835\udc65\ud835\udc61", formatter.format("\\textit{text}"));
    }

    @Test
    public void testEscapedDollarSign() {
        Assertions.assertEquals("$", formatter.format("\\$"));
    }

    @Test
    public void testEquationsSingleSymbol() {
        Assertions.assertEquals("?", formatter.format("$\\sigma$"));
    }

    @Test
    public void testEquationsMoreComplicatedFormatting() {
        Assertions.assertEquals("A 32 mA ??-modulator", formatter.format("A 32~{mA} {$\\Sigma\\Delta$}-modulator"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("M?nch", formatter.format(formatter.getExampleInput()));
    }

    @Test
    public void testChi() {
        // See #1464
        Assertions.assertEquals("?", formatter.format("$\\chi$"));
    }

    @Test
    public void testSWithCaron() {
        // Bug #1264
        Assertions.assertEquals("?", formatter.format("{\\v{S}}"));
    }

    @Test
    public void testIWithDiaresis() {
        Assertions.assertEquals("?", formatter.format("\\\"{i}"));
    }

    @Test
    public void testIWithDiaresisAndEscapedI() {
        // this might look strange in the test, but is actually a correct translation and renders identically to the above example in the UI
        Assertions.assertEquals("??", formatter.format("\\\"{\\i}"));
    }

    @Test
    public void testIWithDiaresisAndUnnecessaryBraces() {
        Assertions.assertEquals("?", formatter.format("{\\\"{i}}"));
    }

    @Test
    public void testUpperCaseIWithDiaresis() {
        Assertions.assertEquals("?", formatter.format("\\\"{I}"));
    }

    @Test
    public void testPolishName() {
        Assertions.assertEquals("??ski", formatter.format("\\L\\k{e}ski"));
    }

    @Test
    public void testDoubleCombiningAccents() {
        Assertions.assertEquals("?", formatter.format("$\\acute{\\omega}$"));
    }

    @Test
    public void testCombiningAccentsCase1() {
        Assertions.assertEquals("?", formatter.format("{\\c{h}}"));
    }

    @Test
    public void unknownCommandIsIgnored() {
        Assertions.assertEquals("", formatter.format("\\aaaa"));
    }

    @Test
    public void unknownCommandKeepsArgument() {
        Assertions.assertEquals("bbbb", formatter.format("\\aaaa{bbbb}"));
    }

    @Test
    public void unknownCommandWithEmptyArgumentIsIgnored() {
        Assertions.assertEquals("", formatter.format("\\aaaa{}"));
    }

    @Test
    public void testTildeN() {
        Assertions.assertEquals("Monta?a", formatter.format("Monta\\~{n}a"));
    }

    @Test
    public void testAcuteNLongVersion() {
        Assertions.assertEquals("Mali?ski", formatter.format("Mali\\\'{n}ski"));
        Assertions.assertEquals("Mali?ski", formatter.format("Mali\\\'{N}ski"));
    }

    @Test
    public void testAcuteNShortVersion() {
        Assertions.assertEquals("Mali?ski", formatter.format("Mali\\\'nski"));
        Assertions.assertEquals("Mali?ski", formatter.format("Mali\\\'Nski"));
    }

    @Test
    public void testApostrophN() {
        Assertions.assertEquals("Mali'nski", formatter.format("Mali'nski"));
        Assertions.assertEquals("Mali'Nski", formatter.format("Mali'Nski"));
    }

    @Test
    public void testApostrophO() {
        Assertions.assertEquals("L'oscillation", formatter.format("L'oscillation"));
    }

    @Test
    public void testApostrophC() {
        Assertions.assertEquals("O'Connor", formatter.format("O'Connor"));
    }

    @Test
    public void testPreservationOfSingleUnderscore() {
        Assertions.assertEquals("Lorem ipsum_lorem ipsum", formatter.format("Lorem ipsum_lorem ipsum"));
    }

    @Test
    public void testConversionOfUnderscoreWithBraces() {
        Assertions.assertEquals("Lorem ipsum_(lorem ipsum)", formatter.format("Lorem ipsum_{lorem ipsum}"));
    }

    @Test
    public void testConversionOfOrdinal1st() {
        Assertions.assertEquals("1??", formatter.format("1\\textsuperscript{st}"));
    }

    @Test
    public void testConversionOfOrdinal2nd() {
        Assertions.assertEquals("2??", formatter.format("2\\textsuperscript{nd}"));
    }

    @Test
    public void testConversionOfOrdinal3rd() {
        Assertions.assertEquals("3??", formatter.format("3\\textsuperscript{rd}"));
    }

    @Test
    public void testConversionOfOrdinal4th() {
        Assertions.assertEquals("4??", formatter.format("4\\textsuperscript{th}"));
    }

    @Test
    public void testConversionOfOrdinal9th() {
        Assertions.assertEquals("9??", formatter.format("9\\textsuperscript{th}"));
    }
}

