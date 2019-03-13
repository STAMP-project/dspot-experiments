package org.cf.smalivm;


import CommonTypes.BOOLEAN_OBJ;
import CommonTypes.BYTE_OBJ;
import CommonTypes.CHARACTER_OBJ;
import CommonTypes.CLASS;
import CommonTypes.INTEGER;
import CommonTypes.SHORT_OBJ;
import CommonTypes.STRING;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.util.ClassNameUtils;
import org.junit.Assert;
import org.junit.Test;


public class MethodReflectorTest {
    private static final String CLASS_NAME = "Lmethod_reflector_test;";

    private VMState expected;

    private VMState initial;

    @Test
    @SuppressWarnings({ "unchecked" })
    public void canReflectivelyInstantiateAnEnum() throws ClassNotFoundException {
        String className = "Lextends_enum;";
        String methodDescriptor = "<init>(Ljava/lang/String;II)V";
        String enumName = "NONE";
        VirtualMachine vm = VMTester.spawnVM();
        VirtualClass virtualClass = vm.getClassManager().getVirtualClass(className);
        VirtualMethod method = virtualClass.getMethod(methodDescriptor);
        int offset = (method.getRegisterCount()) - (method.getParameterSize());
        initial.setRegister(offset, new org.cf.smalivm.type.UninitializedInstance(virtualClass), className);
        initial.setRegister((offset + 1), enumName, STRING);
        initial.setRegister((offset + 2), 0, INTEGER);
        initial.setRegister((offset + 3), 0, INTEGER);
        ExecutionGraph graph = VMTester.execute(vm, className, methodDescriptor, initial);
        HeapItem instance = graph.getTerminatingRegisterConsensus(0);
        Class<? extends Enum> klazz = ((Class<? extends Enum>) (vm.getClassLoader().loadClass(ClassNameUtils.internalToSource(className))));
        Object expectedValue = Enum.valueOf(klazz, enumName);
        Assert.assertEquals(expectedValue, instance.getValue());
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void canReflectivelyInstantiateAnObfuscatedEnum() throws ClassNotFoundException {
        String className = "Lextends_enum_obfuscated;";
        String methodDescriptor = "<init>(Ljava/lang/String;II)V";
        String enumName = "WEAK";
        String obfuscatedEnumName = "c";
        VirtualMachine vm = VMTester.spawnVM();
        VirtualClass virtualClass = vm.getClassManager().getVirtualClass(className);
        VirtualMethod method = virtualClass.getMethod(methodDescriptor);
        int offset = (method.getRegisterCount()) - (method.getParameterSize());
        initial.setRegister(offset, new org.cf.smalivm.type.UninitializedInstance(virtualClass), className);
        initial.setRegister((offset + 1), enumName, STRING);
        initial.setRegister((offset + 2), 0, INTEGER);
        initial.setRegister((offset + 3), 0, INTEGER);
        ExecutionGraph graph = VMTester.execute(vm, className, methodDescriptor, initial);
        HeapItem instance = graph.getTerminatingRegisterConsensus(0);
        Class<? extends Enum> klazz = ((Class<? extends Enum>) (vm.getClassLoader().loadClass(ClassNameUtils.internalToSource(className))));
        Object expectedValue = Enum.valueOf(klazz, obfuscatedEnumName);
        Assert.assertEquals(expectedValue, instance.getValue());
    }

    @Test
    public void methodWithPrimitiveReturnValueTypeActuallyReturnsPrimitiveType() {
        int intVal = 42;
        initial.setRegisters(0, Integer.valueOf(intVal), "Ljava/lang/Integer;");
        expected.setRegisters(0, intVal, INTEGER);
        VMTester.test(MethodReflectorTest.CLASS_NAME, "intValueOfInteger()V", initial, expected);
    }

    @Test
    public void canCastIntegerToByte() {
        byte value = 6;
        initial.setRegisters(0, value, "B");
        expected.setRegisters(0, value, BYTE_OBJ);
        VMTester.test(MethodReflectorTest.CLASS_NAME, "byteValueOfByte()V", initial, expected);
    }

    @Test
    public void canInitBooleanWithBoolean() {
        boolean value = true;
        initial.setRegisters(1, value, "Z");
        expected.setRegisters(0, value, BOOLEAN_OBJ);
        VMTester.test(MethodReflectorTest.CLASS_NAME, "initBooleanWithBoolean()V", initial, expected);
    }

    @Test
    public void canInitCharacterWithChar() {
        char value = 'a';
        initial.setRegisters(1, value, "C");
        expected.setRegisters(0, value, CHARACTER_OBJ);
        VMTester.test(MethodReflectorTest.CLASS_NAME, "initCharacterWithChar()V", initial, expected);
    }

    @Test
    public void canGetShortValueOfShort() {
        short value = 5;
        initial.setRegisters(0, value, "S");
        expected.setRegisters(0, value, SHORT_OBJ);
        VMTester.test(MethodReflectorTest.CLASS_NAME, "shortValueOfShort()V", initial, expected);
    }

    @Test
    public void handlesNullArgument() throws NoSuchMethodException, SecurityException {
        initial.setRegisters(0, System.class, CLASS, 1, "currentTimeMillis", STRING, 2, 0, "I");
        expected.setRegisters(0, System.class.getMethod("currentTimeMillis", ((Class<?>[]) (null))), "Ljava/lang/reflect/Method;");
        VMTester.test(MethodReflectorTest.CLASS_NAME, "getClassMethod()V", initial, expected);
    }

    @Test
    public void handlesException() throws NoSuchMethodException, SecurityException {
        initial.setRegisters(0, null, STRING);
        int[] expected = new int[]{ 0, 4 };
        VMTester.testVisitation(MethodReflectorTest.CLASS_NAME, "stringLength()V", initial, expected);
    }

    @Test
    public void handlesNoException() throws NoSuchMethodException, SecurityException {
        initial.setRegisters(0, "four", STRING);
        int[] expected = new int[]{ 0, 3, 4 };
        VMTester.testVisitation(MethodReflectorTest.CLASS_NAME, "stringLength()V", initial, expected);
    }
}

