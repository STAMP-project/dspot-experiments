package com.github.mustachejava.inverter;


import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheNotFoundException;
import com.github.mustachejava.util.Node;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;


public class AmplInverterTest extends InvertUtils {
    @Test(timeout = 10000)
    public void testParser_literalMutationString5270_failAssert0_add5943_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fd<bcli.mustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5270 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5943 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd<bcli.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5270_failAssert0_add5941_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fd<bcli.mustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5270 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5941 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd<bcli.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5270_failAssert0null6105_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fd<bcli.mustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5270 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0null6105 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd<bcli.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5272_failAssert0_literalMutationString5779_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resoures/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5272 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0_literalMutationString5779 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5272_failAssert0_literalMutationString5785_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UT5F-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5272 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0_literalMutationString5785 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_add5290_literalMutationString5382_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node o_testParser_add5290__10 = compile.invert(txt);
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_add5290_literalMutationString5382 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5270_failAssert0_add5941_failAssert0_literalMutationString7812_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("fd<bcli.mstache");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    compile.invert(txt);
                    Node invert = compile.invert(txt);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5270 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5941 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5941_failAssert0_literalMutationString7812 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd<bcli.mstache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5272_failAssert0_literalMutationString5768_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("Thig is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5272 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0_literalMutationString5768 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Thig is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5282_failAssert0_add6014_failAssert0_literalMutationString9690_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "qVey]");
                    Node invert = compile.invert(txt);
                    System.out.println(invert);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5282 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5282_failAssert0_add6014 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5282_failAssert0_add6014_failAssert0_literalMutationString9690 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParsernull5294_failAssert0null6088_failAssert0_literalMutationString8613_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath(null);
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(null);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParsernull5294 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testParsernull5294_failAssert0null6088 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testParsernull5294_failAssert0null6088_failAssert0_literalMutationString8613 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5272_failAssert0_add6003_failAssert0null11784_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath(null);
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5272 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0_add6003 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0_add6003_failAssert0null11784 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5271_failAssert0_literalMutationString5746_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("5Eo2&Q%7H-,aUA!");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "pO4]U");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5271 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5271_failAssert0_literalMutationString5746 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5Eo2&Q%7H-,aUA! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5271_failAssert0_literalMutationString5742_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("5Eo2&Q%7H-,aUA!");
                Path file = getPath("src/te(st/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5271 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5271_failAssert0_literalMutationString5742 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5Eo2&Q%7H-,aUA! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParsernull5296_failAssert0_add5931_failAssert0_literalMutationString8674_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(null);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParsernull5296 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testParsernull5296_failAssert0_add5931 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testParsernull5296_failAssert0_add5931_failAssert0_literalMutationString8674 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5274_failAssert0_literalMutationString5615_failAssert0null11605_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(null);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5274 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5274_failAssert0_literalMutationString5615 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5274_failAssert0_literalMutationString5615_failAssert0null11605 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5270_failAssert0null6104_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fd<bcli.mustache");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5270 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0null6104 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd<bcli.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5271_failAssert0_literalMutationString5735_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("5Eo2&Q%7H-,aUcA!");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5271 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5271_failAssert0_literalMutationString5735 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5Eo2&Q%7H-,aUcA! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5272_failAssert0_add6003_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5272 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0_add6003 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5270_failAssert0_add5943_failAssert0null11853_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("fd<bcli.mustache");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(null), "UTF-8");
                    Node invert = compile.invert(txt);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5270 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5943 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5943_failAssert0null11853 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd<bcli.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5270_failAssert0_literalMutationString5608_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fd<bcli.mustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "03cjy");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5270 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_literalMutationString5608 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd<bcli.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5270_failAssert0_add5943_failAssert0_add10793_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("fd<bcli.mustache");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    Files.readAllBytes(file);
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5270 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5943 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5943_failAssert0_add10793 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd<bcli.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5272_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_literalMutationString5272 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5274_failAssert0_literalMutationString5615_failAssert0_add10462_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    compile.invert(txt);
                    Node invert = compile.invert(txt);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5274 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5274_failAssert0_literalMutationString5615 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5274_failAssert0_literalMutationString5615_failAssert0_add10462 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5272_failAssert0null6155_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5272 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0null6155 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5270_failAssert0_add5943_failAssert0_literalMutationString8993_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("fd<bcli.mustache");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTuF-8");
                    Node invert = compile.invert(txt);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5270 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5943 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5943_failAssert0_literalMutationString8993 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd<bcli.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5271_failAssert0_add5988_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("5Eo2&Q%7H-,aUA!");
                Path file = getPath("src/test/resources/fdbcli.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5271 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5271_failAssert0_add5988 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5Eo2&Q%7H-,aUA! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5272_failAssert0_add6003_failAssert0_literalMutationString8731_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resorces/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5272 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0_add6003 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0_add6003_failAssert0_literalMutationString8731 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5270_failAssert0_add5941_failAssert0_add10365_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("fd<bcli.mustache");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    Files.readAllBytes(file);
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    compile.invert(txt);
                    Node invert = compile.invert(txt);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5270 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5941 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5941_failAssert0_add10365 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd<bcli.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5282_failAssert0_add6012_failAssert0_literalMutationString7560_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("aCBQ]_UYuA-|hqP");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    Files.readAllBytes(file);
                    String txt = new String(Files.readAllBytes(file), "qVey]");
                    Node invert = compile.invert(txt);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5282 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5282_failAssert0_add6012 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5282_failAssert0_add6012_failAssert0_literalMutationString7560 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template aCBQ]_UYuA-|hqP not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5271_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("5Eo2&Q%7H-,aUA!");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_literalMutationString5271 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5Eo2&Q%7H-,aUA! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5270_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("fd<bcli.mustache");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_literalMutationString5270 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd<bcli.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5270_failAssert0_add5941_failAssert0null11528_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("fd<bcli.mustache");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    compile.invert(txt);
                    Node invert = compile.invert(null);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5270 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5941 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_add5941_failAssert0null11528 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd<bcli.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5272_failAssert0_add5999_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                getPath("src/test/resources/fdbcli.txt");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5272 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0_add5999 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_add5287_literalMutationString5410_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache o_testParser_add5287__3 = dmf.compile("fdbcli.mustache");
            Mustache compile = dmf.compile("fd]cli.mustache");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_add5287_literalMutationString5410 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd]cli.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5270_failAssert0_literalMutationString5596_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fd<bcli.m`ustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5270 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5270_failAssert0_literalMutationString5596 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fd<bcli.m`ustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5271_failAssert0null6144_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("5Eo2&Q%7H-,aUA!");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5271 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5271_failAssert0null6144 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5Eo2&Q%7H-,aUA! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5274_failAssert0_literalMutationString5615_failAssert0_literalMutationString8065_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5274 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5274_failAssert0_literalMutationString5615 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5274_failAssert0_literalMutationString5615_failAssert0_literalMutationString8065 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5272_failAssert0_add6003_failAssert0_add10714_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("src/test/resources/fdbcli.txt");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    System.out.println(invert);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5272 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0_add6003 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0_add6003_failAssert0_add10714 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5271_failAssert0_add5986_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("5Eo2&Q%7H-,aUA!");
                Mustache compile = dmf.compile("5Eo2&Q%7H-,aUA!");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5271 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5271_failAssert0_add5986 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5Eo2&Q%7H-,aUA! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5274_failAssert0_literalMutationString5615_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5274 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5274_failAssert0_literalMutationString5615 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5272_failAssert0_add6002_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5272 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0_add6002 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParsernull5294_failAssert0_literalMutationString5532_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("CsbT[[4U!+MPh]-");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParsernull5294 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testParsernull5294_failAssert0_literalMutationString5532 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template CsbT[[4U!+MPh]- not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5271_failAssert0_add5989_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("5Eo2&Q%7H-,aUA!");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5271 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5271_failAssert0_add5989 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5Eo2&Q%7H-,aUA! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_add5290_add5865_literalMutationString6698_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("-8;x_)j}x# YzI>");
            getPath("src/test/resources/fdbcli.txt");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node o_testParser_add5290__10 = compile.invert(txt);
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_add5290_add5865_literalMutationString6698 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -8;x_)j}x# YzI> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5272_failAssert0null6154_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5272 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5272_failAssert0null6154 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5280_failAssert0_literalMutationString5666_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5280 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5280_failAssert0_literalMutationString5666 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5278_failAssert0_literalMutationString5788_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resourcesq/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString5278 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5278_failAssert0_literalMutationString5788 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParsernull5295_failAssert0_literalMutationString5541_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli.mustac[he");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParsernull5295 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testParsernull5295_failAssert0_literalMutationString5541 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli.mustac[he not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString5275_failAssert0_add5985_failAssert0_literalMutationString8745_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                    Mustache compile = dmf.compile("This is a good day");
                    Path file = getPath("");
                    String txt = new String(Files.readAllBytes(file), "UTF-8");
                    Node invert = compile.invert(txt);
                    System.out.println(invert);
                    org.junit.Assert.fail("testParser_literalMutationString5275 should have thrown NoSuchFileException");
                }
                org.junit.Assert.fail("testParser_literalMutationString5275_failAssert0_add5985 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testParser_literalMutationString5275_failAssert0_add5985_failAssert0_literalMutationString8745 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }
}

