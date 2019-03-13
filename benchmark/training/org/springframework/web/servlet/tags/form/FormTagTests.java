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


import FormTag.MODEL_ATTRIBUTE_VARIABLE_NAME;
import PageContext.REQUEST_SCOPE;
import Tag.EVAL_BODY_INCLUDE;
import Tag.EVAL_PAGE;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.servlet.support.RequestDataValueProcessor;


/**
 *
 *
 * @author Rob Harrop
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Scott Andrews
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 */
public class FormTagTests extends AbstractHtmlElementTagTests {
    private static final String REQUEST_URI = "/my/form";

    private static final String QUERY_STRING = "foo=bar";

    private FormTag tag;

    private MockHttpServletRequest request;

    @Test
    public void writeForm() throws Exception {
        String commandName = "myCommand";
        String name = "formName";
        String action = "/form.html";
        String method = "POST";
        String target = "myTarget";
        String enctype = "my/enctype";
        String acceptCharset = "iso-8859-1";
        String onsubmit = "onsubmit";
        String onreset = "onreset";
        String autocomplete = "off";
        String cssClass = "myClass";
        String cssStyle = "myStyle";
        String dynamicAttribute1 = "attr1";
        String dynamicAttribute2 = "attr2";
        this.tag.setName(name);
        this.tag.setCssClass(cssClass);
        this.tag.setCssStyle(cssStyle);
        this.tag.setModelAttribute(commandName);
        this.tag.setAction(action);
        this.tag.setMethod(method);
        this.tag.setTarget(target);
        this.tag.setEnctype(enctype);
        this.tag.setAcceptCharset(acceptCharset);
        this.tag.setOnsubmit(onsubmit);
        this.tag.setOnreset(onreset);
        this.tag.setAutocomplete(autocomplete);
        this.tag.setDynamicAttribute(null, dynamicAttribute1, dynamicAttribute1);
        this.tag.setDynamicAttribute(null, dynamicAttribute2, dynamicAttribute2);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, result);
        Assert.assertEquals("Form attribute not exposed", commandName, getPageContext().getRequest().getAttribute(MODEL_ATTRIBUTE_VARIABLE_NAME));
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        this.tag.doFinally();
        Assert.assertNull("Form attribute not cleared after tag ends", getPageContext().getRequest().getAttribute(MODEL_ATTRIBUTE_VARIABLE_NAME));
        String output = getOutput();
        FormTagTests.assertFormTagOpened(output);
        FormTagTests.assertFormTagClosed(output);
        assertContainsAttribute(output, "class", cssClass);
        assertContainsAttribute(output, "style", cssStyle);
        assertContainsAttribute(output, "action", action);
        assertContainsAttribute(output, "method", method);
        assertContainsAttribute(output, "target", target);
        assertContainsAttribute(output, "enctype", enctype);
        assertContainsAttribute(output, "accept-charset", acceptCharset);
        assertContainsAttribute(output, "onsubmit", onsubmit);
        assertContainsAttribute(output, "onreset", onreset);
        assertContainsAttribute(output, "autocomplete", autocomplete);
        assertContainsAttribute(output, "id", commandName);
        assertContainsAttribute(output, "name", name);
        assertContainsAttribute(output, dynamicAttribute1, dynamicAttribute1);
        assertContainsAttribute(output, dynamicAttribute2, dynamicAttribute2);
    }

    @Test
    public void withActionFromRequest() throws Exception {
        String commandName = "myCommand";
        String enctype = "my/enctype";
        String method = "POST";
        String onsubmit = "onsubmit";
        String onreset = "onreset";
        this.tag.setModelAttribute(commandName);
        this.tag.setMethod(method);
        this.tag.setEnctype(enctype);
        this.tag.setOnsubmit(onsubmit);
        this.tag.setOnreset(onreset);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, result);
        Assert.assertEquals("Form attribute not exposed", commandName, getPageContext().getAttribute(MODEL_ATTRIBUTE_VARIABLE_NAME, REQUEST_SCOPE));
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        this.tag.doFinally();
        Assert.assertNull("Form attribute not cleared after tag ends", getPageContext().getAttribute(MODEL_ATTRIBUTE_VARIABLE_NAME, REQUEST_SCOPE));
        String output = getOutput();
        FormTagTests.assertFormTagOpened(output);
        FormTagTests.assertFormTagClosed(output);
        assertContainsAttribute(output, "action", (((FormTagTests.REQUEST_URI) + "?") + (FormTagTests.QUERY_STRING)));
        assertContainsAttribute(output, "method", method);
        assertContainsAttribute(output, "enctype", enctype);
        assertContainsAttribute(output, "onsubmit", onsubmit);
        assertContainsAttribute(output, "onreset", onreset);
        assertAttributeNotPresent(output, "name");
    }

    @Test
    public void prependServletPath() throws Exception {
        this.request.setContextPath("/myApp");
        this.request.setServletPath("/main");
        this.request.setPathInfo("/index.html");
        String commandName = "myCommand";
        String action = "/form.html";
        String enctype = "my/enctype";
        String method = "POST";
        String onsubmit = "onsubmit";
        String onreset = "onreset";
        this.tag.setModelAttribute(commandName);
        this.tag.setServletRelativeAction(action);
        this.tag.setMethod(method);
        this.tag.setEnctype(enctype);
        this.tag.setOnsubmit(onsubmit);
        this.tag.setOnreset(onreset);
        int result = this.tag.doStartTag();
        Assert.assertEquals(EVAL_BODY_INCLUDE, result);
        Assert.assertEquals("Form attribute not exposed", commandName, getPageContext().getAttribute(MODEL_ATTRIBUTE_VARIABLE_NAME, REQUEST_SCOPE));
        result = this.tag.doEndTag();
        Assert.assertEquals(EVAL_PAGE, result);
        this.tag.doFinally();
        Assert.assertNull("Form attribute not cleared after tag ends", getPageContext().getAttribute(MODEL_ATTRIBUTE_VARIABLE_NAME, REQUEST_SCOPE));
        String output = getOutput();
        FormTagTests.assertFormTagOpened(output);
        FormTagTests.assertFormTagClosed(output);
        assertContainsAttribute(output, "action", "/myApp/main/form.html");
        assertContainsAttribute(output, "method", method);
        assertContainsAttribute(output, "enctype", enctype);
        assertContainsAttribute(output, "onsubmit", onsubmit);
        assertContainsAttribute(output, "onreset", onreset);
        assertAttributeNotPresent(output, "name");
    }

    @Test
    public void withNullResolvedCommand() throws Exception {
        try {
            tag.setModelAttribute(null);
            tag.doStartTag();
            Assert.fail("Must not be able to have a command name that resolves to null");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    /**
     * https://jira.spring.io/browse/SPR-2645
     */
    @Test
    public void xssExploitWhenActionIsResolvedFromQueryString() throws Exception {
        String xssQueryString = (FormTagTests.QUERY_STRING) + "&stuff=\"><script>alert(\'XSS!\')</script>";
        request.setQueryString(xssQueryString);
        tag.doStartTag();
        Assert.assertEquals(("<form id=\"command\" action=\"/my/form?foo=bar&amp;stuff=&quot;&gt;&lt;" + "script&gt;alert(&#39;XSS!&#39;)&lt;/script&gt;\" method=\"post\">"), getOutput());
    }

    @Test
    public void get() throws Exception {
        this.tag.setMethod("get");
        this.tag.doStartTag();
        this.tag.doEndTag();
        this.tag.doFinally();
        String output = getOutput();
        String formOutput = getFormTag(output);
        String inputOutput = getInputTag(output);
        assertContainsAttribute(formOutput, "method", "get");
        Assert.assertEquals("", inputOutput);
    }

    @Test
    public void post() throws Exception {
        this.tag.setMethod("post");
        this.tag.doStartTag();
        this.tag.doEndTag();
        this.tag.doFinally();
        String output = getOutput();
        String formOutput = getFormTag(output);
        String inputOutput = getInputTag(output);
        assertContainsAttribute(formOutput, "method", "post");
        Assert.assertEquals("", inputOutput);
    }

    @Test
    public void put() throws Exception {
        this.tag.setMethod("put");
        this.tag.doStartTag();
        this.tag.doEndTag();
        this.tag.doFinally();
        String output = getOutput();
        String formOutput = getFormTag(output);
        String inputOutput = getInputTag(output);
        assertContainsAttribute(formOutput, "method", "post");
        assertContainsAttribute(inputOutput, "name", "_method");
        assertContainsAttribute(inputOutput, "value", "put");
        assertContainsAttribute(inputOutput, "type", "hidden");
    }

    @Test
    public void delete() throws Exception {
        this.tag.setMethod("delete");
        this.tag.doStartTag();
        this.tag.doEndTag();
        this.tag.doFinally();
        String output = getOutput();
        String formOutput = getFormTag(output);
        String inputOutput = getInputTag(output);
        assertContainsAttribute(formOutput, "method", "post");
        assertContainsAttribute(inputOutput, "name", "_method");
        assertContainsAttribute(inputOutput, "value", "delete");
        assertContainsAttribute(inputOutput, "type", "hidden");
    }

    @Test
    public void customMethodParameter() throws Exception {
        this.tag.setMethod("put");
        this.tag.setMethodParam("methodParameter");
        this.tag.doStartTag();
        this.tag.doEndTag();
        this.tag.doFinally();
        String output = getOutput();
        String formOutput = getFormTag(output);
        String inputOutput = getInputTag(output);
        assertContainsAttribute(formOutput, "method", "post");
        assertContainsAttribute(inputOutput, "name", "methodParameter");
        assertContainsAttribute(inputOutput, "value", "put");
        assertContainsAttribute(inputOutput, "type", "hidden");
    }

    @Test
    public void clearAttributesOnFinally() throws Exception {
        this.tag.setModelAttribute("model");
        getPageContext().setAttribute("model", "foo bar");
        Assert.assertNull(getPageContext().getAttribute(MODEL_ATTRIBUTE_VARIABLE_NAME, REQUEST_SCOPE));
        this.tag.doStartTag();
        Assert.assertNotNull(getPageContext().getAttribute(MODEL_ATTRIBUTE_VARIABLE_NAME, REQUEST_SCOPE));
        this.tag.doFinally();
        Assert.assertNull(getPageContext().getAttribute(MODEL_ATTRIBUTE_VARIABLE_NAME, REQUEST_SCOPE));
    }

    @Test
    public void requestDataValueProcessorHooks() throws Exception {
        String action = "/my/form?foo=bar";
        RequestDataValueProcessor processor = getMockRequestDataValueProcessor();
        BDDMockito.given(processor.processAction(this.request, action, "post")).willReturn(action);
        BDDMockito.given(processor.getExtraHiddenFields(this.request)).willReturn(Collections.singletonMap("key", "value"));
        this.tag.doStartTag();
        this.tag.doEndTag();
        this.tag.doFinally();
        String output = getOutput();
        Assert.assertEquals("<div>\n<input type=\"hidden\" name=\"key\" value=\"value\" />\n</div>", getInputTag(output));
        FormTagTests.assertFormTagOpened(output);
        FormTagTests.assertFormTagClosed(output);
    }

    @Test
    public void defaultActionEncoded() throws Exception {
        this.request.setRequestURI("/a b c");
        request.setQueryString("");
        this.tag.doStartTag();
        this.tag.doEndTag();
        this.tag.doFinally();
        String output = getOutput();
        String formOutput = getFormTag(output);
        assertContainsAttribute(formOutput, "action", "/a%20b%20c");
    }
}

