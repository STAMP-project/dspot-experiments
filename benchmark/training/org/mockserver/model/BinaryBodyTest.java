package org.mockserver.model;


import Body.Type.BINARY;
import com.google.common.net.MediaType;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.DatatypeConverter;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.serialization.Base64Converter;


/**
 *
 *
 * @author jamesdbloom
 */
public class BinaryBodyTest {
    private final Base64Converter base64Converter = new Base64Converter();

    @Test
    public void shouldAlwaysCreateNewObject() {
        byte[] body = DatatypeConverter.parseBase64Binary("some_body");
        Assert.assertEquals(new BinaryBody(body).binary(body), BinaryBody.binary(body));
        Assert.assertNotSame(BinaryBody.binary(body), BinaryBody.binary(body));
    }

    @Test
    public void shouldReturnFormattedRequestInToString() {
        Assert.assertEquals(base64Converter.bytesToBase64String("some_body".getBytes(StandardCharsets.UTF_8)), BinaryBody.binary("some_body".getBytes(StandardCharsets.UTF_8)).toString());
    }

    @Test
    public void shouldReturnValuesSetInConstructor() {
        // given
        byte[] body = DatatypeConverter.parseBase64Binary("some_body");
        // when
        BinaryBody binaryBody = new BinaryBody(body);
        // then
        MatcherAssert.assertThat(binaryBody.getValue(), Is.is(body));
        MatcherAssert.assertThat(binaryBody.getType(), Is.is(BINARY));
        MatcherAssert.assertThat(binaryBody.getCharset(null), Matchers.nullValue());
        MatcherAssert.assertThat(binaryBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(binaryBody.getContentType(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnValueSetInStaticConstructor() {
        // given
        byte[] body = DatatypeConverter.parseBase64Binary("some_body");
        // when
        BinaryBody binaryBody = BinaryBody.binary(body);
        // then
        MatcherAssert.assertThat(binaryBody.getValue(), Is.is(body));
        MatcherAssert.assertThat(binaryBody.getType(), Is.is(BINARY));
        MatcherAssert.assertThat(binaryBody.getCharset(null), Matchers.nullValue());
        MatcherAssert.assertThat(binaryBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(binaryBody.getContentType(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnValueSetInStaticConstructorWithCharsetAndMediaType() {
        // given
        byte[] body = DatatypeConverter.parseBase64Binary("some_body");
        // when
        BinaryBody binaryBody = BinaryBody.binary(body, MediaType.PLAIN_TEXT_UTF_8);
        // then
        MatcherAssert.assertThat(binaryBody.getValue(), Is.is(body));
        MatcherAssert.assertThat(binaryBody.getType(), Is.is(BINARY));
        MatcherAssert.assertThat(binaryBody.getCharset(null), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(binaryBody.getCharset(StandardCharsets.UTF_16), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(binaryBody.getContentType(), Is.is(MediaType.PLAIN_TEXT_UTF_8.toString()));
    }

    @Test
    public void shouldReturnValueSetInStaticConstructorWithNullMediaType() {
        // given
        byte[] body = DatatypeConverter.parseBase64Binary("some_body");
        // when
        BinaryBody binaryBody = BinaryBody.binary(body, null);
        // then
        MatcherAssert.assertThat(binaryBody.getValue(), Is.is(body));
        MatcherAssert.assertThat(binaryBody.getType(), Is.is(BINARY));
        MatcherAssert.assertThat(binaryBody.getCharset(null), Matchers.nullValue());
        MatcherAssert.assertThat(binaryBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(binaryBody.getContentType(), Matchers.nullValue());
    }
}

