package io.fabric8.maven.docker.log;


import com.google.common.io.Files;
import io.fabric8.maven.docker.access.log.LogCallback;
import io.fabric8.maven.docker.access.log.LogCallback.DoneException;
import io.fabric8.maven.docker.util.Timestamp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.maven.shared.utils.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DefaultLogCallbackTest {
    private File file;

    private LogOutputSpec spec;

    private LogCallback callback;

    private Timestamp ts;

    private static final int NR_LOOPS = 100;

    @Test
    public void shouldLogSequentially() throws DoneException, IOException {
        callback.log(1, ts, "line 1");
        callback.log(1, ts, "line 2");
        callback.close();
        List<String> lines = Arrays.asList(FileUtils.fileReadArray(file));
        Assert.assertThat(lines, Matchers.contains("callback-test> line 1", "callback-test> line 2"));
    }

    @Test
    public void shouldLogError() throws DoneException, IOException {
        callback.error("error 1");
        callback.log(1, ts, "line 2");
        callback.error("error 3");
        callback.close();
        List<String> lines = Arrays.asList(FileUtils.fileReadArray(file));
        Assert.assertThat(lines, Matchers.contains("error 1", "callback-test> line 2", "error 3"));
    }

    @Test
    public void shouldLogToStdout() throws DoneException, IOException {
        // we don't need the default stream for this test
        callback.close();
        file = File.createTempFile("logcallback-stdout", ".log");
        file.deleteOnExit();
        FileOutputStream os = new FileOutputStream(file);
        PrintStream ps = new PrintStream(os);
        PrintStream stdout = System.out;
        try {
            System.setOut(ps);
            spec = new LogOutputSpec.Builder().prefix("stdout> ").build();
            callback = new DefaultLogCallback(spec);
            callback.open();
            DefaultLogCallback callback2 = new DefaultLogCallback(spec);
            callback2.open();
            callback.log(1, ts, "line 1");
            callback2.log(1, ts, "line 2");
            callback.log(1, ts, "line 3");
            callback.close();
            callback2.log(1, ts, "line 4");
            callback2.close();
            List<String> lines = Arrays.asList(FileUtils.fileReadArray(file));
            Assert.assertThat(lines, Matchers.contains("stdout> line 1", "stdout> line 2", "stdout> line 3", "stdout> line 4"));
        } finally {
            System.setOut(stdout);
        }
    }

    @Test
    public void shouldKeepStreamOpen() throws DoneException, IOException {
        DefaultLogCallback callback2 = new DefaultLogCallback(spec);
        callback2.open();
        callback.log(1, ts, "line 1");
        callback2.log(1, ts, "line 2");
        callback.log(1, ts, "line 3");
        callback.close();
        callback2.log(1, ts, "line 4");
        callback2.close();
        List<String> lines = Arrays.asList(FileUtils.fileReadArray(file));
        Assert.assertThat(lines, Matchers.contains("callback-test> line 1", "callback-test> line 2", "callback-test> line 3", "callback-test> line 4"));
    }

    @Test
    public void shouldLogInParallel() throws DoneException, IOException, InterruptedException {
        DefaultLogCallback callback2 = new DefaultLogCallback(spec);
        callback2.open();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        DefaultLogCallbackTest.LoggerTask task1 = new DefaultLogCallbackTest.LoggerTask(callback, 1);
        DefaultLogCallbackTest.LoggerTask task2 = new DefaultLogCallbackTest.LoggerTask(callback2, (1 + (DefaultLogCallbackTest.NR_LOOPS)));
        executorService.submit(task1);
        executorService.submit(task2);
        executorService.awaitTermination(1, TimeUnit.SECONDS);
        List<String> lines = Arrays.asList(FileUtils.fileReadArray(file));
        // System.out.println(lines);
        Assert.assertThat(lines.size(), Matchers.is(((DefaultLogCallbackTest.NR_LOOPS) * 2)));
        // fill set with expected line numbers
        Set<Integer> indexes = new HashSet<>();
        for (int i = 1; i <= (2 * (DefaultLogCallbackTest.NR_LOOPS)); i++) {
            indexes.add(i);
        }
        // remove found line numbers from set
        for (String line : lines) {
            String prefix = "callback-test> line ";
            Assert.assertThat(line, Matchers.startsWith(prefix));
            String suffix = line.substring(prefix.length());
            indexes.remove(Integer.parseInt(suffix));
        }
        // expect empty set
        Assert.assertThat(indexes, Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldCreateParentDirs() throws IOException {
        File dir = Files.createTempDir();
        dir.deleteOnExit();
        file = new File(dir, "non/existing/dirs/file.log");
        spec = new LogOutputSpec.Builder().prefix("callback-test> ").file(file.toString()).build();
        callback = new DefaultLogCallback(spec);
        callback.open();
        Assert.assertTrue(file.exists());
    }

    private class LoggerTask implements Runnable {
        private LogCallback cb;

        private int start;

        LoggerTask(LogCallback cb, int start) {
            this.cb = cb;
            this.start = start;
        }

        @Override
        public void run() {
            for (int i = 0; i < (DefaultLogCallbackTest.NR_LOOPS); i++) {
                try {
                    callback.log(1, ts, ("line " + ((start) + i)));
                } catch (DoneException e) {
                    // ignore
                }
            }
            cb.close();
        }
    }
}

