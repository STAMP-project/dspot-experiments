package com.vaadin.ui.declarative;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link Design} declarative support class.
 *
 * @author Vaadin Ltd
 */
public class DesignTest {
    private static final Charset CP1251_CHARSET = Charset.forName("cp1251");

    private static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;

    private static final String NON_ASCII_STRING = "\u043c";

    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    @Test
    public void write_cp1251SystemDefaultEncoding_resultEqualsToUtf8Encoding() throws IOException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        setCp1251Charset();
        String cp1251Html = getHtml();
        setUtf8Charset();
        String utf8Html = getHtml();
        Assert.assertEquals(("Html written with UTF-8 as default encoding " + "differs from html written with cp1251 encoding"), cp1251Html, utf8Html);
    }

    @Test
    public void write_cp1251SystemDefaultEncoding_writtenLabelHasCorrectValue() throws IOException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        setCp1251Charset();
        String cp1251Html = getHtml();
        Assert.assertEquals(("Non ascii string parsed from serialized HTML " + "differs from expected"), DesignTest.NON_ASCII_STRING, getHtmlLabelValue(cp1251Html));
    }

    @Test
    public void write_utf8SystemDefaultEncoding_writtenLabelHasCorrectValue() throws IOException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        setUtf8Charset();
        String utf8 = getHtml();
        Assert.assertEquals(("Non ascii string parsed from serialized HTML " + "differs from expected"), DesignTest.NON_ASCII_STRING, getHtmlLabelValue(utf8));
    }
}

