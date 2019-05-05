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
    public void testParser_literalMutationString2345_failAssert0_literalMutationString2852_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli.>ustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2345 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2345_failAssert0_literalMutationString2852 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli.>ustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2342_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("{xAA:<6z];,:uR?");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_literalMutationString2342 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {xAA:<6z];,:uR? not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2345_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("fdbcli.>ustache");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_literalMutationString2345 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli.>ustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2342_failAssert0null3232_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("{xAA:<6z];,:uR?");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2342 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2342_failAssert0null3232 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {xAA:<6z];,:uR? not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2342_failAssert0null3233_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("{xAA:<6z];,:uR?");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2342 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2342_failAssert0null3233 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {xAA:<6z];,:uR? not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_add2363_literalMutationString2480_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            System.out.println(invert);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_add2363_literalMutationString2480 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2343_failAssert0_add3045_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2343 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2343_failAssert0_add3045 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2346_failAssert0_literalMutationString2878_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2346 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2346_failAssert0_literalMutationString2878 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2345_failAssert0null3228_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli.>ustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2345 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2345_failAssert0null3228 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli.>ustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2343_failAssert0null3202_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2343 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2343_failAssert0null3202 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2342_failAssert0_literalMutationString2865_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("{xAA:<6z];,:uR?");
                Path file = getPath("src/tet/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2342 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2342_failAssert0_literalMutationString2865 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {xAA:<6z];,:uR? not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2342_failAssert0_literalMutationString2866_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("{xAA:<6z];,:uR?");
                Path file = getPath("sBrc/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2342 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2342_failAssert0_literalMutationString2866 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {xAA:<6z];,:uR? not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_add2359_literalMutationString2437_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache o_testParser_add2359__3 = dmf.compile("^8#K(K2xA54@/@y");
            Mustache compile = dmf.compile("fdbcli.mustache");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_add2359_literalMutationString2437 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ^8#K(K2xA54@/@y not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2354_failAssert0_literalMutationString2787_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "^nCpI");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2354 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2354_failAssert0_literalMutationString2787 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2345_failAssert0_add3074_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli.>ustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2345 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2345_failAssert0_add3074 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli.>ustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2343_failAssert0_add3040_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("This is a good day");
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2343 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2343_failAssert0_add3040 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2342_failAssert0_literalMutationString2875_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("{xAA:<6z];,:uR?");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "OTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2342 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2342_failAssert0_literalMutationString2875 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {xAA:<6z];,:uR? not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2353_failAssert0_literalMutationString2685_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("2vv$QHqo`v=8mpl");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2353 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2353_failAssert0_literalMutationString2685 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2vv$QHqo`v=8mpl not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2342_failAssert0_add3080_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("{xAA:<6z];,:uR?");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2342 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2342_failAssert0_add3080 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {xAA:<6z];,:uR? not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2342_failAssert0_add3081_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("{xAA:<6z];,:uR?");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2342 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2342_failAssert0_add3081 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {xAA:<6z];,:uR? not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParsernull2366_failAssert0_literalMutationString2620_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParsernull2366 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testParsernull2366_failAssert0_literalMutationString2620 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2342_failAssert0_add3079_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("{xAA:<6z];,:uR?");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2342 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2342_failAssert0_add3079 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {xAA:<6z];,:uR? not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2352_failAssert0_literalMutationString2824_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbclOi.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2352 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2352_failAssert0_literalMutationString2824 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2343_failAssert0_literalMutationString2760_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("This is a good day");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2343 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2343_failAssert0_literalMutationString2760 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2343_failAssert0_literalMutationString2754_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(",8;o|d7df]aJnB,,(4");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2343 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2343_failAssert0_literalMutationString2754 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ,8;o|d7df]aJnB,,(4 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2357_literalMutationString2557_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("uC;s`q/Z//Qo,dm");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF8");
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_literalMutationString2357_literalMutationString2557 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template uC;s`q/Z//Qo,dm not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2347_failAssert0_literalMutationString2670_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2347 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2347_failAssert0_literalMutationString2670 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2345_failAssert0_add3073_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli.>ustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2345 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2345_failAssert0_add3073 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli.>ustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2343_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_literalMutationString2343 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2345_failAssert0_literalMutationString2855_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli.>ustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2345 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2345_failAssert0_literalMutationString2855 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli.>ustache not found", expected.getMessage());
        }
    }
}

