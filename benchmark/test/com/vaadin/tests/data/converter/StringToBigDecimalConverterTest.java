package com.vaadin.tests.data.converter;


import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import java.math.BigDecimal;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class StringToBigDecimalConverterTest extends AbstractStringConverterTest {
    @Test
    public void testValueParsing() {
        Result<BigDecimal> converted = getConverter().convertToModel("10", new ValueContext());
        BigDecimal expected = new BigDecimal(10);
        assertValue(expected, converted);
    }

    @Test
    public void testValueFormatting() {
        BigDecimal bd = new BigDecimal(12.5);
        String expected = "12,5";
        String converted = getConverter().convertToPresentation(bd, new ValueContext(Locale.GERMAN));
        Assert.assertEquals(expected, converted);
    }

    @Test
    public void customEmptyValue() {
        StringToBigDecimalConverter converter = new StringToBigDecimalConverter(BigDecimal.ZERO, getErrorMessage());
        assertValue(BigDecimal.ZERO, converter.convertToModel("", new ValueContext()));
        Assert.assertEquals("0", converter.convertToPresentation(BigDecimal.ZERO, new ValueContext()));
    }
}

