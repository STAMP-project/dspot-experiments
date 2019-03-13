package brave.servlet;


import ServletRuntime.Servlet25ServerResponseAdapter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Response;
import org.junit.Test;


/**
 * These tests must to be run in a classpath without servlet 3.x types
 */
public class ServletRuntime25Test {
    @Test
    public void status_fromJetty() throws Exception {
        Response jettyResponse = new Response(null);
        Field field = Response.class.getDeclaredField("_status");
        field.setAccessible(true);
        field.set(jettyResponse, 400);
        assertThat(ServletRuntime.get().status(jettyResponse)).isEqualTo(400);
    }

    @Test
    public void httpResponse_wrapsHttpServletResponse() throws Exception {
        assertThat(ServletRuntime.get().httpResponse(new ServletRuntime25Test.WithoutGetStatus())).isInstanceOf(Servlet25ServerResponseAdapter.class);
    }

    @Test
    public void status_fromInvalidMethod() throws Exception {
        assertThat(ServletRuntime.get().status(new ServletRuntime25Test.WithInvalidGetStatus())).isNull();
    }

    public static class WithInvalidGetStatus extends ServletRuntime25Test.WithoutGetStatus {
        public String getStatus() {
            return "foo";
        }
    }

    // intentionally won't compile if servlet > 2.5 is present! testing handling of missing getStatus
    public static class WithoutGetStatus implements HttpServletResponse {
        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
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
        public void flushBuffer() throws IOException {
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
        public void sendError(int sc, String msg) throws IOException {
        }

        @Override
        public void sendError(int sc) throws IOException {
        }

        @Override
        public void sendRedirect(String location) throws IOException {
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
    }
}

