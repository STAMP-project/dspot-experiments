package com.github.mustachejava;


import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import org.junit.Assert;
import org.junit.Test;


public class AmplDelimiterTest {
    @Test(timeout = 10000)
    public void testWithTemplateFunction2_literalMutationString6152_literalMutationString6611_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            Mustache maven = mf.compile(new StringReader("{{=toolyn}}"), "maven", "{{=toolyn}}", "}");
            StringWriter sw = new StringWriter();
            maven.execute(sw, new Object() {
                TemplateFunction foo = ( s) -> "${name}";

                String name = "Jason";
            }).close();
            sw.toString();
            org.junit.Assert.fail("testWithTemplateFunction2_literalMutationString6152_literalMutationString6611 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[maven:1]", expected.getMessage());
        }
    }

    private static class NoEncodingMustacheFactory extends DefaultMustacheFactory {
        @Override
        public void encode(String value, Writer writer) {
            try {
                writer.write(value);
            } catch (IOException e) {
                throw new MustacheException(e);
            }
        }
    }
}

