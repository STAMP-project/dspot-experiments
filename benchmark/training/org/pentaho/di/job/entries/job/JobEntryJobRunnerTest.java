/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.job.entries.job;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;


public class JobEntryJobRunnerTest {
    private JobEntryJobRunner jobRunner;

    private Job mockJob;

    private JobMeta mockJobMeta;

    private Result mockResult;

    private LogChannelInterface mockLog;

    private Job parentJob;

    @Test
    public void testRun() throws Exception {
        // Call all the NO-OP paths
        Mockito.when(mockJob.isStopped()).thenReturn(true);
        jobRunner.run();
        Mockito.when(mockJob.isStopped()).thenReturn(false);
        Mockito.when(mockJob.getParentJob()).thenReturn(null);
        Mockito.when(mockJob.getJobMeta()).thenReturn(mockJobMeta);
        jobRunner.run();
        Mockito.when(parentJob.isStopped()).thenReturn(true);
        Mockito.when(mockJob.getParentJob()).thenReturn(parentJob);
        jobRunner.run();
        Mockito.when(parentJob.isStopped()).thenReturn(false);
        Mockito.when(mockJob.execute(Mockito.anyInt(), Mockito.any(Result.class))).thenReturn(mockResult);
        jobRunner.run();
    }

    @Test
    public void testRunSetsResult() throws Exception {
        Mockito.when(mockJob.isStopped()).thenReturn(false);
        Mockito.when(mockJob.getParentJob()).thenReturn(parentJob);
        Mockito.when(parentJob.isStopped()).thenReturn(false);
        Mockito.when(mockJob.execute(Mockito.anyInt(), Mockito.any(Result.class))).thenReturn(mockResult);
        jobRunner.run();
        Mockito.verify(mockJob, Mockito.times(1)).setResult(Mockito.any(Result.class));
    }

    @Test
    public void testRunWithExceptionOnExecuteSetsResult() throws Exception {
        Mockito.when(mockJob.isStopped()).thenReturn(false);
        Mockito.when(mockJob.getParentJob()).thenReturn(parentJob);
        Mockito.when(parentJob.isStopped()).thenReturn(false);
        Mockito.doThrow(KettleException.class).when(mockJob).execute(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Result.class));
        jobRunner.run();
        Mockito.verify(mockJob, Mockito.times(1)).setResult(Mockito.any(Result.class));
    }

    @Test
    public void testRunWithExceptionOnFireJobSetsResult() throws KettleException {
        Mockito.when(mockJob.isStopped()).thenReturn(false);
        Mockito.when(mockJob.getParentJob()).thenReturn(parentJob);
        Mockito.when(parentJob.isStopped()).thenReturn(false);
        Mockito.when(mockJob.execute(Mockito.anyInt(), Mockito.any(Result.class))).thenReturn(mockResult);
        Mockito.doThrow(Exception.class).when(mockJob).fireJobFinishListeners();
        jobRunner.run();
        Mockito.verify(mockJob, Mockito.times(1)).setResult(Mockito.any(Result.class));
        Assert.assertTrue(jobRunner.isFinished());
    }

    @Test
    public void testRunWithExceptionOnExecuteAndFireJobSetsResult() throws KettleException {
        Mockito.when(mockJob.isStopped()).thenReturn(false);
        Mockito.when(mockJob.getParentJob()).thenReturn(parentJob);
        Mockito.when(parentJob.isStopped()).thenReturn(false);
        Mockito.when(mockJob.execute(Mockito.anyInt(), Mockito.any(Result.class))).thenReturn(mockResult);
        Mockito.doThrow(KettleException.class).when(mockJob).execute(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Result.class));
        Mockito.doThrow(Exception.class).when(mockJob).fireJobFinishListeners();
        jobRunner.run();
        Mockito.verify(mockJob, Mockito.times(1)).setResult(Mockito.any(Result.class));
        Assert.assertTrue(jobRunner.isFinished());
    }

    @Test
    public void testRunWithException() throws Exception {
        Mockito.when(mockJob.isStopped()).thenReturn(false);
        Mockito.when(mockJob.getParentJob()).thenReturn(parentJob);
        Mockito.when(mockJob.getJobMeta()).thenReturn(mockJobMeta);
        Mockito.when(parentJob.isStopped()).thenReturn(false);
        Mockito.doThrow(KettleException.class).when(mockJob).execute(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Result.class));
        jobRunner.run();
        Mockito.verify(mockResult, Mockito.times(1)).setNrErrors(Mockito.anyInt());
        // [PDI-14981] catch more general exception to prevent thread hanging
        Mockito.doThrow(Exception.class).when(mockJob).fireJobFinishListeners();
        jobRunner.run();
    }

    @Test
    public void testGetSetResult() throws Exception {
        Assert.assertEquals(mockResult, jobRunner.getResult());
        jobRunner.setResult(null);
        Assert.assertNull(jobRunner.getResult());
    }

    @Test
    public void testGetSetLog() throws Exception {
        Assert.assertEquals(mockLog, jobRunner.getLog());
        jobRunner.setLog(null);
        Assert.assertNull(jobRunner.getLog());
    }

    @Test
    public void testGetSetJob() throws Exception {
        Assert.assertEquals(mockJob, jobRunner.getJob());
        jobRunner.setJob(null);
        Assert.assertNull(jobRunner.getJob());
    }

    @Test
    public void testGetSetEntryNr() throws Exception {
        Assert.assertEquals(0, jobRunner.getEntryNr());
        jobRunner.setEntryNr(1);
        Assert.assertEquals(1, jobRunner.getEntryNr());
    }

    @Test
    public void testIsFinished() throws Exception {
        Assert.assertFalse(jobRunner.isFinished());
        Mockito.when(mockJob.isStopped()).thenReturn(false);
        Mockito.when(mockJob.getParentJob()).thenReturn(parentJob);
        Mockito.when(parentJob.isStopped()).thenReturn(false);
        Mockito.when(mockJob.execute(Mockito.anyInt(), Mockito.any(Result.class))).thenReturn(mockResult);
        jobRunner.run();
        Assert.assertTrue(jobRunner.isFinished());
    }

    @Test
    public void testWaitUntilFinished() throws Exception {
        Mockito.when(mockJob.isStopped()).thenReturn(true);
        Mockito.when(mockJob.getParentJob()).thenReturn(parentJob);
        Mockito.when(parentJob.isStopped()).thenReturn(false);
        Mockito.when(mockJob.execute(Mockito.anyInt(), Mockito.any(Result.class))).thenReturn(mockResult);
        jobRunner.waitUntilFinished();
    }
}

