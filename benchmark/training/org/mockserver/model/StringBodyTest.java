package org.mockserver.model;


import Body.Type.STRING;
import com.google.common.net.MediaType;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class StringBodyTest {
    @Test
    public void shouldAlwaysCreateNewObject() {
        Assert.assertEquals(new StringBody("some_body").exact("some_body"), StringBody.exact("some_body"));
        Assert.assertNotSame(StringBody.exact("some_body"), StringBody.exact("some_body"));
    }

    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        StringBody stringBody = new StringBody("some_body");
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getCharset(null), Matchers.nullValue());
        MatcherAssert.assertThat(stringBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(stringBody.getContentType(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnValuesSetInConstructorWithSubString() {
        // when
        StringBody stringBody = new StringBody("some_body", true);
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(true));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getContentType(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnValuesSetInConstructorWithCharset() {
        // when
        StringBody stringBody = new StringBody("some_body", StandardCharsets.UTF_16);
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getCharset(null), Is.is(StandardCharsets.UTF_16));
        MatcherAssert.assertThat(stringBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_16));
        MatcherAssert.assertThat(stringBody.getContentType(), Is.is(MediaType.create("text", "plain").withCharset(StandardCharsets.UTF_16).toString()));
    }

    @Test
    public void shouldReturnValueSetInStaticExactConstructor() {
        // when
        StringBody stringBody = StringBody.exact("some_body");
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(false));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getCharset(null), Matchers.nullValue());
        MatcherAssert.assertThat(stringBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(stringBody.getContentType(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnValueSetInStaticExactConstructorWithCharset() {
        // when
        StringBody stringBody = StringBody.exact("some_body", StandardCharsets.UTF_16);
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(false));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getCharset(null), Is.is(StandardCharsets.UTF_16));
        MatcherAssert.assertThat(stringBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_16));
        MatcherAssert.assertThat(stringBody.getContentType(), Is.is(MediaType.create("text", "plain").withCharset(StandardCharsets.UTF_16).toString()));
    }

    @Test
    public void shouldReturnValueSetInStaticExactConstructorWithNullCharset() {
        // when
        StringBody stringBody = StringBody.exact("some_body", ((Charset) (null)));
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(false));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getCharset(null), Matchers.nullValue());
        MatcherAssert.assertThat(stringBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(stringBody.getContentType(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnValueSetInStaticExactConstructorWithContentType() {
        // when
        StringBody stringBody = StringBody.exact("some_body", MediaType.PLAIN_TEXT_UTF_8);
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(false));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getCharset(null), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(stringBody.getCharset(StandardCharsets.UTF_16), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(stringBody.getContentType(), Is.is(MediaType.PLAIN_TEXT_UTF_8.toString()));
    }

    @Test
    public void shouldReturnValueSetInStaticExactConstructorWithNullMediaType() {
        // when
        StringBody stringBody = StringBody.exact("some_body", ((MediaType) (null)));
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(false));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getCharset(null), Matchers.nullValue());
        MatcherAssert.assertThat(stringBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(stringBody.getContentType(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnValueSetInStaticSubStringConstructor() {
        // when
        StringBody stringBody = StringBody.subString("some_body");
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(true));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getCharset(null), Matchers.nullValue());
        MatcherAssert.assertThat(stringBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(stringBody.getContentType(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnValueSetInStaticSubStringConstructorWithCharset() {
        // when
        StringBody stringBody = StringBody.subString("some_body", StandardCharsets.UTF_16);
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(true));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getCharset(null), Is.is(StandardCharsets.UTF_16));
        MatcherAssert.assertThat(stringBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_16));
        MatcherAssert.assertThat(stringBody.getContentType(), Is.is(MediaType.create("text", "plain").withCharset(StandardCharsets.UTF_16).toString()));
    }

    @Test
    public void shouldReturnValueSetInStaticSubStringConstructorWithNullCharset() {
        // when
        StringBody stringBody = StringBody.subString("some_body", ((Charset) (null)));
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(true));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getCharset(null), Matchers.nullValue());
        MatcherAssert.assertThat(stringBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(stringBody.getContentType(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnValueSetInStaticSubStringConstructorWithContentType() {
        // when
        StringBody stringBody = StringBody.subString("some_body", MediaType.PLAIN_TEXT_UTF_8);
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(true));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getCharset(null), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(stringBody.getCharset(StandardCharsets.UTF_16), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(stringBody.getContentType(), Is.is(MediaType.PLAIN_TEXT_UTF_8.toString()));
    }

    @Test
    public void shouldReturnValueSetInStaticSubStringConstructorWithNullMediaType() {
        // when
        StringBody stringBody = StringBody.subString("some_body", ((MediaType) (null)));
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(true));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getCharset(null), Matchers.nullValue());
        MatcherAssert.assertThat(stringBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(stringBody.getContentType(), Matchers.nullValue());
    }
}

