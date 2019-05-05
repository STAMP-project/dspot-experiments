package com.github.mustachejava;


import com.github.mustachejava.codes.DefaultMustache;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class AmplExtensionTest {
    private static File root;

    @Test(timeout = 10000)
    public void testSub_literalMutationString65382_literalMutationString66451_failAssert0_literalMutationString74831_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("i.R1i$]C");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSub_literalMutationString65382__9 = scope.put("name", "V2p");
                Object o_testSub_literalMutationString65382__10 = scope.put("", "asdlkfj");
                Writer o_testSub_literalMutationString65382__11 = m.execute(sw, scope);
                String o_testSub_literalMutationString65382__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString65382_literalMutationString66451 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65382_literalMutationString66451_failAssert0_literalMutationString74831 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i.R1i$]C not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65371_failAssert0_add68241_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("dY[d_B+-");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString65371 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65371_failAssert0_add68241 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dY[d_B+- not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65369_failAssert0_add68217_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString65369 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0_add68217 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubnull65416_literalMutationString65565_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubnull65416__9 = scope.put("name", "Sam");
            Object o_testSubnull65416__10 = scope.put(null, "asdlkfj");
            Writer o_testSubnull65416__11 = m.execute(sw, scope);
            String o_testSubnull65416__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubnull65416_literalMutationString65565 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65369_failAssert0null68659_failAssert0_literalMutationString72957_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put(null, "Sam");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testSub_literalMutationString65369 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0null68659 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0null68659_failAssert0_literalMutationString72957 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65371_failAssert0null68683_failAssert0_add76978_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("dY[d_B+-");
                    Mustache m = c.compile("dY[d_B+-");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put(null, "Sam");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSub_literalMutationString65371 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSub_literalMutationString65371_failAssert0null68683 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65371_failAssert0null68683_failAssert0_add76978 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dY[d_B+- not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65369_failAssert0null68659_failAssert0null78338_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put(null, null);
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSub_literalMutationString65369 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0null68659 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0null68659_failAssert0null78338 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65369_failAssert0_add68220_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString65369 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0_add68220 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65371_failAssert0null68683_failAssert0_add76979_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("dY[d_B+-");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put(null, "Sam");
                    scope.put(null, "Sam");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSub_literalMutationString65371 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSub_literalMutationString65371_failAssert0null68683 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65371_failAssert0null68683_failAssert0_add76979 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dY[d_B+- not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65371_failAssert0null68683_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("dY[d_B+-");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put(null, "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString65371 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65371_failAssert0null68683 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dY[d_B+- not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65379_remove68287_literalMutationString71187_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSub_literalMutationString65379__9 = scope.put("2ame", "Sam");
            Object o_testSub_literalMutationString65379__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSub_literalMutationString65379__11 = m.execute(sw, scope);
            String o_testSub_literalMutationString65379__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            org.junit.Assert.fail("testSub_literalMutationString65379_remove68287_literalMutationString71187 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65369_failAssert0null68659_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put(null, "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString65369 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0null68659 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65382_literalMutationString66451_failAssert0_add77286_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("i.R1i$]C");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSub_literalMutationString65382__9 = scope.put("name", "V2p");
                Object o_testSub_literalMutationString65382__10 = scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                Writer o_testSub_literalMutationString65382__11 = m.execute(sw, scope);
                String o_testSub_literalMutationString65382__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString65382_literalMutationString66451 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65382_literalMutationString66451_failAssert0_add77286 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i.R1i$]C not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65382_literalMutationString66451_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("i.R1i$]C");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSub_literalMutationString65382__9 = scope.put("name", "V2p");
            Object o_testSub_literalMutationString65382__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSub_literalMutationString65382__11 = m.execute(sw, scope);
            String o_testSub_literalMutationString65382__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString65382_literalMutationString66451 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i.R1i$]C not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65370_failAssert0_literalMutationString67358_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("?@wag*m63");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString65370 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65370_failAssert0_literalMutationString67358 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?@wag*m63 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65382_literalMutationString66451_failAssert0null78760_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("i.R1i$]C");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSub_literalMutationString65382__9 = scope.put("name", null);
                Object o_testSub_literalMutationString65382__10 = scope.put("randomid", "asdlkfj");
                Writer o_testSub_literalMutationString65382__11 = m.execute(sw, scope);
                String o_testSub_literalMutationString65382__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString65382_literalMutationString66451 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65382_literalMutationString66451_failAssert0null78760 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i.R1i$]C not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65369_failAssert0_literalMutationString67187_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString65369 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0_literalMutationString67187 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65369_failAssert0_literalMutationString67194_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString65369 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0_literalMutationString67194 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65369_failAssert0_literalMutationString67194_failAssert0_add77175_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "Sam");
                    scope.put("", "asdlkfj");
                    m.execute(sw, scope);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSub_literalMutationString65369 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0_literalMutationString67194 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0_literalMutationString67194_failAssert0_add77175 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65381_literalMutationString66505_literalMutationString72874_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("sub.]html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSub_literalMutationString65381__9 = scope.put("name", "Template 0L0H2nsj;&E$fjp[A;7} not found");
            Object o_testSub_literalMutationString65381__10 = scope.put("randoid", "asdlkfj");
            Writer o_testSub_literalMutationString65381__11 = m.execute(sw, scope);
            String o_testSub_literalMutationString65381__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString65381_literalMutationString66505_literalMutationString72874 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sub.]html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_add65404_remove68266_literalMutationString70779_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testSub_add65404__3 = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            Mustache m = c.compile("sub.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSub_add65404__10 = scope.put("name", "Sam");
            Object o_testSub_add65404__11 = scope.put("randomid", "asdlkfj");
            Writer o_testSub_add65404__12 = m.execute(sw, scope);
            String o_testSub_add65404__13 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            org.junit.Assert.fail("testSub_add65404_remove68266_literalMutationString70779 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65371_failAssert0_literalMutationString67285_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("dY[d_B-");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString65371 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65371_failAssert0_literalMutationString67285 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dY[d_B- not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65368_remove68293_literalMutationString70662_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSub_literalMutationString65368__9 = scope.put("name", "Sam");
            Object o_testSub_literalMutationString65368__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSub_literalMutationString65368__11 = m.execute(sw, scope);
            String o_testSub_literalMutationString65368__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            org.junit.Assert.fail("testSub_literalMutationString65368_remove68293_literalMutationString70662 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65368() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Map scope = new HashMap();
        Object o_testSub_literalMutationString65368__9 = scope.put("name", "Sam");
        Assert.assertNull(o_testSub_literalMutationString65368__9);
        Object o_testSub_literalMutationString65368__10 = scope.put("randomid", "asdlkfj");
        Assert.assertNull(o_testSub_literalMutationString65368__10);
        Writer o_testSub_literalMutationString65368__11 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSub_literalMutationString65368__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSub_literalMutationString65368__11)).toString());
        String o_testSub_literalMutationString65368__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
        Assert.assertEquals("<html>\n<head>\n<title>Subtitle</title>\n<script src=\"jquery.min.js\"></script>\n</head>\n<body>\n<div class=\"body\">\n<div id=\"asdlkfj\">\nHello, Sam!\n</div>\n</div>\n<div class=\"footer\">\n</div>\n</body>\n</html>", o_testSub_literalMutationString65368__12);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertNull(o_testSub_literalMutationString65368__9);
        Assert.assertNull(o_testSub_literalMutationString65368__10);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSub_literalMutationString65368__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSub_literalMutationString65368__11)).toString());
        Assert.assertEquals("<html>\n<head>\n<title>Subtitle</title>\n<script src=\"jquery.min.js\"></script>\n</head>\n<body>\n<div class=\"body\">\n<div id=\"asdlkfj\">\nHello, Sam!\n</div>\n</div>\n<div class=\"footer\">\n</div>\n</body>\n</html>", o_testSub_literalMutationString65368__12);
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65369_failAssert0null68659_failAssert0_add76881_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put(null, "Sam");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSub_literalMutationString65369 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0null68659 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0null68659_failAssert0_add76881 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65371_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("dY[d_B+-");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString65371 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dY[d_B+- not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65369_failAssert0_literalMutationString67194_failAssert0_literalMutationString74276_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "S<am");
                    scope.put("", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSub_literalMutationString65369 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0_literalMutationString67194 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65369_failAssert0_literalMutationString67194_failAssert0_literalMutationString74276 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65371_failAssert0null68683_failAssert0null78434_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("dY[d_B+-");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put(null, "Sam");
                    scope.put("randomid", "asdlkfj");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSub_literalMutationString65371 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSub_literalMutationString65371_failAssert0null68683 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65371_failAssert0null68683_failAssert0null78434 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dY[d_B+- not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65371_failAssert0null68683_failAssert0_literalMutationString73358_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("dY[d_B+-");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put(null, "");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSub_literalMutationString65371 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSub_literalMutationString65371_failAssert0null68683 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65371_failAssert0null68683_failAssert0_literalMutationString73358 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dY[d_B+- not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65371_failAssert0null68683_failAssert0_literalMutationString73380_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("dY[d_B+-");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put(null, "Sam");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "subl.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSub_literalMutationString65371 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSub_literalMutationString65371_failAssert0null68683 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString65371_failAssert0null68683_failAssert0_literalMutationString73380 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dY[d_B+- not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString65369_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString65369 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5131_failAssert0null8399_failAssert0null18396_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("part]alsub.html");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", null);
                    scope.put("randomid", null);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubInPartial_literalMutationString5131 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0null8399 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0null8399_failAssert0null18396 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template part]alsub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5131_failAssert0null8399_failAssert0null18398_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("part]alsub.html");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "Sam");
                    scope.put("randomid", null);
                    m.execute(null, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubInPartial_literalMutationString5131 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0null8399 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0null8399_failAssert0null18398 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template part]alsub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartialnull5176_literalMutationString5436_literalMutationString12249_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartialnull5176__9 = scope.put("name", null);
            Object o_testSubInPartialnull5176__10 = scope.put("randomid", "aslkfj");
            Writer o_testSubInPartialnull5176__11 = m.execute(sw, scope);
            String o_testSubInPartialnull5176__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartialnull5176_literalMutationString5436_literalMutationString12249 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5130_failAssert0_literalMutationString7077_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, ">ub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString5130 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5130_failAssert0_literalMutationString7077 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartialnull5175_literalMutationString5338_literalMutationString12226_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("i0250j`X<oQp48k");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartialnull5175__9 = scope.put(null, "Sam");
            Object o_testSubInPartialnull5175__10 = scope.put("", "asdlkfj");
            Writer o_testSubInPartialnull5175__11 = m.execute(sw, scope);
            String o_testSubInPartialnull5175__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartialnull5175_literalMutationString5338_literalMutationString12226 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i0250j`X<oQp48k not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5134_failAssert0null8432_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Q#2(JW<OL>!cApD");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(null, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString5134 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5134_failAssert0null8432 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Q#2(JW<OL>!cApD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5130_failAssert0null8450_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString5130 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5130_failAssert0null8450 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5130_failAssert0_add7998_failAssert0_add16960_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "Sam");
                    scope.put("randomid", "asdlkfj");
                    scope.put("randomid", "asdlkfj");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubInPartial_literalMutationString5130 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubInPartial_literalMutationString5130_failAssert0_add7998 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5130_failAssert0_add7998_failAssert0_add16960 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5134_failAssert0_add7987_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Q#2(JW<OL>!cApD");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString5134 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5134_failAssert0_add7987 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Q#2(JW<OL>!cApD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5134_failAssert0null8431_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Q#2(JW<OL>!cApD");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", null);
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString5134 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5134_failAssert0null8431 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Q#2(JW<OL>!cApD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5130_failAssert0_add7998_failAssert0null18426_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "Sam");
                    scope.put("randomid", "asdlkfj");
                    scope.put("randomid", null);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubInPartial_literalMutationString5130 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubInPartial_literalMutationString5130_failAssert0_add7998 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5130_failAssert0_add7998_failAssert0null18426 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5130_failAssert0_add7998_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString5130 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5130_failAssert0_add7998 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_add5165_literalMutationString5497_failAssert0_add17163_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testSubInPartial_add5165__3 = c.compile("partialsub.html");
                Mustache m = c.compile("partialsub{.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSubInPartial_add5165__10 = scope.put("name", "Sam");
                Object o_testSubInPartial_add5165__11 = scope.put("randomid", "asdlkfj");
                Writer o_testSubInPartial_add5165__12 = m.execute(sw, scope);
                String o_testSubInPartial_add5165__13 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_add5165_literalMutationString5497 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_add5165_literalMutationString5497_failAssert0_add17163 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template partialsub{.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_add5165_literalMutationString5497_failAssert0null18651_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testSubInPartial_add5165__3 = c.compile("partialsub.html");
                Mustache m = c.compile("partialsub{.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSubInPartial_add5165__10 = scope.put("name", "Sam");
                Object o_testSubInPartial_add5165__11 = scope.put("randomid", "asdlkfj");
                Writer o_testSubInPartial_add5165__12 = m.execute(null, scope);
                String o_testSubInPartial_add5165__13 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_add5165_literalMutationString5497 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_add5165_literalMutationString5497_failAssert0null18651 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template partialsub{.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5152_literalMutationString5807_literalMutationString11809_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("#8(#4V&eLS>{!>p");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartial_literalMutationString5152__9 = scope.put("name", "Sam");
            Object o_testSubInPartial_literalMutationString5152__10 = scope.put("radomid", "Template 0L0H2nsj;&E$fjp[A;7} not found");
            Writer o_testSubInPartial_literalMutationString5152__11 = m.execute(sw, scope);
            String o_testSubInPartial_literalMutationString5152__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString5152_literalMutationString5807_literalMutationString11809 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #8(#4V&eLS>{!>p not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5131_failAssert0_literalMutationString6848_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("part]alsub.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("(FWdCjN$", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString5131 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0_literalMutationString6848 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template part]alsub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5131_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("part]alsub.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString5131 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template part]alsub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5134_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Q#2(JW<OL>!cApD");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString5134 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Q#2(JW<OL>!cApD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5130_failAssert0_literalMutationString7077_failAssert0_add17055_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "Sam");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, ">ub.txt");
                    TestUtil.getContents(AmplExtensionTest.root, ">ub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubInPartial_literalMutationString5130 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubInPartial_literalMutationString5130_failAssert0_literalMutationString7077 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5130_failAssert0_literalMutationString7077_failAssert0_add17055 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5134_failAssert0_add7986_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Q#2(JW<OL>!cApD");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString5134 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5134_failAssert0_add7986 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Q#2(JW<OL>!cApD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5131_failAssert0_add7956_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("part]alsub.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString5131 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0_add7956 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template part]alsub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5131_failAssert0null8399_failAssert0_add16936_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("part]alsub.html");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "Sam");
                    scope.put("randomid", null);
                    scope.put("randomid", null);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubInPartial_literalMutationString5131 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0null8399 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0null8399_failAssert0_add16936 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template part]alsub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5131_failAssert0null8399_failAssert0_literalMutationString13715_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("part]alsub.html");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "Sam");
                    scope.put("randomid", null);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testSubInPartial_literalMutationString5131 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0null8399 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0null8399_failAssert0_literalMutationString13715 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template part]alsub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5131_failAssert0null8399_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("part]alsub.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", null);
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString5131 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0null8399 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template part]alsub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_add5165_literalMutationString5497_failAssert0_literalMutationString14807_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testSubInPartial_add5165__3 = c.compile("dx;jD1QQT(<CNI0");
                Mustache m = c.compile("partialsub{.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSubInPartial_add5165__10 = scope.put("name", "Sam");
                Object o_testSubInPartial_add5165__11 = scope.put("randomid", "asdlkfj");
                Writer o_testSubInPartial_add5165__12 = m.execute(sw, scope);
                String o_testSubInPartial_add5165__13 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_add5165_literalMutationString5497 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_add5165_literalMutationString5497_failAssert0_literalMutationString14807 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dx;jD1QQT(<CNI0 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5131_failAssert0null8399_failAssert0_literalMutationString13703_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("part]alsub.html");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "");
                    scope.put("randomid", null);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubInPartial_literalMutationString5131 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0null8399 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0null8399_failAssert0_literalMutationString13703 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template part]alsub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5152_remove8031_literalMutationString10471_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("partia]lsub.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartial_literalMutationString5152__9 = scope.put("name", "Sam");
            Object o_testSubInPartial_literalMutationString5152__10 = scope.put("radomid", "asdlkfj");
            Writer o_testSubInPartial_literalMutationString5152__11 = m.execute(sw, scope);
            String o_testSubInPartial_literalMutationString5152__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            org.junit.Assert.fail("testSubInPartial_literalMutationString5152_remove8031_literalMutationString10471 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template partia]lsub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_add5165_literalMutationString5497_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testSubInPartial_add5165__3 = c.compile("partialsub.html");
            Mustache m = c.compile("partialsub{.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartial_add5165__10 = scope.put("name", "Sam");
            Object o_testSubInPartial_add5165__11 = scope.put("randomid", "asdlkfj");
            Writer o_testSubInPartial_add5165__12 = m.execute(sw, scope);
            String o_testSubInPartial_add5165__13 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_add5165_literalMutationString5497 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template partialsub{.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5129() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Map scope = new HashMap();
        Object o_testSubInPartial_literalMutationString5129__9 = scope.put("name", "Sam");
        Assert.assertNull(o_testSubInPartial_literalMutationString5129__9);
        Object o_testSubInPartial_literalMutationString5129__10 = scope.put("randomid", "asdlkfj");
        Assert.assertNull(o_testSubInPartial_literalMutationString5129__10);
        Writer o_testSubInPartial_literalMutationString5129__11 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubInPartial_literalMutationString5129__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubInPartial_literalMutationString5129__11)).toString());
        String o_testSubInPartial_literalMutationString5129__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
        Assert.assertEquals("<html>\n<head>\n<title>Subtitle</title>\n<script src=\"jquery.min.js\"></script>\n</head>\n<body>\n<div class=\"body\">\n<div id=\"asdlkfj\">\nHello, Sam!\n</div>\n</div>\n<div class=\"footer\">\n</div>\n</body>\n</html>", o_testSubInPartial_literalMutationString5129__12);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertNull(o_testSubInPartial_literalMutationString5129__9);
        Assert.assertNull(o_testSubInPartial_literalMutationString5129__10);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubInPartial_literalMutationString5129__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubInPartial_literalMutationString5129__11)).toString());
        Assert.assertEquals("<html>\n<head>\n<title>Subtitle</title>\n<script src=\"jquery.min.js\"></script>\n</head>\n<body>\n<div class=\"body\">\n<div id=\"asdlkfj\">\nHello, Sam!\n</div>\n</div>\n<div class=\"footer\">\n</div>\n</body>\n</html>", o_testSubInPartial_literalMutationString5129__12);
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5130_failAssert0_add7998_failAssert0_literalMutationString13804_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not fond");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "Sam");
                    scope.put("randomid", "asdlkfj");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubInPartial_literalMutationString5130 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubInPartial_literalMutationString5130_failAssert0_add7998 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5130_failAssert0_add7998_failAssert0_literalMutationString13804 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not fond not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5152_literalMutationString5807_literalMutationString11808_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("partial[sub.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartial_literalMutationString5152__9 = scope.put("name", "Sam");
            Object o_testSubInPartial_literalMutationString5152__10 = scope.put("radomid", "Template 0L0H2nsj;&E$fjp[A;7} not found");
            Writer o_testSubInPartial_literalMutationString5152__11 = m.execute(sw, scope);
            String o_testSubInPartial_literalMutationString5152__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString5152_literalMutationString5807_literalMutationString11808 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template partial[sub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5131_failAssert0null8399_failAssert0_add16940_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("part]alsub.html");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "Sam");
                    scope.put("randomid", null);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubInPartial_literalMutationString5131 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0null8399 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5131_failAssert0null8399_failAssert0_add16940 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template part]alsub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5134_failAssert0_literalMutationString7000_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Q#2(JW<OL>!cApD");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "Y#h&P9Z");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString5134 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5134_failAssert0_literalMutationString7000 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Q#2(JW<OL>!cApD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5130_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString5130 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5130_failAssert0_literalMutationString7077_failAssert0_literalMutationString14313_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "Sam");
                    scope.put("ranomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, ">ub.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubInPartial_literalMutationString5130 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubInPartial_literalMutationString5130_failAssert0_literalMutationString7077 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5130_failAssert0_literalMutationString7077_failAssert0_literalMutationString14313 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString5134_failAssert0_literalMutationString6994_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Q#2(JW<OL>!cApD");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("(bp<,$ps", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubInPartial_literalMutationString5134 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubInPartial_literalMutationString5134_failAssert0_literalMutationString6994 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Q#2(JW<OL>!cApD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95546_failAssert0_add97005_failAssert0null104596_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("!.xTD2G]}8xeA4P/!&RjmN");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString95546 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97005 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97005_failAssert0null104596 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !.xTD2G]}8xeA4P/!&RjmN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95560_literalMutationString95963_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile(":{?#N2XqPr@e .$iG(|3`f");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testPartialInSub_literalMutationString95560__9 = scope.put("randomid", "z@7b7oS");
            m.execute(sw, scope);
            String o_testPartialInSub_literalMutationString95560__11 = TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString95560_literalMutationString95963 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95546_failAssert0_add97005_failAssert0_literalMutationString101435_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("!.xTD2G]}8xeA4P/!&RjmN");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlPfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString95546 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97005 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97005_failAssert0_literalMutationString101435 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !.xTD2G]}8xeA4P/!&RjmN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95545_failAssert0_add96984_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString95545 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95545_failAssert0_add96984 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95546_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("!.xTD2G]}8xeA4P/!&RjmN");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString95546 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !.xTD2G]}8xeA4P/!&RjmN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95546_failAssert0_add97002_failAssert0_literalMutationString100229_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("!.xTD2G]}8xeA4P/!&RjmN");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString95546 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97002 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97002_failAssert0_literalMutationString100229 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !.xTD2G]}8xeA4P/!&RjmN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95545_failAssert0_literalMutationString96395_failAssert0_add103261_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "parrtialsubpartial.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString95545 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString95545_failAssert0_literalMutationString96395 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95545_failAssert0_literalMutationString96395_failAssert0_add103261 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSubnull95578_failAssert0null97179_failAssert0_literalMutationString99849_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("s[%a%D{BV(J.ez!x$-,(j$");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSubnull95578 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testPartialInSubnull95578_failAssert0null97179 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testPartialInSubnull95578_failAssert0null97179_failAssert0_literalMutationString99849 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template s[%a%D{BV(J.ez!x$-,(j$ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95546_failAssert0_add97005_failAssert0_add103454_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("!.xTD2G]}8xeA4P/!&RjmN");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString95546 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97005 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97005_failAssert0_add103454 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !.xTD2G]}8xeA4P/!&RjmN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95545_failAssert0_literalMutationString96395_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "parrtialsubpartial.txt");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString95545 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95545_failAssert0_literalMutationString96395 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95557_literalMutationString96056_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("/BP>:f5@:D*F!>#G{d#/Oc");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testPartialInSub_literalMutationString95557__9 = scope.put("randomid", "Template 0L0H2nsj;&E$fjp[A;7} not found");
            Writer o_testPartialInSub_literalMutationString95557__10 = m.execute(sw, scope);
            String o_testPartialInSub_literalMutationString95557__11 = TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString95557_literalMutationString96056 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /BP>:f5@:D*F!>#G{d#/Oc not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_add95568_literalMutationString95761_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testPartialInSub_add95568__3 = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            Mustache m = c.compile("partialsubpartial.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testPartialInSub_add95568__10 = scope.put("randomid", "asdlkfj");
            Writer o_testPartialInSub_add95568__11 = m.execute(sw, scope);
            String o_testPartialInSub_add95568__12 = TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_add95568_literalMutationString95761 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95546_failAssert0_add97002_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("!.xTD2G]}8xeA4P/!&RjmN");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString95546 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97002 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !.xTD2G]}8xeA4P/!&RjmN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95545_failAssert0_literalMutationString96384_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString95545 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95545_failAssert0_literalMutationString96384 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95546_failAssert0_add97002_failAssert0null104295_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("!.xTD2G]}8xeA4P/!&RjmN");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    scope.put("randomid", "asdlkfj");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString95546 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97002 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97002_failAssert0null104295 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !.xTD2G]}8xeA4P/!&RjmN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95546_failAssert0_literalMutationString96467_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("!.xTD2G]}8xeA4P/!&RjmN");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "partialsubprtial.txt");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString95546 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_literalMutationString96467 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !.xTD2G]}8xeA4P/!&RjmN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95551_literalMutationString96154_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("partialsubpartial.h%ml");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testPartialInSub_literalMutationString95551__9 = scope.put("Template 0L0H2nsj;&E$fjp[A;7} not found", "asdlkfj");
            Writer o_testPartialInSub_literalMutationString95551__10 = m.execute(sw, scope);
            String o_testPartialInSub_literalMutationString95551__11 = TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString95551_literalMutationString96154 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template partialsubpartial.h%ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95566_failAssert0_literalMutationString96495_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("partialsubparti}l.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "`*lElW[zWme_^XSXx9*-G");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString95566 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95566_failAssert0_literalMutationString96495 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template partialsubparti}l.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95546_failAssert0_literalMutationString96460_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("!.xTD2G]}8xeA4P/!&RjmN");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "aslkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString95546 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_literalMutationString96460 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !.xTD2G]}8xeA4P/!&RjmN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95553_literalMutationString96017_literalMutationString99491_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testPartialInSub_literalMutationString95553__9 = scope.put("rgnyomid", "asdlkfj");
            Writer o_testPartialInSub_literalMutationString95553__10 = m.execute(sw, scope);
            String o_testPartialInSub_literalMutationString95553__11 = TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString95553_literalMutationString96017_literalMutationString99491 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95546_failAssert0null97241_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("!.xTD2G]}8xeA4P/!&RjmN");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(null, scope);
                TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString95546 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0null97241 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !.xTD2G]}8xeA4P/!&RjmN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95546_failAssert0_add97005_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("!.xTD2G]}8xeA4P/!&RjmN");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString95546 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97005 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !.xTD2G]}8xeA4P/!&RjmN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95546_failAssert0_add97002_failAssert0_add103130_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("!.xTD2G]}8xeA4P/!&RjmN");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString95546 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97002 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95546_failAssert0_add97002_failAssert0_add103130 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !.xTD2G]}8xeA4P/!&RjmN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSubnull95577_literalMutationString95699_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile(" artialsubpartial.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testPartialInSubnull95577__9 = scope.put("randomid", null);
            Writer o_testPartialInSubnull95577__10 = m.execute(sw, scope);
            String o_testPartialInSubnull95577__11 = TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSubnull95577_literalMutationString95699 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  artialsubpartial.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_add95572null97054_literalMutationString99516_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("partialsubpart{ial.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testPartialInSub_add95572__9 = scope.put(null, "asdlkfj");
            Writer o_testPartialInSub_add95572__10 = m.execute(sw, scope);
            sw.toString();
            sw.toString();
            String o_testPartialInSub_add95572__12 = TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            org.junit.Assert.fail("testPartialInSub_add95572null97054_literalMutationString99516 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template partialsubpart{ial.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95545_failAssert0_literalMutationString96395_failAssert0_literalMutationString100742_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString95545 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString95545_failAssert0_literalMutationString96395 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95545_failAssert0_literalMutationString96395_failAssert0_literalMutationString100742 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95545_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString95545 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95544() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Map scope = new HashMap();
        Object o_testPartialInSub_literalMutationString95544__9 = scope.put("randomid", "asdlkfj");
        Assert.assertNull(o_testPartialInSub_literalMutationString95544__9);
        Writer o_testPartialInSub_literalMutationString95544__10 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialInSub_literalMutationString95544__10)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialInSub_literalMutationString95544__10)).toString());
        String o_testPartialInSub_literalMutationString95544__11 = TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
        Assert.assertEquals("<div id=\"asdlkfj\">\n<html>\n<head><title>Items</title></head>\n<body>\n<ol>\n<li>Item 1</li>\n<li>Item 2</li>\n<li>Item 3</li>\n</ol>\n</body>\n</html>\n</div>", o_testPartialInSub_literalMutationString95544__11);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertNull(o_testPartialInSub_literalMutationString95544__9);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialInSub_literalMutationString95544__10)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialInSub_literalMutationString95544__10)).toString());
        Assert.assertEquals("<div id=\"asdlkfj\">\n<html>\n<head><title>Items</title></head>\n<body>\n<ol>\n<li>Item 1</li>\n<li>Item 2</li>\n<li>Item 3</li>\n</ol>\n</body>\n</html>\n</div>", o_testPartialInSub_literalMutationString95544__11);
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString95545_failAssert0_literalMutationString96395_failAssert0null104423_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPartialInSub_literalMutationString95545 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPartialInSub_literalMutationString95545_failAssert0_literalMutationString96395 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString95545_failAssert0_literalMutationString96395_failAssert0null104423 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString215_failAssert0null4654_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("/)&jEgsrfnl`4%g@(");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString215 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString215_failAssert0null4654 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgsrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString1_remove492_literalMutationString1120_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Writer o_testFollow_literalMutationString1__7 = m.execute(sw, new Object());
            String o_testFollow_literalMutationString1__9 = TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            org.junit.Assert.fail("testFollow_literalMutationString1_remove492_literalMutationString1120 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString8_failAssert0_literalMutationString237_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("!%Gq@3O!kUvX,j7EG");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString8 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString8_failAssert0_literalMutationString237 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testFollownull19_failAssert0_literalMutationString159_failAssert0_literalMutationString2078_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "follownomenu.tt");
                    sw.toString();
                    org.junit.Assert.fail("testFollownull19 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testFollownull19_failAssert0_literalMutationString159 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testFollownull19_failAssert0_literalMutationString159_failAssert0_literalMutationString2078 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_add450_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("/)&jEgsrfnl`4%g@(");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_add450 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgsrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString12_failAssert0_literalMutationString223_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("PRR p6Z?YMB@@Z,<f");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "iJL#&gY4 B()_&2G");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString12 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString12_failAssert0_literalMutationString223 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template PRR p6Z?YMB@@Z,<f not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_add13_literalMutationString68_failAssert0_add3709_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testFollow_add13__3 = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache m = c.compile("follownomenu.html");
                StringWriter sw = new StringWriter();
                Writer o_testFollow_add13__8 = m.execute(sw, new Object());
                String o_testFollow_add13__10 = TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_add13_literalMutationString68 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_add13_literalMutationString68_failAssert0_add3709 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString215_failAssert0_add4060_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("/)&jEgsrfnl`4%g@(");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                    TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString215 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString215_failAssert0_add4060 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgsrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString216_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("/)&jEgsrfnl`4%g@(");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follown6menu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString216 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgsrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString10_failAssert0_add482_failAssert0_literalMutationString2467_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "follo,nomenu.txt");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString10 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString10_failAssert0_add482 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString10_failAssert0_add482_failAssert0_literalMutationString2467 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString2_failAssert0_add432_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0_add432 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString2_failAssert0_add435_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0_add435 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString215_failAssert0_literalMutationString2757_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("/)&jEgsrfnl`4%g@(");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString215 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString215_failAssert0_literalMutationString2757 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgsrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString10_failAssert0_literalMutationString290_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follo,nomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString10 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString10_failAssert0_literalMutationString290 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_add16null507_failAssert0_literalMutationString2299_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Writer o_testFollow_add16__7 = m.execute(sw, new Object());
                sw.toString();
                sw.toString();
                String o_testFollow_add16__10 = TestUtil.getContents(AmplExtensionTest.root, null);
                org.junit.Assert.fail("testFollow_add16null507 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testFollow_add16null507_failAssert0_literalMutationString2299 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString212_failAssert0null4505_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("/)&jEgNrfnl`4%g@(");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString212 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString212_failAssert0null4505 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgNrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_add446_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("/)&jEgsrfnl`4%g@(");
                Mustache m = c.compile("/)&jEgsrfnl`4%g@(");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_add446 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgsrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString2_failAssert0_add435_failAssert0_add3556_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString2 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0_add435 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0_add435_failAssert0_add3556 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString3_failAssert0null527_failAssert0_literalMutationString1475_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("#sInF[,9i{[c 6 HT%");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString3 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString3_failAssert0null527 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString3_failAssert0null527_failAssert0_literalMutationString1475 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #sInF[,9i{[c 6 HT% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString9_failAssert0_literalMutationString278_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.#txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString9 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString9_failAssert0_literalMutationString278 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_add13_literalMutationString68_failAssert0_add3707_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testFollow_add13__3 = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache m = c.compile("follownomenu.html");
                StringWriter sw = new StringWriter();
                Writer o_testFollow_add13__8 = m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                String o_testFollow_add13__10 = TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_add13_literalMutationString68 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_add13_literalMutationString68_failAssert0_add3707 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString212_failAssert0_literalMutationString2201_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("/)&jEgNrfnl`4%g@(");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, ")fY7l=NJ{#%Hydt:");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString212 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString212_failAssert0_literalMutationString2201 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgNrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollownull19_failAssert0_literalMutationString154_failAssert0_literalMutationString2707_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("x]p:!WNbdzfoI3N[k");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "Q^vA9O/<+0#{I;2e");
                    sw.toString();
                    org.junit.Assert.fail("testFollownull19 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testFollownull19_failAssert0_literalMutationString154 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testFollownull19_failAssert0_literalMutationString154_failAssert0_literalMutationString2707 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template x]p:!WNbdzfoI3N[k not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_add449_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("/)&jEgsrfnl`4%g@(");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_add449 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgsrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString2_failAssert0_add432_failAssert0_add3954_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString2 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0_add432 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0_add432_failAssert0_add3954 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString6_failAssert0_literalMutationString267_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("%%RkxETm9j3ipuK0_");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString6_failAssert0_literalMutationString267 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template %%RkxETm9j3ipuK0_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString2_failAssert0_add432_failAssert0_literalMutationString2521_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;DE$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString2 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0_add432 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0_add432_failAssert0_literalMutationString2521 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;DE$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString11_failAssert0_add469_failAssert0_literalMutationString1556_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("`ollownomenu.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "fllownomenu.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString11 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString11_failAssert0_add469 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString11_failAssert0_add469_failAssert0_literalMutationString1556 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `ollownomenu.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString216_failAssert0_add3769_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("/)&jEgsrfnl`4%g@(");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "follown6menu.txt");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString216 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString216_failAssert0_add3769 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgsrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString2_failAssert0_add435_failAssert0_literalMutationString1704_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsp;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString2 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0_add435 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0_add435_failAssert0_literalMutationString1704 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsp;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_add14_literalMutationString108_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("follownomen}u.html");
            StringWriter sw = new StringWriter();
            Writer o_testFollow_add14__7 = m.execute(sw, new Object());
            Writer o_testFollow_add14__9 = m.execute(sw, new Object());
            String o_testFollow_add14__11 = TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_add14_literalMutationString108 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template follownomen}u.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollownull20_failAssert0_literalMutationString165_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("&4c[@xl=o;yG4xZ(1");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testFollownull20 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testFollownull20_failAssert0_literalMutationString165 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template &4c[@xl=o;yG4xZ(1 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollownull20_failAssert0_literalMutationString161_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testFollownull20 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testFollownull20_failAssert0_literalMutationString161 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0null530_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("/)&jEgsrfnl`4%g@(");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0null530 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgsrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString11_failAssert0_literalMutationString254_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "fllownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString11 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString11_failAssert0_literalMutationString254 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString2_failAssert0null521_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0null521 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString2_failAssert0_literalMutationString176_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Tem<plate 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0_literalMutationString176 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Tem<plate 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0null529_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("/)&jEgsrfnl`4%g@(");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0null529 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgsrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString212_failAssert0_add3774_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("/)&jEgNrfnl`4%g@(");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString212 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString212_failAssert0_add3774 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgNrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString2_failAssert0_literalMutationString181_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownome0nu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0_literalMutationString181 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString2_failAssert0null520_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0null520 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString5_failAssert0_add440_failAssert0_literalMutationString1862_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("X<RKrcf?v>v7]#YB");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString5 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString5_failAssert0_add440 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString5_failAssert0_add440_failAssert0_literalMutationString1862 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template X<RKrcf?v>v7]#YB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString10_failAssert0_add481_failAssert0_literalMutationString1601_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("+z3;cgN=ewPT&<fSA");
                    Mustache m = c.compile("follownomenu.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "follo,nomenu.txt");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString10 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString10_failAssert0_add481 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString10_failAssert0_add481_failAssert0_literalMutationString1601 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template +z3;cgN=ewPT&<fSA not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString212_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("/)&jEgNrfnl`4%g@(");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString212 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgNrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_add13_literalMutationString68_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testFollow_add13__3 = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            Mustache m = c.compile("follownomenu.html");
            StringWriter sw = new StringWriter();
            Writer o_testFollow_add13__8 = m.execute(sw, new Object());
            String o_testFollow_add13__10 = TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_add13_literalMutationString68 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString215_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("/)&jEgsrfnl`4%g@(");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString215 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgsrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_add13_literalMutationString74_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testFollow_add13__3 = c.compile("follownomenu.html");
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Writer o_testFollow_add13__8 = m.execute(sw, new Object());
            String o_testFollow_add13__10 = TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_add13_literalMutationString74 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollownull19_failAssert0_literalMutationString149_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollownull19 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testFollownull19_failAssert0_literalMutationString149 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString1() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testFollow_literalMutationString1__7 = m.execute(sw, new Object());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testFollow_literalMutationString1__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testFollow_literalMutationString1__7)).toString());
        String o_testFollow_literalMutationString1__9 = TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
        Assert.assertEquals("<div class=\"js-follow-combo follow-combo btn-group js-actionable-user not-following\" data-user-id=\"18139619\" data-screen-name=\"pepsi\">\n  <a class=\"follow-btn btn js-combo-btn js-recommended-item\">\n    <div class=\"js-action-follow follow-text action-text\" data-user-id=\"18139619\">\n      <i class=\"follow\"></i>\n      Follow\n    </div>\n    <div class=\"following-text action-text\">\n      Following\n    </div>\n    <div class=\"js-action-unfollow unfollow-text action-text\" data-user-id=\"18139619\">\n      Unfollow\n    </div>\n    <div class=\"block-text action-text\">\n      Blocked\n    </div>\n    <div class=\"js-action-unblock unblock-text action-text\" data-user-id=\"18139619\">\n      Unblock\n    </div>\n    <div class=\"pending-text action-text\">\n      Pending\n    </div>\n    <div class=\"js-action-unfollow cancel-req-text action-text\" data-user-id=\"18139619\">\n      Cancel\n    </div>\n  </a>\n</div>", o_testFollow_literalMutationString1__9);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testFollow_literalMutationString1__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testFollow_literalMutationString1__7)).toString());
        Assert.assertEquals("<div class=\"js-follow-combo follow-combo btn-group js-actionable-user not-following\" data-user-id=\"18139619\" data-screen-name=\"pepsi\">\n  <a class=\"follow-btn btn js-combo-btn js-recommended-item\">\n    <div class=\"js-action-follow follow-text action-text\" data-user-id=\"18139619\">\n      <i class=\"follow\"></i>\n      Follow\n    </div>\n    <div class=\"following-text action-text\">\n      Following\n    </div>\n    <div class=\"js-action-unfollow unfollow-text action-text\" data-user-id=\"18139619\">\n      Unfollow\n    </div>\n    <div class=\"block-text action-text\">\n      Blocked\n    </div>\n    <div class=\"js-action-unblock unblock-text action-text\" data-user-id=\"18139619\">\n      Unblock\n    </div>\n    <div class=\"pending-text action-text\">\n      Pending\n    </div>\n    <div class=\"js-action-unfollow cancel-req-text action-text\" data-user-id=\"18139619\">\n      Cancel\n    </div>\n  </a>\n</div>", o_testFollow_literalMutationString1__9);
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString3_failAssert0_literalMutationString201_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(";@ktI:|[{oBYoFY`xR");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString3_failAssert0_literalMutationString201 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ;@ktI:|[{oBYoFY`xR not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("/)&jEgsrfnl`4%g@(");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgsrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString2_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_literalMutationString2 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_remove17_literalMutationString130_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("`,]#jyX&K8:G?+v5P");
            StringWriter sw = new StringWriter();
            String o_testFollow_remove17__7 = TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_remove17_literalMutationString130 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `,]#jyX&K8:G?+v5P not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString216_failAssert0null4503_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("/)&jEgsrfnl`4%g@(");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString216 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString216_failAssert0null4503 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgsrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString212_failAssert0_add3773_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("/)&jEgNrfnl`4%g@(");
                    Mustache m = c.compile("/)&jEgNrfnl`4%g@(");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString212 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString212_failAssert0_add3773 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgNrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_add13_literalMutationString68_failAssert0_literalMutationString2021_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testFollow_add13__3 = c.compile(" does not exist");
                Mustache m = c.compile("follownomenu.html");
                StringWriter sw = new StringWriter();
                Writer o_testFollow_add13__8 = m.execute(sw, new Object());
                String o_testFollow_add13__10 = TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_add13_literalMutationString68 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_add13_literalMutationString68_failAssert0_literalMutationString2021 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString12_failAssert0_literalMutationString221_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "iJL#&gY4 B()_&2G");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString12 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString12_failAssert0_literalMutationString221 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_add16_add372_literalMutationString963_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("1uLXo@0HTGPGGab]Y");
            m.getName();
            StringWriter sw = new StringWriter();
            Writer o_testFollow_add16__7 = m.execute(sw, new Object());
            sw.toString();
            String o_testFollow_add16__10 = TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_add16_add372_literalMutationString963 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testFollow_add13_literalMutationString68_failAssert0_literalMutationString2035_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testFollow_add13__3 = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache m = c.compile("follownomenu.html");
                StringWriter sw = new StringWriter();
                Writer o_testFollow_add13__8 = m.execute(sw, new Object());
                String o_testFollow_add13__10 = TestUtil.getContents(AmplExtensionTest.root, "!TVQY=)[}-4G/hP]");
                sw.toString();
                org.junit.Assert.fail("testFollow_add13_literalMutationString68 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_add13_literalMutationString68_failAssert0_literalMutationString2035 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_add13_literalMutationString68_failAssert0null4466_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testFollow_add13__3 = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache m = c.compile("follownomenu.html");
                StringWriter sw = new StringWriter();
                Writer o_testFollow_add13__8 = m.execute(null, new Object());
                String o_testFollow_add13__10 = TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_add13_literalMutationString68 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_add13_literalMutationString68_failAssert0null4466 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString11_failAssert0_literalMutationString257_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("*}@8Ap^zpXE&6lf{Y");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "fllownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString11 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString11_failAssert0_literalMutationString257 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *}@8Ap^zpXE&6lf{Y not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString212_failAssert0null4506_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("/)&jEgNrfnl`4%g@(");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString212 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString212_failAssert0null4506 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /)&jEgNrfnl`4%g@( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString7_failAssert0_literalMutationString246_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString7 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString7_failAssert0_literalMutationString246 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString2_failAssert0_add431_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString2_failAssert0_add431 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions.h%tml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multileextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_add60537_failAssert0_add63736_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("multipleextensions.h%tml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60537 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60537_failAssert0_add63736 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60315_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(".r^]nrbT$yF `enx#R$}*u2A");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60315 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .r^]nrbT$yF `enx#R$}*u2A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60316_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensifns.h%tml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60316 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensifns.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_remove60097_add60473_literalMutationString61278_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("mmq*5lSB;5k8]pV!fy-2SB*");
            StringWriter sw = new StringWriter();
            String o_testMultipleExtensions_remove60097__7 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_remove60097_add60473_literalMutationString61278 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mmq*5lSB;5k8]pV!fy-2SB* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0null60615_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions.h%tml");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0null60615 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60086_failAssert0_literalMutationString60335_failAssert0_add63958_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("gy8)P^DA7dW>4%UZqd`F5- ");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multiple|extensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086_failAssert0_literalMutationString60335 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086_failAssert0_literalMutationString60335_failAssert0_add63958 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gy8)P^DA7dW>4%UZqd`F5-  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60083_failAssert0null60606_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083_failAssert0null60606 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("multipleextensions.h%tml");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_add60536_failAssert0null64821_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("multipleextensions.h%tml");
                    Mustache m = c.compile("multipleextensions.h%tml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60536 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60536_failAssert0null64821 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60083_failAssert0_literalMutationString60287_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "D?I[h 9GKbjBW.#I,Ic[U9");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083_failAssert0_literalMutationString60287 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add60093_literalMutationString60182_failAssert0null64665_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testMultipleExtensions_add60093__3 = c.compile("multipleextensions|.html");
                Mustache m = c.compile(null);
                StringWriter sw = new StringWriter();
                Writer o_testMultipleExtensions_add60093__8 = m.execute(sw, new Object());
                String o_testMultipleExtensions_add60093__10 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_add60093_literalMutationString60182 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_add60093_literalMutationString60182_failAssert0null64665 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions|.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add60093_literalMutationString60182_failAssert0_literalMutationString62070_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testMultipleExtensions_add60093__3 = c.compile("multipleextensions|.html");
                Mustache m = c.compile("multipleextensions.html");
                StringWriter sw = new StringWriter();
                Writer o_testMultipleExtensions_add60093__8 = m.execute(sw, new Object());
                String o_testMultipleExtensions_add60093__10 = TestUtil.getContents(AmplExtensionTest.root, "multipleextnsions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_add60093_literalMutationString60182 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_add60093_literalMutationString60182_failAssert0_literalMutationString62070 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions|.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_add60536_failAssert0_add64162_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("multipleextensions.h%tml");
                    Mustache m = c.compile("multipleextensions.h%tml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60536 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60536_failAssert0_add64162 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_remove60097_literalMutationString60210_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("(2wq9];(l;qeV&MjVVvGy2_");
            StringWriter sw = new StringWriter();
            String o_testMultipleExtensions_remove60097__7 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_remove60097_literalMutationString60210 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template (2wq9];(l;qeV&MjVVvGy2_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60083_failAssert0_add60524_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083_failAssert0_add60524 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60083_failAssert0_add60521_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083_failAssert0_add60521 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add60094null60576_failAssert0_literalMutationString63031_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("B$tBP6>H!%3k*VKDldDC*%H");
                StringWriter sw = new StringWriter();
                Writer o_testMultipleExtensions_add60094__7 = m.execute(sw, new Object());
                Writer o_testMultipleExtensions_add60094__9 = m.execute(sw, new Object());
                String o_testMultipleExtensions_add60094__11 = TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_add60094null60576 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testMultipleExtensions_add60094null60576_failAssert0_literalMutationString63031 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template B$tBP6>H!%3k*VKDldDC*%H not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60086_failAssert0_literalMutationString60335_failAssert0_literalMutationString62266_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("gy8)P^DA7!W>4%UZqd`F5- ");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multiple|extensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086_failAssert0_literalMutationString60335 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086_failAssert0_literalMutationString60335_failAssert0_literalMutationString62266 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gy8)P^DA7!W>4%UZqd`F5-  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60086_failAssert0_literalMutationString60335_failAssert0null64718_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("gy8)P^DA7dW>4%UZqd`F5- ");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086_failAssert0_literalMutationString60335 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086_failAssert0_literalMutationString60335_failAssert0null64718 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gy8)P^DA7dW>4%UZqd`F5-  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60090_failAssert0_literalMutationString60350_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("vf*!zYF3jZCuXeB^VC]C-O<");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextesions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60090 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60090_failAssert0_literalMutationString60350 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vf*!zYF3jZCuXeB^VC]C-O< not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60083_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60090_failAssert0null60624_failAssert0_literalMutationString61482_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("`H^`8.Kl;2!q}<A2k(v#v/O");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextesions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60090 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60090_failAssert0null60624 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60090_failAssert0null60624_failAssert0_literalMutationString61482 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `H^`8.Kl;2!q}<A2k(v#v/O not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60083_failAssert0_literalMutationString60279_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(" does not exist");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083_failAssert0_literalMutationString60279 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60086_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("gy8)P^DA7dW>4%UZqd`F5- ");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gy8)P^DA7dW>4%UZqd`F5-  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60086_failAssert0_literalMutationString60331_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("gy8)P^DA7dW>4%UZqd`F5- ");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086_failAssert0_literalMutationString60331 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gy8)P^DA7dW>4%UZqd`F5-  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60086_failAssert0null60618_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("gy8)P^DA7dW>4%UZqd`F5- ");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086_failAssert0null60618 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gy8)P^DA7dW>4%UZqd`F5-  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320_failAssert0_add63935_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("multipleextensions.h%tml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multileextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320_failAssert0_add63935 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60082_literalMutationString60220_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleExtensions_literalMutationString60082__7 = m.execute(sw, new Object());
            String o_testMultipleExtensions_literalMutationString60082__9 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60082_literalMutationString60220 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60083_failAssert0_literalMutationString60285_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipeextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083_failAssert0_literalMutationString60285 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add60093_literalMutationString60182_failAssert0_add63867_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testMultipleExtensions_add60093__3 = c.compile("multipleextensions|.html");
                Mustache m = c.compile("multipleextensions.html");
                StringWriter sw = new StringWriter();
                Writer o_testMultipleExtensions_add60093__8 = m.execute(sw, new Object());
                String o_testMultipleExtensions_add60093__10 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_add60093_literalMutationString60182 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_add60093_literalMutationString60182_failAssert0_add63867 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions|.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60086_failAssert0_add60542_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("gy8)P^DA7dW>4%UZqd`F5- ");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086_failAssert0_add60542 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gy8)P^DA7dW>4%UZqd`F5-  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320_failAssert0_literalMutationString62216_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("muldipleextensions.h%tml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multileextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320_failAssert0_literalMutationString62216 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template muldipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320_failAssert0_literalMutationString62225_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("multipleextensions.h%tml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "U/w/QdV.uTSE6J):;Rl:]");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320_failAssert0_literalMutationString62225 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_add60537_failAssert0null64588_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("multipleextensions.h%tml");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60537 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60537_failAssert0null64588 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60082() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testMultipleExtensions_literalMutationString60082__7 = m.execute(sw, new Object());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleExtensions_literalMutationString60082__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleExtensions_literalMutationString60082__7)).toString());
        String o_testMultipleExtensions_literalMutationString60082__9 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
        Assert.assertEquals("foobar", o_testMultipleExtensions_literalMutationString60082__9);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleExtensions_literalMutationString60082__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleExtensions_literalMutationString60082__7)).toString());
        Assert.assertEquals("foobar", o_testMultipleExtensions_literalMutationString60082__9);
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320_failAssert0_add63936_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("multipleextensions.h%tml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multileextensions.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "multileextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320_failAssert0_add63936 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_add60536_failAssert0_literalMutationString62653_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache m = c.compile("multipleextensions.h%tml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60536 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60536_failAssert0_literalMutationString62653 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_add60536_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("multipleextensions.h%tml");
                Mustache m = c.compile("multipleextensions.h%tml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60536 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60090_failAssert0null60625_failAssert0_literalMutationString62536_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60090 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60090_failAssert0null60625 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60090_failAssert0null60625_failAssert0_literalMutationString62536 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60086_failAssert0null60619_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("gy8)P^DA7dW>4%UZqd`F5- ");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086_failAssert0null60619 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gy8)P^DA7dW>4%UZqd`F5-  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60088_failAssert0null60628_failAssert0_literalMutationString62604_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("F![mT</M9_N7ir-H>7h!F3u");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60088 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60088_failAssert0null60628 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60088_failAssert0null60628_failAssert0_literalMutationString62604 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template F![mT</M9_N7ir-H>7h!F3u not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensionsnull60099_failAssert0_literalMutationString60241_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensionsnull60099 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testMultipleExtensionsnull60099_failAssert0_literalMutationString60241 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60086_failAssert0_add60544_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("gy8)P^DA7dW>4%UZqd`F5- ");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086_failAssert0_add60544 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gy8)P^DA7dW>4%UZqd`F5-  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60083_failAssert0_add60525_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083_failAssert0_add60525 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320_failAssert0null64705_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("multipleextensions.h%tml");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multileextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_literalMutationString60320_failAssert0null64705 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60091_failAssert0_literalMutationString60266_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("OXUDax}Y]nfO(=LrM:(l&A6");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "tx9i{#%XLO}2u{l>:Is^9C");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60091 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60091_failAssert0_literalMutationString60266 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template OXUDax}Y]nfO(=LrM:(l&A6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60086_failAssert0_add60543_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("gy8)P^DA7dW>4%UZqd`F5- ");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086_failAssert0_add60543 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gy8)P^DA7dW>4%UZqd`F5-  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60085_failAssert0_add60534_failAssert0_literalMutationString62672_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60085 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60085_failAssert0_add60534 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60085_failAssert0_add60534_failAssert0_literalMutationString62672 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add60093_literalMutationString60182_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testMultipleExtensions_add60093__3 = c.compile("multipleextensions|.html");
            Mustache m = c.compile("multipleextensions.html");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleExtensions_add60093__8 = m.execute(sw, new Object());
            String o_testMultipleExtensions_add60093__10 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_add60093_literalMutationString60182 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions|.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_add60537_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions.h%tml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60537 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_add60537_failAssert0_literalMutationString61769_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("multipleextensions`.h%tml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60537 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60537_failAssert0_literalMutationString61769 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions`.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60086_failAssert0_literalMutationString60335_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("gy8)P^DA7dW>4%UZqd`F5- ");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multiple|extensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60086_failAssert0_literalMutationString60335 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gy8)P^DA7dW>4%UZqd`F5-  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0_add60540_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions.h%tml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0_add60540 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60083_failAssert0null60607_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60083_failAssert0null60607 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60082_add60483() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testMultipleExtensions_literalMutationString60082_add60483__7 = m.execute(sw, new Object());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleExtensions_literalMutationString60082_add60483__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleExtensions_literalMutationString60082_add60483__7)).toString());
        Writer o_testMultipleExtensions_literalMutationString60082__7 = m.execute(sw, new Object());
        String o_testMultipleExtensions_literalMutationString60082__9 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
        Assert.assertEquals("foobar", o_testMultipleExtensions_literalMutationString60082__9);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleExtensions_literalMutationString60082_add60483__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleExtensions_literalMutationString60082_add60483__7)).toString());
        Assert.assertEquals("foobar", o_testMultipleExtensions_literalMutationString60082__9);
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_remove60097_literalMutationString60208_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            String o_testMultipleExtensions_remove60097__7 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_remove60097_literalMutationString60208 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString60084_failAssert0null60616_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions.h%tml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString60084_failAssert0null60616 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h%tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53134_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Bi@J:k<b6:.T");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String replace = "false";
            });
            TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString53134 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Bi@J:k<b6:.T not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53132_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String replace = "false";
            });
            TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString53132 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplacenull53156_failAssert0null54081_failAssert0_literalMutationString55669_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("k#M&Q^sp[Qtf");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {
                        String replace = "false";
                    });
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testParentReplacenull53156 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testParentReplacenull53156_failAssert0null54081 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testParentReplacenull53156_failAssert0null54081_failAssert0_literalMutationString55669 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template k#M&Q^sp[Qtf not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53132_failAssert0_literalMutationString53658_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "4alse";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53132 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53132_failAssert0_literalMutationString53658 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53132_failAssert0_literalMutationString53665_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.:xt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53132 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53132_failAssert0_literalMutationString53665 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_remove53154_literalMutationString53319_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("replac[.html");
            StringWriter sw = new StringWriter();
            Writer o_testParentReplace_remove53154__7 = m.execute(sw, new Object() {
                String replace = "false";
            });
            String o_testParentReplace_remove53154__11 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_remove53154_literalMutationString53319 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template replac[.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53133_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("replace[html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String replace = "false";
            });
            TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString53133 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template replace[html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53139_literalMutationString53430_failAssert0_literalMutationString57258_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Writer o_testParentReplace_literalMutationString53139__7 = m.execute(sw, new Object() {
                    String replace = "Template 0L0H2nsj;&E$fjp[A;7} not found";
                });
                String o_testParentReplace_literalMutationString53139__12 = TestUtil.getContents(AmplExtensionTest.root, "!Cz!5H&18%?");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53139_literalMutationString53430 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53139_literalMutationString53430_failAssert0_literalMutationString57258 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53139_literalMutationString53430_failAssert0_literalMutationString57255_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Writer o_testParentReplace_literalMutationString53139__7 = m.execute(sw, new Object() {
                    String replace = "Template 0L0H2nsj;&E$fjp[A;7} not found";
                });
                String o_testParentReplace_literalMutationString53139__12 = TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53139_literalMutationString53430 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53139_literalMutationString53430_failAssert0_literalMutationString57255 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53134_failAssert0_add53997_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Bi@J:k<b6:.T");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53134 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53134_failAssert0_add53997 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Bi@J:k<b6:.T not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53148_failAssert0_literalMutationString53599_failAssert0_literalMutationString57452_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String replace = "fOalse";
                    });
                    TestUtil.getContents(AmplExtensionTest.root, "_IvKUH4g4L^");
                    sw.toString();
                    org.junit.Assert.fail("testParentReplace_literalMutationString53148 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testParentReplace_literalMutationString53148_failAssert0_literalMutationString53599 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53148_failAssert0_literalMutationString53599_failAssert0_literalMutationString57452 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53139_literalMutationString53430_failAssert0null59575_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Writer o_testParentReplace_literalMutationString53139__7 = m.execute(null, new Object() {
                    String replace = "Template 0L0H2nsj;&E$fjp[A;7} not found";
                });
                String o_testParentReplace_literalMutationString53139__12 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53139_literalMutationString53430 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53139_literalMutationString53430_failAssert0null59575 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53134_failAssert0_literalMutationString53632_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Big@J:k<b6:.T");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53134 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53134_failAssert0_literalMutationString53632 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Big@J:k<b6:.T not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53132_failAssert0_add54000_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53132 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53132_failAssert0_add54000 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplacenull53156_failAssert0_literalMutationString53477_failAssert0_literalMutationString56332_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("S!{Y&{w(AR:d");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {
                        String replace = "false";
                    });
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testParentReplacenull53156 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testParentReplacenull53156_failAssert0_literalMutationString53477 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testParentReplacenull53156_failAssert0_literalMutationString53477_failAssert0_literalMutationString56332 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template S!{Y&{w(AR:d not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53133_failAssert0null54097_failAssert0_add58763_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("replace[html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String replace = "false";
                    });
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testParentReplace_literalMutationString53133 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParentReplace_literalMutationString53133_failAssert0null54097 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53133_failAssert0null54097_failAssert0_add58763 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template replace[html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53133_failAssert0_add53975_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("replace[html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53133 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53133_failAssert0_add53975 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template replace[html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53131() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testParentReplace_literalMutationString53131__7 = m.execute(sw, new Object() {
            String replace = "false";
        });
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testParentReplace_literalMutationString53131__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testParentReplace_literalMutationString53131__7)).toString());
        String o_testParentReplace_literalMutationString53131__12 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
        Assert.assertEquals("true is true", o_testParentReplace_literalMutationString53131__12);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testParentReplace_literalMutationString53131__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testParentReplace_literalMutationString53131__7)).toString());
        Assert.assertEquals("true is true", o_testParentReplace_literalMutationString53131__12);
    }

    @Test(timeout = 10000)
    public void testParentReplace_add53150_remove54017_literalMutationString55179_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Writer o_testParentReplace_add53150__7 = m.execute(sw, new Object() {
                String replace = "false";
            });
            Writer o_testParentReplace_add53150__12 = m.execute(sw, new Object() {
                String replace = "false";
            });
            String o_testParentReplace_add53150__17 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            org.junit.Assert.fail("testParentReplace_add53150_remove54017_literalMutationString55179 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53134_failAssert0_literalMutationString53642_failAssert0_literalMutationString56301_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Bi@J:k<b6:.T");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String replace = "Template 0L0H2nsj;&E$fjp[A;7} not found";
                    });
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testParentReplace_literalMutationString53134 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParentReplace_literalMutationString53134_failAssert0_literalMutationString53642 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53134_failAssert0_literalMutationString53642_failAssert0_literalMutationString56301 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Bi@J:k<b6:.T not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53139_literalMutationString53430_failAssert0_add58907_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "Template 0L0H2nsj;&E$fjp[A;7} not found";
                });
                Writer o_testParentReplace_literalMutationString53139__7 = m.execute(sw, new Object() {
                    String replace = "Template 0L0H2nsj;&E$fjp[A;7} not found";
                });
                String o_testParentReplace_literalMutationString53139__12 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53139_literalMutationString53430 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53139_literalMutationString53430_failAssert0_add58907 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53147_failAssert0_literalMutationString53526_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "relace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53147 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53147_failAssert0_literalMutationString53526 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53134_failAssert0_literalMutationString53642_failAssert0_add58594_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Bi@J:k<b6:.T");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String replace = "false";
                    });
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testParentReplace_literalMutationString53134 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParentReplace_literalMutationString53134_failAssert0_literalMutationString53642 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53134_failAssert0_literalMutationString53642_failAssert0_add58594 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Bi@J:k<b6:.T not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53132_failAssert0null54112_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53132 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53132_failAssert0null54112 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53136_failAssert0_literalMutationString53511_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("O{lkE$cC)p1");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53136 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53136_failAssert0_literalMutationString53511 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template O{lkE$cC)p1 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53148_failAssert0_literalMutationString53599_failAssert0null59608_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {
                        String replace = "false";
                    });
                    TestUtil.getContents(AmplExtensionTest.root, "_IvKUH4g4L^");
                    sw.toString();
                    org.junit.Assert.fail("testParentReplace_literalMutationString53148 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testParentReplace_literalMutationString53148_failAssert0_literalMutationString53599 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53148_failAssert0_literalMutationString53599_failAssert0null59608 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53139_literalMutationString53430_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Writer o_testParentReplace_literalMutationString53139__7 = m.execute(sw, new Object() {
                String replace = "Template 0L0H2nsj;&E$fjp[A;7} not found";
            });
            String o_testParentReplace_literalMutationString53139__12 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString53139_literalMutationString53430 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53133_failAssert0null54097_failAssert0null59496_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("replace[html");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {
                        String replace = "false";
                    });
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testParentReplace_literalMutationString53133 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParentReplace_literalMutationString53133_failAssert0null54097 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53133_failAssert0null54097_failAssert0null59496 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template replace[html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53141_literalMutationString53449_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("=/ZN{En}4fs*");
            StringWriter sw = new StringWriter();
            Writer o_testParentReplace_literalMutationString53141__7 = m.execute(sw, new Object() {
                String replace = "fbalse";
            });
            String o_testParentReplace_literalMutationString53141__12 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString53141_literalMutationString53449 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template =/ZN{En}4fs* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53141_literalMutationString53449_failAssert0_add58981_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("=/ZN{En}4fs*");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "fbalse";
                });
                Writer o_testParentReplace_literalMutationString53141__7 = m.execute(sw, new Object() {
                    String replace = "fbalse";
                });
                String o_testParentReplace_literalMutationString53141__12 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53141_literalMutationString53449 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53141_literalMutationString53449_failAssert0_add58981 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template =/ZN{En}4fs* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53141_literalMutationString53449_failAssert0_literalMutationString57503_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("=/ZN{En}4fs*");
                StringWriter sw = new StringWriter();
                Writer o_testParentReplace_literalMutationString53141__7 = m.execute(sw, new Object() {
                    String replace = "Xh&`!I";
                });
                String o_testParentReplace_literalMutationString53141__12 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53141_literalMutationString53449 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53141_literalMutationString53449_failAssert0_literalMutationString57503 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template =/ZN{En}4fs* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53148_failAssert0_literalMutationString53599_failAssert0_add58968_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String replace = "false";
                    });
                    TestUtil.getContents(AmplExtensionTest.root, "_IvKUH4g4L^");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testParentReplace_literalMutationString53148 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testParentReplace_literalMutationString53148_failAssert0_literalMutationString53599 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53148_failAssert0_literalMutationString53599_failAssert0_add58968 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53134_failAssert0null54108_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Bi@J:k<b6:.T");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53134 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53134_failAssert0null54108 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Bi@J:k<b6:.T not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53133_failAssert0_literalMutationString53569_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("replace[html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "Walse";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53133 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53133_failAssert0_literalMutationString53569 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template replace[html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53134_failAssert0_literalMutationString53642_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Bi@J:k<b6:.T");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53134 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53134_failAssert0_literalMutationString53642 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Bi@J:k<b6:.T not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53146_failAssert0_literalMutationString53582_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("=cJ:L1th$Md(");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "repvace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53146 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53146_failAssert0_literalMutationString53582 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template =cJ:L1th$Md( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53131_literalMutationString53346_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Writer o_testParentReplace_literalMutationString53131__7 = m.execute(sw, new Object() {
                String replace = "false";
            });
            String o_testParentReplace_literalMutationString53131__12 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString53131_literalMutationString53346 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53134_failAssert0_add53994_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Bi@J:k<b6:.T");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53134 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53134_failAssert0_add53994 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Bi@J:k<b6:.T not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53133_failAssert0null54097_failAssert0_literalMutationString56766_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("relace[html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String replace = "false";
                    });
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testParentReplace_literalMutationString53133 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParentReplace_literalMutationString53133_failAssert0null54097 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53133_failAssert0null54097_failAssert0_literalMutationString56766 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template relace[html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53139_literalMutationString53430_failAssert0null59576_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Writer o_testParentReplace_literalMutationString53139__7 = m.execute(sw, new Object() {
                    String replace = "Template 0L0H2nsj;&E$fjp[A;7} not found";
                });
                String o_testParentReplace_literalMutationString53139__12 = TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53139_literalMutationString53430 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53139_literalMutationString53430_failAssert0null59576 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53134_failAssert0null54109_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Bi@J:k<b6:.T");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53134 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53134_failAssert0null54109 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Bi@J:k<b6:.T not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53140_remove54031_literalMutationString54851_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("vxZp/wkwY#E#");
            StringWriter sw = new StringWriter();
            Writer o_testParentReplace_literalMutationString53140__7 = m.execute(sw, new Object() {
                String replace = "P8-41";
            });
            String o_testParentReplace_literalMutationString53140__12 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString53140_remove54031_literalMutationString54851 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vxZp/wkwY#E# not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53142null54067_failAssert0_literalMutationString55838_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("V6I7Ek8!`}ed");
                StringWriter sw = new StringWriter();
                Writer o_testParentReplace_literalMutationString53142__7 = m.execute(sw, new Object() {
                    String replace = "flse";
                });
                String o_testParentReplace_literalMutationString53142__12 = TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53142null54067 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53142null54067_failAssert0_literalMutationString55838 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template V6I7Ek8!`}ed not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53144_failAssert0_literalMutationString53548_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("[mR4EH|!wr!2");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53144 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53144_failAssert0_literalMutationString53548 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [mR4EH|!wr!2 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53145_failAssert0_literalMutationString53683_failAssert0_literalMutationString57291_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String replace = "false";
                    });
                    TestUtil.getContents(AmplExtensionTest.root, "regpglace.txt");
                    sw.toString();
                    org.junit.Assert.fail("testParentReplace_literalMutationString53145 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testParentReplace_literalMutationString53145_failAssert0_literalMutationString53683 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53145_failAssert0_literalMutationString53683_failAssert0_literalMutationString57291 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53141_literalMutationString53449_failAssert0null59618_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("=/ZN{En}4fs*");
                StringWriter sw = new StringWriter();
                Writer o_testParentReplace_literalMutationString53141__7 = m.execute(sw, new Object() {
                    String replace = "fbalse";
                });
                String o_testParentReplace_literalMutationString53141__12 = TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53141_literalMutationString53449 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53141_literalMutationString53449_failAssert0null59618 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template =/ZN{En}4fs* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53148_failAssert0_literalMutationString53599_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "_IvKUH4g4L^");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53148 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53148_failAssert0_literalMutationString53599 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53133_failAssert0null54097_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("replace[html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53133 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53133_failAssert0null54097 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template replace[html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_add53151_remove54013_literalMutationString54669_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Writer o_testParentReplace_add53151__7 = m.execute(sw, new Object() {
                String replace = "false";
            });
            String o_testParentReplace_add53151__12 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            String o_testParentReplace_add53151__13 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_add53151_remove54013_literalMutationString54669 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53133_failAssert0_add53976_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("replace[html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53133 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53133_failAssert0_add53976 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template replace[html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53134_failAssert0_literalMutationString53642_failAssert0null59421_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Bi@J:k<b6:.T");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String replace = "false";
                    });
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testParentReplace_literalMutationString53134 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParentReplace_literalMutationString53134_failAssert0_literalMutationString53642 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53134_failAssert0_literalMutationString53642_failAssert0null59421 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Bi@J:k<b6:.T not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53133_failAssert0_literalMutationString53574_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("replace[html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53133 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53133_failAssert0_literalMutationString53574 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template replace[html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53139_literalMutationString53430_failAssert0_add58909_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Writer o_testParentReplace_literalMutationString53139__7 = m.execute(sw, new Object() {
                    String replace = "Template 0L0H2nsj;&E$fjp[A;7} not found";
                });
                String o_testParentReplace_literalMutationString53139__12 = TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53139_literalMutationString53430 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53139_literalMutationString53430_failAssert0_add58909 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString53132_failAssert0_add53998_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString53132 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString53132_failAssert0_add53998 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19206_failAssert0_add23434_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("TRT<x`Rzmj@[]?#Tgx&");
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
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_add23434 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template TRT<x`Rzmj@[]?#Tgx& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19215_failAssert0_literalMutationString21089_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("subblkockchild2.html");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19215 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19215_failAssert0_literalMutationString21089 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19214_failAssert0null24124_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214_failAssert0null24124 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19202_failAssert0null24079_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {});
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202_failAssert0null24079 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_add19243_remove23601_literalMutationString26822_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Writer o_testSubBlockCaching_add19243__7 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19243__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("subblockchild2.html");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_add19243__18 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19243__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            String o_testSubBlockCaching_add19243__24 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_add19243__30 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19243__35 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            String o_testSubBlockCaching_add19243__37 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_add19243_remove23601_literalMutationString26822 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19202_failAssert0null24083_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202_failAssert0null24083 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19236_failAssert0_literalMutationString21503_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
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
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockhild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19236 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19236_failAssert0_literalMutationString21503 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19206_failAssert0_add23434_failAssert0_literalMutationString29903_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("TRT<x`Rzmj@[]?#Tgx&");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblokchild1.txt");
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
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_add23434 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_add23434_failAssert0_literalMutationString29903 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template TRT<x`Rzmj@[]?#Tgx& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_add19247_literalMutationString19553_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("L@e6&{o4LI/jMgGn.qr");
            StringWriter sw = new StringWriter();
            Writer o_testSubBlockCaching_add19247__7 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19247__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("subblockchild2.html");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_add19247__18 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19247__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_add19247__29 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19247__34 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            String o_testSubBlockCaching_add19247__35 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            String o_testSubBlockCaching_add19247__37 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_add19247_literalMutationString19553 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19214_failAssert0_add23254_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214_failAssert0_add23254 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19214_failAssert0null24124_failAssert0_add34898_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subblockchild1.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214_failAssert0null24124 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214_failAssert0null24124_failAssert0_add34898 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19206_failAssert0_add23434_failAssert0_add35517_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("TRT<x`Rzmj@[]?#Tgx&");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
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
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_add23434 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_add23434_failAssert0_add35517 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template TRT<x`Rzmj@[]?#Tgx& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_add19244_remove23615_literalMutationString25847_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubBlockCaching_add19244__7 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19244__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("-GaN(oCaUJ!0zv:0[md");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_add19244__18 = m.execute(sw, new Object() {});
            sw.toString();
            String o_testSubBlockCaching_add19244__24 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_add19244__30 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19244__35 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            String o_testSubBlockCaching_add19244__37 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_add19244_remove23615_literalMutationString25847 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -GaN(oCaUJ!0zv:0[md not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19206_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("TRT<x`Rzmj@[]?#Tgx&");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template TRT<x`Rzmj@[]?#Tgx& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19202_failAssert0_add23190_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202_failAssert0_add23190 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_remove19250null23887_failAssert0_literalMutationString28111_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                Writer o_testSubBlockCaching_remove19250__7 = m.execute(sw, new Object() {});
                String o_testSubBlockCaching_remove19250__11 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                sw.toString();
                sw.toString();
                sw.toString();
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                Writer o_testSubBlockCaching_remove19250__17 = m.execute(sw, new Object() {});
                String o_testSubBlockCaching_remove19250__22 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                m = c.compile(null);
                sw = new StringWriter();
                Writer o_testSubBlockCaching_remove19250__28 = m.execute(sw, new Object() {});
                String o_testSubBlockCaching_remove19250__33 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                String o_testSubBlockCaching_remove19250__35 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                org.junit.Assert.fail("testSubBlockCaching_remove19250null23887 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSubBlockCaching_remove19250null23887_failAssert0_literalMutationString28111 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19206_failAssert0_add23434_failAssert0_add35526_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("TRT<x`Rzmj@[]?#Tgx&");
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
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_add23434 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_add23434_failAssert0_add35526 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template TRT<x`Rzmj@[]?#Tgx& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19206_failAssert0_add23434_failAssert0_literalMutationString29909_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("TRT<x`Rzmj@[]?#Tgx&");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblockchild2.[html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_add23434 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_add23434_failAssert0_literalMutationString29909 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template TRT<x`Rzmj@[]?#Tgx& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19234_failAssert0_literalMutationString20916_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("{ubblockchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchil^d1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19234 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19234_failAssert0_literalMutationString20916 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {ubblockchild2.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19206_failAssert0null24245_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("TRT<x`Rzmj@[]?#Tgx&");
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
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0null24245 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template TRT<x`Rzmj@[]?#Tgx& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19226_failAssert0_add23565_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
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
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19226 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19226_failAssert0_add23565 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19212_failAssert0_literalMutationString21628_failAssert0_literalMutationString31365_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("<k7g3+5yWPDF-y&?(|<");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.tt");
                    sw.toString();
                    m = c.compile("subblockchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19212 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19212_failAssert0_literalMutationString21628 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19212_failAssert0_literalMutationString21628_failAssert0_literalMutationString31365 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template <k7g3+5yWPDF-y&?(|< not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_add19238_literalMutationString19440_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubBlockCaching_add19238__7 = m.execute(sw, new Object() {});
            Writer o_testSubBlockCaching_add19238__12 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19238__17 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("subblockchild2.html");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_add19238__23 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19238__28 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("3]XMic6[rmOCc!2<tHc");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_add19238__34 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19238__39 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            String o_testSubBlockCaching_add19238__41 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_add19238_literalMutationString19440 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 3]XMic6[rmOCc!2<tHc not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19226_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19226 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19202_failAssert0_literalMutationString20858_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("HK:F5v#}QFc.nYFP@*Zf%YkY,-kLog_ 7!2ywQy");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202_failAssert0_literalMutationString20858 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template HK:F5v#}QFc.nYFP@*Zf%YkY,-kLog_ 7!2ywQy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_remove19250_literalMutationString20072_failAssert0_literalMutationString31179_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                Writer o_testSubBlockCaching_remove19250__7 = m.execute(sw, new Object() {});
                String o_testSubBlockCaching_remove19250__11 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("%R?CU(`Xl[#,a@=YT?A");
                sw = new StringWriter();
                Writer o_testSubBlockCaching_remove19250__17 = m.execute(sw, new Object() {});
                String o_testSubBlockCaching_remove19250__22 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                Writer o_testSubBlockCaching_remove19250__28 = m.execute(sw, new Object() {});
                String o_testSubBlockCaching_remove19250__33 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                String o_testSubBlockCaching_remove19250__35 = TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_remove19250_literalMutationString20072 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_remove19250_literalMutationString20072_failAssert0_literalMutationString31179 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template %R?CU(`Xl[#,a@=YT?A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19202_failAssert0_add23202_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202_failAssert0_add23202 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_add19243_remove23605_literalMutationString25814_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubBlockCaching_add19243__7 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19243__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            sw.toString();
            m = c.compile("subblockchild2.html");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_add19243__18 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19243__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            String o_testSubBlockCaching_add19243__24 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            m = c.compile("nijt|vo](eP]?ptg BS");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_add19243__30 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19243__35 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            String o_testSubBlockCaching_add19243__37 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_add19243_remove23605_literalMutationString25814 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nijt|vo](eP]?ptg BS not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_add19244_literalMutationString19622_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubBlockCaching_add19244__7 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19244__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("subblockchild2.html");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_add19244__18 = m.execute(sw, new Object() {});
            sw.toString();
            String o_testSubBlockCaching_add19244__24 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_add19244__30 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_add19244__35 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            String o_testSubBlockCaching_add19244__37 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_add19244_literalMutationString19622 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19223_failAssert0_add23505_failAssert0_literalMutationString32358_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("x+e2xr$q7|7arsCYpa{");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblockchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.ntxt");
                    sw.toString();
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19223 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19223_failAssert0_add23505 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19223_failAssert0_add23505_failAssert0_literalMutationString32358 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template x+e2xr$q7|7arsCYpa{ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19206_failAssert0_add23434_failAssert0null38172_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("TRT<x`Rzmj@[]?#Tgx&");
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
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_add23434 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_add23434_failAssert0null38172 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template TRT<x`Rzmj@[]?#Tgx& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19229_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            m = c.compile("h;2,3C=F)]E!{jO8V8i");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19229 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template h;2,3C=F)]E!{jO8V8i not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19227_failAssert0_literalMutationString20733_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("subblockchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchld1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19227 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19227_failAssert0_literalMutationString20733 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19206_failAssert0_literalMutationString21552_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("TRT<x`Rzmj@[]?#Tgx&");
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
                TestUtil.getContents(AmplExtensionTest.root, "E{`WWY&s=o?1%&Xv!%");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_literalMutationString21552 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template TRT<x`Rzmj@[]?#Tgx& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19214_failAssert0null24124_failAssert0null37764_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subblockchild1.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile(null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214_failAssert0null24124 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214_failAssert0null24124_failAssert0null37764 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19206_failAssert0_add23434_failAssert0null38169_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("TRT<x`Rzmj@[]?#Tgx&");
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
                    m = c.compile(null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_add23434 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19206_failAssert0_add23434_failAssert0null38169 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template TRT<x`Rzmj@[]?#Tgx& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19203_failAssert0_literalMutationString20817_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("/iiM>rl%P]wA(=`c XHp");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19203 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19203_failAssert0_literalMutationString20817 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /iiM>rl%P]wA(=`c XHp not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19214_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19217_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("GW:s!xAsxj[Cv54iD#n");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19217 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template GW:s!xAsxj[Cv54iD#n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19214_failAssert0_literalMutationString21048_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchil1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214_failAssert0_literalMutationString21048 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19215_failAssert0_literalMutationString21089_failAssert0null38598_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblkockchild2.html");
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
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19215 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19215_failAssert0_literalMutationString21089 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19215_failAssert0_literalMutationString21089_failAssert0null38598 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19202_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19202_failAssert0_add23202_failAssert0_literalMutationString28855_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.xt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202_failAssert0_add23202 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202_failAssert0_add23202_failAssert0_literalMutationString28855 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19214_failAssert0null24124_failAssert0_literalMutationString28281_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subblockchild1.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "sutbblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214_failAssert0null24124 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19214_failAssert0null24124_failAssert0_literalMutationString28281 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19202_failAssert0_literalMutationString20859_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(" does not exist");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202_failAssert0_literalMutationString20859 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19213() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("subblockchild1.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString19213__7 = m.execute(sw, new Object() {});
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19213__7)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19213__7)).toString());
        String o_testSubBlockCaching_literalMutationString19213__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19213__12);
        sw.toString();
        sw.toString();
        sw.toString();
        sw.toString();
        m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString19213__18 = m.execute(sw, new Object() {});
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19213__18)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19213__18)).toString());
        String o_testSubBlockCaching_literalMutationString19213__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString19213__23);
        m = c.compile("subblockchild1.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString19213__29 = m.execute(sw, new Object() {});
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19213__29)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19213__29)).toString());
        String o_testSubBlockCaching_literalMutationString19213__34 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19213__34);
        String o_testSubBlockCaching_literalMutationString19213__36 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19213__36);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19213__7)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19213__7)).toString());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19213__12);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19213__18)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19213__18)).toString());
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString19213__23);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19213__29)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19213__29)).toString());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19213__34);
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19213__36);
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19201() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString19201__7 = m.execute(sw, new Object() {});
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19201__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19201__7)).toString());
        String o_testSubBlockCaching_literalMutationString19201__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19201__12);
        sw.toString();
        sw.toString();
        sw.toString();
        sw.toString();
        m = c.compile("subblockchild2.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild2.html", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString19201__18 = m.execute(sw, new Object() {});
        Assert.assertEquals("second\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19201__18)).getBuffer())).toString());
        Assert.assertEquals("second\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19201__18)).toString());
        String o_testSubBlockCaching_literalMutationString19201__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString19201__23);
        m = c.compile("subblockchild1.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString19201__29 = m.execute(sw, new Object() {});
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19201__29)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19201__29)).toString());
        String o_testSubBlockCaching_literalMutationString19201__34 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19201__34);
        String o_testSubBlockCaching_literalMutationString19201__36 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19201__36);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19201__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19201__7)).toString());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19201__12);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("second\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19201__18)).getBuffer())).toString());
        Assert.assertEquals("second\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19201__18)).toString());
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString19201__23);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19201__29)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19201__29)).toString());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19201__34);
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19201__36);
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19202_failAssert0_add23202_failAssert0_add35132_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202_failAssert0_add23202 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202_failAssert0_add23202_failAssert0_add35132 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19230_failAssert0_add23580_failAssert0_literalMutationString29302_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                    m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19230 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19230_failAssert0_add23580 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19230_failAssert0_add23580_failAssert0_literalMutationString29302 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19215_failAssert0_literalMutationString21089_failAssert0_add36176_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblkockchild2.html");
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
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19215 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19215_failAssert0_literalMutationString21089 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19215_failAssert0_literalMutationString21089_failAssert0_add36176 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19202_failAssert0_add23202_failAssert0null37910_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202_failAssert0_add23202 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString19202_failAssert0_add23202_failAssert0null37910 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString19225() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("subblockchild1.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString19225__7 = m.execute(sw, new Object() {});
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19225__7)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19225__7)).toString());
        String o_testSubBlockCaching_literalMutationString19225__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19225__12);
        sw.toString();
        sw.toString();
        sw.toString();
        sw.toString();
        m = c.compile("subblockchild2.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild2.html", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString19225__18 = m.execute(sw, new Object() {});
        Assert.assertEquals("second\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19225__18)).getBuffer())).toString());
        Assert.assertEquals("second\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19225__18)).toString());
        String o_testSubBlockCaching_literalMutationString19225__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString19225__23);
        m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString19225__29 = m.execute(sw, new Object() {});
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19225__29)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19225__29)).toString());
        String o_testSubBlockCaching_literalMutationString19225__34 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19225__34);
        String o_testSubBlockCaching_literalMutationString19225__36 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19225__36);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19225__7)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19225__7)).toString());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19225__12);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("second\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19225__18)).getBuffer())).toString());
        Assert.assertEquals("second\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19225__18)).toString());
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString19225__23);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString19225__29)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString19225__29)).toString());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19225__34);
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString19225__36);
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39305_literalMutationString40294_failAssert0_add51051_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSubSub_literalMutationString39305__9 = scope.put("name", "Sam");
                Object o_testSubSub_literalMutationString39305__10 = scope.put("", "asdlkfj");
                Writer o_testSubSub_literalMutationString39305__11 = m.execute(sw, scope);
                String o_testSubSub_literalMutationString39305__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString39305_literalMutationString40294 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39305_literalMutationString40294_failAssert0_add51051 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39289_failAssert0_literalMutationString41033_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("na`me", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString39289 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39289_failAssert0_literalMutationString41033 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubnull39335_literalMutationString39485_failAssert0_add51025_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("-(Q]AF! 56u");
                Mustache m = c.compile("-(Q]AF! 56u");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSubSubnull39335__9 = scope.put("name", "Sam");
                Object o_testSubSubnull39335__10 = scope.put(null, "asdlkfj");
                Writer o_testSubSubnull39335__11 = m.execute(sw, scope);
                String o_testSubSubnull39335__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubnull39335_literalMutationString39485 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubnull39335_literalMutationString39485_failAssert0_add51025 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -(Q]AF! 56u not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39289_failAssert0_add42124_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString39289 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39289_failAssert0_add42124 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39289_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSub_literalMutationString39289 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubnull39335null42218_failAssert0_literalMutationString46917_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSubSubnull39335__9 = scope.put("name", "Sam");
                Object o_testSubSubnull39335__10 = scope.put(null, "asdlkfj");
                Writer o_testSubSubnull39335__11 = m.execute(null, scope);
                String o_testSubSubnull39335__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubnull39335null42218 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSubSubnull39335null42218_failAssert0_literalMutationString46917 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubnull39335_literalMutationString39485_failAssert0_literalMutationString48346_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-(Q]AF! 56u");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSubSubnull39335__9 = scope.put("name", "Sam");
                Object o_testSubSubnull39335__10 = scope.put(null, "aslkfj");
                Writer o_testSubSubnull39335__11 = m.execute(sw, scope);
                String o_testSubSubnull39335__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubnull39335_literalMutationString39485 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubnull39335_literalMutationString39485_failAssert0_literalMutationString48346 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -(Q]AF! 56u not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39287_failAssert0_literalMutationString40940_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("F+`NAi!$YI&");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("kh4LrjrN", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString39287 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39287_failAssert0_literalMutationString40940 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template F+`NAi!$YI& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39289_failAssert0_add42124_failAssert0_literalMutationString48110_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "kam");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testSubSub_literalMutationString39289 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSub_literalMutationString39289_failAssert0_add42124 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39289_failAssert0_add42124_failAssert0_literalMutationString48110 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39294_literalMutationString40714_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubSub_literalMutationString39294__9 = scope.put("Template 0L0H2nsj;&E$fjp[A;7} not found", "Sam");
            Object o_testSubSub_literalMutationString39294__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubSub_literalMutationString39294__11 = m.execute(sw, scope);
            String o_testSubSub_literalMutationString39294__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSub_literalMutationString39294_literalMutationString40714 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39305_literalMutationString40294_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubSub_literalMutationString39305__9 = scope.put("name", "Sam");
            Object o_testSubSub_literalMutationString39305__10 = scope.put("", "asdlkfj");
            Writer o_testSubSub_literalMutationString39305__11 = m.execute(sw, scope);
            String o_testSubSub_literalMutationString39305__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSub_literalMutationString39305_literalMutationString40294 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39294_literalMutationString40714_failAssert0_literalMutationString49029_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSubSub_literalMutationString39294__9 = scope.put("Template 0L0H2nsj;&E$fjp[A;7} not found", "Template 0L0H2nsj;&E$fjp[A;7} not found");
                Object o_testSubSub_literalMutationString39294__10 = scope.put("randomid", "asdlkfj");
                Writer o_testSubSub_literalMutationString39294__11 = m.execute(sw, scope);
                String o_testSubSub_literalMutationString39294__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString39294_literalMutationString40714 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39294_literalMutationString40714_failAssert0_literalMutationString49029 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39316_literalMutationString40049_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubSub_literalMutationString39316__9 = scope.put("name", "Sam");
            Object o_testSubSub_literalMutationString39316__10 = scope.put("randomid", "asdlWkfj");
            Writer o_testSubSub_literalMutationString39316__11 = m.execute(sw, scope);
            String o_testSubSub_literalMutationString39316__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSub_literalMutationString39316_literalMutationString40049 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39289_failAssert0_add42124_failAssert0_add50984_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "Sam");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                    sw.toString();
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testSubSub_literalMutationString39289 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSub_literalMutationString39289_failAssert0_add42124 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39289_failAssert0_add42124_failAssert0_add50984 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39294_literalMutationString40714_failAssert0_add51174_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSubSub_literalMutationString39294__9 = scope.put("Template 0L0H2nsj;&E$fjp[A;7} not found", "Sam");
                Object o_testSubSub_literalMutationString39294__10 = scope.put("randomid", "asdlkfj");
                Writer o_testSubSub_literalMutationString39294__11 = m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                String o_testSubSub_literalMutationString39294__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString39294_literalMutationString40714 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39294_literalMutationString40714_failAssert0_add51174 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubnull39335_literalMutationString39485_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("-(Q]AF! 56u");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubSubnull39335__9 = scope.put("name", "Sam");
            Object o_testSubSubnull39335__10 = scope.put(null, "asdlkfj");
            Writer o_testSubSubnull39335__11 = m.execute(sw, scope);
            String o_testSubSubnull39335__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubnull39335_literalMutationString39485 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -(Q]AF! 56u not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39289_failAssert0_add42124_failAssert0null52426_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "Sam");
                    scope.put("randomid", null);
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testSubSub_literalMutationString39289 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSub_literalMutationString39289_failAssert0_add42124 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39289_failAssert0_add42124_failAssert0null52426 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39294_literalMutationString40714_failAssert0null52633_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSubSub_literalMutationString39294__9 = scope.put(null, "Sam");
                Object o_testSubSub_literalMutationString39294__10 = scope.put("randomid", "asdlkfj");
                Writer o_testSubSub_literalMutationString39294__11 = m.execute(sw, scope);
                String o_testSubSub_literalMutationString39294__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString39294_literalMutationString40714 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39294_literalMutationString40714_failAssert0null52633 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39288() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Map scope = new HashMap();
        Object o_testSubSub_literalMutationString39288__9 = scope.put("name", "Sam");
        Assert.assertNull(o_testSubSub_literalMutationString39288__9);
        Object o_testSubSub_literalMutationString39288__10 = scope.put("randomid", "asdlkfj");
        Assert.assertNull(o_testSubSub_literalMutationString39288__10);
        Writer o_testSubSub_literalMutationString39288__11 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubSub_literalMutationString39288__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubSub_literalMutationString39288__11)).toString());
        String o_testSubSub_literalMutationString39288__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
        Assert.assertEquals("<html>\n<head>\n<title>Subtitle</title>\n<script src=\"jquery.min.js\"></script>\n</head>\n<body>\n<div class=\"body\">\n<div id=\"asdlkfj\">\nHello, world!\n</div>\n</div>\n<div class=\"footer\">\n</div>\n</body>\n</html>", o_testSubSub_literalMutationString39288__12);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertNull(o_testSubSub_literalMutationString39288__9);
        Assert.assertNull(o_testSubSub_literalMutationString39288__10);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubSub_literalMutationString39288__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubSub_literalMutationString39288__11)).toString());
        Assert.assertEquals("<html>\n<head>\n<title>Subtitle</title>\n<script src=\"jquery.min.js\"></script>\n</head>\n<body>\n<div class=\"body\">\n<div id=\"asdlkfj\">\nHello, world!\n</div>\n</div>\n<div class=\"footer\">\n</div>\n</body>\n</html>", o_testSubSub_literalMutationString39288__12);
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39318_failAssert0null42547_failAssert0_literalMutationString47184_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", null);
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw.toString();
                    org.junit.Assert.fail("testSubSub_literalMutationString39318 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSubSub_literalMutationString39318_failAssert0null42547 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39318_failAssert0null42547_failAssert0_literalMutationString47184 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubnull39335_literalMutationString39485_failAssert0null52475_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-(Q]AF! 56u");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSubSubnull39335__9 = scope.put("name", null);
                Object o_testSubSubnull39335__10 = scope.put(null, "asdlkfj");
                Writer o_testSubSubnull39335__11 = m.execute(sw, scope);
                String o_testSubSubnull39335__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubnull39335_literalMutationString39485 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubnull39335_literalMutationString39485_failAssert0null52475 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -(Q]AF! 56u not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39305_literalMutationString40294_failAssert0null52499_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSubSub_literalMutationString39305__9 = scope.put("name", "Sam");
                Object o_testSubSub_literalMutationString39305__10 = scope.put("", null);
                Writer o_testSubSub_literalMutationString39305__11 = m.execute(sw, scope);
                String o_testSubSub_literalMutationString39305__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString39305_literalMutationString40294 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39305_literalMutationString40294_failAssert0null52499 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39305_literalMutationString40294_failAssert0_literalMutationString48423_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("w.$,3<:<vUK^&](#rJEt5w}%2@LC!.(;jQ]hk4K");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testSubSub_literalMutationString39305__9 = scope.put("name", "Sam");
                Object o_testSubSub_literalMutationString39305__10 = scope.put("", "asdlkfj");
                Writer o_testSubSub_literalMutationString39305__11 = m.execute(sw, scope);
                String o_testSubSub_literalMutationString39305__12 = TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString39305_literalMutationString40294 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39305_literalMutationString40294_failAssert0_literalMutationString48423 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template w.$,3<:<vUK^&](#rJEt5w}%2@LC!.(;jQ]hk4K not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39287_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("F+`NAi!$YI&");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSub_literalMutationString39287 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template F+`NAi!$YI& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39287_failAssert0_add42104_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("F+`NAi!$YI&");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString39287 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39287_failAssert0_add42104 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template F+`NAi!$YI& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString39317_failAssert0_literalMutationString40989_failAssert0_literalMutationString48561_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("name", "Sam");
                    scope.put("randomid", "asdlkfj");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testSubSub_literalMutationString39317 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSubSub_literalMutationString39317_failAssert0_literalMutationString40989 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString39317_failAssert0_literalMutationString40989_failAssert0_literalMutationString48561 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116134() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Map scope = new HashMap();
        Object o_testClientMethod_literalMutationString116134__9 = scope.put("reply", "TestReply");
        Assert.assertNull(o_testClientMethod_literalMutationString116134__9);
        Object o_testClientMethod_literalMutationString116134__10 = scope.put("commands", Arrays.asList("a", "b"));
        Assert.assertNull(o_testClientMethod_literalMutationString116134__10);
        Writer o_testClientMethod_literalMutationString116134__12 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testClientMethod_literalMutationString116134__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testClientMethod_literalMutationString116134__12)).toString());
        String o_testClientMethod_literalMutationString116134__13 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
        Assert.assertEquals("1:TestReply\n2:TestReply execute\n3:TestReply execute\n1:TestReply\n2:TestReply execute\n3:TestReply execute\n1:New reply TestReply\n2:New reply TestReply pipeline\n3:New reply TestReply pipeline\n1:New reply TestReply\n2:New reply TestReply pipeline\n3:New reply TestReply pipeline\n", o_testClientMethod_literalMutationString116134__13);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertNull(o_testClientMethod_literalMutationString116134__9);
        Assert.assertNull(o_testClientMethod_literalMutationString116134__10);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testClientMethod_literalMutationString116134__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testClientMethod_literalMutationString116134__12)).toString());
        Assert.assertEquals("1:TestReply\n2:TestReply execute\n3:TestReply execute\n1:TestReply\n2:TestReply execute\n3:TestReply execute\n1:New reply TestReply\n2:New reply TestReply pipeline\n3:New reply TestReply pipeline\n1:New reply TestReply\n2:New reply TestReply pipeline\n3:New reply TestReply pipeline\n", o_testClientMethod_literalMutationString116134__13);
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116139_failAssert0_literalMutationString117817_failAssert0_literalMutationString125298_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("lNm5z-Oag[K");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("reply", "TestReply");
                    scope.put("commands", Arrays.asList("a", "k"));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                    sw.toString();
                    org.junit.Assert.fail("testClientMethod_literalMutationString116139 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testClientMethod_literalMutationString116139_failAssert0_literalMutationString117817 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString116139_failAssert0_literalMutationString117817_failAssert0_literalMutationString125298 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lNm5z-Oag[K not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116135_failAssert0null119412_failAssert0_add127611_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("reply", null);
                    scope.put("commands", Arrays.asList("a", "b"));
                    scope.put("commands", Arrays.asList("a", "b"));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                    sw.toString();
                    org.junit.Assert.fail("testClientMethod_literalMutationString116135 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testClientMethod_literalMutationString116135_failAssert0null119412 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString116135_failAssert0null119412_failAssert0_add127611 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethodnull116184_failAssert0_add118948_failAssert0_literalMutationString124340_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache m = c.compile("client.html");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("reply", "TestReply");
                    scope.put("commands", Arrays.asList("a", "b"));
                    m.execute(null, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                    sw.toString();
                    org.junit.Assert.fail("testClientMethodnull116184 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testClientMethodnull116184_failAssert0_add118948 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testClientMethodnull116184_failAssert0_add118948_failAssert0_literalMutationString124340 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116161_literalMutationString117134_literalMutationString122996_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testClientMethod_literalMutationString116161__9 = scope.put("reply", "TestReply");
            Object o_testClientMethod_literalMutationString116161__10 = scope.put("", Arrays.asList("a", ""));
            Writer o_testClientMethod_literalMutationString116161__12 = m.execute(sw, scope);
            String o_testClientMethod_literalMutationString116161__13 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString116161_literalMutationString117134_literalMutationString122996 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116137_failAssert0_literalMutationString117779_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("o]D+V;8,eCF");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("reply", "TestReply");
                scope.put("commands", Arrays.asList("a", ""));
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_literalMutationString116137 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString116137_failAssert0_literalMutationString117779 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template o]D+V;8,eCF not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116135_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("reply", "TestReply");
            scope.put("commands", Arrays.asList("a", "b"));
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString116135 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_remove116179_add118361_literalMutationString120504_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testClientMethod_remove116179__9 = scope.put("reply", "TestReply");
            List<String> o_testClientMethod_remove116179_add118361__12 = Arrays.asList("a", "b");
            Object o_testClientMethod_remove116179__10 = scope.put("commands", Arrays.asList("a", "b"));
            String o_testClientMethod_remove116179__12 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_remove116179_add118361_literalMutationString120504 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116135_failAssert0null119412_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("reply", null);
                scope.put("commands", Arrays.asList("a", "b"));
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_literalMutationString116135 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString116135_failAssert0null119412 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_add116173_literalMutationString116551_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("clie<t.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testClientMethod_add116173__9 = scope.put("reply", "TestReply");
            List<String> o_testClientMethod_add116173__10 = Arrays.asList("a", "b");
            Object o_testClientMethod_add116173__11 = scope.put("commands", Arrays.asList("a", "b"));
            Writer o_testClientMethod_add116173__13 = m.execute(sw, scope);
            String o_testClientMethod_add116173__14 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_add116173_literalMutationString116551 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template clie<t.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_remove116177_add118394_literalMutationString120561_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("E(#x &+6(EV");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testClientMethod_remove116177__9 = scope.put("commands", Arrays.asList("a", "b"));
            Writer o_testClientMethod_remove116177_add118394__13 = m.execute(sw, scope);
            Writer o_testClientMethod_remove116177__11 = m.execute(sw, scope);
            String o_testClientMethod_remove116177__12 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_remove116177_add118394_literalMutationString120561 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template E(#x &+6(EV not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116135_failAssert0null119412_failAssert0null128977_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put(null, null);
                    scope.put("commands", Arrays.asList("a", "b"));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                    sw.toString();
                    org.junit.Assert.fail("testClientMethod_literalMutationString116135 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testClientMethod_literalMutationString116135_failAssert0null119412 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString116135_failAssert0null119412_failAssert0null128977 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_remove116179null119153_literalMutationString120058_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("client.|html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testClientMethod_remove116179__9 = scope.put(null, "TestReply");
            Object o_testClientMethod_remove116179__10 = scope.put("commands", Arrays.asList("a", "b"));
            String o_testClientMethod_remove116179__12 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_remove116179null119153_literalMutationString120058 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template client.|html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116135_failAssert0null119412_failAssert0_literalMutationString123758_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("reply", null);
                    scope.put("commands", Arrays.asList("a", "b"));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw.toString();
                    org.junit.Assert.fail("testClientMethod_literalMutationString116135 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testClientMethod_literalMutationString116135_failAssert0null119412 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString116135_failAssert0null119412_failAssert0_literalMutationString123758 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116135_failAssert0_literalMutationString118024_failAssert0_add127955_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("reply", "TestReply");
                    scope.put("elf^q(xV", Arrays.asList("a", "b"));
                    scope.put("elf^q(xV", Arrays.asList("a", "b"));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                    sw.toString();
                    org.junit.Assert.fail("testClientMethod_literalMutationString116135 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testClientMethod_literalMutationString116135_failAssert0_literalMutationString118024 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString116135_failAssert0_literalMutationString118024_failAssert0_add127955 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116162_add118811_literalMutationString120629_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testClientMethod_literalMutationString116162_add118811__9 = scope.put("reply", "TestReply");
            Object o_testClientMethod_literalMutationString116162__9 = scope.put("reply", "TestReply");
            Object o_testClientMethod_literalMutationString116162__10 = scope.put("commands", Arrays.asList("a", "Template 0L0H2nsj;&E$fjp[A;7} not found"));
            Writer o_testClientMethod_literalMutationString116162__12 = m.execute(sw, scope);
            String o_testClientMethod_literalMutationString116162__13 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString116162_add118811_literalMutationString120629 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116160_remove119071_literalMutationString122039_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("6N1h5XA>OFs");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testClientMethod_literalMutationString116160__9 = scope.put("reply", "TestReply");
            Object o_testClientMethod_literalMutationString116160__10 = scope.put("commands", Arrays.asList("q", "b"));
            Writer o_testClientMethod_literalMutationString116160__12 = m.execute(sw, scope);
            String o_testClientMethod_literalMutationString116160__13 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            org.junit.Assert.fail("testClientMethod_literalMutationString116160_remove119071_literalMutationString122039 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 6N1h5XA>OFs not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116135_failAssert0_add119014_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("reply", "TestReply");
                scope.put("commands", Arrays.asList("a", "b"));
                scope.put("commands", Arrays.asList("a", "b"));
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_literalMutationString116135 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString116135_failAssert0_add119014 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116137_failAssert0_add118957_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("o]D+V;8,eCF");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("reply", "TestReply");
                scope.put("reply", "TestReply");
                scope.put("commands", Arrays.asList("a", "b"));
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_literalMutationString116137 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString116137_failAssert0_add118957 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template o]D+V;8,eCF not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116137_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("o]D+V;8,eCF");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("reply", "TestReply");
            scope.put("commands", Arrays.asList("a", "b"));
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString116137 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template o]D+V;8,eCF not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116134_add118420_add126238() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Map scope = new HashMap();
        Object o_testClientMethod_literalMutationString116134__9 = scope.put("reply", "TestReply");
        Object o_testClientMethod_literalMutationString116134__10 = scope.put("commands", Arrays.asList("a", "b"));
        Writer o_testClientMethod_literalMutationString116134_add118420_add126238__16 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testClientMethod_literalMutationString116134_add118420_add126238__16)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testClientMethod_literalMutationString116134_add118420_add126238__16)).toString());
        Writer o_testClientMethod_literalMutationString116134__12 = m.execute(sw, scope);
        String o_testClientMethod_literalMutationString116134__13 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
        Assert.assertEquals("1:TestReply\n2:TestReply execute\n3:TestReply execute\n1:TestReply\n2:TestReply execute\n3:TestReply execute\n1:New reply TestReply\n2:New reply TestReply pipeline\n3:New reply TestReply pipeline\n1:New reply TestReply\n2:New reply TestReply pipeline\n3:New reply TestReply pipeline\n", o_testClientMethod_literalMutationString116134__13);
        sw.toString();
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testClientMethod_literalMutationString116134_add118420_add126238__16)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testClientMethod_literalMutationString116134_add118420_add126238__16)).toString());
        Assert.assertEquals("1:TestReply\n2:TestReply execute\n3:TestReply execute\n1:TestReply\n2:TestReply execute\n3:TestReply execute\n1:New reply TestReply\n2:New reply TestReply pipeline\n3:New reply TestReply pipeline\n1:New reply TestReply\n2:New reply TestReply pipeline\n3:New reply TestReply pipeline\n", o_testClientMethod_literalMutationString116134__13);
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116161_add118634_literalMutationString120875_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("client.h<tml");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testClientMethod_literalMutationString116161__9 = scope.put("reply", "TestReply");
            Object o_testClientMethod_literalMutationString116161__10 = scope.put("commands", Arrays.asList("a", ""));
            Writer o_testClientMethod_literalMutationString116161__12 = m.execute(sw, scope);
            String o_testClientMethod_literalMutationString116161__13 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            m.getName();
            org.junit.Assert.fail("testClientMethod_literalMutationString116161_add118634_literalMutationString120875 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template client.h<tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_add116173_literalMutationString116551_failAssert0null129221_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("clie<t.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testClientMethod_add116173__9 = scope.put(null, "TestReply");
                List<String> o_testClientMethod_add116173__10 = Arrays.asList("a", "b");
                Object o_testClientMethod_add116173__11 = scope.put("commands", Arrays.asList("a", "b"));
                Writer o_testClientMethod_add116173__13 = m.execute(sw, scope);
                String o_testClientMethod_add116173__14 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_add116173_literalMutationString116551 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_add116173_literalMutationString116551_failAssert0null129221 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template clie<t.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_add116173_literalMutationString116551_failAssert0_add127915_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("clie<t.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testClientMethod_add116173__9 = scope.put("reply", "TestReply");
                Arrays.asList("a", "b");
                List<String> o_testClientMethod_add116173__10 = Arrays.asList("a", "b");
                Object o_testClientMethod_add116173__11 = scope.put("commands", Arrays.asList("a", "b"));
                Writer o_testClientMethod_add116173__13 = m.execute(sw, scope);
                String o_testClientMethod_add116173__14 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_add116173_literalMutationString116551 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_add116173_literalMutationString116551_failAssert0_add127915 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template clie<t.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116135_failAssert0_literalMutationString118024_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("reply", "TestReply");
                scope.put("elf^q(xV", Arrays.asList("a", "b"));
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_literalMutationString116135 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString116135_failAssert0_literalMutationString118024 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_add116173_literalMutationString116551_failAssert0_literalMutationString125016_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("clie<t.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testClientMethod_add116173__9 = scope.put("reply", "TestReply");
                List<String> o_testClientMethod_add116173__10 = Arrays.asList("a", "b");
                Object o_testClientMethod_add116173__11 = scope.put("com-mands", Arrays.asList("a", "b"));
                Writer o_testClientMethod_add116173__13 = m.execute(sw, scope);
                String o_testClientMethod_add116173__14 = TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_add116173_literalMutationString116551 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_add116173_literalMutationString116551_failAssert0_literalMutationString125016 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template clie<t.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString116135_failAssert0_literalMutationString118024_failAssert0_literalMutationString125172_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("reply", "TestReply");
                    scope.put("el^q(xV", Arrays.asList("a", "b"));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                    sw.toString();
                    org.junit.Assert.fail("testClientMethod_literalMutationString116135 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testClientMethod_literalMutationString116135_failAssert0_literalMutationString118024 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString116135_failAssert0_literalMutationString118024_failAssert0_literalMutationString125172 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105131_failAssert0null107201_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.ht{ml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile(null);
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105131 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0null107201 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchild1.ht{ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105131_failAssert0_add106851_failAssert0null115139_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subsubchild1.ht{ml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("subsubchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105131 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0_add106851 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0_add106851_failAssert0null115139 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchild1.ht{ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105131_failAssert0null107201_failAssert0_add113239_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subsubchild1.ht{ml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile(null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105131 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0null107201 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0null107201_failAssert0_add113239 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchild1.ht{ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105148_failAssert0_literalMutationString106209_failAssert0_literalMutationString111276_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("subsubchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105148 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105148_failAssert0_literalMutationString106209 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105148_failAssert0_literalMutationString106209_failAssert0_literalMutationString111276 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105127_failAssert0null107213_failAssert0_add113395_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    c.compile(null);
                    m = c.compile(null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105127 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105127_failAssert0null107213 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105127_failAssert0null107213_failAssert0_add113395 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_remove105161_remove107022_literalMutationString108799_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("yC8=gw-9ZM<D9ZDc>");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching_remove105161__7 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_remove105161__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            Writer o_testSubSubCaching_remove105161__18 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_remove105161__22 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_remove105161_remove107022_literalMutationString108799 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template yC8=gw-9ZM<D9ZDc> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105127_failAssert0_add106868_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105127 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105127_failAssert0_add106868 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105142_failAssert0_literalMutationString105862_failAssert0_literalMutationString110763_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("subsubcBhild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105142 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105142_failAssert0_literalMutationString105862 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105142_failAssert0_literalMutationString105862_failAssert0_literalMutationString110763 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105140_failAssert0null107196_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("s>bsubchild2.html");
                sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105140 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105140_failAssert0null107196 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template s>bsubchild2.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105131_failAssert0null107201_failAssert0_literalMutationString109196_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subsubchld1.ht{ml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile(null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105131 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0null107201 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0null107201_failAssert0_literalMutationString109196 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchld1.ht{ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_add105150_literalMutationString105366_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testSubSubCaching_add105150__3 = c.compile("subsubchild1.html");
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching_add105150__8 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_add105150__13 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("C&b1[&]iYr&6GCSd3");
            sw = new StringWriter();
            Writer o_testSubSubCaching_add105150__19 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_add105150__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_add105150_literalMutationString105366 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template C&b1[&]iYr&6GCSd3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105131_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.ht{ml");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105131 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchild1.ht{ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_add105153_literalMutationString105324_failAssert0_literalMutationString110624_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("]O]3(+GYi_qx{OE 6");
                StringWriter sw = new StringWriter();
                Writer o_testSubSubCaching_add105153__7 = m.execute(sw, new Object() {});
                sw.toString();
                String o_testSubSubCaching_add105153__13 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild2.html");
                sw = new StringWriter();
                Writer o_testSubSubCaching_add105153__19 = m.execute(sw, new Object() {});
                String o_testSubSubCaching_add105153__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_add105153_literalMutationString105324 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_add105153_literalMutationString105324_failAssert0_literalMutationString110624 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ]O]3(+GYi_qx{OE 6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105130_failAssert0_add106965_failAssert0_literalMutationString109892_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("ye<v>`j6S3DpvkPGI");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("subsubchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105130 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_add106965 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_add106965_failAssert0_literalMutationString109892 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ye<v>`j6S3DpvkPGI not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_add105151_literalMutationString105418_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching_add105151__7 = m.execute(sw, new Object() {});
            Writer o_testSubSubCaching_add105151__12 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_add105151__17 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile(";y`x{ix^;U;;^[(]G");
            sw = new StringWriter();
            Writer o_testSubSubCaching_add105151__23 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_add105151__28 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_add105151_literalMutationString105418 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ;y`x{ix^;U;;^[(]G not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105141_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("FL!Y)F[L(&vi3%t@:");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105141 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template FL!Y)F[L(&vi3%t@: not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_add105156_literalMutationString105454_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile(": H6WG3*(KOE^a;U,");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching_add105156__7 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_add105156__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            Writer o_testSubSubCaching_add105156__18 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_add105156__23 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            String o_testSubSubCaching_add105156__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_add105156_literalMutationString105454 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template : H6WG3*(KOE^a;U, not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105140_failAssert0null107196_failAssert0_add113280_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subsubchild1.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("s>bsubchild2.html");
                    sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105140 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105140_failAssert0null107196 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105140_failAssert0null107196_failAssert0_add113280 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template s>bsubchild2.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105130_failAssert0_add106965_failAssert0_literalMutationString109888_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("ye<v>`j6S3DpvkPGI");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105130 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_add106965 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_add106965_failAssert0_literalMutationString109888 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ye<v>`j6S3DpvkPGI not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105140_failAssert0_literalMutationString105927_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("s>bsubchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchiZd2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105140 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105140_failAssert0_literalMutationString105927 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template s>bsubchild2.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105130_failAssert0_add106965_failAssert0_add113540_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("ye<v>`j6S3DpvkPGI");
                    Mustache m = c.compile("ye<v>`j6S3DpvkPGI");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("subsubchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105130 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_add106965 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_add106965_failAssert0_add113540 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ye<v>`j6S3DpvkPGI not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105127_failAssert0_literalMutationString105983_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                m = c.compile("subsubchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105127 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105127_failAssert0_literalMutationString105983 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105139_failAssert0_literalMutationString105759_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105139 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105139_failAssert0_literalMutationString105759 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105140_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("s>bsubchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105140 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template s>bsubchild2.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105131_failAssert0_add106851_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.ht{ml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105131 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0_add106851 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchild1.ht{ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_add105153_literalMutationString105324_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("[rvS(v$Dcvf/Wr(@[");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching_add105153__7 = m.execute(sw, new Object() {});
            sw.toString();
            String o_testSubSubCaching_add105153__13 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            Writer o_testSubSubCaching_add105153__19 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_add105153__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_add105153_literalMutationString105324 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [rvS(v$Dcvf/Wr(@[ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105139_failAssert0_add106782_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105139 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105139_failAssert0_add106782 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105131_failAssert0null107201_failAssert0null114978_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subsubchild1.ht{ml");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile(null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105131 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0null107201 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0null107201_failAssert0null114978 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchild1.ht{ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105130_failAssert0_add106965_failAssert0_add113546_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("ye<v>`j6S3DpvkPGI");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("subsubchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105130 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_add106965 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_add106965_failAssert0_add113546 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ye<v>`j6S3DpvkPGI not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105131_failAssert0_add106851_failAssert0_literalMutationString109831_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subsubchild1@.ht{ml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("subsubchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105131 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0_add106851 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0_add106851_failAssert0_literalMutationString109831 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchild1@.ht{ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_add105154_literalMutationString105309_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching_add105154__7 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_add105154__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            Mustache o_testSubSubCaching_add105154__14 = c.compile("subsubchild2.html");
            m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            sw = new StringWriter();
            Writer o_testSubSubCaching_add105154__19 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_add105154__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_add105154_literalMutationString105309 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105138() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("subsubchild1.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subsubchild1.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSubSubCaching_literalMutationString105138__7 = m.execute(sw, new Object() {});
        Assert.assertEquals("first\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching_literalMutationString105138__7)).getBuffer())).toString());
        Assert.assertEquals("first\n", ((StringWriter) (o_testSubSubCaching_literalMutationString105138__7)).toString());
        String o_testSubSubCaching_literalMutationString105138__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
        Assert.assertEquals("first\n", o_testSubSubCaching_literalMutationString105138__12);
        sw.toString();
        sw.toString();
        m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubSubCaching_literalMutationString105138__18 = m.execute(sw, new Object() {});
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching_literalMutationString105138__18)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubSubCaching_literalMutationString105138__18)).toString());
        String o_testSubSubCaching_literalMutationString105138__23 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
        Assert.assertEquals("precontent\n\npostcontent\n", o_testSubSubCaching_literalMutationString105138__23);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("first\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching_literalMutationString105138__7)).getBuffer())).toString());
        Assert.assertEquals("first\n", ((StringWriter) (o_testSubSubCaching_literalMutationString105138__7)).toString());
        Assert.assertEquals("first\n", o_testSubSubCaching_literalMutationString105138__12);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching_literalMutationString105138__18)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubSubCaching_literalMutationString105138__18)).toString());
        Assert.assertEquals("precontent\n\npostcontent\n", o_testSubSubCaching_literalMutationString105138__23);
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105130_failAssert0_add106965_failAssert0null115148_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("ye<v>`j6S3DpvkPGI");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    m = c.compile("subsubchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105130 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_add106965 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_add106965_failAssert0null115148 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ye<v>`j6S3DpvkPGI not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105126_add106665() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSubSubCaching_literalMutationString105126_add106665__7 = m.execute(sw, new Object() {});
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching_literalMutationString105126_add106665__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubSubCaching_literalMutationString105126_add106665__7)).toString());
        Writer o_testSubSubCaching_literalMutationString105126__7 = m.execute(sw, new Object() {});
        String o_testSubSubCaching_literalMutationString105126__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
        Assert.assertEquals("first\n", o_testSubSubCaching_literalMutationString105126__12);
        sw.toString();
        sw.toString();
        m = c.compile("subsubchild2.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subsubchild2.html", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubSubCaching_literalMutationString105126__18 = m.execute(sw, new Object() {});
        String o_testSubSubCaching_literalMutationString105126__23 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
        Assert.assertEquals("precontent\n\npostcontent\n", o_testSubSubCaching_literalMutationString105126__23);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subsubchild2.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching_literalMutationString105126_add106665__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubSubCaching_literalMutationString105126_add106665__7)).toString());
        Assert.assertEquals("first\n", o_testSubSubCaching_literalMutationString105126__12);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subsubchild2.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("precontent\n\npostcontent\n", o_testSubSubCaching_literalMutationString105126__23);
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_add105153_literalMutationString105324_failAssert0_add113838_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("[rvS(v$Dcvf/Wr(@[");
                StringWriter sw = new StringWriter();
                Writer o_testSubSubCaching_add105153__7 = m.execute(sw, new Object() {});
                sw.toString();
                String o_testSubSubCaching_add105153__13 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                c.compile("subsubchild2.html");
                m = c.compile("subsubchild2.html");
                sw = new StringWriter();
                Writer o_testSubSubCaching_add105153__19 = m.execute(sw, new Object() {});
                String o_testSubSubCaching_add105153__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_add105153_literalMutationString105324 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_add105153_literalMutationString105324_failAssert0_add113838 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [rvS(v$Dcvf/Wr(@[ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105147_failAssert0_literalMutationString106062_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, ";h$qzlXzz#wFOLM<");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105147 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105147_failAssert0_literalMutationString106062 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105130_failAssert0_add106965_failAssert0null115151_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("ye<v>`j6S3DpvkPGI");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("subsubchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105130 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_add106965 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_add106965_failAssert0null115151 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ye<v>`j6S3DpvkPGI not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_remove105158_literalMutationString105498_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            String o_testSubSubCaching_remove105158__7 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subs]bchild2.html");
            sw = new StringWriter();
            Writer o_testSubSubCaching_remove105158__13 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_remove105158__18 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_remove105158_literalMutationString105498 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subs]bchild2.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105126() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSubSubCaching_literalMutationString105126__7 = m.execute(sw, new Object() {});
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching_literalMutationString105126__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubSubCaching_literalMutationString105126__7)).toString());
        String o_testSubSubCaching_literalMutationString105126__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
        Assert.assertEquals("first\n", o_testSubSubCaching_literalMutationString105126__12);
        sw.toString();
        sw.toString();
        m = c.compile("subsubchild2.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subsubchild2.html", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubSubCaching_literalMutationString105126__18 = m.execute(sw, new Object() {});
        Assert.assertEquals("precontent\n\npostcontent\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching_literalMutationString105126__18)).getBuffer())).toString());
        Assert.assertEquals("precontent\n\npostcontent\n", ((StringWriter) (o_testSubSubCaching_literalMutationString105126__18)).toString());
        String o_testSubSubCaching_literalMutationString105126__23 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
        Assert.assertEquals("precontent\n\npostcontent\n", o_testSubSubCaching_literalMutationString105126__23);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subsubchild2.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching_literalMutationString105126__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubSubCaching_literalMutationString105126__7)).toString());
        Assert.assertEquals("first\n", o_testSubSubCaching_literalMutationString105126__12);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subsubchild2.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("precontent\n\npostcontent\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching_literalMutationString105126__18)).getBuffer())).toString());
        Assert.assertEquals("precontent\n\npostcontent\n", ((StringWriter) (o_testSubSubCaching_literalMutationString105126__18)).toString());
        Assert.assertEquals("precontent\n\npostcontent\n", o_testSubSubCaching_literalMutationString105126__23);
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105131_failAssert0_literalMutationString105951_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.ht{ml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchimd2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105131 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0_literalMutationString105951 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchild1.ht{ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105130_failAssert0_literalMutationString106223_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("ye<v>`j6S3DpvkPGI");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "su subchild1.txt");
                sw.toString();
                m = c.compile("subsubchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105130 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_literalMutationString106223 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ye<v>`j6S3DpvkPGI not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105139_failAssert0_add106781_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                sw.toString();
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105139 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105139_failAssert0_add106781 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105140_failAssert0_add106841_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("subsubchild1.html");
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("s>bsubchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105140 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105140_failAssert0_add106841 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template s>bsubchild2.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_remove105159_literalMutationString105532_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile(";H[uRir|:;i]dWUE[");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching_remove105159__7 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_remove105159__11 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            Writer o_testSubSubCaching_remove105159__17 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_remove105159__22 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_remove105159_literalMutationString105532 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ;H[uRir|:;i]dWUE[ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105130_failAssert0_add106965_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("ye<v>`j6S3DpvkPGI");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105130 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0_add106965 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ye<v>`j6S3DpvkPGI not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105127_failAssert0null107213_failAssert0_literalMutationString109535_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(" does not exist");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile(null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105127 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105127_failAssert0null107213 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105127_failAssert0null107213_failAssert0_literalMutationString109535 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105139_failAssert0null107154_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105139 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105139_failAssert0null107154 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105127_failAssert0null107213_failAssert0null115060_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile(null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105127 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105127_failAssert0null107213 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105127_failAssert0null107213_failAssert0null115060 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105139_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105139 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105130_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("ye<v>`j6S3DpvkPGI");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105130 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ye<v>`j6S3DpvkPGI not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_add105153_add106344_literalMutationString108486_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching_add105153__7 = m.execute(sw, new Object() {});
            ((StringWriter) (o_testSubSubCaching_add105153__7)).getBuffer().toString();
            sw.toString();
            String o_testSubSubCaching_add105153__13 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            Writer o_testSubSubCaching_add105153__19 = m.execute(sw, new Object() {});
            String o_testSubSubCaching_add105153__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_add105153_add106344_literalMutationString108486 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105127_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105127 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105130_failAssert0null107271_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("ye<v>`j6S3DpvkPGI");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105130 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105130_failAssert0null107271 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ye<v>`j6S3DpvkPGI not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105131_failAssert0_add106851_failAssert0_add113522_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subsubchild1.ht{ml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("subsubchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105131 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0_add106851 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105131_failAssert0_add106851_failAssert0_add113522 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchild1.ht{ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_add105153_literalMutationString105324_failAssert0null115334_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("[rvS(v$Dcvf/Wr(@[");
                StringWriter sw = new StringWriter();
                Writer o_testSubSubCaching_add105153__7 = m.execute(null, new Object() {});
                sw.toString();
                String o_testSubSubCaching_add105153__13 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild2.html");
                sw = new StringWriter();
                Writer o_testSubSubCaching_add105153__19 = m.execute(sw, new Object() {});
                String o_testSubSubCaching_add105153__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_add105153_literalMutationString105324 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_add105153_literalMutationString105324_failAssert0null115334 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [rvS(v$Dcvf/Wr(@[ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105127_failAssert0null107213_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile(null);
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105127 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105127_failAssert0null107213 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105141_failAssert0_add106862_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                sw.toString();
                m = c.compile("FL!Y)F[L(&vi3%t@:");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105141 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105141_failAssert0_add106862 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template FL!Y)F[L(&vi3%t@: not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_add105154_literalMutationString105309_failAssert0_add113847_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                Writer o_testSubSubCaching_add105154__7 = m.execute(sw, new Object() {});
                String o_testSubSubCaching_add105154__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                c.compile("subsubchild2.html");
                Mustache o_testSubSubCaching_add105154__14 = c.compile("subsubchild2.html");
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                Writer o_testSubSubCaching_add105154__19 = m.execute(sw, new Object() {});
                String o_testSubSubCaching_add105154__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_add105154_literalMutationString105309 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_add105154_literalMutationString105309_failAssert0_add113847 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105140_failAssert0null107196_failAssert0_literalMutationString109299_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subsubchild1.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("s>bsubchild2.html");
                    sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "S<AT-T9TK_,3lY.q");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching_literalMutationString105140 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105140_failAssert0null107196 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105140_failAssert0null107196_failAssert0_literalMutationString109299 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template s>bsubchild2.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString105149_failAssert0_literalMutationString106155_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.t-xt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString105149 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString105149_failAssert0_literalMutationString106155 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_add79380_literalMutationString79617_failAssert0_add88559_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                Writer o_testSubSubCaching2_add79380__7 = m.execute(sw, new Object() {});
                sw.toString();
                String o_testSubSubCaching2_add79380__13 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                sw.toString();
                m = c.compile("A},[0j 820I[QnUh7");
                sw = new StringWriter();
                Writer o_testSubSubCaching2_add79380__19 = m.execute(sw, new Object() {});
                String o_testSubSubCaching2_add79380__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_add79380_literalMutationString79617 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_add79380_literalMutationString79617_failAssert0_add88559 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template A},[0j 820I[QnUh7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79354_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild3.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79354 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79357_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("l3E$}-!J)[#<gXTe_");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild3.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79357 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template l3E$}-!J)[#<gXTe_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79367_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("}0I%(S:IqTacXTI*4");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79367 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template }0I%(S:IqTacXTI*4 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79365() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("subsubchild1.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subsubchild1.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSubSubCaching2_literalMutationString79365__7 = m.execute(sw, new Object() {});
        Assert.assertEquals("first\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching2_literalMutationString79365__7)).getBuffer())).toString());
        Assert.assertEquals("first\n", ((StringWriter) (o_testSubSubCaching2_literalMutationString79365__7)).toString());
        String o_testSubSubCaching2_literalMutationString79365__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
        Assert.assertEquals("first\n", o_testSubSubCaching2_literalMutationString79365__12);
        sw.toString();
        sw.toString();
        m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubSubCaching2_literalMutationString79365__18 = m.execute(sw, new Object() {});
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching2_literalMutationString79365__18)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubSubCaching2_literalMutationString79365__18)).toString());
        String o_testSubSubCaching2_literalMutationString79365__23 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
        Assert.assertEquals("precontent\nthird\n\npostcontent\n", o_testSubSubCaching2_literalMutationString79365__23);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("first\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching2_literalMutationString79365__7)).getBuffer())).toString());
        Assert.assertEquals("first\n", ((StringWriter) (o_testSubSubCaching2_literalMutationString79365__7)).toString());
        Assert.assertEquals("first\n", o_testSubSubCaching2_literalMutationString79365__12);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching2_literalMutationString79365__18)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubSubCaching2_literalMutationString79365__18)).toString());
        Assert.assertEquals("precontent\nthird\n\npostcontent\n", o_testSubSubCaching2_literalMutationString79365__23);
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_add79380_literalMutationString79617_failAssert0null89824_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                Writer o_testSubSubCaching2_add79380__7 = m.execute(sw, new Object() {});
                sw.toString();
                String o_testSubSubCaching2_add79380__13 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("A},[0j 820I[QnUh7");
                sw = new StringWriter();
                Writer o_testSubSubCaching2_add79380__19 = m.execute(null, new Object() {});
                String o_testSubSubCaching2_add79380__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_add79380_literalMutationString79617 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_add79380_literalMutationString79617_failAssert0null89824 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template A},[0j 820I[QnUh7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79375_failAssert0_literalMutationString80139_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("KZd.|TrGcX)pX(=a,");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild3.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubc(ild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79375 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79375_failAssert0_literalMutationString80139 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template KZd.|TrGcX)pX(=a, not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79365_literalMutationString79840_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching2_literalMutationString79365__7 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_literalMutationString79365__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            sw = new StringWriter();
            Writer o_testSubSubCaching2_literalMutationString79365__18 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_literalMutationString79365__23 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79365_literalMutationString79840 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_add79379_literalMutationString79630_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("lmvL,pY9}<)5}DtCR");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching2_add79379__7 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_add79379__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            String o_testSubSubCaching2_add79379__13 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild3.html");
            sw = new StringWriter();
            Writer o_testSubSubCaching2_add79379__19 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_add79379__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_add79379_literalMutationString79630 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lmvL,pY9}<)5}DtCR not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_add79380_literalMutationString79617_failAssert0_literalMutationString86234_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Writer o_testSubSubCaching2_add79380__7 = m.execute(sw, new Object() {});
                sw.toString();
                String o_testSubSubCaching2_add79380__13 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("A},[0j 820I[QnUh7");
                sw = new StringWriter();
                Writer o_testSubSubCaching2_add79380__19 = m.execute(sw, new Object() {});
                String o_testSubSubCaching2_add79380__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_add79380_literalMutationString79617 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_add79380_literalMutationString79617_failAssert0_literalMutationString86234 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_add79380_literalMutationString79617_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching2_add79380__7 = m.execute(sw, new Object() {});
            sw.toString();
            String o_testSubSubCaching2_add79380__13 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("A},[0j 820I[QnUh7");
            sw = new StringWriter();
            Writer o_testSubSubCaching2_add79380__19 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_add79380__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_add79380_literalMutationString79617 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template A},[0j 820I[QnUh7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79371_failAssert0_literalMutationString80160_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild3.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79371 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79371_failAssert0_literalMutationString80160 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79367_failAssert0_add81067_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("}0I%(S:IqTacXTI*4");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79367 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79367_failAssert0_add81067 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template }0I%(S:IqTacXTI*4 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79353() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSubSubCaching2_literalMutationString79353__7 = m.execute(sw, new Object() {});
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching2_literalMutationString79353__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubSubCaching2_literalMutationString79353__7)).toString());
        String o_testSubSubCaching2_literalMutationString79353__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
        Assert.assertEquals("first\n", o_testSubSubCaching2_literalMutationString79353__12);
        sw.toString();
        sw.toString();
        m = c.compile("subsubchild3.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subsubchild3.html", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubSubCaching2_literalMutationString79353__18 = m.execute(sw, new Object() {});
        Assert.assertEquals("precontent\nthird\n\npostcontent\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching2_literalMutationString79353__18)).getBuffer())).toString());
        Assert.assertEquals("precontent\nthird\n\npostcontent\n", ((StringWriter) (o_testSubSubCaching2_literalMutationString79353__18)).toString());
        String o_testSubSubCaching2_literalMutationString79353__23 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
        Assert.assertEquals("precontent\nthird\n\npostcontent\n", o_testSubSubCaching2_literalMutationString79353__23);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subsubchild3.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching2_literalMutationString79353__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubSubCaching2_literalMutationString79353__7)).toString());
        Assert.assertEquals("first\n", o_testSubSubCaching2_literalMutationString79353__12);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subsubchild3.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("precontent\nthird\n\npostcontent\n", ((StringBuffer) (((StringWriter) (o_testSubSubCaching2_literalMutationString79353__18)).getBuffer())).toString());
        Assert.assertEquals("precontent\nthird\n\npostcontent\n", ((StringWriter) (o_testSubSubCaching2_literalMutationString79353__18)).toString());
        Assert.assertEquals("precontent\nthird\n\npostcontent\n", o_testSubSubCaching2_literalMutationString79353__23);
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_add79383_literalMutationString79585_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching2_add79383__7 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_add79383__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("sub[subchild3.html");
            sw = new StringWriter();
            Writer o_testSubSubCaching2_add79383__18 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_add79383__23 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            String o_testSubSubCaching2_add79383__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_add79383_literalMutationString79585 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sub[subchild3.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2null79394_failAssert0_literalMutationString79924_failAssert0_literalMutationString84728_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.xt");
                    sw.toString();
                    m = c.compile("subsubchild3.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching2null79394 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testSubSubCaching2null79394_failAssert0_literalMutationString79924 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2null79394_failAssert0_literalMutationString79924_failAssert0_literalMutationString84728 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79357_failAssert0_add81020_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("l3E$}-!J)[#<gXTe_");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild3.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79357 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79357_failAssert0_add81020 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template l3E$}-!J)[#<gXTe_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79366_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79366 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_add79384_literalMutationString79561_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching2_add79384__7 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_add79384__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            sw = new StringWriter();
            Writer o_testSubSubCaching2_add79384__18 = m.execute(sw, new Object() {});
            sw.toString();
            String o_testSubSubCaching2_add79384__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_add79384_literalMutationString79561 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2null79390_failAssert0_literalMutationString79892_failAssert0_literalMutationString86080_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("?&iGG4p8G^`ZbK/+W");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "Pz2Y0)w[Qz?f-o}[");
                    sw.toString();
                    m = c.compile("subsubchild3.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching2null79390 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testSubSubCaching2null79390_failAssert0_literalMutationString79892 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2null79390_failAssert0_literalMutationString79892_failAssert0_literalMutationString86080 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?&iGG4p8G^`ZbK/+W not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79365_literalMutationString79840_failAssert0_add87999_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                Writer o_testSubSubCaching2_literalMutationString79365__7 = m.execute(sw, new Object() {});
                String o_testSubSubCaching2_literalMutationString79365__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                Writer o_testSubSubCaching2_literalMutationString79365__18 = m.execute(sw, new Object() {});
                String o_testSubSubCaching2_literalMutationString79365__23 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79365_literalMutationString79840 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79365_literalMutationString79840_failAssert0_add87999 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79354_failAssert0_add81179_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                sw.toString();
                m = c.compile("subsubchild3.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79354 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79354_failAssert0_add81179 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2null79390_failAssert0_literalMutationString79892_failAssert0null89782_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("?&iGG4p8G^`ZbK/+W");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("subsubchild3.html");
                    sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching2null79390 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testSubSubCaching2null79390_failAssert0_literalMutationString79892 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2null79390_failAssert0_literalMutationString79892_failAssert0null89782 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?&iGG4p8G^`ZbK/+W not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79354_failAssert0_literalMutationString80427_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "(1`]I19ot;(nSG4j");
                sw.toString();
                m = c.compile("subsubchild3.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79354 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79354_failAssert0_literalMutationString80427 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79366_failAssert0_add81100_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79366 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79366_failAssert0_add81100 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79354_failAssert0null81495_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild3.html");
                sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79354 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79354_failAssert0null81495 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79362_failAssert0_literalMutationString80389_failAssert0_literalMutationString86166_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(">p)Yo|aZom[J0Py`.");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubcild1.txt");
                    sw.toString();
                    m = c.compile("subsubchild3.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsuchild3.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching2_literalMutationString79362 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79362_failAssert0_literalMutationString80389 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79362_failAssert0_literalMutationString80389_failAssert0_literalMutationString86166 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >p)Yo|aZom[J0Py`. not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2null79390_failAssert0_literalMutationString79892_failAssert0_add88500_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("?&iGG4p8G^`ZbK/+W");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                    sw.toString();
                    m = c.compile("subsubchild3.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubSubCaching2null79390 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testSubSubCaching2null79390_failAssert0_literalMutationString79892 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2null79390_failAssert0_literalMutationString79892_failAssert0_add88500 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?&iGG4p8G^`ZbK/+W not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2null79390_failAssert0_literalMutationString79892_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("?&iGG4p8G^`ZbK/+W");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild3.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2null79390 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSubSubCaching2null79390_failAssert0_literalMutationString79892 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?&iGG4p8G^`ZbK/+W not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79357_failAssert0_literalMutationString80000_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("l3E$}-!J)[#<gXTe_");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "susubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild3.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79357 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79357_failAssert0_literalMutationString80000 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template l3E$}-!J)[#<gXTe_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_add79377_literalMutationString79657_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testSubSubCaching2_add79377__3 = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching2_add79377__8 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_add79377__13 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild3.html");
            sw = new StringWriter();
            Writer o_testSubSubCaching2_add79377__19 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_add79377__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_add79377_literalMutationString79657 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79367_failAssert0null81418_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("}0I%(S:IqTacXTI*4");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79367 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79367_failAssert0null81418 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template }0I%(S:IqTacXTI*4 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79362_failAssert0_literalMutationString80371_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Q90|Efj&yK9P |/{X");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubcild1.txt");
                sw.toString();
                m = c.compile("subsubchild3.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79362 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79362_failAssert0_literalMutationString80371 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Q90|Efj&yK9P |/{X not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79357_failAssert0null81387_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("l3E$}-!J)[#<gXTe_");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild3.html");
                sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString79357 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79357_failAssert0null81387 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template l3E$}-!J)[#<gXTe_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString79353_remove81253_literalMutationString82142_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching2_literalMutationString79353__7 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_literalMutationString79353__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild3.h`ml");
            sw = new StringWriter();
            Writer o_testSubSubCaching2_literalMutationString79353__18 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_literalMutationString79353__23 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString79353_remove81253_literalMutationString82142 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchild3.h`ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_add90285_literalMutationString90388_failAssert0_add94416_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testNested_add90285__3 = c.compile("^H<e&IQxyWZ[#$?LqL;&5x7");
                Mustache m = c.compile("nested_inheritance.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                Writer o_testNested_add90285__8 = m.execute(sw, new Object() {});
                String o_testNested_add90285__13 = TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_add90285_literalMutationString90388 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_add90285_literalMutationString90388_failAssert0_add94416 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ^H<e&IQxyWZ[#$?LqL;&5x7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90772_failAssert0null94774_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("nest<d_inheritance.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90772 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90772_failAssert0null94774 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90275_failAssert0null90844_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90275 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90275_failAssert0null90844 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_add90285_literalMutationString90396_failAssert0() throws IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testNested_add90285__3 = c.compile("nested_inheritance.html");
            Mustache m = c.compile("[$Us^g[=hd}=;)pl*{VSZJ;");
            StringWriter sw = new StringWriter();
            Writer o_testNested_add90285__8 = m.execute(sw, new Object() {});
            String o_testNested_add90285__13 = TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
            sw.toString();
            org.junit.Assert.fail("testNested_add90285_literalMutationString90396 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [$Us^g[=hd}=;)pl*{VSZJ; not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90771_failAssert0_add94507_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("nest<d_inheritance.html");
                    Mustache m = c.compile("nest<d_inheritance.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90771 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90771_failAssert0_add94507 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90771_failAssert0_add94503_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("nest<d_inheritance.html");
                    c.compile("nest<d_inheritance.html");
                    Mustache m = c.compile("nest<d_inheritance.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90771 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90771_failAssert0_add94503 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90775_failAssert0_add93920_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("nest<d_inheritance.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90775 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90775_failAssert0_add93920 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90283_failAssert0null90866_failAssert0_literalMutationString91799_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90283 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90283_failAssert0null90866 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90283_failAssert0null90866_failAssert0_literalMutationString91799 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90283_failAssert0null90865_failAssert0_literalMutationString91808_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("39[abIFh=N{GBt]I#[8d*rB");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_kinheritance.txt");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90283 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90283_failAssert0null90865 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90283_failAssert0null90865_failAssert0_literalMutationString91808 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 39[abIFh=N{GBt]I#[8d*rB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_add90285_literalMutationString90388_failAssert0_literalMutationString93067_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testNested_add90285__3 = c.compile("^H<e&IQxyWZ[#$?LqL;&5x7");
                Mustache m = c.compile("nested_inheritance.html");
                StringWriter sw = new StringWriter();
                Writer o_testNested_add90285__8 = m.execute(sw, new Object() {});
                String o_testNested_add90285__13 = TestUtil.getContents(AmplExtensionTest.root, "nested_inheritan5e.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_add90285_literalMutationString90388 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_add90285_literalMutationString90388_failAssert0_literalMutationString93067 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ^H<e&IQxyWZ[#$?LqL;&5x7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90774_failAssert0null95016_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("nest<d_inheritance.html");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90774 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90774_failAssert0null95016 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90774_failAssert0_literalMutationString92913_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("nest<d_inheritance.Ttml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90774 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90774_failAssert0_literalMutationString92913 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.Ttml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90275_failAssert0null90845_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90275 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90275_failAssert0null90845 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90280_failAssert0_literalMutationString90562_failAssert0_literalMutationString92659_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&Efjp[A;7} not found");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90280 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90280_failAssert0_literalMutationString90562 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90280_failAssert0_literalMutationString90562_failAssert0_literalMutationString92659 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0null90851_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("*Ww*MY/{;j5a+A}E3%e#3|)");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0null90851 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Ww*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_add90286_remove90796_literalMutationString91583_failAssert0() throws IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Writer o_testNested_add90286__7 = m.execute(sw, new Object() {});
            Writer o_testNested_add90286__12 = m.execute(sw, new Object() {});
            String o_testNested_add90286__17 = TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
            sw.toString();
            org.junit.Assert.fail("testNested_add90286_remove90796_literalMutationString91583 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90771_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("nest<d_inheritance.html");
                Mustache m = c.compile("nest<d_inheritance.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90771 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_remove90290_remove90801_literalMutationString91471_failAssert0() throws IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            Writer o_testNested_remove90290__7 = m.execute(sw, new Object() {});
            String o_testNested_remove90290__11 = TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
            org.junit.Assert.fail("testNested_remove90290_remove90801_literalMutationString91471 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90774_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("nest<d_inheritance.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90774 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90275_failAssert0_add90752_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90275 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90275_failAssert0_add90752 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_literalMutationString90547_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("nest<d_inheritance.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_literalMutationString90547 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90771_failAssert0null95098_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("nest<d_inheritance.html");
                    Mustache m = c.compile(null);
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90771 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90771_failAssert0null95098 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0() throws IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("*Ww*MY/{;j5a+A}E3%e#3|)");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
            sw.toString();
            org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Ww*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90279_failAssert0_literalMutationString90476_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("eFd}9DeXr#WD:nNydAk&K/k");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90279 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90279_failAssert0_literalMutationString90476 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template eFd}9DeXr#WD:nNydAk&K/k not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_add90285_literalMutationString90388_failAssert0() throws IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testNested_add90285__3 = c.compile("^H<e&IQxyWZ[#$?LqL;&5x7");
            Mustache m = c.compile("nested_inheritance.html");
            StringWriter sw = new StringWriter();
            Writer o_testNested_add90285__8 = m.execute(sw, new Object() {});
            String o_testNested_add90285__13 = TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
            sw.toString();
            org.junit.Assert.fail("testNested_add90285_literalMutationString90388 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ^H<e&IQxyWZ[#$?LqL;&5x7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90282_failAssert0_literalMutationString90464_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("tKy3aE08$w@Q[|FEu]OTl|i");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inherit!nce.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90282 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90282_failAssert0_literalMutationString90464 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tKy3aE08$w@Q[|FEu]OTl|i not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90775_failAssert0_literalMutationString92002_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("nest<d_inheritance.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90775 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90775_failAssert0_literalMutationString92002 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0_add90763_failAssert0_literalMutationString92308_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("*Ww*MY/{;j5a+A}E3%e#3|)");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_add90763 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_add90763_failAssert0_literalMutationString92308 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Ww*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0_add90764_failAssert0null94860_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("*Ww*MY/{;j5a+A}E3%e#3|)");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_add90764 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_add90764_failAssert0null94860 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Ww*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90771_failAssert0_literalMutationString93241_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("M*7HU^nq`k#Rd-g/(Cd0=fw");
                    Mustache m = c.compile("nest<d_inheritance.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90771 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90771_failAssert0_literalMutationString93241 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template M*7HU^nq`k#Rd-g/(Cd0=fw not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNestednull90293_failAssert0_literalMutationString90440_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("w^}]ki?h#{lecv2z5TW&]v_");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testNestednull90293 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNestednull90293_failAssert0_literalMutationString90440 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template w^}]ki?h#{lecv2z5TW&]v_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90274() throws IOException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testNested_literalMutationString90274__7 = m.execute(sw, new Object() {});
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testNested_literalMutationString90274__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testNested_literalMutationString90274__7)).toString());
        String o_testNested_literalMutationString90274__12 = TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
        Assert.assertEquals("\n<box>\n\n<main>\n\n<box>\n\n<tweetbox classes=\"tweetbox-large\ntweetbox-user-styled\n\" attrs=\"data-rich-text\n\"></tweetbox>\n\n</box>\n</main>\n</box>", o_testNested_literalMutationString90274__12);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testNested_literalMutationString90274__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testNested_literalMutationString90274__7)).toString());
        Assert.assertEquals("\n<box>\n\n<main>\n\n<box>\n\n<tweetbox classes=\"tweetbox-large\ntweetbox-user-styled\n\" attrs=\"data-rich-text\n\"></tweetbox>\n\n</box>\n</main>\n</box>", o_testNested_literalMutationString90274__12);
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90281_failAssert0null90863_failAssert0_literalMutationString91882_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("z]@di (O*/5am?_}gKM9Q_E");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90281 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90281_failAssert0null90863 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90281_failAssert0null90863_failAssert0_literalMutationString91882 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template z]@di (O*/5am?_}gKM9Q_E not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90281_failAssert0null90863_failAssert0_literalMutationString91884_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90281 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90281_failAssert0null90863 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90281_failAssert0null90863_failAssert0_literalMutationString91884 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_add90288_remove90793_literalMutationString91597_failAssert0() throws IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("^#|7#mgc)pAq7_H/9nTA*Gm");
            StringWriter sw = new StringWriter();
            Writer o_testNested_add90288__7 = m.execute(sw, new Object() {});
            sw.toString();
            String o_testNested_add90288__13 = TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
            org.junit.Assert.fail("testNested_add90288_remove90793_literalMutationString91597 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ^#|7#mgc)pAq7_H/9nTA*Gm not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0_add90764_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("*Ww*MY/{;j5a+A}E3%e#3|)");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_add90764 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Ww*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0_add90763_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("*Ww*MY/{;j5a+A}E3%e#3|)");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_add90763 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Ww*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90772_failAssert0_add93912_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("nest<d_inheritance.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90772 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90772_failAssert0_add93912 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_remove90290_literalMutationString90412_failAssert0_literalMutationString93154_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                Writer o_testNested_remove90290__7 = m.execute(sw, new Object() {});
                String o_testNested_remove90290__11 = TestUtil.getContents(AmplExtensionTest.root, "xV&1%#`]MxV[s-cZ<(HS[y");
                sw.toString();
                org.junit.Assert.fail("testNested_remove90290_literalMutationString90412 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testNested_remove90290_literalMutationString90412_failAssert0_literalMutationString93154 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90771_failAssert0null95100_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("nest<d_inheritance.html");
                    Mustache m = c.compile("nest<d_inheritance.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90771 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90771_failAssert0null95100 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90771_failAssert0_literalMutationString93252_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("nest<d_inheritance.html");
                    Mustache m = c.compile("nest<d_inheritance.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "=9EeRI!LS79y7GmEs:F7KI");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90771 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90771_failAssert0_literalMutationString93252 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90275_failAssert0_literalMutationString90498_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj*;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90275 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90275_failAssert0_literalMutationString90498 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj*;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90775_failAssert0null94777_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("nest<d_inheritance.html");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90775 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90775_failAssert0null94777 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0null90857_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("nest<d_inheritance.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0null90857 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0_literalMutationString90520_failAssert0_add94268_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("*Wow*MY/{;j5a+A}E3%e#3|)");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_literalMutationString90520 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_literalMutationString90520_failAssert0_add94268 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Wow*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90774_failAssert0_add94356_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("nest<d_inheritance.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90774 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90774_failAssert0_add94356 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0_add90764_failAssert0_literalMutationString92320_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("*Ww*MY/{;j5a+A}E3%e#3|)");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_add90764 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_add90764_failAssert0_literalMutationString92320 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Ww*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_add90285_literalMutationString90388_failAssert0null95050_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testNested_add90285__3 = c.compile("^H<e&IQxyWZ[#$?LqL;&5x7");
                Mustache m = c.compile(null);
                StringWriter sw = new StringWriter();
                Writer o_testNested_add90285__8 = m.execute(sw, new Object() {});
                String o_testNested_add90285__13 = TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_add90285_literalMutationString90388 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_add90285_literalMutationString90388_failAssert0null95050 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ^H<e&IQxyWZ[#$?LqL;&5x7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_literalMutationString90546_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("0v]M+z&!|cIB+t.tLy{B]ER");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_literalMutationString90546 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 0v]M+z&!|cIB+t.tLy{B]ER not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_literalMutationString90552_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("nest<d_inheritance.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheriHtance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_literalMutationString90552 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90275_failAssert0_literalMutationString90495_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not fund");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90275 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90275_failAssert0_literalMutationString90495 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not fund not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0null90856_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("nest<d_inheritance.html");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0null90856 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNestednull90292_failAssert0_literalMutationString90450_failAssert0_literalMutationString93112_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nestced_inheritance.txt");
                    sw.toString();
                    org.junit.Assert.fail("testNestednull90292 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNestednull90292_failAssert0_literalMutationString90450 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNestednull90292_failAssert0_literalMutationString90450_failAssert0_literalMutationString93112 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90772_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("nest<d_inheritance.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90772 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0_literalMutationString90520_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("*Wow*MY/{;j5a+A}E3%e#3|)");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_literalMutationString90520 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Wow*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_add90775_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("nest<d_inheritance.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_add90775 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90275_failAssert0_literalMutationString90503_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inhereitance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90275 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90275_failAssert0_literalMutationString90503 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0_add90763_failAssert0_add94055_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile("*Ww*MY/{;j5a+A}E3%e#3|)");
                    Mustache m = c.compile("*Ww*MY/{;j5a+A}E3%e#3|)");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_add90763 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_add90763_failAssert0_add94055 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Ww*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0_literalMutationString90545_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("n.est<d_inheritance.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90278_failAssert0_literalMutationString90545 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template n.est<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0null90850_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("*Ww*MY/{;j5a+A}E3%e#3|)");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0null90850 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Ww*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90278_failAssert0() throws IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("nest<d_inheritance.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
            sw.toString();
            org.junit.Assert.fail("testNested_literalMutationString90278 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nest<d_inheritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0_literalMutationString90526_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("*Ww*MY/{;j5a+A}E3%e#3|)");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance].txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_literalMutationString90526 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Ww*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0_add90764_failAssert0_add94066_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("*Ww*MY/{;j5a+A}E3%e#3|)");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_add90764 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_add90764_failAssert0_add94066 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Ww*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90273_failAssert0_literalMutationString90506_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90273 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90273_failAssert0_literalMutationString90506 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90275_failAssert0_add90754_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90275 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90275_failAssert0_add90754 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90275_failAssert0_add90751_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString90275 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90275_failAssert0_add90751 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90275_failAssert0() throws IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
            sw.toString();
            org.junit.Assert.fail("testNested_literalMutationString90275 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString90276_failAssert0_literalMutationString90520_failAssert0_literalMutationString92770_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("*Wow*MY/{;j5a+A}E3%e#3|)");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw.toString();
                    org.junit.Assert.fail("testNested_literalMutationString90276 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_literalMutationString90520 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString90276_failAssert0_literalMutationString90520_failAssert0_literalMutationString92770 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *Wow*MY/{;j5a+A}E3%e#3|) not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplExtensionTest.root = (new File(file, "sub.html").exists()) ? file : new File("src/test/resources");
    }
}

