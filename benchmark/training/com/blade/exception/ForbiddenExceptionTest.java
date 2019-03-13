package com.blade.exception;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class ForbiddenExceptionTest {
    @Test
    public void testForbiddenException() throws Exception {
        try {
            throw new ForbiddenException();
        } catch (ForbiddenException e) {
            Assert.assertEquals(e.getStatus(), 403);
            Assert.assertEquals(e.getName(), "Forbidden");
        }
    }

    @Test
    public void testForbiddenExceptionWithMessage() throws Exception {
        try {
            throw new ForbiddenException("there is no access to");
        } catch (ForbiddenException e) {
            Assert.assertEquals(e.getStatus(), 403);
            Assert.assertEquals(e.getName(), "Forbidden");
            Assert.assertEquals(e.getMessage(), "there is no access to");
        }
    }
}

