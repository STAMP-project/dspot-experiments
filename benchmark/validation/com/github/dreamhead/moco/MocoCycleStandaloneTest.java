package com.github.dreamhead.moco;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoCycleStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_work_well() throws IOException {
        runWithConfiguration("cycle.json");
        Assert.assertThat(helper.get(remoteUrl("/cycle")), CoreMatchers.is("foo"));
        Assert.assertThat(helper.get(remoteUrl("/cycle")), CoreMatchers.is("bar"));
        Assert.assertThat(helper.get(remoteUrl("/cycle")), CoreMatchers.is("foo"));
    }

    @Test
    public void should_work_well_with_json() throws IOException {
        runWithConfiguration("cycle.json");
        assertJson(remoteUrl("/cycle-json"), "{\"foo\":\"bar\"}");
        assertJson(remoteUrl("/cycle-json"), "{\"hello\":\"world\"}");
        assertJson(remoteUrl("/cycle-json"), "{\"foo\":\"bar\"}");
    }
}

