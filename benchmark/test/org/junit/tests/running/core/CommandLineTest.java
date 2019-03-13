package org.junit.tests.running.core;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;


public class CommandLineTest {
    private ByteArrayOutputStream results;

    private PrintStream oldOut;

    private static boolean testWasRun;

    public static class Example {
        @Test
        public void test() {
            CommandLineTest.testWasRun = true;
        }
    }

    @Test
    public void runATest() {
        CommandLineTest.testWasRun = false;
        new MainRunner().runWithCheckForSystemExit(new Runnable() {
            public void run() {
                JUnitCore.main("org.junit.tests.running.core.CommandLineTest$Example");
            }
        });
        Assert.assertTrue(CommandLineTest.testWasRun);
    }

    @Test
    public void runAClass() {
        CommandLineTest.testWasRun = false;
        JUnitCore.runClasses(CommandLineTest.Example.class);
        Assert.assertTrue(CommandLineTest.testWasRun);
    }

    private static int fCount;

    public static class Count {
        @Test
        public void increment() {
            (CommandLineTest.fCount)++;
        }
    }

    @Test
    public void runTwoClassesAsArray() {
        CommandLineTest.fCount = 0;
        JUnitCore.runClasses(new Class[]{ CommandLineTest.Count.class, CommandLineTest.Count.class });
        Assert.assertEquals(2, CommandLineTest.fCount);
    }

    @Test
    public void runTwoClasses() {
        CommandLineTest.fCount = 0;
        JUnitCore.runClasses(CommandLineTest.Count.class, CommandLineTest.Count.class);
        Assert.assertEquals(2, CommandLineTest.fCount);
    }
}

