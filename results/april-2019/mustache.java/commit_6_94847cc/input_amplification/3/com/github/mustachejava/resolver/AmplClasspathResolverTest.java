package com.github.mustachejava.resolver;


import java.io.Reader;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


public class AmplClasspathResolverTest {
    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1139_add1775() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1139__3 = underTest.getReader(" does not exist");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1139_add1775__8 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1139_add1775__8)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1139_add1775__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129_add1900() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("ML,h)#/J6*g|Qr|#^D[4]pprk2 i{");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129_add1900__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129_add1900__5)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129_add1900__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1127_add1888() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1127_add1888__3 = underTest.getReader("ML,h)#/J6*g|Qr|#^D[4]pprk2 i{");
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1127_add1888__3);
        Reader o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1127__3 = underTest.getReader("ML,h)#/J6*g|Qr|#^D[4]pprk2 i{");
        Reader reader = underTest.getReader("ML,h)#/J6*g|Qr|#^D[4]pprk2 i{");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1127_add1888__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("ML,h)#/J6*g|Qr|#^D[4]pprk2 i{");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129__5)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1127() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1127__3 = underTest.getReader("ML,h)#/J6*g|Qr|#^D[4]pprk2 i{");
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1127__3);
        Reader reader = underTest.getReader("ML,h)#/J6*g|Qr|#^D[4]pprk2 i{");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1127__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1128() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("ML,h)#/J6*g|Qr|#^D[4]pprk2 i{");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1128__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1128__5)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1128__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141_add1770() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141_add1770__8 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141_add1770__8)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141_add1770__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141_add1768() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141_add1768__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141_add1768__5)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141_add1768__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1127null2043_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver();
            Reader o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1127__3 = underTest.getReader("ML,h)#/J6*g|Qr|#^D[4]pprk2 i{");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1127null2043 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061__3 = underTest.getReader("sd[QQ#{ [a9ur_%sqx3.K&7x`lf*$");
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061__3);
        Reader reader = underTest.getReader("nested_partials_template.html");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_add1013__6 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1059null2047_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver();
            Reader o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1059__3 = underTest.getReader(" does not exist");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_add1013__6 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1059null2047 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1059() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1059__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1059__3);
        Reader reader = underTest.getReader("nested_partials_template.html");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_add1013__6 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1059__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129_add1899() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129_add1899__3 = underTest.getReader("ML,h)#/J6*g|Qr|#^D[4]pprk2 i{");
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129_add1899__3);
        Reader reader = underTest.getReader("ML,h)#/J6*g|Qr|#^D[4]pprk2 i{");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1129_add1899__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1140_add1762() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1140_add1762__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1140_add1762__5)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1140__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1140_add1762__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1140() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1140__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1140__5)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1140__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141__5)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1141__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1139() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1139__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1139__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1139__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061_add1939() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061__3 = underTest.getReader("sd[QQ#{ [a9ur_%sqx3.K&7x`lf*$");
        Reader reader = underTest.getReader("nested_partials_template.html");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061_add1939__8 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061_add1939__8)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_add1013__6 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061_add1939__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061_add1937() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061_add1937__3 = underTest.getReader("sd[QQ#{ [a9ur_%sqx3.K&7x`lf*$");
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061_add1937__3);
        Reader o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061__3 = underTest.getReader("sd[QQ#{ [a9ur_%sqx3.K&7x`lf*$");
        Reader reader = underTest.getReader("nested_partials_template.html");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_add1013__6 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061_add1937__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1009() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1059_add1907() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1059__3 = underTest.getReader(" does not exist");
        Reader reader = underTest.getReader("nested_partials_template.html");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1059_add1907__8 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1059_add1907__8)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_add1013__6 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1059_add1907__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1010() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("ML,h)#/J6*g|Qr|#^D[4]pprk2 i{");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_add1013null1155_failAssert0_literalMutationString1750_failAssert0() throws Exception {
        try {
            {
                ClasspathResolver underTest = new ClasspathResolver();
                underTest.getReader(" does not exist");
                Reader reader = underTest.getReader(null);
                Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_add1013__6 = Is.is(CoreMatchers.notNullValue());
                org.junit.Assert.fail("getReaderNullRootAndResourceHasRelativePath_add1013null1155 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("getReaderNullRootAndResourceHasRelativePath_add1013null1155_failAssert0_literalMutationString1750 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1139null2019_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver();
            Reader o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1139__3 = underTest.getReader(" does not exist");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1009__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderNullRootAndResourceHasRelativePath_literalMutationString1009_add1139null2019 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1128_add1893() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("ML,h)#/J6*g|Qr|#^D[4]pprk2 i{");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1128_add1893__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1128_add1893__5)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1128__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString1010_add1128_add1893__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061null2054_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver();
            Reader o_getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061__3 = underTest.getReader("sd[QQ#{ [a9ur_%sqx3.K&7x`lf*$");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_add1013__6 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderNullRootAndResourceHasRelativePath_add1013_literalMutationString1061null2054 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12697_add14310() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("46>Sr@WA>");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12697_add14310__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12697_add14310__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12697__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12697_add14310__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709_add14275() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709_add14275__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709_add14275__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709_add14275__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709_add14277() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709_add14277__9 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709_add14277__9)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709_add14277__9)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12418_add12685() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418_add12685__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418_add12685__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418_add12685__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12418_add12686() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418_add12686__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418_add12686__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418_add12686__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12418_add12684() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418_add12684__3 = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418_add12684__3);
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418_add12684__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696null14621_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("46>Sr@WA>");
            Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696__3 = underTest.getReader("absolute_partials_template.html");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696null14621 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12698() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("46>Sr@WA>");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12698__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12698__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12698__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12697() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("46>Sr@WA>");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12697__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12697__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12697__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("46>Sr@WA>");
        Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696__3 = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696__3);
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12425_add12689() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("O1yECWLP/w<D7u##[h}I Sx_b`PDvyw");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425_add12689__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425_add12689__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425_add12689__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12425_add12688() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425_add12688__3 = underTest.getReader("O1yECWLP/w<D7u##[h}I Sx_b`PDvyw");
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425_add12688__3);
        Reader reader = underTest.getReader("O1yECWLP/w<D7u##[h}I Sx_b`PDvyw");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425_add12688__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12425_add12690() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("O1yECWLP/w<D7u##[h}I Sx_b`PDvyw");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425_add12690__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425_add12690__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425_add12690__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12698_add14319() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("46>Sr@WA>");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12698__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12698_add14319__8 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12698_add14319__8)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12698_add14319__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12709__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708null14613_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708__3 = underTest.getReader(" does not exist");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708null14613 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12710() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12710__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12710__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12710__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708_add14280() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708_add14280__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708_add14280__3);
        Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708__3 = underTest.getReader(" does not exist");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708_add14280__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696_add14308() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("46>Sr@WA>");
        Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696__3 = underTest.getReader("absolute_partials_template.html");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696_add14308__8 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696_add14308__8)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696_add14308__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708_add14282() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708__3 = underTest.getReader(" does not exist");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708_add14282__8 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708_add14282__8)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422_add12708_add14282__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696_add14305() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("46>Sr@WA>");
        Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696_add14305__3 = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696_add14305__3);
        Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696__3 = underTest.getReader("absolute_partials_template.html");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415_add12696_add14305__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12418() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12418__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12415() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("46>Sr@WA>");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12415__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12422() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12422__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString12425() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("O1yECWLP/w<D7u##[h}I Sx_b`PDvyw");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString12425__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7238() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7238__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7238__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7238__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7237() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7237__3 = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7237__3);
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7237__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7239() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7239__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7239__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7239__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("&yWRL]RqQY:BSM(!Edvw)(b15TdCW8D");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230_add8815() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("&yWRL]RqQY:BSM(!Edvw)(b15TdCW8D");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230_add8815__9 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230_add8815__9)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230_add8815__9)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230_add8814() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("&yWRL]RqQY:BSM(!Edvw)(b15TdCW8D");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230_add8814__9 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230_add8814__9)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230_add8814__9)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7229null9170_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7229__3 = underTest.getReader("&yWRL]RqQY:BSM(!Edvw)(b15TdCW8D");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7229null9170 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7262() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7262__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7262__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7262__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7261() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7261__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7261__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7261__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7263() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7263__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7263__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7263__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7231() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("&yWRL]RqQY:BSM(!Edvw)(b15TdCW8D");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7231__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7231__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7231__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("&yWRL]RqQY:BSM(!Edvw)(b15TdCW8D");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7230__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7229() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7229__3 = underTest.getReader("&yWRL]RqQY:BSM(!Edvw)(b15TdCW8D");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7229__3);
        Reader reader = underTest.getReader("&yWRL]RqQY:BSM(!Edvw)(b15TdCW8D");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7229__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7237null9173_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver(" does not exist");
            Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7237__3 = underTest.getReader("absolute_partials_template.html");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7237null9173 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7261null9130_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7261__3 = underTest.getReader(" does not exist");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6955_add7261null9130 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add6960null7271_failAssert0_literalMutationString8628_failAssert0() throws Exception {
        try {
            {
                ClasspathResolver underTest = new ClasspathResolver("Djd-^HkPvM");
                Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add6960__3 = underTest.getReader("absolute_partials_template.html");
                Reader reader = underTest.getReader(null);
                Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add6960__6 = Is.is(CoreMatchers.notNullValue());
                org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add6960null7271 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add6960null7271_failAssert0_literalMutationString8628 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7237_add8951() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7237_add8951__3 = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7237_add8951__3);
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7237__3 = underTest.getReader("absolute_partials_template.html");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6949_add7237_add8951__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7231_add8817() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7231_add8817__3 = underTest.getReader("&yWRL]RqQY:BSM(!Edvw)(b15TdCW8D");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7231_add8817__3);
        Reader reader = underTest.getReader("&yWRL]RqQY:BSM(!Edvw)(b15TdCW8D");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7231__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7231_add8817__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7229_add8944() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7229__3 = underTest.getReader("&yWRL]RqQY:BSM(!Edvw)(b15TdCW8D");
        Reader reader = underTest.getReader("&yWRL]RqQY:BSM(!Edvw)(b15TdCW8D");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7229_add8944__8 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7229_add8944__8)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString6958_add7229_add8944__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("hRA`2iiYnzgdO?i+w7X?p!$aIF+D[U3V");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" -v8@*V{p");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947null11908_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver(" does not exist");
            Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947__3 = underTest.getReader("/absolute_partials_template.html");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947null11908 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9991_add11598() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" -v8@*V{p");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9991__3 = underTest.getReader("/absolute_partials_template.html");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9991_add11598__6 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9991_add11598__6);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9991_add11598__6);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9979null11873_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9979__3 = underTest.getReader("hRA`2iiYnzgdO?i+w7X?p!$aIF+D[U3V");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9979null11873 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9991null11884_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver(" -v8@*V{p");
            Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9991__3 = underTest.getReader("/absolute_partials_template.html");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9991null11884 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9979_add11553() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9979__3 = underTest.getReader("hRA`2iiYnzgdO?i+w7X?p!$aIF+D[U3V");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9979_add11553__6 = underTest.getReader("hRA`2iiYnzgdO?i+w7X?p!$aIF+D[U3V");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9979_add11553__6);
        Reader reader = underTest.getReader("hRA`2iiYnzgdO?i+w7X?p!$aIF+D[U3V");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9979_add11553__6);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9993_add11693() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" -v8@*V{p");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9993_add11693__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9993_add11693__3);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9993__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9993_add11693__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687_add9985() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687_add9985__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687_add9985__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687_add9985__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687_add9984() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687_add9984__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687_add9984__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687_add9984__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687_add9983() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687_add9983__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687_add9983__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9687_add9983__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947_add11690() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947__3 = underTest.getReader("/absolute_partials_template.html");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947_add11690__6 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947_add11690__6);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947_add11690__6);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947_add11689() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947_add11689__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947_add11689__3);
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947__3 = underTest.getReader("/absolute_partials_template.html");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947_add11689__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947__3);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9947__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948_add11581() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948_add11581__9 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948_add11581__9)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948_add11581__9)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948_add11577() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948_add11577__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948_add11577__3);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9948_add11577__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9993() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" -v8@*V{p");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9993__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9993__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9993__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9992() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" -v8@*V{p");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9992__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9992__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9992__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9991() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" -v8@*V{p");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9991__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9991__3);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9991__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9992_add11591() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" -v8@*V{p");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9992_add11591__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9992_add11591__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9992__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9682_add9992_add11591__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9979() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9979__3 = underTest.getReader("hRA`2iiYnzgdO?i+w7X?p!$aIF+D[U3V");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9979__3);
        Reader reader = underTest.getReader("hRA`2iiYnzgdO?i+w7X?p!$aIF+D[U3V");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9979__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9980() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("hRA`2iiYnzgdO?i+w7X?p!$aIF+D[U3V");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9980__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9980__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9980__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9981() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("hRA`2iiYnzgdO?i+w7X?p!$aIF+D[U3V");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9981__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9981__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9689_add9981__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949_add11587() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949_add11587__8 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949_add11587__8)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949_add11587__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949_add11585() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949_add11585__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949_add11585__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString9681_add9949_add11585__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("Ekbd}c5c*F");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("Ekbd}c5c*F");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440__3);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("Ekbd}c5c*F");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4446() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4446__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4446__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4446__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4445() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4445__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4445__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4445__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4444() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4444__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4444__3);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4444__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4470_add6166() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4470__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4470_add6166__8 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4470_add6166__8)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4470_add6166__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442_add6048() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("Ekbd}c5c*F");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442_add6048__8 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442_add6048__8)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442_add6048__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442_add6049() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("Ekbd}c5c*F");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442_add6049__8 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442_add6049__8)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4442_add6049__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4436null6377_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4436__3 = underTest.getReader("}4e+yR#p/agA[!nA>#1}6YCA[|)_#S|;");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4436null6377 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4436() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4436__3 = underTest.getReader("}4e+yR#p/agA[!nA>#1}6YCA[|)_#S|;");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4436__3);
        Reader reader = underTest.getReader("}4e+yR#p/agA[!nA>#1}6YCA[|)_#S|;");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4436__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4437() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("}4e+yR#p/agA[!nA>#1}6YCA[|)_#S|;");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4437__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4437__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4437__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4438() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("}4e+yR#p/agA[!nA>#1}6YCA[|)_#S|;");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4438__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4438__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169_add4438__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4470() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4470__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4470__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4470__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4469() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4469__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4469__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4469__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468null6349_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468__3 = underTest.getReader(" does not exist");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468null6349 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441_add6055() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("Ekbd}c5c*F");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441_add6055__9 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441_add6055__9)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441_add6055__9)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441_add6053() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("Ekbd}c5c*F");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441_add6053__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441_add6053__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4441_add6053__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("}4e+yR#p/agA[!nA>#1}6YCA[|)_#S|;");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4169__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("Ekbd}c5c*F");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templ>tes/");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440null6359_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("Ekbd}c5c*F");
            Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440__3 = underTest.getReader("/absolute_partials_template.html");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440null6359 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4452null6390_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templ>tes/");
            Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4452__3 = underTest.getReader("/absolute_partials_template.html");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4452null6390 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4445_add6113() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4445_add6113__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4445_add6113__3);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4445__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4445_add6113__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468_add6012() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468__3 = underTest.getReader(" does not exist");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468_add6012__8 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468_add6012__8)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468_add6012__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468_add6010() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468_add6010__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468_add6010__3);
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468__3 = underTest.getReader(" does not exist");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4168_add4468_add6010__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4444_add6202() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4444_add6202__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4444_add6202__3);
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4444__3 = underTest.getReader("/absolute_partials_template.html");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4444_add6202__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440_add6060() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("Ekbd}c5c*F");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440__3 = underTest.getReader("/absolute_partials_template.html");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440_add6060__8 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440_add6060__8)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440_add6060__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4452() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templ>tes/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4452__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4452__3);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4452__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4453() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templ>tes/");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4453__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4453__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4453__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4454() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templ>tes/");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4454__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4454__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4454__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4453_add6070() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templ>tes/");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4453_add6070__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4453_add6070__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4453__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4453_add6070__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440_add6059() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("Ekbd}c5c*F");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440__3 = underTest.getReader("/absolute_partials_template.html");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440_add6059__6 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440_add6059__6);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4163_add4440_add6059__6);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4444null6395_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver(" does not exist");
            Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4444__3 = underTest.getReader("/absolute_partials_template.html");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4160_add4444null6395 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4452_add6179() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templ>tes/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4452_add6179__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4452_add6179__3);
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4452__3 = underTest.getReader("/absolute_partials_template.html");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString4164_add4452_add6179__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_add2592null2731_failAssert0_literalMutationString3324_failAssert0() throws Exception {
        try {
            {
                ClasspathResolver underTest = new ClasspathResolver();
                Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_add2592__3 = underTest.getReader(" does not exist");
                Reader reader = underTest.getReader(null);
                Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_add2592__6 = Is.is(CoreMatchers.nullValue());
                org.junit.Assert.fail("getReaderNullRootDoesNotFindFileWithAbsolutePath_add2592null2731 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("getReaderNullRootDoesNotFindFileWithAbsolutePath_add2592null2731_failAssert0_literalMutationString3324 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710null3618_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver();
            Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710__3 = underTest.getReader(" does not exist");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588__5 = Is.is(CoreMatchers.nullValue());
            org.junit.Assert.fail("getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710null3618 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718null3609_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver();
            Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718__3 = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590__5 = Is.is(CoreMatchers.nullValue());
            org.junit.Assert.fail("getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718null3609 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711_add3443() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711_add3443__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711_add3443__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711__5 = Is.is(CoreMatchers.nullValue());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711_add3443__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711_add3448() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711__5 = Is.is(CoreMatchers.nullValue());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711_add3448__9 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711_add3448__9)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711_add3448__9)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2711__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712__5 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718_add3402() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718__3 = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Reader reader = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718_add3402__8 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718_add3402__8)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718_add3402__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718_add3400() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718__3 = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718_add3400__6 = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718_add3400__6);
        Reader reader = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718_add3400__6);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710_add3439() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710_add3439__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710_add3439__3);
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710__3 = underTest.getReader(" does not exist");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710_add3439__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710_add3442() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710__3 = underTest.getReader(" does not exist");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710_add3442__8 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710_add3442__8)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2710_add3442__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718__3 = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718__3);
        Reader reader = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2718__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2720() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2720__5 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2720__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2720__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712_add3434() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712_add3434__5 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712_add3434__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712__5 = CoreMatchers.nullValue();
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712_add3434__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712_add3436() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712__5 = CoreMatchers.nullValue();
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712_add3436__8 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712_add3436__8)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2588_add2712_add3436__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719_add3408() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719__5 = Is.is(CoreMatchers.nullValue());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719_add3408__9 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719_add3408__9)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719_add3408__9)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719_add3403() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719_add3403__3 = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719_add3403__3);
        Reader reader = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719__5 = Is.is(CoreMatchers.nullValue());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2719_add3403__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2720_add3410() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2720_add3410__3 = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2720_add3410__3);
        Reader reader = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2720__5 = CoreMatchers.nullValue();
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590_add2720_add3410__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("R`Wg$DE86sA1(?i#g$m`b(A+y!lggX");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString2590__5)).toString());
        Assert.assertNull(reader);
    }
}

