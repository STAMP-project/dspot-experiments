package com.github.mustachejava;


import com.github.mustachejava.codes.DefaultMustache;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AmplAbstractClassTest {
    abstract static class AbstractFoo {
        public abstract String getValue();
    }

    static class Foo extends AmplAbstractClassTest.AbstractFoo {
        @Override
        public String getValue() {
            return "I am Foo";
        }
    }

    static class Bar extends AmplAbstractClassTest.AbstractFoo {
        @Override
        public String getValue() {
            return "I am Bar";
        }
    }

    static class Container {
        public final AmplAbstractClassTest.AbstractFoo foo;

        public Container(final AmplAbstractClassTest.AbstractFoo foo) {
            this.foo = foo;
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString3_failAssert0() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value},} {{/containers}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClasslitString3 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to close \'containers\' tag @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString7() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClasslitString7__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClasslitString7__3);
        boolean o_testAbstractClasslitString7__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClasslitString7__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("\n"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString5() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClasslitString5__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClasslitString5__3);
        boolean o_testAbstractClasslitString5__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClasslitString5__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("dhscbCS@!x*zH_,y(q2 5[gpbL[{$QV5:Wz2[|+mr6#-V"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString6() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClasslitString6__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClasslitString6__3);
        boolean o_testAbstractClasslitString6__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClasslitString6__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader(""), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString4() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClasslitString4__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClasslitString4__3);
        boolean o_testAbstractClasslitString4__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClasslitString4__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.valu}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv48() throws IOException, Exception {
        char[] __DSPOT_arg0_5 = new char[]{ 'S', 'O' };
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClass_rv48__4 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClass_rv48__4);
        boolean o_testAbstractClass_rv48__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClass_rv48__7);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
        writer.flush();
        __DSPOT_invoc_19.write(__DSPOT_arg0_5);
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString18() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClasslitString18__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClasslitString18__3);
        boolean o_testAbstractClasslitString18__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClasslitString18__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("c=ntainers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv40_failAssert4litString700() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_rv40_failAssert4litString700__5 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClass_rv40_failAssert4litString700__5);
            boolean o_testAbstractClass_rv40_failAssert4litString700__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClass_rv40_failAssert4litString700__8);
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.valueU}} {{/containers}}"), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_18 = scopes.put("containers", containers);
            Writer o_testAbstractClass_rv40_failAssert4litString700__23 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClass_rv40_failAssert4litString700__23)).getEncoding());
            writer.flush();
            __DSPOT_invoc_18.notify();
            org.junit.Assert.fail("testAbstractClass_rv40 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv39_failAssert3litString674() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_rv39_failAssert3litString674__5 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClass_rv39_failAssert3litString674__5);
            boolean o_testAbstractClass_rv39_failAssert3litString674__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClass_rv39_failAssert3litString674__8);
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_18 = scopes.put("", containers);
            Writer o_testAbstractClass_rv39_failAssert3litString674__23 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClass_rv39_failAssert3litString674__23)).getEncoding());
            writer.flush();
            __DSPOT_invoc_18.hashCode();
            org.junit.Assert.fail("testAbstractClass_rv39 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv40_failAssert4litString698_failAssert23() throws IOException, Exception {
        try {
            try {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{#conta?ners}} {{foo.value}} {{/containers}}"), "example");
                Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_18.notify();
                org.junit.Assert.fail("testAbstractClass_rv40 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClass_rv40_failAssert4litString698 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
            Assert.assertEquals("Mismatched start/end tags: conta?ners != containers in example:1 @[example:1]", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv38_failAssert2litString824_failAssert26() throws IOException, Exception {
        try {
            try {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}J {{/containers}}"), "example");
                Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_18.getClass();
                org.junit.Assert.fail("testAbstractClass_rv38 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClass_rv38_failAssert2litString824 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
            Assert.assertEquals("Failed to close \'containers\' tag @[example:1]", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv39_failAssert3litString537() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_rv39_failAssert3litString537__5 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClass_rv39_failAssert3litString537__5);
            boolean o_testAbstractClass_rv39_failAssert3litString537__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClass_rv39_failAssert3litString537__8);
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Mustache mustache = mf.compile(new StringReader(""), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_18 = scopes.put("containers", containers);
            Writer o_testAbstractClass_rv39_failAssert3litString537__23 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClass_rv39_failAssert3litString537__23)).getEncoding());
            writer.flush();
            __DSPOT_invoc_18.hashCode();
            org.junit.Assert.fail("testAbstractClass_rv39 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv40_failAssert4litString708() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_rv40_failAssert4litString708__5 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClass_rv40_failAssert4litString708__5);
            boolean o_testAbstractClass_rv40_failAssert4litString708__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClass_rv40_failAssert4litString708__8);
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Mustache mustache = mf.compile(new StringReader("1%h+1!kAF:15u&sdcOgKS{qxxjff`y&R/x5,;cXLFumzT"), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_18 = scopes.put("containers", containers);
            Writer o_testAbstractClass_rv40_failAssert4litString708__23 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClass_rv40_failAssert4litString708__23)).getEncoding());
            writer.flush();
            __DSPOT_invoc_18.notify();
            org.junit.Assert.fail("testAbstractClass_rv40 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString3_failAssert0_add1532() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClasslitString3_failAssert0_add1532__5 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClasslitString3_failAssert0_add1532__5);
            boolean o_testAbstractClasslitString3_failAssert0_add1532__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClasslitString3_failAssert0_add1532__8);
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value},} {{/containers}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClasslitString3 should have thrown MustacheException");
        } catch (MustacheException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_remove33litString233() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClass_remove33__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClass_remove33__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("\n"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        Writer o_testAbstractClass_remove33__18 = mustache.execute(writer, scopes);
        writer.flush();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
    }

    @Test(timeout = 10000)
    public void testAbstractClass_add25_rv1121() throws IOException, Exception {
        int __DSPOT_arg0_200 = 1501056279;
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClass_add25__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClass_add25__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClass_add25__9 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        Writer __DSPOT_invoc_46 = mustache.execute(writer, scopes);
        writer.flush();
        __DSPOT_invoc_46.write(__DSPOT_arg0_200);
    }

    @Test(timeout = 10000)
    public void testAbstractClass_add26_rv2271_failAssert18litString7692() throws IOException, Exception {
        try {
            long __DSPOT_arg0_432 = -1206605329L;
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_add26__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_add26__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            boolean o_testAbstractClass_add26__9 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_45 = scopes.put("contJainers", containers);
            Writer o_testAbstractClass_add26_rv2271_failAssert18litString7692__33 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClass_add26_rv2271_failAssert18litString7692__33)).getEncoding());
            writer.flush();
            __DSPOT_invoc_45.wait(__DSPOT_arg0_432);
            org.junit.Assert.fail("testAbstractClass_add26_rv2271 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv38_failAssert2litString824_failAssert26_add7641() throws IOException, Exception {
        try {
            try {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                boolean o_testAbstractClass_rv38_failAssert2litString824_failAssert26_add7641__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                Assert.assertTrue(o_testAbstractClass_rv38_failAssert2litString824_failAssert26_add7641__7);
                boolean o_testAbstractClass_rv38_failAssert2litString824_failAssert26_add7641__10 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                Assert.assertTrue(o_testAbstractClass_rv38_failAssert2litString824_failAssert26_add7641__10);
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
                Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
                Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}J {{/containers}}"), "example");
                Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_18.getClass();
                org.junit.Assert.fail("testAbstractClass_rv38 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClass_rv38_failAssert2litString824 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_add25_rv1051_failAssert19litString7843_failAssert30() throws IOException, Exception {
        try {
            try {
                long __DSPOT_arg0_174 = 1474904529L;
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                boolean o_testAbstractClass_add25__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                boolean o_testAbstractClass_add25__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                boolean o_testAbstractClass_add25__9 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{containers}}"), "example");
                Object __DSPOT_invoc_45 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_45.wait(__DSPOT_arg0_174);
                org.junit.Assert.fail("testAbstractClass_add25_rv1051 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClass_add25_rv1051_failAssert19litString7843 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
            Assert.assertEquals("Failed to close \'containers\' tag @[example:1]", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv39_failAssert3litString519_failAssert27_add8455() throws IOException, Exception {
        try {
            try {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                boolean o_testAbstractClass_rv39_failAssert3litString519_failAssert27_add8455__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                Assert.assertTrue(o_testAbstractClass_rv39_failAssert3litString519_failAssert27_add8455__7);
                boolean o_testAbstractClass_rv39_failAssert3litString519_failAssert27_add8455__10 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                Assert.assertTrue(o_testAbstractClass_rv39_failAssert3litString519_failAssert27_add8455__10);
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
                Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
                Mustache mustache = mf.compile(new StringReader("{d#containers}} {{foo.value}} {{/containers}}"), "example");
                Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_18.hashCode();
                org.junit.Assert.fail("testAbstractClass_rv39 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClass_rv39_failAssert3litString519 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_add25_rv1051_failAssert19litString7840() throws IOException, Exception {
        try {
            long __DSPOT_arg0_174 = 1474904529L;
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_add25__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_add25__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_add25__9 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{fooS.value}} {{/containers}}"), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_45 = scopes.put("containers", containers);
            Writer o_testAbstractClass_add25_rv1051_failAssert19litString7840__33 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClass_add25_rv1051_failAssert19litString7840__33)).getEncoding());
            writer.flush();
            __DSPOT_invoc_45.wait(__DSPOT_arg0_174);
            org.junit.Assert.fail("testAbstractClass_add25_rv1051 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_add26_rv2278_failAssert15litString6788() throws IOException, Exception {
        try {
            int __DSPOT_arg1_434 = -568544798;
            long __DSPOT_arg0_433 = 755771935L;
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_add26__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_add26__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            boolean o_testAbstractClass_add26__9 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Mustache mustache = mf.compile(new StringReader(""), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_45 = scopes.put("containers", containers);
            Writer o_testAbstractClass_add26_rv2278_failAssert15litString6788__34 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClass_add26_rv2278_failAssert15litString6788__34)).getEncoding());
            writer.flush();
            __DSPOT_invoc_45.wait(__DSPOT_arg0_433, __DSPOT_arg1_434);
            org.junit.Assert.fail("testAbstractClass_add26_rv2278 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_add26_rv2271_failAssert18litString7591_failAssert31() throws IOException, Exception {
        try {
            try {
                long __DSPOT_arg0_432 = -1206605329L;
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                boolean o_testAbstractClass_add26__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                boolean o_testAbstractClass_add26__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                boolean o_testAbstractClass_add26__9 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/con[ainers}}"), "example");
                Object __DSPOT_invoc_45 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_45.wait(__DSPOT_arg0_432);
                org.junit.Assert.fail("testAbstractClass_add26_rv2271 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClass_add26_rv2271_failAssert18litString7591 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
            Assert.assertEquals("Mismatched start/end tags: containers != con[ainers in example:1 @[example:1]", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_remove33litString209_failAssert11_add6214() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_remove33__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_remove33__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Mustache mustache = mf.compile(new StringReader("{{#contaiers}} {{foo.value}} {{/containers}}"), "example");
            Writer o_testAbstractClass_remove33__18 = mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_remove33litString209 should have thrown MustacheException");
        } catch (MustacheException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDots__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClassNoDots__3);
        boolean o_testAbstractClassNoDots__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClassNoDots__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString9864() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString9864__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString9864__3);
        boolean o_testAbstractClassNoDotslitString9864__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString9864__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{vavue}}{{/foo}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString9866_failAssert33() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/container}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClassNoDotslitString9866 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Mismatched start/end tags: containers != container in example:1 @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString9867() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString9867__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString9867__3);
        boolean o_testAbstractClassNoDotslitString9867__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString9867__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("I9#vkmfME>aRSP)FA#>?M^UK4{)N+I+fnw?S(tFW`+X#,b)VTA]wEe*g0"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv9912() throws IOException, Exception {
        String __DSPOT_arg0_1016 = "on(N?n`#+^K`mli}GQ>l";
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDots_rv9912__4 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClassNoDots_rv9912__4);
        boolean o_testAbstractClassNoDots_rv9912__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClassNoDots_rv9912__7);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
        writer.flush();
        __DSPOT_invoc_19.write(__DSPOT_arg0_1016);
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString9868() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString9868__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString9868__3);
        boolean o_testAbstractClassNoDotslitString9868__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString9868__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader(""), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString9879() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString9879__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString9879__3);
        boolean o_testAbstractClassNoDotslitString9879__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString9879__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString9869() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString9869__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString9869__3);
        boolean o_testAbstractClassNoDotslitString9869__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString9869__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("\n"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv9901_failAssert36litString11352() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassNoDots_rv9901_failAssert36litString11352__5 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClassNoDots_rv9901_failAssert36litString11352__5);
            boolean o_testAbstractClassNoDots_rv9901_failAssert36litString11352__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClassNoDots_rv9901_failAssert36litString11352__8);
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_18 = scopes.put("contai,ers", containers);
            Writer o_testAbstractClassNoDots_rv9901_failAssert36litString11352__23 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClassNoDots_rv9901_failAssert36litString11352__23)).getEncoding());
            writer.flush();
            __DSPOT_invoc_18.hashCode();
            org.junit.Assert.fail("testAbstractClassNoDots_rv9901 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_add9888_rv10754() throws IOException, Exception {
        char[] __DSPOT_arg0_1167 = new char[]{ 'g', '-' };
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDots_add9888__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassNoDots_add9888__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        boolean o_testAbstractClassNoDots_add9888__9 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        Writer __DSPOT_invoc_46 = mustache.execute(writer, scopes);
        writer.flush();
        __DSPOT_invoc_46.write(__DSPOT_arg0_1167);
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_remove9895litString10711() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDots_remove9895__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassNoDots_remove9895__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("c41e)EJ<P^8pe[GPgVCdUE<=^JJty`jl[>{YeIijo*2M##hqV}oiplPbd"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        Writer o_testAbstractClassNoDots_remove9895__18 = mustache.execute(writer, scopes);
        writer.flush();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv9901_failAssert36litString11300() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassNoDots_rv9901_failAssert36litString11300__5 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClassNoDots_rv9901_failAssert36litString11300__5);
            boolean o_testAbstractClassNoDots_rv9901_failAssert36litString11300__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClassNoDots_rv9901_failAssert36litString11300__8);
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Mustache mustache = mf.compile(new StringReader(""), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_18 = scopes.put("containers", containers);
            Writer o_testAbstractClassNoDots_rv9901_failAssert36litString11300__23 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClassNoDots_rv9901_failAssert36litString11300__23)).getEncoding());
            writer.flush();
            __DSPOT_invoc_18.hashCode();
            org.junit.Assert.fail("testAbstractClassNoDots_rv9901 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv9905_failAssert40litString11618_failAssert56() throws IOException, Exception {
        try {
            try {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} y{/containers}}"), "example");
                Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_18.wait();
                org.junit.Assert.fail("testAbstractClassNoDots_rv9905 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClassNoDots_rv9905_failAssert40litString11618 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
            Assert.assertEquals("Failed to close \'containers\' tag @[example:1]", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv9902_failAssert37litString10624_failAssert57() throws IOException, Exception {
        try {
            try {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{#containers} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
                Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_18.notify();
                org.junit.Assert.fail("testAbstractClassNoDots_rv9902 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClassNoDots_rv9902_failAssert37litString10624 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
            Assert.assertEquals("Mismatched start/end tags: containers} {{#foo != foo in example:1 @[example:1]", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_remove9895litString10720() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDots_remove9895__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassNoDots_remove9895__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("\n"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        Writer o_testAbstractClassNoDots_remove9895__18 = mustache.execute(writer, scopes);
        writer.flush();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv9901_failAssert36litString11288_failAssert64() throws IOException, Exception {
        try {
            try {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo3}}{{value}}{{/foo}} {{/containers}}"), "example");
                Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_18.hashCode();
                org.junit.Assert.fail("testAbstractClassNoDots_rv9901 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClassNoDots_rv9901_failAssert36litString11288 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
            Assert.assertEquals("Mismatched start/end tags: foo3 != foo in example:1 @[example:1]", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv9901_failAssert36litString11292() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassNoDots_rv9901_failAssert36litString11292__5 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClassNoDots_rv9901_failAssert36litString11292__5);
            boolean o_testAbstractClassNoDots_rv9901_failAssert36litString11292__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClassNoDots_rv9901_failAssert36litString11292__8);
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{alue}}{{/foo}} {{/containers}}"), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_18 = scopes.put("containers", containers);
            Writer o_testAbstractClassNoDots_rv9901_failAssert36litString11292__23 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClassNoDots_rv9901_failAssert36litString11292__23)).getEncoding());
            writer.flush();
            __DSPOT_invoc_18.hashCode();
            org.junit.Assert.fail("testAbstractClassNoDots_rv9901 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv9905_failAssert40litString11618_failAssert56litString17466() throws IOException, Exception {
        try {
            try {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                boolean o_testAbstractClassNoDots_rv9905_failAssert40litString11618_failAssert56litString17466__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                Assert.assertTrue(o_testAbstractClassNoDots_rv9905_failAssert40litString11618_failAssert56litString17466__7);
                boolean o_testAbstractClassNoDots_rv9905_failAssert40litString11618_failAssert56litString17466__10 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                Assert.assertTrue(o_testAbstractClassNoDots_rv9905_failAssert40litString11618_failAssert56litString17466__10);
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
                Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
                Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} y{/containers}}"), "ex;ample");
                Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_18.wait();
                org.junit.Assert.fail("testAbstractClassNoDots_rv9905 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClassNoDots_rv9905_failAssert40litString11618 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv9902_failAssert37litString10624_failAssert57litString17767() throws IOException, Exception {
        try {
            try {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                boolean o_testAbstractClassNoDots_rv9902_failAssert37litString10624_failAssert57litString17767__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                Assert.assertTrue(o_testAbstractClassNoDots_rv9902_failAssert37litString10624_failAssert57litString17767__7);
                boolean o_testAbstractClassNoDots_rv9902_failAssert37litString10624_failAssert57litString17767__10 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                Assert.assertTrue(o_testAbstractClassNoDots_rv9902_failAssert37litString10624_failAssert57litString17767__10);
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
                Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
                Mustache mustache = mf.compile(new StringReader("{{#containers} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
                Object __DSPOT_invoc_18 = scopes.put("", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_18.notify();
                org.junit.Assert.fail("testAbstractClassNoDots_rv9902 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClassNoDots_rv9902_failAssert37litString10624 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv9904_failAssert39litString10365_failAssert60_add18914() throws IOException, Exception {
        try {
            try {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                boolean o_testAbstractClassNoDots_rv9904_failAssert39litString10365_failAssert60_add18914__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                Assert.assertTrue(o_testAbstractClassNoDots_rv9904_failAssert39litString10365_failAssert60_add18914__7);
                boolean o_testAbstractClassNoDots_rv9904_failAssert39litString10365_failAssert60_add18914__10 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                Assert.assertTrue(o_testAbstractClassNoDots_rv9904_failAssert39litString10365_failAssert60_add18914__10);
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
                Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
                Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}{{/foo}} {{/containers}}"), "example");
                Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_18.toString();
                org.junit.Assert.fail("testAbstractClassNoDots_rv9904 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClassNoDots_rv9904_failAssert39litString10365 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv9903_failAssert38litString10119_failAssert65litString17239() throws IOException, Exception {
        try {
            try {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                boolean o_testAbstractClassNoDots_rv9903_failAssert38litString10119_failAssert65litString17239__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                Assert.assertTrue(o_testAbstractClassNoDots_rv9903_failAssert38litString10119_failAssert65litString17239__7);
                boolean o_testAbstractClassNoDots_rv9903_failAssert38litString10119_failAssert65litString17239__10 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                Assert.assertTrue(o_testAbstractClassNoDots_rv9903_failAssert38litString10119_failAssert65litString17239__10);
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
                Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
                Mustache mustache = mf.compile(new StringReader("{!#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "");
                Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_18.notifyAll();
                org.junit.Assert.fail("testAbstractClassNoDots_rv9903 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClassNoDots_rv9903_failAssert38litString10119 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_add9888_rv10719_failAssert50litString13232() throws IOException, Exception {
        try {
            int __DSPOT_arg1_1163 = 1002841987;
            long __DSPOT_arg0_1162 = -1987786446L;
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassNoDots_add9888__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClassNoDots_add9888__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            boolean o_testAbstractClassNoDots_add9888__9 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Mustache mustache = mf.compile(new StringReader(""), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_45 = scopes.put("containers", containers);
            Writer o_testAbstractClassNoDots_add9888_rv10719_failAssert50litString13232__34 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClassNoDots_add9888_rv10719_failAssert50litString13232__34)).getEncoding());
            writer.flush();
            __DSPOT_invoc_45.wait(__DSPOT_arg0_1162, __DSPOT_arg1_1163);
            org.junit.Assert.fail("testAbstractClassNoDots_add9888_rv10719 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }
}

