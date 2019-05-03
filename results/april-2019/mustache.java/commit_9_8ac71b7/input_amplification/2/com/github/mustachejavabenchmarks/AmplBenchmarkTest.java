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
    public void should_handle_more_than_one_level_of_partial_nesting_add14null705_failAssert0() throws java.lang.Exception {
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
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14null705 should have thrown MustacheException");
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
    public void should_handle_more_than_one_level_of_partial_nesting_add14null704_failAssert0() throws java.lang.Exception {
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
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add14null704 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString6null700_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("2gjtvlDf", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString6null700 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add631_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Arrays.asList("w00pw00p", "mustache rocks");
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add631 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null702_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("300pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString3null702 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null701_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "page1.txt");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString9null701 should have thrown MustacheException");
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
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null692_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pwg00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString4null692 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add16null708_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.List<java.lang.String> o_should_handle_more_than_one_level_of_partial_nesting_add16__7 = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add16null708 should have thrown MustacheException");
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
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w?0pw00p", "mustache rocks");
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
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString354_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00w00p", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString354 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString353_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("page1.txt", "mustache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString353 should have thrown MustacheException");
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
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null698_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw0p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString5null698 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null696_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString7null696 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString8null695_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache 9rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString8null695 should have thrown MustacheException");
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
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "QJ=Z!^`s,dmm(l");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString10null699 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString2null697_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("page1.txt", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString2null697 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString359_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mstache rocks");
                }).close();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_literalMutationString359 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString11null703_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustach rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString11null703 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null693_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache roqks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_literalMutationString12null693 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nesting_add13null711_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache o_should_handle_more_than_one_level_of_partial_nesting_add13__3 = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add13null711 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            org.junit.Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add632_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
                com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
                java.io.StringWriter sw = new java.io.StringWriter();
                maven.execute(null, new java.lang.Object() {
                    java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
                }).close();
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
            }
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0_add632 should have thrown MustacheException");
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
    public void should_handle_more_than_one_level_of_partial_nesting_add17null712_failAssert0() throws java.lang.Exception {
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(null, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nesting_add17null712 should have thrown MustacheException");
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

