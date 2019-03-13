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
import ErrorsTag.MESSAGES_ATTRIBUTE;
import PageContext.APPLICATION_SCOPE;
import PageContext.PAGE_SCOPE;
import PageContext.REQUEST_SCOPE;
import PageContext.SESSION_SCOPE;
import Tag.EVAL_PAGE;
import Tag.SKIP_BODY;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockBodyContent;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.validation.Errors;


/**
 *
 *
 * @author Rob Harrop
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Jeremy Grelle
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ErrorsTagTests extends AbstractFormTagTests {
    private static final String COMMAND_NAME = "testBean";

    private ErrorsTag tag;

    @Test
    public void withExplicitNonWhitespaceBodyContent() throws Exception {
        String mockContent = "This is some explicit body content";
        this.tag.setBodyContent(new MockBodyContent(mockContent, getWriter()));
        // construct an errors instance of the tag
        TestBean target = new TestBean();
        target.setName("Rob Harrop");
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(target, ErrorsTagTests.COMMAND_NAME);
        errors.rejectValue("name", "some.code", "Default Message");
        exposeBindingResult(errors);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        Assert.assertEquals(mockContent, getOutput());
    }

    @Test
    public void withExplicitWhitespaceBodyContent() throws Exception {
        this.tag.setBodyContent(new MockBodyContent("\t\n   ", getWriter()));
        // construct an errors instance of the tag
        TestBean target = new TestBean();
        target.setName("Rob Harrop");
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(target, ErrorsTagTests.COMMAND_NAME);
        errors.rejectValue("name", "some.code", "Default Message");
        exposeBindingResult(errors);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertElementTagOpened(output);
        assertElementTagClosed(output);
        assertContainsAttribute(output, "id", "name.errors");
        assertBlockTagContains(output, "Default Message");
    }

    @Test
    public void withExplicitEmptyWhitespaceBodyContent() throws Exception {
        this.tag.setBodyContent(new MockBodyContent("", getWriter()));
        // construct an errors instance of the tag
        TestBean target = new TestBean();
        target.setName("Rob Harrop");
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(target, ErrorsTagTests.COMMAND_NAME);
        errors.rejectValue("name", "some.code", "Default Message");
        exposeBindingResult(errors);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertElementTagOpened(output);
        assertElementTagClosed(output);
        assertContainsAttribute(output, "id", "name.errors");
        assertBlockTagContains(output, "Default Message");
    }

    @Test
    public void withErrors() throws Exception {
        // construct an errors instance of the tag
        TestBean target = new TestBean();
        target.setName("Rob Harrop");
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(target, ErrorsTagTests.COMMAND_NAME);
        errors.rejectValue("name", "some.code", "Default Message");
        errors.rejectValue("name", "too.short", "Too Short");
        exposeBindingResult(errors);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertElementTagOpened(output);
        assertElementTagClosed(output);
        assertContainsAttribute(output, "id", "name.errors");
        assertBlockTagContains(output, "<br/>");
        assertBlockTagContains(output, "Default Message");
        assertBlockTagContains(output, "Too Short");
    }

    @Test
    public void withErrorsAndDynamicAttributes() throws Exception {
        String dynamicAttribute1 = "attr1";
        String dynamicAttribute2 = "attr2";
        this.tag.setDynamicAttribute(null, dynamicAttribute1, dynamicAttribute1);
        this.tag.setDynamicAttribute(null, dynamicAttribute2, dynamicAttribute2);
        // construct an errors instance of the tag
        TestBean target = new TestBean();
        target.setName("Rob Harrop");
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(target, ErrorsTagTests.COMMAND_NAME);
        errors.rejectValue("name", "some.code", "Default Message");
        errors.rejectValue("name", "too.short", "Too Short");
        exposeBindingResult(errors);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertElementTagOpened(output);
        assertElementTagClosed(output);
        assertContainsAttribute(output, "id", "name.errors");
        assertContainsAttribute(output, dynamicAttribute1, dynamicAttribute1);
        assertContainsAttribute(output, dynamicAttribute2, dynamicAttribute2);
        assertBlockTagContains(output, "<br/>");
        assertBlockTagContains(output, "Default Message");
        assertBlockTagContains(output, "Too Short");
    }

    @Test
    public void withEscapedErrors() throws Exception {
        // construct an errors instance of the tag
        TestBean target = new TestBean();
        target.setName("Rob Harrop");
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(target, ErrorsTagTests.COMMAND_NAME);
        errors.rejectValue("name", "some.code", "Default <> Message");
        errors.rejectValue("name", "too.short", "Too & Short");
        exposeBindingResult(errors);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertElementTagOpened(output);
        assertElementTagClosed(output);
        assertContainsAttribute(output, "id", "name.errors");
        assertBlockTagContains(output, "<br/>");
        assertBlockTagContains(output, "Default &lt;&gt; Message");
        assertBlockTagContains(output, "Too &amp; Short");
    }

    @Test
    public void withNonEscapedErrors() throws Exception {
        this.tag.setHtmlEscape(false);
        // construct an errors instance of the tag
        TestBean target = new TestBean();
        target.setName("Rob Harrop");
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(target, ErrorsTagTests.COMMAND_NAME);
        errors.rejectValue("name", "some.code", "Default <> Message");
        errors.rejectValue("name", "too.short", "Too & Short");
        exposeBindingResult(errors);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertElementTagOpened(output);
        assertElementTagClosed(output);
        assertContainsAttribute(output, "id", "name.errors");
        assertBlockTagContains(output, "<br/>");
        assertBlockTagContains(output, "Default <> Message");
        assertBlockTagContains(output, "Too & Short");
    }

    @Test
    public void withErrorsAndCustomElement() throws Exception {
        // construct an errors instance of the tag
        TestBean target = new TestBean();
        target.setName("Rob Harrop");
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(target, ErrorsTagTests.COMMAND_NAME);
        errors.rejectValue("name", "some.code", "Default Message");
        errors.rejectValue("name", "too.short", "Too Short");
        exposeBindingResult(errors);
        this.tag.setElement("div");
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        assertElementTagOpened(output);
        assertElementTagClosed(output);
        assertContainsAttribute(output, "id", "name.errors");
        assertBlockTagContains(output, "<br/>");
        assertBlockTagContains(output, "Default Message");
        assertBlockTagContains(output, "Too Short");
    }

    @Test
    public void withoutErrors() throws Exception {
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(new TestBean(), "COMMAND_NAME");
        exposeBindingResult(errors);
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        Assert.assertEquals(0, output.length());
    }

    @Test
    public void withoutErrorsInstance() throws Exception {
        int result = this.tag.doStartTag();
        Assert.assertEquals(SKIP_BODY, result);
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        String output = getOutput();
        Assert.assertEquals(0, output.length());
    }

    @Test
    public void asBodyTag() throws Exception {
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(new TestBean(), "COMMAND_NAME");
        errors.rejectValue("name", "some.code", "Default Message");
        errors.rejectValue("name", "too.short", "Too Short");
        exposeBindingResult(errors);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        Assert.assertNotNull(getPageContext().getAttribute(MESSAGES_ATTRIBUTE));
        String bodyContent = "Foo";
        this.tag.setBodyContent(new MockBodyContent(bodyContent, getWriter()));
        this.tag.doEndTag();
        this.tag.doFinally();
        Assert.assertEquals(bodyContent, getOutput());
        Assert.assertNull(getPageContext().getAttribute(MESSAGES_ATTRIBUTE));
    }

    @Test
    public void asBodyTagWithExistingMessagesAttribute() throws Exception {
        String existingAttribute = "something";
        getPageContext().setAttribute(MESSAGES_ATTRIBUTE, existingAttribute);
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(new TestBean(), "COMMAND_NAME");
        errors.rejectValue("name", "some.code", "Default Message");
        errors.rejectValue("name", "too.short", "Too Short");
        exposeBindingResult(errors);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        Assert.assertNotNull(getPageContext().getAttribute(MESSAGES_ATTRIBUTE));
        Assert.assertTrue(((getPageContext().getAttribute(MESSAGES_ATTRIBUTE)) instanceof List));
        String bodyContent = "Foo";
        this.tag.setBodyContent(new MockBodyContent(bodyContent, getWriter()));
        this.tag.doEndTag();
        this.tag.doFinally();
        Assert.assertEquals(bodyContent, getOutput());
        Assert.assertEquals(existingAttribute, getPageContext().getAttribute(MESSAGES_ATTRIBUTE));
    }

    /**
     * https://jira.spring.io/browse/SPR-2788
     */
    @Test
    public void asBodyTagWithErrorsAndExistingMessagesAttributeInNonPageScopeAreNotClobbered() throws Exception {
        String existingAttribute = "something";
        getPageContext().setAttribute(MESSAGES_ATTRIBUTE, existingAttribute, APPLICATION_SCOPE);
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(new TestBean(), "COMMAND_NAME");
        errors.rejectValue("name", "some.code", "Default Message");
        errors.rejectValue("name", "too.short", "Too Short");
        exposeBindingResult(errors);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_BUFFERED, result);
        Assert.assertNotNull(getPageContext().getAttribute(MESSAGES_ATTRIBUTE));
        Assert.assertTrue(((getPageContext().getAttribute(MESSAGES_ATTRIBUTE)) instanceof List));
        String bodyContent = "Foo";
        this.tag.setBodyContent(new MockBodyContent(bodyContent, getWriter()));
        this.tag.doEndTag();
        this.tag.doFinally();
        Assert.assertEquals(bodyContent, getOutput());
        Assert.assertEquals(existingAttribute, getPageContext().getAttribute(MESSAGES_ATTRIBUTE, APPLICATION_SCOPE));
    }

    /**
     * https://jira.spring.io/browse/SPR-2788
     */
    @Test
    public void asBodyTagWithNoErrorsAndExistingMessagesAttributeInApplicationScopeAreNotClobbered() throws Exception {
        assertWhenNoErrorsExistingMessagesInScopeAreNotClobbered(APPLICATION_SCOPE);
    }

    /**
     * https://jira.spring.io/browse/SPR-2788
     */
    @Test
    public void asBodyTagWithNoErrorsAndExistingMessagesAttributeInSessionScopeAreNotClobbered() throws Exception {
        assertWhenNoErrorsExistingMessagesInScopeAreNotClobbered(SESSION_SCOPE);
    }

    /**
     * https://jira.spring.io/browse/SPR-2788
     */
    @Test
    public void asBodyTagWithNoErrorsAndExistingMessagesAttributeInPageScopeAreNotClobbered() throws Exception {
        assertWhenNoErrorsExistingMessagesInScopeAreNotClobbered(PAGE_SCOPE);
    }

    /**
     * https://jira.spring.io/browse/SPR-2788
     */
    @Test
    public void asBodyTagWithNoErrorsAndExistingMessagesAttributeInRequestScopeAreNotClobbered() throws Exception {
        assertWhenNoErrorsExistingMessagesInScopeAreNotClobbered(REQUEST_SCOPE);
    }

    /**
     * https://jira.spring.io/browse/SPR-4005
     */
    @Test
    public void omittedPathMatchesObjectErrorsOnly() throws Exception {
        this.tag.setPath(null);
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(new TestBean(), "COMMAND_NAME");
        errors.reject("some.code", "object error");
        errors.rejectValue("name", "some.code", "field error");
        exposeBindingResult(errors);
        this.tag.doStartTag();
        Assert.assertNotNull(getPageContext().getAttribute(MESSAGES_ATTRIBUTE));
        this.tag.doEndTag();
        String output = getOutput();
        Assert.assertTrue(output.contains("id=\"testBean.errors\""));
        Assert.assertTrue(output.contains("object error"));
        Assert.assertFalse(output.contains("field error"));
    }

    @Test
    public void specificPathMatchesSpecificFieldOnly() throws Exception {
        this.tag.setPath("name");
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(new TestBean(), "COMMAND_NAME");
        errors.reject("some.code", "object error");
        errors.rejectValue("name", "some.code", "field error");
        exposeBindingResult(errors);
        this.tag.doStartTag();
        Assert.assertNotNull(getPageContext().getAttribute(MESSAGES_ATTRIBUTE));
        this.tag.doEndTag();
        String output = getOutput();
        Assert.assertTrue(output.contains("id=\"name.errors\""));
        Assert.assertFalse(output.contains("object error"));
        Assert.assertTrue(output.contains("field error"));
    }

    @Test
    public void starMatchesAllErrors() throws Exception {
        this.tag.setPath("*");
        Errors errors = new org.springframework.validation.BeanPropertyBindingResult(new TestBean(), "COMMAND_NAME");
        errors.reject("some.code", "object error");
        errors.rejectValue("name", "some.code", "field error");
        exposeBindingResult(errors);
        this.tag.doStartTag();
        Assert.assertNotNull(getPageContext().getAttribute(MESSAGES_ATTRIBUTE));
        this.tag.doEndTag();
        String output = getOutput();
        Assert.assertTrue(output.contains("id=\"testBean.errors\""));
        Assert.assertTrue(output.contains("object error"));
        Assert.assertTrue(output.contains("field error"));
    }
}

