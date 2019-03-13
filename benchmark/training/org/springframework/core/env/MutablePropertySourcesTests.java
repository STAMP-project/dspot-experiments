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
package org.springframework.core.env;


import java.util.Iterator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.env.MockPropertySource;


/**
 *
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 */
public class MutablePropertySourcesTests {
    @Test
    public void test() {
        MutablePropertySources sources = new MutablePropertySources();
        sources.addLast(new MockPropertySource("b").withProperty("p1", "bValue"));
        sources.addLast(new MockPropertySource("d").withProperty("p1", "dValue"));
        sources.addLast(new MockPropertySource("f").withProperty("p1", "fValue"));
        Assert.assertThat(sources.size(), CoreMatchers.equalTo(3));
        Assert.assertThat(sources.contains("a"), CoreMatchers.is(false));
        Assert.assertThat(sources.contains("b"), CoreMatchers.is(true));
        Assert.assertThat(sources.contains("c"), CoreMatchers.is(false));
        Assert.assertThat(sources.contains("d"), CoreMatchers.is(true));
        Assert.assertThat(sources.contains("e"), CoreMatchers.is(false));
        Assert.assertThat(sources.contains("f"), CoreMatchers.is(true));
        Assert.assertThat(sources.contains("g"), CoreMatchers.is(false));
        Assert.assertThat(sources.get("b"), CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(sources.get("b").getProperty("p1"), CoreMatchers.equalTo("bValue"));
        Assert.assertThat(sources.get("d"), CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(sources.get("d").getProperty("p1"), CoreMatchers.equalTo("dValue"));
        sources.addBefore("b", new MockPropertySource("a"));
        sources.addAfter("b", new MockPropertySource("c"));
        Assert.assertThat(sources.size(), CoreMatchers.equalTo(5));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("a")), CoreMatchers.is(0));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("b")), CoreMatchers.is(1));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("c")), CoreMatchers.is(2));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("d")), CoreMatchers.is(3));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("f")), CoreMatchers.is(4));
        sources.addBefore("f", new MockPropertySource("e"));
        sources.addAfter("f", new MockPropertySource("g"));
        Assert.assertThat(sources.size(), CoreMatchers.equalTo(7));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("a")), CoreMatchers.is(0));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("b")), CoreMatchers.is(1));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("c")), CoreMatchers.is(2));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("d")), CoreMatchers.is(3));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("e")), CoreMatchers.is(4));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("f")), CoreMatchers.is(5));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("g")), CoreMatchers.is(6));
        sources.addLast(new MockPropertySource("a"));
        Assert.assertThat(sources.size(), CoreMatchers.equalTo(7));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("b")), CoreMatchers.is(0));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("c")), CoreMatchers.is(1));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("d")), CoreMatchers.is(2));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("e")), CoreMatchers.is(3));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("f")), CoreMatchers.is(4));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("g")), CoreMatchers.is(5));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("a")), CoreMatchers.is(6));
        sources.addFirst(new MockPropertySource("a"));
        Assert.assertThat(sources.size(), CoreMatchers.equalTo(7));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("a")), CoreMatchers.is(0));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("b")), CoreMatchers.is(1));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("c")), CoreMatchers.is(2));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("d")), CoreMatchers.is(3));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("e")), CoreMatchers.is(4));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("f")), CoreMatchers.is(5));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("g")), CoreMatchers.is(6));
        Assert.assertEquals(sources.remove("a"), PropertySource.named("a"));
        Assert.assertThat(sources.size(), CoreMatchers.equalTo(6));
        Assert.assertThat(sources.contains("a"), CoreMatchers.is(false));
        Assert.assertNull(sources.remove("a"));
        Assert.assertThat(sources.size(), CoreMatchers.equalTo(6));
        String bogusPS = "bogus";
        try {
            sources.addAfter(bogusPS, new MockPropertySource("h"));
            Assert.fail("expected non-existent PropertySource exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("does not exist"));
        }
        sources.addFirst(new MockPropertySource("a"));
        Assert.assertThat(sources.size(), CoreMatchers.equalTo(7));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("a")), CoreMatchers.is(0));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("b")), CoreMatchers.is(1));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("c")), CoreMatchers.is(2));
        sources.replace("a", new MockPropertySource("a-replaced"));
        Assert.assertThat(sources.size(), CoreMatchers.equalTo(7));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("a-replaced")), CoreMatchers.is(0));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("b")), CoreMatchers.is(1));
        Assert.assertThat(sources.precedenceOf(PropertySource.named("c")), CoreMatchers.is(2));
        sources.replace("a-replaced", new MockPropertySource("a"));
        try {
            sources.replace(bogusPS, new MockPropertySource("bogus-replaced"));
            Assert.fail("expected non-existent PropertySource exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("does not exist"));
        }
        try {
            sources.addBefore("b", new MockPropertySource("b"));
            Assert.fail("expected exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("cannot be added relative to itself"));
        }
        try {
            sources.addAfter("b", new MockPropertySource("b"));
            Assert.fail("expected exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("cannot be added relative to itself"));
        }
    }

    @Test
    public void getNonExistentPropertySourceReturnsNull() {
        MutablePropertySources sources = new MutablePropertySources();
        Assert.assertThat(sources.get("bogus"), CoreMatchers.nullValue());
    }

    @Test
    public void iteratorContainsPropertySource() {
        MutablePropertySources sources = new MutablePropertySources();
        sources.addLast(new MockPropertySource("test"));
        Iterator<PropertySource<?>> it = sources.iterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("test", it.next().getName());
        try {
            it.remove();
            Assert.fail("Should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void iteratorIsEmptyForEmptySources() {
        MutablePropertySources sources = new MutablePropertySources();
        Iterator<PropertySource<?>> it = sources.iterator();
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void streamContainsPropertySource() {
        MutablePropertySources sources = new MutablePropertySources();
        sources.addLast(new MockPropertySource("test"));
        Assert.assertThat(sources.stream(), CoreMatchers.notNullValue());
        Assert.assertThat(sources.stream().count(), CoreMatchers.is(1L));
        Assert.assertThat(sources.stream().anyMatch(( source) -> "test".equals(source.getName())), CoreMatchers.is(true));
        Assert.assertThat(sources.stream().anyMatch(( source) -> "bogus".equals(source.getName())), CoreMatchers.is(false));
    }

    @Test
    public void streamIsEmptyForEmptySources() {
        MutablePropertySources sources = new MutablePropertySources();
        Assert.assertThat(sources.stream(), CoreMatchers.notNullValue());
        Assert.assertThat(sources.stream().count(), CoreMatchers.is(0L));
    }
}

