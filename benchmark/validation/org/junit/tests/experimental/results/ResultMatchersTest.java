package org.junit.tests.experimental.results;


import java.util.ArrayList;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;


public class ResultMatchersTest {
    @Test
    public void hasFailuresHasGoodDescription() {
        Assert.assertThat(ResultMatchers.failureCountIs(3).toString(), CoreMatchers.is("has 3 failures"));
    }

    @Test
    public void hasFailureContaining_givenResultWithNoFailures() {
        PrintableResult resultWithNoFailures = new PrintableResult(new ArrayList<Failure>());
        Assert.assertThat(ResultMatchers.hasFailureContaining("").matches(resultWithNoFailures), CoreMatchers.is(false));
    }

    @Test
    public void hasFailureContaining_givenResultWithOneFailure() {
        PrintableResult resultWithOneFailure = new PrintableResult(Collections.singletonList(new Failure(Description.EMPTY, new RuntimeException("my failure"))));
        Assert.assertThat(ResultMatchers.hasFailureContaining("my failure").matches(resultWithOneFailure), CoreMatchers.is(true));
        Assert.assertThat(ResultMatchers.hasFailureContaining("his failure").matches(resultWithOneFailure), CoreMatchers.is(false));
    }
}

