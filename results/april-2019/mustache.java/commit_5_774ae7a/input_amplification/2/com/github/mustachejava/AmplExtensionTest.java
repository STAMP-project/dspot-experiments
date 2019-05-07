package com.github.mustachejava;


import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class AmplExtensionTest {
    private static File root;

    @Test(timeout = 10000)
    public void testSub_literalMutationString6128_failAssert0_add8959_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("t Hg|Fbw");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString6128 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString6128_failAssert0_add8959 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template t Hg|Fbw not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString6151_literalMutationString6837_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("4Mj>t1-E");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSub_literalMutationString6151__9 = scope.put("name", "Sam");
            Object o_testSub_literalMutationString6151__10 = scope.put("randomid", "aUsdlkfj");
            Writer o_testSub_literalMutationString6151__11 = m.execute(sw, scope);
            String o_testSub_literalMutationString6151__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString6151_literalMutationString6837 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4Mj>t1-E not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString6128_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("t Hg|Fbw");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString6128 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template t Hg|Fbw not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString6128_failAssert0_literalMutationString7866_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("t Hg|Fbw");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("C^xW", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString6128 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString6128_failAssert0_literalMutationString7866 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template t Hg|Fbw not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5362_failAssert0_literalMutationString5578_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multiple(xtensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5362 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5362_failAssert0_literalMutationString5578 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5365_failAssert0_literalMutationString5547_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensionq>.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5365 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5365_failAssert0_literalMutationString5547 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensionq>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5362_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5362 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5362_failAssert0null5889_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5362 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5362_failAssert0null5889 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5362_failAssert0_literalMutationString5574_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5362 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5362_failAssert0_literalMutationString5574 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5362_failAssert0_add5809_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5362 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5362_failAssert0_add5809 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5365_failAssert0_add5798_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions>.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5365 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5365_failAssert0_add5798 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5362_failAssert0null5888_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5362 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5362_failAssert0null5888 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5365_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("multipleextensions>.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5365 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5362_failAssert0_add5805_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5362 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5362_failAssert0_add5805 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add5374_literalMutationString5473_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("V=/6`#K(?FGwG^q_=yzg69%");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleExtensions_add5374__7 = m.execute(sw, new Object());
            String o_testMultipleExtensions_add5374__9 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            String o_testMultipleExtensions_add5374__10 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_add5374_literalMutationString5473 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template V=/6`#K(?FGwG^q_=yzg69% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensionsnull5378_failAssert0_literalMutationString5512_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions.h%ml");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensionsnull5378 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testMultipleExtensionsnull5378_failAssert0_literalMutationString5512 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("dr[&6G!S#|+j^,pi4ZW");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dr[&6G!S#|+j^,pi4ZW not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString30_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            m = c.compile("xDDFQ*2sc[c&=M-zk($");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString30 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template xDDFQ*2sc[c&=M-zk($ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString16_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("Z_^fnF#nA0Yt@}XY] &");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString16 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Z_^fnF#nA0Yt@}XY] & not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString20_failAssert0_literalMutationString2717_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subbl^ockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("subblockchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchil*d2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString20 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString20_failAssert0_literalMutationString2717 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subbl^ockchild1.html not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplExtensionTest.root = (new File(file, "sub.html").exists()) ? file : new File("src/test/resources");
    }
}

