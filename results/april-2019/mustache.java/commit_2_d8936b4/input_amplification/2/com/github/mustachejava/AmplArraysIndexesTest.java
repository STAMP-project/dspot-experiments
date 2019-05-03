package com.github.mustachejava;


import com.github.mustachejava.reflect.ReflectionObjectHandler;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class AmplArraysIndexesTest {
    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString8_failAssert0() throws IOException {
        try {
            String template = "<ol>\n" + (((((((("{{=toolyn}}" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString8 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:2]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString44_failAssert0() throws IOException {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "{{=toolyn}}") + "{{/test}}\n") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString44 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:8]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString44_failAssert0_add25773_failAssert0() throws IOException {
        try {
            {
                String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "{{=toolyn}}") + "{{/test}}\n") + "</ol>");
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
                        super.coerce(object);
                        return super.coerce(object);
                    }
                };
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(oh);
                Mustache m = mf.compile(new StringReader(template), "template");
                StringWriter writer = new StringWriter();
                m.execute(writer, scope).flush();
                writer.toString();
                org.junit.Assert.fail("testArrayIndexExtension_literalMutationString44 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString44_failAssert0_add25773 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:8]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString3_failAssert0null27274_failAssert0() throws IOException {
        try {
            {
                String template = "{{=toolyn}}" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
                m.execute(null, scope).flush();
                writer.toString();
                org.junit.Assert.fail("testArrayIndexExtension_literalMutationString3 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString3_failAssert0null27274 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString44_failAssert0null27288_failAssert0() throws IOException {
        try {
            {
                String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "{{=toolyn}}") + "{{/test}}\n") + "</ol>");
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
                org.junit.Assert.fail("testArrayIndexExtension_literalMutationString44 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString44_failAssert0null27288 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[null:8]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString44_failAssert0_literalMutationString22088_failAssert0() throws IOException {
        try {
            {
                String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "{{=toolyn}}") + "{{/test}}\n") + "</ol>");
                String result = "<l>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
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
                org.junit.Assert.fail("testArrayIndexExtension_literalMutationString44 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString44_failAssert0_literalMutationString22088 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:8]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString77_literalMutationString2683_failAssert0() throws IOException {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{=toolyn}}") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "  ^  <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString77_literalMutationString2683 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:9]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString3_failAssert0() throws IOException {
        try {
            String template = "{{=toolyn}}" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString3 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString56_failAssert0() throws IOException {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "{{=toolyn}}");
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString56 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:10]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString33_failAssert0() throws IOException {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "{{=toolyn}}") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString33 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:6]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString38_failAssert0() throws IOException {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{=toolyn}}") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString38 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:7]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString51_failAssert0() throws IOException {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{=toolyn}}") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString51 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:9]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString14_failAssert0() throws IOException {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "{{=toolyn}}") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString14 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:3]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString16_literalMutationString17016_failAssert0() throws IOException {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{testz0}}</li>\n") + "{{=toolyn}}") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString16_literalMutationString17016 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:4]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString3_failAssert0_add25746_failAssert0() throws IOException {
        try {
            {
                String template = "{{=toolyn}}" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
                        super.coerce(object);
                        return super.coerce(object);
                    }
                };
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(oh);
                Mustache m = mf.compile(new StringReader(template), "template");
                StringWriter writer = new StringWriter();
                m.execute(writer, scope).flush();
                writer.toString();
                org.junit.Assert.fail("testArrayIndexExtension_literalMutationString3 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString3_failAssert0_add25746 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString26_failAssert0() throws IOException {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "{{=toolyn}}") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString26 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:5]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString20_failAssert0() throws IOException {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "{{=toolyn}}") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString20 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:4]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtensionnull157_failAssert0_literalMutationString20481_failAssert0() throws IOException {
        try {
            {
                String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{=toolyn}}") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
                org.junit.Assert.fail("testArrayIndexExtensionnull157 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testArrayIndexExtensionnull157_failAssert0_literalMutationString20481 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:7]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString8_failAssert0null27253_failAssert0() throws IOException {
        try {
            {
                String template = "<ol>\n" + (((((((("{{=toolyn}}" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
                org.junit.Assert.fail("testArrayIndexExtension_literalMutationString8 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString8_failAssert0null27253 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[null:2]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString8_failAssert0null27252_failAssert0() throws IOException {
        try {
            {
                String template = "<ol>\n" + (((((((("{{=toolyn}}" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
                org.junit.Assert.fail("testArrayIndexExtension_literalMutationString8 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString8_failAssert0null27252 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:2]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString8_failAssert0_add25708_failAssert0() throws IOException {
        try {
            {
                String template = "<ol>\n" + (((((((("{{=toolyn}}" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
                org.junit.Assert.fail("testArrayIndexExtension_literalMutationString8 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString8_failAssert0_add25708 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:2]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString8_failAssert0_literalMutationString21053_failAssert0() throws IOException {
        try {
            {
                String template = "<ol>\n" + (((((((("{{=toolyn}}" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<oFl>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
                org.junit.Assert.fail("testArrayIndexExtension_literalMutationString8 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString8_failAssert0_literalMutationString21053 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:2]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString8_failAssert0_literalMutationString21147_failAssert0() throws IOException {
        try {
            {
                String template = "<ol>\n" + (((((((("{{=toolyn}}" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
                String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
                Object scope = new Object() {
                    String[] test = new String[]{ "{{=toolyn}}", "b", "c", "d" };
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
                org.junit.Assert.fail("testArrayIndexExtension_literalMutationString8 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString8_failAssert0_literalMutationString21147 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:2]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString8_failAssert0_add25711_failAssert0() throws IOException {
        try {
            {
                String template = "<ol>\n" + (((((((("{{=toolyn}}" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
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
                mf.setObjectHandler(oh);
                Mustache m = mf.compile(new StringReader(template), "template");
                StringWriter writer = new StringWriter();
                m.execute(writer, scope).flush();
                writer.toString();
                org.junit.Assert.fail("testArrayIndexExtension_literalMutationString8 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString8_failAssert0_add25711 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:2]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString84_literalMutationString15991_failAssert0() throws IOException {
        try {
            String template = "{{=toolyn}}" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "t=&ULcQik(28D$?") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString84_literalMutationString15991 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString137_literalMutationString16905_failAssert0() throws IOException {
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "{{=toolyn}}");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "{{=toolyn}}" };
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
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString137_literalMutationString16905 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:10]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString3_failAssert0_literalMutationString21716_failAssert0() throws IOException {
        try {
            {
                String template = "{{=toolyn}}" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
                String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "");
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
                org.junit.Assert.fail("testArrayIndexExtension_literalMutationString3 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString3_failAssert0_literalMutationString21716 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[template:1]", expected.getMessage());
        }
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

