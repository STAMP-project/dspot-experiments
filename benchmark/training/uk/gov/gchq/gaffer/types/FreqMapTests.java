/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.types;


import org.junit.Assert;
import org.junit.Test;


public class FreqMapTests {
    private FreqMap freqMap;

    @Test
    public void testUpsertCreatesNewKeyValue() {
        // given
        final String key = "test";
        final Long value = 6L;
        // when
        freqMap.upsert(key, value);
        // then
        Assert.assertTrue(freqMap.containsKey(key));
        Assert.assertEquals(value, freqMap.get(key));
    }

    @Test
    public void testUpsertUpdatesExistingKeyValue() {
        // given
        final String key = "test";
        final Long initialValue = 3L;
        final Long increment = 11L;
        final Long expected = 14L;
        freqMap.put(key, initialValue);
        // when
        freqMap.upsert(key, increment);
        // then
        Assert.assertEquals(freqMap.get(key), expected);
    }

    @Test
    public void testUpsertOverloadedCreateDefaultValue() {
        // given
        final String key = "test";
        final Long expected = 1L;
        // when
        freqMap.upsert(key);
        // then
        Assert.assertTrue(freqMap.containsKey(key));
        Assert.assertEquals(freqMap.get(key), expected);
    }

    @Test
    public void testUpsertOverloadedIncrementsDefaultValue() {
        // given
        final String key = "test";
        final Long initialValue = 57L;
        final Long expected = 58L;
        freqMap.upsert(key, initialValue);
        // when
        freqMap.upsert(key);
        // then
        Assert.assertEquals(freqMap.get(key), expected);
    }

    @Test
    public void testKeyExistsButValueNullIsHandled() {
        // given
        final String key = "test";
        freqMap.put(key, null);
        final Long initialValue = 7L;
        final Long expectedValue = 8L;
        // when
        freqMap.upsert(key, 7L);
        freqMap.upsert(key);
        // then
        Assert.assertEquals(freqMap.get(key), expectedValue);
    }
}

