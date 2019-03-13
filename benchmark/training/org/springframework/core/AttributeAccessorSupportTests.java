/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.core;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rob Harrop
 * @author Sam Brannen
 * @since 2.0
 */
public class AttributeAccessorSupportTests {
    private static final String NAME = "foo";

    private static final String VALUE = "bar";

    private AttributeAccessor attributeAccessor = new AttributeAccessorSupportTests.SimpleAttributeAccessorSupport();

    @Test
    public void setAndGet() throws Exception {
        this.attributeAccessor.setAttribute(AttributeAccessorSupportTests.NAME, AttributeAccessorSupportTests.VALUE);
        Assert.assertEquals(AttributeAccessorSupportTests.VALUE, this.attributeAccessor.getAttribute(AttributeAccessorSupportTests.NAME));
    }

    @Test
    public void setAndHas() throws Exception {
        Assert.assertFalse(this.attributeAccessor.hasAttribute(AttributeAccessorSupportTests.NAME));
        this.attributeAccessor.setAttribute(AttributeAccessorSupportTests.NAME, AttributeAccessorSupportTests.VALUE);
        Assert.assertTrue(this.attributeAccessor.hasAttribute(AttributeAccessorSupportTests.NAME));
    }

    @Test
    public void remove() throws Exception {
        Assert.assertFalse(this.attributeAccessor.hasAttribute(AttributeAccessorSupportTests.NAME));
        this.attributeAccessor.setAttribute(AttributeAccessorSupportTests.NAME, AttributeAccessorSupportTests.VALUE);
        Assert.assertEquals(AttributeAccessorSupportTests.VALUE, this.attributeAccessor.removeAttribute(AttributeAccessorSupportTests.NAME));
        Assert.assertFalse(this.attributeAccessor.hasAttribute(AttributeAccessorSupportTests.NAME));
    }

    @Test
    public void attributeNames() throws Exception {
        this.attributeAccessor.setAttribute(AttributeAccessorSupportTests.NAME, AttributeAccessorSupportTests.VALUE);
        this.attributeAccessor.setAttribute("abc", "123");
        String[] attributeNames = this.attributeAccessor.attributeNames();
        Arrays.sort(attributeNames);
        Assert.assertTrue(((Arrays.binarySearch(attributeNames, AttributeAccessorSupportTests.NAME)) > (-1)));
        Assert.assertTrue(((Arrays.binarySearch(attributeNames, "abc")) > (-1)));
    }

    @SuppressWarnings("serial")
    private static class SimpleAttributeAccessorSupport extends AttributeAccessorSupport {}
}

