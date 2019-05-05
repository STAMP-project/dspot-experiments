package com.github.mustachejava;


import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public final class AmplAbsolutePartialReferenceTest {
    private static final String TEMPLATE_FILE = "absolute_partials_template.html";

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_add657_failAssert0_literalMutationString3142_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{}Os}IUv`");
                    factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_add657 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_add657_failAssert0_literalMutationString3142 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640_failAssert0_literalMutationString2183_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache roks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640_failAssert0_literalMutationString2183 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643_failAssert0_add6398_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643_failAssert0_add6398 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643_failAssert0_add6394_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    });
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643_failAssert0_add6394 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloadernull26_failAssert0_literalMutationString81_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("@W:+mk ^!");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloadernull26 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloadernull26_failAssert0_literalMutationString81 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194_failAssert0_literalMutationString2360_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    Arrays.asList("WgzUw6vW", "mustache rocks");
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194_failAssert0_literalMutationString2360 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616_failAssert0null6801_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616_failAssert0null6801 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_remove24_failAssert0_literalMutationString227_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("^B}[gpf<f");
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
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644_failAssert0_literalMutationString3311_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BvfO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644_failAssert0_literalMutationString3311 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_literalMutationString258_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00w00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_literalMutationString258 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194_failAssert0_literalMutationString2375_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    Arrays.asList("w00pw00p", "mustache rocks");
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("1<P##yoh", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194_failAssert0_literalMutationString2375 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_literalMutationString248_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(" does not exist");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_literalMutationString248 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString374_failAssert0_add6162_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    Arrays.asList("w00pw00p", "");
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString374 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString374_failAssert0_add6162 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString374_failAssert0null6819_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString374 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString374_failAssert0null6819 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_add19_failAssert0_literalMutationString100_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("te>plates");
                factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add19 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add19_failAssert0_literalMutationString100 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_literalMutationString330_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00Npw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_literalMutationString330 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643_failAssert0null6858_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643_failAssert0null6858 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644_failAssert0_literalMutationString3314_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644_failAssert0_literalMutationString3314 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString16_failAssert0_literalMutationString461_failAssert0null6747_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "QzvB7+19iw{E^1");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString16 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString16_failAssert0_literalMutationString461 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString16_failAssert0_literalMutationString461_failAssert0null6747 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644_failAssert0_add6038_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644_failAssert0_add6038 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617_failAssert0_add5679_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    });
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617_failAssert0_add5679 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Arrays.asList("w00pw00p", "mustache rocks");
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_add23_failAssert0_add590_failAssert0_literalMutationString5142_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("f`aQRu79*");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add23 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add23_failAssert0_add590 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add23_failAssert0_add590_failAssert0_literalMutationString5142 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_add702_failAssert0_literalMutationString2049_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("*3/e){-@:");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    Arrays.asList("w00pw00p", "mustache rocks");
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_add702 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_add702_failAssert0_literalMutationString2049 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617_failAssert0null6736_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    });
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617_failAssert0null6736 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString16_failAssert0_literalMutationString461_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "QzvB7+19iw{E^1");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString16 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString16_failAssert0_literalMutationString461 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_add659_failAssert0_literalMutationString4253_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("b$A Iwde8");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "");
                    });
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_add659 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_add659_failAssert0_literalMutationString4253 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_literalMutationString329_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("Fl#h}h$(", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_literalMutationString329 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735_failAssert0_literalMutationString1544_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735_failAssert0_literalMutationString1544 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0null731_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0null731 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
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
    public void should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194_failAssert0null6745_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    Arrays.asList("w00pw00p", "mustache rocks");
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194_failAssert0null6745 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_remove24_failAssert0_literalMutationString224_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
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

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640_failAssert0null6733_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640_failAssert0null6733 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640_failAssert0_literalMutationString2162_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("PT3|KW#K", "mustache rocks");
                    }).close();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640_failAssert0_literalMutationString2162 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString374_failAssert0_literalMutationString3701_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w:00pw00p", "");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString374 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString374_failAssert0_literalMutationString3701 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString374_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString13_failAssert0_literalMutationString374 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616_failAssert0_literalMutationString3371_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "m[ustache rocks");
                    }).close();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616_failAssert0_literalMutationString3371 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add620_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add620 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640_failAssert0_add5658_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    Arrays.asList("w00pw00p", "mustache rocks");
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640_failAssert0_add5658 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloadernull26_failAssert0_literalMutationString81_failAssert0_add6190_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("@W:+mk ^!");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloadernull26 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloadernull26_failAssert0_literalMutationString81 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloadernull26_failAssert0_literalMutationString81_failAssert0_add6190 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString15_failAssert0_literalMutationString266_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mutache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString15 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString15_failAssert0_literalMutationString266 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloadernull26_failAssert0_literalMutationString81_failAssert0_literalMutationString3785_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("@W:+mk ^!");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "m:stache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloadernull26 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloadernull26_failAssert0_literalMutationString81 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloadernull26_failAssert0_literalMutationString81_failAssert0_literalMutationString3785 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_add19_failAssert0null723_failAssert0_literalMutationString1154_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("t:mplates");
                    factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add19 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add19_failAssert0null723 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add19_failAssert0null723_failAssert0_literalMutationString1154 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString18_failAssert0_literalMutationString513_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mutstache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString18 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString18_failAssert0_literalMutationString513 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617_failAssert0_literalMutationString2243_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    });
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", " M7a7u&2Fd<J+J");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617_failAssert0_literalMutationString2243 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735_failAssert0_add5475_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735_failAssert0_add5475 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735_failAssert0_add5474_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    Arrays.asList("w00pw00p", "mustache rocks");
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735_failAssert0_add5474 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643_failAssert0_literalMutationString4421_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "EO<]-C}|5n+Ow]");
                    }).close();
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643_failAssert0_literalMutationString4421 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644_failAssert0_add6041_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644_failAssert0_add6041 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616_failAssert0_add6057_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    Arrays.asList("w00pw00p", "mustache rocks");
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616_failAssert0_add6057 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617_failAssert0null6737_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    });
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add617_failAssert0null6737 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644_failAssert0null6798_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add644_failAssert0null6798 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString10_failAssert0_literalMutationString435_failAssert0_literalMutationString2486_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("(`dbsn(", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString10 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString10_failAssert0_literalMutationString435 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString10_failAssert0_literalMutationString435_failAssert0_literalMutationString2486 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194_failAssert0_add5721_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    Arrays.asList("w00pw00p", "mustache rocks");
                    Arrays.asList("w00pw00p", "mustache rocks");
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194_failAssert0_add5721 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640_failAssert0null6732_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640_failAssert0null6732 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_literalMutationString496_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("rBP6R&{g#");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString5_failAssert0_literalMutationString496 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643_failAssert0_literalMutationString4413_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("", "mustache rocks");
                    }).close();
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add643_failAssert0_literalMutationString4413 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735_failAssert0_literalMutationString1548_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00w00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0null735_failAssert0_literalMutationString1548 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_literalMutationString328_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00p00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_literalMutationString328 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_add19_failAssert0_literalMutationString99_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("q-{b7dp3;");
                factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add19 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add19_failAssert0_literalMutationString99 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_add599_failAssert0_literalMutationString1748_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    Arrays.asList("w00pw00p", "mustache rocks");
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_add599 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_add599_failAssert0_literalMutationString1748 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_literalMutationString260_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "Template 0L0H2nsj;&E$fjp[A;7} not found");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_literalMutationString260 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616_failAssert0null6802_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString3_failAssert0_add616_failAssert0null6802 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString18_failAssert0_literalMutationString511_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory("]yvf!{Wc$");
                Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mutstache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString18 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString18_failAssert0_literalMutationString511 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194_failAssert0_add5724_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    Arrays.asList("w00pw00p", "mustache rocks");
                    Arrays.asList("w00pw00p", "mustache rocks");
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_add22_failAssert0_literalMutationString194_failAssert0_add5724 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640_failAssert0_add5655_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory("{Go2 BefO");
                    factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    Mustache maven = factory.compile(AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    maven.execute(sw, new Object() {
                        List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutationString1_failAssert0_add640_failAssert0_add5655 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template absolute_partials_template.html not found", expected.getMessage());
        }
    }
}

