/**
 * Copyright 2002-2015 the original author or authors.
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
import java.util.Date;
import javax.servlet.jsp.JspException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.tests.sample.beans.Pet;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Jeremy Grelle
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CheckboxTagTests extends AbstractFormTagTests {
    private CheckboxTag tag;

    private TestBean bean;

    @Test
    public void withSingleValueBooleanObjectChecked() throws Exception {
        this.tag.setPath("someBoolean");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element rootElement = document.getRootElement();
        Assert.assertEquals("Both tag and hidden element not rendered", 2, rootElement.elements().size());
        Element checkboxElement = ((Element) (rootElement.elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("someBoolean1", checkboxElement.attribute("id").getValue());
        Assert.assertEquals("someBoolean", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
        Assert.assertEquals("true", checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withIndexedBooleanObjectNotChecked() throws Exception {
        this.tag.setPath("someMap[key]");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element rootElement = document.getRootElement();
        Assert.assertEquals("Both tag and hidden element not rendered", 2, rootElement.elements().size());
        Element checkboxElement = ((Element) (rootElement.elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("someMapkey1", checkboxElement.attribute("id").getValue());
        Assert.assertEquals("someMap[key]", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
        Assert.assertEquals("true", checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withSingleValueBooleanObjectCheckedAndDynamicAttributes() throws Exception {
        String dynamicAttribute1 = "attr1";
        String dynamicAttribute2 = "attr2";
        this.tag.setPath("someBoolean");
        this.tag.setDynamicAttribute(null, dynamicAttribute1, dynamicAttribute1);
        this.tag.setDynamicAttribute(null, dynamicAttribute2, dynamicAttribute2);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element rootElement = document.getRootElement();
        Assert.assertEquals("Both tag and hidden element not rendered", 2, rootElement.elements().size());
        Element checkboxElement = ((Element) (rootElement.elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("someBoolean", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
        Assert.assertEquals("true", checkboxElement.attribute("value").getValue());
        Assert.assertEquals(dynamicAttribute1, checkboxElement.attribute(dynamicAttribute1).getValue());
        Assert.assertEquals(dynamicAttribute2, checkboxElement.attribute(dynamicAttribute2).getValue());
    }

    @Test
    public void withSingleValueBooleanChecked() throws Exception {
        this.tag.setPath("jedi");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("jedi", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
        Assert.assertEquals("true", checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withSingleValueBooleanObjectUnchecked() throws Exception {
        this.bean.setSomeBoolean(Boolean.FALSE);
        this.tag.setPath("someBoolean");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("someBoolean", checkboxElement.attribute("name").getValue());
        Assert.assertNull(checkboxElement.attribute("checked"));
        Assert.assertEquals("true", checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withSingleValueBooleanUnchecked() throws Exception {
        this.bean.setJedi(false);
        this.tag.setPath("jedi");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("jedi", checkboxElement.attribute("name").getValue());
        Assert.assertNull(checkboxElement.attribute("checked"));
        Assert.assertEquals("true", checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withSingleValueNull() throws Exception {
        this.bean.setName(null);
        this.tag.setPath("name");
        this.tag.setValue("Rob Harrop");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("name", checkboxElement.attribute("name").getValue());
        Assert.assertNull(checkboxElement.attribute("checked"));
        Assert.assertEquals("Rob Harrop", checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withSingleValueNotNull() throws Exception {
        this.bean.setName("Rob Harrop");
        this.tag.setPath("name");
        this.tag.setValue("Rob Harrop");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("name", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
        Assert.assertEquals("Rob Harrop", checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withSingleValueAndEditor() throws Exception {
        this.bean.setName("Rob Harrop");
        this.tag.setPath("name");
        this.tag.setValue("   Rob Harrop");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(this.bean, AbstractHtmlElementTagTests.COMMAND_NAME);
        bindingResult.getPropertyEditorRegistry().registerCustomEditor(String.class, new StringTrimmerEditor(false));
        getPageContext().getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + (AbstractHtmlElementTagTests.COMMAND_NAME)), bindingResult);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("name", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
        Assert.assertEquals("   Rob Harrop", checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withMultiValueChecked() throws Exception {
        this.tag.setPath("stringArray");
        this.tag.setValue("foo");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("stringArray", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
        Assert.assertEquals("foo", checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withMultiValueUnchecked() throws Exception {
        this.tag.setPath("stringArray");
        this.tag.setValue("abc");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("stringArray", checkboxElement.attribute("name").getValue());
        Assert.assertNull(checkboxElement.attribute("checked"));
        Assert.assertEquals("abc", checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withMultiValueWithEditor() throws Exception {
        this.tag.setPath("stringArray");
        this.tag.setValue("   foo");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(this.bean, AbstractHtmlElementTagTests.COMMAND_NAME);
        CheckboxTagTests.MyStringTrimmerEditor editor = new CheckboxTagTests.MyStringTrimmerEditor();
        bindingResult.getPropertyEditorRegistry().registerCustomEditor(String.class, editor);
        getPageContext().getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + (AbstractHtmlElementTagTests.COMMAND_NAME)), bindingResult);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        Assert.assertEquals(1, editor.count);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("stringArray", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
        Assert.assertEquals("   foo", checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withMultiValueIntegerWithEditor() throws Exception {
        this.tag.setPath("someIntegerArray");
        this.tag.setValue("   1");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(this.bean, AbstractHtmlElementTagTests.COMMAND_NAME);
        CheckboxTagTests.MyIntegerEditor editor = new CheckboxTagTests.MyIntegerEditor();
        bindingResult.getPropertyEditorRegistry().registerCustomEditor(Integer.class, editor);
        getPageContext().getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + (AbstractHtmlElementTagTests.COMMAND_NAME)), bindingResult);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        Assert.assertEquals(1, editor.count);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("someIntegerArray", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
        Assert.assertEquals("   1", checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withCollection() throws Exception {
        this.tag.setPath("someList");
        this.tag.setValue("foo");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("someList", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
        Assert.assertEquals("foo", checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withObjectChecked() throws Exception {
        this.tag.setPath("date");
        this.tag.setValue(getDate());
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("date", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
        Assert.assertEquals(getDate().toString(), checkboxElement.attribute("value").getValue());
    }

    @Test
    public void withObjectUnchecked() throws Exception {
        this.tag.setPath("date");
        Date date = new Date();
        this.tag.setValue(date);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("date", checkboxElement.attribute("name").getValue());
        Assert.assertNull(checkboxElement.attribute("checked"));
        Assert.assertEquals(date.toString(), checkboxElement.attribute("value").getValue());
    }

    @Test
    public void collectionOfColoursSelected() throws Exception {
        this.tag.setPath("otherColours");
        this.tag.setValue("RED");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("otherColours", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
    }

    @Test
    public void collectionOfColoursNotSelected() throws Exception {
        this.tag.setPath("otherColours");
        this.tag.setValue("PURPLE");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("otherColours", checkboxElement.attribute("name").getValue());
        Assert.assertNull(checkboxElement.attribute("checked"));
    }

    @Test
    public void collectionOfPetsAsString() throws Exception {
        this.tag.setPath("pets");
        this.tag.setValue("Spot");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("pets", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
    }

    @Test
    public void collectionOfPetsAsStringNotSelected() throws Exception {
        this.tag.setPath("pets");
        this.tag.setValue("Santa's Little Helper");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("pets", checkboxElement.attribute("name").getValue());
        Assert.assertNull(checkboxElement.attribute("checked"));
    }

    @Test
    public void collectionOfPets() throws Exception {
        this.tag.setPath("pets");
        this.tag.setValue(new Pet("Rudiger"));
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("pets", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("Rudiger", checkboxElement.attribute("value").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
    }

    @Test
    public void collectionOfPetsNotSelected() throws Exception {
        this.tag.setPath("pets");
        this.tag.setValue(new Pet("Santa's Little Helper"));
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("pets", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("Santa's Little Helper", checkboxElement.attribute("value").getValue());
        Assert.assertNull(checkboxElement.attribute("checked"));
    }

    @Test
    public void collectionOfPetsWithEditor() throws Exception {
        this.tag.setPath("pets");
        this.tag.setValue(new ItemPet("Rudiger"));
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
        Element checkboxElement = ((Element) (document.getRootElement().elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("pets", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("Rudiger", checkboxElement.attribute("value").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
    }

    @Test
    public void withNullValue() throws Exception {
        try {
            this.tag.setPath("name");
            this.tag.doStartTag();
            Assert.fail("Should not be able to render with a null value when binding to a non-boolean.");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void hiddenElementOmittedOnDisabled() throws Exception {
        this.tag.setPath("someBoolean");
        this.tag.setDisabled(true);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        // wrap the output so it is valid XML
        output = ("<doc>" + output) + "</doc>";
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(output));
        Element rootElement = document.getRootElement();
        Assert.assertEquals("Both tag and hidden element rendered incorrectly", 1, rootElement.elements().size());
        Element checkboxElement = ((Element) (rootElement.elements().get(0)));
        Assert.assertEquals("input", checkboxElement.getName());
        Assert.assertEquals("checkbox", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("someBoolean", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
        Assert.assertEquals("true", checkboxElement.attribute("value").getValue());
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

    private class MyStringTrimmerEditor extends StringTrimmerEditor {
        public int count = 0;

        public MyStringTrimmerEditor() {
            super(false);
        }

        @Override
        public void setAsText(String text) {
            (this.count)++;
            super.setAsText(text);
        }
    }

    private class MyIntegerEditor extends PropertyEditorSupport {
        public int count = 0;

        @Override
        public void setAsText(String text) {
            (this.count)++;
            setValue(new Integer(text.trim()));
        }
    }
}

