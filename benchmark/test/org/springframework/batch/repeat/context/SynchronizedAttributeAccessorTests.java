/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.repeat.context;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.springframework.core.AttributeAccessorSupport;


public class SynchronizedAttributeAccessorTests extends TestCase {
    SynchronizedAttributeAccessor accessor = new SynchronizedAttributeAccessor();

    public void testHashCode() {
        SynchronizedAttributeAccessor another = new SynchronizedAttributeAccessor();
        accessor.setAttribute("foo", "bar");
        another.setAttribute("foo", "bar");
        TestCase.assertEquals(accessor, another);
        TestCase.assertEquals("Object.hashCode() contract broken", accessor.hashCode(), another.hashCode());
    }

    public void testToStringWithNoAttributes() throws Exception {
        TestCase.assertNotNull(accessor.toString());
    }

    public void testToStringWithAttributes() throws Exception {
        accessor.setAttribute("foo", "bar");
        accessor.setAttribute("spam", "bucket");
        TestCase.assertNotNull(accessor.toString());
    }

    public void testAttributeNames() {
        accessor.setAttribute("foo", "bar");
        accessor.setAttribute("spam", "bucket");
        List<String> list = Arrays.asList(accessor.attributeNames());
        TestCase.assertEquals(2, list.size());
        TestCase.assertTrue(list.contains("foo"));
    }

    public void testEqualsSameType() {
        SynchronizedAttributeAccessor another = new SynchronizedAttributeAccessor();
        accessor.setAttribute("foo", "bar");
        another.setAttribute("foo", "bar");
        TestCase.assertEquals(accessor, another);
    }

    public void testEqualsSelf() {
        accessor.setAttribute("foo", "bar");
        TestCase.assertEquals(accessor, accessor);
    }

    public void testEqualsWrongType() {
        accessor.setAttribute("foo", "bar");
        Map<String, String> another = Collections.singletonMap("foo", "bar");
        // Accessor and another are instances of unrelated classes, they should
        // never be equal...
        TestCase.assertFalse(accessor.equals(another));
    }

    public void testEqualsSupport() {
        @SuppressWarnings("serial")
        AttributeAccessorSupport another = new AttributeAccessorSupport() {};
        accessor.setAttribute("foo", "bar");
        another.setAttribute("foo", "bar");
        TestCase.assertEquals(accessor, another);
    }

    public void testGetAttribute() {
        accessor.setAttribute("foo", "bar");
        TestCase.assertEquals("bar", accessor.getAttribute("foo"));
    }

    public void testSetAttributeIfAbsentWhenAlreadyPresent() {
        accessor.setAttribute("foo", "bar");
        TestCase.assertEquals("bar", accessor.setAttributeIfAbsent("foo", "spam"));
    }

    public void testSetAttributeIfAbsentWhenNotAlreadyPresent() {
        TestCase.assertEquals(null, accessor.setAttributeIfAbsent("foo", "bar"));
        TestCase.assertEquals("bar", accessor.getAttribute("foo"));
    }

    public void testHasAttribute() {
        accessor.setAttribute("foo", "bar");
        TestCase.assertEquals(true, accessor.hasAttribute("foo"));
    }

    public void testRemoveAttribute() {
        accessor.setAttribute("foo", "bar");
        TestCase.assertEquals("bar", accessor.getAttribute("foo"));
        accessor.removeAttribute("foo");
        TestCase.assertEquals(null, accessor.getAttribute("foo"));
    }
}

