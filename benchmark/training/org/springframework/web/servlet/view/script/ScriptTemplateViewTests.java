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
package org.springframework.web.servlet.view.script;


import DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import HttpHeaders.CONTENT_TYPE;
import MediaType.TEXT_HTML_VALUE;
import MediaType.TEXT_PLAIN_VALUE;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;


/**
 * Unit tests for {@link ScriptTemplateView}.
 *
 * @author Sebastien Deleuze
 */
public class ScriptTemplateViewTests {
    private ScriptTemplateView view;

    private ScriptTemplateConfigurer configurer;

    private StaticWebApplicationContext wac;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void missingTemplate() throws Exception {
        MockServletContext servletContext = new MockServletContext();
        this.wac.setServletContext(servletContext);
        this.wac.refresh();
        this.view.setResourceLoaderPath("classpath:org/springframework/web/servlet/view/script/");
        this.view.setUrl("missing.txt");
        this.view.setEngine(Mockito.mock(ScriptTemplateViewTests.InvocableScriptEngine.class));
        this.configurer.setRenderFunction("render");
        this.view.setApplicationContext(this.wac);
        Assert.assertFalse(this.view.checkResource(Locale.ENGLISH));
    }

    @Test
    public void missingScriptTemplateConfig() throws Exception {
        this.expectedException.expect(ApplicationContextException.class);
        this.view.setApplicationContext(new StaticApplicationContext());
        this.expectedException.expectMessage(ArgumentMatchers.contains("ScriptTemplateConfig"));
    }

    @Test
    public void detectScriptTemplateConfigWithEngine() {
        ScriptTemplateViewTests.InvocableScriptEngine engine = Mockito.mock(ScriptTemplateViewTests.InvocableScriptEngine.class);
        this.configurer.setEngine(engine);
        this.configurer.setRenderObject("Template");
        this.configurer.setRenderFunction("render");
        this.configurer.setContentType(TEXT_PLAIN_VALUE);
        this.configurer.setCharset(StandardCharsets.ISO_8859_1);
        this.configurer.setSharedEngine(true);
        DirectFieldAccessor accessor = new DirectFieldAccessor(this.view);
        this.view.setApplicationContext(this.wac);
        Assert.assertEquals(engine, accessor.getPropertyValue("engine"));
        Assert.assertEquals("Template", accessor.getPropertyValue("renderObject"));
        Assert.assertEquals("render", accessor.getPropertyValue("renderFunction"));
        Assert.assertEquals(TEXT_PLAIN_VALUE, accessor.getPropertyValue("contentType"));
        Assert.assertEquals(StandardCharsets.ISO_8859_1, accessor.getPropertyValue("charset"));
        Assert.assertEquals(true, accessor.getPropertyValue("sharedEngine"));
    }

    @Test
    public void detectScriptTemplateConfigWithEngineName() {
        this.configurer.setEngineName("nashorn");
        this.configurer.setRenderObject("Template");
        this.configurer.setRenderFunction("render");
        DirectFieldAccessor accessor = new DirectFieldAccessor(this.view);
        this.view.setApplicationContext(this.wac);
        Assert.assertEquals("nashorn", accessor.getPropertyValue("engineName"));
        Assert.assertNotNull(accessor.getPropertyValue("engine"));
        Assert.assertEquals("Template", accessor.getPropertyValue("renderObject"));
        Assert.assertEquals("render", accessor.getPropertyValue("renderFunction"));
        Assert.assertEquals(TEXT_HTML_VALUE, accessor.getPropertyValue("contentType"));
        Assert.assertEquals(StandardCharsets.UTF_8, accessor.getPropertyValue("charset"));
    }

    @Test
    public void customEngineAndRenderFunction() throws Exception {
        ScriptEngine engine = Mockito.mock(ScriptTemplateViewTests.InvocableScriptEngine.class);
        BDDMockito.given(engine.get("key")).willReturn("value");
        this.view.setEngine(engine);
        this.view.setRenderFunction("render");
        this.view.setApplicationContext(this.wac);
        engine = this.view.getEngine();
        Assert.assertNotNull(engine);
        Assert.assertEquals("value", engine.get("key"));
        DirectFieldAccessor accessor = new DirectFieldAccessor(this.view);
        Assert.assertNull(accessor.getPropertyValue("renderObject"));
        Assert.assertEquals("render", accessor.getPropertyValue("renderFunction"));
        Assert.assertEquals(StandardCharsets.UTF_8, accessor.getPropertyValue("charset"));
    }

    @Test
    public void nonSharedEngine() throws Exception {
        int iterations = 20;
        this.view.setEngineName("nashorn");
        this.view.setRenderFunction("render");
        this.view.setSharedEngine(false);
        this.view.setApplicationContext(this.wac);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<Boolean>> results = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            results.add(executor.submit(() -> (view.getEngine()) != null));
        }
        Assert.assertEquals(iterations, results.size());
        for (int i = 0; i < iterations; i++) {
            Assert.assertTrue(results.get(i).get());
        }
        executor.shutdown();
    }

    @Test
    public void nonInvocableScriptEngine() throws Exception {
        this.view.setEngine(Mockito.mock(ScriptEngine.class));
        this.view.setApplicationContext(this.wac);
    }

    @Test
    public void nonInvocableScriptEngineWithRenderFunction() throws Exception {
        this.view.setEngine(Mockito.mock(ScriptEngine.class));
        this.view.setRenderFunction("render");
        this.expectedException.expect(IllegalArgumentException.class);
        this.view.setApplicationContext(this.wac);
    }

    @Test
    public void engineAndEngineNameBothDefined() {
        this.view.setEngine(Mockito.mock(ScriptTemplateViewTests.InvocableScriptEngine.class));
        this.view.setEngineName("test");
        this.view.setRenderFunction("render");
        this.expectedException.expect(IllegalArgumentException.class);
        this.view.setApplicationContext(this.wac);
        this.expectedException.expectMessage(ArgumentMatchers.contains("'engine' or 'engineName'"));
    }

    @Test
    public void engineSetterAndNonSharedEngine() {
        this.view.setEngine(Mockito.mock(ScriptTemplateViewTests.InvocableScriptEngine.class));
        this.view.setRenderFunction("render");
        this.view.setSharedEngine(false);
        this.expectedException.expect(IllegalArgumentException.class);
        this.view.setApplicationContext(this.wac);
        this.expectedException.expectMessage(ArgumentMatchers.contains("sharedEngine"));
    }

    // SPR-14210
    @Test
    public void resourceLoaderPath() throws Exception {
        MockServletContext servletContext = new MockServletContext();
        this.wac.setServletContext(servletContext);
        this.wac.refresh();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.wac);
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, Object> model = new HashMap<>();
        ScriptTemplateViewTests.InvocableScriptEngine engine = Mockito.mock(ScriptTemplateViewTests.InvocableScriptEngine.class);
        Mockito.when(engine.invokeFunction(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("foo");
        this.view.setEngine(engine);
        this.view.setRenderFunction("render");
        this.view.setApplicationContext(this.wac);
        this.view.setUrl("org/springframework/web/servlet/view/script/empty.txt");
        this.view.render(model, request, response);
        Assert.assertEquals("foo", response.getContentAsString());
        response = new MockHttpServletResponse();
        this.view.setResourceLoaderPath("classpath:org/springframework/web/servlet/view/script/");
        this.view.setUrl("empty.txt");
        this.view.render(model, request, response);
        Assert.assertEquals("foo", response.getContentAsString());
        response = new MockHttpServletResponse();
        this.view.setResourceLoaderPath("classpath:org/springframework/web/servlet/view/script");
        this.view.setUrl("empty.txt");
        this.view.render(model, request, response);
        Assert.assertEquals("foo", response.getContentAsString());
    }

    // SPR-13379
    @Test
    public void contentType() throws Exception {
        MockServletContext servletContext = new MockServletContext();
        this.wac.setServletContext(servletContext);
        this.wac.refresh();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.wac);
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, Object> model = new HashMap<>();
        this.view.setEngine(Mockito.mock(ScriptTemplateViewTests.InvocableScriptEngine.class));
        this.view.setRenderFunction("render");
        this.view.setResourceLoaderPath("classpath:org/springframework/web/servlet/view/script/");
        this.view.setUrl("empty.txt");
        this.view.setApplicationContext(this.wac);
        this.view.render(model, request, response);
        Assert.assertEquals((((MediaType.TEXT_HTML_VALUE) + ";charset=") + (StandardCharsets.UTF_8)), response.getHeader(CONTENT_TYPE));
        response = new MockHttpServletResponse();
        this.view.setContentType(TEXT_PLAIN_VALUE);
        this.view.render(model, request, response);
        Assert.assertEquals((((MediaType.TEXT_PLAIN_VALUE) + ";charset=") + (StandardCharsets.UTF_8)), response.getHeader(CONTENT_TYPE));
        response = new MockHttpServletResponse();
        this.view.setCharset(StandardCharsets.ISO_8859_1);
        this.view.render(model, request, response);
        Assert.assertEquals((((MediaType.TEXT_PLAIN_VALUE) + ";charset=") + (StandardCharsets.ISO_8859_1)), response.getHeader(CONTENT_TYPE));
    }

    private interface InvocableScriptEngine extends Invocable , ScriptEngine {}
}

