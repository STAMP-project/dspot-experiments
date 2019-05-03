package com.github.mustachejava;


import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class AmplIssue75Test {
    @Test(timeout = 10000)
    public void testDotNotationWithNull_literalMutationString8_literalMutationString290_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            Mustache m = mf.compile(new StringReader("{{=toolyn}}"), "");
            StringWriter sw = new StringWriter();
            Map map = new HashMap();
            Object o_testDotNotationWithNull_literalMutationString8__10 = map.put("category", null);
            m.execute(sw, map).close();
            org.junit.Assert.fail("testDotNotationWithNull_literalMutationString8_literalMutationString290 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDotNotationWithNull_literalMutationString3_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            Mustache m = mf.compile(new StringReader("{{=toolyn}}"), "test");
            StringWriter sw = new StringWriter();
            Map map = new HashMap();
            map.put("category", null);
            m.execute(sw, map).close();
            org.junit.Assert.fail("testDotNotationWithNull_literalMutationString3 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDotNotationWithNull_literalMutationString3_failAssert0_add904_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                Map map = new HashMap();
                map.put("category", null);
                map.put("category", null);
                m.execute(sw, map).close();
                org.junit.Assert.fail("testDotNotationWithNull_literalMutationString3 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testDotNotationWithNull_literalMutationString3_failAssert0_add904 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDotNotationWithNull_literalMutationString3_failAssert0_literalMutationString585_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                Map map = new HashMap();
                map.put("?ategory", null);
                m.execute(sw, map).close();
                org.junit.Assert.fail("testDotNotationWithNull_literalMutationString3 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testDotNotationWithNull_literalMutationString3_failAssert0_literalMutationString585 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDotNotationWithNull_literalMutationString3_failAssert0null1039_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                Map map = new HashMap();
                map.put("category", null);
                m.execute(null, map).close();
                org.junit.Assert.fail("testDotNotationWithNull_literalMutationString3 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testDotNotationWithNull_literalMutationString3_failAssert0null1039 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDotNotationWithNull_literalMutationString9_literalMutationString356_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            Mustache m = mf.compile(new StringReader("{{=toolyn}}"), "{{=toolyn}}");
            StringWriter sw = new StringWriter();
            Map map = new HashMap();
            Object o_testDotNotationWithNull_literalMutationString9__10 = map.put("category", null);
            m.execute(sw, map).close();
            org.junit.Assert.fail("testDotNotationWithNull_literalMutationString9_literalMutationString356 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDotNotationWithNull_literalMutationString3_failAssert0_add905_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                Map map = new HashMap();
                map.put("category", null);
                m.execute(sw, map).close();
                m.execute(sw, map).close();
                org.junit.Assert.fail("testDotNotationWithNull_literalMutationString3 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testDotNotationWithNull_literalMutationString3_failAssert0_add905 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }
}

