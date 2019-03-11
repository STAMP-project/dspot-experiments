/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.conf;


import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.conf.ReconfigurationUtil.PropertyChange;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestReconfiguration {
    private Configuration conf1;

    private Configuration conf2;

    private static final String PROP1 = "test.prop.one";

    private static final String PROP2 = "test.prop.two";

    private static final String PROP3 = "test.prop.three";

    private static final String PROP4 = "test.prop.four";

    private static final String PROP5 = "test.prop.five";

    private static final String VAL1 = "val1";

    private static final String VAL2 = "val2";

    /**
     * Test ReconfigurationUtil.getChangedProperties.
     */
    @Test
    public void testGetChangedProperties() {
        Collection<ReconfigurationUtil.PropertyChange> changes = ReconfigurationUtil.getChangedProperties(conf2, conf1);
        Assert.assertTrue(("expected 3 changed properties but got " + (changes.size())), ((changes.size()) == 3));
        boolean changeFound = false;
        boolean unsetFound = false;
        boolean setFound = false;
        for (ReconfigurationUtil.PropertyChange c : changes) {
            if (((((c.prop.equals(TestReconfiguration.PROP2)) && ((c.oldVal) != null)) && (c.oldVal.equals(TestReconfiguration.VAL1))) && ((c.newVal) != null)) && (c.newVal.equals(TestReconfiguration.VAL2))) {
                changeFound = true;
            } else
                if ((((c.prop.equals(TestReconfiguration.PROP3)) && ((c.oldVal) != null)) && (c.oldVal.equals(TestReconfiguration.VAL1))) && ((c.newVal) == null)) {
                    unsetFound = true;
                } else
                    if ((((c.prop.equals(TestReconfiguration.PROP4)) && ((c.oldVal) == null)) && ((c.newVal) != null)) && (c.newVal.equals(TestReconfiguration.VAL1))) {
                        setFound = true;
                    }


        }
        Assert.assertTrue("not all changes have been applied", ((changeFound && unsetFound) && setFound));
    }

    /**
     * a simple reconfigurable class
     */
    public static class ReconfigurableDummy extends ReconfigurableBase implements Runnable {
        public volatile boolean running = true;

        public ReconfigurableDummy(Configuration conf) {
            super(conf);
        }

        @Override
        protected Configuration getNewConf() {
            return new Configuration();
        }

        @Override
        public Collection<String> getReconfigurableProperties() {
            return Arrays.asList(TestReconfiguration.PROP1, TestReconfiguration.PROP2, TestReconfiguration.PROP4);
        }

        @Override
        public synchronized String reconfigurePropertyImpl(String property, String newVal) throws ReconfigurationException {
            // do nothing
            return newVal;
        }

        /**
         * Run until PROP1 is no longer VAL1.
         */
        @Override
        public void run() {
            while ((running) && (getConf().get(TestReconfiguration.PROP1).equals(TestReconfiguration.VAL1))) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignore) {
                    // do nothing
                }
            } 
        }
    }

    /**
     * Test reconfiguring a Reconfigurable.
     */
    @Test
    public void testReconfigure() {
        TestReconfiguration.ReconfigurableDummy dummy = new TestReconfiguration.ReconfigurableDummy(conf1);
        Assert.assertTrue(((TestReconfiguration.PROP1) + " set to wrong value "), getConf().get(TestReconfiguration.PROP1).equals(TestReconfiguration.VAL1));
        Assert.assertTrue(((TestReconfiguration.PROP2) + " set to wrong value "), getConf().get(TestReconfiguration.PROP2).equals(TestReconfiguration.VAL1));
        Assert.assertTrue(((TestReconfiguration.PROP3) + " set to wrong value "), getConf().get(TestReconfiguration.PROP3).equals(TestReconfiguration.VAL1));
        Assert.assertTrue(((TestReconfiguration.PROP4) + " set to wrong value "), ((getConf().get(TestReconfiguration.PROP4)) == null));
        Assert.assertTrue(((TestReconfiguration.PROP5) + " set to wrong value "), ((getConf().get(TestReconfiguration.PROP5)) == null));
        Assert.assertTrue(((TestReconfiguration.PROP1) + " should be reconfigurable "), dummy.isPropertyReconfigurable(TestReconfiguration.PROP1));
        Assert.assertTrue(((TestReconfiguration.PROP2) + " should be reconfigurable "), dummy.isPropertyReconfigurable(TestReconfiguration.PROP2));
        Assert.assertFalse(((TestReconfiguration.PROP3) + " should not be reconfigurable "), dummy.isPropertyReconfigurable(TestReconfiguration.PROP3));
        Assert.assertTrue(((TestReconfiguration.PROP4) + " should be reconfigurable "), dummy.isPropertyReconfigurable(TestReconfiguration.PROP4));
        Assert.assertFalse(((TestReconfiguration.PROP5) + " should not be reconfigurable "), dummy.isPropertyReconfigurable(TestReconfiguration.PROP5));
        // change something to the same value as before
        {
            boolean exceptionCaught = false;
            try {
                reconfigureProperty(TestReconfiguration.PROP1, TestReconfiguration.VAL1);
                Assert.assertTrue(((TestReconfiguration.PROP1) + " set to wrong value "), getConf().get(TestReconfiguration.PROP1).equals(TestReconfiguration.VAL1));
            } catch (ReconfigurationException e) {
                exceptionCaught = true;
            }
            Assert.assertFalse("received unexpected exception", exceptionCaught);
        }
        // change something to null
        {
            boolean exceptionCaught = false;
            try {
                dummy.reconfigureProperty(TestReconfiguration.PROP1, null);
                Assert.assertTrue(((TestReconfiguration.PROP1) + "set to wrong value "), ((getConf().get(TestReconfiguration.PROP1)) == null));
            } catch (ReconfigurationException e) {
                exceptionCaught = true;
            }
            Assert.assertFalse("received unexpected exception", exceptionCaught);
        }
        // change something to a different value than before
        {
            boolean exceptionCaught = false;
            try {
                reconfigureProperty(TestReconfiguration.PROP1, TestReconfiguration.VAL2);
                Assert.assertTrue(((TestReconfiguration.PROP1) + "set to wrong value "), getConf().get(TestReconfiguration.PROP1).equals(TestReconfiguration.VAL2));
            } catch (ReconfigurationException e) {
                exceptionCaught = true;
            }
            Assert.assertFalse("received unexpected exception", exceptionCaught);
        }
        // set unset property to null
        {
            boolean exceptionCaught = false;
            try {
                dummy.reconfigureProperty(TestReconfiguration.PROP4, null);
                Assert.assertTrue(((TestReconfiguration.PROP4) + "set to wrong value "), ((getConf().get(TestReconfiguration.PROP4)) == null));
            } catch (ReconfigurationException e) {
                exceptionCaught = true;
            }
            Assert.assertFalse("received unexpected exception", exceptionCaught);
        }
        // set unset property
        {
            boolean exceptionCaught = false;
            try {
                reconfigureProperty(TestReconfiguration.PROP4, TestReconfiguration.VAL1);
                Assert.assertTrue(((TestReconfiguration.PROP4) + "set to wrong value "), getConf().get(TestReconfiguration.PROP4).equals(TestReconfiguration.VAL1));
            } catch (ReconfigurationException e) {
                exceptionCaught = true;
            }
            Assert.assertFalse("received unexpected exception", exceptionCaught);
        }
        // try to set unset property to null (not reconfigurable)
        {
            boolean exceptionCaught = false;
            try {
                dummy.reconfigureProperty(TestReconfiguration.PROP5, null);
            } catch (ReconfigurationException e) {
                exceptionCaught = true;
            }
            Assert.assertTrue("did not receive expected exception", exceptionCaught);
        }
        // try to set unset property to value (not reconfigurable)
        {
            boolean exceptionCaught = false;
            try {
                reconfigureProperty(TestReconfiguration.PROP5, TestReconfiguration.VAL1);
            } catch (ReconfigurationException e) {
                exceptionCaught = true;
            }
            Assert.assertTrue("did not receive expected exception", exceptionCaught);
        }
        // try to change property to value (not reconfigurable)
        {
            boolean exceptionCaught = false;
            try {
                reconfigureProperty(TestReconfiguration.PROP3, TestReconfiguration.VAL2);
            } catch (ReconfigurationException e) {
                exceptionCaught = true;
            }
            Assert.assertTrue("did not receive expected exception", exceptionCaught);
        }
        // try to change property to null (not reconfigurable)
        {
            boolean exceptionCaught = false;
            try {
                dummy.reconfigureProperty(TestReconfiguration.PROP3, null);
            } catch (ReconfigurationException e) {
                exceptionCaught = true;
            }
            Assert.assertTrue("did not receive expected exception", exceptionCaught);
        }
    }

    /**
     * Test whether configuration changes are visible in another thread.
     */
    @Test
    public void testThread() throws ReconfigurationException {
        TestReconfiguration.ReconfigurableDummy dummy = new TestReconfiguration.ReconfigurableDummy(conf1);
        Assert.assertTrue(getConf().get(TestReconfiguration.PROP1).equals(TestReconfiguration.VAL1));
        Thread dummyThread = new Thread(dummy);
        dummyThread.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignore) {
            // do nothing
        }
        reconfigureProperty(TestReconfiguration.PROP1, TestReconfiguration.VAL2);
        long endWait = (Time.now()) + 2000;
        while ((dummyThread.isAlive()) && ((Time.now()) < endWait)) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignore) {
                // do nothing
            }
        } 
        Assert.assertFalse("dummy thread should not be alive", dummyThread.isAlive());
        dummy.running = false;
        try {
            dummyThread.join();
        } catch (InterruptedException ignore) {
            // do nothing
        }
        Assert.assertTrue(((TestReconfiguration.PROP1) + " is set to wrong value"), getConf().get(TestReconfiguration.PROP1).equals(TestReconfiguration.VAL2));
    }

    private static class AsyncReconfigurableDummy extends ReconfigurableBase {
        AsyncReconfigurableDummy(Configuration conf) {
            super(conf);
        }

        @Override
        protected Configuration getNewConf() {
            return new Configuration();
        }

        final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public Collection<String> getReconfigurableProperties() {
            return Arrays.asList(TestReconfiguration.PROP1, TestReconfiguration.PROP2, TestReconfiguration.PROP4);
        }

        @Override
        public synchronized String reconfigurePropertyImpl(String property, String newVal) throws ReconfigurationException {
            try {
                latch.await();
            } catch (InterruptedException e) {
                // Ignore
            }
            return newVal;
        }
    }

    @Test
    public void testAsyncReconfigure() throws IOException, InterruptedException, ReconfigurationException {
        TestReconfiguration.AsyncReconfigurableDummy dummy = Mockito.spy(new TestReconfiguration.AsyncReconfigurableDummy(conf1));
        List<PropertyChange> changes = Lists.newArrayList();
        changes.add(new PropertyChange("name1", "new1", "old1"));
        changes.add(new PropertyChange("name2", "new2", "old2"));
        changes.add(new PropertyChange("name3", "new3", "old3"));
        Mockito.doReturn(changes).when(dummy).getChangedProperties(ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(Configuration.class));
        isPropertyReconfigurable(ArgumentMatchers.eq("name1"));
        isPropertyReconfigurable(ArgumentMatchers.eq("name2"));
        isPropertyReconfigurable(ArgumentMatchers.eq("name3"));
        Mockito.doReturn("dummy").when(dummy).reconfigurePropertyImpl(ArgumentMatchers.eq("name1"), ArgumentMatchers.anyString());
        Mockito.doReturn("dummy").when(dummy).reconfigurePropertyImpl(ArgumentMatchers.eq("name2"), ArgumentMatchers.anyString());
        Mockito.doThrow(new ReconfigurationException("NAME3", "NEW3", "OLD3", new IOException("io exception"))).when(dummy).reconfigurePropertyImpl(ArgumentMatchers.eq("name3"), ArgumentMatchers.anyString());
        startReconfigurationTask();
        TestReconfiguration.waitAsyncReconfigureTaskFinish(dummy);
        ReconfigurationTaskStatus status = getReconfigurationTaskStatus();
        Assert.assertEquals(2, status.getStatus().size());
        for (Map.Entry<PropertyChange, Optional<String>> result : status.getStatus().entrySet()) {
            PropertyChange change = result.getKey();
            if (change.prop.equals("name1")) {
                Assert.assertFalse(result.getValue().isPresent());
            } else
                if (change.prop.equals("name2")) {
                    MatcherAssert.assertThat(result.getValue().get(), CoreMatchers.containsString("Property name2 is not reconfigurable"));
                } else
                    if (change.prop.equals("name3")) {
                        MatcherAssert.assertThat(result.getValue().get(), CoreMatchers.containsString("io exception"));
                    } else {
                        Assert.fail(("Unknown property: " + (change.prop)));
                    }


        }
    }

    @Test(timeout = 30000)
    public void testStartReconfigurationFailureDueToExistingRunningTask() throws IOException, InterruptedException {
        TestReconfiguration.AsyncReconfigurableDummy dummy = Mockito.spy(new TestReconfiguration.AsyncReconfigurableDummy(conf1));
        List<PropertyChange> changes = Lists.newArrayList(new PropertyChange(TestReconfiguration.PROP1, "new1", "old1"));
        Mockito.doReturn(changes).when(dummy).getChangedProperties(ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(Configuration.class));
        ReconfigurationTaskStatus status = getReconfigurationTaskStatus();
        Assert.assertFalse(status.hasTask());
        startReconfigurationTask();
        status = dummy.getReconfigurationTaskStatus();
        Assert.assertTrue(status.hasTask());
        Assert.assertFalse(status.stopped());
        // An active reconfiguration task is running.
        try {
            startReconfigurationTask();
            Assert.fail("Expect to throw IOException.");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Another reconfiguration task is running", e);
        }
        status = dummy.getReconfigurationTaskStatus();
        Assert.assertTrue(status.hasTask());
        Assert.assertFalse(status.stopped());
        dummy.latch.countDown();
        TestReconfiguration.waitAsyncReconfigureTaskFinish(dummy);
        status = dummy.getReconfigurationTaskStatus();
        Assert.assertTrue(status.hasTask());
        Assert.assertTrue(status.stopped());
        // The first task has finished.
        startReconfigurationTask();
        TestReconfiguration.waitAsyncReconfigureTaskFinish(dummy);
        ReconfigurationTaskStatus status2 = getReconfigurationTaskStatus();
        Assert.assertTrue(((status2.getStartTime()) >= (status.getEndTime())));
        shutdownReconfigurationTask();
        try {
            startReconfigurationTask();
            Assert.fail("Expect to throw IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("The server is stopped", e);
        }
    }

    /**
     * Ensure that {@link ReconfigurableBase#reconfigureProperty} updates the
     * parent's cached configuration on success.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 300000)
    public void testConfIsUpdatedOnSuccess() throws ReconfigurationException {
        final String property = "FOO";
        final String value1 = "value1";
        final String value2 = "value2";
        final Configuration conf = new Configuration();
        conf.set(property, value1);
        final Configuration newConf = new Configuration();
        newConf.set(property, value2);
        final ReconfigurableBase reconfigurable = makeReconfigurable(conf, newConf, Arrays.asList(property));
        reconfigurable.reconfigureProperty(property, value2);
        MatcherAssert.assertThat(reconfigurable.getConf().get(property), CoreMatchers.is(value2));
    }

    /**
     * Ensure that {@link ReconfigurableBase#startReconfigurationTask} updates
     * its parent's cached configuration on success.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 300000)
    public void testConfIsUpdatedOnSuccessAsync() throws IOException, InterruptedException, TimeoutException, ReconfigurationException {
        final String property = "FOO";
        final String value1 = "value1";
        final String value2 = "value2";
        final Configuration conf = new Configuration();
        conf.set(property, value1);
        final Configuration newConf = new Configuration();
        newConf.set(property, value2);
        final ReconfigurableBase reconfigurable = makeReconfigurable(conf, newConf, Arrays.asList(property));
        // Kick off a reconfiguration task and wait until it completes.
        reconfigurable.startReconfigurationTask();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return reconfigurable.getReconfigurationTaskStatus().stopped();
            }
        }, 100, 60000);
        MatcherAssert.assertThat(reconfigurable.getConf().get(property), CoreMatchers.is(value2));
    }

    /**
     * Ensure that {@link ReconfigurableBase#reconfigureProperty} unsets the
     * property in its parent's configuration when the new value is null.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 300000)
    public void testConfIsUnset() throws ReconfigurationException {
        final String property = "FOO";
        final String value1 = "value1";
        final Configuration conf = new Configuration();
        conf.set(property, value1);
        final Configuration newConf = new Configuration();
        final ReconfigurableBase reconfigurable = makeReconfigurable(conf, newConf, Arrays.asList(property));
        reconfigurable.reconfigureProperty(property, null);
        Assert.assertNull(reconfigurable.getConf().get(property));
    }

    /**
     * Ensure that {@link ReconfigurableBase#startReconfigurationTask} unsets the
     * property in its parent's configuration when the new value is null.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 300000)
    public void testConfIsUnsetAsync() throws IOException, InterruptedException, TimeoutException, ReconfigurationException {
        final String property = "FOO";
        final String value1 = "value1";
        final Configuration conf = new Configuration();
        conf.set(property, value1);
        final Configuration newConf = new Configuration();
        final ReconfigurableBase reconfigurable = makeReconfigurable(conf, newConf, Arrays.asList(property));
        // Kick off a reconfiguration task and wait until it completes.
        reconfigurable.startReconfigurationTask();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return reconfigurable.getReconfigurationTaskStatus().stopped();
            }
        }, 100, 60000);
        Assert.assertNull(reconfigurable.getConf().get(property));
    }
}

