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
package org.pentaho.di.job.entries.setvariables;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.job.Job;
import org.pentaho.di.repository.Repository;
import org.pentaho.metastore.api.IMetaStore;


public class JobEntrySetVariablesTest {
    private Job job;

    private JobEntrySetVariables entry;

    @Test
    public void testASCIIText() throws Exception {
        // properties file with native2ascii
        entry.setFilename("src/test/resources/org/pentaho/di/job/entries/setvariables/ASCIIText.properties");
        entry.setVariableName(new String[]{  });// For absence of null check in execute method

        entry.setReplaceVars(true);
        Result result = entry.execute(new Result(), 0);
        Assert.assertTrue("Result should be true", result.getResult());
        Assert.assertEquals("???", entry.getVariable("Japanese"));
        Assert.assertEquals("English", entry.getVariable("English"));
        Assert.assertEquals("??", entry.getVariable("Chinese"));
    }

    @Test
    public void testUTF8Text() throws Exception {
        // properties files without native2ascii
        entry.setFilename("src/test/resources/org/pentaho/di/job/entries/setvariables/UTF8Text.properties");
        entry.setVariableName(new String[]{  });// For absence of null check in execute method

        entry.setReplaceVars(true);
        Result result = entry.execute(new Result(), 0);
        Assert.assertTrue("Result should be true", result.getResult());
        Assert.assertEquals("???", entry.getVariable("Japanese"));
        Assert.assertEquals("English", entry.getVariable("English"));
        Assert.assertEquals("??", entry.getVariable("Chinese"));
    }

    @Test
    public void testInputStreamClosed() throws Exception {
        // properties files without native2ascii
        String propertiesFilename = "src/test/resources/org/pentaho/di/job/entries/setvariables/UTF8Text.properties";
        entry.setFilename(propertiesFilename);
        entry.setVariableName(new String[]{  });// For absence of null check in execute method

        entry.setReplaceVars(true);
        Result result = entry.execute(new Result(), 0);
        Assert.assertTrue("Result should be true", result.getResult());
        RandomAccessFile fos = null;
        try {
            File file = new File(propertiesFilename);
            if (file.exists()) {
                fos = new RandomAccessFile(file, "rw");
            }
        } catch (FileNotFoundException | SecurityException e) {
            Assert.fail("the file with properties should be unallocated");
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    @Test
    public void testParentJobVariablesExecutingFilePropertiesThatChangesVariablesAndParameters() throws Exception {
        entry.setVariableName(new String[]{  });// For absence of null check in execute method

        entry.setReplaceVars(true);
        entry.setFileVariableType(1);
        Job parentJob = entry.getParentJob();
        parentJob.addParameterDefinition("parentParam", "", "");
        parentJob.setParameterValue("parentParam", "parentValue");
        parentJob.setVariable("parentParam", "parentValue");
        entry.setFilename("src/test/resources/org/pentaho/di/job/entries/setvariables/configurationA.properties");
        Result result = entry.execute(new Result(), 0);
        Assert.assertTrue("Result should be true", result.getResult());
        Assert.assertEquals("a", parentJob.getVariable("propertyFile"));
        Assert.assertEquals("a", parentJob.getVariable("dynamicProperty"));
        Assert.assertEquals("parentValue", parentJob.getVariable("parentParam"));
        entry.setFilename("src/test/resources/org/pentaho/di/job/entries/setvariables/configurationB.properties");
        result = entry.execute(new Result(), 0);
        Assert.assertTrue("Result should be true", result.getResult());
        Assert.assertEquals("b", parentJob.getVariable("propertyFile"));
        Assert.assertEquals("new", parentJob.getVariable("newProperty"));
        Assert.assertEquals("haha", parentJob.getVariable("parentParam"));
        Assert.assertEquals("static", parentJob.getVariable("staticProperty"));
        Assert.assertEquals("", parentJob.getVariable("dynamicProperty"));
        entry.setFilename("src/test/resources/org/pentaho/di/job/entries/setvariables/configurationA.properties");
        result = entry.execute(new Result(), 0);
        Assert.assertTrue("Result should be true", result.getResult());
        Assert.assertEquals("a", parentJob.getVariable("propertyFile"));
        Assert.assertEquals("", parentJob.getVariable("newProperty"));
        Assert.assertEquals("parentValue", parentJob.getVariable("parentParam"));
        Assert.assertEquals("", parentJob.getVariable("staticProperty"));
        Assert.assertEquals("a", parentJob.getVariable("dynamicProperty"));
        entry.setFilename("src/test/resources/org/pentaho/di/job/entries/setvariables/configurationB.properties");
        result = entry.execute(new Result(), 0);
        Assert.assertTrue("Result should be true", result.getResult());
        Assert.assertEquals("b", parentJob.getVariable("propertyFile"));
        Assert.assertEquals("new", parentJob.getVariable("newProperty"));
        Assert.assertEquals("haha", parentJob.getVariable("parentParam"));
        Assert.assertEquals("static", parentJob.getVariable("staticProperty"));
        Assert.assertEquals("", parentJob.getVariable("dynamicProperty"));
    }

    @Test
    public void testJobEntrySetVariablesExecute_VARIABLE_TYPE_JVM_NullVariable() throws Exception {
        List<DatabaseMeta> databases = Mockito.mock(List.class);
        List<SlaveServer> slaveServers = Mockito.mock(List.class);
        Repository repository = Mockito.mock(Repository.class);
        IMetaStore metaStore = Mockito.mock(IMetaStore.class);
        entry.loadXML(getEntryNode("nullVariable", null), databases, slaveServers, repository, metaStore);
        Result result = entry.execute(new Result(), 0);
        Assert.assertTrue("Result should be true", result.getResult());
        Assert.assertNull(System.getProperty("nullVariable"));
    }

    @Test
    public void testJobEntrySetVariablesExecute_VARIABLE_TYPE_JVM_VariableNotNull() throws Exception {
        List<DatabaseMeta> databases = Mockito.mock(List.class);
        List<SlaveServer> slaveServers = Mockito.mock(List.class);
        Repository repository = Mockito.mock(Repository.class);
        IMetaStore metaStore = Mockito.mock(IMetaStore.class);
        entry.loadXML(getEntryNode("variableNotNull", "someValue"), databases, slaveServers, repository, metaStore);
        Assert.assertNull(System.getProperty("variableNotNull"));
        Result result = entry.execute(new Result(), 0);
        Assert.assertTrue("Result should be true", result.getResult());
        Assert.assertEquals("someValue", System.getProperty("variableNotNull"));
    }
}

