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
package org.pentaho.di.trans.steps.systemdata;


import java.io.StringReader;
import java.util.Random;
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
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 * User: Dzmitry Stsiapanau Date: 1/20/14 Time: 3:04 PM
 */
public class SystemDataMetaTest implements InitializerInterface<StepMetaInterface> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    LoadSaveTester loadSaveTester;

    Class<SystemDataMeta> testMetaClass = SystemDataMeta.class;

    SystemDataMeta expectedSystemDataMeta;

    String expectedXML = "    <fields>\n" + (((((((("      <field>\n" + "        <name>hostname_real</name>\n") + "        <type>Hostname real</type>\n") + "        </field>\n") + "      <field>\n") + "        <name>hostname</name>\n") + "        <type>Hostname</type>\n") + "        </field>\n") + "      </fields>\n");

    @Test
    public void testLoadXML() throws Exception {
        SystemDataMeta systemDataMeta = new SystemDataMeta();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(expectedXML)));
        Node node = document;
        IMetaStore store = null;
        systemDataMeta.loadXML(node, null, store);
        Assert.assertEquals(expectedSystemDataMeta, systemDataMeta);
    }

    @Test
    public void testGetXML() throws Exception {
        String generatedXML = expectedSystemDataMeta.getXML();
        Assert.assertEquals(expectedXML.replaceAll("\n", "").replaceAll("\r", ""), generatedXML.replaceAll("\n", "").replaceAll("\r", ""));
    }

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public class SystemDataTypesLoadSaveValidator implements FieldLoadSaveValidator<SystemDataTypes> {
        final Random rand = new Random();

        @Override
        public SystemDataTypes getTestObject() {
            SystemDataTypes[] allTypes = SystemDataTypes.values();
            return allTypes[rand.nextInt(allTypes.length)];
        }

        @Override
        public boolean validateTestObject(SystemDataTypes testObject, Object actual) {
            if (!(actual instanceof SystemDataTypes)) {
                return false;
            }
            SystemDataTypes actualInput = ((SystemDataTypes) (actual));
            return testObject.toString().equals(actualInput.toString());
        }
    }
}

