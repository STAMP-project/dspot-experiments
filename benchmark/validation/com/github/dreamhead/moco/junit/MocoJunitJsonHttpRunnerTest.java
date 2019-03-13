package com.github.dreamhead.moco.junit;


import com.github.dreamhead.moco.AbstractMocoStandaloneTest;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class MocoJunitJsonHttpRunnerTest extends AbstractMocoStandaloneTest {
    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.jsonHttpRunner(12306, "src/test/resources/foo.json");

    @Test
    public void should_return_expected_message() throws IOException {
        Assert.assertThat(helper.get(root()), CoreMatchers.is("foo"));
    }

    @Test
    public void should_return_expected_message_2() throws IOException {
        Assert.assertThat(helper.get(root()), CoreMatchers.is("foo"));
    }
}

