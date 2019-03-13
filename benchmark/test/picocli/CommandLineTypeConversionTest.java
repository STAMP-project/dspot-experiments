/**
 * Copyright 2017 Remko Popma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package picocli;


import CommandLine.ParameterException;
import CommandLine.Tracer;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URL;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.rules.TestRule;


public class CommandLineTypeConversionTest {
    // allows tests to set any kind of properties they like, without having to individually roll them back
    @Rule
    public final TestRule restoreSystemProperties = new RestoreSystemProperties();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    static class SupportedTypes {
        @Option(names = "-boolean")
        boolean booleanField;

        @Option(names = "-Boolean")
        Boolean aBooleanField;

        @Option(names = "-byte")
        byte byteField;

        @Option(names = "-Byte")
        Byte aByteField;

        @Option(names = "-char")
        char charField;

        @Option(names = "-Character")
        Character aCharacterField;

        @Option(names = "-short")
        short shortField;

        @Option(names = "-Short")
        Short aShortField;

        @Option(names = "-int")
        int intField;

        @Option(names = "-Integer")
        Integer anIntegerField;

        @Option(names = "-long")
        long longField;

        @Option(names = "-Long")
        Long aLongField;

        @Option(names = "-float")
        float floatField;

        @Option(names = "-Float")
        Float aFloatField;

        @Option(names = "-double")
        double doubleField;

        @Option(names = "-Double")
        Double aDoubleField;

        @Option(names = "-String")
        String aStringField;

        @Option(names = "-StringBuilder")
        StringBuilder aStringBuilderField;

        @Option(names = "-CharSequence")
        CharSequence aCharSequenceField;

        @Option(names = "-File")
        File aFileField;

        @Option(names = "-URL")
        URL anURLField;

        @Option(names = "-URI")
        URI anURIField;

        @Option(names = "-Date")
        Date aDateField;

        @Option(names = "-Time")
        Time aTimeField;

        @Option(names = "-BigDecimal")
        BigDecimal aBigDecimalField;

        @Option(names = "-BigInteger")
        BigInteger aBigIntegerField;

        @Option(names = "-Charset")
        Charset aCharsetField;

        @Option(names = "-InetAddress")
        InetAddress anInetAddressField;

        @Option(names = "-Pattern")
        Pattern aPatternField;

        @Option(names = "-UUID")
        UUID anUUIDField;

        @Option(names = "-Currency")
        Currency aCurrencyField;

        @Option(names = "-tz")
        TimeZone aTimeZone;

        @Option(names = "-byteOrder")
        ByteOrder aByteOrder;

        @Option(names = "-Class")
        Class<?> aClass;

        @Option(names = "-Connection")
        Connection aConnection;

        @Option(names = "-Driver")
        Driver aDriver;

        @Option(names = "-Timestamp")
        Timestamp aTimestamp;

        @Option(names = "-NetworkInterface")
        NetworkInterface aNetInterface;
    }

    @Test
    public void testDefaults() {
        CommandLineTypeConversionTest.SupportedTypes bean = CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes());
        Assert.assertEquals("boolean", false, bean.booleanField);
        Assert.assertEquals("Boolean", null, bean.aBooleanField);
        Assert.assertEquals("byte", 0, bean.byteField);
        Assert.assertEquals("Byte", null, bean.aByteField);
        Assert.assertEquals("char", 0, bean.charField);
        Assert.assertEquals("Character", null, bean.aCharacterField);
        Assert.assertEquals("short", 0, bean.shortField);
        Assert.assertEquals("Short", null, bean.aShortField);
        Assert.assertEquals("int", 0, bean.intField);
        Assert.assertEquals("Integer", null, bean.anIntegerField);
        Assert.assertEquals("long", 0, bean.longField);
        Assert.assertEquals("Long", null, bean.aLongField);
        Assert.assertEquals("float", 0.0F, bean.floatField, Float.MIN_VALUE);
        Assert.assertEquals("Float", null, bean.aFloatField);
        Assert.assertEquals("double", 0.0, bean.doubleField, Double.MIN_VALUE);
        Assert.assertEquals("Double", null, bean.aDoubleField);
        Assert.assertEquals("String", null, bean.aStringField);
        Assert.assertEquals("StringBuilder", null, bean.aStringBuilderField);
        Assert.assertEquals("CharSequence", null, bean.aCharSequenceField);
        Assert.assertEquals("File", null, bean.aFileField);
        Assert.assertEquals("URL", null, bean.anURLField);
        Assert.assertEquals("URI", null, bean.anURIField);
        Assert.assertEquals("Date", null, bean.aDateField);
        Assert.assertEquals("Time", null, bean.aTimeField);
        Assert.assertEquals("BigDecimal", null, bean.aBigDecimalField);
        Assert.assertEquals("BigInteger", null, bean.aBigIntegerField);
        Assert.assertEquals("Charset", null, bean.aCharsetField);
        Assert.assertEquals("InetAddress", null, bean.anInetAddressField);
        Assert.assertEquals("Pattern", null, bean.aPatternField);
        Assert.assertEquals("UUID", null, bean.anUUIDField);
        Assert.assertEquals("Currency", null, bean.aCurrencyField);
        Assert.assertEquals("TimeZone", null, bean.aTimeZone);
        Assert.assertEquals("ByteOrder", null, bean.aByteOrder);
        Assert.assertEquals("Class", null, bean.aClass);
        Assert.assertEquals("NetworkInterface", null, bean.aNetInterface);
        Assert.assertEquals("Connection", null, bean.aConnection);
        Assert.assertEquals("Driver", null, bean.aDriver);
        Assert.assertEquals("Timestamp", null, bean.aTimestamp);
    }

    @Test
    public void testTypeConversionSucceedsForValidInput() throws Exception {
        // Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        CommandLineTypeConversionTest.SupportedTypes bean = // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // ,
        // "-Connection", "jdbc:derby:testDB;create=false",
        // "-Driver", "org.apache.derby.jdbc.EmbeddedDriver"
        CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-boolean", "-Boolean", "-byte", "12", "-Byte", "23", "-char", "p", "-Character", "i", "-short", "34", "-Short", "45", "-int", "56", "-Integer", "67", "-long", "78", "-Long", "89", "-float", "1.23", "-Float", "2.34", "-double", "3.45", "-Double", "4.56", "-String", "abc", "-StringBuilder", "bcd", "-CharSequence", "xyz", "-File", "abc.txt", "-URL", "http://pico-cli.github.io", "-URI", "http://pico-cli.github.io/index.html", "-Date", "2017-01-30", "-Time", "23:59:59", "-BigDecimal", "12345678901234567890.123", "-BigInteger", "123456789012345678901", "-Charset", "UTF8", "-InetAddress", InetAddress.getLocalHost().getHostName(), "-Pattern", "a*b", "-UUID", "c7d51423-bf9d-45dd-a30d-5b16fafe42e2", "-Currency", "EUR", "-tz", "Japan/Tokyo", "-byteOrder", "LITTLE_ENDIAN", "-Class", "java.lang.String", "-NetworkInterface", "127.0.0.0", "-Timestamp", "2017-12-13 13:59:59.123456789");
        Assert.assertEquals("boolean", true, bean.booleanField);
        Assert.assertEquals("Boolean", Boolean.TRUE, bean.aBooleanField);
        Assert.assertEquals("byte", 12, bean.byteField);
        Assert.assertEquals("Byte", Byte.valueOf(((byte) (23))), bean.aByteField);
        Assert.assertEquals("char", 'p', bean.charField);
        Assert.assertEquals("Character", Character.valueOf('i'), bean.aCharacterField);
        Assert.assertEquals("short", 34, bean.shortField);
        Assert.assertEquals("Short", Short.valueOf(((short) (45))), bean.aShortField);
        Assert.assertEquals("int", 56, bean.intField);
        Assert.assertEquals("Integer", Integer.valueOf(67), bean.anIntegerField);
        Assert.assertEquals("long", 78L, bean.longField);
        Assert.assertEquals("Long", Long.valueOf(89L), bean.aLongField);
        Assert.assertEquals("float", 1.23F, bean.floatField, Float.MIN_VALUE);
        Assert.assertEquals("Float", Float.valueOf(2.34F), bean.aFloatField);
        Assert.assertEquals("double", 3.45, bean.doubleField, Double.MIN_VALUE);
        Assert.assertEquals("Double", Double.valueOf(4.56), bean.aDoubleField);
        Assert.assertEquals("String", "abc", bean.aStringField);
        Assert.assertEquals("StringBuilder type", StringBuilder.class, bean.aStringBuilderField.getClass());
        Assert.assertEquals("StringBuilder", "bcd", bean.aStringBuilderField.toString());
        Assert.assertEquals("CharSequence", "xyz", bean.aCharSequenceField);
        Assert.assertEquals("File", new File("abc.txt"), bean.aFileField);
        Assert.assertEquals("URL", new URL("http://pico-cli.github.io"), bean.anURLField);
        Assert.assertEquals("URI", new URI("http://pico-cli.github.io/index.html"), bean.anURIField);
        Assert.assertEquals("Date", new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-30"), bean.aDateField);
        Assert.assertEquals("Time", new Time(new SimpleDateFormat("HH:mm:ss").parse("23:59:59").getTime()), bean.aTimeField);
        Assert.assertEquals("BigDecimal", new BigDecimal("12345678901234567890.123"), bean.aBigDecimalField);
        Assert.assertEquals("BigInteger", new BigInteger("123456789012345678901"), bean.aBigIntegerField);
        Assert.assertEquals("Charset", Charset.forName("UTF8"), bean.aCharsetField);
        Assert.assertEquals("InetAddress", InetAddress.getByName(InetAddress.getLocalHost().getHostName()), bean.anInetAddressField);
        Assert.assertEquals("Pattern", Pattern.compile("a*b").pattern(), bean.aPatternField.pattern());
        Assert.assertEquals("UUID", UUID.fromString("c7d51423-bf9d-45dd-a30d-5b16fafe42e2"), bean.anUUIDField);
        Assert.assertEquals("Currency", Currency.getInstance("EUR"), bean.aCurrencyField);
        Assert.assertEquals("TimeZone", TimeZone.getTimeZone("Japan/Tokyo"), bean.aTimeZone);
        Assert.assertEquals("ByteOrder", ByteOrder.LITTLE_ENDIAN, bean.aByteOrder);
        Assert.assertEquals("Class", String.class, bean.aClass);
        Assert.assertEquals("NetworkInterface", NetworkInterface.getByInetAddress(InetAddress.getByName("127.0.0.0")), bean.aNetInterface);
        Assert.assertEquals("Timestamp", Timestamp.valueOf("2017-12-13 13:59:59.123456789"), bean.aTimestamp);
        // assertEquals("Connection", DriverManager.getConnection("jdbc:derby:testDB;create=false"), bean.aConnection);
        // assertEquals("Driver", DriverManager.getDriver("org.apache.derby.jdbc.EmbeddedDriver"), bean.aDriver);
    }

    @Test
    public void testTypeConversionSucceedsForAlternativeValidInput() throws Exception {
        CommandLineTypeConversionTest.SupportedTypes bean = // 
        CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-byteOrder", "BIG_ENDIAN", "-NetworkInterface", "invalid;`!");
        Assert.assertEquals("ByteOrder", ByteOrder.BIG_ENDIAN, bean.aByteOrder);
        Assert.assertEquals("NetworkInterface", null, bean.aNetInterface);
    }

    @Test
    public void testByteFieldsAreDecimal() {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-byte", "0x1F", "-Byte", "0x0F");
            Assert.fail("Should fail on hex input");
        } catch (CommandLine expected) {
            Assert.assertEquals("Invalid value for option '-byte': '0x1F' is not a byte", expected.getMessage());
        }
    }

    @Test
    public void testCustomByteConverterAcceptsHexadecimalDecimalAndOctal() {
        CommandLineTypeConversionTest.SupportedTypes bean = new CommandLineTypeConversionTest.SupportedTypes();
        CommandLine commandLine = new CommandLine(bean);
        ITypeConverter<Byte> converter = new ITypeConverter<Byte>() {
            public Byte convert(String s) {
                return Byte.decode(s);
            }
        };
        commandLine.registerConverter(Byte.class, converter);
        commandLine.registerConverter(Byte.TYPE, converter);
        commandLine.parse("-byte", "0x1F", "-Byte", "0x0F");
        Assert.assertEquals(31, bean.byteField);
        Assert.assertEquals(Byte.valueOf(((byte) (15))), bean.aByteField);
        commandLine.parse("-byte", "010", "-Byte", "010");
        Assert.assertEquals(8, bean.byteField);
        Assert.assertEquals(Byte.valueOf(((byte) (8))), bean.aByteField);
        commandLine.parse("-byte", "34", "-Byte", "34");
        Assert.assertEquals(34, bean.byteField);
        Assert.assertEquals(Byte.valueOf(((byte) (34))), bean.aByteField);
    }

    @Test
    public void testShortFieldsAreDecimal() {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-short", "0xFF", "-Short", "0x6FFE");
            Assert.fail("Should fail on hex input");
        } catch (CommandLine expected) {
            Assert.assertEquals("Invalid value for option '-short': '0xFF' is not a short", expected.getMessage());
        }
    }

    @Test
    public void testCustomShortConverterAcceptsHexadecimalDecimalAndOctal() {
        CommandLineTypeConversionTest.SupportedTypes bean = new CommandLineTypeConversionTest.SupportedTypes();
        CommandLine commandLine = new CommandLine(bean);
        ITypeConverter<Short> shortConverter = new ITypeConverter<Short>() {
            public Short convert(String s) {
                return Short.decode(s);
            }
        };
        commandLine.registerConverter(Short.class, shortConverter);
        commandLine.registerConverter(Short.TYPE, shortConverter);
        commandLine.parse("-short", "0xFF", "-Short", "0x6FFE");
        Assert.assertEquals(255, bean.shortField);
        Assert.assertEquals(Short.valueOf(((short) (28670))), bean.aShortField);
        commandLine.parse("-short", "010", "-Short", "010");
        Assert.assertEquals(8, bean.shortField);
        Assert.assertEquals(Short.valueOf(((short) (8))), bean.aShortField);
        commandLine.parse("-short", "34", "-Short", "34");
        Assert.assertEquals(34, bean.shortField);
        Assert.assertEquals(Short.valueOf(((short) (34))), bean.aShortField);
    }

    @Test
    public void testIntFieldsAreDecimal() {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-int", "0xFF", "-Integer", "0xFFFF");
            Assert.fail("Should fail on hex input");
        } catch (CommandLine expected) {
            Assert.assertEquals("Invalid value for option '-int': '0xFF' is not an int", expected.getMessage());
        }
    }

    @Test
    public void testCustomIntConverterAcceptsHexadecimalDecimalAndOctal() {
        CommandLineTypeConversionTest.SupportedTypes bean = new CommandLineTypeConversionTest.SupportedTypes();
        CommandLine commandLine = new CommandLine(bean);
        ITypeConverter<Integer> intConverter = new ITypeConverter<Integer>() {
            public Integer convert(String s) {
                return Integer.decode(s);
            }
        };
        commandLine.registerConverter(Integer.class, intConverter);
        commandLine.registerConverter(Integer.TYPE, intConverter);
        commandLine.parse("-int", "0xFF", "-Integer", "0xFFFF");
        Assert.assertEquals(255, bean.intField);
        Assert.assertEquals(Integer.valueOf(65535), bean.anIntegerField);
        commandLine.parse("-int", "010", "-Integer", "010");
        Assert.assertEquals(8, bean.intField);
        Assert.assertEquals(Integer.valueOf(8), bean.anIntegerField);
        commandLine.parse("-int", "34", "-Integer", "34");
        Assert.assertEquals(34, bean.intField);
        Assert.assertEquals(Integer.valueOf(34), bean.anIntegerField);
    }

    @Test
    public void testLongFieldsAreDecimal() {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-long", "0xAABBCC", "-Long", "0xAABBCCDD");
            Assert.fail("Should fail on hex input");
        } catch (CommandLine expected) {
            Assert.assertEquals("Invalid value for option '-long': '0xAABBCC' is not a long", expected.getMessage());
        }
    }

    @Test
    public void testCustomLongConverterAcceptsHexadecimalDecimalAndOctal() {
        CommandLineTypeConversionTest.SupportedTypes bean = new CommandLineTypeConversionTest.SupportedTypes();
        CommandLine commandLine = new CommandLine(bean);
        ITypeConverter<Long> longConverter = new ITypeConverter<Long>() {
            public Long convert(String s) {
                return Long.decode(s);
            }
        };
        commandLine.registerConverter(Long.class, longConverter);
        commandLine.registerConverter(Long.TYPE, longConverter);
        commandLine.parse("-long", "0xAABBCC", "-Long", "0xAABBCCDD");
        Assert.assertEquals(11189196, bean.longField);
        Assert.assertEquals(Long.valueOf(2864434397L), bean.aLongField);
        commandLine.parse("-long", "010", "-Long", "010");
        Assert.assertEquals(8, bean.longField);
        Assert.assertEquals(Long.valueOf(8), bean.aLongField);
        commandLine.parse("-long", "34", "-Long", "34");
        Assert.assertEquals(34, bean.longField);
        Assert.assertEquals(Long.valueOf(34), bean.aLongField);
    }

    @Test
    public void testTimeFormatHHmmSupported() throws ParseException {
        CommandLineTypeConversionTest.SupportedTypes bean = CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-Time", "23:59");
        Assert.assertEquals("Time", new Time(new SimpleDateFormat("HH:mm").parse("23:59").getTime()), bean.aTimeField);
    }

    @Test
    public void testTimeFormatHHmmssSupported() throws ParseException {
        CommandLineTypeConversionTest.SupportedTypes bean = CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-Time", "23:59:58");
        Assert.assertEquals("Time", new Time(new SimpleDateFormat("HH:mm:ss").parse("23:59:58").getTime()), bean.aTimeField);
    }

    @Test
    public void testTimeFormatHHmmssDotSSSSupported() throws ParseException {
        CommandLineTypeConversionTest.SupportedTypes bean = CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-Time", "23:59:58.123");
        Assert.assertEquals("Time", new Time(new SimpleDateFormat("HH:mm:ss.SSS").parse("23:59:58.123").getTime()), bean.aTimeField);
    }

    @Test
    public void testTimeFormatHHmmssCommaSSSSupported() throws ParseException {
        CommandLineTypeConversionTest.SupportedTypes bean = CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-Time", "23:59:58,123");
        Assert.assertEquals("Time", new Time(new SimpleDateFormat("HH:mm:ss,SSS").parse("23:59:58,123").getTime()), bean.aTimeField);
    }

    @Test
    public void testTimeFormatHHmmssSSSInvalidError() throws ParseException {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-Time", "23:59:58;123");
            Assert.fail("Invalid format was accepted");
        } catch (CommandLine expected) {
            Assert.assertEquals("Invalid value for option '-Time': '23:59:58;123' is not a HH:mm[:ss[.SSS]] time", expected.getMessage());
        }
    }

    @Test
    public void testTimeFormatHHmmssDotInvalidError() throws ParseException {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-Time", "23:59:58.");
            Assert.fail("Invalid format was accepted");
        } catch (CommandLine expected) {
            Assert.assertEquals("Invalid value for option '-Time': '23:59:58.' is not a HH:mm[:ss[.SSS]] time", expected.getMessage());
        }
    }

    @Test
    public void testTimeFormatHHmmsssInvalidError() throws ParseException {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-Time", "23:59:587");
            Assert.fail("Invalid format was accepted");
        } catch (CommandLine expected) {
            Assert.assertEquals("Invalid value for option '-Time': '23:59:587' is not a HH:mm[:ss[.SSS]] time", expected.getMessage());
        }
    }

    @Test
    public void testTimeFormatHHmmssSSSSInvalidError() throws ParseException {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-Time", "23:59:58.1234");
            Assert.fail("Invalid format was accepted");
        } catch (CommandLine expected) {
            Assert.assertEquals("Invalid value for option '-Time': '23:59:58.1234' is not a HH:mm[:ss[.SSS]] time", expected.getMessage());
        }
    }

    @Test
    public void testISO8601TimeConverterWhenJavaSqlModuleAvailable() throws Exception {
        Class<?> c = Class.forName("picocli.CommandLine$BuiltIn$ISO8601TimeConverter");
        Object converter = c.newInstance();
        Method createTime = c.getDeclaredMethod("createTime", long.class);
        createTime.setAccessible(true);
        long now = System.currentTimeMillis();
        Time actual = ((Time) (createTime.invoke(converter, now)));
        Assert.assertEquals("ISO8601TimeConverter works if java.sql module is available", new Time(now), actual);
    }

    @Test
    public void testISO8601TimeConverterExceptionHandling() throws Exception {
        Class<?> c = Class.forName("picocli.CommandLine$BuiltIn$ISO8601TimeConverter");
        Object converter = c.newInstance();
        Method createTime = c.getDeclaredMethod("createTime", long.class);
        createTime.setAccessible(true);
        long now = System.currentTimeMillis();
        // simulate the absence of the java.sql module
        Field fqcn = c.getDeclaredField("FQCN");
        fqcn.setAccessible(true);
        Object original = fqcn.get(null);
        fqcn.set(null, "a.b.c");// change FQCN to an unknown class

        Assert.assertEquals("a.b.c", fqcn.get(null));
        try {
            createTime.invoke(converter, now);
            Assert.fail("Expect exception");
        } catch (InvocationTargetException outer) {
            TypeConversionException ex = ((TypeConversionException) (outer.getTargetException()));
            Assert.assertTrue(ex.getMessage().startsWith(("Unable to create new java.sql.Time with long value " + now)));
        } finally {
            fqcn.set(null, original);// change FQCN back to java.sql.Time for other tests

        }
    }

    @Test
    public void testISO8601TimeConverterRegisterIfAvailableExceptionHandling() throws Exception {
        Class<?> c = Class.forName("picocli.CommandLine$BuiltIn$ISO8601TimeConverter");
        Object converter = c.newInstance();
        Method registerIfAvailable = c.getDeclaredMethod("registerIfAvailable", Map.class, Tracer.class);
        System.setProperty("picocli.trace", "DEBUG");
        CommandLine.Tracer tracer = new CommandLine.Tracer();
        registerIfAvailable.invoke(converter, null, tracer);
        String expected = String.format("[picocli DEBUG] Could not register converter for java.sql.Time: java.lang.NullPointerException%n");
        Assert.assertEquals(expected, systemErrRule.getLog());
        systemErrRule.clearLog();
        registerIfAvailable.invoke(null, null, tracer);
        Assert.assertEquals("logged only once", "", systemErrRule.getLog());
    }

    @Test
    public void testTimeFormatHHmmssColonInvalidError() throws ParseException {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-Time", "23:59:");
            Assert.fail("Invalid format was accepted");
        } catch (CommandLine expected) {
            Assert.assertEquals("Invalid value for option '-Time': '23:59:' is not a HH:mm[:ss[.SSS]] time", expected.getMessage());
        }
    }

    @Test
    public void testDateFormatYYYYmmddInvalidError() throws ParseException {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-Date", "20170131");
            Assert.fail("Invalid format was accepted");
        } catch (CommandLine expected) {
            Assert.assertEquals("Invalid value for option '-Date': '20170131' is not a yyyy-MM-dd date", expected.getMessage());
        }
    }

    @Test
    public void testCharConverterInvalidError() throws ParseException {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-Character", "aa");
            Assert.fail("Invalid format was accepted");
        } catch (CommandLine expected) {
            Assert.assertEquals("Invalid value for option '-Character': 'aa' is not a single character", expected.getMessage());
        }
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-char", "aa");
            Assert.fail("Invalid format was accepted");
        } catch (CommandLine expected) {
            Assert.assertEquals("Invalid value for option '-char': 'aa' is not a single character", expected.getMessage());
        }
    }

    @Test
    public void testNumberConvertersInvalidError() {
        parseInvalidValue("-Byte", "aa", "Invalid value for option '-Byte': 'aa' is not a byte");
        parseInvalidValue("-byte", "aa", "Invalid value for option '-byte': 'aa' is not a byte");
        parseInvalidValue("-Short", "aa", "Invalid value for option '-Short': 'aa' is not a short");
        parseInvalidValue("-short", "aa", "Invalid value for option '-short': 'aa' is not a short");
        parseInvalidValue("-Integer", "aa", "Invalid value for option '-Integer': 'aa' is not an int");
        parseInvalidValue("-int", "aa", "Invalid value for option '-int': 'aa' is not an int");
        parseInvalidValue("-Long", "aa", "Invalid value for option '-Long': 'aa' is not a long");
        parseInvalidValue("-long", "aa", "Invalid value for option '-long': 'aa' is not a long");
        parseInvalidValue("-Float", "aa", "Invalid value for option '-Float': 'aa' is not a float");
        parseInvalidValue("-float", "aa", "Invalid value for option '-float': 'aa' is not a float");
        parseInvalidValue("-Double", "aa", "Invalid value for option '-Double': 'aa' is not a double");
        parseInvalidValue("-double", "aa", "Invalid value for option '-double': 'aa' is not a double");
        parseInvalidValue("-BigDecimal", "aa", "java.lang.NumberFormatException");
        parseInvalidValue("-BigInteger", "aa", "java.lang.NumberFormatException: For input string: \"aa\"");
    }

    @Test
    public void testURLConvertersInvalidError() {
        parseInvalidValue("-URL", ":::", "java.net.MalformedURLException: no protocol: :::");
    }

    @Test
    public void testURIConvertersInvalidError() {
        parseInvalidValue("-URI", ":::", "java.net.URISyntaxException: Expected scheme name at index 0: :::");
    }

    @Test
    public void testCharsetConvertersInvalidError() {
        parseInvalidValue("-Charset", "aa", "java.nio.charset.UnsupportedCharsetException: aa");
    }

    @Test
    public void testInetAddressConvertersInvalidError() {
        parseInvalidValue("-InetAddress", "%$::a?*!a", "java.net.UnknownHostException: ");
    }

    @Test
    public void testUUIDConvertersInvalidError() {
        parseInvalidValue("-UUID", "aa", "java.lang.IllegalArgumentException: Invalid UUID string: aa");
    }

    @Test
    public void testCurrencyConvertersInvalidError() {
        parseInvalidValue("-Currency", "aa", "java.lang.IllegalArgumentException");
    }

    @Test
    public void testTimeZoneConvertersInvalidError() {
        CommandLineTypeConversionTest.SupportedTypes bean = CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-tz", "Abc/Def");
        Assert.assertEquals(TimeZone.getTimeZone("GMT"), bean.aTimeZone);
    }

    @Test
    public void testNetworkInterfaceConvertersInvalidNull() {
        CommandLineTypeConversionTest.SupportedTypes bean = CommandLine.populateCommand(new CommandLineTypeConversionTest.SupportedTypes(), "-NetworkInterface", "no such interface");
        Assert.assertNull(bean.aNetInterface);
    }

    @Test
    public void testRegexPatternConverterInvalidError() {
        parseInvalidValue("-Pattern", "[[(aa", String.format(("java.util.regex.PatternSyntaxException: Unclosed character class near index 4%n" + ("[[(aa%n" + "    ^"))));
    }

    @Test
    public void testByteOrderConvertersInvalidError() {
        parseInvalidValue("-byteOrder", "aa", "Invalid value for option '-byteOrder': 'aa' is not a valid ByteOrder");
    }

    @Test
    public void testClassConvertersInvalidError() {
        parseInvalidValue("-Class", "aa", "java.lang.ClassNotFoundException: aa");
    }

    @Test
    public void testConnectionConvertersInvalidError() {
        parseInvalidValue("-Connection", "aa", "Invalid value for option '-Connection': cannot convert 'aa' to interface java.sql.Connection (java.sql.SQLException: No suitable driver)", "Invalid value for option '-Connection': cannot convert 'aa' to interface java.sql.Connection (java.sql.SQLException: No suitable driver found for aa)");
    }

    @Test
    public void testDriverConvertersInvalidError() {
        parseInvalidValue("-Driver", "aa", "Invalid value for option '-Driver': cannot convert 'aa' to interface java.sql.Driver (java.sql.SQLException: No suitable driver)");
    }

    @Test
    public void testTimestampConvertersInvalidError() {
        parseInvalidValue("-Timestamp", "aa", "Invalid value for option '-Timestamp': cannot convert 'aa' to class java.sql.Timestamp (java.lang.IllegalArgumentException: Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff])", "Invalid value for option '-Timestamp': cannot convert 'aa' to class java.sql.Timestamp (java.lang.IllegalArgumentException: Timestamp format must be yyyy-mm-dd hh:mm:ss.fffffffff)");
    }

    @Test
    public void testRegisterCustomConverter() {
        class Glob {
            public final String glob;

            public Glob(String glob) {
                this.glob = glob;
            }
        }
        class App {
            @Parameters
            Glob globField;
        }
        class GlobConverter implements ITypeConverter<Glob> {
            public Glob convert(String value) throws Exception {
                return new Glob(value);
            }
        }
        CommandLine commandLine = new CommandLine(new App());
        commandLine.registerConverter(Glob.class, new GlobConverter());
        String[] args = new String[]{ "a*glob*pattern" };
        List<CommandLine> parsed = commandLine.parse(args);
        Assert.assertEquals("not empty", 1, parsed.size());
        Assert.assertTrue(((parsed.get(0).getCommand()) instanceof App));
        App app = ((App) (parsed.get(0).getCommand()));
        Assert.assertEquals(args[0], app.globField.glob);
    }

    static class MyGlobConverter implements ITypeConverter<CommandLineTypeConversionTest.MyGlob> {
        public CommandLineTypeConversionTest.MyGlob convert(String value) {
            return new CommandLineTypeConversionTest.MyGlob(value);
        }
    }

    static class MyGlob {
        public final String glob;

        public MyGlob(String glob) {
            this.glob = glob;
        }
    }

    @Test
    public void testAnnotateCustomConverter() {
        class App {
            @Parameters(converter = CommandLineTypeConversionTest.MyGlobConverter.class)
            CommandLineTypeConversionTest.MyGlob globField;
        }
        CommandLine commandLine = new CommandLine(new App());
        String[] args = new String[]{ "a*glob*pattern" };
        List<CommandLine> parsed = commandLine.parse(args);
        Assert.assertEquals("not empty", 1, parsed.size());
        Assert.assertTrue(((parsed.get(0).getCommand()) instanceof App));
        App app = ((App) (parsed.get(0).getCommand()));
        Assert.assertEquals(args[0], app.globField.glob);
    }

    static class SqlTypeConverter implements ITypeConverter<Integer> {
        public Integer convert(String value) {
            if ("ARRAY".equals(value)) {
                return Types.ARRAY;
            }
            if ("BIGINT".equals(value)) {
                return Types.BIGINT;
            }
            if ("BINARY".equals(value)) {
                return Types.BINARY;
            }
            if ("BIT".equals(value)) {
                return Types.BIT;
            }
            if ("BLOB".equals(value)) {
                return Types.BLOB;
            }
            if ("BOOLEAN".equals(value)) {
                return Types.BOOLEAN;
            }
            if ("CHAR".equals(value)) {
                return Types.CHAR;
            }
            if ("CLOB".equals(value)) {
                return Types.CLOB;
            }
            return Types.OTHER;
        }
    }

    @Test
    public void testAnnotatedCustomConverterDoesNotConflictWithExistingType() {
        class App {
            @Parameters(index = "0", converter = CommandLineTypeConversionTest.SqlTypeConverter.class)
            int sqlTypeParam;

            @Parameters(index = "1")
            int normalIntParam;

            @Option(names = "-t", converter = CommandLineTypeConversionTest.SqlTypeConverter.class)
            int sqlTypeOption;

            @Option(names = "-x")
            int normalIntOption;
        }
        App app = new App();
        CommandLine commandLine = new CommandLine(app);
        String[] args = new String[]{ "-x", "1234", "-t", "BLOB", "CLOB", "5678" };
        commandLine.parse(args);
        Assert.assertEquals(1234, app.normalIntOption);
        Assert.assertEquals(Types.BLOB, app.sqlTypeOption);
        Assert.assertEquals(Types.CLOB, app.sqlTypeParam);
        Assert.assertEquals(5678, app.normalIntParam);
    }

    static class ErrorConverter implements ITypeConverter<Integer> {
        public Integer convert(String value) throws Exception {
            throw new IllegalStateException("bad converter");
        }
    }

    @Test
    public void testAnnotatedCustomConverterErrorHandling() {
        class App {
            @Parameters(converter = CommandLineTypeConversionTest.ErrorConverter.class)
            int sqlTypeParam;
        }
        CommandLine commandLine = new CommandLine(new App());
        try {
            commandLine.parse("anything");
        } catch (CommandLine ex) {
            Assert.assertEquals("Invalid value for positional parameter at index 0..* (<sqlTypeParam>): cannot convert 'anything' to int (java.lang.IllegalStateException: bad converter)", ex.getMessage());
        }
    }

    static class CustomConverter implements ITypeConverter<Integer> {
        public Integer convert(String value) {
            return Integer.parseInt(value);
        }
    }

    static class Plus23Converter implements ITypeConverter<Integer> {
        public Integer convert(String value) {
            return (Integer.parseInt(value)) + 23;
        }
    }

    static class Plus23ConverterFactory implements CommandLine.IFactory {
        @SuppressWarnings("unchecked")
        public <T> T create(Class<T> cls) {
            return ((T) (new CommandLineTypeConversionTest.Plus23Converter()));
        }
    }

    @Test
    public void testAnnotatedCustomConverterFactory() {
        class App {
            @Parameters(converter = CommandLineTypeConversionTest.CustomConverter.class)
            int converted;
        }
        App app = new App();
        CommandLine commandLine = new CommandLine(app, new CommandLineTypeConversionTest.Plus23ConverterFactory());
        commandLine.parse("100");
        Assert.assertEquals(123, app.converted);
    }

    static class EnumParams {
        @Option(names = "-timeUnit")
        TimeUnit timeUnit;

        @Option(names = "-timeUnitArray", arity = "2")
        TimeUnit[] timeUnitArray;

        @Option(names = "-timeUnitList", type = TimeUnit.class, arity = "3")
        List<TimeUnit> timeUnitList;
    }

    @Test
    public void testEnumTypeConversionSuceedsForValidInput() {
        CommandLineTypeConversionTest.EnumParams params = CommandLine.populateCommand(new CommandLineTypeConversionTest.EnumParams(), "-timeUnit SECONDS -timeUnitArray MILLISECONDS SECONDS -timeUnitList SECONDS MICROSECONDS NANOSECONDS".split(" "));
        Assert.assertEquals(TimeUnit.SECONDS, params.timeUnit);
        Assert.assertArrayEquals(new TimeUnit[]{ TimeUnit.MILLISECONDS, TimeUnit.SECONDS }, params.timeUnitArray);
        List<TimeUnit> expected = new ArrayList<TimeUnit>(Arrays.asList(TimeUnit.SECONDS, TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS));
        Assert.assertEquals(expected, params.timeUnitList);
    }

    @Test
    public void testEnumTypeConversionFailsForInvalidInput() {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.EnumParams(), "-timeUnit", "xyz");
            Assert.fail("Accepted invalid timeunit");
        } catch (Exception ex) {
            String prefix = "Invalid value for option '-timeUnit': expected one of ";
            String suffix = " (case-sensitive) but was 'xyz'";
            Assert.assertEquals(prefix, ex.getMessage().substring(0, prefix.length()));
            Assert.assertEquals(suffix, ex.getMessage().substring(((ex.getMessage().length()) - (suffix.length())), ex.getMessage().length()));
        }
    }

    @Test
    public void testEnumTypeConversionIsCaseInsensitiveIfConfigured() {
        CommandLineTypeConversionTest.EnumParams params = new CommandLineTypeConversionTest.EnumParams();
        new CommandLine(params).setCaseInsensitiveEnumValuesAllowed(true).parse("-timeUnit sEcONds -timeUnitArray milliSeconds miCroSeConds -timeUnitList SEConds MiCROsEconds nanoSEConds".split(" "));
        Assert.assertEquals(TimeUnit.SECONDS, params.timeUnit);
        Assert.assertArrayEquals(new TimeUnit[]{ TimeUnit.MILLISECONDS, TimeUnit.MICROSECONDS }, params.timeUnitArray);
        List<TimeUnit> expected = new ArrayList<TimeUnit>(Arrays.asList(TimeUnit.SECONDS, TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS));
        Assert.assertEquals(expected, params.timeUnitList);
    }

    enum MyTestEnum {

        BIG,
        SMALL,
        TINY;
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    @Test
    public void testEnumTypeConversionErrorMessageUsesConstantValuesNotToString() {
        class App {
            @Option(names = "-e")
            CommandLineTypeConversionTest.MyTestEnum myEnum;
        }
        App params = new App();
        try {
            new CommandLine(params).parse("-e big".split(" "));
        } catch (ParameterException ex) {
            Assert.assertEquals("Invalid value for option '-e': expected one of [BIG, SMALL, TINY] (case-sensitive) but was 'big'", ex.getMessage());
        }
    }

    @Test
    public void testEnumCaseInsensitiveTypeConversionErrorMessageUsesConstantValuesNotToString() {
        class App {
            @Option(names = "-e")
            CommandLineTypeConversionTest.MyTestEnum myEnum;
        }
        App params = new App();
        try {
            new CommandLine(params).setCaseInsensitiveEnumValuesAllowed(true).parse("-e big".split(" "));
        } catch (ParameterException ex) {
            Assert.assertEquals("Invalid value for option '-e': expected one of [BIG, SMALL, TINY] (case-insensitive) but was 'big'", ex.getMessage());
        }
    }

    @Test
    public void testEnumArrayTypeConversionFailsForInvalidInput() {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.EnumParams(), "-timeUnitArray", "a", "b");
            Assert.fail("Accepted invalid timeunit");
        } catch (Exception ex) {
            String prefix = "Invalid value for option '-timeUnitArray' at index 0 (<timeUnitArray>): expected one of ";
            String suffix = " (case-sensitive) but was 'a'";
            Assert.assertEquals(prefix, ex.getMessage().substring(0, prefix.length()));
            Assert.assertEquals(suffix, ex.getMessage().substring(((ex.getMessage().length()) - (suffix.length())), ex.getMessage().length()));
        }
    }

    @Test
    public void testEnumListTypeConversionFailsForInvalidInput() {
        try {
            CommandLine.populateCommand(new CommandLineTypeConversionTest.EnumParams(), "-timeUnitList", "SECONDS", "b", "c");
            Assert.fail("Accepted invalid timeunit");
        } catch (Exception ex) {
            String prefix = "Invalid value for option '-timeUnitList' at index 1 (<timeUnitList>): expected one of ";
            String suffix = " (case-sensitive) but was 'b'";
            Assert.assertEquals(prefix, ex.getMessage().substring(0, prefix.length()));
            Assert.assertEquals(suffix, ex.getMessage().substring(((ex.getMessage().length()) - (suffix.length())), ex.getMessage().length()));
        }
    }

    @Test
    public void testArrayOptionParametersAreAlwaysInstantiated() {
        CommandLineTypeConversionTest.EnumParams params = new CommandLineTypeConversionTest.EnumParams();
        TimeUnit[] array = params.timeUnitArray;
        new CommandLine(params).parse("-timeUnitArray", "SECONDS", "MILLISECONDS");
        Assert.assertNotSame(array, params.timeUnitArray);
    }

    @Test
    public void testListOptionParametersAreInstantiatedIfNull() {
        CommandLineTypeConversionTest.EnumParams params = new CommandLineTypeConversionTest.EnumParams();
        Assert.assertNull(params.timeUnitList);
        new CommandLine(params).parse("-timeUnitList", "SECONDS", "MICROSECONDS", "MILLISECONDS");
        Assert.assertEquals(Arrays.asList(TimeUnit.SECONDS, TimeUnit.MICROSECONDS, TimeUnit.MILLISECONDS), params.timeUnitList);
    }

    @Test
    public void testListOptionParametersAreReusedIfNonNull() {
        CommandLineTypeConversionTest.EnumParams params = new CommandLineTypeConversionTest.EnumParams();
        List<TimeUnit> list = new ArrayList<TimeUnit>();
        params.timeUnitList = list;
        new CommandLine(params).parse("-timeUnitList", "SECONDS", "MICROSECONDS", "SECONDS");
        Assert.assertEquals(Arrays.asList(TimeUnit.SECONDS, TimeUnit.MICROSECONDS, TimeUnit.SECONDS), params.timeUnitList);
        Assert.assertSame(list, params.timeUnitList);
    }

    @Test
    public void testConcreteCollectionParametersAreInstantiatedIfNull() {
        class App {
            @Option(names = "-map")
            HashMap<String, String> map;

            @Option(names = "-list")
            ArrayList<String> list;
        }
        App params = new App();
        new CommandLine(params).parse("-list", "a", "-list", "b", "-map", "a=b");
        Assert.assertEquals(Arrays.asList("a", "b"), params.list);
        HashMap<String, String> expected = new HashMap<String, String>();
        expected.put("a", "b");
        Assert.assertEquals(expected, params.map);
    }

    @Test
    public void testJava7Types() throws Exception {
        if ((System.getProperty("java.version").compareTo("1.7.0")) < 0) {
            System.out.println(("Unable to verify Java 7 converters on " + (System.getProperty("java.version"))));
            return;
        }
        CommandLine commandLine = new CommandLine(new CommandLineTypeConversionTest.EnumParams());
        Map<Class<?>, ITypeConverter<?>> registry = extractRegistry(commandLine);
        verifyReflectedConverter(registry, "java.nio.file.Path", "/tmp/some/directory", new File("/tmp/some/directory").toString());
    }

    @Test
    public void testJava8Types() throws Exception {
        CommandLine commandLine = new CommandLine(new CommandLineTypeConversionTest.EnumParams());
        Map<Class<?>, ITypeConverter<?>> registry = extractRegistry(commandLine);
        if ((System.getProperty("java.version").compareTo("1.8.0")) < 0) {
            System.out.println(("Unable to verify Java 8 converters on " + (System.getProperty("java.version"))));
            return;
        }
        verifyReflectedConverter(registry, "java.time.Duration", "P2DT3H4M", "PT51H4M");
        verifyReflectedConverter(registry, "java.time.Instant", "2007-12-03T10:15:30.00Z", "2007-12-03T10:15:30Z");
        verifyReflectedConverter(registry, "java.time.LocalDate", "2007-12-03", "2007-12-03");
        verifyReflectedConverter(registry, "java.time.LocalDateTime", "2007-12-03T10:15:30", "2007-12-03T10:15:30");
        verifyReflectedConverter(registry, "java.time.LocalTime", "10:15", "10:15");
        verifyReflectedConverter(registry, "java.time.MonthDay", "--12-03", "--12-03");
        verifyReflectedConverter(registry, "java.time.OffsetDateTime", "2007-12-03T10:15:30+01:00", "2007-12-03T10:15:30+01:00");
        verifyReflectedConverter(registry, "java.time.OffsetTime", "10:15:30+01:00", "10:15:30+01:00");
        verifyReflectedConverter(registry, "java.time.Period", "P1Y2M3D", "P1Y2M3D");
        verifyReflectedConverter(registry, "java.time.Year", "2007", "2007");
        verifyReflectedConverter(registry, "java.time.YearMonth", "2007-12", "2007-12");
        verifyReflectedConverter(registry, "java.time.ZonedDateTime", "2007-12-03T10:15:30+01:00[Europe/Paris]", "2007-12-03T10:15:30+01:00[Europe/Paris]");
        verifyReflectedConverter(registry, "java.time.ZoneId", "Europe/Paris", "Europe/Paris");
        verifyReflectedConverter(registry, "java.time.ZoneOffset", "+0800", "+08:00");
    }

    static class EmptyValueConverter implements ITypeConverter<Short> {
        public Short convert(String value) throws Exception {
            return value == null ? -1 : "".equals(value) ? -2 : Short.valueOf(value);
        }
    }

    @Test
    public void testOptionEmptyValue() {
        class Standard {
            @Option(names = "-x", arity = "0..1", description = "may have empty value")
            Short x;
        }
        try {
            CommandLine.populateCommand(new Standard(), "-x");
            Assert.fail("Expect exception for Short.valueOf(\"\")");
        } catch (Exception expected) {
            Assert.assertEquals("Invalid value for option '-x': '' is not a short", expected.getMessage());
        }
        Standard withValue1 = CommandLine.populateCommand(new Standard(), "-x=987");
        Assert.assertEquals(Short.valueOf(((short) (987))), withValue1.x);
        // ------------------
        class CustomConverter {
            @Option(names = "-x", arity = "0..1", description = "may have empty value", converter = CommandLineTypeConversionTest.EmptyValueConverter.class)
            Short x;
        }
        CustomConverter withoutValue2 = CommandLine.populateCommand(new CustomConverter(), "-x");
        Assert.assertEquals(Short.valueOf(((short) (-2))), withoutValue2.x);
        CustomConverter withValue2 = CommandLine.populateCommand(new CustomConverter(), "-x=987");
        Assert.assertEquals(Short.valueOf(((short) (987))), withValue2.x);
    }

    @Test
    public void testWithoutExcludes() throws Exception {
        Map<Class<?>, ITypeConverter<?>> registry = createSampleRegistry();
        Assert.assertTrue((("at least 39 (actual " + (registry.size())) + ")"), ((registry.size()) >= 39));
        Assert.assertTrue("java.sql.Time", registry.containsKey(Time.class));
        Assert.assertTrue("java.sql.Timestamp", registry.containsKey(Timestamp.class));
        Assert.assertTrue("java.sql.Connection", registry.containsKey(Connection.class));
        Assert.assertTrue("java.sql.Driver", registry.containsKey(Driver.class));
    }

    @Test
    public void testExcludesRegexByPackage() throws Exception {
        System.setProperty("picocli.converters.excludes", "java.sql.*");
        Map<Class<?>, ITypeConverter<?>> registry = createSampleRegistry();
        Assert.assertFalse("java.sql.Time", registry.containsKey(Time.class));
        Assert.assertFalse("java.sql.Timestamp", registry.containsKey(Timestamp.class));
        Assert.assertFalse("java.sql.Connection", registry.containsKey(Connection.class));
        Assert.assertFalse("java.sql.Driver", registry.containsKey(Driver.class));
    }

    @Test
    public void testExcludesRegex() throws Exception {
        System.setProperty("picocli.converters.excludes", "java.sql.Ti.*");
        Map<Class<?>, ITypeConverter<?>> registry = createSampleRegistry();
        Assert.assertFalse("java.sql.Time", registry.containsKey(Time.class));
        Assert.assertFalse("java.sql.Timestamp", registry.containsKey(Timestamp.class));
        Assert.assertTrue("java.sql.Connection", registry.containsKey(Connection.class));
        Assert.assertTrue("java.sql.Driver", registry.containsKey(Driver.class));
    }

    @Test
    public void testExcludesCommaSeparatedRegex() throws Exception {
        // System.setProperty("picocli.trace", "DEBUG");
        System.setProperty("picocli.converters.excludes", "java.sql.Time,java.sql.Connection");
        Map<Class<?>, ITypeConverter<?>> registry = createSampleRegistry();
        Assert.assertFalse("java.sql.Time", registry.containsKey(Time.class));
        Assert.assertTrue("java.sql.Timestamp", registry.containsKey(Timestamp.class));
        Assert.assertFalse("java.sql.Connection", registry.containsKey(Connection.class));
        Assert.assertTrue("java.sql.Driver", registry.containsKey(Driver.class));
    }

    @Test
    public void testReflectionConverterExceptionHandling() throws Exception {
        Class<?> c = Class.forName("picocli.CommandLine$BuiltIn$ReflectionConverter");
        Constructor<?> constructor = c.getDeclaredConstructor(Method.class, Class[].class);
        Method cannotInvoke = Object.class.getDeclaredMethod("toString");
        Object reflectionConverter = constructor.newInstance(cannotInvoke, new Class[0]);
        Method convert = c.getDeclaredMethod("convert", String.class);
        try {
            convert.invoke(reflectionConverter, "command line parameter");
            Assert.fail("Expected exception: invoking toString() as a static method on a null object");
        } catch (InvocationTargetException ex) {
            TypeConversionException actual = ((TypeConversionException) (ex.getTargetException()));
            Assert.assertTrue(actual.getMessage().startsWith("Internal error converting 'command line parameter' to class java.lang.String"));
        }
    }

    @Test
    public void testRegisterIfAvailableExceptionHandling() throws Exception {
        Class<?> c = Class.forName("picocli.CommandLine$BuiltIn");
        Method registerIfAvailable = c.getDeclaredMethod("registerIfAvailable", Map.class, Tracer.class, String.class, String.class, String.class, Class[].class);
        System.setProperty("picocli.trace", "DEBUG");
        CommandLine.Tracer tracer = new CommandLine.Tracer();
        registerIfAvailable.invoke(null, null, tracer, "a.b.c", null, null, null);
        String expected = String.format("[picocli DEBUG] Could not register converter for a.b.c: java.lang.ClassNotFoundException: a.b.c%n");
        Assert.assertEquals(expected, systemErrRule.getLog());
        systemErrRule.clearLog();
        registerIfAvailable.invoke(null, null, tracer, "a.b.c", null, null, null);
        Assert.assertEquals("logged only once", "", systemErrRule.getLog());
    }

    static class SplitSemiColonConverter implements ITypeConverter<List<String>> {
        public List<String> convert(String value) throws Exception {
            return Arrays.asList(value.split(";"));
        }
    }

    @Test
    public void testCollectionsAreAddedExplosivelyToCollection() {
        class App {
            @Parameters(converter = CommandLineTypeConversionTest.SplitSemiColonConverter.class)
            List<String> all;
        }
        App app = CommandLine.populateCommand(new App(), "a;b;c", "1;2;3");
        Assert.assertEquals(Arrays.asList("a", "b", "c", "1", "2", "3"), app.all);
    }

    @Test
    public void testCollectionsAreAddedExplosivelyToArray() {
        class App {
            @Parameters(converter = CommandLineTypeConversionTest.SplitSemiColonConverter.class)
            String[] all;
        }
        App app = CommandLine.populateCommand(new App(), "a;b;c", "1;2;3");
        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "1", "2", "3" }, app.all);
    }

    @Test
    public void testMapArgumentsMustContainEquals() {
        class App {
            @Parameters
            Map<String, String> map;
        }
        try {
            CommandLine.populateCommand(new App(), "a:c", "1:3");
        } catch (UnmatchedArgumentException ex) {
            Assert.assertEquals("Unmatched arguments: a:c, 1:3", ex.getMessage());
        }
    }

    @Test
    public void testMapArgumentsMustContainEquals2() {
        class App {
            @Parameters(split = "==")
            Map<String, String> map;
        }
        try {
            CommandLine.populateCommand(new App(), "a:c", "1:3");
        } catch (UnmatchedArgumentException ex) {
            Assert.assertEquals("Unmatched arguments: a:c, 1:3", ex.getMessage());
        }
    }

    enum ResultTypes {

        NONE,
        PARTIAL,
        COMPLETE;}

    @Test
    public void testIssue628EnumSetWithNullInitialValue() {
        class App {
            @Option(names = "--result-types", split = ",")
            private EnumSet<CommandLineTypeConversionTest.ResultTypes> resultTypes = null;
        }
        App app = new App();
        new CommandLine(app).parseArgs("--result-types", "PARTIAL,COMPLETE");
        Assert.assertEquals(EnumSet.of(CommandLineTypeConversionTest.ResultTypes.PARTIAL, CommandLineTypeConversionTest.ResultTypes.COMPLETE), app.resultTypes);
    }

    @Test
    public void testIssue628EnumSetWithEmptyInitialValue() {
        class App {
            @Option(names = "--result-types", split = ",")
            private EnumSet<CommandLineTypeConversionTest.ResultTypes> resultTypes = EnumSet.noneOf(CommandLineTypeConversionTest.ResultTypes.class);
        }
        App app = new App();
        new CommandLine(app).parseArgs("--result-types", "PARTIAL,COMPLETE");
        Assert.assertEquals(EnumSet.of(CommandLineTypeConversionTest.ResultTypes.PARTIAL, CommandLineTypeConversionTest.ResultTypes.COMPLETE), app.resultTypes);
    }

    @Test
    public void testIssue628EnumSetWithNonEmptyInitialValue() {
        class App {
            @Option(names = "--result-types", split = ",")
            private EnumSet<CommandLineTypeConversionTest.ResultTypes> resultTypes = EnumSet.of(CommandLineTypeConversionTest.ResultTypes.COMPLETE);
        }
        App app = new App();
        new CommandLine(app).parseArgs("--result-types", "PARTIAL,COMPLETE");
        Assert.assertEquals(EnumSet.of(CommandLineTypeConversionTest.ResultTypes.PARTIAL, CommandLineTypeConversionTest.ResultTypes.COMPLETE), app.resultTypes);
    }
}

