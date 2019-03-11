package org.jabref.model.entry;


import Month.APRIL;
import Month.AUGUST;
import Month.DECEMBER;
import Month.FEBRUARY;
import Month.JANUARY;
import Month.JULY;
import Month.JUNE;
import Month.MARCH;
import Month.MAY;
import Month.NOVEMBER;
import Month.OCTOBER;
import Month.SEPTEMBER;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MonthTest {
    @Test
    public void parseCorrectlyByShortName() {
        Assertions.assertEquals(Optional.of(JANUARY), Month.parse("jan"));
        Assertions.assertEquals(Optional.of(FEBRUARY), Month.parse("feb"));
        Assertions.assertEquals(Optional.of(MARCH), Month.parse("mar"));
        Assertions.assertEquals(Optional.of(APRIL), Month.parse("apr"));
        Assertions.assertEquals(Optional.of(MAY), Month.parse("may"));
        Assertions.assertEquals(Optional.of(JUNE), Month.parse("jun"));
        Assertions.assertEquals(Optional.of(JULY), Month.parse("jul"));
        Assertions.assertEquals(Optional.of(AUGUST), Month.parse("aug"));
        Assertions.assertEquals(Optional.of(SEPTEMBER), Month.parse("sep"));
        Assertions.assertEquals(Optional.of(OCTOBER), Month.parse("oct"));
        Assertions.assertEquals(Optional.of(NOVEMBER), Month.parse("nov"));
        Assertions.assertEquals(Optional.of(DECEMBER), Month.parse("dec"));
    }

    @Test
    public void parseCorrectlyByBibtexName() {
        Assertions.assertEquals(Optional.of(JANUARY), Month.parse("#jan#"));
        Assertions.assertEquals(Optional.of(FEBRUARY), Month.parse("#feb#"));
        Assertions.assertEquals(Optional.of(MARCH), Month.parse("#mar#"));
        Assertions.assertEquals(Optional.of(APRIL), Month.parse("#apr#"));
        Assertions.assertEquals(Optional.of(MAY), Month.parse("#may#"));
        Assertions.assertEquals(Optional.of(JUNE), Month.parse("#jun#"));
        Assertions.assertEquals(Optional.of(JULY), Month.parse("#jul#"));
        Assertions.assertEquals(Optional.of(AUGUST), Month.parse("#aug#"));
        Assertions.assertEquals(Optional.of(SEPTEMBER), Month.parse("#sep#"));
        Assertions.assertEquals(Optional.of(OCTOBER), Month.parse("#oct#"));
        Assertions.assertEquals(Optional.of(NOVEMBER), Month.parse("#nov#"));
        Assertions.assertEquals(Optional.of(DECEMBER), Month.parse("#dec#"));
    }

    @Test
    public void parseCorrectlyByFullName() {
        Assertions.assertEquals(Optional.of(JANUARY), Month.parse("January"));
        Assertions.assertEquals(Optional.of(FEBRUARY), Month.parse("February"));
        Assertions.assertEquals(Optional.of(MARCH), Month.parse("March"));
        Assertions.assertEquals(Optional.of(APRIL), Month.parse("April"));
        Assertions.assertEquals(Optional.of(MAY), Month.parse("May"));
        Assertions.assertEquals(Optional.of(JUNE), Month.parse("June"));
        Assertions.assertEquals(Optional.of(JULY), Month.parse("July"));
        Assertions.assertEquals(Optional.of(AUGUST), Month.parse("August"));
        Assertions.assertEquals(Optional.of(SEPTEMBER), Month.parse("September"));
        Assertions.assertEquals(Optional.of(OCTOBER), Month.parse("October"));
        Assertions.assertEquals(Optional.of(NOVEMBER), Month.parse("November"));
        Assertions.assertEquals(Optional.of(DECEMBER), Month.parse("December"));
    }

    @Test
    public void parseCorrectlyByTwoDigitNumber() {
        Assertions.assertEquals(Optional.of(JANUARY), Month.parse("01"));
        Assertions.assertEquals(Optional.of(FEBRUARY), Month.parse("02"));
        Assertions.assertEquals(Optional.of(MARCH), Month.parse("03"));
        Assertions.assertEquals(Optional.of(APRIL), Month.parse("04"));
        Assertions.assertEquals(Optional.of(MAY), Month.parse("05"));
        Assertions.assertEquals(Optional.of(JUNE), Month.parse("06"));
        Assertions.assertEquals(Optional.of(JULY), Month.parse("07"));
        Assertions.assertEquals(Optional.of(AUGUST), Month.parse("08"));
        Assertions.assertEquals(Optional.of(SEPTEMBER), Month.parse("09"));
        Assertions.assertEquals(Optional.of(OCTOBER), Month.parse("10"));
        Assertions.assertEquals(Optional.of(NOVEMBER), Month.parse("11"));
        Assertions.assertEquals(Optional.of(DECEMBER), Month.parse("12"));
    }

    @Test
    public void parseCorrectlyByNumber() {
        Assertions.assertEquals(Optional.of(JANUARY), Month.parse("1"));
        Assertions.assertEquals(Optional.of(FEBRUARY), Month.parse("2"));
        Assertions.assertEquals(Optional.of(MARCH), Month.parse("3"));
        Assertions.assertEquals(Optional.of(APRIL), Month.parse("4"));
        Assertions.assertEquals(Optional.of(MAY), Month.parse("5"));
        Assertions.assertEquals(Optional.of(JUNE), Month.parse("6"));
        Assertions.assertEquals(Optional.of(JULY), Month.parse("7"));
        Assertions.assertEquals(Optional.of(AUGUST), Month.parse("8"));
        Assertions.assertEquals(Optional.of(SEPTEMBER), Month.parse("9"));
        Assertions.assertEquals(Optional.of(OCTOBER), Month.parse("10"));
        Assertions.assertEquals(Optional.of(NOVEMBER), Month.parse("11"));
        Assertions.assertEquals(Optional.of(DECEMBER), Month.parse("12"));
    }

    @Test
    public void parseReturnsEmptyOptionalForInvalidInput() {
        Assertions.assertEquals(Optional.empty(), Month.parse(";lkjasdf"));
        Assertions.assertEquals(Optional.empty(), Month.parse("3.2"));
        Assertions.assertEquals(Optional.empty(), Month.parse("#test#"));
        Assertions.assertEquals(Optional.empty(), Month.parse("8,"));
    }

    @Test
    public void parseReturnsEmptyOptionalForEmptyInput() {
        Assertions.assertEquals(Optional.empty(), Month.parse(""));
    }

    @Test
    public void parseCorrectlyByShortNameGerman() {
        Assertions.assertEquals(Optional.of(JANUARY), Month.parse("Jan"));
        Assertions.assertEquals(Optional.of(FEBRUARY), Month.parse("Feb"));
        Assertions.assertEquals(Optional.of(MARCH), Month.parse("M?r"));
        Assertions.assertEquals(Optional.of(MARCH), Month.parse("Mae"));
        Assertions.assertEquals(Optional.of(APRIL), Month.parse("Apr"));
        Assertions.assertEquals(Optional.of(MAY), Month.parse("Mai"));
        Assertions.assertEquals(Optional.of(JUNE), Month.parse("Jun"));
        Assertions.assertEquals(Optional.of(JULY), Month.parse("Jul"));
        Assertions.assertEquals(Optional.of(AUGUST), Month.parse("Aug"));
        Assertions.assertEquals(Optional.of(SEPTEMBER), Month.parse("Sep"));
        Assertions.assertEquals(Optional.of(OCTOBER), Month.parse("Okt"));
        Assertions.assertEquals(Optional.of(NOVEMBER), Month.parse("Nov"));
        Assertions.assertEquals(Optional.of(DECEMBER), Month.parse("Dez"));
    }

    @Test
    public void parseCorrectlyByFullNameGerman() {
        Assertions.assertEquals(Optional.of(JANUARY), Month.parse("Januar"));
        Assertions.assertEquals(Optional.of(FEBRUARY), Month.parse("Februar"));
        Assertions.assertEquals(Optional.of(MARCH), Month.parse("M?rz"));
        Assertions.assertEquals(Optional.of(MARCH), Month.parse("Maerz"));
        Assertions.assertEquals(Optional.of(APRIL), Month.parse("April"));
        Assertions.assertEquals(Optional.of(MAY), Month.parse("Mai"));
        Assertions.assertEquals(Optional.of(JUNE), Month.parse("Juni"));
        Assertions.assertEquals(Optional.of(JULY), Month.parse("Juli"));
        Assertions.assertEquals(Optional.of(AUGUST), Month.parse("August"));
        Assertions.assertEquals(Optional.of(SEPTEMBER), Month.parse("September"));
        Assertions.assertEquals(Optional.of(OCTOBER), Month.parse("Oktober"));
        Assertions.assertEquals(Optional.of(NOVEMBER), Month.parse("November"));
        Assertions.assertEquals(Optional.of(DECEMBER), Month.parse("Dezember"));
    }

    @Test
    public void parseCorrectlyByShortNameGermanLowercase() {
        Assertions.assertEquals(Optional.of(JANUARY), Month.parse("jan"));
        Assertions.assertEquals(Optional.of(FEBRUARY), Month.parse("feb"));
        Assertions.assertEquals(Optional.of(MARCH), Month.parse("m?r"));
        Assertions.assertEquals(Optional.of(MARCH), Month.parse("mae"));
        Assertions.assertEquals(Optional.of(APRIL), Month.parse("apr"));
        Assertions.assertEquals(Optional.of(MAY), Month.parse("mai"));
        Assertions.assertEquals(Optional.of(JUNE), Month.parse("jun"));
        Assertions.assertEquals(Optional.of(JULY), Month.parse("jul"));
        Assertions.assertEquals(Optional.of(AUGUST), Month.parse("aug"));
        Assertions.assertEquals(Optional.of(SEPTEMBER), Month.parse("sep"));
        Assertions.assertEquals(Optional.of(OCTOBER), Month.parse("okt"));
        Assertions.assertEquals(Optional.of(NOVEMBER), Month.parse("nov"));
        Assertions.assertEquals(Optional.of(DECEMBER), Month.parse("dez"));
    }
}

