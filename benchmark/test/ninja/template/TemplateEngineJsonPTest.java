/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.template;


import TemplateEngineJsonP.DEFAULT_CALLBACK_PARAMETER_VALUE;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import ninja.Context;
import ninja.Result;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static TemplateEngineJsonP.DEFAULT_CALLBACK_PARAMETER_VALUE;


/**
 * Tests for JSONP render.
 */
public class TemplateEngineJsonPTest {
    Logger logger;

    NinjaProperties properties;

    Context context;

    ResponseStreams responseStreams;

    Result result;

    ObjectMapper objectMapper;

    ByteArrayOutputStream outputStream;

    @Test
    public void testCorrectFlow() throws IOException {
        Mockito.when(context.getParameter("callback", DEFAULT_CALLBACK_PARAMETER_VALUE)).thenReturn("App.callback");
        TemplateEngineJsonP jsonpEngine = new TemplateEngineJsonP(objectMapper, properties);
        jsonpEngine.invoke(context, result);
        String jsonp = new String(outputStream.toByteArray(), "UTF-8");
        Assert.assertEquals("App.callback([123])", jsonp);
        Mockito.verify(context).finalizeHeaders(result);
    }

    @Test
    public void testMissingCallbackVariableFlow() throws IOException {
        TemplateEngineJsonP jsonpEngine = new TemplateEngineJsonP(objectMapper, properties);
        jsonpEngine.invoke(context, result);
        String jsonp = new String(outputStream.toByteArray(), "UTF-8");
        Assert.assertEquals(((DEFAULT_CALLBACK_PARAMETER_VALUE) + "([123])"), jsonp);
        Mockito.verify(context).finalizeHeaders(result);
    }

    @Test
    public void testBadCallbackNameFlow() throws IOException {
        Mockito.when(context.getParameter("callback", DEFAULT_CALLBACK_PARAMETER_VALUE)).thenReturn(".callback");
        TemplateEngineJsonP jsonpEngine = new TemplateEngineJsonP(objectMapper, properties);
        jsonpEngine.invoke(context, result);
        String jsonp = new String(outputStream.toByteArray(), "UTF-8");
        Assert.assertEquals(((DEFAULT_CALLBACK_PARAMETER_VALUE) + "([123])"), jsonp);
        Mockito.verify(context).finalizeHeaders(result);
    }

    @Test
    public void testIsThisASecureCallbackName() {
        Assert.assertTrue("simple function", TemplateEngineJsonP.isThisASecureCallbackName("onResponse"));
        Assert.assertTrue("object function", TemplateEngineJsonP.isThisASecureCallbackName("MyPath.path"));
        Assert.assertTrue("object function", TemplateEngineJsonP.isThisASecureCallbackName("MyApp.Path.myCallback123"));
        Assert.assertTrue("object function, path with numbers", TemplateEngineJsonP.isThisASecureCallbackName("MyApp123.Path789.myCallback123"));
        Assert.assertTrue("complex path", TemplateEngineJsonP.isThisASecureCallbackName("Ext.data.JsonP.callback4"));
        Assert.assertTrue("complex path, $ in identity.", TemplateEngineJsonP.isThisASecureCallbackName("$42.ajaxHandler"));
        Assert.assertFalse("wrong first character", TemplateEngineJsonP.isThisASecureCallbackName("42$.q"));
        Assert.assertFalse("period in the front, simple", TemplateEngineJsonP.isThisASecureCallbackName(".onResponse"));
        Assert.assertFalse("period in the end, simple", TemplateEngineJsonP.isThisASecureCallbackName("onResponse."));
        Assert.assertFalse("period in the front, object function", TemplateEngineJsonP.isThisASecureCallbackName(".MyPath.path"));
        Assert.assertFalse("period in the end, complex path", TemplateEngineJsonP.isThisASecureCallbackName("MyPath.path.path2."));
        Assert.assertFalse("two subsequent periods", TemplateEngineJsonP.isThisASecureCallbackName("MyPath..path.path2"));
        Assert.assertFalse("function call", TemplateEngineJsonP.isThisASecureCallbackName("alert(document.cookie)"));
        // Cases not supported by the validator.
        Assert.assertFalse("simple array", TemplateEngineJsonP.isThisASecureCallbackName("somearray[12345]"));
        Assert.assertFalse("unicode characters", TemplateEngineJsonP.isThisASecureCallbackName("\\u0062oo"));
        Assert.assertFalse("unicode characters", TemplateEngineJsonP.isThisASecureCallbackName("\\u0020"));
    }
}

