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


import ContentTypes.APPLICATION_JSON;
import ContentTypes.APPLICATION_JSONP;
import ContentTypes.TEXT_HTML;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import ninja.ContentTypes;
import ninja.Context;
import ninja.Result;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TemplateEngineManagerImplTest {
    @Test
    public void testGetJson() {
        Assert.assertThat(createTemplateEngineManager().getTemplateEngineForContentType(APPLICATION_JSON), CoreMatchers.instanceOf(TemplateEngineJson.class));
    }

    @Test
    public void testGetJsonP() {
        Assert.assertThat(createTemplateEngineManager().getTemplateEngineForContentType(APPLICATION_JSONP), CoreMatchers.instanceOf(TemplateEngineJsonP.class));
    }

    @Test
    public void testGetFreemarker() {
        Assert.assertThat(createTemplateEngineManager().getTemplateEngineForContentType(TEXT_HTML), CoreMatchers.instanceOf(TemplateEngineFreemarker.class));
    }

    @Test
    public void testGetCustom() {
        Assert.assertThat(createTemplateEngineManager(TemplateEngineManagerImplTest.CustomTemplateEngine.class).getTemplateEngineForContentType("custom"), CoreMatchers.instanceOf(TemplateEngineManagerImplTest.CustomTemplateEngine.class));
    }

    @Test
    public void testOverrideJson() {
        Assert.assertThat(createTemplateEngineManager(TemplateEngineManagerImplTest.OverrideJsonTemplateEngine.class).getTemplateEngineForContentType(APPLICATION_JSON), CoreMatchers.instanceOf(TemplateEngineManagerImplTest.OverrideJsonTemplateEngine.class));
    }

    @Test
    public void testOverrideHtml() {
        Assert.assertThat(createTemplateEngineManager(TemplateEngineManagerImplTest.OverrideHtmlTemplateEngine.class).getTemplateEngineForContentType(TEXT_HTML), CoreMatchers.instanceOf(TemplateEngineManagerImplTest.OverrideHtmlTemplateEngine.class));
    }

    @Test
    public void testOverrideHtmlOrderMatters() {
        TemplateEngineManager templateEngineManager = createTemplateEngineManager(TemplateEngineManagerImplTest.OverrideHtmlTemplateEngine.class, TemplateEngineManagerImplTest.OverrideHtmlTemplateEngine3.class, TemplateEngineManagerImplTest.OverrideHtmlTemplateEngine2.class);
        Assert.assertThat(templateEngineManager.getTemplateEngineForContentType(TEXT_HTML), CoreMatchers.instanceOf(TemplateEngineManagerImplTest.OverrideHtmlTemplateEngine2.class));
    }

    @Test
    public void testContentTypes() {
        List<String> types = Lists.newArrayList(createTemplateEngineManager().getContentTypes());
        Collections.sort(types);
        Assert.assertThat(types.toString(), CoreMatchers.equalTo("[application/javascript, application/json, application/xml, text/html, text/plain]"));
    }

    @Test
    public void testGetNonExistingProducesNoNPE() {
        TemplateEngineManager manager = createTemplateEngineManager(TemplateEngineManagerImplTest.OverrideJsonTemplateEngine.class);
        Assert.assertNull(manager.getTemplateEngineForContentType("non/existing"));
    }

    public abstract static class MockTemplateEngine implements TemplateEngine {
        @Override
        public void invoke(Context context, Result result) {
        }

        @Override
        public String getSuffixOfTemplatingEngine() {
            return null;
        }
    }

    public static class CustomTemplateEngine extends TemplateEngineManagerImplTest.MockTemplateEngine {
        @Override
        public String getContentType() {
            return "custom";
        }
    }

    public static class OverrideJsonTemplateEngine extends TemplateEngineManagerImplTest.MockTemplateEngine {
        @Override
        public String getContentType() {
            return ContentTypes.APPLICATION_JSON;
        }
    }

    public static class OverrideHtmlTemplateEngine extends TemplateEngineManagerImplTest.MockTemplateEngine {
        @Override
        public String getContentType() {
            return ContentTypes.TEXT_HTML;
        }
    }

    public static class OverrideHtmlTemplateEngine2 extends TemplateEngineManagerImplTest.MockTemplateEngine {
        @Override
        public String getContentType() {
            return ContentTypes.TEXT_HTML;
        }
    }

    public static class OverrideHtmlTemplateEngine3 extends TemplateEngineManagerImplTest.MockTemplateEngine {
        @Override
        public String getContentType() {
            return ContentTypes.TEXT_HTML;
        }
    }
}

