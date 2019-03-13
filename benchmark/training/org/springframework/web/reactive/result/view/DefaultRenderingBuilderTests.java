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
package org.springframework.web.reactive.result.view;


import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpHeaders;


/**
 * Unit tests for {@link DefaultRenderingBuilder}.
 *
 * @author Rossen Stoyanchev
 */
public class DefaultRenderingBuilderTests {
    @Test
    public void defaultValues() {
        Rendering rendering = Rendering.view("abc").build();
        Assert.assertEquals("abc", rendering.view());
        Assert.assertEquals(Collections.emptyMap(), rendering.modelAttributes());
        Assert.assertNull(rendering.status());
        Assert.assertEquals(0, rendering.headers().size());
    }

    @Test
    public void defaultValuesForRedirect() throws Exception {
        Rendering rendering = Rendering.redirectTo("abc").build();
        Object view = rendering.view();
        Assert.assertEquals(RedirectView.class, view.getClass());
        Assert.assertEquals("abc", getUrl());
        Assert.assertTrue(isContextRelative());
        Assert.assertFalse(isPropagateQuery());
    }

    @Test
    public void viewName() {
        Rendering rendering = Rendering.view("foo").build();
        Assert.assertEquals("foo", rendering.view());
    }

    @Test
    public void modelAttribute() throws Exception {
        DefaultRenderingBuilderTests.Foo foo = new DefaultRenderingBuilderTests.Foo();
        Rendering rendering = Rendering.view("foo").modelAttribute(foo).build();
        Assert.assertEquals(Collections.singletonMap("foo", foo), rendering.modelAttributes());
    }

    @Test
    public void modelAttributes() throws Exception {
        DefaultRenderingBuilderTests.Foo foo = new DefaultRenderingBuilderTests.Foo();
        DefaultRenderingBuilderTests.Bar bar = new DefaultRenderingBuilderTests.Bar();
        Rendering rendering = Rendering.view("foo").modelAttributes(foo, bar).build();
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("foo", foo);
        map.put("bar", bar);
        Assert.assertEquals(map, rendering.modelAttributes());
    }

    @Test
    public void model() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("foo", new DefaultRenderingBuilderTests.Foo());
        map.put("bar", new DefaultRenderingBuilderTests.Bar());
        Rendering rendering = Rendering.view("foo").model(map).build();
        Assert.assertEquals(map, rendering.modelAttributes());
    }

    @Test
    public void header() throws Exception {
        Rendering rendering = Rendering.view("foo").header("foo", "bar").build();
        Assert.assertEquals(1, rendering.headers().size());
        Assert.assertEquals(Collections.singletonList("bar"), rendering.headers().get("foo"));
    }

    @Test
    public void httpHeaders() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("foo", "bar");
        Rendering rendering = Rendering.view("foo").headers(headers).build();
        Assert.assertEquals(headers, rendering.headers());
    }

    @Test
    public void redirectWithAbsoluteUrl() throws Exception {
        Rendering rendering = Rendering.redirectTo("foo").contextRelative(false).build();
        Object view = rendering.view();
        Assert.assertEquals(RedirectView.class, view.getClass());
        Assert.assertFalse(isContextRelative());
    }

    @Test
    public void redirectWithPropagateQuery() throws Exception {
        Rendering rendering = Rendering.redirectTo("foo").propagateQuery(true).build();
        Object view = rendering.view();
        Assert.assertEquals(RedirectView.class, view.getClass());
        Assert.assertTrue(isPropagateQuery());
    }

    private static class Foo {}

    private static class Bar {}
}

