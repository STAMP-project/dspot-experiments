/**
 * ******************************************************************************
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
 */
package org.pentaho.di.trans.steps.xmloutput;


import DictionaryConst.NODE_TYPE_FILE;
import DictionaryConst.NODE_TYPE_FILE_FIELD;
import java.util.Collection;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.metaverse.api.IComponentDescriptor;
import org.pentaho.metaverse.api.IMetaverseBuilder;
import org.pentaho.metaverse.api.IMetaverseNode;
import org.pentaho.metaverse.api.INamespace;
import org.pentaho.metaverse.api.MetaverseObjectFactory;
import org.pentaho.metaverse.api.model.IExternalResourceInfo;


@RunWith(MockitoJUnitRunner.class)
public class XMLOutputStepAnalyzerTest {
    private XMLOutputStepAnalyzer analyzer;

    @Mock
    private XMLOutput mockXMLOutput;

    @Mock
    private XMLOutputMeta meta;

    @Mock
    private XMLOutputData data;

    @Mock
    IMetaverseNode node;

    @Mock
    IMetaverseBuilder mockBuilder;

    @Mock
    private INamespace mockNamespace;

    @Mock
    private StepMeta parentStepMeta;

    @Mock
    private TransMeta mockTransMeta;

    IComponentDescriptor descriptor;

    static MetaverseObjectFactory metaverseObjectFactory = new MetaverseObjectFactory();

    @Test
    public void testCustomAnalyze() throws Exception {
        Mockito.when(meta.getMainElement()).thenReturn("main");
        Mockito.when(meta.getRepeatElement()).thenReturn("repeat");
        analyzer.customAnalyze(meta, node);
        Mockito.verify(node).setProperty("parentnode", "main");
        Mockito.verify(node).setProperty("rownode", "repeat");
    }

    @Test
    public void testGetSupportedSteps() {
        XMLOutputStepAnalyzer analyzer = new XMLOutputStepAnalyzer();
        Set<Class<? extends BaseStepMeta>> types = analyzer.getSupportedSteps();
        Assert.assertNotNull(types);
        Assert.assertEquals(types.size(), 1);
        Assert.assertTrue(types.contains(XMLOutputMeta.class));
    }

    @Test
    public void testGetOutputResourceFields() throws Exception {
        XMLField[] outputFields = new XMLField[2];
        XMLField field1 = Mockito.mock(XMLField.class);
        XMLField field2 = Mockito.mock(XMLField.class);
        outputFields[0] = field1;
        outputFields[1] = field2;
        Mockito.when(field1.getFieldName()).thenReturn("field1");
        Mockito.when(field2.getFieldName()).thenReturn("field2");
        Mockito.when(meta.getOutputFields()).thenReturn(outputFields);
        Set<String> outputResourceFields = analyzer.getOutputResourceFields(meta);
        Assert.assertEquals(outputFields.length, outputResourceFields.size());
        for (XMLField outputField : outputFields) {
            Assert.assertTrue(outputResourceFields.contains(outputField.getFieldName()));
        }
    }

    @Test
    public void testXMLOutputExternalResourceConsumer() throws Exception {
        XMLOutputExternalResourceConsumer consumer = new XMLOutputExternalResourceConsumer();
        StepMeta meta = new StepMeta("test", this.meta);
        StepMeta spyMeta = Mockito.spy(meta);
        Mockito.when(this.meta.getParentStepMeta()).thenReturn(spyMeta);
        Mockito.when(spyMeta.getParentTransMeta()).thenReturn(mockTransMeta);
        Mockito.when(this.meta.getFileName()).thenReturn(null);
        String[] filePaths = new String[]{ "/path/to/file1", "/another/path/to/file2" };
        Mockito.when(this.meta.getFiles(Mockito.any(VariableSpace.class))).thenReturn(filePaths);
        Assert.assertFalse(consumer.isDataDriven(this.meta));
        Collection<IExternalResourceInfo> resources = consumer.getResourcesFromMeta(this.meta);
        Assert.assertFalse(resources.isEmpty());
        Assert.assertEquals(2, resources.size());
        Mockito.when(this.meta.getExtension()).thenReturn("txt");
        Assert.assertEquals(XMLOutputMeta.class, consumer.getMetaClass());
    }

    @Test
    public void testCreateResourceNode() throws Exception {
        IExternalResourceInfo res = Mockito.mock(IExternalResourceInfo.class);
        Mockito.when(res.getName()).thenReturn("file:///Users/home/tmp/xyz.xml");
        IMetaverseNode resourceNode = analyzer.createResourceNode(res);
        Assert.assertNotNull(resourceNode);
        Assert.assertEquals(NODE_TYPE_FILE, resourceNode.getType());
    }

    @Test
    public void testGetResourceInputNodeType() throws Exception {
        Assert.assertNull(analyzer.getResourceInputNodeType());
    }

    @Test
    public void testGetResourceOutputNodeType() throws Exception {
        Assert.assertEquals(NODE_TYPE_FILE_FIELD, analyzer.getResourceOutputNodeType());
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
    public void testGetUsedFields() throws Exception {
        Assert.assertNull(analyzer.getUsedFields(meta));
    }
}

