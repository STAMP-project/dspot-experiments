package org.mockserver.serialization.model;


import Body.Type.STRING;
import CharsetUtil.ISO_8859_1;
import com.google.common.net.MediaType;
import io.netty.util.CharsetUtil;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.mappers.ContentTypeMapper;
import org.mockserver.model.StringBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class StringBodyDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        StringBodyDTO stringBody = new StringBodyDTO(new StringBody("some_body"));
        // then
        MatcherAssert.assertThat(stringBody.getString(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(false));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getContentType(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnValuesSetInConstructorWithSubString() {
        // when
        StringBodyDTO stringBody = new StringBodyDTO(new StringBody("some_body", true));
        // then
        MatcherAssert.assertThat(stringBody.getString(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(true));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getContentType(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnValuesSetInConstructorWithCharset() {
        // when
        StringBodyDTO stringBody = new StringBodyDTO(new StringBody("some_body", CharsetUtil.ISO_8859_1));
        // then
        MatcherAssert.assertThat(stringBody.getString(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getContentType(), Is.is(MediaType.PLAIN_TEXT_UTF_8.withCharset(ISO_8859_1).toString()));
    }

    @Test
    public void shouldReturnValuesSetInConstructorWithContentType() {
        // when
        StringBodyDTO stringBody = new StringBodyDTO(new StringBody("some_body", MediaType.HTML_UTF_8));
        // then
        MatcherAssert.assertThat(stringBody.getString(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getContentType(), Is.is(MediaType.HTML_UTF_8.toString()));
    }

    @Test
    public void shouldBuildCorrectObject() {
        // when
        StringBody stringBody = new StringBodyDTO(new StringBody("some_body", true, CharsetUtil.ISO_8859_1)).buildObject();
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(true));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getContentType(), Is.is(MediaType.PLAIN_TEXT_UTF_8.withCharset(ISO_8859_1).toString()));
    }

    @Test
    public void shouldNotSetDefaultCharset() {
        // when
        StringBody stringBody = new StringBodyDTO(new StringBody("some_body", true, ContentTypeMapper.DEFAULT_HTTP_CHARACTER_SET)).buildObject();
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(stringBody.isSubString(), Is.is(true));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
        MatcherAssert.assertThat(stringBody.getContentType(), Is.is(MediaType.PLAIN_TEXT_UTF_8.withCharset(ContentTypeMapper.DEFAULT_HTTP_CHARACTER_SET).toString()));
    }

    @Test
    public void shouldReturnCorrectObjectFromStaticExactBuilder() {
        MatcherAssert.assertThat(StringBody.exact("some_body"), Is.is(new StringBody("some_body", false)));
        MatcherAssert.assertThat(StringBody.exact("some_body"), Is.is(new StringBody("some_body")));
        MatcherAssert.assertThat(StringBody.exact("some_body", StandardCharsets.UTF_16), Is.is(new StringBody("some_body", false, StandardCharsets.UTF_16)));
        MatcherAssert.assertThat(StringBody.exact("some_body", StandardCharsets.UTF_16), Is.is(new StringBody("some_body", StandardCharsets.UTF_16)));
    }

    @Test
    public void shouldReturnCorrectObjectFromStaticSubStringBuilder() {
        MatcherAssert.assertThat(StringBody.subString("some_body"), Is.is(new StringBody("some_body", true)));
        MatcherAssert.assertThat(StringBody.subString("some_body", StandardCharsets.UTF_16), Is.is(new StringBody("some_body", true, StandardCharsets.UTF_16)));
    }

    @Test
    public void shouldHandleNull() {
        // given
        String body = null;
        // when
        StringBody stringBody = buildObject();
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Matchers.nullValue());
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
    }

    @Test
    public void shouldHandleEmptyByteArray() {
        // given
        String body = "";
        // when
        StringBody stringBody = buildObject();
        // then
        MatcherAssert.assertThat(stringBody.getValue(), Is.is(""));
        MatcherAssert.assertThat(stringBody.getType(), Is.is(STRING));
    }
}

