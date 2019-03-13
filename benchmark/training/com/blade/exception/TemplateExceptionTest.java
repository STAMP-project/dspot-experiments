package com.blade.exception;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class TemplateExceptionTest {
    @Test
    public void testTemplateException() throws Exception {
        try {
            throw new TemplateException("not found template");
        } catch (TemplateException e) {
            Assert.assertEquals("not found template", e.getMessage());
        }
    }
}

