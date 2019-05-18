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
    public void testToJson3_literalMutationString4_failAssert0_literalMutationString393_failAssert0_add2857_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "This is a good day");
                    compile.invert(txt);
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_literalMutationString393 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_literalMutationString393_failAssert0_add2857 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_literalMutationString393_failAssert0_add2858_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "This is a good day");
                    Node invert = compile.invert(txt);
                    output(invert);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_literalMutationString393 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_literalMutationString393_failAssert0_add2858 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_add19null686_failAssert0_literalMutationString1944_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache o_testToJson3_add19__3 = dmf.compile(")SnP{]X}Ud.By_j76");
                Mustache compile = dmf.compile("psauxwww.mustache");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson3_add19null686 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson3_add19null686_failAssert0_literalMutationString1944 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template )SnP{]X}Ud.By_j76 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString14_failAssert0_literalMutationString138_failAssert0_add2864_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("sF{>=7-(O5KZIa+yq");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "");
                    Node invert = compile.invert(txt);
                    output(invert);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString14 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString14_failAssert0_literalMutationString138 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString14_failAssert0_literalMutationString138_failAssert0_add2864 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sF{>=7-(O5KZIa+yq not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_literalMutationString393_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_literalMutationString393 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString9_failAssert0_add575_failAssert0_literalMutationString2255_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("oX`aIm}N)UktyTR]v");
                    Path file = getPath("sr/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString9 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString9_failAssert0_add575 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString9_failAssert0_add575_failAssert0_literalMutationString2255 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template oX`aIm}N)UktyTR]v not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_literalMutationString393_failAssert0_literalMutationString1742_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_literalMutationString393 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_literalMutationString393_failAssert0_literalMutationString1742 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_add22_literalMutationString118_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("E%}o5!4oLKriZd||G");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node o_testToJson3_add22__10 = compile.invert(txt);
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_add22_literalMutationString118 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template E%}o5!4oLKriZd||G not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString3_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("5@l<M*C8yK+)?KNa.");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_literalMutationString3 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5@l<M*C8yK+)?KNa. not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_add630_failAssert0null3277_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    getPath("src/test/resources/psauxwww.txt");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(null);
                    org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_add630 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_add630_failAssert0null3277 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString14_failAssert0_literalMutationString138_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("sF{>=7-(O5KZIa+yq");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString14 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString14_failAssert0_literalMutationString138 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sF{>=7-(O5KZIa+yq not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_add630_failAssert0_add2712_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    getPath("src/test/resources/psauxwww.txt");
                    getPath("src/test/resources/psauxwww.txt");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_add630 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_add630_failAssert0_add2712 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0null767_failAssert0_add3008_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(null);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0null767 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0null767_failAssert0_add3008 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString3_failAssert0_add587_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("5@l<M*C8yK+)?KNa.");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString3_failAssert0_add587 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5@l<M*C8yK+)?KNa. not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_add19_literalMutationString103_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache o_testToJson3_add19__3 = dmf.compile("psauxwww.mustache");
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_add19_literalMutationString103 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0null767_failAssert0_add3006_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    compile.invert(null);
                    Node invert = compile.invert(null);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0null767 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0null767_failAssert0_add3006 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString3_failAssert0_add585_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("5@l<M*C8yK+)?KNa.");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString3_failAssert0_add585 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5@l<M*C8yK+)?KNa. not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString14_failAssert0_literalMutationString138_failAssert0_literalMutationString1750_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("sF{>=7-(O5]KZIa+yq");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString14 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString14_failAssert0_literalMutationString138 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString14_failAssert0_literalMutationString138_failAssert0_literalMutationString1750 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sF{>=7-(O5]KZIa+yq not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_literalMutationString393_failAssert0_literalMutationString1732_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("\\s+[0-9]+\\s+[0-9]+\\s+{{tag_device}} {{reads}} {{reads_merged}} {{sectors_read}} {{read_time}} {{writes}} {{writes_merged}} {{sectors_written}} {{write_time}} {{ios}} {{io_time}} {{weighted_io_time}}\n");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "This is a good day");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_literalMutationString393 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_literalMutationString393_failAssert0_literalMutationString1732 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template \\s+[0-9]+\\s+[0-9]+\\s+{{tag_device}} {{reads}} {{reads_merged}} {{sectors_read}} {{read_time}} {{writes}} {{writes_merged}} {{sectors_written}} {{write_time}} {{ios}} {{io_time}} {{weighted_io_time}}\n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0null767_failAssert0_literalMutationString2134_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/Best/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(null);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0null767 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0null767_failAssert0_literalMutationString2134 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0null767_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0null767 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0null767_failAssert0_literalMutationString2139_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "UT-8");
                    Node invert = compile.invert(null);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0null767 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0null767_failAssert0_literalMutationString2139 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_add630_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                getPath("src/test/resources/psauxwww.txt");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_add630 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString16_failAssert0_literalMutationString346_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), ">X&@%");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString16 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString16_failAssert0_literalMutationString346 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_literalMutationString393_failAssert0null3397_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "This is a good day");
                    Node invert = compile.invert(null);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_literalMutationString393 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_literalMutationString393_failAssert0null3397 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_add630_failAssert0_literalMutationString1330_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    getPath("src/test/resources/psauxwww.txt");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "This is a good day");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_add630 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_add630_failAssert0_literalMutationString1330 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString10_failAssert0_literalMutationString449_failAssert0_literalMutationString1804_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("m*rW(E]g1UL.I?rD(");
                    Path file = getPath("This is a good day");
                    String txt = new String(Files.readAllBytes(file), "k$ZSD");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString10 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString10_failAssert0_literalMutationString449 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString10_failAssert0_literalMutationString449_failAssert0_literalMutationString1804 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template m*rW(E]g1UL.I?rD( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString3_failAssert0_literalMutationString243_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("5@l<M*C8yK+)?KNa.");
                Path file = getPath("This is a good day");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString3_failAssert0_literalMutationString243 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5@l<M*C8yK+)?KNa. not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0null767_failAssert0null3502_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(null);
                    output(null);
                    org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0null767 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0null767_failAssert0null3502 should have thrown MustacheNotFoundException");
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

