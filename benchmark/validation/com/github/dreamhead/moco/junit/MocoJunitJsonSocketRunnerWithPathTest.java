package com.github.dreamhead.moco.junit;


import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.helper.MocoSocketHelper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class MocoJunitJsonSocketRunnerWithPathTest {
    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.jsonSocketRunner(12306, Moco.pathResource("base.json"));

    private MocoSocketHelper helper;

    @Test
    public void should_return_expected_response() throws Exception {
        helper.connect();
        Assert.assertThat(helper.send("foo", 3), CoreMatchers.is("bar"));
        helper.close();
    }
}

