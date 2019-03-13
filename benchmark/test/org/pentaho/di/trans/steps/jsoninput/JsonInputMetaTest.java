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
package org.pentaho.di.trans.steps.jsoninput;


import JsonInputMeta.InputFiles;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.metastore.api.IMetaStore;


/**
 * Created by bmorrise on 3/22/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonInputMetaTest {
    private static final List<DatabaseMeta> DATABASES_LIST = Collections.emptyList();

    public static final String DATA = "data";

    public static final String NAME = "name";

    private static final Pattern CLEAN_NODES = Pattern.compile("(<step>)[\\r|\\n]+|</step>");

    JsonInputMeta jsonInputMeta;

    @Mock
    RowMetaInterface rowMeta;

    @Mock
    RowMetaInterface rowMetaInterfaceItem;

    @Mock
    StepMeta nextStep;

    @Mock
    VariableSpace space;

    @Mock
    Repository repository;

    @Mock
    IMetaStore metaStore;

    @Mock
    InputFiles inputFiles;

    @Mock
    JsonInputField inputField;

    @Test
    public void getFieldsRemoveSourceField() throws Exception {
        RowMetaInterface[] info = new RowMetaInterface[1];
        info[0] = rowMetaInterfaceItem;
        jsonInputMeta.setRemoveSourceField(true);
        jsonInputMeta.setFieldValue(JsonInputMetaTest.DATA);
        jsonInputMeta.setInFields(true);
        Mockito.when(rowMeta.indexOfValue(JsonInputMetaTest.DATA)).thenReturn(0);
        jsonInputMeta.getFields(rowMeta, JsonInputMetaTest.NAME, info, nextStep, space, repository, metaStore);
        Mockito.verify(rowMeta).removeValueMeta(0);
    }

    @Test
    public void verifyReadingRepoSetsAcceptFilenames() throws Exception {
        ObjectId objectId = () -> "id";
        Mockito.when(repository.getStepAttributeBoolean(objectId, "IsInFields")).thenReturn(true);
        jsonInputMeta.readRep(repository, null, objectId, null);
        Assert.assertTrue(jsonInputMeta.isInFields());
        Assert.assertTrue(jsonInputMeta.inputFiles.acceptingFilenames);
    }

    @Test
    public void testGetXmlOfDefaultMeta_defaultPathLeafToNull_Y() throws Exception {
        jsonInputMeta = new JsonInputMeta();
        jsonInputMeta.setDefault();
        String xml = jsonInputMeta.getXML();
        Assert.assertEquals(expectedMeta("step_default.xml"), xml);
    }

    @Test
    public void testGetXmlOfMeta_defaultPathLeafToNull_N() throws Exception {
        jsonInputMeta = new JsonInputMeta();
        jsonInputMeta.setDefault();
        jsonInputMeta.setDefaultPathLeafToNull(false);
        String xml = jsonInputMeta.getXML();
        Assert.assertEquals(expectedMeta("step_defaultPathLeafToNull_N.xml"), xml);
    }

    // Loading step meta from the step xml where DefaultPathLeafToNull=N
    @Test
    public void testMetaLoad_DefaultPathLeafToNull_Is_N() throws KettleXMLException {
        jsonInputMeta = new JsonInputMeta();
        jsonInputMeta.loadXML(loadStep("step_defaultPathLeafToNull_N.xml"), JsonInputMetaTest.DATABASES_LIST, metaStore);
        Assert.assertEquals("Option.DEFAULT_PATH_LEAF_TO_NULL ", false, jsonInputMeta.isDefaultPathLeafToNull());
    }

    // Loading step meta from default step xml. In this case DefaultPathLeafToNull=Y in xml.
    @Test
    public void testDefaultMetaLoad_DefaultPathLeafToNull_Is_Y() throws KettleXMLException {
        jsonInputMeta = new JsonInputMeta();
        jsonInputMeta.loadXML(loadStep("step_default.xml"), JsonInputMetaTest.DATABASES_LIST, metaStore);
        Assert.assertEquals("Option.DEFAULT_PATH_LEAF_TO_NULL ", true, jsonInputMeta.isDefaultPathLeafToNull());
    }

    // Loading step meta from the step xml that was created before PDI-17060 fix. In this case xml contains no
    // DefaultPathLeafToNull node at all.
    // For backward compatibility in this case we think that the option is set to default value - Y.
    @Test
    public void testMetaLoadAsDefault_NoDefaultPathLeafToNull_In_Xml() throws KettleXMLException {
        jsonInputMeta = new JsonInputMeta();
        jsonInputMeta.loadXML(loadStep("step_no_defaultPathLeafToNull_node.xml"), JsonInputMetaTest.DATABASES_LIST, metaStore);
        Assert.assertEquals("Option.DEFAULT_PATH_LEAF_TO_NULL ", true, jsonInputMeta.isDefaultPathLeafToNull());
    }
}

