package spark;


import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;


public class ExceptionMapperTest {
    @Test
    public void testGetInstance_whenDefaultInstanceIsNull() {
        // given
        ExceptionMapper exceptionMapper = null;
        Whitebox.setInternalState(ExceptionMapper.class, "servletInstance", exceptionMapper);
        // then
        exceptionMapper = ExceptionMapper.getServletInstance();
        Assert.assertEquals("Should be equals because ExceptionMapper is a singleton", Whitebox.getInternalState(ExceptionMapper.class, "servletInstance"), exceptionMapper);
    }

    @Test
    public void testGetInstance_whenDefaultInstanceIsNotNull() {
        // given
        ExceptionMapper.getServletInstance();// initialize Singleton

        // then
        ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();
        Assert.assertEquals("Should be equals because ExceptionMapper is a singleton", Whitebox.getInternalState(ExceptionMapper.class, "servletInstance"), exceptionMapper);
    }
}

