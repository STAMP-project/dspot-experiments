package org.junit.tests.listening;


import java.io.OutputStream;
import junit.framework.TestCase;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;


public class TextListenerTest extends TestCase {
    private JUnitCore runner;

    private OutputStream results;

    public static class OneTest {
        @Test
        public void one() {
        }
    }

    public void testSuccess() throws Exception {
        runner.run(TextListenerTest.OneTest.class);
        TestCase.assertTrue(results.toString().startsWith(convert(".\nTime: ")));
        TestCase.assertTrue(results.toString().endsWith(convert("\n\nOK (1 test)\n\n")));
    }

    public static class ErrorTest {
        @Test
        public void error() throws Exception {
            throw new Exception();
        }
    }

    public void testError() throws Exception {
        runner.run(TextListenerTest.ErrorTest.class);
        TestCase.assertTrue(results.toString().startsWith(convert(".E\nTime: ")));
        TestCase.assertTrue(((results.toString().indexOf(convert("\nThere was 1 failure:\n1) error(org.junit.tests.listening.TextListenerTest$ErrorTest)\njava.lang.Exception"))) != (-1)));
    }

    public static class Time {
        @Test
        public void time() {
        }
    }

    public void testTime() {
        runner.run(TextListenerTest.Time.class);
        Assert.assertThat(results.toString(), StringContains.containsString("Time: "));
        Assert.assertThat(results.toString(), IsNot.not(StringContains.containsString(convert("Time: \n"))));
    }
}

