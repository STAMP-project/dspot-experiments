/**
 * ******************************************************************************
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
 */
package org.pentaho.di.trans.steps.getxmldata;


import DictionaryConst.NODE_TYPE_FILE;
import DictionaryConst.NODE_TYPE_FILE_FIELD;
import DictionaryConst.NODE_TYPE_TRANS_FIELD;
import DictionaryConst.PROPERTY_KETTLE_TYPE;
import DictionaryConst.PROPERTY_TARGET_STEP;
import ExternalResourceStepAnalyzer.RESOURCE;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.metaverse.api.IAnalysisContext;
import org.pentaho.metaverse.api.IComponentDescriptor;
import org.pentaho.metaverse.api.IMetaverseBuilder;
import org.pentaho.metaverse.api.IMetaverseNode;
import org.pentaho.metaverse.api.INamespace;
import org.pentaho.metaverse.api.MetaverseObjectFactory;
import org.pentaho.metaverse.api.StepField;
import org.pentaho.metaverse.api.analyzer.kettle.ComponentDerivationRecord;
import org.pentaho.metaverse.api.analyzer.kettle.step.StepNodes;
import org.pentaho.metaverse.api.model.IExternalResourceInfo;


/**
 * Created by mburgess on 4/24/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetXMLDataStepAnalyzerTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    GetXMLDataStepAnalyzer analyzer;

    @Mock
    GetXMLDataMeta meta;

    @Mock
    INamespace mockNamespace;

    @Mock
    IMetaverseNode node;

    @Mock
    IMetaverseBuilder builder;

    @Mock
    TransMeta parentTransMeta;

    @Mock
    StepMeta parentStepMeta;

    @Mock
    RowMetaInterface rmi;

    @Mock
    GetXMLData data;

    IComponentDescriptor descriptor;

    static MetaverseObjectFactory metaverseObjectFactory = new MetaverseObjectFactory();

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
        GetXMLDataStepAnalyzer analyzer = new GetXMLDataStepAnalyzer();
        Set<Class<? extends BaseStepMeta>> types = analyzer.getSupportedSteps();
        Assert.assertNotNull(types);
        Assert.assertEquals(types.size(), 1);
        Assert.assertTrue(types.contains(GetXMLDataMeta.class));
    }

    @Test
    public void testGetUsedFields_xmlNotInField() throws Exception {
        Mockito.when(meta.isInFields()).thenReturn(false);
        Set<StepField> usedFields = analyzer.getUsedFields(meta);
        Assert.assertEquals(0, usedFields.size());
    }

    @Test
    public void testGetUsedFields() throws Exception {
        Mockito.when(meta.isInFields()).thenReturn(true);
        Mockito.when(meta.getXMLField()).thenReturn("xml");
        StepNodes inputs = new StepNodes();
        inputs.addNode("previousStep", "xml", node);
        inputs.addNode("previousStep", "otherField", node);
        Mockito.doReturn(inputs).when(analyzer).getInputs();
        Set<StepField> usedFields = analyzer.getUsedFields(meta);
        Assert.assertEquals(1, usedFields.size());
        Assert.assertEquals("xml", usedFields.iterator().next().getFieldName());
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
    public void testCreateOutputFieldNode() throws Exception {
        Mockito.doReturn(builder).when(analyzer).getMetaverseBuilder();
        analyzer.setBaseStepMeta(meta);
        GetXMLDataField[] fields = new GetXMLDataField[2];
        GetXMLDataField field1 = new GetXMLDataField("name");
        GetXMLDataField field2 = new GetXMLDataField("age");
        field1.setXPath("field1/xpath");
        field2.setElementType(1);
        field1.setResultType(1);
        field2.setRepeated(true);
        fields[0] = field1;
        fields[1] = field2;
        Mockito.when(meta.getInputFields()).thenReturn(fields);
        IAnalysisContext context = Mockito.mock(IAnalysisContext.class);
        Mockito.doReturn("thisStepName").when(analyzer).getStepName();
        Mockito.when(node.getLogicalId()).thenReturn("logical id");
        ValueMetaInterface vmi = new ValueMeta("name", 1);
        IMetaverseNode outputFieldNode = analyzer.createOutputFieldNode(context, vmi, RESOURCE, NODE_TYPE_TRANS_FIELD);
        Assert.assertNotNull(outputFieldNode);
        Assert.assertNotNull(outputFieldNode.getProperty(PROPERTY_KETTLE_TYPE));
        Assert.assertEquals(RESOURCE, outputFieldNode.getProperty(PROPERTY_TARGET_STEP));
        Assert.assertEquals("field1/xpath", outputFieldNode.getProperty("xpath"));
        Assert.assertNotNull(outputFieldNode.getProperty("resultType"));
        Assert.assertNotNull(outputFieldNode.getProperty("element"));
        Assert.assertEquals(false, outputFieldNode.getProperty("repeat"));
        // the input node should be added by this step
        Mockito.verify(builder).addNode(outputFieldNode);
    }

    @Test
    public void testGetInputRowMetaInterfaces_isInFields() throws Exception {
        Mockito.when(parentTransMeta.getPrevStepNames(parentStepMeta)).thenReturn(null);
        RowMetaInterface rowMetaInterface = Mockito.mock(RowMetaInterface.class);
        Mockito.doReturn(rowMetaInterface).when(analyzer).getOutputFields(meta);
        Mockito.when(meta.isInFields()).thenReturn(true);
        Mockito.when(meta.getIsAFile()).thenReturn(false);
        Mockito.when(meta.isReadUrl()).thenReturn(false);
        Map<String, RowMetaInterface> rowMetaInterfaces = analyzer.getInputRowMetaInterfaces(meta);
        Assert.assertNotNull(rowMetaInterfaces);
        Assert.assertEquals(0, rowMetaInterfaces.size());
    }

    @Test
    public void testGetInputRowMetaInterfaces_isNotInField() throws Exception {
        Map<String, RowMetaInterface> inputs = new HashMap<>();
        RowMetaInterface inputRmi = Mockito.mock(RowMetaInterface.class);
        List<ValueMetaInterface> vmis = new ArrayList<>();
        ValueMetaInterface vmi = new ValueMeta("filename");
        vmis.add(vmi);
        Mockito.when(inputRmi.getValueMetaList()).thenReturn(vmis);
        inputs.put("test", inputRmi);
        Mockito.doReturn(inputs).when(analyzer).getInputFields(meta);
        Mockito.when(parentTransMeta.getPrevStepNames(parentStepMeta)).thenReturn(null);
        RowMetaInterface rowMetaInterface = new RowMeta();
        rowMetaInterface.addValueMeta(vmi);
        ValueMetaInterface vmi2 = new ValueMeta("otherField");
        rowMetaInterface.addValueMeta(vmi2);
        Mockito.doReturn(rowMetaInterface).when(analyzer).getOutputFields(meta);
        Mockito.when(meta.isInFields()).thenReturn(false);
        Mockito.when(meta.getIsAFile()).thenReturn(false);
        Mockito.when(meta.isReadUrl()).thenReturn(false);
        Map<String, RowMetaInterface> rowMetaInterfaces = analyzer.getInputRowMetaInterfaces(meta);
        Assert.assertNotNull(rowMetaInterfaces);
        Assert.assertEquals(2, rowMetaInterfaces.size());
        RowMetaInterface metaInterface = rowMetaInterfaces.get(RESOURCE);
        // the row meta interface should only have 1 value meta in it, and it should NOT be filename
        Assert.assertEquals(1, metaInterface.size());
        Assert.assertEquals("otherField", metaInterface.getFieldNames()[0]);
    }

    @Test
    public void testGetChangeRecords() throws Exception {
        Mockito.when(meta.isInFields()).thenReturn(true);
        Mockito.when(meta.getIsAFile()).thenReturn(false);
        Mockito.when(meta.isReadUrl()).thenReturn(false);
        Mockito.when(meta.getXMLField()).thenReturn("xml");
        analyzer.setBaseStepMeta(meta);
        GetXMLDataField[] fields = new GetXMLDataField[2];
        GetXMLDataField field1 = new GetXMLDataField("name");
        GetXMLDataField field2 = new GetXMLDataField("age");
        field1.setXPath("field1/xpath");
        field2.setElementType(1);
        field1.setResultType(1);
        field2.setRepeated(true);
        fields[0] = field1;
        fields[1] = field2;
        Mockito.when(meta.getInputFields()).thenReturn(fields);
        StepNodes inputs = new StepNodes();
        inputs.addNode("previousStep", "xml", node);
        Mockito.doReturn(inputs).when(analyzer).getInputs();
        Set<ComponentDerivationRecord> changeRecords = analyzer.getChangeRecords(meta);
        Assert.assertNotNull(changeRecords);
        Assert.assertEquals(2, changeRecords.size());
    }

    @Test
    public void testCustomAnalyze() throws Exception {
        Mockito.when(meta.getLoopXPath()).thenReturn("file/xpath/name");
        analyzer.customAnalyze(meta, node);
        Mockito.verify(node).setProperty("loopXPath", "file/xpath/name");
    }

    @Test
    public void testGetXMLDataExternalResourceConsumer() throws Exception {
        GetXMLDataExternalResourceConsumer consumer = new GetXMLDataExternalResourceConsumer();
        StepMeta spyMeta = Mockito.spy(new StepMeta("test", meta));
        Mockito.when(meta.getParentStepMeta()).thenReturn(spyMeta);
        Mockito.when(spyMeta.getParentTransMeta()).thenReturn(parentTransMeta);
        Mockito.when(data.getStepMetaInterface()).thenReturn(meta);
        Mockito.when(meta.isInFields()).thenReturn(false);
        String[] filePaths = new String[]{ "/path/to/file1", "/another/path/to/file2" };
        Mockito.when(meta.getFileName()).thenReturn(filePaths);
        Mockito.when(parentTransMeta.environmentSubstitute(ArgumentMatchers.any(String[].class))).thenReturn(filePaths);
        Assert.assertFalse(consumer.isDataDriven(meta));
        Collection<IExternalResourceInfo> resources = consumer.getResourcesFromMeta(meta);
        Assert.assertFalse(resources.isEmpty());
        Assert.assertEquals(2, resources.size());
        Mockito.when(meta.isInFields()).thenReturn(true);
        Mockito.when(meta.getIsAFile()).thenReturn(true);
        Assert.assertTrue(consumer.isDataDriven(meta));
        Assert.assertTrue(consumer.getResourcesFromMeta(meta).isEmpty());
        Mockito.when(rmi.getString(Mockito.any(Object[].class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn("/path/to/row/file");
        resources = consumer.getResourcesFromRow(data, rmi, new String[]{ "id", "name" });
        Assert.assertFalse(resources.isEmpty());
        Assert.assertEquals(1, resources.size());
        Mockito.when(rmi.getString(Mockito.any(Object[].class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenThrow(new KettleValueException());
        resources = consumer.getResourcesFromRow(data, rmi, new String[]{ "id", "name" });
        Assert.assertTrue(resources.isEmpty());
        Assert.assertEquals(GetXMLDataMeta.class, consumer.getMetaClass());
    }

    @Test
    public void testCloneAnalyzer() {
        final GetXMLDataStepAnalyzer analyzer = new GetXMLDataStepAnalyzer();
        // verify that cloneAnalyzer returns an instance that is different from the original
        Assert.assertNotEquals(analyzer, analyzer.cloneAnalyzer());
    }

    @Test
    public void testNewInstance() {
        GetXMLDataStepAnalyzer analyzer = new GetXMLDataStepAnalyzer();
        Assert.assertTrue(analyzer.newInstance().getClass().equals(GetXMLDataStepAnalyzer.class));
    }
}

