package org.junit.tests.running.classes;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.RunListener;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;


public class BlockJUnit4ClassRunnerTest {
    public static class OuterClass {
        public class Enclosed {
            @Test
            public void test() {
            }
        }
    }

    @Test
    public void detectNonStaticEnclosedClass() throws Exception {
        try {
            new BlockJUnit4ClassRunner(BlockJUnit4ClassRunnerTest.OuterClass.Enclosed.class);
        } catch (InitializationError e) {
            List<Throwable> causes = e.getCauses();
            Assert.assertEquals("Wrong number of causes.", 1, causes.size());
            Assert.assertEquals("Wrong exception.", "The inner class org.junit.tests.running.classes.BlockJUnit4ClassRunnerTest$OuterClass$Enclosed is not static.", causes.get(0).getMessage());
        }
    }

    private static String log;

    public static class MethodBlockAfterFireTestStarted {
        public MethodBlockAfterFireTestStarted() {
            BlockJUnit4ClassRunnerTest.log += " init";
        }

        @Test
        public void test() {
            BlockJUnit4ClassRunnerTest.log += " test";
        }
    }

    @Test
    public void methodBlockAfterFireTestStarted() {
        BlockJUnit4ClassRunnerTest.log = "";
        JUnitCore junit = new JUnitCore();
        junit.addListener(new RunListener() {
            @Override
            public void testStarted(Description description) throws Exception {
                BlockJUnit4ClassRunnerTest.log += (" testStarted(" + (description.getMethodName())) + ")";
            }

            @Override
            public void testFinished(Description description) throws Exception {
                BlockJUnit4ClassRunnerTest.log += (" testFinished(" + (description.getMethodName())) + ")";
            }
        });
        junit.run(BlockJUnit4ClassRunnerTest.MethodBlockAfterFireTestStarted.class);
        Assert.assertEquals(" testStarted(test) init test testFinished(test)", BlockJUnit4ClassRunnerTest.log);
    }
}

