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
    public void testToJson_literalMutationString3537_failAssert0_add4274_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3537 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3537_failAssert0_add4274 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3537_failAssert0_add4270_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("This is a good day");
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3537 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3537_failAssert0_add4270 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3538_failAssert0_add4288_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("$=)>)R*_S#>]1NY");
                Mustache compile = dmf.compile("$=)>)R*_S#>]1NY");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3538 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3538_failAssert0_add4288 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template $=)>)R*_S#>]1NY not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3537_failAssert0null4434_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(null);
                org.junit.Assert.fail("testToJson_literalMutationString3537 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3537_failAssert0null4434 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3537_failAssert0null4432_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3537 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3537_failAssert0null4432 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3539_failAssert0_literalMutationString4108_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3539 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3539_failAssert0_literalMutationString4108 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3538_failAssert0_literalMutationString4063_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("$=)>)R*_S#>]1NY");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3538 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3538_failAssert0_literalMutationString4063 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template $=)>)R*_S#>]1NY not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3538_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("$=)>)R*_S#>]1NY");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson_literalMutationString3538 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template $=)>)R*_S#>]1NY not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3538_failAssert0null4449_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("$=)>)R*_S#>]1NY");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(null);
                org.junit.Assert.fail("testToJson_literalMutationString3538 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3538_failAssert0null4449 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template $=)>)R*_S#>]1NY not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3537_failAssert0_literalMutationString4008_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("This is a good day");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3537 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3537_failAssert0_literalMutationString4008 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3538_failAssert0_add4290_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("$=)>)R*_S#>]1NY");
                Path file = getPath("src/test/resources/fdbcli.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3538 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3538_failAssert0_add4290 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template $=)>)R*_S#>]1NY not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3537_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson_literalMutationString3537 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3537_failAssert0_literalMutationString4002_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("\\s+[0-9]+\\s+[0-9]+\\s+{{tag_device}} {{reads}} {{reads_merged}} {{sectors_read}} {{read_time}} {{writes}} {{writes_merged}} {{sectors_written}} {{write_time}} {{ios}} {{io_time}} {{weighted_io_time}}\n");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3537 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3537_failAssert0_literalMutationString4002 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template \\s+[0-9]+\\s+[0-9]+\\s+{{tag_device}} {{reads}} {{reads_merged}} {{sectors_read}} {{read_time}} {{writes}} {{writes_merged}} {{sectors_written}} {{write_time}} {{ios}} {{io_time}} {{weighted_io_time}}\n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3538_failAssert0_literalMutationString4057_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("$=)>)R*_S#>]1NY");
                Path file = getPath("This is a good day");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3538 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3538_failAssert0_literalMutationString4057 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template $=)>)R*_S#>]1NY not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4688_failAssert0_literalMutationString5270_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4688 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4688_failAssert0_literalMutationString5270 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4688_failAssert0_literalMutationString5272_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UT-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4688 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4688_failAssert0_literalMutationString5272 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4699_failAssert0_literalMutationString4999_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("_<w<UnR.|b(4?T[;");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4699 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4699_failAssert0_literalMutationString4999 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template _<w<UnR.|b(4?T[; not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4688_failAssert0null5615_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4688 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4688_failAssert0null5615 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4687_failAssert0_literalMutationString5189_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4687 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4687_failAssert0_literalMutationString5189 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4689_failAssert0_literalMutationString5115_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("kncOE-WqkQ n`Zd;");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4689 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4689_failAssert0_literalMutationString5115 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template kncOE-WqkQ n`Zd; not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4688_failAssert0_add5457_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("This is a good day");
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4688 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4688_failAssert0_add5457 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4689_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("kncOE-WqkQ n`Zd;");
            Path file = getPath("src/test/resources/fdbcli2.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson2_literalMutationString4689 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template kncOE-WqkQ n`Zd; not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4688_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli2.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson2_literalMutationString4688 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4689_failAssert0null5568_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("kncOE-WqkQ n`Zd;");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4689 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4689_failAssert0null5568 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template kncOE-WqkQ n`Zd; not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4689_failAssert0_literalMutationString5104_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("kncOE-WqzkQ n`Zd;");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4689 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4689_failAssert0_literalMutationString5104 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template kncOE-WqzkQ n`Zd; not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4695_failAssert0_literalMutationString5051_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("0P066r&&EZh3Y4}^");
                Path file = getPath("sr$c/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4695 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4695_failAssert0_literalMutationString5051 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 0P066r&&EZh3Y4}^ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4688_failAssert0_add5460_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4688 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4688_failAssert0_add5460 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4696_failAssert0_literalMutationString5068_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resoures/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4696 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4696_failAssert0_literalMutationString5068 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4689_failAssert0_add5405_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("kncOE-WqkQ n`Zd;");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4689 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4689_failAssert0_add5405 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template kncOE-WqkQ n`Zd; not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString4689_failAssert0_add5403_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("kncOE-WqkQ n`Zd;");
                Mustache compile = dmf.compile("kncOE-WqkQ n`Zd;");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString4689 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString4689_failAssert0_add5403 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template kncOE-WqkQ n`Zd; not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString2_failAssert0_literalMutationString457_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("w/UH97eDU`<Z64Uf5#");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString2_failAssert0_literalMutationString457 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template w/UH97eDU`<Z64Uf5# not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString14_failAssert0_literalMutationString404_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString14 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString14_failAssert0_literalMutationString404 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString6_failAssert0_add779_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("w>p3j%YTWlZYrDNWl");
                Mustache compile = dmf.compile("w>p3j%YTWlZYrDNWl");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString6_failAssert0_add779 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template w>p3j%YTWlZYrDNWl not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString11_literalMutationString216_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("c-ryjgO=vFn:UVY4f");
            Path file = getPath("src/test/resources//psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_literalMutationString11_literalMutationString216 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template c-ryjgO=vFn:UVY4f not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString2_failAssert0_add735_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString2 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString2_failAssert0_add735 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString6_failAssert0null935_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("w>p3j%YTWlZYrDNWl");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString6_failAssert0null935 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template w>p3j%YTWlZYrDNWl not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString6_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("w>p3j%YTWlZYrDNWl");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_literalMutationString6 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template w>p3j%YTWlZYrDNWl not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3null26_failAssert0_literalMutationString252_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testToJson3null26_failAssert0_literalMutationString252 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
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
    public void testToJson3_literalMutationString6_failAssert0_literalMutationString596_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("w>p3j%YTWlZYrDNWl");
                Path file = getPath("");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString6 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString6_failAssert0_literalMutationString596 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template w>p3j%YTWlZYrDNWl not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_add1188_literalMutationString1345_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile(">>i}z$}@a|#E,Z3q");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("Input text:[");
            System.out.print(txt);
            System.out.println("]");
            Node invert = compile.invert(txt);
            output(invert);
            output(invert);
            org.junit.Assert.fail("testToJson4_add1188_literalMutationString1345 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >>i}z$}@a|#E,Z3q not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1157_failAssert0_literalMutationString2279_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("\\s+[0-9]+\\s+[0-9]+\\s+{{tag_device}} {{reads}} {{reads_merged}} {{sectors_read}} {{read_time}} {{writes}} {{writes_merged}} {{sectors_written}} {{write_time}} {{ios}} {{io_time}} {{weighted_io_time}}\n");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString1157 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString1157_failAssert0_literalMutationString2279 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template \\s+[0-9]+\\s+[0-9]+\\s+{{tag_device}} {{reads}} {{reads_merged}} {{sectors_read}} {{read_time}} {{writes}} {{writes_merged}} {{sectors_written}} {{write_time}} {{ios}} {{io_time}} {{weighted_io_time}}\n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1159_failAssert0_literalMutationString2017_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString1159 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString1159_failAssert0_literalMutationString2017 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1167_failAssert0_literalMutationString2120_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("Sn@-/U)3Z,?E4<F_");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString1167 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString1167_failAssert0_literalMutationString2120 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Sn@-/U)3Z,?E4<F_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_remove1192_literalMutationString1600_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("mq3g:_..0P:(}>oP");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("Input text:[");
            System.out.print(txt);
            System.out.println("]");
            Node invert = compile.invert(txt);
            org.junit.Assert.fail("testToJson4_remove1192_literalMutationString1600 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mq3g:_..0P:(}>oP not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_remove1189_literalMutationString1552_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("fdbcli2.m]stache");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.print(txt);
            System.out.println("]");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_remove1189_literalMutationString1552 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli2.m]stache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1157_failAssert0_add2811_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString1157 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString1157_failAssert0_add2811 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1157_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testToJson4_literalMutationString1157 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString5853_failAssert0_literalMutationString6377_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli3[.mustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "ULTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString5853 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString5853_failAssert0_literalMutationString6377 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli3[.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add5857_literalMutationString5951_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            getPath("src/test/resources/fdbcli.txt");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_add5857_literalMutationString5951 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add5860_literalMutationString5933_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            output(invert);
            org.junit.Assert.fail("testToJson5_add5860_literalMutationString5933 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_remove5861_literalMutationString6036_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            org.junit.Assert.fail("testToJson5_remove5861_literalMutationString6036 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString5843_failAssert0_literalMutationString6229_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("Wd<{=Df`IjHpoEXL");
                Path file = getPath("src/test/resource/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString5843 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString5843_failAssert0_literalMutationString6229 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Wd<{=Df`IjHpoEXL not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString5842_failAssert0_literalMutationString6320_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString5842 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString5842_failAssert0_literalMutationString6320 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString5843_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("Wd<{=Df`IjHpoEXL");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_literalMutationString5843 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Wd<{=Df`IjHpoEXL not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add5859_literalMutationString5998_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node o_testToJson5_add5859__10 = compile.invert(txt);
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_add5859_literalMutationString5998 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString5841_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_literalMutationString5841 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString5843_failAssert0_add6547_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("Wd<{=Df`IjHpoEXL");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString5843 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString5843_failAssert0_add6547 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Wd<{=Df`IjHpoEXL not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString5850_failAssert0_literalMutationString6413_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("1QZ[3H2`/:xQOblS");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "!8T$B");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString5850 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString5850_failAssert0_literalMutationString6413 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 1QZ[3H2`/:xQOblS not found", expected.getMessage());
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

