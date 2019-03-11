package org.mockserver.model;


import Body.Type.XML;
import XmlBody.DEFAULT_CONTENT_TYPE;
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
public class XmlBodyTest {
    @Test
    public void shouldAlwaysCreateNewObject() {
        Assert.assertEquals(new XmlBody("some_body").xml("some_body"), XmlBody.xml("some_body"));
        Assert.assertNotSame(XmlBody.xml("some_body"), XmlBody.xml("some_body"));
    }

    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        XmlBody xmlBody = new XmlBody("some_body");
        // then
        MatcherAssert.assertThat(xmlBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xmlBody.getType(), Is.is(XML));
        MatcherAssert.assertThat(xmlBody.getContentType(), Is.is(DEFAULT_CONTENT_TYPE.toString()));
        MatcherAssert.assertThat(xmlBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(xmlBody.getContentType(), Is.is(MediaType.create("application", "xml").toString()));
    }

    @Test
    public void shouldReturnValuesSetInConstructorWithCharset() {
        // when
        XmlBody xmlBody = new XmlBody("some_body", StandardCharsets.UTF_16);
        // then
        MatcherAssert.assertThat(xmlBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xmlBody.getType(), Is.is(XML));
        MatcherAssert.assertThat(xmlBody.getCharset(null), Is.is(StandardCharsets.UTF_16));
        MatcherAssert.assertThat(xmlBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_16));
        MatcherAssert.assertThat(xmlBody.getContentType(), Is.is(MediaType.APPLICATION_XML_UTF_8.withCharset(StandardCharsets.UTF_16).toString()));
    }

    @Test
    public void shouldReturnValueSetInStaticConstructor() {
        // when
        XmlBody xmlBody = XmlBody.xml("some_body");
        // then
        MatcherAssert.assertThat(xmlBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xmlBody.getType(), Is.is(XML));
        MatcherAssert.assertThat(xmlBody.getContentType(), Is.is(DEFAULT_CONTENT_TYPE.toString()));
        MatcherAssert.assertThat(xmlBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(xmlBody.getContentType(), Is.is(MediaType.create("application", "xml").toString()));
    }

    @Test
    public void shouldReturnValueSetInStaticConstructorWithCharset() {
        // when
        XmlBody xmlBody = XmlBody.xml("some_body", StandardCharsets.UTF_16);
        // then
        MatcherAssert.assertThat(xmlBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xmlBody.getType(), Is.is(XML));
        MatcherAssert.assertThat(xmlBody.getCharset(null), Is.is(StandardCharsets.UTF_16));
        MatcherAssert.assertThat(xmlBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_16));
        MatcherAssert.assertThat(xmlBody.getContentType(), Is.is(MediaType.APPLICATION_XML_UTF_8.withCharset(StandardCharsets.UTF_16).toString()));
    }

    @Test
    public void shouldReturnValueSetInStaticConstructorWithNullCharset() {
        // when
        XmlBody xmlBody = XmlBody.xml("some_body", ((Charset) (null)));
        // then
        MatcherAssert.assertThat(xmlBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xmlBody.getType(), Is.is(XML));
        MatcherAssert.assertThat(xmlBody.getCharset(null), Matchers.nullValue());
        MatcherAssert.assertThat(xmlBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(xmlBody.getContentType(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnValueSetInStaticConstructorWithContentType() {
        // when
        XmlBody xmlBody = XmlBody.xml("some_body", MediaType.PLAIN_TEXT_UTF_8);
        // then
        MatcherAssert.assertThat(xmlBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xmlBody.getType(), Is.is(XML));
        MatcherAssert.assertThat(xmlBody.getCharset(null), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(xmlBody.getCharset(StandardCharsets.UTF_16), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(xmlBody.getContentType(), Is.is(MediaType.PLAIN_TEXT_UTF_8.toString()));
    }

    @Test
    public void shouldReturnValueSetInStaticConstructorWithNullMediaType() {
        // when
        XmlBody xmlBody = XmlBody.xml("some_body", ((MediaType) (null)));
        // then
        MatcherAssert.assertThat(xmlBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xmlBody.getType(), Is.is(XML));
        MatcherAssert.assertThat(xmlBody.getCharset(null), Matchers.nullValue());
        MatcherAssert.assertThat(xmlBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(xmlBody.getContentType(), Matchers.nullValue());
    }
}

