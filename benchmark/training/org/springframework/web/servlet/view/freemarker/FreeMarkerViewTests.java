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


import AbstractView.DEFAULT_CONTENT_TYPE;
import Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;
import DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE;
import DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContextException;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 14.03.2004
 */
public class FreeMarkerViewTests {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void noFreeMarkerConfig() throws Exception {
        FreeMarkerView fv = new FreeMarkerView();
        WebApplicationContext wac = Mockito.mock(WebApplicationContext.class);
        BDDMockito.given(wac.getBeansOfType(FreeMarkerConfig.class, true, false)).willReturn(new HashMap());
        BDDMockito.given(wac.getServletContext()).willReturn(new MockServletContext());
        fv.setUrl("anythingButNull");
        exception.expect(ApplicationContextException.class);
        exception.expectMessage(CoreMatchers.containsString("FreeMarkerConfig"));
        fv.setApplicationContext(wac);
    }

    @Test
    public void noTemplateName() throws Exception {
        FreeMarkerView fv = new FreeMarkerView();
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.containsString("url"));
        fv.afterPropertiesSet();
    }

    @Test
    public void validTemplateName() throws Exception {
        FreeMarkerView fv = new FreeMarkerView();
        WebApplicationContext wac = Mockito.mock(WebApplicationContext.class);
        MockServletContext sc = new MockServletContext();
        Map<String, FreeMarkerConfig> configs = new HashMap<>();
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setConfiguration(new FreeMarkerViewTests.TestConfiguration());
        configurer.setServletContext(sc);
        configs.put("configurer", configurer);
        BDDMockito.given(wac.getBeansOfType(FreeMarkerConfig.class, true, false)).willReturn(configs);
        BDDMockito.given(wac.getServletContext()).willReturn(sc);
        fv.setUrl("templateName");
        fv.setApplicationContext(wac);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.US);
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, new AcceptHeaderLocaleResolver());
        HttpServletResponse response = new MockHttpServletResponse();
        Map<String, Object> model = new HashMap<>();
        model.put("myattr", "myvalue");
        fv.render(model, request, response);
        Assert.assertEquals(DEFAULT_CONTENT_TYPE, response.getContentType());
    }

    @Test
    public void keepExistingContentType() throws Exception {
        FreeMarkerView fv = new FreeMarkerView();
        WebApplicationContext wac = Mockito.mock(WebApplicationContext.class);
        MockServletContext sc = new MockServletContext();
        Map<String, FreeMarkerConfig> configs = new HashMap<>();
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setConfiguration(new FreeMarkerViewTests.TestConfiguration());
        configurer.setServletContext(sc);
        configs.put("configurer", configurer);
        BDDMockito.given(wac.getBeansOfType(FreeMarkerConfig.class, true, false)).willReturn(configs);
        BDDMockito.given(wac.getServletContext()).willReturn(sc);
        fv.setUrl("templateName");
        fv.setApplicationContext(wac);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.US);
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, new AcceptHeaderLocaleResolver());
        HttpServletResponse response = new MockHttpServletResponse();
        response.setContentType("myContentType");
        Map<String, Object> model = new HashMap<>();
        model.put("myattr", "myvalue");
        fv.render(model, request, response);
        Assert.assertEquals("myContentType", response.getContentType());
    }

    @Test
    public void freeMarkerViewResolver() throws Exception {
        MockServletContext sc = new MockServletContext();
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setConfiguration(new FreeMarkerViewTests.TestConfiguration());
        configurer.setServletContext(sc);
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.getBeanFactory().registerSingleton("configurer", configurer);
        wac.refresh();
        FreeMarkerViewResolver vr = new FreeMarkerViewResolver("prefix_", "_suffix");
        vr.setApplicationContext(wac);
        View view = vr.resolveViewName("test", Locale.CANADA);
        Assert.assertEquals("Correct view class", FreeMarkerView.class, view.getClass());
        Assert.assertEquals("Correct URL", "prefix_test_suffix", getUrl());
        view = vr.resolveViewName("non-existing", Locale.CANADA);
        Assert.assertNull(view);
        view = vr.resolveViewName("redirect:myUrl", Locale.getDefault());
        Assert.assertEquals("Correct view class", RedirectView.class, view.getClass());
        Assert.assertEquals("Correct URL", "myUrl", getUrl());
        view = vr.resolveViewName("forward:myUrl", Locale.getDefault());
        Assert.assertEquals("Correct view class", InternalResourceView.class, view.getClass());
        Assert.assertEquals("Correct URL", "myUrl", getUrl());
    }

    private class TestConfiguration extends Configuration {
        TestConfiguration() {
            super(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        }

        @Override
        public Template getTemplate(String name, final Locale locale) throws IOException {
            if ((name.equals("templateName")) || (name.equals("prefix_test_suffix"))) {
                return new Template(name, new StringReader("test"), this) {
                    @Override
                    public void process(Object model, Writer writer) throws TemplateException, IOException {
                        Assert.assertEquals(Locale.US, locale);
                        Assert.assertTrue((model instanceof AllHttpScopesHashModel));
                        AllHttpScopesHashModel fmModel = ((AllHttpScopesHashModel) (model));
                        Assert.assertEquals("myvalue", fmModel.get("myattr").toString());
                    }
                };
            } else {
                throw new FileNotFoundException();
            }
        }
    }
}

