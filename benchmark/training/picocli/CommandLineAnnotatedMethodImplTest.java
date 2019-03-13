package picocli;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.PicocliException;


public class CommandLineAnnotatedMethodImplTest {
    @Rule
    public final ProvideSystemProperty ansiOFF = new ProvideSystemProperty("picocli.ansi", "false");

    static class Primitives {
        boolean aBoolean;

        byte aByte;

        short aShort;

        int anInt;

        long aLong;

        float aFloat;

        double aDouble;

        @CommandLine.Option(names = "-b")
        void setBoolean(boolean val) {
            aBoolean = val;
        }

        @CommandLine.Option(names = "-y")
        void setByte(byte val) {
            aByte = val;
        }

        @CommandLine.Option(names = "-s")
        void setShort(short val) {
            aShort = val;
        }

        @CommandLine.Option(names = "-i")
        void setInt(int val) {
            anInt = val;
        }

        @CommandLine.Option(names = "-l")
        void setLong(long val) {
            aLong = val;
        }

        @CommandLine.Option(names = "-f")
        void setFloat(float val) {
            aFloat = val;
        }

        @CommandLine.Option(names = "-d")
        void setDouble(double val) {
            aDouble = val;
        }
    }

    @Test
    public void testPrimitivesWithoutDefaultValues() {
        CommandLineAnnotatedMethodImplTest.Primitives primitives = CommandLine.populateCommand(new CommandLineAnnotatedMethodImplTest.Primitives());
        Assert.assertFalse(primitives.aBoolean);
        Assert.assertEquals(0, primitives.aByte);
        Assert.assertEquals(((short) (0)), primitives.aShort);
        Assert.assertEquals(0, primitives.anInt);
        Assert.assertEquals(0, primitives.aLong);
        Assert.assertEquals(0, primitives.aFloat, 1.0E-4);
        Assert.assertEquals(0, primitives.aDouble, 1.0E-4);
    }

    static class PrimitivesWithDefault {
        boolean aBoolean;

        byte aByte;

        short aShort;

        int anInt;

        long aLong;

        float aFloat;

        double aDouble;

        @CommandLine.Option(names = "-b", defaultValue = "true")
        void setBoolean(boolean val) {
            aBoolean = val;
        }

        @CommandLine.Option(names = "-y", defaultValue = "11")
        void setByte(byte val) {
            aByte = val;
        }

        @CommandLine.Option(names = "-s", defaultValue = "12")
        void setShort(short val) {
            aShort = val;
        }

        @CommandLine.Option(names = "-i", defaultValue = "13")
        void setInt(int val) {
            anInt = val;
        }

        @CommandLine.Option(names = "-l", defaultValue = "14")
        void setLong(long val) {
            aLong = val;
        }

        @CommandLine.Option(names = "-f", defaultValue = "15.5")
        void setFloat(float val) {
            aFloat = val;
        }

        @CommandLine.Option(names = "-d", defaultValue = "16.6")
        void setDouble(double val) {
            aDouble = val;
        }
    }

    @Test
    public void testPrimitivesWithDefaultValues() {
        CommandLineAnnotatedMethodImplTest.PrimitivesWithDefault primitives = CommandLine.populateCommand(new CommandLineAnnotatedMethodImplTest.PrimitivesWithDefault());
        Assert.assertTrue(primitives.aBoolean);
        Assert.assertEquals(11, primitives.aByte);
        Assert.assertEquals(((short) (12)), primitives.aShort);
        Assert.assertEquals(13, primitives.anInt);
        Assert.assertEquals(14, primitives.aLong);
        Assert.assertEquals(15.5, primitives.aFloat, 1.0E-4);
        Assert.assertEquals(16.6, primitives.aDouble, 1.0E-4);
    }

    @Test
    public void testPrimitives() {
        String[] args = "-b -y1 -s2 -i3 -l4 -f5 -d6".split(" ");
        CommandLineAnnotatedMethodImplTest.Primitives primitives = CommandLine.populateCommand(new CommandLineAnnotatedMethodImplTest.Primitives(), args);
        Assert.assertTrue(primitives.aBoolean);
        Assert.assertEquals(1, primitives.aByte);
        Assert.assertEquals(2, primitives.aShort);
        Assert.assertEquals(3, primitives.anInt);
        Assert.assertEquals(4, primitives.aLong);
        Assert.assertEquals(5, primitives.aFloat, 1.0E-4);
        Assert.assertEquals(6, primitives.aDouble, 1.0E-4);
    }

    static class ObjectsWithDefaults {
        Boolean aBoolean;

        Byte aByte;

        Short aShort;

        Integer anInt;

        Long aLong;

        Float aFloat;

        Double aDouble;

        BigDecimal aBigDecimal;

        String aString;

        List<String> aList;

        Map<Integer, Double> aMap;

        SortedSet<Short> aSet;

        @CommandLine.Option(names = "-b", defaultValue = "true")
        void setBoolean(Boolean val) {
            aBoolean = val;
        }

        @CommandLine.Option(names = "-y", defaultValue = "123")
        void setByte(Byte val) {
            aByte = val;
        }

        @CommandLine.Option(names = "-s", defaultValue = "11")
        void setShort(Short val) {
            aShort = val;
        }

        @CommandLine.Option(names = "-i", defaultValue = "12")
        void setInt(Integer val) {
            anInt = val;
        }

        @CommandLine.Option(names = "-l", defaultValue = "13")
        void setLong(Long val) {
            aLong = val;
        }

        @CommandLine.Option(names = "-f", defaultValue = "14.4")
        void setFloat(Float val) {
            aFloat = val;
        }

        @CommandLine.Option(names = "-d", defaultValue = "15.5")
        void setDouble(Double val) {
            aDouble = val;
        }

        @CommandLine.Option(names = "-bigint", defaultValue = "16.6")
        void setBigDecimal(BigDecimal val) {
            aBigDecimal = val;
        }

        @CommandLine.Option(names = "-string", defaultValue = "abc")
        void setString(String val) {
            aString = val;
        }

        @CommandLine.Option(names = "-list", defaultValue = "a,b,c", split = ",")
        void setList(List<String> val) {
            aList = val;
        }

        @CommandLine.Option(names = "-map", defaultValue = "1=1,2=2,3=3", split = ",")
        void setMap(Map<Integer, Double> val) {
            aMap = val;
        }

        @CommandLine.Option(names = "-set", defaultValue = "1,2,3", split = ",")
        void setSortedSet(SortedSet<Short> val) {
            aSet = val;
        }
    }

    @Test
    public void testObjectsWithDefaultValues() {
        CommandLine cmd = new CommandLine(CommandLineAnnotatedMethodImplTest.ObjectsWithDefaults.class);
        cmd.parse();
        CommandLineAnnotatedMethodImplTest.ObjectsWithDefaults objects = cmd.getCommand();
        Assert.assertTrue(objects.aBoolean);
        Assert.assertEquals(Byte.valueOf(((byte) (123))), objects.aByte);
        Assert.assertEquals(Short.valueOf(((short) (11))), objects.aShort);
        Assert.assertEquals(Integer.valueOf(12), objects.anInt);
        Assert.assertEquals(Long.valueOf(13), objects.aLong);
        Assert.assertEquals(14.4F, objects.aFloat, 1.0E-4);
        Assert.assertEquals(15.5, objects.aDouble, 1.0E-4);
        Assert.assertEquals(new BigDecimal("16.6"), objects.aBigDecimal);
        Assert.assertEquals("abc", objects.aString);
        Assert.assertEquals(Arrays.asList("a", "b", "c"), objects.aList);
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        map.put(1, 1.0);
        map.put(2, 2.0);
        map.put(3, 3.0);
        Assert.assertEquals(map, objects.aMap);
        Assert.assertEquals(new TreeSet<Short>(Arrays.asList(((short) (1)), ((short) (2)), ((short) (3)))), objects.aSet);
    }

    static class Objects {
        Boolean aBoolean;

        Byte aByte;

        Short aShort;

        Integer anInt;

        Long aLong;

        Float aFloat;

        Double aDouble;

        BigInteger aBigInteger;

        String aString;

        List<String> aList;

        Map<Integer, Double> aMap;

        SortedSet<Short> aSet;

        @CommandLine.Option(names = "-b")
        void setBoolean(Boolean val) {
            aBoolean = val;
        }

        @CommandLine.Option(names = "-y")
        void setByte(Byte val) {
            aByte = val;
        }

        @CommandLine.Option(names = "-s")
        void setShort(Short val) {
            aShort = val;
        }

        @CommandLine.Option(names = "-i")
        void setInt(Integer val) {
            anInt = val;
        }

        @CommandLine.Option(names = "-l")
        void setLong(Long val) {
            aLong = val;
        }

        @CommandLine.Option(names = "-f")
        void setFloat(Float val) {
            aFloat = val;
        }

        @CommandLine.Option(names = "-d")
        void setDouble(Double val) {
            aDouble = val;
        }

        @CommandLine.Option(names = "-bigint")
        void setBigInteger(BigInteger val) {
            aBigInteger = val;
        }

        @CommandLine.Option(names = "-string")
        void setString(String val) {
            aString = val;
        }

        @CommandLine.Option(names = "-list")
        void setList(List<String> val) {
            aList = val;
        }

        @CommandLine.Option(names = "-map")
        void setMap(Map<Integer, Double> val) {
            aMap = val;
        }

        @CommandLine.Option(names = "-set")
        void setSortedSet(SortedSet<Short> val) {
            aSet = val;
        }
    }

    @Test
    public void testObjectsWithoutDefaultValues() {
        CommandLineAnnotatedMethodImplTest.Objects objects = CommandLine.populateCommand(new CommandLineAnnotatedMethodImplTest.Objects());
        Assert.assertNull(objects.aBoolean);
        Assert.assertNull(objects.aByte);
        Assert.assertNull(objects.aShort);
        Assert.assertNull(objects.anInt);
        Assert.assertNull(objects.aLong);
        Assert.assertNull(objects.aFloat);
        Assert.assertNull(objects.aDouble);
        Assert.assertNull(objects.aBigInteger);
        Assert.assertNull(objects.aString);
        Assert.assertNull(objects.aList);
        Assert.assertNull(objects.aMap);
        Assert.assertNull(objects.aSet);
    }

    @Test
    public void testObjects() {
        String[] args = "-b -y1 -s2 -i3 -l4 -f5 -d6 -bigint=7 -string abc -list a -list b -map 1=2.0 -set 33 -set 22".split(" ");
        CommandLineAnnotatedMethodImplTest.Objects objects = CommandLine.populateCommand(new CommandLineAnnotatedMethodImplTest.Objects(), args);
        Assert.assertTrue(objects.aBoolean);
        Assert.assertEquals(Byte.valueOf(((byte) (1))), objects.aByte);
        Assert.assertEquals(Short.valueOf(((short) (2))), objects.aShort);
        Assert.assertEquals(Integer.valueOf(3), objects.anInt);
        Assert.assertEquals(Long.valueOf(4), objects.aLong);
        Assert.assertEquals(5.0F, objects.aFloat, 1.0E-4);
        Assert.assertEquals(6.0, objects.aDouble, 1.0E-4);
        Assert.assertEquals(BigInteger.valueOf(7), objects.aBigInteger);
        Assert.assertEquals("abc", objects.aString);
        Assert.assertEquals(Arrays.asList("a", "b"), objects.aList);
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        map.put(1, 2.0);
        Assert.assertEquals(map, objects.aMap);
        Set<Short> set = new TreeSet<Short>();
        set.add(((short) (22)));
        set.add(((short) (33)));
        Assert.assertEquals(set, objects.aSet);
    }

    @Test
    public void testExceptionFromMethod() {
        class App {
            @CommandLine.Option(names = "--jvm")
            public void jvmException(String value) {
                throw new IllegalArgumentException("Boo!");
            }
        }
        CommandLine parser = new CommandLine(new App());
        try {
            parser.parse("--jvm", "abc");
            Assert.fail("Expected exception");
        } catch (ParameterException ex) {
            Assert.assertNotNull(ex.getCause());
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
            Assert.assertEquals("Boo!", ex.getCause().getMessage());
            Assert.assertEquals("Could not invoke public void picocli.CommandLineAnnotatedMethodImplTest$1App.jvmException(java.lang.String) with abc", ex.getMessage());
        }
    }

    @Test
    public void testParameterExceptionFromMethod() {
        class App {
            @CommandLine.Option(names = "--param")
            public void paramException(String value) {
                throw new ParameterException(new CommandLine(new App()), "Param!");
            }
        }
        CommandLine parser = new CommandLine(new App());
        try {
            parser.parse("--param", "abc");
            Assert.fail("Expected exception");
        } catch (ParameterException ex) {
            Assert.assertNull(ex.getCause());
            Assert.assertEquals("Param!", ex.getMessage());
        }
    }

    @Test
    public void testPicocliExceptionFromMethod() {
        class App {
            @CommandLine.Option(names = "--pico")
            public void picocliException(String value) {
                throw new PicocliException("Pico!");
            }
        }
        CommandLine parser = new CommandLine(new App());
        try {
            parser.parse("--pico", "abc");
            Assert.fail("Expected exception");
        } catch (ParameterException ex) {
            Assert.assertNotNull(ex.getCause());
            Assert.assertTrue(((ex.getCause()) instanceof PicocliException));
            Assert.assertEquals("Pico!", ex.getCause().getMessage());
            Assert.assertEquals("PicocliException: Pico! while processing argument at or before arg[1] 'abc' in [--pico, abc]: picocli.CommandLine$PicocliException: Pico!", ex.getMessage());
        }
    }
}

