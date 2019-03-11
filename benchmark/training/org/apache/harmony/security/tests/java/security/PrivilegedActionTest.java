package org.apache.harmony.security.tests.java.security;


import java.security.AccessController;
import java.security.PrivilegedAction;
import junit.framework.TestCase;


public class PrivilegedActionTest extends TestCase {
    private class MyPrivilegedAction implements PrivilegedAction<String> {
        private boolean called = false;

        public String run() {
            called = true;
            return "ok";
        }
    }

    private class MyPrivilegedAction2 implements PrivilegedAction<String> {
        private boolean called = false;

        public String run() {
            called = true;
            throw new RuntimeException("fail");
        }
    }

    public void testRun() {
        PrivilegedActionTest.MyPrivilegedAction action = new PrivilegedActionTest.MyPrivilegedAction();
        String result = AccessController.doPrivileged(action);
        TestCase.assertEquals("return value not correct", "ok", result);
        TestCase.assertTrue("run method was not called", action.called);
        PrivilegedActionTest.MyPrivilegedAction2 action2 = new PrivilegedActionTest.MyPrivilegedAction2();
        try {
            result = AccessController.doPrivileged(action2);
            TestCase.fail("exception expected");
        } catch (RuntimeException e) {
            // expected exception
        }
    }
}

