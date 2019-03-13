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
import javax.servlet.jsp.JspException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Test;
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
public class RadioButtonTagTests extends AbstractFormTagTests {
    private RadioButtonTag tag;

    private TestBean bean;

    @Test
    public void withCheckedValue() throws Exception {
        String dynamicAttribute1 = "attr1";
        String dynamicAttribute2 = "attr2";
        this.tag.setPath("sex");
        this.tag.setValue("M");
        this.tag.setDynamicAttribute(null, dynamicAttribute1, dynamicAttribute1);
        this.tag.setDynamicAttribute(null, dynamicAttribute2, dynamicAttribute2);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        assertTagOpened(output);
        assertTagClosed(output);
        assertContainsAttribute(output, "name", "sex");
        assertContainsAttribute(output, "type", "radio");
        assertContainsAttribute(output, "value", "M");
        assertContainsAttribute(output, "checked", "checked");
        assertContainsAttribute(output, dynamicAttribute1, dynamicAttribute1);
        assertContainsAttribute(output, dynamicAttribute2, dynamicAttribute2);
    }

    @Test
    public void withCheckedValueAndDynamicAttributes() throws Exception {
        this.tag.setPath("sex");
        this.tag.setValue("M");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        assertTagOpened(output);
        assertTagClosed(output);
        assertContainsAttribute(output, "name", "sex");
        assertContainsAttribute(output, "type", "radio");
        assertContainsAttribute(output, "value", "M");
        assertContainsAttribute(output, "checked", "checked");
    }

    @Test
    public void withCheckedObjectValue() throws Exception {
        this.tag.setPath("myFloat");
        this.tag.setValue(getFloat());
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        assertTagOpened(output);
        assertTagClosed(output);
        assertContainsAttribute(output, "name", "myFloat");
        assertContainsAttribute(output, "type", "radio");
        assertContainsAttribute(output, "value", getFloat().toString());
        assertContainsAttribute(output, "checked", "checked");
    }

    @Test
    public void withCheckedObjectValueAndEditor() throws Exception {
        this.tag.setPath("myFloat");
        this.tag.setValue("F12.99");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(this.bean, AbstractHtmlElementTagTests.COMMAND_NAME);
        RadioButtonTagTests.MyFloatEditor editor = new RadioButtonTagTests.MyFloatEditor();
        bindingResult.getPropertyEditorRegistry().registerCustomEditor(Float.class, editor);
        getPageContext().getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + (AbstractHtmlElementTagTests.COMMAND_NAME)), bindingResult);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        assertTagOpened(output);
        assertTagClosed(output);
        assertContainsAttribute(output, "name", "myFloat");
        assertContainsAttribute(output, "type", "radio");
        assertContainsAttribute(output, "value", ("F" + (getFloat().toString())));
        assertContainsAttribute(output, "checked", "checked");
    }

    @Test
    public void withUncheckedObjectValue() throws Exception {
        Float value = new Float("99.45");
        this.tag.setPath("myFloat");
        this.tag.setValue(value);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        assertTagOpened(output);
        assertTagClosed(output);
        assertContainsAttribute(output, "name", "myFloat");
        assertContainsAttribute(output, "type", "radio");
        assertContainsAttribute(output, "value", value.toString());
        assertAttributeNotPresent(output, "checked");
    }

    @Test
    public void withUncheckedValue() throws Exception {
        this.tag.setPath("sex");
        this.tag.setValue("F");
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        String output = getOutput();
        assertTagOpened(output);
        assertTagClosed(output);
        assertContainsAttribute(output, "name", "sex");
        assertContainsAttribute(output, "type", "radio");
        assertContainsAttribute(output, "value", "F");
        assertAttributeNotPresent(output, "checked");
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
        Assert.assertEquals("radio", checkboxElement.attribute("type").getValue());
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
        Assert.assertEquals("radio", checkboxElement.attribute("type").getValue());
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
        Assert.assertEquals("radio", checkboxElement.attribute("type").getValue());
        Assert.assertEquals("pets", checkboxElement.attribute("name").getValue());
        Assert.assertEquals("Rudiger", checkboxElement.attribute("value").getValue());
        Assert.assertEquals("checked", checkboxElement.attribute("checked").getValue());
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

    private static class MyFloatEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue(text.substring(1));
        }

        @Override
        public String getAsText() {
            return "F" + (getValue());
        }
    }
}

