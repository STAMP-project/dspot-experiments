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


import BodyTag.EVAL_BODY_BUFFERED;
import SelectTag.LIST_VALUE_PAGE_ATTRIBUTE;
import Tag.EVAL_PAGE;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.mock.web.test.MockBodyContent;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.support.BindStatus;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 * @author Jeremy Grelle
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class OptionTagTests extends AbstractHtmlElementTagTests {
    private static final String ARRAY_SOURCE = "abc,123,def";

    private static final String[] ARRAY = StringUtils.commaDelimitedListToStringArray(OptionTagTests.ARRAY_SOURCE);

    private OptionTag tag;

    private SelectTag parentTag;

    @Test
    public void canBeDisabledEvenWhenSelected() throws Exception {
        String selectName = "testBean.name";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false);
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        this.tag.setValue("bar");
        this.tag.setLabel("Bar");
        this.tag.setDisabled(true);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertContainsAttribute(output, "value", "bar");
        assertContainsAttribute(output, "disabled", "disabled");
        assertBlockTagContains(output, "Bar");
    }

    @Test
    public void renderNotSelected() throws Exception {
        String selectName = "testBean.name";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false);
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        this.tag.setValue("bar");
        this.tag.setLabel("Bar");
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertContainsAttribute(output, "value", "bar");
        assertBlockTagContains(output, "Bar");
    }

    @Test
    public void renderWithDynamicAttributes() throws Exception {
        String dynamicAttribute1 = "attr1";
        String dynamicAttribute2 = "attr2";
        String selectName = "testBean.name";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false);
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        this.tag.setValue("bar");
        this.tag.setLabel("Bar");
        this.tag.setDynamicAttribute(null, dynamicAttribute1, dynamicAttribute1);
        this.tag.setDynamicAttribute(null, dynamicAttribute2, dynamicAttribute2);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertContainsAttribute(output, "value", "bar");
        assertContainsAttribute(output, dynamicAttribute1, dynamicAttribute1);
        assertContainsAttribute(output, dynamicAttribute2, dynamicAttribute2);
        assertBlockTagContains(output, "Bar");
    }

    @Test
    public void renderSelected() throws Exception {
        String selectName = "testBean.name";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false);
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        this.tag.setId("myOption");
        this.tag.setValue("foo");
        this.tag.setLabel("Foo");
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertContainsAttribute(output, "id", "myOption");
        assertContainsAttribute(output, "value", "foo");
        assertContainsAttribute(output, "selected", "selected");
        assertBlockTagContains(output, "Foo");
    }

    @Test
    public void withNoLabel() throws Exception {
        String selectName = "testBean.name";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false);
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        this.tag.setValue("bar");
        this.tag.setCssClass("myClass");
        this.tag.setOnclick("CLICK");
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertContainsAttribute(output, "value", "bar");
        assertContainsAttribute(output, "class", "myClass");
        assertContainsAttribute(output, "onclick", "CLICK");
        assertBlockTagContains(output, "bar");
    }

    @Test
    public void withoutContext() throws Exception {
        this.tag.setParent(null);
        this.tag.setValue("foo");
        this.tag.setLabel("Foo");
        try {
            tag.doStartTag();
            Assert.fail("Must not be able to use <option> tag without exposed context.");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void withPropertyEditor() throws Exception {
        String selectName = "testBean.stringArray";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false) {
            @Override
            public PropertyEditor getEditor() {
                return new StringArrayPropertyEditor();
            }
        };
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        this.tag.setValue(OptionTagTests.ARRAY_SOURCE);
        this.tag.setLabel("someArray");
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertContainsAttribute(output, "value", OptionTagTests.ARRAY_SOURCE);
        assertContainsAttribute(output, "selected", "selected");
        assertBlockTagContains(output, "someArray");
    }

    @Test
    public void withPropertyEditorStringComparison() throws Exception {
        final PropertyEditor testBeanEditor = new OptionTagTests.TestBeanPropertyEditor();
        testBeanEditor.setValue(new TestBean("Sally"));
        String selectName = "testBean.spouse";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false) {
            @Override
            public PropertyEditor getEditor() {
                return testBeanEditor;
            }
        };
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        this.tag.setValue("Sally");
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertContainsAttribute(output, "value", "Sally");
        assertContainsAttribute(output, "selected", "selected");
        assertBlockTagContains(output, "Sally");
    }

    @Test
    public void withCustomObjectSelected() throws Exception {
        String selectName = "testBean.someNumber";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false);
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        this.tag.setValue(new Float(12.34));
        this.tag.setLabel("GBP 12.34");
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertContainsAttribute(output, "value", "12.34");
        assertContainsAttribute(output, "selected", "selected");
        assertBlockTagContains(output, "GBP 12.34");
    }

    @Test
    public void withCustomObjectNotSelected() throws Exception {
        String selectName = "testBean.someNumber";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false);
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        this.tag.setValue(new Float(12.35));
        this.tag.setLabel("GBP 12.35");
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertContainsAttribute(output, "value", "12.35");
        assertAttributeNotPresent(output, "selected");
        assertBlockTagContains(output, "GBP 12.35");
    }

    @Test
    public void withCustomObjectAndEditorSelected() throws Exception {
        final PropertyEditor floatEditor = new SimpleFloatEditor();
        floatEditor.setValue(new Float("12.34"));
        String selectName = "testBean.someNumber";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false) {
            @Override
            public PropertyEditor getEditor() {
                return floatEditor;
            }
        };
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        this.tag.setValue(new Float(12.34));
        this.tag.setLabel("12.34f");
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertContainsAttribute(output, "selected", "selected");
        assertBlockTagContains(output, "12.34f");
    }

    @Test
    public void withCustomObjectAndEditorNotSelected() throws Exception {
        final PropertyEditor floatEditor = new SimpleFloatEditor();
        String selectName = "testBean.someNumber";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false) {
            @Override
            public PropertyEditor getEditor() {
                return floatEditor;
            }
        };
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        this.tag.setValue(new Float(12.35));
        this.tag.setLabel("12.35f");
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertAttributeNotPresent(output, "selected");
        assertBlockTagContains(output, "12.35f");
    }

    @Test
    public void asBodyTag() throws Exception {
        String selectName = "testBean.name";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false);
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        String bodyContent = "some content";
        this.tag.setValue("foo");
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        this.tag.setBodyContent(new MockBodyContent(bodyContent, getWriter()));
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertContainsAttribute(output, "selected", "selected");
        assertBlockTagContains(output, bodyContent);
    }

    @Test
    public void asBodyTagSelected() throws Exception {
        String selectName = "testBean.name";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false);
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        String bodyContent = "some content";
        this.tag.setValue("Rob Harrop");
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        this.tag.setBodyContent(new MockBodyContent(bodyContent, getWriter()));
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertBlockTagContains(output, bodyContent);
    }

    @Test
    public void asBodyTagCollapsed() throws Exception {
        String selectName = "testBean.name";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false);
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        String bodyContent = "some content";
        this.tag.setValue(bodyContent);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        this.tag.setBodyContent(new MockBodyContent(bodyContent, getWriter()));
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertOptionTagOpened(output);
        assertOptionTagClosed(output);
        assertContainsAttribute(output, "value", bodyContent);
        assertBlockTagContains(output, bodyContent);
    }

    @Test
    public void asBodyTagWithEditor() throws Exception {
        String selectName = "testBean.stringArray";
        BindStatus bindStatus = new BindStatus(getRequestContext(), selectName, false) {
            @Override
            public PropertyEditor getEditor() {
                return new OptionTagTests.RulesVariantEditor();
            }
        };
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        OptionTagTests.RulesVariant rulesVariant = new OptionTagTests.RulesVariant("someRules", "someVariant");
        this.tag.setValue(rulesVariant);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        Assert.assertEquals(rulesVariant, getPageContext().getAttribute("value"));
        Assert.assertEquals(rulesVariant.toId(), getPageContext().getAttribute("displayValue"));
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
    }

    @Test
    public void multiBind() throws Exception {
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(new TestBean(), "testBean");
        result.getPropertyAccessor().registerCustomEditor(TestBean.class, "friends", new OptionTagTests.FriendEditor());
        exposeBindingResult(result);
        BindStatus bindStatus = new BindStatus(getRequestContext(), "testBean.friends", false);
        getPageContext().setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, bindStatus);
        this.tag.setValue(new TestBean("foo"));
        this.tag.doStartTag();
        this.tag.doEndTag();
        Assert.assertEquals("<option value=\"foo\">foo</option>", getOutput());
    }

    @Test
    public void optionTagNotNestedWithinSelectTag() throws Exception {
        try {
            tag.setParent(null);
            tag.setValue("foo");
            tag.doStartTag();
            Assert.fail("Must throw an IllegalStateException when not nested within a <select/> tag.");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    private static class TestBeanPropertyEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue(new TestBean((text + "k"), 123));
        }

        @Override
        public String getAsText() {
            return getName();
        }
    }

    @SuppressWarnings("serial")
    public static class RulesVariant implements Serializable {
        private String rules;

        private String variant;

        public RulesVariant(String rules, String variant) {
            this.setRules(rules);
            this.setVariant(variant);
        }

        private void setRules(String rules) {
            this.rules = rules;
        }

        public String getRules() {
            return rules;
        }

        private void setVariant(String variant) {
            this.variant = variant;
        }

        public String getVariant() {
            return variant;
        }

        public String toId() {
            if ((this.variant) != null) {
                return ((this.rules) + "-") + (this.variant);
            } else {
                return rules;
            }
        }

        public static OptionTagTests.RulesVariant fromId(String id) {
            String[] s = id.split("-", 2);
            String rules = s[0];
            String variant = ((s.length) > 1) ? s[1] : null;
            return new OptionTagTests.RulesVariant(rules, variant);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof OptionTagTests.RulesVariant) {
                OptionTagTests.RulesVariant other = ((OptionTagTests.RulesVariant) (obj));
                return this.toId().equals(other.toId());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.toId().hashCode();
        }
    }

    public class RulesVariantEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue(OptionTagTests.RulesVariant.fromId(text));
        }

        @Override
        public String getAsText() {
            OptionTagTests.RulesVariant rulesVariant = ((OptionTagTests.RulesVariant) (getValue()));
            return rulesVariant.toId();
        }
    }

    private static class FriendEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue(new TestBean(text));
        }

        @Override
        public String getAsText() {
            return getName();
        }
    }
}

