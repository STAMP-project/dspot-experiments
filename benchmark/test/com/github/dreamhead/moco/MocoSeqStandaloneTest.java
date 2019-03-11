package com.github.dreamhead.moco;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoSeqStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_work_well() throws IOException {
        runWithConfiguration("seq.json");
        Assert.assertThat(helper.get(remoteUrl("/seq")), CoreMatchers.is("foo"));
        Assert.assertThat(helper.get(remoteUrl("/seq")), CoreMatchers.is("bar"));
        Assert.assertThat(helper.get(remoteUrl("/seq")), CoreMatchers.is("bar"));
    }

    @Test
    public void should_work_well_with_json() throws IOException {
        runWithConfiguration("seq.json");
        assertJson(remoteUrl("/seq-json"), "{\"foo\":\"bar\"}");
        assertJson(remoteUrl("/seq-json"), "{\"hello\":\"world\"}");
        assertJson(remoteUrl("/seq-json"), "{\"hello\":\"world\"}");
    }
}

