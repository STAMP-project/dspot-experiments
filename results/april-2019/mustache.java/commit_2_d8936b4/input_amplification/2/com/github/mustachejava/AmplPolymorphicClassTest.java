package com.github.mustachejava;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


public class AmplPolymorphicClassTest {
    static class Value {
        public String getText() {
            return "ok";
        }
    }

    static class A {
        public AmplPolymorphicClassTest.Value getValue() {
            return new AmplPolymorphicClassTest.Value();
        }
    }

    static class B extends AmplPolymorphicClassTest.A {
        @Override
        public AmplPolymorphicClassTest.Value getValue() {
            return new AmplPolymorphicClassTest.Value();
        }
    }

    String compile(String template, Object model) {
        final StringWriter buffer = new StringWriter();
        factory.compile(template).execute(buffer, model);
        return buffer.toString();
    }

    DefaultMustacheFactory factory = new DefaultMustacheFactory() {
        public Reader getReader(String resourceName) {
            return new StringReader(resourceName);
        }
    };

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString5_failAssert0_add2255_failAssert0() throws IOException {
        try {
            {
                HashMap<String, Object> model = new HashMap<>();
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{=toolyn}}", model);
                model.put("x", new AmplPolymorphicClassTest.A());
                model.put("x", new AmplPolymorphicClassTest.A());
                compile("{{x.value.text}}", model);
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{x.value.text}}", model);
                compile("{{x.value.text}}", model);
                org.junit.Assert.fail("testPolyClass_literalMutationString5 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testPolyClass_literalMutationString5_failAssert0_add2255 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString14_failAssert0_literalMutationString1880_failAssert0() throws IOException {
        try {
            {
                HashMap<String, Object> model = new HashMap<>();
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{x.value.text}}", model);
                model.put("{{=toolyn}}", new AmplPolymorphicClassTest.A());
                compile("{{=toolyn}}", model);
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{x.value.text}}", model);
                compile("{{x.value.text}}", model);
                org.junit.Assert.fail("testPolyClass_literalMutationString14 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testPolyClass_literalMutationString14_failAssert0_literalMutationString1880 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString23_failAssert0_literalMutationString1811_failAssert0() throws IOException {
        try {
            {
                HashMap<String, Object> model = new HashMap<>();
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("5Mq_q^jezmZ#2Xy ", model);
                model.put("x", new AmplPolymorphicClassTest.A());
                compile("{{x.value.text}}", model);
                compile("{{x.value.text}}", model);
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{=toolyn}}", model);
                org.junit.Assert.fail("testPolyClass_literalMutationString23 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testPolyClass_literalMutationString23_failAssert0_literalMutationString1811 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString25_literalMutationString1524_failAssert0() throws IOException {
        try {
            HashMap<String, Object> model = new HashMap<>();
            Object o_testPolyClass_literalMutationString25__3 = model.put("x", new AmplPolymorphicClassTest.B());
            String o_testPolyClass_literalMutationString25__5 = compile("{{=toolyn}}", model);
            model.put("x", new AmplPolymorphicClassTest.A());
            String o_testPolyClass_literalMutationString25__8 = compile("{{x.value.text}}", model);
            String o_testPolyClass_literalMutationString25__9 = compile("{{x.value.text}}", model);
            model.put("x", new AmplPolymorphicClassTest.B());
            String o_testPolyClass_literalMutationString25__12 = compile("{x.value.text}}", model);
            org.junit.Assert.fail("testPolyClass_literalMutationString25_literalMutationString1524 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString23_failAssert0null2886_failAssert0() throws IOException {
        try {
            {
                HashMap<String, Object> model = new HashMap<>();
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{x.value.text}}", model);
                model.put("x", new AmplPolymorphicClassTest.A());
                compile("{{x.value.text}}", model);
                compile("{{x.value.text}}", model);
                model.put(null, new AmplPolymorphicClassTest.B());
                compile("{{=toolyn}}", model);
                org.junit.Assert.fail("testPolyClass_literalMutationString23 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testPolyClass_literalMutationString23_failAssert0null2886 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString14_failAssert0() throws IOException {
        try {
            HashMap<String, Object> model = new HashMap<>();
            model.put("x", new AmplPolymorphicClassTest.B());
            compile("{{x.value.text}}", model);
            model.put("x", new AmplPolymorphicClassTest.A());
            compile("{{=toolyn}}", model);
            model.put("x", new AmplPolymorphicClassTest.B());
            compile("{{x.value.text}}", model);
            compile("{{x.value.text}}", model);
            org.junit.Assert.fail("testPolyClass_literalMutationString14 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString23_failAssert0_add2246_failAssert0() throws IOException {
        try {
            {
                HashMap<String, Object> model = new HashMap<>();
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{x.value.text}}", model);
                compile("{{x.value.text}}", model);
                model.put("x", new AmplPolymorphicClassTest.A());
                compile("{{x.value.text}}", model);
                compile("{{x.value.text}}", model);
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{=toolyn}}", model);
                org.junit.Assert.fail("testPolyClass_literalMutationString23 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testPolyClass_literalMutationString23_failAssert0_add2246 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString4_literalMutationString1430_failAssert0() throws IOException {
        try {
            HashMap<String, Object> model = new HashMap<>();
            Object o_testPolyClass_literalMutationString4__3 = model.put("x", new AmplPolymorphicClassTest.B());
            String o_testPolyClass_literalMutationString4__5 = compile("", model);
            model.put("x", new AmplPolymorphicClassTest.A());
            String o_testPolyClass_literalMutationString4__8 = compile("{{x.value.text}}", model);
            model.put("x", new AmplPolymorphicClassTest.B());
            String o_testPolyClass_literalMutationString4__11 = compile("{{=toolyn}}", model);
            String o_testPolyClass_literalMutationString4__12 = compile("{{x.value.text}}", model);
            org.junit.Assert.fail("testPolyClass_literalMutationString4_literalMutationString1430 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString14_failAssert0_add2264_failAssert0() throws IOException {
        try {
            {
                HashMap<String, Object> model = new HashMap<>();
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{x.value.text}}", model);
                model.put("x", new AmplPolymorphicClassTest.A());
                compile("{{=toolyn}}", model);
                compile("{{=toolyn}}", model);
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{x.value.text}}", model);
                compile("{{x.value.text}}", model);
                org.junit.Assert.fail("testPolyClass_literalMutationString14 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testPolyClass_literalMutationString14_failAssert0_add2264 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString10_literalMutationString1110_failAssert0() throws IOException {
        try {
            HashMap<String, Object> model = new HashMap<>();
            Object o_testPolyClass_literalMutationString10__3 = model.put("x", new AmplPolymorphicClassTest.B());
            String o_testPolyClass_literalMutationString10__5 = compile("{{x.value.text}}", model);
            Object o_testPolyClass_literalMutationString10__6 = model.put("", new AmplPolymorphicClassTest.A());
            String o_testPolyClass_literalMutationString10__8 = compile("{{x.value.text}}", model);
            String o_testPolyClass_literalMutationString10__9 = compile("{{x.value.text}}", model);
            model.put("x", new AmplPolymorphicClassTest.B());
            String o_testPolyClass_literalMutationString10__12 = compile("{{=toolyn}}", model);
            String o_testPolyClass_literalMutationString10__13 = compile("{{x.value.text}}", model);
            String o_testPolyClass_literalMutationString10__14 = compile("{{x.value.text}}", model);
            org.junit.Assert.fail("testPolyClass_literalMutationString10_literalMutationString1110 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString12_literalMutationString1022_failAssert0() throws IOException {
        try {
            HashMap<String, Object> model = new HashMap<>();
            Object o_testPolyClass_literalMutationString12__3 = model.put("x", new AmplPolymorphicClassTest.B());
            String o_testPolyClass_literalMutationString12__5 = compile("{{x.value.text}}", model);
            Object o_testPolyClass_literalMutationString12__6 = model.put("R", new AmplPolymorphicClassTest.A());
            String o_testPolyClass_literalMutationString12__8 = compile("{{x.value.text}}", model);
            String o_testPolyClass_literalMutationString12__9 = compile("{{=toolyn}}", model);
            model.put("x", new AmplPolymorphicClassTest.B());
            String o_testPolyClass_literalMutationString12__12 = compile("{{x.value.text}}", model);
            String o_testPolyClass_literalMutationString12__13 = compile("{{x.value.text}}", model);
            String o_testPolyClass_literalMutationString12__14 = compile("{{x.value.text}}", model);
            org.junit.Assert.fail("testPolyClass_literalMutationString12_literalMutationString1022 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString5_failAssert0() throws IOException {
        try {
            HashMap<String, Object> model = new HashMap<>();
            model.put("x", new AmplPolymorphicClassTest.B());
            compile("{{=toolyn}}", model);
            model.put("x", new AmplPolymorphicClassTest.A());
            compile("{{x.value.text}}", model);
            model.put("x", new AmplPolymorphicClassTest.B());
            compile("{{x.value.text}}", model);
            compile("{{x.value.text}}", model);
            org.junit.Assert.fail("testPolyClass_literalMutationString5 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString23_failAssert0() throws IOException {
        try {
            HashMap<String, Object> model = new HashMap<>();
            model.put("x", new AmplPolymorphicClassTest.B());
            compile("{{x.value.text}}", model);
            model.put("x", new AmplPolymorphicClassTest.A());
            compile("{{x.value.text}}", model);
            compile("{{x.value.text}}", model);
            model.put("x", new AmplPolymorphicClassTest.B());
            compile("{{=toolyn}}", model);
            org.junit.Assert.fail("testPolyClass_literalMutationString23 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString14_failAssert0null2908_failAssert0() throws IOException {
        try {
            {
                HashMap<String, Object> model = new HashMap<>();
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{x.value.text}}", model);
                model.put("x", new AmplPolymorphicClassTest.A());
                compile("{{=toolyn}}", model);
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{x.value.text}}", null);
                compile("{{x.value.text}}", model);
                org.junit.Assert.fail("testPolyClass_literalMutationString14 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testPolyClass_literalMutationString14_failAssert0null2908 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString5_failAssert0null2896_failAssert0() throws IOException {
        try {
            {
                HashMap<String, Object> model = new HashMap<>();
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{=toolyn}}", model);
                model.put("x", new AmplPolymorphicClassTest.A());
                compile("{{x.value.text}}", model);
                model.put("x", new AmplPolymorphicClassTest.B());
                compile(null, model);
                compile("{{x.value.text}}", model);
                org.junit.Assert.fail("testPolyClass_literalMutationString5 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testPolyClass_literalMutationString5_failAssert0null2896 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString6_literalMutationString984_failAssert0() throws IOException {
        try {
            HashMap<String, Object> model = new HashMap<>();
            Object o_testPolyClass_literalMutationString6__3 = model.put("x", new AmplPolymorphicClassTest.B());
            String o_testPolyClass_literalMutationString6__5 = compile("{{x.value.;text}}", model);
            model.put("x", new AmplPolymorphicClassTest.A());
            String o_testPolyClass_literalMutationString6__8 = compile("{{=toolyn}}", model);
            model.put("x", new AmplPolymorphicClassTest.B());
            String o_testPolyClass_literalMutationString6__11 = compile("{{x.value.text}}", model);
            String o_testPolyClass_literalMutationString6__12 = compile("{{x.value.text}}", model);
            org.junit.Assert.fail("testPolyClass_literalMutationString6_literalMutationString984 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPolyClass_literalMutationString5_failAssert0_literalMutationString1849_failAssert0() throws IOException {
        try {
            {
                HashMap<String, Object> model = new HashMap<>();
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{=toolyn}}", model);
                model.put("x", new AmplPolymorphicClassTest.A());
                compile("", model);
                model.put("x", new AmplPolymorphicClassTest.B());
                compile("{{x.value.text}}", model);
                compile("{{x.value.text}}", model);
                org.junit.Assert.fail("testPolyClass_literalMutationString5 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testPolyClass_literalMutationString5_failAssert0_literalMutationString1849 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[{{=toolyn}}:1]", expected.getMessage());
        }
    }
}

