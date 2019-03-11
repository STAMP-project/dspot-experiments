package tests.api.java.lang.reflect;


import java.io.EOFException;
import java.lang.reflect.UndeclaredThrowableException;
import junit.framework.TestCase;


public class UndeclaredThrowableExceptionTests extends TestCase {
    private static EOFException throwable = new EOFException();

    private static String msg = "TEST_MSG";

    /**
     * java.lang.reflect.UndeclaredThrowableException#getCause()
     */
    public void test_getCause() throws Exception {
        UndeclaredThrowableException ute = new UndeclaredThrowableException(UndeclaredThrowableExceptionTests.throwable);
        TestCase.assertSame("Wrong cause returned", UndeclaredThrowableExceptionTests.throwable, ute.getCause());
    }

    /**
     * java.lang.reflect.UndeclaredThrowableException#getUndeclaredThrowable()
     */
    public void test_getUndeclaredThrowable() throws Exception {
        UndeclaredThrowableException ute = new UndeclaredThrowableException(UndeclaredThrowableExceptionTests.throwable);
        TestCase.assertSame("Wrong undeclared throwable returned", UndeclaredThrowableExceptionTests.throwable, ute.getUndeclaredThrowable());
    }

    /**
     * java.lang.reflect.UndeclaredThrowableException#UndeclaredThrowableException(java.lang.Throwable)
     */
    public void test_Constructor_Throwable() throws Exception {
        UndeclaredThrowableException e = new UndeclaredThrowableException(UndeclaredThrowableExceptionTests.throwable);
        TestCase.assertEquals("Wrong cause returned", UndeclaredThrowableExceptionTests.throwable, e.getCause());
        TestCase.assertEquals("Wrong throwable returned", UndeclaredThrowableExceptionTests.throwable, e.getUndeclaredThrowable());
    }

    /**
     * java.lang.reflect.UndeclaredThrowableException#UndeclaredThrowableException(java.lang.Throwable, java.lang.String)
     */
    public void test_Constructor_Throwable_String() throws Exception {
        UndeclaredThrowableException e = new UndeclaredThrowableException(UndeclaredThrowableExceptionTests.throwable, UndeclaredThrowableExceptionTests.msg);
        TestCase.assertEquals("Wrong cause returned", UndeclaredThrowableExceptionTests.throwable, e.getCause());
        TestCase.assertEquals("Wrong throwable returned", UndeclaredThrowableExceptionTests.throwable, e.getUndeclaredThrowable());
        TestCase.assertEquals("Wrong message returned", UndeclaredThrowableExceptionTests.msg, e.getMessage());
    }
}

