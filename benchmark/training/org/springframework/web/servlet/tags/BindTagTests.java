/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.servlet.tags;


import BindErrorsTag.ERRORS_VARIABLE_NAME;
import BindTag.STATUS_VARIABLE_NAME;
import NestedPathTag.NESTED_PATH_VARIABLE_NAME;
import PageContext.REQUEST_SCOPE;
import Tag.EVAL_BODY_INCLUDE;
import Tag.EVAL_PAGE;
import java.beans.PropertyEditorSupport;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.tests.sample.beans.IndexedTestBean;
import org.springframework.tests.sample.beans.NestedTestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.FormTag;
import org.springframework.web.servlet.tags.form.TagWriter;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Alef Arendsen
 * @author Mark Fisher
 */
public class BindTagTests extends AbstractTagTests {
    @Test
    public void bindTagWithoutErrors() throws JspException {
        PageContext pc = createPageContext();
        Errors errors = getBindingResult();
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", ((status.getExpression()) == null));
        Assert.assertTrue("Correct value", ((status.getValue()) == null));
        Assert.assertTrue("Correct displayValue", "".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", (!(status.isError())));
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 0));
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 0));
        Assert.assertTrue("Correct errorCode", "".equals(status.getErrorCode()));
        Assert.assertTrue("Correct errorMessage", "".equals(status.getErrorMessage()));
        Assert.assertTrue("Correct errorMessagesAsString", "".equals(status.getErrorMessagesAsString(",")));
    }

    @Test
    public void bindTagWithGlobalErrors() throws JspException {
        PageContext pc = createPageContext();
        Errors errors = getBindingResult();
        errors.reject("code1", "message1");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", ((status.getExpression()) == null));
        Assert.assertTrue("Correct value", ((status.getValue()) == null));
        Assert.assertTrue("Correct displayValue", "".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 1));
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 1));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCode()));
        Assert.assertTrue("Correct errorMessage", "message1".equals(status.getErrorMessage()));
        Assert.assertTrue("Correct errorMessagesAsString", "message1".equals(status.getErrorMessagesAsString(",")));
        tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.*");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "*".equals(status.getExpression()));
        Assert.assertTrue("Correct value", ((status.getValue()) == null));
        Assert.assertTrue("Correct displayValue", "".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 1));
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 1));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCode()));
        Assert.assertTrue("Correct errorMessage", "message1".equals(status.getErrorMessage()));
        Assert.assertTrue("Correct errorMessagesAsString", "message1".equals(status.getErrorMessagesAsString(",")));
    }

    @Test
    public void bindTagWithGlobalErrorsAndNoDefaultMessage() throws JspException {
        PageContext pc = createPageContext();
        Errors errors = getBindingResult();
        errors.reject("code1");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", ((status.getExpression()) == null));
        Assert.assertTrue("Correct value", ((status.getValue()) == null));
        Assert.assertTrue("Correct displayValue", "".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 1));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCode()));
        tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.*");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "*".equals(status.getExpression()));
        Assert.assertTrue("Correct value", ((status.getValue()) == null));
        Assert.assertTrue("Correct displayValue", "".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 1));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCode()));
    }

    @Test
    public void bindTagWithGlobalErrorsAndDefaultMessageOnly() throws JspException {
        PageContext pc = createPageContext();
        Errors errors = getBindingResult();
        errors.reject(null, "message1");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", ((status.getExpression()) == null));
        Assert.assertTrue("Correct value", ((status.getValue()) == null));
        Assert.assertTrue("Correct displayValue", "".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 1));
        Assert.assertTrue("Correct errorMessage", "message1".equals(status.getErrorMessage()));
        Assert.assertTrue("Correct errorMessagesAsString", "message1".equals(status.getErrorMessagesAsString(",")));
        tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.*");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "*".equals(status.getExpression()));
        Assert.assertTrue("Correct value", ((status.getValue()) == null));
        Assert.assertTrue("Correct displayValue", "".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 1));
        Assert.assertTrue("Correct errorMessage", "message1".equals(status.getErrorMessage()));
        Assert.assertTrue("Correct errorMessagesAsString", "message1".equals(status.getErrorMessagesAsString(",")));
    }

    @Test
    public void bindStatusGetErrorMessagesAsString() throws JspException {
        // one error (should not include delimiter)
        PageContext pc = createPageContext();
        Errors errors = getBindingResult();
        errors.reject("code1", null, "message1");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb");
        tag.doStartTag();
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertEquals("Error messages String should be 'message1'", "message1", status.getErrorMessagesAsString(","));
        // two errors
        pc = createPageContext();
        errors = new ServletRequestDataBinder(new TestBean(), "tb").getBindingResult();
        errors.reject("code1", null, "message1");
        errors.reject("code1", null, "message2");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb");
        tag.doStartTag();
        status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertEquals("Error messages String should be 'message1,message2'", "message1,message2", status.getErrorMessagesAsString(","));
        // no errors
        pc = createPageContext();
        errors = new ServletRequestDataBinder(new TestBean(), "tb").getBindingResult();
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb");
        tag.doStartTag();
        status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertEquals("Error messages String should be ''", "", status.getErrorMessagesAsString(","));
    }

    @Test
    public void bindTagWithFieldErrors() throws JspException {
        PageContext pc = createPageContext();
        TestBean tb = new TestBean();
        tb.setName("name1");
        Errors errors = getBindingResult();
        errors.rejectValue("name", "code1", "message & 1");
        errors.rejectValue("name", "code2", "message2");
        errors.rejectValue("age", "code2", "message2");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.name");
        tag.setHtmlEscape(true);
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "name".equals(status.getExpression()));
        Assert.assertTrue("Correct value", "name1".equals(status.getValue()));
        Assert.assertTrue("Correct displayValue", "name1".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 2));
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 2));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCode()));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCodes()[0]));
        Assert.assertTrue("Correct errorCode", "code2".equals(status.getErrorCodes()[1]));
        Assert.assertTrue("Correct errorMessage", "message &amp; 1".equals(status.getErrorMessage()));
        Assert.assertTrue("Correct errorMessage", "message &amp; 1".equals(status.getErrorMessages()[0]));
        Assert.assertTrue("Correct errorMessage", "message2".equals(status.getErrorMessages()[1]));
        Assert.assertTrue("Correct errorMessagesAsString", "message &amp; 1 - message2".equals(status.getErrorMessagesAsString(" - ")));
        tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.age");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "age".equals(status.getExpression()));
        Assert.assertTrue("Correct value", new Integer(0).equals(status.getValue()));
        Assert.assertTrue("Correct displayValue", "0".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 1));
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 1));
        Assert.assertTrue("Correct errorCode", "code2".equals(status.getErrorCode()));
        Assert.assertTrue("Correct errorMessage", "message2".equals(status.getErrorMessage()));
        Assert.assertTrue("Correct errorMessagesAsString", "message2".equals(status.getErrorMessagesAsString(" - ")));
        tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.*");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "*".equals(status.getExpression()));
        Assert.assertTrue("Correct value", ((status.getValue()) == null));
        Assert.assertTrue("Correct displayValue", "".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 3));
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 3));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCode()));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCodes()[0]));
        Assert.assertTrue("Correct errorCode", "code2".equals(status.getErrorCodes()[1]));
        Assert.assertTrue("Correct errorCode", "code2".equals(status.getErrorCodes()[2]));
        Assert.assertTrue("Correct errorMessage", "message & 1".equals(status.getErrorMessage()));
        Assert.assertTrue("Correct errorMessage", "message & 1".equals(status.getErrorMessages()[0]));
        Assert.assertTrue("Correct errorMessage", "message2".equals(status.getErrorMessages()[1]));
        Assert.assertTrue("Correct errorMessage", "message2".equals(status.getErrorMessages()[2]));
    }

    @Test
    public void bindTagWithFieldErrorsAndNoDefaultMessage() throws JspException {
        PageContext pc = createPageContext();
        TestBean tb = new TestBean();
        tb.setName("name1");
        Errors errors = getBindingResult();
        errors.rejectValue("name", "code1");
        errors.rejectValue("name", "code2");
        errors.rejectValue("age", "code2");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.name");
        tag.setHtmlEscape(true);
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "name".equals(status.getExpression()));
        Assert.assertTrue("Correct value", "name1".equals(status.getValue()));
        Assert.assertTrue("Correct displayValue", "name1".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 2));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCode()));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCodes()[0]));
        Assert.assertTrue("Correct errorCode", "code2".equals(status.getErrorCodes()[1]));
        tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.age");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "age".equals(status.getExpression()));
        Assert.assertTrue("Correct value", new Integer(0).equals(status.getValue()));
        Assert.assertTrue("Correct displayValue", "0".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 1));
        Assert.assertTrue("Correct errorCode", "code2".equals(status.getErrorCode()));
        tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.*");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "*".equals(status.getExpression()));
        Assert.assertTrue("Correct value", ((status.getValue()) == null));
        Assert.assertTrue("Correct displayValue", "".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 3));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCode()));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCodes()[0]));
        Assert.assertTrue("Correct errorCode", "code2".equals(status.getErrorCodes()[1]));
        Assert.assertTrue("Correct errorCode", "code2".equals(status.getErrorCodes()[2]));
    }

    @Test
    public void bindTagWithFieldErrorsAndDefaultMessageOnly() throws JspException {
        PageContext pc = createPageContext();
        TestBean tb = new TestBean();
        tb.setName("name1");
        Errors errors = getBindingResult();
        errors.rejectValue("name", null, "message & 1");
        errors.rejectValue("name", null, "message2");
        errors.rejectValue("age", null, "message2");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.name");
        tag.setHtmlEscape(true);
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "name".equals(status.getExpression()));
        Assert.assertTrue("Correct value", "name1".equals(status.getValue()));
        Assert.assertTrue("Correct displayValue", "name1".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 2));
        Assert.assertTrue("Correct errorMessage", "message &amp; 1".equals(status.getErrorMessage()));
        Assert.assertTrue("Correct errorMessage", "message &amp; 1".equals(status.getErrorMessages()[0]));
        Assert.assertTrue("Correct errorMessage", "message2".equals(status.getErrorMessages()[1]));
        Assert.assertTrue("Correct errorMessagesAsString", "message &amp; 1 - message2".equals(status.getErrorMessagesAsString(" - ")));
        tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.age");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "age".equals(status.getExpression()));
        Assert.assertTrue("Correct value", new Integer(0).equals(status.getValue()));
        Assert.assertTrue("Correct displayValue", "0".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 1));
        Assert.assertTrue("Correct errorMessage", "message2".equals(status.getErrorMessage()));
        Assert.assertTrue("Correct errorMessagesAsString", "message2".equals(status.getErrorMessagesAsString(" - ")));
        tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.*");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "*".equals(status.getExpression()));
        Assert.assertTrue("Correct value", ((status.getValue()) == null));
        Assert.assertTrue("Correct displayValue", "".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 3));
        Assert.assertTrue("Correct errorMessage", "message & 1".equals(status.getErrorMessage()));
        Assert.assertTrue("Correct errorMessage", "message & 1".equals(status.getErrorMessages()[0]));
        Assert.assertTrue("Correct errorMessage", "message2".equals(status.getErrorMessages()[1]));
        Assert.assertTrue("Correct errorMessage", "message2".equals(status.getErrorMessages()[2]));
    }

    @Test
    public void bindTagWithNestedFieldErrors() throws JspException {
        PageContext pc = createPageContext();
        TestBean tb = new TestBean();
        tb.setName("name1");
        TestBean spouse = new TestBean();
        spouse.setName("name2");
        tb.setSpouse(spouse);
        Errors errors = getBindingResult();
        errors.rejectValue("spouse.name", "code1", "message1");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.spouse.name");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "spouse.name".equals(status.getExpression()));
        Assert.assertTrue("Correct value", "name2".equals(status.getValue()));
        Assert.assertTrue("Correct displayValue", "name2".equals(status.getDisplayValue()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 1));
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 1));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCode()));
        Assert.assertTrue("Correct errorMessage", "message1".equals(status.getErrorMessage()));
        Assert.assertTrue("Correct errorMessagesAsString", "message1".equals(status.getErrorMessagesAsString(" - ")));
    }

    @Test
    public void propertyExposing() throws JspException {
        PageContext pc = createPageContext();
        TestBean tb = new TestBean();
        tb.setName("name1");
        Errors errors = new org.springframework.validation.BindException(tb, "tb");
        errors.rejectValue("name", "code1", null, "message & 1");
        errors.rejectValue("name", "code2", null, "message2");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        // test global property (should be null)
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        Assert.assertNull(tag.getProperty());
        // test property set (tb.name)
        tag.release();
        tag.setPageContext(pc);
        tag.setPath("tb.name");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        Assert.assertEquals("name", tag.getProperty());
    }

    @Test
    public void bindTagWithIndexedProperties() throws JspException {
        PageContext pc = createPageContext();
        IndexedTestBean tb = new IndexedTestBean();
        Errors errors = getBindingResult();
        errors.rejectValue("array[0]", "code1", "message1");
        errors.rejectValue("array[0]", "code2", "message2");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.array[0]");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "array[0]".equals(status.getExpression()));
        Assert.assertTrue("Value is TestBean", ((status.getValue()) instanceof TestBean));
        Assert.assertTrue("Correct value", "name0".equals(getName()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 2));
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 2));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCodes()[0]));
        Assert.assertTrue("Correct errorCode", "code2".equals(status.getErrorCodes()[1]));
        Assert.assertTrue("Correct errorMessage", "message1".equals(status.getErrorMessages()[0]));
        Assert.assertTrue("Correct errorMessage", "message2".equals(status.getErrorMessages()[1]));
    }

    @Test
    public void bindTagWithMappedProperties() throws JspException {
        PageContext pc = createPageContext();
        IndexedTestBean tb = new IndexedTestBean();
        Errors errors = getBindingResult();
        errors.rejectValue("map[key1]", "code1", "message1");
        errors.rejectValue("map[key1]", "code2", "message2");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.map[key1]");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "map[key1]".equals(status.getExpression()));
        Assert.assertTrue("Value is TestBean", ((status.getValue()) instanceof TestBean));
        Assert.assertTrue("Correct value", "name4".equals(getName()));
        Assert.assertTrue("Correct isError", status.isError());
        Assert.assertTrue("Correct errorCodes", ((status.getErrorCodes().length) == 2));
        Assert.assertTrue("Correct errorMessages", ((status.getErrorMessages().length) == 2));
        Assert.assertTrue("Correct errorCode", "code1".equals(status.getErrorCodes()[0]));
        Assert.assertTrue("Correct errorCode", "code2".equals(status.getErrorCodes()[1]));
        Assert.assertTrue("Correct errorMessage", "message1".equals(status.getErrorMessages()[0]));
        Assert.assertTrue("Correct errorMessage", "message2".equals(status.getErrorMessages()[1]));
    }

    @Test
    public void bindTagWithIndexedPropertiesAndCustomEditor() throws JspException {
        PageContext pc = createPageContext();
        IndexedTestBean tb = new IndexedTestBean();
        DataBinder binder = new ServletRequestDataBinder(tb, "tb");
        binder.registerCustomEditor(TestBean.class, null, new PropertyEditorSupport() {
            @Override
            public String getAsText() {
                return "something";
            }
        });
        Errors errors = binder.getBindingResult();
        errors.rejectValue("array[0]", "code1", "message1");
        errors.rejectValue("array[0]", "code2", "message2");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.array[0]");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertTrue("Correct expression", "array[0]".equals(status.getExpression()));
        // because of the custom editor getValue() should return a String
        Assert.assertTrue("Value is TestBean", ((status.getValue()) instanceof String));
        Assert.assertTrue("Correct value", "something".equals(status.getValue()));
    }

    @Test
    public void bindTagWithToStringAndHtmlEscaping() throws JspException {
        PageContext pc = createPageContext();
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.doctor");
        tag.setHtmlEscape(true);
        TestBean tb = new TestBean("somebody", 99);
        NestedTestBean ntb = new NestedTestBean("juergen&eva");
        tb.setDoctor(ntb);
        pc.getRequest().setAttribute("tb", tb);
        tag.doStartTag();
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertEquals("doctor", status.getExpression());
        Assert.assertTrue(((status.getValue()) instanceof NestedTestBean));
        Assert.assertTrue(status.getDisplayValue().contains("juergen&amp;eva"));
    }

    @Test
    public void bindTagWithSetValueAndHtmlEscaping() throws JspException {
        PageContext pc = createPageContext();
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.someSet");
        tag.setHtmlEscape(true);
        pc.getRequest().setAttribute("tb", new TestBean("juergen&eva", 99));
        tag.doStartTag();
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertEquals("someSet", status.getExpression());
        Assert.assertTrue(((status.getValue()) instanceof Set));
    }

    @Test
    public void bindTagWithFieldButWithoutErrorsInstance() throws JspException {
        PageContext pc = createPageContext();
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.name");
        pc.getRequest().setAttribute("tb", new TestBean("juergen&eva", 99));
        tag.doStartTag();
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertEquals("name", status.getExpression());
        Assert.assertEquals("juergen&eva", status.getValue());
    }

    @Test
    public void bindTagWithFieldButWithoutErrorsInstanceAndHtmlEscaping() throws JspException {
        PageContext pc = createPageContext();
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb.name");
        tag.setHtmlEscape(true);
        pc.getRequest().setAttribute("tb", new TestBean("juergen&eva", 99));
        tag.doStartTag();
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertEquals("name", status.getExpression());
        Assert.assertEquals("juergen&amp;eva", status.getValue());
    }

    @Test
    public void bindTagWithBeanButWithoutErrorsInstance() throws JspException {
        PageContext pc = createPageContext();
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb");
        pc.getRequest().setAttribute("tb", new TestBean("juergen", 99));
        tag.doStartTag();
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertNull(status.getExpression());
        Assert.assertNull(status.getValue());
    }

    @Test
    public void bindTagWithoutBean() throws JspException {
        PageContext pc = createPageContext();
        BindTag tag = new BindTag();
        tag.setPageContext(pc);
        tag.setPath("tb");
        try {
            tag.doStartTag();
            Assert.fail("Should have thrown JspException");
        } catch (JspException ex) {
            // expected
        }
    }

    @Test
    public void bindErrorsTagWithoutErrors() throws JspException {
        PageContext pc = createPageContext();
        Errors errors = getBindingResult();
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindErrorsTag tag = new BindErrorsTag();
        tag.setPageContext(pc);
        tag.setName("tb");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.SKIP_BODY)));
        Assert.assertTrue("Doesn't have errors variable", ((pc.getAttribute(ERRORS_VARIABLE_NAME)) == null));
    }

    @Test
    public void bindErrorsTagWithErrors() throws JspException {
        PageContext pc = createPageContext();
        Errors errors = getBindingResult();
        errors.reject("test", null, "test");
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        BindErrorsTag tag = new BindErrorsTag();
        tag.setPageContext(pc);
        tag.setName("tb");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        Assert.assertTrue("Has errors variable", ((pc.getAttribute(ERRORS_VARIABLE_NAME, REQUEST_SCOPE)) == errors));
    }

    @Test
    public void bindErrorsTagWithoutBean() throws JspException {
        PageContext pc = createPageContext();
        BindErrorsTag tag = new BindErrorsTag();
        tag.setPageContext(pc);
        tag.setName("tb");
        Assert.assertTrue("Correct doStartTag return value", ((tag.doStartTag()) == (Tag.SKIP_BODY)));
    }

    @Test
    public void nestedPathDoEndTag() throws JspException {
        PageContext pc = createPageContext();
        NestedPathTag tag = new NestedPathTag();
        tag.setPath("foo");
        tag.setPageContext(pc);
        tag.doStartTag();
        int returnValue = tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, returnValue);
        Assert.assertNull(pc.getAttribute(NESTED_PATH_VARIABLE_NAME, REQUEST_SCOPE));
    }

    @Test
    public void nestedPathDoEndTagWithNesting() throws JspException {
        PageContext pc = createPageContext();
        NestedPathTag tag = new NestedPathTag();
        tag.setPath("foo");
        tag.setPageContext(pc);
        tag.doStartTag();
        NestedPathTag anotherTag = new NestedPathTag();
        anotherTag.setPageContext(pc);
        anotherTag.setPath("bar");
        anotherTag.doStartTag();
        anotherTag.doEndTag();
        Assert.assertEquals("foo.", pc.getAttribute(NESTED_PATH_VARIABLE_NAME, REQUEST_SCOPE));
        tag.doEndTag();
        Assert.assertNull(pc.getAttribute(NESTED_PATH_VARIABLE_NAME, REQUEST_SCOPE));
    }

    @Test
    public void nestedPathDoStartTagInternal() throws JspException {
        PageContext pc = createPageContext();
        NestedPathTag tag = new NestedPathTag();
        tag.setPath("foo");
        tag.setPageContext(pc);
        int returnValue = tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, returnValue);
        Assert.assertEquals("foo.", pc.getAttribute(NESTED_PATH_VARIABLE_NAME, REQUEST_SCOPE));
    }

    @Test
    public void nestedPathDoStartTagInternalWithNesting() throws JspException {
        PageContext pc = createPageContext();
        NestedPathTag tag = new NestedPathTag();
        tag.setPath("foo");
        tag.setPageContext(pc);
        tag.doStartTag();
        Assert.assertEquals("foo.", pc.getAttribute(NESTED_PATH_VARIABLE_NAME, REQUEST_SCOPE));
        NestedPathTag anotherTag = new NestedPathTag();
        anotherTag.setPageContext(pc);
        anotherTag.setPath("bar");
        anotherTag.doStartTag();
        Assert.assertEquals("foo.bar.", pc.getAttribute(NESTED_PATH_VARIABLE_NAME, REQUEST_SCOPE));
        NestedPathTag yetAnotherTag = new NestedPathTag();
        yetAnotherTag.setPageContext(pc);
        yetAnotherTag.setPath("boo");
        yetAnotherTag.doStartTag();
        Assert.assertEquals("foo.bar.boo.", pc.getAttribute(NESTED_PATH_VARIABLE_NAME, REQUEST_SCOPE));
        yetAnotherTag.doEndTag();
        NestedPathTag andAnotherTag = new NestedPathTag();
        andAnotherTag.setPageContext(pc);
        andAnotherTag.setPath("boo2");
        andAnotherTag.doStartTag();
        Assert.assertEquals("foo.bar.boo2.", pc.getAttribute(NESTED_PATH_VARIABLE_NAME, REQUEST_SCOPE));
    }

    @Test
    public void nestedPathWithBindTag() throws JspException {
        PageContext pc = createPageContext();
        Errors errors = getBindingResult();
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), errors);
        NestedPathTag nestedPathTag = new NestedPathTag();
        nestedPathTag.setPath("tb");
        nestedPathTag.setPageContext(pc);
        nestedPathTag.doStartTag();
        BindTag bindTag = new BindTag();
        bindTag.setPageContext(pc);
        bindTag.setPath("name");
        Assert.assertTrue("Correct doStartTag return value", ((bindTag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertEquals("tb.name", status.getPath());
        Assert.assertEquals("Correct field value", "", status.getDisplayValue());
        BindTag bindTag2 = new BindTag();
        bindTag2.setPageContext(pc);
        bindTag2.setPath("age");
        Assert.assertTrue("Correct doStartTag return value", ((bindTag2.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status2 = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status2 != null));
        Assert.assertEquals("tb.age", status2.getPath());
        Assert.assertEquals("Correct field value", "0", status2.getDisplayValue());
        bindTag2.doEndTag();
        BindStatus status3 = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertSame("Status matches previous status", status, status3);
        Assert.assertEquals("tb.name", status.getPath());
        Assert.assertEquals("Correct field value", "", status.getDisplayValue());
        bindTag.doEndTag();
        nestedPathTag.doEndTag();
    }

    @Test
    public void nestedPathWithBindTagWithIgnoreNestedPath() throws JspException {
        PageContext pc = createPageContext();
        Errors errors = getBindingResult();
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb2"), errors);
        NestedPathTag tag = new NestedPathTag();
        tag.setPath("tb");
        tag.setPageContext(pc);
        tag.doStartTag();
        BindTag bindTag = new BindTag();
        bindTag.setPageContext(pc);
        bindTag.setIgnoreNestedPath(true);
        bindTag.setPath("tb2.name");
        Assert.assertTrue("Correct doStartTag return value", ((bindTag.doStartTag()) == (Tag.EVAL_BODY_INCLUDE)));
        BindStatus status = ((BindStatus) (pc.getAttribute(STATUS_VARIABLE_NAME, REQUEST_SCOPE)));
        Assert.assertTrue("Has status variable", (status != null));
        Assert.assertEquals("tb2.name", status.getPath());
    }

    @Test
    public void transformTagCorrectBehavior() throws JspException {
        // first set up the pagecontext and the bean
        PageContext pc = createPageContext();
        TestBean tb = new TestBean();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ServletRequestDataBinder binder = new ServletRequestDataBinder(tb, "tb");
        CustomDateEditor l = new CustomDateEditor(df, true);
        binder.registerCustomEditor(Date.class, l);
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), binder.getBindingResult());
        // execute the bind tag using the date property
        BindTag bind = new BindTag();
        bind.setPageContext(pc);
        bind.setPath("tb.date");
        bind.doStartTag();
        // transform stuff
        TransformTag transform = new TransformTag();
        transform.setPageContext(pc);
        transform.setParent(bind);
        transform.setValue(tb.getDate());
        transform.setVar("theDate");
        transform.doStartTag();
        Assert.assertNotNull(pc.getAttribute("theDate"));
        Assert.assertEquals(pc.getAttribute("theDate"), df.format(tb.getDate()));
        // try another time, this time using Strings
        bind = new BindTag();
        bind.setPageContext(pc);
        bind.setPath("tb.name");
        bind.doStartTag();
        transform = new TransformTag();
        transform.setPageContext(pc);
        transform.setValue("name");
        transform.setParent(bind);
        transform.setVar("theString");
        transform.doStartTag();
        Assert.assertNotNull(pc.getAttribute("theString"));
        Assert.assertEquals("name", pc.getAttribute("theString"));
    }

    @Test
    public void transformTagWithHtmlEscape() throws JspException {
        // first set up the PageContext and the bean
        PageContext pc = createPageContext();
        TestBean tb = new TestBean();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ServletRequestDataBinder binder = new ServletRequestDataBinder(tb, "tb");
        CustomDateEditor l = new CustomDateEditor(df, true);
        binder.registerCustomEditor(Date.class, l);
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), binder.getBindingResult());
        // try another time, this time using Strings
        BindTag bind = new BindTag();
        bind.setPageContext(pc);
        bind.setPath("tb.name");
        bind.doStartTag();
        TransformTag transform = new TransformTag();
        transform.setPageContext(pc);
        transform.setValue("na<me");
        transform.setParent(bind);
        transform.setVar("theString");
        transform.setHtmlEscape(true);
        transform.doStartTag();
        Assert.assertNotNull(pc.getAttribute("theString"));
        Assert.assertEquals("na&lt;me", pc.getAttribute("theString"));
    }

    @Test
    public void transformTagOutsideBindTag() throws JspException {
        // first set up the pagecontext and the bean
        PageContext pc = createPageContext();
        TestBean tb = new TestBean();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ServletRequestDataBinder binder = new ServletRequestDataBinder(tb, "tb");
        CustomDateEditor l = new CustomDateEditor(df, true);
        binder.registerCustomEditor(Date.class, l);
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), binder.getBindingResult());
        // now try to execute the tag outside a bindtag
        TransformTag transform = new TransformTag();
        transform.setPageContext(pc);
        transform.setVar("var");
        transform.setValue("bla");
        try {
            transform.doStartTag();
            Assert.fail("Tag can be executed outside BindTag");
        } catch (JspException e) {
            // this is ok!
        }
        // now try to execute the tag outside a bindtag, but inside a messageTag
        MessageTag message = new MessageTag();
        message.setPageContext(pc);
        transform = new TransformTag();
        transform.setPageContext(pc);
        transform.setVar("var");
        transform.setValue("bla");
        transform.setParent(message);
        try {
            transform.doStartTag();
            Assert.fail("Tag can be executed outside BindTag and inside messagetag");
        } catch (JspException e) {
            // this is ok!
        }
    }

    @Test
    public void transformTagNonExistingValue() throws JspException {
        // first set up the pagecontext and the bean
        PageContext pc = createPageContext();
        TestBean tb = new TestBean();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ServletRequestDataBinder binder = new ServletRequestDataBinder(tb, "tb");
        CustomDateEditor l = new CustomDateEditor(df, true);
        binder.registerCustomEditor(Date.class, l);
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), binder.getBindingResult());
        // try with non-existing value
        BindTag bind = new BindTag();
        bind.setPageContext(pc);
        bind.setPath("tb.name");
        bind.doStartTag();
        TransformTag transform = new TransformTag();
        transform.setPageContext(pc);
        transform.setValue(null);
        transform.setParent(bind);
        transform.setVar("theString2");
        transform.doStartTag();
        Assert.assertNull(pc.getAttribute("theString2"));
    }

    @Test
    public void transformTagWithSettingOfScope() throws JspException {
        // first set up the pagecontext and the bean
        PageContext pc = createPageContext();
        TestBean tb = new TestBean();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ServletRequestDataBinder binder = new ServletRequestDataBinder(tb, "tb");
        CustomDateEditor l = new CustomDateEditor(df, true);
        binder.registerCustomEditor(Date.class, l);
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), binder.getBindingResult());
        // execute the bind tag using the date property
        BindTag bind = new BindTag();
        bind.setPageContext(pc);
        bind.setPath("tb.date");
        bind.doStartTag();
        // transform stuff
        TransformTag transform = new TransformTag();
        transform.setPageContext(pc);
        transform.setParent(bind);
        transform.setValue(tb.getDate());
        transform.setVar("theDate");
        transform.setScope("page");
        transform.doStartTag();
        transform.release();
        Assert.assertNotNull(pc.getAttribute("theDate"));
        Assert.assertEquals(df.format(tb.getDate()), pc.getAttribute("theDate"));
        // try another time, this time using Strings
        bind = new BindTag();
        bind.setPageContext(pc);
        bind.setPath("tb.name");
        bind.doStartTag();
        transform = new TransformTag();
        transform.setPageContext(pc);
        transform.setValue("name");
        transform.setParent(bind);
        transform.setVar("theString");
        transform.setScope("page");
        transform.doStartTag();
        transform.release();
        Assert.assertNotNull(pc.getAttribute("theString"));
        Assert.assertEquals("name", pc.getAttribute("theString"));
    }

    /**
     * SPR-4022
     */
    @SuppressWarnings("serial")
    @Test
    public void nestingInFormTag() throws JspException {
        PageContext pc = createPageContext();
        TestBean tb = new TestBean();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ServletRequestDataBinder binder = new ServletRequestDataBinder(tb, "tb");
        CustomDateEditor l = new CustomDateEditor(df, true);
        binder.registerCustomEditor(Date.class, l);
        pc.getRequest().setAttribute(((BindingResult.MODEL_KEY_PREFIX) + "tb"), binder.getBindingResult());
        FormTag formTag = new FormTag() {
            @Override
            protected TagWriter createTagWriter() {
                return new TagWriter(new StringWriter());
            }
        };
        String action = "/form.html";
        String commandName = "tb";
        String name = "formName";
        String enctype = "my/enctype";
        String method = "POST";
        String onsubmit = "onsubmit";
        String onreset = "onreset";
        String cssClass = "myClass";
        String cssStyle = "myStyle";
        String acceptCharset = "iso-8859-1";
        formTag.setName(name);
        formTag.setCssClass(cssClass);
        formTag.setCssStyle(cssStyle);
        formTag.setAction(action);
        formTag.setModelAttribute(commandName);
        formTag.setEnctype(enctype);
        formTag.setMethod(method);
        formTag.setOnsubmit(onsubmit);
        formTag.setOnreset(onreset);
        formTag.setAcceptCharset(acceptCharset);
        formTag.setPageContext(pc);
        formTag.doStartTag();
        BindTag bindTag1 = new BindTag();
        bindTag1.setPageContext(pc);
        bindTag1.setPath("date");
        bindTag1.doStartTag();
        bindTag1.doEndTag();
        BindTag bindTag2 = new BindTag();
        bindTag2.setPageContext(pc);
        bindTag2.setPath("tb.date");
        bindTag2.doStartTag();
        bindTag2.doEndTag();
        BindTag bindTag3 = new BindTag();
        bindTag3.setPageContext(pc);
        bindTag3.setPath("tb");
        bindTag3.doStartTag();
        bindTag3.doEndTag();
        formTag.doEndTag();
    }
}

