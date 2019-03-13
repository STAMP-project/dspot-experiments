package com.vaadin.tests.server.component.abstractdatefield;


import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.server.component.abstractfield.AbstractFieldDeclarativeTest;
import com.vaadin.ui.AbstractLocalDateField;
import java.time.LocalDate;
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
public abstract class AbstractLocalDateFieldDeclarativeTest<T extends AbstractLocalDateField> extends AbstractFieldDeclarativeTest<T, LocalDate> {
    @Test
    public void abstractDateFieldAttributesDeserialization() throws IllegalAccessException, InstantiationException {
        boolean showIsoWeeks = true;
        LocalDate end = LocalDate.of(2019, 1, 15);
        LocalDate start = LocalDate.of(2001, 2, 11);
        String dateOutOfRange = "test date out of range";
        DateResolution resolution = DateResolution.MONTH;
        String dateFormat = "test format";
        boolean lenient = true;
        String parseErrorMsg = "test parse error";
        String design = String.format(("<%s show-iso-week-numbers range-end='%s' range-start='%s' " + ("date-out-of-range-message='%s' resolution='%s' " + "date-format='%s' lenient parse-error-message='%s'/>")), getComponentTag(), end, start, dateOutOfRange, resolution.name().toLowerCase(Locale.ROOT), dateFormat, parseErrorMsg);
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

