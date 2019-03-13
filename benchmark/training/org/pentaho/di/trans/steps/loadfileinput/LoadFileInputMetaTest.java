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
package org.pentaho.di.trans.steps.loadfileinput;


import java.io.StringReader;
import java.util.Random;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


/**
 * User: Dzmitry Stsiapanau Date: 12/17/13 Time: 3:11 PM
 */
public class LoadFileInputMetaTest implements InitializerInterface<StepMetaInterface> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    LoadSaveTester loadSaveTester;

    String xmlOrig = "    " + (((((((("<include>N</include>    <include_field/>    <rownum>N</rownum>   " + " <addresultfile>Y</addresultfile>    <IsIgnoreEmptyFile>N</IsIgnoreEmptyFile>   ") + " <IsIgnoreMissingPath>N</IsIgnoreMissingPath>    <rownum_field/>   ") + " <encoding/>    <file>      <name>D:\\DZMITRY</name>      <filemask>*/</filemask>     ") + " <exclude_filemask>/***</exclude_filemask>      <file_required>N</file_required>     ") + " <include_subfolders>N</include_subfolders>      </file>    <fields>      </fields>   ") + " <limit>0</limit>    <IsInFields>N</IsInFields>    <DynamicFilenameField/>   ") + " <shortFileFieldName/>    <pathFieldName/>    <hiddenFieldName/>    <lastModificationTimeFieldName/>   ") + " <uriNameFieldName/>    <rootUriNameFieldName/>    <extensionFieldName/>");

    @Test
    public void testGetXML() throws Exception {
        LoadFileInputMeta testMeta = createMeta();
        String xml = testMeta.getXML();
        Assert.assertEquals(xmlOrig.replaceAll("\n", "").replaceAll("\r", ""), xml.replaceAll("\n", "").replaceAll("\r", ""));
    }

    @Test
    public void testLoadXML() throws Exception {
        LoadFileInputMeta origMeta = createMeta();
        LoadFileInputMeta testMeta = new LoadFileInputMeta();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader((("<step>" + (xmlOrig)) + "</step>"))));
        IMetaStore metaStore = null;
        testMeta.loadXML(doc.getFirstChild(), null, metaStore);
        Assert.assertEquals(origMeta, testMeta);
    }

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public class LoadFileInputFieldLoadSaveValidator implements FieldLoadSaveValidator<LoadFileInputField> {
        final Random rand = new Random();

        @Override
        public LoadFileInputField getTestObject() {
            LoadFileInputField rtn = new LoadFileInputField();
            rtn.setCurrencySymbol(UUID.randomUUID().toString());
            rtn.setDecimalSymbol(UUID.randomUUID().toString());
            rtn.setFormat(UUID.randomUUID().toString());
            rtn.setGroupSymbol(UUID.randomUUID().toString());
            rtn.setName(UUID.randomUUID().toString());
            rtn.setElementType(rand.nextInt(2));
            rtn.setTrimType(rand.nextInt(4));
            rtn.setType(rand.nextInt(5));
            rtn.setPrecision(rand.nextInt(9));
            rtn.setRepeated(rand.nextBoolean());
            rtn.setLength(rand.nextInt(50));
            return rtn;
        }

        @Override
        public boolean validateTestObject(LoadFileInputField testObject, Object actualObj) {
            if (!(actualObj instanceof LoadFileInputField)) {
                return false;
            }
            LoadFileInputField actual = ((LoadFileInputField) (actualObj));
            boolean tst1 = actual.getXML().equals(testObject.getXML());
            LoadFileInputField aClone = ((LoadFileInputField) (testObject.clone()));
            boolean tst2 = actual.getXML().equals(aClone.getXML());
            return tst1 && tst2;
        }
    }
}

