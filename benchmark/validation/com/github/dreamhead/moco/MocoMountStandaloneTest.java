package com.github.dreamhead.moco;


import com.google.common.io.CharStreams;
import com.google.common.net.HttpHeaders;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoMountStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_mount_dir_to_uri() throws IOException {
        runWithConfiguration("mount.json");
        Assert.assertThat(helper.get(remoteUrl("/mount/mount.response")), CoreMatchers.is("response from mount"));
    }

    @Test
    public void should_mount_dir_to_uri_with_include() throws IOException {
        runWithConfiguration("mount.json");
        Assert.assertThat(helper.get(remoteUrl("/mount-include/mount.response")), CoreMatchers.is("response from mount"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_return_non_inclusion() throws IOException {
        runWithConfiguration("mount.json");
        helper.get(remoteUrl("/mount-include/foo.bar"));
    }

    @Test
    public void should_mount_dir_to_uri_with_exclude() throws IOException {
        runWithConfiguration("mount.json");
        Assert.assertThat(helper.get(remoteUrl("/mount-exclude/foo.bar")), CoreMatchers.is("foo.bar"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_return_exclusion() throws IOException {
        runWithConfiguration("mount.json");
        helper.get(remoteUrl("/mount-exclude/mount.response"));
    }

    @Test
    public void should_mount_dir_to_uri_with_response() throws IOException {
        runWithConfiguration("mount.json");
        HttpResponse httpResponse = helper.getResponse(remoteUrl("/mount-response/mount.response"));
        String value = httpResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
        Assert.assertThat(value, CoreMatchers.is("text/plain"));
        String content = CharStreams.toString(new InputStreamReader(httpResponse.getEntity().getContent()));
        Assert.assertThat(content, CoreMatchers.is("response from mount"));
    }
}

