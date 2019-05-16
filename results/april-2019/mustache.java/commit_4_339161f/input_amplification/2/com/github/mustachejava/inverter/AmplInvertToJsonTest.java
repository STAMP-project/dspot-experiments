package com.github.mustachejava.inverter;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheNotFoundException;
import com.github.mustachejava.util.Node;
import com.github.mustachejava.util.NodeValue;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class AmplInvertToJsonTest extends InvertUtils {
    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0null703_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("tDn`z+z/suL*:=`&X");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0null703 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString5_failAssert0null736_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(null);
                org.junit.Assert.fail("testToJson3_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString5_failAssert0null736 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString5_failAssert0_add592_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                getPath("src/test/resources/psauxwww.txt");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString5_failAssert0_add592 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString5_failAssert0_literalMutationString291_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString5_failAssert0_literalMutationString291 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_add19_literalMutationString113_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache o_testToJson3_add19__3 = dmf.compile("psauxwww.m`stache");
            Mustache compile = dmf.compile("psauxwww.mustache");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_add19_literalMutationString113 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template psauxwww.m`stache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_add560_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("tDn`z+z/suL*:=`&X");
                Path file = getPath("src/test/resources/psauxwww.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_add560 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString6_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("psauxwww.m%stache");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_literalMutationString6 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template psauxwww.m%stache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString5_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_literalMutationString5 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString6_failAssert0_literalMutationString433_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("pauxwww.m%stache");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString6_failAssert0_literalMutationString433 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template pauxwww.m%stache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("tDn`z+z/suL*:=`&X");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testToJson3null29_failAssert0_literalMutationString512_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(null);
                org.junit.Assert.fail("testToJson3null29 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson3null29_failAssert0_literalMutationString512 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3null29_failAssert0_literalMutationString514_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("Dwz]&j,4H{I>X,_T ");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(null);
                org.junit.Assert.fail("testToJson3null29 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson3null29_failAssert0_literalMutationString514 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Dwz]&j,4H{I>X,_T  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString6_failAssert0_add642_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("psauxwww.m%stache");
                Mustache compile = dmf.compile("psauxwww.m%stache");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString6_failAssert0_add642 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template psauxwww.m%stache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_literalMutationString173_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_literalMutationString173 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    private void output(Node invert) throws IOException {
        MappingJsonFactory jf = new MappingJsonFactory();
        StringWriter out = new StringWriter();
        JsonGenerator jg = jf.createJsonGenerator(out);
        writeNode(jg, invert);
        jg.flush();
        System.out.println(out.toString());
    }

    private void writeNode(final JsonGenerator jg, Node invert) throws IOException {
        jg.writeStartObject();
        for (final Map.Entry<String, NodeValue> entry : invert.entrySet()) {
            NodeValue nodeValue = entry.getValue();
            if ((nodeValue.isList()) && ((nodeValue.list().size()) > 0)) {
                jg.writeFieldName(entry.getKey());
                List<Node> list = nodeValue.list();
                boolean array = (list.size()) > 1;
                if (array) {
                    jg.writeStartArray();
                }
                for (Node node : list) {
                    writeNode(jg, node);
                }
                if (array) {
                    jg.writeEndArray();
                }
            } else {
                String value = nodeValue.value();
                if (value != null) {
                    jg.writeFieldName(entry.getKey());
                    try {
                        double v = Double.parseDouble(value);
                        jg.writeNumber(v);
                    } catch (NumberFormatException e) {
                        jg.writeString(value);
                    }
                }
            }
        }
        jg.writeEndObject();
    }
}

