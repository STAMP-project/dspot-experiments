package com.github.mustachejava;


import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public final class AmplAbsolutePartialReferenceTest {
    private static final String TEMPLATE_FILE = "absolute_partials_template.html";

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2_failAssert0_literalMutationString495_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("|d\t]+");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2_failAssert0_literalMutationString495 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString477_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("[d\t]+");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString477 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString476_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("@$zdPR]/s");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString476 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2_failAssert0_literalMutationString497_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("[d\t]+");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("[d\t]+", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2_failAssert0_literalMutationString497 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_remove24_failAssert0_literalMutationString227_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("xD3E4}&I=");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_remove24 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_remove24_failAssert0_literalMutationString227 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_remove24_failAssert0_literalMutationString225_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("[d\t]+");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_remove24 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_remove24_failAssert0_literalMutationString225 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_literalMutationString375_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("[d\t]+");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_literalMutationString375 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_remove25_failAssert0_literalMutationString234_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("temp{ates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_remove25 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_remove25_failAssert0_literalMutationString234 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2_failAssert0_add700_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("[d\t]+");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2_failAssert0_add700 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString11_failAssert0_literalMutationString511_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("}[]4,X[Oc");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w40pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString11_failAssert0_literalMutationString511 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_literalMutationString392_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("[d\t]+");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_literalMutationString392 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0null737_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("#emplates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0null737 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory("[d\t]+");
            Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString14_failAssert0_literalMutationString306_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("templa>es");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "[d\t]+");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString14 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString14_failAssert0_literalMutationString306 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_literalMutationString358_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("?emplates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_literalMutationString358 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2_failAssert0_add699_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("[d\t]+");
                factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2_failAssert0_add699 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_literalMutationString370_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("#emplates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustahe rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_literalMutationString370 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_add655_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("#emplates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_add655 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_add652_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("#emplates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_add652 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_add653_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("#emplates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_add653 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString9_failAssert0_literalMutationString285_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("[d\t]+");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw0T0p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString9 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString9_failAssert0_literalMutationString285 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2_failAssert0null745_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("[d\t]+");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2_failAssert0null745 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory("#emplates");
            Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_literalMutationString364_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("#emplates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("vu}v.V$Y", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_literalMutationString364 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2_failAssert0_add702_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("[d\t]+");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Arrays.asList("w00pw00p", "mustache rocks");
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString2_failAssert0_add702 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }
}

