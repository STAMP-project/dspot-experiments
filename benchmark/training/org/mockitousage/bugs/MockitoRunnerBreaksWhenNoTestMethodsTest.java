/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import java.io.OutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockitoutil.TestBase;


// @Ignore("for demo only. this test cannot be enabled as it fails :)")
public class MockitoRunnerBreaksWhenNoTestMethodsTest extends TestBase {
    @Test
    public void ensure_the_test_runner_breaks() throws Exception {
        JUnitCore runner = new JUnitCore();
        // runner.addListener(new TextListener(System.out));
        runner.addListener(new TextListener(MockitoRunnerBreaksWhenNoTestMethodsTest.DevNull.out));
        Result result = runner.run(MockitoRunnerBreaksWhenNoTestMethodsTest.TestClassWithoutTestMethod.class);
        Assert.assertEquals(1, result.getFailureCount());
        Assert.assertTrue(((result.getFailures().get(0).getException()) instanceof MockitoException));
        Assert.assertFalse(result.wasSuccessful());
    }

    @RunWith(MockitoJUnitRunner.class)
    static class TestClassWithoutTestMethod {
        // package visibility is important
        public void notATestMethod() {
        }
    }

    public static final class DevNull {
        public static final PrintStream out = new PrintStream(new OutputStream() {
            public void close() {
            }

            public void flush() {
            }

            public void write(byte[] b) {
            }

            public void write(byte[] b, int off, int len) {
            }

            public void write(int b) {
            }
        });
    }
}

