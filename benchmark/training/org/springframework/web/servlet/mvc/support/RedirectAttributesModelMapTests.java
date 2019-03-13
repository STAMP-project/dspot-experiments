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
package org.springframework.web.servlet.mvc.support;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Test fixture for {@link RedirectAttributesModelMap} tests.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class RedirectAttributesModelMapTests {
    private RedirectAttributesModelMap redirectAttributes;

    private FormattingConversionService conversionService;

    @Test
    public void addAttributePrimitiveType() {
        this.redirectAttributes.addAttribute("speed", 65);
        Assert.assertEquals("65", this.redirectAttributes.get("speed"));
    }

    @Test
    public void addAttributeCustomType() {
        String attrName = "person";
        this.redirectAttributes.addAttribute(attrName, new TestBean("Fred"));
        Assert.assertEquals("ConversionService should have invoked toString()", "Fred", this.redirectAttributes.get(attrName));
        this.conversionService.addConverter(new RedirectAttributesModelMapTests.TestBeanConverter());
        this.redirectAttributes.addAttribute(attrName, new TestBean("Fred"));
        Assert.assertEquals("Type converter should have been used", "[Fred]", this.redirectAttributes.get(attrName));
    }

    @Test
    public void addAttributeToString() {
        String attrName = "person";
        RedirectAttributesModelMap model = new RedirectAttributesModelMap();
        model.addAttribute(attrName, new TestBean("Fred"));
        Assert.assertEquals("toString() should have been used", "Fred", model.get(attrName));
    }

    @Test
    public void addAttributeValue() {
        this.redirectAttributes.addAttribute(new TestBean("Fred"));
        Assert.assertEquals("Fred", this.redirectAttributes.get("testBean"));
    }

    @Test
    public void addAllAttributesList() {
        this.redirectAttributes.addAllAttributes(Arrays.asList(new TestBean("Fred"), new Integer(5)));
        Assert.assertEquals("Fred", this.redirectAttributes.get("testBean"));
        Assert.assertEquals("5", this.redirectAttributes.get("integer"));
    }

    @Test
    public void addAttributesMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("person", new TestBean("Fred"));
        map.put("age", 33);
        this.redirectAttributes.addAllAttributes(map);
        Assert.assertEquals("Fred", this.redirectAttributes.get("person"));
        Assert.assertEquals("33", this.redirectAttributes.get("age"));
    }

    @Test
    public void mergeAttributes() {
        Map<String, Object> map = new HashMap<>();
        map.put("person", new TestBean("Fred"));
        map.put("age", 33);
        this.redirectAttributes.addAttribute("person", new TestBean("Ralph"));
        this.redirectAttributes.mergeAttributes(map);
        Assert.assertEquals("Ralph", this.redirectAttributes.get("person"));
        Assert.assertEquals("33", this.redirectAttributes.get("age"));
    }

    @Test
    public void put() {
        this.redirectAttributes.put("testBean", new TestBean("Fred"));
        Assert.assertEquals("Fred", this.redirectAttributes.get("testBean"));
    }

    @Test
    public void putAll() {
        Map<String, Object> map = new HashMap<>();
        map.put("person", new TestBean("Fred"));
        map.put("age", 33);
        this.redirectAttributes.putAll(map);
        Assert.assertEquals("Fred", this.redirectAttributes.get("person"));
        Assert.assertEquals("33", this.redirectAttributes.get("age"));
    }

    public static class TestBeanConverter implements Converter<TestBean, String> {
        @Override
        public String convert(TestBean source) {
            return ("[" + (source.getName())) + "]";
        }
    }
}

