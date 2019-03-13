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


import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.context.WebApplicationContext;


/**
 * Unit tests for ERB templates running on JRuby.
 *
 * @author Sebastien Deleuze
 */
@Ignore("JRuby not compatible with JDK 9 yet")
public class JRubyScriptTemplateTests {
    private WebApplicationContext webAppContext;

    private ServletContext servletContext;

    @Test
    public void renderTemplate() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Layout example");
        model.put("body", "This is the body");
        String url = "org/springframework/web/servlet/view/script/jruby/template.erb";
        MockHttpServletResponse response = render(url, model);
        Assert.assertEquals("<html><head><title>Layout example</title></head><body><p>This is the body</p></body></html>", response.getContentAsString());
    }

    @Configuration
    static class ScriptTemplatingConfiguration {
        @Bean
        public ScriptTemplateConfigurer jRubyConfigurer() {
            ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
            configurer.setScripts("org/springframework/web/servlet/view/script/jruby/render.rb");
            configurer.setEngineName("jruby");
            configurer.setRenderFunction("render");
            return configurer;
        }
    }
}

