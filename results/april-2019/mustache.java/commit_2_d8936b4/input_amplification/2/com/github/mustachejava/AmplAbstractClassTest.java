package com.github.mustachejava;


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
    public void testAbstractClass_literalMutationString18_literalMutationString566_failAssert0() throws IOException {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_literalMutationString18__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_literalMutationString18__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
            Object o_testAbstractClass_literalMutationString18__18 = scopes.put("L^t!C)GZ,G", containers);
            Writer o_testAbstractClass_literalMutationString18__19 = mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_literalMutationString18_literalMutationString566 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_add23_literalMutationString257_failAssert0() throws IOException {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_add23__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClass_add23__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
            Object o_testAbstractClass_add23__18 = scopes.put("containers", containers);
            Writer o_testAbstractClass_add23__19 = mustache.execute(writer, scopes);
            Writer o_testAbstractClass_add23__20 = mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_add23_literalMutationString257 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_literalMutationString2_failAssert0() throws IOException {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_literalMutationString2 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_literalMutationString2_failAssert0_add1135_failAssert0() throws IOException {
        try {
            {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                mf.compile(new StringReader("{{=toolyn}}"), "example");
                Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
                scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                org.junit.Assert.fail("testAbstractClass_literalMutationString2 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testAbstractClass_literalMutationString2_failAssert0_add1135 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_literalMutationString2_failAssert0null1325_failAssert0() throws IOException {
        try {
            {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
                scopes.put("containers", null);
                mustache.execute(writer, scopes);
                writer.flush();
                org.junit.Assert.fail("testAbstractClass_literalMutationString2 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testAbstractClass_literalMutationString2_failAssert0null1325 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClass_remove25_literalMutationString209_failAssert0() throws IOException {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClass_remove25__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
            Object o_testAbstractClass_remove25__15 = scopes.put("containers", containers);
            Writer o_testAbstractClass_remove25__16 = mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_remove25_literalMutationString209 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_literalMutationString1837_failAssert0_add3007_failAssert0() throws IOException {
        try {
            {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
                scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString1837 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString1837_failAssert0_add3007 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_remove1860_literalMutationString2076_failAssert0() throws IOException {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassNoDots_remove1860__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
            Object o_testAbstractClassNoDots_remove1860__15 = scopes.put("containers", containers);
            Writer o_testAbstractClassNoDots_remove1860__16 = mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClassNoDots_remove1860_literalMutationString2076 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_literalMutationString1837_failAssert0_literalMutationString2498_failAssert0() throws IOException {
        try {
            {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "{{=toolyn}}");
                scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString1837 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString1837_failAssert0_literalMutationString2498 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_literalMutationString1837_failAssert0_add3002_failAssert0() throws IOException {
        try {
            {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
                scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString1837 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString1837_failAssert0_add3002 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_literalMutationString1837_failAssert0_literalMutationString2507_failAssert0() throws IOException {
        try {
            {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
                scopes.put(" Q4gw=Nmw,", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString1837 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString1837_failAssert0_literalMutationString2507 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_literalMutationString1837_failAssert0null3191_failAssert0() throws IOException {
        try {
            {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
                scopes.put("containers", null);
                mustache.execute(writer, scopes);
                writer.flush();
                org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString1837 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString1837_failAssert0null3191 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_literalMutationString1837_failAssert0null3190_failAssert0() throws IOException {
        try {
            {
                final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
                containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
                HashMap<String, Object> scopes = new HashMap<>();
                Writer writer = new OutputStreamWriter(System.out);
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
                scopes.put(null, containers);
                mustache.execute(writer, scopes);
                writer.flush();
                org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString1837 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString1837_failAssert0null3190 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_remove1861_literalMutationString2063_failAssert0() throws IOException {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            boolean o_testAbstractClassNoDots_remove1861__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            boolean o_testAbstractClassNoDots_remove1861__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
            Writer o_testAbstractClassNoDots_remove1861__18 = mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClassNoDots_remove1861_literalMutationString2063 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots_literalMutationString1837_failAssert0() throws IOException {
        try {
            final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
            containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
            HashMap<String, Object> scopes = new HashMap<>();
            Writer writer = new OutputStreamWriter(System.out);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader("{{=toolyn}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString1837 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[example:1]", expected.getMessage());
        }
    }
}

