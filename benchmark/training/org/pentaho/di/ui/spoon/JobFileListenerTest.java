/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.ui.spoon;


import ObjectLocationSpecificationMethod.FILENAME;
import ObjectLocationSpecificationMethod.REPOSITORY_BY_NAME;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.job.JobEntryJob;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.job.entry.JobEntryCopy;


public class JobFileListenerTest {
    JobFileListener jobFileListener;

    @Test
    public void testAccepts() throws Exception {
        Assert.assertFalse(jobFileListener.accepts(null));
        Assert.assertFalse(jobFileListener.accepts("NoDot"));
        Assert.assertTrue(jobFileListener.accepts("Job.kjb"));
        Assert.assertTrue(jobFileListener.accepts(".kjb"));
    }

    @Test
    public void testAcceptsXml() throws Exception {
        Assert.assertFalse(jobFileListener.acceptsXml(null));
        Assert.assertFalse(jobFileListener.acceptsXml(""));
        Assert.assertFalse(jobFileListener.acceptsXml("Job"));
        Assert.assertTrue(jobFileListener.acceptsXml("job"));
    }

    @Test
    public void testGetFileTypeDisplayNames() throws Exception {
        String[] names = jobFileListener.getFileTypeDisplayNames(null);
        Assert.assertNotNull(names);
        Assert.assertEquals(2, names.length);
        Assert.assertEquals("Jobs", names[0]);
        Assert.assertEquals("XML", names[1]);
    }

    @Test
    public void testGetRootNodeName() throws Exception {
        Assert.assertEquals("job", jobFileListener.getRootNodeName());
    }

    @Test
    public void testGetSupportedExtensions() throws Exception {
        String[] extensions = jobFileListener.getSupportedExtensions();
        Assert.assertNotNull(extensions);
        Assert.assertEquals(2, extensions.length);
        Assert.assertEquals("kjb", extensions[0]);
        Assert.assertEquals("xml", extensions[1]);
    }

    @Test
    public void testProcessLinkedTransWithFilename() {
        JobEntryTrans jobTransExecutor = Mockito.spy(new JobEntryTrans());
        jobTransExecutor.setFileName("/path/to/Transformation2.ktr");
        jobTransExecutor.setSpecificationMethod(FILENAME);
        JobEntryCopy jobEntry = Mockito.mock(JobEntryCopy.class);
        Mockito.when(jobEntry.getEntry()).thenReturn(jobTransExecutor);
        JobMeta parent = Mockito.mock(JobMeta.class);
        Mockito.when(parent.nrJobEntries()).thenReturn(1);
        Mockito.when(parent.getJobEntry(0)).thenReturn(jobEntry);
        JobMeta result = jobFileListener.processLinkedTrans(parent);
        JobEntryCopy meta = result.getJobEntry(0);
        Assert.assertNotNull(meta);
        JobEntryTrans resultExecMeta = ((JobEntryTrans) (meta.getEntry()));
        Assert.assertEquals(REPOSITORY_BY_NAME, resultExecMeta.getSpecificationMethod());
        Assert.assertEquals("/path/to", resultExecMeta.getDirectory());
        Assert.assertEquals("Transformation2", resultExecMeta.getTransname());
    }

    @Test
    public void testProcessLinkedTransWithNoFilenameMethodFileName() {
        testProcessLinkedTransWithNoFilename(FILENAME);
    }

    @Test
    public void testProcessLinkedTransWithNoFilenameMethodRepoByName() {
        testProcessLinkedTransWithNoFilename(REPOSITORY_BY_NAME);
    }

    @Test
    public void testProcessLinkedJobsWithFilename() {
        JobEntryJob jobJobExecutor = Mockito.spy(new JobEntryJob());
        jobJobExecutor.setFileName("/path/to/Job1.kjb");
        jobJobExecutor.setSpecificationMethod(FILENAME);
        JobEntryCopy jobEntry = Mockito.mock(JobEntryCopy.class);
        Mockito.when(jobEntry.getEntry()).thenReturn(jobJobExecutor);
        JobMeta parent = Mockito.mock(JobMeta.class);
        Mockito.when(parent.nrJobEntries()).thenReturn(1);
        Mockito.when(parent.getJobEntry(0)).thenReturn(jobEntry);
        JobMeta result = jobFileListener.processLinkedJobs(parent);
        JobEntryCopy meta = result.getJobEntry(0);
        Assert.assertNotNull(meta);
        JobEntryJob resultExecMeta = ((JobEntryJob) (meta.getEntry()));
        Assert.assertEquals(REPOSITORY_BY_NAME, resultExecMeta.getSpecificationMethod());
        Assert.assertEquals(resultExecMeta.getDirectory(), "/path/to");
        Assert.assertEquals(resultExecMeta.getJobName(), "Job1");
    }

    @Test
    public void testProcessLinkedJobsWithNoFilenameMethodFilename() {
        testProcessLinkedJobsWithNoFilename(FILENAME);
    }

    @Test
    public void testProcessLinkedJobsWithNoFilenameMethodRepoByName() {
        testProcessLinkedJobsWithNoFilename(REPOSITORY_BY_NAME);
    }
}

