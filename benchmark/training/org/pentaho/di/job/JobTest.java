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
package org.pentaho.di.job;


import Const.INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY;
import Const.INTERNAL_VARIABLE_JOB_FILENAME_DIRECTORY;
import Const.INTERNAL_VARIABLE_JOB_REPOSITORY_DIRECTORY;
import LogStatus.END;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.logging.JobEntryLogTable;
import org.pentaho.di.core.logging.JobLogTable;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.HasDatabasesInterface;


public class JobTest {
    private static final String STRING_DEFAULT = "<def>";

    private Job mockedJob;

    private Database mockedDataBase;

    private VariableSpace mockedVariableSpace;

    private HasDatabasesInterface hasDatabasesInterface;

    private JobMeta mockedJobMeta;

    private JobEntryCopy mockedJobEntryCopy;

    private JobEntrySpecial mockedJobEntrySpecial;

    private LogChannel mockedLogChannel;

    @Test
    public void recordsCleanUpMethodIsCalled_JobEntryLogTable() throws Exception {
        JobEntryLogTable jobEntryLogTable = JobEntryLogTable.getDefault(mockedVariableSpace, hasDatabasesInterface);
        setAllTableParamsDefault(jobEntryLogTable);
        JobMeta jobMeta = new JobMeta();
        jobMeta.setJobEntryLogTable(jobEntryLogTable);
        Mockito.when(mockedJob.getJobMeta()).thenReturn(jobMeta);
        Mockito.doCallRealMethod().when(mockedJob).writeJobEntryLogInformation();
        mockedJob.writeJobEntryLogInformation();
        Mockito.verify(mockedDataBase).cleanupLogRecords(jobEntryLogTable);
    }

    @Test
    public void recordsCleanUpMethodIsCalled_JobLogTable() throws Exception {
        JobLogTable jobLogTable = JobLogTable.getDefault(mockedVariableSpace, hasDatabasesInterface);
        setAllTableParamsDefault(jobLogTable);
        Mockito.doCallRealMethod().when(mockedJob).writeLogTableInformation(jobLogTable, END);
        mockedJob.writeLogTableInformation(jobLogTable, END);
        Mockito.verify(mockedDataBase).cleanupLogRecords(jobLogTable);
    }

    @Test
    public void testNewJobWithContainerObjectId() {
        Repository repository = Mockito.mock(Repository.class);
        JobMeta meta = Mockito.mock(JobMeta.class);
        String carteId = UUID.randomUUID().toString();
        Mockito.doReturn(carteId).when(meta).getContainerObjectId();
        Job job = new Job(repository, meta);
        Assert.assertEquals(carteId, job.getContainerObjectId());
    }

    /**
     * This test demonstrates the issue fixed in PDI-17398.
     * When a job is scheduled twice, it gets the same log channel Id and both logs get merged
     */
    @Test
    public void testTwoJobsGetSameLogChannelId() {
        Repository repository = Mockito.mock(Repository.class);
        JobMeta meta = Mockito.mock(JobMeta.class);
        Job job1 = new Job(repository, meta);
        Job job2 = new Job(repository, meta);
        Assert.assertEquals(job1.getLogChannelId(), job2.getLogChannelId());
    }

    /**
     * This test demonstrates the fix for PDI-17398.
     * Two schedules -> two Carte object Ids -> two log channel Ids
     */
    @Test
    public void testTwoJobsGetDifferentLogChannelIdWithDifferentCarteId() {
        Repository repository = Mockito.mock(Repository.class);
        JobMeta meta1 = Mockito.mock(JobMeta.class);
        JobMeta meta2 = Mockito.mock(JobMeta.class);
        String carteId1 = UUID.randomUUID().toString();
        String carteId2 = UUID.randomUUID().toString();
        Mockito.doReturn(carteId1).when(meta1).getContainerObjectId();
        Mockito.doReturn(carteId2).when(meta2).getContainerObjectId();
        Job job1 = new Job(repository, meta1);
        Job job2 = new Job(repository, meta2);
        Assert.assertNotEquals(job1.getContainerObjectId(), job2.getContainerObjectId());
        Assert.assertNotEquals(job1.getLogChannelId(), job2.getLogChannelId());
    }

    @Test
    public void testSetInternalEntryCurrentDirectoryWithFilename() {
        Job jobTest = new Job();
        boolean hasFilename = true;
        boolean hasRepoDir = false;
        jobTest.copyVariablesFrom(null);
        jobTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        jobTest.setVariable(INTERNAL_VARIABLE_JOB_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        jobTest.setVariable(INTERNAL_VARIABLE_JOB_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        jobTest.setInternalEntryCurrentDirectory(hasFilename, hasRepoDir);
        Assert.assertEquals("file:///C:/SomeFilenameDirectory", jobTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }

    @Test
    public void testSetInternalEntryCurrentDirectoryWithRepository() {
        Job jobTest = new Job();
        boolean hasFilename = false;
        boolean hasRepoDir = true;
        jobTest.copyVariablesFrom(null);
        jobTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        jobTest.setVariable(INTERNAL_VARIABLE_JOB_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        jobTest.setVariable(INTERNAL_VARIABLE_JOB_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        jobTest.setInternalEntryCurrentDirectory(hasFilename, hasRepoDir);
        Assert.assertEquals("/SomeRepDirectory", jobTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }

    @Test
    public void testSetInternalEntryCurrentDirectoryWithoutFilenameOrRepository() {
        Job jobTest = new Job();
        jobTest.copyVariablesFrom(null);
        boolean hasFilename = false;
        boolean hasRepoDir = false;
        jobTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        jobTest.setVariable(INTERNAL_VARIABLE_JOB_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        jobTest.setVariable(INTERNAL_VARIABLE_JOB_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        jobTest.setInternalEntryCurrentDirectory(hasFilename, hasRepoDir);
        Assert.assertEquals("Original value defined at run execution", jobTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }

    /**
     * Tests the execution of Job With Previous Results (Called by JobExecutor - Job Calling another Job)
     * The called Job is not set to Repeat
     */
    @Test
    public void executeWithPreviousResultsNoRepeatTest() {
        executeWithPreviousResultsTest(false);
    }

    /**
     * Tests the execution of Job With Previous Results (Called by JobExecutor - Job Calling another Job)
     * The called Job is set to Repeat
     */
    @Test
    public void executeWithPreviousResultsWithRepeatTest() {
        executeWithPreviousResultsTest(true);
    }
}

