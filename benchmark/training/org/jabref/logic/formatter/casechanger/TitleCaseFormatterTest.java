package org.jabref.logic.formatter.casechanger;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class TitleCaseFormatterTest {
    private TitleCaseFormatter formatter;

    @Test
    public void eachFirstLetterIsUppercased() {
        Assertions.assertEquals("Upper Each First", formatter.format("upper each first"));
    }

    @Test
    public void eachFirstLetterIsUppercasedAndOthersLowercased() {
        Assertions.assertEquals("Upper Each First", formatter.format("upper eACH first"));
    }

    @Test
    public void eachFirstLetterIsUppercasedAndATralingAndIsAlsoUppercased() {
        Assertions.assertEquals("An Upper Each First And", formatter.format("an upper each first and"));
    }

    @Test
    public void eachFirstLetterIsUppercasedAndATralingAndIsAlsoCorrectlyCased() {
        Assertions.assertEquals("An Upper Each First And", formatter.format("an upper each first AND"));
    }

    @Test
    public void eachFirstLetterIsUppercasedButIntermediateAndsAreKeptLowercase() {
        Assertions.assertEquals("An Upper Each of the and First And", formatter.format("an upper each of the and first and"));
    }

    @Test
    public void eachFirstLetterIsUppercasedButIntermediateAndsArePutLowercase() {
        Assertions.assertEquals("An Upper Each of the and First And", formatter.format("an upper each of the AND first and"));
    }

    @Test
    public void theAfterColonGetsCapitalized() {
        Assertions.assertEquals("An Upper Each of: The and First And", formatter.format("an upper each of: the and first and"));
    }

    @Test
    public void completeWordsInCurlyBracketsIsLeftUnchanged() {
        Assertions.assertEquals("An Upper First with and without {CURLY} {brackets}", formatter.format("AN UPPER FIRST WITH AND WITHOUT {CURLY} {brackets}"));
    }

    @Test
    public void lettersInCurlyBracketsIsLeftUnchanged() {
        Assertions.assertEquals("An Upper First with {A}nd without {C}urly {b}rackets", formatter.format("AN UPPER FIRST WITH {A}ND WITHOUT {C}URLY {b}rackets"));
    }

    @Test
    public void intraWordLettersInCurlyBracketsIsLeftUnchanged() {
        Assertions.assertEquals("{b}rackets {b}rac{K}ets Brack{E}ts", formatter.format("{b}RaCKeTS {b}RaC{K}eTS bRaCK{E}ts"));
    }

    @Test
    public void testTwoExperiencesTitle() {
        Assertions.assertEquals("Two Experiences Designing for Effective Security", formatter.format("Two experiences designing for effective security"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("{BPMN} Conformance in Open Source Engines", formatter.format(formatter.getExampleInput()));
    }
}

