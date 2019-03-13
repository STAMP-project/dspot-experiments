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
package org.pentaho.di.job.entries.trans;


import LogLevel.NOTHING;
import ObjectLocationSpecificationMethod.FILENAME;
import ObjectLocationSpecificationMethod.REPOSITORY_BY_NAME;
import ObjectLocationSpecificationMethod.REPOSITORY_BY_REFERENCE;
import java.io.IOException;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.parameters.NamedParams;
import org.pentaho.di.core.parameters.NamedParamsDefault;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.resource.ResourceNamingInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.xml.sax.SAXException;


public class JobEntryTransTest {
    private final String JOB_ENTRY_TRANS_NAME = "JobEntryTransName";

    private final String JOB_ENTRY_FILE_NAME = "JobEntryFileName";

    private final String JOB_ENTRY_FILE_DIRECTORY = "JobEntryFileDirectory";

    private final String JOB_ENTRY_DESCRIPTION = "JobEntryDescription";

    /**
     * BACKLOG-179 - Exporting/Importing Jobs breaks Transformation specification when using "Specify by reference"
     *
     * Test checks that we choose different {@link ObjectLocationSpecificationMethod} when connection to
     * {@link Repository} and disconnected.
     *
     * <b>Important!</b> You must rewrite test when change import logic
     *
     * @throws KettleXMLException
     * 		
     * @throws IOException
     * 		
     * @throws SAXException
     * 		
     * @throws ParserConfigurationException
     * 		
     */
    @Test
    public void testChooseSpecMethodByRepositoryConnectionStatus() throws IOException, ParserConfigurationException, KettleXMLException, SAXException {
        Repository rep = Mockito.mock(Repository.class);
        Mockito.when(rep.isConnected()).thenReturn(true);
        // 000
        // not connected, no jobname, no method
        testJobEntry(null, false, null, FILENAME);
        // 001
        // not connected, no jobname, REPOSITORY_BY_REFERENCE method
        testJobEntry(null, false, REPOSITORY_BY_REFERENCE, REPOSITORY_BY_REFERENCE);
        // not connected, no jobname, REPOSITORY_BY_NAME method
        testJobEntry(null, false, REPOSITORY_BY_NAME, REPOSITORY_BY_NAME);
        // not connected, no jobname, FILENAME method
        testJobEntry(null, false, FILENAME, FILENAME);
        // 010
        // not connected, jobname, no method
        testJobEntry(null, true, null, FILENAME);
        // 011
        // not connected, jobname, REPOSITORY_BY_REFERENCE method
        testJobEntry(null, true, REPOSITORY_BY_REFERENCE, REPOSITORY_BY_REFERENCE);
        // not connected, jobname, REPOSITORY_BY_NAME method
        testJobEntry(null, true, REPOSITORY_BY_NAME, REPOSITORY_BY_NAME);
        // not connected, jobname, FILENAME method
        testJobEntry(null, true, FILENAME, FILENAME);
        // 100
        // connected, no jobname, no method
        testJobEntry(rep, false, null, FILENAME);
        // 101
        // connected, no jobname, REPOSITORY_BY_REFERENCE method
        testJobEntry(rep, false, REPOSITORY_BY_REFERENCE, REPOSITORY_BY_REFERENCE);
        // connected, no jobname, REPOSITORY_BY_NAME method
        testJobEntry(rep, false, REPOSITORY_BY_NAME, REPOSITORY_BY_NAME);
        // connected, no jobname, FILENAME method
        testJobEntry(rep, false, FILENAME, FILENAME);
        // 110
        // connected, jobname, no method
        testJobEntry(rep, true, null, REPOSITORY_BY_NAME);
        // 111
        // connected, jobname, REPOSITORY_BY_REFERENCE method
        testJobEntry(rep, true, REPOSITORY_BY_REFERENCE, REPOSITORY_BY_NAME);
        // connected, jobname, REPOSITORY_BY_NAME method
        testJobEntry(rep, true, REPOSITORY_BY_NAME, REPOSITORY_BY_NAME);
        // connected, jobname, FILENAME method
        testJobEntry(rep, true, FILENAME, REPOSITORY_BY_NAME);
    }

    @Test
    public void testExecute_result_false_get_transMeta_exception() throws KettleException {
        JobEntryTrans jobEntryTrans = Mockito.spy(new JobEntryTrans(JOB_ENTRY_TRANS_NAME));
        jobEntryTrans.setSpecificationMethod(FILENAME);
        jobEntryTrans.setParentJob(Mockito.mock(Job.class));
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        jobEntryTrans.setParentJobMeta(mockJobMeta);
        jobEntryTrans.setLogLevel(NOTHING);
        Mockito.doThrow(new KettleException("Error while loading transformation")).when(jobEntryTrans).getTransMeta(ArgumentMatchers.any(Repository.class), ArgumentMatchers.any(IMetaStore.class), ArgumentMatchers.any(VariableSpace.class));
        Result result = Mockito.mock(Result.class);
        jobEntryTrans.execute(result, 1);
        Mockito.verify(result).setResult(false);
    }

    @Test
    public void testCurrDirListener() throws Exception {
        JobMeta meta = Mockito.mock(JobMeta.class);
        JobEntryTrans jet = getJobEntryTrans();
        jet.setParentJobMeta(meta);
        jet.setParentJobMeta(null);
        Mockito.verify(meta, Mockito.times(1)).addCurrentDirectoryChangedListener(ArgumentMatchers.any());
        Mockito.verify(meta, Mockito.times(1)).removeCurrentDirectoryChangedListener(ArgumentMatchers.any());
    }

    @Test
    public void testExportResources() throws KettleException {
        JobEntryTrans jobEntryTrans = Mockito.spy(getJobEntryTrans());
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        String testName = "test";
        Mockito.doReturn(transMeta).when(jobEntryTrans).getTransMeta(ArgumentMatchers.any(Repository.class), ArgumentMatchers.any(VariableSpace.class));
        Mockito.when(transMeta.exportResources(ArgumentMatchers.any(TransMeta.class), ArgumentMatchers.any(Map.class), ArgumentMatchers.any(ResourceNamingInterface.class), ArgumentMatchers.any(Repository.class), ArgumentMatchers.any(IMetaStore.class))).thenReturn(testName);
        jobEntryTrans.exportResources(null, null, null, null, null);
        Mockito.verify(transMeta).setFilename(((("${" + (Const.INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY)) + "}/") + testName));
        Mockito.verify(jobEntryTrans).setSpecificationMethod(FILENAME);
    }

    @Test
    public void testPrepareFieldNamesParameters() throws UnknownParamException {
        // array of params
        String[] parameterNames = new String[2];
        parameterNames[0] = "param1";
        parameterNames[1] = "param2";
        // array of fieldNames params
        String[] parameterFieldNames = new String[1];
        parameterFieldNames[0] = "StreamParam1";
        // array of parameterValues params
        String[] parameterValues = new String[2];
        parameterValues[1] = "ValueParam2";
        JobEntryTrans jet = new JobEntryTrans();
        VariableSpace variableSpace = new Variables();
        jet.copyVariablesFrom(variableSpace);
        // at this point StreamColumnNameParams are already inserted in namedParams
        NamedParams namedParam = Mockito.mock(NamedParamsDefault.class);
        Mockito.doReturn("value1").when(namedParam).getParameterValue("param1");
        Mockito.doReturn("value2").when(namedParam).getParameterValue("param2");
        jet.prepareFieldNamesParameters(parameterNames, parameterFieldNames, parameterValues, namedParam, jet);
        Assert.assertEquals("value1", jet.getVariable("param1"));
        Assert.assertEquals(null, jet.getVariable("param2"));
    }

    @Test
    public void testGetTransMeta() throws KettleException {
        String param1 = "param1";
        String param2 = "param2";
        String param3 = "param3";
        String parentValue1 = "parentValue1";
        String parentValue2 = "parentValue2";
        String childValue3 = "childValue3";
        JobEntryTrans jobEntryTrans = Mockito.spy(getJobEntryTrans());
        Repository rep = Mockito.mock(Repository.class);
        RepositoryDirectory repositoryDirectory = Mockito.mock(RepositoryDirectory.class);
        RepositoryDirectoryInterface repositoryDirectoryInterface = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.doReturn(repositoryDirectoryInterface).when(rep).loadRepositoryDirectoryTree();
        Mockito.doReturn(repositoryDirectory).when(repositoryDirectoryInterface).findDirectory("/home/admin");
        TransMeta meta = new TransMeta();
        meta.setVariable(param2, "childValue2 should be override");
        meta.setVariable(param3, childValue3);
        Mockito.doReturn(meta).when(rep).loadTransformation(Mockito.eq("test.ktr"), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyBoolean(), Mockito.anyObject());
        VariableSpace parentSpace = new Variables();
        parentSpace.setVariable(param1, parentValue1);
        parentSpace.setVariable(param2, parentValue2);
        jobEntryTrans.setFileName("/home/admin/test.ktr");
        Mockito.doNothing().when(jobEntryTrans).logBasic(Mockito.anyString());
        jobEntryTrans.setSpecificationMethod(FILENAME);
        TransMeta transMeta;
        jobEntryTrans.setPassingAllParameters(false);
        transMeta = jobEntryTrans.getTransMeta(rep, null, parentSpace);
        Assert.assertEquals(null, transMeta.getVariable(param1));
        Assert.assertEquals(parentValue2, transMeta.getVariable(param2));
        Assert.assertEquals(childValue3, transMeta.getVariable(param3));
        jobEntryTrans.setPassingAllParameters(true);
        transMeta = jobEntryTrans.getTransMeta(rep, null, parentSpace);
        Assert.assertEquals(parentValue1, transMeta.getVariable(param1));
        Assert.assertEquals(parentValue2, transMeta.getVariable(param2));
        Assert.assertEquals(childValue3, transMeta.getVariable(param3));
    }

    @Test
    public void testGetTransMetaRepo() throws KettleException {
        String param1 = "dir";
        String param2 = "file";
        String parentValue1 = "/home/admin";
        String parentValue2 = "test";
        JobEntryTrans jobEntryTrans = Mockito.spy(getJobEntryTrans());
        Repository rep = Mockito.mock(Repository.class);
        RepositoryDirectory repositoryDirectory = Mockito.mock(RepositoryDirectory.class);
        RepositoryDirectoryInterface repositoryDirectoryInterface = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.doReturn(repositoryDirectoryInterface).when(rep).loadRepositoryDirectoryTree();
        Mockito.doReturn(repositoryDirectory).when(repositoryDirectoryInterface).findDirectory(parentValue1);
        TransMeta meta = new TransMeta();
        meta.setVariable(param2, "childValue2 should be override");
        Mockito.doReturn(meta).when(rep).loadTransformation(Mockito.eq("test"), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyBoolean(), Mockito.anyObject());
        VariableSpace parentSpace = new Variables();
        parentSpace.setVariable(param1, parentValue1);
        parentSpace.setVariable(param2, parentValue2);
        jobEntryTrans.setDirectory("${dir}");
        jobEntryTrans.setTransname("${file}");
        Mockito.doNothing().when(jobEntryTrans).logBasic(Mockito.anyString());
        jobEntryTrans.setSpecificationMethod(REPOSITORY_BY_NAME);
        TransMeta transMeta;
        jobEntryTrans.setPassingAllParameters(false);
        transMeta = jobEntryTrans.getTransMeta(rep, null, parentSpace);
        Assert.assertEquals(null, transMeta.getVariable(param1));
        Assert.assertEquals(parentValue2, transMeta.getVariable(param2));
        jobEntryTrans.setPassingAllParameters(true);
        transMeta = jobEntryTrans.getTransMeta(rep, null, parentSpace);
        Assert.assertEquals(parentValue1, transMeta.getVariable(param1));
        Assert.assertEquals(parentValue2, transMeta.getVariable(param2));
    }
}

