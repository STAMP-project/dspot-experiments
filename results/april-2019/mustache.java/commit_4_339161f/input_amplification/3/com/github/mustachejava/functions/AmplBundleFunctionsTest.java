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


public class AmplBundleFunctionsTest {
    private static File root;

    private static final String BUNDLE = "com.github.mustachejava.functions.translatebundle";

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString10914_literalMutationString11488_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("&@r6Ona:uwZv");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreLabels_literalMutationString10914__9 = scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreLabels_literalMutationString10914__11 = scope.put("replaceMe", "repliaced");
            Writer o_testPreLabels_literalMutationString10914__12 = m.execute(sw, scope);
            String o_testPreLabels_literalMutationString10914__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            String o_testPreLabels_literalMutationString10914__14 = sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString10914_literalMutationString11488 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template &@r6Ona:uwZv not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString10898_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("0&%)sb@uMq[j");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString10898 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 0&%)sb@uMq[j not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString10917_literalMutationString11605_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("/JY6*!d25]Hu");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreLabels_literalMutationString10917__9 = scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreLabels_literalMutationString10917__11 = scope.put("replaceMe", "replaGed");
            Writer o_testPreLabels_literalMutationString10917__12 = m.execute(sw, scope);
            String o_testPreLabels_literalMutationString10917__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            String o_testPreLabels_literalMutationString10917__14 = sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString10917_literalMutationString11605 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString10899_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bundles[.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString10899 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles[.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0_add6090_failAssert0_literalMutationString9075_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(":po1n8]SHsE6");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("relaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_add6090 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_add6090_failAssert0_literalMutationString9075 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0_add6090_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile(":po1n8]SHsE6");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_add6090 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0null10548_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(":po1n8]SHsE6");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put(null, BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0null10548 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0_literalMutationString8873_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(":po1n8]SHsE6");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0_literalMutationString8873 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0_add6090_failAssert0_add9953_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(":po1n8]SHsE6");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    scope.put("replaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_add6090 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_add6090_failAssert0_add9953 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile(":po1n8]SHsE6");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile(":po1n8]SHsE6");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0_add6086_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile(":po1n8]SHsE6");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_add6086 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0_literalMutationString5543_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile(":po1n8]SHsE6");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("rplaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_literalMutationString5543 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0_add9885_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(":po1n8]SHsE6");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0_add9885 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4041_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("b`undles.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString4041 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template b`undles.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0_literalMutationString5531_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile(":po1zn8]SHsE6");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_literalMutationString5531 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :po1zn8]SHsE6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0_literalMutationString22543_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("E)nT}(({|eAn");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0_literalMutationString22543 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0_add23437_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("E)nT}(({|eAn");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0_add23437 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17674_failAssert0_literalMutationString18939_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("C3e.793<a[+j");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "}(LG-[^]hrY.Zp`l9x]!d(");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17674 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17674_failAssert0_literalMutationString18939 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template C3e.793<a[+j not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0_add19656_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("E)nT}(({|eAn");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0_add19656 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("E)nT}(({|eAn");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0_literalMutationString18985_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("E)nT}(({|eAn");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("FxP/<Hy0T", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0_literalMutationString18985 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("E)nT}(({|eAn");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(null, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0null24071_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("E)nT}(({|eAn");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0null24071 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_add17675_literalMutationString17823_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache o_testPreNullLabels_add17675__3 = c.compile("bundles.h<ml");
            Mustache m = c.compile("bundles.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreNullLabels_add17675__10 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreNullLabels_add17675__12 = scope.put("replaceMe", "replaced");
            Writer o_testPreNullLabels_add17675__13 = m.execute(sw, scope);
            String o_testPreNullLabels_add17675__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            String o_testPreNullLabels_add17675__15 = sw.toString();
            org.junit.Assert.fail("testPreNullLabels_add17675_literalMutationString17823 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles.h<ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString9_literalMutationString282_literalMutationString1632_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("g|azou|gm9wo");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString9__9 = scope.put("bundles5post_labels.txt", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString9__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString9__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            String o_testPostNullLabels_literalMutationString9__14 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString9_literalMutationString282_literalMutationString1632 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0null3618_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(":i Mgy2-k1 z");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put(null, BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0null3618 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :i Mgy2-k1 z not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0_add3082_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(":i Mgy2-k1 z");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0_add3082 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :i Mgy2-k1 z not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("7r[}eQ>{]^tQ");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString13 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 7r[}eQ>{]^tQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add22_literalMutationString223_failAssert0_add3122_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("fSGjyR[IMIsf");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Object o_testPostNullLabels_add22__9 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Writer o_testPostNullLabels_add22__11 = m.execute(sw, scope);
                Writer o_testPostNullLabels_add22__13 = m.execute(sw, scope);
                String o_testPostNullLabels_add22__15 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                String o_testPostNullLabels_add22__16 = sw.toString();
                org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223_failAssert0_add3122 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fSGjyR[IMIsf not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0_literalMutationString2153_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(":i Mgy2-k1 z");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "Tundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0_literalMutationString2153 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :i Mgy2-k1 z not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0null3679_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("7r[}eQ>{]^tQ");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString13 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0null3679 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 7r[}eQ>{]^tQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add22_literalMutationString223_failAssert0_literalMutationString2257_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("fSGjyR[IMIsf");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPostNullLabels_add22__9 = scope.put("t2rans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Writer o_testPostNullLabels_add22__11 = m.execute(sw, scope);
                Writer o_testPostNullLabels_add22__13 = m.execute(sw, scope);
                String o_testPostNullLabels_add22__15 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                String o_testPostNullLabels_add22__16 = sw.toString();
                org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223_failAssert0_literalMutationString2257 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fSGjyR[IMIsf not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add22_literalMutationString223_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("fSGjyR[IMIsf");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_add22__9 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_add22__11 = m.execute(sw, scope);
            Writer o_testPostNullLabels_add22__13 = m.execute(sw, scope);
            String o_testPostNullLabels_add22__15 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            String o_testPostNullLabels_add22__16 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fSGjyR[IMIsf not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString10_literalMutationString379_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("gY$e5wb>3`97");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString10__9 = scope.put("tra^ns", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString10__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString10__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            String o_testPostNullLabels_literalMutationString10__14 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString10_literalMutationString379 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gY$e5wb>3`97 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile(":i Mgy2-k1 z");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :i Mgy2-k1 z not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString10_literalMutationString379_failAssert0_add3325_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("gY$e5wb>3`97");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("tra^ns", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Object o_testPostNullLabels_literalMutationString10__9 = scope.put("tra^ns", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Writer o_testPostNullLabels_literalMutationString10__11 = m.execute(sw, scope);
                String o_testPostNullLabels_literalMutationString10__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                String o_testPostNullLabels_literalMutationString10__14 = sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString10_literalMutationString379 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString10_literalMutationString379_failAssert0_add3325 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gY$e5wb>3`97 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add22_literalMutationString223_failAssert0_literalMutationString2254_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("fSGjyR[IMIsf");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPostNullLabels_add22__9 = scope.put("Nrans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Writer o_testPostNullLabels_add22__11 = m.execute(sw, scope);
                Writer o_testPostNullLabels_add22__13 = m.execute(sw, scope);
                String o_testPostNullLabels_add22__15 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                String o_testPostNullLabels_add22__16 = sw.toString();
                org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223_failAssert0_literalMutationString2254 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fSGjyR[IMIsf not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabelsnull31_failAssert0_literalMutationString606_failAssert0_literalMutationString2453_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(":");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabelsnull31 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testPostNullLabelsnull31_failAssert0_literalMutationString606 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testPostNullLabelsnull31_failAssert0_literalMutationString606_failAssert0_literalMutationString2453 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template : not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add22_literalMutationString223_failAssert0_add3121_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                c.compile("fSGjyR[IMIsf");
                Mustache m = c.compile("fSGjyR[IMIsf");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPostNullLabels_add22__9 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Writer o_testPostNullLabels_add22__11 = m.execute(sw, scope);
                Writer o_testPostNullLabels_add22__13 = m.execute(sw, scope);
                String o_testPostNullLabels_add22__15 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                String o_testPostNullLabels_add22__16 = sw.toString();
                org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223_failAssert0_add3121 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fSGjyR[IMIsf not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString15_failAssert0_literalMutationString534_failAssert0_literalMutationString2751_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("7s#4=5^sn6P>");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString15 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString15_failAssert0_literalMutationString534 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString15_failAssert0_literalMutationString534_failAssert0_literalMutationString2751 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 7s#4=5^sn6P> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_literalMutationString557_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile(":i Mgy2-k1 z");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels:.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString557 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :i Mgy2-k1 z not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString10_literalMutationString379_failAssert0null3785_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("gY$e5wb>3`97");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPostNullLabels_literalMutationString10__9 = scope.put("tra^ns", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Writer o_testPostNullLabels_literalMutationString10__11 = m.execute(sw, scope);
                String o_testPostNullLabels_literalMutationString10__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                String o_testPostNullLabels_literalMutationString10__14 = sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString10_literalMutationString379 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString10_literalMutationString379_failAssert0null3785 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gY$e5wb>3`97 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile(":i Mgy2-k1 z");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :i Mgy2-k1 z not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0_literalMutationString2346_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("7r[}eQ>{]^tQ");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString13 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0_literalMutationString2346 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 7r[}eQ>{]^tQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0_add3159_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("7r[}eQ>{]^tQ");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "");
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString13 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0_add3159 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 7r[}eQ>{]^tQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add21_literalMutationString116_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("tg6u14;-&Q,^");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
            Object o_testPostNullLabels_add21__10 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_add21__12 = m.execute(sw, scope);
            String o_testPostNullLabels_add21__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            String o_testPostNullLabels_add21__15 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_add21_literalMutationString116 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tg6u14;-&Q,^ not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplBundleFunctionsTest.root = new File(compiler, "src/test/resources/functions");
    }
}

