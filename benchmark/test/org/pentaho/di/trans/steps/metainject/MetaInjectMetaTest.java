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
package org.pentaho.di.trans.steps.metainject;


import ObjectLocationSpecificationMethod.FILENAME;
import ResourceEntry.ResourceType.ACTIONFILE;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.ProgressMonitorListener;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.resource.ResourceDefinition;
import org.pentaho.di.resource.ResourceEntry;
import org.pentaho.di.resource.ResourceNamingInterface;
import org.pentaho.di.resource.ResourceReference;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.metastore.api.IMetaStore;


public class MetaInjectMetaTest {
    private static final String SOURCE_STEP_NAME = "SOURCE_STEP_NAME";

    private static final String SOURCE_FIELD_NAME = "SOURCE_STEP_NAME";

    private static final String TARGET_STEP_NAME = "TARGET_STEP_NAME";

    private static final String TARGET_FIELD_NAME = "TARGET_STEP_NAME";

    private static final String TEST_FILE_NAME = "TEST_FILE_NAME";

    private static final String EXPORTED_FILE_NAME = (("${" + (Const.INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY)) + "}/") + (MetaInjectMetaTest.TEST_FILE_NAME);

    private MetaInjectMeta metaInjectMeta;

    @Test
    public void getResourceDependencies() {
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        List<ResourceReference> actualResult = metaInjectMeta.getResourceDependencies(transMeta, stepMeta);
        Assert.assertEquals(1, actualResult.size());
        ResourceReference reference = actualResult.iterator().next();
        Assert.assertEquals(0, reference.getEntries().size());
    }

    @Test
    public void getResourceDependencies_with_defined_fileName() {
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        metaInjectMeta.setFileName("FILE_NAME");
        Mockito.doReturn("FILE_NAME_WITH_SUBSTITUTIONS").when(transMeta).environmentSubstitute("FILE_NAME");
        List<ResourceReference> actualResult = metaInjectMeta.getResourceDependencies(transMeta, stepMeta);
        Assert.assertEquals(1, actualResult.size());
        ResourceReference reference = actualResult.iterator().next();
        Assert.assertEquals(1, reference.getEntries().size());
    }

    @Test
    public void getResourceDependencies_with_defined_transName() {
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        metaInjectMeta.setTransName("TRANS_NAME");
        Mockito.doReturn("TRANS_NAME_WITH_SUBSTITUTIONS").when(transMeta).environmentSubstitute("TRANS_NAME");
        List<ResourceReference> actualResult = metaInjectMeta.getResourceDependencies(transMeta, stepMeta);
        Assert.assertEquals(1, actualResult.size());
        ResourceReference reference = actualResult.iterator().next();
        Assert.assertEquals(1, reference.getEntries().size());
    }

    @Test
    public void getResourceDependencies_repository_full_path() {
        // checks getResourceDependencies() returns action file resource w/ transname including full repository path name
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        metaInjectMeta.setTransName("TRANS_NAME");
        metaInjectMeta.setDirectoryPath("/REPO/DIR");
        Mockito.doReturn("TRANS_NAME_SUBS").when(transMeta).environmentSubstitute("TRANS_NAME");
        Mockito.doReturn("/REPO/DIR_SUBS").when(transMeta).environmentSubstitute("/REPO/DIR");
        List<ResourceReference> actualResult = metaInjectMeta.getResourceDependencies(transMeta, stepMeta);
        Assert.assertEquals(1, actualResult.size());
        ResourceReference reference = actualResult.iterator().next();
        Assert.assertEquals(1, reference.getEntries().size());
        ResourceEntry resourceEntry = reference.getEntries().get(0);
        Assert.assertEquals("/REPO/DIR_SUBS/TRANS_NAME_SUBS", resourceEntry.getResource());
        Assert.assertEquals(ACTIONFILE, resourceEntry.getResourcetype());
    }

    @Test
    public void exportResources() throws KettleException {
        VariableSpace variableSpace = Mockito.mock(VariableSpace.class);
        ResourceNamingInterface resourceNamingInterface = Mockito.mock(ResourceNamingInterface.class);
        Repository repository = Mockito.mock(Repository.class);
        IMetaStore metaStore = Mockito.mock(IMetaStore.class);
        MetaInjectMeta injectMetaSpy = Mockito.spy(metaInjectMeta);
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        Map<String, ResourceDefinition> definitions = Collections.<String, ResourceDefinition>emptyMap();
        Mockito.doReturn(MetaInjectMetaTest.TEST_FILE_NAME).when(transMeta).exportResources(transMeta, definitions, resourceNamingInterface, repository, metaStore);
        Mockito.doReturn(transMeta).when(injectMetaSpy).loadTransformationMeta(repository, variableSpace);
        String actualExportedFileName = injectMetaSpy.exportResources(variableSpace, definitions, resourceNamingInterface, repository, metaStore);
        Assert.assertEquals(MetaInjectMetaTest.TEST_FILE_NAME, actualExportedFileName);
        Assert.assertEquals(MetaInjectMetaTest.EXPORTED_FILE_NAME, injectMetaSpy.getFileName());
        Mockito.verify(transMeta).exportResources(transMeta, definitions, resourceNamingInterface, repository, metaStore);
    }

    @Test
    public void convertToMap() {
        MetaInjectMapping metaInjectMapping = new MetaInjectMapping();
        metaInjectMapping.setSourceStep(MetaInjectMetaTest.SOURCE_STEP_NAME);
        metaInjectMapping.setSourceField(MetaInjectMetaTest.SOURCE_FIELD_NAME);
        metaInjectMapping.setTargetStep(MetaInjectMetaTest.TARGET_STEP_NAME);
        metaInjectMapping.setTargetField(MetaInjectMetaTest.TARGET_FIELD_NAME);
        Map<TargetStepAttribute, SourceStepField> actualResult = MetaInjectMeta.convertToMap(Collections.singletonList(metaInjectMapping));
        Assert.assertEquals(1, actualResult.size());
        TargetStepAttribute targetStepAttribute = actualResult.keySet().iterator().next();
        Assert.assertEquals(MetaInjectMetaTest.TARGET_STEP_NAME, targetStepAttribute.getStepname());
        Assert.assertEquals(MetaInjectMetaTest.TARGET_FIELD_NAME, targetStepAttribute.getAttributeKey());
        SourceStepField sourceStepField = actualResult.values().iterator().next();
        Assert.assertEquals(MetaInjectMetaTest.SOURCE_STEP_NAME, sourceStepField.getStepname());
        Assert.assertEquals(MetaInjectMetaTest.SOURCE_FIELD_NAME, sourceStepField.getField());
    }

    @Test
    public void testLoadMappingMetaWhenConnectedToRep() throws Exception {
        String variablePath = "Internal.Entry.Current.Directory";
        String virtualDir = "/testFolder/test";
        String fileName = "testTrans.ktr";
        VariableSpace variables = new Variables();
        variables.setVariable(variablePath, virtualDir);
        MetaInjectMeta metaInjectMetaMock = Mockito.mock(MetaInjectMeta.class);
        Mockito.when(metaInjectMetaMock.getSpecificationMethod()).thenReturn(FILENAME);
        Mockito.when(metaInjectMetaMock.getFileName()).thenReturn(((("${" + variablePath) + "}/") + fileName));
        // mock repo and answers
        Repository rep = Mockito.mock(Repository.class);
        Mockito.doAnswer(( invocation) -> {
            String originalArgument = ((String) (invocation.getArguments()[0]));
            // be sure that the variable was replaced by real path
            Assert.assertEquals(originalArgument, virtualDir);
            return null;
        }).when(rep).findDirectory(ArgumentMatchers.anyString());
        Mockito.doAnswer(( invocation) -> {
            String originalArgument = ((String) (invocation.getArguments()[0]));
            // be sure that transformation name was resolved correctly
            Assert.assertEquals(originalArgument, fileName);
            return Mockito.mock(TransMeta.class);
        }).when(rep).loadTransformation(ArgumentMatchers.anyString(), ArgumentMatchers.any(RepositoryDirectoryInterface.class), ArgumentMatchers.any(ProgressMonitorListener.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyString());
        Assert.assertNotNull(MetaInjectMeta.loadTransformationMeta(metaInjectMetaMock, rep, null, variables));
    }
}

