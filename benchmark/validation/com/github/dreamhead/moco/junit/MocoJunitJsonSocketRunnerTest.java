package com.github.dreamhead.moco.junit;


import com.github.dreamhead.moco.helper.MocoSocketHelper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class MocoJunitJsonSocketRunnerTest {
    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.jsonSocketRunner(12306, "src/test/resources/base.json");

    private MocoSocketHelper helper;

    @Test
    public void should_return_expected_response() throws Exception {
        helper.connect();
        Assert.assertThat(helper.send("foo", 3), CoreMatchers.is("bar"));
        helper.close();
    }
}

