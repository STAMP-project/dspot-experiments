package com.baeldung.algorithms.moneywords;


import NumberWordConverter.INVALID_INPUT_GIVEN;
import com.baeldung.algorithms.numberwordconverter.NumberWordConverter;
import org.junit.Assert;
import org.junit.Test;


public class NumberWordConverterUnitTest {
    @Test
    public void whenMoneyNegative_thenReturnInvalidInput() {
        Assert.assertEquals(INVALID_INPUT_GIVEN, NumberWordConverter.getMoneyIntoWords((-13)));
    }

    @Test
    public void whenZeroDollarsGiven_thenReturnEmptyString() {
        Assert.assertEquals("", NumberWordConverter.getMoneyIntoWords(0));
    }

    @Test
    public void whenOnlyDollarsGiven_thenReturnWords() {
        Assert.assertEquals("one dollar", NumberWordConverter.getMoneyIntoWords(1));
    }

    @Test
    public void whenOnlyCentsGiven_thenReturnWords() {
        Assert.assertEquals("sixty cents", NumberWordConverter.getMoneyIntoWords(0.6));
    }

    @Test
    public void whenAlmostAMillioDollarsGiven_thenReturnWords() {
        String expectedResult = "nine hundred ninety nine thousand nine hundred ninety nine dollars";
        Assert.assertEquals(expectedResult, NumberWordConverter.getMoneyIntoWords(999999));
    }

    @Test
    public void whenThirtyMillionDollarsGiven_thenReturnWords() {
        String expectedResult = "thirty three million three hundred forty eight thousand nine hundred seventy eight dollars";
        Assert.assertEquals(expectedResult, NumberWordConverter.getMoneyIntoWords(33348978));
    }

    @Test
    public void whenTwoBillionDollarsGiven_thenReturnWords() {
        String expectedResult = "two billion one hundred thirty three million two hundred forty seven thousand eight hundred ten dollars";
        Assert.assertEquals(expectedResult, NumberWordConverter.getMoneyIntoWords(2133247810));
    }

    @Test
    public void whenGivenDollarsAndCents_thenReturnWords() {
        String expectedResult = "nine hundred twenty four dollars and sixty cents";
        Assert.assertEquals(expectedResult, NumberWordConverter.getMoneyIntoWords(924.6));
    }

    @Test
    public void whenOneDollarAndNoCents_thenReturnDollarSingular() {
        Assert.assertEquals("one dollar", NumberWordConverter.getMoneyIntoWords(1));
    }

    @Test
    public void whenNoDollarsAndOneCent_thenReturnCentSingular() {
        Assert.assertEquals("one cent", NumberWordConverter.getMoneyIntoWords(0.01));
    }

    @Test
    public void whenNoDollarsAndTwoCents_thenReturnCentsPlural() {
        Assert.assertEquals("two cents", NumberWordConverter.getMoneyIntoWords(0.02));
    }

    @Test
    public void whenNoDollarsAndNinetyNineCents_thenReturnWords() {
        Assert.assertEquals("ninety nine cents", NumberWordConverter.getMoneyIntoWords(0.99));
    }

    @Test
    public void whenNoDollarsAndNineFiveNineCents_thenCorrectRounding() {
        Assert.assertEquals("ninety six cents", NumberWordConverter.getMoneyIntoWords(0.959));
    }

    @Test
    public void whenGivenDollarsAndCents_thenReturnWordsVersionTwo() {
        Assert.assertEquals("three hundred ten ? 00/100", NumberWordConverter.getMoneyIntoWords("310"));
    }
}

