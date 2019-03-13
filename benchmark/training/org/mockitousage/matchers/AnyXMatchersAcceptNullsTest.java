/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class AnyXMatchersAcceptNullsTest extends TestBase {
    private IMethods mock;

    @Test
    public void shouldAcceptNullsInAnyMatcher() {
        Mockito.when(mock.oneArg(((Object) (ArgumentMatchers.any())))).thenReturn("matched");
        Assert.assertEquals(null, mock.forObject(null));
    }

    @Test
    public void shouldAcceptNullsInAnyObjectMatcher() {
        Mockito.when(mock.oneArg(((Object) (ArgumentMatchers.anyObject())))).thenReturn("matched");
        Assert.assertEquals(null, mock.forObject(null));
    }

    @Test
    public void shouldNotAcceptNullInAnyXMatchers() {
        Mockito.when(mock.oneArg(ArgumentMatchers.anyString())).thenReturn("0");
        Mockito.when(mock.forList(ArgumentMatchers.anyListOf(String.class))).thenReturn("1");
        Mockito.when(mock.forMap(ArgumentMatchers.anyMapOf(String.class, String.class))).thenReturn("2");
        Mockito.when(mock.forCollection(ArgumentMatchers.anyCollectionOf(String.class))).thenReturn("3");
        Mockito.when(mock.forSet(ArgumentMatchers.anySetOf(String.class))).thenReturn("4");
        Assert.assertEquals(null, mock.oneArg(((Object) (null))));
        Assert.assertEquals(null, mock.oneArg(((String) (null))));
        Assert.assertEquals(null, mock.forList(null));
        Assert.assertEquals(null, mock.forMap(null));
        Assert.assertEquals(null, mock.forCollection(null));
        Assert.assertEquals(null, mock.forSet(null));
    }

    @Test
    public void shouldNotAcceptNullInAllAnyPrimitiveWrapperMatchers() {
        Mockito.when(mock.forInteger(ArgumentMatchers.anyInt())).thenReturn("0");
        Mockito.when(mock.forCharacter(ArgumentMatchers.anyChar())).thenReturn("1");
        Mockito.when(mock.forShort(ArgumentMatchers.anyShort())).thenReturn("2");
        Mockito.when(mock.forByte(ArgumentMatchers.anyByte())).thenReturn("3");
        Mockito.when(mock.forBoolean(ArgumentMatchers.anyBoolean())).thenReturn("4");
        Mockito.when(mock.forLong(ArgumentMatchers.anyLong())).thenReturn("5");
        Mockito.when(mock.forFloat(ArgumentMatchers.anyFloat())).thenReturn("6");
        Mockito.when(mock.forDouble(ArgumentMatchers.anyDouble())).thenReturn("7");
        Assert.assertEquals(null, mock.forInteger(null));
        Assert.assertEquals(null, mock.forCharacter(null));
        Assert.assertEquals(null, mock.forShort(null));
        Assert.assertEquals(null, mock.forByte(null));
        Assert.assertEquals(null, mock.forBoolean(null));
        Assert.assertEquals(null, mock.forLong(null));
        Assert.assertEquals(null, mock.forFloat(null));
        Assert.assertEquals(null, mock.forDouble(null));
    }
}

