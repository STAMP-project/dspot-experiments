package com.github.mustachejava.functions;


import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.MustacheNotFoundException;
import com.github.mustachejava.TestUtil;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class AmplTranslateBundleTest {
    private static File root;

    private static final String BUNDLE = "com.github.mustachejava.functions.translatebundle";

    @Test(timeout = 10000)
    public void testTranslation_remove25_literalMutationString240_failAssert0null6485_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("4f+!*^-q-5WP2}qm$]?r");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslation_remove25__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                String o_testTranslation_remove25__11 = TestUtil.getContents(AmplTranslateBundleTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testTranslation_remove25_literalMutationString240 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_remove25_literalMutationString240_failAssert0null6485 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4f+!*^-q-5WP2}qm$]?r not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_remove24_literalMutationString229_failAssert0_literalMutationString3178_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Flj:8Mef$N.0yVUHP!&");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Writer o_testTranslation_remove24__9 = m.execute(sw, scope);
                String o_testTranslation_remove24__10 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_remove24_literalMutationString229 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_remove24_literalMutationString229_failAssert0_literalMutationString3178 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Flj:8Mef$N.0yVUHP!& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString17_failAssert0_literalMutationString559_failAssert0_literalMutationString3344_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("9cB0FC$[>M`k3w3>pJmf,PAIy7Vc5}y-.xpxv");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, "translatebund:le.txt");
                    sw.toString();
                    org.junit.Assert.fail("testTranslation_literalMutationString17 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTranslation_literalMutationString17_failAssert0_literalMutationString559 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString17_failAssert0_literalMutationString559_failAssert0_literalMutationString3344 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 9cB0FC$[>M`k3w3>pJmf,PAIy7Vc5}y-.xpxv not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_remove24_literalMutationString227_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Writer o_testTranslation_remove24__9 = m.execute(sw, scope);
            String o_testTranslation_remove24__10 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_remove24_literalMutationString227 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_literalMutationString459_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_literalMutationString459 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString17_failAssert0_literalMutationString559_failAssert0_add5236_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, "translatebund:le.txt");
                    sw.toString();
                    org.junit.Assert.fail("testTranslation_literalMutationString17 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTranslation_literalMutationString17_failAssert0_literalMutationString559 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString17_failAssert0_literalMutationString559_failAssert0_add5236 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_remove24_literalMutationString229_failAssert0_literalMutationString3175_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Fl)j:8Mef$N.0yVUHQP!&");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Writer o_testTranslation_remove24__9 = m.execute(sw, scope);
                String o_testTranslation_remove24__10 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_remove24_literalMutationString229 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_remove24_literalMutationString229_failAssert0_literalMutationString3175 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Fl)j:8Mef$N.0yVUHQP!& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslationnull28_failAssert0null972_failAssert0_literalMutationString3597_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(null, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testTranslationnull28 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testTranslationnull28_failAssert0null972 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testTranslationnull28_failAssert0null972_failAssert0_literalMutationString3597 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString17_failAssert0_literalMutationString559_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebund:le.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString17 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString17_failAssert0_literalMutationString559 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_add21_literalMutationString191_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("=&?oUxq?M{VK=Od-VN@a");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_add21__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslation_add21__11 = m.execute(sw, scope);
            Writer o_testTranslation_add21__12 = m.execute(sw, scope);
            String o_testTranslation_add21__13 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_add21_literalMutationString191 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString9_literalMutationString278_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("mo6BddDP`#3Y4h9hodEO");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_literalMutationString9__9 = scope.put("tZrans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslation_literalMutationString9__11 = m.execute(sw, scope);
            String o_testTranslation_literalMutationString9__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString9_literalMutationString278 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mo6BddDP`#3Y4h9hodEO not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString8_literalMutationString311_failAssert0null6320_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslation_literalMutationString8__9 = scope.put("", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                Writer o_testTranslation_literalMutationString8__11 = m.execute(null, scope);
                String o_testTranslation_literalMutationString8__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString8_literalMutationString311 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString8_literalMutationString311_failAssert0null6320 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_remove24_literalMutationString227_failAssert0_add5672_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                m.execute(sw, scope);
                Writer o_testTranslation_remove24__9 = m.execute(sw, scope);
                String o_testTranslation_remove24__10 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_remove24_literalMutationString227 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_remove24_literalMutationString227_failAssert0_add5672 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_remove24_literalMutationString229_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("Flj:8Mef$N.0yVUHQP!&");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Writer o_testTranslation_remove24__9 = m.execute(sw, scope);
            String o_testTranslation_remove24__10 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_remove24_literalMutationString229 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Flj:8Mef$N.0yVUHQP!& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString8_literalMutationString311_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_literalMutationString8__9 = scope.put("", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslation_literalMutationString8__11 = m.execute(sw, scope);
            String o_testTranslation_literalMutationString8__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString8_literalMutationString311 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_remove25_literalMutationString240_failAssert0_add5486_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                c.compile("4f+!*^-q-5WP2}qm$]?r");
                Mustache m = c.compile("4f+!*^-q-5WP2}qm$]?r");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslation_remove25__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                String o_testTranslation_remove25__11 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_remove25_literalMutationString240 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_remove25_literalMutationString240_failAssert0_add5486 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4f+!*^-q-5WP2}qm$]?r not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString4_failAssert0null1019_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("/OH4;%3xnhxXG&]1#QHo");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put(null, new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString4_failAssert0null1019 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /OH4;%3xnhxXG&]1#QHo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_remove24_literalMutationString229_failAssert0_add5174_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                c.compile("Flj:8Mef$N.0yVUHQP!&");
                Mustache m = c.compile("Flj:8Mef$N.0yVUHQP!&");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Writer o_testTranslation_remove24__9 = m.execute(sw, scope);
                String o_testTranslation_remove24__10 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_remove24_literalMutationString229 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_remove24_literalMutationString229_failAssert0_add5174 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Flj:8Mef$N.0yVUHQP!& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_add23null916_failAssert0_literalMutationString4376_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("#F|/JhA#0=n9*S0cr+1?");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslation_add23__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                Writer o_testTranslation_add23__11 = m.execute(null, scope);
                sw.toString();
                sw.toString();
                String o_testTranslation_add23__13 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                org.junit.Assert.fail("testTranslation_add23null916 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testTranslation_add23null916_failAssert0_literalMutationString4376 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #F|/JhA#0=n9*S0cr+1? not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_remove25_literalMutationString240_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("4f+!*^-q-5WP2}qm$]?r");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_remove25__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            String o_testTranslation_remove25__11 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_remove25_literalMutationString240 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4f+!*^-q-5WP2}qm$]?r not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString2_failAssert0_add843_failAssert0_literalMutationString3001_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("{ $4-I(W%QequI4wC|%,");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                    sw.toString();
                    org.junit.Assert.fail("testTranslation_literalMutationString2 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testTranslation_literalMutationString2_failAssert0_add843 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString2_failAssert0_add843_failAssert0_literalMutationString3001 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template { $4-I(W%QequI4wC|%, not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0null985_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(null, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0null985 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0null987_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0null987 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_remove24_literalMutationString229_failAssert0_add5177_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Flj:8Mef$N.0yVUHQP!&");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Writer o_testTranslation_remove24__9 = m.execute(sw, scope);
                String o_testTranslation_remove24__10 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testTranslation_remove24_literalMutationString229 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_remove24_literalMutationString229_failAssert0_add5177 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Flj:8Mef$N.0yVUHQP!& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_add818_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_add818 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString8_literalMutationString311_failAssert0_literalMutationString3428_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Ip}*5QKb!uL&z/j{+aWq8a=EQKD$nrb)y{#X{");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslation_literalMutationString8__9 = scope.put("", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                Writer o_testTranslation_literalMutationString8__11 = m.execute(sw, scope);
                String o_testTranslation_literalMutationString8__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString8_literalMutationString311 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString8_literalMutationString311_failAssert0_literalMutationString3428 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Ip}*5QKb!uL&z/j{+aWq8a=EQKD$nrb)y{#X{ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString4_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("/OH4;%3xnhxXG&]1#QHo");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString4 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /OH4;%3xnhxXG&]1#QHo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString4_failAssert0null1020_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("/OH4;%3xnhxXG&]1#QHo");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(null, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString4_failAssert0null1020 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /OH4;%3xnhxXG&]1#QHo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString4_failAssert0_add859_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("/OH4;%3xnhxXG&]1#QHo");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString4_failAssert0_add859 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /OH4;%3xnhxXG&]1#QHo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString8_literalMutationString311_failAssert0_add5267_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslation_literalMutationString8__9 = scope.put("", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                Writer o_testTranslation_literalMutationString8__11 = m.execute(sw, scope);
                String o_testTranslation_literalMutationString8__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString8_literalMutationString311 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString8_literalMutationString311_failAssert0_add5267 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString4_failAssert0_literalMutationString593_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("/OH4;%3xnhxXG&]1#QHo");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundletxt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString4_failAssert0_literalMutationString593 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /OH4;%3xnhxXG&]1#QHo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString8_literalMutationString314_failAssert0_literalMutationString4321_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslation_literalMutationString8__9 = scope.put("", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                Writer o_testTranslation_literalMutationString8__11 = m.execute(sw, scope);
                String o_testTranslation_literalMutationString8__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString8_literalMutationString314 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString8_literalMutationString314_failAssert0_literalMutationString4321 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString4_failAssert0_add858_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("/OH4;%3xnhxXG&]1#QHo");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString4_failAssert0_add858 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /OH4;%3xnhxXG&]1#QHo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString12_literalMutationString296_failAssert0_literalMutationString3481_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslation_literalMutationString12__9 = scope.put("tans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                Writer o_testTranslation_literalMutationString12__11 = m.execute(sw, scope);
                String o_testTranslation_literalMutationString12__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString12_literalMutationString296 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString12_literalMutationString296_failAssert0_literalMutationString3481 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString4_failAssert0_literalMutationString579_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("/OH4;%3x(nhxXG&]1#QHo");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString4_failAssert0_literalMutationString579 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /OH4;%3x(nhxXG&]1#QHo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString1() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map<String, Object> scope = new HashMap<>();
        Object o_testTranslation_literalMutationString1__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
        Assert.assertNull(o_testTranslation_literalMutationString1__9);
        Writer o_testTranslation_literalMutationString1__11 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testTranslation_literalMutationString1__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testTranslation_literalMutationString1__11)).toString());
        String o_testTranslation_literalMutationString1__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
        Assert.assertEquals("Translation bundles work!\nNotFound\n", o_testTranslation_literalMutationString1__12);
        sw.toString();
        Assert.assertNull(o_testTranslation_literalMutationString1__9);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testTranslation_literalMutationString1__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testTranslation_literalMutationString1__11)).toString());
        Assert.assertEquals("Translation bundles work!\nNotFound\n", o_testTranslation_literalMutationString1__12);
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString1_add764() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map<String, Object> scope = new HashMap<>();
        Object o_testTranslation_literalMutationString1__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
        Writer o_testTranslation_literalMutationString1_add764__13 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testTranslation_literalMutationString1_add764__13)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testTranslation_literalMutationString1_add764__13)).toString());
        Writer o_testTranslation_literalMutationString1__11 = m.execute(sw, scope);
        String o_testTranslation_literalMutationString1__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
        Assert.assertEquals("Translation bundles work!\nNotFound\n", o_testTranslation_literalMutationString1__12);
        sw.toString();
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testTranslation_literalMutationString1_add764__13)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testTranslation_literalMutationString1_add764__13)).toString());
        Assert.assertEquals("Translation bundles work!\nNotFound\n", o_testTranslation_literalMutationString1__12);
    }

    @Test(timeout = 10000)
    public void testTranslation_remove25_literalMutationString240_failAssert0_literalMutationString4000_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("4f+!*^-q-5WP2}qm$]?r");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslation_remove25__9 = scope.put("tSans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                String o_testTranslation_remove25__11 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_remove25_literalMutationString240 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_remove25_literalMutationString240_failAssert0_literalMutationString4000 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4f+!*^-q-5WP2}qm$]?r not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslationnull27_literalMutationString107_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("n>SSGjx[+/D#l&6;jl$C");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslationnull27__9 = scope.put(null, new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslationnull27__11 = m.execute(sw, scope);
            String o_testTranslationnull27__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslationnull27_literalMutationString107 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template n>SSGjx[+/D#l&6;jl$C not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_add819_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_add819 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplTranslateBundleTest.root = new File(compiler, "src/test/resources/functions");
    }
}

