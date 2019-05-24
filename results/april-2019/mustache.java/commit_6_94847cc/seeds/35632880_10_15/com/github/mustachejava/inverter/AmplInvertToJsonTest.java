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
    public void testToJson_literalMutationString256_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson_literalMutationString256 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString259_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("/b|^x&ySYxV@3{m");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson_literalMutationString259 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString257_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("fdbcl}i.mustache");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson_literalMutationString257 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcl}i.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString349_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli2.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson2_literalMutationString349 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString348_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("Y5NV0.uaP5`0>s5%");
            Path file = getPath("src/test/resources/fdbcli2.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson2_literalMutationString348 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Y5NV0.uaP5`0>s5% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString2_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_literalMutationString2 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString3_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("Ui3TWYA%,:pd8<Km3");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_literalMutationString3 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Ui3TWYA%,:pd8<Km3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString97_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("Id^A,/^mNhC7S :I");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("Input text:[");
            System.out.print(txt);
            System.out.println("]");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_literalMutationString97 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Id^A,/^mNhC7S :I not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString99_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("Input text:[");
            System.out.print(txt);
            System.out.println("]");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_literalMutationString99 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString441_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("fdbcli3.musta<he");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_literalMutationString441 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli3.musta<he not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString445_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("qC1[EQ3]8O[hN[IY");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_literalMutationString445 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template qC1[EQ3]8O[hN[IY not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString442_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_literalMutationString442 should have thrown MustacheNotFoundException");
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

