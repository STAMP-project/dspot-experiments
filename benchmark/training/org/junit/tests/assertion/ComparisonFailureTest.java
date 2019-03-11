package org.junit.tests.assertion;


import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ComparisonFailureTest {
    private String expected;

    private String actual;

    private String message;

    public ComparisonFailureTest(String e, String a, String m) {
        expected = e;
        actual = a;
        message = m;
    }

    @Test
    public void compactFailureMessage() {
        ComparisonFailure failure = new ComparisonFailure("", expected, actual);
        Assert.assertEquals(message, failure.getMessage());
    }
}

