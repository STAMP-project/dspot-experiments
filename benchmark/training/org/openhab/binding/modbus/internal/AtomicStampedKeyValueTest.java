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
package org.openhab.binding.modbus.internal;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AtomicStampedKeyValueTest {
    @Test(expected = NullPointerException.class)
    public void testInitWithNullKey() {
        new AtomicStampedKeyValue<Object, Object>(0, null, new Object());
    }

    @Test(expected = NullPointerException.class)
    public void testInitWithNullValue() {
        new AtomicStampedKeyValue<Object, Object>(0, new Object(), null);
    }

    @Test
    public void testGetters() {
        Object key = new Object();
        Object val = new Object();
        AtomicStampedKeyValue<Object, Object> keyValue = new AtomicStampedKeyValue(42L, key, val);
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo(42L)));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.equalTo(key)));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.equalTo(val)));
    }

    @Test
    public void testUpdateWithSameStampAndKey() {
        Object key = new Object();
        Object val = new Object();
        AtomicStampedKeyValue<Object, Object> keyValue = new AtomicStampedKeyValue(42L, key, val);
        keyValue.update(42L, key, new Object());
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo(42L)));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.equalTo(key)));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(val))));
    }

    @Test
    public void testUpdateWithSameStamp() {
        Object key = new Object();
        Object val = new Object();
        AtomicStampedKeyValue<Object, Object> keyValue = new AtomicStampedKeyValue(42L, key, val);
        keyValue.update(42L, new Object(), new Object());
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo(42L)));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(key))));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(val))));
    }

    @Test
    public void testUpdateWithSameKey() {
        Object key = new Object();
        Object val = new Object();
        AtomicStampedKeyValue<Object, Object> keyValue = new AtomicStampedKeyValue(42L, key, val);
        keyValue.update((-99L), key, new Object());
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo((-99L))));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.equalTo(key)));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(val))));
    }

    @Test
    public void testUpdateWithSameValue() {
        Object key = new Object();
        Object val = new Object();
        AtomicStampedKeyValue<Object, Object> keyValue = new AtomicStampedKeyValue(42L, key, val);
        keyValue.update((-99L), new Object(), val);
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo((-99L))));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(key))));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.equalTo(val)));
    }

    @Test
    public void testCopy() {
        Object key = new Object();
        Object val = new Object();
        AtomicStampedKeyValue<Object, Object> keyValue = new AtomicStampedKeyValue(42L, key, val);
        AtomicStampedKeyValue<Object, Object> copy = keyValue.copy();
        // keyValue unchanged
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo(42L)));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.equalTo(key)));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.equalTo(val)));
        // data matches
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo(copy.getStamp())));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.equalTo(copy.getKey())));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.equalTo(copy.getValue())));
        // after update they live life of their own
        Object key2 = new Object();
        Object val2 = new Object();
        copy.update((-99L), key2, val2);
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo(42L)));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.equalTo(key)));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.equalTo(val)));
        Assert.assertThat(copy.getStamp(), CoreMatchers.is(CoreMatchers.equalTo((-99L))));
        Assert.assertThat(copy.getKey(), CoreMatchers.is(CoreMatchers.equalTo(key2)));
        Assert.assertThat(copy.getValue(), CoreMatchers.is(CoreMatchers.equalTo(val2)));
    }

    /**
     * instance(stamp=x).copyIfStampAfter(x)
     */
    @Test
    public void testCopyIfStampAfterEqual() {
        Object key = new Object();
        Object val = new Object();
        AtomicStampedKeyValue<Object, Object> keyValue = new AtomicStampedKeyValue(42L, key, val);
        AtomicStampedKeyValue<Object, Object> copy = keyValue.copyIfStampAfter(42L);
        // keyValue unchanged
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo(42L)));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.equalTo(key)));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.equalTo(val)));
        // data matches
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo(copy.getStamp())));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.equalTo(copy.getKey())));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.equalTo(copy.getValue())));
        // after update they live life of their own
        Object key2 = new Object();
        Object val2 = new Object();
        copy.update((-99L), key2, val2);
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo(42L)));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.equalTo(key)));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.equalTo(val)));
        Assert.assertThat(copy.getStamp(), CoreMatchers.is(CoreMatchers.equalTo((-99L))));
        Assert.assertThat(copy.getKey(), CoreMatchers.is(CoreMatchers.equalTo(key2)));
        Assert.assertThat(copy.getValue(), CoreMatchers.is(CoreMatchers.equalTo(val2)));
    }

    /**
     * instance(stamp=x-1).copyIfStampAfter(x)
     */
    @Test
    public void testCopyIfStampAfterTooOld() {
        Object key = new Object();
        Object val = new Object();
        AtomicStampedKeyValue<Object, Object> keyValue = new AtomicStampedKeyValue(42L, key, val);
        AtomicStampedKeyValue<Object, Object> copy = keyValue.copyIfStampAfter(43L);
        // keyValue unchanged
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo(42L)));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.equalTo(key)));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.equalTo(val)));
        // copy is null
        Assert.assertThat(copy, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    /**
     * instance(stamp=x).copyIfStampAfter(x-1)
     */
    @Test
    public void testCopyIfStampAfterFresh() {
        Object key = new Object();
        Object val = new Object();
        AtomicStampedKeyValue<Object, Object> keyValue = new AtomicStampedKeyValue(42L, key, val);
        AtomicStampedKeyValue<Object, Object> copy = keyValue.copyIfStampAfter(41L);
        // keyValue unchanged
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo(42L)));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.equalTo(key)));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.equalTo(val)));
        // data matches
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo(copy.getStamp())));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.equalTo(copy.getKey())));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.equalTo(copy.getValue())));
        // after update they live life of their own
        Object key2 = new Object();
        Object val2 = new Object();
        copy.update((-99L), key2, val2);
        Assert.assertThat(keyValue.getStamp(), CoreMatchers.is(CoreMatchers.equalTo(42L)));
        Assert.assertThat(keyValue.getKey(), CoreMatchers.is(CoreMatchers.equalTo(key)));
        Assert.assertThat(keyValue.getValue(), CoreMatchers.is(CoreMatchers.equalTo(val)));
        Assert.assertThat(copy.getStamp(), CoreMatchers.is(CoreMatchers.equalTo((-99L))));
        Assert.assertThat(copy.getKey(), CoreMatchers.is(CoreMatchers.equalTo(key2)));
        Assert.assertThat(copy.getValue(), CoreMatchers.is(CoreMatchers.equalTo(val2)));
    }

    @Test
    public void testCompare() {
        // equal, smaller, larger
        Assert.assertThat(AtomicStampedKeyValue.compare(new AtomicStampedKeyValue<Object, Object>(42L, "", ""), new AtomicStampedKeyValue<Object, Object>(42L, "", "")), CoreMatchers.is(CoreMatchers.equalTo(0)));
        Assert.assertThat(AtomicStampedKeyValue.compare(new AtomicStampedKeyValue<Object, Object>(41L, "", ""), new AtomicStampedKeyValue<Object, Object>(42L, "", "")), CoreMatchers.is(CoreMatchers.equalTo((-1))));
        Assert.assertThat(AtomicStampedKeyValue.compare(new AtomicStampedKeyValue<Object, Object>(42L, "", ""), new AtomicStampedKeyValue<Object, Object>(41L, "", "")), CoreMatchers.is(CoreMatchers.equalTo(1)));
        // Nulls come first
        Assert.assertThat(AtomicStampedKeyValue.compare(null, new AtomicStampedKeyValue<Object, Object>(42L, "", "")), CoreMatchers.is(CoreMatchers.equalTo((-1))));
        Assert.assertThat(AtomicStampedKeyValue.compare(new AtomicStampedKeyValue<Object, Object>(42L, "", ""), null), CoreMatchers.is(CoreMatchers.equalTo(1)));
    }
}

