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
    public void testSub_literalMutationString833_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("^eiCT8Id");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString833 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ^eiCT8Id not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString70_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("partialsub.h^tml");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString70 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template partialsub.h^tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString71_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("f8*9F|=U;I4Q<SD");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString71 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template f8*9F|=U;I4Q<SD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString69_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("{artialsub.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString69 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {artialsub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString1240_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Yh.*wIQDk(+F};Gs/h0&pB");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString1240 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Yh.*wIQDk(+F};Gs/h0&pB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString1236_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("part ialsubpartial.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString1236 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template part ialsubpartial.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("[A0j$95#m8uIKXN@Z");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [A0j$95#m8uIKXN@Z not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString5_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("fol<ownomenu.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_literalMutationString5 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fol<ownomenu.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString766_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("multipleextensions.h]tml");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString766 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h]tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString767_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("-ou SLy_0%=QC*46ymNRXSK");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString767 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -ou SLy_0%=QC*46ymNRXSK not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString672_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("lm0^d_+ y]n*");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String replace = "false";
            });
            TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString672 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lm0^d_+ y]n* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString291_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            m = c.compile("Ho[Fj6ujfbTf{g=[esx");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString291 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Ho[Fj6ujfbTf{g=[esx not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString265_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("l%lbXHDkQ?AA%hVV/@[");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString265 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template l%lbXHDkQ?AA%hVV/@[ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString279_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("7)>L69<[UAPUKm Mj[b");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString279 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 7)>L69<[UAPUKm Mj[b not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString478_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("%v[WI?Eqmt}");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSub_literalMutationString478 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template %v[WI?Eqmt} not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString1508_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("iS,]GZ)4H%=");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("reply", "TestReply");
            scope.put("commands", Arrays.asList("a", "b"));
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString1508 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template iS,]GZ)4H%= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString1370_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.<tml");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString1370 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchild1.<tml not found", expected.getMessage());
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
            m = c.compile("dtQ]|blM60|9)o*D!");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString1382 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dtQ]|blM60|9)o*D! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString1372_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("r*yr`,nB2mXV|Djcy");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString1372 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template r*yr`,nB2mXV|Djcy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString1383_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subs]bchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString1383 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subs]bchild2.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString1028_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("/F+!E3=H3.x,g4^:v");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild3.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString1028 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /F+!E3=H3.x,g4^:v not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString1027_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsu>bchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild3.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString1027 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsu>bchild1.html not found", expected.getMessage());
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
            m = c.compile("subsubch ild3.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString1042 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubch ild3.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString1169_failAssert0() throws IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("yg s!b3NJ{MT%8):W1f,nf=");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
            sw.toString();
            org.junit.Assert.fail("testNested_literalMutationString1169 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template yg s!b3NJ{MT%8):W1f,nf= not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplExtensionTest.root = (new File(file, "sub.html").exists()) ? file : new File("src/test/resources");
    }
}

