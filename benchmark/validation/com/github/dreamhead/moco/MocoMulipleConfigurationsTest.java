package com.github.dreamhead.moco;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoMulipleConfigurationsTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_run_with_many_configurations() throws IOException {
        runWithConfiguration("settings/details/foo.json", "settings/details/bar.json");
        Assert.assertThat(helper.get(remoteUrl("/foo")), CoreMatchers.is("foo"));
        Assert.assertThat(helper.get(remoteUrl("/bar")), CoreMatchers.is("bar"));
    }
}

