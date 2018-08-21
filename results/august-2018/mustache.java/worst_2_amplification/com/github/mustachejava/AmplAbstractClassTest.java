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
    static abstract class AbstractFoo {
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
    public void testAbstractClassnull55_failAssert10() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(null, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClassnull55 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
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
    public void testAbstractClass_remove33() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClass_remove33__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClass_remove33__3);
        boolean o_testAbstractClass_remove33__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClass_remove33__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        Writer o_testAbstractClass_remove33__18 = mustache.execute(writer, scopes);
        Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClass_remove33__18)).getEncoding());
        writer.flush();
        Assert.assertTrue(o_testAbstractClass_remove33__3);
        Assert.assertTrue(o_testAbstractClass_remove33__6);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClass_remove33__18)).getEncoding());
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString17() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClasslitString17__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClasslitString17__3);
        boolean o_testAbstractClasslitString17__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClasslitString17__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("I am Foo", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString2() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClasslitString2__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClasslitString2__3);
        boolean o_testAbstractClasslitString2__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClasslitString2__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.valke}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString3_failAssert11() throws IOException, Exception {
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
    public void testAbstractClassnull54() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassnull54__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClassnull54__3);
        boolean o_testAbstractClassnull54__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClassnull54__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", null);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv50_remove1695() throws IOException, Exception {
        String __DSPOT_arg0_7 = "^FT)-ef&bk*201yCi*Od";
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClass_rv50__4 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClass_rv50__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
        writer.flush();
        __DSPOT_invoc_19.write(__DSPOT_arg0_7);
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
    }

    @Test(timeout = 10000)
    public void testAbstractClassnull52null2634() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassnull52__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassnull52__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), null);
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertNull(((DefaultMustache) (mustache)).getName());
        scopes.put("containers", null);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv37_failAssert2litString870() throws IOException, Exception {
        try {
            Object __DSPOT_arg0_0 = new Object();
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_rv37_failAssert2litString870__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClass_rv37_failAssert2litString870__7);
            boolean o_testAbstractClass_rv37_failAssert2litString870__10 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClass_rv37_failAssert2litString870__10);
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{wfoo.value}} {{/containers}}"), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_18 = scopes.put("containers", containers);
            Writer o_testAbstractClass_rv37_failAssert2litString870__25 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClass_rv37_failAssert2litString870__25)).getEncoding());
            writer.flush();
            __DSPOT_invoc_18.equals(__DSPOT_arg0_0);
            org.junit.Assert.fail("testAbstractClass_rv37 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_add29null2576_failAssert33() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_add29__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_add29__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            mustache.execute(null, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_add29null2576 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassnull52null2635_failAssert23() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassnull52__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClassnull52__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), null);
            scopes.put("containers", containers);
            mustache.execute(null, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClassnull52null2635 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv41_failAssert6litString969() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_rv41_failAssert6litString969__5 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClass_rv41_failAssert6litString969__5);
            boolean o_testAbstractClass_rv41_failAssert6litString969__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClass_rv41_failAssert6litString969__8);
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Mustache mustache = mf.compile(new StringReader(""), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_18 = scopes.put("containers", containers);
            Writer o_testAbstractClass_rv41_failAssert6litString969__23 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClass_rv41_failAssert6litString969__23)).getEncoding());
            writer.flush();
            __DSPOT_invoc_18.notifyAll();
            org.junit.Assert.fail("testAbstractClass_rv41 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString3_failAssert11_add1658() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClasslitString3_failAssert11_add1658__5 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClasslitString3_failAssert11_add1658__5);
            boolean o_testAbstractClasslitString3_failAssert11_add1658__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClasslitString3_failAssert11_add1658__8);
            boolean o_testAbstractClasslitString3_failAssert11_add1658__11 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClasslitString3_failAssert11_add1658__11);
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value},} {{/containers}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClasslitString3 should have thrown MustacheException");
        } catch (MustacheException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_add29litString208_failAssert35() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_add29__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_add29__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/contaiers}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_add29litString208 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Mismatched start/end tags: containers != contaiers in example:1 @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassnull53_add1288() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassnull53__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassnull53__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put(null, containers);
        mustache.execute(writer, scopes);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClassnull54null2631() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassnull54__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassnull54__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", null);
        mustache.execute(null, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv38_failAssert4litString919_failAssert25() throws IOException, Exception {
        try {
            try {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value} {{/containers}}"), "example");
                Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_18.getClass();
                org.junit.Assert.fail("testAbstractClass_rv38 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClass_rv38_failAssert4litString919 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
            Assert.assertEquals("Failed to close \'containers\' tag @[example:1]", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString20_rv2112_failAssert16() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClasslitString20__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClasslitString20__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
            Object __DSPOT_invoc_38 = scopes.put("conainers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            __DSPOT_invoc_38.wait();
            org.junit.Assert.fail("testAbstractClasslitString20_rv2112 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv47litChar1137null12150_failAssert53() throws IOException, Exception {
        try {
            char __DSPOT_arg0_4 = ' ';
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_rv47__4 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_rv47__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
            scopes.put("containers", containers);
            Writer __DSPOT_invoc_19 = mustache.execute(null, scopes);
            writer.flush();
            __DSPOT_invoc_19.append(__DSPOT_arg0_4);
            org.junit.Assert.fail("testAbstractClass_rv47litChar1137null12150 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString24_rv2308_rv11375_failAssert38() throws IOException, Exception {
        try {
            char __DSPOT_arg0_357 = '7';
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClasslitString24__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClasslitString24__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
            Object __DSPOT_invoc_35 = scopes.put(":", containers);
            Writer __DSPOT_invoc_39 = mustache.execute(writer, scopes);
            writer.flush();
            __DSPOT_invoc_39.append(__DSPOT_arg0_357);
            __DSPOT_invoc_35.notify();
            org.junit.Assert.fail("testAbstractClasslitString24_rv2308_rv11375 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_add29null2576_failAssert33_rv11707() throws IOException, Exception {
        try {
            char[] __DSPOT_arg0_1953 = new char[0];
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_add29__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_add29__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            Writer __DSPOT_invoc_26 = mustache.execute(null, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_add29null2576 should have thrown NullPointerException");
            __DSPOT_invoc_26.write(__DSPOT_arg0_1953);
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_add29litString224null12007() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClass_add29__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClass_add29__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("continers", containers);
        mustache.execute(writer, scopes);
        mustache.execute(null, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClass_add29_rv1799litString3823_failAssert54() throws IOException, Exception {
        try {
            char[] __DSPOT_arg0_16 = new char[]{ 'B', 'V' };
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_add29__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_add29__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/cotainers}}"), "example");
            scopes.put("containers", containers);
            Writer __DSPOT_invoc_39 = mustache.execute(writer, scopes);
            mustache.execute(writer, scopes);
            writer.flush();
            __DSPOT_invoc_39.write(__DSPOT_arg0_16);
            org.junit.Assert.fail("testAbstractClass_add29_rv1799litString3823 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Mismatched start/end tags: containers != cotainers in example:1 @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv39_failAssert7_add1634litString5097() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_rv39_failAssert7_add1634__5 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_rv39_failAssert7_add1634__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Mustache mustache = mf.compile(new StringReader(""), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_18 = scopes.put("containers", containers);
            Writer o_testAbstractClass_rv39_failAssert7_add1634__23 = mustache.execute(writer, scopes);
            writer.flush();
            __DSPOT_invoc_18.hashCode();
            org.junit.Assert.fail("testAbstractClass_rv39 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            expected.getMessage();
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv47litChar1138null12144() throws IOException, Exception {
        char __DSPOT_arg0_4 = 'N';
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClass_rv47__4 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClass_rv47__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", null);
        Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
        writer.flush();
        __DSPOT_invoc_19.append(__DSPOT_arg0_4);
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv47litChar1139litString4813_failAssert57() throws IOException, Exception {
        try {
            char __DSPOT_arg0_4 = '0';
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_rv47__4 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_rv47__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.valueL} {{/containers}}"), "example");
            scopes.put("containers", containers);
            Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
            writer.flush();
            __DSPOT_invoc_19.append(__DSPOT_arg0_4);
            org.junit.Assert.fail("testAbstractClass_rv47litChar1139litString4813 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to close \'containers\' tag @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv47litChar1139_add7883() throws IOException, Exception {
        char __DSPOT_arg0_4 = '0';
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClass_rv47__4 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClass_rv47__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
        writer.flush();
        __DSPOT_invoc_19.append(__DSPOT_arg0_4);
    }

    @Test(timeout = 10000)
    public void testAbstractClass_rv47litChar1137litString4794() throws IOException, Exception {
        char __DSPOT_arg0_4 = ' ';
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClass_rv47__4 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClass_rv47__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("\n"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
        writer.flush();
        __DSPOT_invoc_19.append(__DSPOT_arg0_4);
    }

    @Test(timeout = 10000)
    public void testAbstractClass_add29litString224_add7484() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClass_add29__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClass_add29__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("continers", containers);
        mustache.execute(writer, scopes);
        mustache.execute(writer, scopes);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClasslitString3_failAssert11_rv2567litChar6873() throws IOException, Exception {
        try {
            char[] __DSPOT_arg0_529 = new char[]{ '}', ')', 'T' };
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClasslitString3_failAssert11_rv2567__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClasslitString3_failAssert11_rv2567__9 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value},} {{/containers}}"), "example");
            scopes.put("containers", containers);
            Writer __DSPOT_invoc_21 = mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClasslitString3 should have thrown MustacheException");
            __DSPOT_invoc_21.write(__DSPOT_arg0_529);
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
    public void testAbstractClassNoDotslitString13196_failAssert69() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containrs}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClassNoDotslitString13196 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Mismatched start/end tags: containers != containrs in example:1 @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString13199() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString13199__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString13199__3);
        boolean o_testAbstractClassNoDotslitString13199__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString13199__6);
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
    public void testAbstractClassNoDotslitString13198() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString13198__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString13198__3);
        boolean o_testAbstractClassNoDotslitString13198__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString13198__6);
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
    public void testAbstractClassNoDotsnull13247_failAssert68() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(null, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClassNoDotsnull13247 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString13211() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString13211__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString13211__3);
        boolean o_testAbstractClassNoDotslitString13211__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClassNoDotslitString13211__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("contXainers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotsnull13246() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotsnull13246__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClassNoDotsnull13246__3);
        boolean o_testAbstractClassNoDotsnull13246__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClassNoDotsnull13246__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", null);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_remove13225() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDots_remove13225__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        Assert.assertTrue(o_testAbstractClassNoDots_remove13225__3);
        boolean o_testAbstractClassNoDots_remove13225__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        Assert.assertTrue(o_testAbstractClassNoDots_remove13225__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        Writer o_testAbstractClassNoDots_remove13225__18 = mustache.execute(writer, scopes);
        Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClassNoDots_remove13225__18)).getEncoding());
        writer.flush();
        Assert.assertTrue(o_testAbstractClassNoDots_remove13225__3);
        Assert.assertTrue(o_testAbstractClassNoDots_remove13225__6);
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClassNoDots_remove13225__18)).getEncoding());
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv13237_failAssert66litString14192() throws IOException, Exception {
        try {
            int __DSPOT_arg1_1991 = -62342630;
            long __DSPOT_arg0_1990 = -1906205760L;
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassNoDots_rv13237_failAssert66litString14192__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClassNoDots_rv13237_failAssert66litString14192__7);
            boolean o_testAbstractClassNoDots_rv13237_failAssert66litString14192__10 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClassNoDots_rv13237_failAssert66litString14192__10);
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Mustache mustache = mf.compile(new StringReader("u_mMhfw&?G85)8*YzSDY6N(V9riWE`vH%^a1,@3#7?EjnAM{vpovoB/#["), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            Object __DSPOT_invoc_18 = scopes.put("containers", containers);
            Writer o_testAbstractClassNoDots_rv13237_failAssert66litString14192__25 = mustache.execute(writer, scopes);
            Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClassNoDots_rv13237_failAssert66litString14192__25)).getEncoding());
            writer.flush();
            __DSPOT_invoc_18.wait(__DSPOT_arg0_1990, __DSPOT_arg1_1991);
            org.junit.Assert.fail("testAbstractClassNoDots_rv13237 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv13241null15805() throws IOException, Exception {
        int __DSPOT_arg0_1994 = -777484674;
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDots_rv13241__4 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassNoDots_rv13241__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put(null, containers);
        Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
        writer.flush();
        __DSPOT_invoc_19.write(__DSPOT_arg0_1994);
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_add13222null15783_failAssert92() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassNoDots_add13222__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClassNoDots_add13222__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(null, scopes);
            writer.flush();
            writer.flush();
            org.junit.Assert.fail("testAbstractClassNoDots_add13222null15783 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv13241null15806() throws IOException, Exception {
        int __DSPOT_arg0_1994 = -777484674;
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDots_rv13241__4 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassNoDots_rv13241__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", null);
        Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
        writer.flush();
        __DSPOT_invoc_19.write(__DSPOT_arg0_1994);
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString13198_remove14939() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString13198__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassNoDotslitString13198__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader(""), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString13193null15914_failAssert93() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassNoDotslitString13193__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClassNoDotslitString13193__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("example"), "example");
            scopes.put("containers", containers);
            mustache.execute(null, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClassNoDotslitString13193null15914 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv13241litString13566_failAssert95() throws IOException, Exception {
        try {
            int __DSPOT_arg0_1994 = -777484674;
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassNoDots_rv13241__4 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClassNoDots_rv13241__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#contain]ers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
            scopes.put("containers", containers);
            Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
            writer.flush();
            __DSPOT_invoc_19.write(__DSPOT_arg0_1994);
            org.junit.Assert.fail("testAbstractClassNoDots_rv13241litString13566 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Mismatched start/end tags: contain]ers != containers in example:1 @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString13198null15877() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString13198__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassNoDotslitString13198__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader(""), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        mustache.execute(null, scopes);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv13242null15819_failAssert94() throws IOException, Exception {
        try {
            String __DSPOT_arg0_1995 = "2!j*M,.-sDKmyGs!vZS4";
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassNoDots_rv13242__4 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClassNoDots_rv13242__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
            scopes.put("containers", containers);
            Writer __DSPOT_invoc_19 = mustache.execute(null, scopes);
            writer.flush();
            __DSPOT_invoc_19.write(__DSPOT_arg0_1995);
            org.junit.Assert.fail("testAbstractClassNoDots_rv13242null15819 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString13199_remove14960() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString13199__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassNoDotslitString13199__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("\n"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        writer.flush();
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString13196_failAssert69_add14853() throws IOException, Exception {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassNoDotslitString13196_failAssert69_add14853__5 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            Assert.assertTrue(o_testAbstractClassNoDotslitString13196_failAssert69_add14853__5);
            boolean o_testAbstractClassNoDotslitString13196_failAssert69_add14853__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClassNoDotslitString13196_failAssert69_add14853__8);
            boolean o_testAbstractClassNoDotslitString13196_failAssert69_add14853__11 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            Assert.assertTrue(o_testAbstractClassNoDotslitString13196_failAssert69_add14853__11);
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containrs}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClassNoDotslitString13196 should have thrown MustacheException");
        } catch (MustacheException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString13193_remove14965() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString13193__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassNoDotslitString13193__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("example"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        Writer o_testAbstractClassNoDotslitString13193_remove14965__22 = mustache.execute(writer, scopes);
        Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClassNoDotslitString13193_remove14965__22)).getEncoding());
        writer.flush();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClassNoDotslitString13193_remove14965__22)).getEncoding());
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString13205_remove14929null25735() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString13205__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassNoDotslitString13205__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "3p,b3(x");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("3p,b3(x", ((DefaultMustache) (mustache)).getName());
        Writer o_testAbstractClassNoDotslitString13205_remove14929__22 = mustache.execute(null, scopes);
        writer.flush();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("3p,b3(x", ((DefaultMustache) (mustache)).getName());
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_remove13224_remove14970_remove23314() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDots_remove13224__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        Writer o_testAbstractClassNoDots_remove13224_remove14970_remove23314__17 = mustache.execute(writer, scopes);
        Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClassNoDots_remove13224_remove14970_remove23314__17)).getEncoding());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_remove13223_remove14976litString19673() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDots_remove13223__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("n0<s(py}pGOAt^DmzD@ wT#SX!ik={,mm<PLkW`PC1W 8>Hz@%(B/`h/x"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        Writer o_testAbstractClassNoDots_remove13223_remove14976__17 = mustache.execute(writer, scopes);
        writer.flush();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString13193_remove14967_remove23302() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString13193__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassNoDotslitString13193__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("example"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        Writer o_testAbstractClassNoDotslitString13193_remove14967_remove23302__22 = mustache.execute(writer, scopes);
        Assert.assertEquals("ASCII", ((OutputStreamWriter) (o_testAbstractClassNoDotslitString13193_remove14967_remove23302__22)).getEncoding());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv13243_failAssert61litString14061null25613_failAssert115() throws IOException, Exception {
        try {
            try {
                int __DSPOT_arg2_1998 = -1411503133;
                int __DSPOT_arg1_1997 = 1856843628;
                String __DSPOT_arg0_1996 = "uR!2jy3B^SchvB2$|;*B";
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                boolean o_testAbstractClassNoDots_rv13243_failAssert61litString14061__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                boolean o_testAbstractClassNoDots_rv13243_failAssert61litString14061__11 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
                scopes.put("containers", containers);
                Writer __DSPOT_invoc_19 = mustache.execute(null, scopes);
                writer.flush();
                __DSPOT_invoc_19.write(__DSPOT_arg0_1996, __DSPOT_arg1_1997, __DSPOT_arg2_1998);
                org.junit.Assert.fail("testAbstractClassNoDots_rv13243 should have thrown IndexOutOfBoundsException");
            } catch (IndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("testAbstractClassNoDots_rv13243_failAssert61litString14061null25613 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv13240litChar14328litChar20729() throws IOException, Exception {
        char[] __DSPOT_arg0_1993 = new char[]{ '\u0000', ']', '1' };
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDots_rv13240__4 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassNoDots_rv13240__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("containers", containers);
        Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
        writer.flush();
        __DSPOT_invoc_19.write(__DSPOT_arg0_1993);
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv13237_failAssert66litNum14318litString17449_failAssert102() throws IOException, Exception {
        try {
            try {
                int __DSPOT_arg1_1991 = Integer.MAX_VALUE;
                long __DSPOT_arg0_1990 = -1906205760L;
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                boolean o_testAbstractClassNoDots_rv13237_failAssert66litNum14318__7 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                boolean o_testAbstractClassNoDots_rv13237_failAssert66litNum14318__10 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{foo}} {{/containers}}"), "example");
                Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                Writer o_testAbstractClassNoDots_rv13237_failAssert66litNum14318__25 = mustache.execute(writer, scopes);
                writer.flush();
                __DSPOT_invoc_18.wait(__DSPOT_arg0_1990, __DSPOT_arg1_1991);
                org.junit.Assert.fail("testAbstractClassNoDots_rv13237 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testAbstractClassNoDots_rv13237_failAssert66litNum14318litString17449 should have thrown MustacheException");
        } catch (MustacheException expected_1) {
            Assert.assertEquals("Mismatched start/end tags: foo != containers in example:1 @[example:1]", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_rv13243_failAssert61litString14061null25612() throws IOException, Exception {
        try {
            int __DSPOT_arg2_1998 = -1411503133;
            int __DSPOT_arg1_1997 = 1856843628;
            String __DSPOT_arg0_1996 = "uR!2jy3B^SchvB2$|;*B";
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassNoDots_rv13243_failAssert61litString14061__8 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClassNoDots_rv13243_failAssert61litString14061__11 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
            Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
            Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
            Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
            Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
            scopes.put("containers", null);
            Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
            writer.flush();
            __DSPOT_invoc_19.write(__DSPOT_arg0_1996, __DSPOT_arg1_1997, __DSPOT_arg2_1998);
            org.junit.Assert.fail("testAbstractClassNoDots_rv13243 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDotslitString13199_remove14960litString18050() throws IOException, Exception {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        boolean o_testAbstractClassNoDotslitString13199__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        boolean o_testAbstractClassNoDotslitString13199__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Assert.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache mustache = mf.compile(new StringReader("\n"), "example");
        Assert.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        Assert.assertEquals("example", ((DefaultMustache) (mustache)).getName());
        scopes.put("<#Uuk*8o^_", containers);
        writer.flush();
    }
}

