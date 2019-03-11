package org.cf.util;


import TypeFormat.BINARY;
import TypeFormat.INTERNAL;
import TypeFormat.SOURCE;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class ClassNameUtilsTest {
    private static final Map<String, String> internalNameToBinaryName;

    private static final Map<String, Boolean> internalNameToIsPrimitive;

    private static final Map<String, String> internalNameToSourceName;

    private static final Map<String, Class<?>> internalPrimitiveToWrapperClass;

    static {
        internalNameToBinaryName = new HashMap<String, String>();
        ClassNameUtilsTest.internalNameToBinaryName.put("Lthis/is/Test;", "this.is.Test");
        ClassNameUtilsTest.internalNameToBinaryName.put("[Lthis/is/Test;", "[Lthis.is.Test;");
        ClassNameUtilsTest.internalNameToBinaryName.put("I", "int");
        ClassNameUtilsTest.internalNameToBinaryName.put("B", "byte");
        ClassNameUtilsTest.internalNameToBinaryName.put("V", "void");
        ClassNameUtilsTest.internalNameToBinaryName.put("[I", int[].class.getName());// [I

        ClassNameUtilsTest.internalNameToBinaryName.put("[[Z", boolean[][].class.getName());// [[Z

        internalNameToSourceName = new HashMap<String, String>();
        ClassNameUtilsTest.internalNameToSourceName.put("Lthis/is/Test;", "this.is.Test");
        ClassNameUtilsTest.internalNameToSourceName.put("[Lthis/is/Test;", "this.is.Test[]");
        ClassNameUtilsTest.internalNameToSourceName.put("I", "int");
        ClassNameUtilsTest.internalNameToSourceName.put("B", "byte");
        ClassNameUtilsTest.internalNameToSourceName.put("V", "void");
        ClassNameUtilsTest.internalNameToSourceName.put("[I", "int[]");
        ClassNameUtilsTest.internalNameToSourceName.put("[[Z", "boolean[][]");
        internalNameToIsPrimitive = new HashMap<String, Boolean>();
        ClassNameUtilsTest.internalNameToIsPrimitive.put("Lsome/class;", false);
        ClassNameUtilsTest.internalNameToIsPrimitive.put("[Lsome/class;", false);
        ClassNameUtilsTest.internalNameToIsPrimitive.put("I", true);
        ClassNameUtilsTest.internalNameToIsPrimitive.put("Z", true);
        ClassNameUtilsTest.internalNameToIsPrimitive.put("C", true);
        ClassNameUtilsTest.internalNameToIsPrimitive.put("S", true);
        ClassNameUtilsTest.internalNameToIsPrimitive.put("D", true);
        ClassNameUtilsTest.internalNameToIsPrimitive.put("F", true);
        ClassNameUtilsTest.internalNameToIsPrimitive.put("J", true);
        ClassNameUtilsTest.internalNameToIsPrimitive.put("B", true);
        ClassNameUtilsTest.internalNameToIsPrimitive.put("V", true);
        // Arrays are mutable objects, not primitives
        ClassNameUtilsTest.internalNameToIsPrimitive.put("[I", false);
        ClassNameUtilsTest.internalNameToIsPrimitive.put("[[I", false);
        internalPrimitiveToWrapperClass = new HashMap<String, Class<?>>();
        ClassNameUtilsTest.internalPrimitiveToWrapperClass.put("I", Integer.class);
        ClassNameUtilsTest.internalPrimitiveToWrapperClass.put("Z", Boolean.class);
        ClassNameUtilsTest.internalPrimitiveToWrapperClass.put("C", Character.class);
        ClassNameUtilsTest.internalPrimitiveToWrapperClass.put("S", Short.class);
        ClassNameUtilsTest.internalPrimitiveToWrapperClass.put("D", Double.class);
        ClassNameUtilsTest.internalPrimitiveToWrapperClass.put("F", Float.class);
        ClassNameUtilsTest.internalPrimitiveToWrapperClass.put("J", Long.class);
        ClassNameUtilsTest.internalPrimitiveToWrapperClass.put("B", Byte.class);
        ClassNameUtilsTest.internalPrimitiveToWrapperClass.put("[I", Integer[].class);
        ClassNameUtilsTest.internalPrimitiveToWrapperClass.put("[[I", Integer[][].class);
    }

    @Test
    public void canConvertBinaryToInternal() {
        for (Map.Entry<String, String> entry : ClassNameUtilsTest.internalNameToBinaryName.entrySet()) {
            String className = entry.getValue();
            String expected = entry.getKey();
            String actual = ClassNameUtils.binaryToInternal(className);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void canConvertInternalToBinary() {
        for (Map.Entry<String, String> entry : ClassNameUtilsTest.internalNameToBinaryName.entrySet()) {
            String className = entry.getKey();
            String expected = entry.getValue();
            String actual = ClassNameUtils.internalToBinary(className);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void canConvertInternalToSource() {
        for (Map.Entry<String, String> entry : ClassNameUtilsTest.internalNameToSourceName.entrySet()) {
            String className = entry.getKey();
            String expected = entry.getValue();
            String actual = ClassNameUtils.internalToSource(className);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void canConvertSourceToBinary() {
        for (Map.Entry<String, String> entry : ClassNameUtilsTest.internalNameToSourceName.entrySet()) {
            String className = entry.getValue();
            String expected = ClassNameUtilsTest.internalNameToBinaryName.get(entry.getKey());
            String actual = ClassNameUtils.sourceToBinary(className);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void canConvertSurceToInternal() {
        for (Map.Entry<String, String> entry : ClassNameUtilsTest.internalNameToSourceName.entrySet()) {
            String className = entry.getValue();
            String expected = entry.getKey();
            String actual = ClassNameUtils.sourceToInternal(className);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void canDetermineIsPrimitive() {
        for (Map.Entry<String, Boolean> entry : ClassNameUtilsTest.internalNameToIsPrimitive.entrySet()) {
            String className = entry.getKey();
            boolean expected = entry.getValue();
            boolean actual = ClassNameUtils.isPrimitive(className);
            Assert.assertEquals(("class: " + className), expected, actual);
        }
    }

    @Test
    public void canGetComponentBase() {
        String componentBase;
        componentBase = ClassNameUtils.getComponentBase("[[[[[[Lsome/class;");
        Assert.assertEquals("Lsome/class;", componentBase);
        componentBase = ClassNameUtils.getComponentBase("[[[Lsome.class;");
        Assert.assertEquals("Lsome.class;", componentBase);
        componentBase = ClassNameUtils.getComponentBase("some.class[][][]");
        Assert.assertEquals("some.class", componentBase);
    }

    @Test
    public void canGetComponentType() {
        String componentType;
        componentType = ClassNameUtils.getComponentType("[[[[[[Lsome/class;");
        Assert.assertEquals("[[[[[Lsome/class;", componentType);
        componentType = ClassNameUtils.getComponentType("[[[Lsome.class;");
        Assert.assertEquals("[[Lsome.class;", componentType);
        componentType = ClassNameUtils.getComponentType("some.class[][][]");
        Assert.assertEquals("some.class[][]", componentType);
    }

    @Test
    public void canGetPackageName() {
        Assert.assertEquals("some.package", ClassNameUtils.getPackageName("Lsome/package/Class;"));
        Assert.assertEquals("", ClassNameUtils.getPackageName("LSomeClass;"));
        Assert.assertEquals("", ClassNameUtils.getPackageName("SomeClass"));
        Assert.assertEquals("", ClassNameUtils.getPackageName("SomeClass[]"));
        Assert.assertEquals("", ClassNameUtils.getPackageName("[LSomeClass;"));
    }

    @Test
    public void canGetPrimitive() {
        String primitive;
        primitive = ClassNameUtils.getPrimitive("Ljava/lang/Long;");
        Assert.assertEquals("J", primitive);
        primitive = ClassNameUtils.getPrimitive("Ljava.lang.Long;");
        Assert.assertEquals("J", primitive);
        primitive = ClassNameUtils.getPrimitive("java.lang.Long");
        Assert.assertEquals("J", primitive);
        primitive = ClassNameUtils.getPrimitive("[[[Ljava.lang.Integer;");
        Assert.assertEquals("[[[I", primitive);
    }

    @Test
    public void canGetPrimitiveWrapper() {
        for (Map.Entry<String, Class<?>> entry : ClassNameUtilsTest.internalPrimitiveToWrapperClass.entrySet()) {
            String className = entry.getKey();
            String expected = entry.getValue().getName();
            String actual = ClassNameUtils.getWrapper(className);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void getDimensionCountOfRandomRankArrayReturnsExpected() {
        Random rng = new Random();
        for (int run = 0; run < 100; run++) {
            StringBuilder sb = new StringBuilder();
            int expected = rng.nextInt(10);
            for (int i = 0; i < expected; i++) {
                sb.append('[');
            }
            sb.append("Lsome/Type;");
            String typeReference = sb.toString();
            int actual = ClassNameUtils.getDimensionCount(typeReference);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void getDimensionCountWithSourceFormat() {
        int actual = ClassNameUtils.getDimensionCount("java.lang.Object[][][]");
        Assert.assertEquals(3, actual);
    }

    @Test
    public void toFormatTest() {
        Assert.assertEquals("java.lang.Object", ClassNameUtils.toFormat("Ljava/lang/Object;", SOURCE));
        Assert.assertEquals("java.lang.Object", ClassNameUtils.toFormat("java.lang.Object", SOURCE));
        Assert.assertEquals("java.lang.Object[]", ClassNameUtils.toFormat("[Ljava.lang.Object;", SOURCE));
        Assert.assertEquals("SomeClass", ClassNameUtils.toFormat("LSomeClass;", SOURCE));
        Assert.assertEquals("SomeClass", ClassNameUtils.toFormat("SomeClass", SOURCE));
        Assert.assertEquals("SomeClass[]", ClassNameUtils.toFormat("[LSomeClass;", SOURCE));
        Assert.assertEquals("[Ljava/lang/Object;", ClassNameUtils.toFormat("java.lang.Object[]", INTERNAL));
        Assert.assertEquals("I", ClassNameUtils.toFormat("int", INTERNAL));
        Assert.assertEquals("[I", ClassNameUtils.toFormat("int[]", INTERNAL));
        Assert.assertEquals("[Ljava/lang/Object;", ClassNameUtils.toFormat("[Ljava.lang.Object;", INTERNAL));
        Assert.assertEquals("[Ljava/lang/Object;", ClassNameUtils.toFormat("[Ljava/lang/Object;", INTERNAL));
        Assert.assertEquals("[Ljava.lang.Object;", ClassNameUtils.toFormat("java.lang.Object[]", BINARY));
        Assert.assertEquals("java.lang.Object", ClassNameUtils.toFormat("java.lang.Object", BINARY));
        Assert.assertEquals("java.lang.Object", ClassNameUtils.toFormat("Ljava/lang/Object;", BINARY));
        Assert.assertEquals("int", ClassNameUtils.toFormat("I", BINARY));
        Assert.assertEquals("[I", ClassNameUtils.toFormat("[I", BINARY));
    }
}

