package org.junit.tests.running.classes;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;


public class RunWithTest {
    private static String log;

    public static class ExampleRunner extends Runner {
        public ExampleRunner(Class<?> klass) {
            RunWithTest.log += "initialize";
        }

        @Override
        public void run(RunNotifier notifier) {
            RunWithTest.log += "run";
        }

        @Override
        public int testCount() {
            RunWithTest.log += "count";
            return 0;
        }

        @Override
        public Description getDescription() {
            RunWithTest.log += "plan";
            return Description.createSuiteDescription("example");
        }
    }

    @RunWith(RunWithTest.ExampleRunner.class)
    public static class ExampleTest {}

    @Test
    public void run() {
        RunWithTest.log = "";
        JUnitCore.runClasses(RunWithTest.ExampleTest.class);
        Assert.assertTrue(RunWithTest.log.contains("plan"));
        Assert.assertTrue(RunWithTest.log.contains("initialize"));
        Assert.assertTrue(RunWithTest.log.contains("run"));
    }

    public static class SubExampleTest extends RunWithTest.ExampleTest {}

    @Test
    public void runWithExtendsToSubclasses() {
        RunWithTest.log = "";
        JUnitCore.runClasses(RunWithTest.SubExampleTest.class);
        Assert.assertTrue(RunWithTest.log.contains("run"));
    }

    public static class BadRunner extends Runner {
        @Override
        public Description getDescription() {
            return null;
        }

        @Override
        public void run(RunNotifier notifier) {
            // do nothing
        }
    }

    @RunWith(RunWithTest.BadRunner.class)
    public static class Empty {}

    @Test
    public void characterizeErrorMessageFromBadRunner() {
        Assert.assertEquals("Custom runner class BadRunner should have a public constructor with signature BadRunner(Class testClass)", JUnitCore.runClasses(RunWithTest.Empty.class).getFailures().get(0).getMessage());
    }
}

