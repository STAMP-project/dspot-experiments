/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.network.internal.toberemoved.cache;


import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.openhab.binding.network.internal.toberemoved.cache.ExpiringCacheAsync.ExpiringCacheUpdate;


/**
 * Tests cases for {@see ExpiringAsyncCache}
 *
 * @author David Graeff - Initial contribution
 */
public class ExpiringCacheAsyncTest {
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWrongCacheTime() {
        // Fail if cache time is <= 0
        new ExpiringCacheAsync<Double>(0, () -> {
        });
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNoRefrehCommand() {
        new ExpiringCacheAsync<Double>(2000, null);
    }

    @Test
    public void testFetchValue() {
        ExpiringCacheUpdate u = Mockito.mock(ExpiringCacheUpdate.class);
        ExpiringCacheAsync<Double> t = new ExpiringCacheAsync(2000, u);
        Assert.assertTrue(t.isExpired());
        // Request a value
        @SuppressWarnings("unchecked")
        Consumer<Double> consumer = Mockito.mock(Consumer.class);
        t.getValue(consumer);
        // We expect a call to the updater object
        Mockito.verify(u).requestCacheUpdate();
        // Update the value now
        t.setValue(10.0);
        // The value should be valid
        Assert.assertFalse(t.isExpired());
        // We expect a call to the consumer
        ArgumentCaptor<Double> valueCaptor = ArgumentCaptor.forClass(Double.class);
        Mockito.verify(consumer).accept(valueCaptor.capture());
        Assert.assertEquals(10.0, valueCaptor.getValue(), 0);
    }

    @Test
    public void testExpiring() {
        ExpiringCacheUpdate u = Mockito.mock(ExpiringCacheUpdate.class);
        @SuppressWarnings("unchecked")
        Consumer<Double> consumer = Mockito.mock(Consumer.class);
        ExpiringCacheAsync<Double> t = new ExpiringCacheAsync(100, u);
        t.setValue(10.0);
        Assert.assertFalse(t.isExpired());
        // Request a value
        t.getValue(consumer);
        // There should be no call to update the cache
        Mockito.verify(u, Mockito.times(0)).requestCacheUpdate();
        // Wait
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
            return;
        }
        // Request a value two times
        t.getValue(consumer);
        t.getValue(consumer);
        // There should be one call to update the cache
        Mockito.verify(u, Mockito.times(1)).requestCacheUpdate();
        Assert.assertTrue(t.isExpired());
    }

    @Test
    public void testFetchExpiredValue() {
        ExpiringCacheUpdate u = Mockito.mock(ExpiringCacheUpdate.class);
        ExpiringCacheAsync<Double> t = new ExpiringCacheAsync(2000, u);
        t.setValue(10.0);
        // We should always be able to get the raw value, expired or not
        Assert.assertEquals(10.0, t.getExpiredValue(), 0);
        t.invalidateValue();
        Assert.assertTrue(t.isExpired());
        Assert.assertEquals(10.0, t.getExpiredValue(), 0);
    }
}

