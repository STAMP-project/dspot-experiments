package org.cf.smalivm.opcode;


import CommonTypes.OBJECT;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.ClassNameUtils;
import org.junit.Assert;
import org.junit.Test;


public class APutOpTest {
    private static final String CLASS_NAME = "Laput_test;";

    private VMState initial;

    private VMState expected;

    @Test
    public void canInsertLocalClassAndClassIntoSameArray() throws ClassNotFoundException {
        String valueType = CommonTypes.CLASS;
        String arrayType = "[" + valueType;
        Object[] array = new Class<?>[2];
        int index1 = 0;
        int index2 = 1;
        Class<?> value1 = String.class;
        String binaryClassName = ClassNameUtils.internalToBinary(APutOpTest.CLASS_NAME);
        ClassLoader classLoader = VMTester.spawnVM().getClassLoader();
        Class<?> value2 = classLoader.loadClass(binaryClassName);
        initial.setRegisters(0, array, arrayType, 1, index1, "I", 2, value1, valueType, 3, index2, "I", 4, value2, valueType);
        expected.setRegisters(0, new Class<?>[]{ value1, value2 }, arrayType);
        VMTester.test(APutOpTest.CLASS_NAME, "putObjects()V", initial, expected);
    }

    @Test
    public void canArrayIntoObjectArray() {
        initial.setRegisters(0, new Object[1], ("[" + (CommonTypes.OBJECT)), 1, 0, "I", 2, new byte[1], "[B");
        expected.setRegisters(0, new Object[]{ new byte[1] }, ("[" + (CommonTypes.OBJECT)));
        VMTester.test(APutOpTest.CLASS_NAME, "putObject()V", initial, expected);
    }

    @Test
    public void canPutBoolean() {
        initial.setRegisters(0, new boolean[1], "[Z", 1, 0, "I", 2, 1, "Z");
        expected.setRegisters(0, new boolean[]{ true }, "[Z");
        VMTester.test(APutOpTest.CLASS_NAME, "putBoolean()V", initial, expected);
    }

    @Test
    public void canPutBooleanWithShortValue() {
        Short value = 1;
        initial.setRegisters(0, new boolean[1], "[Z", 1, 0, "I", 2, value, "Z");
        expected.setRegisters(0, new boolean[]{ true }, "[Z");
        VMTester.test(APutOpTest.CLASS_NAME, "putBoolean()V", initial, expected);
    }

    @Test
    public void canPutByte() {
        Byte value = 15;
        initial.setRegisters(0, new byte[1], "[B", 1, 0, "I", 2, value, "B");
        expected.setRegisters(0, new byte[]{ value }, "[B");
        VMTester.test(APutOpTest.CLASS_NAME, "putByte()V", initial, expected);
    }

    @Test
    public void canPutByteFromInt() {
        int value = 15;
        initial.setRegisters(0, new byte[1], "[B", 1, 0, "I", 2, value, "B");
        expected.setRegisters(0, new byte[]{ ((byte) (value)) }, "[B");
        VMTester.test(APutOpTest.CLASS_NAME, "putByte()V", initial, expected);
    }

    @Test
    public void canPutChar() {
        initial.setRegisters(0, new char[1], "[C", 1, 0, "I", 2, '$', "C");
        expected.setRegisters(0, new char[]{ '$' }, "[C");
        VMTester.test(APutOpTest.CLASS_NAME, "putChar()V", initial, expected);
    }

    @Test
    public void canPutCharFromInt() {
        initial.setRegisters(0, new char[1], "[C", 1, 0, "I", 2, ((int) ('$')), "I");
        expected.setRegisters(0, new char[]{ '$' }, "[C");
        VMTester.test(APutOpTest.CLASS_NAME, "putChar()V", initial, expected);
    }

    @Test
    public void canPutConstZeroNullObject() {
        String valueType = "I";
        String arrayType = "[" + valueType;
        Object[] array = new String[1];
        int index = 0;
        int value = 0;
        initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType);
        expected.setRegisters(0, new String[]{ null }, arrayType);
        VMTester.test(APutOpTest.CLASS_NAME, "putObject()V", initial, expected);
    }

    @Test
    public void canPutIntegerWithShortIndex() {
        Short index = 0;
        initial.setRegisters(0, new int[1], "[I", 1, index, "S", 2, 4, "I");
        expected.setRegisters(0, new int[]{ 4 }, "[I");
        VMTester.test(APutOpTest.CLASS_NAME, "put()V", initial, expected);
    }

    @Test
    public void canPutStringInStringArray() {
        String valueType = CommonTypes.STRING;
        String arrayType = "[" + valueType;
        String[] array = new String[1];
        int index = 0;
        String value = "Arrakis, Dune, desert planet...";
        initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType);
        expected.setRegisters(0, new String[]{ value }, arrayType);
        VMTester.test(APutOpTest.CLASS_NAME, "putObject()V", initial, expected);
    }

    @Test
    public void canPutStringInObjectArray() {
        String valueType = CommonTypes.STRING;
        String arrayType = "[" + (CommonTypes.OBJECT);
        Object[] array = new String[1];
        int index = 0;
        String value = "Arrakis, Dune, desert planet...";
        initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType);
        expected.setRegisters(0, new String[]{ value }, arrayType);
        VMTester.test(APutOpTest.CLASS_NAME, "putObject()V", initial, expected);
    }

    @Test
    public void canPutStringArrayIn2DStringArray() {
        String valueType = "[" + (CommonTypes.STRING);
        String arrayType = "[" + valueType;
        String[][] array = new String[1][];
        int index = 0;
        String[] value = new String[]{ "Arrakis, Dune, desert planet..." };
        initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType);
        String[][] expectedValue = new String[1][];
        expectedValue[0] = value;
        expected.setRegisters(0, expectedValue, arrayType);
        VMTester.test(APutOpTest.CLASS_NAME, "putObject()V", initial, expected);
    }

    @Test
    public void canPutStringArrayIn2DObjectArray() {
        String valueType = "[" + (CommonTypes.STRING);
        String arrayType = "[[" + (CommonTypes.OBJECT);
        String[][] array = new String[1][];
        int index = 0;
        String[] value = new String[]{ "Arrakis, Dune, desert planet..." };
        initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType);
        String[][] expectedValue = new String[1][];
        expectedValue[0] = value;
        expected.setRegisters(0, expectedValue, arrayType);
        VMTester.test(APutOpTest.CLASS_NAME, "putObject()V", initial, expected);
    }

    @Test
    public void canPutShort() {
        Short value = 66;
        initial.setRegisters(0, new short[1], "[S", 1, 0, "I", 2, value, "S");
        expected.setRegisters(0, new short[]{ value }, "[S");
        VMTester.test(APutOpTest.CLASS_NAME, "putShort()V", initial, expected);
    }

    @Test
    public void canPutShortWithIntegerValue() {
        int value = 66;
        initial.setRegisters(0, new short[1], "[S", 1, 0, "I", 2, value, "I");
        expected.setRegisters(0, new short[]{ ((short) (value)) }, "[S");
        VMTester.test(APutOpTest.CLASS_NAME, "putShort()V", initial, expected);
    }

    @Test
    public void canPutIntoUnknownValueOfObjectTypeWithoutThrowingException() {
        initial.setRegisters(0, new UnknownValue(), OBJECT, 1, 0, "I", 2, 5, "I");
        expected.setRegisters(0, new UnknownValue(), OBJECT);
        VMTester.test(APutOpTest.CLASS_NAME, "put()V", initial, expected);
    }

    @Test
    public void canPutUnknownValue() {
        // TODO: Ideally, setting an element unknown shouldn't set entire array unknown.
        // This is tricky to handle gracefully. See APutOp for more details.
        initial.setRegisters(0, new int[1], "[I", 1, 0, "I", 2, new UnknownValue(), "I");
        expected.setRegisters(0, new UnknownValue(), "[I");
        VMTester.test(APutOpTest.CLASS_NAME, "put()V", initial, expected);
    }

    @Test
    public void canPutWideWithDouble() {
        Double value = 1.0E11;
        initial.setRegisters(0, new double[1], "[D", 1, 0, "I", 2, value, "D");
        expected.setRegisters(0, new double[]{ value }, "[D");
        VMTester.test(APutOpTest.CLASS_NAME, "putWide()V", initial, expected);
    }

    @Test
    public void canPutWideWithFloat() {
        Float value = 10.45F;
        initial.setRegisters(0, new float[1], "[F", 1, 0, "I", 2, value, "F");
        expected.setRegisters(0, new float[]{ value }, "[F");
        VMTester.test(APutOpTest.CLASS_NAME, "putWide()V", initial, expected);
    }

    @Test
    public void canPutWideWithLong() {
        Long value = 10000000000L;
        initial.setRegisters(0, new long[1], "[J", 1, 0, "I", 2, value, "J");
        expected.setRegisters(0, new long[]{ value }, "[J");
        VMTester.test(APutOpTest.CLASS_NAME, "putWide()V", initial, expected);
    }

    @Test
    public void canPutWithInteger() {
        initial.setRegisters(0, new int[1], "[I", 1, 0, "I", 2, 4, "I");
        expected.setRegisters(0, new int[]{ 4 }, "[I");
        VMTester.test(APutOpTest.CLASS_NAME, "put()V", initial, expected);
    }

    @Test
    public void canPutWithUnknownIndex() {
        initial.setRegisters(0, new int[1], "[I", 1, new UnknownValue(), "I", 2, 5, "I");
        expected.setRegisters(0, new UnknownValue(), "[I");
        VMTester.test(APutOpTest.CLASS_NAME, "put()V", initial, expected);
    }

    @Test
    public void nullArrayValueThrowsNullPointerExceptionAndHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, null, "[I", 1, 0, "I", 2, 0, "I");
        APutOpTest.testException("putWithCatch()V", NullPointerException.class, initial);
    }

    @Test
    public void outOfBoundsIndexThrowsArrayIndexOutOfBoundsExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, new int[5], "[I", 1, 10, "I", 2, 0, "I");
        APutOpTest.testException("putWithCatch()V", ArrayIndexOutOfBoundsException.class, initial);
    }

    @Test
    public void incompatibleValueTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, new int[5], "[I", 1, 0, "I", 2, "wrong type", "Ljava/lang/String;");
        APutOpTest.testException("putWithCatch()V", ArrayStoreException.class, initial);
    }

    @Test
    public void outOfBoundsIndexAndIncompatibleValueTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, new int[5], "[I", 1, 10, "I", 2, "wrong type", "Ljava/lang/String;");
        APutOpTest.testException("putWithCatch()V", ArrayStoreException.class, initial);
    }

    @Test
    public void unknownValueItemWithIncompatibleTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, new int[5], "[I", 1, 10, "I", 2, new UnknownValue(), "Ljava/lang/String;");
        APutOpTest.testException("putWithCatch()V", ArrayStoreException.class, initial);
    }

    @Test
    public void unknownArrayWithIncompatibleTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, new UnknownValue(), "[I", 1, 0, "I", 2, "wrong type", "Ljava/lang/String;");
        APutOpTest.testException("putWithCatch()V", ArrayStoreException.class, initial);
    }

    @Test
    public void unknownValueItemMakesArrayUnknownAndDoesNotClearExceptions() {
        initial.setRegisters(0, new int[5], "[I", 1, 0, "I", 2, new UnknownValue(), "I");
        ExecutionGraph graph = VMTester.execute(APutOpTest.CLASS_NAME, "putWithCatch()V", initial);
        ExecutionNode putNode = graph.getNodePile(0).get(0);
        Set<Throwable> exceptions = putNode.getExceptions();
        Assert.assertEquals(2, exceptions.size());
        List<Class<?>> exceptionClasses = exceptions.stream().map(Throwable::getClass).collect(Collectors.toList());
        Assert.assertTrue(exceptionClasses.contains(ArrayIndexOutOfBoundsException.class));
        Assert.assertTrue(exceptionClasses.contains(NullPointerException.class));
        HeapItem item = graph.getTerminatingRegisterConsensus(0);
        Assert.assertEquals(OBJECT, item.getType());
        Assert.assertEquals(UnknownValue.class, item.getValue().getClass());
    }
}

