package com.github.dreamhead.moco.runner;


import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.google.common.collect.ImmutableMultimap;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SettingRunnerTest {
    private final MocoTestHelper helper = new MocoTestHelper();

    private SettingRunner runner;

    private InputStream stream;

    @Test
    public void should_run_with_setting() throws IOException {
        stream = getResourceAsStream("settings/settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();
        Assert.assertThat(helper.get(remoteUrl("/foo")), CoreMatchers.is("foo"));
        Assert.assertThat(helper.get(remoteUrl("/bar")), CoreMatchers.is("bar"));
    }

    @Test
    public void should_run_with_setting_with_context() throws IOException {
        stream = getResourceAsStream("settings/context-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();
        Assert.assertThat(helper.get(remoteUrl("/foo/foo")), CoreMatchers.is("foo"));
        Assert.assertThat(helper.get(remoteUrl("/bar/bar")), CoreMatchers.is("bar"));
    }

    @Test
    public void should_run_with_setting_with_file_root() throws IOException {
        stream = getResourceAsStream("settings/fileroot-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();
        Assert.assertThat(helper.get(remoteUrl("/fileroot/fileroot")), CoreMatchers.is("foo.response"));
    }

    @Test
    public void should_run_with_env() throws IOException {
        stream = getResourceAsStream("settings/env-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306, "foo"));
        runner.run();
        Assert.assertThat(helper.get(remoteUrl("/foo/foo")), CoreMatchers.is("foo"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_run_without_env() throws IOException {
        stream = getResourceAsStream("settings/env-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306, "bar"));
        runner.run();
        helper.get(remoteUrl("/foo/foo"));
    }

    @Test
    public void should_run_with_global_response_settings() throws IOException {
        stream = getResourceAsStream("settings/response-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();
        Header header = helper.getResponse(remoteUrl("/foo")).getFirstHeader("foo");
        Assert.assertThat(header.getValue(), CoreMatchers.is("bar"));
    }

    @Test
    public void should_run_with_global_request_settings() throws IOException {
        stream = getResourceAsStream("settings/request-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();
        Assert.assertThat(helper.getWithHeader(remoteUrl("/foo"), ImmutableMultimap.of("foo", "bar")), CoreMatchers.is("foo"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_global_request_settings() throws IOException {
        stream = getResourceAsStream("settings/request-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();
        helper.get(remoteUrl("/foo"));
    }
}

