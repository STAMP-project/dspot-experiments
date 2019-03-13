/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core;


import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class MappedFieldsTrackerTest extends AbstractDozerTest {
    private MappedFieldsTracker tracker;

    @Test
    public void testPut_OK() {
        tracker.put("", "1");
        Assert.assertEquals("1", tracker.getMappedValue("", String.class));
    }

    @Test
    public void testPut_Interface() {
        tracker.put("", "1");
        tracker.put("", new HashSet());
        Assert.assertEquals(new HashSet(), tracker.getMappedValue("", Set.class));
    }

    @Test
    public void testGetMappedValue() {
        Assert.assertNull(tracker.getMappedValue("", String.class));
    }

    @Test
    public void testGetMappedValue_NoSuchType() {
        tracker.put("", new HashSet());
        Assert.assertNull(tracker.getMappedValue("", String.class));
    }

    @Test
    public void doesNotCallEqualsOrHashCode() {
        MappedFieldsTrackerTest.Boom src = new MappedFieldsTrackerTest.Boom();
        MappedFieldsTrackerTest.Boom dest = new MappedFieldsTrackerTest.Boom();
        tracker.put(src, dest);
        Object result = tracker.getMappedValue(src, MappedFieldsTrackerTest.Boom.class);
        Assert.assertSame(dest, result);
    }

    @Test
    public void testGetMappedValue_honorsMapId() {
        tracker.put("", "42", "someId");
        Assert.assertNull(tracker.getMappedValue("", String.class));
        Assert.assertEquals("42", tracker.getMappedValue("", String.class, "someId"));
        Assert.assertNull(tracker.getMappedValue("", String.class, "brandNewMapId"));
    }

    public static class Boom {
        @Override
        public int hashCode() {
            throw new RuntimeException();
        }

        @Override
        public boolean equals(Object obj) {
            throw new RuntimeException();
        }
    }
}

