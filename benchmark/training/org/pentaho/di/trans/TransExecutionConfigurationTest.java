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
package org.pentaho.di.trans;


import TransExecutionConfiguration.XML_TAG;
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
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryMeta;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class TransExecutionConfigurationTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testConnectRepository() throws KettleException {
        TransExecutionConfiguration transExecConf = new TransExecutionConfiguration();
        final RepositoriesMeta repositoriesMeta = Mockito.mock(RepositoriesMeta.class);
        final RepositoryMeta repositoryMeta = Mockito.mock(RepositoryMeta.class);
        final Repository repository = Mockito.mock(Repository.class);
        final String mockRepo = "mockRepo";
        final boolean[] connectionSuccess = new boolean[]{ false };
        Repository initialRepo = Mockito.mock(Repository.class);
        transExecConf.setRepository(initialRepo);
        KettleLogStore.init();
        // Create mock repository plugin
        TransExecutionConfigurationTest.MockRepositoryPlugin mockRepositoryPlugin = Mockito.mock(TransExecutionConfigurationTest.MockRepositoryPlugin.class);
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
        List<PluginInterface> registeredPlugins = PluginRegistry.getInstance().getPlugins(RepositoryPluginType.class);
        for (PluginInterface registeredPlugin : registeredPlugins) {
            PluginRegistry.getInstance().removePlugin(RepositoryPluginType.class, registeredPlugin);
        }
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
        transExecConf.connectRepository(repositoriesMeta, "notFound", "username", "password");
        Assert.assertEquals("Repository Changed", initialRepo, transExecConf.getRepository());
        // Ignore failed attempt to connect
        transExecConf.connectRepository(repositoriesMeta, mockRepo, "username", "");
        Assert.assertEquals("Repository Changed", initialRepo, transExecConf.getRepository());
        // Save repository if connection passes
        transExecConf.connectRepository(repositoriesMeta, mockRepo, "username", "password");
        Assert.assertEquals("Repository didn't change", repository, transExecConf.getRepository());
        Assert.assertTrue("Repository not connected", connectionSuccess[0]);
    }

    public interface MockRepositoryPlugin extends ClassLoadingPluginInterface , PluginInterface {}

    @Test
    public void testDefaultPassedBatchId() {
        TransExecutionConfiguration tec = new TransExecutionConfiguration();
        Assert.assertEquals("default passedBatchId value must be null", null, tec.getPassedBatchId());
    }

    @Test
    public void testCopy() {
        TransExecutionConfiguration tec = new TransExecutionConfiguration();
        tec.setPassedBatchId(null);
        // CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
        {
            TransExecutionConfiguration tecCopy = ((TransExecutionConfiguration) (tec.clone()));
            Assert.assertEquals("clone-copy", tec.getPassedBatchId(), tecCopy.getPassedBatchId());
        }
        tec.setPassedBatchId(0L);
        // CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
        {
            TransExecutionConfiguration tecCopy = ((TransExecutionConfiguration) (tec.clone()));
            Assert.assertEquals("clone-copy", tec.getPassedBatchId(), tecCopy.getPassedBatchId());
        }
        tec.setPassedBatchId(5L);
        // CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
        {
            TransExecutionConfiguration tecCopy = ((TransExecutionConfiguration) (tec.clone()));
            Assert.assertEquals("clone-copy", tec.getPassedBatchId(), tecCopy.getPassedBatchId());
        }
    }

    @Test
    public void testCopyXml() throws Exception {
        TransExecutionConfiguration tec = new TransExecutionConfiguration();
        final Long passedBatchId0 = null;
        final long passedBatchId1 = 0L;
        final long passedBatchId2 = 5L;
        tec.setPassedBatchId(passedBatchId0);
        // CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
        {
            String xml = tec.getXML();
            Document doc = XMLHandler.loadXMLString(xml);
            Node node = XMLHandler.getSubNode(doc, XML_TAG);
            TransExecutionConfiguration tecCopy = new TransExecutionConfiguration(node);
            Assert.assertEquals("xml-copy", tec.getPassedBatchId(), tecCopy.getPassedBatchId());
        }
        tec.setPassedBatchId(passedBatchId1);
        // CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
        {
            String xml = tec.getXML();
            Document doc = XMLHandler.loadXMLString(xml);
            Node node = XMLHandler.getSubNode(doc, XML_TAG);
            TransExecutionConfiguration tecCopy = new TransExecutionConfiguration(node);
            Assert.assertEquals("xml-copy", tec.getPassedBatchId(), tecCopy.getPassedBatchId());
        }
        tec.setPassedBatchId(passedBatchId2);
        // CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
        {
            String xml = tec.getXML();
            Document doc = XMLHandler.loadXMLString(xml);
            Node node = XMLHandler.getSubNode(doc, XML_TAG);
            TransExecutionConfiguration tecCopy = new TransExecutionConfiguration(node);
            Assert.assertEquals("xml-copy", tec.getPassedBatchId(), tecCopy.getPassedBatchId());
        }
    }
}

