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
    public void testSub_add19913_literalMutationString20196_failAssert0_literalMutationString25602_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("ThA[{p|=");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSub_add19913__9 = scope.put("name", "Pam");
                Object o_testSub_add19913__10 = scope.put("name", "Sam");
                Object o_testSub_add19913__11 = scope.put("randomid", "asdlkfj");
                Writer o_testSub_add19913__12 = m.execute(sw, scope);
                String o_testSub_add19913__13 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                String o_testSub_add19913__14 = sw.toString();
                org.junit.Assert.fail("testSub_add19913_literalMutationString20196 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_add19913_literalMutationString20196_failAssert0_literalMutationString25602 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ThA[{p|= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_add19913_literalMutationString20196_failAssert0null27699_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("ThA[{p|=");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSub_add19913__9 = scope.put(null, "Sam");
                Object o_testSub_add19913__10 = scope.put("name", "Sam");
                Object o_testSub_add19913__11 = scope.put("randomid", "asdlkfj");
                Writer o_testSub_add19913__12 = m.execute(sw, scope);
                String o_testSub_add19913__13 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                String o_testSub_add19913__14 = sw.toString();
                org.junit.Assert.fail("testSub_add19913_literalMutationString20196 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_add19913_literalMutationString20196_failAssert0null27699 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ThA[{p|= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_add19913_literalMutationString20196_failAssert0_add27052_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("ThA[{p|=");
                Mustache m = c.compile("ThA[{p|=");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSub_add19913__9 = scope.put("name", "Sam");
                Object o_testSub_add19913__10 = scope.put("name", "Sam");
                Object o_testSub_add19913__11 = scope.put("randomid", "asdlkfj");
                Writer o_testSub_add19913__12 = m.execute(sw, scope);
                String o_testSub_add19913__13 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                String o_testSub_add19913__14 = sw.toString();
                org.junit.Assert.fail("testSub_add19913_literalMutationString20196 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_add19913_literalMutationString20196_failAssert0_add27052 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ThA[{p|= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString19879_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Tjy,.,[k");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString19879 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Tjy,.,[k not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString19877_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("su].html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString19877 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template su].html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_add19913_literalMutationString20196_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("ThA[{p|=");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSub_add19913__9 = scope.put("name", "Sam");
            Object o_testSub_add19913__10 = scope.put("name", "Sam");
            Object o_testSub_add19913__11 = scope.put("randomid", "asdlkfj");
            Writer o_testSub_add19913__12 = m.execute(sw, scope);
            String o_testSub_add19913__13 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            String o_testSub_add19913__14 = sw.toString();
            org.junit.Assert.fail("testSub_add19913_literalMutationString20196 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ThA[{p|= not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString10_literalMutationString1216_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile(">#78KM?bQU0WZU ");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartial_literalMutationString10__9 = scope.put("n5ame", "Sam");
            Object o_testSubInPartial_literalMutationString10__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubInPartial_literalMutationString10__11 = m.execute(sw, scope);
            String o_testSubInPartial_literalMutationString10__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            String o_testSubInPartial_literalMutationString10__13 = sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString10_literalMutationString1216 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >#78KM?bQU0WZU  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_add37null2923_literalMutationString4616_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testSubInPartial_add37__3 = c.compile("partial[ub.html");
            Mustache m = c.compile("partialsub.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartial_add37__10 = scope.put(null, "Sam");
            Object o_testSubInPartial_add37__11 = scope.put("randomid", "asdlkfj");
            Writer o_testSubInPartial_add37__12 = m.execute(sw, scope);
            String o_testSubInPartial_add37__13 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            String o_testSubInPartial_add37__14 = sw.toString();
            org.junit.Assert.fail("testSubInPartial_add37null2923_literalMutationString4616 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template partial[ub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartialnull49null3085_literalMutationString4522_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("IIEhC(,LT@_dOy`");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartialnull49__9 = scope.put("name", "Sam");
            Object o_testSubInPartialnull49__10 = scope.put(null, null);
            Writer o_testSubInPartialnull49__11 = m.execute(sw, scope);
            String o_testSubInPartialnull49__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            String o_testSubInPartialnull49__13 = sw.toString();
            org.junit.Assert.fail("testSubInPartialnull49null3085_literalMutationString4522 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template IIEhC(,LT@_dOy` not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString15_literalMutationString939_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("f63>5OC@9s=.>HC");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartial_literalMutationString15__9 = scope.put("name", "SWam");
            Object o_testSubInPartial_literalMutationString15__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubInPartial_literalMutationString15__11 = m.execute(sw, scope);
            String o_testSubInPartial_literalMutationString15__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            String o_testSubInPartial_literalMutationString15__13 = sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString15_literalMutationString939 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString3_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("kgnz;OPz,fxv} ^");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString3 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template kgnz;OPz,fxv} ^ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString28084_failAssert0_add29527_failAssert0_literalMutationString30773_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("pa t-ialsubpartial.html");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString28084 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString28084_failAssert0_add29527 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString28084_failAssert0_add29527_failAssert0_literalMutationString30773 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template pa t-ialsubpartial.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString28086_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("lBn$DmabV(g&!ZD.t<B:.U");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString28086 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lBn$DmabV(g&!ZD.t<B:.U not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString28086_failAssert0null29736_failAssert0_add32577_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("lBn$DmabV(g&!ZD.t<B:.U");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString28086 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString28086_failAssert0null29736 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString28086_failAssert0null29736_failAssert0_add32577 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lBn$DmabV(g&!ZD.t<B:.U not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString28083_failAssert0_add29495_failAssert0_literalMutationString30808_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(" does not exist");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString28083 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString28083_failAssert0_add29495 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString28083_failAssert0_add29495_failAssert0_literalMutationString30808 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString28086_failAssert0null29736_failAssert0_literalMutationString31607_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("lBn$DmabV(g&!ZD.t<B:.U");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("radomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString28086 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString28086_failAssert0null29736 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString28086_failAssert0null29736_failAssert0_literalMutationString31607 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lBn$DmabV(g&!ZD.t<B:.U not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString28086_failAssert0null29736_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("lBn$DmabV(g&!ZD.t<B:.U");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString28086 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString28086_failAssert0null29736 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lBn$DmabV(g&!ZD.t<B:.U not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString28086_failAssert0_literalMutationString28904_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("lBn$DmabH(g&!ZD.t<B:.U");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString28086 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString28086_failAssert0_literalMutationString28904 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lBn$DmabH(g&!ZD.t<B:.U not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString28100_failAssert0_literalMutationString28833_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("plJ,cu^>0AnM$VwFtRJd 8");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "6< W#aa|0hdP>^NjvCp<+");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString28100 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString28100_failAssert0_literalMutationString28833 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template plJ,cu^>0AnM$VwFtRJd 8 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString28086_failAssert0_literalMutationString28904_failAssert0_add32468_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("lBn$DmabH(g&!ZD.t<B:.U");
                    Mustache m = c.compile("lBn$DmabH(g&!ZD.t<B:.U");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString28086 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString28086_failAssert0_literalMutationString28904 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString28086_failAssert0_literalMutationString28904_failAssert0_add32468 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lBn$DmabH(g&!ZD.t<B:.U not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString28102_failAssert0_literalMutationString28880_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("DS}}{!NbX m)bSFZZQ@Ew[");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "par2tialsubpartial.txt");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString28102 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString28102_failAssert0_literalMutationString28880 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template DS}}{!NbX m)bSFZZQ@Ew[ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString28086_failAssert0_literalMutationString28904_failAssert0_literalMutationString31282_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("lBn$DmabH(g&!ZD.t<B:.U");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkRfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString28086 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString28086_failAssert0_literalMutationString28904 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString28086_failAssert0_literalMutationString28904_failAssert0_literalMutationString31282 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lBn$DmabH(g&!ZD.t<B:.U not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString28100_failAssert0_add29503_failAssert0_literalMutationString31661_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("n`V$huzMN6(If]R Oc96zG");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "6< W#aa|0hdP>^NjvCp<+");
                    TestUtil.getContents(AmplExtensionTest.root, "6< W#aa|0hdP>^NjvCp<+");
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString28100 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString28100_failAssert0_add29503 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString28100_failAssert0_add29503_failAssert0_literalMutationString31661 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template n`V$huzMN6(If]R Oc96zG not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString28086_failAssert0_add29519_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("lBn$DmabV(g&!ZD.t<B:.U");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString28086 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString28086_failAssert0_add29519 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lBn$DmabV(g&!ZD.t<B:.U not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17351_failAssert0null19600_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("#[UL12<,+Jq(8Kan]1#`q%3");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17351 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17351_failAssert0null19600 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #[UL12<,+Jq(8Kan]1#`q%3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17093_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("#[UL12<,+Jq(8Kan]1#`q%3");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #[UL12<,+Jq(8Kan]1#`q%3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17093_failAssert0_add17550_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("#[UL12<,+Jq(8Kan]1#`q%3");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_add17550 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #[UL12<,+Jq(8Kan]1#`q%3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17093_failAssert0_add17551_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("#[UL12<,+Jq(8Kan]1#`q%3");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_add17551 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #[UL12<,+Jq(8Kan]1#`q%3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17351_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("#[UL12<,+Jq(8Kan]1#`q%3");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17351 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #[UL12<,+Jq(8Kan]1#`q%3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17351_failAssert0_add19266_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("#[UL12<,+Jq(8Kan]1#`q%3");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17351 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17351_failAssert0_add19266 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #[UL12<,+Jq(8Kan]1#`q%3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17354_failAssert0_add19037_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("#[UL12<,+Jq(8Kan]1#`q%3");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleex#tensions.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17354 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17354_failAssert0_add19037 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #[UL12<,+Jq(8Kan]1#`q%3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17354_failAssert0null19481_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("#[UL12<,+Jq(8Kan]1#`q%3");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleex#tensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17354 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17354_failAssert0null19481 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #[UL12<,+Jq(8Kan]1#`q%3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17104_failAssert0_add17517_failAssert0_literalMutationString18058_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("multipleextensions.html");
                    Mustache m = c.compile(" 28 ;Hd,#)YF9Vw{rB*fi6R");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleexteMnsions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString17104 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17104_failAssert0_add17517 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17104_failAssert0_add17517_failAssert0_literalMutationString18058 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  28 ;Hd,#)YF9Vw{rB*fi6R not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17093_failAssert0null17622_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("#[UL12<,+Jq(8Kan]1#`q%3");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0null17622 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #[UL12<,+Jq(8Kan]1#`q%3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17095_failAssert0_literalMutationString17337_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensio:Ls.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17095 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17095_failAssert0_literalMutationString17337 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensio:Ls.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17354_failAssert0_literalMutationString18135_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("#[UL12<,+Jq(8Kan]1#`q%3");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multileex#tensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17354 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17354_failAssert0_literalMutationString18135 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #[UL12<,+Jq(8Kan]1#`q%3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17097_failAssert0_literalMutationString17360_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("|ultip3eextensions.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17097 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17097_failAssert0_literalMutationString17360 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template |ultip3eextensions.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17100_failAssert0_literalMutationString17293_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("MR`o&L!.:NF8F}1Nj:qz2{g");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17100 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17100_failAssert0_literalMutationString17293 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template MR`o&L!.:NF8F}1Nj:qz2{g not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17354_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("#[UL12<,+Jq(8Kan]1#`q%3");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleex#tensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17354 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #[UL12<,+Jq(8Kan]1#`q%3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensionsnull17111_failAssert0_add17561_failAssert0_literalMutationString18103_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("}z:%|<<bE6`3D_:)umib{T7");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensionsnull17111 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testMultipleExtensionsnull17111_failAssert0_add17561 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testMultipleExtensionsnull17111_failAssert0_add17561_failAssert0_literalMutationString18103 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template }z:%|<<bE6`3D_:)umib{T7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17351_failAssert0_literalMutationString18598_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("#[UL12<,+Jq(8Kan]1#`q%3");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "Q");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17351 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString17093_failAssert0_literalMutationString17351_failAssert0_literalMutationString18598 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #[UL12<,+Jq(8Kan]1#`q%3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString8400_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("#k:[R(NYG}l");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSub_literalMutationString8400 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #k:[R(NYG}l not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_add8437_literalMutationString8599_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("I<%2[N &ns:");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubSub_add8437__9 = scope.put("name", "Sam");
            Object o_testSubSub_add8437__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubSub_add8437__11 = m.execute(sw, scope);
            Writer o_testSubSub_add8437__12 = m.execute(sw, scope);
            String o_testSubSub_add8437__13 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            String o_testSubSub_add8437__14 = sw.toString();
            org.junit.Assert.fail("testSubSub_add8437_literalMutationString8599 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template I<%2[N &ns: not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString8407_literalMutationString9771_literalMutationString12779_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("xW[2SsHAM}4");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubSub_literalMutationString8407__9 = scope.put("page1.txt", "Saam");
            Object o_testSubSub_literalMutationString8407__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubSub_literalMutationString8407__11 = m.execute(sw, scope);
            String o_testSubSub_literalMutationString8407__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            String o_testSubSub_literalMutationString8407__13 = sw.toString();
            org.junit.Assert.fail("testSubSub_literalMutationString8407_literalMutationString9771_literalMutationString12779 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template xW[2SsHAM}4 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_add8437_add10423_literalMutationString12123_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testSubSub_add8437_add10423__3 = c.compile(":G+v>3[Y)}_");
            Mustache m = c.compile("subsub.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubSub_add8437__9 = scope.put("name", "Sam");
            Object o_testSubSub_add8437__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubSub_add8437__11 = m.execute(sw, scope);
            Writer o_testSubSub_add8437__12 = m.execute(sw, scope);
            String o_testSubSub_add8437__13 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            String o_testSubSub_add8437__14 = sw.toString();
            org.junit.Assert.fail("testSubSub_add8437_add10423_literalMutationString12123 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :G+v>3[Y)}_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_add8439_literalMutationString8724_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("O{.SY )9l`v");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubSub_add8439__9 = scope.put("name", "Sam");
            Object o_testSubSub_add8439__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubSub_add8439__11 = m.execute(sw, scope);
            sw.toString();
            String o_testSubSub_add8439__13 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            String o_testSubSub_add8439__14 = sw.toString();
            org.junit.Assert.fail("testSubSub_add8439_literalMutationString8724 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template O{.SY )9l`v not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_add8434_add10521_literalMutationString11939_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testSubSub_add8434__3 = c.compile("subsub.html");
            Mustache m = c.compile("*i)+m%RE3Zn");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubSub_add8434__10 = scope.put("name", "Sam");
            Object o_testSubSub_add8434__11 = scope.put("randomid", "asdlkfj");
            Writer o_testSubSub_add8434_add10521__18 = m.execute(sw, scope);
            Writer o_testSubSub_add8434__12 = m.execute(sw, scope);
            String o_testSubSub_add8434__13 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            String o_testSubSub_add8434__14 = sw.toString();
            org.junit.Assert.fail("testSubSub_add8434_add10521_literalMutationString11939 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *i)+m%RE3Zn not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_add8438_literalMutationString8679_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsub.ht%ml");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubSub_add8438__9 = scope.put("name", "Sam");
            Object o_testSubSub_add8438__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubSub_add8438__11 = m.execute(sw, scope);
            String o_testSubSub_add8438__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            String o_testSubSub_add8438__13 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            String o_testSubSub_add8438__14 = sw.toString();
            org.junit.Assert.fail("testSubSub_add8438_literalMutationString8679 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsub.ht%ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_add33418_literalMutationString33622_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testClientMethod_add33418__3 = c.compile("client.html");
            Mustache m = c.compile("G[ytqa%/[MW");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testClientMethod_add33418__10 = scope.put("reply", "TestReply");
            Object o_testClientMethod_add33418__11 = scope.put("commands", Arrays.asList("a", "b"));
            Writer o_testClientMethod_add33418__13 = m.execute(sw, scope);
            String o_testClientMethod_add33418__14 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            String o_testClientMethod_add33418__15 = sw.toString();
            org.junit.Assert.fail("testClientMethod_add33418_literalMutationString33622 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString33386_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("yP<l:!18=k#");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("reply", "TestReply");
            scope.put("commands", Arrays.asList("a", "b"));
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString33386 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template yP<l:!18=k# not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString33387_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("cl]ient.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("reply", "TestReply");
            scope.put("commands", Arrays.asList("a", "b"));
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString33387 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cl]ient.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString33390_literalMutationString34103_literalMutationString37890_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("cli[ent.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testClientMethod_literalMutationString33390__9 = scope.put("Feply", "");
            Object o_testClientMethod_literalMutationString33390__10 = scope.put("commands", Arrays.asList("a", "b"));
            Writer o_testClientMethod_literalMutationString33390__12 = m.execute(sw, scope);
            String o_testClientMethod_literalMutationString33390__13 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            String o_testClientMethod_literalMutationString33390__14 = sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString33390_literalMutationString34103_literalMutationString37890 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cli[ent.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString33388null36508_literalMutationString38084_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("6:N#F_S3&LN");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testClientMethod_literalMutationString33388__9 = scope.put("", null);
            Object o_testClientMethod_literalMutationString33388__10 = scope.put("commands", Arrays.asList("a", "b"));
            Writer o_testClientMethod_literalMutationString33388__12 = m.execute(sw, scope);
            String o_testClientMethod_literalMutationString33388__13 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            String o_testClientMethod_literalMutationString33388__14 = sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString33388null36508_literalMutationString38084 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 6:N#F_S3&LN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString33384_failAssert0_literalMutationString35085_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("client].tml");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("reply", "TestReply");
                scope.put("commands", Arrays.asList("a", "b"));
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_literalMutationString33384 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString33384_failAssert0_literalMutationString35085 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template client].tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_add33422_literalMutationString33582_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("b{,4Zw([[j<");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testClientMethod_add33422__9 = scope.put("reply", "TestReply");
            Object o_testClientMethod_add33422__10 = scope.put("commands", Arrays.asList("a", "b"));
            Writer o_testClientMethod_add33422__12 = m.execute(sw, scope);
            Writer o_testClientMethod_add33422__13 = m.execute(sw, scope);
            String o_testClientMethod_add33422__14 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            String o_testClientMethod_add33422__15 = sw.toString();
            org.junit.Assert.fail("testClientMethod_add33422_literalMutationString33582 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template b{,4Zw([[j< not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplExtensionTest.root = (new File(file, "sub.html").exists()) ? file : new File("src/test/resources");
    }
}

