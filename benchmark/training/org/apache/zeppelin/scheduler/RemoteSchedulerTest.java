/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.scheduler;


import java.util.Map;
import org.apache.zeppelin.interpreter.AbstractInterpreterTest;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterSetting;
import org.apache.zeppelin.interpreter.remote.RemoteInterpreter;
import org.apache.zeppelin.interpreter.remote.RemoteInterpreterProcessListener;
import org.apache.zeppelin.resource.LocalResourcePool;
import org.apache.zeppelin.scheduler.Job.Status;
import org.junit.Assert;
import org.junit.Test;


public class RemoteSchedulerTest extends AbstractInterpreterTest implements RemoteInterpreterProcessListener {
    private InterpreterSetting interpreterSetting;

    private SchedulerFactory schedulerSvc;

    private static final int TICK_WAIT = 100;

    private static final int MAX_WAIT_CYCLES = 100;

    @Test
    public void test() throws Exception {
        final RemoteInterpreter intpA = ((RemoteInterpreter) (interpreterSetting.getInterpreter("user1", "note1", "mock")));
        intpA.open();
        Scheduler scheduler = intpA.getScheduler();
        Job job = new Job("jobId", "jobName", null) {
            Object results;

            @Override
            public Object getReturn() {
                return results;
            }

            @Override
            public int progress() {
                return 0;
            }

            @Override
            public Map<String, Object> info() {
                return null;
            }

            @Override
            protected Object jobRun() throws Throwable {
                intpA.interpret("1000", InterpreterContext.builder().setNoteId("noteId").setParagraphId("jobId").setResourcePool(new LocalResourcePool("pool1")).build());
                return "1000";
            }

            @Override
            protected boolean jobAbort() {
                return false;
            }

            @Override
            public void setResult(Object results) {
                this.results = results;
            }
        };
        scheduler.submit(job);
        int cycles = 0;
        while ((!(job.isRunning())) && (cycles < (RemoteSchedulerTest.MAX_WAIT_CYCLES))) {
            AbstractInterpreterTest.LOGGER.info(("Status:" + (job.getStatus())));
            Thread.sleep(RemoteSchedulerTest.TICK_WAIT);
            cycles++;
        } 
        Assert.assertTrue(job.isRunning());
        Thread.sleep((5 * (RemoteSchedulerTest.TICK_WAIT)));
        cycles = 0;
        while ((!(job.isTerminated())) && (cycles < (RemoteSchedulerTest.MAX_WAIT_CYCLES))) {
            Thread.sleep(RemoteSchedulerTest.TICK_WAIT);
            cycles++;
        } 
        Assert.assertTrue(job.isTerminated());
        intpA.close();
        schedulerSvc.removeScheduler("test");
    }

    @Test
    public void testAbortOnPending() throws Exception {
        final RemoteInterpreter intpA = ((RemoteInterpreter) (interpreterSetting.getInterpreter("user1", "note1", "mock")));
        intpA.open();
        Scheduler scheduler = intpA.getScheduler();
        Job job1 = new Job("jobId1", "jobName1", null) {
            Object results;

            InterpreterContext context = InterpreterContext.builder().setNoteId("noteId").setParagraphId("jobId1").setResourcePool(new LocalResourcePool("pool1")).build();

            @Override
            public Object getReturn() {
                return results;
            }

            @Override
            public int progress() {
                return 0;
            }

            @Override
            public Map<String, Object> info() {
                return null;
            }

            @Override
            protected Object jobRun() throws Throwable {
                intpA.interpret("1000", context);
                return "1000";
            }

            @Override
            protected boolean jobAbort() {
                if (isRunning()) {
                    try {
                        intpA.cancel(context);
                    } catch (InterpreterException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }

            @Override
            public void setResult(Object results) {
                this.results = results;
            }
        };
        Job job2 = new Job("jobId2", "jobName2", null) {
            public Object results;

            InterpreterContext context = InterpreterContext.builder().setNoteId("noteId").setParagraphId("jobId2").setResourcePool(new LocalResourcePool("pool1")).build();

            @Override
            public Object getReturn() {
                return results;
            }

            @Override
            public int progress() {
                return 0;
            }

            @Override
            public Map<String, Object> info() {
                return null;
            }

            @Override
            protected Object jobRun() throws Throwable {
                intpA.interpret("1000", context);
                return "1000";
            }

            @Override
            protected boolean jobAbort() {
                if (isRunning()) {
                    try {
                        intpA.cancel(context);
                    } catch (InterpreterException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }

            @Override
            public void setResult(Object results) {
                this.results = results;
            }
        };
        job2.setResult("result2");
        scheduler.submit(job1);
        scheduler.submit(job2);
        int cycles = 0;
        while ((!(job1.isRunning())) && (cycles < (RemoteSchedulerTest.MAX_WAIT_CYCLES))) {
            Thread.sleep(RemoteSchedulerTest.TICK_WAIT);
            cycles++;
        } 
        Assert.assertTrue(job1.isRunning());
        Assert.assertTrue(((job2.getStatus()) == (Status.PENDING)));
        job2.abort();
        cycles = 0;
        while ((!(job1.isTerminated())) && (cycles < (RemoteSchedulerTest.MAX_WAIT_CYCLES))) {
            Thread.sleep(RemoteSchedulerTest.TICK_WAIT);
            cycles++;
        } 
        Assert.assertNotNull(job1.getDateFinished());
        Assert.assertTrue(job1.isTerminated());
        Assert.assertNull(job2.getDateFinished());
        Assert.assertTrue(job2.isTerminated());
        Assert.assertEquals("result2", job2.getReturn());
        intpA.close();
        schedulerSvc.removeScheduler("test");
    }
}

