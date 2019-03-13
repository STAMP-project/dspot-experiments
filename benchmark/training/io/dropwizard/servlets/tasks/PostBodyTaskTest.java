package io.dropwizard.servlets.tasks;


import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;


public class PostBodyTaskTest {
    private final PostBodyTask task = new PostBodyTask("test") {
        @Override
        public void execute(Map<String, List<String>> parameters, String body, PrintWriter output) throws Exception {
        }
    };

    @SuppressWarnings("deprecation")
    @Test
    public void throwsExceptionWhenCallingExecuteWithoutThePostBody() throws Exception {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> task.execute(Collections.emptyMap(), new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8))));
    }
}

