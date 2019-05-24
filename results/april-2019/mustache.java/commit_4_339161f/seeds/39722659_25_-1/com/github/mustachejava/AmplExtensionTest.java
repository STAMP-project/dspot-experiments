package com.github.mustachejava;


import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class AmplExtensionTest {
    private static File root;

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString1033_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("nZ[)zqi}4Qw.>*e");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString1033 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nZ[)zqi}4Qw.>*e not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString497_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("_El!)*mM*C7v?q*7rMw*}W");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString497 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template _El!)*mM*C7v?q*7rMw*}W not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString200_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("^u4riHkM!{PgqReh&");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_literalMutationString200 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ^u4riHkM!{PgqReh& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString267_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("g(fS`6&3QcTEDWKjZr9ah]`");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString267 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g(fS`6&3QcTEDWKjZr9ah]` not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString403_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("]7q*Tt=+eVs ");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String replace = "false";
            });
            TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString403 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ]7q*Tt=+eVs  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString850_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("subblockchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile(" -|w]9Bd6<uFYcJ-,P}");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString850 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  -|w]9Bd6<uFYcJ-,P} not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString826_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("su]bblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("subblockchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString826 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template su]bblockchild1.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString838_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("|VHr5x7cM{QPrU9IEH>");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString838 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template |VHr5x7cM{QPrU9IEH> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString824_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("`UK>M+M[&,I-Ld!Nye?");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("subblockchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString824 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `UK>M+M[&,I-Ld!Nye? not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString2_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("fp?9Dj>KW11");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("reply", "TestReply");
            scope.put("commands", Arrays.asList("a", "b"));
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString2 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fp?9Dj>KW11 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString1428_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("GFE8% %s4uL|S{@Pw");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString1428 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template GFE8% %s4uL|S{@Pw not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString1426_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubc^ild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString1426 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubc^ild1.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString1436_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("rCO[J4N0s{69PK(HT");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString1436 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template rCO[J4N0s{69PK(HT not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString1564_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("jOffS!gvT]ANH@]Kv");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild3.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString1564 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template jOffS!gvT]ANH@]Kv not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString1580_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("2J8GpeJJ!1*W)F}e:");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString1580 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2J8GpeJJ!1*W)F}e: not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString1565_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild[1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild3.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString1565 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchild[1.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString329_failAssert0() throws IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("?;xviUnU*am6W@lB84uf{%J");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
            sw.toString();
            org.junit.Assert.fail("testNested_literalMutationString329 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?;xviUnU*am6W@lB84uf{%J not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplExtensionTest.root = (new File(file, "sub.html").exists()) ? file : new File("src/test/resources");
    }
}

