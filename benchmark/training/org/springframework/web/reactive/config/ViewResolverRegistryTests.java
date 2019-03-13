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
package org.springframework.web.reactive.config;


import Ordered.LOWEST_PRECEDENCE;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.result.view.UrlBasedViewResolver;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.reactive.result.view.script.ScriptTemplateViewResolver;


/**
 * Unit tests for {@link ViewResolverRegistry}.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 */
public class ViewResolverRegistryTests {
    private ViewResolverRegistry registry;

    @Test
    public void order() {
        Assert.assertEquals(LOWEST_PRECEDENCE, this.registry.getOrder());
    }

    @Test
    public void hasRegistrations() {
        Assert.assertFalse(this.registry.hasRegistrations());
        this.registry.freeMarker();
        Assert.assertTrue(this.registry.hasRegistrations());
    }

    @Test
    public void noResolvers() {
        Assert.assertNotNull(this.registry.getViewResolvers());
        Assert.assertEquals(0, this.registry.getViewResolvers().size());
        Assert.assertFalse(this.registry.hasRegistrations());
    }

    @Test
    public void customViewResolver() {
        UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
        this.registry.viewResolver(viewResolver);
        Assert.assertSame(viewResolver, this.registry.getViewResolvers().get(0));
        Assert.assertEquals(1, this.registry.getViewResolvers().size());
    }

    @Test
    public void defaultViews() throws Exception {
        View view = new org.springframework.web.reactive.result.view.HttpMessageWriterView(new Jackson2JsonEncoder());
        this.registry.defaultViews(view);
        Assert.assertEquals(1, this.registry.getDefaultViews().size());
        Assert.assertSame(view, this.registry.getDefaultViews().get(0));
    }

    // SPR-16431
    @Test
    public void scriptTemplate() {
        this.registry.scriptTemplate().prefix("/").suffix(".html");
        List<ViewResolver> viewResolvers = this.registry.getViewResolvers();
        Assert.assertEquals(1, viewResolvers.size());
        Assert.assertEquals(ScriptTemplateViewResolver.class, viewResolvers.get(0).getClass());
        DirectFieldAccessor accessor = new DirectFieldAccessor(viewResolvers.get(0));
        Assert.assertEquals("/", accessor.getPropertyValue("prefix"));
        Assert.assertEquals(".html", accessor.getPropertyValue("suffix"));
    }
}

