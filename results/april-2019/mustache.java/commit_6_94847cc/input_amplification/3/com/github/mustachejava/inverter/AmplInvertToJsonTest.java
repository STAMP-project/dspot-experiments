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
    public void testToJson_literalMutationString18887_failAssert0_add19645_failAssert0_literalMutationString21228_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    dmf.compile("fdbcli.mustache");
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "This is a good day");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18887 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18887_failAssert0_add19645 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18887_failAssert0_add19645_failAssert0_literalMutationString21228 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18874_failAssert0_add19633_failAssert0null25799_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    dmf.compile(".f?H|m#!&3ie2re");
                    Mustache compile = dmf.compile(".f?H|m#!&3ie2re");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(null);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18874 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0_add19633 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0_add19633_failAssert0null25799 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .f?H|m#!&3ie2re not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJsonnull18898_failAssert0_literalMutationString19141_failAssert0_add24422_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("2.3#y#gRet/CF*i");
                    getPath(null);
                    Path file = getPath(null);
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJsonnull18898 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testToJsonnull18898_failAssert0_literalMutationString19141 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJsonnull18898_failAssert0_literalMutationString19141_failAssert0_add24422 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2.3#y#gRet/CF*i not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18887_failAssert0_literalMutationString19444_failAssert0null25851_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(null), "This is a good day");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18887 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18887_failAssert0_literalMutationString19444 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18887_failAssert0_literalMutationString19444_failAssert0null25851 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18874_failAssert0_add19633_failAssert0_literalMutationString22781_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    dmf.compile(".f?H|m#!&3i 2re");
                    Mustache compile = dmf.compile(".f?H|m#!&3ie2re");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18874 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0_add19633 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0_add19633_failAssert0_literalMutationString22781 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .f?H|m#!&3i 2re not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18876_failAssert0null19807_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18876 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0null19807 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18874_failAssert0null19793_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(".f?H|m#!&3ie2re");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(null);
                org.junit.Assert.fail("testToJson_literalMutationString18874 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0null19793 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .f?H|m#!&3ie2re not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJsonnull18898_failAssert0_literalMutationString19141_failAssert0_add24421_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    dmf.compile("2.3#y#gRet/CF*i");
                    Mustache compile = dmf.compile("2.3#y#gRet/CF*i");
                    Path file = getPath(null);
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJsonnull18898 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testToJsonnull18898_failAssert0_literalMutationString19141 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJsonnull18898_failAssert0_literalMutationString19141_failAssert0_add24421 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2.3#y#gRet/CF*i not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18876_failAssert0_literalMutationString19474_failAssert0_add25032_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "");
                    Node invert = compile.invert(txt);
                    output(invert);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18876 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_literalMutationString19474 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_literalMutationString19474_failAssert0_add25032 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18877_failAssert0_literalMutationString19286_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18877 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18877_failAssert0_literalMutationString19286 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18877_failAssert0_literalMutationString19286_failAssert0_add25009_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18877 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18877_failAssert0_literalMutationString19286 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18877_failAssert0_literalMutationString19286_failAssert0_add25009 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18874_failAssert0null19792_failAssert0_add24561_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile(".f?H|m#!&3ie2re");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    compile.invert(null);
                    Node invert = compile.invert(null);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18874 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0null19792 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0null19792_failAssert0_add24561 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .f?H|m#!&3ie2re not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18874_failAssert0null19792_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(".f?H|m#!&3ie2re");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18874 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0null19792 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .f?H|m#!&3ie2re not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJsonnull18898_failAssert0_literalMutationString19141_failAssert0_literalMutationString22041_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("2.3#y#gRet/4CF*i");
                    Path file = getPath(null);
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJsonnull18898 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testToJsonnull18898_failAssert0_literalMutationString19141 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJsonnull18898_failAssert0_literalMutationString19141_failAssert0_literalMutationString22041 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2.3#y#gRet/4CF*i not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJsonnull18898_failAssert0_literalMutationString19141_failAssert0null25596_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("2.3#y#gRet/CF*i");
                    Path file = getPath(null);
                    String txt = new String(Files.readAllBytes(null), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJsonnull18898 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testToJsonnull18898_failAssert0_literalMutationString19141 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJsonnull18898_failAssert0_literalMutationString19141_failAssert0null25596 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2.3#y#gRet/CF*i not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18876_failAssert0_add19655_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18876 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_add19655 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18876_failAssert0_literalMutationString19464_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("5Eo2&Q%7H-,aUA!");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18876 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_literalMutationString19464 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5Eo2&Q%7H-,aUA! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJsonnull18898_failAssert0_literalMutationString19141_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("2.3#y#gRet/CF*i");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJsonnull18898 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJsonnull18898_failAssert0_literalMutationString19141 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2.3#y#gRet/CF*i not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18887_failAssert0_literalMutationString19444_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18887 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18887_failAssert0_literalMutationString19444 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18874_failAssert0_add19633_failAssert0_add24705_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    dmf.compile(".f?H|m#!&3ie2re");
                    Mustache compile = dmf.compile(".f?H|m#!&3ie2re");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18874 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0_add19633 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0_add19633_failAssert0_add24705 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .f?H|m#!&3ie2re not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18876_failAssert0_literalMutationString19474_failAssert0null26062_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "");
                    Node invert = compile.invert(txt);
                    output(null);
                    org.junit.Assert.fail("testToJson_literalMutationString18876 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_literalMutationString19474 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_literalMutationString19474_failAssert0null26062 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJsonnull18898_failAssert0_literalMutationString19141_failAssert0null25598_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("2.3#y#gRet/CF*i");
                    Path file = getPath(null);
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(null);
                    org.junit.Assert.fail("testToJsonnull18898 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testToJsonnull18898_failAssert0_literalMutationString19141 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJsonnull18898_failAssert0_literalMutationString19141_failAssert0null25598 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2.3#y#gRet/CF*i not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18887_failAssert0_literalMutationString19444_failAssert0_literalMutationString22971_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("&^/I=6RSvl/]33Ar;k");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "This is a good day");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18887 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18887_failAssert0_literalMutationString19444 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18887_failAssert0_literalMutationString19444_failAssert0_literalMutationString22971 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template &^/I=6RSvl/]33Ar;k not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18874_failAssert0_literalMutationString19425_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(".f?H|m#!&3ie2re");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UT@-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18874 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0_literalMutationString19425 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .f?H|m#!&3ie2re not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_add18891_literalMutationString18970_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache o_testToJson_add18891__3 = dmf.compile("This is a good day");
            Mustache compile = dmf.compile("fdbcli.mustache");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson_add18891_literalMutationString18970 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18876_failAssert0_add19653_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18876 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_add19653 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18874_failAssert0_add19633_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile(".f?H|m#!&3ie2re");
                Mustache compile = dmf.compile(".f?H|m#!&3ie2re");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18874 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0_add19633 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .f?H|m#!&3ie2re not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18876_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson_literalMutationString18876 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18874_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile(".f?H|m#!&3ie2re");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson_literalMutationString18874 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .f?H|m#!&3ie2re not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18876_failAssert0_literalMutationString19476_failAssert0_literalMutationString21688_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This ik a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "This is a good day");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18876 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_literalMutationString19476 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_literalMutationString19476_failAssert0_literalMutationString21688 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This ik a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18883_failAssert0_literalMutationString19323_failAssert0_literalMutationString21903_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("S[lcJJ4o5U)a9U");
                    Path file = getPath("S_;]S:J}hX]$<<r>.e4G=7&r?:vJ<");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18883 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18883_failAssert0_literalMutationString19323 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18883_failAssert0_literalMutationString19323_failAssert0_literalMutationString21903 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template S[lcJJ4o5U)a9U not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18876_failAssert0_literalMutationString19476_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18876 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_literalMutationString19476 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18880_failAssert0null19775_failAssert0_literalMutationString21043_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath(null);
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18880 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18880_failAssert0null19775 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18880_failAssert0null19775_failAssert0_literalMutationString21043 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18876_failAssert0_literalMutationString19474_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18876 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_literalMutationString19474 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18876_failAssert0_literalMutationString19476_failAssert0_add24300_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    Files.readAllBytes(file);
                    String txt = new String(Files.readAllBytes(file), "This is a good day");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18876 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_literalMutationString19476 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_literalMutationString19476_failAssert0_add24300 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18874_failAssert0_literalMutationString19415_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("}N)Hm(B<W: v!]!");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18874 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0_literalMutationString19415 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template }N)Hm(B<W: v!]! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18875_failAssert0_literalMutationString19164_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli:-mustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18875 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18875_failAssert0_literalMutationString19164 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli:-mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18888_failAssert0_literalMutationString19271_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("*]=_}Tb`iZ]A`u&");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "_(w50");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18888 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18888_failAssert0_literalMutationString19271 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *]=_}Tb`iZ]A`u& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18887_failAssert0_literalMutationString19444_failAssert0_add24766_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    dmf.compile("This is a good day");
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "This is a good day");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18887 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18887_failAssert0_literalMutationString19444 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18887_failAssert0_literalMutationString19444_failAssert0_add24766 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18876_failAssert0null19806_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18876 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0null19806 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJsonnull18898_failAssert0_literalMutationString19141_failAssert0_literalMutationString22039_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("|kdC0DFf$k7]yw}");
                    Path file = getPath(null);
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJsonnull18898 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testToJsonnull18898_failAssert0_literalMutationString19141 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJsonnull18898_failAssert0_literalMutationString19141_failAssert0_literalMutationString22039 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template |kdC0DFf$k7]yw} not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18874_failAssert0null19792_failAssert0_literalMutationString22407_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile(".f?H|m#!&3ie2re");
                    Path file = getPath("src/test/resources/fdb4cli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(null);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18874 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0null19792 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0null19792_failAssert0_literalMutationString22407 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .f?H|m#!&3ie2re not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18884_failAssert0null19763_failAssert0_literalMutationString20982_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("]}pQnWT$3H#Lxi}");
                    Path file = getPath("This is a good day");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(null);
                    org.junit.Assert.fail("testToJson_literalMutationString18884 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18884_failAssert0null19763 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18884_failAssert0null19763_failAssert0_literalMutationString20982 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ]}pQnWT$3H#Lxi} not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18876_failAssert0_add19651_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("This is a good day");
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18876 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18876_failAssert0_add19651 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18874_failAssert0_add19636_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(".f?H|m#!&3ie2re");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString18874 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18874_failAssert0_add19636 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .f?H|m#!&3ie2re not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_add18891_literalMutationString18987_failAssert0_literalMutationString21712_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache o_testToJson_add18891__3 = dmf.compile("fdbcli.mustache");
                Mustache compile = dmf.compile("A9Ryo1qm+9?}o<z");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTR-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_add18891_literalMutationString18987 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson_add18891_literalMutationString18987_failAssert0_literalMutationString21712 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template A9Ryo1qm+9?}o<z not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString18877_failAssert0_literalMutationString19286_failAssert0_literalMutationString23653_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson_literalMutationString18877 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson_literalMutationString18877_failAssert0_literalMutationString19286 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString18877_failAssert0_literalMutationString19286_failAssert0_literalMutationString23653 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26506_failAssert0_add27171_failAssert0_literalMutationString30318_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("fdbc%i2.mustache");
                    getPath("src/test/resources/fdbcli2.txt");
                    Path file = getPath("src/test/resources/fdbcli2.txt");
                    String txt = new String(Files.readAllBytes(file), ",IEL2");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson2_literalMutationString26506 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testToJson2_literalMutationString26506_failAssert0_add27171 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26506_failAssert0_add27171_failAssert0_literalMutationString30318 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbc%i2.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26495_failAssert0_literalMutationString26977_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26495 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26495_failAssert0_literalMutationString26977 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26500_failAssert0_literalMutationString26943_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26500 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26500_failAssert0_literalMutationString26943 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_remove26517null27319_failAssert0_literalMutationString28369_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("BS$Ghj<Miqngk9AK");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                org.junit.Assert.fail("testToJson2_remove26517null27319 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson2_remove26517null27319_failAssert0_literalMutationString28369 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template BS$Ghj<Miqngk9AK not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26497_failAssert0_literalMutationString26856_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "*`fM ");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26497 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26497_failAssert0_literalMutationString26856 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26497_failAssert0null27357_failAssert0_add31514_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    getPath("src/test/resources/fdbcli2.txt");
                    Path file = getPath("src/test/resources/fdbcli2.txt");
                    String txt = new String(Files.readAllBytes(null), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson2_literalMutationString26497 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson2_literalMutationString26497_failAssert0null27357 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26497_failAssert0null27357_failAssert0_add31514 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26497_failAssert0null27357_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26497 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26497_failAssert0null27357 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2null26521_failAssert0_literalMutationString26740_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("y/@`85=}K.|4i*:}");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson2null26521 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson2null26521_failAssert0_literalMutationString26740 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template y/@`85=}K.|4i*:} not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26499_failAssert0_add27223_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(".+83%evx[g#Xb#<K");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26499 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26499_failAssert0_add27223 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .+83%evx[g#Xb#<K not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26499_failAssert0_literalMutationString26926_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26499 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26499_failAssert0_literalMutationString26926 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_add26512_add27117_literalMutationString28200_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache o_testToJson2_add26512__3 = dmf.compile("fdbcli2.mustache");
            Mustache compile = dmf.compile("1t[9G38$q,v*NsFn");
            Path file = getPath("src/test/resources/fdbcli2.txt");
            Files.readAllBytes(file);
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson2_add26512_add27117_literalMutationString28200 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 1t[9G38$q,v*NsFn not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_remove26517_add27138_literalMutationString27853_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli2.txt");
            Files.readAllBytes(file);
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            org.junit.Assert.fail("testToJson2_remove26517_add27138_literalMutationString27853 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2null26521_failAssert0_literalMutationString26739_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson2null26521 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson2null26521_failAssert0_literalMutationString26739 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2null26520_failAssert0_literalMutationString26760_failAssert0null33418_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("C^${y!KGTbssg?2:");
                    Path file = getPath("src/test/resources/fdbcli2.txt");
                    String txt = new String(Files.readAllBytes(null), "UTF-8");
                    Node invert = compile.invert(null);
                    output(invert);
                    org.junit.Assert.fail("testToJson2null26520 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testToJson2null26520_failAssert0_literalMutationString26760 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2null26520_failAssert0_literalMutationString26760_failAssert0null33418 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template C^${y!KGTbssg?2: not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26499_failAssert0null27381_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(".+83%evx[g#Xb#<K");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26499 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26499_failAssert0null27381 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .+83%evx[g#Xb#<K not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_add26512_literalMutationString26642_failAssert0_literalMutationString30925_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache o_testToJson2_add26512__3 = dmf.compile("fdbcli2.mustache");
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_add26512_literalMutationString26642 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson2_add26512_literalMutationString26642_failAssert0_literalMutationString30925 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2null26522_failAssert0_literalMutationString26720_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(null);
                org.junit.Assert.fail("testToJson2null26522 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson2null26522_failAssert0_literalMutationString26720 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_remove26517_add27136_literalMutationString28081_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache o_testToJson2_remove26517_add27136__3 = dmf.compile("This is a good day");
            Mustache compile = dmf.compile("fdbcli2.mustache");
            Path file = getPath("src/test/resources/fdbcli2.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            org.junit.Assert.fail("testToJson2_remove26517_add27136_literalMutationString28081 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26499_failAssert0_literalMutationString26929_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(".+83%evx[g#Xb#<K");
                Path file = getPath("");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26499 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26499_failAssert0_literalMutationString26929 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .+83%evx[g#Xb#<K not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_add26515_literalMutationString26681_failAssert0_literalMutationString29558_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("This is a good day");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node o_testToJson2_add26515__10 = compile.invert(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_add26515_literalMutationString26681 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson2_add26515_literalMutationString26681_failAssert0_literalMutationString29558 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_add26514_literalMutationString26590_failAssert0_literalMutationString30880_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("<^#BlX#meXG.fx;");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_add26514_literalMutationString26590 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_add26514_literalMutationString26590_failAssert0_literalMutationString30880 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template <^#BlX#meXG.fx; not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26497_failAssert0null27357_failAssert0_literalMutationString28415_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("5Eo2&Q%7H-,aUA!");
                    Path file = getPath("src/test/resources/fdbcli2.txt");
                    String txt = new String(Files.readAllBytes(null), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson2_literalMutationString26497 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson2_literalMutationString26497_failAssert0null27357 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26497_failAssert0null27357_failAssert0_literalMutationString28415 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5Eo2&Q%7H-,aUA! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2null26520_failAssert0_literalMutationString26760_failAssert0_literalMutationString30664_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("C^${y!KGTbssg?2:");
                    Path file = getPath("src/test/resources/fdbcli2.txt");
                    String txt = new String(Files.readAllBytes(null), "UTrF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson2null26520 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testToJson2null26520_failAssert0_literalMutationString26760 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2null26520_failAssert0_literalMutationString26760_failAssert0_literalMutationString30664 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template C^${y!KGTbssg?2: not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26511_failAssert0_add27217_failAssert0_literalMutationString29163_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli2.txt");
                    String txt = new String(Files.readAllBytes(file), "UT-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson2_literalMutationString26511 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testToJson2_literalMutationString26511_failAssert0_add27217 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26511_failAssert0_add27217_failAssert0_literalMutationString29163 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26499_failAssert0_add27218_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile(".+83%evx[g#Xb#<K");
                Mustache compile = dmf.compile(".+83%evx[g#Xb#<K");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26499 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26499_failAssert0_add27218 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .+83%evx[g#Xb#<K not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26497_failAssert0null27357_failAssert0null32788_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli2.txt");
                    String txt = new String(Files.readAllBytes(null), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(null);
                    org.junit.Assert.fail("testToJson2_literalMutationString26497 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson2_literalMutationString26497_failAssert0null27357 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26497_failAssert0null27357_failAssert0null32788 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26499_failAssert0null27383_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(".+83%evx[g#Xb#<K");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26499 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26499_failAssert0null27383 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .+83%evx[g#Xb#<K not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26496_failAssert0_literalMutationString26823_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("u2@R}omP$SH-*&6");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26496 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26496_failAssert0_literalMutationString26823 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template u2@R}omP$SH-*&6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26497_failAssert0_add27193_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26497 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26497_failAssert0_add27193 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26499_failAssert0_literalMutationString26937_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(".+83%evx[g#Xb#<K");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26499 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26499_failAssert0_literalMutationString26937 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .+83%evx[g#Xb#<K not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26499_failAssert0_add27220_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(".+83%evx[g#Xb#<K");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26499 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26499_failAssert0_add27220 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .+83%evx[g#Xb#<K not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2null26520_failAssert0_literalMutationString26760_failAssert0_add32351_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("C^${y!KGTbssg?2:");
                    getPath("src/test/resources/fdbcli2.txt");
                    Path file = getPath("src/test/resources/fdbcli2.txt");
                    String txt = new String(Files.readAllBytes(null), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson2null26520 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testToJson2null26520_failAssert0_literalMutationString26760 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2null26520_failAssert0_literalMutationString26760_failAssert0_add32351 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template C^${y!KGTbssg?2: not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_remove26517_add27139_literalMutationString28277_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("#dbcli2.mustache");
            Path file = getPath("src/test/resources/fdbcli2.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node o_testToJson2_remove26517_add27139__10 = compile.invert(txt);
            Node invert = compile.invert(txt);
            org.junit.Assert.fail("testToJson2_remove26517_add27139_literalMutationString28277 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #dbcli2.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2null26520_failAssert0_literalMutationString26760_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("C^${y!KGTbssg?2:");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2null26520 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson2null26520_failAssert0_literalMutationString26760 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template C^${y!KGTbssg?2: not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26499_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile(".+83%evx[g#Xb#<K");
            Path file = getPath("src/test/resources/fdbcli2.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson2_literalMutationString26499 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .+83%evx[g#Xb#<K not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26497_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli2.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson2_literalMutationString26497 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26498_failAssert0_literalMutationString27062_failAssert0_literalMutationString30531_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile(".<K7fLg(dB &{TUh");
                    Path file = getPath("src/test/resources/fdbcli2.txt");
                    String txt = new String(Files.readAllBytes(file), "oj4$a");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson2_literalMutationString26498 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson2_literalMutationString26498_failAssert0_literalMutationString27062 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26498_failAssert0_literalMutationString27062_failAssert0_literalMutationString30531 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .<K7fLg(dB &{TUh not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString26497_failAssert0_add27190_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString26497 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString26497_failAssert0_add27190 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2null26522_failAssert0_literalMutationString26725_failAssert0_literalMutationString30548_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("CAoP*e]9W$R{*)|]");
                    Path file = getPath("");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(null);
                    org.junit.Assert.fail("testToJson2null26522 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testToJson2null26522_failAssert0_literalMutationString26725 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson2null26522_failAssert0_literalMutationString26725_failAssert0_literalMutationString30548 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template CAoP*e]9W$R{*)|] not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_add26516_literalMutationString26622_failAssert0_literalMutationString29539_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UF-8");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson2_add26516_literalMutationString26622 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson2_add26516_literalMutationString26622_failAssert0_literalMutationString29539 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString10_failAssert0_add773_failAssert0_literalMutationString3946_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    dmf.compile("Nz6_kX9%^&v:7J?[^");
                    Mustache compile = dmf.compile("psauxwww.mustache");
                    Path file = getPath("src/test/reso|rces/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString10 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString10_failAssert0_add773 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString10_failAssert0_add773_failAssert0_literalMutationString3946 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Nz6_kX9%^&v:7J?[^ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString9_failAssert0_add704_failAssert0_literalMutationString3586_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resurces/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    compile.invert(txt);
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString9 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString9_failAssert0_add704 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString9_failAssert0_add704_failAssert0_literalMutationString3586 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString9_failAssert0_add704_failAssert0_literalMutationString3588_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("i_hi1|Hx ia%J]zbb");
                    Path file = getPath("src/test/resurces/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    compile.invert(txt);
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString9 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString9_failAssert0_add704 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString9_failAssert0_add704_failAssert0_literalMutationString3588 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i_hi1|Hx ia%J]zbb not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString14_failAssert0null923_failAssert0_literalMutationString2066_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath(null);
                    String txt = new String(Files.readAllBytes(file), "PTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson3_literalMutationString14 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString14_failAssert0null923 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString14_failAssert0null923_failAssert0_literalMutationString2066 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString5_failAssert0null905_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("n[1Nj=e}jC=.,Nmyh");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString5_failAssert0null905 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template n[1Nj=e}jC=.,Nmyh not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3null26_failAssert0_literalMutationString234_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3null26 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson3null26_failAssert0_literalMutationString234 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString3_failAssert0_literalMutationString539_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("5Eo2&Q%7H-,aUA!");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString3_failAssert0_literalMutationString539 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5Eo2&Q%7H-,aUA! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString5_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("n[1Nj=e}jC=.,Nmyh");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_literalMutationString5 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template n[1Nj=e}jC=.,Nmyh not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString3_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_literalMutationString3 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString3_failAssert0_literalMutationString552_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UT-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString3_failAssert0_literalMutationString552 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString5_failAssert0null904_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("n[1Nj=e}jC=.,Nmyh");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString5_failAssert0null904 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template n[1Nj=e}jC=.,Nmyh not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString3_failAssert0null921_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(null);
                org.junit.Assert.fail("testToJson3_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString3_failAssert0null921 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString11_failAssert0_literalMutationString470_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("qui_fC-I^a<jCl:T+");
                Path file = getPath("This is a good day");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString11 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString11_failAssert0_literalMutationString470 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template qui_fC-I^a<jCl:T+ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString5_failAssert0_literalMutationString498_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("n[1Nj=e}jC=.,Nmyh");
                Path file = getPath("src/test/resources/ps:auxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString5_failAssert0_literalMutationString498 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template n[1Nj=e}jC=.,Nmyh not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString2_failAssert0_literalMutationString592_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString2_failAssert0_literalMutationString592 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString17_failAssert0_literalMutationString295_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(";u?m9P`]2x8>(B),(");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UT-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString17 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString17_failAssert0_literalMutationString295 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ;u?m9P`]2x8>(B),( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString5_failAssert0_add743_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("n[1Nj=e}jC=.,Nmyh");
                Mustache compile = dmf.compile("n[1Nj=e}jC=.,Nmyh");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString5_failAssert0_add743 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template n[1Nj=e}jC=.,Nmyh not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString5_failAssert0_add747_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("n[1Nj=e}jC=.,Nmyh");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString5_failAssert0_add747 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template n[1Nj=e}jC=.,Nmyh not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString3_failAssert0_add764_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString3_failAssert0_add764 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString3_failAssert0_add762_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                getPath("src/test/resources/psauxwww.txt");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString3_failAssert0_add762 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3null27_failAssert0_literalMutationString276_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("%x&JA[](2-_UPPBW}");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3null27 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson3null27_failAssert0_literalMutationString276 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template %x&JA[](2-_UPPBW} not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString9_failAssert0null871_failAssert0_literalMutationString1795_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile(".n(%4f3]l1l*y([X%");
                    Path file = getPath("src/test/resurces/psauxwww.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(null);
                    org.junit.Assert.fail("testToJson3_literalMutationString9 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson3_literalMutationString9_failAssert0null871 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString9_failAssert0null871_failAssert0_literalMutationString1795 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .n(%4f3]l1l*y([X% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3null29_failAssert0_add666_failAssert0_literalMutationString2315_failAssert0() throws IOException {
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
                    org.junit.Assert.fail("testToJson3null29 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testToJson3null29_failAssert0_add666 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson3null29_failAssert0_add666_failAssert0_literalMutationString2315 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString6_failAssert0_literalMutationString314_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("131TfvQ[a!`QCxs?oo");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString6_failAssert0_literalMutationString314 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 131TfvQ[a!`QCxs?oo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString5_failAssert0_literalMutationString501_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("n[1Nj=e}jC=.,Nmyh");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString5_failAssert0_literalMutationString501 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template n[1Nj=e}jC=.,Nmyh not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString8_failAssert0_literalMutationString523_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString8 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString8_failAssert0_literalMutationString523 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString7484_failAssert0_literalMutationString8747_failAssert0_literalMutationString14310_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("fdbcli2.<mustache");
                    Path file = getPath("src/Ltest/resources/fdbcli3.txt");
                    String txt = new String(Files.readAllBytes(file), "U)TF-8");
                    System.out.println("Input text:[");
                    System.out.print(txt);
                    System.out.println("]");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson4_literalMutationString7484 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson4_literalMutationString7484_failAssert0_literalMutationString8747 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString7484_failAssert0_literalMutationString8747_failAssert0_literalMutationString14310 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli2.<mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_remove7513_literalMutationString7949_failAssert0_add16654_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_remove7513_literalMutationString7949 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_remove7513_literalMutationString7949_failAssert0_add16654 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString7501null9440_failAssert0_literalMutationString12364_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(" Dcg=uG}`}+S[]mD");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("s");
                Node invert = compile.invert(txt);
                output(null);
                org.junit.Assert.fail("testToJson4_literalMutationString7501null9440 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString7501null9440_failAssert0_literalMutationString12364 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  Dcg=uG}`}+S[]mD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString7478_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("4}(:f=&m9fQfMe7I");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("Input text:[");
            System.out.print(txt);
            System.out.println("]");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_literalMutationString7478 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4}(:f=&m9fQfMe7I not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString7478_failAssert0_add9116_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("4}(:f=&m9fQfMe7I");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString7478 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString7478_failAssert0_add9116 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4}(:f=&m9fQfMe7I not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_remove7513_literalMutationString7949_failAssert0_literalMutationString14944_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("yn(-R$]sP[Z/");
                System.out.print(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_remove7513_literalMutationString7949 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_remove7513_literalMutationString7949_failAssert0_literalMutationString14944 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString7480_failAssert0_literalMutationString8794_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resourceH/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString7480 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString7480_failAssert0_literalMutationString8794 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_add7506_add8889_literalMutationString10195_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("If$P$u ^j%bS;Bb$");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("Input text:[");
            System.out.println("Input text:[");
            System.out.print(txt);
            System.out.println("]");
            System.out.println("]");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_add7506_add8889_literalMutationString10195 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template If$P$u ^j%bS;Bb$ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_add7510_literalMutationString7728_failAssert0_literalMutationString13786_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("!?KPm lq<ndb_P-T");
                Path file = getPath("rf0Jv<yfPKS@Kzim).Z(TWe8=,uD?%");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson4_add7510_literalMutationString7728 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson4_add7510_literalMutationString7728_failAssert0_literalMutationString13786 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !?KPm lq<ndb_P-T not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString7478_failAssert0_literalMutationString8552_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("4}(:f=&m9fQfMe7I");
                Path file = getPath("src/testY/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString7478 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString7478_failAssert0_literalMutationString8552 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4}(:f=&m9fQfMe7I not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString7478_failAssert0_literalMutationString8556_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("4}(:f=&m9fQfMe7I");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString7478 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString7478_failAssert0_literalMutationString8556 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 4}(:f=&m9fQfMe7I not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString7502_add8944_literalMutationString10480_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache o_testToJson4_literalMutationString7502_add8944__3 = dmf.compile("KX1,:QaCl@a4%#RV");
            Mustache compile = dmf.compile("fdbcli2.mustache");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("Input text:[");
            System.out.print(txt);
            System.out.println("This is a good day");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_literalMutationString7502_add8944_literalMutationString10480 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_remove7511_remove9243_literalMutationString10926_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("]");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_remove7511_remove9243_literalMutationString10926 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_remove7513_literalMutationString7949_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("Input text:[");
            System.out.print(txt);
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_remove7513_literalMutationString7949 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_remove7513_literalMutationString7949_failAssert0null18410_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_remove7513_literalMutationString7949 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_remove7513_literalMutationString7949_failAssert0null18410 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString7494_literalMutationString7975_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("hXFYQcNG3,QZY^f@");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("");
            System.out.print(txt);
            System.out.println("]");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_literalMutationString7494_literalMutationString7975 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template hXFYQcNG3,QZY^f@ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4null7517_failAssert0_literalMutationString8220_failAssert0_literalMutationString14749_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("[>E=/}p=j2D@tK#b");
                    Path file = getPath("");
                    String txt = new String(Files.readAllBytes(null), "UTF-8");
                    System.out.println("Input text:[");
                    System.out.print(txt);
                    System.out.println("]");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson4null7517 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testToJson4null7517_failAssert0_literalMutationString8220 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson4null7517_failAssert0_literalMutationString8220_failAssert0_literalMutationString14749 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [>E=/}p=j2D@tK#b not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_add7507_add8827_literalMutationString10229_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            Files.readAllBytes(file);
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("Input text:[");
            System.out.print(txt);
            System.out.print(txt);
            System.out.println("]");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_add7507_add8827_literalMutationString10229 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString7480_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testToJson4_literalMutationString7480 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString7480_failAssert0_add9192_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString7480 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString7480_failAssert0_add9192 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString7480_failAssert0null9629_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString7480 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString7480_failAssert0null9629 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString7487_failAssert0_literalMutationString8602_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/dbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString7487 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString7487_failAssert0_literalMutationString8602 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0_literalMutationString34563_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a-good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34563 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a-good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34031_failAssert0_literalMutationString34358_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("Bd=HY(O(U/$7>[Dj");
                Path file = getPath("");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34031 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34031_failAssert0_literalMutationString34358 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Bd=HY(O(U/$7>[Dj not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0_literalMutationString34576_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34576 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34036_failAssert0_literalMutationString34599_failAssert0_add39943_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("fd%cli3.mustache");
                    Path file = getPath("src/test/resurces/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34036 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34036_failAssert0_literalMutationString34599 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34036_failAssert0_literalMutationString34599_failAssert0_add39943 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd%cli3.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34036_failAssert0_literalMutationString34599_failAssert0_add39941_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("fd%cli3.mustache");
                    Path file = getPath("src/test/resurces/fdbcli.txt");
                    Files.readAllBytes(file);
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34036 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34036_failAssert0_literalMutationString34599 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34036_failAssert0_literalMutationString34599_failAssert0_add39941 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd%cli3.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34032_failAssert0_literalMutationString34629_failAssert0_literalMutationString38828_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("$^,^&qUv=QzH`Z>(");
                    Path file = getPath("This is a good day");
                    String txt = new String(Files.readAllBytes(file), "8p_m4");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34032 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0_literalMutationString34629 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0_literalMutationString34629_failAssert0_literalMutationString38828 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template $^,^&qUv=QzH`Z>( not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0_literalMutationString34563_failAssert0_literalMutationString38268_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a-good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "_Dj;m");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34563 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34563_failAssert0_literalMutationString38268 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a-good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0null34944_failAssert0_add39646_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    dmf.compile("This is a good day");
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(null), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0null34944 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0null34944_failAssert0_add39646 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34028_failAssert0null34951_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("`1.+n{N8)wm^*@uB");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(null);
                org.junit.Assert.fail("testToJson5_literalMutationString34028 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34028_failAssert0null34951 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `1.+n{N8)wm^*@uB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34040_failAssert0_literalMutationString34513_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("CkuV%z}B`x=REQ|O");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34040 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34040_failAssert0_literalMutationString34513 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template CkuV%z}B`x=REQ|O not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0_literalMutationString34563_failAssert0_literalMutationString38258_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is; a-good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34563 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34563_failAssert0_literalMutationString38258 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is; a-good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add34047_literalMutationString34186_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            output(invert);
            org.junit.Assert.fail("testToJson5_add34047_literalMutationString34186 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34036_failAssert0_literalMutationString34599_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fd%cli3.mustache");
                Path file = getPath("src/test/resurces/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34036 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34036_failAssert0_literalMutationString34599 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd%cli3.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add34047_literalMutationString34186_failAssert0_literalMutationString36892_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UT/F-8");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson5_add34047_literalMutationString34186 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_add34047_literalMutationString34186_failAssert0_literalMutationString36892 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add34047_literalMutationString34186_failAssert0_literalMutationString36886_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("This is a good day");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson5_add34047_literalMutationString34186 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_add34047_literalMutationString34186_failAssert0_literalMutationString36886 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34032_failAssert0_literalMutationString34618_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("This is a good day");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34032 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0_literalMutationString34618 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add34046_literalMutationString34205_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node o_testToJson5_add34046__10 = compile.invert(txt);
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_add34046_literalMutationString34205 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34032_failAssert0_literalMutationString34627_failAssert0_literalMutationString38790_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("This is a good day");
                    String txt = new String(Files.readAllBytes(file), "UTF+-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34032 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0_literalMutationString34627 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0_literalMutationString34627_failAssert0_literalMutationString38790 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0_add34790_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_add34790 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34034_failAssert0_literalMutationString34441_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("[Smr/&d9C.j+T2KZ");
                Path file = getPath("src/teat/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34034 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34034_failAssert0_literalMutationString34441 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [Smr/&d9C.j+T2KZ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0_literalMutationString34563_failAssert0null40996_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a-good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(null);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34563 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34563_failAssert0null40996 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a-good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34036_failAssert0_literalMutationString34599_failAssert0null41014_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("fd%cli3.mustache");
                    Path file = getPath(null);
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34036 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34036_failAssert0_literalMutationString34599 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34036_failAssert0_literalMutationString34599_failAssert0null41014 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd%cli3.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34028_failAssert0_literalMutationString34591_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("`1.+n{N8)wm^*@uB");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UT%-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34028 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34028_failAssert0_literalMutationString34591 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `1.+n{N8)wm^*@uB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34028_failAssert0null34950_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("`1.+n{N8)wm^*@uB");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34028 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34028_failAssert0null34950 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `1.+n{N8)wm^*@uB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_remove34048_literalMutationString34223_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            org.junit.Assert.fail("testToJson5_remove34048_literalMutationString34223 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34035_failAssert0_literalMutationString34475_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("+TJT4{I5]n`H%4kcuXL@suwJuWwbJ");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34035 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34035_failAssert0_literalMutationString34475 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34032_failAssert0_literalMutationString34618_failAssert0_add39876_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("This is a good day");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34032 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0_literalMutationString34618 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0_literalMutationString34618_failAssert0_add39876 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34037_failAssert0_literalMutationString34408_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("*;[$MY}&(SS+HbrY");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34037 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34037_failAssert0_literalMutationString34408 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template *;[$MY}&(SS+HbrY not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34028_failAssert0_add34795_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("`1.+n{N8)wm^*@uB");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34028 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34028_failAssert0_add34795 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `1.+n{N8)wm^*@uB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34028_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("`1.+n{N8)wm^*@uB");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_literalMutationString34028 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `1.+n{N8)wm^*@uB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0_literalMutationString34563_failAssert0_add39918_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a-good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    compile.invert(txt);
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34563 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34563_failAssert0_add39918 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a-good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0_literalMutationString34563_failAssert0null40995_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a-good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(null), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34563 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34563_failAssert0null40995 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a-good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34028_failAssert0_literalMutationString34586_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("`1.+n{N8)wm^*@uB");
                Path file = getPath("This is a good day");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34028 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34028_failAssert0_literalMutationString34586 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `1.+n{N8)wm^*@uB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34036_failAssert0_literalMutationString34599_failAssert0_literalMutationString38333_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("fd%cli3.mustache");
                    Path file = getPath("src/test/resurces/fdbcli3txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34036 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34036_failAssert0_literalMutationString34599 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34036_failAssert0_literalMutationString34599_failAssert0_literalMutationString38333 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd%cli3.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34032_failAssert0_literalMutationString34628_failAssert0_literalMutationString36914_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("fdbcli3.musta<he");
                    Path file = getPath("This is a good day");
                    String txt = new String(Files.readAllBytes(file), "This is a good day");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34032 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0_literalMutationString34628 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0_literalMutationString34628_failAssert0_literalMutationString36914 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli3.musta<he not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34041_failAssert0_literalMutationString34496_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(">_5wl?q4|BO4SBUi");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "i6pD^");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34041 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34041_failAssert0_literalMutationString34496 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >_5wl?q4|BO4SBUi not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add34047_literalMutationString34186_failAssert0_add39406_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson5_add34047_literalMutationString34186 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_add34047_literalMutationString34186_failAssert0_add39406 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add34047_literalMutationString34186_failAssert0_add39401_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                getPath("src/test/resources/fdbcli.txt");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson5_add34047_literalMutationString34186 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_add34047_literalMutationString34186_failAssert0_add39401 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34042_failAssert0_literalMutationString34470_failAssert0_literalMutationString38741_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("2g`F%**Qq];j3QRs");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UUT-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34042 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34042_failAssert0_literalMutationString34470 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34042_failAssert0_literalMutationString34470_failAssert0_literalMutationString38741 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2g`F%**Qq];j3QRs not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0_literalMutationString34563_failAssert0_add39915_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    dmf.compile("This is a-good day");
                    Mustache compile = dmf.compile("This is a-good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34563 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_literalMutationString34563_failAssert0_add39915 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a-good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0null34944_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0null34944 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34032_failAssert0null34959_failAssert0_literalMutationString38529_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("This is a good day");
                    String txt = new String(Files.readAllBytes(null), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34032 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0null34959 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0null34959_failAssert0_literalMutationString38529 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34036_failAssert0_literalMutationString34599_failAssert0_literalMutationString38325_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("nN0HY/G1uLF?pK`?");
                    Path file = getPath("src/test/resurces/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34036 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34036_failAssert0_literalMutationString34599 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34036_failAssert0_literalMutationString34599_failAssert0_literalMutationString38325 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nN0HY/G1uLF?pK`? not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0null34944_failAssert0null40786_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(null), "UTF-8");
                    Node invert = compile.invert(null);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0null34944 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0null34944_failAssert0null40786 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34032_failAssert0_literalMutationString34618_failAssert0null40962_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("This is a good day");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(null);
                    org.junit.Assert.fail("testToJson5_literalMutationString34032 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0_literalMutationString34618 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0_literalMutationString34618_failAssert0null40962 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0_add34788_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0_add34788 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0null34944_failAssert0_literalMutationString37554_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("");
                    String txt = new String(Files.readAllBytes(null), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0null34944 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34029_failAssert0null34944_failAssert0_literalMutationString37554 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34028_failAssert0_add34796_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("`1.+n{N8)wm^*@uB");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString34028 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34028_failAssert0_add34796 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `1.+n{N8)wm^*@uB not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add34047_literalMutationString34186_failAssert0null40603_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson5_add34047_literalMutationString34186 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_add34047_literalMutationString34186_failAssert0null40603 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34029_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_literalMutationString34029 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString34032_failAssert0_literalMutationString34618_failAssert0_literalMutationString38133_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This Ks a good day");
                    Path file = getPath("This is a good day");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    output(invert);
                    org.junit.Assert.fail("testToJson5_literalMutationString34032 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0_literalMutationString34618 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString34032_failAssert0_literalMutationString34618_failAssert0_literalMutationString38133 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This Ks a good day not found", expected.getMessage());
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

