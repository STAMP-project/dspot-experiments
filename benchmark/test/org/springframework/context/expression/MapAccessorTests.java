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
package org.springframework.context.expression;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelCompiler;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;


/**
 * Unit tests for compilation of {@link MapAccessor}.
 *
 * @author Andy Clement
 */
public class MapAccessorTests {
    @Test
    public void mapAccessorCompilable() {
        Map<String, Object> testMap = getSimpleTestMap();
        StandardEvaluationContext sec = new StandardEvaluationContext();
        sec.addPropertyAccessor(new MapAccessor());
        SpelExpressionParser sep = new SpelExpressionParser();
        // basic
        Expression ex = sep.parseExpression("foo");
        Assert.assertEquals("bar", ex.getValue(sec, testMap));
        Assert.assertTrue(SpelCompiler.compile(ex));
        Assert.assertEquals("bar", ex.getValue(sec, testMap));
        // compound expression
        ex = sep.parseExpression("foo.toUpperCase()");
        Assert.assertEquals("BAR", ex.getValue(sec, testMap));
        Assert.assertTrue(SpelCompiler.compile(ex));
        Assert.assertEquals("BAR", ex.getValue(sec, testMap));
        // nested map
        Map<String, Map<String, Object>> nestedMap = getNestedTestMap();
        ex = sep.parseExpression("aaa.foo.toUpperCase()");
        Assert.assertEquals("BAR", ex.getValue(sec, nestedMap));
        Assert.assertTrue(SpelCompiler.compile(ex));
        Assert.assertEquals("BAR", ex.getValue(sec, nestedMap));
        // avoiding inserting checkcast because first part of expression returns a Map
        ex = sep.parseExpression("getMap().foo");
        MapAccessorTests.MapGetter mapGetter = new MapAccessorTests.MapGetter();
        Assert.assertEquals("bar", ex.getValue(sec, mapGetter));
        Assert.assertTrue(SpelCompiler.compile(ex));
        Assert.assertEquals("bar", ex.getValue(sec, mapGetter));
    }

    public static class MapGetter {
        Map<String, Object> map = new HashMap<>();

        public MapGetter() {
            map.put("foo", "bar");
        }

        @SuppressWarnings("rawtypes")
        public Map getMap() {
            return map;
        }
    }
}

