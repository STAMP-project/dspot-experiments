package org.mockserver.serialization;


import java.nio.charset.StandardCharsets;
import javax.xml.bind.DatatypeConverter;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class Base64ConverterTest {
    private final Base64Converter base64Converter = new Base64Converter();

    @Test
    public void shouldConvertToBase64Value() {
        MatcherAssert.assertThat(base64Converter.bytesToBase64String("some_value".getBytes(StandardCharsets.UTF_8)), CoreMatchers.is(DatatypeConverter.printBase64Binary("some_value".getBytes(StandardCharsets.UTF_8))));
        MatcherAssert.assertThat(base64Converter.bytesToBase64String("some_value".getBytes(StandardCharsets.UTF_8)), CoreMatchers.is("c29tZV92YWx1ZQ=="));
    }

    @Test
    public void shouldConvertBase64ValueToString() {
        MatcherAssert.assertThat(new String(base64Converter.base64StringToBytes(DatatypeConverter.printBase64Binary("some_value".getBytes(StandardCharsets.UTF_8)))), CoreMatchers.is("some_value"));
        MatcherAssert.assertThat(base64Converter.base64StringToBytes("c29tZV92YWx1ZQ=="), CoreMatchers.is("some_value".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldConvertBase64NullValueToString() {
        MatcherAssert.assertThat(new String(base64Converter.base64StringToBytes(null)), CoreMatchers.is(""));
    }

    @Test
    public void shouldNotConvertNoneBase64Value() {
        MatcherAssert.assertThat(new String(base64Converter.base64StringToBytes("some_value")), CoreMatchers.is("some_value"));
    }
}

