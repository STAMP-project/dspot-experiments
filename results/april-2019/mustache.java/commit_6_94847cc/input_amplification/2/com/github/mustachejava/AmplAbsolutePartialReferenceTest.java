package com.github.mustachejava;


import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public final class AmplAbsolutePartialReferenceTest {
    private static final String TEMPLATE_FILE = "absolute_partials_template.html";

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_literalMutationString290_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("8hudn]qyq");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pwH0p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_literalMutationString290 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0null733_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("8hudn]qyq");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0null733 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory("8hudn]qyq");
            Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add630_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("8hudn]qyq");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Arrays.asList("w00pw00p", "mustache rocks");
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add630 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_remove24_failAssert0_literalMutationString224_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("tem}lates");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_remove24 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_remove24_failAssert0_literalMutationString224 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }
}

