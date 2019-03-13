package com.vaadin.tests.data.converter;


import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToBigIntegerConverter;
import java.math.BigInteger;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class StringToBigIntegerConverterTest extends AbstractStringConverterTest {
    @Test
    public void testValueParsing() {
        String bigInt = "1180591620717411303424";// 2^70 > 2^63 - 1

        Result<BigInteger> converted = getConverter().convertToModel(bigInt, new ValueContext());
        BigInteger expected = new BigInteger(bigInt);
        assertValue("Value bigger than max long was converted incorrectly", expected, converted);
    }

    @Test
    public void testValueFormatting() {
        BigInteger bd = new BigInteger("1000");
        String expected = "1.000";
        String converted = getConverter().convertToPresentation(bd, new ValueContext(Locale.GERMAN));
        Assert.assertEquals("Value with specific locale was converted incorrectly", expected, converted);
    }

    @Test
    public void customEmptyValue() {
        StringToBigIntegerConverter converter = new StringToBigIntegerConverter(BigInteger.ZERO, getErrorMessage());
        assertValue(BigInteger.ZERO, converter.convertToModel("", new ValueContext()));
        Assert.assertEquals("0", converter.convertToPresentation(BigInteger.ZERO, new ValueContext()));
    }
}

