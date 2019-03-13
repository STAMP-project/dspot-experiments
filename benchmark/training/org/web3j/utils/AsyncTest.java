package org.web3j.utils;


import java.util.concurrent.ExecutionException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class AsyncTest {
    @Test
    public void testRun() throws Exception {
        Assert.assertThat(Async.run(() -> "").get(), Is.is(""));
    }

    @Test(expected = ExecutionException.class)
    public void testRunException() throws Exception {
        Async.run(() -> {
            throw new RuntimeException("");
        }).get();
    }
}

