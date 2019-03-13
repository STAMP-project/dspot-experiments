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
package samples.junit4.prepareeverything;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareEverythingForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.exceptions.ConstructorNotFoundException;
import samples.Service;
import samples.expectnew.ExpectNewDemo;
import samples.expectnew.ExpectNewServiceUser;
import samples.expectnew.VarArgsConstructorDemo;
import samples.newmocking.MyClass;


/**
 * Test class to demonstrate new instance mocking using expectNew(..) with the
 * {@link PrepareEverythingForTest} annotation and replayAll and verifyAll.
 */
@RunWith(PowerMockRunner.class)
@PrepareEverythingForTest
public class ExpectNewDemoUsingThePrepareEverythingAnnotationTest {
    @Test
    public void testNewWithCheckedException() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        final String expectedFailMessage = "testing checked exception";
        expectNew(MyClass.class).andThrow(new IOException(expectedFailMessage));
        replayAll();
        try {
            tested.throwExceptionAndWrapInRunTimeWhenInvoction();
            Assert.fail("Should throw a checked Exception!");
        } catch (RuntimeException e) {
            Assert.assertTrue(((e.getCause()) instanceof IOException));
            Assert.assertEquals(expectedFailMessage, e.getMessage());
        }
        verifyAll();
    }

    @PrepareEverythingForTest
    @Test
    public void testGetMessage() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock);
        String expected = "Hello altered World";
        expect(myClassMock.getMessage()).andReturn("Hello altered World");
        replayAll();
        String actual = tested.getMessage();
        verifyAll();
        Assert.assertEquals("Expected and actual did not match", expected, actual);
    }

    @Test
    public void testGetMessageWithArgument() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock);
        String expected = "Hello altered World";
        expect(myClassMock.getMessage("test")).andReturn("Hello altered World");
        replayAll();
        String actual = tested.getMessageWithArgument();
        verifyAll();
        Assert.assertEquals("Expected and actual did not match", expected, actual);
    }

    @Test
    public void testInvokeVoidMethod() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock);
        myClassMock.voidMethod();
        expectLastCall().times(1);
        replayAll();
        tested.invokeVoidMethod();
        verifyAll();
    }

    @Test
    public void testNewWithRuntimeException() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        final String expectedFailMessage = "testing";
        expectNew(MyClass.class).andThrow(new RuntimeException(expectedFailMessage));
        replayAll();
        try {
            tested.throwExceptionWhenInvoction();
            Assert.fail("Should throw RuntimeException!");
        } catch (RuntimeException e) {
            Assert.assertEquals(expectedFailMessage, e.getMessage());
        }
        verifyAll();
    }

    @Test
    public void testPreviousProblemsWithByteCodeManipulation() throws Exception {
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expect(myClassMock1.getMessage()).andReturn("Hello");
        expect(myClassMock1.getMessage()).andReturn("World");
        replayAll();
        Assert.assertEquals("Hello", myClassMock1.getMessage());
        Assert.assertEquals("World", myClassMock1.getMessage());
        verifyAll();
    }

    @Test
    public void testMultipleNew() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        MyClass myClassMock2 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1);
        expectNew(MyClass.class).andReturn(myClassMock2);
        expect(myClassMock1.getMessage()).andReturn("Hello ");
        expect(myClassMock2.getMessage()).andReturn("World");
        replayAll();
        final String actual = tested.multipleNew();
        verifyAll();
        Assert.assertEquals("Hello World", actual);
    }

    @Test
    public void testSimpleMultipleNew() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1).times(3);
        replayAll();
        tested.simpleMultipleNew();
        verifyAll();
    }

    @Test
    public void testSimpleMultipleNew_tooManyTimesExpected() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1).times(4);
        replayAll();
        tested.simpleMultipleNew();
        try {
            verifyAll();
            Assert.fail("Should throw AssertionError.");
        } catch (AssertionError e) {
            Assert.assertEquals(("\n  Expectation failure on verify:" + "\n    samples.newmocking.MyClass(): expected: 4, actual: 3"), e.getMessage());
        }
    }

    @Test
    public void testSimpleMultipleNew_tooFewTimesExpected() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1).times(2);
        replayAll();
        try {
            tested.simpleMultipleNew();
            Assert.fail("Should throw AssertionError.");
        } catch (AssertionError e) {
            Assert.assertEquals(("\n  Unexpected constructor call samples.newmocking.MyClass():" + "\n    samples.newmocking.MyClass(): expected: 2, actual: 3"), e.getMessage());
        }
    }

    /**
     * Verifies that the issue
     * http://code.google.com/p/powermock/issues/detail?id=10 is solved.
     */
    @Test
    public void testSimpleMultipleNewPrivate_tooFewTimesExpected() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1).times(2);
        replayAll();
        try {
            Whitebox.invokeMethod(tested, "simpleMultipleNewPrivate");
            Assert.fail("Should throw AssertionError.");
        } catch (AssertionError e) {
            Assert.assertEquals(("\n  Unexpected constructor call samples.newmocking.MyClass():" + "\n    samples.newmocking.MyClass(): expected: 2, actual: 3"), e.getMessage());
        }
    }

    @Test
    public void testSimpleMultipleNewPrivate_ok() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1).times(3);
        replayAll();
        Whitebox.invokeMethod(tested, "simpleMultipleNewPrivate");
    }

    @Test
    public void testSimpleSingleNew_withOnce() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1).once();
        replayAll();
        tested.simpleSingleNew();
        verifyAll();
    }

    @Test
    public void testSimpleSingleNew_withAtLeastOnce() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1).atLeastOnce();
        replayAll();
        tested.simpleSingleNew();
        verifyAll();
    }

    @Test
    public void testSimpleMultipleNew_withAtLeastOnce() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1).atLeastOnce();
        replayAll();
        tested.simpleMultipleNew();
        verifyAll();
    }

    @Test
    public void testSimpleMultipleNew_withRange_lowerBoundLessThan0() throws Exception {
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        try {
            expectNew(MyClass.class).andReturn(myClassMock1).times((-20), 2);
            Assert.fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("minimum must be >= 0", e.getMessage());
        }
    }

    @Test
    public void testSimpleMultipleNew_withRange_upperBoundLessThan0() throws Exception {
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        try {
            expectNew(MyClass.class).andReturn(myClassMock1).times((-1), (-2));
            Assert.fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("<="));
        }
    }

    @Test
    public void testSimpleMultipleNew_withRange_upperBoundLessThanLowerBound() throws Exception {
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        try {
            expectNew(MyClass.class).andReturn(myClassMock1).times(10, 2);
            Assert.fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("<="));
        }
    }

    @Test
    public void testSimpleMultipleNew_withRange_OK() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1).times(1, 5);
        replayAll();
        tested.simpleMultipleNew();
        verifyAll();
    }

    @Test
    public void testSimpleMultipleNew_anyTimes() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1).anyTimes();
        replayAll();
        tested.simpleMultipleNew();
        verifyAll();
    }

    @Test
    public void testSimpleMultipleNew_withRange_notWithinRange() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1).times(5, 7);
        replayAll();
        tested.simpleMultipleNew();
        try {
            verifyAll();
            Assert.fail("Should throw AssertionError.");
        } catch (AssertionError e) {
            Assert.assertEquals(("\n  Expectation failure on verify:" + "\n    samples.newmocking.MyClass(): expected: between 5 and 7, actual: 3"), e.getMessage());
        }
    }

    @Test
    public void testAlternativeFlow() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        expectNew(DataInputStream.class, new Object[]{ null }).andThrow(new RuntimeException("error"));
        replayAll();
        InputStream stream = tested.alternativePath();
        verifyAll();
        Assert.assertNotNull("The returned inputstream should not be null.", stream);
        Assert.assertTrue("The returned inputstream should be an instance of ByteArrayInputStream.", (stream instanceof ByteArrayInputStream));
    }

    @Test
    public void testSimpleMultipleNewPrivate_tooManyTimesExpected() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1).times(4);
        replayAll();
        try {
            Whitebox.invokeMethod(tested, "simpleMultipleNewPrivate");
            verifyAll();
            Assert.fail("Should throw an exception!.");
        } catch (AssertionError e) {
            Assert.assertEquals(("\n  Expectation failure on verify:" + "\n    samples.newmocking.MyClass(): expected: 4, actual: 3"), e.getMessage());
        }
    }

    @Test
    public void testNewWithArguments() throws Exception {
        final int numberOfTimes = 2;
        final String expected = "used";
        ExpectNewDemo tested = new ExpectNewDemo();
        ExpectNewServiceUser expectNewServiceImplMock = PowerMock.createMock(ExpectNewServiceUser.class);
        Service serviceMock = PowerMock.createMock(Service.class);
        expectNew(ExpectNewServiceUser.class, serviceMock, numberOfTimes).andReturn(expectNewServiceImplMock);
        expect(expectNewServiceImplMock.useService()).andReturn(expected);
        replayAll();
        Assert.assertEquals(expected, tested.newWithArguments(serviceMock, numberOfTimes));
        verifyAll();
    }

    @Test
    public void testNewWithVarArgs() throws Exception {
        final String firstString = "hello";
        final String secondString = "world";
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        expectNew(VarArgsConstructorDemo.class, firstString, secondString).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getAllMessages()).andReturn(new String[]{ firstString, secondString });
        replayAll();
        String[] varArgs = tested.newVarArgs(firstString, secondString);
        Assert.assertEquals(2, varArgs.length);
        Assert.assertEquals(firstString, varArgs[0]);
        Assert.assertEquals(secondString, varArgs[1]);
        verifyAll();
    }

    @Test
    public void testNewWhenTheExpectedConstructorIsNotFound() throws Exception {
        final Object object = new Object();
        try {
            expectNew(VarArgsConstructorDemo.class, object);
            Assert.fail("Should throw ConstructorNotFoundException!");
        } catch (ConstructorNotFoundException e) {
            Assert.assertEquals((((("No constructor found in class '" + (VarArgsConstructorDemo.class.getName())) + "' with parameter types: [ ") + (object.getClass().getName())) + " ]."), e.getMessage());
        }
    }

    @Test
    public void testNewWithVarArgsConstructorWhenOneArgumentIsOfASubType() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        Service serviceMock = PowerMock.createMock(Service.class);
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        final Service serviceSubTypeInstance = new Service() {
            @Override
            public String getServiceMessage() {
                return "message";
            }
        };
        expectNew(VarArgsConstructorDemo.class, serviceSubTypeInstance, serviceMock).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getAllServices()).andReturn(new Service[]{ serviceMock });
        replayAll();
        Service[] varArgs = tested.newVarArgs(serviceSubTypeInstance, serviceMock);
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(serviceMock, varArgs[0]);
        verifyAll();
    }

    @Test
    public void testNewWithArrayVarArgs() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        final byte[] byteArrayOne = new byte[]{ 42 };
        final byte[] byteArrayTwo = new byte[]{ 17 };
        expectNew(VarArgsConstructorDemo.class, byteArrayOne, byteArrayTwo).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getByteArrays()).andReturn(new byte[][]{ byteArrayOne });
        replayAll();
        byte[][] varArgs = tested.newVarArgs(byteArrayOne, byteArrayTwo);
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(byteArrayOne, varArgs[0]);
        verifyAll();
    }

    @Test
    public void testNewWithArrayVarArgsAndMatchers() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        final byte[] byteArrayOne = new byte[]{ 42 };
        final byte[] byteArrayTwo = new byte[]{ 17 };
        expectNew(VarArgsConstructorDemo.class, aryEq(byteArrayOne), aryEq(byteArrayTwo)).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getByteArrays()).andReturn(new byte[][]{ byteArrayOne });
        replayAll();
        byte[][] varArgs = tested.newVarArgsWithMatchers();
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(byteArrayOne, varArgs[0]);
        verifyAll();
    }

    @Test
    public void testNewWithArrayVarArgsWhenFirstArgumentIsNullAndSubseqentArgumentsAreNotNull() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        final byte[] byteArrayOne = null;
        final byte[] byteArrayTwo = new byte[]{ 17 };
        expectNew(VarArgsConstructorDemo.class, byteArrayOne, byteArrayTwo).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getByteArrays()).andReturn(new byte[][]{ byteArrayTwo });
        replayAll();
        byte[][] varArgs = tested.newVarArgs(byteArrayOne, byteArrayTwo);
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(byteArrayTwo, varArgs[0]);
        verifyAll();
    }

    @Test
    public void testNewWithArrayVarArgsWhenFirstArgumentIsNotNullButSubseqentArgumentsAreNull() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        final byte[] byteArrayOne = new byte[]{ 42 };
        final byte[] byteArrayTwo = null;
        expectNew(VarArgsConstructorDemo.class, byteArrayOne, byteArrayTwo).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getByteArrays()).andReturn(new byte[][]{ byteArrayOne });
        replayAll();
        byte[][] varArgs = tested.newVarArgs(byteArrayOne, byteArrayTwo);
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(byteArrayOne, varArgs[0]);
        verifyAll();
    }

    @Test
    public void testNewWithArrayVarArgsWhenFirstArgumentIsNullSecondArgumentIsNotNullAndThirdArgumentIsNull() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        final byte[] byteArrayOne = null;
        final byte[] byteArrayTwo = new byte[]{ 42 };
        final byte[] byteArrayThree = null;
        expectNew(VarArgsConstructorDemo.class, byteArrayOne, byteArrayTwo, byteArrayThree).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getByteArrays()).andReturn(new byte[][]{ byteArrayTwo });
        replayAll();
        byte[][] varArgs = tested.newVarArgs(byteArrayOne, byteArrayTwo, byteArrayThree);
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(byteArrayTwo, varArgs[0]);
        verifyAll();
    }

    @Test
    public void testNewWithArrayVarArgsWhenAllArgumentsAreNull() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        final byte[] byteArrayOne = null;
        final byte[] byteArrayTwo = null;
        expectNew(VarArgsConstructorDemo.class, byteArrayOne, byteArrayTwo).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getByteArrays()).andReturn(new byte[][]{ byteArrayTwo });
        replayAll();
        byte[][] varArgs = tested.newVarArgs(byteArrayOne, byteArrayTwo);
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(byteArrayTwo, varArgs[0]);
        verifyAll();
    }

    @Test
    public void testNewWithWrongArgument() throws Exception {
        final int numberOfTimes = 2;
        final String expected = "used";
        ExpectNewDemo tested = new ExpectNewDemo();
        ExpectNewServiceUser expectNewServiceImplMock = PowerMock.createMock(ExpectNewServiceUser.class);
        Service serviceMock = PowerMock.createMock(Service.class);
        expectNew(ExpectNewServiceUser.class, serviceMock, numberOfTimes).andReturn(expectNewServiceImplMock);
        expect(expectNewServiceImplMock.useService()).andReturn(expected);
        replayAll();
        try {
            Assert.assertEquals(expected, tested.newWithWrongArguments(serviceMock, numberOfTimes));
            verifyAll();
            Assert.fail("Should throw AssertionError!");
        } catch (AssertionError e) {
            Assert.assertEquals(("\n  Unexpected constructor call samples.expectnew.ExpectNewServiceUser(EasyMock for interface samples.Service, 4 (int)):" + "\n    samples.expectnew.ExpectNewServiceUser(EasyMock for interface samples.Service, 2 (int)): expected: 1, actual: 0"), e.getMessage());
        }
    }

    @Test
    public void testExpectNewButNoNewCallWasMade() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock1).once();
        replayAll();
        try {
            tested.makeDate();
            verifyAll();
            Assert.fail("Should throw AssertionError!");
        } catch (AssertionError e) {
            Assert.assertTrue(e.getMessage().contains(((MyClass.class.getName()) + "(): expected: 1, actual: 0")));
        }
    }
}

