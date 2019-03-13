/**
 * Copyright 2014 Anno van Vliet, 2018 Florian Schmaus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smackx.xdatavalidation.provider;


import java.io.IOException;
import org.jivesoftware.smack.test.util.TestUtils;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement.BasicValidateElement;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement.ListRange;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement.RangeValidateElement;
import org.junit.Assert;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


/**
 * Data validation test.
 *
 * @author Anno van Vliet
 */
public class DataValidationTest {
    private static final String TEST_INPUT_MIN = "<validate xmlns='http://jabber.org/protocol/xdata-validate'></validate>";

    private static final String TEST_OUTPUT_MIN = "<validate xmlns='http://jabber.org/protocol/xdata-validate'><basic/></validate>";

    private static final String TEST_OUTPUT_RANGE = "<validate xmlns='http://jabber.org/protocol/xdata-validate' datatype='xs:string'><range min='min-val' max='max-val'/><list-range min='111' max='999'/></validate>";

    private static final String TEST_OUTPUT_RANGE2 = "<validate xmlns='http://jabber.org/protocol/xdata-validate'><range/></validate>";

    private static final String TEST_OUTPUT_FAIL = "<validate xmlns='http://jabber.org/protocol/xdata-validate'><list-range min='1-1-1' max='999'/></validate>";

    @Test
    public void testMin() throws IOException, XmlPullParserException {
        ValidateElement dv = new BasicValidateElement(null);
        Assert.assertNotNull(dv.toXML());
        String output = dv.toXML().toString();
        Assert.assertEquals(DataValidationTest.TEST_OUTPUT_MIN, output);
        XmlPullParser parser = DataValidationTest.getParser(DataValidationTest.TEST_INPUT_MIN);
        dv = DataValidationProvider.parse(parser);
        Assert.assertNotNull(dv);
        Assert.assertEquals("xs:string", dv.getDatatype());
        Assert.assertTrue((dv instanceof BasicValidateElement));
        Assert.assertNotNull(dv.toXML());
        output = dv.toXML().toString();
        Assert.assertEquals(DataValidationTest.TEST_OUTPUT_MIN, output);
    }

    @Test
    public void testRange() throws IOException, XmlPullParserException {
        ValidateElement dv = new RangeValidateElement("xs:string", "min-val", "max-val");
        ListRange listRange = new ListRange(111L, 999L);
        dv.setListRange(listRange);
        Assert.assertNotNull(dv.toXML());
        String output = dv.toXML().toString();
        Assert.assertEquals(DataValidationTest.TEST_OUTPUT_RANGE, output);
        XmlPullParser parser = DataValidationTest.getParser(output);
        dv = DataValidationProvider.parse(parser);
        Assert.assertNotNull(dv);
        Assert.assertEquals("xs:string", dv.getDatatype());
        Assert.assertTrue((dv instanceof RangeValidateElement));
        RangeValidateElement rdv = ((RangeValidateElement) (dv));
        Assert.assertEquals("min-val", rdv.getMin());
        Assert.assertEquals("max-val", rdv.getMax());
        Assert.assertNotNull(rdv.getListRange());
        Assert.assertEquals(Long.valueOf(111), rdv.getListRange().getMin());
        Assert.assertEquals(999, rdv.getListRange().getMax().intValue());
        Assert.assertNotNull(dv.toXML());
        output = dv.toXML().toString();
        Assert.assertEquals(DataValidationTest.TEST_OUTPUT_RANGE, output);
    }

    @Test
    public void testRange2() throws IOException, XmlPullParserException {
        ValidateElement dv = new RangeValidateElement(null, null, null);
        Assert.assertNotNull(dv.toXML());
        String output = dv.toXML().toString();
        Assert.assertEquals(DataValidationTest.TEST_OUTPUT_RANGE2, output);
        XmlPullParser parser = DataValidationTest.getParser(output);
        dv = DataValidationProvider.parse(parser);
        Assert.assertNotNull(dv);
        Assert.assertEquals("xs:string", dv.getDatatype());
        Assert.assertTrue((dv instanceof RangeValidateElement));
        RangeValidateElement rdv = ((RangeValidateElement) (dv));
        Assert.assertEquals(null, rdv.getMin());
        Assert.assertEquals(null, rdv.getMax());
        Assert.assertNotNull(rdv.toXML());
        output = rdv.toXML().toString();
        Assert.assertEquals(DataValidationTest.TEST_OUTPUT_RANGE2, output);
    }

    @Test(expected = NumberFormatException.class)
    public void testRangeFailure() throws IOException, XmlPullParserException {
        XmlPullParser parser = DataValidationTest.getParser(DataValidationTest.TEST_OUTPUT_FAIL);
        DataValidationProvider.parse(parser);
    }

    @Test
    public void testNamespacePrefix() throws Exception {
        String formFieldUsingNamespacePrefix = "<x xmlns='jabber:x:data'" + ((((((((((((((((("   xmlns:xdv='http://jabber.org/protocol/xdata-validate'" + "   type='form'>") + "  <title>Sample Form</title>") + "  <instructions>") + "    Please provide information for the following fields...") + "  </instructions>") + "  <field type='text-single' var='name' label='Event Name'/>") + "  <field type='text-single' var='date/start' label='Starting Date'>") + "    <xdv:validate datatype='xs:date'>") + "      <basic/>") + "    </xdv:validate>") + "  </field>") + "  <field type='text-single' var='date/end' label='Ending Date'>") + "    <xdv:validate datatype='xs:date'>") + "      <basic/>") + "    </xdv:validate>") + "  </field>") + "</x>");
        DataForm dataForm = TestUtils.parseExtensionElement(formFieldUsingNamespacePrefix);
        Assert.assertEquals("Sample Form", dataForm.getTitle());
        FormField nameField = dataForm.getField("name");
        Assert.assertEquals("Event Name", nameField.getLabel());
        FormField dataStartField = dataForm.getField("date/start");
        ValidateElement dataStartValidateElement = dataStartField.getValidateElement();
        Assert.assertEquals("xs:date", dataStartValidateElement.getDatatype());
        Assert.assertTrue((dataStartValidateElement instanceof BasicValidateElement));
    }
}

