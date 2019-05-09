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
    public void testPreLabels_add1356_literalMutationString1575_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("&t).&,G!q>[z");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreLabels_add1356__9 = scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreLabels_add1356__11 = scope.put("replaceMe", "replaced");
            Writer o_testPreLabels_add1356__12 = m.execute(sw, scope);
            Writer o_testPreLabels_add1356__13 = m.execute(sw, scope);
            String o_testPreLabels_add1356__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_add1356_literalMutationString1575 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template &t).&,G!q>[z not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString1324_failAssert0null3713_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(null, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString1324 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString1324_failAssert0null3713 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString1324_failAssert0_add3358_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString1324 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString1324_failAssert0_add3358 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString1324_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template XI%U^=`EY>I, not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString1324 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString1324_failAssert0_literalMutationString2775_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString1324 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString1324_failAssert0_literalMutationString2775 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString1327_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("UnXOsF2;b]V@");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString1327 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template UnXOsF2;b]V@ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString1345_literalMutationString1939_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template XI%U^=`EY>I, not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreLabels_literalMutationString1345__9 = scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreLabels_literalMutationString1345__11 = scope.put("replaceMe", "r>placed");
            Writer o_testPreLabels_literalMutationString1345__12 = m.execute(sw, scope);
            String o_testPreLabels_literalMutationString1345__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString1345_literalMutationString1939 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString3988_failAssert0_literalMutationString5361_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString3988 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString3988_failAssert0_literalMutationString5361 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString3988_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template XI%U^=`EY>I, not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString3988 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4002_literalMutationString5043_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("4]d`!! /pV*k");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_literalMutationString4002__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_literalMutationString4002__11 = scope.put("rplaceMe", "replaced");
            Writer o_testPostLabels_literalMutationString4002__12 = m.execute(sw, scope);
            String o_testPostLabels_literalMutationString4002__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString4002_literalMutationString5043 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4]d`!! /pV*k not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_remove4024_literalMutationString4498_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template XI%U^=`EY>I, not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_remove4024__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostLabels_remove4024__11 = m.execute(sw, scope);
            String o_testPostLabels_remove4024__12 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_remove4024_literalMutationString4498 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString3989_failAssert0_add6020_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("lA6}!oEX[&%o");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString3989 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString3989_failAssert0_add6020 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lA6}!oEX[&%o not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString3987_failAssert0null6383_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bundl[s.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", null);
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString3987 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString3987_failAssert0null6383 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundl[s.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString3987_failAssert0_add6032_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bundl[s.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString3987 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString3987_failAssert0_add6032 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundl[s.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString3988_failAssert0_add6004_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString3988 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString3988_failAssert0_add6004 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString3989_failAssert0_literalMutationString5440_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("lA6}!oEX[&%o");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "v}!QvsT]2gx(Q.,m<={_]R@");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString3989 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString3989_failAssert0_literalMutationString5440 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lA6}!oEX[&%o not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString3989_failAssert0null6376_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("lA6}!oEX[&%o");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", null);
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString3989 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString3989_failAssert0null6376 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lA6}!oEX[&%o not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString3987_failAssert0_literalMutationString5452_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bundl[s.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("Template XI%U^=`EY>I, not found", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString3987 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString3987_failAssert0_literalMutationString5452 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundl[s.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString3987_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bundl[s.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString3987 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundl[s.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString3989_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("lA6}!oEX[&%o");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString3989 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lA6}!oEX[&%o not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString3987_failAssert0_literalMutationString5468_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bundl[s.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replacd");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString3987 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString3987_failAssert0_literalMutationString5468 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundl[s.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString3988_failAssert0null6365_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString3988 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString3988_failAssert0null6365 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString6641_failAssert0null8991_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put(null, BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString6641 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString6641_failAssert0null8991 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString6641_failAssert0_add8632_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString6641 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString6641_failAssert0_add8632 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString6650_literalMutationString7461_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bundles]html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreNullLabels_literalMutationString6650__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreNullLabels_literalMutationString6650__11 = scope.put("", "replaced");
            Writer o_testPreNullLabels_literalMutationString6650__12 = m.execute(sw, scope);
            String o_testPreNullLabels_literalMutationString6650__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString6650_literalMutationString7461 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles]html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString6641_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template XI%U^=`EY>I, not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString6641 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString6641_failAssert0_literalMutationString7938_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replfced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString6641 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString6641_failAssert0_literalMutationString7938 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_add6671_literalMutationString6878_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template XI%U^=`EY>I, not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreNullLabels_add6671__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreNullLabels_add6671__11 = scope.put("replaceMe", "replaced");
            Object o_testPreNullLabels_add6671__12 = scope.put("replaceMe", "replaced");
            Writer o_testPreNullLabels_add6671__13 = m.execute(sw, scope);
            String o_testPreNullLabels_add6671__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_add6671_literalMutationString6878 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0null1093_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("`(wHSJD|Zkcl");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0null1093 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `(wHSJD|Zkcl not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add22_literalMutationString132_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("N:e>dA_w=`V ");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_add22__9 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_add22__11 = m.execute(sw, scope);
            Writer o_testPostNullLabels_add22__13 = m.execute(sw, scope);
            String o_testPostNullLabels_add22__15 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString132 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template N:e>dA_w=`V  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString3_failAssert0_add930_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString3_failAssert0_add930 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString16_failAssert0_literalMutationString439_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("tUv]^`mC*vj|");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nuulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString16 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString16_failAssert0_literalMutationString439 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tUv]^`mC*vj| not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString12_literalMutationString280_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template XI%U^=`EY>I, not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString12__9 = scope.put("lkh2a", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString12__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString12__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString12_literalMutationString280 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add920_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("`(wHSJD|Zkcl");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add920 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `(wHSJD|Zkcl not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_literalMutationString600_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString600 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString3_failAssert0_literalMutationString629_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "Template XI%U^=`EY>I, not found");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString3_failAssert0_literalMutationString629 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabelsnull28_literalMutationString238_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template XI%U^=`EY>I, not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabelsnull28__9 = scope.put(null, BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabelsnull28__11 = m.execute(sw, scope);
            String o_testPostNullLabelsnull28__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabelsnull28_literalMutationString238 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString10_literalMutationString347_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("c/#7YX=]6(k ");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString10__9 = scope.put("trns", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString10__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString10__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString10_literalMutationString347 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template c/#7YX=]6(k  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString3_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template XI%U^=`EY>I, not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString3 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("`(wHSJD|Zkcl");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `(wHSJD|Zkcl not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplBundleFunctionsTest.root = new File(compiler, "src/test/resources/functions");
    }
}

