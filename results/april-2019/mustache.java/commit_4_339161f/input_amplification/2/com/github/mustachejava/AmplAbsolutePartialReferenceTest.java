package com.github.mustachejava;


import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public final class AmplAbsolutePartialReferenceTest {
    private static final String TEMPLATE_FILE = "absolute_partials_template.html";

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4_failAssert0_add675_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Vj!){RFVm");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Arrays.asList("w00pw00p", "mustache rocks");
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4_failAssert0_add675 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory("t>emplates");
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
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory("Vj!){RFVm");
            Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0_add650_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("t>emplates");
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
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0_add650 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4_failAssert0_literalMutationString422_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Vj!){RFVm");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("page1.txt", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4_failAssert0_literalMutationString422 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_literalMutationString486_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(" does not exist");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_literalMutationString486 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0null738_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("t>emplates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0null738 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString12_failAssert0_literalMutationString242_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("1.]:P6x;k");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("a00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString12 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString12_failAssert0_literalMutationString242 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0_literalMutationString361_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("t>emplates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "page1.txt");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString6_failAssert0_literalMutationString361 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4_failAssert0null742_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Vj!){RFVm");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString4_failAssert0null742 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_remove24_failAssert0_literalMutationString523_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("=s==Yn6{x");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_remove24 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_remove24_failAssert0_literalMutationString523 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }
}

