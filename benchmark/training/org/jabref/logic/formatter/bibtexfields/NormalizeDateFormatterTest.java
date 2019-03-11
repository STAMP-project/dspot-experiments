package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class NormalizeDateFormatterTest {
    private NormalizeDateFormatter formatter;

    @Test
    public void formatDateYYYYMM0D() {
        Assertions.assertEquals("2015-11-08", formatter.format("2015-11-08"));
    }

    @Test
    public void formatDateYYYYM0D() {
        Assertions.assertEquals("2015-01-08", formatter.format("2015-1-08"));
    }

    @Test
    public void formatDateYYYYMD() {
        Assertions.assertEquals("2015-01-08", formatter.format("2015-1-8"));
    }

    @Test
    public void formatDateYYYYMM() {
        Assertions.assertEquals("2015-11", formatter.format("2015-11"));
    }

    @Test
    public void formatDateYYYYM() {
        Assertions.assertEquals("2015-01", formatter.format("2015-1"));
    }

    @Test
    public void formatDateMMYY() {
        Assertions.assertEquals("2015-11", formatter.format("11/15"));
    }

    @Test
    public void formatDateMYY() {
        Assertions.assertEquals("2015-01", formatter.format("1/15"));
    }

    @Test
    public void formatDate0MYY() {
        Assertions.assertEquals("2015-01", formatter.format("01/15"));
    }

    @Test
    public void formatDateMMYYYY() {
        Assertions.assertEquals("2015-11", formatter.format("11/2015"));
    }

    @Test
    public void formatDateMYYYY() {
        Assertions.assertEquals("2015-01", formatter.format("1/2015"));
    }

    @Test
    public void formatDate0MYYYY() {
        Assertions.assertEquals("2015-01", formatter.format("01/2015"));
    }

    @Test
    public void formatDateMMMDDCommaYYYY() {
        Assertions.assertEquals("2015-11-08", formatter.format("November 08, 2015"));
    }

    @Test
    public void formatDateMMMDCommaYYYY() {
        Assertions.assertEquals("2015-11-08", formatter.format("November 8, 2015"));
    }

    @Test
    public void formatDateMMMCommaYYYY() {
        Assertions.assertEquals("2015-11", formatter.format("November, 2015"));
    }

    @Test
    public void formatDate0DdotMMdotYYYY() {
        Assertions.assertEquals("2015-11-08", formatter.format("08.11.2015"));
    }

    @Test
    public void formatDateDdotMMdotYYYY() {
        Assertions.assertEquals("2015-11-08", formatter.format("8.11.2015"));
    }

    @Test
    public void formatDateDDdotMMdotYYYY() {
        Assertions.assertEquals("2015-11-15", formatter.format("15.11.2015"));
    }

    @Test
    public void formatDate0Ddot0MdotYYYY() {
        Assertions.assertEquals("2015-01-08", formatter.format("08.01.2015"));
    }

    @Test
    public void formatDateDdot0MdotYYYY() {
        Assertions.assertEquals("2015-01-08", formatter.format("8.01.2015"));
    }

    @Test
    public void formatDateDDdot0MdotYYYY() {
        Assertions.assertEquals("2015-01-15", formatter.format("15.01.2015"));
    }

    @Test
    public void formatDate0DdotMdotYYYY() {
        Assertions.assertEquals("2015-01-08", formatter.format("08.1.2015"));
    }

    @Test
    public void formatDateDdotMdotYYYY() {
        Assertions.assertEquals("2015-01-08", formatter.format("8.1.2015"));
    }

    @Test
    public void formatDateDDdotMdotYYYY() {
        Assertions.assertEquals("2015-01-15", formatter.format("15.1.2015"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("2003-11-29", formatter.format(formatter.getExampleInput()));
    }
}

