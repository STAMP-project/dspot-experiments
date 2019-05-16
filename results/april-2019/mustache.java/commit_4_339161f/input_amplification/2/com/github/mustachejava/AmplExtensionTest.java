package com.github.mustachejava;


import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
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
    public void testSub_literalMutationString7802_literalMutationString8266_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("bj@dxok]");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSub_literalMutationString7802__9 = scope.put("name", "Sam");
            Object o_testSub_literalMutationString7802__10 = scope.put("ran2domid", "asdlkfj");
            Writer o_testSub_literalMutationString7802__11 = m.execute(sw, scope);
            String o_testSub_literalMutationString7802__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            String o_testSub_literalMutationString7802__13 = sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString7802_literalMutationString8266 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString7784_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("?-e%gW|Q");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString7784 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?-e%gW|Q not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString6_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("6}<.}S,]0Mz;M<F");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString6 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 6}<.}S,]0Mz;M<F not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString6_failAssert0_literalMutationString1549_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("6}<.}S,]0Mz;M<F");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "page1.txt");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString6_failAssert0_literalMutationString1549 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 6}<.}S,]0Mz;M<F not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString9_literalMutationString559_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("a&AyG1#GAN^?l!Y");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartial_literalMutationString9__9 = scope.put("naxme", "Sam");
            Object o_testSubInPartial_literalMutationString9__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubInPartial_literalMutationString9__11 = m.execute(sw, scope);
            String o_testSubInPartial_literalMutationString9__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            String o_testSubInPartial_literalMutationString9__13 = sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString9_literalMutationString559 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template a&AyG1#GAN^?l!Y not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString6_failAssert0null3190_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("6}<.}S,]0Mz;M<F");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", null);
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString6_failAssert0null3190 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 6}<.}S,]0Mz;M<F not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString6_failAssert0_add2794_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("6}<.}S,]0Mz;M<F");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString6_failAssert0_add2794 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 6}<.}S,]0Mz;M<F not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString18_literalMutationString1431_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("partials}b.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartial_literalMutationString18__9 = scope.put("name", "A=[");
            Object o_testSubInPartial_literalMutationString18__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubInPartial_literalMutationString18__11 = m.execute(sw, scope);
            String o_testSubInPartial_literalMutationString18__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            String o_testSubInPartial_literalMutationString18__13 = sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString18_literalMutationString1431 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template partials}b.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_add11332_literalMutationString11563_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("@BikPqtwSHmMS t}zx&SXN");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testPartialInSub_add11332__9 = scope.put("randomid", "asdlkfj");
            Writer o_testPartialInSub_add11332__10 = m.execute(sw, scope);
            Writer o_testPartialInSub_add11332__11 = m.execute(sw, scope);
            String o_testPartialInSub_add11332__12 = TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            String o_testPartialInSub_add11332__13 = sw.toString();
            org.junit.Assert.fail("testPartialInSub_add11332_literalMutationString11563 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template @BikPqtwSHmMS t}zx&SXN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString11307_failAssert0_literalMutationString12199_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("#&Wyv0Nn@[+[RVuUF)Vm&$");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString11307 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString11307_failAssert0_literalMutationString12199 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #&Wyv0Nn@[+[RVuUF)Vm&$ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString11307_failAssert0_add12759_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("#&Wyv0Nn@[+[RVUUF)Vm&$");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString11307 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString11307_failAssert0_add12759 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #&Wyv0Nn@[+[RVUUF)Vm&$ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString11307_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("#&Wyv0Nn@[+[RVUUF)Vm&$");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString11307 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #&Wyv0Nn@[+[RVUUF)Vm&$ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add7050_literalMutationString7151_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("=;Xjz0|Tb6pg4ocA9[gZ9p!");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleExtensions_add7050__7 = m.execute(sw, new Object());
            sw.toString();
            String o_testMultipleExtensions_add7050__10 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            String o_testMultipleExtensions_add7050__11 = sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_add7050_literalMutationString7151 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template =;Xjz0|Tb6pg4ocA9[gZ9p! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensionsnull7054_failAssert0_literalMutationString7315_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextens:ons.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensionsnull7054 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testMultipleExtensionsnull7054_failAssert0_literalMutationString7315 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextens:ons.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString7041_failAssert0_literalMutationString7186_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("pT&uI<{:{C&&H;_fYq$*LeK");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString7041 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString7041_failAssert0_literalMutationString7186 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template pT&uI<{:{C&&H;_fYq$*LeK not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString7039_failAssert0_literalMutationString7214_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("9Xx}[muTErtxgbNd%CRg@q*");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipXleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString7039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString7039_failAssert0_literalMutationString7214 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 9Xx}[muTErtxgbNd%CRg@q* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString7040_failAssert0_literalMutationString7267_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("m8ltipl[extensions.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString7040 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString7040_failAssert0_literalMutationString7267 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template m8ltipl[extensions.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString7039_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("9Xx}[muTErtxgbNd%CRg@q*");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString7039 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 9Xx}[muTErtxgbNd%CRg@q* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString7039_failAssert0null7543_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("9Xx}[muTErtxgbNd%CRg@q*");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString7039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString7039_failAssert0null7543 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 9Xx}[muTErtxgbNd%CRg@q* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString7039_failAssert0_add7459_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("9Xx}[muTErtxgbNd%CRg@q*");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString7039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString7039_failAssert0_add7459 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 9Xx}[muTErtxgbNd%CRg@q* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString7043_failAssert0_literalMutationString7255_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("y^MdK_iA:wF/#?!/j$/o!)7");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString7043 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString7043_failAssert0_literalMutationString7255 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString3523_failAssert0_add6383_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("jm>^:EG%[;H");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString3523 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString3523_failAssert0_add6383 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template jm>^:EG%[;H not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString3528_literalMutationString4953_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("kF[EV][$KK<");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubSub_literalMutationString3528__9 = scope.put("nme", "Sam");
            Object o_testSubSub_literalMutationString3528__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubSub_literalMutationString3528__11 = m.execute(sw, scope);
            String o_testSubSub_literalMutationString3528__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            String o_testSubSub_literalMutationString3528__13 = sw.toString();
            org.junit.Assert.fail("testSubSub_literalMutationString3528_literalMutationString4953 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template kF[EV][$KK< not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString3523_failAssert0_literalMutationString5445_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("jm>^:EG%[;H");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("page1.txt", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString3523 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString3523_failAssert0_literalMutationString5445 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template jm>^:EG%[;H not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString3523_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("jm>^:EG%[;H");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSub_literalMutationString3523 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template jm>^:EG%[;H not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString13227_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("D:pK9,8ZL]!");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("reply", "TestReply");
            scope.put("commands", Arrays.asList("a", "b"));
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString13227 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template D:pK9,8ZL]! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString13227_failAssert0_literalMutationString15064_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("D:pK9,8ZL]!");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("reply", "TestReply");
                scope.put("commands", Arrays.asList("a", "b"));
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "clien1t.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_literalMutationString13227 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString13227_failAssert0_literalMutationString15064 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template D:pK9,8ZL]! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString13227_failAssert0_add16084_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("D:pK9,8ZL]!");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("reply", "TestReply");
                scope.put("commands", Arrays.asList("a", "b"));
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_literalMutationString13227 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString13227_failAssert0_add16084 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template D:pK9,8ZL]! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString13244_literalMutationString14676_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("{ZvLMMt(Fc{");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testClientMethod_literalMutationString13244__9 = scope.put("reply", "TestReply");
            Object o_testClientMethod_literalMutationString13244__10 = scope.put("&>:IinV/", Arrays.asList("a", "b"));
            Writer o_testClientMethod_literalMutationString13244__12 = m.execute(sw, scope);
            String o_testClientMethod_literalMutationString13244__13 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            String o_testClientMethod_literalMutationString13244__14 = sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString13244_literalMutationString14676 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {ZvLMMt(Fc{ not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplExtensionTest.root = (new File(file, "sub.html").exists()) ? file : new File("src/test/resources");
    }
}

