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
package org.pentaho.di.trans.steps.xmloutput;


import Const.KETTLE_COMPATIBILITY_XML_OUTPUT_NULL_VALUES;
import ContentType.Attribute;
import ContentType.Element;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.pentaho.di.trans.steps.xmloutput.XMLField.ContentType;


/**
 *
 *
 * @author Tatsiana_Kasiankova
 */
public class XMLOutputTest {
    private StepMockHelper<XMLOutputMeta, XMLOutputData> stepMockHelper;

    private XMLOutput xmlOutput;

    private XMLOutputMeta xmlOutputMeta;

    private XMLOutputData xmlOutputData;

    private Trans trans = Mockito.mock(Trans.class);

    private static final String[] ILLEGAL_CHARACTERS_IN_XML_ATTRIBUTES = new String[]{ "<", ">", "&", "\'", "\"" };

    private static Object[] rowWithData;

    private static Object[] rowWithNullData;

    @Test
    public void testSpecialSymbolsInAttributeValuesAreEscaped() throws XMLStreamException, KettleException {
        xmlOutput.init(xmlOutputMeta, xmlOutputData);
        xmlOutputData.writer = Mockito.mock(XMLStreamWriter.class);
        xmlOutput.writeRowAttributes(XMLOutputTest.rowWithData);
        xmlOutput.dispose(xmlOutputMeta, xmlOutputData);
        Mockito.verify(xmlOutputData.writer, Mockito.times(XMLOutputTest.rowWithData.length)).writeAttribute(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(xmlOutput, Mockito.atLeastOnce()).closeOutputStream(ArgumentMatchers.any());
    }

    @Test
    public void testNullInAttributeValuesAreEscaped() throws XMLStreamException, KettleException {
        testNullValuesInAttribute(0);
    }

    @Test
    public void testNullInAttributeValuesAreNotEscaped() throws XMLStreamException, KettleException {
        xmlOutput.setVariable(KETTLE_COMPATIBILITY_XML_OUTPUT_NULL_VALUES, "Y");
        testNullValuesInAttribute(XMLOutputTest.rowWithNullData.length);
    }

    /**
     * [PDI-15575] Testing to verify that getIfPresent defaults the XMLField ContentType value
     */
    @Test
    public void testDefaultXmlFieldContentType() {
        XMLField[] xmlFields = initOutputFields(4, null);
        xmlFields[0].setContentType(ContentType.getIfPresent("Element"));
        xmlFields[1].setContentType(ContentType.getIfPresent("Attribute"));
        xmlFields[2].setContentType(ContentType.getIfPresent(""));
        xmlFields[3].setContentType(ContentType.getIfPresent("WrongValue"));
        Assert.assertEquals(xmlFields[0].getContentType(), Element);
        Assert.assertEquals(xmlFields[1].getContentType(), Attribute);
        Assert.assertEquals(xmlFields[2].getContentType(), Element);
        Assert.assertEquals(xmlFields[3].getContentType(), Element);
    }
}

