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
    public void testSub_literalMutationString15263_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile(">N/x,ryV");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString15263 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >N/x,ryV not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString15263_failAssert0_literalMutationString16894_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(">N/x,ryV");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString15263 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString15263_failAssert0_literalMutationString16894 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >N/x,ryV not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString15263_failAssert0null18510_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(">N/x,ryV");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", null);
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString15263 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString15263_failAssert0null18510 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >N/x,ryV not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString15263_failAssert0_add18072_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(">N/x,ryV");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString15263 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString15263_failAssert0_add18072 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >N/x,ryV not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString767_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("-i39+o//#{tQj:Z");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString767 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -i39+o//#{tQj:Z not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_add805_literalMutationString1181_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("parti[alsub.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartial_add805__9 = scope.put("name", "Sam");
            Object o_testSubInPartial_add805__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubInPartial_add805__11 = m.execute(sw, scope);
            sw.toString();
            String o_testSubInPartial_add805__13 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_add805_literalMutationString1181 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template parti[alsub.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubInPartial_literalMutationString771_literalMutationString1662_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("yX}edS]f#:5 (RY");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubInPartial_literalMutationString771__9 = scope.put("0}Ai", "Sam");
            Object o_testSubInPartial_literalMutationString771__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubInPartial_literalMutationString771__11 = m.execute(sw, scope);
            String o_testSubInPartial_literalMutationString771__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubInPartial_literalMutationString771_literalMutationString1662 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template yX}edS]f#:5 (RY not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString22036_failAssert0_add23500_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("_Ua=m1B!RK((XC+i1DbMN:");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString22036 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString22036_failAssert0_add23500 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template _Ua=m1B!RK((XC+i1DbMN: not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString22036_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("_Ua=m1B!RK((XC+i1DbMN:");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
            sw.toString();
            org.junit.Assert.fail("testPartialInSub_literalMutationString22036 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template _Ua=m1B!RK((XC+i1DbMN: not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPartialInSub_literalMutationString22036_failAssert0_literalMutationString22940_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("_Ua=m1B!RK((XC+i1bMN:");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "partialsubpartial.txt");
                sw.toString();
                org.junit.Assert.fail("testPartialInSub_literalMutationString22036 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPartialInSub_literalMutationString22036_failAssert0_literalMutationString22940 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template _Ua=m1B!RK((XC+i1bMN: not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("MsT|/[h3||(,tC8vz");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
            sw.toString();
            org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template MsT|/[h3||(,tC8vz not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString7_failAssert0_literalMutationString177_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("V`^u&weViHW[ [qlH");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString7 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString7_failAssert0_literalMutationString177 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template V`^u&weViHW[ [qlH not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_add462_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("MsT|/[h3||(,tC8vz");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_add462 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template MsT|/[h3||(,tC8vz not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFollow_literalMutationString4_failAssert0_literalMutationString246_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("MsT/[h3||(,tC8vz");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "follownomenu.txt");
                sw.toString();
                org.junit.Assert.fail("testFollow_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testFollow_literalMutationString4_failAssert0_literalMutationString246 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template MsT/[h3||(,tC8vz not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString14502_failAssert0null15017_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("3Z|p!7i::X+[?-s1NVlhKgB");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString14502 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString14502_failAssert0null15017 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 3Z|p!7i::X+[?-s1NVlhKgB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString14506_failAssert0_literalMutationString14763_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("d]5r_=fObS,y-/P>49&Yt(@");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "Tet");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString14506 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString14506_failAssert0_literalMutationString14763 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template d]5r_=fObS,y-/P>49&Yt(@ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString14502_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("3Z|p!7i::X+[?-s1NVlhKgB");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString14502 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 3Z|p!7i::X+[?-s1NVlhKgB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString14502_failAssert0_literalMutationString14677_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("3Z|p!7i::X+[?-s1NVlhKgB");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "Tet");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString14502 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString14502_failAssert0_literalMutationString14677 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 3Z|p!7i::X+[?-s1NVlhKgB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString14502_failAssert0_add14932_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("3Z|p!7i::X+[?-s1NVlhKgB");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString14502 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString14502_failAssert0_add14932 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 3Z|p!7i::X+[?-s1NVlhKgB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString14499_failAssert0_literalMutationString14719_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(" does not exist");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString14499 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString14499_failAssert0_literalMutationString14719 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString13286_failAssert0null14245_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("repl{ace.html");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString13286 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString13286_failAssert0null14245 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template repl{ace.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString13287_failAssert0_literalMutationString13743_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("BU]?$4rRFXYr");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replac%e.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString13287 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString13287_failAssert0_literalMutationString13743 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template BU]?$4rRFXYr not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString13298_failAssert0_literalMutationString13786_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("PhFpm|^%G@SH");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "uW;fa]WZx?c");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString13298 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString13298_failAssert0_literalMutationString13786 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template PhFpm|^%G@SH not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString13287_failAssert0_add14132_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("BU]?$4rRFXYr");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString13287 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString13287_failAssert0_add14132 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template BU]?$4rRFXYr not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString13287_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("BU]?$4rRFXYr");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String replace = "false";
            });
            TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString13287 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template BU]?$4rRFXYr not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString13286_failAssert0_add14122_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("repl{ace.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString13286 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString13286_failAssert0_add14122 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template repl{ace.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString13287_failAssert0null14252_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("BU]?$4rRFXYr");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString13287 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString13287_failAssert0null14252 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template BU]?$4rRFXYr not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString13286_failAssert0_literalMutationString13706_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("repl{ace.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String replace = "false";
                });
                TestUtil.getContents(AmplExtensionTest.root, "!n8}!#4@<eY");
                sw.toString();
                org.junit.Assert.fail("testParentReplace_literalMutationString13286 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParentReplace_literalMutationString13286_failAssert0_literalMutationString13706 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template repl{ace.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParentReplace_literalMutationString13286_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("repl{ace.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String replace = "false";
            });
            TestUtil.getContents(AmplExtensionTest.root, "replace.txt");
            sw.toString();
            org.junit.Assert.fail("testParentReplace_literalMutationString13286 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template repl{ace.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4348_failAssert0_add8361_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("[P:7%^}i]z+[ }37;P#");
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
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString4348 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4348_failAssert0_add8361 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [P:7%^}i]z+[ }37;P# not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4348_failAssert0_add8362_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("[P:7%^}i]z+[ }37;P#");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString4348 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4348_failAssert0_add8362 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [P:7%^}i]z+[ }37;P# not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4363_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("xY!U Mnzy]F=XGv:S[7");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4363 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template xY!U Mnzy]F=XGv:S[7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4348_failAssert0_literalMutationString6080_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("[P:7%^}i]z+[ }37;P#");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString4348 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4348_failAssert0_literalMutationString6080 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [P:7%^}i]z+[ }37;P# not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4348_failAssert0_literalMutationString6076_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("[P:7%^}i]z+[ }37;P#");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "su(blockchild1.txt");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString4348 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4348_failAssert0_literalMutationString6076 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [P:7%^}i]z+[ }37;P# not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4380_failAssert0_literalMutationString5962_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("YSQ<%i]UcP} f7z[WF&");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subbxlockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString4380 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4380_failAssert0_literalMutationString5962 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template YSQ<%i]UcP} f7z[WF& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4375_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            m = c.compile("Pzy;g*,mdIER3X(]F0T");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4375 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Pzy;g*,mdIER3X(]F0T not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4364_failAssert0_literalMutationString6987_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Q$)pghd)#N<I3Z$x*Z(");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("subblockchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString4364 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4364_failAssert0_literalMutationString6987 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Q$)pghd)#N<I3Z$x*Z( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4348_failAssert0null9244_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("[P:7%^}i]z+[ }37;P#");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString4348 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4348_failAssert0null9244 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [P:7%^}i]z+[ }37;P# not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4363_failAssert0_add8358_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("xY!U Mnzy]F=XGv:S[7");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString4363 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4363_failAssert0_add8358 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template xY!U Mnzy]F=XGv:S[7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4360_failAssert0_literalMutationString6475_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("U/Qi>y_7);Ul3vAR>[A");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("subblockchi)d2.html");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString4360 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4360_failAssert0_literalMutationString6475 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template U/Qi>y_7);Ul3vAR>[A not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4363_failAssert0_literalMutationString6048_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("xY!U Mnzy]F=XGv:S[7");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "sub+blockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString4363 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4363_failAssert0_literalMutationString6048 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template xY!U Mnzy]F=XGv:S[7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4348_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("[P:7%^}i]z+[ }37;P#");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4348 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [P:7%^}i]z+[ }37;P# not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString9708_failAssert0_add12567_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("WL8;M)|9z-3");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString9708 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString9708_failAssert0_add12567 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template WL8;M)|9z-3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString9708_failAssert0_add12566_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("WL8;M)|9z-3");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString9708 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString9708_failAssert0_add12566 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template WL8;M)|9z-3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString9708_failAssert0null13015_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("WL8;M)|9z-3");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(null, scope);
                TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString9708 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString9708_failAssert0null13015 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template WL8;M)|9z-3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString9708_failAssert0_literalMutationString11590_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("WL8;M)|9z-3");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("", "Sam");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString9708 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString9708_failAssert0_literalMutationString11590 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template WL8;M)|9z-3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString9708_failAssert0null13012_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("WL8;M)|9z-3");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", null);
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString9708 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString9708_failAssert0null13012 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template WL8;M)|9z-3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString9708_failAssert0_literalMutationString11596_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("WL8;M)|9z-3");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Tet");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSub_literalMutationString9708 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSub_literalMutationString9708_failAssert0_literalMutationString11596 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template WL8;M)|9z-3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSub_literalMutationString9708_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("WL8;M)|9z-3");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "subsub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSub_literalMutationString9708 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template WL8;M)|9z-3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString26356_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("I<:!_7k!;V6");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("reply", "TestReply");
            scope.put("commands", Arrays.asList("a", "b"));
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "client.txt");
            sw.toString();
            org.junit.Assert.fail("testClientMethod_literalMutationString26356 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template I<:!_7k!;V6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString26356_failAssert0null29589_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("I<:!_7k!;V6");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("reply", "TestReply");
                scope.put("commands", Arrays.asList("a", "b"));
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testClientMethod_literalMutationString26356 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString26356_failAssert0null29589 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template I<:!_7k!;V6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString26356_failAssert0null29587_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("I<:!_7k!;V6");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("reply", "TestReply");
                scope.put("commands", Arrays.asList("a", "b"));
                m.execute(null, scope);
                TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_literalMutationString26356 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString26356_failAssert0null29587 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template I<:!_7k!;V6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString26356_failAssert0_add29183_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("I<:!_7k!;V6");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("reply", "TestReply");
                scope.put("commands", Arrays.asList("a", "b"));
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_literalMutationString26356 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString26356_failAssert0_add29183 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template I<:!_7k!;V6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString26356_failAssert0_add29182_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("I<:!_7k!;V6");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("reply", "TestReply");
                scope.put("commands", Arrays.asList("a", "b"));
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_literalMutationString26356 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString26356_failAssert0_add29182 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template I<:!_7k!;V6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testClientMethod_literalMutationString26356_failAssert0_literalMutationString27984_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("I<:!_7k!;V6");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("r:eply", "TestReply");
                scope.put("commands", Arrays.asList("a", "b"));
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "client.txt");
                sw.toString();
                org.junit.Assert.fail("testClientMethod_literalMutationString26356 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testClientMethod_literalMutationString26356_failAssert0_literalMutationString27984 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template I<:!_7k!;V6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString23998_failAssert0_literalMutationString24701_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("1$Z#6)]iI}hv#`Hkb");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "sbsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString23998 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString23998_failAssert0_literalMutationString24701 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 1$Z#6)]iI}hv#`Hkb not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString23998_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("1$Z#6)]iI}hv#`Hkb");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString23998 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 1$Z#6)]iI}hv#`Hkb not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString24007_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("W(xFP(Ios[:BrU:zU");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching_literalMutationString24007 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template W(xFP(Ios[:BrU:zU not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString24009_failAssert0_literalMutationString24920_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("su%subchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("suJsubchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString24009 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString24009_failAssert0_literalMutationString24920 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template su%subchild1.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCachingnull24033_failAssert0_literalMutationString24527_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsu]bchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile(null);
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCachingnull24033 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSubSubCachingnull24033_failAssert0_literalMutationString24527 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsu]bchild1.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching_literalMutationString23998_failAssert0_add25678_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("1$Z#6)]iI}hv#`Hkb");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("subsubchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild2.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching_literalMutationString23998 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching_literalMutationString23998_failAssert0_add25678 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 1$Z#6)]iI}hv#`Hkb not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString18864_failAssert0_literalMutationString19483_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("4@Mf(`I&4z;]grsb[");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubPhild3.txt");
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString18864 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString18864_failAssert0_literalMutationString19483 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4@Mf(`I&4z;]grsb[ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString18864_failAssert0_add20506_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("4@Mf(`I&4z;]grsb[");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString18864 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString18864_failAssert0_add20506 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4@Mf(`I&4z;]grsb[ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString18851_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("ZypfGo]mb.PU(VMZQ");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild3.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString18851 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ZypfGo]mb.PU(VMZQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_remove18880_literalMutationString19255_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("m1|<3Y-JbofOMRozX");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching2_remove18880__7 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_remove18880__11 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("subsubchild3.html");
            sw = new StringWriter();
            Writer o_testSubSubCaching2_remove18880__17 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_remove18880__22 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_remove18880_literalMutationString19255 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template m1|<3Y-JbofOMRozX not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_add18875_literalMutationString19088_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching2_add18875__7 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_add18875__12 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            Mustache o_testSubSubCaching2_add18875__14 = c.compile("subsubchild3{html");
            m = c.compile("subsubchild3.html");
            sw = new StringWriter();
            Writer o_testSubSubCaching2_add18875__19 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_add18875__24 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_add18875_literalMutationString19088 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subsubchild3{html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_add18872_literalMutationString19142_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubSubCaching2_add18872__7 = m.execute(sw, new Object() {});
            Writer o_testSubSubCaching2_add18872__12 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_add18872__17 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("j4DaT40%8(:OnEdGb");
            sw = new StringWriter();
            Writer o_testSubSubCaching2_add18872__23 = m.execute(sw, new Object() {});
            String o_testSubSubCaching2_add18872__28 = TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_add18872_literalMutationString19142 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template j4DaT40%8(:OnEdGb not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString18864_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subsubchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
            sw.toString();
            m = c.compile("4@Mf(`I&4z;]grsb[");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subsubchild3.txt");
            sw.toString();
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString18864 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4@Mf(`I&4z;]grsb[ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubSubCaching2_literalMutationString18864_failAssert0null20876_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subsubchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subsubchild1.txt");
                sw.toString();
                m = c.compile("4@Mf(`I&4z;]grsb[");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testSubSubCaching2_literalMutationString18864 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubSubCaching2_literalMutationString18864_failAssert0null20876 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4@Mf(`I&4z;]grsb[ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString21225_failAssert0() throws IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile(" Eai[Ni)T;VE!,JE7!qI]mi");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
            sw.toString();
            org.junit.Assert.fail("testNested_literalMutationString21225 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  Eai[Ni)T;VE!,JE7!qI]mi not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString21225_failAssert0_add21712_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(" Eai[Ni)T;VE!,JE7!qI]mi");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString21225 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString21225_failAssert0_add21712 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  Eai[Ni)T;VE!,JE7!qI]mi not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString21223_failAssert0_literalMutationString21434_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("nested<_inhe)ritance.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString21223 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString21223_failAssert0_literalMutationString21434 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nested<_inhe)ritance.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString21225_failAssert0null21799_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(" Eai[Ni)T;VE!,JE7!qI]mi");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString21225 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString21225_failAssert0null21799 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  Eai[Ni)T;VE!,JE7!qI]mi not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString21225_failAssert0_add21711_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(" Eai[Ni)T;VE!,JE7!qI]mi");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "nested_inheritance.txt");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString21225 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString21225_failAssert0_add21711 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  Eai[Ni)T;VE!,JE7!qI]mi not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString21225_failAssert0_literalMutationString21475_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(" Eai[Ni)T;VE!,JE7!qI]mi");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "&[E0{G`yFzCD1Z1lHG5k45");
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString21225 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString21225_failAssert0_literalMutationString21475 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  Eai[Ni)T;VE!,JE7!qI]mi not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNested_literalMutationString21225_failAssert0null21800_failAssert0() throws IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(" Eai[Ni)T;VE!,JE7!qI]mi");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testNested_literalMutationString21225 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testNested_literalMutationString21225_failAssert0null21800 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  Eai[Ni)T;VE!,JE7!qI]mi not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplExtensionTest.root = (new File(file, "sub.html").exists()) ? file : new File("src/test/resources");
    }
}

