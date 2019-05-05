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
    public void testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931_failAssert0_add16806_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("NWrj`X_^@Jx2");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "Template _@`vBf7r7:O#u2`d4M not found");
                    scope.put("replaceMe", "Template _@`vBf7r7:O#u2`d4M not found");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7484 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931_failAssert0_add16806 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template NWrj`X_^@Jx2 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931_failAssert0_add16803_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    c.compile("NWrj`X_^@Jx2");
                    Mustache m = c.compile("NWrj`X_^@Jx2");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "Template _@`vBf7r7:O#u2`d4M not found");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7484 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931_failAssert0_add16803 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template NWrj`X_^@Jx2 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7484_failAssert0null9871_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("NWrj`X_^@Jx2");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put(null, "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString7484 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0null9871 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template NWrj`X_^@Jx2 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7483_failAssert0null9885_failAssert0_add16445_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US);
                    scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put(null, "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7483 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0null9885 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0null9885_failAssert0_add16445 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7489_literalMutationString8120_literalMutationString12865_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("JCc]+t[JK!CD");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreLabels_literalMutationString7489__9 = scope.put("Template _@`vBf7r7:O#u2`d4M not found", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreLabels_literalMutationString7489__11 = scope.put("replaceMe", "repaced");
            Writer o_testPreLabels_literalMutationString7489__12 = m.execute(sw, scope);
            String o_testPreLabels_literalMutationString7489__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString7489_literalMutationString8120_literalMutationString12865 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template JCc]+t[JK!CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931_failAssert0null18200_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("NWrj`X_^@Jx2");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "Template _@`vBf7r7:O#u2`d4M not found");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7484 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931_failAssert0null18200 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template NWrj`X_^@Jx2 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7506_failAssert0_literalMutationString8943_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "0J@Prx41mw<323N@ !`_@+");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString7506 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7506_failAssert0_literalMutationString8943 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7483_failAssert0_add9534_failAssert0null18049_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put(null, "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7483 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0_add9534 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0_add9534_failAssert0null18049 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7483_failAssert0null9885_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put(null, "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString7483 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0null9885 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7483_failAssert0_add9534_failAssert0_add16632_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7483 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0_add9534 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0_add9534_failAssert0_add16632 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_add7512_literalMutationString7871_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache o_testPreLabels_add7512__3 = c.compile("bundles.html");
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreLabels_add7512__10 = scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreLabels_add7512__12 = scope.put("replaceMe", "replaced");
            Writer o_testPreLabels_add7512__13 = m.execute(sw, scope);
            String o_testPreLabels_add7512__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_add7512_literalMutationString7871 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7483_failAssert0null9885_failAssert0_literalMutationString13533_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("rE6&(qK|&V6EA/JO!g^3f[#!zl2m5FfP@Ir p");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put(null, "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7483 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0null9885 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0null9885_failAssert0_literalMutationString13533 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template rE6&(qK|&V6EA/JO!g^3f[#!zl2m5FfP@Ir p not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("NWrj`X_^@Jx2");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "Template _@`vBf7r7:O#u2`d4M not found");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString7484 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template NWrj`X_^@Jx2 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931_failAssert0null18197_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("NWrj`X_^@Jx2");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put(null, BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "Template _@`vBf7r7:O#u2`d4M not found");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7484 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931_failAssert0null18197 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template NWrj`X_^@Jx2 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7483_failAssert0_add9534_failAssert0_add16629_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7483 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0_add9534 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0_add9534_failAssert0_add16629 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7488_literalMutationString8254_literalMutationString12838_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("$&E(HV2Y}Z:F");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreLabels_literalMutationString7488__9 = scope.put("", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreLabels_literalMutationString7488__11 = scope.put("replrceMe", "replaced");
            Writer o_testPreLabels_literalMutationString7488__12 = m.execute(sw, scope);
            String o_testPreLabels_literalMutationString7488__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString7488_literalMutationString8254_literalMutationString12838 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template $&E(HV2Y}Z:F not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7483_failAssert0null9885_failAssert0null17918_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put(null, "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7483 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0null9885 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0null9885_failAssert0null17918 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7484_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("NWrj`X_^@Jx2");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString7484 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template NWrj`X_^@Jx2 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7483_failAssert0_add9534_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString7483 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0_add9534 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7482() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map<String, Object> scope = new HashMap<>();
        Object o_testPreLabels_literalMutationString7482__9 = scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
        Assert.assertNull(o_testPreLabels_literalMutationString7482__9);
        Object o_testPreLabels_literalMutationString7482__11 = scope.put("replaceMe", "replaced");
        Assert.assertNull(o_testPreLabels_literalMutationString7482__11);
        Writer o_testPreLabels_literalMutationString7482__12 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPreLabels_literalMutationString7482__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPreLabels_literalMutationString7482__12)).toString());
        String o_testPreLabels_literalMutationString7482__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
        Assert.assertEquals("Translation bundles work!\nreplaced.Label2\n", o_testPreLabels_literalMutationString7482__13);
        sw.toString();
        Assert.assertNull(o_testPreLabels_literalMutationString7482__9);
        Assert.assertNull(o_testPreLabels_literalMutationString7482__11);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPreLabels_literalMutationString7482__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPreLabels_literalMutationString7482__12)).toString());
        Assert.assertEquals("Translation bundles work!\nreplaced.Label2\n", o_testPreLabels_literalMutationString7482__13);
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931_failAssert0_literalMutationString14742_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("NWrj`X_^@Jx2");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaeMe", "Template _@`vBf7r7:O#u2`d4M not found");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7484 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931_failAssert0_literalMutationString14742 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template NWrj`X_^@Jx2 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabelsnull7528_failAssert0_literalMutationString8597_failAssert0_literalMutationString14946_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "relaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPreLabelsnull7528 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testPreLabelsnull7528_failAssert0_literalMutationString8597 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabelsnull7528_failAssert0_literalMutationString8597_failAssert0_literalMutationString14946 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7483_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString7483 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7483_failAssert0_add9534_failAssert0_literalMutationString14109_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trns", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7483 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0_add9534 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0_add9534_failAssert0_literalMutationString14109 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931_failAssert0_literalMutationString14728_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "Template _@`vBf7r7:O#u2`d4M not found");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7484 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_literalMutationString8931_failAssert0_literalMutationString14728 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabelsnull7528_failAssert0_literalMutationString8597_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPreLabelsnull7528 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testPreLabelsnull7528_failAssert0_literalMutationString8597 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_remove7521null9654_literalMutationString10390_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreLabels_remove7521__9 = scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreLabels_remove7521__11 = scope.put(null, "replaced");
            String o_testPreLabels_remove7521__12 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_remove7521null9654_literalMutationString10390 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7510_failAssert0_literalMutationString8792_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "Template _@`vBf7r7:O#u2`d4M not found");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString7510 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7510_failAssert0_literalMutationString8792 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7483_failAssert0_literalMutationString8985_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("rep!aceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString7483 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0_literalMutationString8985 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7484_failAssert0_add9515_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("NWrj`X_^@Jx2");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US);
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString7484 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7484_failAssert0_add9515 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template NWrj`X_^@Jx2 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString7483_failAssert0_add9534_failAssert0_literalMutationString14110_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trzns", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreLabels_literalMutationString7483 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0_add9534 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString7483_failAssert0_add9534_failAssert0_literalMutationString14110 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_remove7521null9654_literalMutationString10392_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("ABjm6;#Q+A>K");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreLabels_remove7521__9 = scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreLabels_remove7521__11 = scope.put(null, "replaced");
            String o_testPreLabels_remove7521__12 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_remove7521null9654_literalMutationString10392 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ABjm6;#Q+A>K not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18891_failAssert0null21273_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("z&W<0]NMLz4*");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(null, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString18891 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18891_failAssert0null21273 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template z&W<0]NMLz4* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18892_failAssert0null21240_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bundles.`html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString18892 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18892_failAssert0null21240 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles.`html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18891_failAssert0_literalMutationString20309_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("z&W<0]NMLz4*");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString18891 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18891_failAssert0_literalMutationString20309 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template z&W<0]NMLz4* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18892_failAssert0_literalMutationString20147_failAssert0_add28375_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("bundles.`html");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString18892 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString18892_failAssert0_literalMutationString20147 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18892_failAssert0_literalMutationString20147_failAssert0_add28375 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles.`html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18897_literalMutationString19798_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_literalMutationString18897__9 = scope.put("trns", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_literalMutationString18897__11 = scope.put("replaceMe", "replaced");
            Writer o_testPostLabels_literalMutationString18897__12 = m.execute(sw, scope);
            String o_testPostLabels_literalMutationString18897__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString18897_literalMutationString19798 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18910_literalMutationString19507_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Z|T;HvGXW[}M");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_literalMutationString18910__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_literalMutationString18910__11 = scope.put("replaceMe", "#BxfDd]?");
            Writer o_testPostLabels_literalMutationString18910__12 = m.execute(sw, scope);
            String o_testPostLabels_literalMutationString18910__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString18910_literalMutationString19507 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Z|T;HvGXW[}M not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18890_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString18890 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18913_failAssert0_literalMutationString20382_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString18913 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18913_failAssert0_literalMutationString20382 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18891_failAssert0_add20914_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("z&W<0]NMLz4*");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US);
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString18891 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18891_failAssert0_add20914 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template z&W<0]NMLz4* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18892_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bundles.`html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString18892 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles.`html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18890_failAssert0_add20860_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString18890 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18890_failAssert0_add20860 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18904_literalMutationString19769_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bu|dles.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_literalMutationString18904__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_literalMutationString18904__11 = scope.put("replceMe", "replaced");
            Writer o_testPostLabels_literalMutationString18904__12 = m.execute(sw, scope);
            String o_testPostLabels_literalMutationString18904__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString18904_literalMutationString19769 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bu|dles.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_add18919_literalMutationString19342_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache o_testPostLabels_add18919__3 = c.compile("bundles.ht>ml");
            Mustache m = c.compile("bundles.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_add18919__10 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_add18919__12 = scope.put("replaceMe", "replaced");
            Writer o_testPostLabels_add18919__13 = m.execute(sw, scope);
            String o_testPostLabels_add18919__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_add18919_literalMutationString19342 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles.ht>ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18890_failAssert0_literalMutationString20099_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString18890 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18890_failAssert0_literalMutationString20099 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18890_failAssert0_add20860_failAssert0null29792_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", null);
                    m.execute(sw, scope);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString18890 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString18890_failAssert0_add20860 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18890_failAssert0_add20860_failAssert0null29792 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18890_failAssert0_literalMutationString20109_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundl5es_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString18890 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18890_failAssert0_literalMutationString20109 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabelsnull18932_literalMutationString19095_literalMutationString24947_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("=K*[NAK!|}D]");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabelsnull18932__9 = scope.put("PDkD-", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabelsnull18932__11 = scope.put("replaceMe", null);
            Writer o_testPostLabelsnull18932__12 = m.execute(sw, scope);
            String o_testPostLabelsnull18932__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabelsnull18932_literalMutationString19095_literalMutationString24947 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template =K*[NAK!|}D] not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18892_failAssert0_literalMutationString20147_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bundles.`html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString18892 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18892_failAssert0_literalMutationString20147 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles.`html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabelsnull18931_literalMutationString19066_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("NlXP:J,C)gei");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabelsnull18931__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabelsnull18931__11 = scope.put(null, "replaced");
            Writer o_testPostLabelsnull18931__12 = m.execute(sw, scope);
            String o_testPostLabelsnull18931__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabelsnull18931_literalMutationString19066 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template NlXP:J,C)gei not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_add18920_literalMutationString19138_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("{p(t,O>vjWBr");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_add18920__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_add18920__13 = scope.put("replaceMe", "replaced");
            Writer o_testPostLabels_add18920__14 = m.execute(sw, scope);
            String o_testPostLabels_add18920__15 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_add18920_literalMutationString19138 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {p(t,O>vjWBr not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabelsnull18931_remove20944_literalMutationString23074_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabelsnull18931__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabelsnull18931__11 = scope.put(null, "replaced");
            Writer o_testPostLabelsnull18931__12 = m.execute(sw, scope);
            String o_testPostLabelsnull18931__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            org.junit.Assert.fail("testPostLabelsnull18931_remove20944_literalMutationString23074 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18892_failAssert0_add20877_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bundles.`html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString18892 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18892_failAssert0_add20877 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles.`html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18894_failAssert0_literalMutationString20205_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("buntles]html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString18894 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18894_failAssert0_literalMutationString20205 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template buntles]html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18891_failAssert0null21273_failAssert0_add28040_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("z&W<0]NMLz4*");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString18891 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString18891_failAssert0null21273 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18891_failAssert0null21273_failAssert0_add28040 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template z&W<0]NMLz4* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18891_failAssert0null21273_failAssert0_literalMutationString25495_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("z`W<0]NMLz4*");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString18891 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString18891_failAssert0null21273 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18891_failAssert0null21273_failAssert0_literalMutationString25495 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template z`W<0]NMLz4* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabelsnull18931_add20413_literalMutationString24690_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("A<eR3Xs5(R.u");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabelsnull18931__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabelsnull18931__11 = scope.put(null, "replaced");
            Writer o_testPostLabelsnull18931__12 = m.execute(sw, scope);
            ((StringWriter) (o_testPostLabelsnull18931__12)).getBuffer().toString();
            String o_testPostLabelsnull18931__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabelsnull18931_add20413_literalMutationString24690 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template A<eR3Xs5(R.u not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_remove18927_literalMutationString19399_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_remove18927__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostLabels_remove18927__11 = m.execute(sw, scope);
            String o_testPostLabels_remove18927__12 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_remove18927_literalMutationString19399 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18890_failAssert0_add20862_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString18890 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18890_failAssert0_add20862 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabelsnull18930_remove20946_literalMutationString23098_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabelsnull18930__9 = scope.put(null, BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabelsnull18930__11 = scope.put("replaceMe", "replaced");
            Writer o_testPostLabelsnull18930__12 = m.execute(sw, scope);
            String o_testPostLabelsnull18930__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            org.junit.Assert.fail("testPostLabelsnull18930_remove20946_literalMutationString23098 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18891_failAssert0null21273_failAssert0null29473_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("z&W<0]NMLz4*");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put(null, BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString18891 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString18891_failAssert0null21273 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18891_failAssert0null21273_failAssert0null29473 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template z&W<0]NMLz4* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_add18920_add20447_literalMutationString22527_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache o_testPostLabels_add18920_add20447__3 = c.compile("bundles.html");
            Mustache m = c.compile("F|*M.PgZm;Ep");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_add18920__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_add18920__13 = scope.put("replaceMe", "replaced");
            Writer o_testPostLabels_add18920__14 = m.execute(sw, scope);
            String o_testPostLabels_add18920__15 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_add18920_add20447_literalMutationString22527 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template F|*M.PgZm;Ep not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18910null21089_failAssert0_literalMutationString25087_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPostLabels_literalMutationString18910__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Object o_testPostLabels_literalMutationString18910__11 = scope.put("replaceMe", "#BxfDd]?");
                Writer o_testPostLabels_literalMutationString18910__12 = m.execute(sw, scope);
                String o_testPostLabels_literalMutationString18910__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString18910null21089 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString18910null21089_failAssert0_literalMutationString25087 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18889() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map<String, Object> scope = new HashMap<>();
        Object o_testPostLabels_literalMutationString18889__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
        Assert.assertNull(o_testPostLabels_literalMutationString18889__9);
        Object o_testPostLabels_literalMutationString18889__11 = scope.put("replaceMe", "replaced");
        Assert.assertNull(o_testPostLabels_literalMutationString18889__11);
        Writer o_testPostLabels_literalMutationString18889__12 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPostLabels_literalMutationString18889__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPostLabels_literalMutationString18889__12)).toString());
        String o_testPostLabels_literalMutationString18889__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
        Assert.assertEquals("Translation bundles work!\nPost translate text works, too\n", o_testPostLabels_literalMutationString18889__13);
        sw.toString();
        Assert.assertNull(o_testPostLabels_literalMutationString18889__9);
        Assert.assertNull(o_testPostLabels_literalMutationString18889__11);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPostLabels_literalMutationString18889__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPostLabels_literalMutationString18889__12)).toString());
        Assert.assertEquals("Translation bundles work!\nPost translate text works, too\n", o_testPostLabels_literalMutationString18889__13);
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString18891_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("z&W<0]NMLz4*");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString18891 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template z&W<0]NMLz4* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30350_failAssert0null32691_failAssert0_add39350_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(" R^;#/H&du]=");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put(null, "replaced");
                    m.execute(sw, scope);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString30350 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0null32691 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0null32691_failAssert0_add39350 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  R^;#/H&du]= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30368_literalMutationString31280_failAssert0_add39638_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPreNullLabels_literalMutationString30368__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "roplaced");
                Object o_testPreNullLabels_literalMutationString30368__11 = scope.put("replaceMe", "roplaced");
                Writer o_testPreNullLabels_literalMutationString30368__12 = m.execute(sw, scope);
                String o_testPreNullLabels_literalMutationString30368__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30368_literalMutationString31280 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30368_literalMutationString31280_failAssert0_add39638 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30345_remove32418_add38911() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map<String, Object> scope = new HashMap<>();
        Object o_testPreNullLabels_literalMutationString30345__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
        Object o_testPreNullLabels_literalMutationString30345__11 = scope.put("replaceMe", "replaced");
        Writer o_testPreNullLabels_literalMutationString30345_remove32418_add38911__16 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPreNullLabels_literalMutationString30345_remove32418_add38911__16)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPreNullLabels_literalMutationString30345_remove32418_add38911__16)).toString());
        Writer o_testPreNullLabels_literalMutationString30345__12 = m.execute(sw, scope);
        String o_testPreNullLabels_literalMutationString30345__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
        Assert.assertEquals("Translation bundles work!\n\n", o_testPreNullLabels_literalMutationString30345__13);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPreNullLabels_literalMutationString30345_remove32418_add38911__16)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPreNullLabels_literalMutationString30345_remove32418_add38911__16)).toString());
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30350_failAssert0null32691_failAssert0_add39347_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(" R^;#/H&du]=");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put(null, "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString30350 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0null32691 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0null32691_failAssert0_add39347 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  R^;#/H&du]= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_remove30383_literalMutationString30884_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreNullLabels_remove30383__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPreNullLabels_remove30383__11 = m.execute(sw, scope);
            String o_testPreNullLabels_remove30383__12 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_remove30383_literalMutationString30884 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30345() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map<String, Object> scope = new HashMap<>();
        Object o_testPreNullLabels_literalMutationString30345__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
        Assert.assertNull(o_testPreNullLabels_literalMutationString30345__9);
        Object o_testPreNullLabels_literalMutationString30345__11 = scope.put("replaceMe", "replaced");
        Assert.assertNull(o_testPreNullLabels_literalMutationString30345__11);
        Writer o_testPreNullLabels_literalMutationString30345__12 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPreNullLabels_literalMutationString30345__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPreNullLabels_literalMutationString30345__12)).toString());
        String o_testPreNullLabels_literalMutationString30345__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
        Assert.assertEquals("Translation bundles work!\n\n", o_testPreNullLabels_literalMutationString30345__13);
        sw.toString();
        Assert.assertNull(o_testPreNullLabels_literalMutationString30345__9);
        Assert.assertNull(o_testPreNullLabels_literalMutationString30345__11);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPreNullLabels_literalMutationString30345__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPreNullLabels_literalMutationString30345__12)).toString());
        Assert.assertEquals("Translation bundles work!\n\n", o_testPreNullLabels_literalMutationString30345__13);
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30350_failAssert0_add32330_failAssert0_literalMutationString37168_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(" R^;#/H&du]=");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
                    scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("Template _@`vBf7r7:O#u2`d4M not found", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString30350 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0_add32330 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0_add32330_failAssert0_literalMutationString37168 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  R^;#/H&du]= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_add30376_literalMutationString30732_failAssert0_literalMutationString37481_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bundl|es.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPreNullLabels_add30376__9 = scope.put("Template _@`vBf7r7:O#u2`d4M not found", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Object o_testPreNullLabels_add30376__13 = scope.put("replaceMe", "replaced");
                Writer o_testPreNullLabels_add30376__14 = m.execute(sw, scope);
                String o_testPreNullLabels_add30376__15 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_add30376_literalMutationString30732 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_add30376_literalMutationString30732_failAssert0_literalMutationString37481 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundl|es.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabelsnull30388_literalMutationString30567_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreNullLabelsnull30388__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreNullLabelsnull30388__11 = scope.put("replaceMe", null);
            Writer o_testPreNullLabelsnull30388__12 = m.execute(sw, scope);
            String o_testPreNullLabelsnull30388__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabelsnull30388_literalMutationString30567 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_add30376_literalMutationString30732_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bundl|es.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreNullLabels_add30376__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreNullLabels_add30376__13 = scope.put("replaceMe", "replaced");
            Writer o_testPreNullLabels_add30376__14 = m.execute(sw, scope);
            String o_testPreNullLabels_add30376__15 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_add30376_literalMutationString30732 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundl|es.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30350_failAssert0_add32330_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile(" R^;#/H&du]=");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30350 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0_add32330 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  R^;#/H&du]= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30345_literalMutationString30990_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("]");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreNullLabels_literalMutationString30345__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreNullLabels_literalMutationString30345__11 = scope.put("replaceMe", "replaced");
            Writer o_testPreNullLabels_literalMutationString30345__12 = m.execute(sw, scope);
            String o_testPreNullLabels_literalMutationString30345__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30345_literalMutationString30990 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ] not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30350_failAssert0null32691_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile(" R^;#/H&du]=");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put(null, "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30350 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0null32691 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  R^;#/H&du]= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30368_literalMutationString31280_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreNullLabels_literalMutationString30368__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreNullLabels_literalMutationString30368__11 = scope.put("replaceMe", "roplaced");
            Writer o_testPreNullLabels_literalMutationString30368__12 = m.execute(sw, scope);
            String o_testPreNullLabels_literalMutationString30368__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30368_literalMutationString31280 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30350_failAssert0_literalMutationString31623_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile(" R^;#/H&du]=");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabel-.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30350 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0_literalMutationString31623 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  R^;#/H&du]= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30350_failAssert0null32691_failAssert0_literalMutationString36836_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(" R^;#x/H&du]=");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put(null, "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString30350 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0null32691 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0null32691_failAssert0_literalMutationString36836 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  R^;#x/H&du]= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30350_failAssert0null32691_failAssert0_literalMutationString36846_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(" R^;#/H&du]=");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put(null, "");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString30350 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0null32691 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0null32691_failAssert0_literalMutationString36846 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  R^;#/H&du]= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_add30376_literalMutationString30732_failAssert0_add39537_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bundl|es.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPreNullLabels_add30376__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Object o_testPreNullLabels_add30376__13 = scope.put("replaceMe", "replaced");
                Writer o_testPreNullLabels_add30376__14 = m.execute(sw, scope);
                String o_testPreNullLabels_add30376__15 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_add30376_literalMutationString30732 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_add30376_literalMutationString30732_failAssert0_add39537 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundl|es.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30348_failAssert0_literalMutationString31746_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30348 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30348_failAssert0_literalMutationString31746 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30350_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile(" R^;#/H&du]=");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30350 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  R^;#/H&du]= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30368_literalMutationString31280_failAssert0null41009_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPreNullLabels_literalMutationString30368__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Object o_testPreNullLabels_literalMutationString30368__11 = scope.put("replaceMe", "roplaced");
                Writer o_testPreNullLabels_literalMutationString30368__12 = m.execute(sw, scope);
                String o_testPreNullLabels_literalMutationString30368__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30368_literalMutationString31280 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30368_literalMutationString31280_failAssert0null41009 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30368_literalMutationString31280_failAssert0_literalMutationString37878_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPreNullLabels_literalMutationString30368__9 = scope.put("5$eu^", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Object o_testPreNullLabels_literalMutationString30368__11 = scope.put("replaceMe", "roplaced");
                Writer o_testPreNullLabels_literalMutationString30368__12 = m.execute(sw, scope);
                String o_testPreNullLabels_literalMutationString30368__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30368_literalMutationString31280 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30368_literalMutationString31280_failAssert0_literalMutationString37878 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30350_failAssert0null32691_failAssert0null40760_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(" R^;#/H&du]=");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put(null, "replaced");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString30350 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0null32691 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0null32691_failAssert0null40760 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  R^;#/H&du]= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30346_failAssert0_literalMutationString31632_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trns", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30346 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30346_failAssert0_literalMutationString31632 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30346_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30346 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30367_literalMutationString31168_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("8pH0L+e)E%XT");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreNullLabels_literalMutationString30367__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreNullLabels_literalMutationString30367__11 = scope.put("replaceMe", "Knn[&,LX");
            Writer o_testPreNullLabels_literalMutationString30367__12 = m.execute(sw, scope);
            String o_testPreNullLabels_literalMutationString30367__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30367_literalMutationString31168 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 8pH0L+e)E%XT not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30350_failAssert0_add32330_failAssert0_add39444_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile(" R^;#/H&du]=");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
                    scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    scope.put("replaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString30350 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0_add32330 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30350_failAssert0_add32330_failAssert0_add39444 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  R^;#/H&du]= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30371_failAssert0_literalMutationString31693_failAssert0_literalMutationString37812_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("bundles.]tml");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("tras", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nullabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString30371 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30371_failAssert0_literalMutationString31693 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30371_failAssert0_literalMutationString31693_failAssert0_literalMutationString37812 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles.]tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_remove30383_add32029_literalMutationString34400_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache o_testPreNullLabels_remove30383_add32029__3 = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            Mustache m = c.compile("bundles.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreNullLabels_remove30383__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPreNullLabels_remove30383__11 = m.execute(sw, scope);
            String o_testPreNullLabels_remove30383__12 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_remove30383_add32029_literalMutationString34400 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30346_failAssert0_add32340_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString30346 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30346_failAssert0_add32340 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString30351_literalMutationString31264_literalMutationString36440_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreNullLabels_literalMutationString30351__9 = scope.put("", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreNullLabels_literalMutationString30351__11 = scope.put("replac)eMe", "replaced");
            Writer o_testPreNullLabels_literalMutationString30351__12 = m.execute(sw, scope);
            String o_testPreNullLabels_literalMutationString30351__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString30351_literalMutationString31264_literalMutationString36440 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString6_failAssert0_literalMutationString537_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("!U{_Zh a{V2Y");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("Template _@`vBf7r7:O#u2`d4M not found", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString6_failAssert0_literalMutationString537 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !U{_Zh a{V2Y not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add19_literalMutationString166_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache o_testPostNullLabels_add19__3 = c.compile("bundles.html");
            Mustache m = c.compile("bundles.h^ml");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_add19__10 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_add19__12 = m.execute(sw, scope);
            String o_testPostNullLabels_add19__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_add19_literalMutationString166 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles.h^ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString18_failAssert0_literalMutationString513_failAssert0_add5529_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabls.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString18 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString18_failAssert0_literalMutationString513 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString18_failAssert0_literalMutationString513_failAssert0_add5529 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString15_failAssert0_literalMutationString620_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("5:{koWpQ5UtC");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "Template _@`vBf7r7:O#u2`d4M not found");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString15 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString15_failAssert0_literalMutationString620 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5:{koWpQ5UtC not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString6_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("!U{_Zh a{V2Y");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString6 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !U{_Zh a{V2Y not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString9_literalMutationString347_failAssert0_literalMutationString3498_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bun^dles.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPostNullLabels_literalMutationString9__9 = scope.put("tHrans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Writer o_testPostNullLabels_literalMutationString9__11 = m.execute(sw, scope);
                String o_testPostNullLabels_literalMutationString9__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "Template _@`vBf7r7:O#u2`d4M not found");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString9_literalMutationString347 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString9_literalMutationString347_failAssert0_literalMutationString3498 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bun^dles.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString9_literalMutationString347_failAssert0null6691_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bun^dles.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPostNullLabels_literalMutationString9__9 = scope.put("tHrans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Writer o_testPostNullLabels_literalMutationString9__11 = m.execute(null, scope);
                String o_testPostNullLabels_literalMutationString9__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString9_literalMutationString347 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString9_literalMutationString347_failAssert0null6691 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bun^dles.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString6_failAssert0_literalMutationString537_failAssert0null6779_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("!U{_Zh a{V2Y");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("Template _@`vBf7r7:O#u2`d4M not found", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString6 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString6_failAssert0_literalMutationString537 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString6_failAssert0_literalMutationString537_failAssert0null6779 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !U{_Zh a{V2Y not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString6_failAssert0_add894_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("!U{_Zh a{V2Y");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString6_failAssert0_add894 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !U{_Zh a{V2Y not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add19_literalMutationString167_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache o_testPostNullLabels_add19__3 = c.compile("bundles.html");
            Mustache m = c.compile("bund>les.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_add19__10 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_add19__12 = m.execute(sw, scope);
            String o_testPostNullLabels_add19__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_add19_literalMutationString167 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bund>les.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString3_failAssert0_add872_failAssert0_literalMutationString3170_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("bundle-s.ht ml");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString3 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString3_failAssert0_add872 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString3_failAssert0_add872_failAssert0_literalMutationString3170 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundle-s.ht ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0_add913_failAssert0_add5418_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_add913 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_add913_failAssert0_add5418 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString18_failAssert0_literalMutationString513_failAssert0_literalMutationString3532_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString18 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString18_failAssert0_literalMutationString513 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString18_failAssert0_literalMutationString513_failAssert0_literalMutationString3532 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString6_failAssert0_literalMutationString537_failAssert0_add5638_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("!U{_Zh a{V2Y");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("Template _@`vBf7r7:O#u2`d4M not found", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString6 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString6_failAssert0_literalMutationString537 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString6_failAssert0_literalMutationString537_failAssert0_add5638 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !U{_Zh a{V2Y not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString17_failAssert0_add858_failAssert0_literalMutationString4115_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_null6abels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString17 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString17_failAssert0_add858 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString17_failAssert0_add858_failAssert0_literalMutationString4115 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString10_literalMutationString380_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString10__9 = scope.put("tras", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString10__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString10__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString10_literalMutationString380 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0_add913_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_add913 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabelsnull31_failAssert0_literalMutationString432_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bund[es.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPostNullLabelsnull31 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testPostNullLabelsnull31_failAssert0_literalMutationString432 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bund[es.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0_literalMutationString590_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("Template _@`vBf7r7:O#u2`d4M not found", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_literalMutationString590 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0_add914_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_add914 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString18_failAssert0_literalMutationString513_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabls.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString18 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString18_failAssert0_literalMutationString513 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0_literalMutationString589_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_literalMutationString589 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabelsnull31_failAssert0_literalMutationString428_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPostNullLabelsnull31 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testPostNullLabelsnull31_failAssert0_literalMutationString428 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0_add913_failAssert0_literalMutationString3245_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundlqes_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_add913 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_add913_failAssert0_literalMutationString3245 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0_add914_failAssert0_add5814_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    m.execute(sw, scope);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_add914 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_add914_failAssert0_add5814 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString11_literalMutationString279_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bundles.<tml");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString11__9 = scope.put("t!ans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString11__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString11__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString11_literalMutationString279 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles.<tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_remove946_add4906() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map<String, Object> scope = new HashMap<>();
        Object o_testPostNullLabels_literalMutationString1__9 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
        Writer o_testPostNullLabels_literalMutationString1_remove946_add4906__13 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPostNullLabels_literalMutationString1_remove946_add4906__13)).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPostNullLabels_literalMutationString1_remove946_add4906__13)).getBuffer())).toString());
        Writer o_testPostNullLabels_literalMutationString1__11 = m.execute(sw, scope);
        String o_testPostNullLabels_literalMutationString1__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
        Assert.assertEquals("Translation bundles work!\n\n", o_testPostNullLabels_literalMutationString1__13);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPostNullLabels_literalMutationString1_remove946_add4906__13)).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPostNullLabels_literalMutationString1_remove946_add4906__13)).getBuffer())).toString());
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0_add914_failAssert0null6887_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_add914 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_add914_failAssert0null6887 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map<String, Object> scope = new HashMap<>();
        Object o_testPostNullLabels_literalMutationString1__9 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
        Assert.assertNull(o_testPostNullLabels_literalMutationString1__9);
        Writer o_testPostNullLabels_literalMutationString1__11 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPostNullLabels_literalMutationString1__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPostNullLabels_literalMutationString1__11)).toString());
        String o_testPostNullLabels_literalMutationString1__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
        Assert.assertEquals("Translation bundles work!\n\n", o_testPostNullLabels_literalMutationString1__13);
        sw.toString();
        Assert.assertNull(o_testPostNullLabels_literalMutationString1__9);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPostNullLabels_literalMutationString1__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPostNullLabels_literalMutationString1__11)).toString());
        Assert.assertEquals("Translation bundles work!\n\n", o_testPostNullLabels_literalMutationString1__13);
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0_add914_failAssert0_literalMutationString4135_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4 not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_add914 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_add914_failAssert0_literalMutationString4135 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4 not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString9_literalMutationString347_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bun^dles.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString9__9 = scope.put("tHrans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString9__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString9__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString9_literalMutationString347 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bun^dles.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString7_remove945_literalMutationString2100_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("D?[t|,2D!Fr,");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString7__9 = scope.put("", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString7__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString7__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            org.junit.Assert.fail("testPostNullLabels_literalMutationString7_remove945_literalMutationString2100 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template D?[t|,2D!Fr, not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add21_literalMutationString140_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("_I}CIJZ&XE3z");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
            Object o_testPostNullLabels_add21__10 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_add21__12 = m.execute(sw, scope);
            String o_testPostNullLabels_add21__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_add21_literalMutationString140 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template _I}CIJZ&XE3z not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0null1088_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template _@`vBf7r7:O#u2`d4M not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0null1088 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template _@`vBf7r7:O#u2`d4M not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString9_literalMutationString347_failAssert0_add5514_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bun^dles.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("tHrans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Object o_testPostNullLabels_literalMutationString9__9 = scope.put("tHrans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Writer o_testPostNullLabels_literalMutationString9__11 = m.execute(sw, scope);
                String o_testPostNullLabels_literalMutationString9__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString9_literalMutationString347 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString9_literalMutationString347_failAssert0_add5514 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bun^dles.html not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplBundleFunctionsTest.root = new File(compiler, "src/test/resources/functions");
    }
}

