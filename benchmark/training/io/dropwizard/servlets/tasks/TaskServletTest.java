package io.dropwizard.servlets.tasks;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TaskServletTest {
    private final Task gc = Mockito.mock(Task.class);

    private final PostBodyTask printJSON = Mockito.mock(PostBodyTask.class);

    private final MetricRegistry metricRegistry = new MetricRegistry();

    private final TaskServlet servlet = new TaskServlet(metricRegistry);

    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    @Test
    public void returnsA404WhenNotFound() throws Exception {
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getPathInfo()).thenReturn("/test");
        servlet.service(request, response);
        Mockito.verify(response).sendError(404);
    }

    @Test
    public void runsATaskWhenFound() throws Exception {
        final PrintWriter output = Mockito.mock(PrintWriter.class);
        final ServletInputStream bodyStream = new TaskServletTest.TestServletInputStream(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getPathInfo()).thenReturn("/gc");
        Mockito.when(request.getParameterNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        Mockito.when(response.getWriter()).thenReturn(output);
        Mockito.when(request.getInputStream()).thenReturn(bodyStream);
        servlet.service(request, response);
        Mockito.verify(gc).execute(Collections.emptyMap(), output);
    }

    @Test
    public void passesQueryStringParamsAlong() throws Exception {
        final PrintWriter output = Mockito.mock(PrintWriter.class);
        final ServletInputStream bodyStream = new TaskServletTest.TestServletInputStream(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getPathInfo()).thenReturn("/gc");
        Mockito.when(request.getParameterNames()).thenReturn(Collections.enumeration(Collections.singletonList("runs")));
        Mockito.when(request.getParameterValues("runs")).thenReturn(new String[]{ "1" });
        Mockito.when(request.getInputStream()).thenReturn(bodyStream);
        Mockito.when(response.getWriter()).thenReturn(output);
        servlet.service(request, response);
        Mockito.verify(gc).execute(Collections.singletonMap("runs", Collections.singletonList("1")), output);
    }

    @Test
    public void passesPostBodyAlongToPostBodyTasks() throws Exception {
        String body = "{\"json\": true}";
        final PrintWriter output = Mockito.mock(PrintWriter.class);
        final ServletInputStream bodyStream = new TaskServletTest.TestServletInputStream(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getPathInfo()).thenReturn("/print-json");
        Mockito.when(request.getParameterNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        Mockito.when(request.getInputStream()).thenReturn(bodyStream);
        Mockito.when(response.getWriter()).thenReturn(output);
        servlet.service(request, response);
        Mockito.verify(printJSON).execute(Collections.emptyMap(), body, output);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void returnsA500OnExceptions() throws Exception {
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getPathInfo()).thenReturn("/gc");
        Mockito.when(request.getParameterNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        final PrintWriter output = Mockito.mock(PrintWriter.class);
        Mockito.when(response.getWriter()).thenReturn(output);
        final RuntimeException ex = new RuntimeException("whoops");
        Mockito.doThrow(ex).when(gc).execute(ArgumentMatchers.any(Map.class), ArgumentMatchers.any(PrintWriter.class));
        servlet.service(request, response);
        Mockito.verify(response).setStatus(500);
    }

    /**
     * Add a test to make sure the signature of the Task class does not change as the TaskServlet
     * depends on this to perform record metrics on Tasks
     */
    @Test
    public void verifyTaskExecuteMethod() {
        assertThatCode(() -> .class.getMethod("execute", .class, .class)).doesNotThrowAnyException();
    }

    @Test
    public void verifyPostBodyTaskExecuteMethod() {
        assertThatCode(() -> .class.getMethod("execute", .class, .class, .class)).doesNotThrowAnyException();
    }

    @Test
    public void returnAllTaskNamesLexicallyOnGet() throws Exception {
        try (StringWriter sw = new StringWriter();PrintWriter pw = new PrintWriter(sw)) {
            Mockito.when(request.getMethod()).thenReturn("GET");
            Mockito.when(request.getPathInfo()).thenReturn(null);
            Mockito.when(response.getWriter()).thenReturn(pw);
            servlet.service(request, response);
            final String newLine = System.lineSeparator();
            assertThat(sw.toString()).isEqualTo(((((gc.getName()) + newLine) + (printJSON.getName())) + newLine));
        }
    }

    @Test
    public void returnsA404WhenGettingUnknownTask() throws Exception {
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getPathInfo()).thenReturn("/absent");
        servlet.service(request, response);
        Mockito.verify(response).sendError(404);
    }

    @Test
    public void returnsA405WhenGettingTaskByName() throws Exception {
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getPathInfo()).thenReturn("/gc");
        servlet.service(request, response);
        Mockito.verify(response).sendError(405);
    }

    @Test
    public void testRunsTimedTask() throws Exception {
        final Task timedTask = new Task("timed-task") {
            @Override
            @Timed(name = "vacuum-cleaning")
            public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
                output.println("Vacuum cleaning");
            }
        };
        servlet.add(timedTask);
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getPathInfo()).thenReturn("/timed-task");
        Mockito.when(response.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));
        servlet.service(request, response);
        assertThat(metricRegistry.getTimers()).containsKey(name(timedTask.getClass(), "vacuum-cleaning"));
    }

    @Test
    public void testRunsMeteredTask() throws Exception {
        final Task meteredTask = new Task("metered-task") {
            @Override
            @Metered(name = "vacuum-cleaning")
            public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
                output.println("Vacuum cleaning");
            }
        };
        servlet.add(meteredTask);
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getPathInfo()).thenReturn("/metered-task");
        Mockito.when(response.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));
        servlet.service(request, response);
        assertThat(metricRegistry.getMeters()).containsKey(name(meteredTask.getClass(), "vacuum-cleaning"));
    }

    @Test
    public void testRunsExceptionMeteredTask() throws Exception {
        final Task exceptionMeteredTask = new Task("exception-metered-task") {
            @Override
            @ExceptionMetered(name = "vacuum-cleaning-exceptions")
            public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
                throw new RuntimeException("The engine has died");
            }
        };
        servlet.add(exceptionMeteredTask);
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getPathInfo()).thenReturn("/exception-metered-task");
        Mockito.when(response.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));
        servlet.service(request, response);
        assertThat(metricRegistry.getMeters()).containsKey(name(exceptionMeteredTask.getClass(), "vacuum-cleaning-exceptions"));
    }

    @Test
    public void testReturnsA404ForTaskRoot() throws IOException, ServletException {
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getPathInfo()).thenReturn(null);
        servlet.service(request, response);
        Mockito.verify(response).sendError(404);
    }

    @SuppressWarnings("InputStreamSlowMultibyteRead")
    private static class TestServletInputStream extends ServletInputStream {
        private InputStream delegate;

        public TestServletInputStream(InputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }

        @Override
        public int read() throws IOException {
            return delegate.read();
        }
    }
}

