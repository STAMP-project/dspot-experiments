/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.defaultanswers;


import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


public class ReturnsMoreEmptyValuesTest extends TestBase {
    private ReturnsMoreEmptyValues rv = new ReturnsMoreEmptyValues();

    @Test
    public void shouldReturnEmptyArray() {
        String[] ret = ((String[]) (rv.returnValueFor(new String[0].getClass())));
        Assert.assertTrue(ret.getClass().isArray());
        Assert.assertTrue(((ret.length) == 0));
    }

    @Test
    public void shouldReturnEmptyString() {
        Assert.assertEquals("", rv.returnValueFor(String.class));
    }
}

