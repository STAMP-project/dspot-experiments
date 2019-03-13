/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.jsoninput.analyzer;


import DictionaryConst.NODE_TYPE_FILE_FIELD;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.jsoninput.JsonInput;
import org.pentaho.di.trans.steps.jsoninput.JsonInputMeta;
import org.pentaho.metaverse.api.IMetaverseBuilder;
import org.pentaho.metaverse.api.IMetaverseObjectFactory;
import org.pentaho.metaverse.api.INamespace;
import org.pentaho.metaverse.api.StepField;
import org.pentaho.metaverse.api.model.IExternalResourceInfo;


@RunWith(MockitoJUnitRunner.class)
public class JsonInputAnalyzerTest {
    @Mock
    private JsonInputAnalyzer analyzerMock;

    private JsonInputAnalyzer analyzer;

    @Mock
    private JsonInput mockJsonInput;

    @Mock
    private JsonInputMeta meta;

    @Mock
    private StepMeta mockStepMeta;

    @Mock
    private TransMeta transMeta;

    @Mock
    private RowMetaInterface mockRowMetaInterface;

    @Mock
    private IMetaverseBuilder mockBuilder;

    @Mock
    private INamespace mockNamespace;

    private IMetaverseObjectFactory mockFactory;

    @Test
    public void testGetUsedFields_fileNameFromField() throws Exception {
        Mockito.when(meta.isAcceptingFilenames()).thenReturn(true);
        Mockito.when(meta.getAcceptingField()).thenReturn("filename");
        Set<String> stepNames = new HashSet<>();
        stepNames.add("previousStep");
        Set<StepField> usedFields = analyzer.getUsedFields(meta);
        Assert.assertNotNull(usedFields);
        Assert.assertEquals(1, usedFields.size());
        StepField used = usedFields.iterator().next();
        Assert.assertEquals("previousStep", used.getStepName());
        Assert.assertEquals("filename", used.getFieldName());
    }

    @Test
    public void testGetUsedFields_isNotAcceptingFilenames() throws Exception {
        Mockito.when(meta.isAcceptingFilenames()).thenReturn(false);
        Mockito.when(meta.getAcceptingField()).thenReturn("filename");
        Set<StepField> usedFields = analyzerMock.getUsedFields(meta);
        Assert.assertNotNull(usedFields);
        Assert.assertEquals(0, usedFields.size());
    }

    @Test
    public void testGetResourceInputNodeType() throws Exception {
        Assert.assertEquals(NODE_TYPE_FILE_FIELD, analyzer.getResourceInputNodeType());
    }

    @Test
    public void testGetResourceOutputNodeType() throws Exception {
        Assert.assertNull(analyzer.getResourceOutputNodeType());
    }

    @Test
    public void testIsOutput() throws Exception {
        Assert.assertFalse(analyzer.isOutput());
    }

    @Test
    public void testIsInput() throws Exception {
        Assert.assertTrue(analyzer.isInput());
    }

    @Test
    public void testGetSupportedSteps() {
        JsonInputAnalyzer analyzer = new JsonInputAnalyzer();
        Set<Class<? extends BaseStepMeta>> types = analyzer.getSupportedSteps();
        Assert.assertNotNull(types);
        Assert.assertEquals(types.size(), 1);
        Assert.assertTrue(types.contains(JsonInputMeta.class));
    }

    @Test
    public void testJsonInputExternalResourceConsumer() throws Exception {
        JsonInputExternalResourceConsumer consumer = new JsonInputExternalResourceConsumer();
        StepMeta spyMeta = Mockito.spy(new StepMeta("test", meta));
        Mockito.when(meta.getParentStepMeta()).thenReturn(spyMeta);
        Mockito.when(spyMeta.getParentTransMeta()).thenReturn(transMeta);
        Mockito.when(meta.getFileName()).thenReturn(null);
        Mockito.when(meta.isAcceptingFilenames()).thenReturn(false);
        Mockito.when(meta.getFilePaths(false)).thenReturn(new String[]{ "/path/to/file1", "/another/path/to/file2" });
        Assert.assertFalse(consumer.isDataDriven(meta));
        Collection<IExternalResourceInfo> resources = consumer.getResourcesFromMeta(meta);
        Assert.assertTrue(resources.isEmpty());
        Mockito.when(meta.writesToFile()).thenReturn(true);
        resources = consumer.getResourcesFromMeta(meta);
        Assert.assertFalse(resources.isEmpty());
        Assert.assertEquals(2, resources.size());
        Mockito.when(meta.getFilePaths(false)).thenReturn(new String[]{ "/path/to/file1", "/another/path/to/file2", "/another/path/to/file3" });
        resources = consumer.getResourcesFromMeta(meta);
        Assert.assertFalse(resources.isEmpty());
        Assert.assertEquals(3, resources.size());
        Mockito.when(meta.isAcceptingFilenames()).thenReturn(true);
        Assert.assertTrue(consumer.isDataDriven(meta));
        Assert.assertTrue(consumer.getResourcesFromMeta(meta).isEmpty());
        Mockito.when(mockJsonInput.environmentSubstitute(Mockito.any(String.class))).thenReturn("/path/to/row/file");
        Mockito.when(mockJsonInput.getStepMetaInterface()).thenReturn(meta);
        resources = consumer.getResourcesFromRow(mockJsonInput, mockRowMetaInterface, new String[]{ "id", "name" });
        Assert.assertFalse(resources.isEmpty());
        Assert.assertEquals(1, resources.size());
        // when getString throws an exception, we still get the cached resources
        Mockito.when(mockRowMetaInterface.getString(Mockito.any(Object[].class), Mockito.anyString(), Mockito.anyString())).thenThrow(KettleException.class);
        resources = consumer.getResourcesFromRow(mockJsonInput, mockRowMetaInterface, new String[]{ "id", "name" });
        Assert.assertFalse(resources.isEmpty());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(JsonInputMeta.class, consumer.getMetaClass());
    }

    @Test
    public void testCloneAnalyzer() {
        final JsonInputAnalyzer analyzer = new JsonInputAnalyzer();
        // verify that cloneAnalyzer returns an instance that is different from the original
        Assert.assertNotEquals(analyzer, analyzer.cloneAnalyzer());
    }

    @Test
    public void testNewInstance() {
        JsonInputAnalyzer analyzer = new JsonInputAnalyzer();
        Assert.assertTrue(analyzer.newInstance().getClass().equals(JsonInputAnalyzer.class));
    }
}

