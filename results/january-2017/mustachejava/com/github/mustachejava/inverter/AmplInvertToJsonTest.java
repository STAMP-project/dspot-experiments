

package com.github.mustachejava.inverter;


public class AmplInvertToJsonTest extends com.github.mustachejava.inverter.InvertUtils {
    @org.junit.Test
    public void testToJson() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache compile = dmf.compile("fdbcli.mustache");
        java.nio.file.Path file = getPath("src/test/resources/fdbcli.txt");
        java.lang.String txt = new java.lang.String(java.nio.file.Files.readAllBytes(file), "UTF-8");
        com.github.mustachejava.util.Node invert = compile.invert(txt);
        output(invert);
    }

    @org.junit.Test
    public void testToJson2() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache compile = dmf.compile("fdbcli2.mustache");
        java.nio.file.Path file = getPath("src/test/resources/fdbcli2.txt");
        java.lang.String txt = new java.lang.String(java.nio.file.Files.readAllBytes(file), "UTF-8");
        com.github.mustachejava.util.Node invert = compile.invert(txt);
        output(invert);
    }

    @org.junit.Test
    public void testToJson3() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache compile = dmf.compile("psauxwww.mustache");
        java.nio.file.Path file = getPath("src/test/resources/psauxwww.txt");
        java.lang.String txt = new java.lang.String(java.nio.file.Files.readAllBytes(file), "UTF-8");
        com.github.mustachejava.util.Node invert = compile.invert(txt);
        output(invert);
    }

    @org.junit.Test
    public void testToJson4() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache compile = dmf.compile("fdbcli2.mustache");
        java.nio.file.Path file = getPath("src/test/resources/fdbcli3.txt");
        java.lang.String txt = new java.lang.String(java.nio.file.Files.readAllBytes(file), "UTF-8");
        java.lang.System.out.println("Input text:[");
        java.lang.System.out.print(txt);
        java.lang.System.out.println("]");
        com.github.mustachejava.util.Node invert = compile.invert(txt);
        output(invert);
    }

    @org.junit.Test
    public void testToJson5() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache compile = dmf.compile("fdbcli3.mustache");
        java.nio.file.Path file = getPath("src/test/resources/fdbcli.txt");
        java.lang.String txt = new java.lang.String(java.nio.file.Files.readAllBytes(file), "UTF-8");
        com.github.mustachejava.util.Node invert = compile.invert(txt);
        output(invert);
    }

    private void output(com.github.mustachejava.util.Node invert) throws java.io.IOException {
        com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
        java.io.StringWriter out = new java.io.StringWriter();
        com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(out);
        writeNode(jg, invert);
        jg.flush();
        java.lang.System.out.println(out.toString());
    }

    @org.junit.Test
    public void testDiskstats() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache m = dmf.compile(new java.io.StringReader(("{{#disk}}\n" + ("\\s+[0-9]+\\s+[0-9]+\\s+{{tag_device}} {{reads}} {{reads_merged}} {{sectors_read}} {{read_time}} {{writes}} {{writes_merged}} {{sectors_written}} {{write_time}} {{ios}} {{io_time}} {{weighted_io_time}}\n" + "{{/disk}}"))), "diskstats");
        java.lang.String txt = "  220   100 xvdb 3140 43 23896 216 57698654 45893891 1261011016 12232816 0 10994276 12222124\n" + "  220   100  xvdk 2417241 93 19338786 1287328 284969078 116717514 10144866416 1520589288 0 329180460 1521686240\n";
        com.github.mustachejava.util.Node invert = m.invert(txt);
        output(invert);
    }

    private void writeNode(final com.fasterxml.jackson.core.JsonGenerator jg, com.github.mustachejava.util.Node invert) throws java.io.IOException {
        jg.writeStartObject();
        for (final java.util.Map.Entry<java.lang.String, com.github.mustachejava.util.NodeValue> entry : invert.entrySet()) {
            com.github.mustachejava.util.NodeValue nodeValue = entry.getValue();
            if ((nodeValue.isList()) && ((nodeValue.list().size()) > 0)) {
                jg.writeFieldName(entry.getKey());
                java.util.List<com.github.mustachejava.util.Node> list = nodeValue.list();
                boolean array = (list.size()) > 1;
                if (array)
                    jg.writeStartArray();
                
                for (com.github.mustachejava.util.Node node : list) {
                    writeNode(jg, node);
                }
                if (array)
                    jg.writeEndArray();
                
            }else {
                java.lang.String value = nodeValue.value();
                if (value != null) {
                    jg.writeFieldName(entry.getKey());
                    try {
                        double v = java.lang.Double.parseDouble(value);
                        jg.writeNumber(v);
                    } catch (java.lang.NumberFormatException e) {
                        jg.writeString(value);
                    }
                }
            }
        }
        jg.writeEndObject();
    }

    /* amplification of com.github.mustachejava.inverter.InvertToJsonTest#testDiskstats */
    @org.junit.Test
    public void testDiskstats_literalMutation8_failAssert6() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache m = dmf.compile(new java.io.StringReader(("{{#disk}}\n" + ("This is a good day" + "{{/disk}}"))), "diskstats");
            java.lang.String txt = "  220   100 xvdb 3140 43 23896 216 57698654 45893891 1261011016 12232816 0 10994276 12222124\n" + "  220   100  xvdk 2417241 93 19338786 1287328 284969078 116717514 10144866416 1520589288 0 329180460 1521686240\n";
            com.github.mustachejava.util.Node invert = m.invert(txt);
            output(invert);
            org.junit.Assert.fail("testDiskstats_literalMutation8 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

