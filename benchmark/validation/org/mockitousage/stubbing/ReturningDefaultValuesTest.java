/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class ReturningDefaultValuesTest extends TestBase {
    @Mock
    private IMethods mock;

    @Test
    public void shouldReturnAllKindsOfPrimitives() throws Exception {
        Assert.assertEquals(((byte) (0)), mock.byteReturningMethod());
        Assert.assertEquals(((short) (0)), mock.shortReturningMethod());
        Assert.assertEquals(0, mock.intReturningMethod());
        Assert.assertEquals(0L, mock.longReturningMethod());
        Assert.assertEquals(0.0F, mock.floatReturningMethod(), 0.0F);
        Assert.assertEquals(0.0, mock.doubleReturningMethod(), 0.0);
        Assert.assertEquals(((char) (0)), mock.charReturningMethod());
        Assert.assertEquals(false, mock.booleanReturningMethod());
        Assert.assertEquals(null, mock.objectReturningMethod());
    }

    @Test
    public void shouldReturnTheSameValuesForWrapperClasses() throws Exception {
        Assert.assertEquals(new Byte(((byte) (0))), mock.byteObjectReturningMethod());
        Assert.assertEquals(new Short(((short) (0))), mock.shortObjectReturningMethod());
        Assert.assertEquals(new Integer(0), mock.integerReturningMethod());
        Assert.assertEquals(new Long(0L), mock.longObjectReturningMethod());
        Assert.assertEquals(new Float(0.0F), mock.floatObjectReturningMethod(), 0.0F);
        Assert.assertEquals(new Double(0.0), mock.doubleObjectReturningMethod(), 0.0);
        Assert.assertEquals(new Character(((char) (0))), mock.charObjectReturningMethod());
        Assert.assertEquals(new Boolean(false), mock.booleanObjectReturningMethod());
    }

    @Test
    public void shouldReturnEmptyCollections() {
        ReturningDefaultValuesTest.CollectionsServer mock = Mockito.mock(ReturningDefaultValuesTest.CollectionsServer.class);
        Assert.assertTrue(mock.list().isEmpty());
        Assert.assertTrue(mock.linkedList().isEmpty());
        Assert.assertTrue(mock.map().isEmpty());
        Assert.assertTrue(mock.hashSet().isEmpty());
    }

    @Test
    public void shouldReturnMutableEmptyCollection() {
        ReturningDefaultValuesTest.CollectionsServer mock = Mockito.mock(ReturningDefaultValuesTest.CollectionsServer.class);
        List list = mock.list();
        list.add("test");
        Assert.assertTrue(mock.list().isEmpty());
    }

    private class CollectionsServer {
        List<?> list() {
            return null;
        }

        LinkedList<?> linkedList() {
            return null;
        }

        Map<?, ?> map() {
            return null;
        }

        HashSet<?> hashSet() {
            return null;
        }
    }
}

