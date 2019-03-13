package org.junit.tests.listening;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.RunListener;


public class ListenerTest {
    private static String log;

    public static class OneTest {
        @Test
        public void nothing() {
        }
    }

    @Test
    public void notifyListenersInTheOrderInWhichTheyAreAdded() {
        JUnitCore core = new JUnitCore();
        ListenerTest.log = "";
        core.addListener(new RunListener() {
            @Override
            public void testRunStarted(Description description) throws Exception {
                ListenerTest.log += "first ";
            }
        });
        core.addListener(new RunListener() {
            @Override
            public void testRunStarted(Description description) throws Exception {
                ListenerTest.log += "second ";
            }
        });
        core.run(ListenerTest.OneTest.class);
        Assert.assertEquals("first second ", ListenerTest.log);
    }
}

