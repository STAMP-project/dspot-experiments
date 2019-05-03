package com.github.mustachejavabenchmarks;


public class AmplBenchmarkTest extends junit.framework.TestCase {
    private static final int TIME = 2000;

    protected java.io.File root;

    protected void setUp() throws java.lang.Exception {
        super.setUp();
        java.io.File file = new java.io.File("src/test/resources");
        root = (new java.io.File(file, "simple.html").exists()) ? file : new java.io.File("../src/test/resources");
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString351_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("fY@Vl]nY", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString351 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add16_add536null6477_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            maven.getName();
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.List<java.lang.String> o_should_handle_more_than_one_level_of_partial_nesting_add16__7 = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add16_add536null6477 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add17null704_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add17null704 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_add494null6386_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache o_should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_add494__3 = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "page1.txt");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_add494null6386 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString11_remove653null6410_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "{:a0L]),N$ I4K");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString11_remove653null6410 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString11_remove654null6409_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "{:a0L]),N$ I4K");
            }).close();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString11_remove654null6409 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null700_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null700 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_literalMutationString204null6436_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "page1.xt");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_literalMutationString204null6436 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null701_failAssert0_literalMutationString3425_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "@F2#niPhnp!Nu");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null701 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null701_failAssert0_literalMutationString3425 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add13_remove684null6462_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache o_should_handle_more_than_one_level_of_partial_nesting_add13__3 = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13_remove684null6462 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add13_literalMutationString306null6431_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache o_should_handle_more_than_one_level_of_partial_nesting_add13__3 = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("page1.txt", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13_literalMutationString306null6431 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null700_failAssert0_add5818_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "");
                }).close();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null700 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null700_failAssert0_add5818 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_remove665null6412_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "page1.txt");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_remove665null6412 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null701_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustche rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null701 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString6_remove639null6469_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w0pw00p", "mustache rocks");
            }).close();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString6_remove639null6469 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add629_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add629 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString355_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00opw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString355 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null695_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("U00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null695 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_remove19null715_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_remove19null715 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add628_failAssert0_literalMutationString3663_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                    factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                    com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                    java.io.StringWriter sw = new java.io.StringWriter();
                    maven.execute(null, new java.lang.Object() {
                        java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
                }
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add628 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add628_failAssert0_literalMutationString3663 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add16null705null6424_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.List<java.lang.String> o_should_handle_more_than_one_level_of_partial_nesting_add16__7 = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add16null705null6424 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null695_failAssert0_literalMutationString3456_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("U00pw00p", "");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null695 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null695_failAssert0_literalMutationString3456 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add16null706null6323_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.List<java.lang.String> o_should_handle_more_than_one_level_of_partial_nesting_add16__7 = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add16null706null6323 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null694_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustachc rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null694 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null696_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw_00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null696 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add628_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add628 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null697_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("page1.txt", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null697 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null695_failAssert0_add5810_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("U00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null695 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null695_failAssert0_add5810 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_add498null6451_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_add498__7 = maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "page1.txt");
            });
            maven.execute(sw, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "page1.txt");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_add498null6451 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString12_literalMutationString192null6435_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustc_e rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString12_literalMutationString192null6435 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_add498null6452_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_add498__7 = maven.execute(sw, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "page1.txt");
            });
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "page1.txt");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_add498null6452 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString9_literalMutationString109null6341_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w0pw00p", "mustachc rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9_literalMutationString109null6341 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString9_literalMutationString111null6433_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "page1.txt");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9_literalMutationString111null6433 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add633_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add633 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null694_failAssert0_literalMutationString3401_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustachcirocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null694 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null694_failAssert0_literalMutationString3401 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_remove666null6465_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "page1.txt");
            }).close();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_remove666null6465 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString361_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocs");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString361 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null714_failAssert0_add5738_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(sw, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null714 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null714_failAssert0_add5738 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null700_failAssert0_literalMutationString3478_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList(",@QbamNa", "");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null700 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null700_failAssert0_literalMutationString3478 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString6_remove638null6418_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w0pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString6_remove638null6418 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString8null702_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "page1.txt");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString8null702 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null696_failAssert0_add5814_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Arrays.asList("w00pw_00p", "mustache rocks");
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw_00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null696 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null696_failAssert0_add5814 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_remove19null715_failAssert0_literalMutationString3267_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache Aocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_remove19null715 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_remove19null715_failAssert0_literalMutationString3267 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add14null711_failAssert0_add5748_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(sw, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14null711 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14null711_failAssert0_add5748 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null714_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(sw, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            });
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null714 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString6_literalMutationString96null6357_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("wqpw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString6_literalMutationString96null6357 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null700_failAssert0_add5822_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null700 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null700_failAssert0_add5822 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_literalMutationString205null6346_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "@CSk2H57l");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_literalMutationString205null6346 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add16null707_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.List<java.lang.String> o_should_handle_more_than_one_level_of_partial_nesting_add16__7 = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add16null707 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add630_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add630 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add16_add548null6443_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.List<java.lang.String> o_should_handle_more_than_one_level_of_partial_nesting_add16__7 = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            boolean o_should_handle_more_than_one_level_of_partial_nesting_add16_add548__18 = o_should_handle_more_than_one_level_of_partial_nesting_add16__7.contains("w00pw00p");
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add16_add548null6443 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add628_failAssert0_add5905_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                    factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                    com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Arrays.asList("w00pw00p", "mustache rocks");
                    maven.execute(null, new java.lang.Object() {
                        java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
                }
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add628 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add628_failAssert0_add5905 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add629_failAssert0_add5859_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                    factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                    com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                    java.io.StringWriter sw = new java.io.StringWriter();
                    maven.execute(null, new java.lang.Object() {
                        java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    maven.execute(null, new java.lang.Object() {
                        java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
                }
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add629 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add629_failAssert0_add5859 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString4_remove648null6415_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw_00p", "mustache rocks");
            }).close();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString4_remove648null6415 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString2null692_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("8xIL6h+a", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString2null692 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add13_literalMutationString310null6337_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache o_should_handle_more_than_one_level_of_partial_nesting_add13__3 = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "musta;he rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13_literalMutationString310null6337 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null701_failAssert0_add5792_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustche rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null701 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null701_failAssert0_add5792 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString6null693_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w0pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString6null693 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null694_failAssert0_add5778_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Arrays.asList("w00pw00p", "mustachc rocks");
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustachc rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null694 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null694_failAssert0_add5778 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add14_remove680null6460_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            maven.execute(sw, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14_remove680null6460 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null696_failAssert0_literalMutationString3467_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00p_00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null696 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null696_failAssert0_literalMutationString3467 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add14_remove680null6461_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(sw, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14_remove680null6461 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add13_literalMutationString309null6336_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache o_should_handle_more_than_one_level_of_partial_nesting_add13__3 = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13_literalMutationString309null6336 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_remove19null715_failAssert0_add5723_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_remove19null715 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_remove19null715_failAssert0_add5723 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null714_failAssert0_literalMutationString3314_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(sw, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "&1(mH@a/[ivX0a");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null714 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null714_failAssert0_literalMutationString3314 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString6_literalMutationString103null6356_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w0pw00p", "mustac9he rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString6_literalMutationString103null6356 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add13_remove683null6405_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache o_should_handle_more_than_one_level_of_partial_nesting_add13__3 = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13_remove683null6405 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString11null698_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "{:a0L]),N$ I4K");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString11null698 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString10null699_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocmks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString10null699 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString7_remove660null6417_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "");
            }).close();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString7_remove660null6417 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString6_add377null6393_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache o_should_handle_more_than_one_level_of_partial_nesting_literalMutationString6_add377__3 = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w0pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString6_add377null6393 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add14null710_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            maven.execute(sw, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14null710 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add13null712_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache o_should_handle_more_than_one_level_of_partial_nesting_add13__3 = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13null712 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add14null711_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(sw, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14null711 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_add496null6385_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            maven.getName();
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "page1.txt");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString8_add496null6385 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString3_literalMutationString150null6360_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("page1.txt", "mustach rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString3_literalMutationString150null6360 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add16null708null6423_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.List<java.lang.String> o_should_handle_more_than_one_level_of_partial_nesting_add16__7 = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add16null708null6423 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString5_add409null6389_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("U00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString5_add409null6389 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null697_failAssert0_literalMutationString3514_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("page1.txt", "mustache roks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null697 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null697_failAssert0_literalMutationString3514 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null714_failAssert0null6485_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                });
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null714 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null714_failAssert0null6485 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add15null713_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_should_handle_more_than_one_level_of_partial_nesting_add15__7 = maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            });
            maven.execute(sw, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add15null713 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString4_remove647null6416_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw_00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString4_remove647null6416 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add16null709null6322_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.List<java.lang.String> o_should_handle_more_than_one_level_of_partial_nesting_add16__7 = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add16null709null6322 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add16_add534null6365_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache o_should_handle_more_than_one_level_of_partial_nesting_add16_add534__3 = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.List<java.lang.String> o_should_handle_more_than_one_level_of_partial_nesting_add16__7 = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add16_add534null6365 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add629_failAssert0_literalMutationString3553_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                    com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                    java.io.StringWriter sw = new java.io.StringWriter();
                    maven.execute(null, new java.lang.Object() {
                        java.util.List<java.lang.String> messages = java.util.Arrays.asList("page1.txt", "mustache rocks");
                    }).close();
                    maven.execute(null, new java.lang.Object() {
                        java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                    }).close();
                    sw.toString();
                    org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
                }
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add629 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add629_failAssert0_literalMutationString3553 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    public static boolean skip() {
        return (java.lang.System.getenv().containsKey("CI")) || ((java.lang.System.getProperty("CI")) != null);
    }

    protected com.github.mustachejava.DefaultMustacheFactory createMustacheFactory() {
        return new com.github.mustachejava.DefaultMustacheFactory();
    }

    private java.io.Writer complextest(com.github.mustachejava.Mustache m, java.lang.Object complexObject) throws com.github.mustachejava.MustacheException, java.io.IOException {
        java.io.Writer sw = new com.github.mustachejavabenchmarks.NullWriter();
        m.execute(sw, complexObject).close();
        return sw;
    }

    public static void main(java.lang.String[] args) throws java.lang.Exception {
        com.github.mustachejavabenchmarks.AmplBenchmarkTest benchmarkTest = new com.github.mustachejavabenchmarks.AmplBenchmarkTest();
        benchmarkTest.setUp();
        benchmarkTest.testComplex();
        benchmarkTest.testParallelComplex();
        benchmarkTest.testParallelComplexNoExecutor();
        java.lang.System.exit(0);
    }
}

