/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.dataflow.worker.util.common.worker;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link ShuffleEntry}.
 */
@RunWith(JUnit4.class)
public class ShuffleEntryTest {
    private static final byte[] KEY = new byte[]{ 10 };

    private static final byte[] SKEY = new byte[]{ 11 };

    private static final byte[] VALUE = new byte[]{ 12 };

    @Test
    public void accessors() {
        ShuffleEntry entry = new ShuffleEntry(ShuffleEntryTest.KEY, ShuffleEntryTest.SKEY, ShuffleEntryTest.VALUE);
        Assert.assertThat(entry.getKey(), Matchers.equalTo(ShuffleEntryTest.KEY));
        Assert.assertThat(entry.getSecondaryKey(), Matchers.equalTo(ShuffleEntryTest.SKEY));
        Assert.assertThat(entry.getValue(), Matchers.equalTo(ShuffleEntryTest.VALUE));
    }

    @Test
    @SuppressWarnings("SelfEquals")
    public void equalsToItself() {
        ShuffleEntry entry = new ShuffleEntry(ShuffleEntryTest.KEY, ShuffleEntryTest.SKEY, ShuffleEntryTest.VALUE);
        Assert.assertEquals(entry, entry);
    }

    @Test
    public void equalsForEqualEntries() {
        ShuffleEntry entry0 = new ShuffleEntry(ShuffleEntryTest.KEY, ShuffleEntryTest.SKEY, ShuffleEntryTest.VALUE);
        ShuffleEntry entry1 = new ShuffleEntry(ShuffleEntryTest.KEY.clone(), ShuffleEntryTest.SKEY.clone(), ShuffleEntryTest.VALUE.clone());
        Assert.assertEquals(entry0, entry1);
        Assert.assertEquals(entry1, entry0);
        Assert.assertEquals(entry0.hashCode(), entry1.hashCode());
    }

    @Test
    public void equalsForEqualNullEntries() {
        ShuffleEntry entry0 = new ShuffleEntry(null, null, null);
        ShuffleEntry entry1 = new ShuffleEntry(null, null, null);
        Assert.assertEquals(entry0, entry1);
        Assert.assertEquals(entry1, entry0);
        Assert.assertEquals(entry0.hashCode(), entry1.hashCode());
    }

    @Test
    public void notEqualsWhenKeysDiffer() {
        final byte[] otherKey = new byte[]{ 1 };
        ShuffleEntry entry0 = new ShuffleEntry(ShuffleEntryTest.KEY, ShuffleEntryTest.SKEY, ShuffleEntryTest.VALUE);
        ShuffleEntry entry1 = new ShuffleEntry(otherKey, ShuffleEntryTest.SKEY, ShuffleEntryTest.VALUE);
        Assert.assertFalse(entry0.equals(entry1));
        Assert.assertFalse(entry1.equals(entry0));
        Assert.assertThat(entry0.hashCode(), Matchers.not(Matchers.equalTo(entry1.hashCode())));
    }

    @Test
    public void notEqualsWhenKeysDifferOneNull() {
        ShuffleEntry entry0 = new ShuffleEntry(ShuffleEntryTest.KEY, ShuffleEntryTest.SKEY, ShuffleEntryTest.VALUE);
        ShuffleEntry entry1 = new ShuffleEntry(null, ShuffleEntryTest.SKEY, ShuffleEntryTest.VALUE);
        Assert.assertFalse(entry0.equals(entry1));
        Assert.assertFalse(entry1.equals(entry0));
        Assert.assertThat(entry0.hashCode(), Matchers.not(Matchers.equalTo(entry1.hashCode())));
    }

    @Test
    public void notEqualsWhenSecondaryKeysDiffer() {
        final byte[] otherSKey = new byte[]{ 2 };
        ShuffleEntry entry0 = new ShuffleEntry(ShuffleEntryTest.KEY, ShuffleEntryTest.SKEY, ShuffleEntryTest.VALUE);
        ShuffleEntry entry1 = new ShuffleEntry(ShuffleEntryTest.KEY, otherSKey, ShuffleEntryTest.VALUE);
        Assert.assertFalse(entry0.equals(entry1));
        Assert.assertFalse(entry1.equals(entry0));
        Assert.assertThat(entry0.hashCode(), Matchers.not(Matchers.equalTo(entry1.hashCode())));
    }

    @Test
    public void notEqualsWhenSecondaryKeysDifferOneNull() {
        ShuffleEntry entry0 = new ShuffleEntry(ShuffleEntryTest.KEY, ShuffleEntryTest.SKEY, ShuffleEntryTest.VALUE);
        ShuffleEntry entry1 = new ShuffleEntry(ShuffleEntryTest.KEY, null, ShuffleEntryTest.VALUE);
        Assert.assertFalse(entry0.equals(entry1));
        Assert.assertFalse(entry1.equals(entry0));
        Assert.assertThat(entry0.hashCode(), Matchers.not(Matchers.equalTo(entry1.hashCode())));
    }

    @Test
    public void notEqualsWhenValuesDiffer() {
        final byte[] otherValue = new byte[]{ 2 };
        ShuffleEntry entry0 = new ShuffleEntry(ShuffleEntryTest.KEY, ShuffleEntryTest.SKEY, ShuffleEntryTest.VALUE);
        ShuffleEntry entry1 = new ShuffleEntry(ShuffleEntryTest.KEY, ShuffleEntryTest.SKEY, otherValue);
        Assert.assertFalse(entry0.equals(entry1));
        Assert.assertFalse(entry1.equals(entry0));
        Assert.assertThat(entry0.hashCode(), Matchers.not(Matchers.equalTo(entry1.hashCode())));
    }

    @Test
    public void notEqualsWhenValuesDifferOneNull() {
        ShuffleEntry entry0 = new ShuffleEntry(ShuffleEntryTest.KEY, ShuffleEntryTest.SKEY, ShuffleEntryTest.VALUE);
        ShuffleEntry entry1 = new ShuffleEntry(ShuffleEntryTest.KEY, ShuffleEntryTest.SKEY, null);
        Assert.assertFalse(entry0.equals(entry1));
        Assert.assertFalse(entry1.equals(entry0));
        Assert.assertThat(entry0.hashCode(), Matchers.not(Matchers.equalTo(entry1.hashCode())));
    }

    @Test
    public void emptyNotTheSameAsNull() {
        final byte[] empty = new byte[]{  };
        ShuffleEntry entry0 = new ShuffleEntry(null, null, null);
        ShuffleEntry entry1 = new ShuffleEntry(empty, empty, empty);
        Assert.assertFalse(entry0.equals(entry1));
        Assert.assertFalse(entry1.equals(entry0));
        Assert.assertThat(entry0.hashCode(), Matchers.not(Matchers.equalTo(entry1.hashCode())));
    }
}

