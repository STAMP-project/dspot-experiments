/**
 * Copyright 2014 Anno van Vliet
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
package org.jivesoftware.smackx.xdata.packet;


import Type.fixed;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.FormField.Type;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.jivesoftware.smackx.xdatalayout.packet.DataLayout;
import org.jivesoftware.smackx.xdatalayout.packet.DataLayout.Fieldref;
import org.jivesoftware.smackx.xdatalayout.packet.DataLayout.Section;
import org.jivesoftware.smackx.xdatalayout.packet.DataLayout.Text;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement.RangeValidateElement;
import org.junit.Assert;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;


/**
 * Unit tests for DataForm reading and parsing.
 *
 * @author Anno van Vliet
 */
public class DataFormTest {
    private static final String TEST_OUTPUT_1 = "<x xmlns='jabber:x:data' type='submit'><instructions>InstructionTest1</instructions><field var='testField1'></field></x>";

    private static final String TEST_OUTPUT_2 = "<x xmlns='jabber:x:data' type='submit'><instructions>InstructionTest1</instructions><field var='testField1'></field><page xmlns='http://jabber.org/protocol/xdata-layout' label='Label'><fieldref var='testField1'/><section label='section Label'><text>SectionText</text></section><text>PageText</text></page></x>";

    private static final String TEST_OUTPUT_3 = "<x xmlns='jabber:x:data' type='submit'><instructions>InstructionTest1</instructions><field var='testField1'><validate xmlns='http://jabber.org/protocol/xdata-validate' datatype='xs:integer'><range min='1111' max='9999'/></validate></field></x>";

    private static final DataFormProvider pr = new DataFormProvider();

    @Test
    public void test() throws Exception {
        // Build a Form.
        DataForm df = new DataForm(Type.submit);
        String instruction = "InstructionTest1";
        df.addInstruction(instruction);
        FormField field = new FormField("testField1");
        df.addField(field);
        Assert.assertNotNull(df.toXML());
        String output = df.toXML().toString();
        Assert.assertEquals(DataFormTest.TEST_OUTPUT_1, output);
        XmlPullParser parser = PacketParserUtils.getParserFor(output);
        df = DataFormTest.pr.parse(parser);
        Assert.assertNotNull(df);
        Assert.assertNotNull(df.getFields());
        Assert.assertEquals(1, df.getFields().size());
        Assert.assertEquals(1, df.getInstructions().size());
        Assert.assertNotNull(df.toXML());
        output = df.toXML().toString();
        Assert.assertEquals(DataFormTest.TEST_OUTPUT_1, output);
    }

    @Test
    public void testLayout() throws Exception {
        // Build a Form.
        DataForm df = new DataForm(Type.submit);
        String instruction = "InstructionTest1";
        df.addInstruction(instruction);
        FormField field = new FormField("testField1");
        df.addField(field);
        DataLayout layout = new DataLayout("Label");
        Fieldref reffield = new Fieldref("testField1");
        layout.getPageLayout().add(reffield);
        Section section = new Section("section Label");
        section.getSectionLayout().add(new Text("SectionText"));
        layout.getPageLayout().add(section);
        layout.getPageLayout().add(new Text("PageText"));
        df.addExtensionElement(layout);
        Assert.assertNotNull(df.toXML());
        String output = df.toXML().toString();
        Assert.assertEquals(DataFormTest.TEST_OUTPUT_2, output);
        XmlPullParser parser = PacketParserUtils.getParserFor(output);
        df = DataFormTest.pr.parse(parser);
        Assert.assertNotNull(df);
        Assert.assertNotNull(df.getExtensionElements());
        Assert.assertEquals(1, df.getExtensionElements().size());
        Element element = df.getExtensionElements().get(0);
        Assert.assertNotNull(element);
        layout = ((DataLayout) (element));
        Assert.assertEquals(3, layout.getPageLayout().size());
        Assert.assertNotNull(df.toXML());
        output = df.toXML().toString();
        Assert.assertEquals(DataFormTest.TEST_OUTPUT_2, output);
    }

    @Test
    public void testValidation() throws Exception {
        // Build a Form.
        DataForm df = new DataForm(Type.submit);
        String instruction = "InstructionTest1";
        df.addInstruction(instruction);
        FormField field = new FormField("testField1");
        df.addField(field);
        ValidateElement dv = new RangeValidateElement("xs:integer", "1111", "9999");
        field.setValidateElement(dv);
        Assert.assertNotNull(df.toXML());
        String output = df.toXML().toString();
        Assert.assertEquals(DataFormTest.TEST_OUTPUT_3, output);
        XmlPullParser parser = PacketParserUtils.getParserFor(output);
        df = DataFormTest.pr.parse(parser);
        Assert.assertNotNull(df);
        Assert.assertNotNull(df.getFields());
        Assert.assertEquals(1, df.getFields().size());
        Element element = df.getFields().get(0).getValidateElement();
        Assert.assertNotNull(element);
        dv = ((ValidateElement) (element));
        Assert.assertEquals("xs:integer", dv.getDatatype());
        Assert.assertNotNull(df.toXML());
        output = df.toXML().toString();
        Assert.assertEquals(DataFormTest.TEST_OUTPUT_3, output);
    }

    @Test
    public void testFixedField() throws Exception {
        final String formWithFixedField = "<x xmlns='jabber:x:data' type='submit'><instructions>InstructionTest1</instructions><field type='fixed'></field></x>";
        DataForm df = DataFormTest.pr.parse(PacketParserUtils.getParserFor(formWithFixedField));
        Assert.assertEquals(fixed, df.getFields().get(0).getType());
    }
}

