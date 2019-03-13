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
package org.pentaho.di.trans.steps.xmlinputstream;


import StringUtils.EMPTY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.SingleRowRowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.step.RowAdapter;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 *
 *
 * @author Tatsiana_Kasiankova
 */
public class XMLInputStreamTest {
    private static final String INCORRECT_XML_DATA_VALUE_MESSAGE = "Incorrect xml data value - ";

    private static final String INCORRECT_XML_DATA_NAME_MESSAGE = "Incorrect xml data name - ";

    private static final String INCORRECT_XML_PATH_MESSAGE = "Incorrect xml path - ";

    private static final String INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE = "Incorrect xml data type description - ";

    private static final String ATTRIBUTE_2 = "ATTRIBUTE_2";

    private static final String ATTRIBUTE_1 = "ATTRIBUTE_1";

    private static final int START_ROW_IN_XML_TO_VERIFY = 9;

    private static StepMockHelper<XMLInputStreamMeta, XMLInputStreamData> stepMockHelper;

    private XMLInputStreamMeta xmlInputStreamMeta;

    private XMLInputStreamData xmlInputStreamData;

    private XMLInputStreamTest.TestRowListener rl;

    private int typeDescriptionPos = 0;

    private int pathPos = 1;

    private int dataNamePos = 2;

    private int dataValue = 3;

    @Test
    public void testParseXmlWithPrefixes_WhenSetEnableNamespaceAsTrue() throws IOException, KettleException {
        xmlInputStreamMeta.setFilename(createTestFile(getXMLString(getGroupWithPrefix())));
        xmlInputStreamMeta.setEnableNamespaces(true);
        doTest();
        // Assertions
        // check StartElement for the ProductGroup element
        // when namespaces are enabled, we have additional NAMESPACE events - 3 for our test xml;
        int expectedRowNum = (XMLInputStreamTest.START_ROW_IN_XML_TO_VERIFY) + 3;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "START_ELEMENT", rl.getWritten().get(expectedRowNum)[typeDescriptionPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/Products/Product/Fruits:ProductGroup", rl.getWritten().get(expectedRowNum)[pathPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "Fruits:ProductGroup", rl.getWritten().get(expectedRowNum)[dataNamePos]);
        // attributes
        // ATTRIBUTE_1
        expectedRowNum++;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "ATTRIBUTE", rl.getWritten().get(expectedRowNum)[typeDescriptionPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/Products/Product/Fruits:ProductGroup", rl.getWritten().get(expectedRowNum)[pathPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "Fruits:attribute", rl.getWritten().get(expectedRowNum)[dataNamePos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_VALUE_MESSAGE, XMLInputStreamTest.ATTRIBUTE_1, rl.getWritten().get(expectedRowNum)[dataValue]);
        // ATTRIBUTE_2
        expectedRowNum++;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "ATTRIBUTE", rl.getWritten().get(expectedRowNum)[typeDescriptionPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/Products/Product/Fruits:ProductGroup", rl.getWritten().get(expectedRowNum)[pathPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "Fish:attribute", rl.getWritten().get(expectedRowNum)[dataNamePos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_VALUE_MESSAGE, XMLInputStreamTest.ATTRIBUTE_2, rl.getWritten().get(expectedRowNum)[dataValue]);
        // check EndElement for the ProductGroup element
        expectedRowNum = expectedRowNum + 2;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "END_ELEMENT", rl.getWritten().get(expectedRowNum)[typeDescriptionPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/Products/Product/Fruits:ProductGroup", rl.getWritten().get(expectedRowNum)[pathPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "Fruits:ProductGroup", rl.getWritten().get(expectedRowNum)[dataNamePos]);
    }

    @Test
    public void testParseXmlWithPrefixes_WhenSetEnableNamespaceAsFalse() throws IOException, KettleException {
        xmlInputStreamMeta.setFilename(createTestFile(getXMLString(getGroupWithPrefix())));
        xmlInputStreamMeta.setEnableNamespaces(false);
        doTest();
        // Assertions
        // check StartElement for the ProductGroup element
        int expectedRowNum = XMLInputStreamTest.START_ROW_IN_XML_TO_VERIFY;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "START_ELEMENT", rl.getWritten().get(expectedRowNum)[typeDescriptionPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/Products/Product/ProductGroup", rl.getWritten().get(expectedRowNum)[pathPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "ProductGroup", rl.getWritten().get(expectedRowNum)[dataNamePos]);
        // attributes
        // ATTRIBUTE_1
        expectedRowNum++;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "ATTRIBUTE", rl.getWritten().get(expectedRowNum)[typeDescriptionPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/Products/Product/ProductGroup", rl.getWritten().get(expectedRowNum)[pathPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "attribute", rl.getWritten().get(expectedRowNum)[dataNamePos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_VALUE_MESSAGE, XMLInputStreamTest.ATTRIBUTE_1, rl.getWritten().get(expectedRowNum)[dataValue]);
        // ATTRIBUTE_2
        expectedRowNum++;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "ATTRIBUTE", rl.getWritten().get(expectedRowNum)[typeDescriptionPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/Products/Product/ProductGroup", rl.getWritten().get(expectedRowNum)[pathPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "attribute", rl.getWritten().get(expectedRowNum)[dataNamePos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_VALUE_MESSAGE, XMLInputStreamTest.ATTRIBUTE_2, rl.getWritten().get(expectedRowNum)[dataValue]);
        // check EndElement for the ProductGroup element
        expectedRowNum = expectedRowNum + 2;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "END_ELEMENT", rl.getWritten().get(expectedRowNum)[typeDescriptionPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/Products/Product/ProductGroup", rl.getWritten().get(expectedRowNum)[pathPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "ProductGroup", rl.getWritten().get(expectedRowNum)[dataNamePos]);
    }

    @Test
    public void testParseXmlWithoutPrefixes_WhenSetEnableNamespaceAsTrue() throws IOException, KettleException {
        xmlInputStreamMeta.setFilename(createTestFile(getXMLString(getGroupWithoutPrefix())));
        xmlInputStreamMeta.setEnableNamespaces(true);
        doTest();
        // Assertions
        // check StartElement for the ProductGroup element
        // when namespaces are enabled, we have additional NAMESPACE events - 3 for our test xml;
        int expectedRowNum = (XMLInputStreamTest.START_ROW_IN_XML_TO_VERIFY) + 3;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "START_ELEMENT", rl.getWritten().get(expectedRowNum)[typeDescriptionPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/Products/Product/ProductGroup", rl.getWritten().get(expectedRowNum)[pathPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "ProductGroup", rl.getWritten().get(expectedRowNum)[dataNamePos]);
        // attributes
        // ATTRIBUTE_1
        expectedRowNum++;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "ATTRIBUTE", rl.getWritten().get(expectedRowNum)[typeDescriptionPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/Products/Product/ProductGroup", rl.getWritten().get(expectedRowNum)[pathPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "attribute1", rl.getWritten().get(expectedRowNum)[dataNamePos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_VALUE_MESSAGE, XMLInputStreamTest.ATTRIBUTE_1, rl.getWritten().get(expectedRowNum)[dataValue]);
        // ATTRIBUTE_2
        expectedRowNum++;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "ATTRIBUTE", rl.getWritten().get(expectedRowNum)[typeDescriptionPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/Products/Product/ProductGroup", rl.getWritten().get(expectedRowNum)[pathPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "attribute2", rl.getWritten().get(expectedRowNum)[dataNamePos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_VALUE_MESSAGE, XMLInputStreamTest.ATTRIBUTE_2, rl.getWritten().get(expectedRowNum)[dataValue]);
        // check EndElement for the ProductGroup element
        expectedRowNum = expectedRowNum + 2;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "END_ELEMENT", rl.getWritten().get(expectedRowNum)[typeDescriptionPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/Products/Product/ProductGroup", rl.getWritten().get(expectedRowNum)[pathPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "ProductGroup", rl.getWritten().get(expectedRowNum)[dataNamePos]);
    }

    @Test
    public void testFromPreviousStep() throws Exception {
        xmlInputStreamMeta.sourceFromInput = true;
        xmlInputStreamMeta.sourceFieldName = "inf";
        xmlInputStreamData.outputRowMeta = new RowMeta();
        RowMeta rm = new RowMeta();
        String xml = "<ProductGroup attribute1=\"v1\"/>";
        ValueMetaString ms = new ValueMetaString("inf");
        RowSet rs = new SingleRowRowSet();
        rs.putRow(rm, new Object[]{ xml });
        rs.setDone();
        XMLInputStream xmlInputStream = new XMLInputStream(XMLInputStreamTest.stepMockHelper.stepMeta, XMLInputStreamTest.stepMockHelper.stepDataInterface, 0, XMLInputStreamTest.stepMockHelper.transMeta, XMLInputStreamTest.stepMockHelper.trans);
        xmlInputStream.setInputRowMeta(rm);
        xmlInputStream.getInputRowMeta().addValueMeta(ms);
        xmlInputStream.addRowSetToInputRowSets(rs);
        xmlInputStream.setOutputRowSets(new ArrayList());
        xmlInputStream.init(xmlInputStreamMeta, xmlInputStreamData);
        xmlInputStream.addRowListener(rl);
        boolean haveRowsToRead;
        do {
            haveRowsToRead = !(xmlInputStream.processRow(xmlInputStreamMeta, xmlInputStreamData));
        } while (!haveRowsToRead );
        int expectedRowNum = 1;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "<ProductGroup attribute1=\"v1\"/>", rl.getWritten().get(expectedRowNum)[typeDescriptionPos]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "START_ELEMENT", rl.getWritten().get(expectedRowNum)[((typeDescriptionPos) + 1)]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/ProductGroup", rl.getWritten().get(expectedRowNum)[((pathPos) + 1)]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "ProductGroup", rl.getWritten().get(expectedRowNum)[((dataNamePos) + 1)]);
        // attributes
        // ATTRIBUTE_1
        expectedRowNum++;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "ATTRIBUTE", rl.getWritten().get(expectedRowNum)[((typeDescriptionPos) + 1)]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/ProductGroup", rl.getWritten().get(expectedRowNum)[((pathPos) + 1)]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "attribute1", rl.getWritten().get(expectedRowNum)[((dataNamePos) + 1)]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_VALUE_MESSAGE, "v1", rl.getWritten().get(expectedRowNum)[((dataValue) + 1)]);
        // check EndElement for the ProductGroup element
        expectedRowNum++;
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_TYPE_DESCRIPTION_MESSAGE, "END_ELEMENT", rl.getWritten().get(expectedRowNum)[((typeDescriptionPos) + 1)]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_PATH_MESSAGE, "/ProductGroup", rl.getWritten().get(expectedRowNum)[((pathPos) + 1)]);
        Assert.assertEquals(XMLInputStreamTest.INCORRECT_XML_DATA_NAME_MESSAGE, "ProductGroup", rl.getWritten().get(expectedRowNum)[((dataNamePos) + 1)]);
    }

    @Test
    public void testFileNameEnteredManuallyWithIncomingHops() throws Exception {
        testCorrectFileSelected(getFile("default.xml"), 0);
    }

    @Test
    public void testFileNameSelectedFromIncomingHops() throws Exception {
        testCorrectFileSelected("filename", 1);
    }

    @Test(expected = KettleException.class)
    public void testNotValidFilePathAndFileField() throws Exception {
        testCorrectFileSelected("notPathNorValidFieldName", 0);
    }

    @Test(expected = KettleException.class)
    public void testEmptyFileField() throws Exception {
        testCorrectFileSelected(EMPTY, 0);
    }

    private class TestRowListener extends RowAdapter {
        private List<Object[]> written = new ArrayList<Object[]>();

        public List<Object[]> getWritten() {
            return written;
        }

        @Override
        public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
            written.add(row);
        }
    }
}

