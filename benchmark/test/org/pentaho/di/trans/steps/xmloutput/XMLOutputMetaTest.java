/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
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
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.xmloutput;


import Const.XML_ENCODING;
import XMLField.ContentType.Element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.vfs2.FileObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.resource.ResourceNamingInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;


public class XMLOutputMetaTest {
    @Test
    public void testLoadAndGetXml() throws Exception {
        XMLOutputMeta xmlOutputMeta = new XMLOutputMeta();
        Node stepnode = getTestNode();
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        IMetaStore metaStore = Mockito.mock(IMetaStore.class);
        xmlOutputMeta.loadXML(stepnode, Collections.singletonList(dbMeta), metaStore);
        assertXmlOutputMeta(xmlOutputMeta);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testReadRep() throws Exception {
        XMLOutputMeta xmlOutputMeta = new XMLOutputMeta();
        Repository rep = Mockito.mock(Repository.class);
        IMetaStore metastore = Mockito.mock(IMetaStore.class);
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        String encoding = "UTF-8";
        String namespace = "";
        String mainElement = "rows";
        String repeatElement = "row";
        String fileName = "repFileName";
        StringObjectId oid = new StringObjectId("oid");
        String fileExtension = "repxml";
        boolean servletOutput = true;
        boolean newFile = true;
        long split = 100L;
        boolean addStepNbr = false;
        boolean addDate = false;
        boolean addTime = true;
        boolean specifyFormat = true;
        boolean omitNull = false;
        String dateTimeFormat = "yyyyMMdd";
        boolean addToResult = true;
        boolean zipped = true;
        String contentType = "Element";
        String fieldName = "aField";
        String fieldElement = "field";
        String fieldType = "String";
        long fieldLength = 20L;
        long fieldPrecision = 0L;
        Mockito.when(rep.getStepAttributeString(oid, "encoding")).thenReturn(encoding);
        Mockito.when(rep.getStepAttributeString(oid, "name_space")).thenReturn(namespace);
        Mockito.when(rep.getStepAttributeString(oid, "xml_main_element")).thenReturn(mainElement);
        Mockito.when(rep.getStepAttributeString(oid, "xml_repeat_element")).thenReturn(repeatElement);
        Mockito.when(rep.getStepAttributeString(oid, "file_name")).thenReturn(fileName);
        Mockito.when(rep.getStepAttributeString(oid, "file_extention")).thenReturn(fileExtension);
        Mockito.when(rep.getStepAttributeBoolean(oid, "file_servlet_output")).thenReturn(servletOutput);
        Mockito.when(rep.getStepAttributeBoolean(oid, "do_not_open_newfile_init")).thenReturn(newFile);
        Mockito.when(rep.getStepAttributeInteger(oid, "file_split")).thenReturn(split);
        Mockito.when(rep.getStepAttributeBoolean(oid, "file_add_stepnr")).thenReturn(addStepNbr);
        Mockito.when(rep.getStepAttributeBoolean(oid, "file_add_date")).thenReturn(addDate);
        Mockito.when(rep.getStepAttributeBoolean(oid, "file_add_time")).thenReturn(addTime);
        Mockito.when(rep.getStepAttributeBoolean(oid, "SpecifyFormat")).thenReturn(specifyFormat);
        Mockito.when(rep.getStepAttributeBoolean(oid, "omit_null_values")).thenReturn(omitNull);
        Mockito.when(rep.getStepAttributeString(oid, "date_time_format")).thenReturn(dateTimeFormat);
        Mockito.when(rep.getStepAttributeBoolean(oid, "add_to_result_filenames")).thenReturn(addToResult);
        Mockito.when(rep.getStepAttributeBoolean(oid, "file_zipped")).thenReturn(zipped);
        Mockito.when(rep.countNrStepAttributes(oid, "field_name")).thenReturn(1);
        Mockito.when(rep.getStepAttributeString(oid, 0, "field_content_type")).thenReturn(contentType);
        Mockito.when(rep.getStepAttributeString(oid, 0, "field_name")).thenReturn(fieldName);
        Mockito.when(rep.getStepAttributeString(oid, 0, "field_element")).thenReturn(fieldElement);
        Mockito.when(rep.getStepAttributeString(oid, 0, "field_type")).thenReturn(fieldType);
        Mockito.when(rep.getStepAttributeString(oid, 0, "field_format")).thenReturn(null);
        Mockito.when(rep.getStepAttributeString(oid, 0, "field_currency")).thenReturn(null);
        Mockito.when(rep.getStepAttributeString(oid, 0, "field_decimal")).thenReturn(null);
        Mockito.when(rep.getStepAttributeString(oid, 0, "field_group")).thenReturn(null);
        Mockito.when(rep.getStepAttributeString(oid, 0, "field_nullif")).thenReturn(null);
        Mockito.when(rep.getStepAttributeInteger(oid, 0, "field_length")).thenReturn(fieldLength);
        Mockito.when(rep.getStepAttributeInteger(oid, 0, "field_precision")).thenReturn(fieldPrecision);
        xmlOutputMeta.readRep(rep, metastore, oid, Collections.singletonList(dbMeta));
        Assert.assertEquals(fileName, xmlOutputMeta.getFileName());
        Assert.assertTrue(xmlOutputMeta.isDoNotOpenNewFileInit());
        Assert.assertTrue(xmlOutputMeta.isServletOutput());
        Assert.assertEquals(fileExtension, xmlOutputMeta.getExtension());
        Assert.assertFalse(xmlOutputMeta.isStepNrInFilename());
        Assert.assertFalse(xmlOutputMeta.isDateInFilename());
        Assert.assertTrue(xmlOutputMeta.isTimeInFilename());
        Assert.assertTrue(xmlOutputMeta.isSpecifyFormat());
        Assert.assertEquals(dateTimeFormat, xmlOutputMeta.getDateTimeFormat());
        Assert.assertTrue(xmlOutputMeta.isAddToResultFiles());
        Assert.assertTrue(xmlOutputMeta.isZipped());
        Assert.assertEquals(encoding, xmlOutputMeta.getEncoding());
        Assert.assertTrue(StringUtil.isEmpty(xmlOutputMeta.getNameSpace()));
        Assert.assertEquals(mainElement, xmlOutputMeta.getMainElement());
        Assert.assertEquals(repeatElement, xmlOutputMeta.getRepeatElement());
        Assert.assertEquals(split, xmlOutputMeta.getSplitEvery());
        Assert.assertFalse(xmlOutputMeta.isOmitNullValues());
        XMLField[] outputFields = xmlOutputMeta.getOutputFields();
        Assert.assertEquals(1, outputFields.length);
        Assert.assertEquals(fieldName, outputFields[0].getFieldName());
        Assert.assertEquals(Element, outputFields[0].getContentType());
        Assert.assertEquals(fieldElement, outputFields[0].getElementName());
        Assert.assertEquals(fieldLength, outputFields[0].getLength());
        Assert.assertEquals(fieldPrecision, outputFields[0].getPrecision());
        Mockito.reset(rep, metastore);
        StringObjectId transid = new StringObjectId("transid");
        xmlOutputMeta.saveRep(rep, metastore, transid, oid);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "encoding", encoding);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "name_space", namespace);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "xml_main_element", mainElement);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "xml_repeat_element", repeatElement);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "file_name", fileName);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "file_extention", fileExtension);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "file_servlet_output", servletOutput);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "do_not_open_newfile_init", newFile);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "file_split", split);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "file_add_stepnr", addStepNbr);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "file_add_date", addDate);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "file_add_time", addTime);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "SpecifyFormat", specifyFormat);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "omit_null_values", omitNull);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "date_time_format", dateTimeFormat);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "add_to_result_filenames", addToResult);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "file_zipped", zipped);
        Mockito.verify(rep).saveStepAttribute(transid, oid, 0, "field_content_type", contentType);
        Mockito.verify(rep).saveStepAttribute(transid, oid, 0, "field_name", fieldName);
        Mockito.verify(rep).saveStepAttribute(transid, oid, 0, "field_element", fieldElement);
        Mockito.verify(rep).saveStepAttribute(transid, oid, 0, "field_type", fieldType);
        Mockito.verify(rep).saveStepAttribute(transid, oid, 0, "field_format", null);
        Mockito.verify(rep).saveStepAttribute(transid, oid, 0, "field_currency", null);
        Mockito.verify(rep).saveStepAttribute(transid, oid, 0, "field_decimal", null);
        Mockito.verify(rep).saveStepAttribute(transid, oid, 0, "field_group", null);
        Mockito.verify(rep).saveStepAttribute(transid, oid, 0, "field_nullif", null);
        Mockito.verify(rep).saveStepAttribute(transid, oid, 0, "field_length", fieldLength);
        Mockito.verify(rep).saveStepAttribute(transid, oid, 0, "field_precision", fieldPrecision);
        Mockito.verifyNoMoreInteractions(rep, metastore);
    }

    @Test
    public void testGetNewline() throws Exception {
        XMLOutputMeta xmlOutputMeta = new XMLOutputMeta();
        Assert.assertEquals("\r\n", xmlOutputMeta.getNewLine("DOS"));
        Assert.assertEquals("\n", xmlOutputMeta.getNewLine("UNIX"));
        Assert.assertEquals(System.getProperty("line.separator"), xmlOutputMeta.getNewLine(null));
    }

    @Test
    public void testClone() throws Exception {
        XMLOutputMeta xmlOutputMeta = new XMLOutputMeta();
        Node stepnode = getTestNode();
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        IMetaStore metaStore = Mockito.mock(IMetaStore.class);
        xmlOutputMeta.loadXML(stepnode, Collections.singletonList(dbMeta), metaStore);
        XMLOutputMeta cloned = ((XMLOutputMeta) (xmlOutputMeta.clone()));
        Assert.assertNotSame(cloned, xmlOutputMeta);
        assertXmlOutputMeta(cloned);
    }

    @Test
    public void testSetDefault() throws Exception {
        XMLOutputMeta xmlOutputMeta = new XMLOutputMeta();
        xmlOutputMeta.setDefault();
        Assert.assertEquals("file", xmlOutputMeta.getFileName());
        Assert.assertEquals("xml", xmlOutputMeta.getExtension());
        Assert.assertFalse(xmlOutputMeta.isStepNrInFilename());
        Assert.assertFalse(xmlOutputMeta.isDoNotOpenNewFileInit());
        Assert.assertFalse(xmlOutputMeta.isDateInFilename());
        Assert.assertFalse(xmlOutputMeta.isTimeInFilename());
        Assert.assertFalse(xmlOutputMeta.isAddToResultFiles());
        Assert.assertFalse(xmlOutputMeta.isZipped());
        Assert.assertEquals(0, xmlOutputMeta.getSplitEvery());
        Assert.assertEquals(XML_ENCODING, xmlOutputMeta.getEncoding());
        Assert.assertEquals("", xmlOutputMeta.getNameSpace());
        Assert.assertNull(xmlOutputMeta.getDateTimeFormat());
        Assert.assertFalse(xmlOutputMeta.isSpecifyFormat());
        Assert.assertFalse(xmlOutputMeta.isOmitNullValues());
        Assert.assertEquals("Rows", xmlOutputMeta.getMainElement());
        Assert.assertEquals("Row", xmlOutputMeta.getRepeatElement());
    }

    @Test
    public void testGetFiles() throws Exception {
        XMLOutputMeta xmlOutputMeta = new XMLOutputMeta();
        xmlOutputMeta.setDefault();
        xmlOutputMeta.setStepNrInFilename(true);
        xmlOutputMeta.setSplitEvery(100);
        xmlOutputMeta.setSpecifyFormat(true);
        xmlOutputMeta.setDateTimeFormat("99");
        String[] files = xmlOutputMeta.getFiles(new Variables());
        Assert.assertEquals(10, files.length);
        Assert.assertArrayEquals(new String[]{ "file99_0_00001.xml", "file99_0_00002.xml", "file99_0_00003.xml", "file99_1_00001.xml", "file99_1_00002.xml", "file99_1_00003.xml", "file99_2_00001.xml", "file99_2_00002.xml", "file99_2_00003.xml", "..." }, files);
    }

    @Test
    public void testGetFields() throws Exception {
        XMLOutputMeta xmlOutputMeta = new XMLOutputMeta();
        xmlOutputMeta.setDefault();
        XMLField xmlField = new XMLField();
        xmlField.setFieldName("aField");
        xmlField.setLength(10);
        xmlField.setPrecision(3);
        xmlOutputMeta.setOutputFields(new XMLField[]{ xmlField });
        RowMetaInterface row = Mockito.mock(RowMetaInterface.class);
        RowMetaInterface rmi = Mockito.mock(RowMetaInterface.class);
        StepMeta nextStep = Mockito.mock(StepMeta.class);
        Repository repo = Mockito.mock(Repository.class);
        IMetaStore metastore = Mockito.mock(IMetaStore.class);
        ValueMetaInterface vmi = Mockito.mock(ValueMetaInterface.class);
        Mockito.when(row.searchValueMeta("aField")).thenReturn(vmi);
        xmlOutputMeta.getFields(row, "", new RowMetaInterface[]{ rmi }, nextStep, new Variables(), repo, metastore);
        Mockito.verify(vmi).setLength(10, 3);
    }

    @Test
    public void testLoadXmlException() throws Exception {
        XMLOutputMeta xmlOutputMeta = new XMLOutputMeta();
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        IMetaStore metaStore = Mockito.mock(IMetaStore.class);
        Node stepNode = Mockito.mock(Node.class);
        Mockito.when(stepNode.getChildNodes()).thenThrow(new RuntimeException("some words"));
        try {
            xmlOutputMeta.loadXML(stepNode, Collections.singletonList(dbMeta), metaStore);
        } catch (KettleXMLException e) {
            Assert.assertEquals("some words", e.getCause().getMessage());
        }
    }

    @Test
    public void testReadRepException() throws Exception {
        XMLOutputMeta xmlOutputMeta = new XMLOutputMeta();
        Repository rep = Mockito.mock(Repository.class);
        IMetaStore metastore = Mockito.mock(IMetaStore.class);
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        ObjectId oid = new StringObjectId("oid");
        Mockito.when(rep.getStepAttributeString(oid, "encoding")).thenThrow(new RuntimeException("encoding exception"));
        try {
            xmlOutputMeta.readRep(rep, metastore, oid, Collections.singletonList(dbMeta));
        } catch (KettleException e) {
            Assert.assertEquals("encoding exception", e.getCause().getMessage());
        }
    }

    @Test
    public void testGetRequiredFields() throws Exception {
        XMLOutputMeta xmlOutputMeta = new XMLOutputMeta();
        xmlOutputMeta.setDefault();
        XMLField xmlField = new XMLField();
        xmlField.setFieldName("aField");
        xmlField.setType(1);
        xmlField.setLength(10);
        xmlField.setPrecision(3);
        XMLField xmlField2 = new XMLField();
        xmlField2.setFieldName("bField");
        xmlField2.setType(3);
        xmlField2.setLength(4);
        xmlField2.setPrecision(5);
        xmlOutputMeta.setOutputFields(new XMLField[]{ xmlField, xmlField2 });
        RowMetaInterface requiredFields = xmlOutputMeta.getRequiredFields(new Variables());
        List<ValueMetaInterface> valueMetaList = requiredFields.getValueMetaList();
        Assert.assertEquals(2, valueMetaList.size());
        Assert.assertEquals("aField", valueMetaList.get(0).getName());
        Assert.assertEquals(1, valueMetaList.get(0).getType());
        Assert.assertEquals(10, valueMetaList.get(0).getLength());
        Assert.assertEquals(3, valueMetaList.get(0).getPrecision());
        Assert.assertEquals("bField", valueMetaList.get(1).getName());
        Assert.assertEquals(3, valueMetaList.get(1).getType());
        Assert.assertEquals(4, valueMetaList.get(1).getLength());
        Assert.assertEquals(5, valueMetaList.get(1).getPrecision());
    }

    @Test
    public void testExportResources() throws Exception {
        XMLOutputMeta xmlOutputMeta = new XMLOutputMeta();
        xmlOutputMeta.setDefault();
        ResourceNamingInterface resourceNamingInterface = Mockito.mock(ResourceNamingInterface.class);
        Variables space = new Variables();
        Mockito.when(resourceNamingInterface.nameResource(ArgumentMatchers.any(FileObject.class), ArgumentMatchers.eq(space), ArgumentMatchers.eq(true))).thenReturn("exportFile");
        xmlOutputMeta.exportResources(space, null, resourceNamingInterface, null, null);
        Assert.assertEquals("exportFile", xmlOutputMeta.getFileName());
    }

    @Test
    public void testCheck() throws Exception {
        XMLOutputMeta xmlOutputMeta = new XMLOutputMeta();
        xmlOutputMeta.setDefault();
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        StepMeta stepInfo = Mockito.mock(StepMeta.class);
        RowMetaInterface prev = Mockito.mock(RowMetaInterface.class);
        Repository repos = Mockito.mock(Repository.class);
        IMetaStore metastore = Mockito.mock(IMetaStore.class);
        RowMetaInterface info = Mockito.mock(RowMetaInterface.class);
        ArrayList<CheckResultInterface> remarks = new ArrayList<>();
        xmlOutputMeta.check(remarks, transMeta, stepInfo, prev, new String[]{ "input" }, new String[]{ "output" }, info, new Variables(), repos, metastore);
        Assert.assertEquals(2, remarks.size());
        Assert.assertEquals("Step is receiving info from other steps.", remarks.get(0).getText());
        Assert.assertEquals("File specifications are not checked.", remarks.get(1).getText());
        XMLField xmlField = new XMLField();
        xmlField.setFieldName("aField");
        xmlField.setType(1);
        xmlField.setLength(10);
        xmlField.setPrecision(3);
        xmlOutputMeta.setOutputFields(new XMLField[]{ xmlField });
        Mockito.when(prev.size()).thenReturn(1);
        remarks.clear();
        xmlOutputMeta.check(remarks, transMeta, stepInfo, prev, new String[]{ "input" }, new String[]{ "output" }, info, new Variables(), repos, metastore);
        Assert.assertEquals(4, remarks.size());
        Assert.assertEquals("Step is connected to previous one, receiving 1 fields", remarks.get(0).getText());
        Assert.assertEquals("All output fields are found in the input stream.", remarks.get(1).getText());
        Assert.assertEquals("Step is receiving info from other steps.", remarks.get(2).getText());
        Assert.assertEquals("File specifications are not checked.", remarks.get(3).getText());
    }
}

