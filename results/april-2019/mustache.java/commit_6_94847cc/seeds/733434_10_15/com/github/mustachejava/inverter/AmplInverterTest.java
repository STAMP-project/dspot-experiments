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
    public void testParser_literalMutationString5_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_literalMutationString5 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testParser_literalMutationString1_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("vTZbrJ/Lxa_Zk|]");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            System.out.println(invert);
            org.junit.Assert.fail("testParser_literalMutationString1 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vTZbrJ/Lxa_Zk|] not found", expected.getMessage());
        }
    }
}

