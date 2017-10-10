

package com.github.mustachejava;


/**
 * Specification tests
 */
public class AmplSpecTest {
    @org.junit.Test
    public void interpolations() throws java.io.IOException {
        run(getSpec("interpolation.yml"));
    }

    @org.junit.Test
    public void sections() throws java.io.IOException {
        run(getSpec("sections.yml"));
    }

    @org.junit.Test
    public void delimiters() throws java.io.IOException {
        run(getSpec("delimiters.yml"));
    }

    @org.junit.Test
    public void inverted() throws java.io.IOException {
        run(getSpec("inverted.yml"));
    }

    @org.junit.Test
    public void partials() throws java.io.IOException {
        run(getSpec("partials.yml"));
    }

    @org.junit.Test
    public void lambdas() throws java.io.IOException {
        run(getSpec("~lambdas.yml"));
    }

    // @Test — need this to appear in the spec repository to enable
    public void inheritance() throws java.io.IOException {
        run(getSpec("inheritance.yml"));
    }

    private void run(com.fasterxml.jackson.databind.JsonNode spec) {
        int fail = 0;
        int success = 0;
        int whitespace = 0;
        java.util.Map<java.lang.String, java.lang.Object> functionMap = new java.util.HashMap<java.lang.String, java.lang.Object>() {
            {
                put("Interpolation", new java.lang.Object() {
                    java.util.function.Function lambda() {
                        return ( input) -> "world";
                    }
                });
                put("Interpolation - Expansion", new java.lang.Object() {
                    java.util.function.Function lambda() {
                        return ( input) -> "{{planet}}";
                    }
                });
                put("Interpolation - Alternate Delimiters", new java.lang.Object() {
                    java.util.function.Function lambda() {
                        return ( input) -> "|planet| => {{planet}}";
                    }
                });
                put("Interpolation - Multiple Calls", new java.lang.Object() {
                    int calls = 0;

                    java.util.function.Function lambda() {
                        return ( input) -> java.lang.String.valueOf((++(calls)));
                    }
                });
                put("Escaping", new java.lang.Object() {
                    java.util.function.Function lambda() {
                        return ( input) -> ">";
                    }
                });
                put("Section", new java.lang.Object() {
                    java.util.function.Function lambda() {
                        return new com.github.mustachejava.TemplateFunction() {
                            @java.lang.Override
                            public java.lang.String apply(java.lang.String input) {
                                return input.equals("{{x}}") ? "yes" : "no";
                            }
                        };
                    }
                });
                put("Section - Expansion", new java.lang.Object() {
                    java.util.function.Function lambda() {
                        return new com.github.mustachejava.TemplateFunction() {
                            @java.lang.Override
                            public java.lang.String apply(java.lang.String input) {
                                return (input + "{{planet}}") + input;
                            }
                        };
                    }
                });
                put("Section - Alternate Delimiters", new java.lang.Object() {
                    java.util.function.Function lambda() {
                        return new com.github.mustachejava.TemplateFunction() {
                            @java.lang.Override
                            public java.lang.String apply(java.lang.String input) {
                                return (input + "{{planet}} => |planet|") + input;
                            }
                        };
                    }
                });
                put("Section - Multiple Calls", new java.lang.Object() {
                    java.util.function.Function lambda() {
                        return new java.util.function.Function<java.lang.String, java.lang.String>() {
                            @java.lang.Override
                            public java.lang.String apply(java.lang.String input) {
                                return ("__" + input) + "__";
                            }
                        };
                    }
                });
                put("Inverted Section", new java.lang.Object() {
                    java.util.function.Function lambda() {
                        return ( input) -> false;
                    }
                });
            }
        };
        for (final com.fasterxml.jackson.databind.JsonNode test : spec.get("tests")) {
            boolean failed = false;
            final com.github.mustachejava.DefaultMustacheFactory CF = createMustacheFactory(test);
            java.lang.String file = test.get("name").asText();
            java.lang.System.out.print(((("Running " + file) + " - ") + (test.get("desc").asText())));
            java.io.StringReader template = new java.io.StringReader(test.get("template").asText());
            com.fasterxml.jackson.databind.JsonNode data = test.get("data");
            try {
                com.github.mustachejava.Mustache compile = CF.compile(template, file);
                java.io.StringWriter writer = new java.io.StringWriter();
                compile.execute(writer, new java.lang.Object[]{ new com.fasterxml.jackson.databind.ObjectMapper().readValue(data.toString(), java.util.Map.class) , functionMap.get(file) });
                java.lang.String expected = test.get("expected").asText();
                if (writer.toString().replaceAll("\\s+", "").equals(expected.replaceAll("\\s+", ""))) {
                    java.lang.System.out.print(": success");
                    if (writer.toString().equals(expected)) {
                        java.lang.System.out.println("!");
                    }else {
                        whitespace++;
                        java.lang.System.out.println(", whitespace differences.");
                    }
                }else {
                    java.lang.System.out.println(": failed!");
                    java.lang.System.out.println(((expected + " != ") + (writer.toString())));
                    java.lang.System.out.println(test);
                    failed = true;
                }
            } catch (java.lang.Throwable e) {
                java.lang.System.out.println(": exception");
                e.printStackTrace();
                java.lang.System.out.println(test);
                failed = true;
            }
            if (failed)
                fail++;
            else
                success++;
            
        }
        java.lang.System.out.println(((((("Success: " + success) + " Whitespace: ") + whitespace) + " Fail: ") + fail));
        junit.framework.Assert.assertFalse((fail > 0));
    }

    protected com.github.mustachejava.DefaultMustacheFactory createMustacheFactory(final com.fasterxml.jackson.databind.JsonNode test) {
        return new com.github.mustachejava.DefaultMustacheFactory("/spec/specs") {
            @java.lang.Override
            public java.io.Reader getReader(java.lang.String resourceName) {
                com.fasterxml.jackson.databind.JsonNode partial = test.get("partials").get(resourceName);
                return new java.io.StringReader((partial == null ? "" : partial.asText()));
            }
        };
    }

    private com.fasterxml.jackson.databind.JsonNode getSpec(java.lang.String spec) throws java.io.IOException {
        return new com.fasterxml.jackson.dataformat.yaml.YAMLFactory(new com.fasterxml.jackson.dataformat.yaml.YAMLMapper()).createParser(new java.io.InputStreamReader(com.github.mustachejava.AmplSpecTest.class.getResourceAsStream(("/spec/specs/" + spec)))).readValueAsTree();
    }
}

