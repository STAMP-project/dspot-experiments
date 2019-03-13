package cn.hutool.core.exceptions;


import cn.hutool.core.io.IORuntimeException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * ????????
 *
 * @author looly
 */
public class ExceptionUtilTest {
    @Test
    public void wrapTest() {
        IORuntimeException e = ExceptionUtil.wrap(new IOException(), IORuntimeException.class);
        Assert.assertNotNull(e);
    }

    @Test
    public void getRootTest() {
        // ??????
        StackTraceElement ele = ExceptionUtil.getRootStackElement();
        Assert.assertEquals("main", ele.getMethodName());
    }

    @Test
    public void convertTest() {
        // RuntimeException e = new RuntimeException();
        IOException ioException = new IOException();
        IllegalArgumentException argumentException = new IllegalArgumentException(ioException);
        IOException ioException1 = ExceptionUtil.convertFromOrSuppressedThrowable(argumentException, IOException.class, true);
        Assert.assertNotNull(ioException1);
    }
}

