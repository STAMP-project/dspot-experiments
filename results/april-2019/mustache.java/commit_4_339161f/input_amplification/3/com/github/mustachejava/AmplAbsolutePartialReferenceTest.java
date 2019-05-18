package com.github.mustachejava;


import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public final class AmplAbsolutePartialReferenceTest {
    private static final String TEMPLATE_FILE = "absolute_partials_template.html";

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null739_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("sg,OI{)+ ");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null739 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0_literalMutationString234_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("temp[ates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "page1.txt");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0_literalMutationString234 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory("temp[ates");
            Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory("sg,OI{)+ ");
            Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_literalMutationString380_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("sg,OI{)+ ");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rCcks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_literalMutationString380 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add659_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("sg,OI{)+ ");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add659 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0null731_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("temp[ates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0null731 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString14_failAssert0_add601_failAssert0_literalMutationString1188_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("te|plates");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "");
                    }).close();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString14 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString14_failAssert0_add601 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString14_failAssert0_add601_failAssert0_literalMutationString1188 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4_failAssert0_literalMutationString274_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("())[_>-n");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4_failAssert0_literalMutationString274 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0_add608_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("temp[ates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0_add608 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_remove24_failAssert0_literalMutationString543_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("8S;O&]mwu");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_remove24 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_remove24_failAssert0_literalMutationString543 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }
}

