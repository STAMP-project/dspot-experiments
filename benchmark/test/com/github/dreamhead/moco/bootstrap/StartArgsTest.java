package com.github.dreamhead.moco.bootstrap;


import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.bootstrap.parser.SocketArgsParser;
import com.github.dreamhead.moco.bootstrap.parser.StartArgsParser;
import com.google.common.base.Optional;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class StartArgsTest {
    private StartArgsParser startArgsParser;

    @Test
    public void should_parse_start_arguments() {
        StartArgs args = startArgsParser.parse(new String[]{ "start", "-p", "12306", "-c", "foo.json" });
        Assert.assertThat(args.getPort().get(), CoreMatchers.is(12306));
        Assert.assertThat(args.getConfigurationFile().get(), CoreMatchers.is("foo.json"));
    }

    @Test
    public void should_parse_settings() {
        StartArgs args = startArgsParser.parse(new String[]{ "start", "-p", "12306", "-g", "settings.json" });
        Assert.assertThat(args.getSettings().get(), CoreMatchers.is("settings.json"));
    }

    @Test
    public void should_parse_environment() {
        StartArgs args = startArgsParser.parse(new String[]{ "start", "-p", "12306", "-g", "setting.json", "-e", "foo" });
        Assert.assertThat(args.getEnv().get(), CoreMatchers.is("foo"));
    }

    @Test(expected = ParseArgException.class)
    public void should_set_at_least_config_or_settings() {
        startArgsParser.parse(new String[]{ "start", "-p", "12306" });
    }

    @Test(expected = ParseArgException.class)
    public void should_not_set_config_and_settings() {
        startArgsParser.parse(new String[]{ "start", "-p", "12306", "-c", "foo.json", "-g", "settings.json" });
    }

    @Test(expected = ParseArgException.class)
    public void should_not_set_environment_without_global_settings() {
        startArgsParser.parse(new String[]{ "start", "-p", "12306", "-e", "foo" });
    }

    @Test(expected = ParseArgException.class)
    public void should_not_set_environment_with_config() {
        startArgsParser.parse(new String[]{ "start", "-p", "12306", "-c", "foo.json", "-e", "foo" });
    }

    @Test
    public void should_parse_without_port() {
        StartArgs args = startArgsParser.parse(new String[]{ "start", "-c", "foo.json" });
        Assert.assertThat(args.getPort(), CoreMatchers.is(Optional.<Integer>absent()));
        Assert.assertThat(args.getConfigurationFile().get(), CoreMatchers.is("foo.json"));
    }

    @Test
    public void should_parse_socket() {
        StartArgs args = new SocketArgsParser().parse(new String[]{ "start", "-c", "foo.json" });
        Assert.assertThat(args.isSocket(), CoreMatchers.is(true));
    }
}

