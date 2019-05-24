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
    public void testSub_literalMutationString830_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Yv&0Qx@<");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString830 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Yv&0Qx@< not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString67_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("}p8Y6`g6<Qm!%)_");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString67 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template }p8Y6`g6<Qm!%)_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString1237_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("06bf%[!SxM-cAs7U`SrPA{");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString1237 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 06bf%[!SxM-cAs7U`SrPA{ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString5_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile(" ]uaupNA%_VMs<urz");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_literalMutationString5 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  ]uaupNA%_VMs<urz not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString765_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("A)GSWC[w!p*[C[PLQQ?F<KN");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString765 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template A)GSWC[w!p*[C[PLQQ?F<KN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString674_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("replace. tml");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String replace = "false";
            });
            TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString674 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template replace. tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString673_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("r[eplace.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String replace = "false";
            });
            TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString673 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template r[eplace.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString672_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("KXy<J}X-,Ko{");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String replace = "false";
            });
            TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString672 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template KXy<J}X-,Ko{ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString292_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            m = c.compile("m1--fp2sp+1-cMSL]>f");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString292 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template m1--fp2sp+1-cMSL]>f not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString290_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            m = c.compile("subblockchild1.%html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString290 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subblockchild1.%html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString263_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile(")V&,Iwk^RI3OBqVAv4w");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString263 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template )V&,Iwk^RI3OBqVAv4w not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString278_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile(":TXv6mXThajD9ysEc%@");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString278 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :TXv6mXThajD9ysEc%@ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString477_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("PMZ,Oc`R)+ ");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSub_literalMutationString477 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template PMZ,Oc`R)+  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString1508_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("_m_|U>}$FZy");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("reply", "TestReply");
            scope.put("commands", Arrays.asList("a", "b"));
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString1508 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template _m_|U>}$FZy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString1382_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("Q8:]_-2E@lH61byMC");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString1382 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Q8:]_-2E@lH61byMC not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString1371_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("OLd#&#sU:IS0wK$&7");
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
            Assert.assertEquals("Template OLd#&#sU:IS0wK$&7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString1029_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.ht ml");
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
            Assert.assertEquals("Template subsubchild1.ht ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString1042_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("mmT%wRk|KrUiE>1<_");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString1042 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mmT%wRk|KrUiE>1<_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString1030_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("dy,%rzI;-AvjCt5*S");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild3.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString1030 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dy,%rzI;-AvjCt5*S not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString1167_failAssert0() throws IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("4U>tqw}H6/Y;`(ob.hMuTM3");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
            sw.toString();
            org.junit.Assert.fail("testNested_literalMutationString1167 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4U>tqw}H6/Y;`(ob.hMuTM3 not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplExtensionTest.root = (new File(file, "sub.html").exists()) ? file : new File("src/test/resources");
    }
}

