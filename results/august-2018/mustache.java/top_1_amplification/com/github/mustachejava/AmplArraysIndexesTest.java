package com.github.mustachejava;


import com.github.mustachejava.codes.DefaultMustache;
import com.github.mustachejava.reflect.MissingWrapper;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.github.mustachejava.util.Wrapper;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
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
        String o_testArrayIndexExtension__32 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension__32);
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
        String o_testArrayIndexExtension_remove207__31 = writer.toString();
        Assert.assertEquals("<ol>\n    <li></li>\n    <li></li>\n    <li></li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_remove207__31);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg220() throws IOException, Exception {
        int __DSPOT_recursionLimit_11 = 149630311;
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
        String o_testArrayIndexExtension_mg220__33 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg220__33);
        mf.setRecursionLimit(__DSPOT_recursionLimit_11);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(149630311, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg220__33);
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
        String o_testArrayIndexExtension_remove208__30 = writer.toString();
        Assert.assertEquals("", o_testArrayIndexExtension_remove208__30);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString175() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "I", "c", "d" };
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
        String o_testArrayIndexExtensionlitString175__32 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>I</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>I</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtensionlitString175__32);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg211_failAssert1() throws IOException, Exception {
        try {
            String __DSPOT_s_3 = "$G`c:wkJ!][,J^uy}s#6";
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
            Assert.assertEquals("Illegal character in scheme name at index 0: $G`c:wkJ!][,J^uy}s#6", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString51_failAssert18() throws IOException, Exception {
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
    public void testArrayIndexExtensionlitString66_failAssert16() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test&}\n") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtensionlitString66 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Mismatched start/end tags: test != test&}\n</ol> in template:9 @[template:9]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg210_failAssert2() throws IOException, Exception {
        try {
            String __DSPOT_name_2 = "7%uE_&Ml%;sG#Ahw*&z*";
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
            Assert.assertEquals("Malformed escape pair at index 1: 7%uE_&Ml%;sG#Ahw*&z*", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString54_failAssert4() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtensionlitString54 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Mismatched start/end tags: null != test in template:8 @[template:8]", expected.getMessage());
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
        String o_testArrayIndexExtensionlitString58__32 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li></li>\n    <li></li>\n    <li></li>\n    <li></li>\n</ol>", o_testArrayIndexExtensionlitString58__32);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{p}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString15() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
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
        String o_testArrayIndexExtensionlitString15__32 = writer.toString();
        Assert.assertEquals("<ol>\n\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtensionlitString15__32);
        Assert.assertEquals("<ol>\n\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_add202() throws IOException, Exception {
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
        m.execute(writer, scope).flush();
        String o_testArrayIndexExtension_add202__34 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol><ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_add202__34);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString169() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "    <li>d</li>\n", "b", "c", "d" };
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
        String o_testArrayIndexExtensionlitString169__32 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>    &lt;li&gt;d&lt;/li&gt;&#10;</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>    &lt;li&gt;d&lt;/li&gt;&#10;</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtensionlitString169__32);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg209() throws IOException, Exception {
        List<Object> __DSPOT_scopes_1 = Collections.<Object>emptyList();
        String __DSPOT_name_0 = "[&oDAIOw? O!T}Lq8xal";
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
        String o_testArrayIndexExtension_mg209__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg209__35);
        Wrapper o_testArrayIndexExtension_mg209__36 = oh.find(__DSPOT_name_0, __DSPOT_scopes_1);
        Assert.assertEquals("[Missing: [&oDAIOw? O!T}Lq8xal Guards: [[DepthGuard: 0]]]", ((MissingWrapper) (o_testArrayIndexExtension_mg209__36)).toString());
        Assert.assertEquals(1, ((int) (((MissingWrapper) (o_testArrayIndexExtension_mg209__36)).hashCode())));
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg209__35);
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString7() throws IOException, Exception {
        String template = "\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
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
        String o_testArrayIndexExtensionlitString7__32 = writer.toString();
        Assert.assertEquals("\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtensionlitString7__32);
        Assert.assertEquals("\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString65_failAssert11() throws IOException, Exception {
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
    public void testArrayIndexExtension_mg219() throws IOException, Exception {
        String __DSPOT_extension_10 = "*s>).BmtV)2[gaNZ#S&G";
        String __DSPOT_name_9 = "l>^r@)C1RND7C-6y}W`_";
        String __DSPOT_dir_8 = "DA!L#vK5WR{oE1L&q_{{";
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
        String o_testArrayIndexExtension_mg219__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg219__35);
        String o_testArrayIndexExtension_mg219__36 = mf.resolvePartialPath(__DSPOT_dir_8, __DSPOT_name_9, __DSPOT_extension_10);
        Assert.assertEquals("DA!L#vK5WR{oE1L&q_{{l>^r@)C1RND7C-6y}W`_*s>).BmtV)2[gaNZ#S&G", o_testArrayIndexExtension_mg219__36);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg219__35);
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString45() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "]6^FT") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n]6^FT{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
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
        String o_testArrayIndexExtensionlitString45__32 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n]6^FT    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n\n</ol>", o_testArrayIndexExtensionlitString45__32);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n]6^FT{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
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
        String o_testArrayIndexExtensionlitString12__32 = writer.toString();
        Assert.assertEquals("<ol>\n    <li></li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtensionlitString12__32);
        Assert.assertEquals("<ol>\n    <li>{{test.1}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString64() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + ":") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n:{{/test}}\n</ol>", template);
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
        String o_testArrayIndexExtensionlitString64__32 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n::::</ol>", o_testArrayIndexExtensionlitString64__32);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n:{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString20() throws IOException, Exception {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
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
        String o_testArrayIndexExtensionlitString20__32 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>{test.0}}</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtensionlitString20__32);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionlitString69_failAssert13litString9414() throws IOException, Exception {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "gh`l V!3a(") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\ngh`l V!3a(</ol>", template);
            String result = "<ol>\n" + ((((((((("" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Assert.assertEquals("<ol>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
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
            StringWriter writer = new StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtensionlitString69 should have thrown MustacheException");
        } catch (MustacheException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771_add52382() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
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
        m.execute(writer, scope).flush();
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol><ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol><ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771litString51695() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "</ol>\n", "b", "c", "d" };
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>&lt;/ol&gt;&#10;</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>&lt;/ol&gt;&#10;</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>&lt;/ol&gt;&#10;</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>&lt;/ol&gt;&#10;</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771litString50792() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771litString50900_failAssert23() throws IOException, Exception {
        try {
            FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
            FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{v{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
            String o_testArrayIndexExtension_mg215__35 = writer.toString();
            Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
            Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
            org.junit.Assert.fail("testArrayIndexExtension_mg215_mg27771litString50900 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Mismatched start/end tags: null != test in template:9 @[template:9]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771litString50566() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
        String template = "<ol>\n" + (((((((("    <li>{{test.X}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.X}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li></li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.X}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li></li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771litString50975() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "_YXOa*|^GUQ9uzJBgrR") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n_YXOa*|^GUQ9uzJBgrR{{/test}}\n</ol>", template);
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n_YXOa*|^GUQ9uzJBgrR_YXOa*|^GUQ9uzJBgrR_YXOa*|^GUQ9uzJBgrR_YXOa*|^GUQ9uzJBgrR</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n_YXOa*|^GUQ9uzJBgrR{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n_YXOa*|^GUQ9uzJBgrR_YXOa*|^GUQ9uzJBgrR_YXOa*|^GUQ9uzJBgrR_YXOa*|^GUQ9uzJBgrR</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771litString50545() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
        String template = "\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        Assert.assertEquals("\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771litString51009_failAssert24() throws IOException, Exception {
        try {
            FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
            FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{Y/test}}\n") + "</ol>");
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
            String o_testArrayIndexExtension_mg215__35 = writer.toString();
            Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
            Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
            org.junit.Assert.fail("testArrayIndexExtension_mg215_mg27771litString51009 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to close \'test\' tag @[template:7]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771_mg52674_failAssert20() throws IOException, Exception {
        try {
            String __DSPOT_resourceName_6427 = "6|$>bT)n=A)!>90n&w@[";
            FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
            FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
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
            String o_testArrayIndexExtension_mg215__35 = writer.toString();
            Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
            Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
            mf.getReader(__DSPOT_resourceName_6427);
            org.junit.Assert.fail("testArrayIndexExtension_mg215_mg27771_mg52674 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 1: 6|$>bT)n=A)!>90n&w@[", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771litString50845() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "ab;>*") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\nab;>*{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\nab;>*    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\nab;>*{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\nab;>*    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n\n</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771litString50704() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "!_QKk0Mb!jodQm?`aeg5Z{Zq") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n!_QKk0Mb!jodQm?`aeg5Z{Zq</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n!_QKk0Mb!jodQm?`aeg5Z{Zq</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n!_QKk0Mb!jodQm?`aeg5Z{Zq</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n!_QKk0Mb!jodQm?`aeg5Z{Zq</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771_mg52569_failAssert22() throws IOException, Exception {
        try {
            String __DSPOT_name_6402 = "oOEEEv)w(Mq6x3}78eu#";
            FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
            FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
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
            String o_testArrayIndexExtension_mg215__35 = writer.toString();
            Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
            Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
            mf.compile(__DSPOT_name_6402);
            org.junit.Assert.fail("testArrayIndexExtension_mg215_mg27771_mg52569 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 14: oOEEEv)w(Mq6x3}78eu#", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771litString51809() throws IOException, Exception {
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "`" };
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>&#96;</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>&#96;</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>&#96;</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>&#96;</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771_mg52714() throws IOException, Exception {
        int __DSPOT_recursionLimit_6440 = -360834164;
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        mf.setRecursionLimit(__DSPOT_recursionLimit_6440);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(-360834164, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771litString50677_failAssert28() throws IOException, Exception {
        try {
            FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
            FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}C</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
            String o_testArrayIndexExtension_mg215__35 = writer.toString();
            Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
            Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
            org.junit.Assert.fail("testArrayIndexExtension_mg215_mg27771litString50677 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Mismatched start/end tags: null != test in template:6 @[template:6]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771_mg52558() throws IOException, Exception {
        List<Object> __DSPOT_scopes_6398 = Collections.<Object>emptyList();
        String __DSPOT_name_6397 = "$[<j.jmI+x2&1zKf#G@?";
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        Wrapper o_testArrayIndexExtension_mg215_mg27771_mg52558__50 = oh.find(__DSPOT_name_6397, __DSPOT_scopes_6398);
        Assert.assertEquals("[Missing: $[<j.jmI+x2&1zKf#G@? Guards: [[DepthGuard: 0]]]", ((MissingWrapper) (o_testArrayIndexExtension_mg215_mg27771_mg52558__50)).toString());
        Assert.assertEquals(1, ((int) (((MissingWrapper) (o_testArrayIndexExtension_mg215_mg27771_mg52558__50)).hashCode())));
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771_mg52701() throws IOException, Exception {
        String __DSPOT_extension_6437 = "ed v@S[5W0w2/2Lnt# `";
        String __DSPOT_name_6436 = "|Y().}TjMo8LgUh&u(!e";
        String __DSPOT_dir_6435 = "WB+$T_L(*lN[/X9N_8lI";
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        String o_testArrayIndexExtension_mg215_mg27771_mg52701__50 = mf.resolvePartialPath(__DSPOT_dir_6435, __DSPOT_name_6436, __DSPOT_extension_6437);
        Assert.assertEquals("WB+$T_L(*lN[/X9N_8lI|Y().}TjMo8LgUh&u(!e", o_testArrayIndexExtension_mg215_mg27771_mg52701__50);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771_mg52581_failAssert21() throws IOException, Exception {
        try {
            String __DSPOT_s_6405 = "_Z4zak$,$%)h(^siaSm{";
            FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
            FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
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
            String o_testArrayIndexExtension_mg215__35 = writer.toString();
            Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
            Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
            mf.compilePartial(__DSPOT_s_6405);
            org.junit.Assert.fail("testArrayIndexExtension_mg215_mg27771_mg52581 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Malformed escape pair at index 9: _Z4zak$,$%)h(^siaSm{", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_mg215_mg27771_mg52531() throws IOException, Exception {
        FragmentKey __DSPOT_o_6390 = new FragmentKey(new TemplateContext("6rvc?:PMtfS`F1La#<)K", "Km9}m`D! [}]#CG*u_@/", "DQ_I?Y@#)Q!c3-K6Ez#g", 855858073, true), "Z,1L[Q$8uL5I  )9o*1w");
        FragmentKey __DSPOT_templateKey_3095 = new FragmentKey(new TemplateContext("=(Wlf!,E`r3I1)HOJv|9", "x( k@w_XkvSGn[K`LV*)", "SA_7i-p%d-(6+=:l/1]v", 1563590835, true), "h%L}&YIWp)K[h`Wn[sV{");
        FragmentKey __DSPOT_templateKey_6 = new FragmentKey(new TemplateContext("HJ*J8r}4@(!YL#ZQsb>_", "1JVt2Y][1u)p]QM-k,I]", "-r8//GGUV@1wly$),bA%", 132111952, false), "Jum&)<4oK[>Va&1`i[aM");
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
        String o_testArrayIndexExtension_mg215__35 = writer.toString();
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Mustache o_testArrayIndexExtension_mg215__36 = mf.getFragment(__DSPOT_templateKey_6);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Mustache o_testArrayIndexExtension_mg215_mg27771__44 = mf.getFragment(__DSPOT_templateKey_3095);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
        boolean o_testArrayIndexExtension_mg215_mg27771_mg52531__50 = __DSPOT_templateKey_6.equals(__DSPOT_o_6390);
        Assert.assertFalse(o_testArrayIndexExtension_mg215_mg27771_mg52531__50);
        Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("template", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_mg215__35);
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).isRecursive());
        Assert.assertEquals("-r8//GGUV@1wly$),bA%", ((DefaultMustache) (o_testArrayIndexExtension_mg215__36)).getName());
        Assert.assertFalse(((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).isRecursive());
        Assert.assertEquals("SA_7i-p%d-(6+=:l/1]v", ((DefaultMustache) (o_testArrayIndexExtension_mg215_mg27771__44)).getName());
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

