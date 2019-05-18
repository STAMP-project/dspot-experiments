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
    public void testTranslation_literalMutationString6_failAssert0_literalMutationString511_failAssert0_literalMutationString2633_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("5)i{c%NH_Iw_Q#9N4]Jz,");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, "g|azou|gm9wo");
                    sw.toString();
                    org.junit.Assert.fail("testTranslation_literalMutationString6 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0_literalMutationString511 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0_literalMutationString511_failAssert0_literalMutationString2633 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5)i{c%NH_Iw_Q#9N4]Jz, not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString6_failAssert0_add825_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("tran]slatebundle.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0_add825 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tran]slatebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_literalMutationString471_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("g|azou|gm9wo");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("tr}ans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_literalMutationString471 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString6_failAssert0_add822_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                c.compile("tran]slatebundle.html");
                Mustache m = c.compile("tran]slatebundle.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0_add822 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tran]slatebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString1_failAssert0_add791_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("{(t@&UVv!v.^_/B/O,f=");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString1_failAssert0_add791 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString6_failAssert0_literalMutationString506_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("tran]slatebundle.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("*6iP`", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0_literalMutationString506 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tran]slatebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString6_failAssert0_literalMutationString511_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("tran]slatebundle.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "g|azou|gm9wo");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0_literalMutationString511 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tran]slatebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString6_failAssert0_literalMutationString511_failAssert0_add3236_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("tran]slatebundle.html");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, "g|azou|gm9wo");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testTranslation_literalMutationString6 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0_literalMutationString511 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0_literalMutationString511_failAssert0_add3236 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tran]slatebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_add813_failAssert0_add2918_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("g|azou|gm9wo");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                    TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                    sw.toString();
                    org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_add813 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_add813_failAssert0_add2918 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslationnull27_literalMutationString334_failAssert0_literalMutationString2121_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("MW^07%.P;KP7");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslationnull27__9 = scope.put(null, new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                Writer o_testTranslationnull27__11 = m.execute(sw, scope);
                String o_testTranslationnull27__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                String o_testTranslationnull27__13 = sw.toString();
                org.junit.Assert.fail("testTranslationnull27_literalMutationString334 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslationnull27_literalMutationString334_failAssert0_literalMutationString2121 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template MW^07%.P;KP7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslationnull27_literalMutationString334_failAssert0_literalMutationString2127_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("g|azou|gm9wo");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslationnull27__9 = scope.put(null, new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                Writer o_testTranslationnull27__11 = m.execute(sw, scope);
                String o_testTranslationnull27__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translateundle.txt");
                String o_testTranslationnull27__13 = sw.toString();
                org.junit.Assert.fail("testTranslationnull27_literalMutationString334 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslationnull27_literalMutationString334_failAssert0_literalMutationString2127 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString6_failAssert0null978_failAssert0_literalMutationString2709_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("g|azou|gm9wo");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testTranslation_literalMutationString6 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0null978 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0null978_failAssert0_literalMutationString2709 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString1_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("{(t@&UVv!v.^_/B/O,f=");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString1 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString7_literalMutationString223_failAssert0_literalMutationString2168_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("C>pmS}0D^GOZzt1N]x7O");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslation_literalMutationString7__9 = scope.put("", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                Writer o_testTranslation_literalMutationString7__11 = m.execute(sw, scope);
                String o_testTranslation_literalMutationString7__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "g|azou|gm9wo");
                String o_testTranslation_literalMutationString7__13 = sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString7_literalMutationString223 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString7_literalMutationString223_failAssert0_literalMutationString2168 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template C>pmS}0D^GOZzt1N]x7O not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString6_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("tran]slatebundle.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString6 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tran]slatebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString6_failAssert0null976_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("tran]slatebundle.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(null, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0null976 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tran]slatebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString6_failAssert0null976_failAssert0_add3121_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("tran]slatebundle.html");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(null, scope);
                    m.execute(null, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                    sw.toString();
                    org.junit.Assert.fail("testTranslation_literalMutationString6 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0null976 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0null976_failAssert0_add3121 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tran]slatebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("g|azou|gm9wo");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString6_failAssert0null978_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("tran]slatebundle.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0null978 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tran]slatebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslationnull27_literalMutationString334_failAssert0null3538_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("g|azou|gm9wo");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslationnull27__9 = scope.put(null, new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                Writer o_testTranslationnull27__11 = m.execute(null, scope);
                String o_testTranslationnull27__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                String o_testTranslationnull27__13 = sw.toString();
                org.junit.Assert.fail("testTranslationnull27_literalMutationString334 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslationnull27_literalMutationString334_failAssert0null3538 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslationnull27_literalMutationString334_failAssert0_add3041_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("g|azou|gm9wo");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put(null, new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                Object o_testTranslationnull27__9 = scope.put(null, new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                Writer o_testTranslationnull27__11 = m.execute(sw, scope);
                String o_testTranslationnull27__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                String o_testTranslationnull27__13 = sw.toString();
                org.junit.Assert.fail("testTranslationnull27_literalMutationString334 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslationnull27_literalMutationString334_failAssert0_add3041 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslationnull28_failAssert0_literalMutationString584_failAssert0_literalMutationString2013_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("*k>lUtw`J9wux#I=`IZ");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(null, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                    sw.toString();
                    org.junit.Assert.fail("testTranslationnull28 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testTranslationnull28_failAssert0_literalMutationString584 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslationnull28_failAssert0_literalMutationString584_failAssert0_literalMutationString2013 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *k>lUtw`J9wux#I=`IZ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslationnull27_literalMutationString334_failAssert0_add3043_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("g|azou|gm9wo");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testTranslationnull27__9 = scope.put(null, new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                Writer o_testTranslationnull27__11 = m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                String o_testTranslationnull27__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                String o_testTranslationnull27__13 = sw.toString();
                org.junit.Assert.fail("testTranslationnull27_literalMutationString334 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslationnull27_literalMutationString334_failAssert0_add3043 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString6_failAssert0null978_failAssert0_add3266_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("tran]slatebundle.html");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, null);
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testTranslation_literalMutationString6 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0null978 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0null978_failAssert0_add3266 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tran]slatebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString6_failAssert0null976_failAssert0_literalMutationString2358_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("tran]slatebundle.html");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("g|azou|gm9wo", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(null, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                    sw.toString();
                    org.junit.Assert.fail("testTranslation_literalMutationString6 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0null976 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString6_failAssert0null976_failAssert0_literalMutationString2358 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tran]slatebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString1_failAssert0null948_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("{(t@&UVv!v.^_/B/O,f=");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString1_failAssert0null948 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_add21_literalMutationString153_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("unXU[zg9&(vc7*E+CH5K");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_add21__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslation_add21__11 = m.execute(sw, scope);
            Writer o_testTranslation_add21__12 = m.execute(sw, scope);
            String o_testTranslation_add21__13 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            String o_testTranslation_add21__14 = sw.toString();
            org.junit.Assert.fail("testTranslation_add21_literalMutationString153 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template unXU[zg9&(vc7*E+CH5K not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString1_failAssert0_literalMutationString406_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("{(t@&UVv!v.^_/B/O,f=");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "g|azou|gm9wo");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString1_failAssert0_literalMutationString406 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_add813_failAssert0_literalMutationString1748_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("g|azou|gm9wo");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                    TestUtil.getContents(AmplTranslateBundleTest.root, "translaMtebundle.txt");
                    sw.toString();
                    org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_add813 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_add813_failAssert0_literalMutationString1748 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslationnull27_literalMutationString334_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("g|azou|gm9wo");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslationnull27__9 = scope.put(null, new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslationnull27__11 = m.execute(sw, scope);
            String o_testTranslationnull27__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            String o_testTranslationnull27__13 = sw.toString();
            org.junit.Assert.fail("testTranslationnull27_literalMutationString334 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString16_failAssert0null983_failAssert0_literalMutationString2465_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("tra[nslatebundle.html");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testTranslation_literalMutationString16 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTranslation_literalMutationString16_failAssert0null983 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString16_failAssert0null983_failAssert0_literalMutationString2465 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tra[nslatebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_add813_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("g|azou|gm9wo");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_add813 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_add813_failAssert0null3430_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                    Mustache m = c.compile("g|azou|gm9wo");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                    TestUtil.getContents(AmplTranslateBundleTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_add813 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_add813_failAssert0null3430 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplTranslateBundleTest.root = new File(compiler, "src/test/resources/functions");
    }
}

