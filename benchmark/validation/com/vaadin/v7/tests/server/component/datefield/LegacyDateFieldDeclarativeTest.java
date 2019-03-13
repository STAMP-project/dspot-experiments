package com.vaadin.v7.tests.server.component.datefield;


import Resolution.YEAR;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.DateField;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;


/**
 * Tests the declarative support for implementations of {@link DateField}.
 *
 * @author Vaadin Ltd
 * @since 7.4
 */
public class LegacyDateFieldDeclarativeTest extends DeclarativeTestBase<DateField> {
    @Test
    public void readTimezone() {
        testRead(getTimezoneDesign(), getTimezoneExpected());
    }

    @Test
    public void writeTimezone() {
        testWrite(getTimezoneDesign(), getTimezoneExpected());
    }

    @Test
    public void readYearResolution() {
        testRead(getYearResolutionDesign(), getYearResolutionExpected());
    }

    @Test
    public void writeYearResolution() {
        // Writing is always done in full resolution..
        String timeZone = new SimpleDateFormat("Z").format(new Date((2020 - 1900), (1 - 1), 1));
        testWrite(getYearResolutionDesign().replace("2020", ("2020-01-01 00:00:00" + timeZone)), getYearResolutionExpected());
    }

    @Test
    public void testReadOnlyValue() {
        Date date = new Date((2020 - 1900), (1 - 1), 1);
        String timeZone = new SimpleDateFormat("Z").format(date);
        String design = ("<vaadin7-date-field readonly resolution='year' value='2020-01-01 00:00:00" + timeZone) + "'/>";
        DateField df = new DateField();
        df.setResolution(YEAR);
        df.setValue(date);
        df.setReadOnly(true);
        testRead(design, df);
        testWrite(design, df);
    }
}

