package io.dropwizard.jetty;


import HttpHeader.ACCEPT_ENCODING;
import HttpHeader.CONTENT_ENCODING;
import HttpHeader.CONTENT_LENGTH;
import HttpHeader.CONTENT_TYPE;
import HttpHeader.VARY;
import HttpTester.Request;
import HttpTester.Response;
import io.dropwizard.util.CharStreams;
import io.dropwizard.util.Resources;
import io.dropwizard.util.Size;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletTester;
import org.junit.jupiter.api.Test;


public class GzipHandlerTest {
    private static final String PLAIN_TEXT_UTF_8 = "text/plain;charset=UTF-8";

    private final GzipHandler gzipHandler;

    private final ServletTester servletTester = new ServletTester();

    private final Request request = HttpTester.newRequest();

    public GzipHandlerTest() {
        final GzipHandlerFactory gzipHandlerFactory = new GzipHandlerFactory();
        gzipHandlerFactory.setMinimumEntitySize(Size.bytes(0));
        gzipHandler = gzipHandlerFactory.build(null);
    }

    @Test
    public void testCompressResponse() throws Exception {
        request.setMethod("GET");
        request.setHeader(ACCEPT_ENCODING.asString(), "gzip");
        HttpTester.Response response = HttpTester.parseResponse(servletTester.getResponses(request.generate()));
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.get(CONTENT_ENCODING)).isEqualTo("gzip");
        assertThat(response.get(VARY)).isEqualTo(ACCEPT_ENCODING.asString());
        assertThat(response.get(CONTENT_TYPE)).isEqualToIgnoringCase(GzipHandlerTest.PLAIN_TEXT_UTF_8);
        final byte[] expectedBytes = Resources.toByteArray(Resources.getResource("assets/banner.txt"));
        try (GZIPInputStream is = new GZIPInputStream(new ByteArrayInputStream(response.getContentBytes()));ByteArrayInputStream expected = new ByteArrayInputStream(expectedBytes)) {
            assertThat(is).hasSameContentAs(expected);
        }
    }

    @Test
    public void testDecompressRequest() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gz = new GZIPOutputStream(baos)) {
            Resources.copy(Resources.getResource("assets/new-banner.txt"), gz);
        }
        setRequestPostGzipPlainText(baos.toByteArray());
        HttpTester.Response response = HttpTester.parseResponse(servletTester.getResponses(request.generate()));
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContent()).isEqualTo("Banner has been updated");
    }

    @SuppressWarnings("serial")
    public static class BannerServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            resp.setContentType(GzipHandlerTest.PLAIN_TEXT_UTF_8);
            resp.getWriter().write(Resources.toString(Resources.getResource("assets/banner.txt"), StandardCharsets.UTF_8));
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            assertThat(req.getHeader(CONTENT_TYPE.asString())).isEqualToIgnoringCase(GzipHandlerTest.PLAIN_TEXT_UTF_8);
            assertThat(req.getHeader(CONTENT_ENCODING.asString())).isNull();
            assertThat(req.getHeader(CONTENT_LENGTH.asString())).isNull();
            assertThat(req.getContentLength()).isEqualTo((-1));
            assertThat(req.getContentLengthLong()).isEqualTo((-1L));
            assertThat(CharStreams.toString(req.getReader())).isEqualTo(Resources.toString(Resources.getResource("assets/new-banner.txt"), StandardCharsets.UTF_8));
            resp.setContentType(GzipHandlerTest.PLAIN_TEXT_UTF_8);
            resp.getWriter().write("Banner has been updated");
        }
    }
}

