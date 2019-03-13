/**
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.reflect;


import FieldMatchingStrategy.MATCHING;
import FieldMatchingStrategy.STRICT;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.reflect.context.ClassFieldsNotInTargetContext;
import org.powermock.reflect.context.InstanceFieldsNotInTargetContext;
import org.powermock.reflect.context.MyContext;
import org.powermock.reflect.context.MyIntContext;
import org.powermock.reflect.context.MyStringContext;
import org.powermock.reflect.context.OneInstanceAndOneStaticFieldOfSameTypeContext;
import org.powermock.reflect.exceptions.FieldNotFoundException;
import org.powermock.reflect.exceptions.MethodNotFoundException;
import org.powermock.reflect.exceptions.TooManyFieldsFoundException;
import org.powermock.reflect.exceptions.TooManyMethodsFoundException;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.powermock.reflect.testclasses.AbstractClass;
import org.powermock.reflect.testclasses.AnInterface;
import org.powermock.reflect.testclasses.Child;
import org.powermock.reflect.testclasses.ClassWithAMethod;
import org.powermock.reflect.testclasses.ClassWithChildThatHasInternalState;
import org.powermock.reflect.testclasses.ClassWithInterfaceConstructors;
import org.powermock.reflect.testclasses.ClassWithInternalState;
import org.powermock.reflect.testclasses.ClassWithList;
import org.powermock.reflect.testclasses.ClassWithObjectConstructors;
import org.powermock.reflect.testclasses.ClassWithOverloadedConstructors;
import org.powermock.reflect.testclasses.ClassWithOverloadedMethods;
import org.powermock.reflect.testclasses.ClassWithOverriddenMethod;
import org.powermock.reflect.testclasses.ClassWithPrimitiveConstructors;
import org.powermock.reflect.testclasses.ClassWithPrivateMethods;
import org.powermock.reflect.testclasses.ClassWithSerializableState;
import org.powermock.reflect.testclasses.ClassWithSeveralMethodsWithSameName;
import org.powermock.reflect.testclasses.ClassWithSeveralMethodsWithSameNameOneWithoutParameters;
import org.powermock.reflect.testclasses.ClassWithSimpleInternalState;
import org.powermock.reflect.testclasses.ClassWithStaticAndInstanceInternalStateOfSameType;
import org.powermock.reflect.testclasses.ClassWithStaticMethod;
import org.powermock.reflect.testclasses.ClassWithUniquePrivateMethods;
import org.powermock.reflect.testclasses.ClassWithVarArgsConstructor;
import org.powermock.reflect.testclasses.ClassWithVarArgsConstructor2;


/**
 * Tests the WhiteBox's functionality.
 */
public class WhiteBoxTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testFindMethod_classContainingMethodWithNoParameters() throws Exception {
        Method expected = ClassWithSeveralMethodsWithSameNameOneWithoutParameters.class.getMethod("getDouble");
        Method actual = WhiteboxImpl.findMethodOrThrowException(ClassWithSeveralMethodsWithSameNameOneWithoutParameters.class, "getDouble");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFindMethod_classContainingOnlyMethodsWithParameters() throws Exception {
        try {
            WhiteboxImpl.findMethodOrThrowException(ClassWithSeveralMethodsWithSameName.class, "getDouble");
            Assert.fail("Should throw runtime exception!");
        } catch (RuntimeException e) {
            Assert.assertTrue("Error message did not match", e.getMessage().contains("Several matching methods found, please specify the argument parameter types"));
        }
    }

    @Test
    public void testFindMethod_noMethodFound() throws Exception {
        try {
            WhiteboxImpl.findMethodOrThrowException(ClassWithSeveralMethodsWithSameName.class, "getDouble2");
            Assert.fail("Should throw runtime exception!");
        } catch (RuntimeException e) {
            Assert.assertEquals("Error message did not match", (("No method found with name 'getDouble2' with parameter types: [ <none> ] in class " + (ClassWithSeveralMethodsWithSameName.class.getName())) + "."), e.getMessage());
        }
    }

    @Test
    public void testGetInternalState_object() throws Exception {
        ClassWithInternalState tested = new ClassWithInternalState();
        tested.increaseInteralState();
        Object internalState = Whitebox.getInternalState(tested, "internalState");
        Assert.assertTrue("InternalState should be instanceof Integer", (internalState instanceof Integer));
        Assert.assertEquals(1, internalState);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetInternalState_parmaterizedType() throws Exception {
        ClassWithInternalState tested = new ClassWithInternalState();
        tested.increaseInteralState();
        int internalState = Whitebox.getInternalState(tested, "internalState", tested.getClass(), int.class);
        Assert.assertEquals(1, internalState);
    }

    @Test
    public void testSetInternalState() throws Exception {
        ClassWithInternalState tested = new ClassWithInternalState();
        tested.increaseInteralState();
        Whitebox.setInternalState(tested, "anotherInternalState", 2);
        Assert.assertEquals(2, tested.getAnotherInternalState());
    }

    @Test
    public void testSetInternalStateWithMultipleValues() throws Exception {
        ClassWithInternalState tested = new ClassWithInternalState();
        final ClassWithPrivateMethods classWithPrivateMethods = new ClassWithPrivateMethods();
        final String stringState = "someStringState";
        Whitebox.setInternalState(tested, classWithPrivateMethods, stringState);
        Assert.assertEquals(stringState, Whitebox.getInternalState(tested, String.class));
        Assert.assertSame(classWithPrivateMethods, Whitebox.getInternalState(tested, ClassWithPrivateMethods.class));
    }

    @Test
    public void testSetInternalState_superClass() throws Exception {
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState();
        tested.increaseInteralState();
        Whitebox.setInternalState(tested, "anotherInternalState", 2, ClassWithInternalState.class);
        Assert.assertEquals(2, tested.getAnotherInternalState());
    }

    @Test
    public void testGetInternalState_superClass_object() throws Exception {
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState();
        Object internalState = Whitebox.getInternalState(tested, "internalState", ClassWithInternalState.class);
        Assert.assertEquals(0, internalState);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetInternalState_superClass_parameterized() throws Exception {
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState();
        int internalState = Whitebox.getInternalState(tested, "internalState", ClassWithInternalState.class, int.class);
        Assert.assertEquals(0, internalState);
    }

    @Test
    public void testInvokePrivateMethod_primtiveType() throws Exception {
        Assert.assertTrue(Whitebox.<Boolean>invokeMethod(new ClassWithPrivateMethods(), "primitiveMethod", 8.2));
    }

    @Test
    public void testInvokePrivateMethod_primtiveType_withoutSpecifyingMethodName() throws Exception {
        Assert.assertTrue(((Boolean) (Whitebox.invokeMethod(new ClassWithUniquePrivateMethods(), 8.2, 8.4))));
    }

    @Test
    public void testInvokePrivateMethod_withoutSpecifyingMethodName_assertThatNullWorks() throws Exception {
        Assert.assertTrue(((Whitebox.invokeMethod(new ClassWithUniquePrivateMethods(), 8.2, 8.3, null)) instanceof Object));
    }

    /**
     * This test should actually fail since equals takes an Object and we pass
     * in a primitive wrapped as a Double. Thus PowerMock cannot determine
     * whether to invoke the single argument method defined in
     * {@link ClassWithUniquePrivateMethods} or the
     * {@link Object#equals(Object)} method because we could potentially invoke
     * equals with a Double.
     */
    @Test(expected = TooManyMethodsFoundException.class)
    public void testInvokePrivateMethod_withoutSpecifyingMethodName_onlyOneArgument() throws Exception {
        Whitebox.invokeMethod(new ClassWithUniquePrivateMethods(), 8.2);
    }

    @Test(expected = TooManyMethodsFoundException.class)
    public void testInvokeStaticPrivateMethod_withoutSpecifyingMethodName_onlyOneArgument() throws Exception {
        Assert.assertTrue(((Boolean) (Whitebox.invokeMethod(ClassWithUniquePrivateMethods.class, 8.2))));
    }

    @Test
    public void testInvokePrivateMethod_primtiveType_Wrapped() throws Exception {
        Assert.assertTrue(((Boolean) (Whitebox.invokeMethod(new ClassWithPrivateMethods(), "primitiveMethod", new Double(8.2)))));
    }

    @Test
    public void testInvokePrivateMethod_wrappedType() throws Exception {
        Assert.assertTrue(((Boolean) (Whitebox.invokeMethod(new ClassWithPrivateMethods(), "wrappedMethod", new Double(8.2)))));
    }

    @Test
    public void testInvokePrivateMethod_wrappedType_primitive() throws Exception {
        Assert.assertTrue(((Boolean) (Whitebox.invokeMethod(new ClassWithPrivateMethods(), "wrappedMethod", 8.2))));
    }

    @Test
    public void testMethodWithPrimitiveIntAndString_primitive() throws Exception {
        Assert.assertEquals(("My int value is: " + 8), Whitebox.invokeMethod(new ClassWithPrivateMethods(), "methodWithPrimitiveIntAndString", 8, "My int value is: "));
    }

    @Test
    public void testMethodWithPrimitiveIntAndString_Wrapped() throws Exception {
        Assert.assertEquals(("My int value is: " + 8), Whitebox.invokeMethod(new ClassWithPrivateMethods(), "methodWithPrimitiveIntAndString", Integer.valueOf(8), "My int value is: "));
    }

    @Test
    public void testMethodWithPrimitiveAndWrappedInt_primtive_wrapped() throws Exception {
        Assert.assertEquals(17, Whitebox.invokeMethod(new ClassWithPrivateMethods(), "methodWithPrimitiveAndWrappedInt", new Class[]{ int.class, Integer.class }, 9, Integer.valueOf(8)));
    }

    @Test
    public void testStaticState() {
        int expected = 123;
        Whitebox.setInternalState(ClassWithInternalState.class, "staticState", expected);
        Assert.assertEquals(expected, ClassWithInternalState.getStaticState());
        Assert.assertEquals(expected, Whitebox.getInternalState(ClassWithInternalState.class, "staticState"));
    }

    @Test
    public void testStaticFinalPrimitiveState() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("You are trying to set a private static final primitive. Try using an object like Integer instead of int!");
        Whitebox.setInternalState(ClassWithInternalState.class, "staticFinalIntState", 123);
    }

    @Test
    public void testStaticFinalStringState() throws NoSuchFieldException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("You are trying to set a private static final String. Cannot set such fields!");
        Whitebox.setInternalState(ClassWithInternalState.class, "staticFinalStringState", "Brand new string");
    }

    @Test
    public void testStaticFinalObject() throws NoSuchFieldException {
        int modifiersBeforeSet = ClassWithInternalState.class.getDeclaredField("staticFinalIntegerState").getModifiers();
        Integer newValue = (ClassWithInternalState.getStaticFinalIntegerState()) + 1;
        Whitebox.setInternalState(ClassWithInternalState.class, "staticFinalIntegerState", newValue);
        int modifiersAfterSet = ClassWithInternalState.class.getDeclaredField("staticFinalIntegerState").getModifiers();
        Assert.assertEquals(newValue, ClassWithInternalState.getStaticFinalIntegerState());
        Assert.assertEquals(modifiersBeforeSet, modifiersAfterSet);
    }

    /**
     * Verifies that the http://code.google.com/p/powermock/issues/detail?id=6
     * is fixed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvokeMethodWithNullParameter() throws Exception {
        Whitebox.invokeMethod(null, "method");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvokeConstructorWithNullParameter() throws Exception {
        Whitebox.invokeConstructor(null, "constructor");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetInternalWithNullParameter() throws Exception {
        Whitebox.getInternalState(null, "state");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInternalWithNullParameter() throws Exception {
        Whitebox.setInternalState(null, "state", new Object());
    }

    @Test
    public void testInstantiateVarArgsOnlyConstructor() throws Exception {
        final String argument1 = "argument1";
        final String argument2 = "argument2";
        ClassWithVarArgsConstructor instance = Whitebox.invokeConstructor(ClassWithVarArgsConstructor.class, argument1, argument2);
        String[] strings = instance.getStrings();
        Assert.assertEquals(2, strings.length);
        Assert.assertEquals(argument1, strings[0]);
        Assert.assertEquals(argument2, strings[1]);
    }

    @Test
    public void testInstantiateVarArgsOnlyConstructor_noArguments() throws Exception {
        ClassWithVarArgsConstructor instance = Whitebox.invokeConstructor(ClassWithVarArgsConstructor.class);
        String[] strings = instance.getStrings();
        Assert.assertEquals(0, strings.length);
    }

    @Test
    public void testInvokeVarArgsMethod_multipleValues() throws Exception {
        ClassWithPrivateMethods tested = new ClassWithPrivateMethods();
        Assert.assertEquals(6, Whitebox.invokeMethod(tested, "varArgsMethod", 1, 2, 3));
    }

    @Test
    public void testInvokeVarArgsMethod_noArguments() throws Exception {
        ClassWithPrivateMethods tested = new ClassWithPrivateMethods();
        Assert.assertEquals(0, Whitebox.invokeMethod(tested, "varArgsMethod"));
    }

    @Test
    public void testInvokeVarArgsMethod_oneArgument() throws Exception {
        ClassWithPrivateMethods tested = new ClassWithPrivateMethods();
        Assert.assertEquals(4, Whitebox.invokeMethod(tested, "varArgsMethod", 2));
    }

    @Test
    public void testInvokeVarArgsMethod_invokeVarArgsWithOneArgument() throws Exception {
        ClassWithPrivateMethods tested = new ClassWithPrivateMethods();
        Assert.assertEquals(1, Whitebox.invokeMethod(tested, "varArgsMethod", new Class<?>[]{ int[].class }, 1));
    }

    @Test
    public void testInvokePrivateMethodWithASubTypeOfTheArgumentType() throws Exception {
        ClassWithPrivateMethods tested = new ClassWithPrivateMethods();
        ClassWithChildThatHasInternalState argument = new ClassWithChildThatHasInternalState();
        Assert.assertSame(argument, Whitebox.invokeMethod(tested, "methodWithObjectArgument", argument));
    }

    @Test
    public void testInvokePrivateMethodWithAClassArgument() throws Exception {
        ClassWithPrivateMethods tested = new ClassWithPrivateMethods();
        Assert.assertEquals(ClassWithChildThatHasInternalState.class, Whitebox.invokeMethod(tested, "methodWithClassArgument", ClassWithChildThatHasInternalState.class));
    }

    @Test
    public void testSetInternalStateInChildClassWithoutSpecifyingTheChildClass() throws Exception {
        final int value = 22;
        final String fieldName = "internalState";
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState() {};
        Whitebox.setInternalState(tested, fieldName, value);
        Assert.assertEquals(value, Whitebox.getInternalState(tested, fieldName));
    }

    @Test
    public void testSetInternalStateInClassAndMakeSureThatTheChildClassIsNotAffectedEvenThoughItHasAFieldWithTheSameName() throws Exception {
        final int value = 22;
        final String fieldName = "anotherInternalState";
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState() {};
        Whitebox.setInternalState(tested, fieldName, value);
        Assert.assertEquals(value, Whitebox.getInternalState(tested, fieldName));
        Assert.assertEquals((-1), Whitebox.getInternalState(tested, fieldName, ClassWithInternalState.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInternalStateWithInvalidArgumentType() throws Exception {
        final int value = 22;
        final String fieldName = "internalState";
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState() {};
        Whitebox.setInternalState(tested, fieldName, new Object());
        Assert.assertEquals(value, Whitebox.getInternalState(tested, fieldName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInternalStateWithNull() throws Exception {
        final int value = 22;
        final String fieldName = "internalState";
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState() {};
        Whitebox.setInternalState(tested, fieldName, ((Object) (null)));
        Assert.assertEquals(value, Whitebox.getInternalState(tested, fieldName));
    }

    @Test
    public void testSetAndGetInternalStateBasedOnFieldType() throws Exception {
        final int value = 22;
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState();
        Whitebox.setInternalState(tested, int.class, value);
        Assert.assertEquals(value, ((int) (Whitebox.getInternalState(tested, int.class))));
        Assert.assertEquals(value, Whitebox.getInternalState(tested, "anotherInternalState"));
        Assert.assertEquals(value, Whitebox.getInternalState(tested, "anotherInternalState", ClassWithChildThatHasInternalState.class));
    }

    @Test
    public void testSetAndGetInternalStateAtASpecificPlaceInTheHierarchyBasedOnFieldType() throws Exception {
        final int value = 22;
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState();
        Whitebox.setInternalState(tested, int.class, value, ClassWithInternalState.class);
        Assert.assertEquals(42, ((int) (Whitebox.getInternalState(tested, int.class))));
        Assert.assertEquals(value, ((int) (Whitebox.getInternalState(tested, int.class, ClassWithInternalState.class))));
        Assert.assertEquals(value, Whitebox.getInternalState(tested, "staticState", ClassWithInternalState.class));
    }

    @Test
    public void testSetInternalStateBasedOnObjectType() throws Exception {
        final String value = "a string";
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState();
        Whitebox.setInternalState(tested, value);
        Assert.assertEquals(value, Whitebox.getInternalState(tested, String.class));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSetInternalStateBasedOnObjectTypeWhenArgumentIsAPrimitiveType() throws Exception {
        final int value = 22;
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState();
        Whitebox.setInternalState(tested, value);
        Assert.assertEquals(((Integer) (value)), Whitebox.getInternalState(tested, "anotherInternalState", ClassWithChildThatHasInternalState.class, Integer.class));
    }

    @Test
    public void testSetInternalStateBasedOnObjectTypeWhenArgumentIsAPrimitiveTypeUsingGenerics() throws Exception {
        final int value = 22;
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState();
        Whitebox.setInternalState(tested, value);
        Assert.assertEquals(((Integer) (value)), Whitebox.<Integer>getInternalState(tested, "anotherInternalState", ClassWithChildThatHasInternalState.class));
    }

    @Test
    public void testSetInternalStateBasedOnObjectTypeAtASpecificPlaceInTheClassHierarchy() throws Exception {
        final String value = "a string";
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState();
        Whitebox.setInternalState(tested, ((Object) (value)), ClassWithInternalState.class);
        Assert.assertEquals(value, Whitebox.getInternalState(tested, "finalString"));
    }

    @Test
    public void testSetInternalStateBasedOnObjectTypeAtASpecificPlaceInTheClassHierarchyForPrimitiveType() throws Exception {
        final long value = 31;
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState();
        Whitebox.setInternalState(tested, value, ClassWithInternalState.class);
        Assert.assertEquals(value, tested.getInternalLongState());
    }

    @Test
    public void testSetInternalStateBasedOnObjectTypeAtANonSpecificPlaceInTheClassHierarchyForPrimitiveType() throws Exception {
        final long value = 31;
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState();
        Whitebox.setInternalState(tested, value);
        Assert.assertEquals(value, tested.getInternalLongState());
    }

    @Test
    public void testSetInternalMultipleOfSameTypeOnSpecificPlaceInHierarchy() throws Exception {
        final int value = 31;
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState();
        try {
            Whitebox.setInternalState(tested, value, ClassWithInternalState.class);
            Assert.fail("should throw TooManyFieldsFoundException!");
        } catch (TooManyFieldsFoundException e) {
            Assert.assertEquals("Two or more fields matching type int.", e.getMessage());
        }
    }

    @Test
    public void testSetInternalMultipleOfSameType() throws Exception {
        final int value = 31;
        ClassWithInternalState tested = new ClassWithInternalState();
        try {
            Whitebox.setInternalState(tested, value);
            Assert.fail("should throw TooManyFieldsFoundException!");
        } catch (TooManyFieldsFoundException e) {
            Assert.assertEquals("Two or more fields matching type int.", e.getMessage());
        }
    }

    @Test
    public void testSetInternalStateBasedOnObjectSubClassTypeAtASpecificPlaceInTheClassHierarchy() throws Exception {
        final ClassWithPrivateMethods value = new ClassWithPrivateMethods() {};
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState();
        Whitebox.setInternalState(tested, value, ClassWithInternalState.class);
        Assert.assertSame(value, tested.getClassWithPrivateMethods());
    }

    @Test
    public void testSetInternalStateBasedOnObjectSubClassType() throws Exception {
        final ClassWithPrivateMethods value = new ClassWithPrivateMethods() {};
        ClassWithChildThatHasInternalState tested = new ClassWithChildThatHasInternalState() {};
        Whitebox.setInternalState(tested, value);
        Assert.assertSame(value, tested.getClassWithPrivateMethods());
    }

    @Test
    public void testGetAllInstanceFields() throws Exception {
        Set<Field> allFields = Whitebox.getAllInstanceFields(new ClassWithChildThatHasInternalState());
        Assert.assertEquals(8, allFields.size());
    }

    @Test
    public void testGetAllStaticFields_assertNoFieldsFromParent() throws Exception {
        Set<Field> allFields = Whitebox.getAllStaticFields(ClassWithChildThatHasInternalState.class);
        Assert.assertEquals(0, allFields.size());
    }

    @Test
    public void testGetAllStaticFields() throws Exception {
        Set<Field> allFields = Whitebox.getAllStaticFields(ClassWithInternalState.class);
        Assert.assertEquals(4, allFields.size());
    }

    @Test
    public void testMethodWithNoMethodName_noMethodFound() throws Exception {
        try {
            Whitebox.getMethod(ClassWithInternalState.class, int.class);
            Assert.fail("Should throw MethodNotFoundException");
        } catch (MethodNotFoundException e) {
            Assert.assertEquals("No method was found with parameter types: [ int ] in class org.powermock.reflect.testclasses.ClassWithInternalState.", e.getMessage());
        }
    }

    @Test
    public void testMethodWithNoMethodName_tooManyMethodsFound() throws Exception {
        try {
            Whitebox.getMethod(ClassWithSeveralMethodsWithSameName.class);
            Assert.fail("Should throw TooManyMethodsFoundException");
        } catch (TooManyMethodsFoundException e) {
            Assert.assertTrue(e.getMessage().contains("Several matching methods found, please specify the method name so that PowerMock can determine which method you're referring to"));
        }
    }

    @Test
    public void testMethodWithNoMethodName_ok() throws Exception {
        final Method method = Whitebox.getMethod(ClassWithSeveralMethodsWithSameName.class, double.class);
        Assert.assertEquals(method, ClassWithSeveralMethodsWithSameName.class.getDeclaredMethod("getDouble", double.class));
    }

    @Test
    public void testGetTwoMethodsWhenNoneOfThemAreFound() throws Exception {
        try {
            Whitebox.getMethods(ClassWithSeveralMethodsWithSameName.class, "notFound1", "notFound2");
        } catch (MethodNotFoundException e) {
            Assert.assertEquals("No methods matching the name(s) notFound1 or notFound2 were found in the class hierarchy of class org.powermock.reflect.testclasses.ClassWithSeveralMethodsWithSameName.", e.getMessage());
        }
    }

    @Test
    public void testGetThreeMethodsWhenNoneOfThemAreFound() throws Exception {
        try {
            Whitebox.getMethods(ClassWithSeveralMethodsWithSameName.class, "notFound1", "notFound2", "notFound3");
        } catch (MethodNotFoundException e) {
            Assert.assertEquals("No methods matching the name(s) notFound1, notFound2 or notFound3 were found in the class hierarchy of class org.powermock.reflect.testclasses.ClassWithSeveralMethodsWithSameName.", e.getMessage());
        }
    }

    /**
     * Asserts that <a
     * href="http://code.google.com/p/powermock/issues/detail?id=118">issue
     * 118</a> is fixed. Thanks to cemcatik for finding this.
     */
    @Test
    public void testInvokeConstructorWithBothNormalAndVarArgsParameter() throws Exception {
        ClassWithVarArgsConstructor2 instance = Whitebox.invokeConstructor(ClassWithVarArgsConstructor2.class, "first", "second", "third");
        Assert.assertArrayEquals(new String[]{ "first", "second", "third" }, instance.getStrings());
    }

    /**
     * Asserts that <a
     * href="http://code.google.com/p/powermock/issues/detail?id=118">issue
     * 118</a> is fixed. Thanks to cemcatik for finding this.
     */
    @Test
    public void testInvokeMethodWithBothNormalAndVarArgsParameter() throws Exception {
        ClassWithPrivateMethods tested = new ClassWithPrivateMethods();
        Assert.assertEquals(4, Whitebox.invokeMethod(tested, "varArgsMethod2", 1, 2, 3));
    }

    @Test
    public void testInvokePrivateMethodWithArrayArgument() throws Exception {
        ClassWithPrivateMethods tested = new ClassWithPrivateMethods();
        Assert.assertEquals("Hello World", Whitebox.invokeMethod(tested, "evilConcatOfStrings", new Object[]{ new String[]{ "Hello ", "World" } }));
    }

    @Test
    public void testSetInternalStateFromContext_allStatesInSameOneContext() throws Exception {
        ClassWithSimpleInternalState tested = new ClassWithSimpleInternalState();
        MyContext context = new MyContext();
        Whitebox.setInternalStateFromContext(tested, context);
        Assert.assertEquals(context.getMyStringState(), tested.getSomeStringState());
        Assert.assertEquals(context.getMyIntState(), tested.getSomeIntState());
    }

    @Test
    public void testSetInternalStateFromContext_statesInDifferentContext() throws Exception {
        ClassWithSimpleInternalState tested = new ClassWithSimpleInternalState();
        MyIntContext myIntContext = new MyIntContext();
        MyStringContext myStringContext = new MyStringContext();
        Whitebox.setInternalStateFromContext(tested, myIntContext, myStringContext);
        Assert.assertEquals(myStringContext.getMyStringState(), tested.getSomeStringState());
        Assert.assertEquals(myIntContext.getSimpleIntState(), tested.getSomeIntState());
    }

    @Test
    public void testSetInternalStateFromContext_contextIsAClass() throws Exception {
        ClassWithSimpleInternalState tested = new ClassWithSimpleInternalState();
        Whitebox.setInternalStateFromContext(tested, MyContext.class);
        Assert.assertEquals(Whitebox.getInternalState(MyContext.class, long.class), ((Long) (tested.getSomeStaticLongState())));
    }

    @Test
    public void testSetInternalStateFromContext_contextIsAClassAndAnInstance() throws Exception {
        ClassWithSimpleInternalState tested = new ClassWithSimpleInternalState();
        MyContext myContext = new MyContext();
        Whitebox.setInternalStateFromContext(tested, MyContext.class, myContext);
        Assert.assertEquals(myContext.getMyStringState(), tested.getSomeStringState());
        Assert.assertEquals(myContext.getMyIntState(), tested.getSomeIntState());
        Assert.assertEquals(((Long) (myContext.getMyLongState())), ((Long) (tested.getSomeStaticLongState())));
    }

    @Test
    public void testSetInternalStateFromContext_contextHasOneInstanceAndOneStaticFieldOfSameType_onlyInstanceContext() throws Exception {
        ClassWithStaticAndInstanceInternalStateOfSameType.reset();
        ClassWithStaticAndInstanceInternalStateOfSameType tested = new ClassWithStaticAndInstanceInternalStateOfSameType();
        OneInstanceAndOneStaticFieldOfSameTypeContext context = new OneInstanceAndOneStaticFieldOfSameTypeContext();
        Whitebox.setInternalStateFromContext(tested, context);
        Assert.assertEquals(context.getMyStringState(), tested.getStringState());
        Assert.assertEquals("Static String state", tested.getStaticStringState());
    }

    @Test
    public void testSetInternalStateFromContext_contextHasOneInstanceAndOneStaticFieldOfSameType_onlyStaticContext() throws Exception {
        ClassWithStaticAndInstanceInternalStateOfSameType.reset();
        ClassWithStaticAndInstanceInternalStateOfSameType tested = new ClassWithStaticAndInstanceInternalStateOfSameType();
        Whitebox.setInternalStateFromContext(tested, OneInstanceAndOneStaticFieldOfSameTypeContext.class);
        Assert.assertEquals(OneInstanceAndOneStaticFieldOfSameTypeContext.getMyStaticStringState(), tested.getStaticStringState());
        Assert.assertEquals("String state", tested.getStringState());
    }

    @Test
    public void setInternalStateFromInstanceContextCopiesMatchingContextFieldsToTargetObjectByDefault() throws Exception {
        ClassWithSimpleInternalState tested = new ClassWithSimpleInternalState();
        InstanceFieldsNotInTargetContext fieldsNotInTargetContext = new InstanceFieldsNotInTargetContext();
        assertThat(tested.getSomeStringState()).isNotEqualTo(fieldsNotInTargetContext.getString());
        Whitebox.setInternalStateFromContext(tested, fieldsNotInTargetContext);
        Assert.assertEquals(tested.getSomeStringState(), fieldsNotInTargetContext.getString());
    }

    @Test
    public void setInternalStateFromInstanceContextCopiesMatchingContextFieldsToTargetObjectWhenSpecifyingMatchingStrategy() throws Exception {
        ClassWithSimpleInternalState tested = new ClassWithSimpleInternalState();
        InstanceFieldsNotInTargetContext fieldsNotInTargetContext = new InstanceFieldsNotInTargetContext();
        assertThat(tested.getSomeStringState()).isNotEqualTo(fieldsNotInTargetContext.getString());
        Whitebox.setInternalStateFromContext(tested, fieldsNotInTargetContext, MATCHING);
        Assert.assertEquals(tested.getSomeStringState(), fieldsNotInTargetContext.getString());
    }

    @Test(expected = FieldNotFoundException.class)
    public void setInternalStateFromInstanceContextThrowsExceptionWhenContextContainsFieldsNotDefinedInTargetObjectWhenSpecifyingStrictStrategy() throws Exception {
        ClassWithSimpleInternalState tested = new ClassWithSimpleInternalState();
        InstanceFieldsNotInTargetContext fieldsNotInTargetContext = new InstanceFieldsNotInTargetContext();
        assertThat(tested.getSomeStringState()).isNotEqualTo(fieldsNotInTargetContext.getString());
        Whitebox.setInternalStateFromContext(tested, fieldsNotInTargetContext, STRICT);
        Assert.assertEquals(tested.getSomeStringState(), fieldsNotInTargetContext.getString());
    }

    @Test
    public void setInternalStateFromClassContextCopiesMatchingContextFieldsToTargetObjectByDefault() throws Exception {
        long state = ClassWithSimpleInternalState.getLong();
        try {
            assertThat(state).isNotEqualTo(ClassFieldsNotInTargetContext.getLong());
            Whitebox.setInternalStateFromContext(ClassWithSimpleInternalState.class, ClassFieldsNotInTargetContext.class);
            Assert.assertEquals(ClassFieldsNotInTargetContext.getLong(), ClassWithSimpleInternalState.getLong());
        } finally {
            // Restore the state
            ClassWithSimpleInternalState.setLong(state);
        }
    }

    @Test
    public void setInternalStateFromClassContextCopiesMatchingContextFieldsToTargetObjectWhenSpecifyingMatchingStrategy() throws Exception {
        long state = ClassWithSimpleInternalState.getLong();
        try {
            assertThat(state).isNotEqualTo(ClassFieldsNotInTargetContext.getLong());
            Whitebox.setInternalStateFromContext(ClassWithSimpleInternalState.class, ClassFieldsNotInTargetContext.class, MATCHING);
            Assert.assertEquals(ClassFieldsNotInTargetContext.getLong(), ClassWithSimpleInternalState.getLong());
        } finally {
            // Restore the state
            ClassWithSimpleInternalState.setLong(state);
        }
    }

    @Test(expected = FieldNotFoundException.class)
    public void setInternalStateFromClassContextThrowsExceptionWhenContextContainsFieldsNotDefinedInTargetObjectWhenSpecifyingStrictStrategy() throws Exception {
        long state = ClassWithSimpleInternalState.getLong();
        try {
            assertThat(state).isNotEqualTo(ClassFieldsNotInTargetContext.getLong());
            Whitebox.setInternalStateFromContext(ClassWithSimpleInternalState.class, ClassFieldsNotInTargetContext.class, STRICT);
        } finally {
            // Restore the state
            ClassWithSimpleInternalState.setLong(state);
        }
    }

    @Test
    public void assertThatErrorMessageIsCorrectWhenNoInstanceFieldFound() throws Exception {
        ClassWithInternalState classWithInternalState = new ClassWithInternalState();
        try {
            Whitebox.setInternalState(classWithInternalState, ((byte) (23)));
            Assert.fail("Should throw a FieldNotFoundException.");
        } catch (FieldNotFoundException e) {
            Assert.assertEquals((("No instance field assignable from \"java.lang.Byte\" could be found in the class hierarchy of " + (ClassWithInternalState.class.getName())) + "."), e.getMessage());
        }
    }

    @Test
    public void assertThatErrorMessageIsCorrectWhenNoStaticFieldFound() throws Exception {
        try {
            Whitebox.setInternalState(ClassWithInternalState.class, ((byte) (23)));
            Assert.fail("Should throw a FieldNotFoundException.");
        } catch (FieldNotFoundException e) {
            Assert.assertEquals((("No static field assignable from \"java.lang.Byte\" could be found in the class hierarchy of " + (ClassWithInternalState.class.getName())) + "."), e.getMessage());
        }
    }

    @Test
    public void assertThatWhiteboxWorksWithGenericsWhenSpecifyingFieldName() throws Exception {
        ClassWithInternalState object = new ClassWithInternalState();
        Set<String> state = Whitebox.getInternalState(object, "genericState");
        Assert.assertSame(object.getGenericState(), state);
    }

    @Test
    public void whiteboxGetMethodWithCorrectMethodNameButWrongParameterTypeReturnsErrorMessageReflectingTheWrongParameter() throws Exception {
        try {
            Whitebox.getMethod(ClassWithInternalState.class, "methodWithArgument", String.class, InputStream.class);
            Assert.fail("Should throw MethodNotFoundException");
        } catch (MethodNotFoundException e) {
            Assert.assertEquals("No method found with name 'methodWithArgument' with parameter types: [ java.lang.String, java.io.InputStream ] in class org.powermock.reflect.testclasses.ClassWithInternalState.", e.getMessage());
        }
    }

    @Test
    public void whiteboxSetInternalStateWorksOnArraysWhenDefiningMethodName() {
        ClassWithInternalState tested = new ClassWithInternalState();
        final String[] expected = new String[]{ "string1", "string2" };
        Whitebox.setInternalState(tested, "stringArray", expected);
        Assert.assertArrayEquals(expected, tested.getStringArray());
    }

    @Test
    public void whiteboxSetInternalStateWorksOnArraysWhenNotDefiningMethodName() {
        ClassWithInternalState tested = new ClassWithInternalState();
        final String[] expected = new String[]{ "string1", "string2" };
        Whitebox.setInternalState(tested, expected);
        Assert.assertArrayEquals(expected, tested.getStringArray());
    }

    @Test
    public void getInternalStateThrowsIAEWhenInstanceIsNull() {
        try {
            Whitebox.getInternalState(null, String.class);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The object containing the field cannot be null", e.getMessage());
        }
    }

    @Test
    public void getInternalStateSupportsObjectArrayWhenSUTContainsSerializable() {
        ClassWithSerializableState tested = new ClassWithSerializableState();
        tested.setSerializable(new Serializable() {
            private static final long serialVersionUID = -1850246005852779087L;
        });
        tested.setObjectArray(new Object[0]);
        Assert.assertNotNull(Whitebox.getInternalState(tested, Object[].class));
    }

    @Test
    public void getInternalStateUsesAssignableToWhenLookingForObject() {
        ClassWithList tested = new ClassWithList();
        Assert.assertNotNull(Whitebox.getInternalState(tested, Object.class));
    }

    @Test(expected = TooManyFieldsFoundException.class)
    public void getInternalStateThrowsTooManyFieldsFoundWhenTooManyFieldsMatchTheSuppliedType() {
        ClassWithInternalState tested = new ClassWithInternalState();
        Assert.assertNotNull(Whitebox.getInternalState(tested, Object.class));
    }

    @Test
    public void invokeMethodInvokesOverridenMethods() throws Exception {
        Assert.assertTrue(Whitebox.<Boolean>invokeMethod(new ClassWithOverriddenMethod(), 2.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newInstanceThrowsIAEWhenClassIsAbstract() throws Exception {
        Whitebox.newInstance(AbstractClass.class);
    }

    @Test
    public void newInstanceReturnsJavaProxyWhenInterface() throws Exception {
        AnInterface instance = Whitebox.newInstance(AnInterface.class);
        Assert.assertTrue(Proxy.isProxyClass(instance.getClass()));
    }

    @Test
    public void newInstanceCreatesAnEmptyArrayWhenClassIsArray() throws Exception {
        byte[] newInstance = Whitebox.newInstance(byte[].class);
        Assert.assertEquals(0, newInstance.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invokeMethodSupportsNullParameters() throws Exception {
        ClassWithAMethod classWithAMethod = new ClassWithAMethod();
        Connection connection = null;
        Whitebox.invokeMethod(classWithAMethod, "connect", connection);
    }

    @Test(expected = MethodNotFoundException.class)
    public void invokeOverriddenMethodWithNullParameterThrowsIAE() throws Exception {
        ClassWithOverloadedMethods tested = new ClassWithOverloadedMethods();
        Child child = null;
        Whitebox.invokeMethod(tested, "overloaded", 2, child);
    }

    @Test
    public void canPassNullParamToPrivateStaticMethod() throws Exception {
        Assert.assertEquals("hello", Whitebox.invokeMethod(ClassWithStaticMethod.class, "aStaticMethod", ((Object[]) (null))));
    }

    @Test
    public void canPassNullParamToPrivateStaticMethodWhenDefiningParameterTypes() throws Exception {
        Assert.assertEquals("hello", Whitebox.invokeMethod(ClassWithStaticMethod.class, "aStaticMethod", new Class<?>[]{ byte[].class }, ((Object[]) (null))));
    }

    @Test
    public void canPassNullPrimitiveArraysToAPrivateStaticMethod() throws Exception {
        Assert.assertEquals("hello", Whitebox.invokeMethod(ClassWithStaticMethod.class, "aStaticMethod", ((byte[]) (null))));
    }

    @Test
    public void canPassMultipleNullValuesToStaticMethod() throws Exception {
        Assert.assertEquals("null null", Whitebox.invokeMethod(ClassWithStaticMethod.class, "anotherStaticMethod", ((Object) (null)), ((byte[]) (null))));
    }

    @Test
    public void testObjectConstructors() throws Exception {
        String name = "objectConstructor";
        ClassWithObjectConstructors instance = Whitebox.invokeConstructor(ClassWithObjectConstructors.class, name);
        Assert.assertEquals(instance.getName(), name);
    }

    @Test
    public void testInterfaceConstructors() throws Exception {
        ClassWithInterfaceConstructors.ConstructorInterface param = new ClassWithInterfaceConstructors.ConstructorInterfaceImpl("constructorInterfaceSomeValue");
        ClassWithInterfaceConstructors instance = Whitebox.invokeConstructor(ClassWithInterfaceConstructors.class, param);
        Assert.assertEquals(instance.getValue(), param.getValue());
    }

    @Test
    public void testPrimitiveConstructorsArgumentBoxed() throws Exception {
        Long arg = 1L;
        ClassWithPrimitiveConstructors instance = Whitebox.invokeConstructor(ClassWithPrimitiveConstructors.class, arg);
        Assert.assertEquals(instance.getValue(), arg.longValue());
    }

    @Test
    public void testOverloadedConstructors() throws Exception {
        String name = "overloadedConstructor";
        ClassWithOverloadedConstructors instance = Whitebox.invokeConstructor(ClassWithOverloadedConstructors.class, true, name);
        Assert.assertEquals(instance.getName(), name);
        Assert.assertTrue(instance.isBool());
        assertThat(instance.isBool1()).isFalse();
    }
}

