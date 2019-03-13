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
package org.springframework.web.reactive.result.view.freemarker;


import freemarker.template.Configuration;
import java.time.Duration;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.ModelMap;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Rossen Stoyanchev
 */
public class FreeMarkerViewTests {
    private static final String TEMPLATE_PATH = "classpath*:org/springframework/web/reactive/view/freemarker/";

    private final MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/path"));

    private GenericApplicationContext context;

    private Configuration freeMarkerConfig;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void noFreeMarkerConfig() throws Exception {
        this.exception.expect(ApplicationContextException.class);
        this.exception.expectMessage("Must define a single FreeMarkerConfig bean");
        FreeMarkerView view = new FreeMarkerView();
        view.setApplicationContext(this.context);
        view.setUrl("anythingButNull");
        view.afterPropertiesSet();
    }

    @Test
    public void noTemplateName() throws Exception {
        this.exception.expect(IllegalArgumentException.class);
        this.exception.expectMessage("Property 'url' is required");
        FreeMarkerView freeMarkerView = new FreeMarkerView();
        freeMarkerView.afterPropertiesSet();
    }

    @Test
    public void checkResourceExists() throws Exception {
        FreeMarkerView view = new FreeMarkerView();
        view.setConfiguration(this.freeMarkerConfig);
        view.setUrl("test.ftl");
        Assert.assertTrue(view.checkResourceExists(Locale.US));
    }

    @Test
    public void render() throws Exception {
        FreeMarkerView view = new FreeMarkerView();
        view.setConfiguration(this.freeMarkerConfig);
        view.setUrl("test.ftl");
        ModelMap model = new ExtendedModelMap();
        model.addAttribute("hello", "hi FreeMarker");
        view.render(model, null, this.exchange).block(Duration.ofMillis(5000));
        StepVerifier.create(this.exchange.getResponse().getBody()).consumeNextWith(( buf) -> assertEquals("<html><body>hi FreeMarker</body></html>", asString(buf))).expectComplete().verify();
    }
}

