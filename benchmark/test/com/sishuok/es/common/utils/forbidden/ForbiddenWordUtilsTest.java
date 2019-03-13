/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.common.utils.forbidden;


import org.junit.Assert;
import org.junit.Test;


/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-5-27 ??3:25
 * <p>Version: 1.0
 */
public class ForbiddenWordUtilsTest {
    @Test
    public void testReplaceWithDefaultMask() {
        String input = "12???????32";
        String expected = "12***32";
        String actual = ForbiddenWordUtils.replace(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceWithDefaultMask2() {
        String input = "1264.*????123";
        String expected = "12***123";
        String actual = ForbiddenWordUtils.replace(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceWithDefaultMask3() {
        String input = "freenet123";
        String expected = "***123";
        String actual = ForbiddenWordUtils.replace(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceWithDefaultMask4() {
        String input = " ?? ";
        String expected = "***";
        String actual = ForbiddenWordUtils.replace(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceWithCustomMask() {
        String input = "12???????32";
        String expected = "12###32";
        String actual = ForbiddenWordUtils.replace(input, "###");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testContainsForbiddenWord() {
        String input = "12???????32";
        Assert.assertTrue(ForbiddenWordUtils.containsForbiddenWord(input));
    }
}

