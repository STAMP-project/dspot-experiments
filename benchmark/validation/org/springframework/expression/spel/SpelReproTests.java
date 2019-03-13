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
package org.springframework.expression.spel;


import SpelMessage.EXCEPTION_DURING_BEAN_RESOLUTION;
import SpelMessage.INVALID_BEAN_REFERENCE;
import SpelMessage.NO_BEAN_RESOLVER_REGISTERED;
import SpelMessage.SETVALUE_NOT_SUPPORTED;
import SpelMessage.TYPE_CONVERSION_ERROR;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.ParserContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectiveMethodResolver;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.expression.spel.testresources.le.div.mod.reserved.Reserver;
import org.springframework.util.ObjectUtils;

import static SpelCompilerMode.IMMEDIATE;


/**
 * Reproduction tests cornering various reported SpEL issues.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @author Clark Duplichien
 * @author Phillip Webb
 * @author Sam Brannen
 */
public class SpelReproTests extends AbstractExpressionTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void NPE_SPR5661() {
        evaluate("joinThreeStrings('a',null,'c')", "anullc", String.class);
    }

    @Test
    public void SWF1086() {
        evaluate("printDouble(T(java.math.BigDecimal).valueOf(14.35))", "14.35", String.class);
    }

    @Test
    public void doubleCoercion() {
        evaluate("printDouble(14.35)", "14.35", String.class);
    }

    @Test
    public void doubleArrayCoercion() {
        evaluate("printDoubles(getDoublesAsStringList())", "{14.35, 15.45}", String.class);
    }

    @Test
    public void SPR5899() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.Spr5899Class());
        Expression expr = new SpelExpressionParser().parseRaw("tryToInvokeWithNull(12)");
        Assert.assertEquals(12, expr.getValue(context));
        expr = new SpelExpressionParser().parseRaw("tryToInvokeWithNull(null)");
        Assert.assertEquals(null, expr.getValue(context));
        try {
            expr = new SpelExpressionParser().parseRaw("tryToInvokeWithNull2(null)");
            expr.getValue();
            Assert.fail("Should have failed to find a method to which it could pass null");
        } catch (EvaluationException see) {
            // success
        }
        context.setTypeLocator(new SpelReproTests.MyTypeLocator());
        // varargs
        expr = new SpelExpressionParser().parseRaw("tryToInvokeWithNull3(null,'a','b')");
        Assert.assertEquals("ab", expr.getValue(context));
        // varargs 2 - null is packed into the varargs
        expr = new SpelExpressionParser().parseRaw("tryToInvokeWithNull3(12,'a',null,'c')");
        Assert.assertEquals("anullc", expr.getValue(context));
        // check we can find the ctor ok
        expr = new SpelExpressionParser().parseRaw("new Spr5899Class().toString()");
        Assert.assertEquals("instance", expr.getValue(context));
        expr = new SpelExpressionParser().parseRaw("new Spr5899Class(null).toString()");
        Assert.assertEquals("instance", expr.getValue(context));
        // ctor varargs
        expr = new SpelExpressionParser().parseRaw("new Spr5899Class(null,'a','b').toString()");
        Assert.assertEquals("instance", expr.getValue(context));
        // ctor varargs 2
        expr = new SpelExpressionParser().parseRaw("new Spr5899Class(null,'a', null, 'b').toString()");
        Assert.assertEquals("instance", expr.getValue(context));
    }

    @Test
    public void SPR5905_InnerTypeReferences() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.Spr5899Class());
        Expression expr = new SpelExpressionParser().parseRaw("T(java.util.Map$Entry)");
        Assert.assertEquals(Map.Entry.class, expr.getValue(context));
        expr = new SpelExpressionParser().parseRaw("T(org.springframework.expression.spel.SpelReproTests$Outer$Inner).run()");
        Assert.assertEquals(12, expr.getValue(context));
        expr = new SpelExpressionParser().parseRaw("new org.springframework.expression.spel.SpelReproTests$Outer$Inner().run2()");
        Assert.assertEquals(13, expr.getValue(context));
    }

    @Test
    public void SPR5804() {
        Map<String, String> m = new HashMap<>();
        m.put("foo", "bar");
        StandardEvaluationContext context = new StandardEvaluationContext(m);// root is a map instance

        context.addPropertyAccessor(new SpelReproTests.MapAccessor());
        Expression expr = new SpelExpressionParser().parseRaw("['foo']");
        Assert.assertEquals("bar", expr.getValue(context));
    }

    @Test
    public void SPR5847() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.TestProperties());
        String name = null;
        Expression expr = null;
        expr = new SpelExpressionParser().parseRaw("jdbcProperties['username']");
        name = expr.getValue(context, String.class);
        Assert.assertEquals("Dave", name);
        expr = new SpelExpressionParser().parseRaw("jdbcProperties[username]");
        name = expr.getValue(context, String.class);
        Assert.assertEquals("Dave", name);
        // MapAccessor required for this to work
        expr = new SpelExpressionParser().parseRaw("jdbcProperties.username");
        context.addPropertyAccessor(new SpelReproTests.MapAccessor());
        name = expr.getValue(context, String.class);
        Assert.assertEquals("Dave", name);
        // --- dotted property names
        // lookup foo on the root, then bar on that, then use that as the key into
        // jdbcProperties
        expr = new SpelExpressionParser().parseRaw("jdbcProperties[foo.bar]");
        context.addPropertyAccessor(new SpelReproTests.MapAccessor());
        name = expr.getValue(context, String.class);
        Assert.assertEquals("Dave2", name);
        // key is foo.bar
        expr = new SpelExpressionParser().parseRaw("jdbcProperties['foo.bar']");
        context.addPropertyAccessor(new SpelReproTests.MapAccessor());
        name = expr.getValue(context, String.class);
        Assert.assertEquals("Elephant", name);
    }

    @Test
    public void NPE_SPR5673() {
        ParserContext hashes = TemplateExpressionParsingTests.HASH_DELIMITED_PARSER_CONTEXT;
        ParserContext dollars = TemplateExpressionParsingTests.DEFAULT_TEMPLATE_PARSER_CONTEXT;
        checkTemplateParsing("abc${'def'} ghi", "abcdef ghi");
        checkTemplateParsingError("abc${ {}( 'abc'", "Missing closing ')' for '(' at position 8");
        checkTemplateParsingError("abc${ {}[ 'abc'", "Missing closing ']' for '[' at position 8");
        checkTemplateParsingError("abc${ {}{ 'abc'", "Missing closing '}' for '{' at position 8");
        checkTemplateParsingError("abc${ ( 'abc' }", "Found closing '}' at position 14 but most recent opening is '(' at position 6");
        checkTemplateParsingError("abc${ '... }", "Found non terminating string literal starting at position 6");
        checkTemplateParsingError("abc${ \"... }", "Found non terminating string literal starting at position 6");
        checkTemplateParsingError("abc${ ) }", "Found closing ')' at position 6 without an opening '('");
        checkTemplateParsingError("abc${ ] }", "Found closing ']' at position 6 without an opening '['");
        checkTemplateParsingError("abc${ } }", "No expression defined within delimiter '${}' at character 3");
        checkTemplateParsingError("abc$[ } ]", SpelReproTests.DOLLARSQUARE_TEMPLATE_PARSER_CONTEXT, "Found closing '}' at position 6 without an opening '{'");
        checkTemplateParsing("abc ${\"def\'\'g}hi\"} jkl", "abc def'g}hi jkl");
        checkTemplateParsing("abc ${'def''g}hi'} jkl", "abc def'g}hi jkl");
        checkTemplateParsing("}", "}");
        checkTemplateParsing("${'hello'} world", "hello world");
        checkTemplateParsing("Hello ${'}'}]", "Hello }]");
        checkTemplateParsing("Hello ${'}'}", "Hello }");
        checkTemplateParsingError("Hello ${ ( ", "No ending suffix '}' for expression starting at character 6: ${ ( ");
        checkTemplateParsingError("Hello ${ ( }", "Found closing '}' at position 11 but most recent opening is '(' at position 9");
        checkTemplateParsing("#{'Unable to render embedded object: File ({#this == 2}'}", hashes, "Unable to render embedded object: File ({#this == 2}");
        checkTemplateParsing("This is the last odd number in the list: ${listOfNumbersUpToTen.$[#this%2==1]}", dollars, "This is the last odd number in the list: 9");
        checkTemplateParsing("Hello ${'here is a curly bracket }'}", dollars, "Hello here is a curly bracket }");
        checkTemplateParsing("He${'${'}llo ${'here is a curly bracket }'}}", dollars, "He${llo here is a curly bracket }}");
        checkTemplateParsing("Hello ${'()()()}{}{}{][]{}{][}[][][}{()()'} World", dollars, "Hello ()()()}{}{}{][]{}{][}[][][}{()() World");
        checkTemplateParsing("Hello ${'inner literal that''s got {[(])]}an escaped quote in it'} World", "Hello inner literal that's got {[(])]}an escaped quote in it World");
        checkTemplateParsingError("Hello ${", "No ending suffix '}' for expression starting at character 6: ${");
    }

    @Test
    public void propertyAccessOnNullTarget_SPR5663() throws AccessException {
        PropertyAccessor accessor = new ReflectivePropertyAccessor();
        EvaluationContext context = TestScenarioCreator.getTestEvaluationContext();
        Assert.assertFalse(accessor.canRead(context, null, "abc"));
        Assert.assertFalse(accessor.canWrite(context, null, "abc"));
        try {
            accessor.read(context, null, "abc");
            Assert.fail("Should have failed with an IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
        try {
            accessor.write(context, null, "abc", "foo");
            Assert.fail("Should have failed with an IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void nestedProperties_SPR6923() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.Foo());
        Expression expr = new SpelExpressionParser().parseRaw("resource.resource.server");
        String name = expr.getValue(context, String.class);
        Assert.assertEquals("abc", name);
    }

    /**
     * Should be accessing Goo.getKey because 'bar' field evaluates to "key"
     */
    @Test
    public void indexingAsAPropertyAccess_SPR6968_1() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.Goo());
        String name = null;
        Expression expr = null;
        expr = new SpelExpressionParser().parseRaw("instance[bar]");
        name = expr.getValue(context, String.class);
        Assert.assertEquals("hello", name);
        name = expr.getValue(context, String.class);// will be using the cached accessor this time

        Assert.assertEquals("hello", name);
    }

    /**
     * Should be accessing Goo.getKey because 'bar' variable evaluates to "key"
     */
    @Test
    public void indexingAsAPropertyAccess_SPR6968_2() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.Goo());
        context.setVariable("bar", "key");
        String name = null;
        Expression expr = null;
        expr = new SpelExpressionParser().parseRaw("instance[#bar]");
        name = expr.getValue(context, String.class);
        Assert.assertEquals("hello", name);
        name = expr.getValue(context, String.class);// will be using the cached accessor this time

        Assert.assertEquals("hello", name);
    }

    /**
     * $ related identifiers
     */
    @Test
    public void dollarPrefixedIdentifier_SPR7100() {
        SpelReproTests.Holder h = new SpelReproTests.Holder();
        StandardEvaluationContext context = new StandardEvaluationContext(h);
        context.addPropertyAccessor(new SpelReproTests.MapAccessor());
        h.map.put("$foo", "wibble");
        h.map.put("foo$bar", "wobble");
        h.map.put("foobar$$", "wabble");
        h.map.put("$", "wubble");
        h.map.put("$$", "webble");
        h.map.put("$_$", "tribble");
        String name = null;
        Expression expr = null;
        expr = new SpelExpressionParser().parseRaw("map.$foo");
        name = expr.getValue(context, String.class);
        Assert.assertEquals("wibble", name);
        expr = new SpelExpressionParser().parseRaw("map.foo$bar");
        name = expr.getValue(context, String.class);
        Assert.assertEquals("wobble", name);
        expr = new SpelExpressionParser().parseRaw("map.foobar$$");
        name = expr.getValue(context, String.class);
        Assert.assertEquals("wabble", name);
        expr = new SpelExpressionParser().parseRaw("map.$");
        name = expr.getValue(context, String.class);
        Assert.assertEquals("wubble", name);
        expr = new SpelExpressionParser().parseRaw("map.$$");
        name = expr.getValue(context, String.class);
        Assert.assertEquals("webble", name);
        expr = new SpelExpressionParser().parseRaw("map.$_$");
        name = expr.getValue(context, String.class);
        Assert.assertEquals("tribble", name);
    }

    /**
     * Should be accessing Goo.wibble field because 'bar' variable evaluates to "wibble"
     */
    @Test
    public void indexingAsAPropertyAccess_SPR6968_3() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.Goo());
        context.setVariable("bar", "wibble");
        String name = null;
        Expression expr = null;
        expr = new SpelExpressionParser().parseRaw("instance[#bar]");
        // will access the field 'wibble' and not use a getter
        name = expr.getValue(context, String.class);
        Assert.assertEquals("wobble", name);
        name = expr.getValue(context, String.class);// will be using the cached accessor this time

        Assert.assertEquals("wobble", name);
    }

    /**
     * Should be accessing (setting) Goo.wibble field because 'bar' variable evaluates to
     * "wibble"
     */
    @Test
    public void indexingAsAPropertyAccess_SPR6968_4() {
        SpelReproTests.Goo g = SpelReproTests.Goo.instance;
        StandardEvaluationContext context = new StandardEvaluationContext(g);
        context.setVariable("bar", "wibble");
        Expression expr = null;
        expr = new SpelExpressionParser().parseRaw("instance[#bar]='world'");
        // will access the field 'wibble' and not use a getter
        expr.getValue(context, String.class);
        Assert.assertEquals("world", g.wibble);
        expr.getValue(context, String.class);// will be using the cached accessor this time

        Assert.assertEquals("world", g.wibble);
    }

    /**
     * Should be accessing Goo.setKey field because 'bar' variable evaluates to "key"
     */
    @Test
    public void indexingAsAPropertyAccess_SPR6968_5() {
        SpelReproTests.Goo g = SpelReproTests.Goo.instance;
        StandardEvaluationContext context = new StandardEvaluationContext(g);
        Expression expr = null;
        expr = new SpelExpressionParser().parseRaw("instance[bar]='world'");
        expr.getValue(context, String.class);
        Assert.assertEquals("world", g.value);
        expr.getValue(context, String.class);// will be using the cached accessor this time

        Assert.assertEquals("world", g.value);
    }

    @Test
    public void dollars() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.XX());
        Expression expr = null;
        expr = new SpelExpressionParser().parseRaw("m['$foo']");
        context.setVariable("file_name", "$foo");
        Assert.assertEquals("wibble", expr.getValue(context, String.class));
    }

    @Test
    public void dollars2() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.XX());
        Expression expr = null;
        expr = new SpelExpressionParser().parseRaw("m[$foo]");
        context.setVariable("file_name", "$foo");
        Assert.assertEquals("wibble", expr.getValue(context, String.class));
    }

    private static final ParserContext DOLLARSQUARE_TEMPLATE_PARSER_CONTEXT = new ParserContext() {
        @Override
        public String getExpressionPrefix() {
            return "$[";
        }

        @Override
        public String getExpressionSuffix() {
            return "]";
        }

        @Override
        public boolean isTemplate() {
            return true;
        }
    };

    @Test
    public void beanResolution() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.XX());
        Expression expr = null;
        // no resolver registered == exception
        try {
            expr = new SpelExpressionParser().parseRaw("@foo");
            Assert.assertEquals("custard", expr.getValue(context, String.class));
        } catch (SpelEvaluationException see) {
            Assert.assertEquals(NO_BEAN_RESOLVER_REGISTERED, see.getMessageCode());
            Assert.assertEquals("foo", see.getInserts()[0]);
        }
        context.setBeanResolver(new SpelReproTests.MyBeanResolver());
        // bean exists
        expr = new SpelExpressionParser().parseRaw("@foo");
        Assert.assertEquals("custard", expr.getValue(context, String.class));
        // bean does not exist
        expr = new SpelExpressionParser().parseRaw("@bar");
        Assert.assertEquals(null, expr.getValue(context, String.class));
        // bean name will cause AccessException
        expr = new SpelExpressionParser().parseRaw("@goo");
        try {
            Assert.assertEquals(null, expr.getValue(context, String.class));
        } catch (SpelEvaluationException see) {
            Assert.assertEquals(EXCEPTION_DURING_BEAN_RESOLUTION, see.getMessageCode());
            Assert.assertEquals("goo", see.getInserts()[0]);
            Assert.assertTrue(((see.getCause()) instanceof AccessException));
            Assert.assertTrue(see.getCause().getMessage().startsWith("DONT"));
        }
        // bean exists
        expr = new SpelExpressionParser().parseRaw("@'foo.bar'");
        Assert.assertEquals("trouble", expr.getValue(context, String.class));
        // bean exists
        try {
            expr = new SpelExpressionParser().parseRaw("@378");
            Assert.assertEquals("trouble", expr.getValue(context, String.class));
        } catch (SpelParseException spe) {
            Assert.assertEquals(INVALID_BEAN_REFERENCE, spe.getMessageCode());
        }
    }

    @Test
    public void elvis_SPR7209_1() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.XX());
        Expression expr = null;
        // Different parts of elvis expression are null
        expr = new SpelExpressionParser().parseRaw("(?:'default')");
        Assert.assertEquals("default", expr.getValue());
        expr = new SpelExpressionParser().parseRaw("?:'default'");
        Assert.assertEquals("default", expr.getValue());
        expr = new SpelExpressionParser().parseRaw("?:");
        Assert.assertEquals(null, expr.getValue());
        // Different parts of ternary expression are null
        try {
            expr = new SpelExpressionParser().parseRaw("(?'abc':'default')");
            expr.getValue(context);
            Assert.fail();
        } catch (SpelEvaluationException see) {
            Assert.assertEquals(TYPE_CONVERSION_ERROR, see.getMessageCode());
        }
        expr = new SpelExpressionParser().parseRaw("(false?'abc':null)");
        Assert.assertEquals(null, expr.getValue());
        // Assignment
        try {
            expr = new SpelExpressionParser().parseRaw("(='default')");
            expr.getValue(context);
            Assert.fail();
        } catch (SpelEvaluationException see) {
            Assert.assertEquals(SETVALUE_NOT_SUPPORTED, see.getMessageCode());
        }
    }

    @Test
    public void elvis_SPR7209_2() {
        Expression expr = null;
        // Have empty string treated as null for elvis
        expr = new SpelExpressionParser().parseRaw("?:'default'");
        Assert.assertEquals("default", expr.getValue());
        expr = new SpelExpressionParser().parseRaw("\"\"?:\'default\'");
        Assert.assertEquals("default", expr.getValue());
        expr = new SpelExpressionParser().parseRaw("''?:'default'");
        Assert.assertEquals("default", expr.getValue());
    }

    @Test
    public void mapOfMap_SPR7244() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("uri", "http:");
        Map<String, String> nameMap = new LinkedHashMap<>();
        nameMap.put("givenName", "Arthur");
        map.put("value", nameMap);
        StandardEvaluationContext context = new StandardEvaluationContext(map);
        ExpressionParser parser = new SpelExpressionParser();
        String el1 = "#root['value'].get('givenName')";
        Expression exp = parser.parseExpression(el1);
        Object evaluated = exp.getValue(context);
        Assert.assertEquals("Arthur", evaluated);
        String el2 = "#root['value']['givenName']";
        exp = parser.parseExpression(el2);
        evaluated = exp.getValue(context);
        Assert.assertEquals("Arthur", evaluated);
    }

    @Test
    public void projectionTypeDescriptors_1() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.C());
        SpelExpressionParser parser = new SpelExpressionParser();
        String el1 = "ls.![#this.equals('abc')]";
        SpelExpression exp = parser.parseRaw(el1);
        List<?> value = ((List<?>) (exp.getValue(context)));
        // value is list containing [true,false]
        Assert.assertEquals(Boolean.class, value.get(0).getClass());
        TypeDescriptor evaluated = exp.getValueTypeDescriptor(context);
        Assert.assertEquals(null, evaluated.getElementTypeDescriptor());
    }

    @Test
    public void projectionTypeDescriptors_2() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.C());
        SpelExpressionParser parser = new SpelExpressionParser();
        String el1 = "as.![#this.equals('abc')]";
        SpelExpression exp = parser.parseRaw(el1);
        Object[] value = ((Object[]) (exp.getValue(context)));
        // value is array containing [true,false]
        Assert.assertEquals(Boolean.class, value[0].getClass());
        TypeDescriptor evaluated = exp.getValueTypeDescriptor(context);
        Assert.assertEquals(Boolean.class, evaluated.getElementTypeDescriptor().getType());
    }

    @Test
    public void projectionTypeDescriptors_3() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.C());
        SpelExpressionParser parser = new SpelExpressionParser();
        String el1 = "ms.![key.equals('abc')]";
        SpelExpression exp = parser.parseRaw(el1);
        List<?> value = ((List<?>) (exp.getValue(context)));
        // value is list containing [true,false]
        Assert.assertEquals(Boolean.class, value.get(0).getClass());
        TypeDescriptor evaluated = exp.getValueTypeDescriptor(context);
        Assert.assertEquals(null, evaluated.getElementTypeDescriptor());
    }

    @Test
    public void greaterThanWithNulls_SPR7840() {
        List<SpelReproTests.D> list = new ArrayList<>();
        list.add(new SpelReproTests.D("aaa"));
        list.add(new SpelReproTests.D("bbb"));
        list.add(new SpelReproTests.D(null));
        list.add(new SpelReproTests.D("ccc"));
        list.add(new SpelReproTests.D(null));
        list.add(new SpelReproTests.D("zzz"));
        StandardEvaluationContext context = new StandardEvaluationContext(list);
        SpelExpressionParser parser = new SpelExpressionParser();
        String el1 = "#root.?[a < 'hhh']";
        SpelExpression exp = parser.parseRaw(el1);
        Object value = exp.getValue(context);
        Assert.assertEquals("[D(aaa), D(bbb), D(null), D(ccc), D(null)]", value.toString());
        String el2 = "#root.?[a > 'hhh']";
        SpelExpression exp2 = parser.parseRaw(el2);
        Object value2 = exp2.getValue(context);
        Assert.assertEquals("[D(zzz)]", value2.toString());
        // trim out the nulls first
        String el3 = "#root.?[a!=null].?[a < 'hhh']";
        SpelExpression exp3 = parser.parseRaw(el3);
        Object value3 = exp3.getValue(context);
        Assert.assertEquals("[D(aaa), D(bbb), D(ccc)]", value3.toString());
    }

    /**
     * Test whether {@link ReflectiveMethodResolver} follows Java Method Invocation
     * Conversion order. And more precisely that widening reference conversion is 'higher'
     * than a unboxing conversion.
     */
    @Test
    public void conversionPriority_SPR8224() throws Exception {
        @SuppressWarnings("unused")
        class ConversionPriority1 {
            public int getX(Number i) {
                return 20;
            }

            public int getX(int i) {
                return 10;
            }
        }
        @SuppressWarnings("unused")
        class ConversionPriority2 {
            public int getX(int i) {
                return 10;
            }

            public int getX(Number i) {
                return 20;
            }
        }
        final Integer INTEGER = Integer.valueOf(7);
        EvaluationContext emptyEvalContext = new StandardEvaluationContext();
        List<TypeDescriptor> args = new ArrayList<>();
        args.add(TypeDescriptor.forObject(new Integer(42)));
        ConversionPriority1 target = new ConversionPriority1();
        MethodExecutor me = new ReflectiveMethodResolver(true).resolve(emptyEvalContext, target, "getX", args);
        // MethodInvoker chooses getX(int i) when passing Integer
        final int actual = ((Integer) (me.execute(emptyEvalContext, target, new Integer(42)).getValue()));
        // Compiler chooses getX(Number i) when passing Integer
        final int compiler = target.getX(INTEGER);
        // Fails!
        Assert.assertEquals(compiler, actual);
        ConversionPriority2 target2 = new ConversionPriority2();
        MethodExecutor me2 = new ReflectiveMethodResolver(true).resolve(emptyEvalContext, target2, "getX", args);
        // MethodInvoker chooses getX(int i) when passing Integer
        int actual2 = ((Integer) (me2.execute(emptyEvalContext, target2, new Integer(42)).getValue()));
        // Compiler chooses getX(Number i) when passing Integer
        int compiler2 = target2.getX(INTEGER);
        // Fails!
        Assert.assertEquals(compiler2, actual2);
    }

    /**
     * Test whether {@link ReflectiveMethodResolver} handles Widening Primitive Conversion. That's passing an 'int' to a
     * method accepting 'long' is ok.
     */
    @Test
    public void wideningPrimitiveConversion_SPR8224() throws Exception {
        class WideningPrimitiveConversion {
            public int getX(long i) {
                return 10;
            }
        }
        final Integer INTEGER_VALUE = Integer.valueOf(7);
        WideningPrimitiveConversion target = new WideningPrimitiveConversion();
        EvaluationContext emptyEvalContext = new StandardEvaluationContext();
        List<TypeDescriptor> args = new ArrayList<>();
        args.add(TypeDescriptor.forObject(INTEGER_VALUE));
        MethodExecutor me = new ReflectiveMethodResolver(true).resolve(emptyEvalContext, target, "getX", args);
        final int actual = ((Integer) (me.execute(emptyEvalContext, target, INTEGER_VALUE).getValue()));
        final int compiler = target.getX(INTEGER_VALUE);
        Assert.assertEquals(compiler, actual);
    }

    @Test
    public void varargsAgainstProxy_SPR16122() {
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expr = parser.parseExpression("process('a', 'b')");
        SpelReproTests.VarargsReceiver receiver = new SpelReproTests.VarargsReceiver();
        SpelReproTests.VarargsInterface proxy = ((SpelReproTests.VarargsInterface) (Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ SpelReproTests.VarargsInterface.class }, ( proxy1, method, args) -> method.invoke(receiver, args))));
        Assert.assertEquals("OK", expr.getValue(new StandardEvaluationContext(receiver)));
        Assert.assertEquals("OK", expr.getValue(new StandardEvaluationContext(proxy)));
    }

    @Test
    public void testCompiledExpressionForProxy_SPR16191() {
        SpelExpressionParser expressionParser = new SpelExpressionParser(new SpelParserConfiguration(IMMEDIATE, null));
        Expression expression = expressionParser.parseExpression("#target.process(#root)");
        SpelReproTests.VarargsReceiver receiver = new SpelReproTests.VarargsReceiver();
        SpelReproTests.VarargsInterface proxy = ((SpelReproTests.VarargsInterface) (Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ SpelReproTests.VarargsInterface.class }, ( proxy1, method, args) -> method.invoke(receiver, args))));
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariable("target", proxy);
        String result = expression.getValue(evaluationContext, "foo", String.class);
        result = expression.getValue(evaluationContext, "foo", String.class);
        Assert.assertEquals("OK", result);
    }

    @Test
    public void varargsAndPrimitives_SPR8174() throws Exception {
        EvaluationContext emptyEvalContext = new StandardEvaluationContext();
        List<TypeDescriptor> args = new ArrayList<>();
        args.add(TypeDescriptor.forObject(34L));
        SpelReproTests.ReflectionUtil<Integer> ru = new SpelReproTests.ReflectionUtil<>();
        MethodExecutor me = new ReflectiveMethodResolver().resolve(emptyEvalContext, ru, "methodToCall", args);
        args.set(0, TypeDescriptor.forObject(23));
        me = new ReflectiveMethodResolver().resolve(emptyEvalContext, ru, "foo", args);
        me.execute(emptyEvalContext, ru, 45);
        args.set(0, TypeDescriptor.forObject(23.0F));
        me = new ReflectiveMethodResolver().resolve(emptyEvalContext, ru, "foo", args);
        me.execute(emptyEvalContext, ru, 45.0F);
        args.set(0, TypeDescriptor.forObject(23.0));
        me = new ReflectiveMethodResolver().resolve(emptyEvalContext, ru, "foo", args);
        me.execute(emptyEvalContext, ru, 23.0);
        args.set(0, TypeDescriptor.forObject(((short) (23))));
        me = new ReflectiveMethodResolver().resolve(emptyEvalContext, ru, "foo", args);
        me.execute(emptyEvalContext, ru, ((short) (23)));
        args.set(0, TypeDescriptor.forObject(23L));
        me = new ReflectiveMethodResolver().resolve(emptyEvalContext, ru, "foo", args);
        me.execute(emptyEvalContext, ru, 23L);
        args.set(0, TypeDescriptor.forObject(((char) (65))));
        me = new ReflectiveMethodResolver().resolve(emptyEvalContext, ru, "foo", args);
        me.execute(emptyEvalContext, ru, ((char) (65)));
        args.set(0, TypeDescriptor.forObject(((byte) (23))));
        me = new ReflectiveMethodResolver().resolve(emptyEvalContext, ru, "foo", args);
        me.execute(emptyEvalContext, ru, ((byte) (23)));
        args.set(0, TypeDescriptor.forObject(true));
        me = new ReflectiveMethodResolver().resolve(emptyEvalContext, ru, "foo", args);
        me.execute(emptyEvalContext, ru, true);
        // trickier:
        args.set(0, TypeDescriptor.forObject(12));
        args.add(TypeDescriptor.forObject(23.0F));
        me = new ReflectiveMethodResolver().resolve(emptyEvalContext, ru, "bar", args);
        me.execute(emptyEvalContext, ru, 12, 23.0F);
    }

    @Test
    public void reservedWords_SPR8228() {
        // "DIV","EQ","GE","GT","LE","LT","MOD","NE","NOT"
        @SuppressWarnings("unused")
        class Reserver {
            public Reserver getReserver() {
                return this;
            }

            public String NE = "abc";

            public String ne = "def";

            public int DIV = 1;

            public int div = 3;

            public Map<String, String> m = new HashMap<>();

            Reserver() {
                m.put("NE", "xyz");
            }
        }
        StandardEvaluationContext context = new StandardEvaluationContext(new Reserver());
        SpelExpressionParser parser = new SpelExpressionParser();
        String ex = "getReserver().NE";
        SpelExpression exp = parser.parseRaw(ex);
        String value = ((String) (exp.getValue(context)));
        Assert.assertEquals("abc", value);
        ex = "getReserver().ne";
        exp = parser.parseRaw(ex);
        value = ((String) (exp.getValue(context)));
        Assert.assertEquals("def", value);
        ex = "getReserver().m[NE]";
        exp = parser.parseRaw(ex);
        value = ((String) (exp.getValue(context)));
        Assert.assertEquals("xyz", value);
        ex = "getReserver().DIV";
        exp = parser.parseRaw(ex);
        Assert.assertEquals(1, exp.getValue(context));
        ex = "getReserver().div";
        exp = parser.parseRaw(ex);
        Assert.assertEquals(3, exp.getValue(context));
        exp = parser.parseRaw("NE");
        Assert.assertEquals("abc", exp.getValue(context));
    }

    @Test
    public void reservedWordProperties_SPR9862() {
        StandardEvaluationContext context = new StandardEvaluationContext();
        SpelExpressionParser parser = new SpelExpressionParser();
        SpelExpression expression = parser.parseRaw("T(org.springframework.expression.spel.testresources.le.div.mod.reserved.Reserver).CONST");
        Object value = expression.getValue(context);
        Assert.assertEquals(value, Reserver.CONST);
    }

    /**
     * We add property accessors in the order:
     * First, Second, Third, Fourth.
     * They are not utilized in this order; preventing a priority or order of operations
     * in evaluation of SPEL expressions for a given context.
     */
    @Test
    public void propertyAccessorOrder_SPR8211() {
        ExpressionParser expressionParser = new SpelExpressionParser();
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(new SpelReproTests.ContextObject());
        evaluationContext.addPropertyAccessor(new SpelReproTests.TestPropertyAccessor("firstContext"));
        evaluationContext.addPropertyAccessor(new SpelReproTests.TestPropertyAccessor("secondContext"));
        evaluationContext.addPropertyAccessor(new SpelReproTests.TestPropertyAccessor("thirdContext"));
        evaluationContext.addPropertyAccessor(new SpelReproTests.TestPropertyAccessor("fourthContext"));
        Assert.assertEquals("first", expressionParser.parseExpression("shouldBeFirst").getValue(evaluationContext));
        Assert.assertEquals("second", expressionParser.parseExpression("shouldBeSecond").getValue(evaluationContext));
        Assert.assertEquals("third", expressionParser.parseExpression("shouldBeThird").getValue(evaluationContext));
        Assert.assertEquals("fourth", expressionParser.parseExpression("shouldBeFourth").getValue(evaluationContext));
    }

    /**
     * Test the ability to subclass the ReflectiveMethodResolver and change how it
     * determines the set of methods for a type.
     */
    @Test
    public void customStaticFunctions_SPR9038() {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        List<MethodResolver> methodResolvers = new ArrayList<>();
        methodResolvers.add(new ReflectiveMethodResolver() {
            @Override
            protected Method[] getMethods(Class<?> type) {
                try {
                    return new Method[]{ Integer.class.getDeclaredMethod("parseInt", String.class, Integer.TYPE) };
                } catch (NoSuchMethodException ex) {
                    return new Method[0];
                }
            }
        });
        context.setMethodResolvers(methodResolvers);
        Expression expression = parser.parseExpression("parseInt('-FF', 16)");
        Integer result = expression.getValue(context, "", Integer.class);
        Assert.assertEquals((-255), result.intValue());
    }

    @Test
    public void array() {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = null;
        Object result = null;
        expression = parser.parseExpression("new java.lang.Long[0].class");
        result = expression.getValue(context, "");
        Assert.assertEquals("Equal assertion failed: ", "class [Ljava.lang.Long;", result.toString());
        expression = parser.parseExpression("T(java.lang.Long[])");
        result = expression.getValue(context, "");
        Assert.assertEquals("Equal assertion failed: ", "class [Ljava.lang.Long;", result.toString());
        expression = parser.parseExpression("T(java.lang.String[][][])");
        result = expression.getValue(context, "");
        Assert.assertEquals("Equal assertion failed: ", "class [[[Ljava.lang.String;", result.toString());
        Assert.assertEquals("T(java.lang.String[][][])", toStringAST());
        expression = parser.parseExpression("new int[0].class");
        result = expression.getValue(context, "");
        Assert.assertEquals("Equal assertion failed: ", "class [I", result.toString());
        expression = parser.parseExpression("T(int[][])");
        result = expression.getValue(context, "");
        Assert.assertEquals("class [[I", result.toString());
    }

    @Test
    public void SPR9486_floatFunctionResolver() {
        Number expectedResult = Math.abs((-10.2F));
        ExpressionParser parser = new SpelExpressionParser();
        SpelReproTests.SPR9486_FunctionsClass testObject = new SpelReproTests.SPR9486_FunctionsClass();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("abs(-10.2f)");
        Number result = expression.getValue(context, testObject, Number.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void SPR9486_addFloatWithDouble() {
        Number expectedNumber = 10.21F + 10.2;
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.21f + 10.2");
        Number result = expression.getValue(context, null, Number.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_addFloatWithFloat() {
        Number expectedNumber = 10.21F + 10.2F;
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.21f + 10.2f");
        Number result = expression.getValue(context, null, Number.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_subtractFloatWithDouble() {
        Number expectedNumber = 10.21F - 10.2;
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.21f - 10.2");
        Number result = expression.getValue(context, null, Number.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_subtractFloatWithFloat() {
        Number expectedNumber = 10.21F - 10.2F;
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.21f - 10.2f");
        Number result = expression.getValue(context, null, Number.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_multiplyFloatWithDouble() {
        Number expectedNumber = 10.21F * 10.2;
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.21f * 10.2");
        Number result = expression.getValue(context, null, Number.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_multiplyFloatWithFloat() {
        Number expectedNumber = 10.21F * 10.2F;
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.21f * 10.2f");
        Number result = expression.getValue(context, null, Number.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_floatDivideByFloat() {
        Number expectedNumber = (-10.21F) / (-10.2F);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("-10.21f / -10.2f");
        Number result = expression.getValue(context, null, Number.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_floatDivideByDouble() {
        Number expectedNumber = (-10.21F) / (-10.2);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("-10.21f / -10.2");
        Number result = expression.getValue(context, null, Number.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_floatEqFloatUnaryMinus() {
        Boolean expectedResult = (-10.21F) == (-10.2F);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("-10.21f == -10.2f");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void SPR9486_floatEqDoubleUnaryMinus() {
        Boolean expectedResult = (-10.21F) == (-10.2);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("-10.21f == -10.2");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void SPR9486_floatEqFloat() {
        Boolean expectedResult = 10.215F == 10.2109F;
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.215f == 10.2109f");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void SPR9486_floatEqDouble() {
        Boolean expectedResult = 10.215F == 10.2109;
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.215f == 10.2109");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void SPR9486_floatNotEqFloat() {
        Boolean expectedResult = 10.215F != 10.2109F;
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.215f != 10.2109f");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void SPR9486_floatNotEqDouble() {
        Boolean expectedResult = 10.215F != 10.2109;
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.215f != 10.2109");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void SPR9486_floatLessThanFloat() {
        Boolean expectedNumber = (-10.21F) < (-10.2F);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("-10.21f < -10.2f");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_floatLessThanDouble() {
        Boolean expectedNumber = (-10.21F) < (-10.2);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("-10.21f < -10.2");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_floatLessThanOrEqualFloat() {
        Boolean expectedNumber = (-10.21F) <= (-10.22F);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("-10.21f <= -10.22f");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_floatLessThanOrEqualDouble() {
        Boolean expectedNumber = (-10.21F) <= (-10.2);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("-10.21f <= -10.2");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_floatGreaterThanFloat() {
        Boolean expectedNumber = (-10.21F) > (-10.2F);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("-10.21f > -10.2f");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_floatGreaterThanDouble() {
        Boolean expectedResult = (-10.21F) > (-10.2);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("-10.21f > -10.2");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void SPR9486_floatGreaterThanOrEqualFloat() {
        Boolean expectedNumber = (-10.21F) >= (-10.2F);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("-10.21f >= -10.2f");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedNumber, result);
    }

    @Test
    public void SPR9486_floatGreaterThanEqualDouble() {
        Boolean expectedResult = (-10.21F) >= (-10.2);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("-10.21f >= -10.2");
        Boolean result = expression.getValue(context, null, Boolean.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void SPR9486_floatModulusFloat() {
        Number expectedResult = 10.21F % 10.2F;
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.21f % 10.2f");
        Number result = expression.getValue(context, null, Number.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void SPR9486_floatModulusDouble() {
        Number expectedResult = 10.21F % 10.2;
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.21f % 10.2");
        Number result = expression.getValue(context, null, Number.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void SPR9486_floatPowerFloat() {
        Number expectedResult = Math.pow(10.21F, (-10.2F));
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.21f ^ -10.2f");
        Number result = expression.getValue(context, null, Number.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void SPR9486_floatPowerDouble() {
        Number expectedResult = Math.pow(10.21F, 10.2);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("10.21f ^ 10.2");
        Number result = expression.getValue(context, null, Number.class);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void SPR9994_bridgeMethods() throws Exception {
        ReflectivePropertyAccessor accessor = new ReflectivePropertyAccessor();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object target = new SpelReproTests.GenericImplementation();
        TypedValue value = accessor.read(context, target, "property");
        Assert.assertEquals(Integer.class, value.getTypeDescriptor().getType());
    }

    @Test
    public void SPR10162_onlyBridgeMethod() throws Exception {
        ReflectivePropertyAccessor accessor = new ReflectivePropertyAccessor();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object target = new SpelReproTests.OnlyBridgeMethod();
        TypedValue value = accessor.read(context, target, "property");
        Assert.assertEquals(Integer.class, value.getTypeDescriptor().getType());
    }

    @Test
    public void SPR10091_simpleTestValueType() {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(new SpelReproTests.BooleanHolder());
        Class<?> valueType = parser.parseExpression("simpleProperty").getValueType(evaluationContext);
        Assert.assertNotNull(valueType);
    }

    @Test
    public void SPR10091_simpleTestValue() {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(new SpelReproTests.BooleanHolder());
        Object value = parser.parseExpression("simpleProperty").getValue(evaluationContext);
        Assert.assertNotNull(value);
    }

    @Test
    public void SPR10091_primitiveTestValueType() {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(new SpelReproTests.BooleanHolder());
        Class<?> valueType = parser.parseExpression("primitiveProperty").getValueType(evaluationContext);
        Assert.assertNotNull(valueType);
    }

    @Test
    public void SPR10091_primitiveTestValue() {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(new SpelReproTests.BooleanHolder());
        Object value = parser.parseExpression("primitiveProperty").getValue(evaluationContext);
        Assert.assertNotNull(value);
    }

    @Test
    public void SPR16123() {
        ExpressionParser parser = new SpelExpressionParser();
        parser.parseExpression("simpleProperty").setValue(new SpelReproTests.BooleanHolder(), null);
        try {
            parser.parseExpression("primitiveProperty").setValue(new SpelReproTests.BooleanHolder(), null);
            Assert.fail("Should have thrown EvaluationException");
        } catch (EvaluationException ex) {
            // expected
        }
    }

    @Test
    public void SPR10146_malformedExpressions() {
        doTestSpr10146("/foo", "EL1070E: Problem parsing left operand");
        doTestSpr10146("*foo", "EL1070E: Problem parsing left operand");
        doTestSpr10146("%foo", "EL1070E: Problem parsing left operand");
        doTestSpr10146("<foo", "EL1070E: Problem parsing left operand");
        doTestSpr10146(">foo", "EL1070E: Problem parsing left operand");
        doTestSpr10146("&&foo", "EL1070E: Problem parsing left operand");
        doTestSpr10146("||foo", "EL1070E: Problem parsing left operand");
        doTestSpr10146("&foo", "EL1069E: missing expected character '&'");
        doTestSpr10146("|foo", "EL1069E: missing expected character '|'");
    }

    @Test
    public void SPR10125() {
        StandardEvaluationContext context = new StandardEvaluationContext();
        String fromInterface = parser.parseExpression((("T(" + (SpelReproTests.StaticFinalImpl1.class.getName())) + ").VALUE")).getValue(context, String.class);
        Assert.assertThat(fromInterface, is("interfaceValue"));
        String fromClass = parser.parseExpression((("T(" + (SpelReproTests.StaticFinalImpl2.class.getName())) + ").VALUE")).getValue(context, String.class);
        Assert.assertThat(fromClass, is("interfaceValue"));
    }

    @Test
    public void SPR10210() {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("bridgeExample", new org.springframework.expression.spel.spr10210.D());
        Expression parseExpression = parser.parseExpression("#bridgeExample.bridgeMethod()");
        parseExpression.getValue(context);
    }

    @Test
    public void SPR10328() {
        thrown.expect(SpelParseException.class);
        thrown.expectMessage("EL1071E: A required selection expression has not been specified");
        Expression exp = parser.parseExpression("$[]");
        exp.getValue(Arrays.asList("foo", "bar", "baz"));
    }

    @Test
    public void SPR10452() {
        SpelParserConfiguration configuration = new SpelParserConfiguration(false, false);
        ExpressionParser parser = new SpelExpressionParser(configuration);
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression spel = parser.parseExpression("#enumType.values()");
        context.setVariable("enumType", SpelReproTests.ABC.class);
        Object result = spel.getValue(context);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getClass().isArray());
        Assert.assertEquals(SpelReproTests.ABC.A, Array.get(result, 0));
        Assert.assertEquals(SpelReproTests.ABC.B, Array.get(result, 1));
        Assert.assertEquals(SpelReproTests.ABC.C, Array.get(result, 2));
        context.setVariable("enumType", SpelReproTests.XYZ.class);
        result = spel.getValue(context);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getClass().isArray());
        Assert.assertEquals(SpelReproTests.XYZ.X, Array.get(result, 0));
        Assert.assertEquals(SpelReproTests.XYZ.Y, Array.get(result, 1));
        Assert.assertEquals(SpelReproTests.XYZ.Z, Array.get(result, 2));
    }

    @Test
    public void SPR9495() {
        SpelParserConfiguration configuration = new SpelParserConfiguration(false, false);
        ExpressionParser parser = new SpelExpressionParser(configuration);
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression spel = parser.parseExpression("#enumType.values()");
        context.setVariable("enumType", SpelReproTests.ABC.class);
        Object result = spel.getValue(context);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getClass().isArray());
        Assert.assertEquals(SpelReproTests.ABC.A, Array.get(result, 0));
        Assert.assertEquals(SpelReproTests.ABC.B, Array.get(result, 1));
        Assert.assertEquals(SpelReproTests.ABC.C, Array.get(result, 2));
        context.addMethodResolver(new MethodResolver() {
            @Override
            public MethodExecutor resolve(EvaluationContext context, Object targetObject, String name, List<TypeDescriptor> argumentTypes) throws AccessException {
                return new MethodExecutor() {
                    @Override
                    public TypedValue execute(EvaluationContext context, Object target, Object... arguments) throws AccessException {
                        try {
                            Method method = SpelReproTests.XYZ.class.getMethod("values");
                            Object value = method.invoke(target, arguments);
                            return new TypedValue(value, narrow(value));
                        } catch (Exception ex) {
                            throw new AccessException(ex.getMessage(), ex);
                        }
                    }
                };
            }
        });
        result = spel.getValue(context);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getClass().isArray());
        Assert.assertEquals(SpelReproTests.XYZ.X, Array.get(result, 0));
        Assert.assertEquals(SpelReproTests.XYZ.Y, Array.get(result, 1));
        Assert.assertEquals(SpelReproTests.XYZ.Z, Array.get(result, 2));
    }

    @Test
    public void SPR10486() {
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        SpelReproTests.Spr10486 rootObject = new SpelReproTests.Spr10486();
        Expression classNameExpression = parser.parseExpression("class.name");
        Expression nameExpression = parser.parseExpression("name");
        Assert.assertThat(classNameExpression.getValue(context, rootObject), equalTo(((Object) (SpelReproTests.Spr10486.class.getName()))));
        Assert.assertThat(nameExpression.getValue(context, rootObject), equalTo(((Object) ("name"))));
    }

    @Test
    public void SPR11142() {
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        SpelReproTests.Spr11142 rootObject = new SpelReproTests.Spr11142();
        Expression expression = parser.parseExpression("something");
        thrown.expect(SpelEvaluationException.class);
        thrown.expectMessage("'something' cannot be found");
        expression.getValue(context, rootObject);
    }

    @Test
    public void SPR9194() {
        SpelReproTests.TestClass2 one = new SpelReproTests.TestClass2("abc");
        SpelReproTests.TestClass2 two = new SpelReproTests.TestClass2("abc");
        Map<String, SpelReproTests.TestClass2> map = new HashMap<>();
        map.put("one", one);
        map.put("two", two);
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expr = parser.parseExpression("['one'] == ['two']");
        Assert.assertTrue(expr.getValue(map, Boolean.class));
    }

    @Test
    public void SPR11348() {
        Collection<String> coll = new LinkedHashSet<>();
        coll.add("one");
        coll.add("two");
        coll = Collections.unmodifiableCollection(coll);
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expr = parser.parseExpression("new java.util.ArrayList(#root)");
        Object value = expr.getValue(coll);
        Assert.assertTrue((value instanceof ArrayList));
        @SuppressWarnings("rawtypes")
        ArrayList list = ((ArrayList) (value));
        Assert.assertEquals("one", list.get(0));
        Assert.assertEquals("two", list.get(1));
    }

    @Test
    public void SPR11445_simple() {
        StandardEvaluationContext context = new StandardEvaluationContext(new SpelReproTests.Spr11445Class());
        Expression expr = new SpelExpressionParser().parseRaw("echo(parameter())");
        Assert.assertEquals(1, expr.getValue(context));
    }

    @Test
    public void SPR11445_beanReference() {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new SpelReproTests.Spr11445Class());
        Expression expr = new SpelExpressionParser().parseRaw("@bean.echo(@bean.parameter())");
        Assert.assertEquals(1, expr.getValue(context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void SPR11494() {
        Expression exp = new SpelExpressionParser().parseExpression("T(java.util.Arrays).asList('a','b')");
        List<String> list = ((List<String>) (exp.getValue()));
        Assert.assertThat(list.size(), is(2));
    }

    @Test
    public void SPR11609() {
        StandardEvaluationContext sec = new StandardEvaluationContext();
        sec.addPropertyAccessor(new SpelReproTests.MapAccessor());
        Expression exp = new SpelExpressionParser().parseExpression("T(org.springframework.expression.spel.SpelReproTests$MapWithConstant).X");
        Assert.assertEquals(1, exp.getValue(sec));
    }

    @Test
    public void SPR9735() {
        SpelReproTests.Item item = new SpelReproTests.Item();
        item.setName("parent");
        SpelReproTests.Item item1 = new SpelReproTests.Item();
        item1.setName("child1");
        SpelReproTests.Item item2 = new SpelReproTests.Item();
        item2.setName("child2");
        item.add(item1);
        item.add(item2);
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        Expression exp = parser.parseExpression("#item[0].name");
        context.setVariable("item", item);
        Assert.assertEquals("child1", exp.getValue(context));
    }

    @Test
    public void SPR12502() {
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("#root.getClass().getName()");
        Assert.assertEquals(SpelReproTests.UnnamedUser.class.getName(), expression.getValue(new SpelReproTests.UnnamedUser()));
        Assert.assertEquals(SpelReproTests.NamedUser.class.getName(), expression.getValue(new SpelReproTests.NamedUser()));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void SPR12522() {
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("T(java.util.Arrays).asList('')");
        Object value = expression.getValue();
        Assert.assertTrue((value instanceof List));
        Assert.assertTrue(((List) (value)).isEmpty());
    }

    @Test
    public void SPR12803() {
        StandardEvaluationContext sec = new StandardEvaluationContext();
        sec.setVariable("iterable", Collections.emptyList());
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("T(org.springframework.expression.spel.SpelReproTests.FooLists).newArrayList(#iterable)");
        Assert.assertTrue(((expression.getValue(sec)) instanceof ArrayList));
    }

    @Test
    public void SPR12808() {
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("T(org.springframework.expression.spel.SpelReproTests.DistanceEnforcer).from(#no)");
        StandardEvaluationContext sec = new StandardEvaluationContext();
        sec.setVariable("no", new Integer(1));
        Assert.assertTrue(expression.getValue(sec).toString().startsWith("Integer"));
        sec = new StandardEvaluationContext();
        sec.setVariable("no", new Float(1.0));
        Assert.assertTrue(expression.getValue(sec).toString().startsWith("Number"));
        sec = new StandardEvaluationContext();
        sec.setVariable("no", "1.0");
        Assert.assertTrue(expression.getValue(sec).toString().startsWith("Object"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void SPR13055() {
        List<Map<String, Object>> myPayload = new ArrayList<>();
        Map<String, Object> v1 = new HashMap<>();
        Map<String, Object> v2 = new HashMap<>();
        v1.put("test11", "test11");
        v1.put("test12", "test12");
        v2.put("test21", "test21");
        v2.put("test22", "test22");
        myPayload.add(v1);
        myPayload.add(v2);
        EvaluationContext context = new StandardEvaluationContext(myPayload);
        ExpressionParser parser = new SpelExpressionParser();
        String ex = "#root.![T(org.springframework.util.StringUtils).collectionToCommaDelimitedString(#this.values())]";
        List res = parser.parseExpression(ex).getValue(context, List.class);
        Assert.assertEquals("[test12,test11, test22,test21]", res.toString());
        res = parser.parseExpression("#root.![#this.values()]").getValue(context, List.class);
        Assert.assertEquals("[[test12, test11], [test22, test21]]", res.toString());
        res = parser.parseExpression("#root.![values()]").getValue(context, List.class);
        Assert.assertEquals("[[test12, test11], [test22, test21]]", res.toString());
    }

    @Test
    public void AccessingFactoryBean_spr9511() {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new SpelReproTests.MyBeanResolver());
        Expression expr = new SpelExpressionParser().parseRaw("@foo");
        Assert.assertEquals("custard", expr.getValue(context));
        expr = new SpelExpressionParser().parseRaw("&foo");
        Assert.assertEquals("foo factory", expr.getValue(context));
        try {
            expr = new SpelExpressionParser().parseRaw("&@foo");
            Assert.fail("Illegal syntax, error expected");
        } catch (SpelParseException spe) {
            Assert.assertEquals(INVALID_BEAN_REFERENCE, spe.getMessageCode());
            Assert.assertEquals(0, spe.getPosition());
        }
        try {
            expr = new SpelExpressionParser().parseRaw("@&foo");
            Assert.fail("Illegal syntax, error expected");
        } catch (SpelParseException spe) {
            Assert.assertEquals(INVALID_BEAN_REFERENCE, spe.getMessageCode());
            Assert.assertEquals(0, spe.getPosition());
        }
    }

    @Test
    public void SPR12035() {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression1 = parser.parseExpression("list.?[ value>2 ].size()!=0");
        Assert.assertTrue(expression1.getValue(new SpelReproTests.BeanClass(new SpelReproTests.ListOf(1.1), new SpelReproTests.ListOf(2.2)), Boolean.class));
        Expression expression2 = parser.parseExpression("list.?[ T(java.lang.Math).abs(value) > 2 ].size()!=0");
        Assert.assertTrue(expression2.getValue(new SpelReproTests.BeanClass(new SpelReproTests.ListOf(1.1), new SpelReproTests.ListOf((-2.2))), Boolean.class));
    }

    @Test
    public void SPR13055_maps() {
        EvaluationContext context = new StandardEvaluationContext();
        ExpressionParser parser = new SpelExpressionParser();
        Expression ex = parser.parseExpression("{'a':'y','b':'n'}.![value=='y'?key:null]");
        Assert.assertEquals("[a, null]", ex.getValue(context).toString());
        ex = parser.parseExpression("{2:4,3:6}.![T(java.lang.Math).abs(#this.key) + 5]");
        Assert.assertEquals("[7, 8]", ex.getValue(context).toString());
        ex = parser.parseExpression("{2:4,3:6}.![T(java.lang.Math).abs(#this.value) + 5]");
        Assert.assertEquals("[9, 11]", ex.getValue(context).toString());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void SPR10417() {
        List list1 = new ArrayList();
        list1.add("a");
        list1.add("b");
        list1.add("x");
        List list2 = new ArrayList();
        list2.add("c");
        list2.add("x");
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("list1", list1);
        context.setVariable("list2", list2);
        // #this should be the element from list1
        Expression ex = parser.parseExpression("#list1.?[#list2.contains(#this)]");
        Object result = ex.getValue(context);
        Assert.assertEquals("[x]", result.toString());
        // toString() should be called on the element from list1
        ex = parser.parseExpression("#list1.?[#list2.contains(toString())]");
        result = ex.getValue(context);
        Assert.assertEquals("[x]", result.toString());
        List list3 = new ArrayList();
        list3.add(1);
        list3.add(2);
        list3.add(3);
        list3.add(4);
        context = new StandardEvaluationContext();
        context.setVariable("list3", list3);
        ex = parser.parseExpression("#list3.?[#this > 2]");
        result = ex.getValue(context);
        Assert.assertEquals("[3, 4]", result.toString());
        ex = parser.parseExpression("#list3.?[#this >= T(java.lang.Math).abs(T(java.lang.Math).abs(#this))]");
        result = ex.getValue(context);
        Assert.assertEquals("[1, 2, 3, 4]", result.toString());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void SPR10417_maps() {
        Map map1 = new HashMap();
        map1.put("A", 65);
        map1.put("B", 66);
        map1.put("X", 66);
        Map map2 = new HashMap();
        map2.put("X", 66);
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("map1", map1);
        context.setVariable("map2", map2);
        // #this should be the element from list1
        Expression ex = parser.parseExpression("#map1.?[#map2.containsKey(#this.getKey())]");
        Object result = ex.getValue(context);
        Assert.assertEquals("{X=66}", result.toString());
        ex = parser.parseExpression("#map1.?[#map2.containsKey(key)]");
        result = ex.getValue(context);
        Assert.assertEquals("{X=66}", result.toString());
    }

    @Test
    public void SPR13918() {
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("encoding", "UTF-8");
        Expression ex = parser.parseExpression("T(java.nio.charset.Charset).forName(#encoding)");
        Object result = ex.getValue(context);
        Assert.assertEquals(StandardCharsets.UTF_8, result);
    }

    @Test
    public void SPR16032() {
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("str", "a\u0000b");
        Expression ex = parser.parseExpression("#str?.split(\'\u0000\')");
        Object result = ex.getValue(context);
        Assert.assertTrue(ObjectUtils.nullSafeEquals(result, new String[]{ "a", "b" }));
    }

    static class MyTypeLocator extends StandardTypeLocator {
        @Override
        public Class<?> findType(String typeName) throws EvaluationException {
            if (typeName.equals("Spr5899Class")) {
                return SpelReproTests.Spr5899Class.class;
            }
            if (typeName.equals("Outer")) {
                return SpelReproTests.Outer.class;
            }
            return super.findType(typeName);
        }
    }

    static class Spr5899Class {
        public Spr5899Class() {
        }

        public Spr5899Class(Integer i) {
        }

        public Spr5899Class(Integer i, String... s) {
        }

        public Integer tryToInvokeWithNull(Integer value) {
            return value;
        }

        public Integer tryToInvokeWithNull2(int i) {
            return new Integer(i);
        }

        public String tryToInvokeWithNull3(Integer value, String... strings) {
            StringBuilder sb = new StringBuilder();
            for (String string : strings) {
                if (string == null) {
                    sb.append("null");
                } else {
                    sb.append(string);
                }
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            return "instance";
        }
    }

    static class TestProperties {
        public Properties jdbcProperties = new Properties();

        public Properties foo = new Properties();

        TestProperties() {
            jdbcProperties.put("username", "Dave");
            jdbcProperties.put("alias", "Dave2");
            jdbcProperties.put("foo.bar", "Elephant");
            foo.put("bar", "alias");
        }
    }

    static class MapAccessor implements PropertyAccessor {
        @Override
        public Class<?>[] getSpecificTargetClasses() {
            return new Class<?>[]{ Map.class };
        }

        @Override
        public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
            return ((Map<?, ?>) (target)).containsKey(name);
        }

        @Override
        public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
            return new TypedValue(((Map<?, ?>) (target)).get(name));
        }

        @Override
        public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
            ((Map<String, Object>) (target)).put(name, newValue);
        }
    }

    static class Outer {
        static class Inner {
            public Inner() {
            }

            public static int run() {
                return 12;
            }

            public int run2() {
                return 13;
            }
        }
    }

    static class XX {
        public Map<String, String> m;

        public String floo = "bar";

        public XX() {
            m = new HashMap<>();
            m.put("$foo", "wibble");
            m.put("bar", "siddle");
        }
    }

    static class MyBeanResolver implements BeanResolver {
        @Override
        public Object resolve(EvaluationContext context, String beanName) throws AccessException {
            if (beanName.equals("foo")) {
                return "custard";
            } else
                if (beanName.equals("foo.bar")) {
                    return "trouble";
                } else
                    if (beanName.equals("&foo")) {
                        return "foo factory";
                    } else
                        if (beanName.equals("goo")) {
                            throw new AccessException("DONT ASK ME ABOUT GOO");
                        }



            return null;
        }
    }

    static class CCC {
        public boolean method(Object o) {
            System.out.println(o);
            return false;
        }
    }

    static class C {
        public List<String> ls;

        public String[] as;

        public Map<String, String> ms;

        C() {
            ls = new ArrayList<>();
            ls.add("abc");
            ls.add("def");
            as = new String[]{ "abc", "def" };
            ms = new HashMap<>();
            ms.put("abc", "xyz");
            ms.put("def", "pqr");
        }
    }

    static class D {
        public String a;

        private D(String s) {
            a = s;
        }

        @Override
        public String toString() {
            return ("D(" + (a)) + ")";
        }
    }

    static class Resource {
        public String getServer() {
            return "abc";
        }
    }

    static class ResourceSummary {
        private final SpelReproTests.Resource resource;

        ResourceSummary() {
            this.resource = new SpelReproTests.Resource();
        }

        public SpelReproTests.Resource getResource() {
            return resource;
        }
    }

    static class Foo {
        public SpelReproTests.ResourceSummary resource = new SpelReproTests.ResourceSummary();
    }

    static class Foo2 {
        public void execute(String str) {
            System.out.println(("Value: " + str));
        }
    }

    static class Message {
        private String payload;

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }
    }

    static class Goo {
        public static SpelReproTests.Goo instance = new SpelReproTests.Goo();

        public String bar = "key";

        public String value = null;

        public String wibble = "wobble";

        public String getKey() {
            return "hello";
        }

        public void setKey(String s) {
            value = s;
        }
    }

    static class Holder {
        public Map<String, String> map = new HashMap<>();
    }

    static class SPR9486_FunctionsClass {
        public int abs(int value) {
            return Math.abs(value);
        }

        public float abs(float value) {
            return Math.abs(value);
        }
    }

    public interface VarargsInterface {
        String process(String... args);
    }

    public static class VarargsReceiver implements SpelReproTests.VarargsInterface {
        @Override
        public String process(String... args) {
            return "OK";
        }
    }

    public static class ReflectionUtil<T extends Number> {
        public Object methodToCall(T param) {
            System.out.println(((param + " ") + (param.getClass())));
            return "Object methodToCall(T param)";
        }

        public void foo(int... array) {
            if ((array.length) == 0) {
                throw new RuntimeException();
            }
        }

        public void foo(float... array) {
            if ((array.length) == 0) {
                throw new RuntimeException();
            }
        }

        public void foo(double... array) {
            if ((array.length) == 0) {
                throw new RuntimeException();
            }
        }

        public void foo(short... array) {
            if ((array.length) == 0) {
                throw new RuntimeException();
            }
        }

        public void foo(long... array) {
            if ((array.length) == 0) {
                throw new RuntimeException();
            }
        }

        public void foo(boolean... array) {
            if ((array.length) == 0) {
                throw new RuntimeException();
            }
        }

        public void foo(char... array) {
            if ((array.length) == 0) {
                throw new RuntimeException();
            }
        }

        public void foo(byte... array) {
            if ((array.length) == 0) {
                throw new RuntimeException();
            }
        }

        public void bar(int... array) {
            if ((array.length) == 0) {
                throw new RuntimeException();
            }
        }
    }

    class TestPropertyAccessor implements PropertyAccessor {
        private String mapName;

        public TestPropertyAccessor(String mapName) {
            this.mapName = mapName;
        }

        @SuppressWarnings("unchecked")
        public Map<String, String> getMap(Object target) {
            try {
                Field f = target.getClass().getDeclaredField(mapName);
                return ((Map<String, String>) (f.get(target)));
            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
            return getMap(target).containsKey(name);
        }

        @Override
        public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
            return getMap(target).containsKey(name);
        }

        @Override
        public Class<?>[] getSpecificTargetClasses() {
            return new Class<?>[]{ SpelReproTests.ContextObject.class };
        }

        @Override
        public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
            return new TypedValue(getMap(target).get(name));
        }

        @Override
        public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
            getMap(target).put(name, ((String) (newValue)));
        }
    }

    static class ContextObject {
        public Map<String, String> firstContext = new HashMap<>();

        public Map<String, String> secondContext = new HashMap<>();

        public Map<String, String> thirdContext = new HashMap<>();

        public Map<String, String> fourthContext = new HashMap<>();

        public ContextObject() {
            firstContext.put("shouldBeFirst", "first");
            secondContext.put("shouldBeFirst", "second");
            thirdContext.put("shouldBeFirst", "third");
            fourthContext.put("shouldBeFirst", "fourth");
            secondContext.put("shouldBeSecond", "second");
            thirdContext.put("shouldBeSecond", "third");
            fourthContext.put("shouldBeSecond", "fourth");
            thirdContext.put("shouldBeThird", "third");
            fourthContext.put("shouldBeThird", "fourth");
            fourthContext.put("shouldBeFourth", "fourth");
        }

        public Map<String, String> getFirstContext() {
            return firstContext;
        }

        public Map<String, String> getSecondContext() {
            return secondContext;
        }

        public Map<String, String> getThirdContext() {
            return thirdContext;
        }

        public Map<String, String> getFourthContext() {
            return fourthContext;
        }
    }

    public static class ListOf {
        private final double value;

        public ListOf(double v) {
            this.value = v;
        }

        public double getValue() {
            return value;
        }
    }

    public static class BeanClass {
        private final List<SpelReproTests.ListOf> list;

        public BeanClass(SpelReproTests.ListOf... list) {
            this.list = Arrays.asList(list);
        }

        public List<SpelReproTests.ListOf> getList() {
            return list;
        }
    }

    private enum ABC {

        A,
        B,
        C;}

    private enum XYZ {

        X,
        Y,
        Z;}

    public static class BooleanHolder {
        private Boolean simpleProperty = true;

        private boolean primitiveProperty = true;

        public void setSimpleProperty(Boolean simpleProperty) {
            this.simpleProperty = simpleProperty;
        }

        public Boolean isSimpleProperty() {
            return this.simpleProperty;
        }

        public void setPrimitiveProperty(boolean primitiveProperty) {
            this.primitiveProperty = primitiveProperty;
        }

        public boolean isPrimitiveProperty() {
            return this.primitiveProperty;
        }
    }

    private interface GenericInterface<T extends Number> {
        T getProperty();
    }

    private static class GenericImplementation implements SpelReproTests.GenericInterface<Integer> {
        @Override
        public Integer getProperty() {
            return null;
        }
    }

    static class PackagePrivateClassWithGetter {
        public Integer getProperty() {
            return null;
        }
    }

    public static class OnlyBridgeMethod extends SpelReproTests.PackagePrivateClassWithGetter {}

    public interface StaticFinal {
        String VALUE = "interfaceValue";
    }

    public abstract static class AbstractStaticFinal implements SpelReproTests.StaticFinal {}

    public static class StaticFinalImpl1 extends SpelReproTests.AbstractStaticFinal implements SpelReproTests.StaticFinal {}

    public static class StaticFinalImpl2 extends SpelReproTests.AbstractStaticFinal {}

    public static class Spr10486 {
        private String name = "name";

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    static class Spr11142 {
        public String isSomething() {
            return "";
        }
    }

    static class TestClass2 {
        // SPR-9194
        String string;

        public TestClass2(String string) {
            this.string = string;
        }

        public boolean equals(Object other) {
            return ((this) == other) || ((other instanceof SpelReproTests.TestClass2) && (this.string.equals(((SpelReproTests.TestClass2) (other)).string)));
        }

        @Override
        public int hashCode() {
            return this.string.hashCode();
        }
    }

    static class Spr11445Class implements BeanResolver {
        private final AtomicInteger counter = new AtomicInteger();

        public int echo(int invocation) {
            return invocation;
        }

        public int parameter() {
            return this.counter.incrementAndGet();
        }

        @Override
        public Object resolve(EvaluationContext context, String beanName) throws AccessException {
            return beanName.equals("bean") ? this : null;
        }
    }

    @SuppressWarnings({ "rawtypes", "serial" })
    public static class MapWithConstant extends HashMap {
        public static final int X = 1;
    }

    public static class Item implements List<SpelReproTests.Item> {
        private String name;

        private List<SpelReproTests.Item> children = new ArrayList<>();

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public int size() {
            return this.children.size();
        }

        @Override
        public boolean isEmpty() {
            return this.children.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.children.contains(o);
        }

        @Override
        public Iterator<SpelReproTests.Item> iterator() {
            return this.children.iterator();
        }

        @Override
        public Object[] toArray() {
            return this.children.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return this.children.toArray(a);
        }

        @Override
        public boolean add(SpelReproTests.Item e) {
            return this.children.add(e);
        }

        @Override
        public boolean remove(Object o) {
            return this.children.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.children.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends SpelReproTests.Item> c) {
            return this.children.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends SpelReproTests.Item> c) {
            return this.children.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return this.children.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return this.children.retainAll(c);
        }

        @Override
        public void clear() {
            this.children.clear();
        }

        @Override
        public SpelReproTests.Item get(int index) {
            return this.children.get(index);
        }

        @Override
        public SpelReproTests.Item set(int index, SpelReproTests.Item element) {
            return this.children.set(index, element);
        }

        @Override
        public void add(int index, SpelReproTests.Item element) {
            this.children.add(index, element);
        }

        @Override
        public SpelReproTests.Item remove(int index) {
            return this.children.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return this.children.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return this.children.lastIndexOf(o);
        }

        @Override
        public ListIterator<SpelReproTests.Item> listIterator() {
            return this.children.listIterator();
        }

        @Override
        public ListIterator<SpelReproTests.Item> listIterator(int index) {
            return this.children.listIterator(index);
        }

        @Override
        public List<SpelReproTests.Item> subList(int fromIndex, int toIndex) {
            return this.children.subList(fromIndex, toIndex);
        }
    }

    public static class UnnamedUser {}

    public static class NamedUser {
        public String getName() {
            return "foo";
        }
    }

    public static class FooLists {
        public static <T> List<T> newArrayList(Iterable<T> iterable) {
            return new ArrayList<>();
        }

        public static <T> List<T> newArrayList(Object... elements) {
            throw new UnsupportedOperationException();
        }
    }

    public static class DistanceEnforcer {
        public static String from(Number no) {
            return "Number:" + (no.toString());
        }

        public static String from(Integer no) {
            return "Integer:" + (no.toString());
        }

        public static String from(Object no) {
            return "Object:" + (no.toString());
        }
    }
}

