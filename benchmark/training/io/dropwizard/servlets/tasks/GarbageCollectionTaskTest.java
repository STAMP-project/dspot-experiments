package io.dropwizard.servlets.tasks;


import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


@SuppressWarnings("CallToSystemGC")
public class GarbageCollectionTaskTest {
    private final Runtime runtime = Mockito.mock(Runtime.class);

    private final PrintWriter output = Mockito.mock(PrintWriter.class);

    private final GarbageCollectionTask task = new GarbageCollectionTask(runtime);

    @Test
    public void runsOnceWithNoParameters() throws Exception {
        task.execute(Collections.emptyMap(), output);
        Mockito.verify(runtime, Mockito.times(1)).gc();
    }

    @Test
    public void usesTheFirstRunsParameter() throws Exception {
        final Map<String, List<String>> parameters = Collections.singletonMap("runs", Arrays.asList("3", "2"));
        task.execute(parameters, output);
        Mockito.verify(runtime, Mockito.times(3)).gc();
    }

    @Test
    public void defaultsToOneRunIfTheQueryParamDoesNotParse() throws Exception {
        task.execute(Collections.singletonMap("runs", Collections.singletonList("$")), output);
        Mockito.verify(runtime, Mockito.times(1)).gc();
    }
}

