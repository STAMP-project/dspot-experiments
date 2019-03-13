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


import Const.INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY;
import ObjectLocationSpecificationMethod.FILENAME;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.ProgressMonitorListener;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Created by Yury_Bakhmutski on 2/8/2017.
 */
@RunWith(PowerMockRunner.class)
public class StepWithMappingMetaTest {
    @Mock
    TransMeta transMeta;

    @Test
    public void loadMappingMeta() throws Exception {
        String variablePath = "Internal.Entry.Current.Directory";
        String virtualDir = "/testFolder/CDA-91";
        String fileName = "testTrans.ktr";
        VariableSpace variables = new Variables();
        StepMeta stepMeta = new StepMeta();
        TransMeta parentTransMeta = new TransMeta();
        stepMeta.setParentTransMeta(parentTransMeta);
        RepositoryDirectoryInterface repositoryDirectory = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.when(repositoryDirectory.toString()).thenReturn(virtualDir);
        stepMeta.getParentTransMeta().setRepositoryDirectory(repositoryDirectory);
        StepWithMappingMeta mappingMetaMock = Mockito.mock(StepWithMappingMeta.class);
        Mockito.when(mappingMetaMock.getSpecificationMethod()).thenReturn(FILENAME);
        Mockito.when(mappingMetaMock.getFileName()).thenReturn(((("${" + variablePath) + "}/") + fileName));
        Mockito.when(mappingMetaMock.getParentStepMeta()).thenReturn(stepMeta);
        // mock repo and answers
        Repository rep = Mockito.mock(Repository.class);
        Mockito.doAnswer(new Answer<TransMeta>() {
            @Override
            public TransMeta answer(final InvocationOnMock invocation) throws Throwable {
                final String originalArgument = ((String) (invocation.getArguments()[0]));
                // be sure that the variable was replaced by real path
                Assert.assertEquals(virtualDir, originalArgument);
                return null;
            }
        }).when(rep).findDirectory(ArgumentMatchers.anyString());
        Mockito.doAnswer(new Answer<TransMeta>() {
            @Override
            public TransMeta answer(final InvocationOnMock invocation) throws Throwable {
                final String originalArgument = ((String) (invocation.getArguments()[0]));
                // be sure that transformation name was resolved correctly
                Assert.assertEquals(fileName, originalArgument);
                return Mockito.mock(TransMeta.class);
            }
        }).when(rep).loadTransformation(ArgumentMatchers.anyString(), ArgumentMatchers.any(RepositoryDirectoryInterface.class), ArgumentMatchers.any(ProgressMonitorListener.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyString());
        StepWithMappingMeta.loadMappingMeta(mappingMetaMock, rep, null, variables, true);
    }

    @SuppressWarnings("unchecked")
    @Test
    @PrepareForTest(StepWithMappingMeta.class)
    public void testExportResources() throws Exception {
        StepWithMappingMeta stepWithMappingMeta = Mockito.spy(new StepWithMappingMeta() {
            @Override
            public void setDefault() {
            }

            @Override
            public StepDataInterface getStepData() {
                return null;
            }

            @Override
            public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
                return null;
            }
        });
        String testName = "test";
        PowerMockito.mockStatic(StepWithMappingMeta.class);
        Mockito.when(StepWithMappingMeta.loadMappingMeta(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(transMeta);
        Mockito.when(transMeta.exportResources(ArgumentMatchers.any(), ArgumentMatchers.anyMap(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(testName);
        stepWithMappingMeta.exportResources(null, null, null, null, null);
        Mockito.verify(transMeta).setFilename(((("${" + (Const.INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY)) + "}/") + testName));
        Mockito.verify(stepWithMappingMeta).setSpecificationMethod(FILENAME);
    }

    @Test
    @PrepareForTest(StepWithMappingMeta.class)
    public void loadMappingMetaTest() throws Exception {
        String childParam = "childParam";
        String childValue = "childValue";
        String paramOverwrite = "paramOverwrite";
        String parentParam = "parentParam";
        String parentValue = "parentValue";
        String variablePath = "Internal.Entry.Current.Directory";
        String virtualDir = "/testFolder/CDA-91";
        String fileName = "testTrans.ktr";
        VariableSpace variables = new Variables();
        variables.setVariable(parentParam, parentValue);
        variables.setVariable(paramOverwrite, parentValue);
        StepMeta stepMeta = new StepMeta();
        TransMeta parentTransMeta = new TransMeta();
        stepMeta.setParentTransMeta(parentTransMeta);
        RepositoryDirectoryInterface repositoryDirectory = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.when(repositoryDirectory.toString()).thenReturn(virtualDir);
        stepMeta.getParentTransMeta().setRepositoryDirectory(repositoryDirectory);
        StepWithMappingMeta mappingMetaMock = Mockito.mock(StepWithMappingMeta.class);
        Mockito.when(mappingMetaMock.getSpecificationMethod()).thenReturn(FILENAME);
        Mockito.when(mappingMetaMock.getFileName()).thenReturn(((("${" + variablePath) + "}/") + fileName));
        Mockito.when(mappingMetaMock.getParentStepMeta()).thenReturn(stepMeta);
        Repository rep = Mockito.mock(Repository.class);
        Mockito.doReturn(Mockito.mock(RepositoryDirectoryInterface.class)).when(rep).findDirectory(ArgumentMatchers.anyString());
        TransMeta child = new TransMeta();
        child.setVariable(childParam, childValue);
        child.setVariable(paramOverwrite, childValue);
        Mockito.doReturn(child).when(rep).loadTransformation(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
        TransMeta transMeta = StepWithMappingMeta.loadMappingMeta(mappingMetaMock, rep, null, variables, true);
        Assert.assertNotNull(transMeta);
        // When the child parameter does exist in the parent parameters, overwrite the child parameter by the parent parameter.
        Assert.assertEquals(parentValue, transMeta.getVariable(paramOverwrite));
        // When the child parameter does not exist in the parent parameters, keep it.
        Assert.assertEquals(childValue, transMeta.getVariable(childParam));
        // All other parent parameters need to get copied into the child parameters  (when the 'Inherit all
        // variables from the transformation?' option is checked)
        Assert.assertEquals(parentValue, transMeta.getVariable(parentParam));
    }

    @Test
    @PrepareForTest(StepWithMappingMeta.class)
    public void loadMappingMetaTest_PathShouldBeTakenFromParentTrans() throws Exception {
        String fileName = "subtrans-executor-sub.ktr";
        Path parentFolder = Paths.get(getClass().getResource("subtrans-executor-sub.ktr").toURI()).getParent();
        // we have transformation
        VariableSpace variables = new Variables();
        variables.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, parentFolder.toString());
        TransMeta parentTransMeta = new TransMeta(variables);
        // we have step in this transformation
        StepMeta stepMeta = new StepMeta();
        stepMeta.setParentTransMeta(parentTransMeta);
        // attach the executor to step which was described above
        StepWithMappingMeta mappingMetaMock = Mockito.mock(StepWithMappingMeta.class);
        Mockito.when(mappingMetaMock.getSpecificationMethod()).thenReturn(FILENAME);
        Mockito.when(mappingMetaMock.getFileName()).thenReturn(((("${" + (Const.INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY)) + "}/") + fileName));
        Mockito.when(mappingMetaMock.getParentStepMeta()).thenReturn(stepMeta);
        // we will try to load the subtras which was linked at the step metas
        TransMeta transMeta = StepWithMappingMeta.loadMappingMeta(mappingMetaMock, null, null, variables, true);
        StringBuilder expected = new StringBuilder(parentFolder.toUri().toString());
        /**
         * we need to remove "/" at the end of expected string because during load the trans from file
         * internal variables will be replaced by uri from kettle vfs
         * check the follow points
         * {@link org.pentaho.di.trans.TransMeta#setInternalFilenameKettleVariables(VariableSpace)}
         */
        Assert.assertEquals(expected.deleteCharAt(((expected.length()) - 1)).toString(), transMeta.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }

    @Test
    @PrepareForTest(StepWithMappingMeta.class)
    public void activateParamsTest() throws Exception {
        String childParam = "childParam";
        String childValue = "childValue";
        String paramOverwrite = "paramOverwrite";
        String parentValue = "parentValue";
        String stepValue = "stepValue";
        VariableSpace parent = new Variables();
        parent.setVariable(paramOverwrite, parentValue);
        TransMeta childVariableSpace = new TransMeta();
        childVariableSpace.addParameterDefinition(childParam, "", "");
        childVariableSpace.setParameterValue(childParam, childValue);
        String[] parameters = childVariableSpace.listParameters();
        StepWithMappingMeta.activateParams(childVariableSpace, childVariableSpace, parent, parameters, new String[]{ childParam, paramOverwrite }, new String[]{ childValue, stepValue });
        Assert.assertEquals(childValue, childVariableSpace.getVariable(childParam));
        // the step parameter prevails
        Assert.assertEquals(stepValue, childVariableSpace.getVariable(paramOverwrite));
    }

    @Test
    @PrepareForTest(StepWithMappingMeta.class)
    public void activateParamsWithFalsePassParametersFlagTest() throws Exception {
        String childParam = "childParam";
        String childValue = "childValue";
        String paramOverwrite = "paramOverwrite";
        String parentValue = "parentValue";
        String stepValue = "stepValue";
        String parentAndChildParameter = "parentAndChildParameter";
        VariableSpace parent = new Variables();
        parent.setVariable(paramOverwrite, parentValue);
        parent.setVariable(parentAndChildParameter, parentValue);
        TransMeta childVariableSpace = new TransMeta();
        childVariableSpace.addParameterDefinition(childParam, "", "");
        childVariableSpace.setParameterValue(childParam, childValue);
        childVariableSpace.addParameterDefinition(parentAndChildParameter, "", "");
        childVariableSpace.setParameterValue(parentAndChildParameter, childValue);
        String[] parameters = childVariableSpace.listParameters();
        StepWithMappingMeta.activateParams(childVariableSpace, childVariableSpace, parent, parameters, new String[]{ childParam, paramOverwrite }, new String[]{ childValue, stepValue }, false);
        Assert.assertEquals(childValue, childVariableSpace.getVariable(childParam));
        // the step parameter prevails
        Assert.assertEquals(stepValue, childVariableSpace.getVariable(paramOverwrite));
        Assert.assertEquals(childValue, childVariableSpace.getVariable(parentAndChildParameter));
    }

    @Test
    @PrepareForTest(StepWithMappingMeta.class)
    public void activateParamsWithTruePassParametersFlagTest() throws Exception {
        String childParam = "childParam";
        String childValue = "childValue";
        String paramOverwrite = "paramOverwrite";
        String parentValue = "parentValue";
        String stepValue = "stepValue";
        String parentAndChildParameter = "parentAndChildParameter";
        VariableSpace parent = new Variables();
        parent.setVariable(paramOverwrite, parentValue);
        parent.setVariable(parentAndChildParameter, parentValue);
        TransMeta childVariableSpace = new TransMeta();
        childVariableSpace.addParameterDefinition(childParam, "", "");
        childVariableSpace.setParameterValue(childParam, childValue);
        childVariableSpace.addParameterDefinition(parentAndChildParameter, "", "");
        childVariableSpace.setParameterValue(parentAndChildParameter, childValue);
        String[] parameters = childVariableSpace.listParameters();
        StepWithMappingMeta.activateParams(childVariableSpace, childVariableSpace, parent, parameters, new String[]{ childParam, paramOverwrite }, new String[]{ childValue, stepValue }, true);
        // childVariableSpace.setVariable( parentAndChildParameter, parentValue);
        Assert.assertEquals(childValue, childVariableSpace.getVariable(childParam));
        // the step parameter prevails
        Assert.assertEquals(stepValue, childVariableSpace.getVariable(paramOverwrite));
        Assert.assertEquals(parentValue, childVariableSpace.getVariable(parentAndChildParameter));
    }

    @Test
    @PrepareForTest(StepWithMappingMeta.class)
    public void activateParamsTestWithNoParameterChild() throws Exception {
        String newParam = "newParamParent";
        String parentValue = "parentValue";
        TransMeta parentMeta = new TransMeta();
        TransMeta childVariableSpace = new TransMeta();
        String[] parameters = childVariableSpace.listParameters();
        StepWithMappingMeta.activateParams(childVariableSpace, childVariableSpace, parentMeta, parameters, new String[]{ newParam }, new String[]{ parentValue });
        Assert.assertEquals(parentValue, childVariableSpace.getParameterValue(newParam));
    }

    @Test
    @PrepareForTest(StepWithMappingMeta.class)
    public void testFileNameAsVariable() throws Exception {
        String transName = "test.ktr";
        String transDirectory = "/admin";
        String transNameVar = "transName";
        String transDirectoryVar = "transDirectory";
        VariableSpace parent = new Variables();
        parent.setVariable(transNameVar, transName);
        parent.setVariable(transDirectoryVar, transDirectory);
        StepMeta stepMeta = new StepMeta();
        TransMeta parentTransMeta = new TransMeta();
        stepMeta.setParentTransMeta(parentTransMeta);
        StepWithMappingMeta mappingMetaMock = Mockito.mock(StepWithMappingMeta.class);
        Mockito.when(mappingMetaMock.getSpecificationMethod()).thenReturn(FILENAME);
        Mockito.when(mappingMetaMock.getFileName()).thenReturn((((("${" + transDirectoryVar) + "}/${") + transNameVar) + "}"));
        Mockito.when(mappingMetaMock.getParentStepMeta()).thenReturn(stepMeta);
        Repository rep = Mockito.mock(Repository.class);
        RepositoryDirectoryInterface directoryInterface = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.doReturn(directoryInterface).when(rep).findDirectory(ArgumentMatchers.anyString());
        Mockito.doReturn(new TransMeta()).when(rep).loadTransformation(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
        TransMeta transMeta = StepWithMappingMeta.loadMappingMeta(mappingMetaMock, rep, null, parent, true);
        Assert.assertNotNull(transMeta);
        Mockito.verify(rep, Mockito.times(1)).findDirectory(Mockito.eq(transDirectory));
        Mockito.verify(rep, Mockito.times(1)).loadTransformation(Mockito.eq(transName), Mockito.eq(directoryInterface), Mockito.eq(null), Mockito.eq(true), Mockito.eq(null));
    }

    @Test
    @PrepareForTest(StepWithMappingMeta.class)
    public void replaceVariablesWithJobInternalVariablesTest() {
        String variableOverwrite = "paramOverwrite";
        String variableChildOnly = "childValueVariable";
        String[] jobVariables = Const.INTERNAL_JOB_VARIABLES;
        VariableSpace ChildVariables = new Variables();
        VariableSpace replaceByParentVariables = new Variables();
        for (String internalVariable : jobVariables) {
            ChildVariables.setVariable(internalVariable, "childValue");
            replaceByParentVariables.setVariable(internalVariable, "parentValue");
        }
        ChildVariables.setVariable(variableChildOnly, "childValueVariable");
        ChildVariables.setVariable(variableOverwrite, "childNotInternalValue");
        replaceByParentVariables.setVariable(variableOverwrite, "parentNotInternalValue");
        StepWithMappingMeta.replaceVariableValues(ChildVariables, replaceByParentVariables);
        // do not replace internal variables
        Assert.assertEquals("childValue", ChildVariables.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
        // replace non internal variables
        Assert.assertEquals("parentNotInternalValue", ChildVariables.getVariable(variableOverwrite));
        // keep child only variables
        Assert.assertEquals(variableChildOnly, ChildVariables.getVariable(variableChildOnly));
    }

    @Test
    @PrepareForTest(StepWithMappingMeta.class)
    public void replaceVariablesWithTransInternalVariablesTest() {
        String variableOverwrite = "paramOverwrite";
        String variableChildOnly = "childValueVariable";
        String[] jobVariables = Const.INTERNAL_TRANS_VARIABLES;
        VariableSpace ChildVariables = new Variables();
        VariableSpace replaceByParentVariables = new Variables();
        for (String internalVariable : jobVariables) {
            ChildVariables.setVariable(internalVariable, "childValue");
            replaceByParentVariables.setVariable(internalVariable, "parentValue");
        }
        ChildVariables.setVariable(variableChildOnly, "childValueVariable");
        ChildVariables.setVariable(variableOverwrite, "childNotInternalValue");
        replaceByParentVariables.setVariable(variableOverwrite, "parentNotInternalValue");
        StepWithMappingMeta.replaceVariableValues(ChildVariables, replaceByParentVariables);
        // do not replace internal variables
        Assert.assertEquals("childValue", ChildVariables.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
        // replace non internal variables
        Assert.assertEquals("parentNotInternalValue", ChildVariables.getVariable(variableOverwrite));
        // keep child only variables
        Assert.assertEquals(variableChildOnly, ChildVariables.getVariable(variableChildOnly));
    }
}

