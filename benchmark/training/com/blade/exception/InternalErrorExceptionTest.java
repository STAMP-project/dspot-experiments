package com.blade.exception;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class InternalErrorExceptionTest {
    @Test
    public void testInternalErrorException() throws Exception {
        try {
            throw new InternalErrorException();
        } catch (InternalErrorException e) {
            Assert.assertEquals(e.getStatus(), 500);
            Assert.assertEquals(e.getName(), "Internal Error");
        }
    }

    @Test
    public void testInternalErrorExceptionWithMessage() throws Exception {
        try {
            throw new InternalErrorException("param [name] not is empty");
        } catch (InternalErrorException e) {
            Assert.assertEquals(e.getStatus(), 500);
            Assert.assertEquals(e.getName(), "Internal Error");
            Assert.assertEquals(e.getMessage(), "param [name] not is empty");
        }
    }
}

