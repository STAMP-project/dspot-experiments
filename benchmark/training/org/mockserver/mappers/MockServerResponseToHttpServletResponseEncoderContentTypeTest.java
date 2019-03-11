package org.mockserver.mappers;


import com.google.common.net.MediaType;
import java.nio.charset.StandardCharsets;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Header;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.Parameter;
import org.mockserver.model.ParameterBody;
import org.mockserver.model.StringBody;
import org.mockserver.model.XmlBody;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerResponseToHttpServletResponseEncoderContentTypeTest {
    @Test
    public void shouldReturnNoDefaultContentTypeWhenNoBodySpecified() {
        // given
        HttpResponse httpResponse = HttpResponse.response();
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        // when
        new MockServerResponseToHttpServletResponseEncoder().mapMockServerResponseToHttpServletResponse(httpResponse, httpServletResponse);
        // then
        Assert.assertEquals(null, httpServletResponse.getHeader("Content-Type"));
    }

    @Test
    public void shouldReturnContentTypeForStringBodyWithCharset() {
        // given
        HttpResponse httpResponse = HttpResponse.response().withBody(StringBody.exact("somebody", StandardCharsets.US_ASCII));
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        // when
        new MockServerResponseToHttpServletResponseEncoder().mapMockServerResponseToHttpServletResponse(httpResponse, httpServletResponse);
        // then
        Assert.assertEquals("text/plain; charset=us-ascii", httpServletResponse.getHeader("Content-Type"));
    }

    @Test
    public void shouldReturnContentTypeForStringBodyWithMediaType() {
        // given
        HttpResponse httpResponse = HttpResponse.response().withBody(StringBody.exact("somebody", MediaType.ATOM_UTF_8));
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        // when
        new MockServerResponseToHttpServletResponseEncoder().mapMockServerResponseToHttpServletResponse(httpResponse, httpServletResponse);
        // then
        Assert.assertEquals("application/atom+xml; charset=utf-8", httpServletResponse.getHeader("Content-Type"));
    }

    @Test
    public void shouldReturnContentTypeForStringBodyWithoutMediaTypeOrCharset() {
        // given
        HttpResponse httpResponse = HttpResponse.response().withBody("somebody");
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        // when
        new MockServerResponseToHttpServletResponseEncoder().mapMockServerResponseToHttpServletResponse(httpResponse, httpServletResponse);
        // then
        Assert.assertThat(httpServletResponse.getHeader("Content-Type"), CoreMatchers.nullValue());
    }

    @Test
    public void shouldReturnContentTypeForJsonBody() {
        // given
        HttpResponse httpResponse = HttpResponse.response().withBody(JsonBody.json("somebody"));
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        // when
        new MockServerResponseToHttpServletResponseEncoder().mapMockServerResponseToHttpServletResponse(httpResponse, httpServletResponse);
        // then
        Assert.assertThat(httpServletResponse.getHeader("Content-Type"), Is.is("application/json"));
    }

    @Test
    public void shouldReturnContentTypeForParameterBody() {
        // given
        HttpResponse httpResponse = HttpResponse.response().withBody(ParameterBody.params(Parameter.param("key", "value")));
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        // when
        new MockServerResponseToHttpServletResponseEncoder().mapMockServerResponseToHttpServletResponse(httpResponse, httpServletResponse);
        // then
        Assert.assertEquals("application/x-www-form-urlencoded", httpServletResponse.getHeader("Content-Type"));
    }

    @Test
    public void shouldReturnNoContentTypeForBodyWithNoAssociatedContentType() {
        // given
        HttpResponse httpResponse = HttpResponse.response().withBody(XmlBody.xml("some_value", ((MediaType) (null))));
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        // when
        new MockServerResponseToHttpServletResponseEncoder().mapMockServerResponseToHttpServletResponse(httpResponse, httpServletResponse);
        // then
        Assert.assertEquals(null, httpServletResponse.getHeader("Content-Type"));
    }

    @Test
    public void shouldNotSetDefaultContentTypeWhenContentTypeExplicitlySpecified() {
        // given
        HttpResponse httpResponse = HttpResponse.response().withBody(JsonBody.json("somebody")).withHeaders(new Header("Content-Type", "some/value"));
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        // when
        new MockServerResponseToHttpServletResponseEncoder().mapMockServerResponseToHttpServletResponse(httpResponse, httpServletResponse);
        // then
        Assert.assertEquals("some/value", httpServletResponse.getHeader("Content-Type"));
    }
}

