/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.common.property;


import org.junit.Assert;
import org.junit.Test;


public class DirectPropertyAccessStrategyTest {
    @Test
    public void testGetValue() {
        final Property<DirectPropertyAccessStrategyTest.TestMessage> actualProperty = getProperty(regularPropertyName());
        Assert.assertNotNull(actualProperty);
        Assert.assertNotNull(actualProperty.<String>getValue(propertyHoldingInstance()));
    }

    @Test
    public void testGetValue_BogusProperty() {
        Assert.assertNull(getProperty(unknownPropertyName()));
    }

    @Test(expected = NullPointerException.class)
    public void testGetValue_NullExceptionOnAccess() {
        getProperty(privatePropertyName()).getValue(propertyHoldingInstance());
    }

    @Test
    public void testOverriddenPropertyValue() {
        Assert.assertEquals("realValue", getProperty(overriddenPropertyName()).getValue(propertyHoldingInstance()));
    }

    class TestMessage extends DirectPropertyAccessStrategyTest.TestMessageParent {
        public String property1 = "property1Value";

        private Integer privateProperty1;

        private String overriddenProperty1 = "fakeValue";
    }

    class TestMessageParent {
        public String parentProperty1 = "parentProperty1Value";

        public String overriddenProperty1 = "realValue";
    }
}

