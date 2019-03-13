/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.servlet.tags.form;


import Tag.SKIP_BODY;
import java.beans.PropertyEditorSupport;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.jsp.JspException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;


/**
 *
 *
 * @author Thomas Risberg
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Benjamin Hoffmann
 * @author Jeremy Grelle
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CheckboxesTagTests extends AbstractFormTagTests {
    private CheckboxesTag tag;

    private TestBean bean;

    @Test
    public void withMultiValueArray() throws Exception {
        this.tag.setPath("stringArray");
        this.tag.setItems(new Object[]{ "foo", "bar", "baz" });
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element spanElement1 = ((Element) (document.getRootElement().elements().get(0)));
        Element checkboxElement1 = ((Element) (spanElement1.elements().get(0)));
        Assert.assertEquals("input", checkboxElement1.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("foo", getValue());
        Assert.assertEquals("foo", spanElement1.getStringValue());
        Element spanElement2 = ((Element) (document.getRootElement().elements().get(1)));
        Element checkboxElement2 = ((Element) (spanElement2.elements().get(0)));
        Assert.assertEquals("input", checkboxElement2.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("bar", getValue());
        Assert.assertEquals("bar", spanElement2.getStringValue());
        Element spanElement3 = ((Element) (document.getRootElement().elements().get(2)));
        Element checkboxElement3 = ((Element) (spanElement3.elements().get(0)));
        Assert.assertEquals("input", checkboxElement3.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertNull("not checked", checkboxElement3.attribute("checked"));
        Assert.assertEquals("baz", getValue());
        Assert.assertEquals("baz", spanElement3.getStringValue());
    }

    @Test
    public void withMultiValueArrayAndDynamicAttributes() throws Exception {
        String dynamicAttribute1 = "attr1";
        String dynamicAttribute2 = "attr2";
        this.tag.setPath("stringArray");
        this.tag.setItems(new Object[]{ "foo", "bar", "baz" });
        this.tag.setDynamicAttribute(null, dynamicAttribute1, dynamicAttribute1);
        this.tag.setDynamicAttribute(null, dynamicAttribute2, dynamicAttribute2);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element spanElement1 = ((Element) (document.getRootElement().elements().get(0)));
        Element checkboxElement1 = ((Element) (spanElement1.elements().get(0)));
        Assert.assertEquals("input", checkboxElement1.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("foo", getValue());
        Assert.assertEquals("foo", spanElement1.getStringValue());
        Assert.assertEquals(dynamicAttribute1, getValue());
        Assert.assertEquals(dynamicAttribute2, getValue());
        Element spanElement2 = ((Element) (document.getRootElement().elements().get(1)));
        Element checkboxElement2 = ((Element) (spanElement2.elements().get(0)));
        Assert.assertEquals("input", checkboxElement2.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("bar", getValue());
        Assert.assertEquals("bar", spanElement2.getStringValue());
        Assert.assertEquals(dynamicAttribute1, getValue());
        Assert.assertEquals(dynamicAttribute2, getValue());
        Element spanElement3 = ((Element) (document.getRootElement().elements().get(2)));
        Element checkboxElement3 = ((Element) (spanElement3.elements().get(0)));
        Assert.assertEquals("input", checkboxElement3.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertNull("not checked", checkboxElement3.attribute("checked"));
        Assert.assertEquals("baz", getValue());
        Assert.assertEquals("baz", spanElement3.getStringValue());
        Assert.assertEquals(dynamicAttribute1, getValue());
        Assert.assertEquals(dynamicAttribute2, getValue());
    }

    @Test
    public void withMultiValueArrayWithDelimiter() throws Exception {
        this.tag.setDelimiter("<br/>");
        this.tag.setPath("stringArray");
        this.tag.setItems(new Object[]{ "foo", "bar", "baz" });
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element spanElement1 = ((Element) (document.getRootElement().elements().get(0)));
        Element delimiterElement1 = spanElement1.element("br");
        Assert.assertNull(delimiterElement1);
        Element checkboxElement1 = ((Element) (spanElement1.elements().get(0)));
        Assert.assertEquals("input", checkboxElement1.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("foo", getValue());
        Assert.assertEquals("foo", spanElement1.getStringValue());
        Element spanElement2 = ((Element) (document.getRootElement().elements().get(1)));
        Element delimiterElement2 = ((Element) (spanElement2.elements().get(0)));
        Assert.assertEquals("br", delimiterElement2.getName());
        Element checkboxElement2 = ((Element) (spanElement2.elements().get(1)));
        Assert.assertEquals("input", checkboxElement2.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("bar", getValue());
        Assert.assertEquals("bar", spanElement2.getStringValue());
        Element spanElement3 = ((Element) (document.getRootElement().elements().get(2)));
        Element delimiterElement3 = ((Element) (spanElement3.elements().get(0)));
        Assert.assertEquals("br", delimiterElement3.getName());
        Element checkboxElement3 = ((Element) (spanElement3.elements().get(1)));
        Assert.assertEquals("input", checkboxElement3.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertNull("not checked", checkboxElement3.attribute("checked"));
        Assert.assertEquals("baz", getValue());
        Assert.assertEquals("baz", spanElement3.getStringValue());
    }

    @Test
    public void withMultiValueMap() throws Exception {
        this.tag.setPath("stringArray");
        Map m = new LinkedHashMap();
        m.put("foo", "FOO");
        m.put("bar", "BAR");
        m.put("baz", "BAZ");
        this.tag.setItems(m);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element spanElement1 = ((Element) (document.getRootElement().elements().get(0)));
        Element checkboxElement1 = ((Element) (spanElement1.elements().get(0)));
        Assert.assertEquals("input", checkboxElement1.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("foo", getValue());
        Assert.assertEquals("FOO", spanElement1.getStringValue());
        Element spanElement2 = ((Element) (document.getRootElement().elements().get(1)));
        Element checkboxElement2 = ((Element) (spanElement2.elements().get(0)));
        Assert.assertEquals("input", checkboxElement2.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("bar", getValue());
        Assert.assertEquals("BAR", spanElement2.getStringValue());
        Element spanElement3 = ((Element) (document.getRootElement().elements().get(2)));
        Element checkboxElement3 = ((Element) (spanElement3.elements().get(0)));
        Assert.assertEquals("input", checkboxElement3.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertNull("not checked", checkboxElement3.attribute("checked"));
        Assert.assertEquals("baz", getValue());
        Assert.assertEquals("BAZ", spanElement3.getStringValue());
    }

    @Test
    public void withPetItemsMap() throws Exception {
        this.tag.setPath("someSet");
        Map m = new LinkedHashMap();
        m.put(new ItemPet("PET1"), "PET1Label");
        m.put(new ItemPet("PET2"), "PET2Label");
        m.put(new ItemPet("PET3"), "PET3Label");
        this.tag.setItems(m);
        tag.setItemValue("name");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element spanElement1 = ((Element) (document.getRootElement().elements().get(0)));
        Element checkboxElement1 = ((Element) (spanElement1.elements().get(0)));
        Assert.assertEquals("input", checkboxElement1.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("someSet", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("PET1", getValue());
        Assert.assertEquals("PET1Label", spanElement1.getStringValue());
        Element spanElement2 = ((Element) (document.getRootElement().elements().get(1)));
        Element checkboxElement2 = ((Element) (spanElement2.elements().get(0)));
        Assert.assertEquals("input", checkboxElement2.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("someSet", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("PET2", getValue());
        Assert.assertEquals("PET2Label", spanElement2.getStringValue());
        Element spanElement3 = ((Element) (document.getRootElement().elements().get(2)));
        Element checkboxElement3 = ((Element) (spanElement3.elements().get(0)));
        Assert.assertEquals("input", checkboxElement3.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("someSet", getValue());
        Assert.assertNull("not checked", checkboxElement3.attribute("checked"));
        Assert.assertEquals("PET3", getValue());
        Assert.assertEquals("PET3Label", spanElement3.getStringValue());
    }

    @Test
    public void withMultiValueMapWithDelimiter() throws Exception {
        String delimiter = " | ";
        this.tag.setDelimiter(delimiter);
        this.tag.setPath("stringArray");
        Map m = new LinkedHashMap();
        m.put("foo", "FOO");
        m.put("bar", "BAR");
        m.put("baz", "BAZ");
        this.tag.setItems(m);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element spanElement1 = ((Element) (document.getRootElement().elements().get(0)));
        Element checkboxElement1 = ((Element) (spanElement1.elements().get(0)));
        Assert.assertEquals("input", checkboxElement1.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("foo", getValue());
        Assert.assertEquals("FOO", spanElement1.getStringValue());
        Element spanElement2 = ((Element) (document.getRootElement().elements().get(1)));
        Element checkboxElement2 = ((Element) (spanElement2.elements().get(0)));
        Assert.assertEquals("input", checkboxElement2.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("bar", getValue());
        Assert.assertEquals((delimiter + "BAR"), spanElement2.getStringValue());
        Element spanElement3 = ((Element) (document.getRootElement().elements().get(2)));
        Element checkboxElement3 = ((Element) (spanElement3.elements().get(0)));
        Assert.assertEquals("input", checkboxElement3.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertNull("not checked", checkboxElement3.attribute("checked"));
        Assert.assertEquals("baz", getValue());
        Assert.assertEquals((delimiter + "BAZ"), spanElement3.getStringValue());
    }

    @Test
    public void withMultiValueWithEditor() throws Exception {
        this.tag.setPath("stringArray");
        this.tag.setItems(new Object[]{ "   foo", "   bar", "   baz" });
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(this.bean, AbstractHtmlElementTagTests.COMMAND_NAME);
        CheckboxesTagTests.MyStringTrimmerEditor editor = new CheckboxesTagTests.MyStringTrimmerEditor();
        bindingResult.getPropertyEditorRegistry().registerCustomEditor(String.class, editor);
        getPageContext().getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + (AbstractHtmlElementTagTests.COMMAND_NAME)), bindingResult);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        Assert.assertEquals(3, editor.allProcessedValues.size());
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element spanElement1 = ((Element) (document.getRootElement().elements().get(0)));
        Element checkboxElement1 = ((Element) (spanElement1.elements().get(0)));
        Assert.assertEquals("input", checkboxElement1.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("   foo", getValue());
        Element spanElement2 = ((Element) (document.getRootElement().elements().get(1)));
        Element checkboxElement2 = ((Element) (spanElement2.elements().get(0)));
        Assert.assertEquals("input", checkboxElement2.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("   bar", getValue());
        Element spanElement3 = ((Element) (document.getRootElement().elements().get(2)));
        Element checkboxElement3 = ((Element) (spanElement3.elements().get(0)));
        Assert.assertEquals("input", checkboxElement3.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertNull("not checked", checkboxElement3.attribute("checked"));
        Assert.assertEquals("   baz", getValue());
    }

    @Test
    public void withMultiValueWithReverseEditor() throws Exception {
        this.tag.setPath("stringArray");
        this.tag.setItems(new Object[]{ "FOO", "BAR", "BAZ" });
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(this.bean, AbstractHtmlElementTagTests.COMMAND_NAME);
        CheckboxesTagTests.MyLowerCaseEditor editor = new CheckboxesTagTests.MyLowerCaseEditor();
        bindingResult.getPropertyEditorRegistry().registerCustomEditor(String.class, editor);
        getPageContext().getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + (AbstractHtmlElementTagTests.COMMAND_NAME)), bindingResult);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element spanElement1 = ((Element) (document.getRootElement().elements().get(0)));
        Element checkboxElement1 = ((Element) (spanElement1.elements().get(0)));
        Assert.assertEquals("input", checkboxElement1.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("FOO", getValue());
        Element spanElement2 = ((Element) (document.getRootElement().elements().get(1)));
        Element checkboxElement2 = ((Element) (spanElement2.elements().get(0)));
        Assert.assertEquals("input", checkboxElement2.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("BAR", getValue());
        Element spanElement3 = ((Element) (document.getRootElement().elements().get(2)));
        Element checkboxElement3 = ((Element) (spanElement3.elements().get(0)));
        Assert.assertEquals("input", checkboxElement3.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertNull("not checked", checkboxElement3.attribute("checked"));
        Assert.assertEquals("BAZ", getValue());
    }

    @Test
    public void withMultiValueWithFormatter() throws Exception {
        this.tag.setPath("stringArray");
        this.tag.setItems(new Object[]{ "   foo", "   bar", "   baz" });
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(this.bean, AbstractHtmlElementTagTests.COMMAND_NAME);
        FormattingConversionService cs = new FormattingConversionService();
        cs.addFormatterForFieldType(String.class, new org.springframework.format.Formatter<String>() {
            @Override
            public String print(String object, Locale locale) {
                return object;
            }

            @Override
            public String parse(String text, Locale locale) throws ParseException {
                return text.trim();
            }
        });
        bindingResult.initConversion(cs);
        getPageContext().getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + (AbstractHtmlElementTagTests.COMMAND_NAME)), bindingResult);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element spanElement1 = ((Element) (document.getRootElement().elements().get(0)));
        Element checkboxElement1 = ((Element) (spanElement1.elements().get(0)));
        Assert.assertEquals("input", checkboxElement1.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("   foo", getValue());
        Element spanElement2 = ((Element) (document.getRootElement().elements().get(1)));
        Element checkboxElement2 = ((Element) (spanElement2.elements().get(0)));
        Assert.assertEquals("input", checkboxElement2.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("   bar", getValue());
        Element spanElement3 = ((Element) (document.getRootElement().elements().get(2)));
        Element checkboxElement3 = ((Element) (spanElement3.elements().get(0)));
        Assert.assertEquals("input", checkboxElement3.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertNull("not checked", checkboxElement3.attribute("checked"));
        Assert.assertEquals("   baz", getValue());
    }

    @Test
    public void collectionOfPets() throws Exception {
        this.tag.setPath("pets");
        List allPets = new ArrayList();
        allPets.add(new ItemPet("Rudiger"));
        allPets.add(new ItemPet("Spot"));
        allPets.add(new ItemPet("Checkers"));
        allPets.add(new ItemPet("Fluffy"));
        allPets.add(new ItemPet("Mufty"));
        this.tag.setItems(allPets);
        this.tag.setItemValue("name");
        this.tag.setItemLabel("label");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element spanElement1 = ((Element) (document.getRootElement().elements().get(0)));
        Element checkboxElement1 = ((Element) (spanElement1.elements().get(0)));
        Assert.assertEquals("input", checkboxElement1.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("pets", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("Rudiger", getValue());
        Assert.assertEquals("RUDIGER", spanElement1.getStringValue());
        Element spanElement2 = ((Element) (document.getRootElement().elements().get(1)));
        Element checkboxElement2 = ((Element) (spanElement2.elements().get(0)));
        Assert.assertEquals("input", checkboxElement2.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("pets", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("Spot", getValue());
        Assert.assertEquals("SPOT", spanElement2.getStringValue());
        Element spanElement3 = ((Element) (document.getRootElement().elements().get(2)));
        Element checkboxElement3 = ((Element) (spanElement3.elements().get(0)));
        Assert.assertEquals("input", checkboxElement3.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("pets", getValue());
        Assert.assertNull("not checked", checkboxElement3.attribute("checked"));
        Assert.assertEquals("Checkers", getValue());
        Assert.assertEquals("CHECKERS", spanElement3.getStringValue());
        Element spanElement4 = ((Element) (document.getRootElement().elements().get(3)));
        Element checkboxElement4 = ((Element) (spanElement4.elements().get(0)));
        Assert.assertEquals("input", checkboxElement4.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("pets", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("Fluffy", getValue());
        Assert.assertEquals("FLUFFY", spanElement4.getStringValue());
        Element spanElement5 = ((Element) (document.getRootElement().elements().get(4)));
        Element checkboxElement5 = ((Element) (spanElement5.elements().get(0)));
        Assert.assertEquals("input", checkboxElement5.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("pets", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("Mufty", getValue());
        Assert.assertEquals("MUFTY", spanElement5.getStringValue());
    }

    /**
     * Test case where items toString() doesn't fit the item ID
     */
    @Test
    public void collectionOfItemPets() throws Exception {
        this.tag.setPath("someSet");
        List allPets = new ArrayList();
        allPets.add(new ItemPet("PET1"));
        allPets.add(new ItemPet("PET2"));
        allPets.add(new ItemPet("PET3"));
        this.tag.setItems(allPets);
        this.tag.setItemValue("name");
        this.tag.setItemLabel("label");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element spanElement1 = ((Element) (document.getRootElement().elements().get(0)));
        Element checkboxElement1 = ((Element) (spanElement1.elements().get(0)));
        Assert.assertEquals("input", checkboxElement1.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("someSet", getValue());
        Assert.assertNotNull("should be checked", checkboxElement1.attribute("checked"));
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("PET1", getValue());
        Assert.assertEquals("PET1", spanElement1.getStringValue());
        Element spanElement2 = ((Element) (document.getRootElement().elements().get(1)));
        Element checkboxElement2 = ((Element) (spanElement2.elements().get(0)));
        Assert.assertEquals("input", checkboxElement2.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("someSet", getValue());
        Assert.assertNotNull("should be checked", checkboxElement2.attribute("checked"));
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("PET2", getValue());
        Assert.assertEquals("PET2", spanElement2.getStringValue());
        Element spanElement3 = ((Element) (document.getRootElement().elements().get(2)));
        Element checkboxElement3 = ((Element) (spanElement3.elements().get(0)));
        Assert.assertEquals("input", checkboxElement3.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("someSet", getValue());
        Assert.assertNull("not checked", checkboxElement3.attribute("checked"));
        Assert.assertEquals("PET3", getValue());
        Assert.assertEquals("PET3", spanElement3.getStringValue());
    }

    @Test
    public void collectionOfPetsWithEditor() throws Exception {
        this.tag.setPath("pets");
        List allPets = new ArrayList();
        allPets.add(new ItemPet("Rudiger"));
        allPets.add(new ItemPet("Spot"));
        allPets.add(new ItemPet("Checkers"));
        allPets.add(new ItemPet("Fluffy"));
        allPets.add(new ItemPet("Mufty"));
        this.tag.setItems(allPets);
        this.tag.setItemLabel("label");
        this.tag.setId("myId");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(this.bean, AbstractHtmlElementTagTests.COMMAND_NAME);
        PropertyEditorSupport editor = new ItemPet.CustomEditor();
        bindingResult.getPropertyEditorRegistry().registerCustomEditor(ItemPet.class, editor);
        getPageContext().getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + (AbstractHtmlElementTagTests.COMMAND_NAME)), bindingResult);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element spanElement1 = ((Element) (document.getRootElement().elements().get(0)));
        Element checkboxElement1 = ((Element) (spanElement1.elements().get(0)));
        Assert.assertEquals("input", checkboxElement1.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("pets", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("Rudiger", getValue());
        Assert.assertEquals("RUDIGER", spanElement1.getStringValue());
        Element spanElement2 = ((Element) (document.getRootElement().elements().get(1)));
        Element checkboxElement2 = ((Element) (spanElement2.elements().get(0)));
        Assert.assertEquals("input", checkboxElement2.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("pets", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("Spot", getValue());
        Assert.assertEquals("SPOT", spanElement2.getStringValue());
        Element spanElement3 = ((Element) (document.getRootElement().elements().get(2)));
        Element checkboxElement3 = ((Element) (spanElement3.elements().get(0)));
        Assert.assertEquals("input", checkboxElement3.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("pets", getValue());
        Assert.assertNull("not checked", checkboxElement3.attribute("checked"));
        Assert.assertEquals("Checkers", getValue());
        Assert.assertEquals("CHECKERS", spanElement3.getStringValue());
        Element spanElement4 = ((Element) (document.getRootElement().elements().get(3)));
        Element checkboxElement4 = ((Element) (spanElement4.elements().get(0)));
        Assert.assertEquals("input", checkboxElement4.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("pets", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("Fluffy", getValue());
        Assert.assertEquals("FLUFFY", spanElement4.getStringValue());
        Element spanElement5 = ((Element) (document.getRootElement().elements().get(4)));
        Element checkboxElement5 = ((Element) (spanElement5.elements().get(0)));
        Assert.assertEquals("input", checkboxElement5.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("pets", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("Mufty", getValue());
        Assert.assertEquals("MUFTY", spanElement5.getStringValue());
    }

    @Test
    public void withNullValue() throws Exception {
        try {
            this.tag.setPath("name");
            this.tag.doStartTag();
            Assert.fail("Should not be able to render with a null value when binding to a non-boolean.");
        } catch (IllegalArgumentException ex) {
            // success
        }
    }

    @Test
    public void hiddenElementOmittedOnDisabled() throws Exception {
        this.tag.setPath("stringArray");
        this.tag.setItems(new Object[]{ "foo", "bar", "baz" });
        this.tag.setDisabled(true);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element rootElement = document.getRootElement();
        Assert.assertEquals("Both tag and hidden element rendered incorrectly", 3, rootElement.elements().size());
        Element spanElement = ((Element) (document.getRootElement().elements().get(0)));
        Element checkboxElement = ((Element) (spanElement.elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", getValue());
        Assert.assertEquals("stringArray", getValue());
        Assert.assertEquals("checked", getValue());
        Assert.assertEquals("disabled", getValue());
        Assert.assertEquals("foo", getValue());
    }

    @Test
    public void spanElementCustomizable() throws Exception {
        this.tag.setPath("stringArray");
        this.tag.setItems(new Object[]{ "foo", "bar", "baz" });
        this.tag.setElement("element");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element spanElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("element", spanElement.getName());
    }

    @Test
    public void dynamicTypeAttribute() throws JspException {
        try {
            this.tag.setDynamicAttribute(null, "type", "email");
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Attribute type=\"email\" is not allowed", e.getMessage());
        }
    }

    private static class MyStringTrimmerEditor extends StringTrimmerEditor {
        public final Set allProcessedValues = new HashSet();

        public MyStringTrimmerEditor() {
            super(false);
        }

        @Override
        public void setAsText(String text) {
            super.setAsText(text);
            this.allProcessedValues.add(getValue());
        }
    }

    private static class MyLowerCaseEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue(text.toLowerCase());
        }

        @Override
        public String getAsText() {
            return ObjectUtils.nullSafeToString(getValue()).toUpperCase();
        }
    }
}

