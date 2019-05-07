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
    public void testSub_remove15084_literalMutationString16240_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("sub.`html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSub_remove15084__9 = scope.put("name", "Sam");
            Writer o_testSub_remove15084__10 = m.execute(sw, scope);
            String o_testSub_remove15084__11 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_remove15084_literalMutationString16240 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sub.`html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString15064_literalMutationString16421_literalMutationString19576_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("$S3$,K<4");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSub_literalMutationString15064__9 = scope.put("name", "Sam");
            Object o_testSub_literalMutationString15064__10 = scope.put("randomd", "page1.txt");
            Writer o_testSub_literalMutationString15064__11 = m.execute(sw, scope);
            String o_testSub_literalMutationString15064__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString15064_literalMutationString16421_literalMutationString19576 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template $S3$,K<4 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_remove15084_literalMutationString16240_failAssert0null23263_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("sub.`html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSub_remove15084__9 = scope.put(null, "Sam");
                Writer o_testSub_remove15084__10 = m.execute(sw, scope);
                String o_testSub_remove15084__11 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_remove15084_literalMutationString16240 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_remove15084_literalMutationString16240_failAssert0null23263 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sub.`html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_remove15084_literalMutationString16240_failAssert0_literalMutationString21277_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("sub.`html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSub_remove15084__9 = scope.put("name", "Sam");
                Writer o_testSub_remove15084__10 = m.execute(sw, scope);
                String o_testSub_remove15084__11 = TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_remove15084_literalMutationString16240 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_remove15084_literalMutationString16240_failAssert0_literalMutationString21277 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sub.`html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_remove15084_literalMutationString16240_failAssert0_add22558_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("sub.`html");
                Mustache m = c.compile("sub.`html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSub_remove15084__9 = scope.put("name", "Sam");
                Writer o_testSub_remove15084__10 = m.execute(sw, scope);
                String o_testSub_remove15084__11 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_remove15084_literalMutationString16240 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_remove15084_literalMutationString16240_failAssert0_add22558 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sub.`html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_remove15084_literalMutationString16240_failAssert0_add22559_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("sub.`html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                Object o_testSub_remove15084__9 = scope.put("name", "Sam");
                Writer o_testSub_remove15084__10 = m.execute(sw, scope);
                String o_testSub_remove15084__11 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_remove15084_literalMutationString16240 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_remove15084_literalMutationString16240_failAssert0_add22559 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sub.`html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_remove15084_literalMutationString16240_failAssert0_literalMutationString21258_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("s:b.`html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSub_remove15084__9 = scope.put("name", "Sam");
                Writer o_testSub_remove15084__10 = m.execute(sw, scope);
                String o_testSub_remove15084__11 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_remove15084_literalMutationString16240 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_remove15084_literalMutationString16240_failAssert0_literalMutationString21258 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template s:b.`html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString15046_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("sub<html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString15046 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sub<html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString15069_literalMutationString16469_failAssert0_literalMutationString21390_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Qj<>p.JK");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSub_literalMutationString15069__9 = scope.put("name", "Sam");
                Object o_testSub_literalMutationString15069__10 = scope.put("randomid", "W#`|b4m");
                Writer o_testSub_literalMutationString15069__11 = m.execute(sw, scope);
                String o_testSub_literalMutationString15069__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString15069_literalMutationString16469 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString15069_literalMutationString16469_failAssert0_literalMutationString21390 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Qj<>p.JK not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString12140_failAssert0_literalMutationString12320_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("ek}FqN:BPqq9h$-P i,sV b]");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString12140 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString12140_failAssert0_literalMutationString12320 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ek}FqN:BPqq9h$-P i,sV b] not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString12141_failAssert0_literalMutationString12376_failAssert0_literalMutationString13412_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("sR0vp|>tR");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString12141 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString12141_failAssert0_literalMutationString12376 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString12141_failAssert0_literalMutationString12376_failAssert0_literalMutationString13412 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sR0vp|>tR not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString12143_failAssert0_add12613_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("1jN54{j{Up%/ #cWbjsd>XB");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString12143 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString12143_failAssert0_add12613 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 1jN54{j{Up%/ #cWbjsd>XB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString12143_failAssert0_add12613_failAssert0_literalMutationString13403_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("1j54{j{Up%/ #cWbjsd>XB");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString12143 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString12143_failAssert0_add12613 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString12143_failAssert0_add12613_failAssert0_literalMutationString13403 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 1j54{j{Up%/ #cWbjsd>XB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString12143_failAssert0_add12613_failAssert0_add14297_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("1jN54{j{Up%/ #cWbjsd>XB");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString12143 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString12143_failAssert0_add12613 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString12143_failAssert0_add12613_failAssert0_add14297 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 1jN54{j{Up%/ #cWbjsd>XB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString12143_failAssert0_literalMutationString12403_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("1jN54{j{Up%/ T#cWbjsd>XB");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString12143 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString12143_failAssert0_literalMutationString12403 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 1jN54{j{Up%/ T#cWbjsd>XB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString12143_failAssert0null12682_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("1jN54{j{Up%/ #cWbjsd>XB");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString12143 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString12143_failAssert0null12682 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 1jN54{j{Up%/ #cWbjsd>XB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString12143_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("1jN54{j{Up%/ #cWbjsd>XB");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString12143 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 1jN54{j{Up%/ #cWbjsd>XB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717_failAssert0_add10503_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(":7[8r9B&346s8rj?![A");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblockchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717_failAssert0_add10503 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :7[8r9B&346s8rj?![A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(":7[8r9B&346s8rj?![A");
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
                TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :7[8r9B&346s8rj?![A not found", expected.getMessage());
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
            m = c.compile("nMC03A mik1l|tR?XxE");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString30 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nMC03A mik1l|tR?XxE not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCachingnull61_failAssert0_literalMutationString1215_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("78F69wHv]%!#MBQy&ci");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile(null);
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCachingnull61 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSubBlockCachingnull61_failAssert0_literalMutationString1215 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 78F69wHv]%!#MBQy&ci not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0_add4007_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(":7[8r9B&346s8rj?![A");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("subblockchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                c.compile("subblockchild1.html");
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_add4007 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :7[8r9B&346s8rj?![A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCachingnull61_failAssert0_literalMutationString1215_failAssert0null11779_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subblockchild1.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("78F69wHv]%!#MBQy&ci");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile(null);
                    sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCachingnull61 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testSubBlockCachingnull61_failAssert0_literalMutationString1215 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCachingnull61_failAssert0_literalMutationString1215_failAssert0null11779 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 78F69wHv]%!#MBQy&ci not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0_add4007_failAssert0_literalMutationString7374_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(":7[8r9B&346s8rj?![A");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblockchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    c.compile("subblockchild1.html");
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockch^ild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_add4007 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_add4007_failAssert0_literalMutationString7374 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :7[8r9B&346s8rj?![A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile(":7[8r9B&346s8rj?![A");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :7[8r9B&346s8rj?![A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717_failAssert0_literalMutationString8120_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(":7[8r9B&346s8rj?![A");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("[I+3dNVy?^Rgq-]]:4L");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717_failAssert0_literalMutationString8120 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :7[8r9B&346s8rj?![A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717_failAssert0_add10504_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(":7[8r9B&346s8rj?![A");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblockchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    c.compile("subblockchild1.html");
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717_failAssert0_add10504 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :7[8r9B&346s8rj?![A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717_failAssert0_literalMutationString8132_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(":7[8r9B&346s8rj?![A");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblockchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile("");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717_failAssert0_literalMutationString8132 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :7[8r9B&346s8rj?![A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString1_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1}.html");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString1 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subblockchild1}.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0null4889_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(":7[8r9B&346s8rj?![A");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("subblockchild2.html");
                sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0null4889 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :7[8r9B&346s8rj?![A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCachingnull63_failAssert0_literalMutationString1295_failAssert0_literalMutationString8595_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
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
                    m = c.compile("5[fYf&|sB");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCachingnull63 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testSubBlockCachingnull63_failAssert0_literalMutationString1295 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCachingnull63_failAssert0_literalMutationString1295_failAssert0_literalMutationString8595 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5[fYf&|sB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString18_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("TM`<@a`<[94rQxpAIgj");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString18 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template TM`<@a`<[94rQxpAIgj not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString27_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            m = c.compile("s]bblockchild1.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString27 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template s]bblockchild1.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString3_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockch[ld1.html");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString3 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subblockch[ld1.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString9_failAssert0_literalMutationString2366_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("v>e_:A#,q7l6:|M<<_>");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString9 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString9_failAssert0_literalMutationString2366 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template v>e_:A#,q7l6:|M<<_> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_remove53_literalMutationString970_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubBlockCaching_remove53__7 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_remove53__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("3v!,Z7<=x1:1u`3{8`v");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_remove53__18 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_remove53__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            String o_testSubBlockCaching_remove53__29 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            String o_testSubBlockCaching_remove53__31 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_remove53_literalMutationString970 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 3v!,Z7<=x1:1u`3{8`v not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717_failAssert0null11684_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(":7[8r9B&346s8rj?![A");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblockchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile(null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717_failAssert0null11684 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :7[8r9B&346s8rj?![A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717_failAssert0null11683_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(":7[8r9B&346s8rj?![A");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblockchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString1717_failAssert0null11683 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :7[8r9B&346s8rj?![A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCachingnull61_failAssert0_literalMutationString1215_failAssert0_add10653_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subblockchild1.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("78F69wHv]%!#MBQy&ci");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile(null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCachingnull61 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testSubBlockCachingnull61_failAssert0_literalMutationString1215 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCachingnull61_failAssert0_literalMutationString1215_failAssert0_add10653 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 78F69wHv]%!#MBQy&ci not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0_add4007_failAssert0_add10213_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(":7[8r9B&346s8rj?![A");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblockchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    c.compile("subblockchild1.html");
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_add4007 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_add4007_failAssert0_add10213 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :7[8r9B&346s8rj?![A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCachingnull61_failAssert0_literalMutationString1215_failAssert0_literalMutationString8521_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subblockchild1.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("78F69wHv]%!#MBQy&ci");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile(null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "wubblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCachingnull61 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testSubBlockCachingnull61_failAssert0_literalMutationString1215 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCachingnull61_failAssert0_literalMutationString1215_failAssert0_literalMutationString8521 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 78F69wHv]%!#MBQy&ci not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplExtensionTest.root = (new File(file, "sub.html").exists()) ? file : new File("src/test/resources");
    }
}

