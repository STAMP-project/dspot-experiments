package org.junit.runner;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.tests.TestSystem;


public class JUnitCoreTest {
    @Test
    public void shouldAddFailuresToResult() {
        JUnitCore jUnitCore = new JUnitCore();
        Result result = jUnitCore.runMain(new TestSystem(), "NonExistentTest");
        MatcherAssert.assertThat(result.getFailureCount(), CoreMatchers.is(1));
        MatcherAssert.assertThat(result.getFailures().get(0).getException(), CoreMatchers.instanceOf(IllegalArgumentException.class));
    }
}

