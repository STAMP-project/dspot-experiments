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
    public void testToJson_literalMutationString3975_failAssert0_literalMutationString4366_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli]mustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTCF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3975 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3975_failAssert0_literalMutationString4366 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli]mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJsonnull4000_failAssert0_literalMutationString4220_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJsonnull4000 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJsonnull4000_failAssert0_literalMutationString4220 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3976_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson_literalMutationString3976 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_add3993_literalMutationString4105_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("U3{:i+eYB4I+<mi");
            getPath("src/test/resources/fdbcli.txt");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson_add3993_literalMutationString4105 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template U3{:i+eYB4I+<mi not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_add3992_literalMutationString4148_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache o_testToJson_add3992__3 = dmf.compile(";QLK<z!q/0F-g3P");
            Mustache compile = dmf.compile("fdbcli.mustache");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson_add3992_literalMutationString4148 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ;QLK<z!q/0F-g3P not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3979_failAssert0null4842_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("AXI+.&>]-,-zR{>");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3979 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3979_failAssert0null4842 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AXI+.&>]-,-zR{> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3975_failAssert0_add4681_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli]mustache");
                getPath("src/test/resources/fdbcli.txt");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3975 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3975_failAssert0_add4681 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli]mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3975_failAssert0null4846_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli]mustache");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3975 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3975_failAssert0null4846 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli]mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3982_failAssert0_literalMutationString4494_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("_-=(*,w{w2dahFT");
                Path file = getPath("This is a good day");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3982 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3982_failAssert0_literalMutationString4494 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template _-=(*,w{w2dahFT not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3976_failAssert0null4872_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3976 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3976_failAssert0null4872 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3976_failAssert0null4874_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(null);
                org.junit.Assert.fail("testToJson_literalMutationString3976 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3976_failAssert0null4874 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3979_failAssert0_add4676_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("AXI+.&>]-,-zR{>");
                Path file = getPath("src/test/resources/fdbcli.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3979 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3979_failAssert0_add4676 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AXI+.&>]-,-zR{> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3979_failAssert0_add4674_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("AXI+.&>]-,-zR{>");
                Mustache compile = dmf.compile("AXI+.&>]-,-zR{>");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3979 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3979_failAssert0_add4674 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AXI+.&>]-,-zR{> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3979_failAssert0_literalMutationString4351_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("AXI+.&>]-,-zR{>");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UT/-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3979 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3979_failAssert0_literalMutationString4351 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AXI+.&>]-,-zR{> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3979_failAssert0_literalMutationString4342_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("AXI+.&>]-,-zR{>");
                Path file = getPath("src/test/resources/fdbclZ.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3979 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3979_failAssert0_literalMutationString4342 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AXI+.&>]-,-zR{> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3976_failAssert0_add4712_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3976 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3976_failAssert0_add4712 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3976_failAssert0_add4715_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3976 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3976_failAssert0_add4715 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_remove3997_literalMutationString4172_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            org.junit.Assert.fail("testToJson_remove3997_literalMutationString4172 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_add3996_literalMutationString4087_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            output(invert);
            org.junit.Assert.fail("testToJson_add3996_literalMutationString4087 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3975_failAssert0_add4684_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli]mustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3975 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3975_failAssert0_add4684 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli]mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3979_failAssert0null4844_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("AXI+.&>]-,-zR{>");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(null);
                org.junit.Assert.fail("testToJson_literalMutationString3979 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3979_failAssert0null4844 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AXI+.&>]-,-zR{> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3976_failAssert0_literalMutationString4451_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson_literalMutationString3976 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson_literalMutationString3976_failAssert0_literalMutationString4451 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3975_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("fdbcli]mustache");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson_literalMutationString3975 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli]mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson_literalMutationString3979_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("AXI+.&>]-,-zR{>");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson_literalMutationString3979 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AXI+.&>]-,-zR{> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5337_failAssert0_add6054_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5337 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5337_failAssert0_add6054 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5340_failAssert0_literalMutationString5771_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5340 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5340_failAssert0_literalMutationString5771 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5337_failAssert0_add6056_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5337 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5337_failAssert0_add6056 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5338_failAssert0null6262_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("!+_x)VhJ[<)(8g5_");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5338 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5338_failAssert0null6262 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !+_x)VhJ[<)(8g5_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5338_failAssert0_add6108_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("!+_x)VhJ[<)(8g5_");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5338 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5338_failAssert0_add6108 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !+_x)VhJ[<)(8g5_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5338_failAssert0_add6106_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("!+_x)VhJ[<)(8g5_");
                Mustache compile = dmf.compile("!+_x)VhJ[<)(8g5_");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5338 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5338_failAssert0_add6106 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !+_x)VhJ[<)(8g5_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5338_failAssert0_literalMutationString5905_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("!+_x)VhJ[<)(8g_");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5338 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5338_failAssert0_literalMutationString5905 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !+_x)VhJ[<)(8g_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5345_failAssert0_literalMutationString5646_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resoTurces/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5345 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5345_failAssert0_literalMutationString5646 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5337_failAssert0null6216_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5337 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5337_failAssert0null6216 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5338_failAssert0_add6109_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("!+_x)VhJ[<)(8g5_");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5338 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5338_failAssert0_add6109 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !+_x)VhJ[<)(8g5_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5338_failAssert0null6263_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("!+_x)VhJ[<)(8g5_");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5338 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5338_failAssert0null6263 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !+_x)VhJ[<)(8g5_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2null5359_failAssert0_literalMutationString5586_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("9eEE|2mQD? .Kabw");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2null5359 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson2null5359_failAssert0_literalMutationString5586 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 9eEE|2mQD? .Kabw not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5346_failAssert0_literalMutationString5887_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "37n>M");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5346 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5346_failAssert0_literalMutationString5887 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5350_failAssert0_literalMutationString5923_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("S_$Wk.a3rnIFmb^:");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5350 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5350_failAssert0_literalMutationString5923 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template S_$Wk.a3rnIFmb^: not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5338_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("!+_x)VhJ[<)(8g5_");
            Path file = getPath("src/test/resources/fdbcli2.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson2_literalMutationString5338 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !+_x)VhJ[<)(8g5_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_add5355_literalMutationString5513_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("|3SV! R|%sf]IU-7");
            Path file = getPath("src/test/resources/fdbcli2.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node o_testToJson2_add5355__10 = compile.invert(txt);
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson2_add5355_literalMutationString5513 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template |3SV! R|%sf]IU-7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5337_failAssert0_literalMutationString5768_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "f,G^i");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5337 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5337_failAssert0_literalMutationString5768 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5337_failAssert0_literalMutationString5760_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("This is a good day");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5337 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5337_failAssert0_literalMutationString5760 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5338_failAssert0_literalMutationString5917_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("!+_x)VhJ[<)(8g5_");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTFu-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5338 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5338_failAssert0_literalMutationString5917 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !+_x)VhJ[<)(8g5_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5347_failAssert0_literalMutationString5824_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(";Uo`vfxT,+-_6P1^");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5347 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5347_failAssert0_literalMutationString5824 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ;Uo`vfxT,+-_6P1^ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5338_failAssert0_literalMutationString5907_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5338 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5338_failAssert0_literalMutationString5907 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5337_failAssert0null6218_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5337 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5337_failAssert0null6218 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5342_failAssert0_literalMutationString5717_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli2.mu<tache");
                Path file = getPath("src/test/resources/fdbcl<2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5342 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5342_failAssert0_literalMutationString5717 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli2.mu<tache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5337_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli2.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson2_literalMutationString5337 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson2_literalMutationString5341_failAssert0_literalMutationString5735_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/est/resources/fdbcli2.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson2_literalMutationString5341 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson2_literalMutationString5341_failAssert0_literalMutationString5735 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString8_failAssert0_literalMutationString491_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath(":^F)m.mzQ]W_/.]uJQ6BSa]FJC5b).%");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString8 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString8_failAssert0_literalMutationString491 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_remove24_literalMutationString202_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("Bat^&0d$[[T`Z)>W7");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            org.junit.Assert.fail("testToJson3_remove24_literalMutationString202 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Bat^&0d$[[T`Z)>W7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString15_literalMutationString216_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("tbq}@$v8+p#}QdXKO");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_literalMutationString15_literalMutationString216 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tbq}@$v8+p#}QdXKO not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3null28_failAssert0_literalMutationString268_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile(",+5w` Wso(/l}0#H/");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson3null28 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson3null28_failAssert0_literalMutationString268 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ,+5w` Wso(/l}0#H/ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_add712_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_add712 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString9_failAssert0_literalMutationString595_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("Ma^cCfMt5w!xArJY>");
                Path file = getPath("This is a good day");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString9 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString9_failAssert0_literalMutationString595 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Ma^cCfMt5w!xArJY> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_add19_literalMutationString158_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache o_testToJson3_add19__3 = dmf.compile("This is a good day");
            Mustache compile = dmf.compile("psauxwww.mustache");
            Path file = getPath("src/test/resources/psauxwww.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson3_add19_literalMutationString158 should have thrown MustacheNotFoundException");
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
    public void testToJson3_literalMutationString4_failAssert0null877_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(null);
                org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0null877 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_add709_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                Files.readAllBytes(file);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_add709 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3null29_failAssert0_literalMutationString247_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testToJson3null29_failAssert0_literalMutationString247 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString3_failAssert0_literalMutationString311_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fq)AC[*sH&-L>Vw0r");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString3_failAssert0_literalMutationString311 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fq)AC[*sH&-L>Vw0r not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString16_failAssert0_literalMutationString329_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("/Jl,-UOKu7Q!mmU] ");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTFv-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString16 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString16_failAssert0_literalMutationString329 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template /Jl,-UOKu7Q!mmU]  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3null26_failAssert0_literalMutationString301_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("E5Ho5H*K|1lmo&fl2");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3null26 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson3null26_failAssert0_literalMutationString301 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template E5Ho5H*K|1lmo&fl2 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3null28_failAssert0_literalMutationString265_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson3null28 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson3null28_failAssert0_literalMutationString265 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson3_literalMutationString4_failAssert0_literalMutationString385_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a goodday");
                Path file = getPath("src/test/resources/psauxwww.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson3_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson3_literalMutationString4_failAssert0_literalMutationString385 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a goodday not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1379_literalMutationString2047_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("");
            System.out.print(txt);
            System.out.println("]");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_literalMutationString1379_literalMutationString2047 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1365_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("]mxdzPrW4{6-JI Z");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("Input text:[");
            System.out.print(txt);
            System.out.println("]");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_literalMutationString1365 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ]mxdzPrW4{6-JI Z not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1363_failAssert0() throws IOException {
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
            org.junit.Assert.fail("testToJson4_literalMutationString1363 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1364_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("fdbcli2^.mustache");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("Input text:[");
            System.out.print(txt);
            System.out.println("]");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_literalMutationString1364 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli2^.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1364_failAssert0_add3079_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli2^.mustache");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString1364 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString1364_failAssert0_add3079 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli2^.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1363_failAssert0_add3039_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("This is a good day");
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString1363 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString1363_failAssert0_add3039 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1387_literalMutationString1889_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("Input text:[");
            System.out.print(txt);
            System.out.println("M");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_literalMutationString1387_literalMutationString1889 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1365_failAssert0_add2990_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("]mxdzPrW4{6-JI Z");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString1365 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString1365_failAssert0_add2990 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ]mxdzPrW4{6-JI Z not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1374_failAssert0_literalMutationString2621_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UNj=3");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString1374 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString1374_failAssert0_literalMutationString2621 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1370_failAssert0_literalMutationString2514_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/tesc/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString1370 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString1370_failAssert0_literalMutationString2514 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1364_failAssert0_literalMutationString2677_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testToJson4_literalMutationString1364 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString1364_failAssert0_literalMutationString2677 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1364_failAssert0null3523_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdbcli2^.mustache");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("]");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString1364 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString1364_failAssert0null3523 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli2^.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_add1393_literalMutationString1672_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli3.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            System.out.println("Input text:[");
            System.out.print(txt);
            System.out.println("]");
            System.out.println("]");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson4_add1393_literalMutationString1672 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson4_literalMutationString1363_failAssert0_literalMutationString2590_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli3.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                System.out.println("Input text:[");
                System.out.print(txt);
                System.out.println("This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson4_literalMutationString1363 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson4_literalMutationString1363_failAssert0_literalMutationString2590 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5null6718_failAssert0_literalMutationString6983_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5null6718 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testToJson5null6718_failAssert0_literalMutationString6983 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6695_failAssert0null7563_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("%S&8?.#teBI27%uk");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6695 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6695_failAssert0null7563 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template %S&8?.#teBI27%uk not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6698_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("fdb%li3.mustache");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_literalMutationString6698 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdb%li3.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6698_failAssert0_add7388_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdb%li3.mustache");
                getPath("src/test/resources/fdbcli.txt");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6698 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6698_failAssert0_add7388 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdb%li3.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6698_failAssert0null7557_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdb%li3.mustache");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6698 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6698_failAssert0null7557 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdb%li3.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6698_failAssert0_literalMutationString7050_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdb%li3.mustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UT;F-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6698 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6698_failAssert0_literalMutationString7050 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdb%li3.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6695_failAssert0_add7393_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("%S&8?.#teBI27%uk");
                Mustache compile = dmf.compile("%S&8?.#teBI27%uk");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6695 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6695_failAssert0_add7393 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template %S&8?.#teBI27%uk not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6695_failAssert0null7564_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("%S&8?.#teBI27%uk");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(null);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6695 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6695_failAssert0null7564 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template %S&8?.#teBI27%uk not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6697_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_literalMutationString6697 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6699_failAssert0_literalMutationString7219_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("S1XM=ZG_e+hyn[ss");
                Path file = getPath("src/test/resources/fdbcli.%xt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6699 should have thrown NoSuchFileException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6699_failAssert0_literalMutationString7219 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template S1XM=ZG_e+hyn[ss not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6698_failAssert0_literalMutationString7051_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdb%li3.mustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6698 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6698_failAssert0_literalMutationString7051 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdb%li3.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6697_failAssert0null7622_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath(null);
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6697 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6697_failAssert0null7622 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6697_failAssert0null7623_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6697 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6697_failAssert0null7623 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6695_failAssert0_literalMutationString7059_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("%S&,?.#teBI27%uk");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6695 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6695_failAssert0_literalMutationString7059 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template %S&,?.#teBI27%uk not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6697_failAssert0_add7470_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6697 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6697_failAssert0_add7470 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6697_failAssert0_add7465_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.compile("This is a good day");
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6697 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6697_failAssert0_add7465 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6695_failAssert0_literalMutationString7071_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("%S&8?.#teBI27%uk");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6695 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6695_failAssert0_literalMutationString7071 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template %S&8?.#teBI27%uk not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6697_failAssert0_literalMutationString7272_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("U=x>FK Y@Y&=2S^gN,_T5Q|g&vIH_");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6697 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6697_failAssert0_literalMutationString7272 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add6711_literalMutationString6832_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache o_testToJson5_add6711__3 = dmf.compile("This is a good day");
            Mustache compile = dmf.compile("fdbcli3.mustache");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_add6711_literalMutationString6832 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add6715_literalMutationString6876_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("fdbcli3.mustac}he");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            output(invert);
            org.junit.Assert.fail("testToJson5_add6715_literalMutationString6876 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdbcli3.mustac}he not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add6713_literalMutationString6856_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            Files.readAllBytes(file);
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_add6713_literalMutationString6856 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6695_failAssert0_add7396_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("%S&8?.#teBI27%uk");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6695 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6695_failAssert0_add7396 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template %S&8?.#teBI27%uk not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6694_literalMutationString6928_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_literalMutationString6694_literalMutationString6928 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6695_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("%S&8?.#teBI27%uk");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_literalMutationString6695 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template %S&8?.#teBI27%uk not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6698_failAssert0_add7390_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdb%li3.mustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "UTF-8");
                compile.invert(txt);
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6698 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6698_failAssert0_add7390 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdb%li3.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6698_failAssert0null7558_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("fdb%li3.mustache");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(null), "UTF-8");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6698 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6698_failAssert0null7558 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fdb%li3.mustache not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_add6714_literalMutationString6814_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            Mustache compile = dmf.compile("This is a good day");
            Path file = getPath("src/test/resources/fdbcli.txt");
            String txt = new String(Files.readAllBytes(file), "UTF-8");
            Node o_testToJson5_add6714__10 = compile.invert(txt);
            Node invert = compile.invert(txt);
            output(invert);
            org.junit.Assert.fail("testToJson5_add6714_literalMutationString6814 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template This is a good day not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToJson5_literalMutationString6697_failAssert0_literalMutationString7279_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                Mustache compile = dmf.compile("This is a good day");
                Path file = getPath("src/test/resources/fdbcli.txt");
                String txt = new String(Files.readAllBytes(file), "This is a good day");
                Node invert = compile.invert(txt);
                output(invert);
                org.junit.Assert.fail("testToJson5_literalMutationString6697 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testToJson5_literalMutationString6697_failAssert0_literalMutationString7279 should have thrown MustacheNotFoundException");
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

