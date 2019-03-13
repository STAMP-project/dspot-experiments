/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ninja.template;


import TemplateEngineFreemarker.FREEMARKER_CONFIGURATION_FILE_SUFFIX;
import freemarker.template.Configuration;
import java.io.Writer;
import javax.inject.Singleton;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.Route;
import ninja.exceptions.RenderingException;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.utils.NinjaProperties;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;


@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineFreemarkerTest {
    @Mock
    Lang lang;

    @Mock
    Logger logger;

    @Mock
    TemplateEngineHelper templateEngineHelper;

    @Mock
    TemplateEngineManager templateEngineManager;

    @Mock
    TemplateEngineFreemarkerReverseRouteMethod templateEngineFreemarkerReverseRouteMethod;

    @Mock
    TemplateEngineFreemarkerAssetsAtMethod templateEngineFreemarkerAssetsAtMethod;

    @Mock
    TemplateEngineFreemarkerWebJarsAtMethod templateEngineFreemarkerWebJarsAtMethod;

    @Mock
    NinjaProperties ninjaProperties;

    @Mock
    Messages messages;

    @Mock
    Context context;

    @Mock
    Result result;

    @Mock
    Route route;

    TemplateEngineFreemarker templateEngineFreemarker;

    Writer writer;

    @Test
    public void testThatTemplateEngineFreemarkerHasSingletonAnnotation() {
        Singleton singleton = TemplateEngineFreemarker.class.getAnnotation(Singleton.class);
        Assert.assertThat(singleton, CoreMatchers.notNullValue());
    }

    @Test
    public void testBasicInvocation() throws Exception {
        templateEngineFreemarker.invoke(context, Results.ok());
        Mockito.verify(ninjaProperties).getWithDefault(FREEMARKER_CONFIGURATION_FILE_SUFFIX, ".ftl.html");
        Assert.assertThat(templateEngineFreemarker.getSuffixOfTemplatingEngine(), CoreMatchers.equalTo(".ftl.html"));
        Mockito.verify(templateEngineHelper).getTemplateForResult(ArgumentMatchers.eq(route), ArgumentMatchers.any(Result.class), ArgumentMatchers.eq(".ftl.html"));
        Assert.assertThat(writer.toString(), CoreMatchers.equalTo("Just a plain template for testing..."));
    }

    @Test
    public void testThatConfigurationCanBeRetrieved() throws Exception {
        templateEngineFreemarker.invoke(context, Results.ok());
        Assert.assertThat(templateEngineFreemarker.getConfiguration(), CoreMatchers.notNullValue(Configuration.class));
    }

    @Test
    public void testThatWhenNotProdModeThrowsRenderingException() {
        Mockito.when(templateEngineHelper.getTemplateForResult(ArgumentMatchers.any(Route.class), ArgumentMatchers.any(Result.class), Mockito.anyString())).thenReturn("views/broken.ftl.html");
        // only freemarker templates generated exceptions to browser -- it makes
        // sense that this continues in diagnostic mode only
        // when(ninjaProperties.isDev()).thenReturn(true);
        // when(ninjaProperties.areDiagnosticsEnabled()).thenReturn(true);
        try {
            templateEngineFreemarker.invoke(context, Results.ok());
            Assert.fail("exception expected");
        } catch (RenderingException e) {
            // expected
        }
    }

    @Test(expected = RuntimeException.class)
    public void testThatProdModeThrowsTemplateException() throws RuntimeException {
        Mockito.when(templateEngineHelper.getTemplateForResult(ArgumentMatchers.any(Route.class), ArgumentMatchers.any(Result.class), Mockito.anyString())).thenReturn("views/broken.ftl.html");
        Mockito.when(ninjaProperties.isProd()).thenReturn(true);
        templateEngineFreemarker.invoke(context, Results.ok());
    }
}

