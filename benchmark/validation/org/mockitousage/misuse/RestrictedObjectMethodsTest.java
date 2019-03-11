/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.misuse;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockitoutil.TestBase;


public class RestrictedObjectMethodsTest extends TestBase {
    @Mock
    List<?> mock;

    @Test
    public void shouldScreamWhenVerifyToString() {
        try {
            Mockito.verify(mock).toString();
            Assert.fail();
        } catch (MockitoException e) {
            assertThat(e).hasMessageContaining("cannot verify");
        }
    }

    @Test
    public void shouldBeSilentWhenVerifyHashCode() {
        // because it leads to really weird behavior sometimes
        // it's because cglib & my code can occasionelly call those methods
        // and when user has verification started at that time there will be a mess
        Mockito.verify(mock).hashCode();
    }

    @Test
    public void shouldBeSilentWhenVerifyEquals() {
        // because it leads to really weird behavior sometimes
        // it's because cglib & my code can occasionelly call those methods
        // and when user has verification started at that time there will be a mess
        Mockito.verify(mock).equals(null);
    }

    @Test
    public void shouldBeSilentWhenVerifyEqualsInOrder() {
        // because it leads to really weird behavior sometimes
        // it's because cglib & my code can occasionelly call those methods
        // and when user has verification started at that time there will be a mess
        InOrder inOrder = Mockito.inOrder(mock);
        inOrder.verify(mock).equals(null);
    }
}

