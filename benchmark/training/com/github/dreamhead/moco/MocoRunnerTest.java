package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoRunnerTest {
    private Runner runner;

    private MocoTestHelper helper;

    @Test
    public void should_work_well() throws IOException {
        Assert.assertThat(helper.get(RemoteTestUtils.root()), CoreMatchers.is("foo"));
    }
}

