/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.web.servlet.view.freemarker;


import FreeMarkerView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;


/**
 *
 *
 * @author Darren Davison
 * @author Juergen Hoeller
 * @since 25.01.2005
 */
public class FreeMarkerMacroTests {
    private static final String TEMPLATE_FILE = "test.ftl";

    private StaticWebApplicationContext wac;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private FreeMarkerConfigurer fc;

    @Test
    public void testExposeSpringMacroHelpers() throws Exception {
        FreeMarkerView fv = new FreeMarkerView() {
            @Override
            @SuppressWarnings("rawtypes")
            protected void processTemplate(Template template, SimpleHash fmModel, HttpServletResponse response) throws TemplateException {
                Map model = fmModel.toMap();
                Assert.assertTrue(((model.get(SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE)) instanceof RequestContext));
                RequestContext rc = ((RequestContext) (model.get(SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE)));
                BindStatus status = rc.getBindStatus("tb.name");
                Assert.assertEquals("name", status.getExpression());
                Assert.assertEquals("juergen", status.getValue());
            }
        };
        fv.setUrl(FreeMarkerMacroTests.TEMPLATE_FILE);
        fv.setApplicationContext(wac);
        fv.setExposeSpringMacroHelpers(true);
        Map<String, Object> model = new HashMap<>();
        model.put("tb", new TestBean("juergen", 99));
        fv.render(model, request, response);
    }

    @Test
    public void testSpringMacroRequestContextAttributeUsed() {
        final String helperTool = "wrongType";
        FreeMarkerView fv = new FreeMarkerView() {
            @Override
            protected void processTemplate(Template template, SimpleHash model, HttpServletResponse response) {
                Assert.fail();
            }
        };
        fv.setUrl(FreeMarkerMacroTests.TEMPLATE_FILE);
        fv.setApplicationContext(wac);
        fv.setExposeSpringMacroHelpers(true);
        Map<String, Object> model = new HashMap<>();
        model.put(SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, helperTool);
        try {
            fv.render(model, request, response);
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof ServletException));
            Assert.assertTrue(ex.getMessage().contains(SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE));
        }
    }

    @Test
    public void testName() throws Exception {
        Assert.assertEquals("Darren", getMacroOutput("NAME"));
    }

    @Test
    public void testAge() throws Exception {
        Assert.assertEquals("99", getMacroOutput("AGE"));
    }

    @Test
    public void testMessage() throws Exception {
        Assert.assertEquals("Howdy Mundo", getMacroOutput("MESSAGE"));
    }

    @Test
    public void testDefaultMessage() throws Exception {
        Assert.assertEquals("hi planet", getMacroOutput("DEFAULTMESSAGE"));
    }

    @Test
    public void testMessageArgs() throws Exception {
        Assert.assertEquals("Howdy[World]", getMacroOutput("MESSAGEARGS"));
    }

    @Test
    public void testMessageArgsWithDefaultMessage() throws Exception {
        Assert.assertEquals("Hi", getMacroOutput("MESSAGEARGSWITHDEFAULTMESSAGE"));
    }

    @Test
    public void testTheme() throws Exception {
        Assert.assertEquals("Howdy! Mundo!", getMacroOutput("THEME"));
    }

    @Test
    public void testDefaultTheme() throws Exception {
        Assert.assertEquals("hi! planet!", getMacroOutput("DEFAULTTHEME"));
    }

    @Test
    public void testThemeArgs() throws Exception {
        Assert.assertEquals("Howdy![World]", getMacroOutput("THEMEARGS"));
    }

    @Test
    public void testThemeArgsWithDefaultMessage() throws Exception {
        Assert.assertEquals("Hi!", getMacroOutput("THEMEARGSWITHDEFAULTMESSAGE"));
    }

    @Test
    public void testUrl() throws Exception {
        Assert.assertEquals("/springtest/aftercontext.html", getMacroOutput("URL"));
    }

    @Test
    public void testUrlParams() throws Exception {
        Assert.assertEquals("/springtest/aftercontext/bar?spam=bucket", getMacroOutput("URLPARAMS"));
    }

    @Test
    public void testForm1() throws Exception {
        Assert.assertEquals("<input type=\"text\" id=\"name\" name=\"name\" value=\"Darren\"     >", getMacroOutput("FORM1"));
    }

    @Test
    public void testForm2() throws Exception {
        Assert.assertEquals("<input type=\"text\" id=\"name\" name=\"name\" value=\"Darren\" class=\"myCssClass\"    >", getMacroOutput("FORM2"));
    }

    @Test
    public void testForm3() throws Exception {
        Assert.assertEquals("<textarea id=\"name\" name=\"name\" >\nDarren</textarea>", getMacroOutput("FORM3"));
    }

    @Test
    public void testForm4() throws Exception {
        Assert.assertEquals("<textarea id=\"name\" name=\"name\" rows=10 cols=30>\nDarren</textarea>", getMacroOutput("FORM4"));
    }

    // TODO verify remaining output (fix whitespace)
    @Test
    public void testForm9() throws Exception {
        Assert.assertEquals("<input type=\"password\" id=\"name\" name=\"name\" value=\"\"     >", getMacroOutput("FORM9"));
    }

    @Test
    public void testForm10() throws Exception {
        Assert.assertEquals("<input type=\"hidden\" id=\"name\" name=\"name\" value=\"Darren\"     >", getMacroOutput("FORM10"));
    }

    @Test
    public void testForm11() throws Exception {
        Assert.assertEquals("<input type=\"text\" id=\"name\" name=\"name\" value=\"Darren\"     >", getMacroOutput("FORM11"));
    }

    @Test
    public void testForm12() throws Exception {
        Assert.assertEquals("<input type=\"hidden\" id=\"name\" name=\"name\" value=\"Darren\"     >", getMacroOutput("FORM12"));
    }

    @Test
    public void testForm13() throws Exception {
        Assert.assertEquals("<input type=\"password\" id=\"name\" name=\"name\" value=\"\"     >", getMacroOutput("FORM13"));
    }

    @Test
    public void testForm15() throws Exception {
        String output = getMacroOutput("FORM15");
        Assert.assertTrue(("Wrong output: " + output), output.startsWith("<input type=\"hidden\" name=\"_name\" value=\"on\"/>"));
        Assert.assertTrue(("Wrong output: " + output), output.contains("<input type=\"checkbox\" id=\"name\" name=\"name\" />"));
    }

    @Test
    public void testForm16() throws Exception {
        String output = getMacroOutput("FORM16");
        Assert.assertTrue(("Wrong output: " + output), output.startsWith("<input type=\"hidden\" name=\"_jedi\" value=\"on\"/>"));
        Assert.assertTrue(("Wrong output: " + output), output.contains("<input type=\"checkbox\" id=\"jedi\" name=\"jedi\" checked=\"checked\" />"));
    }

    @Test
    public void testForm17() throws Exception {
        Assert.assertEquals("<input type=\"text\" id=\"spouses0.name\" name=\"spouses[0].name\" value=\"Fred\"     >", getMacroOutput("FORM17"));
    }

    @Test
    public void testForm18() throws Exception {
        String output = getMacroOutput("FORM18");
        Assert.assertTrue(("Wrong output: " + output), output.startsWith("<input type=\"hidden\" name=\"_spouses[0].jedi\" value=\"on\"/>"));
        Assert.assertTrue(("Wrong output: " + output), output.contains("<input type=\"checkbox\" id=\"spouses0.jedi\" name=\"spouses[0].jedi\" checked=\"checked\" />"));
    }
}

