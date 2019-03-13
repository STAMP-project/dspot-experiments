/**
 * Copyright 2015 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.bootstrap.resolver.condition;


import com.navercorp.pinpoint.common.util.SystemProperty;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class SystemPropertyConditionTest {
    @Test
    public void testMatch() {
        // Given
        final String existingSystemProperty1 = "set.one.key.one";
        final String existingSystemProperty2 = "set.one.key.two";
        SystemProperty property = SystemPropertyConditionTest.createTestProperty(existingSystemProperty1, existingSystemProperty2);
        SystemPropertyCondition systemPropertyCondition = new SystemPropertyCondition(property);
        // When
        boolean firstKeyExists = systemPropertyCondition.check(existingSystemProperty1);
        boolean secondKeyExists = systemPropertyCondition.check(existingSystemProperty2);
        // Then
        Assert.assertTrue(firstKeyExists);
        Assert.assertTrue(secondKeyExists);
    }

    @Test
    public void testNoMatch() {
        // Given
        final String existingSystemProperty = "existing.system.property";
        SystemProperty property = SystemPropertyConditionTest.createTestProperty(existingSystemProperty);
        SystemPropertyCondition systemPropertyCondition = new SystemPropertyCondition(property);
        // When
        boolean keyExists = systemPropertyCondition.check("some.other.property");
        // Then
        Assert.assertFalse(keyExists);
    }

    @Test
    public void emptyConditionShouldNotMatch() {
        // Given
        final String existingSystemProperty = "existing.system.property";
        SystemProperty property = SystemPropertyConditionTest.createTestProperty(existingSystemProperty);
        SystemPropertyCondition systemPropertyCondition = new SystemPropertyCondition(property);
        // When
        boolean matches = systemPropertyCondition.check("");
        // Then
        Assert.assertFalse(matches);
    }

    @Test
    public void nullConditionShouldNotMatch() {
        // Given
        final String existingSystemProperty = "existing.system.property";
        SystemProperty property = SystemPropertyConditionTest.createTestProperty(existingSystemProperty);
        SystemPropertyCondition systemPropertyCondition = new SystemPropertyCondition(property);
        // When
        boolean matches = systemPropertyCondition.check(null);
        // Then
        Assert.assertFalse(matches);
    }
}

