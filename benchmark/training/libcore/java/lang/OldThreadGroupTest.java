/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package libcore.java.lang;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;


public class OldThreadGroupTest extends TestCase implements Thread.UncaughtExceptionHandler {
    class MyThread extends Thread {
        public volatile int heartBeat = 0;

        public MyThread(ThreadGroup group, String name) throws IllegalThreadStateException, SecurityException {
            super(group, name);
        }

        @Override
        public void run() {
            while (true) {
                (heartBeat)++;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            } 
        }

        public boolean isActivelyRunning() {
            long MAX_WAIT = 100;
            return isActivelyRunning(MAX_WAIT);
        }

        public boolean isActivelyRunning(long maxWait) {
            int beat = heartBeat;
            long start = System.currentTimeMillis();
            do {
                Thread.yield();
                int beat2 = heartBeat;
                if (beat != beat2) {
                    return true;
                }
            } while (((System.currentTimeMillis()) - start) < maxWait );
            return false;
        }
    }

    private ThreadGroup initialThreadGroup = null;

    public void test_activeGroupCount() {
        ThreadGroup tg = new ThreadGroup("group count");
        TestCase.assertEquals("Incorrect number of groups", 0, tg.activeGroupCount());
        Thread t1 = new Thread(tg, new Runnable() {
            public void run() {
            }
        });
        TestCase.assertEquals("Incorrect number of groups", 0, tg.activeGroupCount());
        t1.start();
        TestCase.assertEquals("Incorrect number of groups", 0, tg.activeGroupCount());
        new ThreadGroup(tg, "test group 1");
        TestCase.assertEquals("Incorrect number of groups", 1, tg.activeGroupCount());
        new ThreadGroup(tg, "test group 2");
        TestCase.assertEquals("Incorrect number of groups", 2, tg.activeGroupCount());
    }

    public void test_enumerateLThreadArray() {
        int numThreads = initialThreadGroup.activeCount();
        Thread[] listOfThreads = new Thread[numThreads];
        int countThread = initialThreadGroup.enumerate(listOfThreads);
        TestCase.assertEquals(numThreads, countThread);
        TestCase.assertTrue("Current thread must be in enumeration of threads", inListOfThreads(listOfThreads));
    }

    public void test_enumerateLThreadArrayLZtest_enumerateLThreadArrayLZ() throws Exception {
        // capture the initial condition
        int initialThreadCount = initialThreadGroup.activeCount();
        Thread[] initialThreads = new Thread[initialThreadCount];
        TestCase.assertEquals(initialThreadCount, initialThreadGroup.enumerate(initialThreads, false));
        TestCase.assertEquals(initialThreadCount, initialThreadGroup.enumerate(initialThreads, true));
        TestCase.assertTrue(inListOfThreads(initialThreads));
        // start some the threads and see how the count changes
        ThreadGroup group = new ThreadGroup(initialThreadGroup, "enumerateThreadArray");
        int groupSize = 3;
        List<OldThreadGroupTest.MyThread> newThreads = populateGroupsWithThreads(group, groupSize);
        TestCase.assertEquals(initialThreadCount, initialThreadGroup.enumerate(initialThreads, true));
        TestCase.assertTrue(inListOfThreads(initialThreads));
        for (OldThreadGroupTest.MyThread thread : newThreads) {
            thread.start();
        }
        Thread.sleep(500);// starting threads isn't instant!

        int afterStartCount = initialThreadGroup.activeCount();
        Set<Thread> initialPlusNew = new HashSet<Thread>();
        initialPlusNew.addAll(Arrays.asList(initialThreads));
        initialPlusNew.addAll(newThreads);
        Thread[] afterStartThreads = new Thread[afterStartCount];
        TestCase.assertEquals(afterStartCount, initialThreadGroup.enumerate(afterStartThreads, true));
        TestCase.assertEquals(initialPlusNew, new HashSet<Thread>(Arrays.asList(afterStartThreads)));
        TestCase.assertTrue(inListOfThreads(afterStartThreads));
        // kill the threads and count 'em again
        for (OldThreadGroupTest.MyThread thread : newThreads) {
            thread.interrupt();
        }
        Thread.sleep(500);// killing threads isn't instant

        int afterDeathCount = initialThreadGroup.activeCount();
        Thread[] afterDeathThreads = new Thread[afterDeathCount];
        TestCase.assertEquals(afterDeathCount, initialThreadGroup.enumerate(afterDeathThreads, false));
        TestCase.assertEquals(Arrays.asList(initialThreads), Arrays.asList(afterDeathThreads));
        TestCase.assertTrue(inListOfThreads(afterDeathThreads));
    }

    public void test_enumerateLThreadGroupArray() {
        int numChildGroupsBefore = initialThreadGroup.activeGroupCount();
        ThreadGroup childGroup = new ThreadGroup(initialThreadGroup, "child group");
        int numChildGroupsAfter = initialThreadGroup.activeGroupCount();
        TestCase.assertTrue(initialThreadGroup.toString(), (numChildGroupsAfter == (numChildGroupsBefore + 1)));
        ThreadGroup[] listOfGroups = new ThreadGroup[numChildGroupsAfter];
        int countGroupThread = initialThreadGroup.enumerate(listOfGroups);
        TestCase.assertEquals(numChildGroupsAfter, countGroupThread);
        TestCase.assertTrue(Arrays.asList(listOfGroups).contains(childGroup));
        ThreadGroup[] listOfGroups1 = new ThreadGroup[numChildGroupsAfter + 1];
        countGroupThread = initialThreadGroup.enumerate(listOfGroups1);
        TestCase.assertEquals(numChildGroupsAfter, countGroupThread);
        TestCase.assertNull(listOfGroups1[((listOfGroups1.length) - 1)]);
        ThreadGroup[] listOfGroups2 = new ThreadGroup[numChildGroupsAfter - 1];
        countGroupThread = initialThreadGroup.enumerate(listOfGroups2);
        TestCase.assertEquals((numChildGroupsAfter - 1), countGroupThread);
        ThreadGroup thrGroup1 = new ThreadGroup("Test Group 1");
        countGroupThread = thrGroup1.enumerate(listOfGroups);
        TestCase.assertEquals(0, countGroupThread);
        childGroup.destroy();
        TestCase.assertTrue(((initialThreadGroup.activeGroupCount()) == (numChildGroupsBefore + 1)));
    }

    public void test_enumerateLThreadGroupArrayLZ() {
        ThreadGroup thrGroup = new ThreadGroup("Test Group 1");
        List<OldThreadGroupTest.MyThread> subThreads = populateGroupsWithThreads(thrGroup, 3);
        int numGroupThreads = thrGroup.activeGroupCount();
        ThreadGroup[] listOfGroups = new ThreadGroup[numGroupThreads];
        TestCase.assertEquals(0, thrGroup.enumerate(listOfGroups, true));
        TestCase.assertEquals(0, thrGroup.enumerate(listOfGroups, false));
        for (OldThreadGroupTest.MyThread thr : subThreads) {
            thr.start();
        }
        numGroupThreads = thrGroup.activeGroupCount();
        listOfGroups = new ThreadGroup[numGroupThreads];
        TestCase.assertEquals(0, thrGroup.enumerate(listOfGroups, true));
        TestCase.assertEquals(0, thrGroup.enumerate(listOfGroups, false));
        ThreadGroup subGroup1 = new ThreadGroup(thrGroup, "Test Group 2");
        List<OldThreadGroupTest.MyThread> subThreads1 = populateGroupsWithThreads(subGroup1, 3);
        numGroupThreads = thrGroup.activeGroupCount();
        listOfGroups = new ThreadGroup[numGroupThreads];
        TestCase.assertEquals(1, thrGroup.enumerate(listOfGroups, true));
        TestCase.assertEquals(1, thrGroup.enumerate(listOfGroups, false));
        for (OldThreadGroupTest.MyThread thr : subThreads1) {
            thr.start();
        }
        numGroupThreads = thrGroup.activeGroupCount();
        listOfGroups = new ThreadGroup[numGroupThreads];
        TestCase.assertEquals(1, thrGroup.enumerate(listOfGroups, true));
        TestCase.assertEquals(1, thrGroup.enumerate(listOfGroups, false));
        for (OldThreadGroupTest.MyThread thr : subThreads) {
            thr.interrupt();
        }
        ThreadGroup subGroup2 = new ThreadGroup(subGroup1, "Test Group 3");
        List<OldThreadGroupTest.MyThread> subThreads2 = populateGroupsWithThreads(subGroup2, 3);
        numGroupThreads = thrGroup.activeGroupCount();
        listOfGroups = new ThreadGroup[numGroupThreads];
        TestCase.assertEquals(2, thrGroup.enumerate(listOfGroups, true));
        TestCase.assertEquals(1, thrGroup.enumerate(listOfGroups, false));
        // RoboVM note: Cleanup. Wait for the threads to finish.
        List<OldThreadGroupTest.MyThread> allThreads = new ArrayList<OldThreadGroupTest.MyThread>();
        allThreads.addAll(subThreads);
        allThreads.addAll(subThreads1);
        allThreads.addAll(subThreads2);
        for (OldThreadGroupTest.MyThread thr : allThreads) {
            thr.interrupt();
            try {
                thr.join(5000);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * java.lang.ThreadGroup#interrupt()
     */
    private static boolean interrupted = false;

    public void test_interrupt() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        ThreadGroup tg = new ThreadGroup("interrupt");
        Thread t1 = new Thread(tg, new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    TestCase.fail("ok");
                }
            }
        });
        TestCase.assertFalse("Incorrect state of thread", OldThreadGroupTest.interrupted);
        t1.start();
        TestCase.assertFalse("Incorrect state of thread", OldThreadGroupTest.interrupted);
        t1.interrupt();
        try {
            t1.join();
        } catch (InterruptedException e) {
        }
        TestCase.assertTrue("Incorrect state of thread", OldThreadGroupTest.interrupted);
        tg.destroy();
    }

    public void test_isDestroyed() {
        final ThreadGroup originalCurrent = getInitialThreadGroup();
        final ThreadGroup testRoot = new ThreadGroup(originalCurrent, "Test group");
        TestCase.assertFalse("Test group is not destroyed yet", testRoot.isDestroyed());
        testRoot.destroy();
        TestCase.assertTrue("Test group already destroyed", testRoot.isDestroyed());
    }
}

