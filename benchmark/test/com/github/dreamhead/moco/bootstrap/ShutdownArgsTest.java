package com.github.dreamhead.moco.bootstrap;


import com.google.common.base.Optional;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ShutdownArgsTest {
    @Test
    public void should_parse_shutdown_arguments() {
        ShutdownArgs args = ShutdownArgs.parse(new String[]{ "shutdown", "-s", "12305" });
        Assert.assertThat(12305, CoreMatchers.is(args.getShutdownPort().get()));
    }

    @Test(expected = ParseArgException.class)
    public void should_parse_shutdown_default_arguments() {
        ShutdownArgs args = ShutdownArgs.parse(new String[]{ "shutdown" });
        Assert.assertThat(Optional.<Integer>absent(), CoreMatchers.is(args.getShutdownPort()));
    }

    @Test(expected = ParseArgException.class)
    public void should_set_shutdown_port() {
        ShutdownArgs.parse(new String[]{ "shutdown", "-s" });
    }
}

