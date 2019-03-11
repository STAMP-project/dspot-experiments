package cucumber.runtime.formatter;


import cucumber.runtime.Utils;
import cucumber.util.FixJava;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServer;
import org.webbitserver.rest.Rest;


public class URLOutputStreamTest {
    private WebServer webbit;

    private final int threadsCount = 100;

    private final long waitTimeoutMillis = 30000L;

    private final List<File> tmpFiles = new ArrayList<File>();

    private final List<String> threadErrors = new ArrayList<String>();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void write_to_file_without_existing_parent_directory() throws IOException, URISyntaxException {
        Path filesWithoutParent = Files.createTempDirectory("filesWithoutParent");
        String baseURL = filesWithoutParent.toUri().toURL().toString();
        URL urlWithoutParentDirectory = new URL((baseURL + "/non/existing/directory"));
        Writer w = new UTF8OutputStreamWriter(new URLOutputStream(urlWithoutParentDirectory));
        w.write("Helles?y");
        w.close();
        File testFile = new File(urlWithoutParentDirectory.toURI());
        Assert.assertEquals("Helles?y", FixJava.readReader(openUTF8FileReader(testFile)));
    }

    @Test
    public void can_write_to_file() throws IOException {
        File tmp = File.createTempFile("cucumber-jvm", "tmp");
        Writer w = new UTF8OutputStreamWriter(new URLOutputStream(tmp.toURI().toURL()));
        w.write("Helles?y");
        w.close();
        Assert.assertEquals("Helles?y", FixJava.readReader(openUTF8FileReader(tmp)));
    }

    @Test
    public void can_http_put() throws IOException, InterruptedException, ExecutionException {
        final BlockingQueue<String> data = new LinkedBlockingDeque<String>();
        Rest r = new Rest(webbit);
        r.PUT("/.cucumber/stepdefs.json", new HttpHandler() {
            @Override
            public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) throws Exception {
                data.offer(req.body());
                res.end();
            }
        });
        Writer w = new UTF8OutputStreamWriter(new URLOutputStream(new URL(Utils.toURL("http://localhost:9873/.cucumber"), "stepdefs.json")));
        w.write("Helles?y");
        w.flush();
        w.close();
        Assert.assertEquals("Helles?y", data.poll(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void throws_fnfe_if_http_response_is_404() throws IOException, InterruptedException, ExecutionException {
        Writer w = new UTF8OutputStreamWriter(new URLOutputStream(new URL(Utils.toURL("http://localhost:9873/.cucumber"), "stepdefs.json")));
        w.write("Helles?y");
        w.flush();
        try {
            w.close();
            Assert.fail();
        } catch (FileNotFoundException expected) {
        }
    }

    @Test
    public void throws_ioe_if_http_response_is_500() throws IOException, InterruptedException, ExecutionException {
        Rest r = new Rest(webbit);
        r.PUT("/.cucumber/stepdefs.json", new HttpHandler() {
            @Override
            public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) throws Exception {
                res.status(500);
                res.content("something went wrong");
                res.end();
            }
        });
        Writer w = new UTF8OutputStreamWriter(new URLOutputStream(new URL(Utils.toURL("http://localhost:9873/.cucumber"), "stepdefs.json")));
        w.write("Helles?y");
        w.flush();
        try {
            w.close();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals(("PUT http://localhost:9873/.cucumber/stepdefs.json\n" + "HTTP 500\nsomething went wrong"), expected.getMessage());
        }
    }

    @Test
    public void do_not_throw_ioe_if_parent_dir_created_by_another_thread() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<Thread> testThreads = getThreadsWithLatchForFile(countDownLatch, threadsCount);
        startThreadsFromList(testThreads);
        countDownLatch.countDown();
        waitAllThreadsFromList(testThreads);
        Assert.assertTrue("Not all parent folders were created for tmp file or tmp file was not created", isAllFilesCreated());
        Assert.assertTrue(("Some thread get error during work. Error list:" + (threadErrors.toString())), threadErrors.isEmpty());
    }
}

