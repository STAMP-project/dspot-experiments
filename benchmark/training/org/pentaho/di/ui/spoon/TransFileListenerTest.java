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
package org.pentaho.di.ui.spoon;


import ObjectLocationSpecificationMethod.FILENAME;
import ObjectLocationSpecificationMethod.REPOSITORY_BY_NAME;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.jobexecutor.JobExecutorMeta;
import org.pentaho.di.trans.steps.transexecutor.TransExecutorMeta;


public class TransFileListenerTest {
    TransFileListener transFileListener;

    @Test
    public void testAccepts() throws Exception {
        Assert.assertFalse(transFileListener.accepts(null));
        Assert.assertFalse(transFileListener.accepts("NoDot"));
        Assert.assertTrue(transFileListener.accepts("Trans.ktr"));
        Assert.assertTrue(transFileListener.accepts(".ktr"));
    }

    @Test
    public void testAcceptsXml() throws Exception {
        Assert.assertFalse(transFileListener.acceptsXml(null));
        Assert.assertFalse(transFileListener.acceptsXml(""));
        Assert.assertFalse(transFileListener.acceptsXml("Transformation"));
        Assert.assertTrue(transFileListener.acceptsXml("transformation"));
    }

    @Test
    public void testGetFileTypeDisplayNames() throws Exception {
        String[] names = transFileListener.getFileTypeDisplayNames(null);
        Assert.assertNotNull(names);
        Assert.assertEquals(2, names.length);
        Assert.assertEquals("Transformations", names[0]);
        Assert.assertEquals("XML", names[1]);
    }

    @Test
    public void testGetRootNodeName() throws Exception {
        Assert.assertEquals("transformation", transFileListener.getRootNodeName());
    }

    @Test
    public void testGetSupportedExtensions() throws Exception {
        String[] extensions = transFileListener.getSupportedExtensions();
        Assert.assertNotNull(extensions);
        Assert.assertEquals(2, extensions.length);
        Assert.assertEquals("ktr", extensions[0]);
        Assert.assertEquals("xml", extensions[1]);
    }

    @Test
    public void testProcessLinkedTransWithFilename() {
        TransExecutorMeta transExecutorMeta = Mockito.spy(new TransExecutorMeta());
        transExecutorMeta.setFileName("/path/to/Transformation2.ktr");
        transExecutorMeta.setSpecificationMethod(FILENAME);
        StepMeta transExecutorStep = Mockito.mock(StepMeta.class);
        Mockito.when(transExecutorStep.getStepID()).thenReturn("TransExecutor");
        Mockito.when(transExecutorStep.getStepMetaInterface()).thenReturn(transExecutorMeta);
        TransMeta parent = Mockito.mock(TransMeta.class);
        Mockito.when(parent.getSteps()).thenReturn(Arrays.asList(transExecutorStep));
        TransMeta result = transFileListener.processLinkedTrans(parent);
        boolean found = false;
        for (StepMeta stepMeta : result.getSteps()) {
            if (stepMeta.getStepID().equalsIgnoreCase("TransExecutor")) {
                found = true;
                TransExecutorMeta resultExecMeta = ((TransExecutorMeta) (stepMeta.getStepMetaInterface()));
                Assert.assertEquals(REPOSITORY_BY_NAME, resultExecMeta.getSpecificationMethod());
                Assert.assertEquals(resultExecMeta.getDirectoryPath(), "/path/to");
                Assert.assertEquals(resultExecMeta.getTransName(), "Transformation2");
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testProcessLinkedTransWithNoFilename() {
        TransExecutorMeta transExecutorMeta = Mockito.spy(new TransExecutorMeta());
        transExecutorMeta.setFileName(null);
        transExecutorMeta.setDirectoryPath("/path/to");
        transExecutorMeta.setTransName("Transformation2");
        transExecutorMeta.setSpecificationMethod(REPOSITORY_BY_NAME);
        StepMeta transExecutorStep = Mockito.mock(StepMeta.class);
        Mockito.when(transExecutorStep.getStepID()).thenReturn("TransExecutor");
        Mockito.when(transExecutorStep.getStepMetaInterface()).thenReturn(transExecutorMeta);
        TransMeta parent = Mockito.mock(TransMeta.class);
        Mockito.when(parent.getSteps()).thenReturn(Arrays.asList(transExecutorStep));
        TransMeta result = transFileListener.processLinkedTrans(parent);
        boolean found = false;
        for (StepMeta stepMeta : result.getSteps()) {
            if (stepMeta.getStepID().equalsIgnoreCase("TransExecutor")) {
                found = true;
                TransExecutorMeta resultExecMeta = ((TransExecutorMeta) (stepMeta.getStepMetaInterface()));
                Assert.assertEquals(REPOSITORY_BY_NAME, resultExecMeta.getSpecificationMethod());
                Assert.assertEquals(resultExecMeta.getDirectoryPath(), "/path/to");
                Assert.assertEquals(resultExecMeta.getTransName(), "Transformation2");
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testProcessLinkedJobsWithFilename() {
        JobExecutorMeta jobExecutorMeta = Mockito.spy(new JobExecutorMeta());
        jobExecutorMeta.setFileName("/path/to/Job1.kjb");
        jobExecutorMeta.setSpecificationMethod(FILENAME);
        StepMeta jobExecutorStep = Mockito.mock(StepMeta.class);
        Mockito.when(jobExecutorStep.getStepID()).thenReturn("JobExecutor");
        Mockito.when(jobExecutorStep.getStepMetaInterface()).thenReturn(jobExecutorMeta);
        TransMeta parent = Mockito.mock(TransMeta.class);
        Mockito.when(parent.getSteps()).thenReturn(Arrays.asList(jobExecutorStep));
        TransMeta result = transFileListener.processLinkedJobs(parent);
        boolean found = false;
        for (StepMeta stepMeta : result.getSteps()) {
            if (stepMeta.getStepID().equalsIgnoreCase("JobExecutor")) {
                found = true;
                JobExecutorMeta resultExecMeta = ((JobExecutorMeta) (stepMeta.getStepMetaInterface()));
                Assert.assertEquals(REPOSITORY_BY_NAME, resultExecMeta.getSpecificationMethod());
                Assert.assertEquals(resultExecMeta.getDirectoryPath(), "/path/to");
                Assert.assertEquals(resultExecMeta.getJobName(), "Job1");
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testProcessLinkedJobsWithNoFilename() {
        JobExecutorMeta jobExecutorMeta = Mockito.spy(new JobExecutorMeta());
        jobExecutorMeta.setFileName(null);
        jobExecutorMeta.setDirectoryPath("/path/to");
        jobExecutorMeta.setJobName("Job1");
        jobExecutorMeta.setSpecificationMethod(REPOSITORY_BY_NAME);
        StepMeta transExecutorStep = Mockito.mock(StepMeta.class);
        Mockito.when(transExecutorStep.getStepID()).thenReturn("JobExecutor");
        Mockito.when(transExecutorStep.getStepMetaInterface()).thenReturn(jobExecutorMeta);
        TransMeta parent = Mockito.mock(TransMeta.class);
        Mockito.when(parent.getSteps()).thenReturn(Arrays.asList(transExecutorStep));
        TransMeta result = transFileListener.processLinkedJobs(parent);
        boolean found = false;
        for (StepMeta stepMeta : result.getSteps()) {
            if (stepMeta.getStepID().equalsIgnoreCase("JobExecutor")) {
                found = true;
                JobExecutorMeta resultExecMeta = ((JobExecutorMeta) (stepMeta.getStepMetaInterface()));
                Assert.assertEquals(REPOSITORY_BY_NAME, resultExecMeta.getSpecificationMethod());
                Assert.assertEquals(resultExecMeta.getDirectoryPath(), "/path/to");
                Assert.assertEquals(resultExecMeta.getJobName(), "Job1");
            }
        }
        Assert.assertTrue(found);
    }
}

