/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.syslog;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class SyslogMessageConcurrentTest {
    AtomicInteger numOfErrors = null;

    CountDownLatch countDownLatch = null;

    private String testMessage = "message value";

    int numOfTasks = 5;

    private StepMockHelper<SyslogMessageMeta, SyslogMessageData> stepMockHelper;

    @Test
    public void concurrentSyslogMessageTest() throws Exception {
        SyslogMessageConcurrentTest.SyslogMessageTask syslogMessage = null;
        ExecutorService service = Executors.newFixedThreadPool(numOfTasks);
        for (int i = 0; i < (numOfTasks); i++) {
            syslogMessage = createSyslogMessageTask();
            service.execute(syslogMessage);
        }
        service.shutdown();
        countDownLatch.countDown();
        service.awaitTermination(10000, TimeUnit.NANOSECONDS);
        Assert.assertTrue(((numOfErrors.get()) == 0));
    }

    private class SyslogMessageTask extends SyslogMessage implements Runnable {
        SyslogMessageMeta syslogMessageMeta = null;

        public SyslogMessageTask(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans, SyslogMessageMeta processRowsStepMetaInterface) {
            super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
            syslogMessageMeta = processRowsStepMetaInterface;
        }

        @Override
        public void run() {
            try {
                countDownLatch.await();
                processRow(syslogMessageMeta, getStepDataInterface());
            } catch (Exception e) {
                e.printStackTrace();
                numOfErrors.getAndIncrement();
            } finally {
                try {
                    dispose(syslogMessageMeta, getStepDataInterface());
                } catch (Exception e) {
                    e.printStackTrace();
                    numOfErrors.getAndIncrement();
                }
            }
        }

        @Override
        public void putRow(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
            Assert.assertNotNull(row);
            Assert.assertTrue(((row.length) == 1));
            Assert.assertEquals(testMessage, row[0]);
        }

        @Override
        public Object[] getRow() throws KettleException {
            return new Object[]{ testMessage };
        }
    }
}

