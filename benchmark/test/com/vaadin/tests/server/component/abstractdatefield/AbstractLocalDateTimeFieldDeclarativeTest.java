package com.vaadin.tests.server.component.abstractdatefield;


import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.tests.server.component.abstractfield.AbstractFieldDeclarativeTest;
import com.vaadin.ui.AbstractLocalDateTimeField;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.junit.Test;


/**
 * Abstract test class which contains tests for declarative format for
 * properties that are common for AbstractDateField.
 * <p>
 * It's an abstract so it's not supposed to be run as is. Instead each
 * declarative test for a real component should extend it and implement abstract
 * methods to be able to test the common properties. Components specific
 * properties should be tested additionally in the subclasses implementations.
 *
 * @author Vaadin Ltd
 */
public abstract class AbstractLocalDateTimeFieldDeclarativeTest<T extends AbstractLocalDateTimeField> extends AbstractFieldDeclarativeTest<T, LocalDateTime> {
    protected DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    @Test
    public void abstractDateFieldAttributesDeserialization() throws IllegalAccessException, InstantiationException {
        boolean showIsoWeeks = true;
        LocalDateTime end = LocalDateTime.of(2019, 2, 27, 10, 37, 43);
        LocalDateTime start = LocalDateTime.of(2001, 2, 27, 23, 12, 34);
        String dateOutOfRange = "test date out of range";
        DateTimeResolution resolution = DateTimeResolution.HOUR;
        String dateFormat = "test format";
        boolean lenient = true;
        String parseErrorMsg = "test parse error";
        String design = String.format(("<%s show-iso-week-numbers range-end='%s' range-start='%s' " + ("date-out-of-range-message='%s' resolution='%s' " + "date-format='%s' lenient parse-error-message='%s'/>")), getComponentTag(), DATE_FORMATTER.format(end), DATE_FORMATTER.format(start), dateOutOfRange, resolution.name().toLowerCase(Locale.ROOT), dateFormat, parseErrorMsg);
        T component = getComponentClass().newInstance();
        setShowISOWeekNumbers(showIsoWeeks);
        setRangeEnd(end);
        setRangeStart(start);
        setDateOutOfRangeMessage(dateOutOfRange);
        component.setResolution(resolution);
        setDateFormat(dateFormat);
        setLenient(lenient);
        setParseErrorMessage(parseErrorMsg);
        testRead(design, component);
        testWrite(design, component);
    }
}

