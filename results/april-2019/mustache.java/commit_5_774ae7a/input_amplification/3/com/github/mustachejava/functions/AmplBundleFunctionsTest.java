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
    public void testPostLabels_literalMutationString4341_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("DK$ Lo]u4-)S");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString4341 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template DK$ Lo]u4-)S not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4360_literalMutationString4823_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_literalMutationString4360__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_literalMutationString4360__11 = scope.put("replaceMe", "rplaced");
            Writer o_testPostLabels_literalMutationString4360__12 = m.execute(sw, scope);
            String o_testPostLabels_literalMutationString4360__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString4360_literalMutationString4823 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4360_literalMutationString4823_failAssert0_literalMutationString9456_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPostLabels_literalMutationString4360__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Object o_testPostLabels_literalMutationString4360__11 = scope.put("replaceMe", "rplaced");
                Writer o_testPostLabels_literalMutationString4360__12 = m.execute(sw, scope);
                String o_testPostLabels_literalMutationString4360__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4360_literalMutationString4823 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4360_literalMutationString4823_failAssert0_literalMutationString9456 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4341_failAssert0null6738_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("DK$ Lo]u4-)S");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4341 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4341_failAssert0null6738 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template DK$ Lo]u4-)S not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4348_literalMutationString5208_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_literalMutationString4348__9 = scope.put("trCans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_literalMutationString4348__11 = scope.put("replaceMe", "replaced");
            Writer o_testPostLabels_literalMutationString4348__12 = m.execute(sw, scope);
            String o_testPostLabels_literalMutationString4348__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString4348_literalMutationString5208 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4342_failAssert0_literalMutationString5594_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("^eF+>J)veqY");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4342 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4342_failAssert0_literalMutationString5594 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ^eF+>J)veqY not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4364_failAssert0_literalMutationString5738_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "h6zluCH?3)Y9%T|:._KQV9:");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4364 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4364_failAssert0_literalMutationString5738 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4341_failAssert0_add6377_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                c.compile("DK$ Lo]u4-)S");
                Mustache m = c.compile("DK$ Lo]u4-)S");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4341 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4341_failAssert0_add6377 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template DK$ Lo]u4-)S not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4339_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString4339 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4341_failAssert0_literalMutationString5804_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("DK$ Lo]u4-)S");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4341 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4341_failAssert0_literalMutationString5804 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template DK$ Lo]u4-)S not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4355_add6061_literalMutationString7467_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_literalMutationString4355__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_literalMutationString4355__11 = scope.put("r3placeMe", "replaced");
            Writer o_testPostLabels_literalMutationString4355_add6061__16 = m.execute(sw, scope);
            Writer o_testPostLabels_literalMutationString4355__12 = m.execute(sw, scope);
            String o_testPostLabels_literalMutationString4355__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString4355_add6061_literalMutationString7467 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4343_failAssert0_literalMutationString5642_failAssert0_literalMutationString9372_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "I=@<!yz#");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString4343 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString4343_failAssert0_literalMutationString5642 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4343_failAssert0_literalMutationString5642_failAssert0_literalMutationString9372 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString4360_literalMutationString4823_failAssert0_add10428_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPostLabels_literalMutationString4360__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Object o_testPostLabels_literalMutationString4360__11 = scope.put("replaceMe", "rplaced");
                Writer o_testPostLabels_literalMutationString4360__12 = m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                String o_testPostLabels_literalMutationString4360__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4360_literalMutationString4823 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4360_literalMutationString4823_failAssert0_add10428 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add21_remove934_literalMutationString1886_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("/_@)_(]4GfQD");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
            Object o_testPostNullLabels_add21__10 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_add21__12 = m.execute(sw, scope);
            String o_testPostNullLabels_add21__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            org.junit.Assert.fail("testPostNullLabels_add21_remove934_literalMutationString1886 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /_@)_(]4GfQD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabelsnull31_failAssert0_add836_failAssert0_literalMutationString2483_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("DS[rVTfD]vvo");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabelsnull31 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testPostNullLabelsnull31_failAssert0_add836 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testPostNullLabelsnull31_failAssert0_add836_failAssert0_literalMutationString2483 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template DS[rVTfD]vvo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0null1058_failAssert0_add3313_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0null1058 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0null1058_failAssert0_add3313 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0_add870_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_add870 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString18_failAssert0_add860_failAssert0_literalMutationString2340_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nullFabels.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString18 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString18_failAssert0_add860 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString18_failAssert0_add860_failAssert0_literalMutationString2340 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString14_failAssert0_add893_failAssert0_literalMutationString2376_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("r+SV})*;6Oq{");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString14 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString14_failAssert0_add893 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString14_failAssert0_add893_failAssert0_literalMutationString2376 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template r+SV})*;6Oq{ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add20_remove939_literalMutationString2078_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("z]@9DS^=MCqz");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_add20__9 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_add20__13 = m.execute(sw, scope);
            String o_testPostNullLabels_add20__15 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_add20_remove939_literalMutationString2078 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template z]@9DS^=MCqz not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString11_literalMutationString315_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("6mR{eM?TJ |E");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString11__9 = scope.put("trCns", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString11__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString11__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString11_literalMutationString315 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 6mR{eM?TJ |E not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add24_add680_literalMutationString2001_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bundles.ht]ml");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
            Object o_testPostNullLabels_add24__9 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_add24__11 = m.execute(sw, scope);
            sw.toString();
            String o_testPostNullLabels_add24__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_add24_add680_literalMutationString2001 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles.ht]ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0null1058_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0null1058 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0null1058_failAssert0_literalMutationString2273_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0null1058 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0null1058_failAssert0_literalMutationString2273 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add21_literalMutationString136_failAssert0_literalMutationString2667_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("U^x;Bc>o [9D");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
                Object o_testPostNullLabels_add21__10 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Writer o_testPostNullLabels_add21__12 = m.execute(sw, scope);
                String o_testPostNullLabels_add21__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "havG0BkqD}aYo $70%(U9d");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_add21_literalMutationString136 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_add21_literalMutationString136_failAssert0_literalMutationString2667 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template U^x;Bc>o [9D not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString5_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("B_O6#=6z(@%@");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString5 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template B_O6#=6z(@%@ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_add20null980_literalMutationString1349_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_add20__9 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostNullLabels_add20null980__13 = scope.put(null, BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_add20__13 = m.execute(sw, scope);
            String o_testPostNullLabels_add20__15 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_add20null980_literalMutationString1349 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0_literalMutationString481_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("uEotU", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0_literalMutationString481 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0null1058_failAssert0null3922_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0null1058 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_failAssert0null1058_failAssert0null3922 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplBundleFunctionsTest.root = new File(compiler, "src/test/resources/functions");
    }
}

