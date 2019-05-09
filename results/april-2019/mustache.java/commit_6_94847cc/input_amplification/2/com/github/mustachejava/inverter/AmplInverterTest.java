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
    public void testParser_literalMutationString2054_failAssert0_literalMutationString2465_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(" /[%>=nF*3F]rN8");
                Path file = getPath("This is a good day");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2054 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2054_failAssert0_literalMutationString2465 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  /[%>=nF*3F]rN8 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2047_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_literalMutationString2047 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2047_failAssert0null2902_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2047 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2047_failAssert0null2902 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2047_failAssert0_add2741_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2047 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2047_failAssert0_add2741 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString2047_failAssert0_literalMutationString2443_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("Thi is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                System.out.println(invert);
                org.junit.Assert.fail("testParser_literalMutationString2047 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testParser_literalMutationString2047_failAssert0_literalMutationString2443 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Thi is a good day not found", expected.getMessage());
        }
    }
}

