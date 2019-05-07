package com.github.mustachejava;


import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public final class AmplNestedPartialTest {
    private static final String TEMPLATE_FILE = "nested_partials_template.html";

    private static File root;

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplNestedPartialTest.root = (new File(file, AmplNestedPartialTest.TEMPLATE_FILE).exists()) ? file : new File("src/test/resources");
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00>w00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add17null704_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add17null704 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null702_failAssert0_literalMutationString1973_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw0p", "page1.txt");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null702 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null702_failAssert0_literalMutationString1973 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null702_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "page1.txt");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null702 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null708_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            });
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null708 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add13null705_failAssert0_add3111_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache o_should_handle_more_than_one_level_of_partial_nesting_add13__3 = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13null705 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13null705_failAssert0_add3111 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString10null692_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustacheBrocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString10null692 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null703_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "RTcjxq&BE9YPx[");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null703 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add13null705_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache o_should_handle_more_than_one_level_of_partial_nesting_add13__3 = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13null705 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add13null705_failAssert0_literalMutationString1937_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache o_should_handle_more_than_one_level_of_partial_nesting_add13__3 = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("wS00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13null705 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13null705_failAssert0_literalMutationString1937 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add17null704_failAssert0_literalMutationString1951_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "");
                }).close();
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add17null704 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add17null704_failAssert0_literalMutationString1951 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString6_literalMutationString188null3423_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w0pw00p", "");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString6_literalMutationString188null3423 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null694_failAssert0_add3167_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null694 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null694_failAssert0_add3167 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString10_remove636null3445_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustacheBrocks");
            }).close();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString10_remove636null3445 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null709_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            });
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null709 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null709_failAssert0_literalMutationString2142_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", ",!I8`$ZOwRm&qE");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null709 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null709_failAssert0_literalMutationString2142 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null695_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("page1.txt", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null695 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_remove19null715_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_remove19null715 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null708_failAssert0null3477_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null708 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null708_failAssert0null3477 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add13_literalMutationString232null3413_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache o_should_handle_more_than_one_level_of_partial_nesting_add13__3 = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00p]00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13_literalMutationString232null3413 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null694_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null694 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null699_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("wr0pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null699 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null696_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("QS&k]lVF", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null696 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString8null698_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustace rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString8null698 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350_failAssert0_literalMutationString1861_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                    Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w0>w00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
                }
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350_failAssert0_literalMutationString1861 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350_failAssert0_literalMutationString1872_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                    Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00>w00p", "mf4E!Wkj*LC&OG");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
                }
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350_failAssert0_literalMutationString1872 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString10null692_failAssert0_add3147_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Arrays.asList("w00pw00p", "mustacheBrocks");
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustacheBrocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString10null692 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString10null692_failAssert0_add3147 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString11_literalMutationString99null3415_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString11_literalMutationString99null3415 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString9_literalMutationString205null3416_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "e368G#,3l");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9_literalMutationString205null3416 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString2null700_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw0V0p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString2null700 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add632_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add632 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add633_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add633 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString10null692_failAssert0_literalMutationString2016_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mus[tacheBrocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString10null692 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString10null692_failAssert0_literalMutationString2016 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null709_failAssert0null3479_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null709 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null709_failAssert0null3479 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_remove19null715_failAssert0_add3086_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Arrays.asList("w00pw00p", "mustache rocks");
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_remove19null715 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_remove19null715_failAssert0_add3086 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15_add584null3431_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            });
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            ((StringWriter) (o_should_handle_more_than_one_level_of_partial_nesting_add15__7)).getBuffer().toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15_add584null3431 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15_literalMutationString275null3409_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rMcks");
            });
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15_literalMutationString275null3409 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add14null707_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14null707 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15_add584null3430_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            });
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            ((StringWriter) (o_should_handle_more_than_one_level_of_partial_nesting_add15__7)).getBuffer().toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15_add584null3430 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null695_failAssert0_add3180_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("page1.txt", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null695 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null695_failAssert0_add3180 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_remove19_add617null3428_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            maven.getName();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_remove19_add617null3428 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350_failAssert0_add3079_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                    Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00>w00p", "mustache rocks");
                    });
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00>w00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
                }
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350_failAssert0_add3079 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add16_remove688null3469_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            List<String> o_should_handle_more_than_one_level_of_partial_nesting_add16__7 = Arrays.asList("w00pw00p", "mustache rocks");
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add16_remove688null3469 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add17null704_failAssert0_add3119_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add17null704 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add17null704_failAssert0_add3119 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350_failAssert0_add3078_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                    Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                    StringWriter sw = new StringWriter();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00>w00p", "mustache rocks");
                    }).close();
                    maven.execute(null, new Object() {
                        List<String> messages = Arrays.asList("w00>w00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
                }
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString350_failAssert0_add3078 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add14null706_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14null706 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString6null701_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w0pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString6null701 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null709_failAssert0_add3209_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                });
                Arrays.asList("w00pw00p", "mustache rocks");
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null709 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null709_failAssert0_add3209 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add17null704_failAssert0_literalMutationString1949_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("Pft!ahof", "mustache rocks");
                }).close();
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add17null704 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add17null704_failAssert0_literalMutationString1949 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString11_add379null3434_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            maven.getName();
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache- rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString11_add379null3434 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null708_failAssert0_literalMutationString1905_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustachet rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null708 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null708_failAssert0_literalMutationString1905 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null703_failAssert0_add3142_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "RTcjxq&BE9YPx[");
                }).close();
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null703 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null703_failAssert0_add3142 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add17null704_failAssert0_add3115_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add17null704 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add17null704_failAssert0_add3115 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null702_failAssert0_add3129_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Arrays.asList("w00pw00p", "page1.txt");
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "page1.txt");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null702 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null702_failAssert0_add3129 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add16_add589null3461_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache o_should_handle_more_than_one_level_of_partial_nesting_add16_add589__3 = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            List<String> o_should_handle_more_than_one_level_of_partial_nesting_add16__7 = Arrays.asList("w00pw00p", "mustache rocks");
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add16_add589null3461 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString358_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "page1.txt");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString358 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString11null693_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache- rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString11null693 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add14_literalMutationString248null3412_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustaNhe rocks");
            }).close();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14_literalMutationString248null3412 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add14_literalMutationString248null3411_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustaNhe rocks");
            }).close();
            maven.execute(sw, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14_literalMutationString248null3411 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_remove19null715_failAssert0_literalMutationString1884_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "xustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_remove19null715 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_remove19null715_failAssert0_literalMutationString1884 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null708_failAssert0_add3091_failAssert0() throws Exception {
        try {
            {
                MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
                Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
                StringWriter sw = new StringWriter();
                Arrays.asList("w00pw00p", "mustache rocks");
                Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(null, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(sw, new Object() {
                    List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null708 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null708_failAssert0_add3091 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }
}

