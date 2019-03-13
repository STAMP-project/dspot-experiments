package samples.junit4.expectnew;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.exceptions.ConstructorNotFoundException;
import samples.Service;
import samples.expectnew.CreationException;
import samples.expectnew.ExpectNewDemo;
import samples.expectnew.ExpectNewServiceUser;
import samples.expectnew.ITarget;
import samples.expectnew.Target;
import samples.expectnew.VarArgsConstructorDemo;
import samples.newmocking.MyClass;


public abstract class ExpectNewCases {
    public static final String TARGET_NAME = "MyTarget";

    public static final int TARGET_ID = 1;

    public static final String UNKNOWN_TARGET_NAME = "Unknown2";

    public static final int UNKNOWN_TARGET_ID = -11;

    @Test
    public void testNewWithCheckedException() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        final String expectedFailMessage = "testing checked exception";
        PowerMock.expectNew(MyClass.class).andThrow(new IOException(expectedFailMessage));
        PowerMock.replay(MyClass.class);
        try {
            tested.throwExceptionAndWrapInRunTimeWhenInvoction();
            Assert.fail("Should throw a checked Exception!");
        } catch (RuntimeException e) {
            Assert.assertTrue(((e.getCause()) instanceof IOException));
            Assert.assertEquals(expectedFailMessage, e.getMessage());
        }
        PowerMock.verify(MyClass.class);
    }

    @Test
    public void testGetMessage() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock);
        String expected = "Hello altered World";
        expect(myClassMock.getMessage()).andReturn("Hello altered World");
        PowerMock.replay(myClassMock, MyClass.class);
        String actual = tested.getMessage();
        PowerMock.verify(myClassMock, MyClass.class);
        Assert.assertEquals("Expected and actual did not match", expected, actual);
    }

    @Test
    public void testGetMessageWithArgument() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock);
        String expected = "Hello altered World";
        expect(myClassMock.getMessage("test")).andReturn("Hello altered World");
        PowerMock.replay(myClassMock, MyClass.class);
        String actual = tested.getMessageWithArgument();
        PowerMock.verify(myClassMock, MyClass.class);
        Assert.assertEquals("Expected and actual did not match", expected, actual);
    }

    @Test
    public void testInvokeVoidMethod() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock);
        myClassMock.voidMethod();
        expectLastCall().times(1);
        PowerMock.replay(myClassMock, MyClass.class);
        tested.invokeVoidMethod();
        PowerMock.verify(myClassMock, MyClass.class);
    }

    @Test
    public void testNewWithRuntimeException() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        final String expectedFailMessage = "testing";
        PowerMock.expectNew(MyClass.class).andThrow(new RuntimeException(expectedFailMessage));
        PowerMock.replay(MyClass.class);
        try {
            tested.throwExceptionWhenInvoction();
            Assert.fail("Should throw RuntimeException!");
        } catch (RuntimeException e) {
            Assert.assertEquals(expectedFailMessage, e.getMessage());
        }
        PowerMock.verify(MyClass.class);
    }

    @Test
    public void testPreviousProblemsWithByteCodeManipulation() throws Exception {
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        expect(myClassMock1.getMessage()).andReturn("Hello");
        expect(myClassMock1.getMessage()).andReturn("World");
        PowerMock.replay(myClassMock1);
        Assert.assertEquals("Hello", myClassMock1.getMessage());
        Assert.assertEquals("World", myClassMock1.getMessage());
        PowerMock.verify(myClassMock1);
    }

    @Test
    public void testMultipleNew() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        MyClass myClassMock2 = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock2);
        expect(myClassMock1.getMessage()).andReturn("Hello ");
        expect(myClassMock2.getMessage()).andReturn("World");
        PowerMock.replay(myClassMock1, myClassMock2, MyClass.class);
        final String actual = tested.multipleNew();
        PowerMock.verify(myClassMock1, myClassMock2, MyClass.class);
        Assert.assertEquals("Hello World", actual);
    }

    @Test
    public void testSimpleMultipleNew() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).times(3);
        PowerMock.replay(myClassMock1, MyClass.class);
        tested.simpleMultipleNew();
        PowerMock.verify(myClassMock1, MyClass.class);
    }

    @Test
    public void testSimpleMultipleNew_tooManyTimesExpected() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).times(4);
        PowerMock.replay(myClassMock1, MyClass.class);
        tested.simpleMultipleNew();
        try {
            PowerMock.verify(myClassMock1, MyClass.class);
            Assert.fail("Should throw AssertionError.");
        } catch (AssertionError e) {
            Assert.assertEquals(("\n  Expectation failure on verify:" + "\n    samples.newmocking.MyClass(): expected: 4, actual: 3"), e.getMessage());
        }
    }

    @Test
    public void testSimpleMultipleNew_tooFewTimesExpected() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).times(2);
        PowerMock.replay(myClassMock1, MyClass.class);
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
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).times(2);
        PowerMock.replay(myClassMock1, MyClass.class);
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
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).times(3);
        PowerMock.replay(myClassMock1, MyClass.class);
        Whitebox.invokeMethod(tested, "simpleMultipleNewPrivate");
    }

    @Test
    public void testSimpleSingleNew_withOnce() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).once();
        PowerMock.replay(myClassMock1, MyClass.class);
        tested.simpleSingleNew();
        PowerMock.verify(myClassMock1, MyClass.class);
    }

    @Test
    public void testSimpleSingleNew_withAtLeastOnce() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).atLeastOnce();
        PowerMock.replay(myClassMock1, MyClass.class);
        tested.simpleSingleNew();
        PowerMock.verify(myClassMock1, MyClass.class);
    }

    @Test
    public void testSimpleMultipleNew_withAtLeastOnce() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).atLeastOnce();
        PowerMock.replay(myClassMock1, MyClass.class);
        tested.simpleMultipleNew();
        PowerMock.verify(myClassMock1, MyClass.class);
    }

    @Test
    public void testSimpleMultipleNew_withRange_lowerBoundLessThan0() throws Exception {
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        try {
            PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).times((-20), 2);
            Assert.fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("minimum must be >= 0", e.getMessage());
        }
    }

    @Test
    public void testSimpleMultipleNew_withRange_upperBoundLessThan0() throws Exception {
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        try {
            PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).times((-1), (-2));
            Assert.fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("<="));
        }
    }

    @Test
    public void testSimpleMultipleNew_withRange_upperBoundLessThanLowerBound() throws Exception {
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        try {
            PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).times(10, 2);
            Assert.fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("<="));
        }
    }

    @Test
    public void testSimpleMultipleNew_withRange_OK() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).times(1, 5);
        PowerMock.replay(myClassMock1, MyClass.class);
        tested.simpleMultipleNew();
        PowerMock.verify(myClassMock1, MyClass.class);
    }

    @Test
    public void testSimpleMultipleNew_anyTimes() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).anyTimes();
        PowerMock.replay(myClassMock1, MyClass.class);
        tested.simpleMultipleNew();
        PowerMock.verify(myClassMock1, MyClass.class);
    }

    @Test
    public void testSimpleMultipleNew_withRange_notWithinRange() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).times(5, 7);
        PowerMock.replay(myClassMock1, MyClass.class);
        tested.simpleMultipleNew();
        try {
            PowerMock.verify(myClassMock1, MyClass.class);
            Assert.fail("Should throw AssertionError.");
        } catch (AssertionError e) {
            Assert.assertEquals(("\n  Expectation failure on verify:" + "\n    samples.newmocking.MyClass(): expected: between 5 and 7, actual: 3"), e.getMessage());
        }
    }

    @Test
    public void testAlternativeFlow() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        PowerMock.expectNew(DataInputStream.class, new Object[]{ null }).andThrow(new RuntimeException("error"));
        PowerMock.replay(ExpectNewDemo.class, DataInputStream.class);
        InputStream stream = tested.alternativePath();
        PowerMock.verify(ExpectNewDemo.class, DataInputStream.class);
        Assert.assertNotNull("The returned inputstream should not be null.", stream);
        Assert.assertTrue("The returned inputstream should be an instance of ByteArrayInputStream.", (stream instanceof ByteArrayInputStream));
    }

    @Test
    public void testSimpleMultipleNewPrivate_tooManyTimesExpected() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).times(4);
        PowerMock.replay(myClassMock1, MyClass.class);
        try {
            Whitebox.invokeMethod(tested, "simpleMultipleNewPrivate");
            PowerMock.verify(myClassMock1, MyClass.class);
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
        PowerMock.expectNew(ExpectNewServiceUser.class, serviceMock, numberOfTimes).andReturn(expectNewServiceImplMock);
        expect(expectNewServiceImplMock.useService()).andReturn(expected);
        PowerMock.replay(expectNewServiceImplMock, serviceMock, ExpectNewServiceUser.class);
        Assert.assertEquals(expected, tested.newWithArguments(serviceMock, numberOfTimes));
        PowerMock.verify(expectNewServiceImplMock, serviceMock, ExpectNewServiceUser.class);
    }

    @Test
    public void testNewWithVarArgs() throws Exception {
        final String firstString = "hello";
        final String secondString = "world";
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        PowerMock.expectNew(VarArgsConstructorDemo.class, firstString, secondString).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getAllMessages()).andReturn(new String[]{ firstString, secondString });
        PowerMock.replay(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
        String[] varArgs = tested.newVarArgs(firstString, secondString);
        Assert.assertEquals(2, varArgs.length);
        Assert.assertEquals(firstString, varArgs[0]);
        Assert.assertEquals(secondString, varArgs[1]);
        PowerMock.verify(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
    }

    @Test
    public void testNewWhenTheExpectedConstructorIsNotFound() throws Exception {
        final Object object = new Object();
        try {
            PowerMock.expectNew(VarArgsConstructorDemo.class, object);
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
            public String getServiceMessage() {
                return "message";
            }
        };
        PowerMock.expectNew(VarArgsConstructorDemo.class, serviceSubTypeInstance, serviceMock).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getAllServices()).andReturn(new Service[]{ serviceMock });
        PowerMock.replay(serviceMock, VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
        Service[] varArgs = tested.newVarArgs(serviceSubTypeInstance, serviceMock);
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(serviceMock, varArgs[0]);
        PowerMock.verify(serviceMock, VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
    }

    @Test
    public void testNewWithArrayVarArgs() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        final byte[] byteArrayOne = new byte[]{ 42 };
        final byte[] byteArrayTwo = new byte[]{ 17 };
        PowerMock.expectNew(VarArgsConstructorDemo.class, byteArrayOne, byteArrayTwo).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getByteArrays()).andReturn(new byte[][]{ byteArrayOne });
        PowerMock.replay(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
        byte[][] varArgs = tested.newVarArgs(byteArrayOne, byteArrayTwo);
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(byteArrayOne, varArgs[0]);
        PowerMock.verify(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
    }

    @Test
    public void testNewWithArrayVarArgsAndMatchers() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        final byte[] byteArrayOne = new byte[]{ 42 };
        final byte[] byteArrayTwo = new byte[]{ 17 };
        PowerMock.expectNew(VarArgsConstructorDemo.class, aryEq(byteArrayOne), aryEq(byteArrayTwo)).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getByteArrays()).andReturn(new byte[][]{ byteArrayOne });
        PowerMock.replay(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
        byte[][] varArgs = tested.newVarArgsWithMatchers();
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(byteArrayOne, varArgs[0]);
        PowerMock.verify(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
    }

    @Test
    public void testNewWithArrayVarArgsWhenFirstArgumentIsNullAndSubseqentArgumentsAreNotNull() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        final byte[] byteArrayOne = null;
        final byte[] byteArrayTwo = new byte[]{ 17 };
        PowerMock.expectNew(VarArgsConstructorDemo.class, byteArrayOne, byteArrayTwo).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getByteArrays()).andReturn(new byte[][]{ byteArrayTwo });
        PowerMock.replay(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
        byte[][] varArgs = tested.newVarArgs(byteArrayOne, byteArrayTwo);
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(byteArrayTwo, varArgs[0]);
        PowerMock.verify(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
    }

    @Test
    public void testNewWithArrayVarArgsWhenFirstArgumentIsNotNullButSubseqentArgumentsAreNull() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        final byte[] byteArrayOne = new byte[]{ 42 };
        final byte[] byteArrayTwo = null;
        PowerMock.expectNew(VarArgsConstructorDemo.class, byteArrayOne, byteArrayTwo).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getByteArrays()).andReturn(new byte[][]{ byteArrayOne });
        PowerMock.replay(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
        byte[][] varArgs = tested.newVarArgs(byteArrayOne, byteArrayTwo);
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(byteArrayOne, varArgs[0]);
        PowerMock.verify(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
    }

    @Test
    public void testNewWithArrayVarArgsWhenFirstArgumentIsNullSecondArgumentIsNotNullAndThirdArgumentIsNull() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        final byte[] byteArrayOne = null;
        final byte[] byteArrayTwo = new byte[]{ 42 };
        final byte[] byteArrayThree = null;
        PowerMock.expectNew(VarArgsConstructorDemo.class, byteArrayOne, byteArrayTwo, byteArrayThree).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getByteArrays()).andReturn(new byte[][]{ byteArrayTwo });
        PowerMock.replay(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
        byte[][] varArgs = tested.newVarArgs(byteArrayOne, byteArrayTwo, byteArrayThree);
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(byteArrayTwo, varArgs[0]);
        PowerMock.verify(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
    }

    @Test
    public void testNewWithArrayVarArgsWhenAllArgumentsAreNull() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        VarArgsConstructorDemo varArgsConstructorDemoMock = PowerMock.createMock(VarArgsConstructorDemo.class);
        final byte[] byteArrayOne = null;
        final byte[] byteArrayTwo = null;
        PowerMock.expectNew(VarArgsConstructorDemo.class, byteArrayOne, byteArrayTwo).andReturn(varArgsConstructorDemoMock);
        expect(varArgsConstructorDemoMock.getByteArrays()).andReturn(new byte[][]{ byteArrayTwo });
        PowerMock.replay(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
        byte[][] varArgs = tested.newVarArgs(byteArrayOne, byteArrayTwo);
        Assert.assertEquals(1, varArgs.length);
        Assert.assertSame(byteArrayTwo, varArgs[0]);
        PowerMock.verify(VarArgsConstructorDemo.class, varArgsConstructorDemoMock);
    }

    @Test
    public void testNewWithWrongArgument() throws Exception {
        final int numberOfTimes = 2;
        final String expected = "used";
        ExpectNewDemo tested = new ExpectNewDemo();
        ExpectNewServiceUser expectNewServiceImplMock = PowerMock.createMock(ExpectNewServiceUser.class);
        Service serviceMock = PowerMock.createMock(Service.class);
        PowerMock.expectNew(ExpectNewServiceUser.class, serviceMock, numberOfTimes).andReturn(expectNewServiceImplMock);
        expect(expectNewServiceImplMock.useService()).andReturn(expected);
        PowerMock.replay(expectNewServiceImplMock, serviceMock, ExpectNewServiceUser.class);
        try {
            Assert.assertEquals(expected, tested.newWithWrongArguments(serviceMock, numberOfTimes));
            PowerMock.verify(expectNewServiceImplMock, serviceMock, ExpectNewServiceUser.class);
            Assert.fail("Should throw AssertionError!");
        } catch (AssertionError e) {
            Assert.assertEquals(("\n  Unexpected constructor call samples.expectnew.ExpectNewServiceUser(EasyMock for interface samples.Service, 4 (int)):" + "\n    samples.expectnew.ExpectNewServiceUser(EasyMock for interface samples.Service, 2 (int)): expected: 1, actual: 0"), e.getMessage());
        }
    }

    @Test
    public void testExpectNewButNoNewCallWasMade() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock1 = PowerMock.createMock(MyClass.class);
        PowerMock.expectNew(MyClass.class).andReturn(myClassMock1).once();
        PowerMock.replay(myClassMock1, MyClass.class);
        try {
            tested.makeDate();
            PowerMock.verify(myClassMock1, MyClass.class);
            Assert.fail("Should throw AssertionError!");
        } catch (AssertionError e) {
            Assert.assertTrue(e.getMessage().contains(((MyClass.class.getName()) + "(): expected: 1, actual: 0")));
        }
    }

    @Test
    public void canDefineSeveralMockResultForNew() throws Exception {
        final Target expectedTarget = new Target(ExpectNewCases.UNKNOWN_TARGET_NAME, ExpectNewCases.UNKNOWN_TARGET_ID);
        PowerMock.expectNew(Target.class, ExpectNewCases.TARGET_NAME, ExpectNewCases.TARGET_ID).andThrow(new CreationException());
        PowerMock.expectNew(Target.class, "Unknown", (-1)).andReturn(expectedTarget);
        PowerMock.replay(Target.class);
        Target actualTarget = new ExpectNewDemo().createTarget(new ITarget() {
            @Override
            public int getId() {
                return ExpectNewCases.TARGET_ID;
            }

            @Override
            public String getName() {
                return ExpectNewCases.TARGET_NAME;
            }
        });
        assertThat(actualTarget).isEqualToComparingFieldByField(expectedTarget);
    }
}

