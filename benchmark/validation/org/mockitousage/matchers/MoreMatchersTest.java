/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.matchers;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class MoreMatchersTest extends TestBase {
    @Mock
    private IMethods mock;

    @Test
    public void should_help_out_with_unnecessary_casting() {
        Mockito.when(mock.objectArgMethod(ArgumentMatchers.any(String.class))).thenReturn("string");
        Assert.assertEquals("string", mock.objectArgMethod("foo"));
    }

    @Test
    public void any_should_be_actual_alias_to_anyObject() {
        mock.simpleMethod(((Object) (null)));
        Mockito.verify(mock).simpleMethod(Mockito.<Object>any());
        Mockito.verify(mock).simpleMethod(Mockito.<Object>anyObject());
    }

    @Test
    public void any_class_should_be_actual_alias_to_isA() {
        mock.simpleMethod(new ArrayList());
        Mockito.verify(mock).simpleMethod(ArgumentMatchers.isA(List.class));
        Mockito.verify(mock).simpleMethod(ArgumentMatchers.any(List.class));
        mock.simpleMethod(((String) (null)));
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                Mockito.verify(mock).simpleMethod(ArgumentMatchers.isA(String.class));
            }
        }).isInstanceOf(ArgumentsAreDifferent.class);
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                Mockito.verify(mock).simpleMethod(ArgumentMatchers.any(String.class));
            }
        }).isInstanceOf(ArgumentsAreDifferent.class);
    }

    @Test
    public void should_help_out_with_unnecessary_casting_of_lists() {
        // Below yields compiler warning:
        // when(mock.listArgMethod(anyList())).thenReturn("list");
        Mockito.when(mock.listArgMethod(ArgumentMatchers.anyListOf(String.class))).thenReturn("list");
        Assert.assertEquals("list", mock.listArgMethod(new LinkedList<String>()));
        Assert.assertEquals("list", mock.listArgMethod(Collections.<String>emptyList()));
    }

    @Test
    public void should_help_out_with_unnecessary_casting_of_sets() {
        // Below yields compiler warning:
        // when(mock.setArgMethod(anySet())).thenReturn("set");
        Mockito.when(mock.setArgMethod(ArgumentMatchers.anySetOf(String.class))).thenReturn("set");
        Assert.assertEquals("set", mock.setArgMethod(new HashSet<String>()));
        Assert.assertEquals("set", mock.setArgMethod(Collections.<String>emptySet()));
    }

    @Test
    public void should_help_out_with_unnecessary_casting_of_maps() {
        // Below yields compiler warning:
        // when(mock.setArgMethod(anySet())).thenReturn("set");
        Mockito.when(mock.forMap(ArgumentMatchers.anyMapOf(String.class, String.class))).thenReturn("map");
        Assert.assertEquals("map", mock.forMap(new HashMap<String, String>()));
        Assert.assertEquals("map", mock.forMap(Collections.<String, String>emptyMap()));
    }

    @Test
    public void should_help_out_with_unnecessary_casting_of_collections() {
        // Below yields compiler warning:
        // when(mock.setArgMethod(anySet())).thenReturn("set");
        Mockito.when(mock.collectionArgMethod(ArgumentMatchers.anyCollectionOf(String.class))).thenReturn("collection");
        Assert.assertEquals("collection", mock.collectionArgMethod(new ArrayList<String>()));
        Assert.assertEquals("collection", mock.collectionArgMethod(Collections.<String>emptyList()));
    }

    @Test
    public void should_help_out_with_unnecessary_casting_of_iterables() {
        // Below yields compiler warning:
        // when(mock.setArgMethod(anySet())).thenReturn("set");
        Mockito.when(mock.iterableArgMethod(ArgumentMatchers.anyIterableOf(String.class))).thenReturn("iterable");
        Assert.assertEquals("iterable", mock.iterableArgMethod(new ArrayList<String>()));
        Assert.assertEquals("iterable", mock.iterableArgMethod(Collections.<String>emptyList()));
    }

    @Test
    public void should_help_out_with_unnecessary_casting_of_nullity_checks() {
        Mockito.when(mock.objectArgMethod(ArgumentMatchers.isNull(LinkedList.class))).thenReturn("string");
        Mockito.when(mock.objectArgMethod(ArgumentMatchers.notNull(LinkedList.class))).thenReturn("string");
        Mockito.when(mock.objectArgMethod(ArgumentMatchers.isNotNull(LinkedList.class))).thenReturn("string");
        Assert.assertEquals("string", mock.objectArgMethod(null));
        Assert.assertEquals("string", mock.objectArgMethod("foo"));
        Assert.assertEquals("string", mock.objectArgMethod("foo"));
    }
}

