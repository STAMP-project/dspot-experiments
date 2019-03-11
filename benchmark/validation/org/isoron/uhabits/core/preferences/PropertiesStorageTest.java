/**
 * Copyright (C) 2015-2017 ?linson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isoron.uhabits.core.preferences;


import java.io.File;
import java.util.Arrays;
import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;


public class PropertiesStorageTest extends BaseUnitTest {
    private PropertiesStorage storage;

    private File file;

    @Test
    public void testPutGetRemove() throws Exception {
        storage.putBoolean("booleanKey", true);
        Assert.assertTrue(storage.getBoolean("booleanKey", false));
        TestCase.assertFalse(storage.getBoolean("random", false));
        storage.putInt("intKey", 64);
        MatcherAssert.assertThat(storage.getInt("intKey", 200), IsEqual.equalTo(64));
        MatcherAssert.assertThat(storage.getInt("random", 200), IsEqual.equalTo(200));
        storage.putLong("longKey", 64L);
        MatcherAssert.assertThat(storage.getLong("intKey", 200L), IsEqual.equalTo(64L));
        MatcherAssert.assertThat(storage.getLong("random", 200L), IsEqual.equalTo(200L));
        storage.putString("stringKey", "Hello");
        MatcherAssert.assertThat(storage.getString("stringKey", ""), IsEqual.equalTo("Hello"));
        MatcherAssert.assertThat(storage.getString("random", ""), IsEqual.equalTo(""));
        storage.remove("stringKey");
        MatcherAssert.assertThat(storage.getString("stringKey", ""), IsEqual.equalTo(""));
        storage.clear();
        MatcherAssert.assertThat(storage.getLong("intKey", 200L), IsEqual.equalTo(200L));
        TestCase.assertFalse(storage.getBoolean("booleanKey", false));
    }

    @Test
    public void testPersistence() throws Exception {
        storage.putBoolean("booleanKey", true);
        storage.putInt("intKey", 64);
        storage.putLong("longKey", 64L);
        storage.putString("stringKey", "Hello");
        PropertiesStorage storage2 = new PropertiesStorage(file);
        Assert.assertTrue(storage2.getBoolean("booleanKey", false));
        MatcherAssert.assertThat(storage2.getInt("intKey", 200), IsEqual.equalTo(64));
        MatcherAssert.assertThat(storage2.getLong("intKey", 200L), IsEqual.equalTo(64L));
        MatcherAssert.assertThat(storage2.getString("stringKey", ""), IsEqual.equalTo("Hello"));
    }

    @Test
    public void testLongArray() throws Exception {
        long[] expected1 = new long[]{ 1L, 2L, 3L, 5L };
        long[] expected2 = new long[]{ 1L };
        long[] expected3 = new long[]{  };
        long[] expected4 = new long[]{  };
        storage.putLongArray("key1", expected1);
        storage.putLongArray("key2", expected2);
        storage.putLongArray("key3", expected3);
        long[] actual1 = storage.getLongArray("key1");
        long[] actual2 = storage.getLongArray("key2");
        long[] actual3 = storage.getLongArray("key3");
        long[] actual4 = storage.getLongArray("invalidKey");
        Assert.assertTrue(Arrays.equals(actual1, expected1));
        Assert.assertTrue(Arrays.equals(actual2, expected2));
        Assert.assertTrue(Arrays.equals(actual3, expected3));
        Assert.assertTrue(Arrays.equals(actual4, expected4));
        TestCase.assertEquals("1,2,3,5", storage.getString("key1", ""));
        TestCase.assertEquals(1, storage.getLong("key2", (-1)));
    }
}

