package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class MocoLogTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private MocoTestHelper helper;

    @Test
    public void should_log_request_and_response() throws Exception {
        HttpServer server = Moco.httpServer(RemoteTestUtils.port(), Moco.log());
        server.request(Moco.by("0XCAFE")).response("0XBABE");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), "0XCAFE"), CoreMatchers.is("0XBABE"));
            }
        });
        String actual = Files.toString(file, Charset.defaultCharset());
        Assert.assertThat(actual, CoreMatchers.containsString("0XBABE"));
        Assert.assertThat(actual, CoreMatchers.containsString("0XCAFE"));
    }

    @Test
    public void should_log_request_and_response_into_file() throws Exception {
        File file = folder.newFile();
        HttpServer server = Moco.httpServer(RemoteTestUtils.port(), Moco.log(file.getAbsolutePath()));
        server.request(Moco.by("0XCAFE")).response("0XBABE");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), "0XCAFE"), CoreMatchers.is("0XBABE"));
            }
        });
        String actual = Files.toString(file, Charset.defaultCharset());
        Assert.assertThat(actual, CoreMatchers.containsString("0XBABE"));
        Assert.assertThat(actual, CoreMatchers.containsString("0XCAFE"));
    }

    @Test
    public void should_log_request_and_response_with_exception() throws Exception {
        File file = folder.newFile();
        HttpServer server = Moco.httpServer(RemoteTestUtils.port(), Moco.log(file.getAbsolutePath()));
        ResponseHandler mock = Mockito.mock(ResponseHandler.class);
        Mockito.doThrow(RuntimeException.class).when(mock).writeToResponse(ArgumentMatchers.any(SessionContext.class));
        server.request(Moco.by("0XCAFE")).response(mock);
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                try {
                    helper.postContent(RemoteTestUtils.root(), "0XCAFE");
                } catch (IOException ignored) {
                }
            }
        });
        String actual = Files.toString(file, Charset.defaultCharset());
        Assert.assertThat(actual, CoreMatchers.containsString("RuntimeException"));
    }

    @Test
    public void should_log_request_and_response_into_file_with_charset() throws Exception {
        File file = folder.newFile();
        HttpServer server = Moco.httpServer(RemoteTestUtils.port(), Moco.log(file.getAbsolutePath(), Charset.forName("UTF-8")));
        server.request(Moco.by("0XCAFE")).response("0XBABE");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), "0XCAFE"), CoreMatchers.is("0XBABE"));
            }
        });
        String actual = Files.toString(file, Charset.defaultCharset());
        Assert.assertThat(actual, CoreMatchers.containsString("0XBABE"));
        Assert.assertThat(actual, CoreMatchers.containsString("0XCAFE"));
    }
}

