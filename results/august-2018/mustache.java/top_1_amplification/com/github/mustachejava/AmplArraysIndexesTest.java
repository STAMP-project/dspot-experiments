package com.github.mustachejava;


import com.github.mustachejava.codes.DefaultMustache;
import com.github.mustachejava.reflect.MissingWrapper;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.github.mustachejava.util.Wrapper;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class AmplArraysIndexesTest {
    @Test(timeout = 10000)
    public void testArrayIndexExtension() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_remove207() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_remove208() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString51_failAssert10() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{&#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtensionlitString51 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Mismatched start/end tags: null != test in template:9 @[template:9]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString17_failAssert7() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "{{/test}}\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtensionlitString17 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Mismatched start/end tags: null != test in template:3 @[template:3]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString52_failAssert18() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtensionlitString52 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Mismatched start/end tags: test}\n    <li>{{. != test in template:8 @[template:8]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_add203() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        Writer o_testArrayIndexExtension_add203__30 = m.execute(writer, scope);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", ((StringBuffer) (((StringWriter) (o_testArrayIndexExtension_add203__30)).getBuffer())).toString());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", ((StringWriter) (o_testArrayIndexExtension_add203__30)).toString());
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol><ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", ((StringBuffer) (((StringWriter) (o_testArrayIndexExtension_add203__30)).getBuffer())).toString());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol><ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", ((StringWriter) (o_testArrayIndexExtension_add203__30)).toString());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionnull231() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(null);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg211_failAssert4() throws IOException, Exception {
        try {
            String __DSPOT_s_3 = "gaNZ#S&Gu4*{9Gi}cRQH";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            mf.compilePartial(__DSPOT_s_3);
            org.junit.Assert.fail("testArrayIndexExtension_mg211 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in fragment at index 11: gaNZ#S&Gu4*{9Gi}cRQH", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg210_failAssert2() throws IOException, Exception {
        try {
            String __DSPOT_name_2 = "C-6y}W`_*s>).BmtV)2[";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            mf.compile(__DSPOT_name_2);
            org.junit.Assert.fail("testArrayIndexExtension_mg210 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 4: C-6y}W`_*s>).BmtV)2[", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionnull232_failAssert6() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(null);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtensionnull232 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for test.1 @[template:2]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString58() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{p}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{p}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{p}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg209() throws IOException, Exception {
        List<Object> __DSPOT_scopes_1 = Collections.singletonList(new Object());
        String __DSPOT_name_0 = "{oE1L&q_{{l>^r@)C1RN";
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Wrapper o_testArrayIndexExtension_mg209__37 = oh.find(__DSPOT_name_0, __DSPOT_scopes_1);
        Assert.assertEquals("[Missing: {oE1L&q_{{l>^r@)C1RN Guards: [[DepthGuard: 1], [ClassGuard: 0 java.lang.Object]]]", ((MissingWrapper) (o_testArrayIndexExtension_mg209__37)).toString());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString12() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString65_failAssert13() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "<ol>\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtensionlitString65 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to close \'test\' tag @[template:7]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg217() throws IOException, Exception {
        int __DSPOT_recursionLimit_11 = 507272895;
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        mf.setRecursionLimit(__DSPOT_recursionLimit_11);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(507272895, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString57() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{test.3}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{test.3}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{test.3}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg216() throws IOException, Exception {
        String __DSPOT_extension_10 = "B+ _ 2&pb?56TtKz.F5M";
        String __DSPOT_name_9 = "O|oPq,r5>K`HNw]f4QDh";
        String __DSPOT_dir_8 = "o-9M/^zOCxu?!rIXp5pN";
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        String o_testArrayIndexExtension_mg216__36 = mf.resolvePartialPath(__DSPOT_dir_8, __DSPOT_name_9, __DSPOT_extension_10);
        Assert.assertEquals("o-9M/^zOCxu?!rIXp5pNO|oPq,r5>K`HNw]f4QDhB+ _ 2&pb?56TtKz.F5M", o_testArrayIndexExtension_mg216__36);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg214() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("/&h4]]s%=])JWOM_4gds", "L9rC)A6fdF&0xT!&b-W-", "(y_V1a;?h(*fl<xJgehg", 1605040354, false), "?HCt1H=N6{+DN-eV8<Or");
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Mustache o_testArrayIndexExtension_mg214__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg214__36)).isRecursive());
        Assert.assertEquals("(y_V1a;?h(*fl<xJgehg", ((DefaultMustache) (o_testArrayIndexExtension_mg214__36)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString75() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</{ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</{ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</{ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_failAssert3() throws IOException, Exception {
        try {
            String __DSPOT_resourceName_7 = ";(?xw0]W#nkib%A@IY:W";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            mf.getReader(__DSPOT_resourceName_7);
            org.junit.Assert.fail("testArrayIndexExtension_mg215 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Malformed escape pair at index 13: ;(?xw0]W#nkib%A@IY:W", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString57_remove27939() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{test.3}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{test.3}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{test.3}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString51_failAssert10_add27192() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{&#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{&#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope);
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtensionlitString51 should have thrown MustacheException");
        } catch (MustacheException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString145null32648_failAssert38() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "d") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(null);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtensionlitString145null32648 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for test.1 @[template:2]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString144_mg28777() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_808 = new FragmentKey(new TemplateContext("losBN)T4Y(7ra(ptm%7:", "T6.wTEnV09Fk*xMfh@lu", "s4LakD0R/uxjvnl>+RT+", -541609876, false), "6gC73k5A|5?]_YLm!1%W");
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + ":") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n:    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Mustache o_testArrayIndexExtensionlitString144_mg28777__36 = mf.getFragment(__DSPOT_templateKey_808);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtensionlitString144_mg28777__36)).isRecursive());
        Assert.assertEquals("s4LakD0R/uxjvnl>+RT+", ((DefaultMustache) (o_testArrayIndexExtensionlitString144_mg28777__36)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n:    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString145_remove27383() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "d") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\nd    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\nd    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionnull231_add25022() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(null);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString51_failAssert10null33487() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{&#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{&#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            mf.setObjectHandler(null);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtensionlitString51 should have thrown MustacheException");
        } catch (MustacheException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg217litNum24176() throws IOException, Exception {
        int __DSPOT_recursionLimit_11 = 507272894;
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        mf.setRecursionLimit(__DSPOT_recursionLimit_11);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(507272894, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionnull233_mg28664_failAssert21() throws IOException, Exception {
        try {
            String __DSPOT_s_662 = "nXQ>g,p6h#&T)0-D3Cvr";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), null);
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            mf.compilePartial(__DSPOT_s_662);
            org.junit.Assert.fail("testArrayIndexExtensionnull233_mg28664 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 3: nXQ>g,p6h#&T)0-D3Cvr", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString140_mg29503() throws IOException, Exception {
        String __DSPOT_name_1753 = "?(4vSt+pdt0Y]Pg,b0_(";
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "   <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n   <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Mustache o_testArrayIndexExtensionlitString140_mg29503__34 = mf.compile(__DSPOT_name_1753);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtensionlitString140_mg29503__34)).isRecursive());
        Assert.assertEquals("?(4vSt+pdt0Y]Pg,b0_(", ((DefaultMustache) (o_testArrayIndexExtensionlitString140_mg29503__34)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n   <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString59_mg28789() throws IOException, Exception {
        String __DSPOT_extension_825 = "ylHQN[7oX]yUf!+k>Lh6";
        String __DSPOT_name_824 = "#s3a1_kOGPv21-61}y`A";
        String __DSPOT_dir_823 = "mqgHB].A_bnaE4WLTj!{";
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</lui>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</lui>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        String o_testArrayIndexExtensionlitString59_mg28789__36 = mf.resolvePartialPath(__DSPOT_dir_823, __DSPOT_name_824, __DSPOT_extension_825);
        Assert.assertEquals("mqgHB].A_bnaE4WLTj!{#s3a1_kOGPv21-61}y`AylHQN[7oX]yUf!+k>Lh6", o_testArrayIndexExtensionlitString59_mg28789__36);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</lui>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString70_failAssert8_add27174() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n</ol>", template);
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope);
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtensionlitString70 should have thrown MustacheException");
        } catch (MustacheException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString58_remove27374() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{p}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{p}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{p}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString132_mg28712() throws IOException, Exception {
        List<Object> __DSPOT_scopes_725 = Collections.singletonList(new Object());
        String __DSPOT_name_724 = "+B!%<>yN#vhi]?4sWyn6";
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</i>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</i>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Wrapper o_testArrayIndexExtensionlitString132_mg28712__37 = oh.find(__DSPOT_name_724, __DSPOT_scopes_725);
        Assert.assertEquals("[Missing: +B!%<>yN#vhi]?4sWyn6 Guards: [[DepthGuard: 1], [ClassGuard: 0 java.lang.Object]]]", ((MissingWrapper) (o_testArrayIndexExtensionlitString132_mg28712__37)).toString());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</i>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg210_failAssert2null33443() throws IOException, Exception {
        try {
            String __DSPOT_name_2 = "C-6y}W`_*s>).BmtV)2[";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(null);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
            Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            mf.compile(__DSPOT_name_2);
            org.junit.Assert.fail("testArrayIndexExtension_mg210 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_add197litString883_failAssert41() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    object.getClass().isArray();
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtension_add197litString883 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to close \'test\' tag @[template:7]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString143_mg28249() throws IOException, Exception {
        List<Object> __DSPOT_scopes_124 = Collections.<Object>emptyList();
        String __DSPOT_name_123 = "k{n;V+ u`@6=(qbgTE($";
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Wrapper o_testArrayIndexExtensionlitString143_mg28249__36 = oh.find(__DSPOT_name_123, __DSPOT_scopes_124);
        Assert.assertEquals("[Missing: k{n;V+ u`@6=(qbgTE($ Guards: [[DepthGuard: 0]]]", ((MissingWrapper) (o_testArrayIndexExtensionlitString143_mg28249__36)).toString());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString139_add25407() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + " 9   <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n 9   <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        Writer o_testArrayIndexExtensionlitString139_add25407__30 = m.execute(writer, scope);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", ((StringBuffer) (((StringWriter) (o_testArrayIndexExtensionlitString139_add25407__30)).getBuffer())).toString());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", ((StringWriter) (o_testArrayIndexExtensionlitString139_add25407__30)).toString());
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n 9   <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol><ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", ((StringBuffer) (((StringWriter) (o_testArrayIndexExtensionlitString139_add25407__30)).getBuffer())).toString());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol><ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", ((StringWriter) (o_testArrayIndexExtensionlitString139_add25407__30)).toString());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionnull231litString8676() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</i>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</i>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(null);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</i>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg214litNum24186litBool51108() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("/&h4]]s%=])JWOM_4gds", "L9rC)A6fdF&0xT!&b-W-", "(y_V1a;?h(*fl<xJgehg", -1605463793, true), "?HCt1H=N6{+DN-eV8<Or");
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Mustache o_testArrayIndexExtension_mg214__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg214__36)).isRecursive());
        Assert.assertEquals("(y_V1a;?h(*fl<xJgehg", ((DefaultMustache) (o_testArrayIndexExtension_mg214__36)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString133_remove27730_mg55411_failAssert42() throws IOException, Exception {
        try {
            String __DSPOT_name_4681 = "/24$),9E@g:ljR?D3xdi";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "ycw,-c^.vZ(8(U^") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            mf.compile(__DSPOT_name_4681);
            org.junit.Assert.fail("testArrayIndexExtensionlitString133_remove27730_mg55411 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /24$),9E@g:ljR?D3xdi not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString51_failAssert10_rv32425_add52316() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{&#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{&#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope);
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtensionlitString51 should have thrown MustacheException");
        } catch (MustacheException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString133_remove27730_mg55412_failAssert54() throws IOException, Exception {
        try {
            String __DSPOT_s_4682 = "-PdlP2@_Q=B1qCfB0]De";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "ycw,-c^.vZ(8(U^") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            mf.compilePartial(__DSPOT_s_4682);
            org.junit.Assert.fail("testArrayIndexExtensionlitString133_remove27730_mg55412 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 17: -PdlP2@_Q=B1qCfB0]De", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionnull231_rv30745_remove54654() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(null);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString132_mg28712null59044_failAssert51() throws IOException, Exception {
        try {
            List<Object> __DSPOT_scopes_725 = Collections.singletonList(new Object());
            String __DSPOT_name_724 = "+B!%<>yN#vhi]?4sWyn6";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</i>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(null);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            Wrapper o_testArrayIndexExtensionlitString132_mg28712__37 = oh.find(__DSPOT_name_724, __DSPOT_scopes_725);
            org.junit.Assert.fail("testArrayIndexExtensionlitString132_mg28712null59044 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for test.1 @[template:2]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString145_remove27383_mg55297() throws IOException, Exception {
        String __DSPOT_extension_4533 = "y[&11MB:nM%5e^]_#o-S";
        String __DSPOT_name_4532 = "/G.:`|AAsrdM|y%PL|`<";
        String __DSPOT_dir_4531 = "Da&-Vmq@!eH!wNw1b#uU";
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "d") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\nd    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        String o_testArrayIndexExtensionlitString145_remove27383_mg55297__35 = mf.resolvePartialPath(__DSPOT_dir_4531, __DSPOT_name_4532, __DSPOT_extension_4533);
        Assert.assertEquals("/G.:`|AAsrdM|y%PL|`<", o_testArrayIndexExtensionlitString145_remove27383_mg55297__35);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\nd    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionnull231_rv30745_mg56279_failAssert43() throws IOException, Exception {
        try {
            String __DSPOT_name_5797 = "$U$+lPq5I$ n)luh:(xS";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(null);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            mf.compile(__DSPOT_name_5797);
            org.junit.Assert.fail("testArrayIndexExtensionnull231_rv30745_mg56279 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in scheme name at index 0: $U$+lPq5I$ n)luh:(xS", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionnull231_rv30745litNum51098() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(null);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString59_mg28789_remove54208() throws IOException, Exception {
        String __DSPOT_extension_825 = "ylHQN[7oX]yUf!+k>Lh6";
        String __DSPOT_name_824 = "#s3a1_kOGPv21-61}y`A";
        String __DSPOT_dir_823 = "mqgHB].A_bnaE4WLTj!{";
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</lui>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</lui>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        String o_testArrayIndexExtensionlitString59_mg28789__36 = mf.resolvePartialPath(__DSPOT_dir_823, __DSPOT_name_824, __DSPOT_extension_825);
        Assert.assertEquals("mqgHB].A_bnaE4WLTj!{#s3a1_kOGPv21-61}y`AylHQN[7oX]yUf!+k>Lh6", o_testArrayIndexExtensionlitString59_mg28789__36);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</lui>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString132_mg28712litString38647() throws IOException, Exception {
        List<Object> __DSPOT_scopes_725 = Collections.singletonList(new Object());
        String __DSPOT_name_724 = "+B!%<>yN#vhi]?4sWyn6";
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</i>\n") + "    <li>b</li>\n") + "    ]li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</i>\n    <li>b</li>\n    ]li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Wrapper o_testArrayIndexExtensionlitString132_mg28712__37 = oh.find(__DSPOT_name_724, __DSPOT_scopes_725);
        Assert.assertEquals("[Missing: +B!%<>yN#vhi]?4sWyn6 Guards: [[DepthGuard: 1], [ClassGuard: 0 java.lang.Object]]]", ((MissingWrapper) (o_testArrayIndexExtensionlitString132_mg28712__37)).toString());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</i>\n    <li>b</li>\n    ]li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString70_failAssert8_mg30037litBool51102() throws IOException, Exception {
        try {
            FragmentKey __DSPOT_templateKey_2446 = new FragmentKey(new TemplateContext("`@oXb+}k<z6I3#]pp?@U", "qkTqw0{8<VZ?1Xy_EHQ$", "#@B5Ytbg>)PZHMf{YZms", -1704865608, false), "O_=(]n!mOHsO8Tzel3f-");
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n</ol>", template);
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            ReflectionObjectHandler oh = new ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new AmplArraysIndexesTest.ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new StringReader(template), "template");
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtensionlitString70 should have thrown MustacheException");
            mf.getFragment(__DSPOT_templateKey_2446);
        } catch (MustacheException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString58_mg28353_add51854() throws IOException, Exception {
        boolean __DSPOT_startOfLine_258 = false;
        String __DSPOT_appended_257 = "@3;ZC u)_0.ne. (h0PD";
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{p}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{p}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        String o_testArrayIndexExtensionlitString58_mg28353_add51854__35 = mf.filterText(__DSPOT_appended_257, __DSPOT_startOfLine_258);
        Assert.assertEquals("@3;ZC u)_0.ne. (h0PD", o_testArrayIndexExtensionlitString58_mg28353_add51854__35);
        String o_testArrayIndexExtensionlitString58_mg28353__35 = mf.filterText(__DSPOT_appended_257, __DSPOT_startOfLine_258);
        Assert.assertEquals("@3;ZC u)_0.ne. (h0PD", o_testArrayIndexExtensionlitString58_mg28353__35);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{p}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("@3;ZC u)_0.ne. (h0PD", o_testArrayIndexExtensionlitString58_mg28353_add51854__35);
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString59_add25229_mg54824() throws IOException, Exception {
        int __DSPOT_recursionLimit_3921 = 716305884;
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</lui>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</lui>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        ((DefaultMustache) (m)).getName();
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        mf.setRecursionLimit(__DSPOT_recursionLimit_3921);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</lui>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(716305884, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString59_add25229_remove54057() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</lui>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</lui>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        ReflectionObjectHandler oh = new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache m = mf.compile(new StringReader(template), "template");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        ((DefaultMustache) (m)).getName();
        StringWriter writer = new StringWriter();
        m.execute(writer, scope).flush();
        writer.toString();
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</lui>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    private static class ArrayMap extends AbstractMap<Object, Object> implements Iterable<Object> {
        private final Object object;

        public ArrayMap(Object object) {
            this.object = object;
        }

        @Override
        public Object get(Object key) {
            try {
                int index = Integer.parseInt(key.toString());
                return Array.get(object, index);
            } catch (NumberFormatException nfe) {
                return null;
            }
        }

        @Override
        public boolean containsKey(Object key) {
            return (get(key)) != null;
        }

        @Override
        public Set<Map.Entry<Object, Object>> entrySet() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<Object> iterator() {
            return new Iterator<Object>() {
                int index = 0;

                int length = Array.getLength(object);

                @Override
                public boolean hasNext() {
                    return (index) < (length);
                }

                @Override
                public Object next() {
                    return Array.get(object, ((index)++));
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}

