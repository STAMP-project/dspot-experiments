package brave.servlet;


import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Response;
import org.junit.Test;


public class ServletRuntimeTest {
    ServletRuntime servlet25 = new ServletRuntime.Servlet25();

    @Test
    public void servlet25_status_doesntParseAnonymousTypes() throws Exception {
        // while looks nice, this will overflow our cache
        Response jettyResponse = new Response(null) {
            @Override
            public int getStatus() {
                throw new AssertionError();
            }
        };
        assertThat(servlet25.status(jettyResponse)).isNull();
    }

    @Test
    public void servlet25_status_doesntParseLocalTypes() throws Exception {
        // while looks nice, this will overflow our cache
        class LocalResponse extends ServletRuntimeTest.HttpServletResponseImpl {}
        assertThat(servlet25.status(new LocalResponse())).isNull();
    }

    class ExceptionResponse extends ServletRuntimeTest.HttpServletResponseImpl {
        @Override
        public int getStatus() {
            throw new IllegalStateException("foo");
        }
    }

    @Test
    public void servlet25_status_nullOnException() throws Exception {
        assertThat(servlet25.status(new ServletRuntimeTest.ExceptionResponse())).isNull();
    }

    class Response1 extends ServletRuntimeTest.HttpServletResponseImpl {}

    class Response2 extends ServletRuntimeTest.HttpServletResponseImpl {}

    class Response3 extends ServletRuntimeTest.HttpServletResponseImpl {}

    class Response4 extends ServletRuntimeTest.HttpServletResponseImpl {}

    class Response5 extends ServletRuntimeTest.HttpServletResponseImpl {}

    class Response6 extends ServletRuntimeTest.HttpServletResponseImpl {}

    class Response7 extends ServletRuntimeTest.HttpServletResponseImpl {}

    class Response8 extends ServletRuntimeTest.HttpServletResponseImpl {}

    class Response9 extends ServletRuntimeTest.HttpServletResponseImpl {}

    class Response10 extends ServletRuntimeTest.HttpServletResponseImpl {}

    class Response11 extends ServletRuntimeTest.HttpServletResponseImpl {}

    @Test
    public void servlet25_status_cachesUpToTenTypes() throws Exception {
        assertThat(servlet25.status(new ServletRuntimeTest.Response1())).isEqualTo(200);
        assertThat(servlet25.status(new ServletRuntimeTest.Response2())).isEqualTo(200);
        assertThat(servlet25.status(new ServletRuntimeTest.Response3())).isEqualTo(200);
        assertThat(servlet25.status(new ServletRuntimeTest.Response4())).isEqualTo(200);
        assertThat(servlet25.status(new ServletRuntimeTest.Response5())).isEqualTo(200);
        assertThat(servlet25.status(new ServletRuntimeTest.Response6())).isEqualTo(200);
        assertThat(servlet25.status(new ServletRuntimeTest.Response7())).isEqualTo(200);
        assertThat(servlet25.status(new ServletRuntimeTest.Response8())).isEqualTo(200);
        assertThat(servlet25.status(new ServletRuntimeTest.Response9())).isEqualTo(200);
        assertThat(servlet25.status(new ServletRuntimeTest.Response10())).isEqualTo(200);
        assertThat(servlet25.status(new ServletRuntimeTest.Response11())).isNull();
    }

    public static class HttpServletResponseImpl implements HttpServletResponse {
        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return null;
        }

        @Override
        public PrintWriter getWriter() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String charset) {
        }

        @Override
        public void setContentLength(int len) {
        }

        @Override
        public void setContentType(String type) {
        }

        @Override
        public void setBufferSize(int size) {
        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() {
        }

        @Override
        public void resetBuffer() {
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {
        }

        @Override
        public void setLocale(Locale loc) {
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public void addCookie(Cookie cookie) {
        }

        @Override
        public boolean containsHeader(String name) {
            return false;
        }

        @Override
        public String encodeURL(String url) {
            return null;
        }

        @Override
        public String encodeRedirectURL(String url) {
            return null;
        }

        @Override
        public String encodeUrl(String url) {
            return null;
        }

        @Override
        public String encodeRedirectUrl(String url) {
            return null;
        }

        @Override
        public void sendError(int sc, String msg) {
        }

        @Override
        public void sendError(int sc) {
        }

        @Override
        public void sendRedirect(String location) {
        }

        @Override
        public void setDateHeader(String name, long date) {
        }

        @Override
        public void addDateHeader(String name, long date) {
        }

        @Override
        public void setHeader(String name, String value) {
        }

        @Override
        public void addHeader(String name, String value) {
        }

        @Override
        public void setIntHeader(String name, int value) {
        }

        @Override
        public void addIntHeader(String name, int value) {
        }

        @Override
        public void setStatus(int sc) {
        }

        @Override
        public void setStatus(int sc, String sm) {
        }

        @Override
        public int getStatus() {
            return 200;
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public Collection<String> getHeaders(String name) {
            return null;
        }

        @Override
        public Collection<String> getHeaderNames() {
            return null;
        }
    }
}

