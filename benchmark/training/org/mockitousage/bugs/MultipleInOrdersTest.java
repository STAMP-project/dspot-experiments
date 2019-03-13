/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
public class MultipleInOrdersTest {
    @Test
    public void inOrderTest() {
        List<String> list = Mockito.mock(List.class);
        list.add("a");
        list.add("x");
        list.add("b");
        list.add("y");
        InOrder inOrder = Mockito.inOrder(list);
        InOrder inAnotherOrder = Mockito.inOrder(list);
        Assert.assertNotSame(inOrder, inAnotherOrder);
        inOrder.verify(list).add("a");
        inOrder.verify(list).add("b");
        inAnotherOrder.verify(list).add("x");
        inAnotherOrder.verify(list).add("y");
    }
}

