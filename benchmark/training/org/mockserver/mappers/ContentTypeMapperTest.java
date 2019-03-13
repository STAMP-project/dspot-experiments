package org.mockserver.mappers;


import CharsetUtil.UTF_8;
import ContentTypeMapper.DEFAULT_HTTP_CHARACTER_SET;
import com.google.common.net.MediaType;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class ContentTypeMapperTest {
    private List<String> utf8ContentTypes = Arrays.asList("application/atom+xml", "application/ecmascript", "application/javascript", "application/json", "application/jsonml+json", "application/lost+xml", "application/wsdl+xml", "application/xaml+xml", "application/xhtml+xml", "application/xml", "application/xml-dtd", "application/xop+xml", "application/xslt+xml", "application/xspf+xml", "application/x-www-form-urlencoded", "image/svg+xml", "text/css", "text/csv", "text/html", "text/plain", "text/richtext", "text/sgml", "text/tab-separated-values", "text/x-fortran", "text/x-java-source");

    private List<String> binaryContentTypes = Arrays.asList("application/applixware", "application/font-tdpfr", "application/java-archive", "application/java-serialized-object", "application/java-vm", "application/mp4", "application/octet-stream", "application/pdf", "application/pkcs10", "application/pkix-cert", "application/x-font-bdf", "application/x-font-ghostscript", "application/x-font-linux-psf", "application/x-font-otf", "application/x-font-pcf", "application/x-font-snf", "application/x-font-ttf", "application/x-font-type1", "application/font-woff", "application/x-java-jnlp-file", "application/x-latex", "application/x-shockwave-flash", "application/x-silverlight-app", "application/x-stuffit", "application/x-tar", "application/x-tex", "application/x-tex-tfm", "application/x-x509-ca-cert", "application/zip", "audio/midi", "audio/mp4", "audio/mpeg", "audio/ogg", "audio/x-aiff", "audio/x-wav", "audio/xm", "image/bmp", "image/gif", "image/jpeg", "image/png", "image/sgi", "image/tiff", "image/x-xbitmap", "video/jpeg", "video/mp4", "video/mpeg", "video/ogg", "video/quicktime", "video/x-msvideo", "video/x-sgi-movie");

    @Test
    public void shouldNotDetectAsBinaryBody() {
        for (String contentType : utf8ContentTypes) {
            MatcherAssert.assertThat((contentType + " should not be binary"), new ContentTypeMapper().isBinary(contentType), Is.is(false));
        }
    }

    @Test
    public void shouldDetectAsBinaryBody() {
        for (String contentType : binaryContentTypes) {
            MatcherAssert.assertThat((contentType + " should be binary"), ContentTypeMapper.isBinary(contentType), Is.is(true));
        }
    }

    @Test
    public void shouldDefaultToNotBinary() {
        MatcherAssert.assertThat("null should not be binary", ContentTypeMapper.isBinary(null), Is.is(false));
    }

    @Test
    public void shouldDetermineCharsetFromResponseContentType() {
        // when
        Charset charset = ContentTypeMapper.getCharsetFromContentTypeHeader(MediaType.create("text", "plain").withCharset(StandardCharsets.UTF_16).toString());
        // then
        MatcherAssert.assertThat(charset, Is.is(StandardCharsets.UTF_16));
    }

    @Test
    public void shouldDetermineUTFCharsetWhenFileTypeIsUtf() {
        Charset charset = ContentTypeMapper.getCharsetFromContentTypeHeader("application/json");
        MatcherAssert.assertThat(charset, Is.is(UTF_8));
    }

    @Test
    public void shouldDetermineCharsetWhenIllegalContentTypeHeader() {
        // when
        Charset charset = ContentTypeMapper.getCharsetFromContentTypeHeader("some_rubbish");
        // then
        MatcherAssert.assertThat(charset, Is.is(DEFAULT_HTTP_CHARACTER_SET));
    }

    @Test
    public void shouldDetermineCharsetWithQuotes() {
        // when
        Charset charset = ContentTypeMapper.getCharsetFromContentTypeHeader("text/html; charset=\"utf-8\"");
        // then
        MatcherAssert.assertThat(charset, Is.is(Charset.forName("utf-8")));
    }

    @Test
    public void shouldDetermineCharsetWhenUnsupportedCharset() {
        // when
        Charset charset = ContentTypeMapper.getCharsetFromContentTypeHeader("text/plain; charset=some_rubbish");
        // then
        MatcherAssert.assertThat(charset, Is.is(DEFAULT_HTTP_CHARACTER_SET));
    }

    @Test
    public void shouldDetermineCharsetWhenNoContentTypeHeader() {
        // when
        Charset charset = ContentTypeMapper.getCharsetFromContentTypeHeader(null);
        // then
        MatcherAssert.assertThat(charset, Is.is(DEFAULT_HTTP_CHARACTER_SET));
    }
}

