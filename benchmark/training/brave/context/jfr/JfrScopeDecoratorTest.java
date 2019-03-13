package brave.context.jfr;


import brave.propagation.CurrentTraceContext;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContext;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class JfrScopeDecoratorTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    ExecutorService wrappedExecutor = Executors.newSingleThreadExecutor();

    CurrentTraceContext currentTraceContext = ThreadLocalCurrentTraceContext.newBuilder().addScopeDecorator(StrictScopeDecorator.create()).addScopeDecorator(JfrScopeDecorator.create()).build();

    Executor executor = currentTraceContext.executor(wrappedExecutor);

    TraceContext context = TraceContext.newBuilder().traceId(1).spanId(1).build();

    TraceContext context2 = TraceContext.newBuilder().traceId(1).parentId(1).spanId(2).build();

    TraceContext context3 = TraceContext.newBuilder().traceId(2).spanId(3).build();

    @Test
    public void endToEndTest() throws Exception {
        Path destination = folder.newFile("execute.jfr").toPath();
        try (Recording recording = new Recording()) {
            recording.start();
            makeFiveScopes();
            recording.dump(destination);
        }
        List<RecordedEvent> events = RecordingFile.readAllEvents(destination);
        assertThat(events).extracting(( e) -> tuple(e.getString("traceId"), e.getString("parentId"), e.getString("spanId"))).containsExactlyInAnyOrder(tuple("0000000000000001", null, "0000000000000001"), tuple("0000000000000001", null, "0000000000000001"), tuple(null, null, null), tuple("0000000000000001", "0000000000000001", "0000000000000002"), tuple("0000000000000002", null, "0000000000000003"));
    }
}

