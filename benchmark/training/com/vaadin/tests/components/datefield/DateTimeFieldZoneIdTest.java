package com.vaadin.tests.components.datefield;


import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.Test;


public class DateTimeFieldZoneIdTest extends MultiBrowserTest {
    private static LocalDateTime THIRTY_OF_JULY = DateTimeFieldZoneId.INITIAL_DATE_TIME.plus(6, ChronoUnit.MONTHS).withDayOfMonth(30);

    @Test
    public void defaultDisplayName() {
        openTestURL();
        DateTimeFieldElement dateField = $(DateTimeFieldElement.class).first();
        dateField.openPopup();
        LocalDate initialDate = DateTimeFieldZoneId.INITIAL_DATE_TIME.toLocalDate();
        assertEndsWith(dateField, getUTCString(initialDate));
        dateField.setDateTime(DateTimeFieldZoneIdTest.THIRTY_OF_JULY);
        assertEndsWith(dateField, getUTCString(DateTimeFieldZoneIdTest.THIRTY_OF_JULY.toLocalDate()));
    }

    @Test
    public void zoneIdTokyo() {
        openTestURL();
        DateTimeFieldElement dateField = $(DateTimeFieldElement.class).first();
        setZoneId("Asia/Tokyo");
        dateField.openPopup();
        assertEndsWith(dateField, "JST");
        dateField.setDateTime(DateTimeFieldZoneIdTest.THIRTY_OF_JULY);
        assertEndsWith(dateField, "JST");
    }

    @Test
    public void zoneIdBerlin() {
        openTestURL();
        DateTimeFieldElement dateField = $(DateTimeFieldElement.class).first();
        setZoneId("Europe/Berlin");
        dateField.openPopup();
        assertEndsWith(dateField, "CET");
        dateField.setDateTime(DateTimeFieldZoneIdTest.THIRTY_OF_JULY);
        assertEndsWith(dateField, "CEST");
    }

    @Test
    public void defaultDisplayNameLocaleGerman() {
        openTestURL();
        setLocale("de");
        DateTimeFieldElement dateField = $(DateTimeFieldElement.class).first();
        dateField.openPopup();
        assertEndsWith(dateField, getUTCString(DateTimeFieldZoneId.INITIAL_DATE_TIME.toLocalDate()));
        dateField.setDateTime(DateTimeFieldZoneIdTest.THIRTY_OF_JULY);
        assertEndsWith(dateField, getUTCString(DateTimeFieldZoneIdTest.THIRTY_OF_JULY.toLocalDate()));
    }

    @Test
    public void zoneIdBeirutLocaleGerman() {
        openTestURL();
        DateTimeFieldElement dateField = $(DateTimeFieldElement.class).first();
        setZoneId("Asia/Beirut");
        setLocale("de");
        dateField.openPopup();
        assertEndsWith(dateField, "OEZ");
        dateField.setDateTime(DateTimeFieldZoneIdTest.THIRTY_OF_JULY);
        assertEndsWith(dateField, "OESZ");
    }

    @Test
    public void zInQuotes() {
        openTestURL();
        DateTimeFieldElement dateField = $(DateTimeFieldElement.class).first();
        setZoneId("Asia/Tokyo");
        TextFieldElement patternField = $(TextFieldElement.class).id(DateTimeFieldZoneId.PATTERN_ID);
        patternField.setValue("dd MMM yyyy - hh:mm:ss a 'z' z");
        dateField.openPopup();
        assertEndsWith(dateField, "z JST");
        dateField.setDateTime(DateTimeFieldZoneIdTest.THIRTY_OF_JULY);
        assertEndsWith(dateField, "z JST");
    }
}

