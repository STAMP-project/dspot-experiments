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


import com.google.common.collect.Maps;
import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.io.StringWriter;
import java.util.Map;
import ninja.Context;
import ninja.session.Session;
import ninja.template.directives.TemplateEngineFreemarkerAuthenticityTokenDirective;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineFreemarkerAuthenticityTokenDirectiveTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    Context context;

    @Mock
    Session session;

    StringWriter stringWriter = new StringWriter();

    Environment environment = new Environment(Mockito.mock(Template.class), null, stringWriter);

    TemplateEngineFreemarkerAuthenticityTokenDirective templateEngineFreemarkerAuthenticityTokenDirective;

    Map<String, String> parameters = Maps.newHashMap();

    @Test
    public void testThatAuthenticityTokenIsNotCalledInConstructor() throws Exception {
        Mockito.verify(session, Mockito.never()).getAuthenticityToken();
    }

    @Test
    public void testThatItWorks() throws Exception {
        TemplateModel[] loopVars = new TemplateModel[0];
        templateEngineFreemarkerAuthenticityTokenDirective.execute(environment, parameters, loopVars, null);
        Assert.assertThat(stringWriter.toString(), CoreMatchers.equalTo("12345"));
        Mockito.verify(session).getAuthenticityToken();
    }

    @Test
    public void testThatParamsThrowException() throws Exception {
        thrown.expect(TemplateException.class);
        parameters.put("foo", "bar");
        templateEngineFreemarkerAuthenticityTokenDirective.execute(null, parameters, null, null);
    }

    @Test
    public void testThatLoopVarsThrowException() throws Exception {
        TemplateModel[] loopVars = new TemplateModel[1];
        thrown.expect(TemplateException.class);
        loopVars[0] = new TemplateModel() {};
        templateEngineFreemarkerAuthenticityTokenDirective.execute(null, parameters, loopVars, null);
    }
}

