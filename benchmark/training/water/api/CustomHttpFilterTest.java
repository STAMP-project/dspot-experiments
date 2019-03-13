package water.api;


import NetworkInit.h2oHttpView;
import RequestServer.LogFilterLevel;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;
import org.mockito.Mockito;
import water.H2O;
import water.TestUtil;


public class CustomHttpFilterTest extends TestUtil {
    @Test
    public void testNoLog() throws Exception {
        final Logger _logger = Logger.getLogger("water.default");
        _logger.addAppender(new AppenderSkeleton() {
            @Override
            protected void append(LoggingEvent event) {
                if (event.getRenderedMessage().contains("GET"))
                    throw new RuntimeException("All GETs should be filtered");

            }

            @Override
            public void close() {
                _logger.removeAppender(this);
            }

            @Override
            public boolean requiresLayout() {
                return false;
            }
        });
        // let's filter out all GETs
        RequestServer.setFilters(RequestServer.defaultFilter(), new RequestServer.HttpLogFilter() {
            @Override
            public LogFilterLevel filter(RequestUri uri, Properties header, Properties parms) {
                String[] path = uri.getPath();
                if (path[1].equals("GET"))
                    return LogFilterLevel.DO_NOT_LOG;
                else
                    return LogFilterLevel.LOG;

            }
        });
        // mock up a "GET /flow/index.html" call
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(request.getServletPath()).thenReturn("/flow/index.html");
        Mockito.when(request.getRequestURI()).thenReturn("/flow/index.html");
        // mock up the headers
        // define the headers you want to be returned
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Cookie", "_yummy");
        headers.put("Accept-Encoding", "gzip, deflate, sdch");
        headers.put("Host", H2O.getIpPortString());
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("Accept-Language", "en-US,en;q=0.8");
        headers.put("Connection", "keep-alive");
        // create an Enumeration over the header keys
        final Iterator<String> iterator = headers.keySet().iterator();
        Enumeration headerNames = new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public String nextElement() {
                return iterator.next();
            }
        };
        Mockito.when(request.getHeaderNames()).thenReturn(headerNames);
        Mockito.when(request.getHeader("User-Agent")).thenReturn(headers.get("User-Agent"));
        Mockito.when(request.getHeader("Accept")).thenReturn(headers.get("Accept"));
        Mockito.when(request.getHeader("Cookie")).thenReturn(headers.get("Cookie"));
        Mockito.when(request.getHeader("Accept-Encoding")).thenReturn(headers.get("Accept-Encoding"));
        Mockito.when(request.getHeader("Host")).thenReturn(headers.get("Host"));
        Mockito.when(request.getHeader("Upgrade-Insecure-Requests")).thenReturn(headers.get("Upgrade-Insecure-Requests"));
        Mockito.when(request.getHeader("Accept-Language")).thenReturn(headers.get("Accept-Language"));
        Mockito.when(request.getHeader("Connection")).thenReturn(headers.get("Connection"));
        Mockito.when(request.getParameterMap()).thenReturn(new HashMap<String, String[]>());
        Mockito.when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        });
        // start the request lifecycle
        h2oHttpView.gateHandler(request, response);
        new RequestServer().doGet(request, response);
    }
}

