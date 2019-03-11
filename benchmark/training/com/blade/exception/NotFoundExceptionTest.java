package com.blade.exception;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class NotFoundExceptionTest {
    @Test
    public void testNotFoundException() throws Exception {
        try {
            throw new NotFoundException("/hello");
        } catch (NotFoundException e) {
            Assert.assertEquals(e.getStatus(), 404);
            Assert.assertEquals(e.getName(), "Not Found");
        }
    }

    @Test
    public void testNotFoundExceptionWithMessage() throws Exception {
        try {
            throw new NotFoundException("the url not found");
        } catch (NotFoundException e) {
            Assert.assertEquals(e.getStatus(), 404);
            Assert.assertEquals(e.getName(), "Not Found");
            Assert.assertEquals(e.getMessage(), "the url not found");
        }
    }
}

