/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.matchers;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class NewMatchersTest extends TestBase {
    private IMethods mock;

    @Test
    public void shouldAllowAnyList() {
        Mockito.when(mock.forList(ArgumentMatchers.anyListOf(String.class))).thenReturn("matched");
        Assert.assertEquals("matched", mock.forList(Arrays.asList("x", "y")));
        Assert.assertEquals(null, mock.forList(null));
        Mockito.verify(mock, Mockito.times(1)).forList(ArgumentMatchers.anyListOf(String.class));
    }

    @Test
    public void shouldAllowAnyCollection() {
        Mockito.when(mock.forCollection(ArgumentMatchers.anyCollectionOf(String.class))).thenReturn("matched");
        Assert.assertEquals("matched", mock.forCollection(Arrays.asList("x", "y")));
        Assert.assertEquals(null, mock.forCollection(null));
        Mockito.verify(mock, Mockito.times(1)).forCollection(ArgumentMatchers.anyCollectionOf(String.class));
    }

    @Test
    public void shouldAllowAnyMap() {
        Mockito.when(mock.forMap(ArgumentMatchers.anyMapOf(String.class, String.class))).thenReturn("matched");
        Assert.assertEquals("matched", mock.forMap(new HashMap<String, String>()));
        Assert.assertEquals(null, mock.forMap(null));
        Mockito.verify(mock, Mockito.times(1)).forMap(ArgumentMatchers.anyMapOf(String.class, String.class));
    }

    @Test
    public void shouldAllowAnySet() {
        Mockito.when(mock.forSet(ArgumentMatchers.anySetOf(String.class))).thenReturn("matched");
        Assert.assertEquals("matched", mock.forSet(new HashSet<String>()));
        Assert.assertEquals(null, mock.forSet(null));
        Mockito.verify(mock, Mockito.times(1)).forSet(ArgumentMatchers.anySetOf(String.class));
    }

    @Test
    public void shouldAllowAnyIterable() {
        Mockito.when(mock.forIterable(ArgumentMatchers.anyIterableOf(String.class))).thenReturn("matched");
        Assert.assertEquals("matched", mock.forIterable(new HashSet<String>()));
        Assert.assertEquals(null, mock.forIterable(null));
        Mockito.verify(mock, Mockito.times(1)).forIterable(ArgumentMatchers.anyIterableOf(String.class));
    }
}

