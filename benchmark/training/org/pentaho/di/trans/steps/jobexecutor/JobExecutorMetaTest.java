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
package org.pentaho.di.trans.steps.jobexecutor;


import ObjectLocationSpecificationMethod.FILENAME;
import java.util.Map;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.resource.ResourceNamingInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.metastore.api.IMetaStore;


/**
 * <p>
 * PDI-11979 - Fieldnames in the "Execution results" tab of the Job executor step saved incorrectly in repository.
 * </p>
 */
public class JobExecutorMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    LoadSaveTester loadSaveTester;

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void testRemoveHopFrom() throws Exception {
        JobExecutorMeta jobExecutorMeta = new JobExecutorMeta();
        jobExecutorMeta.setExecutionResultTargetStepMeta(new StepMeta());
        jobExecutorMeta.setResultRowsTargetStepMeta(new StepMeta());
        jobExecutorMeta.setResultFilesTargetStepMeta(new StepMeta());
        jobExecutorMeta.cleanAfterHopFromRemove();
        Assert.assertNull(jobExecutorMeta.getExecutionResultTargetStepMeta());
        Assert.assertNull(jobExecutorMeta.getResultRowsTargetStepMeta());
        Assert.assertNull(jobExecutorMeta.getResultFilesTargetStepMeta());
    }

    @Test
    public void testExportResources() throws KettleException {
        JobExecutorMeta jobExecutorMeta = Mockito.spy(new JobExecutorMeta());
        JobMeta jobMeta = Mockito.mock(JobMeta.class);
        String testName = "test";
        Mockito.doReturn(jobMeta).when(jobExecutorMeta).loadJobMetaProxy(ArgumentMatchers.any(JobExecutorMeta.class), ArgumentMatchers.any(Repository.class), ArgumentMatchers.any(VariableSpace.class));
        Mockito.when(jobMeta.exportResources(ArgumentMatchers.any(JobMeta.class), ArgumentMatchers.any(Map.class), ArgumentMatchers.any(ResourceNamingInterface.class), ArgumentMatchers.any(Repository.class), ArgumentMatchers.any(IMetaStore.class))).thenReturn(testName);
        jobExecutorMeta.exportResources(null, null, null, null, null);
        Mockito.verify(jobMeta).setFilename(((("${" + (Const.INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY)) + "}/") + testName));
        Mockito.verify(jobExecutorMeta).setSpecificationMethod(FILENAME);
    }

    @Test
    public void testLoadJobMeta() throws KettleException {
        String param1 = "param1";
        String param2 = "param2";
        String param3 = "param3";
        String parentValue1 = "parentValue1";
        String parentValue2 = "parentValue2";
        String childValue3 = "childValue3";
        JobExecutorMeta jobExecutorMeta = Mockito.spy(new JobExecutorMeta());
        Repository repository = Mockito.mock(Repository.class);
        JobMeta meta = new JobMeta();
        meta.setVariable(param2, "childValue2 should be override");
        meta.setVariable(param3, childValue3);
        Mockito.doReturn(meta).when(repository).loadJob(Mockito.eq("test.kjb"), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());
        VariableSpace parentSpace = new Variables();
        parentSpace.setVariable(param1, parentValue1);
        parentSpace.setVariable(param2, parentValue2);
        jobExecutorMeta.setSpecificationMethod(FILENAME);
        jobExecutorMeta.setFileName("/home/admin/test.kjb");
        JobMeta jobMeta;
        jobExecutorMeta.getParameters().setInheritingAllVariables(false);
        jobMeta = JobExecutorMeta.loadJobMeta(jobExecutorMeta, repository, parentSpace);
        Assert.assertEquals(null, jobMeta.getVariable(param1));
        Assert.assertEquals(parentValue2, jobMeta.getVariable(param2));
        Assert.assertEquals(childValue3, jobMeta.getVariable(param3));
        jobExecutorMeta.getParameters().setInheritingAllVariables(true);
        jobMeta = JobExecutorMeta.loadJobMeta(jobExecutorMeta, repository, parentSpace);
        Assert.assertEquals(parentValue1, jobMeta.getVariable(param1));
        Assert.assertEquals(parentValue2, jobMeta.getVariable(param2));
        Assert.assertEquals(childValue3, jobMeta.getVariable(param3));
    }
}

