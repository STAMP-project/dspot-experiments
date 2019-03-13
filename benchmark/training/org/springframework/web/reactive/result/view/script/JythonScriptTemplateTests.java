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
package org.springframework.web.reactive.result.view.script;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.http.server.reactive.test.MockServerHttpResponse;


/**
 * Unit tests for String templates running on Jython.
 *
 * @author Sebastien Deleuze
 */
public class JythonScriptTemplateTests {
    @Test
    public void renderTemplate() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Layout example");
        model.put("body", "This is the body");
        String url = "org/springframework/web/reactive/result/view/script/jython/template.html";
        MockServerHttpResponse response = renderViewWithModel(url, model);
        Assert.assertEquals("<html><head><title>Layout example</title></head><body><p>This is the body</p></body></html>", response.getBodyAsString().block());
    }

    @Configuration
    static class ScriptTemplatingConfiguration {
        @Bean
        public ScriptTemplateConfigurer jythonConfigurer() {
            ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
            configurer.setScripts("org/springframework/web/reactive/result/view/script/jython/render.py");
            configurer.setEngineName("jython");
            configurer.setRenderFunction("render");
            return configurer;
        }
    }
}

