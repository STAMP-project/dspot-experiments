package com.baeldung.string;


import org.junit.Assert;
import org.junit.Test;


public class TitleCaseConverterUnitTest {
    private static final String TEXT = "tHis IS a tiTLe";

    private static final String TEXT_EXPECTED = "This Is A Title";

    private static final String TEXT_EXPECTED_NOT_FULL = "THis IS A TiTLe";

    private static final String TEXT_OTHER_DELIMITERS = "tHis, IS a   tiTLe";

    private static final String TEXT_EXPECTED_OTHER_DELIMITERS = "This, Is A   Title";

    private static final String TEXT_EXPECTED_OTHER_DELIMITERS_NOT_FULL = "THis, IS A   TiTLe";

    @Test
    public void whenConvertingToTitleCaseIterating_thenStringConverted() {
        Assert.assertEquals(TitleCaseConverterUnitTest.TEXT_EXPECTED, TitleCaseConverter.convertToTitleCaseIteratingChars(TitleCaseConverterUnitTest.TEXT));
    }

    @Test
    public void whenConvertingToTitleCaseSplitting_thenStringConverted() {
        Assert.assertEquals(TitleCaseConverterUnitTest.TEXT_EXPECTED, TitleCaseConverter.convertToTitleCaseSplitting(TitleCaseConverterUnitTest.TEXT));
    }

    @Test
    public void whenConvertingToTitleCaseUsingWordUtilsFull_thenStringConverted() {
        Assert.assertEquals(TitleCaseConverterUnitTest.TEXT_EXPECTED, TitleCaseConverter.convertToTileCaseWordUtilsFull(TitleCaseConverterUnitTest.TEXT));
    }

    @Test
    public void whenConvertingToTitleCaseUsingWordUtils_thenStringConvertedOnlyFirstCharacter() {
        Assert.assertEquals(TitleCaseConverterUnitTest.TEXT_EXPECTED_NOT_FULL, TitleCaseConverter.convertToTileCaseWordUtils(TitleCaseConverterUnitTest.TEXT));
    }

    @Test
    public void whenConvertingToTitleCaseUsingIcu4j_thenStringConverted() {
        Assert.assertEquals(TitleCaseConverterUnitTest.TEXT_EXPECTED, TitleCaseConverter.convertToTitleCaseIcu4j(TitleCaseConverterUnitTest.TEXT));
    }

    @Test
    public void whenConvertingToTitleCaseWithDifferentDelimiters_thenDelimitersKept() {
        Assert.assertEquals(TitleCaseConverterUnitTest.TEXT_EXPECTED_OTHER_DELIMITERS, TitleCaseConverter.convertToTitleCaseIteratingChars(TitleCaseConverterUnitTest.TEXT_OTHER_DELIMITERS));
        Assert.assertEquals(TitleCaseConverterUnitTest.TEXT_EXPECTED_OTHER_DELIMITERS, TitleCaseConverter.convertToTitleCaseSplitting(TitleCaseConverterUnitTest.TEXT_OTHER_DELIMITERS));
        Assert.assertEquals(TitleCaseConverterUnitTest.TEXT_EXPECTED_OTHER_DELIMITERS, TitleCaseConverter.convertToTileCaseWordUtilsFull(TitleCaseConverterUnitTest.TEXT_OTHER_DELIMITERS));
        Assert.assertEquals(TitleCaseConverterUnitTest.TEXT_EXPECTED_OTHER_DELIMITERS_NOT_FULL, TitleCaseConverter.convertToTileCaseWordUtils(TitleCaseConverterUnitTest.TEXT_OTHER_DELIMITERS));
        Assert.assertEquals(TitleCaseConverterUnitTest.TEXT_EXPECTED_OTHER_DELIMITERS, TitleCaseConverter.convertToTitleCaseIcu4j(TitleCaseConverterUnitTest.TEXT_OTHER_DELIMITERS));
    }

    @Test
    public void givenNull_whenConvertingToTileCase_thenReturnNull() {
        Assert.assertEquals(null, TitleCaseConverter.convertToTitleCaseIteratingChars(null));
        Assert.assertEquals(null, TitleCaseConverter.convertToTitleCaseSplitting(null));
        Assert.assertEquals(null, TitleCaseConverter.convertToTileCaseWordUtilsFull(null));
        Assert.assertEquals(null, TitleCaseConverter.convertToTileCaseWordUtils(null));
        Assert.assertEquals(null, TitleCaseConverter.convertToTitleCaseIcu4j(null));
    }

    @Test
    public void givenEmptyString_whenConvertingToTileCase_thenReturnEmptyString() {
        Assert.assertEquals("", TitleCaseConverter.convertToTitleCaseIteratingChars(""));
        Assert.assertEquals("", TitleCaseConverter.convertToTitleCaseSplitting(""));
        Assert.assertEquals("", TitleCaseConverter.convertToTileCaseWordUtilsFull(""));
        Assert.assertEquals("", TitleCaseConverter.convertToTileCaseWordUtils(""));
        Assert.assertEquals("", TitleCaseConverter.convertToTitleCaseIcu4j(""));
    }
}

