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
package org.pentaho.di.trans.steps.jsonoutput.analyzer;


import DictionaryConst.NODE_TYPE_FILE_FIELD;
import java.util.Collection;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.jsonoutput.JsonOutput;
import org.pentaho.di.trans.steps.jsonoutput.JsonOutputMeta;
import org.pentaho.metaverse.api.IMetaverseBuilder;
import org.pentaho.metaverse.api.IMetaverseObjectFactory;
import org.pentaho.metaverse.api.INamespace;
import org.pentaho.metaverse.api.model.IExternalResourceInfo;


@RunWith(MockitoJUnitRunner.class)
public class JsonOutputAnalyzerTest {
    private JsonOutputAnalyzer analyzer;

    @Mock
    private JsonOutput mockJsonOutput;

    @Mock
    private JsonOutputMeta meta;

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
    public void testGetResourceOutputNodeType() throws Exception {
        Assert.assertEquals(NODE_TYPE_FILE_FIELD, analyzer.getResourceOutputNodeType());
    }

    @Test
    public void testGetResourceInputNodeType() throws Exception {
        Assert.assertNull(analyzer.getResourceInputNodeType());
    }

    @Test
    public void testIsOutput() throws Exception {
        Assert.assertTrue(analyzer.isOutput());
    }

    @Test
    public void testIsInput() throws Exception {
        Assert.assertFalse(analyzer.isInput());
    }

    @Test
    public void testGetSupportedSteps() {
        JsonOutputAnalyzer analyzer = new JsonOutputAnalyzer();
        Set<Class<? extends BaseStepMeta>> types = analyzer.getSupportedSteps();
        Assert.assertNotNull(types);
        Assert.assertEquals(types.size(), 1);
        Assert.assertTrue(types.contains(JsonOutputMeta.class));
    }

    @Test
    public void testJsonOutputExternalResourceConsumer() throws Exception {
        JsonOutputExternalResourceConsumer consumer = new JsonOutputExternalResourceConsumer();
        Assert.assertEquals(JsonOutputMeta.class, consumer.getMetaClass());
        StepMeta spyMeta = Mockito.spy(new StepMeta("test", meta));
        Mockito.when(meta.getParentStepMeta()).thenReturn(spyMeta);
        Mockito.when(spyMeta.getParentTransMeta()).thenReturn(transMeta);
        Mockito.when(meta.getFileName()).thenReturn(null);
        String[] outputFilePaths = new String[]{ "/path/to/file1", "/another/path/to/file2" };
        Mockito.when(meta.getFilePaths(true)).thenReturn(outputFilePaths);
        Mockito.when(meta.getFilePaths(false)).thenReturn(outputFilePaths);
        Mockito.when(meta.writesToFile()).thenReturn(false);
        Assert.assertFalse(consumer.isDataDriven(meta));
        Collection<IExternalResourceInfo> resources = consumer.getResourcesFromMeta(meta);
        Assert.assertTrue(resources.isEmpty());
        Mockito.when(meta.writesToFile()).thenReturn(true);
        resources = consumer.getResourcesFromMeta(meta);
        Assert.assertFalse(resources.isEmpty());
        Assert.assertEquals(2, resources.size());
    }

    @Test
    public void testCloneAnalyzer() {
        final JsonOutputAnalyzer analyzer = new JsonOutputAnalyzer();
        // verify that cloneAnalyzer returns an instance that is different from the original
        Assert.assertNotEquals(analyzer, analyzer.cloneAnalyzer());
    }

    @Test
    public void testNewInstance() {
        JsonOutputAnalyzer analyzer = new JsonOutputAnalyzer();
        Assert.assertTrue(analyzer.newInstance().getClass().equals(JsonOutputAnalyzer.class));
    }
}

