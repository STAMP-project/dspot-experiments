package org.docx4j.model.fields;


import javax.xml.transform.TransformerException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.junit.Test;


public class FormattingSwitchHelperDateTests {
    static boolean wasDateFormatInferencerUSA = false;

    @Test
    public void testDate1() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@  ", "4/15/2013");
        doit("MERGEFIELD", data, "Error! Switch argument not specified.");
        doit("DOCPROPERTY", data, "Error! Switch argument not specified.");
    }

    @Test
    public void testDate2() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ M/d/yyyy", "4/15/2013");
        doit("MERGEFIELD", data, "4/15/2013");
        doit("DOCPROPERTY", data, "4/15/2013");
    }

    @Test
    public void testDate3() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ \"dddd, MMMM dd, yyyy\"", "4/15/2013");
        doit("MERGEFIELD", data, "Monday, April 15, 2013");
        doit("DOCPROPERTY", data, "Monday, April 15, 2013");
    }

    @Test
    public void testDate4() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ \"MMMM d, yyyy\"", "4/15/2013");
        doit("MERGEFIELD", data, "April 15, 2013");
        doit("DOCPROPERTY", data, "April 15, 2013");
    }

    @Test
    public void testDate5() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ M/d/yy", "4/15/2013");
        doit("MERGEFIELD", data, "4/15/13");
        doit("DOCPROPERTY", data, "4/15/13");
    }

    @Test
    public void testDate6() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ yyyy-MM-dd", "4/15/2013");
        doit("MERGEFIELD", data, "2013-04-15");
        doit("DOCPROPERTY", data, "2013-04-15");
    }

    @Test
    public void testDate7() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ d-MMM-yy", "4/15/2013");
        doit("MERGEFIELD", data, "15-Apr-13");
        doit("DOCPROPERTY", data, "15-Apr-13");
    }

    @Test
    public void testDate8() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ M.d.yyyy", "4/15/2013");
        doit("MERGEFIELD", data, "4.15.2013");
        doit("DOCPROPERTY", data, "4.15.2013");
    }

    @Test
    public void testDate9() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ \"MMM. d, yy\"", "4/15/2013");
        doit("MERGEFIELD", data, "Apr. 15, 13");
        doit("DOCPROPERTY", data, "Apr. 15, 13");
    }

    @Test
    public void testDate10() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ \"d MMMM yyyy\"", "4/15/2013");
        doit("MERGEFIELD", data, "15 April 2013");
        doit("DOCPROPERTY", data, "15 April 2013");
    }

    @Test
    public void testDate11() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ \"MMMM yy\"", "4/15/2013");
        doit("MERGEFIELD", data, "April 13");
        doit("DOCPROPERTY", data, "April 13");
    }

    @Test
    public void testDate12() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ MMM-yy", "4/15/2013");
        doit("MERGEFIELD", data, "Apr-13");
        doit("DOCPROPERTY", data, "Apr-13");
    }

    @Test
    public void testDate13() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ \"dddd, MMMM dd, yyyy\"", "4/15/2013");
        doit("MERGEFIELD", data, "Monday, April 15, 2013");
        doit("DOCPROPERTY", data, "Monday, April 15, 2013");
    }

    @Test
    public void testDate14() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ \"MMMM d, yyyy\"", "4/15/2013");
        doit("MERGEFIELD", data, "April 15, 2013");
        doit("DOCPROPERTY", data, "April 15, 2013");
    }

    @Test
    public void testDate15() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ \"MMM. d, yy\"", "4/15/2013");
        doit("MERGEFIELD", data, "Apr. 15, 13");
        doit("DOCPROPERTY", data, "Apr. 15, 13");
    }

    @Test
    public void testDate16() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ \"d MMMM yyyy\"", "4/15/2013");
        doit("MERGEFIELD", data, "15 April 2013");
        doit("DOCPROPERTY", data, "15 April 2013");
    }

    @Test
    public void testDate17() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ \"MMMM yy\"", "4/15/2013");
        doit("MERGEFIELD", data, "April 13");
        doit("DOCPROPERTY", data, "April 13");
    }

    // No quotes ....
    @Test
    public void testDateNoQuotes13() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ dddd, MMMM dd, yyyy", "4/15/2013");
        doit("MERGEFIELD", data, "Monday,");
        doit("DOCPROPERTY", data, "Monday,");
    }

    @Test
    public void testDateNoQuotes14() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ MMMM d, yyyy", "4/15/2013");
        doit("MERGEFIELD", data, "April");
        doit("DOCPROPERTY", data, "April");
    }

    @Test
    public void testDateNoQuotes15() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ MMM. d, yy", "4/15/2013");
        doit("MERGEFIELD", data, "Apr.");
        doit("DOCPROPERTY", data, "Apr.");
    }

    @Test
    public void testDateNoQuotes16() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ d MMMM yyyy", "4/15/2013");
        doit("MERGEFIELD", data, "15");
        doit("DOCPROPERTY", data, "15");
    }

    @Test
    public void testDateNoQuotes17() throws TransformerException, Docx4JException {
        FormattingSwitchHelperDateTests.SwitchTestData data = new FormattingSwitchHelperDateTests.SwitchTestData("\\@ MMMM yy", "4/15/2013");
        doit("MERGEFIELD", data, "April");
        doit("DOCPROPERTY", data, "April");
    }

    private static class SwitchTestData {
        String format;

        String val;

        public String toString() {
            return (("format " + (format)) + " to data ") + (val);
        }

        public SwitchTestData(String format, String val) {
            this.format = format;
            this.val = val;
        }
    }
}

