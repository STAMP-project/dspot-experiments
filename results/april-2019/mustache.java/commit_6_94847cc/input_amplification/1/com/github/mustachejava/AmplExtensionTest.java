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
    public void testSub_literalMutationString834_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("pbI_d(}=");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString834 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template pbI_d(}= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString830_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("su`.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString830 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template su`.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString72_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("-We!$8H_I=8-[lO");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString72 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -We!$8H_I=8-[lO not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString1236_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("XrA{T5PL)Z;/Ed_ T#PrJh");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString1236 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("8%_w)s ;Kkv9t0uaH");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 8%_w)s ;Kkv9t0uaH not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString2_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("follownom[enu.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_literalMutationString2 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template follownom[enu.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString766_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Cpw[KKIRaR1JHw:7h`I?]oG");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString766 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Cpw[KKIRaR1JHw:7h`I?]oG not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString768_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("multi[leextensions.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString768 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multi[leextensions.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString669_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("-,/&`OigT`JE");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String replace = "false";
            });
            TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString669 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString289_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            m = c.compile("vhHYUv!mAq0xxww`4JA");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString289 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vhHYUv!mAq0xxww`4JA not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString266_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("#z{Mzy8=+|CeGXE5Y&|");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString266 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #z{Mzy8=+|CeGXE5Y&| not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString280_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("U(sN&;Qy]-J(&+WU``^");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString280 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template U(sN&;Qy]-J(&+WU``^ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString477_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Nr 8m{vtctU");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSub_literalMutationString477 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Nr 8m{vtctU not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString1511_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("c^ient.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("reply", "TestReply");
            scope.put("commands", Arrays.asList("a", "b"));
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString1511 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template c^ient.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString1371_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("tPJ#I@rL.{XK2uP_o");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString1371 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tPJ#I@rL.{XK2uP_o not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString1384_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("6q[9AUOLxY[s=ns5,");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString1384 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 6q[9AUOLxY[s=ns5, not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString1029_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("[yh@xl+Y)]GWMwTIP");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild3.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString1029 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [yh@xl+Y)]GWMwTIP not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString1041_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("_#KqEB_>y.MP=$5mU");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString1041 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template _#KqEB_>y.MP=$5mU not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString1168_failAssert0() throws IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("4dxe9d3L<Q[o@+ YZf@=7TN");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
            sw.toString();
            org.junit.Assert.fail("testNested_literalMutationString1168 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4dxe9d3L<Q[o@+ YZf@=7TN not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplExtensionTest.root = (new File(file, "sub.html").exists()) ? file : new File("src/test/resources");
    }
}

