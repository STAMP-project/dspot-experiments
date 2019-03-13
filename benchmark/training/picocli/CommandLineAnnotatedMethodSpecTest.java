package picocli;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
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


public class CommandLineAnnotatedMethodSpecTest {
    @Rule
    public final ProvideSystemProperty ansiOFF = new ProvideSystemProperty("picocli.ansi", "false");

    interface Primitives {
        @Option(names = "-b")
        boolean aBoolean();

        @Option(names = "-y")
        byte aByte();

        @Option(names = "-s")
        short aShort();

        @Option(names = "-i")
        int anInt();

        @Option(names = "-l")
        long aLong();

        @Option(names = "-f")
        float aFloat();

        @Option(names = "-d")
        double aDouble();
    }

    interface PrimitivesWithDefault {
        @Option(names = "-b", defaultValue = "true")
        boolean aBoolean();

        @Option(names = "-y", defaultValue = "11")
        byte aByte();

        @Option(names = "-s", defaultValue = "12")
        short aShort();

        @Option(names = "-i", defaultValue = "13")
        int anInt();

        @Option(names = "-l", defaultValue = "14")
        long aLong();

        @Option(names = "-f", defaultValue = "15.5")
        float aFloat();

        @Option(names = "-d", defaultValue = "16.6")
        double aDouble();
    }

    @Test
    public void testInterfaceIsInstantiated() {
        CommandLine.CommandLine cmd = new CommandLine.CommandLine(CommandLineAnnotatedMethodSpecTest.Primitives.class);
        Assert.assertTrue(((cmd.getCommand()) instanceof CommandLineAnnotatedMethodSpecTest.Primitives));
    }

    @Test
    public void testPrimitiveWithoutDefaultValues() {
        CommandLine.CommandLine cmd = new CommandLine.CommandLine(CommandLineAnnotatedMethodSpecTest.Primitives.class);
        cmd.parse();
        CommandLineAnnotatedMethodSpecTest.Primitives primitives = cmd.getCommand();
        Assert.assertFalse(primitives.aBoolean());
        Assert.assertEquals(0, primitives.aByte());
        Assert.assertEquals(((short) (0)), primitives.aShort());
        Assert.assertEquals(0, primitives.anInt());
        Assert.assertEquals(0, primitives.aLong());
        Assert.assertEquals(0, primitives.aFloat(), 1.0E-4);
        Assert.assertEquals(0, primitives.aDouble(), 1.0E-4);
    }

    @Test
    public void testPrimitivesWithDefaultValues() {
        CommandLine.CommandLine cmd = new CommandLine.CommandLine(CommandLineAnnotatedMethodSpecTest.PrimitivesWithDefault.class);
        cmd.parse();
        CommandLineAnnotatedMethodSpecTest.PrimitivesWithDefault primitives = cmd.getCommand();
        Assert.assertTrue(primitives.aBoolean());
        Assert.assertEquals(11, primitives.aByte());
        Assert.assertEquals(((short) (12)), primitives.aShort());
        Assert.assertEquals(13, primitives.anInt());
        Assert.assertEquals(14, primitives.aLong());
        Assert.assertEquals(15.5F, primitives.aFloat(), 1.0E-4);
        Assert.assertEquals(16.6, primitives.aDouble(), 1.0E-4);
    }

    @Test
    public void testPrimitives() {
        CommandLine.CommandLine cmd = new CommandLine.CommandLine(CommandLineAnnotatedMethodSpecTest.Primitives.class);
        cmd.parse("-b -y1 -s2 -i3 -l4 -f5 -d6".split(" "));
        CommandLineAnnotatedMethodSpecTest.Primitives primitives = cmd.getCommand();
        Assert.assertTrue(primitives.aBoolean());
        Assert.assertEquals(1, primitives.aByte());
        Assert.assertEquals(2, primitives.aShort());
        Assert.assertEquals(3, primitives.anInt());
        Assert.assertEquals(4, primitives.aLong());
        Assert.assertEquals(5, primitives.aFloat(), 1.0E-4);
        Assert.assertEquals(6, primitives.aDouble(), 1.0E-4);
    }

    interface Objects {
        @Option(names = "-b")
        Boolean aBoolean();

        @Option(names = "-y")
        Byte aByte();

        @Option(names = "-s")
        Short aShort();

        @Option(names = "-i")
        Integer anInt();

        @Option(names = "-l")
        Long aLong();

        @Option(names = "-f")
        Float aFloat();

        @Option(names = "-d")
        Double aDouble();

        @Option(names = "-bigint")
        BigInteger aBigInteger();

        @Option(names = "-string")
        String aString();

        @Option(names = "-list")
        List<String> getList();

        @Option(names = "-map")
        Map<Integer, Double> getMap();

        @Option(names = "-set")
        SortedSet<Short> getSortedSet();
    }

    interface ObjectsWithDefault {
        @Option(names = "-b", defaultValue = "true")
        Boolean aBoolean();

        @Option(names = "-y", defaultValue = "123")
        Byte aByte();

        @Option(names = "-s", defaultValue = "11")
        Short aShort();

        @Option(names = "-i", defaultValue = "12")
        Integer anInt();

        @Option(names = "-l", defaultValue = "13")
        Long aLong();

        @Option(names = "-f", defaultValue = "14.4")
        Float aFloat();

        @Option(names = "-d", defaultValue = "15.5")
        Double aDouble();

        @Option(names = "-bigint", defaultValue = "16.6")
        BigDecimal aBigDecimal();

        @Option(names = "-string", defaultValue = "abc")
        String aString();

        @Option(names = "-list", defaultValue = "a,b,c", split = ",")
        List<String> getList();

        @Option(names = "-map", defaultValue = "1=1,2=2,3=3", split = ",")
        Map<Integer, Double> getMap();

        @Option(names = "-set", defaultValue = "1,2,3", split = ",")
        SortedSet<Short> getSortedSet();
    }

    @Test
    public void testObjectsWithoutDefaultValues() {
        CommandLine.CommandLine cmd = new CommandLine.CommandLine(CommandLineAnnotatedMethodSpecTest.Objects.class);
        cmd.parse();
        CommandLineAnnotatedMethodSpecTest.Objects objects = cmd.getCommand();
        Assert.assertNull(objects.aBoolean());
        Assert.assertNull(objects.aByte());
        Assert.assertNull(objects.aShort());
        Assert.assertNull(objects.anInt());
        Assert.assertNull(objects.aLong());
        Assert.assertNull(objects.aFloat());
        Assert.assertNull(objects.aDouble());
        Assert.assertNull(objects.aBigInteger());
        Assert.assertNull(objects.aString());
        Assert.assertNull(objects.getList());
        Assert.assertNull(objects.getMap());
        Assert.assertNull(objects.getSortedSet());
    }

    @Test
    public void testObjectsWithDefaultValues() {
        CommandLine.CommandLine cmd = new CommandLine.CommandLine(CommandLineAnnotatedMethodSpecTest.ObjectsWithDefault.class);
        cmd.parse();
        CommandLineAnnotatedMethodSpecTest.ObjectsWithDefault objects = cmd.getCommand();
        Assert.assertTrue(objects.aBoolean());
        Assert.assertEquals(Byte.valueOf(((byte) (123))), objects.aByte());
        Assert.assertEquals(Short.valueOf(((short) (11))), objects.aShort());
        Assert.assertEquals(Integer.valueOf(12), objects.anInt());
        Assert.assertEquals(Long.valueOf(13), objects.aLong());
        Assert.assertEquals(14.4F, objects.aFloat(), 1.0E-4);
        Assert.assertEquals(15.5, objects.aDouble(), 1.0E-4);
        Assert.assertEquals(new BigDecimal("16.6"), objects.aBigDecimal());
        Assert.assertEquals("abc", objects.aString());
        Assert.assertEquals(Arrays.asList("a", "b", "c"), objects.getList());
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        map.put(1, 1.0);
        map.put(2, 2.0);
        map.put(3, 3.0);
        Assert.assertEquals(map, objects.getMap());
        Assert.assertEquals(new TreeSet<Short>(Arrays.asList(((short) (1)), ((short) (2)), ((short) (3)))), objects.getSortedSet());
    }

    @Test
    public void testObjects() {
        CommandLine.CommandLine cmd = new CommandLine.CommandLine(CommandLineAnnotatedMethodSpecTest.Objects.class);
        cmd.parse("-b -y1 -s2 -i3 -l4 -f5 -d6 -bigint=7 -string abc -list a -list b -map 1=2.0 -set 33 -set 22".split(" "));
        CommandLineAnnotatedMethodSpecTest.Objects objects = cmd.getCommand();
        Assert.assertTrue(objects.aBoolean());
        Assert.assertEquals(Byte.valueOf(((byte) (1))), objects.aByte());
        Assert.assertEquals(Short.valueOf(((short) (2))), objects.aShort());
        Assert.assertEquals(Integer.valueOf(3), objects.anInt());
        Assert.assertEquals(Long.valueOf(4), objects.aLong());
        Assert.assertEquals(5.0F, objects.aFloat(), 1.0E-4);
        Assert.assertEquals(6.0, objects.aDouble(), 1.0E-4);
        Assert.assertEquals(BigInteger.valueOf(7), objects.aBigInteger());
        Assert.assertEquals("abc", objects.aString());
        Assert.assertEquals(Arrays.asList("a", "b"), objects.getList());
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        map.put(1, 2.0);
        Assert.assertEquals(map, objects.getMap());
        Set<Short> set = new TreeSet<Short>();
        set.add(((short) (22)));
        set.add(((short) (33)));
        Assert.assertEquals(set, objects.getSortedSet());
    }

    interface InvalidAnnotatedStringOrPrimitiveFields {
        @Option(names = "-i")
        int anInt = 0;

        @Option(names = "-s")
        String aString = null;
    }

    @Test
    public void testInvalidAnnotatedFieldsOnInterface() {
        try {
            new CommandLine.CommandLine(CommandLineAnnotatedMethodSpecTest.InvalidAnnotatedStringOrPrimitiveFields.class);
            Assert.fail("Expected exception");
        } catch (InitializationException ok) {
            Assert.assertEquals("Invalid picocli annotation on interface field", ok.getMessage());
        }
    }

    interface InvalidAnnotatedMutableFields {
        @Option(names = "-s")
        final List<String> aList = new ArrayList<String>();
    }

    @Test
    public void testAnnotatedMutableFieldsOnInterfaceAreValid() {
        try {
            CommandLine.CommandLine cmd = new CommandLine.CommandLine(CommandLineAnnotatedMethodSpecTest.InvalidAnnotatedMutableFields.class);
            cmd.parse("-s a -s b -s c".split(" "));
            Assert.fail("Expected exception");
        } catch (InitializationException ok) {
            Assert.assertEquals("Invalid picocli annotation on interface field", ok.getMessage());
        }
    }

    @Test
    public void testPopulateSpec() {
        CommandLineAnnotatedMethodSpecTest.Objects objects = CommandLine.CommandLine.populateSpec(CommandLineAnnotatedMethodSpecTest.Objects.class, "-b -y1 -s2 -i3 -l4 -f5 -d6 -bigint=7 -string abc -list a -list b -map 1=2.0 -set 33 -set 22".split(" "));
        Assert.assertTrue(objects.aBoolean());
        Assert.assertEquals(Byte.valueOf(((byte) (1))), objects.aByte());
        Assert.assertEquals(Short.valueOf(((short) (2))), objects.aShort());
        Assert.assertEquals(Integer.valueOf(3), objects.anInt());
        Assert.assertEquals(Long.valueOf(4), objects.aLong());
        Assert.assertEquals(5.0F, objects.aFloat(), 1.0E-4);
        Assert.assertEquals(6.0, objects.aDouble(), 1.0E-4);
        Assert.assertEquals(BigInteger.valueOf(7), objects.aBigInteger());
        Assert.assertEquals("abc", objects.aString());
        Assert.assertEquals(Arrays.asList("a", "b"), objects.getList());
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        map.put(1, 2.0);
        Assert.assertEquals(map, objects.getMap());
        Set<Short> set = new TreeSet<Short>();
        set.add(((short) (22)));
        set.add(((short) (33)));
        Assert.assertEquals(set, objects.getSortedSet());
    }
}

