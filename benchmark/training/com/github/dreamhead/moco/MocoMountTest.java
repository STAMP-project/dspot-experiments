package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.RemoteTestUtils;
import com.google.common.io.CharStreams;
import com.google.common.net.HttpHeaders;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoMountTest extends AbstractMocoHttpTest {
    private static final String MOUNT_DIR = "src/test/resources/test";

    @Test
    public void should_mount_dir_to_uri() throws Exception {
        server.mount(MocoMountTest.MOUNT_DIR, MocoMount.to("/dir"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/dir/dir.response")), CoreMatchers.is("response from dir"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_return_bad_request_for_nonexistence_file() throws Exception {
        server.mount(MocoMountTest.MOUNT_DIR, MocoMount.to("/dir"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(RemoteTestUtils.remoteUrl("/dir/unknown.response"));
            }
        });
    }

    @Test
    public void should_return_inclusion_file() throws Exception {
        server.mount(MocoMountTest.MOUNT_DIR, MocoMount.to("/dir"), MocoMount.include("*.response"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/dir/dir.response")), CoreMatchers.is("response from dir"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_non_inclusion_file() throws Exception {
        server.mount(MocoMountTest.MOUNT_DIR, MocoMount.to("/dir"), MocoMount.include("*.response"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(RemoteTestUtils.remoteUrl("/dir/foo.bar"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_exclusion_file() throws Exception {
        server.mount(MocoMountTest.MOUNT_DIR, MocoMount.to("/dir"), MocoMount.exclude("*.response"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(RemoteTestUtils.remoteUrl("/dir/dir.response"));
            }
        });
    }

    @Test
    public void should_return_non_exclusion_file() throws Exception {
        server.mount(MocoMountTest.MOUNT_DIR, MocoMount.to("/dir"), MocoMount.exclude("*.response"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/dir/foo.bar")), CoreMatchers.is("foo.bar"));
            }
        });
    }

    @Test
    public void should_mount_with_other_handler() throws Exception {
        server.mount(MocoMountTest.MOUNT_DIR, MocoMount.to("/dir")).response(Moco.header(HttpHeaders.CONTENT_TYPE, "text/plain"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse httpResponse = helper.getResponse(RemoteTestUtils.remoteUrl("/dir/dir.response"));
                String value = httpResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
                Assert.assertThat(value, CoreMatchers.is("text/plain"));
                String content = CharStreams.toString(new InputStreamReader(httpResponse.getEntity().getContent()));
                Assert.assertThat(content, CoreMatchers.is("response from dir"));
            }
        });
    }
}

