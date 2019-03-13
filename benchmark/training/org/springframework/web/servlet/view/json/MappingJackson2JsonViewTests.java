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
package org.springframework.web.servlet.view.json;


import MappingJackson2JsonView.DEFAULT_CONTENT_TYPE;
import View.SELECTED_CONTENT_TYPE;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.http.MediaType;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;


/**
 *
 *
 * @author Jeremy Grelle
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 */
public class MappingJackson2JsonViewTests {
    private MappingJackson2JsonView view;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private Context jsContext;

    private ScriptableObject jsScope;

    @Test
    public void isExposePathVars() {
        Assert.assertEquals("Must not expose path variables", false, view.isExposePathVariables());
    }

    @Test
    public void renderSimpleMap() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("bindingResult", Mockito.mock(BindingResult.class, "binding_result"));
        model.put("foo", "bar");
        view.setUpdateContentLength(true);
        view.render(model, request, response);
        Assert.assertEquals("no-store", response.getHeader("Cache-Control"));
        Assert.assertEquals(DEFAULT_CONTENT_TYPE, response.getContentType());
        String jsonResult = response.getContentAsString();
        Assert.assertTrue(((jsonResult.length()) > 0));
        Assert.assertEquals(jsonResult.length(), response.getContentLength());
        validateResult();
    }

    @Test
    public void renderWithSelectedContentType() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("foo", "bar");
        view.render(model, request, response);
        Assert.assertEquals("application/json", response.getContentType());
        request.setAttribute(SELECTED_CONTENT_TYPE, new MediaType("application", "vnd.example-v2+xml"));
        view.render(model, request, response);
        Assert.assertEquals("application/vnd.example-v2+xml", response.getContentType());
    }

    @Test
    public void renderCaching() throws Exception {
        view.setDisableCaching(false);
        Map<String, Object> model = new HashMap<>();
        model.put("bindingResult", Mockito.mock(BindingResult.class, "binding_result"));
        model.put("foo", "bar");
        view.render(model, request, response);
        Assert.assertNull(response.getHeader("Cache-Control"));
    }

    @Test
    public void renderSimpleMapPrefixed() throws Exception {
        view.setPrefixJson(true);
        renderSimpleMap();
    }

    @Test
    public void renderSimpleBean() throws Exception {
        Object bean = new MappingJackson2JsonViewTests.TestBeanSimple();
        Map<String, Object> model = new HashMap<>();
        model.put("bindingResult", Mockito.mock(BindingResult.class, "binding_result"));
        model.put("foo", bean);
        view.setUpdateContentLength(true);
        view.render(model, request, response);
        Assert.assertTrue(((response.getContentAsString().length()) > 0));
        Assert.assertEquals(response.getContentAsString().length(), response.getContentLength());
        validateResult();
    }

    @Test
    public void renderWithPrettyPrint() throws Exception {
        ModelMap model = new ModelMap("foo", new MappingJackson2JsonViewTests.TestBeanSimple());
        view.setPrettyPrint(true);
        view.render(model, request, response);
        String result = response.getContentAsString().replace("\r\n", "\n");
        Assert.assertTrue(("Pretty printing not applied:\n" + result), result.startsWith("{\n  \"foo\" : {\n    "));
        validateResult();
    }

    @Test
    public void renderSimpleBeanPrefixed() throws Exception {
        view.setPrefixJson(true);
        renderSimpleBean();
        Assert.assertTrue(response.getContentAsString().startsWith(")]}', "));
    }

    @Test
    public void renderSimpleBeanNotPrefixed() throws Exception {
        view.setPrefixJson(false);
        renderSimpleBean();
        Assert.assertFalse(response.getContentAsString().startsWith(")]}', "));
    }

    @Test
    public void renderWithCustomSerializerLocatedByAnnotation() throws Exception {
        Object bean = new MappingJackson2JsonViewTests.TestBeanSimpleAnnotated();
        Map<String, Object> model = new HashMap<>();
        model.put("foo", bean);
        view.render(model, request, response);
        Assert.assertTrue(((response.getContentAsString().length()) > 0));
        Assert.assertEquals("{\"foo\":{\"testBeanSimple\":\"custom\"}}", response.getContentAsString());
        validateResult();
    }

    @Test
    public void renderWithCustomSerializerLocatedByFactory() throws Exception {
        SerializerFactory factory = new MappingJackson2JsonViewTests.DelegatingSerializerFactory(null);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializerFactory(factory);
        view.setObjectMapper(mapper);
        Object bean = new MappingJackson2JsonViewTests.TestBeanSimple();
        Map<String, Object> model = new HashMap<>();
        model.put("foo", bean);
        model.put("bar", new MappingJackson2JsonViewTests.TestChildBean());
        view.render(model, request, response);
        String result = response.getContentAsString();
        Assert.assertTrue(((result.length()) > 0));
        Assert.assertTrue(result.contains("\"foo\":{\"testBeanSimple\":\"custom\"}"));
        validateResult();
    }

    @Test
    public void renderOnlyIncludedAttributes() throws Exception {
        Set<String> attrs = new HashSet<>();
        attrs.add("foo");
        attrs.add("baz");
        attrs.add("nil");
        view.setModelKeys(attrs);
        Map<String, Object> model = new HashMap<>();
        model.put("foo", "foo");
        model.put("bar", "bar");
        model.put("baz", "baz");
        view.render(model, request, response);
        String result = response.getContentAsString();
        Assert.assertTrue(((result.length()) > 0));
        Assert.assertTrue(result.contains("\"foo\":\"foo\""));
        Assert.assertTrue(result.contains("\"baz\":\"baz\""));
        validateResult();
    }

    @Test
    public void filterSingleKeyModel() throws Exception {
        view.setExtractValueFromSingleKeyModel(true);
        Map<String, Object> model = new HashMap<>();
        MappingJackson2JsonViewTests.TestBeanSimple bean = new MappingJackson2JsonViewTests.TestBeanSimple();
        model.put("foo", bean);
        Object actual = view.filterModel(model);
        Assert.assertSame(bean, actual);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void filterTwoKeyModel() throws Exception {
        view.setExtractValueFromSingleKeyModel(true);
        Map<String, Object> model = new HashMap<>();
        MappingJackson2JsonViewTests.TestBeanSimple bean1 = new MappingJackson2JsonViewTests.TestBeanSimple();
        MappingJackson2JsonViewTests.TestBeanSimple bean2 = new MappingJackson2JsonViewTests.TestBeanSimple();
        model.put("foo1", bean1);
        model.put("foo2", bean2);
        Object actual = view.filterModel(model);
        Assert.assertTrue((actual instanceof Map));
        Assert.assertSame(bean1, ((Map) (actual)).get("foo1"));
        Assert.assertSame(bean2, ((Map) (actual)).get("foo2"));
    }

    @Test
    public void renderSimpleBeanWithJsonView() throws Exception {
        Object bean = new MappingJackson2JsonViewTests.TestBeanSimple();
        Map<String, Object> model = new HashMap<>();
        model.put("bindingResult", Mockito.mock(BindingResult.class, "binding_result"));
        model.put("foo", bean);
        model.put(JsonView.class.getName(), MappingJackson2JsonViewTests.MyJacksonView1.class);
        view.setUpdateContentLength(true);
        view.render(model, request, response);
        String content = response.getContentAsString();
        Assert.assertTrue(((content.length()) > 0));
        Assert.assertEquals(content.length(), response.getContentLength());
        Assert.assertTrue(content.contains("foo"));
        Assert.assertFalse(content.contains("boo"));
        Assert.assertFalse(content.contains(JsonView.class.getName()));
    }

    @Test
    public void renderSimpleBeanWithFilters() throws Exception {
        MappingJackson2JsonViewTests.TestSimpleBeanFiltered bean = new MappingJackson2JsonViewTests.TestSimpleBeanFiltered();
        bean.setProperty1("value");
        bean.setProperty2("value");
        Map<String, Object> model = new HashMap<>();
        model.put("bindingResult", Mockito.mock(BindingResult.class, "binding_result"));
        model.put("foo", bean);
        FilterProvider filters = new SimpleFilterProvider().addFilter("myJacksonFilter", SimpleBeanPropertyFilter.serializeAllExcept("property2"));
        model.put(FilterProvider.class.getName(), filters);
        view.setUpdateContentLength(true);
        view.render(model, request, response);
        String content = response.getContentAsString();
        Assert.assertTrue(((content.length()) > 0));
        Assert.assertEquals(content.length(), response.getContentLength());
        Assert.assertThat(content, CoreMatchers.containsString("\"property1\":\"value\""));
        Assert.assertThat(content, CoreMatchers.not(CoreMatchers.containsString("\"property2\":\"value\"")));
        Assert.assertFalse(content.contains(FilterProvider.class.getName()));
    }

    public interface MyJacksonView1 {}

    public interface MyJacksonView2 {}

    @SuppressWarnings("unused")
    public static class TestBeanSimple {
        @JsonView(MappingJackson2JsonViewTests.MyJacksonView1.class)
        private String property1 = "foo";

        private boolean test = false;

        @JsonView(MappingJackson2JsonViewTests.MyJacksonView2.class)
        private String property2 = "boo";

        private MappingJackson2JsonViewTests.TestChildBean child = new MappingJackson2JsonViewTests.TestChildBean();

        public String getProperty1() {
            return property1;
        }

        public boolean getTest() {
            return test;
        }

        public String getProperty2() {
            return property2;
        }

        public Date getNow() {
            return new Date();
        }

        public MappingJackson2JsonViewTests.TestChildBean getChild() {
            return child;
        }
    }

    @JsonSerialize(using = MappingJackson2JsonViewTests.TestBeanSimpleSerializer.class)
    public static class TestBeanSimpleAnnotated extends MappingJackson2JsonViewTests.TestBeanSimple {}

    public static class TestChildBean {
        private String value = "bar";

        private String baz = null;

        private MappingJackson2JsonViewTests.TestBeanSimple parent = null;

        public String getValue() {
            return value;
        }

        public String getBaz() {
            return baz;
        }

        public MappingJackson2JsonViewTests.TestBeanSimple getParent() {
            return parent;
        }

        public void setParent(MappingJackson2JsonViewTests.TestBeanSimple parent) {
            this.parent = parent;
        }
    }

    public static class TestBeanSimpleSerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartObject();
            jgen.writeFieldName("testBeanSimple");
            jgen.writeString("custom");
            jgen.writeEndObject();
        }
    }

    @JsonFilter("myJacksonFilter")
    @SuppressWarnings("unused")
    private static class TestSimpleBeanFiltered {
        private String property1;

        private String property2;

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public String getProperty2() {
            return property2;
        }

        public void setProperty2(String property2) {
            this.property2 = property2;
        }
    }

    @SuppressWarnings("serial")
    public static class DelegatingSerializerFactory extends BeanSerializerFactory {
        protected DelegatingSerializerFactory(SerializerFactoryConfig config) {
            super(config);
        }

        @Override
        public JsonSerializer<Object> createSerializer(SerializerProvider prov, JavaType type) throws JsonMappingException {
            if ((type.getRawClass()) == (MappingJackson2JsonViewTests.TestBeanSimple.class)) {
                return new MappingJackson2JsonViewTests.TestBeanSimpleSerializer();
            } else {
                return super.createSerializer(prov, type);
            }
        }
    }
}

