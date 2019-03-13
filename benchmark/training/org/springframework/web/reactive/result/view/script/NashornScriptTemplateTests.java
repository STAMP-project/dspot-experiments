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
package org.springframework.web.reactive.result.view.script;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.http.server.reactive.test.MockServerHttpResponse;


/**
 * Unit tests for pure JavaScript templates running on Nashorn engine.
 *
 * @author Sebastien Deleuze
 */
public class NashornScriptTemplateTests {
    @Test
    public void renderTemplate() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Layout example");
        model.put("body", "This is the body");
        String url = "org/springframework/web/reactive/result/view/script/nashorn/template.html";
        MockServerHttpResponse response = render(url, model, NashornScriptTemplateTests.ScriptTemplatingConfiguration.class);
        Assert.assertEquals("<html><head><title>Layout example</title></head><body><p>This is the body</p></body></html>", response.getBodyAsString().block());
    }

    // SPR-13453
    @Test
    public void renderTemplateWithUrl() throws Exception {
        String url = "org/springframework/web/reactive/result/view/script/nashorn/template.html";
        Class<?> configClass = NashornScriptTemplateTests.ScriptTemplatingWithUrlConfiguration.class;
        MockServerHttpResponse response = render(url, null, configClass);
        Assert.assertEquals((("<html><head><title>Check url parameter</title></head><body><p>" + url) + "</p></body></html>"), response.getBodyAsString().block());
    }

    @Configuration
    static class ScriptTemplatingConfiguration {
        @Bean
        public ScriptTemplateConfigurer nashornConfigurer() {
            ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
            configurer.setEngineName("nashorn");
            configurer.setScripts("org/springframework/web/reactive/result/view/script/nashorn/render.js");
            configurer.setRenderFunction("render");
            return configurer;
        }
    }

    @Configuration
    static class ScriptTemplatingWithUrlConfiguration {
        @Bean
        public ScriptTemplateConfigurer nashornConfigurer() {
            ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
            configurer.setEngineName("nashorn");
            configurer.setScripts("org/springframework/web/reactive/result/view/script/nashorn/render.js");
            configurer.setRenderFunction("renderWithUrl");
            return configurer;
        }
    }
}

