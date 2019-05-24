package com.github.mustachejavabenchmarks;


public class AmplJsonInterpreterTest extends junit.framework.TestCase {
    @org.junit.Test(timeout = 10000)
    public void testParser_literalMutationString6_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache compile = dmf.compile("/>6KZ1V{@4A3@9s");
            java.nio.file.Path file = getPath("src/test/resources/fdbcli.txt");
            java.lang.String txt = new java.lang.String(java.nio.file.Files.readAllBytes(file), "UTF-8");
            com.github.mustachejava.util.Node invert = compile.invert(txt);
            java.lang.System.out.println(invert);
            org.junit.Assert.fail("testParser_literalMutationString6 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testParser_literalMutationString4_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache compile = dmf.compile("This is a good day");
            java.nio.file.Path file = getPath("src/test/resources/fdbcli.txt");
            java.lang.String txt = new java.lang.String(java.nio.file.Files.readAllBytes(file), "UTF-8");
            com.github.mustachejava.util.Node invert = compile.invert(txt);
            java.lang.System.out.println(invert);
            org.junit.Assert.fail("testParser_literalMutationString4 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testParser_literalMutationString1_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache compile = dmf.compile("fdbcli.mus tache");
            java.nio.file.Path file = getPath("src/test/resources/fdbcli.txt");
            java.lang.String txt = new java.lang.String(java.nio.file.Files.readAllBytes(file), "UTF-8");
            com.github.mustachejava.util.Node invert = compile.invert(txt);
            java.lang.System.out.println(invert);
            org.junit.Assert.fail("testParser_literalMutationString1 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template fdbcli.mus tache not found", expected.getMessage());
        }
    }

    private static final int TIME = 2;

    protected java.io.File root;

    public static java.lang.Object toObject(final com.fasterxml.jackson.databind.JsonNode node) {
        if (node.isArray()) {
            return new java.util.ArrayList() {
                {
                    for (com.fasterxml.jackson.databind.JsonNode jsonNodes : node) {
                        add(com.github.mustachejavabenchmarks.AmplJsonInterpreterTest.toObject(jsonNodes));
                    }
                }
            };
        } else {
            if (node.isObject()) {
                return new java.util.HashMap() {
                    {
                        for (java.util.Iterator<java.util.Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode>> i = node.fields(); i.hasNext();) {
                            java.util.Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode> next = i.next();
                            java.lang.Object o = com.github.mustachejavabenchmarks.AmplJsonInterpreterTest.toObject(next.getValue());
                            put(next.getKey(), o);
                        }
                    }
                };
            } else {
                if (node.isNull()) {
                    return null;
                } else {
                    return node.asText();
                }
            }
        }
    }

    protected com.github.mustachejava.DefaultMustacheFactory createMustacheFactory() {
        return new com.github.mustachejava.DefaultMustacheFactory(root);
    }

    protected java.lang.Object getScope() throws java.io.IOException {
        com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
        java.io.InputStream json = getClass().getClassLoader().getResourceAsStream("hogan.json");
        final java.util.Map node = ((java.util.Map) (com.github.mustachejavabenchmarks.AmplJsonInterpreterTest.toObject(jf.createJsonParser(json).readValueAsTree())));
        java.lang.System.out.println(node);
        return new java.lang.Object() {
            int uid = 0;

            java.util.List tweets = new java.util.ArrayList() {
                {
                    for (int i = 0; i < 50; i++) {
                        add(node);
                    }
                }
            };
        };
    }

    private com.github.mustachejava.Mustache getMustache() {
        com.github.mustachejava.DefaultMustacheFactory mb = createMustacheFactory();
        return mb.compile("timeline.mustache");
    }

    private void singlethreaded(com.github.mustachejava.Mustache parse, java.lang.Object parent) {
        long start = java.lang.System.currentTimeMillis();
        java.lang.System.out.println(((java.lang.System.currentTimeMillis()) - start));
        start = java.lang.System.currentTimeMillis();
        java.io.StringWriter writer = new java.io.StringWriter();
        parse.execute(writer, parent);
        writer.flush();
        time(parse, parent);
        time(parse, parent);
        time(parse, parent);
        java.lang.System.out.println("timeline.html evaluations:");
        for (int i = 0; i < 2; i++) {
            {
                start = java.lang.System.currentTimeMillis();
                int total = 0;
                while (true) {
                    parse.execute(new com.github.mustachejavabenchmarks.NullWriter(), parent);
                    total++;
                    if (((java.lang.System.currentTimeMillis()) - start) > ((com.github.mustachejavabenchmarks.AmplJsonInterpreterTest.TIME) * 1000)) {
                        break;
                    }
                } 
                java.lang.System.out.println((("NullWriter Serial: " + (total / (com.github.mustachejavabenchmarks.AmplJsonInterpreterTest.TIME))) + "/s"));
            }
            {
                start = java.lang.System.currentTimeMillis();
                int total = 0;
                while (true) {
                    parse.execute(new java.io.StringWriter(), parent);
                    total++;
                    if (((java.lang.System.currentTimeMillis()) - start) > ((com.github.mustachejavabenchmarks.AmplJsonInterpreterTest.TIME) * 1000)) {
                        break;
                    }
                } 
                java.lang.System.out.println((("StringWriter Serial: " + (total / (com.github.mustachejavabenchmarks.AmplJsonInterpreterTest.TIME))) + "/s"));
            }
        }
    }

    private void time(com.github.mustachejava.Mustache parse, java.lang.Object parent) {
        long start;
        start = java.lang.System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
            parse.execute(new java.io.StringWriter(), parent);
        }
        java.lang.System.out.println(((java.lang.System.currentTimeMillis()) - start));
    }

    protected void setUp() throws java.lang.Exception {
        super.setUp();
        java.io.File file = new java.io.File("src/test/resources");
        root = (new java.io.File(file, "simple.html").exists()) ? file : new java.io.File("../src/test/resources");
    }
}

