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
package org.pentaho.di.job;


import JobExecutionConfiguration.XML_TAG;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.plugins.ClassLoadingPluginInterface;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class JobExecutionConfigurationTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testConnectRepository() throws KettleException {
        JobExecutionConfiguration jobExecutionConfiguration = new JobExecutionConfiguration();
        final RepositoriesMeta repositoriesMeta = Mockito.mock(RepositoriesMeta.class);
        final RepositoryMeta repositoryMeta = Mockito.mock(RepositoryMeta.class);
        final Repository repository = Mockito.mock(Repository.class);
        final String mockRepo = "mockRepo";
        final boolean[] connectionSuccess = new boolean[]{ false };
        Repository initialRepo = Mockito.mock(Repository.class);
        jobExecutionConfiguration.setRepository(initialRepo);
        KettleLogStore.init();
        // Create mock repository plugin
        JobExecutionConfigurationTest.MockRepositoryPlugin mockRepositoryPlugin = Mockito.mock(JobExecutionConfigurationTest.MockRepositoryPlugin.class);
        Mockito.when(getIds()).thenReturn(new String[]{ "mockRepo" });
        Mockito.when(matches("mockRepo")).thenReturn(true);
        Mockito.when(getName()).thenReturn("mock-repository");
        Mockito.when(getClassMap()).thenAnswer(new Answer<Map<Class<?>, String>>() {
            @Override
            public Map<Class<?>, String> answer(InvocationOnMock invocation) throws Throwable {
                Map<Class<?>, String> dbMap = new HashMap<Class<?>, String>();
                dbMap.put(Repository.class, getName());
                return dbMap;
            }
        });
        PluginRegistry.getInstance().registerPlugin(RepositoryPluginType.class, mockRepositoryPlugin);
        // Define valid connection criteria
        Mockito.when(repositoriesMeta.findRepository(ArgumentMatchers.anyString())).thenAnswer(new Answer<RepositoryMeta>() {
            @Override
            public RepositoryMeta answer(InvocationOnMock invocation) throws Throwable {
                return mockRepo.equals(invocation.getArguments()[0]) ? repositoryMeta : null;
            }
        });
        Mockito.when(loadClass(Repository.class)).thenReturn(repository);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if (("username".equals(invocation.getArguments()[0])) && ("password".equals(invocation.getArguments()[1]))) {
                    connectionSuccess[0] = true;
                } else {
                    connectionSuccess[0] = false;
                    throw new KettleException("Mock Repository connection failed");
                }
                return null;
            }
        }).when(repository).connect(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        // Ignore repository not found in RepositoriesMeta
        jobExecutionConfiguration.connectRepository(repositoriesMeta, "notFound", "username", "password");
        Assert.assertEquals("Repository Changed", initialRepo, jobExecutionConfiguration.getRepository());
        // Ignore failed attempt to connect
        jobExecutionConfiguration.connectRepository(repositoriesMeta, mockRepo, "username", "");
        Assert.assertEquals("Repository Changed", initialRepo, jobExecutionConfiguration.getRepository());
        // Save repository if connection passes
        jobExecutionConfiguration.connectRepository(repositoriesMeta, mockRepo, "username", "password");
        Assert.assertEquals("Repository didn't change", repository, jobExecutionConfiguration.getRepository());
        Assert.assertTrue("Repository not connected", connectionSuccess[0]);
    }

    public interface MockRepositoryPlugin extends ClassLoadingPluginInterface , PluginInterface {}

    @Test
    public void testDefaultPassedBatchId() {
        JobExecutionConfiguration jec = new JobExecutionConfiguration();
        Assert.assertEquals("default passedBatchId value must be null", null, jec.getPassedBatchId());
    }

    @Test
    public void testCopy() {
        JobExecutionConfiguration jec = new JobExecutionConfiguration();
        final Long passedBatchId0 = null;
        final long passedBatchId1 = 0L;
        final long passedBatchId2 = 5L;
        jec.setPassedBatchId(passedBatchId0);
        // CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
        {
            JobExecutionConfiguration jecCopy = ((JobExecutionConfiguration) (jec.clone()));
            Assert.assertEquals("clone-copy", jec.getPassedBatchId(), jecCopy.getPassedBatchId());
        }
        jec.setPassedBatchId(passedBatchId1);
        // CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
        {
            JobExecutionConfiguration jecCopy = ((JobExecutionConfiguration) (jec.clone()));
            Assert.assertEquals("clone-copy", jec.getPassedBatchId(), jecCopy.getPassedBatchId());
        }
        jec.setPassedBatchId(passedBatchId2);
        // CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
        {
            JobExecutionConfiguration jecCopy = ((JobExecutionConfiguration) (jec.clone()));
            Assert.assertEquals("clone-copy", jec.getPassedBatchId(), jecCopy.getPassedBatchId());
        }
    }

    @Test
    public void testCopyXml() throws Exception {
        JobExecutionConfiguration jec = new JobExecutionConfiguration();
        final Long passedBatchId0 = null;
        final long passedBatchId1 = 0L;
        final long passedBatchId2 = 5L;
        jec.setPassedBatchId(passedBatchId0);
        // CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
        {
            String xml = jec.getXML();
            Document doc = XMLHandler.loadXMLString(xml);
            Node node = XMLHandler.getSubNode(doc, XML_TAG);
            JobExecutionConfiguration jecCopy = new JobExecutionConfiguration(node);
            Assert.assertEquals("xml-copy", jec.getPassedBatchId(), jecCopy.getPassedBatchId());
        }
        jec.setPassedBatchId(passedBatchId1);
        // CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
        {
            String xml = jec.getXML();
            Document doc = XMLHandler.loadXMLString(xml);
            Node node = XMLHandler.getSubNode(doc, XML_TAG);
            JobExecutionConfiguration jecCopy = new JobExecutionConfiguration(node);
            Assert.assertEquals("xml-copy", jec.getPassedBatchId(), jecCopy.getPassedBatchId());
        }
        jec.setPassedBatchId(passedBatchId2);
        // CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
        {
            String xml = jec.getXML();
            Document doc = XMLHandler.loadXMLString(xml);
            Node node = XMLHandler.getSubNode(doc, XML_TAG);
            JobExecutionConfiguration jecCopy = new JobExecutionConfiguration(node);
            Assert.assertEquals("xml-copy", jec.getPassedBatchId(), jecCopy.getPassedBatchId());
        }
    }

    @Test
    public void testGetUsedArguments() throws KettleException {
        JobExecutionConfiguration executionConfiguration = new JobExecutionConfiguration();
        JobMeta jobMeta = new JobMeta();
        jobMeta.jobcopies = new ArrayList();
        String[] commandLineArguments = new String[0];
        IMetaStore metaStore = Mockito.mock(IMetaStore.class);
        JobEntryCopy jobEntryCopy0 = new JobEntryCopy();
        TransMeta transMeta0 = Mockito.mock(TransMeta.class);
        Map<String, String> map0 = new HashMap<>();
        map0.put("arg0", "argument0");
        Mockito.when(transMeta0.getUsedArguments(commandLineArguments)).thenReturn(map0);
        JobEntryInterface jobEntryInterface0 = Mockito.mock(JobEntryInterface.class);
        Mockito.when(jobEntryInterface0.isTransformation()).thenReturn(false);
        jobEntryCopy0.setEntry(jobEntryInterface0);
        jobMeta.jobcopies.add(jobEntryCopy0);
        JobEntryCopy jobEntryCopy1 = new JobEntryCopy();
        TransMeta transMeta1 = Mockito.mock(TransMeta.class);
        Map<String, String> map1 = new HashMap<>();
        map1.put("arg1", "argument1");
        Mockito.when(transMeta1.getUsedArguments(commandLineArguments)).thenReturn(map1);
        JobEntryTrans jobEntryTrans1 = Mockito.mock(JobEntryTrans.class);
        Mockito.when(jobEntryTrans1.isTransformation()).thenReturn(true);
        Mockito.when(jobEntryTrans1.getTransMeta(executionConfiguration.getRepository(), metaStore, jobMeta)).thenReturn(transMeta1);
        jobEntryCopy1.setEntry(jobEntryTrans1);
        jobMeta.jobcopies.add(jobEntryCopy1);
        JobEntryCopy jobEntryCopy2 = new JobEntryCopy();
        TransMeta transMeta2 = Mockito.mock(TransMeta.class);
        Map<String, String> map2 = new HashMap<>();
        map2.put("arg1", "argument1");
        map2.put("arg2", "argument2");
        Mockito.when(transMeta2.getUsedArguments(commandLineArguments)).thenReturn(map2);
        JobEntryTrans jobEntryTrans2 = Mockito.mock(JobEntryTrans.class);
        Mockito.when(jobEntryTrans2.isTransformation()).thenReturn(true);
        Mockito.when(jobEntryTrans2.getTransMeta(executionConfiguration.getRepository(), metaStore, jobMeta)).thenReturn(transMeta2);
        jobEntryCopy2.setEntry(jobEntryTrans2);
        jobMeta.jobcopies.add(jobEntryCopy2);
        executionConfiguration.getUsedArguments(jobMeta, commandLineArguments, metaStore);
        Assert.assertEquals(2, executionConfiguration.getArguments().size());
    }

    @Test
    public void testGetUsedVariablesWithNoPreviousExecutionConfigurationVariables() throws KettleException {
        JobExecutionConfiguration executionConfiguration = new JobExecutionConfiguration();
        Map<String, String> variables0 = new HashMap<>();
        executionConfiguration.setVariables(variables0);
        JobMeta jobMeta0 = Mockito.mock(JobMeta.class);
        List<String> list0 = new ArrayList<String>();
        list0.add("var1");
        Mockito.when(jobMeta0.getUsedVariables()).thenReturn(list0);
        // Const.INTERNAL_VARIABLE_PREFIX values
        Mockito.when(jobMeta0.getVariable(ArgumentMatchers.anyString())).thenReturn("internalDummyValue");
        executionConfiguration.getUsedVariables(jobMeta0);
        // 8 = 7 internalDummyValues + var1 from JobMeta with default value
        Assert.assertEquals(8, executionConfiguration.getVariables().size());
        Assert.assertEquals("", executionConfiguration.getVariables().get("var1"));
    }

    @Test
    public void testGetUsedVariablesWithSamePreviousExecutionConfigurationVariables() throws KettleException {
        JobExecutionConfiguration executionConfiguration = new JobExecutionConfiguration();
        Map<String, String> variables0 = new HashMap<>();
        variables0.put("var1", "valueVar1");
        executionConfiguration.setVariables(variables0);
        JobMeta jobMeta0 = Mockito.mock(JobMeta.class);
        List<String> list0 = new ArrayList<String>();
        list0.add("var1");
        Mockito.when(jobMeta0.getUsedVariables()).thenReturn(list0);
        Mockito.when(jobMeta0.getVariable(ArgumentMatchers.anyString())).thenReturn("internalDummyValue");
        executionConfiguration.getUsedVariables(jobMeta0);
        // 8 = 7 internalDummyValues + var1 from JobMeta ( with variables0 value )
        Assert.assertEquals(8, executionConfiguration.getVariables().size());
        Assert.assertEquals("valueVar1", executionConfiguration.getVariables().get("var1"));
    }

    @Test
    public void testGetUsedVariablesWithDifferentPreviousExecutionConfigurationVariables() throws KettleException {
        JobExecutionConfiguration executionConfiguration = new JobExecutionConfiguration();
        Map<String, String> variables0 = new HashMap<>();
        variables0.put("var2", "valueVar2");
        executionConfiguration.setVariables(variables0);
        JobMeta jobMeta0 = Mockito.mock(JobMeta.class);
        List<String> list0 = new ArrayList<String>();
        list0.add("var1");
        Mockito.when(jobMeta0.getUsedVariables()).thenReturn(list0);
        Mockito.when(jobMeta0.getVariable(ArgumentMatchers.anyString())).thenReturn("internalDummyValue");
        executionConfiguration.getUsedVariables(jobMeta0);
        // 9 = 7 internalDummyValues + var1 from JobMeta ( with the default value ) + var2 from variables0
        Assert.assertEquals(9, executionConfiguration.getVariables().size());
        Assert.assertEquals("", executionConfiguration.getVariables().get("var1"));
        Assert.assertEquals("valueVar2", executionConfiguration.getVariables().get("var2"));
    }
}

