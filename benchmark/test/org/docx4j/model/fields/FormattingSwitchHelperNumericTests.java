package org.docx4j.model.fields;


import javax.xml.transform.TransformerException;
import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.junit.Assert;
import org.junit.Test;


public class FormattingSwitchHelperNumericTests {
    @Test
    public void testNumberNotSpecified() throws TransformerException, Docx4JException {
        // \# without arg results in Error! Switch argument not specified.
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# ", "123");
        doit("MERGEFIELD", data, "Error! Switch argument not specified.");
        doit("DOCPROPERTY", data, "Error! Switch argument not specified.");
    }

    @Test
    public void testNumber31() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# 00.00", "9");
        doit("MERGEFIELD", data, "09.00");
        doit("DOCPROPERTY", data, "09.00");
    }

    @Test
    public void testNumber32() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# 00.00", "9.006");
        doit("MERGEFIELD", data, "09.01");
        doit("DOCPROPERTY", data, "09.01");
    }

    @Test
    public void testNumber33() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# $###", "15");
        doit("MERGEFIELD", data, "$15");
        doit("DOCPROPERTY", data, "$15");
    }

    @Test
    public void testNumber34() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# ##.##", "9");
        doit("MERGEFIELD", data, "9");
        doit("DOCPROPERTY", data, "9");
    }

    @Test
    public void testNumber35() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# ##.##", "9.006");
        doit("MERGEFIELD", data, "9.01");
        doit("DOCPROPERTY", data, "9.01");
    }

    @Test
    public void testNumber36() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# ##.##0000", "9.006");
        doit("MERGEFIELD", data, "9.006");
        doit("DOCPROPERTY", data, "9.006");
    }

    @Test
    public void testNumber37() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# ##.##00#0", "9.006");
        doit("MERGEFIELD", data, "9.006");
        doit("DOCPROPERTY", data, "9.006");
    }

    @Test
    public void testNumber38() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# ####.0000", "123.456");
        doit("MERGEFIELD", data, "123.4560");
        doit("DOCPROPERTY", data, "123.4560");
    }

    @Test
    public void testNumber39() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# 0000.####", "123.456");
        doit("MERGEFIELD", data, "0123.456");
        doit("DOCPROPERTY", data, "0123.456");
    }

    @Test
    public void testNumber40() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# #.0000", "123.456");
        doit("MERGEFIELD", data, "123.4560");
        doit("DOCPROPERTY", data, "123.4560");
    }

    @Test
    public void testNumber41() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# 0.####", "123.456");
        doit("MERGEFIELD", data, "123.456");
        doit("DOCPROPERTY", data, "123.456");
    }

    @Test
    public void testNumber44() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# 0.00x", "0.125678");
        doit("MERGEFIELD", data, "0.126");
        doit("DOCPROPERTY", data, "0.126");
    }

    @Test
    public void testNumber45() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# .x", "0.75");
        doit("MERGEFIELD", data, ".8");
        doit("DOCPROPERTY", data, ".8");
    }

    @Test
    public void testNumber46() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# .000x", "0.75");
        doit("MERGEFIELD", data, ".7500");
        doit("DOCPROPERTY", data, ".7500");
    }

    @Test
    public void testNumber47() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# .###x", "0.75");
        doit("MERGEFIELD", data, ".75");
        doit("DOCPROPERTY", data, ".75");
    }

    @Test
    public void testNumber48() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# .###x", ".75");
        doit("MERGEFIELD", data, ".75");
        doit("DOCPROPERTY", data, ".75");
    }

    @Test
    public void testNumber55() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# 0.00xx", "0.125678");
        doit("MERGEFIELD", data, "0.1257");
        doit("DOCPROPERTY", data, "0.1257");
    }

    @Test
    public void testNumber56() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# 0.x0x", "0.125678");
        doit("MERGEFIELD", data, "0.126");
        doit("DOCPROPERTY", data, "0.126");
    }

    @Test
    public void testNumber57() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# $###.00", "95.4");
        doit("MERGEFIELD", data, "$95.40");
        doit("DOCPROPERTY", data, "$95.40");
    }

    @Test
    public void testNumber60() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# $#,###,###", "2456800");
        doit("MERGEFIELD", data, "$2,456,800");
        doit("DOCPROPERTY", data, "$2,456,800");
    }

    @Test
    public void testNumber61() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# -##", "-10");
        doit("MERGEFIELD", data, "-10");
        doit("DOCPROPERTY", data, "-10");
    }

    @Test
    public void testNumber62() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# -##", "10");
        doit("MERGEFIELD", data, "10");
        doit("DOCPROPERTY", data, "10");
    }

    @Test
    public void testNumber63() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# +##", "10");
        doit("MERGEFIELD", data, "10");
        doit("DOCPROPERTY", data, "10");
    }

    @Test
    public void testNumber64() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# +##", "-10");
        doit("MERGEFIELD", data, "-10");
        doit("DOCPROPERTY", data, "-10");
    }

    @Test
    public void testNumber65() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# ##%", "33");
        doit("MERGEFIELD", data, "33%");
        doit("DOCPROPERTY", data, "33%");
    }

    @Test
    public void testNumber68() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# \"$##0.00 \'is the sales tax\'\"", "3.89");
        doit("MERGEFIELD", data, "$3.89 is the sales tax");
        doit("DOCPROPERTY", data, "$3.89 is the sales tax");
    }

    @Test
    public void testNumber69() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# \"$##0.00 \'is the ##.00 goodness\'\"", "3.89");
        doit("MERGEFIELD", data, "$3.89 is the ##.00 goodness");
        doit("DOCPROPERTY", data, "$3.89 is the ##.00 goodness");
    }

    @Test
    public void testNumber70() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# \"$##0.00 is the \'##.00\' goodness\"", "3.89");
        doit("MERGEFIELD", data, "$3.89 is the ##.00 goodness");
        doit("DOCPROPERTY", data, "$3.89 is the ##.00 goodness");
    }

    @Test
    public void testNumber72() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# 00", "?180,000.00 EUR");
        doit("MERGEFIELD", data, "180000");
        doit("DOCPROPERTY", data, "180000");
    }

    @Test
    public void testNumber42() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# x#.#x", "123456");
        try {
            doit("MERGEFIELD", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
        try {
            doit("DOCPROPERTY", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
    }

    @Test
    public void testNumber43() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# x#.#x", "123.456");
        try {
            doit("MERGEFIELD", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
        try {
            doit("DOCPROPERTY", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
    }

    @Test
    public void testNumber49() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# .###x", "-0.75");
        try {
            doit("MERGEFIELD", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
        try {
            doit("DOCPROPERTY", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
    }

    @Test
    public void testNumber50() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# .###x", "-.75");
        try {
            doit("MERGEFIELD", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
        try {
            doit("DOCPROPERTY", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
    }

    @Test
    public void testNumber51() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# .###", "-0.75");
        try {
            doit("MERGEFIELD", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
        try {
            doit("DOCPROPERTY", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
    }

    @Test
    public void testNumber52() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# .###", "-.75");
        try {
            doit("MERGEFIELD", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
        try {
            doit("DOCPROPERTY", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
    }

    @Test
    public void testNumber53() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# .000", "-0.75");
        try {
            doit("MERGEFIELD", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
        try {
            doit("DOCPROPERTY", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
    }

    @Test
    public void testNumber54() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# .000", "-.75");
        try {
            doit("MERGEFIELD", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
        try {
            doit("DOCPROPERTY", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
    }

    @Test
    public void testNumber58() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# .00", "95.4");
        try {
            doit("MERGEFIELD", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
        try {
            doit("DOCPROPERTY", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
    }

    @Test
    public void testNumber59() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# .##", "95.4");
        try {
            doit("MERGEFIELD", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
        try {
            doit("DOCPROPERTY", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
    }

    @Test
    public void testNumber66() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# #%#", "33");
        try {
            doit("MERGEFIELD", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
        try {
            doit("DOCPROPERTY", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
    }

    @Test
    public void testNumber67() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# #$#", "33");
        try {
            doit("MERGEFIELD", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
        try {
            doit("DOCPROPERTY", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
    }

    @Test
    public void testNumber71() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# #\'y\'#", "34");
        try {
            doit("MERGEFIELD", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
        try {
            doit("DOCPROPERTY", data, "class org.docx4j.model.fields.FieldFormattingException");
        } catch (Exception e) {
            System.out.println(((("[" + (data.toString())) + "] ") + (e.getMessage())));
            Assert.assertTrue(FieldFormattingException.class.isAssignableFrom(e.getClass()));
        }
    }

    @Test
    public void testNumber73() throws TransformerException, Docx4JException {
        Docx4jProperties.setProperty("docx4j.Fields.Numbers.JavaStylePercentHandling", true);
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# ##%", "33");
        doit("MERGEFIELD", data, "3300%");
        doit("DOCPROPERTY", data, "3300%");
        Docx4jProperties.setProperty("docx4j.Fields.Numbers.JavaStylePercentHandling", false);
    }

    @Test
    public void testNumber74() throws TransformerException, Docx4JException {
        Docx4jProperties.setProperty("docx4j.Fields.Numbers.JavaStylePercentHandling", true);
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# ##%", ".33");
        doit("MERGEFIELD", data, "33%");
        doit("DOCPROPERTY", data, "33%");
        Docx4jProperties.setProperty("docx4j.Fields.Numbers.JavaStylePercentHandling", false);
    }

    @Test
    public void testNumber75() throws TransformerException, Docx4JException {
        Docx4jProperties.setProperty("docx4j.Fields.Numbers.JavaStylePercentHandling", true);
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# ##%", "0.33");
        doit("MERGEFIELD", data, "33%");
        doit("DOCPROPERTY", data, "33%");
        Docx4jProperties.setProperty("docx4j.Fields.Numbers.JavaStylePercentHandling", false);
    }

    @Test
    public void testNumber76() throws TransformerException, Docx4JException {
        Docx4jProperties.setProperty("docx4j.Fields.Numbers.JavaStylePercentHandling", true);
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# ##%", "1.33");
        doit("MERGEFIELD", data, "133%");
        doit("DOCPROPERTY", data, "133%");
        Docx4jProperties.setProperty("docx4j.Fields.Numbers.JavaStylePercentHandling", false);
    }

    // All these should return Error! Switch argument not specified.
    @Test
    public void testNumber1() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# ", "AA");
        doit("MERGEFIELD", data, "Error! Switch argument not specified.");
        doit("DOCPROPERTY", data, "Error! Switch argument not specified.");
    }

    @Test
    public void testNumber2() throws TransformerException, Docx4JException {
        FormattingSwitchHelperNumericTests.SwitchTestData data = new FormattingSwitchHelperNumericTests.SwitchTestData("\\# ", "123.4500");
        doit("MERGEFIELD", data, "Error! Switch argument not specified.");
        doit("DOCPROPERTY", data, "Error! Switch argument not specified.");
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

