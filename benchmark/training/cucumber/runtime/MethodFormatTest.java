package cucumber.runtime;


import MethodFormat.FULL;
import MethodFormat.SHORT;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


public class MethodFormatTest {
    private Method methodWithArgsAndException;

    private Method methodWithoutArgs;

    @Test
    public void shouldUseSimpleFormatWhenMethodHasException() {
        Assert.assertEquals("MethodFormatTest.methodWithArgsAndException(String,Map)", SHORT.format(methodWithArgsAndException));
    }

    @Test
    public void shouldUseSimpleFormatWhenMethodHasNoException() {
        Assert.assertEquals("MethodFormatTest.methodWithoutArgs()", SHORT.format(methodWithoutArgs));
    }

    @Test
    public void prints_code_source() {
        String format = FULL.format(methodWithoutArgs);
        Assert.assertTrue(format.startsWith("cucumber.runtime.MethodFormatTest.methodWithoutArgs() in file:"));
    }
}

